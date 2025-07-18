package net.redfox.metalica.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
  public ModRecipeProvider(PackOutput pOutput) {
    super(pOutput);
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      if (material.hasOre()) {
        List<ItemLike> smeltables = List.of(
            material.getRaw().get(),
            material.getStoneOre().get(),
            material.getDeepslateOre().get(),
            material.getDust().get()
        );
        oreSmelting(pWriter, smeltables, RecipeCategory.MISC, material.getIngot().get(), 0.25f, 200, material.getName());
        oreBlasting(pWriter, smeltables, RecipeCategory.MISC, material.getIngot().get(), 0.25f, 100, material.getName());
      }

      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, material.getIngot().get())
          .pattern("SSS")
          .pattern("SSS")
          .pattern("SSS")
          .define('S', material.getNugget().get())
          .unlockedBy(getHasName(material.getNugget().get()), has(material.getNugget().get()))
          .save(pWriter, material.getName()+"_ingot_from_crafting_"+material.getName()+"_nugget");
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, material.getStorageBlock().get())
          .pattern("SSS")
          .pattern("SSS")
          .pattern("SSS")
          .define('S', material.getIngot().get())
          .unlockedBy(getHasName(material.getIngot().get()), has(material.getIngot().get()))
          .save(pWriter, material.getName()+"_block_from_crafting_"+material.getName()+"_ingot");
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, material.getRawStorageBlock().get())
          .pattern("SSS")
          .pattern("SSS")
          .pattern("SSS")
          .define('S', material.getRaw().get())
          .unlockedBy(getHasName(material.getRaw().get()), has(material.getRaw().get()))
          .save(pWriter, material.getName()+"_raw_block_from_crafting_"+material.getName()+"_raw");

      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getNugget().get(), 9)
          .requires(material.getIngot().get())
          .unlockedBy(getHasName(material.getIngot().get()), has(material.getIngot().get()))
          .save(pWriter, material.getName()+"_nugget_from_crafting_"+material.getName()+"_ingot");
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getIngot().get(), 9)
          .requires(material.getStorageBlock().get())
          .unlockedBy(getHasName(material.getStorageBlock().get()), has(material.getStorageBlock().get()))
          .save(pWriter, material.getName()+"_ingot_from_crafting_"+material.getName()+"_block");
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getRaw().get(), 9)
          .requires(material.getRawStorageBlock().get())
          .unlockedBy(getHasName(material.getRawStorageBlock().get()), has(material.getRawStorageBlock().get()))
          .save(pWriter, material.getName()+"_raw_from_crafting_"+material.getName()+"_raw_block");
    }
  }

  protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTIme, String pGroup) {
    oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
  }

  protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
    oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
  }

  protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
    for(ItemLike itemlike : pIngredients) {
      SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike)).save(pFinishedRecipeConsumer, Metalica.MOD_ID+":"+getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
    }

  }
}
