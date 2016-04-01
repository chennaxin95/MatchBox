package edu.cornell.gdiac.physics.ai;


import java.util.ArrayList;
import java.util.Random;

import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.GameEvent;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class AIController {

	private static final float MIN_WAITTIME=0.5f;
	private static final float MAX_WAITTIME=2f;
	
	private static final float MAX_SENSING_RADIUS=2f;
	
	public void nextMove(ArrayList<CharacterModel> npcs){
		for (CharacterModel npc:npcs){
			if (npc.canChangeMove()){
				GameEvent e=sensing(npc);
				npc.getStateMachine().transit(e);
			}
		}
		for (CharacterModel npc:npcs){
			if (npc.canChangeMove())
				computeMove(npc);
		}
	}
	public GameEvent sensing(CharacterModel npc){
		// check isSpawned
		GameEvent e=new GameEvent();
		e.setSpawned(npc.isSpawned()? 1: -1);
		// Check isCloseToFire
		ArrayList<Obstacle> close=SightDetector.detectObjectsInDistance(npc, MAX_SENSING_RADIUS);
		// Check hasSeenFire
		Obstacle o=SightDetector.detectObjectInSight(npc);
		if (o instanceof FlammableBlock && ((FlammableBlock)o).isBurning()){
			e.setSeenFire(1);
		}
		// Check hasSeenAiden
		if (o instanceof AidenModel){
			e.setSeenAiden(1);
		}
		// Check canFire
		e.setCanFire(npc.canFire()? 1: -1);
		
		return e;
	}
	
	private void computeMove(CharacterModel npc){
		Random r=new Random();
		switch (npc.getStateMachine().getCurrentState()){
		case SPAWN:
			// Still
			npc.setMovement(0f);
			break;
		case WANDER:
			float prob=r.nextFloat();
			// If is moving
			if (npc.getMovement()!=0){
				if (prob<0.5){
					// Still
					npc.setMovement(0);
				}
			}
			else{
				if (prob<0.4){
					// Move right
					npc.setMovement(1f*npc.getForce());
				}
				else if (prob<0.8){
					// Move left
					npc.setMovement(-1f*npc.getForce());
				}
				else{
					// Still
					npc.setMovement(0f);
				}	
			}
			break;
		default: assert(false);
				// Still
				npc.setMovement(0f);
				break;
		}
		npc.setMoveCoolDown(2);
//		npc.setMoveCoolDown(r.nextFloat()*(MAX_WAITTIME-MIN_WAITTIME)+MIN_WAITTIME);
	}
}
