plugins {
    val kotlinVersion = "2.3.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.springframework.boot") version "4.0.2"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

group = "cash.atto"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    val commonsVersion = "6.3.1"
    val cucumberVersion = "7.23.0"

    implementation("cash.atto:commons-worker-opencl:$commonsVersion")
    implementation("cash.atto:commons-spring-boot-starter:$commonsVersion")

    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testImplementation("cash.atto:commons-worker-remote:$commonsVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.junit.platform:junit-platform-suite") // for cucumber
    testImplementation("io.cucumber:cucumber-java:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-spring:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
    testImplementation("org.awaitility:awaitility:4.3.0")

    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("io.github.oshai:kotlin-logging:7.0.13")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
