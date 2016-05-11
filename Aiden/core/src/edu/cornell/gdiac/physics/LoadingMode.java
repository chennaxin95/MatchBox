/*
 * LoadingMode.java
 *
 * Asset loading is a really tricky problem.  If you have a lot of sound or images,
 * it can take a long time to decompress them and load them into memory.  If you just
 * have code at the start to load all your assets, your game will look like it is hung
 * at the start.
 *
 * The alternative is asynchronous asset loading.  In asynchronous loading, you load a
 * little bit of the assets at a time, but still animate the game while you are loading.
 * This way the player knows the game is not hung, even though he or she cannot do 
 * anything until loading is complete. You know those loading screens with the inane tips 
 * that want to be helpful?  That is asynchronous loading.  
 *
 * This player mode provides a basic loading screen.  While you could adapt it for
 * between level loading, it is currently designed for loading all assets at the 
 * start of the game.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import javax.swing.text.StyledEditorKit.ForegroundAction;

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.controllers.*;

import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.util.*;



/**
 * Class that provides a loading screen for the state of the game.
 *
 * You still DO NOT need to understand this class for this lab. We will talk
 * about this class much later in the course. This class provides a basic
 * template for a loading screen to be used at the start of the game or between
 * levels. Feel free to adopt this to your needs.
 *
 * You will note that this mode has some textures that are not loaded by the
 * AssetManager. You are never required to load through the AssetManager. But
 * doing this will block the application. That is why we try to have as few
 * resources as possible for this loading screen.
 */
public class LoadingMode implements Screen, InputProcessor, ControllerListener {
	// Textures necessary to support the loading screen
	public static final String PLAY_BTN_FILE = "shared/start.png";
	public static final String MAIN_MENU = "shared/Main Menu.png";
	public static final String LEVELS = "shared/levels.png";
	public static final String SETTINGS = "shared/settings.png";
	public static final String CREDITS = "shared/credits.png";
	public static final String SELECT = "shared/select level.png";

	/** Background texture for start-up */
	public Texture background;
	/** Play button to display when done */
	public Texture playButton;
	public Texture blackBack;
	public Texture grayLine;
	public Texture whiteLine;
	/** Texture atlas to support a progress bar */
	public Texture statusBar;
	/** Texture for main menu title*/
	public Texture mainMenu;
	/** Texture for levels button*/
	public Texture levels;
	/** Texture for settings button*/
	public Texture settings;
	/** Texture for credits button*/
	public Texture credits;
	/** Texture for each level button*/
	public Texture level;
	public Texture select;
	public Texture back;
	public Texture editor;
	public Texture levelTemp;

	// statusBar is a "texture atlas." Break it up into parts.
	/** Left cap to the status background (grey region) */
	public TextureRegion statusBkgLeft;
	/** Middle portion of the status background (grey region) */
	public TextureRegion statusBkgMiddle;
	/** Right cap to the status background (grey region) */
	public TextureRegion statusBkgRight;
	/** Left cap to the status forground (colored region) */
	public TextureRegion statusFrgLeft;
	/** Middle portion of the status forground (colored region) */
	public TextureRegion statusFrgMiddle;
	/** Right cap to the status forground (colored region) */
	public TextureRegion statusFrgRight;

	/** Default budget for asset loader (do nothing but load 60 fps) */
	public static int DEFAULT_BUDGET = 15;
	/** Standard window size (for scaling) */
	public static int STANDARD_WIDTH = 1920;
	/** Standard window height (for scaling) */
	public static int STANDARD_HEIGHT = 1080;
	/** Ratio of the bar width to the screen */
	public static float BAR_WIDTH_RATIO = 0.66f;
	/** Ration of the bar height to the screen */
	public static float BAR_HEIGHT_RATIO = 0.25f;
	/** Height of the progress bar */
	public static int PROGRESS_HEIGHT = 30;
	/** Width of the rounded cap on left or right */
	public static int PROGRESS_CAP = 15;
	/** Width of the middle portion in texture atlas */
	public static int PROGRESS_MIDDLE = 200;
	/** Amount to scale the play button */
	public static float BUTTON_SCALE = 0.3f;
	/** Amount to scale each level button */
	public static float LEVEL_BUTTON_SCALE = 0.8f;
	/** Amount to scale the main menu title*/
	public static float MENU_SCALE = 0.38f;
	/** Amount to scale start button location vertically*/
	public static float START_V_SCALE = .80f;
	/** Amount to scale levels button location vertically*/
	public static float LEVEL_V_SCALE = .65f;
	/** Amount to scale settings button location vertically*/
	public static float SETTINGS_V_SCALE = .50f;
	/** Amount to scale credits button location vertically*/
	public static float CREDITS_V_SCALE = .35f;
	
