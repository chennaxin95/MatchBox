package edu.cornell.gdiac.physics.ai;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import edu.cornell.gdiac.physics.ai.NavBoard.NavTile;
import edu.cornell.gdiac.physics.character.CharacterModel;
import edu.cornell.gdiac.physics.obstacle.Obstacle;

public class PathFinder {
	
	public Vector2 findPath(NavBoard board, Vector2 srcPos){
		Vector2 start=board.convertToBoardCoord(srcPos);
		if (!board.isValidBoardCoord(start)) {
			return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		}
		if (board.getTile(start).isTarget) return new Vector2();
		
		ArrayList<Vector2> frontier=new ArrayList<Vector2>();
		frontier.add(start);
		Vector2 head=start.cpy();
		while (frontier.size()>0){
			head=frontier.remove(0);
			if (board.getTile(head).isTarget) break;
			for (Vector2 link:board.getTile(head).links){
				NavTile tile=board.getTile(link);
				if (!tile.hasReached()){
					tile.reachedBy=head;
					frontier.add(link);
				}
			}
		}
		if (board.getTile(head).isTarget){
			Vector2 current=head;
			Vector2 parent=board.getTile(current).reachedBy;
			while (!parent.equals(start)){
			    current=parent;
				parent=board.getTile(current).reachedBy;
			}
			return board.convertToWorldUnit(current.sub(start));
		}
		else{
			return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
		}
	}
}
