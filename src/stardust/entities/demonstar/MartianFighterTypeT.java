package stardust.entities.demonstar;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import engine.gfx.Camera;

public class MartianFighterTypeT extends MartianShip{
	
	public int points(){
		return 30;
	}
	
	public MartianFighterTypeT(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(12);
		setDirection(0);
		setSpeedVector(t, aF);
	}
	
	private double aF=75;
	private double dxy=0;
	private double dtt=1;
	private double cooldown=2;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// weapons
		cooldown+=dt;
		if(cooldown>3){
			for(int i=0;i<3;i++){
				StardustEntity e=new MartianProjectile(game, -0.075+(i*0.075), this);
				e.setXY(x-2-(i*2), y+6);
				game.$currentState().addEntity(e);
			}
			cooldown=0;
		}
		
		updateBlip(dt);
		updatePosition(dt);
		deactivateIfOutOfBounds();
		
		// if past threshold, move from side to side
		dxy+=aF*dt;
		if(dxy>=(game.$displayHeight()/game.$camera().$zoom())*0.2){
			dtt+=dt;
			if(dtt%2>1){
				setSpeedVector(Math.PI*0.5, aF*0.5);
			}else{
				setSpeedVector(Math.PI*1.5, aF*0.5);
			}
			
		}
	}

	//x1, y1, x2, y2 line render
	private double l[]={
		-6,-10,-6,-4,-6,-4,-4,-2,-4,-2,-2,-4,-2,-4,-2,-10,-2,-10,-6,-10,-5,-10,-6,-11,-6,-11,-2,-11,-2,-11,-3,-10,2,-10,2,-4,2,-4,4,-2,4,-2,6,-4,6,-4,6,-10,6,-10,2,-10,3,-10,2,-11,2,-11,6,-11,6,-11,5,-10,-2,-8,2,-8,-6,-6,-8,-4,-8,-4,-8,4,-8,4,-6,6,-6,6,-6,4,-6,4,0,4,0,4,2,6,2,6,2,8,2,8,8,4,8,4,8,-4,8,-4,6,-6,5,6,5,8,5,8,6,9,6,9,7,9,7,9,8,8,8,8,7,7,7,7,6,7,6,7,5,8,8,8,8,4,6,7,6,9,7,7,7,9,-6,4,-5,5,-5,5,1,5,-5,5,-5,7,-5,7,-4,7,-4,7,-4,5,-3,5,-3,7,-3,7,-2,7,-2,7,-2,5,-1,5,-1,7,-1,7,0,7,0,7,0,5,0,-3,-2,-2,-2,-2,-3,0,-3,0,-2,2,-2,2,0,3,0,3,2,2,2,2,3,0,3,0,2,-2,2,-2,0,-3,-8,-3,-6,-2,-6,-2,-6,2,-6,2,-8,3,8,-3,6,-2,6,-2,6,2,6,2,8,3,
	};
	private double scale=1.25;
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
