package edu.cornell.gdiac.physics.ai;


import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.ai.NavBoard.NavTile;
import edu.cornell.gdiac.physics.ai.NavBoard.TileType;
import edu.cornell.gdiac.physics.ai.SightDetector.IntersectionRecord;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.GameEvent;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.Scene;
import edu.cornell.gdiac.util.PooledList;

public class AIController {
	private SightDetector sightDetector;
	private PathFinder pathFinder;
	
	private float unitX, unitY;
	
	NavBoard board;
	PooledList<Obstacle> objs; //Temp
	Scene scene; //Temp
	
	ArrayList<IntersectionRecord> detected;
	ArrayList<IntersectionRecord> close;
	
	private static final float MIN_WAITTIME=0.5f;
	private static final float MAX_WAITTIME=2f;
	
	private static final float MAX_SENSING_RADIUS=2f;
	private static final float MAX_ATTACKING_RADIUS=1f;
	
	public AIController(Scene scene, float lx, float ly, float ux, 
			float uy, float unitX, float unitY, PooledList<Obstacle> objects){
		this.scene=scene;
		board=new NavBoard(lx, ly, ux, uy, unitX, unitY);
		this.unitX=unitX;
		this.unitY=unitY;
		this.objs=objects;
		sightDetector=new SightDetector();
		pathFinder=new PathFinder();
	}
	
	public void nextMove(ArrayList<CharacterModel> npcs){
		for (CharacterModel npc:npcs){
			if (npc.canChangeMove()){
				GameEvent e=sensing(npc);
				npc.getStateMachine().transit(e);
			}
		}
		for (CharacterModel npc:npcs){
			if (npc.canChangeMove()){
				board.setupBoard(new ArrayList<Obstacle>(objs)); // Temp
				computeMove(npc);
			}
		}
	}
	
	private int pointer=0; //temp
	public GameEvent sensing(CharacterModel npc){
		// Must set all the fields of game event
		
		// check isSpawned
		GameEvent e=new GameEvent();
		e.setSpawned(npc.isSpawned()? 1: -1);
		
		// Check isCloseToFire
		close=sightDetector.detectObjectInSight(npc, 0, SightDetector.WHOLE_FOV, 
				scene, new ArrayList<Obstacle>(objs));
		
		// Check hasSeenFire
		// Check hasSeenAiden; same thing as hasSeenFire?
		detected=sightDetector.detectObjectInSight(npc, SightDetector.FOV, 
				scene, new ArrayList<Obstacle>(objs));
		for (IntersectionRecord inter: detected){
			if ((inter.obj instanceof FlammableBlock && ((FlammableBlock)inter.obj).isBurning() )
					|| inter.obj instanceof AidenModel){
				e.setSeenFire(1);
				npc.setTarget(inter.obj.getPosition());
			}
		}
		if (e.hasSeenFire()==0) {
			e.setSeenFire(-1);
			e.setSeenAiden(-1);
		}
//		Vector2[] targets=new Vector2[]{new Vector2(15,2), new Vector2(10,2)};
//		if (npc.getPosition().cpy().sub(targets[pointer]).len()<2){
//			pointer++;
//			pointer%=2;
//		}
//		npc.setTarget(targets[pointer]);

		// Check canFire
//		e.setCanFire(npc.canFire()? 1: -1);
		e.setCanFire(-1);
		return e;
	}
	
	private void computeMove(CharacterModel npc){
		Random r=new Random();
		System.out.println(npc.getStateMachine().getCurrentState());
		float lx=npc.getPosition().x-npc.getWidth()/2;
		float ly=npc.getPosition().y-npc.getHeight()/2;			
		float ux=npc.getPosition().x+npc.getWidth()/2;
		float uy=npc.getPosition().y+npc.getHeight()/2;
		Vector2 lInd=board.convertToBoardCoord(new Vector2(lx, ly));
		Vector2 uInd=board.convertToBoardCoord(new Vector2(ux, uy));
		Vector2 start=board.castAround(board.convertToBoardCoord(npc.getPosition()));
		Vector2 move=new Vector2();
		switch (npc.getStateMachine().getCurrentState()){
		case SPAWN:
			// Still
			npc.setMovement(0f);
			break;
		case WANDER:
			if (npc.getMovement()!=0 && r.nextFloat()<0.3f){
				npc.setMovement(0);
				break;
			}
			if (npc.getMovement()!=0){
				Vector2 temp=new Vector2(start.x+(npc.getMovement()>0? 2: -2), start.y);
				NavTile forward=board.getTile(board.convertToBoardCoord(temp));
				if (forward==null || !forward.isSafeToWalkOn()) {
					npc.setMovement(0);
				}
				break;
			}
			NavTile left=board.getTile(board.convertToBoardCoord(new Vector2(start.x-2, start.y)));
			NavTile right=board.getTile(board.convertToBoardCoord(new Vector2(start.x+2, start.y)));
			if ((left!=null && left.isSafeToWalkOn()) && (right!=null && right.isSafeToWalkOn())){
				if (r.nextFloat()>0.5) npc.setMovement(25f);
				else npc.setMovement(-50f);
			}
			else if (left!=null && left.isSafeToWalkOn()){
				npc.setMovement(-50f);
			}
			else if (right!=null && right.isSafeToWalkOn()){
				npc.setMovement(50f);
			}
			else {
				if (r.nextFloat()<0.3f) npc.turnAround();
			}
//			float prob=r.nextFloat();
//			// If is moving
//			if (npc.getMovement()!=0){
//				if (prob<0.5){
//					// Still
//					npc.setMovement(0);
//				}
//			}
//			else{
//				if (prob<0.4){
//					// Move right
//					npc.setMovement(1f*npc.getForce());
//				}
//				else if (prob<0.8){
//					// Move left
//					npc.setMovement(-1f*npc.getForce());
//				}
//				else{
//					// Still
//					npc.setMovement(0f);
//				}	
//			}
			break;
		case CHASE:
//			Vector2 start=new Vector2(-1, -1);
//			for (int i=(int) lInd.x; i<=uInd.x; i++){
//				for (int j=(int) lInd.y; j<=uInd.y; j++){
//					NavTile tile=board.getTile(new Vector2(i,j));
//					if (tile!=null && tile.type!=TileType.NONE &&
//							tile.type!=TileType.DANGER && tile.type!=TileType.SUPPORT){
//						start.x=i; 
//						start.y=j;
//						break;
//					}
//				}
//			}
			Vector2 target=board.convertToBoardCoord(npc.getTarget());
			int radius=(int) (MAX_ATTACKING_RADIUS/unitX);
			for (int dx=-radius; dx<=radius; dx++){
					board.getTile(board.castAround(new Vector2(target.x+dx, target.y)))
						.markAsTarget();
			}
					
			move=pathFinder.findPath(board, board.converToWorldCoord(start));		
//			float far=Math.min(npc.getTarget().dst(npc.getPosition()), 1f)/1f;
			if (move.x>0) npc.setMovement(100f/*npc.getForce()*/);
			if (move.x==0) npc.setMovement(0);
			if (move.x<0) npc.setMovement(-100f/*npc.getForce()*/);
			break;
		default: assert(false);
				// Still
				npc.setMovement(0f);
				break;
		}
		npc.setMoveCoolDown(0.5f);
//		npc.setMoveCoolDown(r.nextFloat()*(MAX_WAITTIME-MIN_WAITTIME)+MIN_WAITTIME);
	}
	
}
