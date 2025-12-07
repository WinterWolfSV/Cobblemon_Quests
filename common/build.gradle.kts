plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("neoforge", "fabric")
    platformSetupLoomIde()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modCompileOnly("dev.architectury:architectury-fabric:${property("architectury_version")}") { isTransitive = false }
    modCompileOnly("com.cobblemon:fabric:${property("cobblemon_version")}") { isTransitive = false }
    modCompileOnly("dev.ftb.mods:ftb-quests:${property("ftb_quests_version")}") { isTransitive = false }
    modCompileOnly("dev.ftb.mods:ftb-teams:${property("ftb_teams_version")}") { isTransitive = false }
    modCompileOnly("dev.ftb.mods:ftb-library:${property("ftb_lib_version")}") { isTransitive = false }
}