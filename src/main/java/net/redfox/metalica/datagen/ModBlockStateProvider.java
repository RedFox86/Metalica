package net.redfox.metalica.datagen;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.datagen.models.MyBlockStateProvider;
import net.redfox.metalica.material.MetalMaterial;

public class ModBlockStateProvider extends MyBlockStateProvider {

  public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
    super(output, Metalica.MOD_ID, exFileHelper);
  }

  @Override
  protected void registerStatesAndModels() {
     for (MetalMaterial material :  MetalMaterial.getMaterials()) {
       blockWithItem(material.getName()+"_block", ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/storage_block_"+material.getName()));
       simpleBlockWithItem(material.getStorageBlock().get(), models().getExistingFile(modLoc(material.getName()+"_block")));

       if (!material.hasOre()) continue;

       blockWithItem("raw_"+material.getName()+"_block", ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/raw_storage_block_"+material.getName()));
       simpleBlockWithItem(material.getRawStorageBlock().get(), models().getExistingFile(modLoc("raw_"+material.getName()+"_block")));

       layeredOreBlock(material.getName()+"_ore", "block/stone", "metalica:block/ore_"+material.getName());
       simpleBlockWithItem(material.getStoneOre().get(), models().getExistingFile(modLoc(material.getName()+"_ore")));

       layeredOreBlock("deepslate_"+material.getName()+"_ore", "block/deepslate", "metalica:block/ore_"+material.getName());
       simpleBlockWithItem(material.getDeepslateOre().get(), models().getExistingFile(modLoc("deepslate_"+material.getName()+"_ore")));
     }
  }

  private void blockWithItem(String name, ResourceLocation texture) {
    models().cubeAll(name, texture);
  }

  public ModelFile layeredOreBlock(String name, String baseTexturePath, String overlayTexturePath) {
    // Define ResourceLocations for textures, handling vanilla vs. mod textures
    ResourceLocation particleTexture = ResourceLocation.parse(baseTexturePath);
    ResourceLocation sideTexture = ResourceLocation.parse(baseTexturePath);
    ResourceLocation overlayTexture = ResourceLocation.parse(overlayTexturePath);

    ModelFile model = models().getBuilder(name)
        .parent(models().getExistingFile(mcLoc("block/block")))
        .renderType("cutout")
        .texture("particle", particleTexture)
        .texture("side", sideTexture)
        .texture("overlay", overlayTexture)
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.DOWN).end()
        .face(Direction.UP).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.UP).end()
        .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.NORTH).end()
        .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.SOUTH).end()
        .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.WEST).end()
        .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.EAST).end()
        .end()
        .element()
        .from(0, 0, 0)
        .to(16, 16, 16)
        .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.NORTH).end()
        .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.SOUTH).end()
        .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.WEST).end()
        .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.EAST).end()
        .face(Direction.UP).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.UP).end()
        .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#overlay").cullface(Direction.DOWN).end()
        .end();

    return model;
  }
}