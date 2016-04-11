package edu.cornell.gdiac.physics.editor;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.Scene;


public class LevelEditor extends WorldController {
	/** The texture file for the character avatar (no animation) */
	private static final String DUDE_FILE = "platform/aiden.png";
	/** texture for water */
	private static final String WATER_FILE = "platform/water.png";
	/** The texture file for the spinning barrier */
	private static final String BARRIER_FILE = "platform/barrier.png";
	/** The texture file for the bullet */
	private static final String BULLET_FILE = "platform/bullet.png";
	/** The texture file for the bridge plank */
	private static final String ROPE_FILE = "platform/rope.png";
	/** The textrue file for the woodenBlock */
	private static final String WOOD_FILE = "platform/woodenBlock.png";
	/** Texture for fuelBlock */
	private static final String FUEL_FILE = "platform/fuelBlock.png";

	// private static final String LADDER_FILE = "platform/ladder.png";

	private static final String AIDEN_ANIME_FILE = "platform/aidenAnime.png";
	private static final String AIDEN_DIE_FILE = "platform/die_animation.png";
	private static final String WATER_WALK = "platform/water_animation.png";

	private static final String BURNING_FILE = "platform/blockburning.png";

	private static final String STONE_FILE = "platform/stone.png";
	/** File to texture for walls and platforms */
	private static String EARTH_FILE = "shared/earthtile.png";

	/** The sound file for a jump */
	private static final String JUMP_FILE = "platform/jump.mp3";
	/** The sound file for a bullet fire */
	private static final String PEW_FILE = "platform/pew.mp3";
	/** The sound file for a bullet collision */
	private static final String POP_FILE = "platform/plop.mp3";

	/** Texture asset for character avatar */
	private TextureRegion avatarTexture;
	/** Texture for woodblock */
	private TextureRegion woodTexture;
	/** Texture for fuel */
	private TextureRegion fuelTexture;
	/** texture for water */
	private TextureRegion waterTexture;
	private TextureRegion stoneTexture;
	private TextureRegion ropeTexture;
	/** Texture for background */
	private static final String BACKGROUND = "shared/background.png";
	/** Texture for background */
	private TextureRegion backGround;
	/** Track asset loading from all instances and subclasses */
	private AssetState platformAssetState = AssetState.EMPTY;

	public static final int PLATFORM_IND=0;
	public static final int WOOD_BOX_IND=1;
	public static final int STONE_BOX_IND=2;
	public static final int FUEL_BOX_IND=3;
	public static final int GOAL_DOOR_IND=4;
	public static final int LADDER_COMPLEX_IND=5;

	private BlockAbstract goalDoor;
	
	public void preLoadContent(AssetManager manager) {
		if (platformAssetState != AssetState.EMPTY) {
			return;
		}

		platformAssetState = AssetState.LOADING;
		manager.load(DUDE_FILE, Texture.class);
		assets.add(DUDE_FILE);
		manager.load(BARRIER_FILE, Texture.class);
		assets.add(BARRIER_FILE);
		manager.load(BULLET_FILE, Texture.class);
		assets.add(BULLET_FILE);
		manager.load(WOOD_FILE, Texture.class);
		assets.add(WOOD_FILE);
		manager.load(FUEL_FILE, Texture.class);
		assets.add(FUEL_FILE);
		manager.load(ROPE_FILE, Texture.class);
		assets.add(ROPE_FILE);
		manager.load(BACKGROUND, Texture.class);
		assets.add(BACKGROUND);
		manager.load(WATER_FILE, Texture.class);
		assets.add(WATER_FILE);
		manager.load(STONE_FILE, Texture.class);
		assets.add(STONE_FILE);
		manager.load(AIDEN_ANIME_FILE, Texture.class);
		assets.add(AIDEN_ANIME_FILE);
		manager.load(AIDEN_DIE_FILE, Texture.class);
		assets.add(AIDEN_DIE_FILE);
		manager.load(WATER_WALK, Texture.class);
		assets.add(WATER_WALK);
		manager.load(BURNING_FILE, Texture.class);
		assets.add(BURNING_FILE);

		manager.load(JUMP_FILE, Sound.class);
		assets.add(JUMP_FILE);
		manager.load(PEW_FILE, Sound.class);
		assets.add(PEW_FILE);
		manager.load(POP_FILE, Sound.class);
		assets.add(POP_FILE);

		super.preLoadContent(manager);
	}

