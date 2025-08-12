package net.redfox.metalica.compat.tconstruct.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

public class TinkersDataAdder {
  public static void addData(DataGenerator generator, GatherDataEvent event, PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    TinkersMaterialDefinitionProvider definitionProvider = generator.addProvider(event.includeServer(), new TinkersMaterialDefinitionProvider(packOutput));
    generator.addProvider(event.includeServer(), new TinkersMaterialStatProvider(packOutput, definitionProvider));
    generator.addProvider(event.includeServer(), new TinkersMaterialTraitProvider(packOutput, definitionProvider));
    generator.addProvider(event.includeClient(), new TinkersMaterialAssetDefinitionProvider(packOutput, new TinkersMaterialSpriteProvider(), existingFileHelper));
  }
}