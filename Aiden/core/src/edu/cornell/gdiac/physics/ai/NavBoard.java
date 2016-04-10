package edu.cornell.gdiac.physics.ai;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import edu.cornell.gdiac.physics.AidenController;
import edu.cornell.gdiac.physics.GameCanvas;
import edu.cornell.gdiac.physics.obstacle.BoxObstacle;
import edu.cornell.gdiac.physics.obstacle.Obstacle;
import edu.cornell.gdiac.physics.obstacle.PolygonObstacle;
import edu.cornell.gdiac.physics.obstacle.SimpleObstacle;

public class NavBoard {
	
	private float lx, ly;
	private float unitX, unitY;
	private int width=0, height=0;
	private NavTile[][] tiles;
	private Vector2 drawScale;
	
	public NavBoard(float lx, float ly, float ux, float uy, float unitX, float unitY){
		this.lx=lx;
		this.ly=ly;
		this.unitX=unitX;
		this.unitY=unitY;
		width=(int) Math.ceil((ux-lx)/unitX);
		height=(int) Math.ceil((uy-ly)/unitY);
		tiles=new NavTile[width][height];
		clear();
	}
	
	public Vector2 convertToBoardCoord(Vector2 in){
		Vector2 out=new Vector2();
		out.x=(int)((in.x-lx)/unitX);
		out.y=(int)((in.y-ly)/unitX);
		return out;		
	}
	
