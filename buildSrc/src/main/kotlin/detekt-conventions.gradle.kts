import gradle.kotlin.dsl.accessors._d67b3de0423560813212b9eec988af78.detekt
import gradle.kotlin.dsl.accessors._d67b3de0423560813212b9eec988af78.detektPlugins
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
}

detekt {
    config = files("${rootDir}/buildSrc/src/main/resources/detekt-config.yml")
    parallel = true
    ignoreFailures = false
    autoCorrect = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        txt.required.set(false)
        xml.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
    jvmTarget = "11"
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "11"
}
