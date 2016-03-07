package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;

import edu.cornell.gdiac.physics.GameCanvas;

public class FlammableBlock extends BlockAbstract implements FlammableInterface {
	
	private float spreadRate;
	private float burnRate;
	
	private float spreadTimer;
	private float burnTimer;
	
	private boolean burning;
	private boolean burnt;
	public int fuelPenalty;
	
	public FlammableBlock(float width, float height, float spreadRate, float burnRate) {
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

	public FlammableBlock(float x, float y, float width, float height, float spreadRate,
			float burnRate) {
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
	@Override
	public void draw(GameCanvas canvas) {
		if (texture != null) {
			if (isBurnt()){
				canvas.draw(texture,Color.BLACK,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
			else if (isBurning()){
				Color c=new Color();
				if (getBurnRatio()>0.3){
					c=new Color(1, getBurnRatio(), 0, 1);
				}
				else{
					c=new Color(getBurnRatio()/0.3f, getBurnRatio(), 0, 1);
				}
				canvas.draw(texture,c,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
			else{
				canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
		}
	}
}
