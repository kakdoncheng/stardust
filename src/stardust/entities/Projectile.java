package stardust.entities;

import stardust.StardustGame;

public abstract class Projectile extends StardustEntity{
	
	protected double range;
	protected double fade;
	protected double speed;
	protected StardustEntity owner;
	
	public StardustEntity $owner(){
		return owner;
	}

	public Projectile(StardustGame game, double x, double y, double t, double speed, double range, StardustEntity owner) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(1);
		this.setDirection(t);
		this.setSpeedVector(t, speed);
		this.speed=speed;
		this.owner=owner;
		this.range=range;
		fade=1;
		blip();
	}
	
	public void update(double dt) {
		updateBlip(dt);
		range-=speed*dt;
		
		if(range<=0){
			fade-=dt*8;
			if(fade<0){
				active=false;
			}
		}else{
			for(StardustEntity e:game.$currentState().$targetableEntities()){
				if(e instanceof Projectile){
					if(((Projectile) e).owner==owner){
						continue;
					}
				}
				if(e==this||e==owner||!e.isCollidable()){
					continue;
				}
				if(distanceTo(e)<this.r+e.$r()){
					active=false;
					onImpactWith(e);
					break;
				}
			}
		}
		this.updatePosition(dt);
	}
	
	protected abstract void onImpactWith(StardustEntity e);
	
}
