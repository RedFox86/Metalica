package net.redfox.metalica.compat.tconstruct.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;
import org.jetbrains.annotations.NotNull;

public class TinkersMaterialDefinitionProvider extends slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider {

  public TinkersMaterialDefinitionProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void addMaterials() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      if (material.getTinkersMaterialBuilder() == null) continue;
      addMaterial(
          new slimeknights.tconstruct.library.materials.definition.MaterialId(ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, material.getName())),
          material.getTinkersMaterialBuilder().getTier().getLevel(),
          slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider.ORDER_COMPAT,
          false
      );
    }

    //Remove pre-existing Tinker's materials
    for (String name : MetalMaterial.getToRemove()) {
      addMaterial(
          new slimeknights.tconstruct.library.materials.definition.MaterialId(ResourceLocation.fromNamespaceAndPath("tconstruct", name)),
          0,
          5,
          false,
          true,
          FalseCondition.INSTANCE
      );
    }
  }

  /**
   * Gets a name for this provider, to use in logging.
   */
  @Override
  public @NotNull String getName() {
    return "TConstruct Definition Provider";
  }
}