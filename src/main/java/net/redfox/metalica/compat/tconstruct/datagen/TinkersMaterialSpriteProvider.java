package net.redfox.metalica.compat.tconstruct.datagen;

import net.minecraft.resources.ResourceLocation;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TinkersMaterialSpriteProvider extends AbstractMaterialSpriteProvider {

  //Let's be honest. This code is shit. But it works. And the #1 rule of programming - if it works, don't change it.
  public static Color[] getColorPixels(String name) {
    Color[] colorPixels = new Color[8];
    try {
      BufferedImage color = ImageIO.read(TinkersMaterialSpriteProvider.class.getResourceAsStream("/assets/"+Metalica.MOD_ID+"/textures/color/"+name+".png"));
      for (int i = 0; i < 8; i++) {
        colorPixels[i] = new Color(color.getRGB(0, i));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return colorPixels;
  }

  /**
   * Gets the name of this material list
   */
  @Override
  public @NotNull String getName() {
    return "TinkersMaterialSpriteProvider";
  }

  /**
   * Adds all materials to the list
   */
  @Override
  protected void addAllMaterials() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      Color[] colorPixels = getColorPixels(material.getName());
      buildMaterial(new MaterialId(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())))
          .statType(new MaterialStatsId(ResourceLocation.fromNamespaceAndPath("tconstruct", "limb")))
          .colorMapper(GreyToColorMapping.builder()
              .addABGR(0, colorPixels[0].getRGB())
              .addABGR(63, colorPixels[1].getRGB())
              .addABGR(102, colorPixels[2].getRGB())
              .addABGR(140, colorPixels[3].getRGB())
              .addABGR(178, colorPixels[4].getRGB())
              .addABGR(216, colorPixels[5].getRGB())
              .addABGR(255, colorPixels[7].getRGB())
              .build()
          );
    }
  }
}
