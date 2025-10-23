rootProject.name = "Planner-app"
include(":services:bot-service", ":services:planner-service", ":libs:contracts")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}