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
	
	
	@Override
	public boolean activatePhysics(World world){
		float end;
		if(isLeft){
			end = getX() + getWidth()/2f;
		}
		else{
			end = getX() - getWidth()/2f;
		}
		super.activatePhysics(world);
		System.out.println("Trap door creation pass 1"+ rw+" "+rl);
		rope = new FlammableBlock(end, getY()+rl/2f, rw, rl, 3, 3);
		rope.activatePhysics(world);
		rope.setDensity(1);
		this.setDensity(1);
		return true;
		//Create the side anchor
	}
	
	public void createJoints(World world){
		float end;
		if(isLeft){
			posX = getX() - getWidth()/2f;
			end = getX() + getWidth()/2f;
		}
		else{
			posX = getX() + getWidth()/2f;
			end = getX() - getWidth()/2f;
		}
		System.out.println("Trap door creation pass in");
		Vector2 pos = new Vector2(posX, this.getY());
		anchor = new WheelObstacle(pos.x, pos.y, rw/2f);
		anchor.setName("anchor");
		anchor.setDensity(1);
		anchor.setBodyType(BodyDef.BodyType.StaticBody);
		anchor.activatePhysics(world);
		System.out.println("Trap door creation pass 1");

		Vector2 anchorP = new Vector2();
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = this.getBody();
		jointDef.bodyB = anchor.getBody();
		anchorP = new Vector2(0, 0);
		anchorP.x = isLeft?-getWidth()/2f:getWidth()/2f;
		jointDef.localAnchorA.set(anchorP);		
		anchorP = new Vector2(0, 0);
		jointDef.localAnchorB.set(anchorP);
		jointDef.collideConnected = true;
		Top = world.createJoint(jointDef);
		System.out.println("Trap door creation pass 2");
		
		//create rope/door joint
		RevoluteJointDef jointDef1 = new RevoluteJointDef();
		jointDef1.bodyA = rope.getBody();
		jointDef1.bodyB = this.body;
		anchorP = new Vector2(0, 0);
		anchorP.y = -rl/2f;
		jointDef1.localAnchorA.set(anchorP);
		anchorP.y = 0;
		anchorP.x = isLeft ? getWidth()/2f : -getWidth()/2f; 
		jointDef1.localAnchorB.set(anchorP);
		jointDef1.collideConnected = true;
		Down = world.createJoint(jointDef1);
		System.out.println("Trap door creation pass 3");

		
		//anchor rope joint
		pos.x = end;
		pos.y = getY()+rl;
		anchorRope = new WheelObstacle(pos.x, pos.y, rw/2f);
		anchorRope.setName(this.getName()+"anchorRope");
		anchorRope.setDensity(1);
		anchorRope.setBodyType(BodyDef.BodyType.StaticBody);
		anchorRope.activatePhysics(world);
		System.out.println("Trap door creation pass 4");
		
		RevoluteJointDef jointDef2 = new RevoluteJointDef();
		jointDef2.bodyA = rope.getBody();
		jointDef2.bodyB = anchorRope.getBody();
		anchorP = new Vector2(0, 0);

		anchorP.y = rl/2;
		jointDef2.localAnchorA.set(anchorP);
		anchorP.y = 0;
		anchorP.x = 0; 
		jointDef2.localAnchorB.set(anchorP);
		jointDef2.collideConnected = true;
		End = world.createJoint(jointDef2);
		System.out.println("Trap door creation pass 5");
	}
	
	public void setChildrenTexture(TextureRegion rope, TextureRegion nail){
		if (this.rope!=null){
			this.rope.setTexture(rope);
			this.rope.setDrawScale(this.drawScale);
		}
		if (this.anchorRope!=null){
			this.anchorRope.setTexture(nail);
			anchorRope.setDrawScale(this.drawScale);
		}
		if (this.anchor!=null){
			this.anchor.setTexture(nail);
			anchor.setDrawScale(this.drawScale);
		}
	}
	
	@Override
	public void draw(GameCanvas canvas) {
		super.draw(canvas);
		if (anchorRope!=null){
			anchor.draw(canvas);
			anchorRope.draw(canvas);
		}
	}
	
	@Override
	public void drawDebug(GameCanvas canvas){
		super.drawDebug(canvas);
		if (anchorRope!=null){
			anchorRope.drawDebug(canvas);
			anchor.drawDebug(canvas);
		}
			
	}
}
