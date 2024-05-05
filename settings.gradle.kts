rootProject.name = "common"

include(
    "easy-random",
    "logging",
    "model",
    "scope"
)

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("lib") {
            version("log4j", "2.23.1")
            version("uom", "2.1")

            library("apache-commons-text", "org.apache.commons:commons-text:1.12.0")
            library("conventions-kotlin", "dev.tcheng:conventions-kotlin:0.0.1")
            library("easyrandom", "org.jeasy:easy-random-core:5.0.0")

            library("junit", "org.junit.jupiter:junit-jupiter:5.10.2")
            library("log4j-api", "org.apache.logging.log4j", "log4j-api").versionRef("log4j")
            library("log4j-api-kotlin", "org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
            library("log4j-core", "org.apache.logging.log4j", "log4j-core").versionRef("log4j")
            library("mockk", "io.mockk:mockk-jvm:1.13.10")
            library("uom-quantity", "systems.uom", "systems-quantity").versionRef("uom")
            library("uom-unicode", "systems.uom", "systems-unicode").versionRef("uom")
        }
    }
}
