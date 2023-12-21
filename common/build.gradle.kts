plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("forge", "fabric")
    platformSetupLoomIde()
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")

    modCompileOnly("com.cobblemon:mod:1.4.0+1.20.1-SNAPSHOT") {
        isTransitive = false
    }

    // alL fabric dependencies:
    modCompileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    modCompileOnly("dev.architectury:architectury-fabric:9.1.12")
    modCompileOnly("com.cobblemon:fabric:1.4.0+1.20.1-SNAPSHOT")
    modCompileOnly("curse.maven:ftb-quests-fabric-438496:4760285")
    modCompileOnly("curse.maven:ftb-teams-fabric-438497:4623115")
    modCompileOnly("curse.maven:ftb-library-fabric-438495:4720055")
    modCompileOnly("curse.maven:item-filters-309674:4838265")

}
