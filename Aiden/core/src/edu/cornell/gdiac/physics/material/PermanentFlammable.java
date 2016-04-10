package edu.cornell.gdiac.physics.material;

public class PermanentFlammable extends GeneralMaterial implements FlammableInterface{	
	private float spreadRate;
	private float burnRate;
	
	private float spreadTimer;
	private float burnTimer;
	
	private boolean burning;
	private boolean burnt;
	public int fuelPenalty;
	
	public PermanentFlammable(float spreadRate) {
		flammable=true;
		climbable=false;
		breakable=false;
		moveable=false;
		
		burning=false;
		burnt=false;
		this.spreadRate=spreadRate;
		this.burnRate=9999999;
		resetBurnTimer();
		resetSpreadTimer();
		fuelPenalty=0;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean canSpreadFire() {
		return !burnt && burning && spreadTimer<=0;
	}

	@Override
	public boolean isBurning() {
		return !burnt && burning;
	}

	@Override
	public float getBurnTime() {
		return burnTimer;
	}

	@Override
	public float getBurnRatio() {
		return this.burnTimer/this.burnRate;
	}

	@Override
	public float getSpreadRatio() {
		return this.spreadTimer/this.spreadRate;
	}

	@Override
	public void activateBurnTimer() {
		if (!burning){
			burning=true;
			resetSpreadTimer();
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopBurnTimer() {
		burning=false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetBurnTimer() {
		burnTimer=burnRate;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetSpreadTimer() {
		spreadTimer=spreadRate;
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isBurnt() {
		// TODO Auto-generated method stub
		return burnt;
	}
	
	private void checkBurnt(){
		if (this.burnTimer<=0) burnt=true;
	}

	@Override
	public void updateBurningState(float dt) {
		// TODO Auto-generated method stub
		if (this.isBurning()){
			burnTimer-=dt;
			spreadTimer-=dt;
		}
		checkBurnt();
	}
	
	public int getFuelPenalty(){
		return fuelPenalty;
	}
	
	public void setFuelPenalty(int fuelP){
		fuelPenalty=fuelP;
	}
	
}
