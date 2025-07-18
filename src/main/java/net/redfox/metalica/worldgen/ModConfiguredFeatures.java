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
import net.redfox.metalica.material.MetalMaterial;

import java.util.List;

public class ModConfiguredFeatures {

  public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
    RuleTest stoneReplacable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    RuleTest deepslateReplacable = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      List<OreConfiguration.TargetBlockState> ores = List.of(
          OreConfiguration.target(stoneReplacable, material.getStoneOre().get().defaultBlockState()),
          OreConfiguration.target(deepslateReplacable, material.getDeepslateOre().get().defaultBlockState())
      );
      register(context, material.getWorldgenContext().getConfiguredFeatureResourceKey(), Feature.ORE, new OreConfiguration(
          ores, material.getWorldgenContext().getVeinSize())
      );
    }
  }

  public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
    return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, name));
  }

  private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
    context.register(key, new ConfiguredFeature<>(feature, configuration));
  }
}