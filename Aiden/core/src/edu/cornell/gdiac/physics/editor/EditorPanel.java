package edu.cornell.gdiac.physics.editor;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.InputController;
import edu.cornell.gdiac.physics.scene.AssetFile;

public class EditorPanel {
	/*
	 * All in Pixels 
	 */
	public float width;
	
	/*
	 * In board cell
	 */
	public int boardWidth=60, boardHeight=34;
	
	private TextureRegion[] textures;
	private Rectangle[] texture_layout;
	private Rectangle width_i_adjust_layout;
	private Rectangle width_d_adjust_layout;
	private Rectangle width_display_layout;
	private Rectangle height_i_adjust_layout;
	private Rectangle height_d_adjust_layout;
	private Rectangle height_display_layout;
	private Rectangle poly_mode_layout;
	private Rectangle poly_display_layout;
	private Rectangle save_layout;
	private Rectangle load_layout;
	
	public boolean polyMode;
	
	public int selectedTexture;
	public EditorMode mode;
	
	private TextureRegion background;

	private TextureRegion button;
	
	private AssetFile af;

	public EditorPanel(float width, TextureRegion[] textures,
			AssetFile af){
		this.textures=textures;
		this.width=width;
		mode=EditorMode.DEFAULT;
		
		texture_layout=new Rectangle[textures.length];
		float accWidth=0;
		float accHeight=0;
		float maxHeight=0;
		for (int i=0; i<textures.length; i++){
			if (accWidth+textures[i].getRegionWidth()>this.width){
				accHeight+=maxHeight;
				maxHeight=0;
				accWidth=0;
			}
			texture_layout[i]=new Rectangle(accWidth, accHeight,
								textures[i].getRegionWidth(), 
								textures[i].getRegionHeight());
			accWidth+=textures[i].getRegionWidth();
			maxHeight=Math.max(maxHeight,textures[i].getRegionHeight());
		}
		this.af=af;
		if (af!=null){
			button=af.earthTile;
			width_i_adjust_layout=new Rectangle(width*1f/3f, 540, 
										  width*1f/6f,
										  button.getRegionHeight());
			width_d_adjust_layout=new Rectangle(width*2f/3f, 540, 
				  width*1f/6f,
				  button.getRegionHeight());
			width_display_layout=new Rectangle(0, 540, 
					  				width, width/6f);
			height_i_adjust_layout=new Rectangle(width*1f/3f, 480, 
				  						   width*1f/6f,
				  						   button.getRegionHeight());
			height_d_adjust_layout=new Rectangle(width*2f/3f, 480, 
				   width*1f/6f,
				   button.getRegionHeight());
			height_display_layout=new Rectangle(0, 480, 
	  									width, width/6f);
			poly_mode_layout=new Rectangle(width*2f/5f, 300, 
					width/5f, button.getRegionHeight());
			poly_display_layout=new Rectangle(0, 300,
									width, width/6f);
			save_layout=new Rectangle(width*2f/5f, 360, 
					width/5f, button.getRegionHeight());
			load_layout=new Rectangle(width*2f/5f, 420, 
					width/5f, button.getRegionHeight());
		}
		
	}
	
	public void update(float x, float y){
		if (mode==EditorMode.GAMEOBJECT) mode=EditorMode.DEFAULT;
		if (this.poly_mode_layout.contains(x,y)){
			this.polyMode=!this.polyMode;
			if (this.polyMode){
				mode=EditorMode.POLY;
			}
			else{
				mode=EditorMode.DEFAULT;
			}
		}
		if (mode==EditorMode.POLY){
			return;
		}
		for (int i=0; i<texture_layout.length; i++){
			if (texture_layout[i].contains(x,y)){
				selectedTexture=i;
				mode=EditorMode.GAMEOBJECT;
				return;
			}
		}
		if (this.width_i_adjust_layout.contains(x,y)){
			System.out.println("++");
			this.boardWidth++;
			mode=EditorMode.DEFAULT;
			return;
		}
		if (this.width_d_adjust_layout.contains(x,y)){
			System.out.println("+--");
			this.boardWidth--;
			mode=EditorMode.DEFAULT;
			return;
		}
		if (this.height_i_adjust_layout.contains(x,y)){
			System.out.println("++");
			this.boardHeight++;
			mode=EditorMode.DEFAULT;
			return;
		}
		if (this.height_d_adjust_layout.contains(x,y)){
			System.out.println("--");
			this.boardHeight--;
			mode=EditorMode.DEFAULT;
			return;
		}
//		if (this.save_layout.contains(x,y)){
//			mode=EditorMode.SAVE;
//			return;
//		}
//		if (this.load_layout.contains(x,y)){
//			mode=EditorMode.LOAD;
//			return;
//		}
	}
	
