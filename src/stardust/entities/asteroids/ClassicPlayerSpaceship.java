package stardust.entities.asteroids;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Explosion;
import stardust.entities.Power;
import stardust.entities.Projectile;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;

public class ClassicPlayerSpaceship extends StardustEntity{

	public ClassicPlayerSpaceship(StardustGame game, int x, int y) {
		super(game);
		this.setBoundRadius(4);
		this.setXY(x, y);
		this.setDirection(Math.PI);
		rc=new RadarScan(game, 0, 0);
		rc.lockOnEntity(this);
	}

	private boolean click=true;
	private double aF=250;
	private double cooldown=0;
	private double invt=3;
	private double error=0;
	
	private RadarScan rc;
	private Power power;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// movement input
		boolean hasInput=false;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			applyAccelerationVector(t, aF, dt);
			hasInput=true;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			t-=Math.PI*1.5*dt;
    	}
    	if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
    		t+=Math.PI*1.5*dt;
    	}
		
		// friction brakes
		// F = v^2 * 0.5pDA
		double cs=$speed();
		if(cs>(hasInput?0:8)){
			double ft=Vector.constrainTheta(Vector.dxyToDirection(dx, dy)+Math.PI);
			double fF=hasInput?cs*cs*0.00125:aF/4;
			applyAccelerationVector(ft, fF, dt);
		}else{
			dx=0;
			dy=0;
		}
		
		updatePosition(dt);
		wraparoundIfOutOfScreenBounds();
		
		// weapons
		// delay reload timer 
		boolean fired=false;
		cooldown+=dt;
    	if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		if(power!=null&&!power.isEmpty()){
    			fired=power.usePrimary(this, dt);
    		}else{
    			if(cooldown>0.125){
    				game.$currentState().addEntity(new ClassicProjectile(game, t, this));
        			fired=true;
        			cooldown=0;
        		}
    		}
    	}
    	
    	// debug secondary
    	//if(GameFlags.is("debuginv")){
    	//	if(Mouse.isButtonDown(1)){
        //		if(cooldown>0.125){
        //			AntiMatterMissile e=new AntiMatterMissile(game, t, this);
        //			//e.biasTowards(MouseHandler.$mx(), MouseHandler.$my());
        //			game.$currentState().addEntity(e);
        //			fired=true;
        //		}
        //	}
    	//}
    	
    	// catch up reload timer
    	if(fired){
    		cooldown=0;
    	}
    	
    	
    	// blink error
    	error-=error*dt*0.5;
		if(error<0){
			error=0;
		}
    	if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
    		// panic blink
    		if(click){
				click=false;
				double hw=game.$displayWidth()/2;
				double hh=game.$displayHeight()/2;
				double dx=game.$prng().$double(-hw, hw);
				double dy=game.$prng().$double(-hh, hh);
				game.$currentState().addEntity(new Explosion(game, x, y, 24));
				setXY(game.$camera().$dx()+dx, game.$camera().$dy()+dy);
				game.$currentState().addEntity(new Explosion(game, x, y, 8));
    		}
    	}else if(game.isBulletTimeActive() && Mouse.isButtonDown(1)){
    		// focused blink
			if(click){
				click=false;
				game.$currentState().addEntity(new Explosion(game, x, y, 24));
				double tx=MouseHandler.$mx();
				double ty=MouseHandler.$my();
				if(error>0){
					double et=game.$prng().$double(0, 2*Math.PI);
    				double ed=game.$prng().$double(0, error);
    				tx+=Vector.vectorToDx(et, ed);
    				ty+=Vector.vectorToDy(et, ed);
				}
				setXY(tx, ty);
				game.$currentState().addEntity(new Explosion(game, x, y, 8));
				error+=36;
			}
		} else {
			click=true;
		}
    	
    	// check for collisions
    	if(isCollidable()){
    		for(StardustEntity e:game.$currentState().$entities()){
    			if(e instanceof Projectile){
    				continue;
    			}
    			if(e!=this && e.isCollidable() && e.isActive() && distanceTo(e)<=r+e.$r()){
    				//System.out.println(e);
    				deactivate();
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

	// legacy render code
	private double scale=1.25;
	private double[] cx={ 0, 3, 2,-2,-3, 0};
	private double[] cy={-6, 3, 1, 1, 3,-6};
	private double[] mcx={-1,1,0,-1};
	private double[] mcy={1.5,1.5,4,1.5};
	public void render(Camera c) {
				
		//render ship
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);	
		GL11.glRotatef((float)Math.toDegrees(t+Math.PI), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?1:0);
		}else{
			GL11.glColor4d(1,1,1,1);
		}
		for(int i=0; i<cx.length; i++){
			GL11.glVertex2d(cx[i]*c.$zoom()*scale, cy[i]*c.$zoom()*scale);
			GL11.glVertex2d(cx[(i+1)%cx.length]*c.$zoom()*scale, cy[(i+1)%cx.length]*c.$zoom()*scale);
		}
		
		//if moving
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			for(int i=0; i<mcx.length; i++){
				GL11.glVertex2d(mcx[i]*c.$zoom()*scale, mcy[i]*c.$zoom()*scale);
				GL11.glVertex2d(mcx[(i+1)%mcx.length]*c.$zoom()*scale, mcy[(i+1)%mcx.length]*c.$zoom()*scale);
			}
    	}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// if bullet time
		// render error range & point for hyperspace jump
		if(game.isBulletTimeActive()){
			// error
			int seg=64;
			double rs=error*c.$zoom();
			if(rs<16){
				rs=16;
			}
			double ci=2*Math.PI;
			double cis=ci/seg;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1-game.$currentSecond());
			for(double i=0;i<ci-cis;i+=cis){
				double dxyx1=Vector.vectorToDx(i,rs), 
					dxyy1=Vector.vectorToDy(i,rs), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),rs),
					dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
				GL11.glVertex2d(MouseHandler.$ax()+dxyx1, MouseHandler.$ay()+dxyy1);
				GL11.glVertex2d(MouseHandler.$ax()+dxyx2, MouseHandler.$ay()+dxyy2);
			}
			GL11.glEnd();
			
			// dot
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			GL11.glVertex2d(MouseHandler.$ax()-1, MouseHandler.$ay()-1);
			GL11.glVertex2d(MouseHandler.$ax()+1, MouseHandler.$ay()+1);
			GL11.glVertex2d(MouseHandler.$ax()-1, MouseHandler.$ay()+1);
			GL11.glVertex2d(MouseHandler.$ax()+1, MouseHandler.$ay()-1);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
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
