package edu.cornell.gdiac.physics.ai;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.ai.NavBoard.NavTile;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class PathFinder {
	
	public static Vector2 findPath(NavBoard board, Vector2 srcPos, Vector2 targetPos){
		Vector2 start=board.convertToBoardCoord(srcPos);
		Vector2 target=board.convertToBoardCoord(targetPos);
		System.out.println("Start path finding "+start+" "+target);
		if (!board.isValidBoardCoord(start) || !board.isValidBoardCoord(target)) {
			System.out.println("Error 1");
			return new Vector2();
		}
		ArrayList<Vector2> frontier=new ArrayList<Vector2>();
		frontier.add(start);
		while (frontier.size()>0){
			Vector2 head=frontier.remove(0);
			if (head.equals(target)) break;
			for (Vector2 link:board.getTile(head).links){
				NavTile tile=board.getTile(link);
				if (!tile.hasReached()){
					tile.reachedBy=head;
					frontier.add(link);
				}
			}
		}
		if (board.getTile(target).hasReached()){
			Vector2 current=target;
			Vector2 parent=board.getTile(current).reachedBy;
			while (!parent.equals(start)){
				current=parent;
				parent=board.getTile(current).reachedBy;
			}
			return board.convertToWorldUnit(current.sub(start));
		}
		else{
			System.out.println("Error 2");
			return new Vector2();
		}
	}
}
