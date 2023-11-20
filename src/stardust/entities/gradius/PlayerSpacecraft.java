package stardust.entities.gradius;

import engine.GameFlags;
import engine.gfx.Camera;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.AntiMatterMissile;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;

public class PlayerSpacecraft extends StardustEntity{

	private ArrayList<PlayerSpacecraftShadow> shadows;
	private double sa=1;
	private void addShadow(){
		if(shadows!=null){
			sa/=2;
			PlayerSpacecraftShadow e=new PlayerSpacecraftShadow(game, x, y, sa);
			if(shadows.size()<1){
				e.setTarget(this);
			}else{
				e.setTarget(shadows.get(shadows.size()-1));
			}
			game.$currentState().addEntity(e);
			shadows.add(e);
		}
	}
	
	public PlayerSpacecraft(StardustGame game, double x, double y) {
		super(game);
		this.setDirection(0);
		this.setBoundRadius(4);
		this.setXY(x, y);
		rc=new RadarScan(game, 0, 0);
		rc.lockOnEntity(this);
		shadows=new ArrayList<PlayerSpacecraftShadow>();
		addShadow();
		addShadow();
	}
	
	private RadarScan rc;

	private double aF=180;
	private double cooldown=0;
	private double invt=3;
	public void stopInvt(){
		invt=-1;
		for(PlayerSpacecraftShadow e:shadows){
			e.stopInvt();
		}
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// invincible frames
		if(invt>0){
			invt-=dt;
		}
		
		// reset speed & movement input
		dx=0;
		dy=0;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			applyAccelerationVector(Math.PI, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)){
			applyAccelerationVector(Math.PI*0.5, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			applyAccelerationVector(0, aF, 1);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)){
			applyAccelerationVector(Math.PI*1.5, aF, 1);
		}
		
		updatePosition(dt);
		
		// limit movement to screen edge
    	if(x<game.$leftScreenEdge()){
    		x=game.$leftScreenEdge();
    	}
    	if(x>game.$rightScreenEdge()){
    		x=game.$rightScreenEdge();
    	}
    	if(y<game.$topScreenEdge()){
    		y=game.$topScreenEdge();
    	}
    	if(y>game.$bottomScreenEdge()){
    		y=game.$bottomScreenEdge();
    	}
		
    	// weapons
		boolean fired=false;
		cooldown+=dt;
    	if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Mouse.isButtonDown(0)) {
    		if(cooldown>0.125){
    			game.$currentState().addEntity(new AntiGradiusProjectile(game, Math.PI*1.5, this));
    			for(PlayerSpacecraftShadow e:shadows){
    				StardustEntity ie=new AntiGradiusProjectile(game, Math.PI*1.5, this);
    				ie.setXY(e.$x(), e.$y());
    				game.$currentState().addEntity(ie);
    			}
    			fired=true;
    		}
    	}
    	
    	// debug secondary
    	if(GameFlags.is("debuginv")){
    		if(Mouse.isButtonDown(1)){
        		if(cooldown>0.125){
        			AntiMatterMissile e=new AntiMatterMissile(game, Math.PI*1.5, this);
        			//e.biasTowards(MouseHandler.$mx(), MouseHandler.$my());
        			game.$currentState().addEntity(e);
        			fired=true;
        		}
        	}
    	}
    	if(fired){
    		cooldown=0;
    	}
    	
    	// check for collisions
    	rc.update(dt);
	}

	private double l[]={
		-2,-1,-5,-4,-5,-4,-7,-4,-7,-4,-5,-1,-5,-1,-5,1,-5,1,-6,2,-6,2,-4,5,-4,5,1,2,1,2,4,2,4,2,2,1,2,1,-5,1,-2,-1,6,-1,3,1,8,0,8,0,4,-2,4,-2,0,-1,-5,-1,-7,0,-7,0,-5,1,
	};
	private double scale=1.25;

	public void render(Camera c) {
		// render player
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		
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
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		rc.render(c);
	}

	public boolean isCollidable(){
		if(GameFlags.is("debuginv")){
			if(Keyboard.isKeyDown(Keyboard.KEY_F)){
				return true;
			}
			return false;
		}
		return !(invt>0);
	}
	
	public void onDeath() {
		game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
	}

}
