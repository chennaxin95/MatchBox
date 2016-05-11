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
import com.badlogic.gdx.physics.box2d.World;

import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.BurnablePlatform;
import edu.cornell.gdiac.physics.blocks.BurnablePlatform.FlamePlatform;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.Platform;
import edu.cornell.gdiac.physics.blocks.Rope;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.blocks.TrapDoor;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.editor.EditorPanel.EditorMode;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
//import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.Scene;

public class LevelEditor extends WorldController {
	private AssetFile af;

	public static final int PLATFORM_IND = 0;
	public static final int WOOD_BOX_IND = 1;
	public static final int STONE_BOX_IND = 2;
	public static final int FUEL_BOX_IND = 3;
	public static final int BURNABLE_PLATFORM_IND = 4;
	public static final int GOAL_DOOR_IND = 5;
	public static final int WATER_IND = 6;
	public static final int AIDEN_IND = 7;
	public static final int ROPE_IND = 8;
	public static final int TRAP_LEFT_IND = 9;
	public static final int TRAP_RIGHT_IND = 10;
	public static final int CHECK_POINT_IND=11;

	private BlockAbstract goalDoor;

	private EditorPanel panel;

	/** Sets asset file */
	public void setAssetFile(AssetFile a) {
		this.af = a;
	}

	ArrayList<CharacterModel> npcs;
	AidenModel aiden;
	ArrayList<Obstacle> blocks;
	ArrayList<Rope> complexs;
	ArrayList<TrapDoor> traps;

	private boolean holding = false;
	private CharacterModel holdingCharacter = null;
	private Obstacle holdingBlock = null;
	private Rope holdingRope = null;
	private TrapDoor holdingTrap=null;
	// private float inputCoolDown = 0;
	// private final static float INPUT_COOL_DOWN = 0.5f;

	private Rectangle platformRect;
	// private boolean isAddingRect;

