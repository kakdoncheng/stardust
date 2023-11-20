package stardust.entities;

import engine.Vector;
import engine.entities.Entity;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class RadarScan extends StardustEntity{

	private double lt;
	private double range;
	private Entity target;
	
	public RadarScan(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		lt=0;
		t=0;
		range=1200;//StardustGame.BOUNDS;
		setBoundRadius(0);
	}
	
	public void lockOnEntity(Entity e){
		target=e;
	}
	
	public void update(double dt) {
		if(target!=null){
			setXY(target.$x(), target.$y());
		}
		
		lt=Vector.constrainTheta(t-0.2);
		t=Vector.constrainTheta(t+2*Math.PI*dt);
		for(StardustEntity e:game.$currentState().$entities()){
			//if(distanceTo(e)>range){
			//	return;
			//}
			double edir=directionTo(e);
			if(lt<=edir && t>=edir){
				e.blip();
			}else if(lt>t){
				if((lt<=edir && edir<=2*Math.PI) || (t>=edir) && (edir>=0)){
					e.blip();
				}
			}
		}
		for(int i=0;i<2;i++){
			for(StardustEntity e:game.$dust(i)){
				double edir=Vector.directionFromTo(game.$camera().$cx(x),game.$camera().$cy(y),
						game.$dustCamera(i).$cx(e.$x()),game.$dustCamera(i).$cy(e.$y()));;
				if(lt<=edir && t>=edir){
					e.blip();
				}else if(lt>t){
					if((lt<=edir && edir<=2*Math.PI) || (t>=edir) && (edir>=0)){
						e.blip();
					}
				}
			}
		}
	}

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setActualRadarColor(0.35);
		GL11.glVertex2d(0, 0);
		GL11.glVertex2d(0, range*c.$zoom());
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		return false;
	}
	public void onDeath() {
		
	}

}
