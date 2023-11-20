package stardust.entities.silo;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.RadarBlip;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;

public class RadarTower extends StardustEntity{

	public RadarTower(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setDirection(0);
		blip();
	}
	
	private double timer=1;
	private double invt=0;

	public void update(double dt) {
		if(invt>0){
			invt-=dt;
		}
		timer+=dt;
		if(timer>=1){
			game.$currentState().addEntity(new RadarBlip(game, x, y-28));
			timer-=1;
		}
		
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		-4,0,-4,-1,-4,-1,4,-1,4,-1,4,0,4,0,-4,0,-2,-1,2,-5,2,-5,-2,-9,-2,-9,2,-13,2,-13,-2,-13,-2,-13,-2,-1,2,-1,2,-13,-2,-13,2,-9,2,-9,-2,-5,-2,-5,2,-1,0,-1,0,-13,-3,-1,0,-27,0,-27,3,-1,0,-13,0,-27,2,-13,-1,-18,-2,-13,1,-18,-1,-25,1,-25,-1,-24,1,-24,1,-26,1,-23,1,-23,2,-24,2,-24,2,-25,2,-25,1,-26,-1,-26,-1,-23,-1,-23,-2,-24,-2,-24,-2,-25,-2,-25,-1,-26,
	};
	private double scale=1;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		//setRadarColor(1);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?1:0);
		}else{
			GL11.glColor4d(1,1,1,1);
		}
		GL11.glColor4d(1,1,1,0.5);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}

	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
