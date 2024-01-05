plugins {
    val kotlinVersion = "1.9.21"
    java
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-resteasy-reactive")
    implementation("io.quarkus:quarkus-rest-client-reactive")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    implementation("io.quarkus:quarkus-rest-client-reactive-jaxb")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-logging-json")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("org.json:json:20231013")
    implementation("io.quarkus:quarkus-websockets")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging")
//    implementation("io.quarkus:quarkus-scheduler")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.3")
    implementation("com.google.firebase:firebase-admin:9.1.1")

    implementation("com.linecorp.kotlin-jdsl:hibernate-support:3.2.0")
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.2.0")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.2.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "org.sbas"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

noArg {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("org.sbas.utils.annotation.NoArg")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}