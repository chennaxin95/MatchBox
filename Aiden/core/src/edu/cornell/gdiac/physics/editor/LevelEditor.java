package edu.cornell.gdiac.physics.editor;
import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.WorldController;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.obstacle.Obstacle;


public class LevelEditor extends WorldController {
	/** The texture file for the character avatar (no animation) */
	private static final String DUDE_FILE = "platform/dude.png";
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
	ArrayList<FlammableBlock> flammableBlocks;
	
	Vector2 leftBottomPos=new Vector2();
	public LevelEditor(){
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_GRAVITY);
		npcs=new ArrayList<CharacterModel>();
		aiden=null;
		flammableBlocks=new ArrayList<FlammableBlock>();
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		npcs.clear();
		aiden=null;
		flammableBlocks.clear();
	}
	
	private boolean holding=false;
	private CharacterModel holdingCharacter=null;
	private FlammableBlock holdingBlock=null;	
	private float inputCoolDown=0;
	private final static float INPUT_COOL_DOWN=0.5f;
	
	
	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		xPos=InputController.getInstance().mousePos.x
				+ canvas.getCamera().position.x-(float)backGround.getRegionWidth()/2f;
		yPos=-InputController.getInstance().mousePos.y
				+ canvas.getCamera().position.y+(float)backGround.getRegionHeight()/2;
    	xPos = xPos* 16f/512f;
    	yPos = yPos* 8f/288f;
    		
		System.out.println(xPos+" "+yPos);
		System.out.println(this.flammableBlocks.size()+" "+this.npcs.size());
		boolean wasHolding=holding;
		if (this.inputCoolDown>0){
			this.inputCoolDown-=dt;
		}
		if (InputController.getInstance().leftClicked && this.inputCoolDown<=0){		
			holding=!holding;
			this.inputCoolDown=INPUT_COOL_DOWN;
		}
		if (holding && !wasHolding){
			for (CharacterModel npc: npcs){
				if (npc.getX()-npc.getWidth()/2<xPos
						&& npc.getX()+npc.getWidth()/2>xPos
						&& npc.getY()-npc.getHeight()/2<yPos
						&& npc.getY()+npc.getHeight()/2>yPos){
					holdingCharacter=npc;
					break;
				}
			}
			for (FlammableBlock block: this.flammableBlocks){
				if (block.getX()-block.getWidth()/2<xPos
						&& block.getX()+block.getWidth()/2>xPos
						&& block.getY()-block.getHeight()/2<yPos
						&& block.getY()+block.getHeight()/2>yPos){
					holdingBlock=block;
					break;
				}
			}
		}
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
		if (holding){
			if (holdingCharacter!=null){
				holdingCharacter.setPosition(new Vector2(xPos, yPos));
			}
			else if (holdingBlock!=null){
				holdingBlock.setPosition(new Vector2(xPos, yPos));
			}
		}
		else{
			if (InputController.getInstance().newCharacterPressed
					&& !InputController.getInstance().hasNewCharacterPressed){
				CharacterModel ch=new CharacterModel(CharacterType.WATER_GUARD, "WaterGuard", 
						xPos, yPos, 2.5f, 3f,
						true);
				System.out.println("width "+ch.getWidth());
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
				FlammableBlock block=new FlammableBlock(xPos, yPos, 2, 2, 1, 4);
				Vector2 trans=fitInGrid(new Vector2(block.getX()
						-block.getWidth()/2f, 
						block.getY()
						-block.getHeight()/2f));
				block.setPosition(block.getPosition().add(trans));
				block.setTexture(woodTexture);
				block.setDrawScale(scale);
				flammableBlocks.add(block);
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
		for (FlammableBlock obj : flammableBlocks) {
			obj.draw(canvas);
		}
		for (CharacterModel obj : npcs) {
			obj.draw(canvas);
		}
		if (aiden!=null){
			aiden.draw(canvas);
		}
		canvas.end();
		canvas.beginDebug(1, 1);
		int numX=(int) (this.gridWidth/this.gridUnit);
		int numY=(int) (this.gridHeight/this.gridUnit);
		for (int i=0; i<numX; i++){
			for (int j=0; j<numY; j++){
				float[] pts=new float[]{0, 0, gridUnit, 0, gridUnit, gridUnit, 0, gridUnit};
				PolygonShape poly=new PolygonShape();
				poly.set(pts);
				canvas.drawPhysics(poly, Color.YELLOW, this.gridUnit*i, this.gridUnit*j,
						0, scale.x,scale.y);
			}
		}
		canvas.endDebug();	
	}
	
	public Vector2 fitInGrid(Vector2 pos){
		Vector2 dest=new Vector2();
		dest.x=(Math.round(pos.x/this.gridUnit))*this.gridUnit;
		dest.y=(Math.round(pos.y/this.gridUnit))*this.gridUnit;
		return dest.sub(pos);
	}
}
