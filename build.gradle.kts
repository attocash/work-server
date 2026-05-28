plugins {
    val kotlinVersion = "2.3.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.springframework.boot") version "4.0.6"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
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
    val commonsVersion = "6.7.1"
    val cucumberVersion = "7.34.3"

    implementation("cash.atto:commons-worker-opencl:$commonsVersion")
    implementation("cash.atto:commons-spring-boot-starter:$commonsVersion")

    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:3.0.3")
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
    implementation("io.github.oshai:kotlin-logging:8.0.03")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
