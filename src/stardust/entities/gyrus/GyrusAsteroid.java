package stardust.entities.gyrus;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class GyrusAsteroid extends GyrusEntity{
	
	private double[] vertices;
	public GyrusAsteroid(StardustGame game) {
		super(game, game.$prng().$double(0, 2*Math.PI), 1);
		setBoundRadius(game.$prng().$int(4, 9));
		sp=game.$prng().$double(80, 160);
		spt=game.$prng().$double(-0.05*Math.PI, 0.05*Math.PI);
		tt=game.$prng().$double(0, 2*Math.PI);
		vertices=new double[16];
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(nr*0.75, nr*1.5);
		}
	}
	
	private double sp;
	private double spt;
	private double tt;
	public void update(double dt) {
		updateBlip(dt);
		
		t+=spt*dt;
		tt+=2*Math.PI*dt;
		double ddx=sp*dt*$ndxscale();
		//if(ddx<0.125){
		//	ddx=0.125;
		//}
		dx+=ddx;
		
		updateNormalizedXY();
		updateNormalizedBoundRadius();
		deactivateIfOutOfBounds();
	}
	
	public void render(Camera c) {
		
		// rough edges
		int seg=vertices.length;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		int vi=0;
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,vertices[vi]*c.$zoom()*$ndxscale()), 
					dxyy1=Vector.vectorToDy(i,vertices[vi]*c.$zoom()*$ndxscale()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom()*$ndxscale()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom()*$ndxscale());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		
		// circle
		/*
		seg=8;
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,nr*c.$zoom()*$ndxscale()), 
					dxyy1=Vector.vectorToDy(i,nr*c.$zoom()*$ndxscale()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),nr*c.$zoom()*$ndxscale()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),nr*c.$zoom()*$ndxscale());
			GL11.glVertex2d(dxyx1, dxyy1);
			GL11.glVertex2d(dxyx2, dxyy2);
			vi++;
		}
		//*/
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public void onDeath() {
		
	}

}