plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.depman)
    alias(libs.plugins.lombok)
}

val telegram = "6.9.7.1"

dependencies {
    implementation(libs.bundles.spring.web)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation("org.telegram:telegrambots:$telegram")
    implementation(project(":libs:contracts"))
    testImplementation(libs.spring.boot.starter.test)
}

tasks.named("jar") { enabled = false }   // выключаем plain JAR