package net.redfox.metalica.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.redfox.metalica.Metalica;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Metalica.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput packOutput = generator.getPackOutput();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

    //Atlas Generator
    generator.addProvider(event.includeClient(), new ModAtlasProvider(packOutput, existingFileHelper));

    //Recipes and Loot Tables
    generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
    generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput));

    //Client JSONs
    generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
    generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
    generator.addProvider(event.includeClient(), new ModLanguageProvider(packOutput, "en_us"));

    //Fluid Textures
    generator.addProvider(event.includeClient(), new ModFluidTextureProvider(packOutput));

    //Tags
    ModBlockTagProvider blockTagProvider = generator.addProvider(event.includeServer(), new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper));
    generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));
    generator.addProvider(event.includeServer(), new ModFluidTagProvider(packOutput, lookupProvider, existingFileHelper));

    //Worldgen
    generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));
  }
}