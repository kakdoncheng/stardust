package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class Asteroid extends StardustEntity{
	
	public int points() {
		return 10;
	}

	private int maxSpeed=60;
	private double[] vertices;
	protected double tt;
	protected double dtt;
	public Asteroid(StardustGame game, double x, double y, int size) {
		super(game);
		if(size<2){
			size=2;
		}
		if(size>36){
			size=36;
		}
		setXY(x, y);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setBoundRadius(size);
		
		if(size<4){
			setSpeedVector(t, game.$prng().$int((int)(maxSpeed*0.75), maxSpeed));
			dtt=(game.$prng().$double(Math.PI, 2*Math.PI));
			vertices=new double[12];
		}else if(size<12){
			setSpeedVector(t, game.$prng().$int((int)(maxSpeed*0.25), (int)(maxSpeed*0.75)));
			dtt=(game.$prng().$double(0.5*Math.PI, Math.PI));
			vertices=new double[16];
		}else{
			setSpeedVector(t, game.$prng().$int((int)(maxSpeed*0.125), (int)(maxSpeed*0.25)));
			dtt=(game.$prng().$double(0, 0.5*Math.PI));
			vertices=new double[16];
		}
		
		tt=0;
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(size*0.75, size*1.5);
		}
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		tt+=dtt*dt;
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	public void render(Camera c) {
		int seg=vertices.length;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(tt), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		int vi=0;
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,vertices[vi]*c.$zoom()), 
					dxyy1=Vector.vectorToDy(i,vertices[vi]*c.$zoom()), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom()),
					dxyy2=Vector.vectorToDy(i+(ci/seg),vertices[(vi+1)%vertices.length]*c.$zoom());
			//GL11.glVertex2d(c.$cx(x+dxyx1), c.$cy(y+dxyy1));
			//GL11.glVertex2d(c.$cx(x+dxyx2), c.$cy(y+dxyy2));
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
