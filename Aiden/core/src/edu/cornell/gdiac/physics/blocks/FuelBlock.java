package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import edu.cornell.gdiac.physics.material.Flammable;

//import edu.cornell.gdiac.physics.blocks.BlockAbstract.BlockType;

public class FuelBlock extends FlammableBlock{
	
	private int fuelBonus;
	
	public FuelBlock(float x, float y, float width, float height, float spreadRate, float burnRate, int fuels) {
		super(x, y+0.5f, width, height, spreadRate, burnRate);
		this.setBlockType(BlockType.FUEL);
		this.setBodyType(BodyType.StaticBody);
		this.fuelBonus=fuels;
		this.setFriction(10f);
		// TODO Auto-generated constructor stub
	}
	
	public FuelBlock(float width, float height,float spreadRate, float burnRate, int fuels) {
		super(width, height, spreadRate, burnRate);
		this.setBlockType(BlockType.FUEL);
		this.fuelBonus=fuels;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		this.setAngle(this.getAngle()+0.02f);
	}
	
	public int getFuelBonus(){
		return fuelBonus;
	}
}
