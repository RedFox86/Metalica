package net.redfox.metalica.material;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.block.ModBlocks;
import net.redfox.metalica.fluid.ModFluids;
import net.redfox.metalica.item.ModItems;
import net.redfox.metalica.util.ModTags;
import net.redfox.metalica.worldgen.WorldgenContext;
import slimeknights.mantle.registration.object.FlowingFluidObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MetalMaterial {
  private static final ArrayList<MetalMaterial> MATERIALS = new ArrayList<>();

  private final String displayName;
  private final String name;
  private final int color;
  private final RegistryObject<Block> stoneOre;
  private final RegistryObject<Block> deepslateOre;
  private final RegistryObject<Block> storageBlock;
  private final RegistryObject<Block> rawStorageBlock;
  private final RegistryObject<Item> ingot;
  private final RegistryObject<Item> nugget;
  private final RegistryObject<Item> dust;
  private final RegistryObject<Item> raw;
  private final FlowingFluidObject<ForgeFlowingFluid> fluid;
  private final TagKey<Block> oreMiningLevel;
  private final TagKey<Block> storageBlockMiningLevel;
  private final TagKey<Item> dustTag;
  private final TagKey<Item> ingotTag;
  private final TagKey<Item> nuggetTag;
  private final TagKey<Item> oreTag;
  private final TagKey<Item> rawTag;
  private final TagKey<Item> storageBlockTag;
  private final TagKey<Fluid> fluidTag;
  private final WorldgenContext worldgenContext;

  private MetalMaterial(String displayName, String name, int color, TagKey<Block> oreMiningLevel, TagKey<Block> storageBlockMiningLevel, WorldgenContext worldgenContext) {
    MATERIALS.add(this);
    this.displayName = displayName;
    this.name = name;
    this.color = color;
    this.worldgenContext = worldgenContext;

    if (worldgenContext != null) {
      stoneOre = ModBlocks.registerBlock(name+"_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
      deepslateOre = ModBlocks.registerBlock("deepslate_"+name+"_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));
      rawStorageBlock = ModBlocks.registerBlock("raw_"+name+"_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK)));
      raw = ModItems.ITEMS.register("raw_"+name, () -> new Item(new Item.Properties()));

      oreTag = ModTags.Items.forgeTag("ores/"+name);
      rawTag = ModTags.Items.forgeTag("raw_materials/"+name);
    } else {
      stoneOre = null;
      deepslateOre = null;
      rawStorageBlock = null;
      raw = null;

      oreTag = null;
      rawTag = null;
    }

    storageBlock = ModBlocks.registerBlock(name+"_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
    ingot = ModItems.ITEMS.register(name+"_ingot", () -> new Item(new Item.Properties()));
    nugget = ModItems.ITEMS.register(name+"_nugget", () -> new Item(new Item.Properties()));
    dust = ModItems.ITEMS.register(name+"_dust", () -> new Item(new Item.Properties()));
    fluid = ModFluids.createMoltenFluid("molten_"+name);

    this.oreMiningLevel = oreMiningLevel;
    this.storageBlockMiningLevel = storageBlockMiningLevel;

    dustTag = ModTags.Items.forgeTag("dusts/"+name);
    ingotTag = ModTags.Items.forgeTag("ingots/"+name);
    nuggetTag = ModTags.Items.forgeTag("nuggets/"+name);
    storageBlockTag = ModTags.Items.forgeTag("storage_blocks/"+name);
    fluidTag = ModTags.Fluids.tag(name);
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getName() {
    return name;
  }

  public int getColor() {
    return color;
  }

  public RegistryObject<Block> getStoneOre() {
    return stoneOre;
  }

  public RegistryObject<Block> getDeepslateOre() {
    return deepslateOre;
  }

  public RegistryObject<Block> getStorageBlock() {
    return storageBlock;
  }

  public RegistryObject<Block> getRawStorageBlock() {
    return rawStorageBlock;
  }

  public RegistryObject<Item> getIngot() {
    return ingot;
  }

  public RegistryObject<Item> getNugget() {
    return nugget;
  }

  public RegistryObject<Item> getDust() {
    return dust;
  }

  public RegistryObject<Item> getRaw() {
    return raw;
  }

  public FlowingFluidObject<ForgeFlowingFluid> getFluid() {
    return fluid;
  }

  public TagKey<Block> getOreMiningLevel() {
    return oreMiningLevel;
  }

  public TagKey<Block> getStorageBlockMiningLevel() {
    return storageBlockMiningLevel;
  }

  public TagKey<Item> getDustTag() {
    return dustTag;
  }

  public TagKey<Item> getIngotTag() {
    return ingotTag;
  }

  public TagKey<Item> getNuggetTag() {
    return nuggetTag;
  }

  public TagKey<Item> getOreTag() {
    return oreTag;
  }

  public TagKey<Item> getRawTag() {
    return rawTag;
  }

  public TagKey<Item> getStorageBlockTag() {
    return storageBlockTag;
  }

  public TagKey<Fluid> getFluidTag() {
    return fluidTag;
  }

  public WorldgenContext getWorldgenContext() {
    return worldgenContext;
  }

  public boolean hasOre() {
    return worldgenContext != null;
  }

  public static ArrayList<MetalMaterial> getMaterials() {
    return MATERIALS;
  }

  public static class Builder {
    private final String displayName;
    private final String name;
    private final int color;

    private WorldgenContext worldgenContext;
    private TagKey<Block> oreMiningLevel;
    private TagKey<Block> storageBlockMiningLevel;

    public Builder(String name, int color) {
      this.displayName = name.substring(0, 1).toUpperCase()+name.substring(1);
      this.name = name;
      this.color = color;

      oreMiningLevel = BlockTags.NEEDS_STONE_TOOL;
      storageBlockMiningLevel = oreMiningLevel;
      worldgenContext = null;
    }

    public Builder storageBlockMiningLevel(TagKey<Block> miningLevel) {
      this.storageBlockMiningLevel = miningLevel;
      return this;
    }

    public Builder ore(int veinSize, int veinCount, int minY, int maxY, TagKey<Block> miningLevel) {
      this.worldgenContext = new WorldgenContext(name, veinSize, veinCount, minY, maxY);
      this.oreMiningLevel = miningLevel;
      return this;
    }

    public MetalMaterial build() {
      return new MetalMaterial(displayName, name, color, oreMiningLevel, storageBlockMiningLevel, worldgenContext);
    }
  }
}