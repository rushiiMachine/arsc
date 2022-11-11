import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
}

group = "com.github.diamondminer88"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
	explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
