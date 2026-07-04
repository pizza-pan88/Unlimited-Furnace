package pipi.mod.unltd_furnace.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import pipi.mod.unltd_furnace.UnlimitedFurnace;

public class UnltdFurnacePacketHandler {
	private static final Integer PROTOCOL_VERSION = Integer.valueOf(1);

	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
					UnlimitedFurnace.locate("packets"),
					PROTOCOL_VERSION::toString,
					PROTOCOL_VERSION.toString()::equals,
					PROTOCOL_VERSION.toString()::equals
	);

	private static int id = 0;
	public static void register() {
		add(new PacketCookingRecipeDefiner.Handler());
	}
	
	private static <T> void add(PacketHandler<T> packet) {
		packet.addToChannel(INSTANCE, id++);
	}
}
