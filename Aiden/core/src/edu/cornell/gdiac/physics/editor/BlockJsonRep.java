package edu.cornell.gdiac.physics.editor;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.Rope;
import edu.cornell.gdiac.physics.blocks.TrapDoor;

public class BlockJsonRep implements Json.Serializable{
	
	public int id = 0;
	
	public String blockType = "wood";
	public String texture = "shared/woodenBlock.png";
	
	public PointJsonRep pos = new PointJsonRep(1,1);
	
	public float scale_x = 1;
	public float scale_y = 1;
	
	public float density = 1;
	
	public boolean fixed = false;
	public boolean linked = false;
	
	public PointJsonRep link_pos = new PointJsonRep(0,0);
	public int link_obj = 0;
	
	public boolean burning = false;
	public float burn_spread = 5;
	public float burn_time = 3;
	public float burn_left = 1;
	public float fuels = 30;
	
	public boolean isLeft=false;
	public int segments=0;
	
	public BlockJsonRep(BlockAbstract block, int id){
		if (block==null) return;
		this.id = id;
		switch (block.type){
		case FLAMMABLEBLOCK:
			blockType = "wood";
			break;
		case FUEL:
			blockType = "fuel";
			break;
		case PLATFORM:
			blockType = "platform";
			fixed=true;
			break;
		case BURNABLE_PLATFORM:
			blockType = "burnable_platform";
			fixed=true;
			break;
		case TRAPDOOR:
			blockType = "trapdoor";
			isLeft=((TrapDoor)block).isLeft;
			break;
		case STONE:
			blockType = "stone";
			break;
		default:
			break;
		}
		pos = new PointJsonRep(block.getX(), block.getY());
		scale_x=block.getWidth();
		scale_y=block.getHeight();
	}
	
	public BlockJsonRep(Rope rope, int id){
		if (rope==null) return;
		this.id = id;
		blockType = "rope";
		pos = new PointJsonRep(rope.getX(),  rope.getY());
		scale_x=rope.getWidth();
		scale_y=rope.getUnitHeight();
		segments=rope.getSegments();
	}
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("id", id);
		json.writeValue("blockType",blockType);
		json.writeValue("texture",texture);
		json.writeValue("pos", pos);
		json.writeValue("scale_x",scale_x);
		json.writeValue("scale_y",scale_y);
		json.writeValue("density",density);
		json.writeValue("fixed",fixed);
		json.writeValue("linked",linked);
		json.writeValue("link_pos",link_pos);
		json.writeValue("burning", burning);
		json.writeValue("burn_spread", burn_spread);
		json.writeValue("burn_time",burn_time);
		json.writeValue("burn_left", burn_left);
		json.writeValue("fuels",fuels);
		json.writeValue("isLeft", isLeft);
		json.writeValue("segments", segments);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
	
}
