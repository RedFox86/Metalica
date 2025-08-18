/* (C)2025 */
package net.redfox.metalica.datagen.models;

import java.util.Objects;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class MyItemModelProvider extends MyModelProvider<MyItemModelBuilder> {

  public MyItemModelProvider(
      PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
    super(output, modid, ITEM_FOLDER, MyItemModelBuilder::new, existingFileHelper);
  }

  public MyItemModelBuilder basicItem(Item item) {
    return basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
  }

  public MyItemModelBuilder basicItem(ResourceLocation item) {
    return getBuilder(item.toString())
        .parent(new ModelFile.UncheckedModelFile("item/generated"))
        .texture(
            "layer0",
            ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
  }

  @NotNull
  @Override
  public String getName() {
    return "Item Models: " + modid;
  }
}
