package net.redfox.metalica.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

  public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
    super(pOutput, pLookupProvider, pBlockTags, Metalica.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider pProvider) {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      this.tag(material.getDustTag()).add(material.getDust().get());
      this.tag(material.getIngotTag()).add(material.getIngot().get());
      this.tag(material.getNuggetTag()).add(material.getNugget().get());
      this.tag(material.getStorageBlockTag()).add(material.getStorageBlock().get().asItem());
      if (!material.hasOre()) continue;
      this.tag(material.getOreTag()).add(material.getStoneOre().get().asItem(), material.getDeepslateOre().get().asItem());
      this.tag(material.getRawTag()).add(material.getRaw().get());
      this.tag(material.getRawStorageBlockTag()).add(material.getRawStorageBlock().get().asItem());
    }
  }
}