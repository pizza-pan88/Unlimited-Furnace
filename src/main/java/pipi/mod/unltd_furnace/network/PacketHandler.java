package pipi.mod.unltd_furnace.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public interface PacketHandler<T> {
	
	/** Type of The Packet. */
	Class<T> getPacketType();

	/** The Encoder. */
	void encode(T packet, FriendlyByteBuf buf);

	/** The Decoder. */
	T decode(FriendlyByteBuf buf);
	
	/** Processing on the Network thread. */
	void handle(T packet, Supplier<NetworkEvent.Context> context);

	
	/** Add Packet to the Channel. */
	default void addToChannel(SimpleChannel channel, int id) {
		channel.messageBuilder(this.getPacketType(), id)
		.encoder(this::encode)
		.decoder(this::decode)
		.consumerNetworkThread(this::handle)
		.add();
	} 

}
