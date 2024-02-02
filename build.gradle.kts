plugins {
	kotlin("jvm") version "1.9.22"
	id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.14.0"
	id("land.sungbin.kotlin.dataclass.nocopy.plugin") version "1.0.4"
}

group = "com.github.diamondminer88"
version = "1.0.0"

kotlin {
	explicitApi()

	compilerOptions.freeCompilerArgs.addAll(
		"-opt-in=kotlin.ExperimentalUnsignedTypes",
		"-opt-in=com.github.diamondminer88.arsc.ArscInternalApi"
	)
}

apiValidation {
	ignoredPackages += "com.github.diamondminer88.arsc.internal"
	nonPublicMarkers += "com.github.diamondminer88.arsc.ArscInternalApi"
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
