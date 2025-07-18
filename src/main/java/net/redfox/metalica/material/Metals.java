package net.redfox.metalica.material;

import net.minecraft.tags.BlockTags;

public class Metals {
  public static MetalMaterial aluminum;
  public static MetalMaterial lead;
  public static MetalMaterial zinc;

  public static void createMaterials() {
    aluminum = new MetalMaterial.Builder("aluminum", 0x9ed2ff)
        .ore(4, 12, -64, 80, BlockTags.NEEDS_STONE_TOOL)
        .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
        .build();
    lead = new MetalMaterial.Builder("lead", 0x383f5e)
        .ore(7, 7, -32, 35, BlockTags.NEEDS_IRON_TOOL)
        .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
        .build();
    zinc = new MetalMaterial.Builder("zinc", 0xfaf7bb)
        .ore(3, 16, -24, 60, BlockTags.NEEDS_STONE_TOOL)
        .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
        .build();
  }
}