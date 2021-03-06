package com.mkobit.gradle.test.kotlin.examples

import com.mkobit.gradle.test.kotlin.testkit.runner.build
import com.mkobit.gradle.test.kotlin.testkit.runner.info
import com.mkobit.gradle.test.kotlin.testkit.runner.profile
import com.mkobit.gradle.test.kotlin.testkit.runner.projectDirPath
import com.mkobit.gradle.test.kotlin.testkit.runner.projectProperties
import com.mkobit.gradle.test.kotlin.testkit.runner.setupProjectDir
import com.mkobit.gradle.test.kotlin.testkit.runner.stacktrace
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import org.junitpioneer.jupiter.TempDirectory
import java.nio.file.Path

@ExtendWith(TempDirectory::class)
internal class MultipleBuildsAndCLIArgumentsExample {

  @Test
  internal fun `run multiple builds with different arguments`(@TempDirectory.TempDir directory: Path) {
    val gradleRunner = GradleRunner.create().apply {
      projectDirPath = directory
      setupProjectDir {
        "build.gradle" {
          content = """
            tasks.create('showProp') {
              logger.info('Look at this info log')
              println("Prop1 value: ${'$'}{project.findProperty('prop1')}")
              println("Prop2 present: ${'$'}{project.hasProperty('prop2')}")
            }
            """.trimIndent().toByteArray()
        }
        "settings.gradle"(content = "rootProject.name = 'example-repeat-build'")
      }
      stacktrace = true
      info = true
      profile = true
      projectProperties = mapOf(
          "prop1" to "myValue",
          "prop2" to null
      )
    }

    gradleRunner.build("showProp").let { buildResult ->
      assertThat(buildResult.output)
          .contains("Look at this info log")
          .contains("Prop1 value: myValue")
          .contains("Prop2 present: true")

    }
    gradleRunner.apply {
      info = false
      projectProperties -= setOf("prop2")
    }
    // arguments passed to 'build' are not persisted in the GradleRunner between builds
    gradleRunner.build("showProp").let { buildResult ->
      assertThat(buildResult.output)
          .doesNotContain("Look at this info log")
          .contains("Prop1 value: myValue")
          .contains("Prop2 present: false")
    }
  }
}
