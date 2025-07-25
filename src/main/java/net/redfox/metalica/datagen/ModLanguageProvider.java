package net.redfox.metalica.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

public class ModLanguageProvider extends LanguageProvider {


  public ModLanguageProvider(PackOutput output, String locale) {
    super(output, Metalica.MOD_ID, locale);
  }

  @Override
  protected void addTranslations() {
    add("creativetab."+Metalica.MOD_ID+".metalica_tab", "Metalica");

    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      add("block."+Metalica.MOD_ID+"."+material.getName()+"_ore", material.getDisplayName()+" Ore");
      add("block."+Metalica.MOD_ID+"."+"deepslate_"+material.getName()+"_ore", "Deepslate "+material.getDisplayName()+" Ore");
      add("block."+Metalica.MOD_ID+"."+material.getName()+"_block", material.getDisplayName()+" Block");
      add("block."+Metalica.MOD_ID+"."+"raw_"+material.getName()+"_block", "Raw "+material.getDisplayName()+" Block");
      add("item."+Metalica.MOD_ID+"."+material.getName()+"_ingot", material.getDisplayName()+" Ingot");
      add("item."+Metalica.MOD_ID+"."+material.getName()+"_dust", material.getDisplayName()+" Dust");
      add("item."+Metalica.MOD_ID+"."+material.getName()+"_nugget", material.getDisplayName()+" Nugget");
      add("item."+Metalica.MOD_ID+"."+"raw_"+material.getName(), "Raw "+material.getDisplayName());
      add("item."+Metalica.MOD_ID+"."+"molten_"+material.getName()+"_bucket", "Molten "+material.getDisplayName()+" Bucket");
      add("fluid."+Metalica.MOD_ID+"."+"molten_"+material.getName(), "Molten "+material.getDisplayName());
    }
  }
}
