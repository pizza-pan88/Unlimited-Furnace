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


	public static void register() {
		INSTANCE.messageBuilder(PacketCookingRecipeDefiner.class, 0)
		.encoder(PacketCookingRecipeDefiner::encode)
		.decoder(PacketCookingRecipeDefiner::decode)
		.consumerMainThread(PacketCookingRecipeDefiner::handle)
		.add();
	}
}
