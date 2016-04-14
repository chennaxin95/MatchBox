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
import edu.cornell.gdiac.physics.character.WaterGuard;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class CollisionController {
	public boolean getCollisions(Array<Contact> cList, AidenModel avatar) {
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
//						if (bd1.getPosition().y - bd2.getPosition().y < fb.getHeight()/2){
						avatar.setGravityScale(0);
						avatar.setSpiriting(true);
//						}
						avatar.setClimbing(true);
						
						if (!fb.isBurnt()) {
							if (!fb.isBurning()) {
								fb.activateBurnTimer();
								// if it's a fuel box
								if (fb instanceof FuelBlock) {
									avatar.addFuel(
											((FuelBlock) fb).getFuelBonus());
								}
							}
						}
					}
				}
				if (bd2 == avatar) {
					if (bd1 instanceof FlammableBlock) {
						FlammableBlock fb = (FlammableBlock) bd1;
//						if (bd2.getPosition().y - bd1.getPosition().y < fb.getHeight()/1.8f){
						avatar.setGravityScale(0);
						avatar.setSpiriting(true);
						avatar.setClimbing(true);

						if (!fb.isBurning() && !fb.isBurnt()) {
							fb.activateBurnTimer();
							// if it's a fuel box
							if (fb instanceof FuelBlock) {
								avatar.addFuel(((FuelBlock) fb).getFuelBonus());
							}
						}
					}
				}

				if (bd1 == avatar && bd2 instanceof WaterGuard) {
					return false;
				}
				if (bd2 == avatar && bd1 instanceof WaterGuard){
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return true;
	}
}
