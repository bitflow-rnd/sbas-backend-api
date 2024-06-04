import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion = "1.9.22"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.noarg") version kotlinVersion
  kotlin("plugin.allopen") version kotlinVersion
  id("io.quarkus")
  id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

repositories {
  mavenCentral()
  mavenLocal()
  maven {
    // https://github.com/gradle/kotlin-dsl-samples/issues/1109
    url = uri("artifactregistry://us-central1-maven.pkg.dev/gen8id/maven")
  }
}

group = "org.sbas"
version = "0.9.1"

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val isLocal: String by project

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

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.3")
  implementation("com.google.firebase:firebase-admin:9.1.1")
  implementation("com.google.code.gson:gson:2.10.1")

  implementation("com.linecorp.kotlin-jdsl:hibernate-support:3.4.1")
  implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.4.1")
  implementation("com.linecorp.kotlin-jdsl:jpql-render:3.4.1")
  implementation("io.seruco.encoding:base62:0.1.3")
  quarkusDev("org.jetbrains.kotlin:kotlin-allopen-compiler-plugin:1.9.22")

  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("io.rest-assured:rest-assured")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

allOpen {
  annotation("jakarta.ws.rs.Path")
  annotation("jakarta.enterprise.context.ApplicationScoped")
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.Embeddable")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

noArg {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.Embeddable")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("org.sbas.utils.annotation.NoArg")
}

tasks.withType<Test> {
  systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
  //exclude("ai/bitflow/api/**")
  //exclude("id/g8id/api/**")
}

tasks.compileTestKotlin {
  //exclude("ai/bitflow/api/**")
  //exclude("id/g8id/api/**")
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  options.compilerArgs.add("-parameters")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
  kotlinOptions.javaParameters = true
}

noArg {
  annotation("org.sbas.utils.annotation.NoArg")
}

tasks.quarkusDev {
  compilerOptions {
    compiler("kotlin").args(
      listOf(
        "-Xplugin=${configurations.quarkusDev.get().files.find { "kotlin-allopen-compiler-plugin" in it.name }}",
        "-P=plugin:org.jetbrains.kotlin.allopen:annotation=jakarta.ws.rs.Path",
        "-P=plugin:org.jetbrains.kotlin.allopen:annotation=jakarta.enterprise.context.ApplicationScoped",
        "-P=plugin:org.jetbrains.kotlin.allopen:annotation=jakarta.persistence.Entity",
        "-P=plugin:org.jetbrains.kotlin.allopen:annotation=io.quarkus.test.junit.QuarkusTest",
      )
    )
  }
}