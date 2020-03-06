
group = "org.jetbrains.kotlin.script.examples"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.70-eap-184"
}

val kotlinVersion: String by extra("1.3.70-eap-184")

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
}

sourceSets {
    test {
        dependencies {
            testImplementation("junit:junit:4.12")
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}