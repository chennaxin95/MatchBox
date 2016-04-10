package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class LadderBlock extends Platform {

	private TextureRegion ropeTexture;	
	private float shrinkingRate;
	
	private float rlength, rwidth, runit;
	private PolygonRegion ropeRegion;
	
	private boolean isShrinking;
	
	
	public LadderBlock(float x, float y, float bwidth, float bheight, float unit,
			float rlength,float rwidth, float runit, float rate) {
		super(new Rectangle(x-bwidth/2, y-bheight/2, bwidth, bheight), unit);
		
		this.rlength=rlength;
		this.rwidth=rwidth;
		this.runit=runit;
		this.shrinkingRate=rate;
		// TODO Auto-generated constructor stub
		material.setClimbable(true);
	}
	
	public void setRopeUnitTexture(TextureRegion rtexture){
		this.ropeTexture=rtexture;
		int numY=Math.round(rlength/runit);
		System.out.println(numY);
		float[] vertices=new float[numY*8];
		short[] tridx=new short[numY*6];
		for (int i=0; i<1; i++){
			for (int j=0; j<numY; j++){
				vertices[8*(i*numY+j)]=i*rtexture.getRegionWidth();
				vertices[8*(i*numY+j)+1]=j*rtexture.getRegionHeight();
				vertices[8*(i*numY+j)+2]=(i+1)*rtexture.getRegionWidth();
				vertices[8*(i*numY+j)+3]=j*rtexture.getRegionHeight();	
				vertices[8*(i*numY+j)+4]=(i+1)*rtexture.getRegionWidth();
				vertices[8*(i*numY+j)+5]=(j+1)*rtexture.getRegionHeight();
				vertices[8*(i*numY+j)+6]=i*rtexture.getRegionWidth();	
				vertices[8*(i*numY+j)+7]=(j+1)*rtexture.getRegionHeight();
				tridx[6*(i*numY+j)]=(short) (4*(i*numY+j));
				tridx[6*(i*numY+j)+1]=(short) (4*(i*numY+j)+3);
				tridx[6*(i*numY+j)+2]=(short) (4*(i*numY+j)+2);
				tridx[6*(i*numY+j)+3]=(short) (4*(i*numY+j)+2);	
				tridx[6*(i*numY+j)+4]=(short) (4*(i*numY+j)+1);
				tridx[6*(i*numY+j)+5]=(short) (4*(i*numY+j));
			}
		}		
		ropeRegion=new PolygonRegion(rtexture, vertices, tridx);
	}
	public TextureRegion getRopeUnitTexture(){
		return ropeTexture;
	}
	
	public void startBurning(float distFromBottom){
		if (rlength>0){
			this.rlength-=distFromBottom;
			this.isShrinking=true;
		}
	}
	
	public void update(float dt){
		if (isShrinking){
			rlength-=dt*shrinkingRate;
		}
		if (rlength<0){
			isShrinking=false;
			rlength=0;
		}
	}
	
	public Vector2 getBottomEnd(){
		return new Vector2(this.getX(), this.getY()-this.getHeight()/2-rlength);
	}
	
	@Override
	public void draw(GameCanvas canvas){
		super.draw(canvas);
		System.out.println(getBottomEnd());
		if (ropeRegion != null) {
			canvas.draw(ropeRegion,Color.WHITE, 0,0,
					(getX()-rwidth/2)*drawScale.x,
					(getY()-rlength)*drawScale.y,
					getAngle(), 1, 1);
		}	
	}
}
