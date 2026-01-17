plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    jacoco
    id("org.sonarqube") version "6.0.1.5171"
}

group = "com.uxstudio"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springBootAdminVersion"] = "3.4.1"
extra["springCloudVersion"] = "2024.0.0"
extra["springBootAdminVersion"] = "3.4.1"
extra["springCloudVersion"] = "2024.0.0"
extra["awsSdkVersion"] = "2.25.0"
extra["mockkVersion"] = "1.13.13"
extra["springDocVersion"] = "2.8.5"

dependencies {
    // Web & Core (Versões geradas pelo Spring Boot Starter Parent)
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Persistence & Cloud
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("software.amazon.awssdk:s3")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")

    // Monitoring & UI
    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("de.codecentric:spring-boot-admin-starter-client")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springDocVersion")}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("software.amazon.awssdk:bom:${property("awsSdkVersion")}")
    }
}

val coverageExclusions = listOf(
    "**/config/**",
    "**/*Application*",
    "**/dto/**",
    "**/domain/ports/**",
    "**/persistence/**",
    "**/*Entity*"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true) // Obrigatório para o SonarQube
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude(coverageExclusions) }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // Threshold de 80% sobre o código filtrado
            }
        }
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude(coverageExclusions) }
        })
    )
}

sonar {
    properties {
        property("sonar.projectKey", "otavioabreu27_uxstudio")
        property("sonar.organization", "otavioabreu27")
        property("sonar.projectName", "uxstudio")
        property("sonar.host.url", "https://sonarcloud.io")

        // Sincronizamos as exclusões do Sonar com as do Jacoco
        property("sonar.exclusions", coverageExclusions.joinToString(","))
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // Gera o relatório automaticamente após os testes
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
