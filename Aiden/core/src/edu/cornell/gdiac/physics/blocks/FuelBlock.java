package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.material.Flammable;
import edu.cornell.gdiac.util.FilmStrip;

//import edu.cornell.gdiac.physics.blocks.BlockAbstract.BlockType;

public class FuelBlock extends FlammableBlock {

	private int fuelBonus;
	private boolean isCheckpoint;
	private FilmStrip fm;
	private float MAX_ANIME_TIME = 0.2f;
	private float animeCoolDown = 0.2f;

	public FuelBlock(float x, float y, float width, float height,
			float spreadRate, float burnRate, int fuels, boolean icp) {
		super(x, y, width, height, spreadRate, burnRate);
		this.setBlockType(BlockType.FUEL);
		this.setBodyType(BodyType.StaticBody);
		this.fuelBonus = fuels;
		this.setFriction(10f);
		this.isCheckpoint = icp;
		// TODO Auto-generated constructor stub
	}

	public void setTexture(FilmStrip fm){
		this.fm = fm;
	}
	
	public FuelBlock(float width, float height, float spreadRate,
			float burnRate, int fuels, boolean icp) {
		super(width, height, spreadRate, burnRate);
		this.setBlockType(BlockType.FUEL);
		this.fuelBonus = fuels;
		this.isCheckpoint = icp;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		animeCoolDown -= dt;
	}

	public int getFuelBonus() {
		return fuelBonus;
	}
	
	@Override
	public void draw(GameCanvas canvas){
		if (this.isBurnt()){
			return;
		}
		else{
			animate(canvas);
		}
	}
	
	public void animate(GameCanvas canvas){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			fm.setFrame((fm.getFrame()+1)%fm.getSize());
		}
		canvas.draw(fm, this.getX()*drawScale.x-25, this.getY()*drawScale.y-25);
	}

	public boolean isCheckpoint() {
		return isCheckpoint;
	}
}
