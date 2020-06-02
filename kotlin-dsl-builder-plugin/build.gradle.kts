/*
	This file as well as the whole project is licensed under
	Apache License Version 2.0
	See LICENSE.txt for more info
 */

plugins {
	id("java-gradle-plugin")
}

gradlePlugin {
	plugins {
		create(rootProject.name) {
			println("${group}.${rootProject.name}")
			id = "${group}.${rootProject.name}"
			implementationClass = "net.highteq.cylab.kotlindslbuilder.DSLBuilderPlugin"
		}
	}
}

dependencies {
	implementation(project(":kotlin-dsl-builder-lib"))
	implementation("org.reflections:reflections:0.9.12")
	implementation("com.github.markusbernhardt:xml-doclet:1.0.5")
	compileOnly(kotlin("gradle-plugin"))
}
