import org.springframework.boot.gradle.tasks.aot.ProcessAot

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("org.springframework.boot.aot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("com.google.cloud.tools.jib") version "3.4.3"
}

group = "io.github.artemptushkin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn("compileAotJava")
    dependsOn("processAotResources")

    from("build/classes/java/aot")
    from("build/generated/aotClasses")
    from("build/resources/aot")
}

jib {
    containerizingMode = "packaged"
    container {
        jvmFlags = listOf("-Dspring.aot.enabled=true")
        mainClass = "io.github.artemptushkin.performance.SpringBootAotCdsGradleJibExampleApplicationKt"
    }
    from {
        image = "eclipse-temurin:21-jre"
        platforms {
            platform {
                os = "linux"
                architecture = "arm64"
            }
            platform {
                os = "linux"
                architecture = "amd64"
            }
        }
    }
    extraDirectories {
        paths {
            path {
                setFrom("build/libs/cds")
                into = "/cds"
            }
        }
    }
}
