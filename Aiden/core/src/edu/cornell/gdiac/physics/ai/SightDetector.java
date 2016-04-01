package edu.cornell.gdiac.physics.ai;

import java.util.ArrayList;

import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class SightDetector {
// Assume horizontal sight; to simplify, assume a single viewing ray	
	public static Obstacle detectObjectInSight(CharacterModel npc){
		return detectObjectInSight(npc, 0/**/);
	}
	
	public static Obstacle detectObjectInSight(CharacterModel npc, int facingDir){
		// Loop over all objects in scene, including characters
			// Skip if being itself
		
			// Skip if not in the facing direction
		
			// Skip if no part of the object is within the vertical range of sight
		
			// Keep track of closest intersected object so far.
		
		return null;
	}
	
	public static ArrayList<Obstacle> detectObjectsInDistance(CharacterModel npc, float threshold){
		// Loop over all objects in scene, including characters
			// Skip if being itself
	
			// Add to return if close enough and within vertical range?? Shoot out several rays
		
		return null;
	}
}