	public LevelEditor() {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		npcs = new ArrayList<CharacterModel>();
		aiden = null;
		blocks = new ArrayList<Obstacle>();

		complexs = new ArrayList<Rope>();
		traps = new ArrayList<TrapDoor>();

		platformRect = new Rectangle(-1, -1, 0, 0);
		// inputCoolDown = 0;
		holding = false;
		holdingCharacter = null;
		holdingBlock = null;
		holdingRope=null;
		holdingTrap=null;
//		isAddingRect = false;
		
		TextureRegion[] textures={};
		
		if (af!=null){
			panel=new EditorPanel(100, textures, af);
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		npcs.clear();
		aiden = null;
		blocks.clear();
		complexs.clear();
		traps.clear();
		platformRect = new Rectangle(-1, -1, 0, 0);
		// inputCoolDown = 0;
		holding = false;
		holdingCharacter = null;
		holdingBlock = null;
		holdingRope = null;
		if (panel != null) {
			panel.boardWidth = 44;
			panel.boardHeight = 24;
		}
		// isAddingRect = false;
	}

	@Override
	public void update(float dt) {
//		System.out.println(this.blocks.size()+" "+this.npcs.size()+" "
//				+complexs.size()+" "+this.traps.size());
		if (af!=null && panel==null){
			TextureRegion[] textures={af.earthTile, af.woodTexture,
				af.stoneTexture, af.fireBall[0], 
				af.burnablePlatform, af.goalTile,
				af.waterTexture, af.avatarTexture, 
				af.ropeLongTexture, 
				af.trapdoorTexture,
				af.trapdoorTexture,
				af.fuelTexture};
			
			panel=new EditorPanel(320, textures, af);
			panel.setBackground(af.backGround);
			panel.setButton(af.earthTile);
		}
		boolean oldMode = panel.polyMode;

		gridWidth = panel.boardWidth;
		gridHeight = panel.boardHeight;

		// TODO Auto-generated method stub
		canvas.setEditor(true);

//		Vector2 nPos = InputController.getInstance().getCrossHair();
		Vector2 nPos = canvas.relativeVector(InputController.getInstance().getCrossHair().x,
				canvas.getHeight()-InputController.getInstance().getCrossHair().y);
//		nPos.y = canvas.getHeight()-nPos.y;
		float nxPos = nPos.x / scale.x;
		float nyPos = nPos.y / scale.y;
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
		boolean reactToPanel = false;
		if (InputController.getInstance().newLeftClick()) {
//			panel.update(nPos.x, nPos.y);
			panel.update(InputController.getInstance().getCrossHair().x,
					canvas.getHeight()-InputController.getInstance().getCrossHair().y);
//			panel.update(InputController.getInstance().mousePos.x,
//					canvas.getHeight()
//							- InputController.getInstance().mousePos.y);

			if (InputController.getInstance().getCrossHair().x <= panel.width) {
				reactToPanel = true;
			}
			if (!panel.polyMode) {
				this.platformRect = new Rectangle(-1, -1, 0, 0);
			} else if (!oldMode) {
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
				if (holdingRope != null) {
					complexs.remove(holdingRope);
					holdingRope = null;
				}
				if (holdingTrap!=null){
					traps.remove(holdingTrap);
					holdingTrap = null;
				}
				holding = false;
				return;
			}
		}

		if (panel != null && panel.polyMode
				&& InputController.getInstance().newLeftClick()) {
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
				// isAddingRect = false;
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
		boolean wasHolding = holding;
		if (InputController.getInstance().newLeftClick()) {
			holding = !holding;
		}
		// newly holding an object
		if (holding && !wasHolding && !reactToPanel) {
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
			for (Obstacle blawk : this.blocks) {
				BlockAbstract block = (blawk instanceof BurnablePlatform)
						? ((BurnablePlatform) blawk).getPlatform()
						: (BlockAbstract) blawk;
				if (block.getX() - block.getWidth() / 2f < xPos
						&& block.getX() + block.getWidth() / 2f > xPos
						&& block.getY() - block.getHeight() / 2f < yPos
						&& block.getY() + block.getHeight() / 2f > yPos) {
					holdingBlock = blawk;
					break;
				}
			}
			for (Rope rope : this.complexs) {
				if (rope.getX() < xPos
						&& rope.getX() + rope.getWidth() > xPos
						&& rope.getY() > yPos
						&& rope.getY() - rope.getHeight() < yPos) {
					holdingRope = rope;
					break;
				}
			}
			for (TrapDoor trap : this.traps) {
				if (trap.getX() - trap.getWidth()/2f < xPos
						&& trap.getX() + trap.getWidth()/2f > xPos
						&& trap.getY() + trap.getHeight()/2f> yPos
						&& trap.getY() - trap.getHeight()/2f < yPos){
					holdingTrap=trap;
					break;
				}
			}
			
		}
		// newly releasing an object
		else if (!holding && wasHolding) {
			if (panel != null && panel.mode == EditorMode.GAMEOBJECT) {
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
				if (holdingRope != null) {
					complexs.remove(holdingRope);
					holdingRope = null;
				}
				if (holdingTrap!=null){
					traps.remove(holdingTrap);
					holdingTrap = null;
				}
				holding = false;
			} else {
				if (this.holdingBlock != null) {
					if (holdingBlock instanceof BurnablePlatform){
						BurnablePlatform hb =((BurnablePlatform) holdingBlock);
						Vector2 trans = fitInGrid(new Vector2(hb.getX()
								- hb.getWidth() / 2f,
								hb.getY() - hb.getHeight() / 2f));
						hb.translate(trans);
					}						
					else{						
						BlockAbstract hb=(BlockAbstract) holdingBlock;
						Vector2 trans = fitInGrid(new Vector2(hb.getX()
								- hb.getWidth() / 2f,
								hb.getY() - hb.getHeight() / 2f));
						this.holdingBlock
							.setPosition(holdingBlock.getPosition().cpy().add(trans));
					}
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
				if (holdingRope!=null){
					Vector2 trans = fitInGrid(new Vector2(holdingRope.getX(),
							holdingRope.getY()));
					this.holdingRope.setPosition(holdingRope.getPosition().cpy()
							.add(trans));
					this.holdingRope = null;
				}
				if (holdingTrap!=null){
					Vector2 trans = fitInGrid(new Vector2(holdingTrap.getX(),
							holdingTrap.getY()));
	//				this.holdingTrap.setPosition(holdingTrap.getPosition().cpy()
	//						.add(trans));
					holdingTrap.translate(trans);
					this.holdingTrap = null;
				}
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
				if (holdingRope != null) {
					complexs.remove(holdingRope);
					holdingRope = null;
				}
				if (holdingTrap!=null){
					traps.remove(holdingTrap);
					holdingTrap = null;
				}
				holding = false;
			} else {
				if (holdingCharacter != null) {
					holdingCharacter.setPosition(holdingCharacter.getPosition()
							.add(new Vector2(deltaX, deltaY)));
				}else if (this.holdingBlock != null) {
					if (holdingBlock instanceof BurnablePlatform){
						BurnablePlatform hb =((BurnablePlatform) holdingBlock);
						hb.translate(new Vector2(deltaX, deltaY));
					}						
					else{						
						BlockAbstract hb=(BlockAbstract) holdingBlock;
						holdingBlock.setPosition(holdingBlock.getPosition()
									.add(new Vector2(deltaX, deltaY)));
					}
				}else if (holdingRope != null) {
					holdingRope.setPosition(holdingRope.getPosition()
						.add(new Vector2(deltaX, deltaY)));

				} else if (holdingTrap!=null){ 
//					holdingTrap.setPosition(holdingTrap.getPosition()
//							.add(new Vector2(deltaX, deltaY)));
					holdingTrap.translate(new Vector2(deltaX, deltaY));
				}
				else {
					holding = false;
				}
			}
		}
		// Under unholding mode, we can add new objects
		else {
			if (panel != null && panel.mode == EditorMode.GAMEOBJECT) {
				holding = true;
				BlockAbstract block = null;
				TrapDoor trap = null;
				Vector2 trans = new Vector2();
				switch (panel.selectedTexture) {
				case PLATFORM_IND:
					block = new Platform(new Rectangle(xPos, yPos, 1, 1), 1);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.earthTile);
					block.setDrawScale(scale);
					this.blocks.add(block);
					holdingBlock = block;
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
					this.blocks.add(block);
					holdingBlock = block;
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
					this.blocks.add(block);
					holdingBlock = block;
					break;
				case FUEL_BOX_IND:
					block = new FuelBlock(xPos, yPos, 1, 1, 1, 2, 25, false);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.fireBall[0]);
					block.setDrawScale(scale);
					this.blocks.add(block);
					holdingBlock = block;
					break;
				case GOAL_DOOR_IND:
					block = new StoneBlock(xPos, yPos, 3.5f, 3);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.goalTile);
					block.setDrawScale(scale);
					goalDoor = block;
					this.blocks.add(block);
					holdingBlock = block;
					break;
				case WATER_IND:
					CharacterModel ch = new CharacterModel(
							CharacterType.WATER_GUARD, "WaterGuard",
							xPos, yPos, 2.5f, 3f, true);
					trans = fitInGrid(new Vector2(ch.getX()
							- ch.getWidth() / 2f,
							ch.getY()
									- ch.getHeight() / 2f));
					ch.setPosition(ch.getPosition().add(trans));
					ch.setTexture(af.waterTexture);
					ch.setDrawScale(scale);
					npcs.add(ch);
					holdingCharacter = ch;
					break;
				case AIDEN_IND:
					aiden = new AidenModel(xPos, yPos, 2.5f, 3f, true);
					trans = fitInGrid(new Vector2(aiden.getX()
							- aiden.getWidth() / 2f,
							aiden.getY()
									- aiden.getHeight() / 2f));
					aiden.setPosition(aiden.getPosition().add(trans));
					aiden.setTexture(af.avatarTexture);
					aiden.setDrawScale(scale);
					holdingCharacter = aiden;
					break;

				case BURNABLE_PLATFORM_IND:
					BurnablePlatform bp = new BurnablePlatform(
							new Rectangle(xPos, yPos, 1, 1), 1, world);
					block = bp.getPlatform();
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					bp.setPosition(block.getPosition().add(trans));
					bp.setTexture(af.burnablePlatform);
					bp.setDrawScale(scale);
					
					this.blocks.add(bp);
					holdingBlock = bp;
					break;

				case ROPE_IND:
					Rope rope = new Rope(xPos, yPos, 0.25f, 0.25f);
					trans = fitInGrid(new Vector2(rope.getX(),
							rope.getY()));
					rope.setPosition(rope.getPosition().add(trans));
					rope.setDrawScale(scale);
					this.complexs.add(rope);
					rope.setTexture(af.ropeTexture, af.nailTexture);
					holdingRope = rope;
					break;
				case TRAP_LEFT_IND:
					trap = new TrapDoor(xPos, yPos, 4, 0.25f, false);
					trans = fitInGrid(new Vector2(trap.getX()
							- trap.getWidth() / 2f,
							trap.getY()
									- trap.getHeight() / 2f));
					trap.translate(trans);
					trap.setChildrenTexture(af.trapdoorTexture, af.longRope, af.nailTexture);

					trap.setDrawScale(scale);
					this.traps.add(trap);
					holdingTrap=trap;
					break;
				case TRAP_RIGHT_IND:
					trap = new TrapDoor(xPos, yPos, 4, 0.25f, true);
					trans = fitInGrid(new Vector2(trap.getX()
							- trap.getWidth() / 2f,
							trap.getY()
									- trap.getHeight() / 2f));
					trap.translate(trans);
					trap.setChildrenTexture(af.trapdoorTexture, af.longRope, af.nailTexture);
					trap.setDrawScale(scale);
					this.traps.add(trap);
					holdingTrap=trap;
					break;	
				case CHECK_POINT_IND:
					block = new FuelBlock(xPos, yPos, 1, 1, 1, 2, 25, true);
					trans = fitInGrid(new Vector2(block.getX()
							- block.getWidth() / 2f,
							block.getY()
									- block.getHeight() / 2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(af.fuelTexture);
					block.setDrawScale(scale);
					this.blocks.add(block);
					holdingBlock = block;
					break;
				default: break;
				}
			}
			// if (InputController.getInstance().newAiden()) {
			// aiden = new AidenModel(xPos, yPos, 2.5f, 3f, true);
			// Vector2 trans = fitInGrid(new Vector2(aiden.getX()
			// - aiden.getWidth() / 2f,
			// aiden.getY()
			// - aiden.getHeight() / 2f));
			// aiden.setPosition(aiden.getPosition().add(trans));
			// aiden.setTexture(af.avatarTexture);
			// aiden.setDrawScale(scale);
			// } else if (InputController.getInstance().newCharacter()) {
			// CharacterModel ch = new CharacterModel(
			// CharacterType.WATER_GUARD, "WaterGuard",
			// xPos, yPos, 2.5f, 3f, true);
			// Vector2 trans = fitInGrid(new Vector2(ch.getX()
			// - ch.getWidth() / 2f,
			// ch.getY()
			// - ch.getHeight() / 2f));
			// ch.setPosition(ch.getPosition().add(trans));
			// ch.setTexture(af.waterTexture);
			// ch.setDrawScale(scale);
			// npcs.add(ch);
			// } else if (InputController.getInstance().newBlock()) {
			// BlockAbstract block = null;
			// Vector2 trans = new Vector2();
			// switch (InputController.getInstance().inputNumber) {
			// case PLATFORM_IND:
			// block = new Platform(new Rectangle(xPos, yPos, 1, 1), 1);
			// trans = fitInGrid(new Vector2(block.getX()
			// - block.getWidth() / 2f,
			// block.getY()
			// - block.getHeight() / 2f));
			// block.setPosition(block.getPosition().add(trans));
			// block.setTexture(af.earthTile);
			// block.setDrawScale(scale);
			// break;
			// case WOOD_BOX_IND:
			// block = new FlammableBlock(xPos, yPos, 2, 2, 1, 4);
			// trans = fitInGrid(new Vector2(block.getX()
			// - block.getWidth() / 2f,
			// block.getY()
			// - block.getHeight() / 2f));
			// block.setPosition(block.getPosition().add(trans));
			// block.setTexture(af.woodTexture);
			// block.setDrawScale(scale);
			// break;
			// case STONE_BOX_IND:
			// block = new StoneBlock(xPos, yPos, 2, 2);
			// trans = fitInGrid(new Vector2(block.getX()
			// - block.getWidth() / 2f,
			// block.getY()
			// - block.getHeight() / 2f));
			// block.setPosition(block.getPosition().add(trans));
			// block.setTexture(af.stoneTexture);
			// block.setDrawScale(scale);
			// break;
			// case FUEL_BOX_IND:
			// block = new FuelBlock(xPos, yPos, 2, 2, 1, 2, 25, false);
			// trans = fitInGrid(new Vector2(block.getX()
			// - block.getWidth() / 2f,
			// block.getY()
			// - block.getHeight() / 2f));
			// block.setPosition(block.getPosition().add(trans));
			// block.setTexture(af.fuelTexture);
			// block.setDrawScale(scale);
			// break;
			// case GOAL_DOOR_IND:
			// block = new StoneBlock(xPos, yPos, 2, 2);
			// trans = fitInGrid(new Vector2(block.getX()
			// - block.getWidth() / 2f,
			// block.getY()
			// - block.getHeight() / 2f));
			// block.setPosition(block.getPosition().add(trans));
			// block.setTexture(af.goalTile);
			// block.setDrawScale(scale);
			// goalDoor = block;
			// break;
			// default:
			// break;
			// }
			// if (block != null)
			// blocks.add(block);
			// }
		}
	}

	private int gridWidth = 44;
	private int gridHeight = 24;
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
		canvas.draw(af.backGround, new Color(1f, 1f, 1f, 1f),
				0f, 0f,
				this.gridWidth * scale.x, this.gridHeight * scale.y
		/* canvas.getWidth(), canvas.getHeight() */);
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
		for (Obstacle obj : blocks) {
			obj.draw(canvas);
		}
		for (CharacterModel obj : npcs) {
			obj.draw(canvas);
		}
		for (Rope obj : complexs) {
			canvas.draw(af.ropeLongTexture, Color.WHITE, 0, 0,
					obj.getX() * scale.x,
					(obj.getY() - obj.getHeight()) * scale.y, 0, 1, 1);
		}
		for (TrapDoor td : traps) {
			td.draw(canvas);
		}
		if (aiden != null) {
			aiden.simpleDraw(canvas);
		}
		canvas.end();
		canvas.beginDebug(1, 1);
		float occupy = Math.round(1f / this.gridUnit);
		float width = fitToGrid((xPos - platformRect.x) / occupy) * occupy;
		float height = fitToGrid((yPos - platformRect.y) / occupy) * occupy;
		if (panel != null && panel.polyMode
				&& platformRect.x >= 0 && platformRect.y >= 0
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

			if (this.holdingRope!=null){
				float[] pts = new float[] { 0, 0,
						holdingRope.getWidth(), 0,
						holdingRope.getWidth(), holdingRope.getHeight(),
						0, holdingRope.getHeight() };
				PolygonShape poly = new PolygonShape();
				poly.set(pts);
				canvas.drawPhysics(poly,
						Color.GREEN,
						holdingRope.getX(),
						(holdingRope.getY() - holdingRope.getHeight()), 0,
						scale.x, scale.y);
			}
			if (this.holdingTrap!=null){
				float[] pts = new float[] { 0, 0,
						holdingTrap.getWidth(), 0,
						holdingTrap.getWidth(), holdingTrap.getHeight(),
						0, holdingTrap.getHeight()};
				PolygonShape poly = new PolygonShape();
				poly.set(pts);
				canvas.drawPhysics(poly,
						Color.GREEN,
						holdingTrap.getX() - holdingTrap.getWidth()/2f, 
						(holdingTrap.getY() - holdingTrap.getHeight()/2f), 0, 
						scale.x, scale.y);
			}
		}
		canvas.endDebug();
		canvas.begin(xPos, yPos);
		panel.draw(canvas);
		canvas.end();
		canvas.beginDebug(1, 1);
		panel.drawDebug(canvas);
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
				complexs, traps, npcs, goalDoor,
				gridWidth, gridHeight);
		String project_str = json.prettyPrint(project);


		String outputfile = "Hard2b.json";



		FileHandle file = Gdx.files
				.absolute(Gdx.files.getLocalStoragePath() + outputfile);

		file.writeString(project_str, false);
	}

