package edu.cornell.gdiac.physics.editor;

//import java.io.BufferedWriter;
//import java.io.OutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
//import com.badlogic.gdx.assets.AssetManager;
//import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.Platform;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
//import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.Scene;

public class LevelEditor extends WorldController {
	private AssetFile af;

	public static final int PLATFORM_IND = 0;
	public static final int WOOD_BOX_IND = 1;
	public static final int STONE_BOX_IND = 2;
	public static final int FUEL_BOX_IND = 3;
	public static final int GOAL_DOOR_IND = 4;
	public static final int LADDER_COMPLEX_IND = 5;

	private BlockAbstract goalDoor;

	/** Sets asset file */
	public void setAssetFile(AssetFile a) {
		this.af = a;
	}

	ArrayList<CharacterModel> npcs;
	AidenModel aiden;
	ArrayList<BlockAbstract> blocks;

	private boolean holding = false;
	private CharacterModel holdingCharacter = null;
	private BlockAbstract holdingBlock = null;
	// private float inputCoolDown = 0;
	// private final static float INPUT_COOL_DOWN = 0.5f;

	private Rectangle platformRect;
	private boolean isAddingRect;

	public LevelEditor() {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		npcs = new ArrayList<CharacterModel>();
		aiden = null;
		blocks = new ArrayList<BlockAbstract>();
		platformRect = new Rectangle(-1, -1, 0, 0);
		// inputCoolDown = 0;
		holding = false;
		holdingCharacter = null;
		holdingBlock = null;
		isAddingRect = false;

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		npcs.clear();
		aiden = null;
		blocks.clear();
		platformRect = new Rectangle(-1, -1, 0, 0);
		// inputCoolDown = 0;
		holding = false;
		holdingCharacter = null;
		holdingBlock = null;
		isAddingRect = false;
	}

