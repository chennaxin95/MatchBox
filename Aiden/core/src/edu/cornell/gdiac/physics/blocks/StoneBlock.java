package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;

import edu.cornell.gdiac.physics.GameCanvas;

public class StoneBlock extends BlockAbstract {

	public StoneBlock(float width, float height) {
		super(width, height);
		// TODO Auto-generated constructor stub
		flammable=false;
		breakable=false;
		climbable=false;
		moveable=false;
	}
	
	public StoneBlock(float x, float y, float width, float height) {
		super(x,y,width,height);
		// TODO Auto-generated constructor stub
		flammable=false;
		breakable=false;
		climbable=false;
		moveable=false;
	}
}
