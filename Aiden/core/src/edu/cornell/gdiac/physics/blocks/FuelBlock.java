package edu.cornell.gdiac.physics.blocks;

public class FuelBlock extends FlammableBlock{
	
	private int fuelBonus;
	
	public FuelBlock(float x, float y, float width, float height,
			int spreadRate, int burnRate, int fuels) {
		super(x, y, width, height, spreadRate, burnRate);
		this.fuelBonus=fuels;
		// TODO Auto-generated constructor stub
		flammable=true;
		breakable=false;
		climbable=false;
		moveable=false;
	}
	
	public FuelBlock(float width, float height,int spreadRate, int burnRate, int fuels) {
		super(width, height, spreadRate, burnRate);
		this.fuelBonus=fuels;
		// TODO Auto-generated constructor stub
		flammable=true;
		breakable=false;
		climbable=false;
		moveable=false;
	}
	
	public int getFuelBonus(){
		return fuelBonus;
	}
}
