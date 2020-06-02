package net.highteq.cylab.kotlindslbuilder

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import java.io.File

open class DSLBuilderExtension(val project: Project){

  internal val dsls = mutableListOf<DSLConfig>()

  fun dsl(config: DSLConfig.() -> Unit) {
    dsls.add( DSLConfig().apply(config) )
  }

  fun dsl(config: Closure<Unit>) {
    dsls.add( ConfigureUtil.configureSelf(config, DSLConfig()) )
  }
}

internal val Project.dslBuilder: DSLBuilderExtension get() =
  extensions.getByName(EXTENSION_NAME) as? DSLBuilderExtension
    ?: throw IllegalStateException("$EXTENSION_NAME is not of the correct type")

class DSLConfig {
  var sourcePackage: String? = null
  var targetPackage: String? = null
  var xmlDoc: File? = null
  var outputDir: File? = null
  var classpath: Collection<File>? = null
}
