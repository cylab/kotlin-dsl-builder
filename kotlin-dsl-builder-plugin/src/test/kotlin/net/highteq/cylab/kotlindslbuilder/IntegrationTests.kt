package net.highteq.cylab.kotlindslbuilder

import io.kotlintest.specs.WordSpec
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files

class IntegrationTests : WordSpec({

  "A configured Groovy DSL builscript" should {
    "apply the plugin" {
      val projectDir = Files.createTempDirectory("")
      val buildScript = projectDir.resolve("build.gradle").toFile()
      buildScript.writeText("""
        plugins {
          id 'net.highteq.cylab.kotlin-dsl-builder'
        }
        
        dslBuilder {
          dsl { name = "foo" }
          dsl { name = "bar" }
        }
      """.trimIndent())

      val result = GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withPluginClasspath()
        .withArguments("generateDSLs", "--info", "--stacktrace")
        .build()

//      result.task(":startLocalElasticMq") should {
//        it != null && it.outcome == TaskOutcome.SUCCESS
//      }
//      result.task(":stopLocalElasticMq") should {
//        it != null && it.outcome == TaskOutcome.SUCCESS
//      }
//      result.output.shouldContain("Starting ElasticMQ local server instance")
//      result.output.shouldContain("Stopping ElasticMQ local server instance")
      println(result.output)
    }
  }
})
