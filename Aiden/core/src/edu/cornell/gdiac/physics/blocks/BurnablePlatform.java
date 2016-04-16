package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;

import edu.cornell.gdiac.physics.GameCanvas;

public class BurnablePlatform extends FlammableBlock {
	
	private PolygonRegion region;
	protected Rectangle r;
	private float unit;
	
	public BurnablePlatform(Rectangle r, float unit) {
		super(r.x+0.5f*r.getWidth(), r.y+0.5f*r.getHeight(), 
				r.getWidth(), r.getHeight());
		this.r=r;
		this.unit=unit;
		setBodyType(BodyDef.BodyType.StaticBody);
		this.setBlockType(BlockType.PLATFORM);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setTexture(TextureRegion texture){
		this.texture=texture;
		int numX=Math.round(getWidth()/unit);
		int numY=Math.round(getHeight()/unit);
		float[] vertices=new float[numX*numY*8];
		short[] tridx=new short[numX*numY*6];
		for (int i=0; i< numX; i++){
			for (int j=0; j<numY; j++){
				vertices[8*(i*numY+j)]=i*texture.getRegionWidth();
				vertices[8*(i*numY+j)+1]=j*texture.getRegionHeight();
				vertices[8*(i*numY+j)+2]=(i+1)*texture.getRegionWidth();
				vertices[8*(i*numY+j)+3]=j*texture.getRegionHeight();	
				vertices[8*(i*numY+j)+4]=(i+1)*texture.getRegionWidth();
				vertices[8*(i*numY+j)+5]=(j+1)*texture.getRegionHeight();
				vertices[8*(i*numY+j)+6]=i*texture.getRegionWidth();	
				vertices[8*(i*numY+j)+7]=(j+1)*texture.getRegionHeight();
				tridx[6*(i*numY+j)]=(short) (4*(i*numY+j));
				tridx[6*(i*numY+j)+1]=(short) (4*(i*numY+j)+3);
				tridx[6*(i*numY+j)+2]=(short) (4*(i*numY+j)+2);
				tridx[6*(i*numY+j)+3]=(short) (4*(i*numY+j)+2);	
				tridx[6*(i*numY+j)+4]=(short) (4*(i*numY+j)+1);
				tridx[6*(i*numY+j)+5]=(short) (4*(i*numY+j));
			}
		}		
		region=new PolygonRegion(texture, vertices, tridx);
	}
	@Override
	public void draw(GameCanvas canvas){
		if (region != null) {
			canvas.draw(region,Color.WHITE,0,0,
					(getX()-getWidth()/2)*drawScale.x,
					(getY()-getHeight()/2)*drawScale.y,
					getAngle(),1, 1);
		}
	}

}
