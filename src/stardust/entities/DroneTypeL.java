package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.boss.LascannonBeam;
import engine.Vector;
import engine.gfx.Camera;

public class DroneTypeL extends Starcraft{
	
	public int points() {
		return 90;
	}
	
	public DroneTypeL(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
	}
	
	private int ammo=3;
	private double warmup=2;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// weapons
		rotateTowards(target, Math.PI*0.5, dt);
		if (distanceTo(target)<280 && ammo>0){
			warmup-=dt;
			if(warmup<=0){
				cooldown+=dt;
	    		if(cooldown>0.125){
	    			// recoil
	    			applyAccelerationVector(t+Math.PI, 8, 1);
	    			game.$currentState().addEntity(new LascannonBeam(game, x, y, t, this));
	    			cooldown=0;
	    			ammo--;
	    		}
			}
    	}else{
    		warmup=2;
    	}
		
		// resolve movement
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		-3,-14,-3,-12,3,-14,3,-12,4,-14,-4,-14,-4,-14,-4,-12,-4,-12,4,-12,4,-12,4,-14,-2,-12,-4,-10,-4,-10,-4,-2,-4,-2,-2,0,-2,0,2,0,2,0,4,-2,4,-2,4,-10,4,-10,2,-12,2,-12,2,0,-2,0,-2,-12,-2,-12,-4,-6,-4,-6,-2,0,2,0,4,-6,4,-6,2,-12,-4,-8,-8,-2,-8,-2,-20,-2,-20,-2,-20,-10,-20,-10,-8,-10,-8,-10,-4,-4,-8,-10,-8,-2,-12,-2,-12,-10,-16,-10,-16,-2,4,-8,8,-2,8,-2,20,-2,20,-2,20,-10,20,-10,8,-10,8,-10,4,-4,8,-10,8,-2,12,-10,12,-2,16,-10,16,-2,-2,0,-6,1,-6,1,-10,3,-10,3,10,3,10,3,6,1,6,1,2,0,1,0,4,1,4,1,6,3,0,0,1,1,1,1,2,3,0,0,-1,1,-1,1,-2,3,-1,0,-4,1,-4,1,-6,3,-6,1,6,1,-1,3,0,11,0,11,1,3,0,3,0,11,
	};
	private double scale=0.825;
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
		
		// render sight line
		if(distanceTo(target)<280 && ammo>0 && warmup>0){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1, 0, 0, 1-(warmup/2));
			GL11.glVertex2d(c.$cx($x()), c.$cy($y()));
			GL11.glVertex2d(c.$cx($x()+Vector.vectorToDx(t, 640)), c.$cy($y()+Vector.vectorToDy(t, 640)));
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
	}

	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
		if(killer instanceof PlayerStarfighter){
			game.$currentState().addEntity(new PowerLascannon(game, x, y, (StardustEntity) killer));
		}
	}
}
