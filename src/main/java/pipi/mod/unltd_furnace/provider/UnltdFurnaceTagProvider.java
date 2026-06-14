package pipi.mod.unltd_furnace.provider;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import pipi.mod.unltd_furnace.ModInfo;
import pipi.mod.unltd_furnace.object.UnltdFurnaceBlocks;

public class UnltdFurnaceTagProvider extends BlockTagsProvider {

	public UnltdFurnaceTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, ModInfo.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
		IntrinsicTagAppender<Block> tag = this.tag(BlockTags.MINEABLE_WITH_PICKAXE);
		UnltdFurnaceBlocks.BLOCKS.getEntries().forEach(
				block -> tag.add(block.get())
		);
	}

}
