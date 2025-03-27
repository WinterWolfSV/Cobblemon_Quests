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
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")

    mappings(loom.officialMojangMappings())

    modApi("dev.architectury:architectury-fabric:${property("architectury_version")}")
    modApi("teamreborn:energy:4.1.0")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin")}")
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    modImplementation(fabricApi.module("fabric-command-api-v2", "0.89.3+1.20.1"))


    "common"(project(":common", "namedElements")) { isTransitive = false }
    "shadowCommon"(project(":common", "transformProductionFabric")) { isTransitive = false }


    modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-quests-fabric/files/
    modImplementation("curse.maven:ftb-quests-fabric-438496:5882271")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-teams-fabric/files/
    modImplementation("curse.maven:ftb-teams-fabric-438497:5882218")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-library-fabric/files/
    modImplementation("curse.maven:ftb-library-fabric-438495:5893688")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-chunks-fabric/files/
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