	/**
	 * Load the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic
	 * loaders this time. However, we still want the assets themselves to be
	 * static. So we have an AssetState that determines the current loading
	 * state. If the assets are already loaded, this method will do nothing.
	 * 
	 * @param manager
	 *            Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager) {		
		if (platformAssetState != AssetState.LOADING) {
			return;
		}
		woodTexture = createTexture(manager, WOOD_FILE, false);
		avatarTexture = createTexture(manager, DUDE_FILE, false);
		fuelTexture = createTexture(manager, FUEL_FILE, false);
		ropeTexture = createTexture(manager, ROPE_FILE, true);
		earthTile = createTexture(manager, EARTH_FILE, true);
		backGround = createTexture(manager, BACKGROUND, false);
		waterTexture = createTexture(manager, WATER_FILE, false);
		stoneTexture = createTexture(manager, STONE_FILE, false);
//		SoundController sounds = SoundController.getInstance();
//		sounds.allocate(manager, JUMP_FILE);
//		sounds.allocate(manager, PEW_FILE);
//		sounds.allocate(manager, POP_FILE);
		super.loadContent(manager);
		platformAssetState = AssetState.COMPLETE;
	}
	
	ArrayList<CharacterModel> npcs;
	AidenModel aiden;
	ArrayList<BlockAbstract> blocks;
	
	private boolean holding=false;
	private CharacterModel holdingCharacter=null;
	private BlockAbstract holdingBlock=null;	
	private float inputCoolDown=0;
	private final static float INPUT_COOL_DOWN=0.5f;
	
	private Rectangle platformRect;
	private boolean isAddingRect;
	
	public LevelEditor(){
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		npcs=new ArrayList<CharacterModel>();
		aiden=null;
		blocks=new ArrayList<BlockAbstract>();
		platformRect=new Rectangle(-1,-1,0,0);
		inputCoolDown=0;
		holding=false;
		holdingCharacter=null;
		holdingBlock=null;	
		isAddingRect=false;
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		npcs.clear();
		aiden=null;
		blocks.clear();
		platformRect=new Rectangle(-1,-1,0,0);
		inputCoolDown=0;
		holding=false;
		holdingCharacter=null;
		holdingBlock=null;	
		isAddingRect=false;
	}
	
	
	@Override
	public void update(float dt) {
		System.out.println(this.npcs.size()+" "+this.blocks.size());
		// TODO Auto-generated method stub
		canvas.setEditor(true);
		xPos=InputController.getInstance().mousePos.x
				+ canvas.getCamera().position.x
				-(float)backGround.getRegionWidth()/2f;
		yPos=-InputController.getInstance().mousePos.y
				+ canvas.getCamera().position.y
				+(float)backGround.getRegionHeight()/2f;
		xPos/=scale.x;
		yPos/=scale.y;
    	if (InputController.getInstance().exportPressed){
    		exportToJson();
    		return;
    	}
    	if (InputController.getInstance().loadPressed){
    		loadFromJson();
    		return;
    	}
//		System.out.println(this.blocks.size()+" "+this.npcs.size());
		boolean wasHolding=holding;
		if (inputCoolDown>0) inputCoolDown-=dt;
		if (this.inputCoolDown<=0){
			if (InputController.getInstance().leftClicked){		
				holding=!holding;
				this.inputCoolDown=INPUT_COOL_DOWN;
			}
			if (InputController.getInstance().hasPressedPoly){
				this.inputCoolDown=INPUT_COOL_DOWN;
				if (!isAddingRect) isAddingRect=true;
				else isAddingRect=false;
				if (!isAddingRect){
					this.platformRect=new Rectangle(-1, -1, 0, 0);
				}
				this.inputCoolDown=INPUT_COOL_DOWN;
				System.out.println(isAddingRect);
			}
			if (isAddingRect && InputController.getInstance().leftClicked){;
//				System.out.println("SET UP RECTANGLE "+ platformRect.toString());
				if (this.platformRect.x>=0 && this.platformRect.y>=0){
					float occupy=Math.round(1f/this.gridUnit);
					float width=fitToGrid((xPos-platformRect.x)/occupy)*occupy;
					float height=fitToGrid((yPos-platformRect.y)/occupy)*occupy;
					this.platformRect.setWidth(width);
					this.platformRect.setHeight(height);
					isAddingRect=false;
					Rectangle adjust=new Rectangle(
							Math.min(platformRect.x,
									platformRect.x+platformRect.width),
							Math.min(platformRect.y,
									platformRect.y+platformRect.height),
							Math.abs(platformRect.width),
							Math.abs(platformRect.height));
					if (adjust.width>0 && adjust.height>0){
						Platform block=new Platform(adjust, 1);
						block.setTexture(earthTile);
						block.setDrawScale(scale);
						blocks.add(block);
					}
					this.platformRect=new Rectangle(-1, -1, 0, 0);
				}
				else{
					this.platformRect.setX(fitToGrid(xPos));
					this.platformRect.setY(fitToGrid(yPos));
				}
//				System.out.println("Finish RECTANGLE "+ platformRect.toString());
				return;
			}
		}
		// newly holding an object
		if (holding && !wasHolding){
			if (aiden!=null && aiden.getX()-aiden.getWidth()/2<xPos
					&& aiden.getX()+aiden.getWidth()/2>xPos
					&& aiden.getY()-aiden.getHeight()/2<yPos
					&& aiden.getY()+aiden.getHeight()/2>yPos){
				holdingCharacter=aiden;
			}
			else{
				for (CharacterModel npc: npcs){
					if (npc.getX()-npc.getWidth()/2<xPos
							&& npc.getX()+npc.getWidth()/2>xPos
							&& npc.getY()-npc.getHeight()/2<yPos
							&& npc.getY()+npc.getHeight()/2>yPos){
						holdingCharacter=npc;
						break;
					}
				}
			}
			for (BlockAbstract block: this.blocks){
				if (block.getX()-block.getWidth()/2<xPos
						&& block.getX()+block.getWidth()/2>xPos
						&& block.getY()-block.getHeight()/2<yPos
						&& block.getY()+block.getHeight()/2>yPos){
					holdingBlock=block;
					break;
				}
			}
		}
		// newly releasing an object
		if (!holding && wasHolding){
			if (this.holdingBlock!=null){
				Vector2 trans=fitInGrid(new Vector2(holdingBlock.getX()
						-holdingBlock.getWidth()/2f, 
						holdingBlock.getY()
						-holdingBlock.getHeight()/2f));
				this.holdingBlock.setPosition(holdingBlock.getPosition().cpy()
						.add(trans));
				this.holdingBlock=null;
			}
			if (holdingCharacter!=null){
				Vector2 trans=fitInGrid(new Vector2(holdingCharacter.getX()
						-holdingCharacter.getWidth()/2f, 
						holdingCharacter.getY()
						-holdingCharacter.getHeight()/2f));
					this.holdingCharacter.setPosition(holdingCharacter.getPosition().cpy()
							.add(trans));
				this.holdingCharacter=null;
			}
		}
		// Hold object around
		if (holding){
			if (InputController.getInstance().hasRemovePressed){
				if (holdingCharacter!=null){
					if (holdingCharacter!=aiden)	
						npcs.remove(holdingCharacter);
					else
						aiden=null;
					holdingCharacter=null;
				}
				if (holdingBlock!=null){
					blocks.remove(holdingBlock);
					holdingBlock=null;
				}
				holding=false;
			}
			else{
				if (holdingCharacter!=null){
					holdingCharacter.setPosition(new Vector2(xPos, yPos));
				}
				else if (holdingBlock!=null){
					holdingBlock.setPosition(new Vector2(xPos, yPos));
				}
				else{
					holding=false;
				}
			}
		}
		// Under unholding mode, we can add new objects 
		else{
			if (InputController.getInstance().newAidenPressed
					&& !InputController.getInstance().hasNewAidenPressed){
				aiden=new AidenModel(xPos,yPos, 2.5f, 3f, true);
				Vector2 trans=fitInGrid(new Vector2(aiden.getX()
						-aiden.getWidth()/2f, 
						aiden.getY()
						-aiden.getHeight()/2f));
				aiden.setPosition(aiden.getPosition().add(trans));
				aiden.setTexture(avatarTexture);
				aiden.setDrawScale(scale);
			}
			else if (InputController.getInstance().newCharacterPressed
					&& !InputController.getInstance().hasNewCharacterPressed){
				CharacterModel ch=new CharacterModel(CharacterType.WATER_GUARD, "WaterGuard", 
						xPos, yPos, 2.5f, 3f, true);
				Vector2 trans=fitInGrid(new Vector2(ch.getX()
						-ch.getWidth()/2f, 
						ch.getY()
						-ch.getHeight()/2f));
				ch.setPosition(ch.getPosition().add(trans));
				ch.setTexture(waterTexture);
				ch.setDrawScale(scale);
				npcs.add(ch);
			}
			else if (InputController.getInstance().newBlockPressed &&
					!InputController.getInstance().hasNewBlockPressed){
				BlockAbstract block=null;
				Vector2 trans=new Vector2();
				switch(InputController.getInstance().inputNumber){
				case PLATFORM_IND:
					block=new Platform(new Rectangle(xPos, yPos, 1, 1), 1);
					trans=fitInGrid(new Vector2(block.getX()
							-block.getWidth()/2f, 
							block.getY()
							-block.getHeight()/2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(earthTile);
					block.setDrawScale(scale);
					break;
				case WOOD_BOX_IND:
					block=new FlammableBlock(xPos, yPos, 2, 2, 1, 4);
					trans=fitInGrid(new Vector2(block.getX()
							-block.getWidth()/2f, 
							block.getY()
							-block.getHeight()/2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(woodTexture);
					block.setDrawScale(scale);
					break;
				case STONE_BOX_IND:
					block=new StoneBlock(xPos, yPos, 2, 2);
					trans=fitInGrid(new Vector2(block.getX()
							-block.getWidth()/2f, 
							block.getY()
							-block.getHeight()/2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(stoneTexture);
					block.setDrawScale(scale);
					break;	
				case FUEL_BOX_IND:
					block=new FuelBlock(xPos, yPos, 2, 2, 1, 2, 25);
					trans=fitInGrid(new Vector2(block.getX()
							-block.getWidth()/2f, 
							block.getY()
							-block.getHeight()/2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(fuelTexture);
					block.setDrawScale(scale);
					break;	
				case GOAL_DOOR_IND:
					block=new StoneBlock(xPos, yPos, 2, 2);
					trans=fitInGrid(new Vector2(block.getX()
							-block.getWidth()/2f, 
							block.getY()
							-block.getHeight()/2f));
					block.setPosition(block.getPosition().add(trans));
					block.setTexture(goalTile);
					block.setDrawScale(scale);
					goalDoor=block;
					break;	
				default: break;
				}
				if (block!=null) blocks.add(block);
			}
		}
	}
	private int gridWidth=32;
	private int gridHeight=18;
	private float gridUnit=0.5f;
	
	private float xPos;
	private float yPos;
	@Override
	public void draw(float delta) {
		canvas.clear();

		// canvas.begin(512,288);
		// canvas.begin();
		canvas.begin(xPos, yPos);
		// canvas.draw(backGround, 0, 0);
		canvas.draw(backGround, new Color(1f, 1f, 1f, 1f), 0f, 0f,
				canvas.getWidth(), canvas.getHeight());
		canvas.end();
		
		canvas.beginDebug(1, 1);
		
		int numX=(int) (this.gridWidth/this.gridUnit);
		int numY=(int) (this.gridHeight/this.gridUnit);
		for (int i=0; i<numY; i+=2){
			float[] pts=new float[]{0, 0, gridWidth, 0, gridWidth, gridUnit, 0, gridUnit};
			PolygonShape poly=new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.YELLOW, 0, this.gridUnit*i,
					0, scale.x,scale.y);
		}
		for (int j=0; j<numX; j+=2){
			float[] pts=new float[]{0, 0, gridUnit, 0, gridUnit, gridHeight, 0, gridHeight};
			PolygonShape poly=new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.YELLOW, this.gridUnit*j, 0,
					0, scale.x,scale.y);
		}
		canvas.endDebug();
		canvas.begin(xPos, yPos);
		for (BlockAbstract obj : blocks) {
			obj.draw(canvas);
		}
		for (CharacterModel obj : npcs) {
			obj.draw(canvas);
		}
		if (aiden!=null){
			aiden.simpleDraw(canvas);
		}
		canvas.end();
		canvas.beginDebug(1, 1);
		float occupy=Math.round(1f/this.gridUnit);
		float width=fitToGrid((xPos-platformRect.x)/occupy)*occupy;
		float height=fitToGrid((yPos-platformRect.y)/occupy)*occupy;
		if (this.isAddingRect && platformRect.x>=0 && platformRect.y>=0
				&& (Math.abs(width)>0 && (Math.abs(height)>0))){
			Rectangle adjust=new Rectangle(
					fitToGrid(Math.min(platformRect.x,
							platformRect.x+width)),
					fitToGrid(Math.min(platformRect.y,
							platformRect.y+height)),
					Math.abs(width),
					Math.abs(height));
			float[] pts=new float[]{0, 0, 
					adjust.width, 0, 
					adjust.width, adjust.height, 
					0, adjust.height};
			PolygonShape poly=new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.RED, 
					adjust.x, adjust.y,
					0, scale.x,scale.y);
		}
		if (holding){
			if (this.holdingBlock!=null)
				holdingBlock.drawDebug(canvas, Color.GREEN);
			if (this.holdingCharacter!=null)
				holdingCharacter.drawDebug(canvas, Color.GREEN);
		}
		canvas.endDebug();
	}
	
	public Vector2 fitInGrid(Vector2 pos){
		Vector2 dest=new Vector2();
		dest.x=(Math.round(pos.x/this.gridUnit))*this.gridUnit;
		dest.y=(Math.round(pos.y/this.gridUnit))*this.gridUnit;
		return dest.sub(pos);
	}
	
	public float fitToGrid(float value){
		return (Math.round(value/this.gridUnit))*this.gridUnit;
	}
	
	
	public void exportToJson(){
		Json json = new Json();
		json.setTypeName(null);
		json.setUsePrototypes(false);
		json.setIgnoreUnknownFields(true);
		json.setOutputType(OutputType.json);
		
		ProjectModelJsonRep project = new ProjectModelJsonRep(aiden, blocks, npcs, goalDoor);
		String project_str = json.prettyPrint(project);
		Gdx.files.local("aiden-example.json").delete();
		FileHandle file = Gdx.files.local("aiden-example.json");
		file.writeString(project_str, true);
	}
	
	public void loadFromJson(){
		System.out.println("Loading");
		Scene scene=new Scene("aiden-example.json");
		reset();
		System.out.println("Loading Characters");
		for (CharacterModel ch:scene.getGuards()){
			npcs.add(ch);
			System.out.println("Character "+ch.getPosition());
			ch.setTexture(waterTexture);
			ch.setDrawScale(scale);
		}
		System.out.println("Loading blocks");
		for (BlockAbstract block:scene.getBlocks()){
			blocks.add(block);
			block.setDrawScale(scale);
			System.out.println("block "+block.getPosition()+" "+block.getWidth()+" "+block.getHeight());
			TextureRegion texture=null;
			switch (block.type){
			case FLAMMABLEBLOCK:
				texture=(this.woodTexture);
				break;
			case FUEL:
				texture=(this.fuelTexture);
				break;
			case PLATFORM:
				texture=(this.earthTile);
				break;
			case STONE:
				texture=(this.stoneTexture);
				break;
			case ROPECOMPLEX:
				break;
			default:
				break;
			}
			if (texture!=null) block.setTexture(texture);
		}	
		aiden=scene.getAidenModel();
		if (aiden!=null) {
			aiden.setDrawScale(scale);
			aiden.setTexture(avatarTexture);
		}
		
	}
}
