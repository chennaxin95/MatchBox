package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Opening extends Platform{
	
	public Opening(Rectangle r, float unit) {
		super(r, unit);
		hasRope=true;
		isCarrying=false;
		// TODO Auto-generated constructor stub
	}
	public static final float UP_SPEED=1;
	
	private Vector2 topEntrance;
	private float ropeLength;
	
	private boolean hasRope;
	
	private boolean isCarrying;

	public void update(float dt){
		ropeLength-=dt;
		if (ropeLength<=0) hasRope=false;
	}
	
	public boolean hasRope(){
		return hasRope;
	}
	
	public Vector2 getBottomEntrance(){
		if (!hasRope()) return new Vector2(-1,-1);
		return new Vector2(topEntrance.x, topEntrance.y-ropeLength);
	}

}
