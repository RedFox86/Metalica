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

import java.util.List;

public class ModPlacedFeatures {
  private static final int ALUMINUM_COUNT = 12;
  private static final int ALUMINUM_MIN_HEIGHT = -64;
  private static final int ALUMINUM_MAX_HEIGHT = 80;

  private static final int LEAD_COUNT = 7;
  private static final int LEAD_MIN_HEIGHT = -32;
  private static final int LEAD_MAX_HEIGHT = 35;

  private static final int ZINC_COUNT = 16;
  private static final int ZINC_MIN_HEIGHT = -24;
  private static final int ZINC_MAX_HEIGHT = 60;

  public static final ResourceKey<PlacedFeature> ALUMINUM_ORE_PLACED_KEY = registerKey("aluminum_ore_placed");
  public static final ResourceKey<PlacedFeature> LEAD_ORE_PLACED_KEY = registerKey("lead_ore_placed");
  public static final ResourceKey<PlacedFeature> ZINC_ORE_PLACED_KEY = registerKey("zinc_ore_placed");

  public static void bootstrap(BootstapContext<PlacedFeature> context) {
    HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

    register(context, ALUMINUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_ALUMINUM_KEY),
        ModOrePlacement.commonOrePlacement(ALUMINUM_COUNT, HeightRangePlacement.uniform(
            VerticalAnchor.absolute(ALUMINUM_MIN_HEIGHT),
            VerticalAnchor.absolute(ALUMINUM_MAX_HEIGHT))
        ));
    register(context, LEAD_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_LEAD_KEY),
        ModOrePlacement.commonOrePlacement(LEAD_COUNT, HeightRangePlacement.uniform(
            VerticalAnchor.absolute(LEAD_MIN_HEIGHT),
            VerticalAnchor.absolute(LEAD_MAX_HEIGHT))
        ));
    register(context, ZINC_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.OVERWORLD_ZINC_KEY),
        ModOrePlacement.commonOrePlacement(ZINC_COUNT, HeightRangePlacement.uniform(
            VerticalAnchor.absolute(ZINC_MIN_HEIGHT),
            VerticalAnchor.absolute(ZINC_MAX_HEIGHT))
        ));
  }

  private static ResourceKey<PlacedFeature> registerKey(String name) {
    return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }

  private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
    context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
  }
}