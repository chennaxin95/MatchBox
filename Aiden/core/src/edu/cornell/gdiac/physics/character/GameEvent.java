package edu.cornell.gdiac.physics.character;

public class GameEvent {
	/**
	 * Event states
	 * 0: Not care
	 * 1: care to be true
	 * -1: care to be false
	 */
	private int seenAiden;
	private int seenFire;
	private int canFire;
	private int isSpawned;
	private int closeToFire;
	
	public GameEvent(){
		seenAiden=0;
		seenFire=0;
		canFire=0;
		isSpawned=0;
		closeToFire=0;
	}
	
	public int hasSeenAiden() {
		return seenAiden;
	}
	public void setSeenAiden(int seenAiden) {
		this.seenAiden = seenAiden;
	}
	public int hasSeenFire() {
		return seenFire;
	}
	public void setSeenFire(int seenFire) {
		this.seenFire = seenFire;
	}
	public int canFire() {
		return canFire;
	}
	public void setCanFire(int canFire) {
		this.canFire = canFire;
	}
	public int isSpawned() {
		return isSpawned;
	}
	public void setSpawned(int isSpawned) {
		this.isSpawned = isSpawned;
	}
	public int isCloseToFire() {
		return closeToFire;
	}
	public void setCloseToFire(int closeToFire) {
		this.closeToFire = closeToFire;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof GameEvent){
			if (((GameEvent)o).canFire()*canFire()>=0 && 
				((GameEvent)o).hasSeenAiden()*hasSeenAiden()>=0 &&
				((GameEvent)o).hasSeenFire()*hasSeenFire()>=0 &&
				((GameEvent)o).isSpawned()*isSpawned()>=0 &&
				((GameEvent)o).isCloseToFire()*isCloseToFire()>=0){
				return true;
			}
		}
		return false;
	}
	@Override
	public int hashCode(){
		int i=(int) (canFire()*Math.pow(2, 5));
		i+=(int) (hasSeenAiden()*Math.pow(2, 4));
		i+=(int) (hasSeenFire()*Math.pow(2, 3));
		i+=(int) (isSpawned()*Math.pow(2, 2));
		i+=(int) (isCloseToFire()*Math.pow(2, 1));
		return i;
	}
}
