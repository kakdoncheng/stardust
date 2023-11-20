package stardust.unused;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.AntiMatterProjectile;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;

public class DemoStarfighter extends StardustEntity{

	public DemoStarfighter(StardustGame game, int x, int y) {
		super(game);
		this.setBoundRadius(4);
		this.setXY(x, y);
	}

	private double aF=500;
	private double cooldown=0;
	private double invt=3;
	
	private double movet=0;
	
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
		movet-=dt;
		if(movet<-20){
			movet=game.$prng().$double(-20, 5);
		}
		if(movet<0&&movet>-20){
			applyAccelerationVector(Vector.constrainTheta(t+Math.PI), aF, dt);
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
		t+=game.$prng().$double(-1, 1)*(Math.PI/8);
		cooldown+=dt;
		if(cooldown>0.125){
			game.$currentState().addEntity(new AntiMatterProjectile(game, t, this));
			cooldown=0;
		}
    	
    	// check for collisions
	}

	// legacy render code
	private double scale=1.75;
	private double[] cx={ 0, 1, 2, 5, 5, 2, 1, 1, 0,-1,-1,-2,-5,-5,-2,-1};
	private double[] cy={-2,-1,-2,-1, 0, 2, 5, 1, 3, 1, 5, 2, 0,-1,-2,-1};

	public void render(Camera c) {
		
		//render ship
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
		for(int i=0; i<cx.length; i++){
			GL11.glVertex2d(cx[i]*c.$zoom()*scale, cy[i]*c.$zoom()*scale);
			GL11.glVertex2d(cx[(i+1)%cx.length]*c.$zoom()*scale, cy[(i+1)%cx.length]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
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
			return false;
		}
		return !(invt>0);
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
