package stardust.entities;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.gfx.VectorGraphics;

public class RadarBlip extends StardustEntity{
	public RadarBlip(StardustGame game, double x, double y) {
		super(game);
		setXY(x, y);
		setBoundRadius(0);
	}

	private int range=480;
	private double rr=0;
	private double rlast=0;
	private double fade=1;
	
	public void update(double dt) {
		rlast=rr-16;
		rr+=dt*range;
		if(rr>range){
			fade-=4*dt;
			if(fade<0){
				active=false;
			}
			return;
		}
		for(StardustEntity e:game.$currentState().$entities()){
			if(rlast<distanceTo(e) && rr>distanceTo(e)){
				e.blip();
			}
		}
		for(int i=0;i<2;i++){
			for(StardustEntity e:game.$dust(i)){
				double dist=Vector.distanceFromTo(game.$camera().$cx(x),game.$camera().$cy(y),
						game.$dustCamera(i).$cx(e.$x()),game.$dustCamera(i).$cy(e.$y()));
				if(rlast*(StardustGame.MAIN_CAMERA_ZOOM)<dist && rr*(StardustGame.MAIN_CAMERA_ZOOM)>dist){
					e.blip();
				}
			}
		}
	}

	public void render(Camera c) {
		// disable render lines
		///*
		double ratio=0.25+(1-(rr/range));
		if(ratio>1) {
			ratio=1;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setActualRadarColor(0.25*fade*ratio);
		VectorGraphics.renderVectorCircle(x, y, rr, 64, c);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//*/
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
