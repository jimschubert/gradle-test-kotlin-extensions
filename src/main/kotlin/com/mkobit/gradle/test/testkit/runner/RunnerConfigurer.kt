package com.mkobit.gradle.test.testkit.runner

import org.gradle.testkit.runner.GradleRunner

/**
 * A consumer of a `GradleRunner` that provides additional context.
 *
 * Implementations must provide a no-args constructor if they intend to be loaded using the default
 * [ServiceLoaderRunnerConfigurer].
 */
interface RunnerConfigurer : Function1<GradleRunner, Unit>
