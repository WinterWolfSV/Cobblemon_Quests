plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version ("1.7.10")

    id("dev.architectury.loom") version ("1.7-SNAPSHOT") apply false
    id("architectury-plugin") version ("3.4-SNAPSHOT") apply false
}

group = "${property("maven_group")}"

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    version = "${property("mod_version")}"
    group = "${property("maven_group")}"

    repositories {
        mavenCentral()
        maven("https://maven.impactdev.net/repository/development/")
        maven ("https://cursemaven.com")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }
}
