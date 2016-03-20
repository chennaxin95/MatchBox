package edu.cornell.gdiac.physics.ai;


import java.util.ArrayList;
import java.util.Random;

import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.BasicFSMState;

public class AIController {

	private static final float MIN_WAITTIME=0.5f;
	private static final float MAX_WAITTIME=2f;
	
	public AIController(){
	}
	
	public void nextMove(ArrayList<CharacterModel> npcs){
		for (CharacterModel npc:npcs){
			if (npc.canChangeMove()){
				updateState(npc);
				computeMove(npc);
			}
		}
	}
	
	private void updateState(CharacterModel npc){
		switch (npc.getState()){
		case SPAWN:
			if (npc.isSpawned()) npc.setState(BasicFSMState.WANDER);
			break;
		case WANDER:
			break;
		default: assert(false); break;
		}
	}
	public void computeMove(CharacterModel npc){
		Random r=new Random();
		switch (npc.getState()){
		case SPAWN:
			// Still
			npc.setMovement(0f);;
			break;
		case WANDER:
			float prob=r.nextFloat();
			if (prob<0.3){
				// Move right
				npc.setMovement(1f*npc.getForce());;
			}
			else if (prob<0.6){
				// Move left
				npc.setMovement(-1f*npc.getForce());;
			}
			else{
				// Still
				npc.setMovement(0f);;
			}
			break;
		default: assert(false);
				// Still
				npc.setMovement(0f);;
				break;
		}
		npc.setMoveCoolDown(r.nextFloat()*(MAX_WAITTIME-MIN_WAITTIME)+MIN_WAITTIME);
	}
}
