package net.redfox.metalica.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.block.ModBlocks;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

  public ModBlockLootTables() {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags());
  }

  @Override
  protected void generate() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      this.dropSelf(material.getStorageBlock().get());
      this.dropSelf(material.getRawStorageBlock().get());
      if (!material.hasOre()) return;
      this.add(material.getStoneOre().get(), block -> createOreDrop(material.getStoneOre().get(), material.getRaw().get()));
      this.add(material.getDeepslateOre().get(), block -> createOreDrop(material.getDeepslateOre().get(), material.getRaw().get()));
    }
  }

  @Override
  protected @NotNull Iterable<Block> getKnownBlocks() {
    return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
  }
}