	public Vector2 converToWorldCoord(Vector2 in){
		Vector2 out=new Vector2();
		out.x=(in.x+0.5f)*(unitX) + lx;
		out.y=(in.y+0.5f)*(unitY) + ly;
		return out;
	}
	public void clear(){
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				tiles[i][j]=new NavTile(i, j);
			}
		}
	}
	public NavTile getTile(Vector2 indices){
		if (isValidBoardCoord(indices)){
			return tiles[(int) indices.x][(int) indices.y];
		}
		else 
			return null;
	}
	/*
	 *  Iterate over all objects, mark on board as occupied/platform 
	 *  if any object overlaps the center point of a tile
	 */
	public void setupBoard(ArrayList<Obstacle> objs){
		clear();
		for (Obstacle obj: objs){
			Vector2 pos=obj.getPosition();
			if (obj instanceof BoxObstacle){
				float w=((BoxObstacle)obj).getWidth();
				float h=((BoxObstacle)obj).getHeight();
				int lIndX=Math.round((pos.x-w/2f-lx)/unitX);
				int lIndY=Math.round((pos.y-h/2f-ly)/unitY);	
				int uIndX=Math.round((pos.x+w/2f-lx)/unitX)-1;
				int uIndY=Math.round((pos.y+h/2f-ly)/unitY)-1;	
				// Need to add "Danger" cases
				for (int i=Math.max(lIndX, 0); i<=Math.min(uIndX, width-1); i++){
					for (int j=Math.max(lIndY,0); j<=Math.min(uIndY, height-1); j++){
						// obj shouldn't be characters or flamming
						markTile(i, j, TileType.SUPPORT);
					}
				}
			}
			else if (obj instanceof PolygonObstacle){
				ArrayList<Vector2> frontier=new ArrayList<Vector2>();
				Vector2 cen=convertToBoardCoord(((PolygonObstacle)obj).getPointMustInside());
				tiles[(int) (cen.x)][(int) cen.y].type=TileType.SUPPORT;
				frontier.add(cen);
				while (frontier.size()>0){
					Vector2 head=frontier.remove(0);
					Vector2 p1=new Vector2(head.x-1, head.y);
					if (head.x>=1 && tiles[(int) p1.x][(int) p1.y].type==TileType.NONE
							&& ((PolygonObstacle)obj).contains(converToWorldCoord(p1))){
						tiles[(int) p1.x][(int) p1.y].type=TileType.SUPPORT;
						frontier.add(p1);
					}
					Vector2 p2=new Vector2(head.x+1, head.y);
					if (head.x<=width-2 && tiles[(int) p2.x][(int) p2.y].type==TileType.NONE
							&& ((PolygonObstacle)obj).contains(converToWorldCoord(p2))){
						tiles[(int) p2.x][(int) p2.y].type=TileType.SUPPORT;
						frontier.add(p2);
					}
					Vector2 p3=new Vector2(head.x, head.y-1);
					if (head.y>=1 && tiles[(int) p3.x][(int) p3.y].type==TileType.NONE
							&& ((PolygonObstacle)obj).contains(converToWorldCoord(p3))){
						tiles[(int)p3.x][(int)p3.y].type=TileType.SUPPORT;
						frontier.add(p3);
					}
					Vector2 p4=new Vector2(head.x, head.y+1);
					if (head.y<=height-2 && tiles[(int) (p4.x)][(int) p4.y].type==TileType.NONE
							&& ((PolygonObstacle)obj).contains(converToWorldCoord(p4))){
						tiles[(int) (p4.x)][(int)p4.y].type=TileType.SUPPORT;
						frontier.add(p4);
					}
				}
			}
		}
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				if (j>=1 && tiles[i][j].type==TileType.NONE && tiles[i][j-1].type==TileType.SUPPORT){
					tiles[i][j].type=TileType.NONEDGE;
					if (i>=1 && tiles[i-1][j-1].type==TileType.NONE)					
						tiles[i][j].type=TileType.LEFTEDGE;
					if (i<=width-2 && tiles[i+1][j-1].type==TileType.NONE)
						if (tiles[i][j].type==TileType.LEFTEDGE){
							tiles[i][j].type=TileType.SOLO;	
						}
						else{
							tiles[i][j].type=TileType.RIGHTEDGE;	
						}
				}
			}
		}
		// Create links
		for (int i=0; i<width; i++){ 
			for (int j=0; j<height; j++){
				switch (tiles[i][j].type){
					case NONEDGE:
						if (i>=1)
							tiles[i][j].links.add(new Vector2(i-1, j));
						if (i<=width-2)
							tiles[i][j].links.add(new Vector2(i+1, j));
						break;
					case LEFTEDGE:
						for (int sj=j-1; sj>=0; sj--){
							if (tiles[i-1][sj].type!=TileType.NONE &&
								tiles[i-1][sj].type!=TileType.DANGER &&
								tiles[i-1][sj].type!=TileType.SUPPORT){
								tiles[i][j].links.add(new Vector2(i-1, sj));
								break;
							}
						}
						break;
					case RIGHTEDGE:
						for (int sj=j-1; sj>=0; sj--){
							if (tiles[i+1][sj].type!=TileType.NONE &&
								tiles[i+1][sj].type!=TileType.DANGER &&
								tiles[i+1][sj].type!=TileType.SUPPORT){
								tiles[i][j].links.add(new Vector2(i+1, sj));
								break;
							}
						}
						break;
					case SOLO:
						for (int sj=j-1; sj>=0; sj--){
							if (tiles[i-1][sj].type!=TileType.NONE &&
								tiles[i-1][sj].type!=TileType.DANGER &&
								tiles[i-1][sj].type!=TileType.SUPPORT){
								tiles[i][j].links.add(new Vector2(i-1, sj));
								break;
							}
						}
						for (int sj=j-1; sj>=0; sj--){
							if (tiles[i+1][sj].type!=TileType.NONE &&
								tiles[i+1][sj].type!=TileType.DANGER &&
								tiles[i+1][sj].type!=TileType.SUPPORT){
								tiles[i][j].links.add(new Vector2(i+1, sj));
								break;
							}
						}
						break;	
					default: break;
				}
			}
		}
	}
	
	private void markTile(int indx, int indy, TileType t){
		this.tiles[indx][indy].type=t;
	}
	
	public void setDrawScale(Vector2 d){
		drawScale=d;
	}

	public class NavTile{
		public TileType type;
		public int indX, indY;
		public ArrayList<Vector2> links;
		public Vector2 reachedBy;
		public boolean isTarget;
		
		public NavTile(int indX, int indY){
			type=TileType.NONE;
			links=new ArrayList<Vector2>();
			this.indX=indX;
			this.indY=indY;
			reachedBy=new Vector2(-1, -1);
			isTarget=false;
		}
		
		public void draw(GameCanvas canvas){
			Color c=Color.WHITE;
			switch (type){
			case SUPPORT: c=Color.BLUE; break;
			case DANGER: c=Color.RED; break;
			case LEFTEDGE: c=Color.GREEN; break;
			case NONEDGE: c=Color.GREEN; break;
			case RIGHTEDGE: c=Color.GREEN; break;
			case SOLO: c=Color.GREEN; break;
			default:break;
			}
			Vector2 pos=converToWorldCoord(new Vector2(indX, indY));
			CircleShape circle=new CircleShape();
			circle.setRadius(Math.min(unitX,unitY)/2);
			canvas.drawPhysics(circle, c, pos.x, pos.y, drawScale.x,drawScale.y);
			if (this.isTarget) {
				CircleShape circle2=new CircleShape();
				circle2.setRadius(Math.min(unitX,unitY)/4);
				canvas.drawPhysics(circle2, Color.RED, pos.x, pos.y, drawScale.x,drawScale.y);
			}
		}
		
		public boolean hasReached(){
			return isValidBoardCoord(reachedBy);
		}
		
		public void markAsTarget(){
			this.isTarget=true;
		}
		
		public boolean isSafeToWalkOn(){
			return type!=TileType.NONE && type!=TileType.DANGER && type!=TileType.SUPPORT;
		}
		
	}
	
	public enum TileType{
		NONE, SUPPORT, DANGER, LEFTEDGE, NONEDGE, RIGHTEDGE, SOLO
	}	
	public void drawDebug(GameCanvas canvas){
		for (int i=0; i<width; i++){
			for (int j=0; j<height; j++){
				if (tiles[i][j].type!=TileType.NONE)
					tiles[i][j].draw(canvas);
			}
		}
	}
	
	public boolean isValidBoardCoord(Vector2 pos){
		if (pos.x>=0 && pos.x<width && pos.y>=0 && pos.y<height) 
			return true;
		else return false;
	}
	
	public Vector2 convertToWorldUnit(Vector2 vec){
		return new Vector2(vec.x*unitX, vec.y*unitY);
	}
	
	public Vector2 castAround(Vector2 v){
		Vector2 in=v.cpy();
		in.x=Math.min(width, in.x);
		in.x=Math.max(0, in.x);
		in.y=Math.min(height, in.y);
		in.y=Math.max(0, in.y);
		Vector2 out1=new Vector2(in);
		boolean found1=false;
		for (int j=(int) in.y; j>=0; j--){
			out1.y=j;
			NavTile tile=this.getTile(out1);
			if (tile.type!=TileType.NONE
					&& tile.type!=TileType.SUPPORT 
					&& tile.type!=TileType.DANGER){
				found1=true;
				break;
			}
		}
		boolean found2=false;
		Vector2 out2=new Vector2(in);
		for (int j=(int) in.y+1; j<height; j++){
			out2.y=j;
			NavTile tile=this.getTile(out2);
			if (tile.type!=TileType.NONE
					&& tile.type!=TileType.SUPPORT 
					&& tile.type!=TileType.DANGER){
				found2=true;
				break;
			}
		}
		if (found1 && found2){
			Vector2 out=new Vector2(in);
			out.y=Math.abs(out1.y-in.y)<=Math.abs(out2.y-in.y)? out1.y: out2.y;
			return out;
		}
		else if (found1){
			return out1;
		}
		else if (found2){
			return out2;
		}
		return new Vector2(in);
	}
}