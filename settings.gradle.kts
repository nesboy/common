rootProject.name = "common"

include(
    "context",
    "model"
)

pluginManagement {
    includeBuild("gradle-plugin")
}
