package edu.cornell.gdiac.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

public class CameraController {
	private int updateFrame = 0;
	private float targetZoom = 1;
	public CameraController(){
		this.targetZoom = 1;
	}
	
	public void starting(){
		
	}
	
	public void ending(){
		
	}
	
	public void zoom(OrthographicCamera camera, float zoom){
		float czoom = camera.zoom;
		targetZoom = zoom;
		if(czoom < zoom - 0.015){
			camera.zoom += 0.015;
		}else if (czoom > zoom + 0.015){
			camera.zoom -= 0.015;
		}
	}
	

	
	public void update(OrthographicCamera camera){
		zoom(camera, targetZoom);
	}
	
	public void update(OrthographicCamera camera, float f){
		zoom(camera, f);
	}
	
}
