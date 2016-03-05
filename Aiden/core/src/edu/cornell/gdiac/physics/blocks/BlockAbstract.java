package edu.cornell.gdiac.physics.blocks;

import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

public abstract class BlockAbstract extends BoxObstacle{
	/**
	 * @return whether it is flammable
	 */
	public abstract boolean isFammable();
	/**
	 * @return whether it is breakable
	 */
	public abstract boolean isBreakable();
	/**
	 * @return whether it is movable
	 */
	public abstract boolean isMoveable();
	/**
	 * @return whether it is climbable
	 */
	public abstract boolean isClimbable();

	public BlockAbstract(float width, float height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}
	public BlockAbstract(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
	}
}
