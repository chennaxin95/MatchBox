package edu.cornell.gdiac.physics.blocks;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

public abstract class BlockAbstract extends BoxObstacle{
	protected boolean flammable;
	protected boolean climbable;
	protected boolean breakable;
	protected boolean moveable;
	
	/**
	 * @return whether it is flammable
	 */
	public boolean isFlammable() {
		return flammable;
	}
	/**
	 * @return whether it is breakable
	 */
	public boolean isBreakable() {
		return breakable;
	}
	/**
	 * @return whether it is movable
	 */
	public boolean isMoveable() {
		return moveable;
	}
	/**
	 * @return whether it is climbable
	 */
	public boolean isClimbable() {
		return climbable;
	}
	
	public void setFlammable(boolean flammable) {
		this.flammable = flammable;
	}
	public void setClimbable(boolean climbable) {
		this.climbable = climbable;
	}
	public void setBreakable(boolean breakable) {
		this.breakable = breakable;
	}
	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	
	public BlockAbstract(float width, float height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}
	public BlockAbstract(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
	}
}
