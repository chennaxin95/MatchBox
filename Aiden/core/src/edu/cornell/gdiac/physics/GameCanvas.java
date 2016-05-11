/*
 * GameCanvas.java
 *
 * To properly follow the model-view-controller separation, we should not have
 * any specific drawing code in GameMode. All of that code goes here.  As
 * with GameEngine, this is a class that you are going to want to copy for
 * your own projects.
 *
 * An important part of this canvas design is that it is loosely coupled with
 * the model classes. All of the drawing methods are abstracted enough that
 * it does not require knowledge of the interfaces of the model classes.  This
 * important, as the model classes are likely to change often.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import static com.badlogic.gdx.Gdx.gl20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;




/**
 * Primary view class for the game, abstracting the basic graphics calls.
 * 
 * This version of GameCanvas only supports both rectangular and polygonal Sprite
 * drawing.  It also supports a debug mode that draws polygonal outlines.  However,
 * that mode must be done in a separate begin/end pass.
 */
public class GameCanvas {

	// Constants only needed locally.
	/** Reverse the y-direction so that it is consistent with SpriteBatch */
	private static final Vector3 UP = new Vector3(0,1,0);
	/** For managing the camera pan interpolation at the start of the game */
	private static final Interpolation.SwingIn SWING_IN = new Interpolation.SwingIn(0.1f);
	/** Distance from the eye to the target */
	private static final float EYE_DIST  = 400.0f;
	/** Field of view for the perspective */
	private static final float FOV = 0.7f;
	/** Near distance for perspective clipping */
	private static final float NEAR_DIST = 10.0f;
	/** Far distance for perspective clipping */
	private static final float FAR_DIST  = 500.0f;
	private float eyepan;
	/** Multiplicative factors for initial camera pan */
	private static final float INIT_TARGET_PAN = 0.1f;
	private static final float INIT_EYE_PAN = 0.05f;

	private boolean isEditor = false;

	private CameraController cc = new CameraController();
	private int updateFrame = 0;

	public void setEditor(boolean b){
		isEditor = b;
	}

	/** Enumeration to track which pass we are in */
	private enum DrawPass {
		/** We are not drawing */
		INACTIVE,
		/** We are drawing sprites */
		STANDARD,
		/** We are drawing outlines */
		DEBUG
	}

	/**
	 * Enumeration of supported BlendStates.
	 *
	 * For reasons of convenience, we do not allow user-defined blend functions.
	 * 99% of the time, we find that the following blend modes are sufficient
	 * (particularly with 2D games).
	 */
	public enum BlendState {
		/** Alpha blending on, assuming the colors have pre-multipled alpha (DEFAULT) */
		ALPHA_BLEND,
		/** Alpha blending on, assuming the colors have no pre-multipled alpha */
		NO_PREMULT,
		/** Color values are added together, causing a white-out effect */
		ADDITIVE,
		/** Color values are draw on top of one another with no transparency support */
		OPAQUE
	}	


	/**
	 * Enumeration of supported depth states.
	 *
	 * For reasons of convenience, we do not allow user-defined depth functions.
	 * 99% of the time, we find that the following depth modes are sufficient
	 * (particularly with 2D games).
	 */
	private static enum DepthState {
		/** Do not enable depth masking at all. */
		NONE,
		/** Read from the depth value, but do not write to it */
		READ,
		/** Write to the depth value, but do not read from it */
		WRITE,
		/** Read and write to the depth value, providing normal masking */
		DEFAULT
	}

	/**
	 * Enumeration of supported culling states.
	 *
	 * For reasons of convenience, we do not allow user-defined culling operations.
	 * 99% of the time, we find that the following culling modes are sufficient
	 * (particularly with 2D games).
	 */
	private static enum CullState {
		/** Do not remove the backsides of any polygons; show both sides */
		NONE,
		/** Remove polygon backsides, using clockwise motion to define the front */
		CLOCKWISE,
		/** Remove polygon backsides, using counter-clockwise motion to define the front */
		COUNTER_CLOCKWISE
	}



	/** Drawing context to handle textures AND POLYGONS as sprites */
	private PolygonSpriteBatch spriteBatch;

	/** Rendering context for the debug outlines */
	private ShapeRenderer debugRender;

	/** Track whether or not we are active (for error checking) */
	private DrawPass active;

	/** The current color blending mode */
	private BlendState blend;

	/** Camera for the underlying SpriteBatch */
	private OrthographicCamera camera;

	/** Target for Perspective FOV */
	private Vector3 target;

