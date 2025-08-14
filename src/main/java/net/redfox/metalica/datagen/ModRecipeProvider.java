package net.redfox.metalica.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import net.redfox.metalica.util.ModTags;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.tools.data.ToolsRecipeProvider;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
  public ModRecipeProvider(PackOutput pOutput) {
    super(pOutput);
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {

      compactingRecipe(pWriter, material.getIngotTag(), material.getIngot().get(), material.getStorageBlock().get(),
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/block_from_ingot"));
      unCompactingRecipe(pWriter, material.getStorageBlockTag(), material.getStorageBlock().get(), material.getIngot().get(),
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/ingot_from_block"));
      compactingRecipe(pWriter, material.getNuggetTag(), material.getNugget().get(), material.getIngot().get(),
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/ingot_from_nugget"));
      unCompactingRecipe(pWriter, material.getIngotTag(), material.getIngot().get(), material.getNugget().get(),
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/nugget_from_ingot"));

      oreSmelting(pWriter, material.getDustTag(), material.getDust().get(), material.getIngot().get(), 0, 200,
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/smelting/ingot_from_dust"));
      oreBlasting(pWriter, material.getDustTag(), material.getDust().get(), material.getIngot().get(), 0, 200,
          ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/blasting/ingot_from_dust"));

      MeltingRecipeBuilder.melting(Ingredient.of(material.getIngotTag()), material.getFluid().get(), 90, 1f).
          save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_ingot"));
      MeltingRecipeBuilder.melting(Ingredient.of(material.getDustTag()), material.getFluid().get(), 90, 1f).
          save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_dust"));
      MeltingRecipeBuilder.melting(Ingredient.of(material.getNuggetTag()), material.getFluid().get(), 10, 1f).
          save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_nugget"));
      MeltingRecipeBuilder.melting(Ingredient.of(material.getStorageBlockTag()), material.getFluid().get(), 810, 1f).
          save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_block"));

      //Storage Block
      ItemCastingRecipeBuilder.basinRecipe(material.getStorageBlockTag()).setFluid(material.getFluidTag(), 810).setCoolingTime(180).
          save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/basin/block"));

      //Ingot
      ItemCastingRecipeBuilder.tableRecipe(material.getIngotTag()).setFluid(material.getFluidTag(), 90).setCoolingTime(60).setCast(ModTags.Items.REUSABLE_INGOT_CAST_TAG, false)
          .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/reusable/ingot"));
      ItemCastingRecipeBuilder.tableRecipe(material.getIngotTag()).setFluid(material.getFluidTag(), 90).setCoolingTime(60).setCast(ModTags.Items.SINGLE_USE_INGOT_CAST_TAG, true)
          .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/single_use/ingot"));

      //Nugget
      ItemCastingRecipeBuilder.tableRecipe(material.getNuggetTag()).setFluid(material.getFluidTag(), 10).setCoolingTime(20).setCast(ModTags.Items.REUSABLE_NUGGET_CAST_TAG, false)
          .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/reusable/nugget"));
      ItemCastingRecipeBuilder.tableRecipe(material.getNuggetTag()).setFluid(material.getFluidTag(), 10).setCoolingTime(20).setCast(ModTags.Items.SINGLE_USE_NUGGET_CAST_TAG, true)
          .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/single_use/nugget"));

      if (material.getTinkersMaterialBuilder() != null) {



        MaterialRecipeBuilder.materialRecipe(new MaterialId(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())))
            .setIngredient(material.getIngotTag())
            .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/single_use/material_item"));

        MaterialFluidRecipeBuilder.material(new MaterialId(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())))
            .setFluid(FluidIngredient.of(material.getFluidTag(), 90))
            .setTemperature(material.getTinkersMaterialBuilder().getTemperature())
            .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/single_use/material_fluid"));

        MaterialMeltingRecipeBuilder.material(new MaterialId(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())),
            material.getTinkersMaterialBuilder().getTemperature(),
            new FluidStack(material.getFluid().get(), 90))
            .save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/casting/table/single_use/material_melting"));
      } if (material.hasOre()) {
        MeltingRecipeBuilder.melting(Ingredient.of(material.getRawTag()), material.getFluid().get(), 90, 1f).
            save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_raw"));
        MeltingRecipeBuilder.melting(Ingredient.of(material.getRawStorageBlockTag()), material.getFluid().get(), 810, 1f).
            save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_raw_block"));
        MeltingRecipeBuilder.melting(Ingredient.of(material.getOreTag()), material.getFluid().get(), 90, 1f).
            save(pWriter, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/melting/fluid_from_ore"));

        oreSmelting(pWriter, material.getRawTag(), material.getRaw().get(), material.getIngot().get(), 0.25f, 200,
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/smelting/ingot_from_raw"));
        oreBlasting(pWriter, material.getRawTag(), material.getRaw().get(), material.getIngot().get(), 0.25f, 200,
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/blasting/ingot_from_raw"));
        oreSmelting(pWriter, material.getOreTag(), material.getStoneOre().get(), material.getIngot().get(), 0.25f, 200,
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/smelting/ingot_from_ore"));
        oreBlasting(pWriter, material.getOreTag(), material.getStoneOre().get(), material.getIngot().get(), 0.25f, 200,
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/blasting/ingot_from_ore"));

        compactingRecipe(pWriter, material.getRawTag(), material.getRaw().get(), material.getRawStorageBlock().get(),
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/raw_from_raw_block"));
        unCompactingRecipe(pWriter, material.getRawStorageBlockTag(), material.getRawStorageBlock().get(), material.getRaw().get(),
            ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName()+"/crafting/raw_block_from_raw"));
      }
    }
  }

  protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> inputTag, ItemLike input, ItemLike pResult, float pExperience, int pCookingTIme, ResourceLocation savePath) {
    oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, inputTag, input, pResult, pExperience, pCookingTIme, savePath);
  }

  protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, TagKey<Item> inputTag, ItemLike input, ItemLike pResult, float pExperience, int pCookingTime, ResourceLocation savePath) {
    oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, inputTag, input, pResult, pExperience, pCookingTime, savePath);
  }

  protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, TagKey<Item> inputTag, ItemLike input, ItemLike pResult, float pExperience, int pCookingTime, ResourceLocation savePath) {
    SimpleCookingRecipeBuilder.generic(Ingredient.of(inputTag), RecipeCategory.MISC, pResult, pExperience, pCookingTime, pCookingSerializer).unlockedBy(getHasName(input), has(input)).save(pFinishedRecipeConsumer, savePath);
  }

  private static void compactingRecipe(Consumer<FinishedRecipe> finishedRecipeConsumer, TagKey<Item> unpackedTag, ItemLike unpacked, ItemLike packed, ResourceLocation savePath) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, packed).define('#', unpackedTag).pattern("###")
        .pattern("###").pattern("###").unlockedBy(getHasName(unpacked), has(unpacked))
        .save(finishedRecipeConsumer, savePath);
  }

  private static void unCompactingRecipe(Consumer<FinishedRecipe> finishedRecipeConsumer, TagKey<Item> packedTag, ItemLike packed, ItemLike unpacked, ResourceLocation savePath) {
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpacked, 9)
        .requires(packedTag).unlockedBy(getHasName(packed), has(packed))
        .save(finishedRecipeConsumer, savePath);
  }
}
