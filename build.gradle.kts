import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.graalvm.buildtools.native") version "0.10.2"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "cash.atto"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    mavenLocal()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude(group = "commons-logging", module = "commons-logging")
    }
}

dependencies {
    val commonsVersion = "2.14.0"
    val cucumberVersion = "7.18.0"

    implementation("cash.atto:commons:$commonsVersion")
    implementation("cash.atto:commons:$commonsVersion") {
        capabilities {
            requireCapability("cash.atto:commons-json")
        }
    }
    implementation("cash.atto:commons:$commonsVersion") {
        capabilities {
            requireCapability("cash.atto:commons-opencl")
        }
    }

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")

    testImplementation("org.junit.platform:junit-platform-suite") // for cucumber
    testImplementation("io.cucumber:cucumber-java:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-spring:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
    testImplementation("org.awaitility:awaitility:4.2.1")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("--static")
            buildArgs.add("--libc=musl")
            buildArgs.add("--gc=G1")
            buildArgs.add("--strict-image-heap")
        }
    }
}
