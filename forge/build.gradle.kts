import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.archivesName

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    forge()
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentForge").extendsFrom(configurations["common"])
}

loom {
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("cobblemon_quests-${project.name}.refmap.json")
    }
    forge {
        mixinConfig("cobblemon_quests-common.mixins.json")
    }
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()


}

dependencies {
    minecraft("net.minecraft:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    //https://www.curseforge.com/minecraft/mc-mods/architectury-api/files
    modApi("dev.architectury:architectury-forge:6.6.92")

    //https://files.minecraftforge.net/net/minecraftforge/forge/
    forge("net.minecraftforge:forge:1.19.2-43.4.0")

    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionForge")) { isTransitive = false }

    // Find this version on https://maven.impactdev.net/#browse/browse:development:com%2Fcobblemon
    modImplementation("com.cobblemon:forge:1.3.0+1.19.2")

    //https://www.curseforge.com/minecraft/mc-mods/kotlin-for-forge/files/
    implementation("thedarkcolour:kotlinforforge:3.12.0")

    //https://www.curseforge.com/minecraft/mc-mods/ftb-quests-forge/files
    modImplementation("curse.maven:ftb-quests-forge-289412:5417957")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-library-forge/files
    modImplementation("curse.maven:ftb-library-forge-404465:4661834")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-teams-forge/files
    modImplementation("curse.maven:ftb-teams-forge-404468:4611938")
    //https://www.curseforge.com/minecraft/mc-mods/item-filters/files
    modImplementation("curse.maven:item-filters-309674:4553326")

}

tasks.processResources {
    filesMatching("META-INF/mods.toml") {
        expand(
            mapOf(
                "mod_name" to project.property("mod_name"),
                "mod_id" to project.property("mod_id"),
                "version" to project.property("mod_version"),
                "mod_description" to project.property("mod_description"),
                "repository" to project.property("repository"),
                "license" to project.property("license"),
                "mod_icon" to project.property("mod_icon"),
                "environment" to project.property("environment"),
                "supported_minecraft_versions" to project.property("supported_minecraft_versions")
            )
        )
    }
}

tasks {
    base.archivesName.set("${project.property("archives_base_name")}-forge")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("fabric.mod.json")
        exclude("generations/gg/generations/core/generationscore/forge/datagen/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")
}