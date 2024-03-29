package edu.cornell.gdiac.physics.blocks;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.material.Flammable;
import edu.cornell.gdiac.util.FilmStrip;

public class FlammableBlock extends BlockAbstract{
	
	public FilmStrip burningSprite;
	protected float animeCoolDown; 
	protected int splitFrame;
	public Vector2 ratio = new Vector2(1, 1);
	
	/** Animation cool down */
	protected static final float MAX_ANIME_TIME=0.1f;
	
	public FlammableBlock(float width, float height, float spreadRate, float burnRate) {
		super(width, height);
		setMaterial(new Flammable(spreadRate, burnRate));
		this.setBlockType(BlockType.FLAMMABLEBLOCK);
		// TODO Auto-generated constructor stub
	}

	public FlammableBlock(float x, float y, float width, float height, float spreadRate,
			float burnRate) {
		super(x, y, width, height);
		setMaterial(new Flammable(spreadRate, burnRate));
		this.setBlockType(BlockType.FLAMMABLEBLOCK);
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
				canvas.draw(texture,Color.BLACK,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),ratio.x, ratio.y);
			}
			else if (((Flammable)material).isBurning()){
				Color c=new Color();
				if (((Flammable)material).getBurnRatio()>0.3){
					c=new Color(1, ((Flammable)material).getBurnRatio(), ((Flammable)material).getBurnRatio(), 1);
				}
				else if(this instanceof FuelBlock){
					c = Color.RED;
				}
				else{
					c=new Color(((Flammable)material).getBurnRatio()/0.3f,((Flammable)material).getBurnRatio(), ratio.x, ratio.y);
				}
				
				canvas.draw(texture,c,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(), ratio.x, ratio.y);
			}
			else{
				canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),ratio.x, ratio.y);
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
		
		canvas.draw(burningSprite, Color.WHITE, ox, oy, 
				getX() * drawScale.x, 
				getY() * drawScale.y, getAngle(), 
				this.getWidth()/burningSprite.getRegionWidth()*drawScale.x,
				this.getHeight()/burningSprite.getRegionHeight()*drawScale.y
				);
	}

	public boolean canSpreadFire() {
		// TODO Auto-generated method stub
		return getMaterial().canSpreadFire();
	}
	
	public boolean isBurnt(){
		return getMaterial().isBurnt();
	}
			

	public boolean isBurning(){
		return getMaterial().isBurning();
	}
	/**
	 * @return the seconds until it gets burnt/destroyed
	 */
	public float getBurnTime(){
		return getMaterial().getBurnTime();
	}
	/**
	 * @return the percentage of remaining frames until it gets burnt/destroyed
	 */
	public float getBurnRatio(){
		return getMaterial().getBurnRatio();
	}
	/**
	 * @return the percentage of remaining frames until it starts to spread
	 */
	public float getSpreadRatio(){
		return getMaterial().getSpreadRatio();
	}
	/**
	 * Set the object to the state of on fire;
	 * start burning count down, if it's not.
	 */
	public void activateBurnTimer(){
		getMaterial().activateBurnTimer();
	}
	/**
	 * Stop burning count down (will not reset burn timer);
	 * equivalent to fire being put out.
	 */
	public void stopBurnTimer(){
		getMaterial().stopBurnTimer();
	}
	
	/**
	 * Reset burn timer to initial value
	 */
	public void resetBurnTimer(){
		getMaterial().resetBurnTimer();
	}
	/**
	 * Reset spread timer to initial value
	 */
	public void resetSpreadTimer(){
		getMaterial().resetSpreadTimer();
	}
	
	@Override
	public Flammable getMaterial(){
		return (Flammable)material; 
	}
}
