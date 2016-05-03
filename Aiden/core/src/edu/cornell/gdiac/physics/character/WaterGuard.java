package edu.cornell.gdiac.physics.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.character.FSMNode.BasicFSMState;
import edu.cornell.gdiac.util.FilmStrip;

public class WaterGuard extends CharacterModel{
	
	private boolean finishDraw = false;
	private FilmStrip death;
	private FilmStrip chase;
	private ParticleEffect putOutLeft;
	private ParticleEffect putOutRight;

	public WaterGuard(CharacterType t, String name, float x, float y, float width, float height, boolean fright) {
		super(t, name, x, y, width, height, fright);
		this.setDensity(1f);
		putOutLeft = new ParticleEffect();
		putOutLeft.load(Gdx.files.internal("platform/putout.p"),
				Gdx.files.internal("platform"));
		putOutLeft.setPosition(getX() * drawScale.x,
					getY() * drawScale.y);
		putOutRight = new ParticleEffect();
		putOutRight.load(Gdx.files.internal("platform/putoutn.p"),
				Gdx.files.internal("platform"));
		putOutRight.setPosition(getX() * drawScale.x,
					getY() * drawScale.y);
	}
	
	public boolean finishDraw(){
		return finishDraw;
	}
	
	public void setChase(FilmStrip chase){
		this.chase = chase;
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
		putOutLeft.update(dt);
		putOutLeft.setPosition(getX() * drawScale.x,
				getY() * drawScale.y);
		putOutRight.update(dt);
		putOutRight.setPosition(getX() * drawScale.x,
				getY() * drawScale.y);
	}
	
	@Override
	public void draw(GameCanvas canvas){
		if(this.getStateMachine().getCurrentState()==BasicFSMState.CHASE){
			if(faceRight){
				canvas.drawParticle(putOutRight);
			}
			else{
				canvas.drawParticle(putOutLeft);
			}
			animateChase(canvas, Color.WHITE, 1, 1);
		}
		else{
			super.draw(canvas);
		}
	}
	
	@Override
	public void animate(GameCanvas canvas, Color c, float sx, float sy){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			characterSprite.setFrame((characterSprite.getFrame()+1)%characterSprite.getSize());
		}
		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		
		canvas.draw(characterSprite, c, ox, oy+0.5f, getX() * drawScale.x, 
				getY() * drawScale.y + 15, getAngle(), effect*sx, sy);
	}
	
	public void animateChase(GameCanvas canvas, Color c, float sx, float sy){
		if (this.animeCoolDown<=0) {
			animeCoolDown=MAX_ANIME_TIME;
			chase.setFrame((chase.getFrame()+1)%chase.getSize());
		}
		// For placement purposes, put origin in center.
		float ox = 0.5f * characterSprite.getRegionWidth();
		float oy = 0.5f * characterSprite.getRegionHeight();

		float effect = faceRight ? 1.0f : -1.0f;
		
		canvas.draw(chase, c, ox, oy, getX() * drawScale.x, 
				getY() * drawScale.y + 15, getAngle(), effect*sx, sy);
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
				getY() * drawScale.y + 18, getAngle(), effect, 1f);
	}
}
