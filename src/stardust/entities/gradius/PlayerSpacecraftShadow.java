package stardust.entities.gradius;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class PlayerSpacecraftShadow extends StardustEntity{

	public PlayerSpacecraftShadow(StardustGame game, double x, double y, double alpha) {
		super(game);
		this.setDirection(0);
		this.setBoundRadius(0);
		this.setXY(x, y);
		aa=alpha;
	}

	private double invt=3;
	public void stopInvt(){
		invt=-1;
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// follow target
		if(target==null || !target.isActive()){
			deactivate();
		}
		// shadow visual glitch fix
		if(distanceTo(target)>2){
			setSpeedVector(directionTo(target), distanceTo(target)*10);
			updatePosition(dt);
		}else{
			dx=0;
			dy=0;
			x=target.$x();
			y=target.$y();
		}
	}

	private double l[]={
		-2,-1,-5,-4,-5,-4,-7,-4,-7,-4,-5,-1,-5,-1,-5,1,-5,1,-6,2,-6,2,-4,5,-4,5,1,2,1,2,4,2,4,2,2,1,2,1,-5,1,-2,-1,6,-1,3,1,8,0,8,0,4,-2,4,-2,0,-1,-5,-1,-7,0,-7,0,-5,1,
	};
	private double scale=1.25;
	private double aa=1;
	public void render(Camera c) {
		//render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		
		GL11.glBegin(GL11.GL_LINES);
		if(invt>0){
			GL11.glColor4d(1,1,1,invt%0.5>0.25?aa:0);
		}else{
			GL11.glColor4d(1,1,1,aa);
		}
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
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
	
	protected void fireProjectile(){
		game.$currentState().addEntity(new AntiGradiusProjectile(game, Math.PI*1.5, this));
	}

}
