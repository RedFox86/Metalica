package net.redfox.metalica.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.datagen.models.MyItemModelBuilder;
import net.redfox.metalica.datagen.models.MyItemModelProvider;
import net.redfox.metalica.material.MetalMaterial;

public class ModItemModelProvider extends MyItemModelProvider {

  public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
    super(output, Metalica.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerModels() {
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      simpleItem(material.getIngot().get(), "ingot_"+material.getName());
      simpleItem(material.getDust().get(), "dust_"+material.getName());
      simpleItem(material.getNugget().get(), "nugget_"+material.getName());
      bucketItem(material.getFluid().getBucket(), "liquid_"+material.getName());
      if (material.hasOre()) simpleItem(material.getRaw().get(), "raw_"+material.getName());
    }
  }

  private MyItemModelBuilder simpleItem(Item item, String modelName) {
    String itemName = item.getDescriptionId().substring(item.getDescriptionId().indexOf(Metalica.MOD_ID)+1+Metalica.MOD_ID.length());
    return withExistingParent(
        Metalica.MOD_ID+":item/"+itemName,
        ResourceLocation.parse("item/generated")
    ).texture(
        "layer0",
        Metalica.MOD_ID+":item/"+modelName
    );
  }

  private MyItemModelBuilder bucketItem(Item item, String modelName) {
    String itemName = item.getDescriptionId().substring(item.getDescriptionId().indexOf(Metalica.MOD_ID)+1+Metalica.MOD_ID.length());
    return withExistingParent(
        Metalica.MOD_ID+":item/"+itemName,
        ResourceLocation.parse("item/generated")
    ).texture(
        "layer0",
        ResourceLocation.withDefaultNamespace("item/bucket")
    ).texture(
        "layer1",
        Metalica.MOD_ID+":item/"+modelName
    );
  }


}