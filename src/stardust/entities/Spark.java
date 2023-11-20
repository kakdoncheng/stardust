package stardust.entities;

import engine.Vector;
import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class Spark extends StardustEntity{

	private double fade;
	private double maxSpeed;
	private double speed;
	public Spark(StardustGame game, double x, double y, int maxSpeed) {
		super(game);
		setXY(x, y);
		this.maxSpeed=maxSpeed;
		speed=game.$prng().$int(maxSpeed/2, maxSpeed);
		setDirection(game.$prng().$double(0, 2*Math.PI));
		fade=1;
		blip();
	}

	public void update(double dt) {
		updateBlip(dt);
		speed-=maxSpeed*dt;
		if(speed<0){
			speed=0;
			fade-=dt;
			if(fade<0){
				active=false;
			}
		}
		x+=Vector.vectorToDx(t, speed*dt);
		y+=Vector.vectorToDy(t, speed*dt);
		
	}

	public void render(Camera c) {
		double len=4*c.$zoom()*fade*speed/60;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		if(len<=4){
			GL11.glRotated(Math.toDegrees(t+(Math.PI*0.25)), 0, 0, 1);
			GL11.glBegin(GL11.GL_QUADS);
			setRadarColor(fade);
			//GL11.glVertex2d(-len/2, -len/2);
			//GL11.glVertex2d(-len/4, len/4);
			//GL11.glVertex2d(len/2, len/2);
			//GL11.glVertex2d(len/4, -len/4);
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(len*0.75, len*0.25);
			GL11.glVertex2d(len, len);
			GL11.glVertex2d(len*0.25, len*0.75);
		}else{
			GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
			GL11.glBegin(GL11.GL_LINES);
			setRadarColor(fade);
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(0, len);
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
