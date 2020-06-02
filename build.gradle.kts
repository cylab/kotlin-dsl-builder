/*
	This file as well as the whole project is licensed under
	Apache License Version 2.0
	See LICENSE.txt for more info
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
}

allprojects {
    repositories {
        jcenter() 
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xinline-classes", "-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "net.highteq.cylab"
    version = "1.0-SNAPSHOT"
    java.sourceCompatibility = JavaVersion.VERSION_1_8

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.10")
        testImplementation("com.google.guava:guava:29.0-jre")
    }
}


