package stardust.entities.terra;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;
import engine.Vector;
import engine.gfx.Camera;

public class Terra extends StardustEntity{
	
	private double[] vertices;
	public Terra(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setDirection(0);
		setBoundRadius(38);
		vertices=new double[16];
		for(int i=0;i<vertices.length;i++){
			vertices[i]=game.$prng().$double(r*0.925, r*1.075);
		}
	}

	public void update(double dt) {
		t+=-Math.PI*0.025*dt;
		updateBlip(dt);
	}

	private double l[]={
		-4,-29,0,-28,0,-28,1,-28,1,-28,1,-27,1,-27,2,-26,2,-26,1,-25,1,-25,5,-25,5,-25,6,-22,6,-22,10,-24,10,-24,14,-18,14,-18,14,-14,14,-14,13,-17,13,-17,12,-16,12,-16,11,-18,11,-18,8,-16,8,-16,7,-14,7,-14,11,-12,11,-12,10,-8,10,-8,11,-7,11,-7,12,-7,12,-7,14,-9,14,-9,20,-7,20,-7,18,-4,18,-4,14,-3,14,-3,14,-6,14,-6,13,-6,13,-6,11,-5,11,-5,10,-8,-4,-29,-8,-27,-8,-27,-4,-28,-4,-28,-1,-26,-1,-26,-3,-26,-3,-26,-5,-24,-5,-24,-6,-24,-6,-24,-6,-26,-6,-26,-9,-26,-9,-26,-10,-25,-10,-25,-13,-20,-13,-20,-17,-18,-17,-18,-12,-16,-12,-16,-13,-15,-13,-15,-12,-14,-12,-14,-9,-15,-9,-15,-9,-8,-9,-8,-8,-9,-8,-9,-3,-10,-3,-10,-3,-12,-3,-12,0,-12,0,-12,1,-15,1,-15,2,-14,2,-14,3,-14,3,-14,3,-13,3,-13,8,-12,8,-12,10,-8,-10,-7,-12,-6,-12,-6,-13,-6,-13,-6,-16,-3,-16,-3,-13,-4,-13,-4,-11,-4,-11,-4,-10,-7,-22,-5,-24,-4,-24,-4,-24,-2,-24,-2,-23,-3,-23,-3,-22,-3,-22,-3,-22,-5,-29,-2,-29,0,-29,0,-28,3,-28,3,-29,4,-29,4,-29,8,-29,8,-26,12,-26,12,-26,15,-26,15,-17,21,-17,21,-12,28,-12,28,-4,28,-4,28,0,30,0,30,0,29,0,29,4,26,4,26,9,27,9,27,3,23,3,23,2,24,2,24,0,23,0,23,-2,23,-2,23,-4,18,-4,18,-5,19,-5,19,-8,18,-8,18,-8,14,-8,14,-6,11,-6,11,-9,9,-9,9,-8,7,-8,7,-9,3,-9,3,-10,7,-10,7,-12,9,-12,9,-13,12,-13,12,-16,13,-16,13,-18,11,-18,11,-19,7,-19,7,-23,4,-23,4,-25,7,-25,7,-27,5,-27,5,-26,3,-26,3,-25,-1,-25,-1,-27,0,-27,0,-29,-2,10,27,12,25,12,25,12,23,12,23,17,22,17,22,20,20,20,20,16,24,16,24,10,27,
	};
	private double scale=1.325;
	public void render(Camera c) {
		
		// render planet bounds
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
		
		// render details
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}
}
