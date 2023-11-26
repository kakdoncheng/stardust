package stardust.entities.asteroids;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.PlayerStarfighter;
import stardust.entities.StardustEntity;

public class Bogey extends StardustEntity{
	
	public int points(){
		return 100;
	}
	
	public Bogey(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(4);
		setDirection(game.$prng().$double(0, 8)*(Math.PI/4));
	}
	
	private int ammo=0;
	private double reloadt=0;
	private double cooldown=0;
	private double movet=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// move erratically
		movet+=dt;
		if(movet>0.5){
			movet=0;
			t+=game.$prng().$int(-1, 1)*(Math.PI/4);
			t=Vector.constrainTheta(t);
		}
		setSpeedVector(t, 160);
		
		// weapons
		if(ammo<1){
			reloadt+=dt;
			if(reloadt>2){
				ammo=3;
				reloadt=0;
			}
		}
		if (distanceTo(target)<240 && ammo>0){
    		cooldown+=dt;
    		if(cooldown>0.25){
    			game.$currentState().addEntity(new BogeyProjectile(game, directionTo(target), this));
    			cooldown=0;
    			ammo--;
    		}
    	}
		
		// resolve movement
		updatePosition(dt);
		wraparoundIfOutOfScreenBounds();
	}

	//x1, y1, x2, y2 line render
	private double[] l={
		-1,-4,1,-4,
		1,-4,2,-2,
		-1,-4,-2,-2,
		-2,-2,2,-2,
		2,-2,6,0,
		-2,-2,-6,0,
		-6,0,6,0,
		-6,0,-2,2,
		6,0,2,2,
		-2,2,2,2
	};
	private double scale=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		//no rotation, bogey stays upright
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0.5,0,alpha);
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
		if(killer instanceof PlayerStarfighter || killer instanceof ClassicPlayerSpaceship){
			game.$currentState().addEntity(new PowerClassicProjectile(game, x, y, (StardustEntity) killer));
		}
	}
}
