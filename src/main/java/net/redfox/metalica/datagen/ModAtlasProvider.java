package net.redfox.metalica.datagen;

import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModAtlasProvider extends SpriteSourceProvider {
  List<ResourceLocation> textures = List.of(
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "item/ingot"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "item/nugget"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "item/dust"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "item/raw"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/ore"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "item/liquid"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/storage_block"),
      ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "block/raw_storage_block")
      );
  ResourceLocation key = ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "color/palette");

  public ModAtlasProvider(PackOutput output, ExistingFileHelper fileHelper) {
    super(output, fileHelper, Metalica.MOD_ID);
  }

  @Override
  protected void addSources() {
    Map<String, ResourceLocation> permutations = new HashMap<>();
    for (MetalMaterial material : MetalMaterial.getMaterials()) {
      permutations.put(material.getName(), ResourceLocation.fromNamespaceAndPath(Metalica.MOD_ID, "color/"+material.getName()));
    }

    atlas(ResourceLocation.withDefaultNamespace("blocks"))
        .addSource(new PalettedPermutations(textures, key, permutations));
  }
}