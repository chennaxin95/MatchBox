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
		if(czoom < zoom - 0.02){
			camera.zoom += 0.02;
		}else if (czoom > zoom + 0.02){
			camera.zoom -= 0.02;
		}
	}
	

	
	public void update(OrthographicCamera camera){
		zoom(camera, targetZoom);
	}
	
	public void update(OrthographicCamera camera, float f){
		zoom(camera, f);
	}
	
}
