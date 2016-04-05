package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.material.GeneralMaterial;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;

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
	}
	
	public StoneBlock(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
		material=new GeneralMaterial();
	}
	
	@Override
	public void draw(GameCanvas canvas) {
		if (texture != null) {
			canvas.draw(texture,Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.x,getAngle(),1,1);
		}
	}
}
