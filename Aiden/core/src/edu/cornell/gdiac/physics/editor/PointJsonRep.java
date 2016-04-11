package edu.cornell.gdiac.physics.editor;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class PointJsonRep  implements Json.Serializable{
	private float x;
	private float y;
	public PointJsonRep(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void setX(float x){
		this.x = x;
	}
	
	public void setY(float y){
		this.y = y;
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("x",x);
		json.writeValue("y",y);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}
