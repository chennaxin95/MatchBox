/*
 * InputController.java
 *
 * This class buffers in input from the devices and converts it into its
 * semantic meaning. If your game had an option that allows the player to
 * remap the control keys, you would store this information in this class.
 * That way, the main GameEngine does not have to keep track of the current
 * key mapping.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac.physics;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;

import edu.cornell.gdiac.util.*;

/**
 * Class for reading player input.
 *
 * This supports both a keyboard and X-Box controller. In previous solutions, we
 * only detected the X-Box controller on start-up. This class allows us to
 * hot-swap in a controller via the new XBox360Controller class.
 */
public class InputController {
	// Sensitivity for moving crosshair with gameplay
	private static final float GP_ACCELERATE = 1.0f;
	private static final float GP_MAX_SPEED = 10.0f;
	private static final float GP_THRESHOLD = 0.01f;

	/** The singleton instance of the input controller */
	private static InputController theController = null;

	/**
	 * Return the singleton instance of the input controller
	 *
	 * @return the singleton instance of the input controller
	 */
	public static InputController getInstance() {
		if (theController == null) {
			theController = new InputController();
		}
		return theController;
	}

	// Fields to manage buttons
	/** Whether the reset button was pressed. */
	private boolean resetPressed;
	private boolean resetPrevious;
	/** Whether the button to advanced worlds was pressed. */
	private boolean nextPressed;
	private boolean nextPrevious;
	/** Whether the button to step back worlds was pressed. */
	private boolean prevPressed;
	private boolean prevPrevious;
	/** Whether the primary action button was pressed. */
	private boolean primePressed;
	/** Whether the secondary action button was pressed. */
	private boolean secondPressed;
	private boolean secondPrevious;
	/** Whether the teritiary action button was pressed. */
	private boolean tertiaryPressed;
	/** Whether the debug toggle was pressed. */
	private boolean debugPressed;
	private boolean debugPrevious;
	/** Whether the exit button was pressed. */
	private boolean exitPressed;
	private boolean exitPrevious;
	private boolean didPause;

	/** Whether spirit mode was toggled */
	private boolean spiritPressed;
	private boolean spiritPrevious;

	/** How much did we move horizontally? */
	private float horizontal;
	/** How much did we move vertically? */
	private float vertical;
	/** The crosshair position (for raddoll) */
	private Vector2 crosshair;
	/** The crosshair cache (for using as a return value) */
	private Vector2 crosscache;
	/** For the gamepad crosshair control */
	private float momentum;
	
	/** An X-Box controller (if it is connected) */
	XBox360Controller xbox;
	
	private boolean hasLeftClicked;
	private boolean leftClicked;
	public boolean newLeftClick(){
		return leftClicked && !hasLeftClicked;
	}
	
	public Vector2 mousePos;
	public int inputNumber;
	
	private boolean hasNewCharacterPressed;
	private boolean newCharacterPressed;
	
	public boolean newCharacter(){
		return newCharacterPressed && !hasNewCharacterPressed;
	}
	
	private boolean hasNewBlockPressed;
	private boolean newBlockPressed;
	
	public boolean newBlock(){
		return newBlockPressed && !hasNewBlockPressed;
	}
	
	private boolean hasRemovePressed;
	private boolean removePressed;
	
	public boolean toRemove(){
		return removePressed && !hasRemovePressed;
	}
	
	private boolean newAidenPressed;
	private boolean hasNewAidenPressed;
	
	public boolean newAiden(){
		return newAidenPressed && !hasNewAidenPressed;
	}
	
	private boolean hasPolyPressed;
	private boolean polyPressed;
	
	public boolean switchPolyMode(){
		return polyPressed && !hasPolyPressed;
	}
	
	private boolean hasExportPressed;
	private boolean exportPressed;
	
	public boolean toExport(){
		return exportPressed && !hasExportPressed;
	}
	
	private boolean hasLoadPressed;
	private boolean loadPressed;
	
	public boolean toLoad(){
		return loadPressed && !hasLoadPressed;
	}
	
	
	/**
	 * Returns the amount of sideways movement.
	 *
	 * -1 = left, 1 = right, 0 = still
	 *
	 * @return the amount of sideways movement.
	 */
	public float getHorizontal() {
		return horizontal;
	}

	/**
	 * Returns the amount of vertical movement.
	 *
	 * -1 = down, 1 = up, 0 = still
	 *
	 * @return the amount of vertical movement.
	 */
	public float getVertical() {
		return vertical;
	}

	/**
	 * Returns the current position of the crosshairs on the screen.
	 *
	 * This value does not return the actual reference to the crosshairs
	 * position. That way this method can be called multiple times without any
	 * fair that the position has been corrupted. However, it does return the
	 * same object each time. So if you modify the object, the object will be
	 * reset in a subsequent call to this getter.
	 *
	 * @return the current position of the crosshairs on the screen.
	 */
	public Vector2 getCrossHair() {
		return crosscache.set(crosshair);
	}

