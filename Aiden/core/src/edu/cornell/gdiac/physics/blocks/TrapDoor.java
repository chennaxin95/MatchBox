package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.ComplexObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

public class TrapDoor extends ComplexObstacle{
	public WheelObstacle anchor = null;
	public WheelObstacle anchorRope;
	public FlammableBlock rope;
	public float rw;
	public float rl;
	public Joint Top;
	public Joint Down;
	public Joint End;
	float posX;
	float end;
	Vector2 doorPos;
	public boolean isLeft;
	
	public TrapDoor(float x, float y, float width, float height, boolean isLeft){
		super(x,y);
		rw = height;
		rl = width;
		this.isLeft = isLeft;
		StoneBlock sb = new StoneBlock(x,y,width,height);
		end = isLeft?width/2f:-width/2f;
		doorPos = new Vector2(x,y);
		sb.setDensity(1f); sb.setName("door");
		bodies.add(sb);
		rope = new FlammableBlock(x-end*1.1f, y+rl/2, rw, rl, 2, 2);
		rope.setDensity(1f); rope.setName("rope");
		bodies.add(rope);
	}
	
	
	
	public boolean createJoints(World world){
		//anchor joint
		Vector2 pos = new Vector2(doorPos.x+end, this.getY());
		anchor = new WheelObstacle(pos.x, pos.y, rw/2f);
		anchor.setName("anchor");
		anchor.setDensity(1f);
		anchor.setDrawScale(this.drawScale);
		anchor.setBodyType(BodyDef.BodyType.StaticBody);
		anchor.activatePhysics(world);

		Vector2 anchorP = new Vector2(0,0);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.bodyB = bodies.get(0).getBody();
		jointDef.bodyA = anchor.getBody();
		jointDef.localAnchorA.set(anchorP);
		anchorP.x = end;
		jointDef.localAnchorB.set(anchorP);		
		jointDef.collideConnected = false;
		End = world.createJoint(jointDef);
		joints.add(End);
		
		//rope stone 
		jointDef = new RevoluteJointDef();
		jointDef.bodyB = bodies.get(0).getBody();
		jointDef.bodyA = rope.getBody();
		anchorP.x = -end;
		jointDef.localAnchorB.set(anchorP);	
		anchorP.x = 0;
		anchorP.y = -rl/2;
		jointDef.localAnchorA.set(anchorP);
		jointDef.collideConnected = false;
		Down = world.createJoint(jointDef);
		joints.add(Down);
		
		//Rope top
		anchorRope = new WheelObstacle(rope.getPosition().x, pos.y+rl, rw/2f);
		anchorRope.setName("anchorRope"); anchorRope.setDensity(1f);
		anchorRope.setDrawScale(this.drawScale);
		anchorRope.setBodyType(BodyDef.BodyType.StaticBody);
		anchorRope.activatePhysics(world);
		jointDef = new RevoluteJointDef();
		jointDef.bodyB = anchorRope.getBody();
		jointDef.bodyA = rope.getBody();
		anchorP.y = 0;
		jointDef.localAnchorB.set(anchorP);	
		anchorP.x = 0;
		anchorP.y = rl/2;
		jointDef.localAnchorA.set(anchorP);
		jointDef.collideConnected = false;
		Top = world.createJoint(jointDef);
		joints.add(Top);
		
		return true;
	}
	
	public boolean updateParts(World world){
		if (bodies.size == 1){
			return false;
		}
		if(((FlammableBlock) bodies.get(1)).isBurnt()){
			world.destroyBody(bodies.get(1).getBody());
			bodies.removeIndex(1);
			return false;
		}
		return false;
	}
	
	
	public void setChildrenTexture(TextureRegion door, TextureRegion rope, TextureRegion nail){
		((StoneBlock) bodies.get(0)).setTexture(door);;
		if (this.rope!=null && rope!=null){
			this.rope.setTexture(rope);
			this.rope.setDrawScale(this.drawScale);
		}
		if (this.anchorRope!=null && nail!=null){
			this.anchorRope.setTexture(nail);
			anchorRope.setDrawScale(this.drawScale);
		}
		if (this.anchor!=null && nail!=null){
			this.anchor.setTexture(nail);
			anchor.setDrawScale(this.drawScale);
		}
	}
	
	@Override
	public void draw(GameCanvas canvas) {
//		super.draw(canvas);
		((StoneBlock) bodies.get(0)).draw(canvas);
		if(!rope.isBurnt()){
			rope.draw(canvas);
		}	
		if (anchor != null){
			anchor.draw(canvas);
		}
		if (anchorRope!=null){
			anchorRope.draw(canvas);
		}
	}
	
	@Override
	public void drawDebug(GameCanvas canvas, Color c){
		super.drawDebug(canvas);
		anchor.drawDebug(canvas);
		if (anchorRope!=null){
			anchorRope.drawDebug(canvas);
		}
			
	}

	public Vector2 getPos(){
		return doorPos;
	}

	public float getWidth() {
		// TODO Auto-generated method stub
		return 4;
	}



	public float getHeight() {
		// TODO Auto-generated method stub
		return 0.25f;
	}



	@Override
	public Rectangle getBoundingBox() {
		// TODO Auto-generated method stub
		Vector2 pos=((StoneBlock) bodies.get(0)).getPosition();
		
		return new Rectangle(pos.x-this.getWidth()/2f, 
				pos.y-this.getHeight()/2f,
				this.getWidth(),
				this.getHeight());
	}
}
