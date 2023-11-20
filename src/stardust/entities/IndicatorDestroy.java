package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.CharGraphics;
import engine.gfx.Camera;

public class IndicatorDestroy extends StardustEntity{
	
	public IndicatorDestroy(StardustGame game, StardustEntity target) {
		this(game, target, false);
	}
	
	public IndicatorDestroy(StardustGame game, StardustEntity target, boolean timer) {
		this(game, target.$x(), target.$y());
		this.setTarget(target);
		usetimer=timer;
	}
	
	public IndicatorDestroy(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(0);
		this.setDirection(0);
		this.setTarget(null);
		usetimer=false;
	}
	
	private double timer=3;
	private boolean usetimer;
	public void update(double dt) {
		if(!isActive()){
			return;
		}
		if(target!=null) {
			if(!target.isActive() && game.$currentSecond()%0.5>=0.25){
				deactivate();
				return;
			}
			setXY(target.$x(), target.$y());
		}
		if(usetimer || target==null){
			timer-=dt;
			if(timer<=0 && game.$currentSecond()%0.5>=0.25){
				deactivate();
				return;
			}
		}
	}

	//x1, y1, x2, y2 line render
	public void render(Camera c) {
		double l[]={
				//0,0,-2,2,-2,2,-1,2,-1,2,-1,6,-1,6,1,6,1,6,1,2,1,2,2,2,2,2,0,0,
				//0,0,-4,7,-4,7,4,7,4,7,0,0,
				0,0,-2,2,-2,2,-1,2,-1,2,-1,4,-1,4,1,4,1,4,1,2,1,2,2,2,2,2,0,0,
		};
		double scale=3;
		// render bounds circle
		if(game.$currentSecond()%0.5<0.25){
			if(target!=null){
				//target.renderCollisionBounds(c, 64);
				CharGraphics.drawRedString("DESTROY", c.$cx(x)-31, c.$cy(y+target.$r()+18), 1f);
			}else{
				CharGraphics.drawRedString("DESTROY", c.$cx(x)-31, c.$cy(y+14), 1f);
			}
		}
		// render arrow
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		
		if(target!=null){
			GL11.glTranslatef(c.$cx(x), c.$cy(y+target.$r()+4), 0);
		}else{
			GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		}
		//GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,game.$currentSecond()%0.5>=0.25?0:1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
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