	/**
	 * Returns true if the primary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the primary action button was pressed.
	 */
	public boolean didPrimary() {
		return primePressed; //&& !primePrevious;
	}

	/**
	 * Returns true if the secondary action button was pressed.
	 *
	 * This is a one-press button. It only returns true at the moment it was
	 * pressed, and returns false at any frame afterwards.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didSecondary() {
		return secondPressed && !secondPrevious;
	}

	/**
	 * Returns true if the tertiary action button was pressed.
	 *
	 * This is a sustained button. It will returns true as long as the player
	 * holds it down.
	 *
	 * @return true if the secondary action button was pressed.
	 */
	public boolean didTertiary() {
		return tertiaryPressed;
	}

	/**
	 * Returns true if the reset button was pressed.
	 *
	 * @return true if the reset button was pressed.
	 */
	public boolean didReset() {
//		return false;
		return resetPressed && !resetPrevious;
	}

	/**
	 * Returns true if the player wants to go to the next level.
	 *
	 * @return true if the player wants to go to the next level.
	 */
	public boolean didAdvance() {
//		return false;
		return nextPressed && !nextPrevious;
	}

	/**
	 * Returns true if the player wants to go to the previous level.
	 *
	 * @return true if the player wants to go to the previous level.
	 */
	public boolean didRetreat() {
//		return false;
		return prevPressed && !prevPrevious;
	}

	/**
	 * Returns true if the player wants to go toggle the debug mode.
	 *
	 * @return true if the player wants to go toggle the debug mode.
	 */
	public boolean didDebug() {
		return debugPressed && !debugPrevious;
	}

	/**
	 * Returns true if the exit button was pressed.
	 *
	 * @return true if the exit button was pressed.
	 */
	public boolean didExit() {
		return exitPressed && !exitPrevious;
	}

	/**
	 * Returns true if the spirit button was pressed.
	 *
	 * @return true if the spirit button was pressed.
	 */
	public boolean didSpirit() {
		return spiritPressed && !spiritPrevious;
	}
	
	private boolean pausePrevious;
	public boolean didPause(){
		return this.didPause && !pausePrevious;
	}

	/**
	 * Creates a new input controller
	 * 
	 * The input controller attempts to connect to the X-Box controller at
	 * device 0, if it exists. Otherwise, it falls back to the keyboard control.
	 */
	public InputController() {
		// If we have a game-pad for id, then use it.
		xbox = new XBox360Controller(0);
		crosshair = new Vector2();
		crosscache = new Vector2();
	}

	/**
	 * Reads the input for the player and converts the result into game logic.
	 *
	 * The method provides both the input bounds and the drawing scale. It needs
	 * the drawing scale to convert screen coordinates to world coordinates. The
	 * bounds are for the crosshair. They cannot go outside of this zone.
	 *
	 * @param bounds
	 *            The input bounds for the crosshair.
	 * @param scale
	 *            The drawing scale
	 */
	public void readInput(Rectangle bounds, Vector2 scale) {
		// Copy state from last animation frame
		// Helps us ignore buttons that are held down
		secondPrevious = secondPressed;
		resetPrevious = resetPressed;
		debugPrevious = debugPressed;
		exitPrevious = exitPressed;
		nextPrevious = nextPressed;
		prevPrevious = prevPressed;
		spiritPrevious = spiritPressed;
		pausePrevious = didPause;
		
		hasNewCharacterPressed=newCharacterPressed;
		hasNewBlockPressed=newBlockPressed;	
		hasNewAidenPressed=newAidenPressed;	
		this.hasLoadPressed=this.loadPressed;
		this.hasExportPressed=this.exportPressed;
		this.hasPolyPressed=this.polyPressed;
		this.hasRemovePressed=this.removePressed;
		hasLeftClicked=this.leftClicked;
		// Check to see if a GamePad is connected
		if (xbox.isConnected()) {
			readGamepad(bounds, scale);
			readKeyboard(bounds, scale, true); // Read as a back-up
		} else {
			readKeyboard(bounds, scale, false);
		}
	}

	/**
	 * Reads input from an X-Box controller connected to this computer.
	 *
	 * The method provides both the input bounds and the drawing scale. It needs
	 * the drawing scale to convert screen coordinates to world coordinates. The
	 * bounds are for the crosshair. They cannot go outside of this zone.
	 *
	 * @param bounds
	 *            The input bounds for the crosshair.
	 * @param scale
	 *            The drawing scale
	 */
	private void readGamepad(Rectangle bounds, Vector2 scale) {
		resetPressed = xbox.getStart();
		exitPressed = xbox.getBack();
		nextPressed = xbox.getRB();
		prevPressed = xbox.getLB();
		primePressed = xbox.getA();
		debugPressed = xbox.getY();

		// Increase animation frame, but only if trying to move
		horizontal = xbox.getLeftX();
		vertical = xbox.getLeftY();
		secondPressed = xbox.getRightTrigger() > 0.6f;

		// Move the crosshairs with the right stick.
		tertiaryPressed = xbox.getA();
		crosscache.set(xbox.getLeftX(), xbox.getLeftY());
		if (crosscache.len2() > GP_THRESHOLD) {
			momentum += GP_ACCELERATE;
			momentum = Math.min(momentum, GP_MAX_SPEED);
			crosscache.scl(momentum);
			crosscache.scl(1 / scale.x, 1 / scale.y);
			crosshair.add(crosscache);
		} else {
			momentum = 0;
		}
		clampPosition(bounds);
	}

