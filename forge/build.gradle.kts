import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.archivesName

plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    platformSetupLoomIde()
    forge()
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
    modApi("dev.architectury:architectury-forge:9.1.12")

    forge("net.minecraftforge:forge:1.20.1-47.2.0")

    implementation(project(":common", configuration = "namedElements"))?.let { include(it) }
    "developmentForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }

    modImplementation("com.cobblemon:forge:1.4.0+1.20.1-SNAPSHOT")
    implementation("thedarkcolour:kotlinforforge:4.4.0")


    modImplementation("curse.maven:ftb-quests-forge-289412:4816382")
    modImplementation("curse.maven:ftb-library-forge-404465:4720056")
    modImplementation("curse.maven:ftb-teams-forge-404468:4623116")
    modImplementation("curse.maven:item-filters-309674:4838266")

}

tasks.processResources{
    filesMatching("META-INF/mods.toml"){
        expand(mapOf(
                "mod_name" to project.property("mod_name"),
                "mod_id" to project.property("mod_id"),
                "version" to project.property("mod_version"),
                "mod_description" to project.property("mod_description"),
                "repository" to project.property("repository"),
                "license" to project.property("license"),
                "mod_icon" to project.property("mod_icon"),
                "environment" to project.property("environment"),
                "supported_minecraft_versions" to project.property("supported_minecraft_versions")
        ))
    }
}

tasks.remapJar{
    // "${property("")}"
    archiveBaseName.set("${project.property("archives_base_name")}-forge")
}