import org.gradle.kotlin.dsl.*
import org.jreleaser.model.Active.ALWAYS


plugins {
	kotlin("jvm") version "2.2.21"
	id("org.jetbrains.dokka-javadoc") version "2.1.0"
	id("org.jetbrains.dokka") version "2.1.0"
	`maven-publish`
	signing
	id("org.jreleaser") version "1.21.0"
}

group = "de.joshuagleitze"
version = if (version == "unspecified") "local" else version.toString().drop("v")
status = if (version == "local") "snapshot" else "release"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation("ch.tutteli.atrium:atrium-fluent:1.2.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
	testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.1")

	constraints {
		testImplementation(kotlin("reflect", KotlinVersion.CURRENT.toString()))
	}

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.1")
}

val compilationTargetJavaVersion = JavaLanguageVersion.of(17)

java {
	toolchain {
		languageVersion = compilationTargetJavaVersion
	}
}

val testTargetJavaVersion = providers
	.gradleProperty("testTargetJavaVersion")
	.map(JavaLanguageVersion::of)
	.orElse(compilationTargetJavaVersion)

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
	reports.junitXml.required = true
	javaLauncher = javaToolchains.launcherFor {
		languageVersion = testTargetJavaVersion
	}
}


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

lateinit var publication: MavenPublication
lateinit var githubPackages: ArtifactRepository
lateinit var mavenCentralStaging: MavenArtifactRepository

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
		mavenCentralStaging = maven(layout.buildDirectory.dir("publish/maven-central-staging")) {
			name = "MavenCentralStaging"
		}
		githubPackages = maven("https://maven.pkg.github.com/$githubRepository") {
			name = "GitHubPackages"
			credentials {
				username = githubOwner
				password = githubToken
			}
		}
	}
}

signing {
	useInMemoryPgpKeys(
		providers.gradleProperty("signingKey").orNull,
		providers.gradleProperty("signingKeyPassword").orNull
		)
	sign(publication)
}

jreleaser {
	deploy {
		maven {
			mavenCentral {
				register("sonatype") {
					url = "https://central.sonatype.com/api/v1/publisher"
					active = ALWAYS
					stagingRepository(mavenCentralStaging.url.toString())
				}
			}
		}
	}
}

val jreleaserConfig by tasks
val jreleaserDeploy by tasks

jreleaserDeploy.mustRunAfter(jreleaserConfig)
jreleaserDeploy.mustRunAfter(mavenCentralStaging.publishTask)

val release by tasks.registering {
	group = "release"
	description = "Releases the project to all repositories"
	dependsOn(jreleaserConfig, githubPackages.publishTask, mavenCentralStaging.publishTask, jreleaserDeploy)
}

fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val ArtifactRepository.publishTask get() = tasks["publishAllPublicationsTo${this.name}Repository"]
fun ProviderFactory.requiredGradleProperty(name: String): Provider<String> = gradleProperty(name)
	.orElse(provider { throw InvalidUserDataException("required project property `$name` was not set!")})