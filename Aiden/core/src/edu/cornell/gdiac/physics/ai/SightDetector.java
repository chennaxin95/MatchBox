package edu.cornell.gdiac.physics.ai;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.StoneBlock;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.Scene;

public class SightDetector {
	public static final float FOV=0.1f;
	public static final float UNIT_ANGLE=0.01f;
	public static final float WHOLE_FOV=(float) Math.PI;
	
	public ArrayList<IntersectionRecord> detectObjectInSight(CharacterModel npc, 
			float fov, Scene scene, ArrayList<Obstacle> objs /* Temp*/){
		return detectObjectInSight(npc, npc.getFacingDir()? 1: -1, fov, scene, objs);
	}
	
	public ArrayList<IntersectionRecord> detectObjectInSight(CharacterModel npc, 
			int facingDir, float fov, Scene scene, ArrayList<Obstacle> objs/*Temp*/){
		facingDir=facingDir>0? 1: ((facingDir<0)? -1: 0);
		Ray[] rays=new Ray[(int) (2*fov/UNIT_ANGLE)+1];
		Obstacle[] intersections=new Obstacle[rays.length];
		float[] dists=new float[rays.length];
		for (int i=0; i<rays.length; i++){
			rays[i]=new Ray();
			Vector3 dir=new Vector3();
			float angle=fov-i*UNIT_ANGLE;
			if (Math.abs(angle)==Math.PI/2f){
				dir.set(0, angle>0? 1: -1, 0);
			}
			else{
				dir=dir.set((facingDir==0? 1: facingDir)*(float)Math.cos(angle),
						(float)Math.sin(angle), 0).nor();
			}
			rays[i].set(new Vector3(npc.getEyePosition().x, 
					npc.getEyePosition().y, 0), dir);
			dists[i]=Float.MAX_VALUE;
			intersections[i]=null;
		}		

		// Loop over all objects in scene, including characters
		for (Obstacle obstacle: objs){
			
			// Skip if being itself
			if (obstacle.equals(npc)) continue;
			// Ray-box intersection test
			float lx=obstacle.getPosition().x;
			float ux=obstacle.getPosition().x;
			float ly=obstacle.getPosition().y;
			float uy=obstacle.getPosition().y;
			if (obstacle instanceof BlockAbstract){
				lx-=((BlockAbstract)obstacle).getWidth()/2f;
				ux+=((BlockAbstract)obstacle).getWidth()/2f;
				ly-=((BlockAbstract)obstacle).getHeight()/2f;
				uy+=((BlockAbstract)obstacle).getHeight()/2f;
			}
			else if (obstacle instanceof CharacterModel){
				lx-=((CharacterModel)obstacle).getWidth()/2f;
				ux+=((CharacterModel)obstacle).getWidth()/2f;
				ly-=((CharacterModel)obstacle).getHeight()/2f;
				uy+=((CharacterModel)obstacle).getHeight()/2f;
			}
			// Keep track of closest intersected object so far.
			for (int k=0; k<rays.length; k++){
				float distance=intersectBox(rays[k].cpy(), lx, ux, ly, uy);
				
				if (distance>0 && distance < dists[k]){
					dists[k] = distance; 
					intersections[k]=obstacle;
				}
			}
		}
		
		ArrayList<IntersectionRecord> results=new ArrayList<IntersectionRecord>();
		for (int i=0; i<rays.length; i++){
			if (intersections[i]!=null) {
				results.add(new IntersectionRecord(intersections[i], dists[i]));
			}
		}
		return results;
	}
		
	public float intersectBox(Ray ray, float lx, float ux, float ly, float uy){
		assert(lx<ux && ly<uy);
		ray.direction.nor();
		if (ray.direction.x!=0 && ray.direction.y!=0){
			boolean f=false;
			float bestDist=Float.MAX_VALUE;
			float [] ts=new float[]{(lx-ray.origin.x)/ray.direction.x,
									(ly-ray.origin.y)/ray.direction.y,
									(ux-ray.origin.x)/ray.direction.x,
									(uy-ray.origin.y)/ray.direction.y};
			for (int ind=0; ind<4; ind++){
				if (ts[ind]<=0) continue;
				Vector3 p = new Vector3();
				ray.cpy().getEndPoint(p, ts[ind]);
				if ((ind%2==0 && p.y <= uy && p.y >= ly) || 
						(ind%2==1 && p.x <= ux && p.x >= lx)){
					f=true;
					if (ts[ind]<bestDist) bestDist=ts[ind];
				}
			}
			return f? bestDist: -1;
		}
		else{
			float ut=-1, lt=-1;
			if (ray.direction.x==0){
				if (ray.origin.x >= lx && ray.origin.x<=ux){
					ut=(uy-ray.origin.y)/ray.direction.y;
					lt=(ly-ray.origin.y)/ray.direction.y;
				}
			}
			else {
				if (ray.origin.y >= ly && ray.origin.y<=uy){
					ut=(ux-ray.origin.x)/ray.direction.x;
					lt=(lx-ray.origin.x)/ray.direction.x;
				}
			}
			if (lt>0 && ut>0){
				return Math.min(ut, lt);
			}
			if (ut>0){
				return ut;
			}
			if (lt>0){
				return lt;
			}
			return -1;
		}
	}

	public void drawDebug(GameCanvas canvas, Vector2 eyePos, int facingDir, Vector2 drawScale, float fov){
		float[] pts=new float[]{0, 0, facingDir, (float) (facingDir*Math.tan(fov)),
				facingDir, (float) (-facingDir*Math.tan(fov))};
		PolygonShape poly=new PolygonShape();
		poly.set(pts);
		canvas.drawPhysics(poly, Color.PINK, eyePos.x, eyePos.y, 0, drawScale.x, drawScale.y);
	}
	
	public class IntersectionRecord{
		public float t;
		public Obstacle obj;
		public IntersectionRecord(Obstacle obj, float t){
			this.obj=obj;
			this.t=t;
		}
	}
}

