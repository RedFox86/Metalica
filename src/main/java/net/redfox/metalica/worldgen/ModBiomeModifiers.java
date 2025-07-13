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

public class ModBiomeModifiers {
  public static final ResourceKey<BiomeModifier> ADD_ALUMINUM_ORE = registerKey("add_aluminum_ore");
  public static final ResourceKey<BiomeModifier> ADD_LEAD_ORE = registerKey("add_lead_ore");
  public static final ResourceKey<BiomeModifier> ADD_ZINC_ORE = registerKey("add_zinc_ore");

  public static void bootstrap(BootstapContext<BiomeModifier> context) {
    HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
    HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

    context.register(ADD_ALUMINUM_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ALUMINUM_ORE_PLACED_KEY)),
        GenerationStep.Decoration.UNDERGROUND_ORES
    ));
    context.register(ADD_LEAD_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LEAD_ORE_PLACED_KEY)),
        GenerationStep.Decoration.UNDERGROUND_ORES
    ));
    context.register(ADD_ZINC_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
        biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
        HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ZINC_ORE_PLACED_KEY)),
        GenerationStep.Decoration.UNDERGROUND_ORES
    ));
  }

  private static ResourceKey<BiomeModifier> registerKey(String name) {
    return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }
}