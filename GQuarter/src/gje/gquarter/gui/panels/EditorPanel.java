package gje.gquarter.gui.panels;

import gje.gquarter.boundings.BoundingSphere;
import gje.gquarter.boundings.BoundingsRenderer;
import gje.gquarter.components.BasicComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.EntityXTestBuilder;
import gje.gquarter.gui.GuiButton;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.GuiSlider;
import gje.gquarter.gui.On3DTerrainPick;
import gje.gquarter.gui.SliderFunction;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.models.TerrainTexturePack;
import gje.gquarter.terrain.Region;
import gje.gquarter.toolbox.BlendmapPainter;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.MousePicker;
import gje.gquarter.toolbox.Rect2i;
import gje.gquarter.toolbox.Rotation3f;
import gje.gquarter.water.WaterTile;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class EditorPanel extends GuiPanel implements OnKeyEventListener, On3DTerrainPick {
	private static final int NONE_ID = -1;
	private static final int ERASER_ID = 0;
	private static final int OAK_TREE_ENTITY_ID = 1;
	private static final int STRAWS_ENTITY_ID = 2;
	private static final int SPRUCE_TREE_ENTITY_ID = 3;
	private static final int MUSHROOM_ENTITY_ID = 4;
	private static final int SND_BIRD_ENTITY_ID = 5;
	private static final int BLEND_MAP_BRUSH_ID = 6;
	private static final int MOVER_ID = 7;
	private static final int REEDS_ENTITY_ID = 8;

	private static final int SPRUCE_TRUNK_ENTITY_ID = 9;
	private static final int LILY_ENTITY_ID = 10;
	private static final int SEAWEED_ENTITY_ID = 11;
	private static final int MARBLE_STONE_ENTITY_ID = 12;

	private static final int RED_SELECTED = 1;
	private static final int GREEN_SELECTED = 2;
	private static final int BLUE_SELECTED = 4;
	private static final int ALPHA_SELECTED = 8;

	private BlendmapPainter blendmapPainter;
	private RegionalComponent regComp;
	private int placerType;
	private int colorSelected;
	private EntityX placerEntity;
	private EntityX highlightedEntity;
	private Random random;
	private Key clearPlacer;
	private float minSoundRadius, maxSoundRadius;
	private GuiSlider minSndSlider, maxSndSlider;
	private GuiSlider brushRadius, brushHardnes, brushOpacity;
	private boolean ready;
	private boolean rmbLatch;
	private Vector3f draggingSphereStart;
	private Vector3f draggingSphereStop;
	private float draggingRadius;
	private BoundingSphere draggingSphere;
	private boolean draggingTrigger;

	public EditorPanel(RegionalComponent regComp, String idName, int panelX, int panelY, int panelW, int panelH, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), visibility, true, frame, GuiPanel.TYPE_RETANGULAR);
		MousePicker.add3DTerrainPicker(this);
		this.regComp = regComp;
		this.blendmapPainter = new BlendmapPainter(10, 0, 0, 0, 0);
		int iconSize = parentFrame.getIconSize();
		placerType = NONE_ID;
		placerEntity = null;
		highlightedEntity = null;
		random = new Random(4234234234l);
		clearPlacer = new Key(Keyboard.KEY_BACK);
		clearPlacer.setOnClickListener(this);
		colorSelected = ALPHA_SELECTED;

		draggingSphereStart = new Vector3f();
		draggingSphereStop = new Vector3f();
		draggingRadius = 0f;
		draggingTrigger = false;
		draggingSphere = new BoundingSphere(new PhysicalComponent(draggingSphereStart, new Rotation3f(), 1f), new Vector3f(), 1f);
		BoundingsRenderer.remove(draggingSphere);

		minSoundRadius = 2f;
		maxSoundRadius = 8f;

		int dy = 0 * (GuiFrame.SPACING + iconSize);
		int pointer = 0;

		GuiButton clearerButton = new GuiButton("clearBrushes", "gui/icons/closeIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(clearerButton);
		GuiButton eraserButton = new GuiButton("greenEraser", "gui/icons/editor/greenEraser", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(eraserButton);
		GuiButton moverButton = new GuiButton("moverButton", "gui/icons/editor/moverPicker", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(moverButton);
		GuiButton grasButton = new GuiButton("grassPlacer", "gui/icons/editor/iconGrass", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(grasButton);
		GuiButton reedsButton = new GuiButton("reedsPlacer", "gui/icons/editor/reedIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		reedsButton.setActive(true);
		addGuiButton(reedsButton);
		GuiButton mushrromsButton = new GuiButton("mushroomPlacer", "gui/icons/editor/mushroomPlacer", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(mushrromsButton);
		GuiButton oakTreeButton = new GuiButton("oakTreePlacer", "gui/icons/editor/bushIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(oakTreeButton);
		GuiButton spruceTreeButton = new GuiButton("spruceTreePlacer", "gui/icons/editor/coniferousTreeIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(spruceTreeButton);

		dy = 1 * (GuiFrame.SPACING + iconSize);
		pointer = 0;
		GuiButton oakBigTreeButton = new GuiButton("oakBigTreePlacer", "gui/icons/editor/treeIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		oakBigTreeButton.setActive(false);
		addGuiButton(oakBigTreeButton);
		GuiButton spruceTrunkButton = new GuiButton("spruceTrunk", "gui/icons/editor/trunkIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(spruceTrunkButton);
		GuiButton seaweedButton = new GuiButton("seaweed", "gui/icons/editor/seaweedIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(seaweedButton);
		GuiButton lilyButton = new GuiButton("lily", "gui/icons/editor/lilyIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(lilyButton);
		GuiButton marblestoneButton = new GuiButton("marbleStone", "gui/icons/editor/rockIcon", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(marblestoneButton);

		dy = 2 * (GuiFrame.SPACING + iconSize);
		pointer = 0;
		GuiButton sndWaterButton = new GuiButton("soundwater", "gui/icons/editor/soundwater", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		sndWaterButton.setActive(false);
		addGuiButton(sndWaterButton);
		GuiButton sndLeafButton = new GuiButton("soundleaf", "gui/icons/editor/soundleaves", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		sndLeafButton.setActive(false);
		addGuiButton(sndLeafButton);
		GuiButton sndWindButton = new GuiButton("soundwind", "gui/icons/editor/soundwind", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		sndWindButton.setActive(false);
		addGuiButton(sndWindButton);
		GuiButton sndBirdButton = new GuiButton("soundbird", "gui/icons/editor/soundbird", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(sndBirdButton);
		GuiButton sndFireButton = new GuiButton("soundfire", "gui/icons/editor/soundfire", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		sndFireButton.setActive(false);
		addGuiButton(sndFireButton);

		dy = 4 * (GuiFrame.SPACING + iconSize);
		pointer = 0;
		GuiButton terrainBrush = new GuiButton("blendbrush", "gui/icons/editor/brush", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(terrainBrush);
		GuiButton redButton = new GuiButton("redbutton", "gui/icons/editor/brush", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(redButton);
		GuiButton greenButton = new GuiButton("greenbutton", "gui/icons/editor/brush", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(greenButton);
		GuiButton blueButton = new GuiButton("bluebutton", "gui/icons/editor/brush", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(blueButton);
		GuiButton alphaButton = new GuiButton("alphabutton", "gui/icons/editor/brush", getGridRect(iconSize, GuiFrame.SPACING, w, h, dy, pointer++, this), this);
		addGuiButton(alphaButton);

		setupSliders(iconSize);

		ready = false;
		rmbLatch = false;
		setVisible(visibility);
	}

	private void setupSliders(int iconSize) {
		SliderFunction fMinSphere = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (float) Math.sqrt((value - 1f) / 89f);
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * norm * 89f + 1f;
			}
		};
		int dy = GuiFrame.SPACING + 3 * (GuiFrame.SPACING + iconSize) + 0 * (GuiFrame.SPACING + GuiSlider.HEIGHT);
		minSndSlider = new GuiSlider("Min Radius", new Rect2i(GuiFrame.OFFSET, dy, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fMinSphere);
		minSndSlider.setValue(2);
		addGuiProgressBar(minSndSlider);

		SliderFunction fMaxSphere = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (float) Math.sqrt((value - 2f) / 98f);
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * norm * 98f + 2f;
			}
		};
		dy = GuiFrame.SPACING + 3 * (GuiFrame.SPACING + iconSize) + 1 * (GuiFrame.SPACING + GuiSlider.HEIGHT);
		maxSndSlider = new GuiSlider("Max Radius", new Rect2i(GuiFrame.OFFSET, dy, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fMaxSphere);
		maxSndSlider.setValue(6);
		addGuiProgressBar(maxSndSlider);

		SliderFunction fBrushRadius = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (float) Math.sqrt((value - 1f) / 200f);
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * norm * 200f + 1f;
			}
		};
		dy = GuiFrame.SPACING + 5 * (GuiFrame.SPACING + iconSize) + 0 * (GuiFrame.SPACING + GuiSlider.HEIGHT);
		brushRadius = new GuiSlider("Brush radius", new Rect2i(GuiFrame.OFFSET, dy, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fBrushRadius);
		addGuiProgressBar(brushRadius);
		brushRadius.setValue(4f);

		SliderFunction fBrushHardness = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (float) Math.sqrt((value) / 10f);
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * norm * 10f;
			}
		};

		dy = GuiFrame.SPACING + 5 * (GuiFrame.SPACING + iconSize) + 1 * (GuiFrame.SPACING + GuiSlider.HEIGHT);
		brushHardnes = new GuiSlider("Brush hardness", new Rect2i(GuiFrame.OFFSET, dy, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fBrushHardness);
		brushHardnes.setValue(1);
		addGuiProgressBar(brushHardnes);

		SliderFunction fOpacity = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return value / 255f;
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * 255f;
			}
		};
		dy = GuiFrame.SPACING + 5 * (GuiFrame.SPACING + iconSize) + 2 * (GuiFrame.SPACING + GuiSlider.HEIGHT);
		brushOpacity = new GuiSlider("Brush opacity", new Rect2i(GuiFrame.OFFSET, dy, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fOpacity);
		brushOpacity.setValue(255);
		addGuiProgressBar(brushOpacity);
	}

	@Override
	public void updateGeneral() {
		super.updateGeneral();
		if (!ready) {
			TerrainTexturePack tp = parentFrame.getPlayer().getRegionalComponentIfHaving().getRegion().getTarrain().getTexturePack();
			getButtonById("redbutton").getTextureIcon().useTexture(tp.getrTexture().getTextureId());
			getButtonById("greenbutton").getTextureIcon().useTexture(tp.getgTexture().getTextureId());
			getButtonById("bluebutton").getTextureIcon().useTexture(tp.getbTexture().getTextureId());
			getButtonById("alphabutton").getTextureIcon().useTexture(tp.getBgTexture().getTextureId());
			ready = true;
		}
		if (rmbLatch) {
			if (!Mouse.isButtonDown(GuiFrame.MOUSE_RMB)) {
				rmbLatch = false;
				clearPlacer();
			}
		} else {
			if (Mouse.isButtonDown(GuiFrame.MOUSE_RMB))
				rmbLatch = true;
		}
	}

	@Override
	public boolean onClick(String idName) {
		Camera cam = MainRenderer.getSelectedCamera();
		if (idName == "oakTreePlacer") {
			clearPlacer();
			placerType = OAK_TREE_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildOakTree(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "grassPlacer") {
			clearPlacer();
			placerType = STRAWS_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildStraws(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "reedsPlacer") {
			clearPlacer();
			placerType = REEDS_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildReeds(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "moverButton") {
			clearPlacer();
			placerType = MOVER_ID;

			return true;
		}
		if (idName == "spruceTreePlacer") {
			clearPlacer();
			placerType = SPRUCE_TREE_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildSpruceTree(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}

		if (idName == "spruceTrunk") {
			clearPlacer();
			placerType = SPRUCE_TRUNK_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildSpruceTrunk(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "seaweed") {
			clearPlacer();
			placerType = SEAWEED_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildSeaweed(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "lily") {
			clearPlacer();
			placerType = LILY_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildWaterLily(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "marbleStone") {
			clearPlacer();
			placerType = MARBLE_STONE_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildMarbleStone(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}

		if (idName == "mushroomPlacer") {
			clearPlacer();
			placerType = MUSHROOM_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildMushroomSpot(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f, 0f, EntityX.TYPE_LIVING);
			cam.getRegional().getRegion().addEntity(placerEntity);
			placerEntity.getModelComponentIfHaving().setSelected(true);
			return true;
		}
		if (idName == "soundbird") {
			clearPlacer();
			parentFrame.getSettingsPanel().setBoundingVisibility(true);
			placerType = SND_BIRD_ENTITY_ID;
			placerEntity = EntityXTestBuilder.buildBirdSound(parentFrame.getWorld(), new Vector3f(cam.getObserved()), 1f);
			placerEntity.getSoundComponentIfHaving().setMinRange(minSoundRadius);
			placerEntity.getSoundComponentIfHaving().setMaxRange(maxSoundRadius);
			cam.getRegional().getRegion().addEntity(placerEntity);
			return true;
		}
		if (idName == "redbutton") {
			clearPlacer();
			placerType = BLEND_MAP_BRUSH_ID;
			colorSelected = RED_SELECTED;
			onRelease(brushOpacity.getIdName());
			return true;
		}
		if (idName == "greenbutton") {
			clearPlacer();
			placerType = BLEND_MAP_BRUSH_ID;
			colorSelected = GREEN_SELECTED;
			onRelease(brushOpacity.getIdName());
			return true;
		}
		if (idName == "bluebutton") {
			clearPlacer();
			placerType = BLEND_MAP_BRUSH_ID;
			colorSelected = BLUE_SELECTED;
			onRelease(brushOpacity.getIdName());
			return true;
		}
		if (idName == "alphabutton") {
			clearPlacer();
			placerType = BLEND_MAP_BRUSH_ID;
			colorSelected = ALPHA_SELECTED;
			onRelease(brushOpacity.getIdName());
			return true;
		}
		if (idName == "blendbrush") {
			clearPlacer();
			placerType = BLEND_MAP_BRUSH_ID;
			return true;
		}

		if (idName == "clearBrushes") {
			clearPlacer();
			return true;
		}
		if (idName == "greenEraser") {
			clearPlacer();
			placerType = ERASER_ID;
			return true;
		}
		return false;
	}

	private void clearPlacer() {
		placerType = NONE_ID;
		Region region = MainRenderer.getSelectedCamera().getRegional().getRegion();
		region.removeEntity(placerEntity);
		placerEntity = null;
	}

	// TODO CZY NA PEWNOWE DZIALA CULLING NA ENV??

	@Override
	public boolean onPress(String idName) {
		if ((placerEntity != null) && placerEntity.hasComponent(BasicComponent.CMP_SOUND)) {
			if (idName == minSndSlider.getIdName()) {
				minSoundRadius = minSndSlider.getValue();
				placerEntity.getSoundComponentIfHaving().setMinRange(minSoundRadius);
				if (maxSoundRadius < minSoundRadius + 1) {
					maxSoundRadius = minSoundRadius + 1;
					maxSndSlider.setValue(maxSoundRadius);
					placerEntity.getSoundComponentIfHaving().setMaxRange(maxSoundRadius);
				}
				return true;
			}
			if (idName == maxSndSlider.getIdName()) {
				maxSoundRadius = maxSndSlider.getValue();
				placerEntity.getSoundComponentIfHaving().setMaxRange(maxSoundRadius);
				if (maxSoundRadius < minSoundRadius + 1) {
					minSoundRadius = maxSoundRadius - 1;
					minSndSlider.setValue(minSoundRadius);
					placerEntity.getSoundComponentIfHaving().setMinRange(minSoundRadius);
				}
				return true;
			}
		}
		return false;
	}

	public void saveBM() {
		int tId = regComp.getRegion().getTarrain().getBlendMap().getTextureId();
		BlendmapPainter.updateBlendmapBuffers(tId);
		BlendmapPainter.savePreupdatedBlendampBufferToFile(tId, "res/world/blendMaps/bm1");
	}

	@Override
	public boolean onRelease(String idName) {
		if (idName == brushRadius.getIdName()) {
			blendmapPainter.buildSquareBrush((int) brushRadius.getValue());
			return true;
		}
		if (idName == brushHardnes.getIdName()) {
			blendmapPainter.setHardnes(brushHardnes.getValue());
			return true;
		}
		if (idName == brushOpacity.getIdName()) {
			int opacity = (int) brushOpacity.getValue();
			if (colorSelected == RED_SELECTED)
				blendmapPainter.setMainColor(opacity, 0, 0, 0);
			else if (colorSelected == GREEN_SELECTED)
				blendmapPainter.setMainColor(0, opacity, 0, 0);
			else if (colorSelected == BLUE_SELECTED)
				blendmapPainter.setMainColor(0, 0, opacity, 0);
			else if (colorSelected == ALPHA_SELECTED)
				blendmapPainter.setMainColor(0, 0, 0, opacity);
			return true;
		}

		return false;
	}

	public boolean isBrushActive() {
		return (placerType != NONE_ID);
	}

	@Override
	public boolean onKeyClick(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		if (keyId == clearPlacer.getKeyId()) {
			saveBM();
			if (isBrushActive())
				clearPlacer();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		return false;
	}

	@Override
	public boolean on3DClick(float x, float y, float z, int buttonId) {
		if (isVisible()) {
			if (buttonId == GuiFrame.MOUSE_LMB) {
				draggingSphereStart.set(x, y, z);
			}
		}
		return false;
	}

	@Override
	public boolean on3DPress(float x, float y, float z, int buttonId) {
		if (isVisible()) {
			if (buttonId == GuiFrame.MOUSE_LMB) {
				if (placerType == BLEND_MAP_BRUSH_ID) {
					int bmSize = regComp.getRegion().getTarrain().getBlendMap().getSizePx();
					int size = regComp.getRegion().getTarrain().getVertexCount();
					int tId = regComp.getRegion().getTarrain().getBlendMap().getTextureId();
					blendmapPainter.applyBlendMapPreBuiltBrush(tId, (int) (bmSize * x / size), (int) (bmSize * z / size));
					return true;
				} else {
					draggingSphereStop.set(x, y, z);
					Vector3f.sub(draggingSphereStop, draggingSphereStart, draggingSphereStop);
					draggingRadius = draggingSphereStop.length();
					if (draggingRadius > 2f) {
						draggingSphere.setRadius(draggingRadius);
						if (!draggingTrigger) {
							// tu sfera ma byc juz widoczna i w razie puszczenia
							// dodac obiekty
							BoundingsRenderer.load(draggingSphere);
							parentFrame.getSettingsPanel().setBoundingVisibility(true);
							draggingTrigger = true;
						}
					} else if (draggingTrigger) {
						BoundingsRenderer.remove(draggingSphere);
						draggingTrigger = false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean on3DHover(float x, float y, float z) {
		if (isVisible()) {
			Region region = MainRenderer.getSelectedCamera().getRegional().getRegion();

			if (placerEntity != null) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
					WaterTile intersectingWaterTile = region.getIntersectingWaterTile(x, z);
					if (intersectingWaterTile != null) {
						float waterY = Maths.max(intersectingWaterTile.getHeight(), y);
						placerEntity.getPhysicalComponentIfHaving().getPosition().set(x, waterY, z);
					} else
						placerEntity.getPhysicalComponentIfHaving().getPosition().set(x, y, z);
				} else
					placerEntity.getPhysicalComponentIfHaving().getPosition().set(x, y, z);

				// TODO MIGA JKABY CALY CZS SIE PRZLEACZALO ... LEPIEJ TEZ NIE
				// ZMIENIAC TYPU OBIEKTU TYLKO JAKOS JEGO MESH..
				if (placerType == ERASER_ID || placerType == MOVER_ID) {
					if (highlightedEntity == null) {
						EntityX closest = region.getClosestEnvEntity(x, z, 0.4f);
						if (closest != null) {
							highlightedEntity = closest;
							region.removeEntity(highlightedEntity);
							highlightedEntity.setEntityType(EntityX.TYPE_LIVING);
							highlightedEntity.setSelect(true);
							region.addEntity(highlightedEntity);
							return true;
						}
					} else if (highlightedEntity != null) {
						EntityX closest = region.getClosestEnvEntity(x, z, 0.4f);
						if (closest == null) {
							region.removeEntity(highlightedEntity);
							highlightedEntity.setEntityType(EntityX.TYPE_ENVIRONMENTAL);
							highlightedEntity.setSelect(true);
							region.addEntity(highlightedEntity);
							highlightedEntity = null;
							return true;
						}
					}
				}
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean on3DRelease(float x, float y, float z, int buttonId) {
		if (isVisible()) {
			if (buttonId == GuiFrame.MOUSE_LMB) {

				boolean added = false;
				Region region = MainRenderer.getSelectedCamera().getRegional().getRegion();
				int amount = 1;
				float xx = x;
				float yy = y;
				float zz = z;

				if (draggingTrigger) {
					draggingTrigger = false;
					BoundingsRenderer.remove(draggingSphere);

					if (placerType == ERASER_ID) {
						region.removeRangeOfEntities(draggingSphereStart.x, draggingSphereStart.z, draggingSphere.getRadius());
						return true;
					} else {
						float density = 2f;
						amount = (int) (draggingRadius * draggingRadius * density);
						if (amount < 1)
							amount = 1;
					}
				}

				for (int ii = 0; ii < amount; ++ii) {
					if (amount > 1) {
						// losujemy pozycje
						float angle = random.nextFloat() * Maths.PI2;
						float radius = random.nextFloat() * draggingRadius;
						xx = draggingSphereStart.x + Maths.sin(angle) * radius;
						zz = draggingSphereStart.z + Maths.cos(angle) * radius;
						yy = region.getTarrain().getHeightOfTerrainGlobal(xx, zz);
					}

					if (placerType == OAK_TREE_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildOakTree(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.8f + 0.85f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == SPRUCE_TREE_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildSpruceTree(parentFrame.getWorld(), new Vector3f(xx, yy, z), random.nextFloat() * 0.3f + 1.2f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == STRAWS_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildStraws(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.2f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == REEDS_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildReeds(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.2f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == MUSHROOM_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildMushroomSpot(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.3f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}

					if (placerType == MARBLE_STONE_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildMarbleStone(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.3f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == SEAWEED_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildSeaweed(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.3f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == LILY_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildWaterLily(parentFrame.getWorld(), new Vector3f(placerEntity.getPhysicalComponentIfHaving().getPosition()), random.nextFloat() * 0.2f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
					if (placerType == SPRUCE_TRUNK_ENTITY_ID) {
						EntityX ent = EntityXTestBuilder.buildSpruceTrunk(parentFrame.getWorld(), new Vector3f(xx, yy, zz), random.nextFloat() * 0.3f + 1.0f, Maths.PI2 * random.nextFloat(), EntityX.TYPE_ENVIRONMENTAL);
						region.addEntity(ent);
						added = true;
					}
				}
				if (added)
					return true;
				/*
				 * obiekty ktorych nie da sie rozlozyc obszarowo
				 */
				if (placerType == SND_BIRD_ENTITY_ID) {
					EntityX ent = EntityXTestBuilder.buildBirdSound(parentFrame.getWorld(), new Vector3f(x, y, z), 1f);
					ent.getSoundComponentIfHaving().setMinRange(minSoundRadius);
					ent.getSoundComponentIfHaving().setMaxRange(maxSoundRadius);
					region.addEntity(ent);
					return true;
				}
			}
		}
		return false;
	}

}