	// CACHE OBJECTS
	//		/** Projection Matrix */
	//		private Matrix4 proj;
	//		/** View Matrix */
	//		private Matrix4 view;
	/** World Matrix */
	private Matrix4 world;
	/** Temporary Matrix (for Calculations) */
	private Matrix4 tmpMat;

	/** Temporary Vectors */
	private Vector3 tmp0;
	private Vector3 tmp1;
	private Vector2 tmp2d;

	/** Value to cache window width (if we are currently full screen) */
	int width;
	/** Value to cache window height (if we are currently full screen) */
	int height;

	// CACHE OBJECTS
	/** Affine cache for current sprite to draw */
	private Affine2 local;
	/** Affine cache for all sprites this drawing pass */
	private Matrix4 global;
	private Vector2 vertex;
	/** Cache object to handle raw textures */
	private TextureRegion holder;

	/**
	 * Creates a new GameCanvas determined by the application configuration.
	 * 
	 * Width, height, and fullscreen are taken from the LWGJApplicationConfig
	 * object used to start the application.  This constructor initializes all
	 * of the necessary graphics objects.
	 */
	public GameCanvas() {
		active = DrawPass.INACTIVE;
		spriteBatch = new PolygonSpriteBatch();
		debugRender = new ShapeRenderer();

		// Set the projection matrix (for proper scaling)
		camera = new OrthographicCamera(getWidth(),getHeight());
		camera.setToOrtho(false);
		spriteBatch.setProjectionMatrix(camera.combined);
		debugRender.setProjectionMatrix(camera.combined);


		// Initialize the perspective camera objects
		target = new Vector3();
		world = new Matrix4();
		//		view  = new Matrix4();
		//		proj  = new Matrix4();

		// Initialize the cache objects
		holder = new TextureRegion();
		local  = new Affine2();
		global = new Matrix4();
		vertex = new Vector2();

		// Initialize the cache objects
		tmpMat = new Matrix4();
		tmp0  = new Vector3();
		tmp1  = new Vector3();
		tmp2d = new Vector2();
	}

	/**
	 * Eliminate any resources that should be garbage collected manually.
	 */
	public void dispose() {
		if (active != DrawPass.INACTIVE) {
			Gdx.app.error("GameCanvas", "Cannot dispose while drawing active", new IllegalStateException());
			return;
		}
		spriteBatch.dispose();
		spriteBatch = null;
		local  = null;
		global = null;
		vertex = null;
		holder = null;
	}

	/**
	 * Returns the width of this canvas
	 *
	 * This currently gets its value from Gdx.graphics.getWidth()
	 *
	 * @return the width of this canvas
	 */
	public int getWidth() {
		return Gdx.graphics.getWidth();
	}

