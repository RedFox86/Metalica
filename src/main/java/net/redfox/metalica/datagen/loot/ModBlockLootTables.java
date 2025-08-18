/* (C)2025 */
package net.redfox.metalica.datagen.loot;

import java.util.Set;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.block.ModBlocks;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.NotNull;

public class ModBlockLootTables extends BlockLootSubProvider {

  public ModBlockLootTables() {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags());
  }

  @Override
  protected void generate() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      this.dropSelf(material.getStorageBlock().get());
      if (!material.hasOre()) continue;
      this.dropSelf(material.getRawStorageBlock().get());
      this.add(
          material.getStoneOre().get(),
          block -> createOreDrop(material.getStoneOre().get(), material.getRaw().get()));
      this.add(
          material.getDeepslateOre().get(),
          block -> createOreDrop(material.getDeepslateOre().get(), material.getRaw().get()));
    }
  }

  @Override
  protected @NotNull Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
  }
}
