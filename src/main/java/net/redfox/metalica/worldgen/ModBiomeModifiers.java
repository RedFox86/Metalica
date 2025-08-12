package net.redfox.metalica.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

public class ModBiomeModifiers {

  public static void bootstrap(BootstapContext<BiomeModifier> context) {
    HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      if (!material.hasOre()) continue;
      context.register(material.getWorldgenContext().getBiomeResourceKey(), new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
          biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
          HolderSet.direct(placedFeatures.getOrThrow(material.getWorldgenContext().getPlacedFeatureResourceKey())),
          GenerationStep.Decoration.UNDERGROUND_ORES
      ));
    }
  }

  public static ResourceKey<BiomeModifier> registerKey(String name) {
    return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }
}