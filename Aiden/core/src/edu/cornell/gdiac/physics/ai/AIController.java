package edu.cornell.gdiac.physics.ai;


import java.util.ArrayList;
import java.util.Random;

import edu.cornell.gdiac.physics.character.Character;
import edu.cornell.gdiac.physics.character.Character.BasicFSMState;

public class AIController {
	private ArrayList<Character> npcs;
	public void nextMove(){
		for (Character npc:npcs){
			updateState(npc);
			
		}
	}
	public void updateState(Character npc){
		switch (npc.getState()){
		case SPAWN:
			if (npc.isSpawned()) npc.setState(BasicFSMState.WANDER);
			break;
		case WANDER:
			break;
		default: assert(false); break;
		}
	}
	public void computeMove(Character npc){
		switch (npc.getState()){
		case SPAWN:
			// Still
			npc.setMovement(0f);;
			break;
		case WANDER:
			Random r=new Random();
			float prob=r.nextFloat();
			if (prob<0.3){
				// Move right
				npc.setMovement(1f);;
			}
			else if (prob<0.6){
				// Move left
				npc.setMovement(-1f);;
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
	}
}
