/* (C)2025 */
package net.redfox.metalica.datagen.models;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class MyBlockModelProvider extends MyModelProvider<MyBlockModelBuilder> {

  public MyBlockModelProvider(
      PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
    super(output, modid, BLOCK_FOLDER, MyBlockModelBuilder::new, existingFileHelper);
  }

  @NotNull
  @Override
  public String getName() {
    return "Block Models: " + modid;
  }
}
