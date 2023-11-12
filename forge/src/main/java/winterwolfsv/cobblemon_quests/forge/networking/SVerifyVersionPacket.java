package winterwolfsv.cobblemon_quests.forge.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SVerifyVersionPacket {
    private final String version;

    public SVerifyVersionPacket(String version) {
        this.version = version;
    }
    public SVerifyVersionPacket(PacketByteBuf buf) {
        this.version = buf.readString();
    }
    public void encode(PacketByteBuf buf) {
        buf.writeString(version);
    }
    public void handle(NetworkEvent.ServerCustomPayloadEvent event) {

    }

}
