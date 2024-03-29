package edu.cornell.gdiac.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import edu.cornell.gdiac.physics.blocks.BurnablePlatform;
import edu.cornell.gdiac.physics.blocks.BurnablePlatform.FlamePlatform;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.blocks.Rope;
import edu.cornell.gdiac.physics.blocks.RopePart;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.WaterGuard;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.scene.AssetFile;
import edu.cornell.gdiac.physics.scene.GameSave;
import edu.cornell.gdiac.util.PooledList;

public class CollisionController {

	/**
	 * If checkpoint has been reached, chkptReached = checkpoint index. Else is
	 * -1.
	 */
	private int chkptReached = -1;

	public int getCheckpoint() {
		return chkptReached;
	}

	public boolean getCollisions(Array<Contact> cList, AidenModel avatar,
			GameSave gs, Array<FuelBlock> checkpoints, AssetFile af) {
		for (Contact c : cList) {
			Fixture fix1 = c.getFixtureA();
			Fixture fix2 = c.getFixtureB();
			Body body1 = fix1.getBody();
			Body body2 = fix2.getBody();
			try {
				Obstacle bd1 = (Obstacle) body1.getUserData();
				Obstacle bd2 = (Obstacle) body2.getUserData();

				// checking for two flammable block's chain reaction
				if (bd1 instanceof FlammableBlock
						&& bd2 instanceof FlammableBlock) {
					FlammableBlock fb1 = (FlammableBlock) bd1;
					FlammableBlock fb2 = (FlammableBlock) bd2;
					
					

					if (fb1.canSpreadFire()
							&& (!fb2.isBurning() && !fb2.isBurnt())) {
						System.out.println(fb1.getName() + "" + fb1.isBurning()
								+ " " + fb2.getName());
						fb2.activateBurnTimer();
						af.match.play();
					} else if (fb2.canSpreadFire()
							&& (!fb1.isBurning() && !fb1.isBurnt())) {
						System.out.println(fb2.getName() + "" + fb2.isBurning()
								+ " " + fb1.getName());
						fb1.activateBurnTimer();
						af.match.play();
					}
				}

				// check for aiden and flammable
				if (bd1 == avatar) {

					if (bd2 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd2;
						if (!(bd2 instanceof FlamePlatform)
								&& !(bd2 instanceof RopePart)) {
							avatar.setGravityScale(0);
							avatar.setSpiriting(true);
//							af.spiriting.play();
						}
						if (bd2 instanceof RopePart) {
							avatar.setClimbing(true);
						}
						if (!fb.isBurnt()) {
							if (!fb.isBurning()) {
								fb.activateBurnTimer();
								// if it's a fuel box
								if (fb instanceof FuelBlock) {
									FuelBlock fbb = (FuelBlock) fb;
									avatar.addFuel(
											fbb.getFuelBonus());
									avatar.gotFuel = true;
									af.bubble.play();
									avatar.setFuelPPos();
									avatar.setSpiriting(false);
									if (fbb.isCheckpoint()) {
										af.bubble.stop();
										af.yay.play();
										int dex = checkpoints.indexOf(fbb,
												true);
										if (gs.getCheckpoint() != dex) {
											chkptReached = dex;
										}
									}

								}
							}
						}
					}
				}
				if (bd2 == avatar) {
					if (bd1 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd1;
						if (!(bd1 instanceof FlamePlatform)
								&& !(bd1 instanceof RopePart)) {
							avatar.setGravityScale(0);
							avatar.setSpiriting(true);
//							af.spiriting.play();
						}
						if ((bd1 instanceof RopePart)) {
							avatar.setClimbing(true);
						}
						if (!fb.isBurning() && !fb.isBurnt()) {
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								FuelBlock fbb = (FuelBlock) fb;
								avatar.addFuel(
										fbb.getFuelBonus());
								af.bubble.play();
								avatar.gotFuel = true;
								avatar.setFuelPPos();
								avatar.setSpiriting(false);
								if (fbb.isCheckpoint()) {
									af.bubble.stop();
									af.yay.play();
									int dex = checkpoints.indexOf(fbb, true);
									if (gs.getCheckpoint() != dex) {
										chkptReached = dex;
									}
								}

							}
						}

					}
				}

				if (bd1 == avatar && bd2 instanceof WaterGuard) {
					if (!((WaterGuard) bd2).isDead()) {
						return false;
					}
				}
				if (bd2 == avatar && bd1 instanceof WaterGuard) {
					if (!((WaterGuard) bd1).isDead()) {
						return false;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return true;
	}
}
