import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import de.marcphilipp.gradle.nexus.NexusRepository

plugins {
	kotlin("jvm").version("1.3.61")
	id("org.jetbrains.dokka") version "0.10.1"
	`maven-publish`
	signing
	id("de.marcphilipp.nexus-publish") version "0.4.0"
	id("io.codearte.nexus-staging") version "0.21.2"
	id("com.palantir.git-version") version "0.12.2"
	idea
}

group = "de.joshuagleitze"
version = if (isSnapshot) versionDetails.gitHash else versionDetails.lastTag.drop("v")
status = if (isSnapshot) "snapshot" else "release"

repositories {
	jcenter()
}

dependencies {
	implementation(kotlin(module = "stdlib"))

	testImplementation(kotlin("reflect"))
	testImplementation(name = "atrium-cc-en_GB-robstoll", group = "ch.tutteli.atrium", version = "0.8.0")
	testImplementation(name = "junit-jupiter-api", group = "org.junit.jupiter", version = "5.6.0")
	testImplementation(name = "junit-jupiter-params", group = "org.junit.jupiter", version = "5.6.0")

	testRuntimeOnly(name = "junit-jupiter-engine", group = "org.junit.jupiter", version = "5.6.0")
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project
val githubRepository: String? by project
val githubOwner = githubRepository?.split("/")?.get(0)
val githubToken: String? by project

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<KotlinCompile>("compileTestKotlin") {
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

val sourcesJar by tasks.creating(Jar::class) {
	group = "build"
	description = "Assembles the source code into a jar"
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

val dokka by tasks.getting(DokkaTask::class) {
	configuration {
		sourceLink {
			path = "./"
			url = "https://github.com/$githubRepository/blob/master"
			lineSuffix = "#L"
		}
	}
}

val dokkaJar by tasks.creating(Jar::class) {
	group = "build"
	description = "Assembles the Kotlin docs with Dokka"
	archiveClassifier.set("javadoc")
	from(dokka)
}

artifacts {
	archives(sourcesJar)
	archives(dokkaJar)
}

lateinit var publication: MavenPublication
lateinit var snapshotRepository: ArtifactRepository
lateinit var releaseRepository: NexusRepository

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
				url.set("https://github.com/jGleitz/string-notation")
				ciManagement {
					system.set("GitHub Actions")
					url.set("https://github.com/jGleitz/string-notation/actions")
				}
				issueManagement {
					system.set("GitHub Issues")
					url.set("https://github.com/jGleitz/string-notation/issues")
				}
				developers {
					developer {
						name.set("Joshua Gleitze")
						email.set("dev@joshuagleitze.de")
					}
				}
				scm {
					connection.set("scm:git:https://github.com/jGleitz/string-notation.git")
					developerConnection.set("scm:git:git://git@github.com:jGleitz/string-notation.git")
					url.set("https://github.com/jGleitz/string-notation")
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
		snapshotRepository = maven("https://maven.pkg.github.com/$githubRepository") {
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
		releaseRepository = sonatype {
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
}

val closeAndReleaseRepository by project.tasks
val publish by tasks

task("publishSnapshot") {
	group = "publishing"
	description = "Publishes a snapshot of the project to GitHub Packages"
	dependsOn(snapshotRepository.publishTask)
}

task("release") {
	group = "release"
	description = "Releases the project to Maven Central"
	dependsOn(releaseRepository.publishTask)
	dependsOn(closeAndReleaseRepository)
	closeAndReleaseRepository.mustRunAfter(releaseRepository.publishTask)
}

idea {
	module {
		isDownloadJavadoc = true
		isDownloadSources = true
	}
}

val Project.isSnapshot get() = versionDetails.commitDistance != 0

fun String.drop(prefix: String) = if (this.startsWith(prefix)) this.drop(prefix.length) else this

val Project.versionDetails
	get() = (this.extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails


val ArtifactRepository.publishTask get() = tasks["publishAllPublicationsTo${this.name}Repository"]
val NexusRepository.publishTask get() = tasks["publishTo${this.name.capitalize()}"]
