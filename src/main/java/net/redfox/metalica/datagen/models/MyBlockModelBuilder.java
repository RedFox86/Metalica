/* (C)2025 */
package net.redfox.metalica.datagen.models;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MyBlockModelBuilder extends MyModelBuilder<MyBlockModelBuilder> {
  public MyBlockModelBuilder(
      ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
    super(outputLocation, existingFileHelper);
  }

  @Override
  public JsonObject toJson() {
    return super.toJson();
  }
}
