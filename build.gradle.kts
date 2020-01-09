import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm").version("1.3.61")
	idea
}

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


idea {
	module {
		isDownloadJavadoc = true
		isDownloadSources = true
	}
}
