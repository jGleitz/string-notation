import io.github.gradlenexus.publishplugin.NexusRepository
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version "2.1.10"
	id("org.jetbrains.dokka-javadoc") version "2.0.0"
	id("org.jetbrains.dokka") version "2.0.0"
	`maven-publish`
	signing
	id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "de.joshuagleitze"
version = if (version == "unspecified") "local" else version
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
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
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
				name.set(provider { "$groupId:$artifactId" })
				description.set("Convert between different string notations commonly found in programming")
				inceptionYear.set("2020")
				url.set("https://github.com/$githubRepository")
				ciManagement {
					system.set("GitHub Actions")
					url.set("https://github.com/$githubRepository/actions")
				}
				issueManagement {
					system.set("GitHub Issues")
					url.set("https://github.com/$githubRepository/issues")
				}
				developers {
					developer {
						name.set("Joshua Gleitze")
						email.set("dev@joshuagleitze.de")
					}
				}
				scm {
					connection.set("scm:git:https://github.com/$githubRepository.git")
					developerConnection.set("scm:git:git://git@github.com:$githubRepository.git")
					url.set("https://github.com/$githubRepository")
				}
				licenses {
					license {
						name.set("MIT")
						url.set("https://opensource.org/licenses/MIT")
						distribution.set("repo")
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
			username.set(ossrhUsername)
			password.set(ossrhPassword)
		}
	}
}

signing {
	val signingKey: String? by project
	val signingKeyPassword: String? by project
	useInMemoryPgpKeys(signingKey, signingKeyPassword)
	sign(publication)
}

val closeAndReleaseStagingRepository by project.tasks
closeAndReleaseStagingRepository.mustRunAfter(mavenCentral.publishTask)

val release by tasks.registering {
	group = "release"
	description = "Releases the project to Maven Central"
	dependsOn(githubPackages.publishTask, mavenCentral.publishTask, closeAndReleaseStagingRepository)
}

fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val ArtifactRepository.publishTask get() = tasks["publishAllPublicationsTo${this.name}Repository"]
val NexusRepository.publishTask get() = "publishTo${this.name.replaceFirstChar { it.titlecase() }}"
