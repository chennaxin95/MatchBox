package edu.cornell.gdiac.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;

import edu.cornell.gdiac.physics.scene.JSONParser;

class TutorialController extends AidenController{
	private int tutmsg_s;
	private Message[] messages;
	// currentMsg is the msg index, is -1 when there is no task
	// larger than 0 when a task started until the user completes the task
	private int currentMsg ;
	boolean read = false;
	boolean tutpause = false;

	public TutorialController(int level) {
		super(level);
		// TODO Auto-generated constructor stub
		switch (level){
		case 0:
			this.messages = parseJson("json/tutorial1message.json");
			break;
		case 1:
			this.messages = parseJson("json/tutorial2message.json");
			break;
		case 2:
			this.messages = parseJson("json/tutorial3message.json");
			break;
		case 3:
			this.messages = parseJson("json/tutorial4message.json");
			break;
		default:
			this.messages = parseJson("json/tutorial1message.json");
		}
		currentMsg = -1;
		tutmsg_s = -1;
	}


	private Message[] parseJson(String s){
		JSONParser jp = new JSONParser(s);
		JsonValue jv = jp.getJsonValue();
		JsonValue messages = jv.get("messages");
		int n = messages.size;
		Message[] msgs =  new Message[n];
		for(int i = 0; i<n; i++){
			JsonValue m = messages.get(i);
			String type = m.getString("type");
			int msg = m.getInt("message");
			int end_s = m.getInt("end_message");
			
			if(type.equals("position")){
				JsonValue pos = m.get("position");
				JsonValue end_pos = m.get("end_pos");
				float end_x = end_pos.getFloat("x");
				float end_y = end_pos.getFloat("y");
				float msg_x = pos.getFloat("x");
				float msg_y = pos.getFloat("y");
				msgs[i] = new PositionMessage(msg, msg_x, msg_y, end_x, end_y, end_s);
			}else{
				float fuel = m.getFloat("fuel");
				float end_f = m.getFloat("end_fuel");
				msgs[i] = new FuelMessage(msg, fuel, end_f, end_s);
			}
		}
		return msgs;
	}

	public void reset(){
		super.reset();
		read = false;
		for (Message m: messages) m.setUndo();
		tutpause = false;
	}
	
	public void update(float dt){
		super.update(dt);
		if(beginCamFrame < 150) return;
		if(level == 0) avatar.keepFuel = true;
		//System.out.println(avatar.getX()+" "+avatar.getY());
		int i = 0;
		// when we are not in a task, check if we should start any task
		while(!this.pause && i < this.messages.length && !read){
			Message m = messages[i];
			// if the task is already done, skip it
			if(m.getDone()) {
				i++;
				//System.out.println("task done");
				continue;
			}

			if(m instanceof PositionMessage){
				//System.out.println(level+" "+avatar.getX() +" "+avatar.getY());
				if(Math.abs(avatar.getX() - ((PositionMessage) m).getX()) < 1
						&& Math.abs(avatar.getY() - ((PositionMessage) m).getY()) < 3){
					//System.out.println("task1 here!!!!!!");
					
					this.currentMsg = i;
					this.tutmsg_s = m.getMsg();
					this.tutpause = true;
					this.pause();
				}
			}else{

				//System.out.println(avatar.getX() +" "+avatar.getY());
				//System.out.println(avatar.getFuel());

				if(avatar.getFuel() < ((FuelMessage) m).getFuel()){
					//System.out.println("task2 here!!!!!!");
					
					this.currentMsg = i;
					this.tutmsg_s = m.getMsg();
					this.tutpause = true;
					this.pause();
				}
			}
			i++;
		}
		// if we are in a task, check if the task is completed
		if(this.currentMsg>-1){
			Message m = messages[currentMsg];
			if(m instanceof PositionMessage){
				float end_x = ((PositionMessage) m).getEndX();
				float end_y = ((PositionMessage) m).getEndY();	
				if(Math.abs(end_x - avatar.getX())<1 && Math.abs(end_y - avatar.getY())<3){
					messages[currentMsg].setDone();
					this.tutmsg_s = m.getEndMsg();
					this.tutpause = true;
					this.pause();
					//System.out.println("task "+currentMsg+" completed");
					this.currentMsg = -1;
					this.read = false;
					
				}
			}else{
				float end_f  = ((FuelMessage) m).getEndFuel();
				if(avatar.getFuel() > end_f){
					//System.out.println("task "+currentMsg+" completed");
					messages[currentMsg].setDone();
					this.tutmsg_s = m.getEndMsg();
					this.tutpause = true;
					this.pause();
					this.read = false;
					this.currentMsg = -1;
				}

			}
		}
		// when we are displaying a task, check if the user pressed enter to dismiss the msg
		if(this.pause && Gdx.input.isKeyPressed(Input.Keys.ENTER)){
			this.pause();
			this.tutpause = false;
			if(this.currentMsg>-1){
				if(messages[currentMsg].msg_end_s!=-1){
					this.read = true;
					this.tutmsg_s = -1;
				}
			}
		}
//		System.out.println(tutpause);
	}
	
	public int getMsgString(){
		return this.tutmsg_s;
	}

	public int getCurrentMsg(){
		return this.currentMsg;
	}

}

abstract class Message{
	protected int msg_s;
	protected int msg_end_s;

	private boolean msg_done;

	public Message(int s, int end_s){
		this.msg_s = s;
		this.msg_done = false;
		this.msg_end_s = end_s;
	}
	
	public int getMsg(){return this.msg_s;}
	public int getEndMsg(){return this.msg_end_s;}

	public void setDone(){this.msg_done = true;}

	public boolean getDone(){return this.msg_done;}

	public void setUndo(){this.msg_done = false;}
}

class PositionMessage extends Message{
	private float msg_x;
	private float msg_y;
	private float end_x;
	private float end_y;

	public PositionMessage(int s, float x, float y, float end_x, float end_y, int end_s){
		super(s,  end_s);
		this.msg_x = x;
		this.msg_y = y;
		this.end_x = end_x;
		this.end_y = end_y;
	}

	public float getX(){return msg_x;}

	public float getY(){return msg_y;}

	public float getEndX(){return end_x;}

	public float getEndY(){return end_y;}

}

class FuelMessage extends Message{
	private float msg_fuel;
	private float end_fuel;

	public FuelMessage(int s, float f, float end_f, int end_s){
		super(s, end_s);
		this.msg_fuel = f;
		this.end_fuel = end_f;
	}

	public float getFuel(){return msg_fuel;}

	public float getEndFuel(){return end_fuel;}
}


