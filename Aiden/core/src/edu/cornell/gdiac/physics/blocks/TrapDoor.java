package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
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
		float end;
		if(isLeft){
			end = getX() + getWidth()/2f;
		}
		else{
			end = getX() - getWidth()/2f;
		}
		super.activatePhysics(world);
		rope = new FlammableBlock(end, getY()+rl/2, rw, rl, 3, 3);
		rope.activatePhysics(world);
		rope.setDensity(1);
		this.setDensity(1);
		return false;
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
		Vector2 pos = new Vector2(posX, this.getY());
		anchor = new WheelObstacle(pos.x, pos.y, rw/2);
		anchor.setName("anchor");
		anchor.setDensity(1);
		anchor.setBodyType(BodyDef.BodyType.StaticBody);
		anchor.activatePhysics(world);
		
		Vector2 anchorP = new Vector2();
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyA = this.getBody();
		jointDef.bodyB = anchor.getBody();
		anchorP = new Vector2(0, 0);
//		anchorP.x = isLeft?-getWidth()/2:getWidth()/2;
		jointDef.localAnchorA.set(anchorP);
		anchorP = new Vector2(0, 0);
		jointDef.localAnchorB.set(anchorP);
		jointDef.collideConnected = false;
		Top = world.createJoint(jointDef);
		
		//create rope/door joint
//		jointDef.bodyA = rope.getBody();
//		jointDef.bodyB = this.body;
//		anchorP = new Vector2(0, 0);
//		anchorP.y = -rl/2;
//		jointDef.localAnchorA.set(anchorP);
//		anchorP.y = 0;
//		anchorP.x = isLeft ? getWidth()/2 : -getWidth()/2; 
//		jointDef.localAnchorB.set(anchorP);
//		jointDef.collideConnected = false;
//		Down = world.createJoint(jointDef);
		
		//anchor rope joint
		pos.x = end;
		pos.y = getY()+rl;
		anchorRope = new WheelObstacle(pos.x, pos.y, rw/2);
		anchorRope.setName(this.getName()+"anchorRope");
		anchorRope.setDensity(1);
		anchorRope.setBodyType(BodyDef.BodyType.StaticBody);
		anchorRope.activatePhysics(world);
		RevoluteJointDef jointDef1 = new RevoluteJointDef();
		jointDef1.bodyA = rope.getBody();
		jointDef1.bodyB = anchorRope.getBody();
		anchorP = new Vector2(0, 0);
		anchorP.y = rl/2;
		jointDef1.localAnchorA.set(anchorP);
		anchorP.y = 0;
		anchorP.x = 0; 
		jointDef1.localAnchorB.set(anchorP);
		jointDef1.collideConnected = false;
		End = world.createJoint(jointDef1);
	}
	
	public void setChildrenTexture(TextureRegion rope, TextureRegion nail){
		this.rope.setTexture(rope);
		this.rope.setDrawScale(this.drawScale);
		this.anchorRope.setTexture(nail);
		anchorRope.setDrawScale(this.drawScale);
		this.anchor.setTexture(nail);
		anchor.setDrawScale(this.drawScale);
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
