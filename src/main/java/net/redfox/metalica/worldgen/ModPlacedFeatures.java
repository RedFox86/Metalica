package net.redfox.metalica.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

import java.util.List;

public class ModPlacedFeatures {
  public static void bootstrap(BootstapContext<PlacedFeature> context) {
    HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      register(context, material.getWorldgenContext().getPlacedFeatureResourceKey(), configuredFeatures.getOrThrow(material.getWorldgenContext().getConfiguredFeatureResourceKey()),
          ModOrePlacement.commonOrePlacement(material.getWorldgenContext().getVeinCount(), HeightRangePlacement.uniform(
              VerticalAnchor.absolute(material.getWorldgenContext().getMinY()),
              VerticalAnchor.absolute(material.getWorldgenContext().getMaxY()))
          ));
    }
  }

  public static ResourceKey<PlacedFeature> registerKey(String name) {
    return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }

  private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
    context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
  }
}