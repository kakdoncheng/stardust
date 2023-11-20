package stardust.entities.invaders;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public abstract class Alien extends StardustEntity{
	
	public int points(){
		return 5;
	}
	
	public Alien(StardustGame game, double x, double y, double[] l1, double[] l2) {
		super(game);
		setXY(x,y);
		setBoundRadius(5);
		setDirection(game.$prng().$double(0, 8)*(Math.PI/4));
		resetCooldown();
		this.l1=l1;
		this.l2=l2;
		//blip();
	}
	
	// formation update methods
	private boolean angry=false;
	private boolean altf=false;
	private int dpx=5;
	private int ddpx=dpx*3;
	
	public void anger(){
		angry=true;
	}
	public void nudgeLeft(){
		x-=dpx;
		altf=!altf;
	}
	public void nudgeRight(){
		x+=dpx;
		altf=!altf;
	}
	public void nudgeDown(){
		y+=ddpx;
		altf=!altf;
	}
	
	
	// normal update methods
	private double cooldown;
	public boolean isReadyToFire(){
		return cooldown<=0;
	}
	public void resetCooldown(){
		cooldown=game.$prng().$double(2,8);
	}
	public void update(double dt) {
		//updateBlip(dt);
		cooldown-=dt;
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l1[];
	private double l2[];
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		//no rotation, bogey stays upright
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		if(angry){
			GL11.glColor4d(1,0,0,1);
		}
		
		double[] l=altf?l2:l1;
		
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
