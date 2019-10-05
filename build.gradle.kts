import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*


plugins  {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    id("com.github.ben-manes.versions") version "0.25.0"
}


group = "com.apurebase"
version = "0.6.6"

repositories  {
    jcenter()
}

dependencies {
    val kotlinVersion = "1.3.50"
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    compile("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")

    compile("com.fasterxml.jackson.core:jackson-databind:2.9.7")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")

    compile("com.github.ben-manes.caffeine:caffeine:1.0.0")

    testCompile("org.jetbrains.kotlinx:kotlinx-html-jvm:0.6.3")
    testCompile("io.netty:netty-all:4.1.9.Final")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testCompile("org.hamcrest:hamcrest-all:1.3")
    testCompile("org.amshove.kluent:kluent:1.56")
}


tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    test {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
        }
        testLogging {
            events = setOf(FAILED, PASSED, SKIPPED, STANDARD_ERROR, STANDARD_OUT)
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
    named<DependencyUpdatesTask>("dependencyUpdates") {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea").any { qualifier ->
                        candidate.version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                    }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

}
