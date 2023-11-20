
package stardust.entities.gyrus;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;

public class GyrusPlayerSpaceship extends GyrusEntity{

	public GyrusPlayerSpaceship(StardustGame game) {
		super(game, 0, ndx);
		setBoundRadius(6);
	}
	
	private double dtt=0;
	private double cooldown=0;
	private double invt=3;
	
	private int bullets=0;
	private double spool=0;
	
	public void stopInvt(){
		invt=-1;
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// one-point perspective movement
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			t+=0.75*Math.PI*dt;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			t-=0.75*Math.PI*dt;
		}
		t=Vector.constrainTheta(t);
		updateNormalizedXY();
		updateNormalizedBoundRadius();
		
		// weapons
		spool-=dt;
		if(spool<=0){
			bullets=0;
		}
		cooldown+=dt;
    	if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		double at=0.0325;
    		double ac=bullets<3?0.125:0.125+(0.075*(bullets-2));
    		spool=0.5;
    		if(cooldown>ac){
    			game.$currentState().addEntity(new AntiGyrusProjectile(game, Vector.vectorToDx(t-at, ndx-4), Vector.vectorToDy(t-at, ndx-4), this));
    			game.$currentState().addEntity(new AntiGyrusProjectile(game, Vector.vectorToDx(t+at, ndx-4), Vector.vectorToDy(t+at, ndx-4), this));
    			cooldown=0;
    			bullets++;
    		}
    	}
    	
    	// exhaust
    	dtt-=dt;
		if(dtt<=0){
			double tt=game.$prng().$double(-0.005, 0.005);
			double ddx=game.$prng().$double(-1, 3);
			game.$currentState().addEntity(new GyrusTracerDot(game,t+tt,dx+ddx,game.$prng().$double(2, 8),1.2,8,game.$prng().$int(4, 8)));
			dtt+=1.0/30;
		}
    	
    	// check for collisions
		if(isCollidable()){
    		for(StardustEntity e:game.$currentState().$entities()){
    			if(e instanceof AntiGyrusProjectile){
    				continue;
    			}
    			if(e!=this && e.isCollidable() && e.isActive() && distanceTo(e)<=r+e.$r()){
    				deactivate();
    			}
    		}
    	}
	}

	private double l[]={
			-2,1,0,-1,0,-1,2,1,2,1,0,2,0,2,-2,1,0,-1,-10,2,-10,2,-2,1,0,-1,10,2,10,2,2,1,-2,-3,0,-5,0,-5,2,-3,-10,2,-5,-1,-5,-1,-4,-3,-4,-3,-2,-3,2,-3,4,-3,4,-3,5,-1,5,-1,10,2,-1,-3,0,-5,0,-5,1,-3,1,-3,0,-4,0,-4,-1,-3,-4,-3,-2,-2,-2,-2,-2,-3,2,-3,2,-2,2,-2,4,-3,-3,-3,-2,-4,-2,-4,-2,-3,2,-3,2,-4,2,-4,3,-3,
	};
	private double scale=1.5;
	public void render(Camera c) {
		// render player
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?1:0);
		}else{
			GL11.glColor4d(1,1,1,1);
		}
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		if(GameFlags.is("debuginv")){
			if(Keyboard.isKeyDown(Keyboard.KEY_F)){
				return true;
			}
			return false;
		}
		return !(invt>0);
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
