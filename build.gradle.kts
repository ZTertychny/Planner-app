plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.depman) apply false
}

subprojects {
    group = "planner-app"
    version = "0.1.0"

    repositories { mavenCentral() }

    // Общие настройки Java
    plugins.withId("java") {
        the<JavaPluginExtension>().toolchain {
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
        }
        tasks.withType<Test>().configureEach { useJUnitPlatform() }
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(libs.versions.java.get().toInt())
        }

        // ВАЖНО: BOM'ы и layered-jar подключаем только когда ЕСТЬ и java, и boot
        plugins.withId("org.springframework.boot") {
            apply(plugin = "io.spring.dependency-management")

            dependencies {
                "implementation"(platform("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}"))
                "implementation"(platform("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud.bom.get()}"))
            }
        }
    }
}
