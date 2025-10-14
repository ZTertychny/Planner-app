plugins {
    id("java-library")
    alias(libs.plugins.lombok)
}

group = "contracts"
version = "0.1.0"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    // Аннотации Jackson — достаточно на этапе компиляции
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    api("jakarta.validation:jakarta.validation-api:3.0.2")

//    annotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test { useJUnitPlatform() }
