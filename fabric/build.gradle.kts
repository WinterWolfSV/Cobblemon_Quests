plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common")
    create("shadowCommon")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentFabric").extendsFrom(configurations["common"])
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()

    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("cobblemon_quests-${project.name}.refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    //https://www.curseforge.com/minecraft/mc-mods/architectury-api/files
    modApi("dev.architectury:architectury-fabric:6.6.92")
    modApi("teamreborn:energy:2.3.0")


    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    modImplementation(fabricApi.module("fabric-command-api-v2", "0.76.1+1.19.2"))


    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionFabric")) { isTransitive = false }


    // Find this version on https://maven.impactdev.net/#browse/browse:development:com%2Fcobblemon
    modImplementation("com.cobblemon:fabric:1.3.0+1.19.2")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-quests-fabric/files
    modImplementation("curse.maven:ftb-quests-fabric-438496:5417955")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-teams-fabric/files
    modImplementation("curse.maven:ftb-teams-fabric-438497:4611937")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-library-fabric/files
    modImplementation("curse.maven:ftb-library-fabric-438495:4661833")
    //https://www.curseforge.com/minecraft/mc-mods/item-filters/files
    modImplementation("curse.maven:item-filters-309674:4553325")
}

tasks.processResources {
    expand(mapOf(
            "mod_name" to project.property("mod_name"),
            "mod_id" to project.property("mod_id"),
            "mod_version" to project.property("mod_version"),
            "mod_description" to project.property("mod_description"),
            "repository" to project.property("repository"),
            "license" to project.property("license"),
            "mod_icon" to project.property("mod_icon"),
            "environment" to project.property("environment"),
            "supported_minecraft_versions" to project.property("supported_minecraft_versions")
    ))
}


tasks {
    base.archivesName.set("${project.property("archives_base_name")}-fabric")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("generations/gg/generations/core/generationscore/fabric/datagen/**")
        exclude("data/forge/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar.get().archiveClassifier.set("dev")
}
