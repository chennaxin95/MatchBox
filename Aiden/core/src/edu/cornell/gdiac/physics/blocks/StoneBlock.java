package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import edu.cornell.gdiac.physics.material.GeneralMaterial;

public class StoneBlock extends BlockAbstract {

	protected GeneralMaterial material;
	
	public void setMaterial(GeneralMaterial m){
		material=m;
	};
	
	
	public GeneralMaterial getMaterial(){
		return material;
	};
	
	
	public StoneBlock(float width, float height) {
		super(width, height);
		// TODO Auto-generated constructor stub
		material=new GeneralMaterial();
		this.setBlockType(BlockType.STONE);
		this.setDensity(1000000);
		this.resetMass();
		this.setFriction(100);
	}
	
	public StoneBlock(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
		material=new GeneralMaterial();
		this.setBlockType(BlockType.STONE);
		this.setDensity(100000);
		this.resetMass();
		this.setFriction(100);
	}
	
}
