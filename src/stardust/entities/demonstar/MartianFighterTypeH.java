package stardust.entities.demonstar;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class MartianFighterTypeH extends MartianShip{
	
	public int points(){
		return 30;
	}
	
	public MartianFighterTypeH(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
		setSpeedVector(t, aF);
	}
	
	private boolean swerve=true;
	private double aF=90;
	
	private int ammo=1;
	private double reload=0;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// homing movement
		// stop if near target
		t=directionTo(target);
		if(distanceTo(target)<aF*1.5){
			swerve=false;
		}
		if(swerve){
			applyAccelerationVector(t, aF, dt);
		}else{
			setSpeedVector(0, aF*0.5);
		}
		
		// weapons
		if(ammo>0){
			cooldown+=dt;
			if(cooldown>0.125){
				game.$currentState().addEntity(new MartianProjectile(game, t, this));
				cooldown=0;
				ammo--;
			}
		}else{
			reload+=dt;
			if(reload>2){
				reload=0;
				ammo=1;
			}
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-2,-5,0,-4,0,-4,2,-5,-2,6,-1,7,-1,7,1,7,1,7,2,6,-2,-5,-3,-1,-3,-1,-5,-1,-5,4,-3,4,-3,4,-2,5,-2,5,-2,6,2,6,2,5,2,5,3,4,3,4,5,4,2,-5,3,-1,3,-1,5,-1,-5,-2,-8,-2,-8,-2,-8,5,-8,5,-5,5,-5,5,-5,-2,5,-2,5,5,5,5,8,5,8,5,8,-2,8,-2,5,-2,-3,-6,-3,-5,-3,-5,3,-5,3,-5,3,-6,3,-6,-3,-6,-2,-6,-3,-7,-3,-7,3,-7,3,-7,2,-6,-2,-4,2,-4,2,-4,2,4,2,4,-2,4,-2,4,-2,-4,-2,-4,-12,-8,-12,-8,-2,0,2,-4,12,-8,12,-8,2,0,-2,0,-7,10,-7,10,-2,4,2,0,7,10,7,10,2,4,
	};

	private double scale=0.75;
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
