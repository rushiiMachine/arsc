plugins {
	kotlin("jvm") version "1.9.22"
}

group = "com.github.diamondminer88"
version = "1.0.0"

kotlin {
	explicitApi()
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
