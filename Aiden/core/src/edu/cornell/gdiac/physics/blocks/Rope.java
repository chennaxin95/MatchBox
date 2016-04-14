package edu.cornell.gdiac.physics.blocks;

/*
 * RopeBridge.java
 *
 * The class is a classic example of how to subclass ComplexPhysicsObject.
 * You have to implement the createJoints() method to stick in all of the
 * joints between objects.
 *
 * This is one of the files that you are expected to modify. Please limit changes to 
 * the regions that say INSERT CODE HERE.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.*;

/**
 * A bridge with planks connected by revolute joints.
 *
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Rope extends ComplexObstacle {
	/** The radius of each anchor pin */
	private static final float BRIDGE_PIN_RADIUS = 0.1f;
	/** The density of each plank in the bridge */
	private static final float BASIC_DENSITY = 1.0f;

	// Invisible anchor objects
	/** The left side of the bridge */
	private WheelObstacle start = null;
	
	protected float SPREAD = 0.5f;
	protected float BURN = 4f;
	

	// Dimension information
	/** The size of the entire bridge */
	protected Vector2 dimension;
	/** The size of a single plank */
	protected Vector2 planksize;
	/* The length of each link */
	protected float linksize = 1.0f;
	/** The spacing between each link */
	protected float spacing = 0.0f;
	/** the length for rope_parts */
	private float lheight = 1.0f;
	/** the width of the rope_parts */
//	private float lwidth = 0.05f;
	/** number of rope segments */
	protected int SEGMENTS = 15;

    /**
     * Creates a new rope bridge with the given anchors.
     *
	 * @param x0  		The x position of the left anchor
	 * @param y0  		The y position of the left anchor
	 */
	public Rope(float x0, float y0, float lwidth, float lheight) {
		super(x0,y0);
		setName("rope");
		Vector2 pos = new Vector2(x0, y0);
		this.lheight = lheight;
		linksize = lheight/4;
		for (int i = 0; i < SEGMENTS; i++){
			FlammableBlock part = new FlammableBlock(pos.x, pos.y, lwidth, lheight,
					SPREAD, BURN);
			part.setName("rope_part");
	        part.setDensity(BASIC_DENSITY);
	        bodies.add(part);
	        part.setClimbable(true);
	        pos.y = pos.y - lheight;
		}
	}

	/**
	 * Creates the joints for this object.
	 * 
	 * This method is executed as part of activePhysics. This is the primary method to 
	 * override for custom physics objects.
	 *
	 * @param world Box2D world to store joints
	 *
	 * @return true if object allocation succeeded
	 */
	protected boolean createJoints(World world) {
		assert bodies.size > 0;
		
		Vector2 anchor1 = new Vector2(); 
		Vector2 anchor2 = new Vector2(0, lheight/2);
		
		// Create the top anchor
		Vector2 pos = bodies.get(0).getPosition();
		pos.y += lheight/2 - linksize/2;
		start = new WheelObstacle(pos.x,pos.y,BRIDGE_PIN_RADIUS);
		start.setName("rope_pin"+0);
		start.setDensity(BASIC_DENSITY);
		start.setBodyType(BodyDef.BodyType.StaticBody);
		start.activatePhysics(world);
		
		// Definition for a revolute joint
		RevoluteJointDef jointDef = new RevoluteJointDef();

		// Initial joint
		jointDef.bodyA = start.getBody();
		jointDef.bodyB = bodies.get(0).getBody();
		jointDef.localAnchorA.set(anchor1);
		jointDef.localAnchorB.set(anchor2);
		jointDef.collideConnected = false;
		Joint joint = world.createJoint(jointDef);
		joints.add(joint);
		
		// Link the planks together
		anchor1.y = -lheight/2;
		for (int ii = 0; ii < bodies.size-1; ii++) {
			jointDef.bodyA = bodies.get(ii).getBody();
			jointDef.bodyB = bodies.get(ii+1).getBody();
			jointDef.localAnchorA.set(anchor1);
 			jointDef.localAnchorB.set(anchor2);
			jointDef.collideConnected = false;
			joint = world.createJoint(jointDef);
			joints.add(joint);
		}
		return true;
	}
	
	/**
	 * Destroys the physics Body(s) of this object if applicable,
	 * removing them from the world.
	 * 
	 * @param world Box2D world that stores body
	 */
	public void deactivatePhysics(World world) {
		super.deactivatePhysics(world);
		if (start != null) {
			start.markRemoved(true);
		}
	}
	
	/**
	 * Sets the texture for the individual planks
	 *
	 * @param texture the texture for the individual planks
	 */
	public void setTexture(TextureRegion texture) {
		for(Obstacle body : bodies) {
			((SimpleObstacle)body).setTexture(texture);
		}
	}
	
	/**update parts burn state*/
	public boolean updateParts(World world){
		FlammableBlock temp;
		if (bodies.size == 0){
			this.start.markRemoved(true);
			return true;
		}
		for (int i = 0; i < bodies.size; i++){
			temp = (FlammableBlock) bodies.get(i);
			if(temp.isBurnt()){
				world.destroyBody(bodies.get(i).getBody());
				bodies.removeIndex(i);
//				world.destroyJoint(joints.get(i));
//				System.out.println("====================");
//				System.out.println("hhhhhhhhhhhhhhhhhhhh");
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Returns the texture for the individual planks
	 *
	 * @return the texture for the individual planks
	 */
	public TextureRegion getTexture() {
		if (bodies.size == 0) {
			return null;
		}
		return ((SimpleObstacle)bodies.get(0)).getTexture();
	}

	@Override
	public void drawDebug(GameCanvas canvas, Color c) {
		// TODO Auto-generated method stub
		
	}
}