package edu.cornell.gdiac.physics.blocks;

import edu.cornell.gdiac.physics.material.GeneralMaterial;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

public abstract class BlockAbstract extends BoxObstacle{
	public BlockType type;
	
	protected GeneralMaterial material;
	
	public void setMaterial(GeneralMaterial m){
		material=m;
	};
	
	public GeneralMaterial getMaterial(){
		return material;
	};
	
	/**
	 * @return whether it is flammable
	 */
	public boolean isFlammable() {
		return material.isFlammable();
	}
	/**
	 * @return whether it is breakable
	 */
	public boolean isBreakable() {
		return material.isBreakable();
	}
	/**
	 * @return whether it is movable
	 */
	public boolean isMoveable() {
		return material.isMoveable();
	}
	/**
	 * @return whether it is climbable
	 */
	public boolean isClimbable() {
		return material.isClimbable();
	}
	
	public void setFlammable(boolean flammable) {
		getMaterial().setFlammable(flammable);
	}
	public void setClimbable(boolean climbable) {
		getMaterial().setClimbable(climbable);
	}
	public void setBreakable(boolean breakable) {
		getMaterial().setBreakable(breakable);
	}
	public void setMoveable(boolean moveable) {
		getMaterial().setMoveable(moveable);
	}
	
	public BlockAbstract(float width, float height) {
		super(width, height);
		// TODO Auto-generated constructor stub
	}
	public BlockAbstract(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
	}
	
	public void setBlockType(BlockType t){
		this.type=t;
	}
	
	public BlockType getBlockType(){
		return type;
	}
	
	public enum BlockType{
		FLAMMABLEBLOCK, WOODBOX, FUEL, PLATFORM, STONE, 
	}
}
