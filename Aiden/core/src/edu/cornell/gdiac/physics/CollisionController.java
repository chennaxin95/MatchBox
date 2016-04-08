package edu.cornell.gdiac.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;

import edu.cornell.gdiac.physics.blocks.BlockAbstract;
import edu.cornell.gdiac.physics.blocks.FlammableBlock;
import edu.cornell.gdiac.physics.blocks.FuelBlock;
import edu.cornell.gdiac.physics.character.AidenModel;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.character.CharacterModel.CharacterType;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
public class CollisionController{
	public boolean getCollisions(Array<Contact> cList, AidenModel avatar){
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
					} else if (fb2.canSpreadFire()
							&& (!fb1.isBurning() && !fb1.isBurnt())) {
						System.out.println(fb2.getName() + "" + fb2.isBurning()
								+ " " + fb1.getName());
						fb1.activateBurnTimer();
					}
				}

				// check for aiden and flammable
				if (bd1 == avatar) {

					if (bd2 instanceof FlammableBlock) {

						FlammableBlock fb = (FlammableBlock) bd2;
						if (avatar.getPosition().dst(
								fb.getPosition()) <= fb.getWidth()
										* Math.sqrt(2) / 2) {
							avatar.setClimbing(true);
							avatar.setGravityScale(0);
							avatar.setSpiriting(true);
						}
						if (!fb.isBurning() && !fb.isBurnt()) {
							System.out.println(fb.getName());
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								avatar.addFuel(((FuelBlock) fb).getFuelBonus());
							}
						}
					}
				}
				if (bd2 == avatar) {

					if (bd1 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd1;
						if (avatar.getPosition().dst(
								fb.getPosition()) <= fb.getWidth()
										* Math.sqrt(2) / 2) {
							avatar.setClimbing(true);
							avatar.setGravityScale(0);
							avatar.setSpiriting(true);
						}

						if (!fb.isBurning() && !fb.isBurnt()) {
							System.out.println(fb.getName());
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								avatar.addFuel(((FuelBlock) fb).getFuelBonus());
							}
						}
					}
				}

				// Set climbing state for climbable blocks
				if (bd1 == avatar && bd2 instanceof BlockAbstract) {
					BlockAbstract b = (BlockAbstract) bd2;
					if (b.getMaterial().isClimbable()) {
						float x = Math.abs(bd1.getX() - bd2.getX());
						float y = Math.abs(bd1.getY() - bd2.getY());
						if (x <= b.getWidth() / 2 && y <= b.getHeight() / 2) {

							avatar.setClimbing(true);
							avatar.setGravityScale(0);
						}

					}
				}
				if (bd2 == avatar && bd1 instanceof BlockAbstract) {
					BlockAbstract b = (BlockAbstract) bd1;
					if (b.getMaterial().isClimbable()) {
						float x = Math.abs(bd1.getX() - bd2.getX());
						float y = Math.abs(bd1.getY() - bd2.getY());
						if (x <= b.getWidth() / 2 && y <= b.getHeight() / 2) {

							avatar.setClimbing(true);
							avatar.setGravityScale(0);
						}

					}
				}

				if (bd1 == avatar && bd2 instanceof CharacterModel
						&& ((CharacterModel) bd2)
								.getType() == CharacterType.WATER_GUARD) {
					return false;
				}
				if (bd2 == avatar && bd1 instanceof CharacterModel
						&& ((CharacterModel) bd1)
								.getType() == CharacterType.WATER_GUARD) {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return true;
	}
}