	@Override
	public void update(float dt) {

		System.out.println(this.npcs.size() + " " + this.blocks.size()
				+ " " + this.goalDoor);

		// TODO Auto-generated method stub
		canvas.setEditor(true);
		float nxPos = InputController.getInstance().mousePos.x
				+ canvas.getCamera().position.x
				- (float) af.backGround.getRegionWidth() / 2f;
		float nyPos = -InputController.getInstance().mousePos.y
				+ canvas.getCamera().position.y
				+ (float) af.backGround.getRegionHeight() / 2f;
		nxPos /= scale.x;
		nyPos /= scale.y;
		float deltaX = nxPos - xPos;
		float deltaY = nyPos - yPos;
		xPos = nxPos;
		yPos = nyPos;
		if (InputController.getInstance().toExport()) {
			exportToJson();
			return;
		}
		if (InputController.getInstance().toLoad()) {
			loadFromJson();
			return;
		}
		if (InputController.getInstance().switchPolyMode()) {
			// this.inputCoolDown=INPUT_COOL_DOWN;
			isAddingRect = !isAddingRect;
			if (!isAddingRect) {
				this.platformRect = new Rectangle(-1, -1, 0, 0);
			} else {
				holding = false;
				this.holdingBlock = null;
				this.holdingCharacter = null;
			}
			// this.inputCoolDown=INPUT_COOL_DOWN;
		}
		if (InputController.getInstance().newLeftClick()) {
			System.out.println("Clicked");
		}
		if (isAddingRect && InputController.getInstance().newLeftClick()) {
			;
			// System.out.println("SET UP RECTANGLE "+ platformRect.toString());
			if (this.platformRect.x >= 0 && this.platformRect.y >= 0) {
				System.out.println("Setting 2nd point");
				float occupy = Math.round(1f / this.gridUnit);
				float width = fitToGrid((xPos - platformRect.x) / occupy)
						* occupy;
				float height = fitToGrid((yPos - platformRect.y) / occupy)
						* occupy;
				this.platformRect.setWidth(width);
				this.platformRect.setHeight(height);
				isAddingRect = false;
				Rectangle adjust = new Rectangle(
						Math.min(platformRect.x,
								platformRect.x + platformRect.width),
						Math.min(platformRect.y,
								platformRect.y + platformRect.height),
						Math.abs(platformRect.width),
						Math.abs(platformRect.height));
				if (adjust.width > 0 && adjust.height > 0) {
					Platform block = new Platform(adjust, 1);
					block.setTexture(af.earthTile);
					block.setDrawScale(scale);
					blocks.add(block);
				}
				this.platformRect = new Rectangle(-1, -1, 0, 0);
			} else {
				System.out.println("Setting 1nd point");
				this.platformRect.setX(fitToGrid(xPos));
				this.platformRect.setY(fitToGrid(yPos));
			}
			// System.out.println("Finish RECTANGLE "+ platformRect.toString());
			return;
		}

		// System.out.println(this.blocks.size()+" "+this.npcs.size());
		boolean wasHolding = holding;
		// if (inputCoolDown>0) inputCoolDown-=dt;
		// if (this.inputCoolDown<=0){
		if (InputController.getInstance().newLeftClick()) {
			holding = !holding;
			// this.inputCoolDown=INPUT_COOL_DOWN;
		}
		// }
		// newly holding an object
		if (holding && !wasHolding) {
			if (aiden != null && aiden.getX() - aiden.getWidth() / 2 < xPos
					&& aiden.getX() + aiden.getWidth() / 2 > xPos
					&& aiden.getY() - aiden.getHeight() / 2 < yPos
					&& aiden.getY() + aiden.getHeight() / 2 > yPos) {
				holdingCharacter = aiden;
			} else {
				for (CharacterModel npc : npcs) {
					if (npc.getX() - npc.getWidth() / 2 < xPos
							&& npc.getX() + npc.getWidth() / 2 > xPos
							&& npc.getY() - npc.getHeight() / 2 < yPos
							&& npc.getY() + npc.getHeight() / 2 > yPos) {
						holdingCharacter = npc;
						break;
					}
				}
			}
			for (BlockAbstract block : this.blocks) {
				if (block.getX() - block.getWidth() / 2 < xPos
						&& block.getX() + block.getWidth() / 2 > xPos
						&& block.getY() - block.getHeight() / 2 < yPos
						&& block.getY() + block.getHeight() / 2 > yPos) {
					holdingBlock = block;
					break;
				}
			}
		}
		// newly releasing an object
		if (!holding && wasHolding) {
			if (this.holdingBlock != null) {
				Vector2 trans = fitInGrid(new Vector2(holdingBlock.getX()
						- holdingBlock.getWidth() / 2f,
						holdingBlock.getY()
								- holdingBlock.getHeight() / 2f));
				this.holdingBlock.setPosition(holdingBlock.getPosition().cpy()
						.add(trans));
				this.holdingBlock = null;
			}
			if (holdingCharacter != null) {
				Vector2 trans = fitInGrid(new Vector2(holdingCharacter.getX()
						- holdingCharacter.getWidth() / 2f,
						holdingCharacter.getY()
								- holdingCharacter.getHeight() / 2f));
				this.holdingCharacter
						.setPosition(holdingCharacter.getPosition().cpy()
								.add(trans));
				this.holdingCharacter = null;
			}
		}
		// Hold object around
		if (holding) {
			if (InputController.getInstance().toRemove()) {
				if (holdingCharacter != null) {
					if (holdingCharacter != aiden)
						npcs.remove(holdingCharacter);
					else
						aiden = null;
					holdingCharacter = null;
				}
				if (holdingBlock != null) {
					blocks.remove(holdingBlock);
					if (holdingBlock == this.goalDoor)
						goalDoor = null;
					holdingBlock = null;
				}
				holding = false;
			} else {
				if (holdingCharacter != null) {
					holdingCharacter.setPosition(holdingCharacter.getPosition()
							.add(new Vector2(deltaX, deltaY)));
				} else if (holdingBlock != null) {
					holdingBlock.setPosition(holdingBlock.getPosition()
							.add(new Vector2(deltaX, deltaY)));
				} else {
					holding = false;
				}
			}
		}
		// Under unholding mode, we can add new objects
		else {
			if (InputController.getInstance().newAiden()) {
				aiden = new AidenModel(xPos, yPos, 2.5f, 3f, true);
				Vector2 trans = fitInGrid(new Vector2(aiden.getX()
						- aiden.getWidth() / 2f,
						aiden.getY()
								- aiden.getHeight() / 2f));
				aiden.setPosition(aiden.getPosition().add(trans));
				aiden.setTexture(af.avatarTexture);
				aiden.setDrawScale(scale);
			} else if (InputController.getInstance().newCharacter()) {
				CharacterModel ch = new CharacterModel(
						CharacterType.WATER_GUARD, "WaterGuard",
						xPos, yPos, 2.5f, 3f, true);
				Vector2 trans = fitInGrid(new Vector2(ch.getX()
						- ch.getWidth() / 2f,
						ch.getY()
								- ch.getHeight() / 2f));
				ch.setPosition(ch.getPosition().add(trans));
				ch.setTexture(af.waterTexture);
				ch.setDrawScale(scale);
				npcs.add(ch);
			} else if (InputController.getInstance().newBlock()) {
				BlockAbstract block = null;
				Vector2 trans = new Vector2();
				switch (InputController.getInstance().inputNumber) {
				case PLATFORM_IND:
					block = new Platform(new Rectangle(xPos, yPos, 1, 1), 1);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.earthTile);
					block.setDrawScale(scale);
					break;
				case WOOD_BOX_IND:
					block = new FlammableBlock(xPos, yPos, 2, 2, 1, 4);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.woodTexture);
					block.setDrawScale(scale);
					break;
				case STONE_BOX_IND:
					block = new StoneBlock(xPos, yPos, 2, 2);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.stoneTexture);
					block.setDrawScale(scale);
					break;
				case FUEL_BOX_IND:
					block = new FuelBlock(xPos, yPos, 2, 2, 1, 2, 25, false);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.fuelTexture);
					block.setDrawScale(scale);
					break;
				case GOAL_DOOR_IND:
					block = new StoneBlock(xPos, yPos, 2, 2);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.goalTile);
					block.setDrawScale(scale);
					goalDoor = block;
					break;
				default:
					break;
				}
				if (block != null)
					blocks.add(block);
			}
		}
	}

	private int gridWidth = 32;
	private int gridHeight = 18;
	private float gridUnit = 0.5f;

	private float xPos;
	private float yPos;

	@Override
	public void draw(float delta) {
		canvas.clear();

		// canvas.begin(512,288);
		// canvas.begin();
		canvas.begin(xPos, yPos);
		// canvas.draw(backGround, 0, 0);
		canvas.draw(af.backGround, new Color(1f, 1f, 1f, 1f), 0f, 0f,
				canvas.getWidth(), canvas.getHeight());
		canvas.end();

		canvas.beginDebug(1, 1);

		int numX = (int) (this.gridWidth / this.gridUnit);
		int numY = (int) (this.gridHeight / this.gridUnit);
		for (int i = 0; i < numY; i += 2) {
			float[] pts = new float[] { 0, 0, gridWidth, 0, gridWidth, gridUnit,
					0, gridUnit };
			PolygonShape poly = new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.YELLOW, 0, this.gridUnit * i,
					0, scale.x, scale.y);
		}
		for (int j = 0; j < numX; j += 2) {
			float[] pts = new float[] { 0, 0, gridUnit, 0, gridUnit, gridHeight,
					0, gridHeight };
			PolygonShape poly = new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.YELLOW, this.gridUnit * j, 0,
					0, scale.x, scale.y);
		}
		canvas.endDebug();
		canvas.begin(xPos, yPos);
		for (BlockAbstract obj : blocks) {
			obj.draw(canvas);
		}
		for (CharacterModel obj : npcs) {
			obj.draw(canvas);
		}
		if (aiden != null) {
			aiden.simpleDraw(canvas);
		}
		canvas.end();
		canvas.beginDebug(1, 1);
		float occupy = Math.round(1f / this.gridUnit);
		float width = fitToGrid((xPos - platformRect.x) / occupy) * occupy;
		float height = fitToGrid((yPos - platformRect.y) / occupy) * occupy;
		if (this.isAddingRect && platformRect.x >= 0 && platformRect.y >= 0
				&& (Math.abs(width) > 0 && (Math.abs(height) > 0))) {
			Rectangle adjust = new Rectangle(
					fitToGrid(Math.min(platformRect.x,
							platformRect.x + width)),
					fitToGrid(Math.min(platformRect.y,
							platformRect.y + height)),
					Math.abs(width),
					Math.abs(height));
			float[] pts = new float[] { 0, 0,
					adjust.width, 0,
					adjust.width, adjust.height,
					0, adjust.height };
			PolygonShape poly = new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.RED,
					adjust.x, adjust.y,
					0, scale.x, scale.y);
		}
		if (holding) {
			if (this.holdingBlock != null)
				holdingBlock.drawDebug(canvas, Color.GREEN);
			if (this.holdingCharacter != null)
				holdingCharacter.drawDebug(canvas, Color.GREEN);
		}
		canvas.endDebug();
	}

	public Vector2 fitInGrid(Vector2 pos) {
		Vector2 dest = new Vector2();
		dest.x = (Math.round(pos.x / this.gridUnit)) * this.gridUnit;
		dest.y = (Math.round(pos.y / this.gridUnit)) * this.gridUnit;
		return dest.sub(pos);
	}

	public float fitToGrid(float value) {
		return (Math.round(value / this.gridUnit)) * this.gridUnit;
	}

	public void exportToJson() {
		Json json = new Json();
		json.setTypeName(null);
		json.setUsePrototypes(false);
		json.setIgnoreUnknownFields(true);
		json.setOutputType(OutputType.json);

		ProjectModelJsonRep project = new ProjectModelJsonRep(aiden, blocks,
				npcs, goalDoor);
		String project_str = json.prettyPrint(project);

		String outputfile = "Level2.json";
		FileHandle file = Gdx.files
				.absolute(Gdx.files.getLocalStoragePath() + outputfile);

		file.writeString(project_str, false);
	}

	public void loadFromJson() {
		System.out.println("Loading");

		Scene scene = new Scene("Level2.json");

		reset();
		System.out.println("Loading Characters");
		for (CharacterModel ch : scene.getGuards()) {
			npcs.add(ch);
			System.out.println("Character " + ch.getPosition());
			ch.setTexture(af.waterTexture);
			ch.setDrawScale(scale);
		}
		System.out.println("Loading blocks");
		for (BlockAbstract block : scene.getBlocks()) {
			blocks.add(block);
			block.setDrawScale(scale);
			System.out.println("block " + block.getPosition() + " "
					+ block.getWidth() + " " + block.getHeight());
			TextureRegion texture = null;
			switch (block.type) {
			case FLAMMABLEBLOCK:
				texture = (af.woodTexture);
				break;
			case FUEL:
				texture = (af.fuelTexture);
				break;
			case PLATFORM:
				texture = (af.earthTile);
				break;
			case STONE:
				texture = (af.stoneTexture);
				break;
			case ROPECOMPLEX:
				break;
			default:
				break;
			}

			if (texture != null)
				block.setTexture(texture);
		}
		goalDoor = scene.getGoalDoor();
		if (goalDoor != null) {
			goalDoor.setTexture(af.goalTile);
			goalDoor.setDrawScale(scale);
			blocks.add(goalDoor);
		}
		aiden = scene.getAidenModel();
		if (aiden != null) {

			aiden.setDrawScale(scale);
			aiden.setTexture(af.avatarTexture);
		}

		System.out.println(this.blocks.size() + " " + this.npcs.size());

	}
}
