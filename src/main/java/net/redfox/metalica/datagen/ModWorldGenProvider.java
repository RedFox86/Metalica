package net.redfox.metalica.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.worldgen.ModBiomeModifiers;
import net.redfox.metalica.worldgen.ModConfiguredFeatures;
import net.redfox.metalica.worldgen.ModPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
  public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
      .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
      .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

  public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(Metalica.MOD_ID));
  }
}