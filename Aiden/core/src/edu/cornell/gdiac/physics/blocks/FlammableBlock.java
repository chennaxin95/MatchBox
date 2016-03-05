package edu.cornell.gdiac.physics.blocks;

public class FlammableBlock extends BlockAbstract implements FlammableInterface {
	
	private int spreadRate;
	private int burnRate;
	
	private int spreadTimer;
	private int burnTimer;
	
	private boolean burning;
	private boolean burnt;
	
	public FlammableBlock(float width, float height, int spreadRate, int burnRate) {
		super(width, height);
		flammable=true;
		burning=false;
		burnt=false;
		this.spreadRate=spreadRate;
		this.burnRate=burnRate;
		resetBurnTimer();
		resetSpreadTimer();
		// TODO Auto-generated constructor stub
	}

	public FlammableBlock(float x, float y, float width, float height, int spreadRate,
			int burnRate) {
		super(x, y, width, height);
		flammable=true;
		burning=false;
		burnt=false;
		this.spreadRate=spreadRate;
		this.burnRate=burnRate;
		resetBurnTimer();
		resetSpreadTimer();
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
	public int getBurnTime() {
		return burnTimer;
	}

	@Override
	public float getBurnRatio() {
		return ((float)this.burnTimer)/this.burnRate;
	}

	@Override
	public void activateBurnTimer() {
		burning=true;
		resetSpreadTimer();
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
	public void updateBurningState() {
		// TODO Auto-generated method stub
		if (this.isBurning()){
			burnTimer--;
			spreadTimer--;
		}
		checkBurnt();
	}
	

}