	public void loadFromJson() {
		System.out.println("Loading");


		Scene scene = new Scene("Hard2.json");

		reset();
		System.out.println("Loading Characters");
		for (CharacterModel ch : scene.getGuards()) {
			npcs.add(ch);
			System.out.println("Character " + ch.getPosition());
			ch.setTexture(af.waterTexture);
			ch.setDrawScale(scale);
		}
		System.out.println("Loading blocks");
		for (Rope rope : scene.getRopes()) {
			rope.setTexture(af.ropeTexture, af.nailTexture);
			Vector2 trans = fitInGrid(new Vector2(rope.getX(),
					rope.getY()));
			rope.setPosition(rope.getPosition().add(trans));
			rope.setDrawScale(scale);
			this.complexs.add(rope);
		}
		System.out.println("Loading blocks");
		for (TrapDoor trap:scene.getTrapDoors()){
			trap.setChildrenTexture(af.trapdoorTexture, af.ropeTexture, af.nailTexture);
			Vector2 trans = fitInGrid(new Vector2(trap.getX(),
					trap.getY()));
//			trap.setPosition(trap.getPosition().add(trans));
			trap.translate(trans);
			trap.setDrawScale(scale);
			this.traps.add(trap);
		}
		
		for (Obstacle block : scene.getBlocks()) {
			blocks.add(block);
			block.setDrawScale(scale);
			// System.out.println("block " + block.getPosition() + " "
			// + block.getWidth() + " " + block.getHeight());
			TextureRegion texture = null;
			if (block instanceof BurnablePlatform) {
				block = ((BurnablePlatform) block).getPlatform();
			}
			BlockAbstract blawk = ((BlockAbstract) block);

			switch (blawk.type) {
			case FLAMMABLEBLOCK:
				texture = (af.woodTexture);
				break;
			case FUEL:
				texture = (af.fireBall[0]);
				if (((FuelBlock)blawk).isCheckpoint()){
					texture = (af.fuelTexture);
				}
				break;
			case PLATFORM:
				texture = (af.earthTile);
				break;
			case STONE:
				texture = (af.stoneTexture);
				break;
			case BURNABLE_PLATFORM:
				texture = (af.burnablePlatform);
				((FlamePlatform) blawk).getBP().setTexture(texture);
				break;
			case TRAPDOOR:
				texture = (af.trapdoorTexture);
				break;
			default:
				break;
			}
			if (texture != null) {
				blawk.setTexture(texture);
			}
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
		panel.boardWidth = (int) scene.getWidth();
		panel.boardHeight = (int) scene.getHeight();

		System.out.println(this.blocks.size() + " " + this.npcs.size());

	}
}
