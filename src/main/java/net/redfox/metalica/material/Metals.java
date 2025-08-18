/* (C)2025 */
package net.redfox.metalica.material;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.fml.ModList;

public class Metals {
  public static MetalMaterial aluminum;
  public static MetalMaterial lead;
  public static MetalMaterial zinc;
  public static MetalMaterial aluminum_brass;

  /*
   * Remember to move the bottom initializer into the else statement
   * when tinkerizing materials!
   */

  public static void createMaterials() {
    if (ModList.get().isLoaded("tconstruct")) {
      aluminum =
          new MetalMaterial.Builder("aluminum", "Aluminum", 0xFF9ED2FF)
              .ore(4, 12, -64, 80, BlockTags.NEEDS_STONE_TOOL)
              .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
              .tinkers(
                  new TinkersMaterialBuilder(Tiers.IRON, 1000)
                      .head(100, 7.5f, 1.5f)
                      .grip(-0.5f, -0.2f, 0.5f)
                      .limb(60, 0.3f, 0.1f, -0.2f)
                      .handle(-0.5f, 0.5f, 0.5f, -0.5f)
                      .binding()
                      .repairKit()
                      .modifier("metalica:malleable"))
              .build();
      lead =
          new MetalMaterial.Builder("lead", "Lead", 0xFF383F5E)
              .ore(7, 7, -32, 35, BlockTags.NEEDS_IRON_TOOL)
              .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
              .tinkers(
                  new TinkersMaterialBuilder(Tiers.IRON, 1000)
                      .head(550, 3f, 4.5f)
                      .grip(2f, -0.7f, 3f)
                      .limb(250, -1.5f, 0.8f, -0.5f)
                      .handle(-0.5f, 0.5f, 0.5f, -0.5f)
                      .binding()
                      .repairKit()
                      .modifier("tconstruct:heavy"))
              .build();
    } else {
      aluminum =
          new MetalMaterial.Builder("aluminum", "Aluminum", 0xFF9ED2FF)
              .ore(4, 12, -64, 80, BlockTags.NEEDS_STONE_TOOL)
              .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
              .build();
      lead =
          new MetalMaterial.Builder("lead", "Lead", 0xFF383F5E)
              .ore(7, 7, -32, 35, BlockTags.NEEDS_IRON_TOOL)
              .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
              .build();
    }
    zinc =
        new MetalMaterial.Builder("zinc", "Zinc", 0xFFfAF7BB)
            .ore(3, 16, -24, 60, BlockTags.NEEDS_STONE_TOOL)
            .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
            .build();
    aluminum_brass =
        new MetalMaterial.Builder("aluminum_brass", "Aluminum Brass", 0xFFFFD966)
            .storageBlockMiningLevel(BlockTags.NEEDS_IRON_TOOL)
            .build();
  }
}
