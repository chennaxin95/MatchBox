package edu.cornell.gdiac.physics.editor;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;

public class ProjectModelJsonRep implements Json.Serializable{
	private int level = 1;
	
	private String background = "shared/background.png";
	private AidenModelJsonRep aiden;
	private ArrayList<BlockJsonRep> blocks;
	private ArrayList<WaterModelJsonRep> waters;
	private GoalModelJsonRep goal;
	
	public ProjectModelJsonRep (int level, String bg, AidenModelJsonRep aiden, GoalModelJsonRep goal){
		this.level = level;
		this.background = bg;
		this.aiden = aiden;
		this.goal = goal;
	}
	
	public ProjectModelJsonRep(AidenModelJsonRep aiden, ArrayList<BlockJsonRep> blocks, 
			ArrayList<WaterModelJsonRep> waters, GoalModelJsonRep goal){
		this.aiden = aiden;
		this.blocks = blocks;
		this.waters = waters;
		this.goal = goal;
	}

	public ProjectModelJsonRep(AidenModel inputAiden, ArrayList<BlockAbstract> inputBlocks, 
			ArrayList<CharacterModel> npcs, BlockAbstract inputGoal){
		if (inputAiden!=null) this.aiden=new AidenModelJsonRep(inputAiden);
		this.blocks=new ArrayList<BlockJsonRep>();
		this.waters=new ArrayList<WaterModelJsonRep>();
		int id=0;
		for (BlockAbstract block: inputBlocks){
			if (block!=inputGoal){
				blocks.add(new BlockJsonRep(block, id));
				id++;
			}
		}
		for (CharacterModel npc: npcs){
			waters.add(new WaterModelJsonRep(npc));
		}
		if (inputGoal!=null) this.goal =new GoalModelJsonRep(inputGoal);
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("level",level);
		json.writeValue("background", background);
		if (aiden!=null) json.writeValue("aiden",aiden);
		json.writeValue("blocks",blocks);
		json.writeValue("waters",waters);
		if (goal!=null) json.writeValue("goal",goal);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
