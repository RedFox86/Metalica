package net.redfox.metalica.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {
  private static final int ALUMINUM_VEIN_SIZE = 4;
  private static final int LEAD_VEIN_SIZE = 7;
  private static final int ZINC_VEIN_SIZE = 3;

  public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ALUMINUM_KEY = registerKey("aluminum_ore");
  public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_LEAD_KEY = registerKey("lead_ore");
  public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_ZINC_KEY = registerKey("zinc_ore");

  public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
    RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    RuleTest deepslateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    List<OreConfiguration.TargetBlockState> overworldAluminumOres = List.of(
        OreConfiguration.target(stoneReplacable, ModBlocks.ALUMINUM_ORE.get().defaultBlockState()),
        OreConfiguration.target(deepslateReplacable, ModBlocks.DEEPSLATE_ALUMINUM_ORE.get().defaultBlockState())
    );
    List<OreConfiguration.TargetBlockState> overworldLeadOres = List.of(
        OreConfiguration.target(stoneReplacable, ModBlocks.LEAD_ORE.get().defaultBlockState()),
        OreConfiguration.target(deepslateReplacable, ModBlocks.DEEPSLATE_LEAD_ORE.get().defaultBlockState())
    );
    List<OreConfiguration.TargetBlockState> overworldZincOres = List.of(
        OreConfiguration.target(stoneReplacable, ModBlocks.ZINC_ORE.get().defaultBlockState()),
        OreConfiguration.target(deepslateReplacable, ModBlocks.DEEPSLATE_ZINC_ORE.get().defaultBlockState())
    );
    register(context, OVERWORLD_ALUMINUM_KEY, Feature.ORE, new OreConfiguration(overworldAluminumOres, ALUMINUM_VEIN_SIZE));
    register(context, OVERWORLD_LEAD_KEY, Feature.ORE, new OreConfiguration(overworldLeadOres, LEAD_VEIN_SIZE));
    register(context, OVERWORLD_ZINC_KEY, Feature.ORE, new OreConfiguration(overworldZincOres, ZINC_VEIN_SIZE));
  }

  public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
    return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }

  private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
    context.register(key, new ConfiguredFeature<>(feature, configuration));
  }
}