	public void setBackground(TextureRegion texture){
		background=texture;
	}
	public void setButton(TextureRegion texture){
		button=texture;
	}

	public void draw(GameCanvas canvas){
		if (af==null || af.editorPanelTexture==null) return;
		Vector2 pos = canvas.relativeVector(0, 0);
		// Draw panel
		canvas.draw(af.editorPanelTexture, Color.WHITE, 
				pos.x, pos.y, width, width*5);
		// Draw Texture
		for (int i=0; i<textures.length; i++){
			canvas.draw(textures[i],Color.WHITE, 
					0, 0, pos.x+texture_layout[i].x, 
					pos.y+texture_layout[i].y,
					0, 1, 1);
		}
		// Draw Poly mode button	
		canvas.draw(button, Color.WHITE, 
				pos.x+poly_mode_layout.x, 
				pos.y+poly_mode_layout.y, 
				poly_mode_layout.width,
				poly_mode_layout.height);
		canvas.drawText("Poly Mode: "+ (polyMode? "ON": "OFF"), 
						af.panelFont, 
						pos.x+poly_display_layout.x, 
						pos.y+poly_display_layout.y);
		// Draw adjust height mode button
		canvas.draw(button, Color.WHITE,  
				pos.x+this.height_i_adjust_layout.x, 
				pos.y+this.height_i_adjust_layout.y, 
				this.height_i_adjust_layout.width,
				this.height_i_adjust_layout.height);
		canvas.drawText("+", af.panelFont, 
				pos.x+height_i_adjust_layout.x
					+height_i_adjust_layout.width/2f, 
				pos.y+height_i_adjust_layout.y
					+height_i_adjust_layout.height);
		canvas.draw(button, Color.WHITE, 
				pos.x+this.height_d_adjust_layout.x, 
				pos.y+this.height_d_adjust_layout.y, 
				this.height_d_adjust_layout.width,
				this.height_d_adjust_layout.height);
		canvas.drawText("-", af.panelFont, 
				pos.x+height_d_adjust_layout.x
					+height_d_adjust_layout.width/2f, 
				pos.y+height_d_adjust_layout.y
					+height_d_adjust_layout.height);
		canvas.drawText("Height: "+boardHeight, af.panelFont, 
				pos.x+height_display_layout.x, 
				pos.y+height_display_layout.y);
		// Draw adjust width mode button
		canvas.draw(button, Color.WHITE, 
				pos.x+this.width_i_adjust_layout.x, 
				pos.y+this.width_i_adjust_layout.y, 
				this.width_i_adjust_layout.width,
				this.width_i_adjust_layout.height);
		canvas.drawText("+", af.panelFont, 
				pos.x+width_i_adjust_layout.x
					+width_i_adjust_layout.width/2f, 
				pos.y+width_i_adjust_layout.y
					+width_i_adjust_layout.height);
		canvas.draw(button, Color.WHITE, 
				pos.x+this.width_d_adjust_layout.x, 
				pos.y+this.width_d_adjust_layout.y, 
				this.width_d_adjust_layout.width,
				this.width_d_adjust_layout.height);
		canvas.drawText("-", af.panelFont, 
				pos.x+width_d_adjust_layout.x
					+width_d_adjust_layout.width/2f, 
				pos.y+width_d_adjust_layout.y
					+width_d_adjust_layout.height);
		canvas.drawText("Width: "+boardWidth, af.panelFont, 
				pos.x+width_display_layout.x, 
				pos.y+width_display_layout.y);
		// Save
		canvas.draw(button, Color.WHITE, 
				pos.x+this.save_layout.x, 
				pos.y+this.save_layout.y, 
				this.save_layout.width,
				this.save_layout.height);
		canvas.draw(button, Color.WHITE, 
				pos.x+this.load_layout.x, 
				pos.y+this.load_layout.y, 
				this.load_layout.width,
				this.load_layout.height);
	}
	
	public enum EditorMode{
		DEFAULT, GAMEOBJECT, POLY, SAVE, LOAD
	}

	public void drawDebug(GameCanvas canvas) {
		if (af==null || af.backGround==null) return;
		Vector2 pos = canvas.relativeVector(0, 0);
		// TODO Auto-generated method stub
		if (mode==EditorMode.GAMEOBJECT && this.selectedTexture>=0
				&& this.selectedTexture<textures.length){
			float sWidth=textures[selectedTexture].getRegionWidth();
			float sHeight=textures[selectedTexture].getRegionHeight();			
			float[] pts = new float[] { 0, 0, sWidth, 0, sWidth, sHeight,
					0, sHeight};
			PolygonShape poly = new PolygonShape();
			poly.set(pts);
			canvas.drawPhysics(poly, Color.GREEN, 
					pos.x+texture_layout[selectedTexture].x,
					pos.y+texture_layout[selectedTexture].y,
					0, 1, 1);
		}
	}
	
	
}
