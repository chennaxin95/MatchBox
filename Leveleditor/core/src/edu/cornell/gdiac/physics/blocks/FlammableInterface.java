package edu.cornell.gdiac.physics.blocks;

public interface FlammableInterface {
	/**
	 * @return whether it is burnt;
	 * True if its burn timer counts down to 0
	 */
	public boolean isBurnt();
	/**
	 * @return whether it can spread fire to contacting neighbors
	 * True if it is on fire and the spread timer counts down to 0
	 */
	public boolean canSpreadFire();
	/**
	 * @return whether it is on fire
	 */
	public boolean isBurning();
	/**
	 * @return the seconds until it gets burnt/destroyed
	 */
	public float getBurnTime();
	/**
	 * @return the percentage of remaining frames until it gets burnt/destroyed
	 */
	public float getBurnRatio();
	/**
	 * @return the percentage of remaining frames until it starts to spread
	 */
	public float getSpreadRatio();
	/**
	 * Set the object to the state of on fire;
	 * start burning count down, if it's not.
	 */
	public void activateBurnTimer();
	/**
	 * Stop burning count down (will not reset burn timer);
	 * equivalent to fire being put out.
	 */
	public void stopBurnTimer();
	
	/**
	 * Reset burn timer to initial value
	 */
	public void resetBurnTimer();
	/**
	 * Reset spread timer to initial value
	 */
	public void resetSpreadTimer();
	/**
	 * Update spread and burn timers per frame; check whether it's burnt
	 */
	public void updateBurningState(float dt);
	
}
