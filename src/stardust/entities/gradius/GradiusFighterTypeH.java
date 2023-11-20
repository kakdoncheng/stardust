package stardust.entities.gradius;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class GradiusFighterTypeH extends GradiusShip{
	public int points(){
		return 25;
	}
	
	public GradiusFighterTypeH(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(6);
		setDirection(Math.PI*0.5);
		setSpeedVector(t, 90);
	}
	
	private int ammo=1;
	private double reload=0;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// follow target
		if(target.$y()>y+r){
			setDirection(Math.PI*0.25);
			setSpeedVector(t, 90);
		}else if(target.$y()<y-r){
			setDirection(Math.PI*0.75);
			setSpeedVector(t, 90);
		}else{
			setDirection(Math.PI*0.5);
			setSpeedVector(t, 90);
		}
		
		if(distanceTo(target)<240){
			if(ammo>0){
				cooldown+=dt;
				if(cooldown>0.125){
					game.$currentState().addEntity(new GradiusProjectile(game, directionTo(target), this));
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
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-2,-4,-5,6,-5,6,-2,9,-2,9,-2,4,-2,4,0,3,0,3,2,4,2,4,2,9,2,9,5,6,5,6,2,-4,2,-4,2,-2,2,-2,0,-3,0,-3,-2,-2,-2,-2,-2,-4,-2,-4,-10,-1,-10,-1,-10,1,-10,1,-3,6,-3,6,-2,9,2,9,3,6,3,6,10,1,10,1,10,-1,10,-1,2,-4,-2,-3,-1,-5,-1,-5,1,-5,1,-5,2,-3,2,-3,-2,-3,-1,-5,-2,-6,-2,-6,2,-6,2,-6,1,-5,-10,0,-11,-2,-11,-2,-8,-8,-8,-8,-9,-4,-9,-4,-5,6,5,6,9,-4,9,-4,8,-8,8,-8,11,-2,11,-2,10,0,-2,7,0,8,0,8,2,7,
	};

	private double scale=0.825;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
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
