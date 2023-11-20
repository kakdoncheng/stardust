package stardust.entities.terra;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import stardust.gfx.VectorGraphics;
import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;

public class PlayerOrbitalDrone extends StardustEntity{

	public PlayerOrbitalDrone(StardustGame game, double x, double y, double dist) {
		super(game);
		this.setBoundRadius(4);
		ox=x;
		oy=y;
		this.dist=dist;
		this.setDirection(game.$prng().$double(0, Math.PI));
		this.setXY(ox+Vector.vectorToDx(t, dist), oy+Vector.vectorToDy(t, dist));
		rc=new RadarScan(game, 0, 0);
		rc.lockOnEntity(this);
	}
	
	private RadarScan rc;

	private double ox;
	private double oy;
	private double dist;
	
	private double aimt=0;
	private int ammo=0;
	private double reload=4;
	private double cooldown=0;
	private double invt=3;
	private double dtt=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// aim with mouse
		aimt=Vector.directionFromTo(x,y,MouseHandler.$mx(),MouseHandler.$my());
				
		// movement
		// mouse towards mouse, then snap to place
		setSpeedVector(aimt,Vector.distanceFromTo(x,y,MouseHandler.$mx(),MouseHandler.$my()));
		updatePosition(dt);
		t=Vector.directionFromTo(ox,oy,x,y);
		this.setXY(ox+Vector.vectorToDx(t, dist), oy+Vector.vectorToDy(t, dist));
		
		
		// weapons
		boolean fired=false;
		cooldown+=dt;
		if(ammo>0&&Keyboard.isKeyDown(Keyboard.KEY_R)){
			ammo=0;
		}
		if(ammo>0){
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
	    		if(cooldown>=0.125){
	    			ammo--;
	    			game.$currentState().addEntity(new BallProjectile(game, aimt, this));
	    			fired=true;
	    		}
	    	}
		}else{
			reload+=0.5*dt;
			if(reload>=1){
				ammo=30;
				reload=0;
			}
		}
		
    	// debug secondary
    	//if(GameFlags.is("debuginv")){
    	//	if(Mouse.isButtonDown(1)){
        //		if(cooldown>0.125){
        //			AntiMatterMissile e=new AntiMatterMissile(game, t, this);
        //			e.biasTowards(MouseHandler.$mx(), MouseHandler.$my());
        //			game.$currentState().addEntity(e);
        //			fired=true;
        //		}
        //	}
    	//}
    	if(fired){
    		cooldown=0;
    	}
		
		// tracer
		dtt-=dt;
		if(dtt<=0){
			double tdx=x+Vector.vectorToDx(aimt, -4);
			double tdy=y+Vector.vectorToDy(aimt, -4);
			game.$currentState().addEntity(new TracerDot(game,tdx,tdy,game.$prng().$double(1,2),this.alpha, 2));
			dtt+=1.0/60;
		}
    	
		rc.update(dt);
		
	}

	private double l[]={
		-1,3,1,3,1,3,2,2,2,2,2,-2,2,-2,0,-8,0,-8,-2,-2,-2,-2,-2,2,-2,2,-1,3,2,-2,4,-1,2,2,4,2,-2,-2,-4,-1,-2,2,-4,2,4,-2,4,4,4,4,6,4,6,4,6,-2,6,-2,5,-3,5,-3,4,-2,-4,-2,-4,4,-4,4,-6,4,-6,4,-6,-2,-6,-2,-5,-3,-5,-3,-4,-2,-6,0,-8,1,-8,1,-8,2,-8,2,-6,3,6,0,8,1,8,1,8,2,8,2,6,3,
	};
	private double scale=1;

	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(aimt), 0, 0, 1);
		GL11.glRotated(180, 0, 0, 1);
		
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
		
		// render reload cursor
		int seg=64, rs=16;
		double ci=2*Math.PI;
		if(ammo>0){
			ci*=((double)ammo*(64.0/30))/(double)seg;
		}else{
			ci*=reload;
		}
		
		if(ammo>0||(ammo<1&&game.$currentSecond()%0.25>0.25*0.5)){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			for(double i=0;i<ci;i+=ci/seg){
				double dxyx1=Vector.vectorToDx(i,rs), 
					dxyy1=Vector.vectorToDy(i,rs), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),rs),
					dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
				GL11.glVertex2d(MouseHandler.$ax()+dxyx1, MouseHandler.$ay()+dxyy1);
				GL11.glVertex2d(MouseHandler.$ax()+dxyx2, MouseHandler.$ay()+dxyy2);
			}
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			VectorGraphics.renderDotCursor();
		}
		
		rc.render(c);
		
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
