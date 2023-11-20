package stardust.entities.luna;

import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.Projectile;
import stardust.entities.RadarBlip;
import stardust.entities.StardustEntity;

public class PlayerLunarModule extends StardustEntity{

	public PlayerLunarModule(StardustGame game) {
		super(game);
		this.setBoundRadius(4);
		this.setDirection(0);
		this.setXY(0, 0);
	}

	private double aF=100;
	private double cooldown=1;
	private double invt=3;
	
	private boolean input=false;
	private boolean frozen=false;
	public void freeze(){
		frozen=true;
	}
	public boolean isFrozen(){
		return frozen;
	}
	
	public void update(double dt) {
		if(frozen){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// player dir
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			t-=Math.PI*1.5*dt;
			input=true;
    	}
    	if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
    		t+=Math.PI*1.5*dt;
    		input=true;
    	}
    	if(!input){
    		t+=Math.PI*0.5*dt;
    	}
    	t=Vector.constrainTheta(t);
    	
    	// thrusters vector
    	if (Keyboard.isKeyDown(Keyboard.KEY_W) || Mouse.isButtonDown(0)) {
    		applyAccelerationVector(t, -aF, dt);
    	}
		
		//radar blip
		cooldown+=dt;
		if(cooldown>=1){
			game.$currentState().addEntity(new RadarBlip(game, x, y));
			cooldown-=1;
		}
		
		updatePosition(dt);
		wraparoundIfOutOfScreenBounds();
		
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
	}
	
	//x1, y1, x2, y2 line render
	private double[] l={
		//lower chassis
		-1.5,-.25,-1.5,1,
		-1.5,1,1.5,1,
		1.5,1,1.5,-.25,
		1.5,-.25,-1.5,-.25,
		-.75,-.25,-.75,1,
		.75,-.25,.75,1,
		-.5,1,-1,2,
		-1,2,1,2,
		1,2,.5,1,
		//mid landing gear
		-.75,-.25,-.25,0,
		-.25,0,.25,0,
		.25,0,.75,-.25,
		-.25,0,0,2.5,
		0,2.5,.25,0,
		0,2.5,-.5,2.75,
		-.5,2.75,.5,2.75,
		.5,2.75,0,2.5,
		//left landing gear
		-.75,-.25,-1.5,0,
		-1.5,0,-2.25,0,
		-2.25,0,-3.5,2,
		-3.5,2,-2,0,
		-1.5,0.75,-2.25,0,
		-2.25,0,-1.5,1,
		-1.5,1,-3,1.25,
		-3,1.25,-1.5,.75,
		-3.5,2,-4,2.25,
		-4,2.25,-3,2.25,
		-3,2.25,-3.5,2,
		//flip
		.75,-.25,1.5,0,
		1.5,0,2.25,0,
		2.25,0,3.5,2,
		3.5,2,2,0,
		1.5,0.75,2.25,0,
		2.25,0,1.5,1,
		1.5,1,3,1.25,
		3,1.25,1.5,.75,
		3.5,2,4,2.25,
		4,2.25,3,2.25,
		3,2.25,3.5,2,
		//upper chassis
		-1,-.25,-1.5,-.75,
		-1.5,-.75,-1.5,-2,
		-1.5,-2,-1.25,-2.25,
		-1.25,-2.25,1.25,-2.25,
		1.25,-2.25,1.5,-2,
		1.5,-2,1.5,-.75,
		1.5,-.75,1,-.25,
		0,-2.25,-1.25,-.5,
		-1.25,-.5,1.25,-.5,
		1.25,-.5,0,-2.25,
		-1.25,-2.25,-1.25,-.5,
		1.25,-2.25,1.25,-.5,
		//antennae
		-.75,-2.25,-.5,-2.5,
		-.5,-2.5,.5,-2.5,
		.5,-2.5,.75,-2.25,
		-.75,-2.25,-1.25,-2.75,
		-.5,-2.5,-1,-3
	};
	private double[] mcx={ -1,  1, 0, -1};
	private double[] mcy={2.5,2.5, 6,2.5};
	private double scale=2;
	public void render(Camera c) {
		
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
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
		//if moving
		if (Keyboard.isKeyDown(Keyboard.KEY_W) || Mouse.isButtonDown(0)) {
			for(int i=0; i<mcx.length; i++){
				GL11.glVertex2d(mcx[i]*c.$zoom()*scale, mcy[i]*c.$zoom()*scale);
				GL11.glVertex2d(mcx[(i+1)%mcx.length]*c.$zoom()*scale, mcy[(i+1)%mcx.length]*c.$zoom()*scale);
			}
    	}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//debug text
		//CharGraphics.drawString(String.format("%.1f %.1f",this.dx,this.dy), 
    	//		-state.$displayWidth()/2, +state.$displayHeight()/2-14, 1f);
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