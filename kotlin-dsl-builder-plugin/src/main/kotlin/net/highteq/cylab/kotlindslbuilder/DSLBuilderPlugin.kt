package net.highteq.cylab.kotlindslbuilder
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.reflections.util.ClasspathHelper
import java.io.File

internal val EXTENSION_NAME = "dslBuilder"

class DSLBuilderPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create(
      EXTENSION_NAME,
      DSLBuilderExtension::class.java,
      project)

    val dslSources = project.configurations.create("dslSources") { config ->
      config.isTransitive = false
    }

    val downloadDSLSources = project.tasks.register("downloadDSLSources", Copy::class.java) { copy ->
      copy.group = "dsl"
      copy.description = "Downloads configured DSL sources"
      dslSources.resolvedConfiguration.resolvedArtifacts.forEach {
        copy.into(File(project.buildDir, "dslSources"))
        copy.from(project.zipTree(it.file.absolutePath)) { spec ->
          spec.into(it.name)
        }
      }
    }

    project.afterEvaluate {
      val main = project.convention.getPlugin(JavaPluginConvention::class.java)
        .sourceSets
        .findByName("main")

      val none = emptyList<File>()
      val mainClasspath = (main?.compileClasspath?: none) + (main?.runtimeClasspath?: none)

      // add the default generated path to the main sources
      main?.java?.srcDir(File(project.buildDir, "generated/kotlin-dsls"))

      // the dependencies should be in the plugin classpath
      val xmlDocletDeps = ClasspathHelper.forClassLoader().filter {
        it.path.contains("xml-doclet") || it.path.contains("slf4j") || it.path.contains("commons")
      }
      val xmlDocletPath = project.files(xmlDocletDeps).toList()

      val dslSourceFolders = dslSources.resolvedConfiguration.resolvedArtifacts
        .map { project.fileTree(File(project.buildDir, "dslSources/${it.name}")) as FileTree }

      val analyseDSLSources = project.tasks.create("analyseDSLSources", Javadoc::class.java) { docs ->
        docs.group = "dsl"
        docs.description = "Generates an xml documentation of the configured DSL sources"
        docs.title = "" // to prevent "invalid flag: -doctitle" with the doclet
        if(dslSourceFolders.isNotEmpty()) {
          docs.source = dslSourceFolders.reduce { acc, tree -> acc + tree }
            .matching { filter -> filter.include("**/*.java") }
        }
        docs.classpath = project.files(mainClasspath)
        docs.setDestinationDir(File("${project.buildDir}/dslDocs"))
        with(docs.options as StandardJavadocDocletOptions) {
          doclet = "com.github.markusbernhardt.xmldoclet.XmlDoclet"
          docletpath = xmlDocletPath
          addStringOption("filename", "docs.xml")
          noTimestamp(false) // to prevent "invalid flag: -notimestamp" with the doclet
        }
        docs.dependsOn(downloadDSLSources)
      }
      val generateDSLs = project.tasks.register("generateDSLs", GenerateDSLsTask::class.java) { generate ->
        generate.dependsOn(analyseDSLSources)
      }
      project.tasks.withType(KotlinCompile::class.java) {
        compileTask -> compileTask.dependsOn(generateDSLs)
      }
    }
  }
}
