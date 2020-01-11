import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm").version("1.3.61")
	id("org.jetbrains.dokka") version "0.10.0"
	`maven-publish`
	signing
	id("io.codearte.nexus-staging") version "0.21.2"
	idea
}

group = "de.joshuagleitze"
version = project.findProperty("releaseVersion") ?: "SNAPSHOT"

repositories {
	jcenter()
}

dependencies {
	implementation(kotlin(module = "stdlib"))

	testImplementation(kotlin("reflect"))
	testImplementation(name = "atrium-cc-en_GB-robstoll", group = "ch.tutteli.atrium", version = "0.8.0")
	testImplementation(name = "junit-jupiter-api", group = "org.junit.jupiter", version = "5.5.1")
	testImplementation(name = "junit-jupiter-params", group = "org.junit.jupiter", version = "5.5.1")

	testRuntimeOnly(name = "junit-jupiter-engine", group = "org.junit.jupiter", version = "5.5.1")
}

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

val dokka by tasks

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

val ossrhUsername: String? by project
val ossrhPassword: String? by project
val githubRepository: String? by project
val githubOwner = githubRepository?.split("/")?.get(0)
val githubToken: String? by project

lateinit var publication: MavenPublication

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
		if (project.isSnapshot) {
			maven("https://maven.pkg.github.com/$githubOwner/REPOSITORY")
		} else {
			maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
				name = "OSSRH-Staging"
				credentials {
					username = ossrhUsername
					password = ossrhPassword
				}
			}
		}
	}
}

signing {
	val signingKeyId: String? by project
	val signingKey: String? by project
	val signingKeyPassword: String? by project
	useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassword)
	sign(publication)
}

nexusStaging {
	username = ossrhUsername
	password = ossrhPassword
}

val closeAndPromoteRepository by project.tasks
val publish by tasks

task("release") {
	group = "release"
	description = "Publishes the project to Maven Central"
	dependsOn(publish)
	dependsOn(closeAndPromoteRepository)
	closeAndPromoteRepository.mustRunAfter(publish)
}

idea {
	module {
		isDownloadJavadoc = true
		isDownloadSources = true
	}
}

val Project.isSnapshot get() = this.version == "SNAPSHOT"
