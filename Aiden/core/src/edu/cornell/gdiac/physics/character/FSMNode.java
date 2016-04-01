package edu.cornell.gdiac.physics.character;

import java.util.ArrayList;



public class FSMNode{
	public enum BasicFSMState {
		SPAWN, WANDER, ESCAPE, CHASE, ATTACK
	}
	
	BasicFSMState state;
	ArrayList<FSMNode> nexts;
	ArrayList<GameEvent> triggers;
	public FSMNode(BasicFSMState s){
		state=s;
		nexts=new ArrayList<FSMNode>();
		triggers=new ArrayList<GameEvent>();
	}
	public void addNext(FSMNode n, GameEvent t){
		nexts.add(n);
		triggers.add(t);
	}
}
