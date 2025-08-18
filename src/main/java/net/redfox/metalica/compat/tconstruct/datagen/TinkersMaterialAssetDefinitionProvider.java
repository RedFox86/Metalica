/* (C)2025 */
package net.redfox.metalica.compat.tconstruct.datagen;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.client.materials.MaterialGeneratorInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

public class TinkersMaterialAssetDefinitionProvider extends AbstractMaterialRenderInfoProvider {

  public TinkersMaterialAssetDefinitionProvider(
      PackOutput packOutput,
      @Nullable AbstractMaterialSpriteProvider materialSprites,
      @Nullable ExistingFileHelper existingFileHelper) {
    super(packOutput, materialSprites, existingFileHelper);
  }

  /** Adds all relevant material stats */
  @Override
  protected void addMaterialRenderInfo() {
    Set<MaterialStatsId> set = new HashSet<>();
    set.add(new MaterialStatsId(ResourceLocation.fromNamespaceAndPath("tconstruct", "maille")));
    set.add(new MaterialStatsId(ResourceLocation.fromNamespaceAndPath("tconstruct", "head")));
    set.add(new MaterialStatsId(ResourceLocation.fromNamespaceAndPath("tconstruct", "grip")));
    set.add(new MaterialStatsId(ResourceLocation.fromNamespaceAndPath("tconstruct", "repair_kit")));

    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      Color[] colorPixels = TinkersMaterialSpriteProvider.getColorPixels(material.getName());
      buildRenderInfo(
              new MaterialId(
                  ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())))
          .color(material.getColor())
          .generator(
              new MaterialGeneratorInfo(
                  new RecolorSpriteTransformer(
                      GreyToColorMapping.builder()
                          .addABGR(0, colorPixels[0].getRGB())
                          .addABGR(63, colorPixels[1].getRGB())
                          .addABGR(102, colorPixels[2].getRGB())
                          .addABGR(140, colorPixels[3].getRGB())
                          .addABGR(178, colorPixels[4].getRGB())
                          .addABGR(216, colorPixels[5].getRGB())
                          .addABGR(255, colorPixels[7].getRGB())
                          .build()),
                  set,
                  true,
                  false))
          .fallbacks("metal")
          .build(
              new MaterialId(
                  ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())));
    }
  }

  /** Gets a name for this provider, to use in logging. */
  @Override
  public String getName() {
    return "";
  }
}
