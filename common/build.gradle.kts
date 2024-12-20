plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
    platformSetupLoomIde()
}

@Suppress("UnstableApiUsage")
loom.mixin {
    defaultRefmapName.set("cobblemon_quests-${project.name}.refmap.json")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") { isTransitive = false }



    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modCompileOnly("dev.architectury:architectury-fabric:${property("architectury_version")}")
    modCompileOnly("com.cobblemon:fabric:${property("cobblemon_version")}")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-quests-fabric/files/
    modCompileOnly("curse.maven:ftb-quests-fabric-438496:5882271")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-teams-fabric/files/
    modCompileOnly("curse.maven:ftb-teams-fabric-438497:5882218")
    // https://www.curseforge.com/minecraft/mc-mods/ftb-library-fabric/files/
    modCompileOnly("curse.maven:ftb-library-fabric-438495:5893688")
}