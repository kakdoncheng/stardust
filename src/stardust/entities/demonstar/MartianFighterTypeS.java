package stardust.entities.demonstar;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;

public class MartianFighterTypeS extends MartianShip{
	
	public int points(){
		return 25;
	}
	
	public MartianFighterTypeS(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
		setSpeedVector(t, aF);
	}
	
	private double aF=90;
	private double mt=0.5;
	
	private int ammo=1;
	private double reload=0;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// serpentine move
		mt+=dt;
		if(mt%2<=1){
			applyAccelerationVector(Math.PI*0.5, aF*Math.PI, dt);
		}else{
			applyAccelerationVector(Math.PI*1.5, aF*Math.PI, dt);
		}
		
		// weapons
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
			if(reload>8){
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
		-1,5,1,5,1,5,2,2,-1,5,-2,2,-2,2,-2,-2,-2,-2,-1,-3,1,-3,2,-2,2,-2,2,2,2,2,-2,2,-2,-2,2,-2,-2,-4,-2,-3,-2,-3,2,-3,2,-3,2,-4,2,-4,-2,-4,-1,-4,-2,-5,-2,-5,2,-5,2,-5,1,-4,-2,-1,-7,-1,-7,-1,-7,1,-7,1,-2,1,2,-1,7,-1,7,-1,7,1,7,1,2,1,-6,-1,-6,1,6,-1,6,1,2,-1,6,0,6,0,2,1,-2,-1,-6,0,-6,0,-2,1,-1,5,-1,2,1,5,1,2,-2,-2,-9,-5,-9,-5,-4,-1,-4,1,-9,5,-9,5,-2,2,2,-2,9,-5,9,-5,4,-1,4,1,9,5,9,5,2,2,-4,-3,-3,-3,-3,1,-2,6,-2,6,-2,2,3,1,2,6,2,6,2,2,
	};
	private double scale=1;
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
