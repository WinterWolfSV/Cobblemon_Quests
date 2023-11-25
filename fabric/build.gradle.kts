plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modApi("dev.architectury:architectury-fabric:9.1.12")
    modApi("teamreborn:energy:3.0.0")


    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")


    modImplementation(fabricApi.module("fabric-command-api-v2", "0.89.3+1.20.1"))
    implementation(project(":common", configuration = "namedElements"))?.let { include(it) }
    "developmentFabric"(project(":common", configuration = "namedElements"))


    modImplementation("com.cobblemon:fabric:1.4.0+1.20.1-SNAPSHOT")
    modImplementation("curse.maven:ftb-quests-fabric-438496:4760285")
    modImplementation("curse.maven:ftb-teams-fabric-438497:4623115")
    modImplementation("curse.maven:ftb-library-fabric-438495:4720055")
    modImplementation("curse.maven:item-filters-309674:4838265")

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

tasks.remapJar{
    archiveBaseName.set("${project.property("archives_base_name")}-fabric")
}
