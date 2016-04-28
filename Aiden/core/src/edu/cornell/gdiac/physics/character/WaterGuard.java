package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.util.FilmStrip;

public class WaterGuard extends CharacterModel{
	
	private boolean finishDraw = false;
	private FilmStrip death;
	private ParticleEffect putOutLeft;

	public WaterGuard(CharacterType t, String name, float x, float y, float width, float height, boolean fright) {
		super(t, name, x, y, width, height, fright);
		this.setDensity(1f);
		putOutLeft = new ParticleEffect();
		putOutLeft.load(Gdx.files.internal("platform/left.p"),
				Gdx.files.internal("platform"));
		// TODO Auto-generated constructor stub
	}
	
	public boolean finishDraw(){
		return finishDraw;
	}
	
	private boolean isDead = false;
	
	public boolean isDead(){
		return isDead;
	}
	
	public void setDead(boolean b){
		isDead = b;
	}
	
	public void setDeath(FilmStrip die){
		this.death = die;
		death.setFrame(0);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		if(finishDraw){
			this.markRemoved(true);
		}
	}
	
	public void drawDead(GameCanvas canvas){
		if(death.getFrame() == death.getSize()-1){
			finishDraw = true;
		}
		
		if (this.animeCoolDown <= 0) {
			animeCoolDown = MAX_ANIME_TIME;
			death.setFrame(Math.min(death.getFrame() + 1, 11));
		}

		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		Color c = Color.WHITE;
		canvas.draw(death, c, ox, oy, getX() * drawScale.x,
				getY() * drawScale.y, getAngle(), effect, 1f);
	}
}
