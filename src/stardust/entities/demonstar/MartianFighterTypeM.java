package stardust.entities.demonstar;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class MartianFighterTypeM extends MartianShip{
	
	public int points(){
		return 50;
	}
	
	public MartianFighterTypeM(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(4);
		setDirection(0);
		setSpeedVector(t, aF);
	}
	
	private boolean warp=false;
	private double aF=160;
	private double dxy=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
		
		// if past threshold, set direction towards target
		dxy+=aF*dt;
		if(!warp&&dxy>=(game.$displayHeight()/game.$camera().$zoom())*0.8){
			t=directionTo(target);
			setSpeedVector(t, aF);
			warp=true;
			
			// explod
			double ci=2*Math.PI;
			double cis=ci/8;
			for(double i=0;i<ci;i+=cis){
				game.$currentState().addEntity(new MartianProjectile(game, i, this));
			}
		}
		
		// check for collision with target
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-4,-4,-3,-5,-3,-5,3,-5,3,-5,4,-4,-4,2,-2,4,-2,4,-2,5,-2,5,-1,5,-1,5,-1,4,-1,4,1,4,1,4,1,5,1,5,2,5,2,5,2,4,2,4,4,2,-4,-5,-5,-6,-5,-6,-6,-5,-6,-5,-6,3,-6,3,-5,4,-5,4,-4,3,-4,3,-4,-5,4,-5,4,3,4,3,5,4,5,4,6,3,6,3,6,-5,6,-5,5,-6,5,-6,4,-5,-3,-5,-3,3,3,-5,3,3,-6,-5,-8,-7,-8,-7,-12,-7,-12,-7,-12,-6,-12,-6,-6,0,-2,-5,-3,-6,-3,-6,3,-6,3,-6,2,-5,-3,-6,-3,-7,-3,-7,3,-7,3,-7,3,-6,-2,4,-1,0,-1,0,-1,4,1,4,1,0,1,0,2,4,6,0,12,-6,12,-6,12,-7,12,-7,8,-7,8,-7,6,-5,-6,1,-8,1,-6,3,-8,5,-8,1,-9,0,-9,0,-10,1,-10,1,-10,5,-10,5,-9,10,-9,10,-8,5,-8,5,-8,1,6,3,8,5,6,1,8,1,8,1,9,0,9,0,10,1,10,1,10,5,10,5,9,10,9,10,8,5,8,5,8,1,
	};
	private double scale=0.625;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t),0,0,1);
		
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor4d(1,0.5,0,alpha);
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
		//game.$currentState().addEntity(new ElectromagneticPulse(game,x,y));
	}
}
