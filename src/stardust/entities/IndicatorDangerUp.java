package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.CharGraphics;
import engine.gfx.Camera;

public class IndicatorDangerUp extends StardustEntity{
	
	public IndicatorDangerUp(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setBoundRadius(0);
		this.setDirection(0);
		this.setTarget(null);
	}
	
	private double rt=0;
	private double timer=3;
	public void update(double dt) {
		if(!isActive()){
			return;
		}
		timer-=dt;
		if(timer<=0 && rt%0.5>=0.25){
			deactivate();
			return;
		}
		rt+=dt;
		
		// update alpha
		ait-=dt;
		if(rt%0.5>0.25){
			ai=0;
		}else if(ait<=0){
			if(ai<l.length){
				al[ai]=1.2;
				ai++;
			}
			ait=0.08;
		}
		for(int i=0;i<l.length;i++){
			al[i]-=4*dt;
		}
	}
	
	// x1, y1, x2, y2 line render
	private double l[][]={
			{-9,18,-10,20,-10,20,10,20,10,20,9,18,9,18,-9,18},
			{-7,14,-8,16,-8,16,8,16,8,16,7,14,7,14,-7,14},
			{0,0,-6,12,-6,12,6,12,6,12,0,0},
	};
	private double al[]={
			0,0,0
	};
	private int ai=0;
	private double ait=0;
	private double scale=1.5;
	public void render(Camera c) {
		// render bounds circle
		if(rt%0.5<0.25){
			CharGraphics.drawRedHeaderString("! DANGER !", c.$cx(x)-90, c.$cy(y+38), 1f);
		}
		// render arrow
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		for(int ii=0;ii<l.length;ii++){
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,al[ii]);
			for(int i=0; i<l[ii].length; i+=4){
				GL11.glVertex2d(l[ii][i]*c.$zoom()*scale, l[ii][i+1]*c.$zoom()*scale);
				GL11.glVertex2d(l[ii][i+2]*c.$zoom()*scale, l[ii][i+3]*c.$zoom()*scale);
			}
			GL11.glEnd();
		}
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		return false;
	}
	public void onDeath() {
		
	}

}