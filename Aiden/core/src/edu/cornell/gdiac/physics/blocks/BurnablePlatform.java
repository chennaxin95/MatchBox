package edu.cornell.gdiac.physics.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.material.Flammable;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

public class BurnablePlatform extends FlammableBlock {

	private PolygonRegion region;
	protected Rectangle r;
	private float unit;
	private WheelObstacle pin;
	private RevoluteJoint joint;

	public BurnablePlatform(Rectangle r, float unit, World world) {
		super(r.x + 0.5f * r.getWidth(), r.y + 0.5f * r.getHeight(),
				r.getWidth(), r.getHeight(), 1, 3);
		this.r = r;
		this.unit = unit;
//		setBodyType(BodyDef.BodyType.DynamicBody);
//		this.setFixedRotation(true);
//		this.setBlockType(BlockType.BURNABLE_PLATFORM);
//		this.pin = new WheelObstacle(this.getX(), this.getY(), 0.1f);
		setBodyType(BodyDef.BodyType.StaticBody);
//		RevoluteJointDef def = new RevoluteJointDef();
//
//		def.bodyA = this.getBody();
//		def.bodyB = pin.getBody();
//		def.localAnchorA.set(new Vector2());
//		def.localAnchorB.set(new Vector2());
//		def.collideConnected = false;
//		joint = (RevoluteJoint) world.createJoint(def);

		// TODO Auto-generated constructor stub
	}

	@Override
	public void setTexture(TextureRegion texture) {
		this.texture = texture;
		int numX = Math.round(getWidth() / unit);
		int numY = Math.round(getHeight() / unit);
		float[] vertices = new float[numX * numY * 8];
		short[] tridx = new short[numX * numY * 6];
		for (int i = 0; i < numX; i++) {
			for (int j = 0; j < numY; j++) {
				vertices[8 * (i * numY + j)] = i * texture.getRegionWidth();
				vertices[8 * (i * numY + j) + 1] = j
						* texture.getRegionHeight();
				vertices[8 * (i * numY + j) + 2] = (i + 1)
						* texture.getRegionWidth();
				vertices[8 * (i * numY + j) + 3] = j
						* texture.getRegionHeight();
				vertices[8 * (i * numY + j) + 4] = (i + 1)
						* texture.getRegionWidth();
				vertices[8 * (i * numY + j) + 5] = (j + 1)
						* texture.getRegionHeight();
				vertices[8 * (i * numY + j) + 6] = i * texture.getRegionWidth();
				vertices[8 * (i * numY + j) + 7] = (j + 1)
						* texture.getRegionHeight();
				tridx[6 * (i * numY + j)] = (short) (4 * (i * numY + j));
				tridx[6 * (i * numY + j)
						+ 1] = (short) (4 * (i * numY + j) + 3);
				tridx[6 * (i * numY + j)
						+ 2] = (short) (4 * (i * numY + j) + 2);
				tridx[6 * (i * numY + j)
						+ 3] = (short) (4 * (i * numY + j) + 2);
				tridx[6 * (i * numY + j)
						+ 4] = (short) (4 * (i * numY + j) + 1);
				tridx[6 * (i * numY + j) + 5] = (short) (4 * (i * numY + j));
			}
		}
		region = new PolygonRegion(texture, vertices, tridx);
	}

	@Override
	public void draw(GameCanvas canvas) {
		if (region != null) {
			if (((Flammable) material).isBurnt()) {
				canvas.draw(texture, Color.BLACK, origin.x, origin.y,
						(getX() - getWidth() / 2) * drawScale.x,
						(getY() - getHeight() / 2) * drawScale.y,
						getAngle(),
						ratio.x, ratio.y);
			} else if (((Flammable) material).isBurning()) {
				Color c = new Color();
				if (((Flammable) material).getBurnRatio() > 0.3) {
					c = new Color(1, ((Flammable) material).getBurnRatio(), 0,
							1);
				} else {
					c = new Color(((Flammable) material).getBurnRatio() / 0.3f,
							((Flammable) material).getBurnRatio(), ratio.x,
							ratio.y);
				}
				canvas.draw(texture, c, origin.x, origin.y,
						(getX() - getWidth() / 2) * drawScale.x,
						(getY() - getHeight() / 2) * drawScale.y,
						getAngle(), ratio.x, ratio.y);
			} else {
				canvas.draw(texture, Color.WHITE, origin.x, origin.y,
						(getX() - getWidth() / 2) * drawScale.x,
						(getY() - getHeight() / 2) * drawScale.y,
						getAngle(), ratio.x, ratio.y);
			}
		}
		if (((Flammable) material).isBurning()) {
			burningAnimate(canvas);
		}
	}

}
