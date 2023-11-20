package stardust.unused;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;

public class Bomber extends StardustEntity{
	
	public Bomber(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(12);
		setDirection(Math.PI*(game.$prng().$double(0, 1)>0.5?0.5:1.5));
		setSpeedVector(t, 160);
		//blip();
	}
	
	private int ammo=4;
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
				ammo=4;
			}
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		14,0,13,-2,13,-2,13,2,13,2,14,0,13,-2,11,-3,11,-3,10,-2,10,-2,6,-2,6,-2,4,-4,4,-4,10,-4,10,-4,11,-3,10,-2,10,-4,6,-4,6,-2,4,-4,-8,-3,-8,-3,-12,-6,-12,-6,-15,-6,-15,-6,-14,-3,-14,-3,-15,-2,-15,-2,-15,-1,-15,-1,-14,-1,-14,-1,-15,1,-15,1,-13,1,-13,1,-9,0,-9,0,0,2,0,2,-2,4,-2,4,2,4,2,4,12,1,12,1,11,0,11,0,3,0,3,0,0,2,13,2,10,3,10,3,6,3,-8,-3,-13,-2,-13,-2,-14,-3,-14,-1,-10,-1,-10,-1,-9,0,
	};
	private double scale=1.25;
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