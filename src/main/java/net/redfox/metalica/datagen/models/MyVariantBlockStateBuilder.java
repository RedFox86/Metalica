package net.redfox.metalica.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class MyVariantBlockStateBuilder implements IGeneratedBlockState
{

  private final Block owner;
  private final Map<MyVariantBlockStateBuilder.PartialBlockstate, MyBlockStateProvider.ConfiguredModelList> models = new LinkedHashMap<>();
  private final Set<BlockState> coveredStates = new HashSet<>();

  MyVariantBlockStateBuilder(Block owner) {
    this.owner = owner;
  }

  public Map<MyVariantBlockStateBuilder.PartialBlockstate, MyBlockStateProvider.ConfiguredModelList> getModels() {
    return models;
  }

  public Block getOwner() {
    return owner;
  }

  @Override
  public JsonObject toJson() {
    List<BlockState> missingStates = Lists.newArrayList(owner.getStateDefinition().getPossibleStates());
    missingStates.removeAll(coveredStates);
    Preconditions.checkState(missingStates.isEmpty(), "Blockstate for block %s does not cover all states. Missing: %s", owner, missingStates);
    JsonObject variants = new JsonObject();
    getModels().entrySet().stream()
        .sorted(Map.Entry.comparingByKey(MyVariantBlockStateBuilder.PartialBlockstate.comparingByProperties()))
        .forEach(entry -> variants.add(entry.getKey().toString(), entry.getValue().toJSON()));
    JsonObject main = new JsonObject();
    main.add("variants", variants);
    return main;
  }

  /**
   * Assign some models to a given {@link net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate partial state}.
   *
   * @param state  The {@link net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate partial state} for which to add
   *               the models
   * @param models A set of models to add to this state
   * @return this builder
   * @throws NullPointerException     if {@code state} is {@code null}
   * @throws IllegalArgumentException if {@code models} is empty
   * @throws IllegalArgumentException if {@code state}'s owning block differs from
   *                                  the builder's
   * @throws IllegalArgumentException if {@code state} partially matches another
   *                                  state which has already been configured
   */
  public MyVariantBlockStateBuilder addModels(MyVariantBlockStateBuilder.PartialBlockstate state, MyConfiguredModel... models) {
    Preconditions.checkNotNull(state, "state must not be null");
    Preconditions.checkArgument(models.length > 0, "Cannot set models to empty array");
    Preconditions.checkArgument(state.getOwner() == owner, "Cannot set models for a different block. Found: %s, Current: %s", state.getOwner(), owner);
    if (!this.models.containsKey(state)) {
      Preconditions.checkArgument(disjointToAll(state), "Cannot set models for a state for which a partial match has already been configured");
      this.models.put(state, new MyBlockStateProvider.ConfiguredModelList(models));
      for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
        if (state.test(fullState)) {
          coveredStates.add(fullState);
        }
      }
    } else {
      this.models.compute(state, ($, cml) -> cml.append(models));
    }
    return this;
  }

  /**
   * Assign some models to a given {@link net.minecraftforge.client.model.generators.VariantBlockStateBuilder.PartialBlockstate partial state},
   * throwing an exception if the state has already been configured. Otherwise,
   * simply calls {@link #addModels(MyVariantBlockStateBuilder.PartialBlockstate, MyConfiguredModel...)}.
   *
   * @param state  The {@link MyVariantBlockStateBuilder.PartialBlockstate partial state} for which to set
   *               the models
   * @param model A set of models to assign to this state
   * @return this builder
   * @throws IllegalArgumentException if {@code state} has already been configured
   * @see #addModels(MyVariantBlockStateBuilder.PartialBlockstate, MyConfiguredModel...)
   */
  public MyVariantBlockStateBuilder setModels(MyVariantBlockStateBuilder.PartialBlockstate state, MyConfiguredModel... model) {
    Preconditions.checkArgument(!models.containsKey(state), "Cannot set models for a state that has already been configured: %s", state);
    addModels(state, model);
    return this;
  }

  private boolean disjointToAll(MyVariantBlockStateBuilder.PartialBlockstate newState) {
    return coveredStates.stream().noneMatch(newState);
  }

  public MyVariantBlockStateBuilder.PartialBlockstate partialState() {
    return new MyVariantBlockStateBuilder.PartialBlockstate(owner, this);
  }

  public MyVariantBlockStateBuilder forAllStates(Function<BlockState, MyConfiguredModel[]> mapper) {
    return forAllStatesExcept(mapper);
  }

  public MyVariantBlockStateBuilder forAllStatesExcept(Function<BlockState, MyConfiguredModel[]> mapper, Property<?>... ignored) {
    Set<MyVariantBlockStateBuilder.PartialBlockstate> seen = new HashSet<>();
    for (BlockState fullState : owner.getStateDefinition().getPossibleStates()) {
      Map<Property<?>, Comparable<?>> propertyValues = Maps.newLinkedHashMap(fullState.getValues());
      for (Property<?> p : ignored) {
        propertyValues.remove(p);
      }
      MyVariantBlockStateBuilder.PartialBlockstate partialState = new MyVariantBlockStateBuilder.PartialBlockstate(owner, propertyValues, this);
      if (seen.add(partialState)) {
        setModels(partialState, mapper.apply(fullState));
      }
    }
    return this;
  }

  public static class PartialBlockstate implements Predicate<BlockState> {
    private final Block owner;
    private final SortedMap<Property<?>, Comparable<?>> setStates;
    @Nullable
    private final MyVariantBlockStateBuilder outerBuilder;

    PartialBlockstate(Block owner, @Nullable MyVariantBlockStateBuilder outerBuilder) {
      this(owner, ImmutableMap.of(), outerBuilder);
    }

    PartialBlockstate(Block owner, Map<Property<?>, Comparable<?>> setStates, @Nullable MyVariantBlockStateBuilder outerBuilder) {
      this.owner = owner;
      this.outerBuilder = outerBuilder;
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        Property<?> prop = entry.getKey();
        Comparable<?> value = entry.getValue();
        Preconditions.checkArgument(owner.getStateDefinition().getProperties().contains(prop), "Property %s not found on block %s", entry, this.owner);
        Preconditions.checkArgument(prop.getPossibleValues().contains(value), "%s is not a valid value for %s", value, prop);
      }
      this.setStates = Maps.newTreeMap(Comparator.comparing(Property::getName));
      this.setStates.putAll(setStates);
    }

    public <T extends Comparable<T>> MyVariantBlockStateBuilder.PartialBlockstate with(Property<T> prop, T value) {
      Preconditions.checkArgument(!setStates.containsKey(prop), "Property %s has already been set", prop);
      Map<Property<?>, Comparable<?>> newState = new HashMap<>(setStates);
      newState.put(prop, value);
      return new MyVariantBlockStateBuilder.PartialBlockstate(owner, newState, outerBuilder);
    }

    private void checkValidOwner() {
      Preconditions.checkNotNull(outerBuilder, "Partial blockstate must have a valid owner to perform this action");
    }

    /**
     * Creates a builder for models to assign to this state, which when completed
     * via {@link MyConfiguredModel.Builder#addModel()} will assign the resultant set
     * of models to this state.
     *
     * @return the model builder
     * @see MyConfiguredModel.Builder
     */
    public MyConfiguredModel.Builder<MyVariantBlockStateBuilder> modelForState() {
      checkValidOwner();
      return MyConfiguredModel.builder(outerBuilder, this);
    }

    /**
     * Add models to the current state's variant. For use when it is more convenient
     * to add multiple sets of models, as a replacement for
     * {@link #setModels(MyConfiguredModel...)}.
     *
     * @param models The models to add.
     * @return {@code this}
     * @throws NullPointerException If the parent builder is {@code null}
     */
    public MyVariantBlockStateBuilder.PartialBlockstate addModels(MyConfiguredModel... models) {
      checkValidOwner();
      outerBuilder.addModels(this, models);
      return this;
    }

    /**
     * Set this variant's models, and return the parent builder.
     *
     * @param models The models to set
     * @return The parent builder instance
     * @throws NullPointerException If the parent builder is {@code null}
     */
    public MyVariantBlockStateBuilder setModels(MyConfiguredModel... models) {
      checkValidOwner();
      return outerBuilder.setModels(this, models);
    }

    /**
     * Complete this state without adding any new models, and return a new partial
     * state via the parent builder. For use after calling
     * {@link #addModels(MyConfiguredModel...)}.
     *
     * @return A fresh partial state as specified by
     *         {@link MyVariantBlockStateBuilder#partialState()}.
     * @throws NullPointerException If the parent builder is {@code null}
     */
    public MyVariantBlockStateBuilder.PartialBlockstate partialState() {
      checkValidOwner();
      return outerBuilder.partialState();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MyVariantBlockStateBuilder.PartialBlockstate that = (MyVariantBlockStateBuilder.PartialBlockstate) o;
      return owner.equals(that.owner) &&
          setStates.equals(that.setStates);
    }

    @Override
    public int hashCode() {
      return Objects.hash(owner, setStates);
    }

    public Block getOwner() {
      return owner;
    }

    public SortedMap<Property<?>, Comparable<?>> getSetStates() {
      return setStates;
    }

    @Override
    public boolean test(BlockState blockState) {
      if (blockState.getBlock() != getOwner()) {
        return false;
      }
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        if (blockState.getValue(entry.getKey()) != entry.getValue()) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      StringBuilder ret = new StringBuilder();
      for (Map.Entry<Property<?>, Comparable<?>> entry : setStates.entrySet()) {
        if (ret.length() > 0) {
          ret.append(',');
        }
        ret.append(entry.getKey().getName())
            .append('=')
            .append(((Property) entry.getKey()).getName(entry.getValue()));
      }
      return ret.toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Comparator<MyVariantBlockStateBuilder.PartialBlockstate> comparingByProperties() {
      // Sort variants inversely by property values, to approximate vanilla style
      return (s1, s2) -> {
        SortedSet<Property<?>> propUniverse = new TreeSet<>(s1.getSetStates().comparator().reversed());
        propUniverse.addAll(s1.getSetStates().keySet());
        propUniverse.addAll(s2.getSetStates().keySet());
        for (Property<?> prop : propUniverse) {
          Comparable val1 = s1.getSetStates().get(prop);
          Comparable val2 = s2.getSetStates().get(prop);
          if (val1 == null && val2 != null) {
            return -1;
          } else if (val2 == null && val1 != null) {
            return 1;
          } else if (val1 != null && val2 != null){
            int cmp = val1.compareTo(val2);
            if (cmp != 0) {
              return cmp;
            }
          }
        }
        return 0;
      };
    }
  }
}
