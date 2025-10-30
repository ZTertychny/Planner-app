plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.depman)
    alias(libs.plugins.lombok)
    id("java")
}


dependencies {
    implementation(libs.bundles.spring.web)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgres)
    implementation(project(":libs:contracts"))
    implementation(libs.logstash.logback.encoder)
    implementation(libs.liquibase)
    runtimeOnly(libs.postgres)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.named("jar") { enabled = false }   // выключаем plain JAR