package net.redfox.metalica.datagen.models;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Builder for item models, adds the ability to build overrides via
 * {@link #override()}.
 */
public class MyItemModelBuilder extends MyModelBuilder<MyItemModelBuilder> {

  protected List<MyItemModelBuilder.OverrideBuilder> overrides = new ArrayList<>();

  public MyItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
    super(outputLocation, existingFileHelper);
  }

  public MyItemModelBuilder.OverrideBuilder override() {
    MyItemModelBuilder.OverrideBuilder ret = new MyItemModelBuilder.OverrideBuilder();
    overrides.add(ret);
    return ret;
  }

  /**
   * Get an existing override builder
   *
   * @param index the index of the existing override builder
   * @return the override builder
   * @throws IndexOutOfBoundsException if {@code} index is out of bounds
   */
  public MyItemModelBuilder.OverrideBuilder override(int index) {
    Preconditions.checkElementIndex(index, overrides.size(), "override");
    return overrides.get(index);
  }

  @Override
  public JsonObject toJson() {
    JsonObject root = super.toJson();
    if (!overrides.isEmpty()) {
      JsonArray overridesJson = new JsonArray();
      overrides.stream().map(MyItemModelBuilder.OverrideBuilder::toJson).forEach(overridesJson::add);
      root.add("overrides", overridesJson);
    }
    return root;
  }

  public class OverrideBuilder {

    private ModelFile model;
    private final Map<ResourceLocation, Float> predicates = new LinkedHashMap<>();

    public MyItemModelBuilder.OverrideBuilder model(ModelFile model) {
      this.model = model;
      model.assertExistence();
      return this;
    }

    public MyItemModelBuilder.OverrideBuilder predicate(ResourceLocation key, float value) {
      this.predicates.put(key, value);
      return this;
    }

    public MyItemModelBuilder end() { return MyItemModelBuilder.this; }

    JsonObject toJson() {
      JsonObject ret = new JsonObject();
      JsonObject predicatesJson = new JsonObject();
      predicates.forEach((key, val) -> predicatesJson.addProperty(key.toString(), val));
      ret.add("predicate", predicatesJson);
      ret.addProperty("model", model.getLocation().toString());
      return ret;
    }
  }

}
