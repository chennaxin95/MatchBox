package edu.cornell.gdiac.physics.blocks;

public interface FlammableInterface {
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
	 * @return the frames until it gets burnt/destroyed
	 */
	public int getBurnTime();
	/**
	 * @return the percentage of remaining frames until it gets burnt/destroyed
	 */
	public float getBurnRatio();
	/**
	 * Start burning count down, if it's not
	 */
	public void activateBurnTimer();
	/**
	 * Stop burning count down (will not reset burn timer)
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
	
}
