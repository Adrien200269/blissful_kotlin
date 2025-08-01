package com.adrien.blissfulcake.testing

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite that runs all Android instrumentation tests for the BlissfulCakes app.
 * This suite includes UI tests and integration tests.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    UITests::class,
    IntegrationTests::class
)
class AndroidTestSuite {
    // This class remains empty, it is used only as a holder for the above annotations
    // which tell JUnit to run all the specified test classes.
} 