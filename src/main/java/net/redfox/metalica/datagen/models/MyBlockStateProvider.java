package net.redfox.metalica.datagen.models;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class MyBlockStateProvider implements DataProvider {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

  @VisibleForTesting
  protected final Map<Block, IGeneratedBlockState> registeredBlocks = new LinkedHashMap<>();

  private final PackOutput output;
  private final String modid;
  private final MyBlockModelProvider blockModels;
  private final MyItemModelProvider itemModels;

  public MyBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
    this.output = output;
    this.modid = modid;
    this.blockModels = new MyBlockModelProvider(output, modid, exFileHelper) {
      @Override public CompletableFuture<?> run(CachedOutput cache) { return CompletableFuture.allOf(); }

      @Override protected void registerModels() {}
    };
    this.itemModels = new MyItemModelProvider(output, modid, this.blockModels.existingFileHelper) {
      @Override protected void registerModels() {}

      @Override public CompletableFuture<?> run(CachedOutput cache) { return CompletableFuture.allOf(); }
    };
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    models().clear();
    itemModels().clear();
    registeredBlocks.clear();
    registerStatesAndModels();
    CompletableFuture<?>[] futures = new CompletableFuture<?>[2 + this.registeredBlocks.size()];
    int i = 0;
    futures[i++] = models().generateAll(cache);
    futures[i++] = itemModels().generateAll(cache);
    for (Map.Entry<Block, IGeneratedBlockState> entry : registeredBlocks.entrySet()) {
      futures[i++] = saveBlockState(cache, entry.getValue().toJson(), entry.getKey());
    }
    return CompletableFuture.allOf(futures);
  }

  protected abstract void registerStatesAndModels();

  public MyVariantBlockStateBuilder getVariantBuilder(Block b) {
    if (registeredBlocks.containsKey(b)) {
      IGeneratedBlockState old = registeredBlocks.get(b);
      Preconditions.checkState(old instanceof MyVariantBlockStateBuilder);
      return (MyVariantBlockStateBuilder) old;
    } else {
      MyVariantBlockStateBuilder ret = new MyVariantBlockStateBuilder(b);
      registeredBlocks.put(b, ret);
      return ret;
    }
  }

  public MultiPartBlockStateBuilder getMultipartBuilder(Block b) {
    if (registeredBlocks.containsKey(b)) {
      IGeneratedBlockState old = registeredBlocks.get(b);
      Preconditions.checkState(old instanceof MultiPartBlockStateBuilder);
      return (MultiPartBlockStateBuilder) old;
    } else {
      MultiPartBlockStateBuilder ret = new MultiPartBlockStateBuilder(b);
      registeredBlocks.put(b, ret);
      return ret;
    }
  }

  public MyBlockModelProvider models() {
    return blockModels;
  }

  public MyItemModelProvider itemModels() {
    return itemModels;
  }

  public ResourceLocation modLoc(String name) {
    return new ResourceLocation(modid, name);
  }

  public ResourceLocation mcLoc(String name) {
    return new ResourceLocation(name);
  }

  private ResourceLocation key(Block block) {
    return ForgeRegistries.BLOCKS.getKey(block);
  }

  private String name(Block block) {
    return key(block).getPath();
  }

  public ResourceLocation blockTexture(Block block) {
    ResourceLocation name = key(block);
    return new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
  }

  private ResourceLocation extend(ResourceLocation rl, String suffix) {
    return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
  }

  public ModelFile cubeAll(Block block) {
    return models().cubeAll(name(block), blockTexture(block));
  }

  public void simpleBlock(Block block) {
    simpleBlock(block, cubeAll(block));
  }

  public void simpleBlock(Block block, Function<ModelFile, MyConfiguredModel[]> expander) {
    simpleBlock(block, expander.apply(cubeAll(block)));
  }

  public void simpleBlock(Block block, ModelFile model) {
    simpleBlock(block, new MyConfiguredModel(model));
  }

  public void simpleBlockItem(Block block, ModelFile model) {
    itemModels().getBuilder(key(block).getPath()).parent(model);
  }

  public void simpleBlockWithItem(Block block, ModelFile model) {
    simpleBlock(block, model);
    simpleBlockItem(block, model);
  }

  public void simpleBlock(Block block, MyConfiguredModel... models) {
    getVariantBuilder(block)
        .partialState().setModels(models);
  }

  public void axisBlock(RotatedPillarBlock block) {
    axisBlock(block, blockTexture(block));
  }

  public void logBlock(RotatedPillarBlock block) {
    axisBlock(block, blockTexture(block), extend(blockTexture(block), "_top"));
  }

  public void axisBlock(RotatedPillarBlock block, ResourceLocation baseName) {
    axisBlock(block, extend(baseName, "_side"), extend(baseName, "_end"));
  }

  public void axisBlock(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end) {
    axisBlock(block,
        models().cubeColumn(name(block), side, end),
        models().cubeColumnHorizontal(name(block) + "_horizontal", side, end));
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, String renderType) {
    axisBlockWithRenderType(block, blockTexture(block), renderType);
  }

  public void logBlockWithRenderType(RotatedPillarBlock block, String renderType) {
    axisBlockWithRenderType(block, blockTexture(block), extend(blockTexture(block), "_top"), renderType);
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation baseName, String renderType) {
    axisBlockWithRenderType(block, extend(baseName, "_side"), extend(baseName, "_end"), renderType);
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, String renderType) {
    axisBlock(block,
        models().cubeColumn(name(block), side, end).renderType(renderType),
        models().cubeColumnHorizontal(name(block) + "_horizontal", side, end).renderType(renderType));
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType) {
    axisBlockWithRenderType(block, blockTexture(block), renderType);
  }

  public void logBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType) {
    axisBlockWithRenderType(block, blockTexture(block), extend(blockTexture(block), "_top"), renderType);
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation baseName, ResourceLocation renderType) {
    axisBlockWithRenderType(block, extend(baseName, "_side"), extend(baseName, "_end"), renderType);
  }

  public void axisBlockWithRenderType(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType) {
    axisBlock(block,
        models().cubeColumn(name(block), side, end).renderType(renderType),
        models().cubeColumnHorizontal(name(block) + "_horizontal", side, end).renderType(renderType));
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, String renderType, String renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, blockTexture(block), renderType, renderTypeFast);
  }

  public void logBlockWithRenderTypeAndFast(RotatedPillarBlock block, String renderType, String renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, blockTexture(block), extend(blockTexture(block), "_top"), renderType, renderTypeFast);
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation baseName, String renderType, String renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, extend(baseName, "_side"), extend(baseName, "_end"), renderType, renderTypeFast);
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, String renderType, String renderTypeFast) {
    axisBlock(block,
        models().cubeColumn(name(block), side, end).renderType(renderType, renderTypeFast),
        models().cubeColumnHorizontal(name(block) + "_horizontal", side, end).renderType(renderType, renderTypeFast));
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, blockTexture(block), renderType, renderTypeFast);
  }

  public void logBlockWithRenderType(RotatedPillarBlock block, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, blockTexture(block), extend(blockTexture(block), "_top"), renderType, renderTypeFast);
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation baseName, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    axisBlockWithRenderTypeAndFast(block, extend(baseName, "_side"), extend(baseName, "_end"), renderType, renderTypeFast);
  }

  public void axisBlockWithRenderTypeAndFast(RotatedPillarBlock block, ResourceLocation side, ResourceLocation end, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    axisBlock(block,
        models().cubeColumn(name(block), side, end).renderType(renderType, renderTypeFast),
        models().cubeColumnHorizontal(name(block) + "_horizontal", side, end).renderType(renderType, renderTypeFast));
  }

  public void axisBlock(RotatedPillarBlock block, ModelFile vertical, ModelFile horizontal) {
    getVariantBuilder(block)
        .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y)
        .modelForState().modelFile(vertical).addModel()
        .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.Z)
        .modelForState().modelFile(horizontal).rotationX(90).addModel()
        .partialState().with(RotatedPillarBlock.AXIS, Direction.Axis.X)
        .modelForState().modelFile(horizontal).rotationX(90).rotationY(90).addModel();
  }

  private static final int DEFAULT_ANGLE_OFFSET = 180;

  public void horizontalBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation top) {
    horizontalBlock(block, models().orientable(name(block), side, front, top));
  }

  public void horizontalBlock(Block block, ModelFile model) {
    horizontalBlock(block, model, DEFAULT_ANGLE_OFFSET);
  }

  public void horizontalBlock(Block block, ModelFile model, int angleOffset) {
    horizontalBlock(block, $ -> model, angleOffset);
  }

  public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
    horizontalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
  }

  public void horizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
    getVariantBuilder(block)
        .forAllStates(state -> MyConfiguredModel.builder()
            .modelFile(modelFunc.apply(state))
            .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) % 360)
            .build()
        );
  }

  public void horizontalFaceBlock(Block block, ModelFile model) {
    horizontalFaceBlock(block, model, DEFAULT_ANGLE_OFFSET);
  }

  public void horizontalFaceBlock(Block block, ModelFile model, int angleOffset) {
    horizontalFaceBlock(block, $ -> model, angleOffset);
  }

  public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
    horizontalFaceBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
  }

  public void horizontalFaceBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
    getVariantBuilder(block)
        .forAllStates(state -> MyConfiguredModel.builder()
            .modelFile(modelFunc.apply(state))
            .rotationX(state.getValue(BlockStateProperties.ATTACH_FACE).ordinal() * 90)
            .rotationY((((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + angleOffset) + (state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? 180 : 0)) % 360)
            .build()
        );
  }

  public void directionalBlock(Block block, ModelFile model) {
    directionalBlock(block, model, DEFAULT_ANGLE_OFFSET);
  }

  public void directionalBlock(Block block, ModelFile model, int angleOffset) {
    directionalBlock(block, $ -> model, angleOffset);
  }

  public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
    directionalBlock(block, modelFunc, DEFAULT_ANGLE_OFFSET);
  }

  public void directionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, int angleOffset) {
    getVariantBuilder(block)
        .forAllStates(state -> {
          Direction dir = state.getValue(BlockStateProperties.FACING);
          return MyConfiguredModel.builder()
              .modelFile(modelFunc.apply(state))
              .rotationX(dir == Direction.DOWN ? 180 : dir.getAxis().isHorizontal() ? 90 : 0)
              .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + angleOffset) % 360)
              .build();
        });
  }

  public void stairsBlock(StairBlock block, ResourceLocation texture) {
    stairsBlock(block, texture, texture, texture);
  }

  public void stairsBlock(StairBlock block, String name, ResourceLocation texture) {
    stairsBlock(block, name, texture, texture, texture);
  }

  public void stairsBlock(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    stairsBlockInternal(block, key(block).toString(), side, bottom, top);
  }

  public void stairsBlock(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    stairsBlockInternal(block, name + "_stairs", side, bottom, top);
  }

  public void stairsBlockWithRenderType(StairBlock block, ResourceLocation texture, String renderType) {
    stairsBlockWithRenderType(block, texture, texture, texture, renderType);
  }

  public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation texture, String renderType) {
    stairsBlockWithRenderType(block, name, texture, texture, texture, renderType);
  }

  public void stairsBlockWithRenderType(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
    stairsBlockInternalWithRenderType(block, key(block).toString(), side, bottom, top, ResourceLocation.tryParse(renderType));
  }

  public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
    stairsBlockInternalWithRenderType(block, name + "_stairs", side, bottom, top, ResourceLocation.tryParse(renderType));
  }

  public void stairsBlockWithRenderType(StairBlock block, ResourceLocation texture, ResourceLocation renderType) {
    stairsBlockWithRenderType(block, texture, texture, texture, renderType);
  }

  public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
    stairsBlockWithRenderType(block, name, texture, texture, texture, renderType);
  }

  public void stairsBlockWithRenderType(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    stairsBlockInternalWithRenderType(block, key(block).toString(), side, bottom, top, renderType);
  }

  public void stairsBlockWithRenderType(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    stairsBlockInternalWithRenderType(block, name + "_stairs", side, bottom, top, renderType);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
    stairsBlockWithRenderTypeAndFast(block, texture, texture, texture, renderType, renderTypeFast);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
    stairsBlockWithRenderTypeAndFast(block, name, texture, texture, texture, renderType, renderTypeFast);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
    stairsBlockInternalWithRenderTypeAndFast(block, key(block).toString(), side, bottom, top, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
    stairsBlockInternalWithRenderTypeAndFast(block, name + "_stairs", side, bottom, top, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    stairsBlockWithRenderTypeAndFast(block, texture, texture, texture, renderType, renderTypeFast);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    stairsBlockWithRenderTypeAndFast(block, name, texture, texture, texture, renderType, renderTypeFast);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    stairsBlockInternalWithRenderTypeAndFast(block, key(block).toString(), side, bottom, top, renderType, renderTypeFast);
  }

  public void stairsBlockWithRenderTypeAndFast(StairBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    stairsBlockInternalWithRenderTypeAndFast(block, name + "_stairs", side, bottom, top, renderType, renderTypeFast);
  }

  private void stairsBlockInternal(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    ModelFile stairs = models().stairs(baseName, side, bottom, top);
    ModelFile stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top);
    ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top);
    stairsBlock(block, stairs, stairsInner, stairsOuter);
  }

  private void stairsBlockInternalWithRenderType(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    ModelFile stairs = models().stairs(baseName, side, bottom, top).renderType(renderType);
    ModelFile stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top).renderType(renderType);
    ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top).renderType(renderType);
    stairsBlock(block, stairs, stairsInner, stairsOuter);
  }

  private void stairsBlockInternalWithRenderTypeAndFast(StairBlock block, String baseName, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    ModelFile stairs = models().stairs(baseName, side, bottom, top).renderType(renderType, renderTypeFast);
    ModelFile stairsInner = models().stairsInner(baseName + "_inner", side, bottom, top).renderType(renderType, renderTypeFast);
    ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", side, bottom, top).renderType(renderType, renderTypeFast);
    stairsBlock(block, stairs, stairsInner, stairsOuter);
  }

  public void stairsBlock(StairBlock block, ModelFile stairs, ModelFile stairsInner, ModelFile stairsOuter) {
    getVariantBuilder(block)
        .forAllStatesExcept(state -> {
          Direction facing = state.getValue(StairBlock.FACING);
          Half half = state.getValue(StairBlock.HALF);
          StairsShape shape = state.getValue(StairBlock.SHAPE);
          int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees clockwise for some reason
          if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
            yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
          }
          if (shape != StairsShape.STRAIGHT && half == Half.TOP) {
            yRot += 90; // Top stairs are rotated 90 degrees clockwise
          }
          yRot %= 360;
          boolean uvlock = yRot != 0 || half == Half.TOP; // Don't set uvlock for states that have no rotation
          return MyConfiguredModel.builder()
              .modelFile(shape == StairsShape.STRAIGHT ? stairs : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? stairsInner : stairsOuter)
              .rotationX(half == Half.BOTTOM ? 0 : 180)
              .rotationY(yRot)
              .uvLock(uvlock)
              .build();
        }, StairBlock.WATERLOGGED);
  }

  public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
    slabBlock(block, doubleslab, texture, texture, texture);
  }

  public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
    slabBlock(block, models().slab(name(block), side, bottom, top), models().slabTop(name(block) + "_top", side, bottom, top), models().getExistingFile(doubleslab));
  }

  public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
    getVariantBuilder(block)
        .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new MyConfiguredModel(bottom))
        .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new MyConfiguredModel(top))
        .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new MyConfiguredModel(doubleslab));
  }

  public void buttonBlock(ButtonBlock block, ResourceLocation texture) {
    ModelFile button = models().button(name(block), texture);
    ModelFile buttonPressed = models().buttonPressed(name(block) + "_pressed", texture);
    buttonBlock(block, button, buttonPressed);
  }

  public void buttonBlock(ButtonBlock block, ModelFile button, ModelFile buttonPressed) {
    getVariantBuilder(block).forAllStates(state -> {
      Direction facing = state.getValue(ButtonBlock.FACING);
      AttachFace face = state.getValue(ButtonBlock.FACE);
      boolean powered = state.getValue(ButtonBlock.POWERED);

      return MyConfiguredModel.builder()
          .modelFile(powered ? buttonPressed : button)
          .rotationX(face == AttachFace.FLOOR ? 0 : (face == AttachFace.WALL ? 90 : 180))
          .rotationY((int) (face == AttachFace.CEILING ? facing : facing.getOpposite()).toYRot())
          .uvLock(face == AttachFace.WALL)
          .build();
    });
  }

  public void pressurePlateBlock(PressurePlateBlock block, ResourceLocation texture) {
    ModelFile pressurePlate = models().pressurePlate(name(block), texture);
    ModelFile pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture);
    pressurePlateBlock(block, pressurePlate, pressurePlateDown);
  }

  public void pressurePlateBlock(PressurePlateBlock block, ModelFile pressurePlate, ModelFile pressurePlateDown) {
    getVariantBuilder(block)
        .partialState().with(PressurePlateBlock.POWERED, true).addModels(new MyConfiguredModel(pressurePlateDown))
        .partialState().with(PressurePlateBlock.POWERED, false).addModels(new MyConfiguredModel(pressurePlate));
  }

  public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ResourceLocation texture) {
    ModelFile sign = models().sign(name(signBlock), texture);
    signBlock(signBlock, wallSignBlock, sign);
  }

  public void signBlock(StandingSignBlock signBlock, WallSignBlock wallSignBlock, ModelFile sign) {
    simpleBlock(signBlock, sign);
    simpleBlock(wallSignBlock, sign);
  }

  public void fourWayBlock(CrossCollisionBlock block, ModelFile post, ModelFile side) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
        .part().modelFile(post).addModel().end();
    fourWayMultipart(builder, side);
  }

  public void fourWayMultipart(MultiPartBlockStateBuilder builder, ModelFile side) {
    PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
      Direction dir = e.getKey();
      if (dir.getAxis().isHorizontal()) {
        builder.part().modelFile(side).rotationY((((int) dir.toYRot()) + 180) % 360).uvLock(true).addModel()
            .condition(e.getValue(), true);
      }
    });
  }

  public void fenceBlock(FenceBlock block, ResourceLocation texture) {
    String baseName = key(block).toString();
    fourWayBlock(block,
        models().fencePost(baseName + "_post", texture),
        models().fenceSide(baseName + "_side", texture));
  }

  public void fenceBlock(FenceBlock block, String name, ResourceLocation texture) {
    fourWayBlock(block,
        models().fencePost(name + "_fence_post", texture),
        models().fenceSide(name + "_fence_side", texture));
  }

  public void fenceBlockWithRenderType(FenceBlock block, ResourceLocation texture, String renderType) {
    String baseName = key(block).toString();
    fourWayBlock(block,
        models().fencePost(baseName + "_post", texture).renderType(renderType),
        models().fenceSide(baseName + "_side", texture).renderType(renderType));
  }

  public void fenceBlockWithRenderType(FenceBlock block, String name, ResourceLocation texture, String renderType) {
    fourWayBlock(block,
        models().fencePost(name + "_fence_post", texture).renderType(renderType),
        models().fenceSide(name + "_fence_side", texture).renderType(renderType));
  }

  public void fenceBlockWithRenderType(FenceBlock block, ResourceLocation texture, ResourceLocation renderType) {
    String baseName = key(block).toString();
    fourWayBlock(block,
        models().fencePost(baseName + "_post", texture).renderType(renderType),
        models().fenceSide(baseName + "_side", texture).renderType(renderType));
  }

  public void fenceBlockWithRenderType(FenceBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
    fourWayBlock(block,
        models().fencePost(name + "_fence_post", texture).renderType(renderType),
        models().fenceSide(name + "_fence_side", texture).renderType(renderType));
  }

  public void fenceBlockWithRenderTypeAndFast(FenceBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
    String baseName = key(block).toString();
    fourWayBlock(block,
        models().fencePost(baseName + "_post", texture).renderType(renderType, renderTypeFast),
        models().fenceSide(baseName + "_side", texture).renderType(renderType, renderTypeFast));
  }

  public void fenceBlockWithRenderTypeAndFast(FenceBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
    fourWayBlock(block,
        models().fencePost(name + "_fence_post", texture).renderType(renderType, renderTypeFast),
        models().fenceSide(name + "_fence_side", texture).renderType(renderType, renderTypeFast));
  }

  public void fenceBlockWithRenderTypeAndFast(FenceBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    String baseName = key(block).toString();
    fourWayBlock(block,
        models().fencePost(baseName + "_post", texture).renderType(renderType, renderTypeFast),
        models().fenceSide(baseName + "_side", texture).renderType(renderType, renderTypeFast));
  }

  public void fenceBlockWithRenderTypeAndFast(FenceBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    fourWayBlock(block,
        models().fencePost(name + "_fence_post", texture).renderType(renderType, renderTypeFast),
        models().fenceSide(name + "_fence_side", texture).renderType(renderType, renderTypeFast));
  }

  public void fenceGateBlock(FenceGateBlock block, ResourceLocation texture) {
    fenceGateBlockInternal(block, key(block).toString(), texture);
  }

  public void fenceGateBlock(FenceGateBlock block, String name, ResourceLocation texture) {
    fenceGateBlockInternal(block, name + "_fence_gate", texture);
  }

  public void fenceGateBlockWithRenderType(FenceGateBlock block, ResourceLocation texture, String renderType) {
    fenceGateBlockInternalWithRenderType(block, key(block).toString(), texture, ResourceLocation.tryParse(renderType));
  }

  public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, ResourceLocation texture, String renderType) {
    fenceGateBlockInternalWithRenderType(block, name + "_fence_gate", texture, ResourceLocation.tryParse(renderType));
  }

  public void fenceGateBlockWithRenderType(FenceGateBlock block, ResourceLocation texture, ResourceLocation renderType) {
    fenceGateBlockInternalWithRenderType(block, key(block).toString(), texture, renderType);
  }

  public void fenceGateBlockWithRenderType(FenceGateBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
    fenceGateBlockInternalWithRenderType(block, name + "_fence_gate", texture, renderType);
  }

  public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
    fenceGateBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
    fenceGateBlockInternalWithRenderTypeAndFast(block, name + "_fence_gate", texture, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    fenceGateBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, renderType, renderTypeFast);
  }

  public void fenceGateBlockWithRenderTypeAndFast(FenceGateBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    fenceGateBlockInternalWithRenderTypeAndFast(block, name + "_fence_gate", texture, renderType, renderTypeFast);
  }

  private void fenceGateBlockInternal(FenceGateBlock block, String baseName, ResourceLocation texture) {
    ModelFile gate = models().fenceGate(baseName, texture);
    ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture);
    ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture);
    ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);
    fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
  }

  private void fenceGateBlockInternalWithRenderType(FenceGateBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType) {
    ModelFile gate = models().fenceGate(baseName, texture).renderType(renderType);
    ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture).renderType(renderType);
    ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture).renderType(renderType);
    ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture).renderType(renderType);
    fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
  }

  private void fenceGateBlockInternalWithRenderTypeAndFast(FenceGateBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    ModelFile gate = models().fenceGate(baseName, texture).renderType(renderType, renderTypeFast);
    ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture).renderType(renderType, renderTypeFast);
    ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture).renderType(renderType, renderTypeFast);
    ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture).renderType(renderType, renderTypeFast);
    fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
  }

  public void fenceGateBlock(FenceGateBlock block, ModelFile gate, ModelFile gateOpen, ModelFile gateWall, ModelFile gateWallOpen) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      ModelFile model = gate;
      if (state.getValue(FenceGateBlock.IN_WALL)) {
        model = gateWall;
      }
      if (state.getValue(FenceGateBlock.OPEN)) {
        model = model == gateWall ? gateWallOpen : gateOpen;
      }
      return MyConfiguredModel.builder()
          .modelFile(model)
          .rotationY((int) state.getValue(FenceGateBlock.FACING).toYRot())
          .uvLock(true)
          .build();
    }, FenceGateBlock.POWERED);
  }

  public void wallBlock(WallBlock block, ResourceLocation texture) {
    wallBlockInternal(block, key(block).toString(), texture);
  }

  public void wallBlock(WallBlock block, String name, ResourceLocation texture) {
    wallBlockInternal(block, name + "_wall", texture);
  }

  public void wallBlockWithRenderType(WallBlock block, ResourceLocation texture, String renderType) {
    wallBlockInternalWithRenderType(block, key(block).toString(), texture, ResourceLocation.tryParse(renderType));
  }

  public void wallBlockWithRenderType(WallBlock block, String name, ResourceLocation texture, String renderType) {
    wallBlockInternalWithRenderType(block, name + "_wall", texture, ResourceLocation.tryParse(renderType));
  }

  public void wallBlockWithRenderType(WallBlock block, ResourceLocation texture, ResourceLocation renderType) {
    wallBlockInternalWithRenderType(block, key(block).toString(), texture, renderType);
  }

  public void wallBlockWithRenderType(WallBlock block, String name, ResourceLocation texture, ResourceLocation renderType) {
    wallBlockInternalWithRenderType(block, name + "_wall", texture, renderType);
  }

  public void wallBlockWithRenderTypeAndFast(WallBlock block, ResourceLocation texture, String renderType, String renderTypeFast) {
    wallBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void wallBlockWithRenderTypeAndFast(WallBlock block, String name, ResourceLocation texture, String renderType, String renderTypeFast) {
    wallBlockInternalWithRenderTypeAndFast(block, name + "_wall", texture, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void wallBlockWithRenderTypeAndFast(WallBlock block, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    wallBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, renderType, renderTypeFast);
  }

  public void wallBlockWithRenderTypeAndFast(WallBlock block, String name, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    wallBlockInternalWithRenderTypeAndFast(block, name + "_wall", texture, renderType, renderTypeFast);
  }

  private void wallBlockInternal(WallBlock block, String baseName, ResourceLocation texture) {
    wallBlock(block, models().wallPost(baseName + "_post", texture),
        models().wallSide(baseName + "_side", texture),
        models().wallSideTall(baseName + "_side_tall", texture));
  }

  private void wallBlockInternalWithRenderType(WallBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType) {
    wallBlock(block, models().wallPost(baseName + "_post", texture).renderType(renderType),
        models().wallSide(baseName + "_side", texture).renderType(renderType),
        models().wallSideTall(baseName + "_side_tall", texture).renderType(renderType));
  }

  private void wallBlockInternalWithRenderTypeAndFast(WallBlock block, String baseName, ResourceLocation texture, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    wallBlock(block, models().wallPost(baseName + "_post", texture).renderType(renderType, renderTypeFast),
        models().wallSide(baseName + "_side", texture).renderType(renderType, renderTypeFast),
        models().wallSideTall(baseName + "_side_tall", texture).renderType(renderType, renderTypeFast));
  }

  public static final ImmutableMap<Direction, Property<WallSide>> WALL_PROPS = ImmutableMap.<Direction, Property<WallSide>>builder()
      .put(Direction.EAST,  BlockStateProperties.EAST_WALL)
      .put(Direction.NORTH, BlockStateProperties.NORTH_WALL)
      .put(Direction.SOUTH, BlockStateProperties.SOUTH_WALL)
      .put(Direction.WEST,  BlockStateProperties.WEST_WALL)
      .build();

  public void wallBlock(WallBlock block, ModelFile post, ModelFile side, ModelFile sideTall) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
        .part().modelFile(post).addModel()
        .condition(WallBlock.UP, true).end();
    WALL_PROPS.entrySet().stream()
        .filter(e -> e.getKey().getAxis().isHorizontal())
        .forEach(e -> {
          wallSidePart(builder, side, e, WallSide.LOW);
          wallSidePart(builder, sideTall, e, WallSide.TALL);
        });
  }

  private void wallSidePart(MultiPartBlockStateBuilder builder, ModelFile model, Map.Entry<Direction, Property<WallSide>> entry, WallSide height) {
    builder.part()
        .modelFile(model)
        .rotationY((((int) entry.getKey().toYRot()) + 180) % 360)
        .uvLock(true)
        .addModel()
        .condition(entry.getValue(), height);
  }

  public void paneBlock(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge) {
    paneBlockInternal(block, key(block).toString(), pane, edge);
  }

  public void paneBlock(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge) {
    paneBlockInternal(block, name + "_pane", pane, edge);
  }

  public void paneBlockWithRenderType(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, String renderType) {
    paneBlockInternalWithRenderType(block, key(block).toString(), pane, edge, ResourceLocation.tryParse(renderType));
  }

  public void paneBlockWithRenderType(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, String renderType) {
    paneBlockInternalWithRenderType(block, name + "_pane", pane, edge, ResourceLocation.tryParse(renderType));
  }

  public void paneBlockWithRenderType(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
    paneBlockInternalWithRenderType(block, key(block).toString(), pane, edge, renderType);
  }

  public void paneBlockWithRenderType(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
    paneBlockInternalWithRenderType(block, name + "_pane", pane, edge, renderType);
  }

  public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, String renderType, String renderTypeFast) {
    paneBlockInternalWithRenderTypeAndFast(block, key(block).toString(), pane, edge, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, String renderType, String renderTypeFast) {
    paneBlockInternalWithRenderTypeAndFast(block, name + "_pane", pane, edge, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    paneBlockInternalWithRenderTypeAndFast(block, key(block).toString(), pane, edge, renderType, renderTypeFast);
  }

  public void paneBlockWithRenderTypeAndFast(IronBarsBlock block, String name, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    paneBlockInternalWithRenderTypeAndFast(block, name + "_pane", pane, edge, renderType, renderTypeFast);
  }

  private void paneBlockInternal(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge) {
    ModelFile post = models().panePost(baseName + "_post", pane, edge);
    ModelFile side = models().paneSide(baseName + "_side", pane, edge);
    ModelFile sideAlt = models().paneSideAlt(baseName + "_side_alt", pane, edge);
    ModelFile noSide = models().paneNoSide(baseName + "_noside", pane);
    ModelFile noSideAlt = models().paneNoSideAlt(baseName + "_noside_alt", pane);
    paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
  }

  private void paneBlockInternalWithRenderType(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType) {
    ModelFile post = models().panePost(baseName + "_post", pane, edge).renderType(renderType);
    ModelFile side = models().paneSide(baseName + "_side", pane, edge).renderType(renderType);
    ModelFile sideAlt = models().paneSideAlt(baseName + "_side_alt", pane, edge).renderType(renderType);
    ModelFile noSide = models().paneNoSide(baseName + "_noside", pane).renderType(renderType);
    ModelFile noSideAlt = models().paneNoSideAlt(baseName + "_noside_alt", pane).renderType(renderType);
    paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
  }

  private void paneBlockInternalWithRenderTypeAndFast(IronBarsBlock block, String baseName, ResourceLocation pane, ResourceLocation edge, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    ModelFile post = models().panePost(baseName + "_post", pane, edge).renderType(renderType, renderTypeFast);
    ModelFile side = models().paneSide(baseName + "_side", pane, edge).renderType(renderType, renderTypeFast);
    ModelFile sideAlt = models().paneSideAlt(baseName + "_side_alt", pane, edge).renderType(renderType, renderTypeFast);
    ModelFile noSide = models().paneNoSide(baseName + "_noside", pane).renderType(renderType, renderTypeFast);
    ModelFile noSideAlt = models().paneNoSideAlt(baseName + "_noside_alt", pane).renderType(renderType, renderTypeFast);
    paneBlock(block, post, side, sideAlt, noSide, noSideAlt);
  }

  public void paneBlock(IronBarsBlock block, ModelFile post, ModelFile side, ModelFile sideAlt, ModelFile noSide, ModelFile noSideAlt) {
    MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
        .part().modelFile(post).addModel().end();
    PipeBlock.PROPERTY_BY_DIRECTION.entrySet().forEach(e -> {
      Direction dir = e.getKey();
      if (dir.getAxis().isHorizontal()) {
        boolean alt = dir == Direction.SOUTH;
        builder.part().modelFile(alt || dir == Direction.WEST ? sideAlt : side).rotationY(dir.getAxis() == Direction.Axis.X ? 90 : 0).addModel()
            .condition(e.getValue(), true).end()
            .part().modelFile(alt || dir == Direction.EAST ? noSideAlt : noSide).rotationY(dir == Direction.WEST ? 270 : dir == Direction.SOUTH ? 90 : 0).addModel()
            .condition(e.getValue(), false);
      }
    });
  }

  public void doorBlock(DoorBlock block, ResourceLocation bottom, ResourceLocation top) {
    doorBlockInternal(block, key(block).toString(), bottom, top);
  }

  public void doorBlock(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top) {
    doorBlockInternal(block, name + "_door", bottom, top);
  }

  public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType) {
    doorBlockInternalWithRenderType(block, key(block).toString(), bottom, top, ResourceLocation.tryParse(renderType));
  }

  public void doorBlockWithRenderType(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, String renderType) {
    doorBlockInternalWithRenderType(block, name + "_door", bottom, top, ResourceLocation.tryParse(renderType));
  }

  public void doorBlockWithRenderType(DoorBlock block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    doorBlockInternalWithRenderType(block, key(block).toString(), bottom, top, renderType);
  }

  public void doorBlockWithRenderType(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    doorBlockInternalWithRenderType(block, name + "_door", bottom, top, renderType);
  }

  public void doorBlockWithRenderTypeAndFast(DoorBlock block, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
    doorBlockInternalWithRenderTypeAndFast(block, key(block).toString(), bottom, top, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void doorBlockWithRenderTypeAndFast(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, String renderType, String renderTypeFast) {
    doorBlockInternalWithRenderTypeAndFast(block, name + "_door", bottom, top, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void doorBlockWithRenderTypeAndFast(DoorBlock block, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    doorBlockInternalWithRenderTypeAndFast(block, key(block).toString(), bottom, top, renderType, renderTypeFast);
  }

  public void doorBlockWithRenderTypeAndFast(DoorBlock block, String name, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    doorBlockInternalWithRenderTypeAndFast(block, name + "_door", bottom, top, renderType, renderTypeFast);
  }

  private void doorBlockInternal(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top) {
    ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom_left", bottom, top);
    ModelFile bottomLeftOpen = models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top);
    ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_right", bottom, top);
    ModelFile bottomRightOpen = models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top);
    ModelFile topLeft = models().doorTopLeft(baseName + "_top_left", bottom, top);
    ModelFile topLeftOpen = models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top);
    ModelFile topRight = models().doorTopRight(baseName + "_top_right", bottom, top);
    ModelFile topRightOpen = models().doorTopRightOpen(baseName + "_top_right_open", bottom, top);
    doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
  }

  private void doorBlockInternalWithRenderType(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType) {
    ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom_left", bottom, top).renderType(renderType);
    ModelFile bottomLeftOpen = models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top).renderType(renderType);
    ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_right", bottom, top).renderType(renderType);
    ModelFile bottomRightOpen = models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top).renderType(renderType);
    ModelFile topLeft = models().doorTopLeft(baseName + "_top_left", bottom, top).renderType(renderType);
    ModelFile topLeftOpen = models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top).renderType(renderType);
    ModelFile topRight = models().doorTopRight(baseName + "_top_right", bottom, top).renderType(renderType);
    ModelFile topRightOpen = models().doorTopRightOpen(baseName + "_top_right_open", bottom, top).renderType(renderType);
    doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
  }

  private void doorBlockInternalWithRenderTypeAndFast(DoorBlock block, String baseName, ResourceLocation bottom, ResourceLocation top, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    ModelFile bottomLeft = models().doorBottomLeft(baseName + "_bottom_left", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile bottomLeftOpen = models().doorBottomLeftOpen(baseName + "_bottom_left_open", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile bottomRight = models().doorBottomRight(baseName + "_bottom_right", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile bottomRightOpen = models().doorBottomRightOpen(baseName + "_bottom_right_open", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile topLeft = models().doorTopLeft(baseName + "_top_left", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile topLeftOpen = models().doorTopLeftOpen(baseName + "_top_left_open", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile topRight = models().doorTopRight(baseName + "_top_right", bottom, top).renderType(renderType, renderTypeFast);
    ModelFile topRightOpen = models().doorTopRightOpen(baseName + "_top_right_open", bottom, top).renderType(renderType, renderTypeFast);
    doorBlock(block, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
  }

  public void doorBlock(DoorBlock block, ModelFile bottomLeft, ModelFile bottomLeftOpen, ModelFile bottomRight, ModelFile bottomRightOpen, ModelFile topLeft, ModelFile topLeftOpen, ModelFile topRight, ModelFile topRightOpen) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      int yRot = ((int) state.getValue(DoorBlock.FACING).toYRot()) + 90;
      boolean right = state.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT;
      boolean open = state.getValue(DoorBlock.OPEN);
      boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
      if (open) {
        yRot += 90;
      }
      if (right && open) {
        yRot += 180;
      }
      yRot %= 360;

      ModelFile model = null;
      if (lower && right && open) {
        model = bottomRightOpen;
      } else if (lower && !right && open) {
        model = bottomLeftOpen;
      }
      if (lower && right && !open) {
        model = bottomRight;
      } else if (lower && !right && !open) {
        model = bottomLeft;
      }
      if (!lower && right && open) {
        model = topRightOpen;
      } else if (!lower && !right && open) {
        model = topLeftOpen;
      }
      if (!lower && right && !open) {
        model = topRight;
      } else if (!lower && !right && !open) {
        model = topLeft;
      }

      return MyConfiguredModel.builder().modelFile(model)
          .rotationY(yRot)
          .build();
    }, DoorBlock.POWERED);
  }

  public void trapdoorBlock(TrapDoorBlock block, ResourceLocation texture, boolean orientable) {
    trapdoorBlockInternal(block, key(block).toString(), texture, orientable);
  }

  public void trapdoorBlock(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable) {
    trapdoorBlockInternal(block, name + "_trapdoor", texture, orientable);
  }

  public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType) {
    trapdoorBlockInternalWithRenderType(block, key(block).toString(), texture, orientable, ResourceLocation.tryParse(renderType));
  }

  public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, String renderType) {
    trapdoorBlockInternalWithRenderType(block, name + "_trapdoor", texture, orientable, ResourceLocation.tryParse(renderType));
  }

  public void trapdoorBlockWithRenderType(TrapDoorBlock block, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
    trapdoorBlockInternalWithRenderType(block, key(block).toString(), texture, orientable, renderType);
  }

  public void trapdoorBlockWithRenderType(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
    trapdoorBlockInternalWithRenderType(block, name + "_trapdoor", texture, orientable, renderType);
  }

  public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, ResourceLocation texture, boolean orientable, String renderType, String renderTypeFast) {
    trapdoorBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, orientable, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, String renderType, String renderTypeFast) {
    trapdoorBlockInternalWithRenderTypeAndFast(block, name + "_trapdoor", texture, orientable, ResourceLocation.tryParse(renderType), ResourceLocation.tryParse(renderTypeFast));
  }

  public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    trapdoorBlockInternalWithRenderTypeAndFast(block, key(block).toString(), texture, orientable, renderType, renderTypeFast);
  }

  public void trapdoorBlockWithRenderTypeAndFast(TrapDoorBlock block, String name, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    trapdoorBlockInternalWithRenderTypeAndFast(block, name + "_trapdoor", texture, orientable, renderType, renderTypeFast);
  }

  private void trapdoorBlockInternal(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable) {
    ModelFile bottom = orientable ? models().trapdoorOrientableBottom(baseName + "_bottom", texture) : models().trapdoorBottom(baseName + "_bottom", texture);
    ModelFile top = orientable ? models().trapdoorOrientableTop(baseName + "_top", texture) : models().trapdoorTop(baseName + "_top", texture);
    ModelFile open = orientable ? models().trapdoorOrientableOpen(baseName + "_open", texture) : models().trapdoorOpen(baseName + "_open", texture);
    trapdoorBlock(block, bottom, top, open, orientable);
  }

  private void trapdoorBlockInternalWithRenderType(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable, ResourceLocation renderType) {
    ModelFile bottom = orientable ? models().trapdoorOrientableBottom(baseName + "_bottom", texture).renderType(renderType) : models().trapdoorBottom(baseName + "_bottom", texture).renderType(renderType);
    ModelFile top = orientable ? models().trapdoorOrientableTop(baseName + "_top", texture).renderType(renderType) : models().trapdoorTop(baseName + "_top", texture).renderType(renderType);
    ModelFile open = orientable ? models().trapdoorOrientableOpen(baseName + "_open", texture).renderType(renderType) : models().trapdoorOpen(baseName + "_open", texture).renderType(renderType);
    trapdoorBlock(block, bottom, top, open, orientable);
  }

  private void trapdoorBlockInternalWithRenderTypeAndFast(TrapDoorBlock block, String baseName, ResourceLocation texture, boolean orientable, ResourceLocation renderType, ResourceLocation renderTypeFast) {
    ModelFile bottom = orientable ? models().trapdoorOrientableBottom(baseName + "_bottom", texture).renderType(renderType, renderTypeFast) : models().trapdoorBottom(baseName + "_bottom", texture).renderType(renderType, renderTypeFast);
    ModelFile top = orientable ? models().trapdoorOrientableTop(baseName + "_top", texture).renderType(renderType, renderTypeFast) : models().trapdoorTop(baseName + "_top", texture).renderType(renderType, renderTypeFast);
    ModelFile open = orientable ? models().trapdoorOrientableOpen(baseName + "_open", texture).renderType(renderType, renderTypeFast) : models().trapdoorOpen(baseName + "_open", texture).renderType(renderType, renderTypeFast);
    trapdoorBlock(block, bottom, top, open, orientable);
  }

  public void trapdoorBlock(TrapDoorBlock block, ModelFile bottom, ModelFile top, ModelFile open, boolean orientable) {
    getVariantBuilder(block).forAllStatesExcept(state -> {
      int xRot = 0;
      int yRot = ((int) state.getValue(TrapDoorBlock.FACING).toYRot()) + 180;
      boolean isOpen = state.getValue(TrapDoorBlock.OPEN);
      if (orientable && isOpen && state.getValue(TrapDoorBlock.HALF) == Half.TOP) {
        xRot += 180;
        yRot += 180;
      }
      if (!orientable && !isOpen) {
        yRot = 0;
      }
      yRot %= 360;
      return MyConfiguredModel.builder().modelFile(isOpen ? open : state.getValue(TrapDoorBlock.HALF) == Half.TOP ? top : bottom)
          .rotationX(xRot)
          .rotationY(yRot)
          .build();
    }, TrapDoorBlock.POWERED, TrapDoorBlock.WATERLOGGED);
  }

  private CompletableFuture<?> saveBlockState(CachedOutput cache, JsonObject stateJson, Block owner) {
    ResourceLocation blockName = Preconditions.checkNotNull(key(owner));
    Path outputPath = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
        .resolve(blockName.getNamespace()).resolve("blockstates").resolve(blockName.getPath() + ".json");
    return DataProvider.saveStable(cache, stateJson, outputPath);
  }

  @NotNull
  @Override
  public String getName() {
    return "Block States: " + modid;
  }

  public static class ConfiguredModelList {
    private final List<MyConfiguredModel> models;

    private ConfiguredModelList(List<MyConfiguredModel> models) {
      Preconditions.checkArgument(!models.isEmpty());
      this.models = models;
    }

    public ConfiguredModelList(MyConfiguredModel model) {
      this(ImmutableList.of(model));
    }

    public ConfiguredModelList(MyConfiguredModel... models) {
      this(Arrays.asList(models));
    }

    public JsonElement toJSON() {
      if (models.size()==1) {
        return models.get(0).toJSON(false);
      } else {
        JsonArray ret = new JsonArray();
        for (MyConfiguredModel m:models) {
          ret.add(m.toJSON(true));
        }
        return ret;
      }
    }

    public MyBlockStateProvider.ConfiguredModelList append(MyConfiguredModel... models) {
      return new MyBlockStateProvider.ConfiguredModelList(ImmutableList.<MyConfiguredModel>builder().addAll(this.models).add(models).build());
    }
  }
}