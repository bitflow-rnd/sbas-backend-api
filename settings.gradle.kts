pluginManagement {
  val quarkusPluginVersion: String by settings
  val quarkusPluginId: String by settings
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
    google()
  }
  plugins {
    id(quarkusPluginId) version quarkusPluginVersion
  }
}
rootProject.name = "sbas-backend-api"
