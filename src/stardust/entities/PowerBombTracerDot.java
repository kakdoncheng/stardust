package stardust.entities;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;

public class PowerBombTracerDot extends TracerDot{

	public PowerBombTracerDot(StardustGame game, double x, double y, double r) {
		super(game, x, y, r, 1, 2, game.$prng().$int(4, 8));
	}
	
	public void render(Camera c) {
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glColor4d(1, 0.5, 0, alpha);
		setRadarColor(1);
		
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,r*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
