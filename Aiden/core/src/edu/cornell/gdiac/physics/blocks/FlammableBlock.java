package edu.cornell.gdiac.physics.blocks;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.material.Flammable;
import edu.cornell.gdiac.physics.material.FlammableInterface;
import edu.cornell.gdiac.physics.material.GeneralMaterial;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

public class FlammableBlock extends BlockAbstract{
	
	public FilmStrip burningSprite;
	protected float animeCoolDown; 
	protected int splitFrame;
	
	/** Animation cool down */
	protected static final float MAX_ANIME_TIME=0.1f;
	
	public FlammableBlock(float width, float height, float spreadRate, float burnRate) {
		super(width, height);
		setMaterial(new Flammable(spreadRate, burnRate));
		// TODO Auto-generated constructor stub
	}

	public FlammableBlock(float x, float y, float width, float height, float spreadRate,
			float burnRate) {
		super(x, y, width, height);
		setMaterial(new Flammable(spreadRate, burnRate));
		// TODO Auto-generated constructor stub
	}
	

	public void update(float dt) {
		((Flammable)material).updateBurningState(dt);
		animeCoolDown-=dt;
	}
	
	@Override
	public void draw(GameCanvas canvas) {
		if (texture != null) {
			if (((Flammable)material).isBurnt()){
				canvas.draw(texture,Color.BLACK,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
			else if (((Flammable)material).isBurning()){
				Color c=new Color();
				if (((Flammable)material).getBurnRatio()>0.3){
					c=new Color(1, ((Flammable)material).getBurnRatio(), 0, 1);
				}
				else{
					c=new Color(((Flammable)material).getBurnRatio()/0.3f,((Flammable)material).getBurnRatio(), 0, 1);
				}
				canvas.draw(texture,c,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
			else{
				canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
			}
		}
		if (((Flammable)material).isBurning()){
			burningAnimate(canvas);
		}
	}
	
	public void setBurningTexture(FilmStrip f, int sf){
		burningSprite=f;
		burningSprite.setFrame(0);
		splitFrame=sf;
	}
	
	
	public void burningAnimate(GameCanvas canvas){
		if (burningSprite==null) return;
		if (this.animeCoolDown<=0) {
			Random r=new Random();
			animeCoolDown=(r.nextFloat()*MAX_ANIME_TIME)/2+MAX_ANIME_TIME/2;
			if (burningSprite.getFrame()==burningSprite.getSize()-1){
				burningSprite.setFrame(splitFrame);
			}
			else{
				burningSprite.setFrame(burningSprite.getFrame()+1);
			}
		}
		// For placement purposes, put origin in center.
		float ox = 0.5f * burningSprite.getRegionWidth();
		float oy = 0.5f * burningSprite.getRegionHeight();
		
		canvas.draw(burningSprite, Color.WHITE, ox, oy, getX() * drawScale.x, 
				getY() * drawScale.y, getAngle(), 1f, 1f);
	}

	public boolean canSpreadFire() {
		// TODO Auto-generated method stub
		return ((Flammable)material).canSpreadFire();
	}
	
	public boolean isBurnt(){
		return ((Flammable)material).isBurnt();
	}
			

	public boolean isBurning(){
		return ((Flammable)material).isBurning();
	}
	/**
	 * @return the seconds until it gets burnt/destroyed
	 */
	public float getBurnTime(){
		return ((Flammable)material).getBurnTime();
	}
	/**
	 * @return the percentage of remaining frames until it gets burnt/destroyed
	 */
	public float getBurnRatio(){
		return ((Flammable)material).getBurnRatio();
	}
	/**
	 * @return the percentage of remaining frames until it starts to spread
	 */
	public float getSpreadRatio(){
		return ((Flammable)material).getSpreadRatio();
	}
	/**
	 * Set the object to the state of on fire;
	 * start burning count down, if it's not.
	 */
	public void activateBurnTimer(){
		((Flammable)material).activateBurnTimer();
	}
	/**
	 * Stop burning count down (will not reset burn timer);
	 * equivalent to fire being put out.
	 */
	public void stopBurnTimer(){
		((Flammable)material).stopBurnTimer();
	}
	
	/**
	 * Reset burn timer to initial value
	 */
	public void resetBurnTimer(){
		((Flammable)material).resetBurnTimer();
	}
	/**
	 * Reset spread timer to initial value
	 */
	public void resetSpreadTimer(){
		((Flammable)material).resetSpreadTimer();
	}
}
