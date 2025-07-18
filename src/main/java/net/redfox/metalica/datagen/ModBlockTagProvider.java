package net.redfox.metalica.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import net.redfox.metalica.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

  public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, Metalica.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider pProvider) {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      this.tag(material.getStorageBlockMiningLevel()).add(material.getStorageBlock().get(), material.getRawStorageBlock().get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(material.getStorageBlock().get(), material.getRawStorageBlock().get());

      if (!material.hasOre()) return;

      this.tag(material.getOreMiningLevel()).add(material.getStoneOre().get(), material.getDeepslateOre().get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(material.getStoneOre().get(), material.getDeepslateOre().get());
    }
  }
}