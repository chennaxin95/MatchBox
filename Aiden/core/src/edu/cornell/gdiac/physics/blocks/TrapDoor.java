package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

public class TrapDoor extends StoneBlock{
	public WheelObstacle anchor;
	public WheelObstacle anchorRope;
	public FlammableBlock rope;
	public float rw;
	public float rl;
	public Joint Top;
	public Joint Down;
	public Joint End;
	float posX;
	public boolean isLeft;
	
	public TrapDoor(float x, float y, float width, float height, boolean isLeft){
		super(x, y, width, height);
		this.isLeft = isLeft;
		this.setBlockType(BlockType.TRAPDOOR);
	}
	
	public boolean activatePhysics(World world){

		super.activatePhysics(world);
		System.out.println("Trap door creation pass in");
		float linkSize=0.1f;
		//Create the side anchor
		float end;
		if(isLeft){
			posX = getX() - getWidth()/2f+linkSize;
			end = getX() + getWidth()/2f-linkSize;
		}
		else{
			posX = getX() + getWidth()/2f-linkSize;
			end = getX() - getWidth()/2f+linkSize;
		}
		Vector2 pos = new Vector2(posX, this.getY());
		anchor = new WheelObstacle(pos.x,  pos.y, 1f);
		anchor.setName("anchor");
		anchor.setDensity(1);
		anchor.setBodyType(BodyDef.BodyType.StaticBody);
		anchor.activatePhysics(world);
		System.out.println("Trap door creation pass 1");

		
		// Definition for a revolute joint
		RevoluteJointDef jointDef = new RevoluteJointDef();
		// Initial joint
		jointDef.bodyA = anchor.getBody();
		jointDef.bodyB = body;
		Vector2 anchorP = new Vector2(0, 0);
		jointDef.localAnchorA.set(anchorP);
		anchorP.x = isLeft ? -getWidth()/2f+linkSize : getWidth()/2f-linkSize; 
		jointDef.localAnchorB.set(anchorP);
		jointDef.collideConnected = false;
		Joint joint = world.createJoint(jointDef);
		Top = joint;
		System.out.println("Trap door creation pass 2");

		//create hanging rope
		System.out.println(end+" "+getY()+rl/2f+" "+rw+" "+rl);

		rope = new FlammableBlock(end, getY()+rl/2f, rw, rl, 1, 3);
		rope.setDrawScale(this.drawScale);
		rope.activatePhysics(world);
		System.out.println("Trap door creation pass 3");

		//anchor rope
		pos.x = end;
		pos.y = getY()+rl;
		anchorRope = new WheelObstacle(pos.x, pos.y, 1f);
		anchorRope.setName(this.getName()+"anchorRope");
		anchorRope.setDensity(1);
		anchorRope.setBodyType(BodyDef.BodyType.StaticBody);
		anchorRope.setDrawScale(this.drawScale);
		anchorRope.activatePhysics(world);
		jointDef.bodyA = rope.getBody();
		jointDef.bodyB = anchorRope.getBody();
		anchorP = new Vector2(0, 0);
		anchorP.y = -rl/2f;
		jointDef.localAnchorA.set(anchorP);
		anchorP.y = 0;
		anchorP.x = 0; 
		jointDef.localAnchorB.set(anchorP);
		jointDef.collideConnected = false;
		Joint joint2 = world.createJoint(jointDef);
		End = joint2;
		System.out.println("Trap door creation pass 4");

		
		//create rope/door joint
		jointDef.bodyA = rope.getBody();
		jointDef.bodyB = body;
		anchorP = new Vector2(0, 0);
		anchorP.y = -rl/2;
		jointDef.localAnchorA.set(anchorP);
		anchorP.y = 0;
		anchorP.x = isLeft ? getWidth()/2f-linkSize : -getWidth()/2f+linkSize; 
		jointDef.localAnchorB.set(anchorP);
		jointDef.collideConnected = false;
		Joint joint1 = world.createJoint(jointDef);
		Down = joint1;
		System.out.println("Trap door creation pass out");
		return false;
	}
	
	public void setChildrenTexture(TextureRegion rope, TextureRegion nail){
		if (rope!=null)
			this.rope.setTexture(rope);
		if (anchorRope!=null)
			this.anchorRope.setTexture(nail);
	}
	
	@Override
	public void draw(GameCanvas canvas) {
		super.draw(canvas);
		if (anchorRope!=null)
			anchorRope.draw(canvas);
	}
	
	@Override
	public void drawDebug(GameCanvas canvas){
		super.drawDebug(canvas);
		if (anchorRope!=null)
			anchorRope.drawDebug(canvas);
	}
}
