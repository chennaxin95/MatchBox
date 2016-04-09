package edu.cornell.gdiac.physics.character;

import java.util.ArrayList;

import edu.cornell.gdiac.physics.character.FSMNode.BasicFSMState;
import edu.cornell.gdiac.util.RandomController;

public class FSMGraph {
	
	private FSMNode current;
	
	public FSMGraph(FSMNode n){
		current=n;
	}
	
	public void transit(GameEvent e){
		ArrayList<Integer> choices=new ArrayList<Integer>();
		for (int i=0; i<current.nexts.size(); i++){
			if (e.equals(current.triggers.get(i))){
				choices.add(i);
			}
		}
		if (choices.size()>0){
			int id=RandomController.rollInt(0, choices.size()-1);
			current=current.nexts.get(choices.get(id));
		}
	}
	
	public BasicFSMState getCurrentState(){
		return current.state;
	}
}