	public Vector2 glPos;
	public float barSize;

	/** Start button for XBox controller on Windows */
	public static int WINDOWS_START = 7;
	/** Start button for XBox controller on Mac OS X */
	public static int MAC_OS_X_START = 4;

	/** AssetManager to be loading in the background */
	public AssetManager manager;
	/** Reference to GameCanvas created by the root */
	public GameCanvas canvas;
	/** Listener that will update the player mode when we are done */
	public ScreenListener listener;
	/** Where all the assets are stored */
	public AssetFile af;

	/** The width of the progress bar */
	public int width;
	/** The y-coordinate of the center of the progress bar */
	public int centerY;
	/** The x-coordinate of the center of the progress bar */
	public int centerX;
	public float tlX;
	public float tlY;
	public int widthX;
	public int centerBarX;
	public int levelSelected;
	/**
	 * The height of the canvas window (necessary since sprite origin != screen
	 * origin)
	 */
	public int heightY;
	/** Scaling factor for when the student changes the resolution. */
	public float scale;

	/** Current progress (0 to 1) of the asset manager */
	public float progress;
	/** The current state of the play button */
	public int pressState;
	/**
	 * The amount of time to devote to loading assets (as opposed to on screen
	 * hints, etc.)
	 */
	public int budget;
	/** Support for the X-Box start button in place of play button */
	public int startButton;
	/** Whether or not this player mode is still active */
	public boolean active;

	/**
	 * Returns the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each
	 * animation frame. This allows you to do something other than load assets.
	 * An animation frame is ~16 milliseconds. So if the budget is 10, you have
	 * 6 milliseconds to do something else. This is how game companies animate
	 * their loading screens.
	 *
	 * @return the budget in milliseconds
	 */
	public int getBudget() {
		return budget;
	}

	/**
	 * Sets the budget for the asset loader.
	 *
	 * The budget is the number of milliseconds to spend loading assets each
	 * animation frame. This allows you to do something other than load assets.
	 * An animation frame is ~16 milliseconds. So if the budget is 10, you have
	 * 6 milliseconds to do something else. This is how game companies animate
	 * their loading screens.
	 *
	 * @param millis
	 *            the budget in milliseconds
	 */
	public void setBudget(int millis) {
		budget = millis;
	}

	/**
	 * Returns true if all assets are loaded and the player is ready to go.
	 *
	 * @return true if the player is ready to go
	 */
	public boolean isReady() {
		return pressState == 2;
	}

