package stardust.entities.demonstar;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class MartianFighter extends MartianShip{
	
	public int points(){
		return 25;
	}
	
	public MartianFighter(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
		setSpeedVector(t, 60);
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
				StardustEntity e=new MartianProjectile(game, t, this);
				e.setXY(x+4, y);
				game.$currentState().addEntity(e);
				e=new MartianProjectile(game, t, this);
				e.setXY(x-4, y);
				game.$currentState().addEntity(e);
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
		-2,-5,0,-4,0,-4,2,-5,2,-6,2,-4,2,-4,7,-4,7,-4,7,-6,7,-6,2,-6,3,-6,2,-7,2,-7,2,-8,2,-8,7,-8,7,-8,7,-7,7,-7,6,-6,2,-4,2,3,2,3,7,3,7,3,7,-4,2,3,3,4,3,4,6,4,6,4,7,3,7,-5,12,-1,12,-1,12,5,12,5,9,8,9,8,9,5,9,5,7,3,-2,-6,-2,3,-2,3,-3,4,-3,4,-6,4,-6,4,-7,3,-7,3,-2,3,-7,3,-7,-6,-7,-6,-2,-6,-2,-4,-7,-4,-3,-6,-2,-7,-2,-7,-2,-8,-2,-8,-7,-8,-7,-8,-7,-7,-7,-7,-6,-6,0,-4,-2,-1,0,-4,2,-1,-2,3,0,7,0,7,2,3,-7,-5,-12,-1,-12,-1,-12,5,-12,5,-9,8,-9,8,-9,5,-9,5,-7,3,7,-4,9,0,9,0,7,3,-7,-4,-9,0,-9,0,-7,3,-2,-1,2,-1,2,2,-2,2,
	};

	private double scale=0.825;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		
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
