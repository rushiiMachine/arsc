plugins {
	kotlin("jvm") version "1.9.22"
	id("maven-publish")
	id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.14.0"
}

version = "1.0.0"

kotlin {
	explicitApi()

	compilerOptions.freeCompilerArgs.addAll(
		"-opt-in=kotlin.ExperimentalUnsignedTypes",
		"-opt-in=dev.rushii.arsc.internal.ArscInternalApi"
	)
}

apiValidation {
	ignoredPackages += "dev.rushii.arsc.internal"
	nonPublicMarkers += "dev.rushii.arsc.internal.ArscInternalApi"
}

tasks.test {
	useJUnitPlatform()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
	testImplementation(kotlin("test"))
}

publishing {
	repositories {
		mavenLocal()
	}

	publications {
		register(project.name, MavenPublication::class) {
			artifactId = "arsc"
			groupId = "dev.materii.rushii"

			artifact(tasks["jar"])
			artifact(tasks["kotlinSourcesJar"])
		}
	}
}