	/**
	 * Changes the width of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param width the canvas width
	 */
	public void setWidth(int width) {
		if (active != DrawPass.INACTIVE) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.width = width;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(width, getHeight());
		}
		resize();
	}

	/**
	 * Returns the height of this canvas
	 *
	 * This currently gets its value from Gdx.graphics.getHeight()
	 *
	 * @return the height of this canvas
	 */
	public int getHeight() {
		return Gdx.graphics.getHeight();
	}

	/**
	 * Changes the height of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param height the canvas height
	 */
	public void setHeight(int height) {
		if (active != DrawPass.INACTIVE) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.height = height;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(getWidth(), height);	
		}
		resize();
	}

	public Camera getCamera(){
		return this.camera;
	}

	/**
	 * Returns the dimensions of this canvas
	 *
	 * @return the dimensions of this canvas
	 */
	public Vector2 getSize() {
		return new Vector2(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}

	/**
	 * Changes the width and height of this canvas
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param width the canvas width
	 * @param height the canvas height
	 */
	public void setSize(int width, int height) {
		if (active != DrawPass.INACTIVE) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		this.width = width;
		this.height = height;
		if (!isFullscreen()) {
			Gdx.graphics.setWindowedMode(width, height);
		}
		resize();

	}

	/**
	 * Returns whether this canvas is currently fullscreen.
	 *
	 * @return whether this canvas is currently fullscreen.
	 */	 
	public boolean isFullscreen() {
		return Gdx.graphics.isFullscreen(); 
	}

	/**
	 * Sets whether or not this canvas should change to fullscreen.
	 *
	 * If desktop is true, it will use the current desktop resolution for
	 * fullscreen, and not the width and height set in the configuration
	 * object at the start of the application. This parameter has no effect
	 * if fullscreen is false.
	 *
	 * This method raises an IllegalStateException if called while drawing is
	 * active (e.g. in-between a begin-end pair).
	 *
	 * @param fullscreen Whether this canvas should change to fullscreen.
	 * @param desktop 	 Whether to use the current desktop resolution
	 */	 
	public void setFullscreen(boolean value, boolean desktop) {
		if (active != DrawPass.INACTIVE) {
			Gdx.app.error("GameCanvas", "Cannot alter property while drawing active", new IllegalStateException());
			return;
		}
		if (value) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		} else {
			Gdx.graphics.setWindowedMode(width, height);
		}
	}

	/**
	 * Resets the SpriteBatch camera when this canvas is resized.
	 *
	 * If you do not call this when the window is resized, you will get
	 * weird scaling issues.
	 */
	public void resize() {
		// Resizing screws up the spriteBatch projection matrix
		camera.zoom = 1;
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, getWidth(), getHeight());
		camera.setToOrtho(false,getWidth(),getHeight());
	}


	/**
	 * Sets the given matrix to a FOV perspective.
	 *
	 * The field of view matrix is computed as follows:
	 *
	 *        /
	 *       /_
	 *      /  \  <-  FOV 
	 * EYE /____|_____
	 *
	 * Let ys = cot(fov)
	 * Let xs = ys / aspect
	 * Let a = zfar / (znear - zfar)
	 * The matrix is
	 * | xs  0   0      0     |
	 * | 0   ys  0      0     |
	 * | 0   0   a  znear * a |
	 * | 0   0  -1      0     |
	 *
	 * @param out Non-null matrix to store result
	 * @param fov field of view y-direction in radians from center plane
	 * @param aspect Width / Height
	 * @param znear Near clip distance
	 * @param zfar Far clip distance
	 *
	 * @returns Newly created matrix stored in out
	 */
	private Matrix4 setToPerspectiveFOV(Matrix4 out, float fov, float aspect, float znear, float zfar) {
		float ys = (float)(1.0 / Math.tan(fov));
		float xs = ys / aspect;
		float a  = zfar / (znear - zfar);

		out.val[0 ] = xs;
		out.val[4 ] = 0.0f;
		out.val[8 ] = 0.0f;
		out.val[12] = 0.0f;

		out.val[1 ] = 0.0f;
		out.val[5 ] = ys;
		out.val[9 ] = 0.0f;
		out.val[13] = 0.0f;

		out.val[2 ] = 0.0f;
		out.val[6 ] = 0.0f;
		out.val[10] = a;
		out.val[14] = znear * a;

		out.val[3 ] = 0.0f;
		out.val[7 ] = 0.0f;
		out.val[11] = -1.0f;
		out.val[15] = 0.0f;

		return out;
	}


	/**
	 * Returns the current color blending state for this canvas.
	 *
	 * Textures draw to this canvas will be composited according
	 * to the rules of this blend state.
	 *
	 * @return the current color blending state for this canvas
	 */
	public BlendState getBlendState() {
		return blend;
	}

	/**
	 * Sets the color blending state for this canvas.
	 *
	 * Any texture draw subsequent to this call will use the rules of this blend 
	 * state to composite with other textures.  Unlike the other setters, if it is 
	 * perfectly safe to use this setter while  drawing is active (e.g. in-between 
	 * a begin-end pair).  
	 *
	 * @param state the color blending rule
	 */
	public void setBlendState(BlendState state) {
		if (state == blend) {
			return;
		}
		switch (state) {
		case NO_PREMULT:
			spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
			break;
		case ALPHA_BLEND:
			spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
			break;
		case ADDITIVE:
			spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE);
			break;
		case OPAQUE:
			spriteBatch.setBlendFunction(GL20.GL_ONE,GL20.GL_ZERO);
			break;
		}
		blend = state;
	}

	/**
	 * Sets the mode for culling unwanted polygons based on depth.
	 *
	 * @param state The depth mode
	 */
	private void setDepthState(DepthState state) {
		boolean shouldRead  = true;
		boolean shouldWrite = true;
		int depthFunc = 0;

		switch (state) {
		case NONE:
			shouldRead  = false;
			shouldWrite = false;
			depthFunc = GL20.GL_ALWAYS;
			break;
		case READ:
			shouldRead  = false;
			shouldWrite = true;
			depthFunc = GL20.GL_LEQUAL;
			break;
		case WRITE:
			shouldRead  = false;
			shouldWrite = true;
			depthFunc = GL20.GL_ALWAYS;
			break;
		case DEFAULT:
			shouldRead  = true;
			shouldWrite = true;
			depthFunc = GL20.GL_LEQUAL;
			break;
		}

		if (shouldRead || shouldWrite) {
			gl20.glEnable(GL20.GL_DEPTH_TEST);
			gl20.glDepthMask(shouldWrite);
			gl20.glDepthFunc(depthFunc);
		} else {
			gl20.glDisable(GL20.GL_DEPTH_TEST);
		}
	}

	/**
	 * Sets the mode for culling unwanted polygons based on facing.
	 *
	 * @param state The culling mode
	 */
	private void setCullState(CullState state) {
		boolean cull = true;
		int mode = 0;
		int face = 0;

		switch (state) {
		case NONE:
			cull = false;
			mode = GL20.GL_BACK;
			face = GL20.GL_CCW;
			break;
		case CLOCKWISE:
			cull = true;
			mode = GL20.GL_BACK;
			face = GL20.GL_CCW;
			break;
		case COUNTER_CLOCKWISE:
			cull = true;
			mode = GL20.GL_BACK;
			face = GL20.GL_CW;
			break;
		}
		if (cull) {
			gl20.glEnable(GL20.GL_CULL_FACE);
			gl20.glFrontFace(face);
			gl20.glCullFace(mode);
		} else {
			gl20.glDisable(GL20.GL_CULL_FACE);
		}

	}

	/**
	 * Clear the screen so we can start a new animation frame
	 */
	public void clear() {
		// Clear the screen
		Gdx.gl.glClearColor(0.25f, 0.165f, 0.102f, 1.0f);  // Homage to the XNA years
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
	}

	/**
	 * Start a standard drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 *
	 * @param affine the global transform apply to the camera
	 */
	public void begin(Affine2 affine) {
		global.setAsAffine(affine);
		global.mulLeft(camera.combined);
		spriteBatch.setProjectionMatrix(global);

		setBlendState(BlendState.NO_PREMULT);
		spriteBatch.begin();
		active = DrawPass.STANDARD;
	}

	/**
	 * Start a standard drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 *
	 * @param sx the amount to scale the x-axis
	 * @param sy the amount to scale the y-axis
	 */
	public void begin(float x, float y) {

		x = x*1920/32;
		y = y*1080/16;
		target.set(x, y, 0);
		//eye.set(target).add(0, NEAR_DIST, -EYE_DIST);

		// Position the camera
		float f = -1f;

		camera.zoom = 0.79f;
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			camera.translate(new Vector3(-2,0,0));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			camera.translate(new Vector3(2,0,0));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP) ||
				Gdx.input.isKeyPressed(Input.Keys.W)){
			camera.translate(new Vector3(0,2,0));
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)||
				Gdx.input.isKeyPressed(Input.Keys.S)){
			camera.translate(new Vector3(0,-2,0));

		}

		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		active = DrawPass.STANDARD;

	}

	public void begin(float x, float y, int w, int h, float camFrame) {
		/*if(camFrame > 200){
			translate(x,y,w,h);
			if (InputController.getInstance().zoomIn() && camera.zoom>0.77) camera.zoom-=0.02f;
			if (InputController.getInstance().zoomOut() && camera.zoom<1.8) camera.zoom+=0.02f;

		}*/
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		active = DrawPass.STANDARD;

	}

	/**
	 * Start a standard drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 */
	public void begin() {
		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		active = DrawPass.STANDARD;
	}

	/**
	 * Ends a drawing sequence, flushing textures to the graphics card.
	 */
	public void end() {
		spriteBatch.end();
		active = DrawPass.INACTIVE;
	}
	
	public float getZoom(){
		return camera.zoom;
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */
	public void draw(Texture image, float x, float y) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(image, x,  y);
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 * @param width	The texture width
	 * @param height The texture height
	 */
	public void draw(Texture image, Color tint, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(tint);
		spriteBatch.draw(image, x,  y, width, height);
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param width	The texture width
	 * @param height The texture height
	 */
	public void draw(Texture image, Color tint, float ox, float oy, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Call the master drawing method (more efficient that base method)
		holder.setRegion(image);
		draw(holder, tint, x-ox, y-oy, width, height);
	}


	/**
	 * Draws the tinted texture with the given transformations
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */	
	public void draw(Texture image, Color tint, float ox, float oy, 
			float x, float y, float angle, float sx, float sy) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Call the master drawing method (more efficient that base method)
		holder.setRegion(image);
		draw(holder,tint,ox,oy,x,y,angle,sx,sy);
	}

	/**
	 * Draws the tinted texture with the given transformations
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param transform  The image transform
	 */	
	public void draw(Texture image, Color tint, float ox, float oy, Affine2 transform) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Call the master drawing method (we have to for transforms)
		holder.setRegion(image);
		draw(holder,tint,ox,oy,transform);
	}

	/**
	 * Draws the tinted texture region (filmstrip) at the given position.
	 *
	 * A texture region is a single texture file that can hold one or more textures.
	 * It is used for filmstrip animation.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param region The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */
	public void draw(TextureRegion region, float x, float y) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(region, x,  y);
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *region
	 * @param image The texture to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 * @param width	The texture width
	 * @param height The texture height
	 */
	public void draw(TextureRegion region, Color tint, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, x,  y, width, height);
	}

	/**
	 * Draws the tinted texture at the given position.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * Unless otherwise transformed by the global transform (@see begin(Affine2)),
	 * the texture will be unscaled.  The bottom left of the texture will be positioned
	 * at the given coordinates.
	 *
	 * @param region The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param width	The texture width
	 * @param height The texture height
	 */	
	public void draw(TextureRegion region, Color tint, float ox, float oy, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, x-ox, y-oy, width, height);
	}

	/**
	 * Draws the tinted texture region (filmstrip) with the given transformations
	 *
	 * A texture region is a single texture file that can hold one or more textures.
	 * It is used for filmstrip animation.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The texture to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */	
	public void draw(TextureRegion region, Color tint, float ox, float oy, 
			float x, float y, float angle, float sx, float sy) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// BUG: The draw command for texture regions does not work properly.
		// There is a workaround, but it will break if the bug is fixed.
		// For now, it is better to set the affine transform directly.
		computeTransform(ox,oy,x,y,angle,sx,sy);
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, region.getRegionWidth(), region.getRegionHeight(), local);
	}

	/**
	 * Draws the tinted texture with the given transformations
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param image The region to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param transform  The image transform
	 */	
	public void draw(TextureRegion region, Color tint, float ox, float oy, Affine2 affine) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		local.set(affine);
		local.translate(-ox,-oy);				
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, region.getRegionWidth(), region.getRegionHeight(), local);
	}

	/**
	 * Draws the polygonal region with the given transformations
	 *
	 * A polygon region is a texture region with attached vertices so that it draws a
	 * textured polygon. The polygon vertices are relative to the texture file.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The polygon to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 */	
	public void draw(PolygonRegion region, float x, float y) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(region, x,  y);
	}

	/**
	 * Draws the polygonal region with the given transformations
	 *
	 * A polygon region is a texture region with attached vertices so that it draws a
	 * textured polygon. The polygon vertices are relative to the texture file.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The polygon to draw
	 * @param tint  The color tint
	 * @param x 	The x-coordinate of the bottom left corner
	 * @param y 	The y-coordinate of the bottom left corner
	 * @param width	The texture width
	 * @param height The texture height
	 */	
	public void draw(PolygonRegion region, Color tint, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, x,  y, width, height);
	}

	/**
	 * Draws the polygonal region with the given transformations
	 *
	 * A polygon region is a texture region with attached vertices so that it draws a
	 * textured polygon. The polygon vertices are relative to the texture file.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The polygon to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param width	The texture width
	 * @param height The texture height
	 */	
	public void draw(PolygonRegion region, Color tint, float ox, float oy, float x, float y, float width, float height) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		// Unlike Lab 1, we can shortcut without a master drawing method
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, x-ox, y-oy, width, height);
	}

	/**
	 * Draws the polygonal region with the given transformations
	 *
	 * A polygon region is a texture region with attached vertices so that it draws a
	 * textured polygon. The polygon vertices are relative to the texture file.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The polygon to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */	
	public void draw(PolygonRegion region, Color tint, float ox, float oy, 
			float x, float y, float angle, float sx, float sy) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		TextureRegion bounds = region.getRegion();
		spriteBatch.setColor(tint);
		spriteBatch.draw(region, x, y, ox, oy, 
				bounds.getRegionWidth(), bounds.getRegionHeight(), 
				sx, sy, 180.0f*angle/(float)Math.PI);
	}

	/**
	 * Draws the polygonal region with the given transformations
	 *
	 * A polygon region is a texture region with attached vertices so that it draws a
	 * textured polygon. The polygon vertices are relative to the texture file.
	 *
	 * The texture colors will be multiplied by the given color.  This will turn
	 * any white into the given color.  Other colors will be similarly affected.
	 *
	 * The transformations are BEFORE after the global transform (@see begin(Affine2)).  
	 * As a result, the specified texture origin will be applied to all transforms 
	 * (both the local and global).
	 *
	 * The local transformations in this method are applied in the following order: 
	 * scaling, then rotation, then translation (e.g. placement at (sx,sy)).
	 *
	 * @param region The polygon to draw
	 * @param tint  The color tint
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param transform  The image transform
	 */	
	public void draw(PolygonRegion region, Color tint, float ox, float oy, Affine2 affine) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		local.set(affine);
		local.translate(-ox,-oy);
		computeVertices(local,region.getVertices());

		spriteBatch.setColor(tint);
		spriteBatch.draw(region, 0, 0);

		// Invert and restore
		local.inv();
		computeVertices(local,region.getVertices());
	}

	/**
	 * Transform the given vertices by the affine transform
	 */
	private void computeVertices(Affine2 affine, float[] vertices) {
		for(int ii = 0; ii < vertices.length; ii += 2) {
			vertex.set(vertices[2*ii], vertices[2*ii+1]);
			affine.applyTo(vertex);
			vertices[2*ii  ] = vertex.x;
			vertices[2*ii+1] = vertex.y;
		}
	}

	/**
	 * Draws text on the screen.
	 *
	 * @param text The string to draw
	 * @param font The font to use
	 * @param x The x-coordinate of the lower-left corner
	 * @param y The y-coordinate of the lower-left corner
	 */
	public void drawText(String text, BitmapFont font, float x, float y) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}
		GlyphLayout layout = new GlyphLayout(font,text);
		font.draw(spriteBatch, layout, x, y);
	}
	/**
	 * Draws text centered on the screen.
	 *
	 * @param text The string to draw
	 * @param font The font to use
	 * @param offset The y-value offset from the center of the screen.
	 */

	public Vector2 relativeVector(float x, float y){
		OrthographicCamera c = this.camera;
		Vector3 pos = new Vector3(x, this.getHeight() - y, 0);
		Vector3 n = c.unproject(pos);
		Vector2 nPos = new Vector2(n.x, n.y);
		return nPos;
		//        return new Vector2(x+c.position.x-c.viewportWidth/2,y+c.position.y-c.viewportHeight/2);
	}

	/**
	 * Draws text centered on the screen.
	 *
	 * @param text The string to draw
	 * @param font The font to use
	 * @param offset The y-value offset from the center of the screen.
	 */
	public void drawTextCentered(String text, BitmapFont font, float offset) {
		if (active != DrawPass.STANDARD) {
			Gdx.app.error("GameCanvas", "Cannot draw without active begin()", new IllegalStateException());
			return;
		}

		GlyphLayout layout = new GlyphLayout(font,text);
		float x = (getWidth()  - layout.width) / 2.0f;
		float y = (getHeight() + layout.height) / 2.0f;
		font.draw(spriteBatch, layout, x, y+offset);
	}

	//drawbutton
	public void drawButton(ImageButton button){
		button.draw(spriteBatch, 1f);
	}

	/**
	 * Start the debug drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 *
	 * @param affine the global transform apply to the camera
	 */
	public void beginDebug(Affine2 affine) {
		global.setAsAffine(affine);
		global.mulLeft(camera.combined);
		debugRender.setProjectionMatrix(global);

		debugRender.begin(ShapeRenderer.ShapeType.Line);
		active = DrawPass.DEBUG;
	}

	/**
	 * Start the debug drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 *
	 * @param sx the amount to scale the x-axis
	 * @param sy the amount to scale the y-axis
	 */    
	public void beginDebug(float sx, float sy) {
		global.idt();
		global.scl(sx,sy,1.0f);
		global.mulLeft(camera.combined);
		debugRender.setProjectionMatrix(global);

		debugRender.begin(ShapeRenderer.ShapeType.Line);
		active = DrawPass.DEBUG;
	}

	/**
	 * Start the debug drawing sequence.
	 *
	 * Nothing is flushed to the graphics card until the method end() is called.
	 */
	public void beginDebug() {
		debugRender.setProjectionMatrix(camera.combined);
		debugRender.begin(ShapeRenderer.ShapeType.Filled);
		debugRender.setColor(Color.RED);
		debugRender.circle(0, 0, 10);
		debugRender.end();

		debugRender.begin(ShapeRenderer.ShapeType.Line);
		active = DrawPass.DEBUG;
	}

	/**
	 * Ends the debug drawing sequence, flushing textures to the graphics card.
	 */
	public void endDebug() {
		debugRender.end();
		active = DrawPass.INACTIVE;
	}

	/**
	 * Draws the outline of the given shape in the specified color
	 *
	 * @param shape The Box2d shape
	 * @param color The outline color
	 * @param x  The x-coordinate of the shape position
	 * @param y  The y-coordinate of the shape position
	 */
	public void drawPhysics(PolygonShape shape, Color color, float x, float y) {
		if (active != DrawPass.DEBUG) {
			Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
			return;
		}

		float x0, y0, x1, y1;
		debugRender.setColor(color);
		for(int ii = 0; ii < shape.getVertexCount()-1; ii++) {
			shape.getVertex(ii  ,vertex);
			x0 = x+vertex.x; y0 = y+vertex.y;
			shape.getVertex(ii+1,vertex);
			x1 = x+vertex.x; y1 = y+vertex.y;
			debugRender.line(x0, y0, x1, y1);
		}
		// Close the loop
		shape.getVertex(shape.getVertexCount()-1,vertex);
		x0 = x+vertex.x; y0 = y+vertex.y;
		shape.getVertex(0,vertex);
		x1 = x+vertex.x; y1 = y+vertex.y;
		debugRender.line(x0, y0, x1, y1);
	}

	/**
	 * Draws the outline of the given shape in the specified color
	 *
	 * @param shape The Box2d shape
	 * @param color The outline color
	 * @param x  The x-coordinate of the shape position
	 * @param y  The y-coordinate of the shape position
	 * @param angle  The shape angle of rotation
	 */
	public void drawPhysics(PolygonShape shape, Color color, float x, float y, float angle) {
		if (active != DrawPass.DEBUG) {
			Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
			return;
		}

		local.setToTranslation(x,y);
		local.rotateRad(angle);

		float x0, y0, x1, y1;
		debugRender.setColor(color);
		for(int ii = 0; ii < shape.getVertexCount()-1; ii++) {
			shape.getVertex(ii  ,vertex);
			local.applyTo(vertex);
			x0 = vertex.x; y0 = vertex.y;
			shape.getVertex(ii+1,vertex);
			local.applyTo(vertex);
			x1 = vertex.x; y1 = vertex.y;
			debugRender.line(x0, y0, x1, y1);
		}
		// Close the loop
		shape.getVertex(shape.getVertexCount()-1,vertex);
		local.applyTo(vertex);
		x0 = vertex.x; y0 = vertex.y;
		shape.getVertex(0,vertex);
		local.applyTo(vertex);
		x1 = vertex.x; y1 = vertex.y;
		debugRender.line(x0, y0, x1, y1);
	}

	/**
	 * Draws the outline of the given shape in the specified color
	 *
	 * @param shape The Box2d shape
	 * @param color The outline color
	 * @param x  The x-coordinate of the shape position
	 * @param y  The y-coordinate of the shape position
	 * @param angle  The shape angle of rotation
	 * @param sx The amount to scale the x-axis
	 * @param sx The amount to scale the y-axis
	 */
	public void drawPhysics(PolygonShape shape, Color color, float x, float y, float angle, float sx, float sy) {
		if (active != DrawPass.DEBUG) {
			Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
			return;
		}

		local.setToScaling(sx,sy);
		local.translate(x,y);
		local.rotateRad(angle);

		float x0, y0, x1, y1;
		debugRender.setColor(color);
		for(int ii = 0; ii < shape.getVertexCount()-1; ii++) {
			shape.getVertex(ii  ,vertex);
			local.applyTo(vertex);
			x0 = vertex.x; y0 = vertex.y;
			shape.getVertex(ii+1,vertex);
			local.applyTo(vertex);
			x1 = vertex.x; y1 = vertex.y;
			debugRender.line(x0, y0, x1, y1);
		}
		// Close the loop
		shape.getVertex(shape.getVertexCount()-1,vertex);
		local.applyTo(vertex);
		x0 = vertex.x; y0 = vertex.y;
		shape.getVertex(0,vertex);
		local.applyTo(vertex);
		x1 = vertex.x; y1 = vertex.y;
		debugRender.line(x0, y0, x1, y1);
	}

	/** 
	 * Draws the outline of the given shape in the specified color
	 *
	 * The position of the circle is ignored.  Only the radius is used. To move the
	 * circle, change the x and y parameters.
	 * 
	 * @param shape The Box2d shape
	 * @param color The outline color
	 * @param x  The x-coordinate of the shape position
	 * @param y  The y-coordinate of the shape position
	 */
	public void drawPhysics(CircleShape shape, Color color, float x, float y) {
		if (active != DrawPass.DEBUG) {
			Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
			return;
		}

		debugRender.setColor(color);
		debugRender.circle(x, y, shape.getRadius(),12);
	}

	/** 
	 * Draws the outline of the given shape in the specified color
	 *
	 * The position of the circle is ignored.  Only the radius is used. To move the
	 * circle, change the x and y parameters.
	 * 
	 * @param shape The Box2d shape
	 * @param color The outline color
	 * @param x  The x-coordinate of the shape position
	 * @param y  The y-coordinate of the shape position
	 * @param sx The amount to scale the x-axis
	 * @param sx The amount to scale the y-axis
	 */
	public void drawPhysics(CircleShape shape, Color color, float x, float y, float sx, float sy) {
		if (active != DrawPass.DEBUG) {
			Gdx.app.error("GameCanvas", "Cannot draw without active beginDebug()", new IllegalStateException());
			return;
		}

		float x0 = x*sx;
		float y0 = y*sy;
		float w = shape.getRadius()*sx;
		float h = shape.getRadius()*sy;
		debugRender.setColor(color);
		debugRender.ellipse(x0-w, y0-h, 2*w, 2*h, 12);
	}

	/**
	 * Compute the affine transform (and store it in local) for this image.
	 * 
	 * @param ox 	The x-coordinate of texture origin (in pixels)
	 * @param oy 	The y-coordinate of texture origin (in pixels)
	 * @param x 	The x-coordinate of the texture origin (on screen)
	 * @param y 	The y-coordinate of the texture origin (on screen)
	 * @param angle The rotation angle (in degrees) about the origin.
	 * @param sx 	The x-axis scaling factor
	 * @param sy 	The y-axis scaling factor
	 */
	private void computeTransform(float ox, float oy, float x, float y, float angle, float sx, float sy) {
		local.setToTranslation(x,y);
		local.rotate(180.0f*angle/(float)Math.PI);
		local.scale(sx,sy);
		local.translate(-ox,-oy);
	}

	public void drawParticle(ParticleEffect pe){
		computeTransform(0, 0, pe.getEmitters().get(0).getX(), pe.getEmitters().get(0).getX(), 0, 1f,1f);
		pe.draw(this.spriteBatch);
	}

	public void zoomCamera(float zoom){
		float czoom = camera.zoom;
		if(czoom < zoom){
			while(camera.zoom < zoom){
				if(updateFrame %100 == 0){
					camera.zoom += 0.02;
				}
				updateFrame ++;
				//camera.update();
			}
		}else{
			while(camera.zoom > zoom){
				if(updateFrame %100 == 0){
					camera.zoom -= 0.02;
				}
				updateFrame ++;
				//camera.update();
			}
		}
	}

	public void updateCam(){
		cc.update(camera);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		active = DrawPass.STANDARD;
	}

	public void updateCam(float f){
		cc.update(camera,f);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		active = DrawPass.STANDARD;
	}

	public void translate(float x, float y, int w, int h){
		if(x<12) {
			x = Math.min(12, w/2);
		}else if(x>w-12){
			x = Math.max(w-12, w/2);
		}
		if(y<8){
			y = Math.min(8, h/2);
		}else if (y > h-8){
			y = Math.max(h-8, h/2);
		}
		x = x*1920/60;
		y = y*1080/36;
		// x = x*getWidth()/w;
		// y = y*getHeight()/w;
		target.set(x, y, 0);
		//eye.set(target).add(0, NEAR_DIST, -EYE_DIST);

		// Position the camera
		float f = -1f;
		Vector3 d = target.add(new Vector3(f*camera.position.x,f*camera.position.y,-1));
		if (d.x*d.x + d.y*d.y>500){
			camera.translate(new Vector3(d.x/40, d.y/40, 0f));
		}
		else if (d.x*d.x + d.y*d.y>400){
			camera.translate(new Vector3(d.x/25, d.y/25, 0f));
		}	
		else if (d.x*d.x + d.y*d.y>200){
			camera.translate(new Vector3(d.x/12, d.y/12, 0f));
		}	
		
		else if (d.x*d.x + d.y*d.y>100){
			camera.translate(new Vector3(d.x/20, d.y/20, 0f));
		}	
		else if (d.x*d.x + d.y*d.y>0 && d.x*d.x + d.y*d.y<100 ){
			camera.translate(new Vector3(d.x/30, d.y/30, 0f));
		}
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
		active = DrawPass.STANDARD;

	}
}