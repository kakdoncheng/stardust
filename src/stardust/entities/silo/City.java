package stardust.entities.silo;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class City extends StardustEntity{

	public City(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setDirection(0);
		setBoundRadius(1);
		blip();
		flip=game.$prng().$double(0, 1)>0.5;
	}
	

	private double invt=0;
	
	public void update(double dt) {
		if(invt>0){
			invt-=dt;
		}
	}

	// x1, y1, x2, y2 line render
	private double l[]={
		-12,0,-12,-1,-12,-1,12,-1,12,-1,12,0,12,0,-12,0,-11,-1,-11,-3,-11,-3,-10,-3,-10,-3,-10,-1,-10,-2,-7,-2,-7,-2,-7,-1,-10,-2,-8,-3,-8,-3,-8,-2,-8,-2,-7,-3,-7,-2,-7,-4,-7,-4,-5,-4,-5,-4,-5,-1,-6,-4,-6,-7,-6,-7,-4,-7,-4,-7,-4,-3,-5,-3,-3,-3,-3,-3,-3,-1,0,-1,0,-3,0,-3,4,-3,4,-3,4,-1,0,-3,1,-4,1,-4,1,-3,1,-3,2,-4,2,-4,2,-3,2,-3,3,-4,3,-4,3,-3,3,-3,4,-4,4,-4,4,-3,4,-4,4,-5,4,-5,5,-5,5,-5,5,-1,5,-5,6,-5,6,-5,6,-1,8,-1,8,-2,8,-2,9,-2,9,-2,9,-1,9,-2,9,-3,9,-3,10,-3,10,-3,10,-1,10,-2,11,-2,11,-2,11,-1,-1,-1,-1,-5,-1,-5,1,-5,1,-5,1,-4,-1,-8,-3,-8,-3,-8,-3,-3,-1,-8,-1,-5,3,-4,3,-7,3,-7,5,-7,5,-7,5,-5,6,-4,8,-4,8,-4,8,-2,0,-5,0,-6,0,-6,2,-6,2,-6,2,-4,6,-5,6,-6,6,-6,7,-5,7,-5,6,-5,6,-5,7,-6,
	};
	private boolean flip;
	private double scale=1;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		if(flip){
			GL11.glRotatef(180, 0, 1, 0);
		}
		GL11.glBegin(GL11.GL_LINES);
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
		
	}

}