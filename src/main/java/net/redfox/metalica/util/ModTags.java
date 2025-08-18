/* (C)2025 */
package net.redfox.metalica.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.redfox.metalica.Metalica;

public class ModTags {
  public static class Blocks {
    private static TagKey<Block> tag(String name) {
      return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
    }
  }

  public static class Items {
    // Ingot
    public static final TagKey<Item> REUSABLE_INGOT_CAST_TAG =
        tconstructTag("casts/multi_use/ingot");
    public static final TagKey<Item> SINGLE_USE_INGOT_CAST_TAG =
        tconstructTag("casts/single_use/ingot");

    // Nugget
    public static final TagKey<Item> REUSABLE_NUGGET_CAST_TAG =
        tconstructTag("casts/multi_use/nugget");
    public static final TagKey<Item> SINGLE_USE_NUGGET_CAST_TAG =
        tconstructTag("casts/single_use/nugget");

    public static TagKey<Item> modTag(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
    }

    public static TagKey<Item> forgeTag(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }

    public static TagKey<Item> tconstructTag(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("tconstruct", name));
    }
  }

  public static class Fluids {
    public static TagKey<Fluid> tag(String name) {
      return FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }
  }
}
