package stardust.entities.demonstar;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.AntiMatterMissile;
import stardust.entities.Asteroid;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Projectile;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;
import stardust.entities.TracerDot;
import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;

public class PlayerSpaceship extends StardustEntity{

	public PlayerSpaceship(StardustGame game, double x, double y) {
		super(game);
		this.setDirection(0);
		this.setBoundRadius(4);
		this.setXY(x, y);
		rc=new RadarScan(game, 0, 0);
		rc.lockOnEntity(this);
	}
	
	private RadarScan rc;

	private double aF=180;
	private double dtt=0;
	private double cooldown=0;
	private double invt=3;
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
		
		// reset speed & movement input
		dx=0;
		dy=0;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			applyAccelerationVector(Math.PI, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			applyAccelerationVector(Math.PI*0.5, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			applyAccelerationVector(0, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			applyAccelerationVector(Math.PI*1.5, aF, 1);
		}
		
		updatePosition(dt);
		
		// weapons
		boolean fired=false;
		cooldown+=dt;
    	if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		if(cooldown>0.125){
    			fireProjectile();
    			fired=true;
    		}
    	}
    	
    	// debug secondary
    	if(GameFlags.is("debuginv")){
    		if(Mouse.isButtonDown(1)){
        		if(cooldown>0.125){
        			AntiMatterMissile e=new AntiMatterMissile(game, Math.PI, this);
        			//e.biasTowards(MouseHandler.$mx(), MouseHandler.$my());
        			game.$currentState().addEntity(e);
        			fired=true;
        		}
        	}
    	}
    	if(fired){
    		cooldown=0;
    	}
    	
    	// limit movement to screen edge
    	if(x<game.$leftScreenEdge()){
    		x=game.$leftScreenEdge();
    	}
    	if(x>game.$rightScreenEdge()){
    		x=game.$rightScreenEdge();
    	}
    	if(y<game.$topScreenEdge()){
    		y=game.$topScreenEdge();
    	}
    	if(y>game.$bottomScreenEdge()){
    		y=game.$bottomScreenEdge();
    	}
    	
    	// tracer
    	dtt-=dt;
		if(dtt<=0){
			double tt=game.$prng().$double(0, 2*Math.PI);
			double di=game.$prng().$double(0, r*0.25);
			double dx=Vector.vectorToDx(tt, di);
			double dy=Vector.vectorToDy(tt, di);
			StardustEntity e=new TracerDot(game,x+dx,y+dy+4,game.$prng().$double(1, 3),this.alpha,3,game.$prng().$int(4, 8));
			e.applyAccelerationVector(0, 60, 1);
			game.$currentState().addEntity(e);
			dtt+=1.0/60;
		}
    	
		// check for collisions
		// inv vs asteroids
    	if(isCollidable()){
    		for(StardustEntity e:game.$currentState().$entities()){
    			if(e instanceof Projectile || e instanceof Asteroid){
    				continue;
    			}
    			if(e!=this && e.isCollidable() && e.isActive() && distanceTo(e)<=r+e.$r()){
    				//System.out.println(e);
    				deactivate();
    			}
    		}
    	}
    	
    	rc.update(dt);
	}

	private double l[]={
		-2,5,0,6,0,6,2,5,2,5,2,-4,2,-4,0,-10,0,-10,-2,-4,-2,-4,-2,5,3,-3,4,-4,4,-4,5,-3,5,-3,5,4,5,4,3,4,3,4,3,-3,-3,-3,-4,-4,-4,-4,-5,-3,-5,-3,-5,4,-5,4,-3,4,-3,4,-3,-3,-2,-3,-3,-2,-3,3,-2,3,2,-3,3,-2,2,3,3,3,5,0,8,3,8,3,8,4,8,4,5,3,-5,0,-8,3,-8,3,-8,4,-8,4,-5,3,
	};
	private double scale=1;

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
	
	protected void fireProjectile(){
		// 0
		StardustEntity e=new AntiMartianProjectile(game, Math.PI, this);
		e.setXY(x+4, y);
		game.$currentState().addEntity(e);
		e=new AntiMartianProjectile(game, Math.PI, this);
		e.setXY(x-4, y);
		game.$currentState().addEntity(e);
		
		// 1
		e=new AntiMartianProjectile(game, Math.PI, this);
		e.setXY(x, y-4);
		game.$currentState().addEntity(e);
		
		// 2
		e=new AntiMartianProjectile(game, Math.PI+0.05, this);
		e.setXY(x+6, y);
		game.$currentState().addEntity(e);
		e=new AntiMartianProjectile(game, Math.PI-0.05, this);
		e.setXY(x-6, y);
		game.$currentState().addEntity(e);
	}

}
