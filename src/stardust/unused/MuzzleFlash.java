package stardust.unused;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class MuzzleFlash extends StardustEntity{

	private double[] vertices;
	protected double fade;
	
	public MuzzleFlash(StardustGame game, double x, double y) {
		super(game);
		double size=4;
		fade=1.2;
		setXY(x, y);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		setBoundRadius(size);
		
		vertices=new double[16];
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(size*0.75, size*1.5);
		}
		
		blip();
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		fade-=8*dt;
		if(fade<=0){
			deactivate();
		}
		deactivateIfOutOfBounds();
	}

	public void render(Camera c) {
		int seg=vertices.length;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(fade);
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
		
		//VectorGraphics.renderVectorCircle(0, 0, 4, 8, c);
		
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
