package stardust.entities.invaders;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;

public class Ufo extends StardustEntity{
	
	public int points(){
		return 25;
	}
	
	public Ufo(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(Math.PI*(game.$prng().$double(0, 1)>0.5?0.5:1.5));
		setSpeedVector(t, 80);
		//blip();
	}
	
	//private double cooldown=0;
	public void update(double dt) {
		if(!active){
			return;
		}
		//updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-2,0,-2,-2,-2,-2,-4,-2,-4,-2,-4,0,-4,0,-2,0,-8,0,-8,-2,-8,-2,-10,-2,-10,-2,-10,0,-10,0,-8,0,2,0,2,-2,2,-2,4,-2,4,-2,4,0,4,0,2,0,8,0,8,-2,8,-2,10,-2,10,-2,10,0,10,0,8,0,16,2,16,0,16,0,14,0,14,0,14,-2,14,-2,12,-2,12,-2,12,-4,12,-4,10,-4,10,-4,10,-6,10,-6,6,-6,6,-6,6,-8,6,-8,-6,-8,-6,-8,-6,-6,-6,-6,-10,-6,-10,-6,-10,-4,-10,-4,-12,-4,-12,-4,-12,-2,-12,-2,-14,-2,-14,-2,-14,0,-14,0,-16,0,-16,0,-16,2,-16,2,-12,2,-12,2,-12,4,-12,4,-10,4,-10,4,-10,6,-10,6,-8,6,-8,6,-8,4,-8,4,-6,4,-6,4,-6,2,-6,2,-2,2,-2,2,-2,4,-2,4,2,4,2,4,2,2,2,2,6,2,6,2,6,4,6,4,8,4,8,4,8,6,8,6,10,6,10,6,10,4,10,4,12,4,12,4,12,2,12,2,16,2,
	};
	private double scale=0.625;
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
		game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
	}
}
