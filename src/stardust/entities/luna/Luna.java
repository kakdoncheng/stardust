package stardust.entities.luna;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class Luna extends StardustEntity{
	
	private double[] vertices;
	public Luna(StardustGame game) {
		super(game);
		setXY(0, 0);
		setDirection(0);
		setBoundRadius(48);
		vertices=new double[16];
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(r*0.875, r*1.125);
		}
	}

	public void update(double dt) {
		t+=-Math.PI*0.025*dt;
		updateBlip(dt);
	}

	public void render(Camera c) {
		
		// render luna bounds
		int seg=vertices.length;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		int vi=0;
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,vertices[vi]*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,vertices[vi]*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,r*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r*c.$zoom());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}
}