	/**
	 * Reads input from the keyboard.
	 *
	 * This controller reads from the keyboard regardless of whether or not an
	 * X-Box controller is connected. However, if a controller is connected,
	 * this method gives priority to the X-Box controller.
	 *
	 * @param secondary
	 *            true if the keyboard should give priority to a gamepad
	 */
	private void readKeyboard(Rectangle bounds, Vector2 scale,
			boolean secondary) {
		// Give priority to gamepad results
		resetPressed = (secondary && resetPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.R));
		debugPressed = false;
		primePressed = (secondary && primePressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.UP)
				|| (Gdx.input.isKeyPressed(Input.Keys.W)));
		secondPressed = (secondary && secondPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.SPACE));
		prevPressed = false;
		nextPressed = false;
		didPause = (secondary && exitPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.ESCAPE));
		
//		exitPressed = (secondary && exitPressed)
//				||Gdx.input.isKeyPressed(Input.Keys.BACKSPACE);
		
		// Unable to detect redundance here
		leftClicked =  (secondary && nextPressed) || 
				(Gdx.input.isButtonPressed(Input.Buttons.LEFT));
		mousePos =  new Vector2(Gdx.input.getX(), Gdx.input.getY());
		
		// Can check here
		newCharacterPressed =(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.C) 
						&& !Gdx.input.isKeyJustPressed(Input.Keys.C));	
		newBlockPressed =(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.B) 
						&& !Gdx.input.isKeyJustPressed(Input.Keys.B));
		
		removePressed=(secondary && nextPressed)
		|| (Gdx.input.isKeyPressed(Input.Keys.DEL)
				&& !Gdx.input.isKeyJustPressed(Input.Keys.DEL));

		newAidenPressed=(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.A)
						&& !Gdx.input.isKeyJustPressed(Input.Keys.A));
		polyPressed=(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.Y)
						&& !Gdx.input.isKeyJustPressed(Input.Keys.Y));		
		
		exportPressed=(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.E)
						&& !Gdx.input.isKeyJustPressed(Input.Keys.E));		
		loadPressed=(secondary && nextPressed)
				|| (Gdx.input.isKeyPressed(Input.Keys.L)
						&& Gdx.input.isKeyJustPressed(Input.Keys.L));	
		
		inputNumber=-1;
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)){
			inputNumber=0;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)){
			inputNumber=1;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)){
			inputNumber=2;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)){
			inputNumber=3;
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)){
			inputNumber=4;
		}
		
		// Directional controls
		horizontal = (secondary ? horizontal : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)||
				Gdx.input.isKeyPressed(Input.Keys.D)) {
			horizontal += 1.0f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
				Gdx.input.isKeyPressed(Input.Keys.A)) {
			horizontal -= 1.0f;
		}

		vertical = (secondary ? vertical : 0.0f);
		if (Gdx.input.isKeyPressed(Input.Keys.UP) ||
				Gdx.input.isKeyPressed(Input.Keys.W)) {
			vertical += 1f;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN) ||
				Gdx.input.isKeyPressed(Input.Keys.S)) {
			vertical -= 1f;
		}

		// Mouse results
		tertiaryPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
		crosshair.set(Gdx.input.getX(), Gdx.input.getY());
	}
	

	/**
	 * Clamp the cursor position so that it does not go outside the window
	 *
	 * While this is not usually a problem with mouse control, this is critical
	 * for the gamepad controls.
	 */
	private void clampPosition(Rectangle bounds) {
		crosshair.x = Math.max(bounds.x,
				Math.min(bounds.x + bounds.width, crosshair.x));
		crosshair.y = Math.max(bounds.y,
				Math.min(bounds.y + bounds.height, crosshair.y));
	}
	
	
	public boolean zoomIn(){
		if(Gdx.input.isKeyPressed(Input.Keys.I)){
			return true;
		}
		return false;
	}
	
	public boolean back(){
		if(Gdx.input.isKeyPressed(Input.Keys.B)){
			return true;
		}
		return false;
	}
	
	public boolean zoomOut(){
		if(Gdx.input.isKeyPressed(Input.Keys.O)){
			return true;
		}
		return false;
	}
}