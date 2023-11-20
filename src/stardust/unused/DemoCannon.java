package stardust.unused;

import engine.GameFlags;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;
import stardust.entities.invaders.ShellProjectile;

public class DemoCannon extends StardustEntity{

	public DemoCannon(StardustGame game, int x, int y) {
		super(game);
		this.setBoundRadius(4);
		this.setDirection(Math.PI);
		this.setXY(x, y);
	}

	private double aF=120;
	private double adl=120;
	private double cooldown=0;
	private double invt=3;
	
	private int mf=0;
	private double mft=0;
	private StardustEntity beam;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// movement input
		if (mf<0){
			mf++;
			x-=aF*dt;
			if(x<-adl){
				x=-adl;
			}
		}else if (mf>0){
			mf--;
			x+=aF*dt;
			if(x>adl){
				x=adl;
			}
		}else{
			mft-=dt;
			if(mft<=0){
				mft=game.$prng().$double(0, 1);
				mf=game.$prng().$int(-60, 60);
			}
		}
		
		updatePosition(dt);
		
		// weapons
		cooldown+=dt;
		if(beam==null || !beam.isActive()){
			if(cooldown>0.125){
				StardustEntity e=new ShellProjectile(game, t, this);
				beam=e;
    			game.$currentState().addEntity(e);
    			cooldown=0;
    		}
		}
    	
    	// check for collisions
	}

	private double l[]={
		-13,-4,-13,4,-13,4,-11,4,-11,4,-11,2,-11,2,-3,2,-3,2,-3,4,-3,4,-5,4,-5,4,-5,6,-5,6,5,6,5,6,5,4,5,4,3,4,3,4,3,2,3,2,11,2,11,2,11,4,11,4,13,4,13,4,13,-4,13,-4,11,-4,11,-4,11,-2,11,-2,9,-2,9,-2,9,-4,9,-4,5,-4,5,-4,5,-6,5,-6,1,-6,1,-6,1,-12,1,-12,-1,-12,-1,-12,-1,-6,-1,-6,-5,-6,-5,-6,-5,-4,-5,-4,-9,-4,-9,-4,-9,-2,-9,-2,-11,-2,-11,-2,-11,-4,-11,-4,-13,-4,
	};
	private double scale=0.625;

	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		
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
			return false;
		}
		return !(invt>0);
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
