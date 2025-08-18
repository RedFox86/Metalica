/* (C)2025 */
package net.redfox.metalica.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import slimeknights.mantle.fluid.texture.AbstractFluidTextureProvider;

public class ModFluidTextureProvider extends AbstractFluidTextureProvider {

  public ModFluidTextureProvider(PackOutput packOutput) {
    super(packOutput, Metalica.MOD_ID);
  }

  @Override
  public void addTextures() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      texture(material.getFluid())
          .still(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/still"))
          .flowing(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/flowing"))
          .overlay(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/flowing"))
          .color(material.getColor())
          .build();
    }
  }

  @Override
  public String getName() {
    return "Metalica Fluid Texture Provider";
  }
}
