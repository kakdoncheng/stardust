package stardust.entities.asteroids;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.entities.PlayerStarfighter;
import stardust.entities.StardustEntity;

public class DumbBogey extends StardustEntity{
	
	public int points(){
		return 25;
	}
	
	public DumbBogey(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(game.$prng().$double(0, 8)*(Math.PI/4));
		setSpeedVector(t, 80);
	}
	
	private double cooldown=0;
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// weapons
		cooldown+=dt;
		if(cooldown>0.75){
			game.$currentState().addEntity(new BogeyProjectile(game, game.$prng().$double(0, Math.PI*2), this));
			cooldown=0;
		}
		
		// resolve movement
		updatePosition(dt);
		if(wraparoundIfOutOfScreenBounds()){
			t+=game.$prng().$int(-1, 1)*(Math.PI/4);
			t=Vector.constrainTheta(t);
			setSpeedVector(t, 80);
		}
	}

	//x1, y1, x2, y2 line render
	private double[] l={
		-2,-3,2,-3,
		2,-3,3,-1,
		3,-1,-3,-1,
		-3,-1,-2,-3,
		-3,-1,-6,1,
		-6,1,6,1,
		6,1,3,-1,
		-3,1,-2,2,
		-2,2,0,2.5,
		0,2.5,2,2,
		2,2,3,1,
	};
	private double scale=1.5;
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
