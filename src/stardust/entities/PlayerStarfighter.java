package stardust.entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;
import stardust.StardustGame;
import stardust.entities.invaders.PowerAlienProjectile;
import stardust.gfx.CharGraphics;
import stardust.gfx.VectorGraphics;

public class PlayerStarfighter extends StardustEntity{

	public PlayerStarfighter(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(4);
		this.setXY(x, y);
		rc=new RadarScan(game, 0, 0);
		rc.lockOnEntity(this);
	}

	private double aF=500;
	private double cooldown=0;
	private double invt=3;
	//private double debugpowi=0;
	//private double debugpowcd=0;
	
	public void resetInvTimer() {
		invt=3;
	}
	
	private boolean isShielded=false;
	private int shield=0;
	private int shieldl=1; // shield max
	private double shielda=0;
	private double shieldr=11;
	private double shieldt=0;
	
	private RadarScan rc;
	private Power power;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// power & invulnurable toggle
		if(GameFlags.is("debug")){
			if(Keyboard.isKeyDown(Keyboard.KEY_F)){
				power=new PowerAlienProjectile(game,0,0,this);
				//power=new PowerBulletHail(game,0,0,this);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_G)){
				GameFlags.setFlag("debuginv", 0);
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_H)){
				GameFlags.setFlag("debuginv", 1);
			}
		}
		
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// recharge shield
		if(isShielded){
			if(shielda>0){
				shielda-=3*dt;
			}else{
				shielda=0;
			}
			if(shield<shieldl){
				shieldt-=dt;
				if(shieldt<=0){
					shield++;
					shielda=1.2;
					shieldt=5;
				}
			}
		}
		
		
		// movement input
		boolean hasInput=false;
		double dxy=aF*0.15*(dt/game.$runSpeed())*(1-game.$runSpeed());
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			y-=dxy;
			applyAccelerationVector(Math.PI, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			x-=dxy;
			applyAccelerationVector(Math.PI*0.5, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			y+=dxy;
			applyAccelerationVector(0, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			x+=dxy;
			applyAccelerationVector(Math.PI*1.5, aF, dt);
			hasInput=true;
		}
		
		// friction brakes
		// F = v^2 * 0.5pDA
		double cs=$speed();
		if(cs>(hasInput?0:8)){
			double ft=Vector.constrainTheta(Vector.dxyToDirection(dx, dy)+Math.PI);
			double fF=hasInput?cs*cs*0.005:aF;
			applyAccelerationVector(ft, fF, dt);
		}else{
			dx=0;
			dy=0;
		}
		
		updatePosition(dt);
		
		// dir & weapons
		t=Vector.directionFromTo(x, y, MouseHandler.$mx(), MouseHandler.$my());
		cooldown+=dt;
    	if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		if(power!=null&&!power.isEmpty()){
    			power.usePrimary(this, dt);
    		}else{
    			if(cooldown>0.125){
        			fireProjectile();
        			cooldown=0;
        		}
    		}
    		
    	}else if(Mouse.isButtonDown(1)){
    		if(power!=null&&!power.isEmpty()){
    			power.useSecondary(this);
    		}
    	}
    	
    	// check for collisions
    	if(isCollidable()){
    		if(isShielded && shield>0){
        		for(StardustEntity e:game.$currentState().$entities()){
        			if(e instanceof Projectile && ((Projectile)e).$owner()==this){
        				continue;
        			}
        			if(e==this || !e.isCollidable()){
        				continue;
        			}
        			if(distanceTo(e)<this.shieldr+e.$r()/2){
        				deactivate();
        				e.setKiller(this);
        				e.deactivate();
        				if(e instanceof Projectile){
        					game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), 4));
        				}else{
        					game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()));
        				}
        			}
        		}
        	}else{
        		// normal fatal collision
        		for(StardustEntity e:game.$currentState().$entities()){
        			if(e instanceof Projectile){
        				continue;
        			}
        			if(e!=this && e.isCollidable() && e.isActive() && distanceTo(e)<=r+e.$r()){
        				deactivate();
        			}
        		}
        	}
    	}

    	// check for powers
    	for(StardustEntity e:game.$currentState().$entities()){
			if(e instanceof Power){
				if(distanceTo(e)<=r+e.$r()){
					power=(Power)e;
					e.deactivate();
				}
				continue;
			}
		}
    	
    	rc.update(dt);
	}

	// 4xy render code
	private double scale=1.75;
	private double l[]={
		-1,0,0,2,0,2,1,0,1,0,1,4,1,4,2,1,2,1,5,-1,5,-1,5,-2,5,-2,2,-3,2,-3,1,-2,1,-2,0,-3,0,-3,-1,-2,-1,-2,-2,-3,-2,-3,-5,-2,-5,-2,-5,-1,-5,-1,-2,1,-2,1,-1,4,-1,4,-1,0,
	};
	
	/*
	private double wscale=2.5;
	private double wl[]={
		0,-9,9,0,9,0,0,9,0,9,-9,0,-9,0,0,-9,-1,-5,1,-5,1,-5,1,1,1,1,-1,1,-1,1,-1,-5,-1,3,1,3,1,3,1,5,1,5,-1,5,-1,5,-1,3,0,-10,10,0,10,0,0,10,0,10,-10,0,-10,0,0,-10,
	};
	//*/

	public void render(Camera c) {
		// render ship
		if(invt<=0 || invt%0.5>0.25){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslated(c.$cx(x), c.$cy(y), 0);	
			GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,1,1,1);
			for(int i=0; i<l.length; i+=4){
				GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
				GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		// render shield hud
		if(isShielded){
			if(shield<1 && shieldt%0.5>0.25){
				game.flashRedWarning();
				// exclamation point
				/*
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glPushMatrix();
				GL11.glTranslated(game.$displayWidth()/2-40, game.$displayHeight()/2-40, 0);
				GL11.glBegin(GL11.GL_LINES);
				GL11.glColor4d(1,0,0,1);
				for(int i=0; i<wl.length; i+=4){
					GL11.glVertex2d(wl[i]*wscale, wl[i+1]*wscale);
					GL11.glVertex2d(wl[i+2]*wscale, wl[i+3]*wscale);
				}
				GL11.glEnd();
				GL11.glPopMatrix();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				//*/
			}
			
			// render shield
			if(shielda>0){
				if(shield>0){
					//GL11.glColor4d(0,1,0,shielda);
					setActualRadarColor(shielda);
				}else{
					GL11.glColor4d(1,0,0,shielda);
				}
				VectorGraphics.beginVectorRender();
				VectorGraphics.renderVectorCircle(x, y, shieldr, 16, c);
				VectorGraphics.endVectorRender();
			}
		}
		
		
		// render cursor
		VectorGraphics.renderCrosshairCursor();
		
		rc.render(c);
		
		//this.renderCollisionBounds(c, 8);
		if(GameFlags.is("debugfps")){
			CharGraphics.drawString(String.format("%.1f m/s",$speed()), 
	    			-game.$displayWidth()/2, +game.$displayHeight()/2-32, 1f);
			CharGraphics.drawString(String.format("x: %.1f y: %.1f",x,y), 
	    			-game.$displayWidth()/2, +game.$displayHeight()/2-14, 1f);
		}
		
	}

	public boolean isCollidable(){
		if(GameFlags.is("debuginv")){
			//if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			//	return true;
			//}
			return false;
		}
		return !(invt>0);
	}
	
	public void deactivate(){
		active=false;
	}
	
	public void onDeath() {
		if(isShielded && shield>0){
			active=true;
			shielda=1.2;
			shield--;
			// replace with shield pulse
			game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
		}else{
			game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
		}
	}
	
	protected void fireProjectile(){
		game.$currentState().addEntity(new AntiMatterProjectile(game, t, this));
	}

}
