package edu.cornell.gdiac.physics.blocks;

import edu.cornell.gdiac.physics.blocks.BlockAbstract.BlockType;

public class FuelBlock extends FlammableBlock{
	
	private int fuelBonus;
	
	public FuelBlock(float x, float y, float width, float height, float spreadRate, float burnRate, int fuels) {
		super(x, y, width, height, spreadRate, burnRate);
		this.setBlockType(BlockType.FUEL);
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
	
	public int getFuelBonus(){
		return fuelBonus;
	}
}
