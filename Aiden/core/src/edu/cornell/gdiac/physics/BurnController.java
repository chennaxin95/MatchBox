package edu.cornell.gdiac.physics;

import com.badlogic.gdx.physics.box2d.World;

import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.util.PooledList;

/** 
 * Due to AidenController (previously platformController) integrates collision
 * controller and it's hard to single it out, I implemented the burning controller code
 * inside Update() and beginContact(). 
 * AidenModel is modified with simple fuel system.
 * 
 * Also I have modified FlammableBlock's UpdateBurningState() to take in float since
 * counting with frame rates are less reliable than with time. 
 * 
 * FlammaBlocks should probably override the draw() method in simpleObstacles or load
 * different texture when burning
 */

public class BurnController{
	public void getBurning(PooledList<FlammableBlock> flammables,PooledList<Obstacle> objects, float dt, World world){
		// update flammable objects;
		for (FlammableBlock fb : flammables) {
			fb.update(dt);
			if (fb.isBurnt()) {
				objects.remove(fb);
				flammables.remove(fb);
				fb.markRemoved(true);
				fb.deactivatePhysics(world);
			}
		}
	}
}