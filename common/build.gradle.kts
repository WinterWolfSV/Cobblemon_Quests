plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

@Suppress("UnstableApiUsage")
loom.mixin {
    defaultRefmapName.set("cobblemon_quests-${project.name}.refmap.json")
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    // Find this version on https://maven.impactdev.net/#browse/browse:development:com%2Fcobblemon
    modCompileOnly("com.cobblemon:mod:1.3.0+1.19.2")


    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    //https://www.curseforge.com/minecraft/mc-mods/architectury-api/files
    modCompileOnly("dev.architectury:architectury-fabric:6.6.92")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-quests-fabric/files
    modCompileOnly("curse.maven:ftb-quests-fabric-438496:5417955")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-teams-fabric/files
    modCompileOnly("curse.maven:ftb-teams-fabric-438497:4611937")
    //https://www.curseforge.com/minecraft/mc-mods/ftb-library-fabric/files
    modCompileOnly("curse.maven:ftb-library-fabric-438495:4661833")
    //https://www.curseforge.com/minecraft/mc-mods/item-filters/files
    modCompileOnly("curse.maven:item-filters-309674:4553325")

}
