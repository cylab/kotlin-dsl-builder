package net.highteq.cylab.kotlindslbuilder

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URLClassLoader

open class GenerateDSLsTask : DefaultTask() {

  init {
    group = "dsl"
    description = "Generated configured DSLs"
  }

  @TaskAction
  fun generateDSLs() {
    val main = project.convention.getPlugin(JavaPluginConvention::class.java)
      .sourceSets
      .findByName("main")

    val none = emptyList<File>()
    val mainClasspath = (main?.compileClasspath ?: none) + (main?.runtimeClasspath ?: none)

    project.dslBuilder.dsls.forEach { dsl ->
      var error = ""
      if (dsl.sourcePackage == null) error += "'sourcePackage' might not be null! "
      if (error.isNotEmpty()) throw IllegalArgumentException(error)

      val classpath = if (dsl.classpath?.isNotEmpty() == true)
        dsl.classpath!! else mainClasspath

      generateDSL(
        dsl.sourcePackage!!,
        dsl.targetPackage ?: (dsl.sourcePackage + "dsl"),
        dsl.xmlDoc ?: File(project.buildDir, "dslDocs/docs.xml"),
        dsl.outputDir ?: File(project.buildDir, "generated/kotlin-dsls"),
        classpath.mapNotNull {
          if (it.exists()) it.toURI().toURL() else null
        }
      )
    }
  }
}
