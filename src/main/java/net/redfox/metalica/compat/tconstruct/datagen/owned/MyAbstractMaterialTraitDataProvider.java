/* (C)2025 */
package net.redfox.metalica.compat.tconstruct.datagen.owned;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.traits.MaterialTraits;
import slimeknights.tconstruct.library.materials.traits.MaterialTraitsManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;

/** Base data generator for use in addons */
@SuppressWarnings({"unused", "SameParameterValue"}) // API
public abstract class MyAbstractMaterialTraitDataProvider extends GenericDataProvider {
  private static final Gson GSON =
      (new GsonBuilder())
          .registerTypeAdapter(ModifierEntry.class, ModifierEntry.LOADABLE)
          .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
          .setPrettyPrinting()
          .disableHtmlEscaping()
          .create();

  /** Map of material ID to builder, there is at most one builder for each ID */
  private final Map<MaterialId, MaterialTraits.Builder> allMaterialTraits = new HashMap<>();

  /* Materials data provider for validation */
  private final AbstractMaterialDataProvider materials;

  public MyAbstractMaterialTraitDataProvider(
      PackOutput packOutput, AbstractMaterialDataProvider materials) {
    super(packOutput, Target.DATA_PACK, MaterialTraitsManager.FOLDER, GSON);
    this.materials = materials;
  }

  /** Adds all relevant material stats */
  protected abstract void addMaterialTraits();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addMaterialTraits();

    // generate
    return allOf(
        allMaterialTraits.entrySet().stream()
            .map(entry -> saveJson(cache, entry.getKey(), entry.getValue().serialize())));
  }

  /* Helpers */

  /**
   * Gets the material traits object from the map, or creates one if needed
   *
   * @param location Material ID
   * @return MaterialTraits object, creating one if needed
   */
  private MaterialTraits.Builder getOrCreateMaterialTraits(MaterialId location) {
    return allMaterialTraits.computeIfAbsent(location, id -> new MaterialTraits.Builder());
  }

  /**
   * Declares the given material with no traits
   *
   * @param location Material ID
   */
  protected void noTraits(MaterialId location) {
    getOrCreateMaterialTraits(location);
  }

  /**
   * Adds a set of material stats for the given material ID
   *
   * @param location Material ID
   * @param traits Traits to add
   */
  protected void addDefaultTraits(MaterialId location, ModifierEntry... traits) {
    getOrCreateMaterialTraits(location).setDefaultTraits(Arrays.asList(traits));
  }

  /**
   * Adds a set of material stats for the given material ID
   *
   * @param location Material ID
   * @param traits Traits to add
   */
  protected void addDefaultTraits(MaterialId location, ModifierId... traits) {
    getOrCreateMaterialTraits(location)
        .setDefaultTraits(
            Arrays.stream(traits)
                .map(trait -> new ModifierEntry(trait, 1))
                .collect(Collectors.toList()));
  }

  /**
   * Adds a set of material stats for the given material ID
   *
   * @param location Material ID
   * @param traits Traits to add
   */
  protected void addDefaultTraits(MaterialId location, LazyModifier... traits) {
    getOrCreateMaterialTraits(location)
        .setDefaultTraits(
            Arrays.stream(traits)
                .map(trait -> new ModifierEntry(trait.getId(), 1))
                .collect(Collectors.toList()));
  }

  /**
   * Adds a set of material stats for the given material ID and stat ID
   *
   * @param location Material ID
   * @param statsId Stats to add the trait for
   * @param traits Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, ModifierEntry... traits) {
    getOrCreateMaterialTraits(location).setTraits(statsId, Arrays.asList(traits));
  }

  /**
   * Adds a set of material stats for the given material ID
   *
   * @param location Material ID
   * @param statsId Stats to add the trait for
   * @param traits Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, ModifierId... traits) {
    getOrCreateMaterialTraits(location)
        .setTraits(
            statsId,
            Arrays.stream(traits)
                .map(trait -> new ModifierEntry(trait, 1))
                .collect(Collectors.toList()));
  }

  /**
   * Adds a set of material stats for the given material ID
   *
   * @param location Material ID
   * @param statsId Stats to add the trait for
   * @param traits Traits to add
   */
  protected void addTraits(MaterialId location, MaterialStatsId statsId, LazyModifier... traits) {
    getOrCreateMaterialTraits(location)
        .setTraits(
            statsId,
            Arrays.stream(traits)
                .map(trait -> new ModifierEntry(trait.getId(), 1))
                .collect(Collectors.toList()));
  }
}
