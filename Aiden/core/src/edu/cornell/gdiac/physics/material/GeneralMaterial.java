package edu.cornell.gdiac.physics.material;

public class GeneralMaterial {
	
	protected boolean flammable;
	protected boolean climbable;
	protected boolean breakable;
	protected boolean moveable;
	
	public GeneralMaterial(){
		flammable = false;
		climbable = false;
		breakable = false;
		moveable  = false;
	}
	
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

}
