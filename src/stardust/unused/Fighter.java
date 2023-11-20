package stardust.unused;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class Fighter extends StardustEntity{
	
	public Fighter(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(12);
		setDirection(Math.PI*(game.$prng().$double(0, 1)>0.5?0.5:1.5));
		setSpeedVector(t, 180);
		//blip();
	}
	
	private int ammo=1;
	private double reload=0;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		if(ammo>0){
			cooldown+=dt;
			if(cooldown>0.125){
				game.$currentState().addEntity(new Bomb(game, t, this));
				cooldown=0;
				ammo--;
			}
		}else{
			reload+=dt;
			if(reload>2){
				reload=0;
				ammo=1;
			}
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		6,-2,6,-1,6,-1,5,0,6,-2,5,-3,5,-3,2,-3,2,-3,1,-4,1,-4,-1,-4,-1,-4,-2,-3,-2,-3,-7,-3,-7,-3,-8,-5,-8,-5,-10,-5,-10,-5,-9,-2,-9,-2,-10,-1,-10,-1,-9,-1,-9,-1,-2,0,-2,0,5,0,2,-3,0,-2,0,-2,-2,-3,0,-2,0,-4,-2,-1,4,-2,4,-2,5,-1,5,-1,2,1,2,1,-2,-1,-7,-3,-9,-2,5,-3,5,0,-2,0,-4,0,-4,0,-4,-3,
	};
	private double scale=1.75;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);

		if(t<Math.PI){
			GL11.glRotated(180,0,1,0);
		}
		
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
		//game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
	}
}