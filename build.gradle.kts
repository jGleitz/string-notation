import de.marcphilipp.gradle.nexus.NexusRepository
import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.4.0"
	id("com.palantir.git-version") version "0.12.3"
	id("org.jetbrains.dokka") version "1.4.30"
	`maven-publish`
	signing
	id("de.marcphilipp.nexus-publish") version "0.4.0"
	id("io.codearte.nexus-staging") version "0.30.0"
}

group = "de.joshuagleitze"
version = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag.drop("v")
status = if (isSnapshot) "snapshot" else "release"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(name = "atrium-cc-en_GB-robstoll", group = "ch.tutteli.atrium", version = "0.15.0")
	testImplementation(name = "junit-jupiter-api", group = "org.junit.jupiter", version = "5.7.1")
	testImplementation(name = "junit-jupiter-params", group = "org.junit.jupiter", version = "5.7.1")

	testRuntimeOnly(name = "junit-jupiter-engine", group = "org.junit.jupiter", version = "5.7.1")
}

tasks.withType<Test> {
	useJUnitPlatform()
	reports.junitXml.isEnabled = true
}

java {
	sourceCompatibility = VERSION_1_8
	targetCompatibility = VERSION_1_8
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project
val githubRepository: String? by project
val githubOwner = githubRepository?.split("/")?.get(0)
val githubToken: String? by project

val sourcesJar by tasks.creating(Jar::class) {
	group = "build"
	description = "Assembles the source code into a jar"
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

tasks.withType<DokkaTask> {
	dokkaSourceSets.named("main") {
		sourceLink {
			localDirectory.set(file("src/main/kotlin"))
			remoteUrl.set(uri("https://github.com/$githubRepository/blob/master/src/main/kotlin").toURL())
			remoteLineSuffix.set("#L")
		}
	}
}

val dokkaJar by tasks.creating(Jar::class) {
	group = "build"
	description = "Assembles the Kotlin docs with Dokka"
	archiveClassifier.set("javadoc")
	from(tasks.named("dokkaJavadoc"))
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

nexusStaging {
	username = ossrhUsername
	password = ossrhPassword
	numberOfRetries = 42
}

val closeAndReleaseRepository by project.tasks
closeAndReleaseRepository.mustRunAfter(mavenCentral.publishTask)

task("release") {
	group = "release"
	description = "Releases the project to Maven Central"
	dependsOn(githubPackages.publishTask, mavenCentral.publishTask, closeAndReleaseRepository)
}

val Project.isSnapshot get() = versionDetails.commitDistance != 0
fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this
val Project.versionDetails get() = (this.extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails
val ArtifactRepository.publishTask get() = tasks["publishAllPublicationsTo${this.name}Repository"]
val NexusRepository.publishTask get() = tasks["publishTo${this.name.capitalize()}"]
