/* (C)2025 */
package net.redfox.metalica.compat.tconstruct.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.compat.tconstruct.datagen.owned.MyAbstractMaterialTraitDataProvider;
import net.redfox.metalica.material.MetalMaterial;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class TinkersMaterialTraitProvider extends MyAbstractMaterialTraitDataProvider {
  public TinkersMaterialTraitProvider(
      PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, materials);
  }

  /** Adds all relevant material stats */
  @Override
  protected void addMaterialTraits() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      if (material.getTinkersMaterialBuilder() == null) continue;
      if (material.getTinkersMaterialBuilder().getModifier() == null) continue;
      addDefaultTraits(
          new MaterialId(
              ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())),
          new ModifierId(
              ResourceLocation.parse(material.getTinkersMaterialBuilder().getModifier())));
    }
  }

  /** Gets a name for this provider, to use in logging. */
  @Override
  public String getName() {
    return "TConstruct Traits Provider";
  }
}
