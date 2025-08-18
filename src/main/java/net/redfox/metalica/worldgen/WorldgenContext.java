/* (C)2025 */
package net.redfox.metalica.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;

public class WorldgenContext {
  private final int veinSize;
  private final int veinCount;
  private final int minY;
  private final int maxY;
  private final ResourceKey<BiomeModifier> biomeResourceKey;
  private final ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureResourceKey;
  private final ResourceKey<PlacedFeature> placedFeatureResourceKey;

  public WorldgenContext(String name, int veinSize, int veinCount, int minY, int maxY) {
    this.veinSize = veinSize;
    this.veinCount = veinCount;
    this.minY = minY;
    this.maxY = maxY;
    biomeResourceKey = ModBiomeModifiers.registerKey("add_" + name + "_ore");
    configuredFeatureResourceKey = ModConfiguredFeatures.registerKey(name + "_ore");
    placedFeatureResourceKey = ModPlacedFeatures.registerKey(name + "_ore_placed");
  }

  public int getVeinSize() {
    return veinSize;
  }

  public int getVeinCount() {
    return veinCount;
  }

  public int getMinY() {
    return minY;
  }

  public int getMaxY() {
    return maxY;
  }

  public ResourceKey<BiomeModifier> getBiomeResourceKey() {
    return biomeResourceKey;
  }

  public ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeatureResourceKey() {
    return configuredFeatureResourceKey;
  }

  public ResourceKey<PlacedFeature> getPlacedFeatureResourceKey() {
    return placedFeatureResourceKey;
  }
}
