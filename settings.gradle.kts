pluginManagement {
    repositories {
        mavenLocal()
//        mavenCentral()
        maven { url = uri("https://nexusprod.onemrva.priv/repository/maven-public/") }
    }
}

buildscript {
    repositories {
        mavenLocal()
//        mavenCentral()
        maven { url = uri("https://nexusprod.onemrva.priv/repository/maven-public/") }
    }
}

rootProject.name = "advent-2025"