	/**
	 * Creates a LoadingMode with the default budget, size and position.
	 *
	 * @param manager
	 *            The AssetManager to load in the background
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager) {
		this(canvas, manager, DEFAULT_BUDGET);
	}

	/**
	 * Creates a LoadingMode with the default size and position.
	 *
	 * The budget is the number of milliseconds to spend loading assets each
	 * animation frame. This allows you to do something other than load assets.
	 * An animation frame is ~16 milliseconds. So if the budget is 10, you have
	 * 6 milliseconds to do something else. This is how game companies animate
	 * their loading screens.
	 *
	 * @param manager
	 *            The AssetManager to load in the background
	 * @param millis
	 *            The loading budget in milliseconds
	 */
	public LoadingMode(GameCanvas canvas, AssetManager manager, int millis) {
		this.manager = manager;
		this.canvas = canvas;
		this.af = new AssetFile();
		budget = millis;

		// Compute the dimensions from the canvas
		resize(canvas.getWidth(), canvas.getHeight());

		// TODO:
		populate_default();
		
		// Load the next two images immediately.
		playButton = null;
		background = new Texture(af.get("BACKGROUND_FILE"));
		statusBar = new Texture(af.get("PROGRESS_FILE"));
		blackBack = new Texture("shared/blackBack.png");
		grayLine = new Texture("shared/grey line.png");
		whiteLine = new Texture("shared/white line.png");
		select = new Texture("shared/select level.png");
		back = new Texture("shared/back_button.png");
		editor = new Texture("shared/level editor.png");
		levelTemp = new Texture("shared/3.png");
		
		
		float ratio = (float)canvas.getWidth()/1920f;
		barSize = 1000 * ratio;
		glPos = new Vector2(canvas.getWidth()/2 - barSize/2, (float)canvas.getHeight()/8);

		// No progress so far.
		progress = 0;
		pressState = 0;
		active = false;

		// Break up the status bar texture into regions
		statusBkgLeft = new TextureRegion(statusBar, 0, 0, PROGRESS_CAP,
				PROGRESS_HEIGHT);
		statusBkgRight = new TextureRegion(statusBar,
				statusBar.getWidth() - PROGRESS_CAP, 0, PROGRESS_CAP,
				PROGRESS_HEIGHT);
		statusBkgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, 0,
				PROGRESS_MIDDLE, PROGRESS_HEIGHT);

		int offset = statusBar.getHeight() - PROGRESS_HEIGHT;
		statusFrgLeft = new TextureRegion(statusBar, 0, offset, PROGRESS_CAP,
				PROGRESS_HEIGHT);
		statusFrgRight = new TextureRegion(statusBar,
				statusBar.getWidth() - PROGRESS_CAP, offset, PROGRESS_CAP,
				PROGRESS_HEIGHT);
		statusFrgMiddle = new TextureRegion(statusBar, PROGRESS_CAP, offset,
				PROGRESS_MIDDLE, PROGRESS_HEIGHT);

