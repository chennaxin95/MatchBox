package edu.cornell.gdiac.physics.ai;


import java.util.ArrayList;
import java.util.Random;

import edu.cornell.gdiac.physics.character.Character;
import edu.cornell.gdiac.physics.character.Character.BasicFSMState;
import edu.cornell.gdiac.physics.character.Character.Move;

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
			npc.setMove(Move.STAY);
			break;
		case WANDER:
			Random r=new Random();
			float prob=r.nextFloat();
			if (prob<0.3){
				npc.setMove(Move.LEFT);
				// Move right
			}
			else if (prob<0.6){
				npc.setMove(Move.RIGHT);	
			}
			else{
				npc.setMove(Move.STAY);
			}
			break;
		default: assert(false);
				 npc.setMove(Move.STAY);
				 break;
		}
	}
}
