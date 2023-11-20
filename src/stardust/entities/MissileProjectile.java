package stardust.entities;

import stardust.StardustGame;
import engine.Vector;

public abstract class MissileProjectile extends StardustEntity{
	
	protected double range;
	protected double aF;
	protected double tx, ty;
	protected StardustEntity owner;

	public MissileProjectile(StardustGame game, double x, double y, double tx, double ty, 
			double t, double aF, double range, StardustEntity owner) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(2);
		this.setSpeedVector(t, 64);
		this.aF=aF;
		this.tx=tx;
		this.ty=ty;
		this.owner=owner;
		this.range=range;
		blip();
	}
	
	public void update(double dt) {
		updateBlip(dt);
		range-=$speed()*dt;
		
		if(range<=0 || Vector.distanceFromTo(x, y, tx, ty)<8){
			deactivate();
		}else{
			for(StardustEntity e:game.$currentState().$targetableEntities()){
				if(e instanceof MissileProjectile){
					if(((MissileProjectile) e).owner==owner){
						continue;
					}
				}
				if(e==this||e==owner||!e.isCollidable()){
					continue;
				}
				if(distanceTo(e)<this.r+e.$r()){
					onImpactWith(e);
					active=false;
					break;
				}
			}
		}
		this.applyAccelerationVector(Vector.directionFromTo(x, y, tx, ty), aF, dt);
		this.setDirection(Vector.directionFromTo(0, 0, dx, dy));
		this.updatePosition(dt);
	}
	
	protected void lockOnTarget(StardustEntity e){
		tx=e.$x();
		ty=e.$y();
	}
	
	protected abstract void onImpactWith(StardustEntity e);
	
}
