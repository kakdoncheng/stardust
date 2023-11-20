package stardust.unused;

import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;

public class PlayerTank extends StardustEntity{

	public PlayerTank(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(12);
		this.setDirection(Math.PI);
		this.setXY(x, y);
	}

	private double lx, ly;
	private double bt=0;
	private double bdt=0;
	private double radart=0;
	
	private double aimt=0;
	private double cooldown=0;
	private double invt=3;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// aim with mouse
		aimt=Vector.directionFromTo(x,y,MouseHandler.$mx(),MouseHandler.$my());
				
		// movement
		// mouse towards mouse, then snap to place
		lx=x;
		ly=y;
		setSpeedVector(aimt,Vector.distanceFromTo(x,y,MouseHandler.$mx(),MouseHandler.$my()));
		updatePosition(dt);
		t=Vector.directionFromTo(lx,ly,x,y);
		this.setXY(lx+Vector.vectorToDx(t, Vector.distanceFromTo(lx,ly,x,y)), ly);
		
		bdt+=dt;
		if(bdt>0.125){
			bt=game.$prng().$double(-0.1, 0.1);
			bdt=0;
		}
		
		radart+=dt;
		if(radart>=1){
			//game.$currentState().addEntity(new RadarBlip(game,x,y));
			radart-=1;
		}
		
		// weapons
		cooldown+=dt;
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		if(cooldown>=0.175){
    			double xx=x+Vector.vectorToDx(aimt, 26);
    			double yy=y-(4*scale2)+Vector.vectorToDy(aimt, 26);
    			StardustEntity e=new FlakProjectile(game, aimt-0.025, this);
    			e.setXY(xx, yy);
    			game.$currentState().addEntity(e);
    			e=new FlakProjectile(game, aimt+0.025, this);
    			e.setXY(xx, yy);
    			game.$currentState().addEntity(e);
    			game.$currentState().addEntity(new MuzzleFlash(game,xx,yy));
    			cooldown=0;
    		}
    	}
    	
		
	}

	// 4xy point render
	private double l[]={
		//-5,-1,-4,0,-4,0,4,0,4,0,5,-1,6,-1,-6,-1,-6,-1,-6,-2,-6,-2,-5,-4,-5,-4,5,-4,5,-4,6,-2,6,-2,6,-1,-4,-4,-2,-5,-2,-5,2,-5,2,-5,4,-4,
		//0,1,-2,2,-2,2,-3,4,-3,4,-2,6,-2,6,0,7,0,7,2,6,2,6,3,4,3,4,2,2,2,2,0,1,6,1,4,2,4,2,3,4,3,4,4,6,4,6,6,7,6,7,8,6,8,6,9,4,9,4,8,2,8,2,6,1,-3,4,-4,2,-4,2,-6,1,-6,1,-8,2,-8,2,-9,4,-9,4,-8,6,-8,6,-6,7,-6,7,-4,6,-4,6,-3,4,-6,7,6,7,-9,2,-6,0,-6,0,6,0,6,0,9,2,9,2,10,1,10,1,7,-1,7,-1,-7,-1,-7,-1,-10,1,-10,1,-9,2,-6,1,6,1,-9,4,-10,1,9,4,10,1,-6,-1,-4,-4,-4,-4,4,-4,4,-4,6,-1,//4,-4,7,-2,7,-2,7,-1,-7,-1,-7,-2,-7,-2,-4,-4,
		-9,2,-10,1,-10,1,-7,-1,-7,-1,7,-1,7,-1,10,1,10,1,9,2,9,2,-9,2,-8,2,-9,4,-9,4,-8,6,-8,6,-6,7,-6,7,-4,6,-4,6,-2,2,2,2,4,6,4,2,2,6,2,6,0,7,0,7,-2,6,-2,6,-4,2,8,2,9,4,9,4,8,6,8,6,6,7,6,7,4,6,-6,7,6,7,9,4,10,1,-9,4,-10,1,-6,-1,-5,-2,-5,-2,5,-2,5,-2,6,-1,
	};
	private double l2[]={
		//-2,-2,-1,-8,-1,-8,-1,-2,-1,-2,1,-2,1,-2,1,-8,1,-8,2,-2,2,-2,3,-1,3,-1,3,1,3,1,0,2,0,2,-3,1,-3,1,-3,-1,-3,-1,-2,-2,-1,-2,0,-3,0,-3,1,-2,
		0,-8,-4,-7,-4,-7,-7,-4,-7,-4,-8,0,-8,0,-7,4,-7,4,-4,7,-4,7,0,8,0,8,4,7,4,7,7,4,7,4,8,0,8,0,7,-4,7,-4,4,-7,4,-7,0,-8,0,-1,-1,0,-1,0,0,1,0,1,1,0,1,0,0,-1,-4,-7,4,-7,4,-7,3,-15,0,-7,1,-15,0,-7,-1,-15,-4,-7,-3,-15,-3,-15,-3,-23,-3,-23,-1,-23,-1,-23,-1,-15,1,-15,1,-23,1,-23,3,-23,3,-23,3,-15,
	};

	private double scale=1.5;
	private double scale2=0.75;

	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		// render tank body
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y-3), 0);
		GL11.glRotated(Math.toDegrees(bt), 0, 0, 1);
		//GL11.glRotated(180, 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?1:0);
		}else{
			GL11.glColor4d(1,1,1,1);
		}
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		// render turret
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y-6), 0);
		GL11.glRotated(Math.toDegrees(aimt), 0, 0, 1);
		GL11.glRotated(180, 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?1:0);
		}else{
			GL11.glColor4d(1,1,1,1);
		}
		for(int i=0; i<l2.length; i+=4){
			GL11.glVertex2d(l2[i]*c.$zoom()*scale2, l2[i+1]*c.$zoom()*scale2);
			GL11.glVertex2d(l2[i+2]*c.$zoom()*scale2, l2[i+3]*c.$zoom()*scale2);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public boolean isCollidable(){
		if(GameFlags.is("debuginv")){
			return false;
		}
		return !(invt>0);
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
