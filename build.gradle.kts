import io.github.gradlenexus.publishplugin.NexusRepository
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version "2.2.20"
	id("org.jetbrains.dokka-javadoc") version "2.1.0"
	id("org.jetbrains.dokka") version "2.1.0"
	`maven-publish`
	signing
	id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "de.joshuagleitze"
version = if (version == "unspecified") "local" else version.toString().drop("v")
status = if (version == "local") "snapshot" else "release"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(name = "atrium-cc-en_GB-robstoll", group = "ch.tutteli.atrium", version = "0.15.0")
	testImplementation(name = "junit-jupiter-api", group = "org.junit.jupiter", version = "5.7.2")
	testImplementation(name = "junit-jupiter-params", group = "org.junit.jupiter", version = "5.7.2")

	constraints {
		testImplementation(kotlin("reflect", KotlinVersion.CURRENT.toString()))
	}

	testRuntimeOnly(name = "junit-platform-launcher", group = "org.junit.platform", version = "1.12.1")
	testRuntimeOnly(name = "junit-jupiter-engine", group = "org.junit.jupiter", version = "5.7.2")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
	reports.junitXml.required = true
}

java {
	sourceCompatibility = VERSION_1_8
	targetCompatibility = VERSION_1_8
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_1_8
	}
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project
val githubRepository: String? by project
val githubOwner = githubRepository?.split("/")?.get(0)
val githubToken: String? by project

val sourcesJar by tasks.registering(Jar::class) {
	group = "build"
	description = "Assembles the source code into a jar"
	archiveClassifier = "sources"
	from(sourceSets.main.map { it.allSource })
}

dokka {
	dokkaSourceSets.main {
		sourceLink {
			localDirectory = file("src/main/kotlin")
			remoteUrl = uri("https://github.com/$githubRepository/blob/main/src/main/kotlin")
			remoteLineSuffix = "#L"
		}
	}
}

val dokkaJar by tasks.registering(Jar::class) {
	group = "build"
	description = "Assembles the Kotlin docs with Dokka"
	archiveClassifier.set("javadoc")
	from(tasks.named("dokkaGeneratePublicationJavadoc"))
}

artifacts {
	archives(sourcesJar)
	archives(dokkaJar)
}

lateinit var publication: MavenPublication
lateinit var githubPackages: ArtifactRepository
lateinit var mavenCentral: NexusRepository

publishing {
	publications {
		publication = create<MavenPublication>("maven") {
			from(components["java"])
			artifact(sourcesJar)
			artifact(dokkaJar)

			pom {
				name = "$groupId:$artifactId"
				description = "Convert between different string notations commonly found in programming"
				inceptionYear = "2020"
				url = "https://github.com/$githubRepository"
				ciManagement {
					system = "GitHub Actions"
					url = "https://github.com/$githubRepository/actions"
				}
				issueManagement {
					system = "GitHub Issues"
					url = "https://github.com/$githubRepository/issues"
				}
				developers {
					developer {
						name = "Joshua Gleitze"
						email = "dev@joshuagleitze.de"
					}
				}
				scm {
					connection = "scm:git:https://github.com/$githubRepository.git"
					developerConnection = "scm:git:git://git@github.com:$githubRepository.git"
					url = "https://github.com/$githubRepository"
				}
				licenses {
					license {
						name = "MIT"
						url = "https://opensource.org/licenses/MIT"
						distribution = "repo"
					}
				}
			}
		}
	}
	repositories {
		githubPackages = maven("https://maven.pkg.github.com/$githubRepository") {
			name = "GitHubPackages"
			credentials {
				username = githubOwner
				password = githubToken
			}
		}
	}
}

nexusPublishing {
	repositories {
		mavenCentral = sonatype {
			username = ossrhUsername
			password = ossrhPassword
		}
	}
}

signing {
	val signingKey: String? by project
	val signingKeyPassword: String? by project
	useInMemoryPgpKeys(signingKey, signingKeyPassword)
	sign(publication)
}

val closeAndReleaseStagingRepositories by project.tasks
closeAndReleaseStagingRepositories.mustRunAfter(mavenCentral.publishTask)

val release by tasks.registering {
	group = "release"
	description = "Releases the project to Maven Central"
	dependsOn(githubPackages.publishTask, mavenCentral.publishTask, closeAndReleaseStagingRepositories)
}

fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val ArtifactRepository.publishTask get() = tasks["publishAllPublicationsTo${this.name}Repository"]
val NexusRepository.publishTask get() = "publishTo${this.name.replaceFirstChar { it.titlecase() }}"