		startButton = (System.getProperty("os.name").equals("Mac OS X")
				? MAC_OS_X_START : WINDOWS_START);
		Gdx.input.setInputProcessor(this);
		// Let ANY connected controller start the game.
		for (Controller controller : Controllers.getControllers()) {
			controller.addListener(this);
		}
		active = true;
	}

	/**
	 * Called when this screen should release all resources.
	 */
	public void dispose() {
		statusBkgLeft = null;
		statusBkgRight = null;
		statusBkgMiddle = null;

		statusFrgLeft = null;
		statusFrgRight = null;
		statusFrgMiddle = null;

		background.dispose();
		statusBar.dispose();
		background = null;
		statusBar = null;
		if (playButton != null) {
			playButton.dispose();
			playButton = null;
		}
	}

	/**
	 * Update the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate
	 * methods, instead of using the single render() method that LibGDX does. We
	 * will talk about why we prefer this in lecture.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 */
	public void update(float delta) {
		if (playButton == null) {
			manager.update(budget);
			this.progress = manager.getProgress();
			if (progress >= 1.0f) {
				this.progress = 1.0f;
				playButton = new Texture(PLAY_BTN_FILE);
				playButton.setFilter(TextureFilter.Linear,
					TextureFilter.Linear);
				mainMenu = new Texture(MAIN_MENU);
				mainMenu.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
				levels = new Texture(LEVELS);
				levels.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
				settings = new Texture(SETTINGS);
				settings.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
				credits = new Texture(CREDITS);
				credits.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
				
			}
		}
	}
	// TODO: POPULATE
	private float[] selectorPos;
	
	private void populate_default(){
		selectorPos=new float[40];
		for (int i = 0; i<4; i++){
			float y = tlY - i *  heightY * 3f / 20f;
			for (int j = 0; j < 5; j++){
				float x = tlX + j * widthX * 3f /  25f;
				selectorPos[2*(i*5+j)]=x;
				selectorPos[2*(i*5+j)+1]=y;
			}
		}
	}

	/**
	 * Draw the status of this player mode.
	 *
	 * We prefer to separate update and draw from one another as separate
	 * methods, instead of using the single render() method that LibGDX does. We
	 * will talk about why we prefer this in lecture.
	 */
	public void draw() {  
		canvas.resize();
		float wRatio = (float)canvas.getWidth() / 1920f;
		float hRatio = wRatio;
		canvas.begin();
		Vector2 pos = canvas.relativeVector(0, 0);
		Vector2 pos1 = canvas.relativeVector(canvas.getWidth()/8, canvas.getHeight()/3.5f);
		canvas.draw(blackBack, pos.x, pos.y);
		if (playButton == null) {
			canvas.draw(background, Color.WHITE, 0, 0, pos1.x, pos1.y, 0, wRatio, hRatio);
			drawProgress(canvas);
		} else if (pressState == 0 || pressState == 1 || pressState == 3){
			canvas.draw(background, Color.WHITE, 0, 0, pos1.x, pos1.y, 0, wRatio, hRatio);
			Color tint1 = (pressState == 1 ? Color.GRAY : Color.WHITE);
			pos = canvas.relativeVector(centerX, centerY * START_V_SCALE);
			canvas.draw(playButton, tint1, playButton.getWidth() / 2,
					playButton.getHeight() / 2,
					pos.x, pos.y, 0, BUTTON_SCALE * scale,
					BUTTON_SCALE * scale);
			pos = canvas.relativeVector(centerX, centerY);
			canvas.draw(mainMenu, Color.WHITE, mainMenu.getWidth() / 2, mainMenu.getHeight() / 2, 
					pos.x, pos.y, 0, MENU_SCALE * scale, MENU_SCALE * scale);
			pos = canvas.relativeVector(centerX, centerY * LEVEL_V_SCALE);
			canvas.draw(levels, Color.WHITE, levels.getWidth() / 2,
					levels.getHeight() / 2,
					pos.x, pos.y, 0, BUTTON_SCALE * scale,
					BUTTON_SCALE * scale);
			pos = canvas.relativeVector(centerX, centerY * SETTINGS_V_SCALE);
			canvas.draw(settings, Color.WHITE, settings.getWidth() / 2,
					settings.getHeight() / 2,
					pos.x, pos.y, 0, BUTTON_SCALE * scale,
					BUTTON_SCALE * scale);
			pos = canvas.relativeVector(centerX, centerY * CREDITS_V_SCALE);
			canvas.draw(credits, Color.WHITE, credits.getWidth() / 2,
					credits.getHeight() / 2,
					pos.x, pos.y, 0, BUTTON_SCALE * scale,
					BUTTON_SCALE * scale);
		}else if (pressState >=4){
			pos = canvas.relativeVector(canvas.getWidth()/2, canvas.getHeight()*4.25f/5);
			canvas.draw(select, Color.WHITE, select.getWidth()/2, select.getHeight()/2,
					pos.x, pos.y, 0, scale, scale);
			pos = canvas.relativeVector(canvas.getWidth()/4f, canvas.getHeight()/7f);
			canvas.draw(back, Color.WHITE, back.getWidth()/2, back.getHeight()/2,
					pos.x, pos.y, 0, BUTTON_SCALE*scale, BUTTON_SCALE*scale);
			
			for (int i=0; i<20; i++){
				String level_texture = "shared/" + (i+1) + ".png";
				level = new Texture(level_texture);
				level.setFilter(TextureFilter.Linear,
						TextureFilter.Linear);
				pos = canvas.relativeVector(selectorPos[2*i], selectorPos[2*i+1]);
				if (pressState==5 && this.levelSelected==i){
					canvas.draw(level, Color.GRAY, level.getWidth() / 2,
							level.getHeight() / 2,
							pos.x, pos.y,
							0, LEVEL_BUTTON_SCALE * scale,
							LEVEL_BUTTON_SCALE * scale);
				}
				else{
					canvas.draw(level, Color.WHITE, level.getWidth() / 2,
							level.getHeight() / 2,
							pos.x, pos.y,
							0, LEVEL_BUTTON_SCALE * scale,
							LEVEL_BUTTON_SCALE * scale);
				}
			}
//			int n = 1;
//			for (int i = 0; i<4; i++){
////				float y = tlY - i *  heightY * 3 / 20;
//				for (int j = 0; j < 5; j++){
//					String level_texture = "shared/" + n + ".png";
//					level = new Texture(level_texture);
//					level.setFilter(TextureFilter.Linear,
//							TextureFilter.Linear);
////					float x = tlX + j * widthX * 3 /  25;
////					pos = canvas.relativeVector(x, y);
//					canvas.draw(level, Color.WHITE, level.getWidth() / 2,
//							level.getHeight() / 2,
//							selectorPos[2*(5*i+j)], selectorPos[2*(5*i+j)+1], 0, LEVEL_BUTTON_SCALE * scale,
//							LEVEL_BUTTON_SCALE * scale);
//					n++;
//				}
//			}
		}
		canvas.end();
	}

	/**
	 * Updates the progress bar according to loading progress
	 *
	 * The progress bar is composed of parts: two rounded caps on the end, and a
	 * rectangle in a middle. We adjust the size of the rectangle in the middle
	 * to represent the amount of progress.
	 *
	 * @param canvas
	 *            The drawing context
	 */
	public void drawProgress(GameCanvas canvas) {
		Vector2 pos = canvas.relativeVector(glPos.x, glPos.y);
		canvas.draw(grayLine, Color.WHITE, pos.x, pos.y, barSize, 13);
		canvas.draw(whiteLine, Color.WHITE, pos.x, pos.y, progress*barSize, 13);
	}

	// ADDITIONAL SCREEN METHODS
	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw(). However, it is VERY
	 * important that we only quit AFTER a draw.
	 *
	 * @param delta
	 *            Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			update(delta);
			draw();

			// We are are ready, notify our listener
			if (isReady() && listener != null && pressState != 6) {
				listener.exitScreen(this, 0);
			}
			if (levelSelected != -1 && pressState == 6){
				int temp = levelSelected;
				levelSelected = -1;
				listener.exitScreen(this, temp);
			}
		}
	}

	/**
	 * Called when the Screen is resized.
	 *
	 * This can happen at any point during a non-paused state but will never
	 * happen before a call to show().
	 *
	 * @param width
	 *            The new width in pixels
	 * @param height
	 *            The new height in pixels
	 */
	public void resize(int width, int height) {
		// Compute the drawing scale
		float sx = ((float) width) / STANDARD_WIDTH;
		float sy = ((float) height) / STANDARD_HEIGHT;
		scale = (sx < sy ? sx : sy);

		this.width = (int) (BAR_WIDTH_RATIO * width);
		centerY = (int) (.80 * height);
		centerX =  2 * width / 3;
		tlX = width * 1.25f / 5;
		tlY = height * 3.65f / 5;
		heightY = height;
		centerBarX = (int) (width/2);
		widthX = width;
		
		// TODO:
		populate_default();
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application
	 * is also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}

	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}

	// PROCESSING PLAYER INPUT
	/**
	 * Called when the screen was touched or a mouse button was pressed.
	 *
	 * This method checks to see if the play button is available and if the
	 * click is in the bounds of the play button. If so, it signals the that the
	 * button has been pressed and is currently down. Any mouse button is
	 * accepted.
	 *
	 * @param screenX
	 *            the x-coordinate of the mouse on the screen
	 * @param screenY
	 *            the y-coordinate of the mouse on the screen
	 * @param pointer
	 *            the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (playButton == null || pressState == 2) {
			return true;
		}

		// Flip to match graphics coordinates
		screenY = heightY - screenY;

		// TODO: Fix scaling
		// Play button is a Rectangle.
		float width = BUTTON_SCALE * scale * playButton.getWidth();
		float height = BUTTON_SCALE * scale * playButton.getHeight();

		if (pressState == 0 && centerX - width/2 < screenX && centerX + width/2 > screenX && centerY * START_V_SCALE - height/2 < screenY && centerY * START_V_SCALE + height/2 > screenY ){
			pressState = 1;
		}
		if (pressState == 0 && centerX - width/2 < screenX && centerX + width/2 > screenX && centerY * LEVEL_V_SCALE - height/2 < screenY && centerY * LEVEL_V_SCALE + height/2 > screenY ){
			pressState = 3;
		}
		width = LEVEL_BUTTON_SCALE * scale * levelTemp.getWidth();
		height = LEVEL_BUTTON_SCALE * scale * levelTemp.getHeight();
		if (pressState == 4){
//			for (int i = 0; i < 4; i++){
//				float y = tlY - i *  heightY * 3 / 20;
//				for (int j = 0; j < 5; j++){
//					float x = tlX + j * widthX * 3 /  25;
//					if (x - width/2 < screenX && x + width/2 > screenX && y - height/2 < screenY && y + height/2 > screenY){
//						pressState = 5;
//						levelSelected = i*5 + j;
//					}
//				}
//			}
			for (int i = 0; i < 20; i++){
					if (selectorPos[2*i] - width/2 < screenX && selectorPos[2*i] + width/2 > screenX 
							&& selectorPos[2*i+1] - height/2 < screenY && selectorPos[2*i+1] + height/2 > screenY){
						pressState = 5;
						levelSelected = i;
					}
			}
			float x = widthX/4f;
			float y = heightY/7f;
			width = BUTTON_SCALE * scale * back.getWidth();
			height = BUTTON_SCALE * scale * back.getHeight();
			if (x - width/2 < screenX && x + width/2 > screenX && y - height/2 < screenY && y + height/2 > screenY){
				pressState = 0;
			}
			
		}
		return false;
	}

	/**
	 * Called when a finger was lifted or a mouse button was released.
	 *
	 * This method checks to see if the play button is currently pressed down.
	 * If so, it signals the that the player is ready to go.
	 *
	 * @param screenX
	 *            the x-coordinate of the mouse on the screen
	 * @param screenY
	 *            the y-coordinate of the mouse on the screen
	 * @param pointer
	 *            the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (pressState == 1) {
			pressState = 2;
			return false;
		}
		if (pressState == 3) {
			pressState = 4;
			return false;
		}
		if (pressState == 5){
			pressState = 6;
			return false;
		}
		return true;
	}

	/**
	 * Called when a button on the Controller was pressed.
	 *
	 * The buttonCode is controller specific. This listener only supports the
	 * start button on an X-Box controller. This outcome of this method is
	 * identical to pressing (but not releasing) the play button.
	 *
	 * @param controller
	 *            The game controller
	 * @param buttonCode
	 *            The button pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean buttonDown(Controller controller, int buttonCode) {
		if (buttonCode == startButton && pressState == 0) {
			pressState = 1;
			return false;
		}
		return true;
	}

	/**
	 * Called when a button on the Controller was released.
	 *
	 * The buttonCode is controller specific. This listener only supports the
	 * start button on an X-Box controller. This outcome of this method is
	 * identical to releasing the the play button after pressing it.
	 *
	 * @param controller
	 *            The game controller
	 * @param buttonCode
	 *            The button pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean buttonUp(Controller controller, int buttonCode) {
		if (pressState == 1 && buttonCode == startButton) {
			pressState = 2;
			return false;
		}
		return true;
	}

	// UNSUPPORTED METHODS FROM InputProcessor

	/**
	 * Called when a key is pressed (UNSUPPORTED)
	 *
	 * @param keycode
	 *            the key pressed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyDown(int keycode) {
		return true;
	}

	/**
	 * Called when a key is typed (UNSUPPORTED)
	 *
	 * @param keycode
	 *            the key typed
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyTyped(char character) {
		return true;
	}

	/**
	 * Called when a key is released.
	 * 
	 * We allow key commands to start the game this time.
	 *
	 * @param keycode
	 *            the key released
	 * @return whether to hand the event to other listeners.
	 */
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.N || keycode == Input.Keys.P) {
			pressState = 2;
			return false;
		}
		return true;
	}

	/**
	 * Called when the mouse was moved without any buttons being pressed.
	 * (UNSUPPORTED)
	 *
	 * @param screenX
	 *            the x-coordinate of the mouse on the screen
	 * @param screenY
	 *            the y-coordinate of the mouse on the screen
	 * @return whether to hand the event to other listeners.
	 */
	public boolean mouseMoved(int screenX, int screenY) {
		return true;
	}

	/**
	 * Called when the mouse wheel was scrolled. (UNSUPPORTED)
	 *
	 * @param amount
	 *            the amount of scroll from the wheel
	 * @return whether to hand the event to other listeners.
	 */
	public boolean scrolled(int amount) {
		return true;
	}

	/**
	 * Called when the mouse or finger was dragged. (UNSUPPORTED)
	 *
	 * @param screenX
	 *            the x-coordinate of the mouse on the screen
	 * @param screenY
	 *            the y-coordinate of the mouse on the screen
	 * @param pointer
	 *            the button or touch finger number
	 * @return whether to hand the event to other listeners.
	 */
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return true;
	}

	// UNSUPPORTED METHODS FROM ControllerListener

	/**
	 * Called when a controller is connected. (UNSUPPORTED)
	 *
	 * @param controller
	 *            The game controller
	 */
	public void connected(Controller controller) {
	}

	/**
	 * Called when a controller is disconnected. (UNSUPPORTED)
	 *
	 * @param controller
	 *            The game controller
	 */
	public void disconnected(Controller controller) {
	}

	/**
	 * Called when an axis on the Controller moved. (UNSUPPORTED)
	 *
	 * The axisCode is controller specific. The axis value is in the range [-1,
	 * 1].
	 *
	 * @param controller
	 *            The game controller
	 * @param axisCode
	 *            The axis moved
	 * @param value
	 *            The axis value, -1 to 1
	 * @return whether to hand the event to other listeners.
	 */
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return true;
	}

	/**
	 * Called when a POV on the Controller moved. (UNSUPPORTED)
	 *
	 * The povCode is controller specific. The value is a cardinal direction.
	 *
	 * @param controller
	 *            The game controller
	 * @param povCode
	 *            The POV controller moved
	 * @param value
	 *            The direction of the POV
	 * @return whether to hand the event to other listeners.
	 */
	public boolean povMoved(Controller controller, int povCode,
			PovDirection value) {
		return true;
	}

	/**
	 * Called when an x-slider on the Controller moved. (UNSUPPORTED)
	 *
	 * The x-slider is controller specific.
	 *
	 * @param controller
	 *            The game controller
	 * @param sliderCode
	 *            The slider controller moved
	 * @param value
	 *            The direction of the slider
	 * @return whether to hand the event to other listeners.
	 */
	public boolean xSliderMoved(Controller controller, int sliderCode,
			boolean value) {
		return true;
	}

	/**
	 * Called when a y-slider on the Controller moved. (UNSUPPORTED)
	 *
	 * The y-slider is controller specific.
	 *
	 * @param controller
	 *            The game controller
	 * @param sliderCode
	 *            The slider controller moved
	 * @param value
	 *            The direction of the slider
	 * @return whether to hand the event to other listeners.
	 */
	public boolean ySliderMoved(Controller controller, int sliderCode,
			boolean value) {
		return true;
	}

	/**
	 * Called when an accelerometer value on the Controller changed.
	 * (UNSUPPORTED)
	 * 
	 * The accelerometerCode is controller specific. The value is a Vector3
	 * representing the acceleration on a 3-axis accelerometer in m/s^2.
	 *
	 * @param controller
	 *            The game controller
	 * @param accelerometerCode
	 *            The accelerometer adjusted
	 * @param value
	 *            A vector with the 3-axis acceleration
	 * @return whether to hand the event to other listeners.
	 */
	public boolean accelerometerMoved(Controller controller,
			int accelerometerCode, Vector3 value) {
		return true;
	}

}