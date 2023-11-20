package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.silo.AnnihilatingExplosionCluster;
import stardust.entities.silo.AnnihilatingMissile;
import engine.gfx.Camera;

public class DroneTypeB extends Starcraft{
	
	public int points() {
		return 50;
	}
	
	public DroneTypeB(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
	}
	
	private int ammo=8;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// weapons
		// no reload
		t=directionTo(target);
		if (distanceTo(target)<280 && ammo>0){
    		cooldown+=dt;
    		if(cooldown>0.25){
    			// recoil
    			applyAccelerationVector(t+Math.PI, 8, 1);
    			
    			AnnihilatingMissile e=new AnnihilatingMissile(game, target.$x(), target.$y(), t+game.$prng().$double(-Math.PI/4, Math.PI/4), this);
    			e.follow((StardustEntity) target);
    			game.$currentState().addEntity(e);
    			cooldown=0;
    			ammo--;
    		}
    	}
		
		// resolve movement
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		-5,-3,-7,-1,-7,-1,-7,2,-7,2,-6,4,-6,4,-6,2,-6,2,-5,1,-5,1,-5,0,-5,0,-3,1,-5,-3,-5,-1,-5,-1,-3,-2,5,-3,7,-1,7,-1,7,2,7,2,6,4,6,4,6,2,6,2,5,1,5,1,5,0,5,0,3,1,3,-2,5,-1,5,-1,5,-3,-1,3,0,2,0,2,1,3,-1,-3,1,-3,-1,4,-3,4,-3,4,-3,-4,-3,-4,-1,-4,-1,-4,-1,4,1,-4,1,4,1,4,3,4,3,4,3,-4,3,-4,1,-4,
	};
	private double scale=1.5;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y-2), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

	public void onDeath() {
		if(ammo>0){
			game.$currentState().addEntity(new AnnihilatingExplosionCluster(game,x,y,this,ammo));
			game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
			
		}
	}
}