plugins {
	kotlin("jvm") version "1.9.22"
	id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.14.0"
}

group = "dev.materii.rushii.arsc"
version = "1.0.0"

kotlin {
	explicitApi()

	compilerOptions.freeCompilerArgs.addAll(
		"-opt-in=kotlin.ExperimentalUnsignedTypes",
		"-opt-in=dev.rushii.arsc.ArscInternalApi"
	)
}

apiValidation {
	ignoredPackages += "dev.rushii.arsc.internal"
	nonPublicMarkers += "dev.rushii.arsc.ArscInternalApi"
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
