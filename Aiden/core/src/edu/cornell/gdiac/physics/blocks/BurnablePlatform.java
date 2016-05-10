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
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.blocks.BlockAbstract.BlockType;
import edu.cornell.gdiac.physics.material.Flammable;
import edu.cornell.gdiac.physics.obstacle.ComplexObstacle;
import edu.cornell.gdiac.physics.obstacle.SimpleObstacle;
import edu.cornell.gdiac.physics.obstacle.WheelObstacle;

public class BurnablePlatform extends ComplexObstacle {

	private PolygonRegion region;
	protected Rectangle r;
	private float unit;

	private FlamePlatform platform;
	private WheelObstacle pin;

	float width;
	float height;

	public BurnablePlatform(Rectangle r, float unit, World world) {
		super(r.x + 0.5f * r.getWidth(), r.y + 0.5f * r.getHeight());
		platform = new FlamePlatform(this, r, unit);
		body = platform.getBody();

		this.r = r;
		this.unit = unit;

		bodies.add(platform);

		this.pin = new WheelObstacle(this.getX(), this.getY(), 0.1f);
		pin.setBodyType(BodyDef.BodyType.StaticBody);
		bodies.add(pin);

		width = r.width;
		height = r.height;
		// TODO Auto-generated constructor stub
	}

	public FlammableBlock getPlatform() {
		return platform;
	}

	protected boolean createJoints(World world) {
		System.out.println("joint created");
		WeldJointDef def = new WeldJointDef();

		def.bodyA = platform.getBody();
		def.bodyB = pin.getBody();
		def.localAnchorA.set(new Vector2());
		def.localAnchorB.set(new Vector2());
		def.collideConnected = false;
		Joint j = world.createJoint(def);

		joints.add(j);

		return true;
	}

	public void setTexture(TextureRegion texture) {
		platform.setTexture(texture);
		this.region = platform.region;
	}

	@Override
	public void draw(GameCanvas canvas) {
		if (region != null) {
			if (((Flammable) platform.material).isBurnt()) {
				canvas.draw(platform.getTexture(), Color.BLACK, 0, 0,
						(getX() - platform.getWidth() / 2) * drawScale.x,
						(getY() - platform.getHeight() / 2) * drawScale.y,
						getAngle(),
						platform.ratio.x, platform.ratio.y);
			} else if (((Flammable) platform.material).isBurning()) {
				Color c = new Color();
				if (((Flammable) platform.material).getBurnRatio() > 0.3) {
					c = new Color(1,
							((Flammable) platform.material).getBurnRatio(), 0,
							1);
				} else {
					c = new Color(((Flammable) platform.material).getBurnRatio()
							/ 0.3f,
							((Flammable) platform.material).getBurnRatio(),
							platform.ratio.x,
							platform.ratio.y);
				}
				canvas.draw(platform.getTexture(), c, 0, 0,
						(getX() - platform.getWidth() / 2) * drawScale.x,
						(getY() - platform.getHeight() / 2) * drawScale.y,
						getAngle(), platform.ratio.x, platform.ratio.y);
			} else {
				canvas.draw(platform.getTexture(), Color.WHITE, 0, 0,
						(getX() - platform.getWidth() / 2) * drawScale.x,
						(getY() - platform.getHeight() / 2) * drawScale.y,
						getAngle(), platform.ratio.x, platform.ratio.y);
			}
		}
		if (((Flammable) platform.material).isBurning()) {
			platform.burningAnimate(canvas);
		}
	}

	public void update(float dt) {
		platform.update(dt);
		pin.update(dt);
	}

	@Override
	public boolean updateParts(World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawDebug(GameCanvas canvas, Color c) {
		// TODO Auto-generated method stub
		platform.drawDebug(canvas, c);

	}

	public static class FlamePlatform extends FlammableBlock {
		/**
		 * Made so that the platform itself can still be a flammable block and
		 * can reference the wrapping objects for lists etc.
		 */
		private BurnablePlatform bp;
		private PolygonRegion region;
		private float unit;

		public FlamePlatform(BurnablePlatform bp, Rectangle r, float unit) {
			super(r.x + 0.5f * r.getWidth(), r.y + 0.5f * r.getHeight(),
					r.getWidth(), r.getHeight(), 1, 3);
			this.bp = bp;
			this.unit = unit;
			setBodyType(BodyDef.BodyType.DynamicBody);
			setFixedRotation(true);
			setBlockType(BlockType.BURNABLE_PLATFORM);
		}

		public BurnablePlatform getBP() {
			return bp;
		}

		@Override
		public void setTexture(TextureRegion texture) {
			super.setTexture(texture);
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
					vertices[8 * (i * numY + j) + 6] = i
							* texture.getRegionWidth();
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
					tridx[6 * (i * numY + j)
							+ 5] = (short) (4 * (i * numY + j));
				}

			}
			region = new PolygonRegion(texture, vertices, tridx);
		}
	}

	public float getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	public float getHeight() {
		// TODO Auto-generated method stub
		return height;
	}

	@Override
	public Rectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return new Rectangle(this.getX() - getWidth() / 2f,
				this.getY() - getHeight() / 2f,
				getWidth(), getHeight());
	}

}
