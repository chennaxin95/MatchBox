package edu.cornell.gdiac.physics.blocks;

public class WoodBlock extends FlammableBlock{
	
	private int fuelPenalty;

	public WoodBlock(float x, float y, float width, float height,
			float spreadRate, float burnRate, int fuels) {
		super(x, y, width, height, spreadRate, burnRate);
		this.fuelPenalty=fuels;
		// TODO Auto-generated constructor stub
		flammable=true;
		breakable=false;
		climbable=false;
		moveable=false;
	}
	
	public WoodBlock(float width, float height,float spreadRate, float burnRate, int fuels) {
		super(width, height, spreadRate, burnRate);
		this.fuelPenalty=fuels;
		flammable=true;
		breakable=false;
		climbable=false;
		moveable=false;
		// TODO Auto-generated constructor stub
	}
	
	public int getFuelPenalty(){
		return fuelPenalty;
	}
	
}
