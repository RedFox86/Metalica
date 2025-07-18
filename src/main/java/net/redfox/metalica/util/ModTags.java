package net.redfox.metalica.util;

import net.minecraft.core.registries.Registries;
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
    public static TagKey<Item> modTag(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
    }

    public static TagKey<Item> forgeTag(String name) {
      return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }
  }

  public static class Fluids {
    public static TagKey<Fluid> tag(String name) {
      return FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
    }
  }
}