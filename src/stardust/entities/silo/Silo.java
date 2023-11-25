package stardust.entities.silo;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import engine.Vector;
import engine.gfx.Camera;
import engine.input.MouseHandler;
import engine.sfx.Audio;

public class Silo extends StardustEntity{

	public Silo(StardustGame game, double x, double y) {
		super(game);
		this.setXY(x,y);
		this.setDirection(0);
		this.setBoundRadius(2);
		blip();
	}
	
	private boolean intact=true;
	private int ammo=10;
	private double dwt=0;
	private double invt=0;
	
	public boolean isEmpty(){
		return ammo<1;
	}
	public void fireMissile(){
		t=Vector.directionFromTo(x, y, MouseHandler.$mx(), MouseHandler.$my());
		if(ammo>0){
			AnnihilatingMissile a=new AnnihilatingMissile(game, MouseHandler.$mx(), MouseHandler.$my(), t, this);
			a.showTXY();
			game.$currentState().addEntity(a);
			ammo--;
		}
	}
	
	private boolean warning=true;
	private int blips=0;
	private double dbt=1;

	public void update(double dt) {
		if(invt>0){
			invt-=dt;
		}
		dwt+=dt;
		
		if(ammo<4&&warning){
			warning=false;
			blips=3;
		}
		if(blips>0){
			dbt+=dt;
			if(dbt>=0.2){
				Audio.addSoundEffect("blip", 1);
				dbt=0;
				blips--;
			}
		}
	}
	
	private double l[]={
		-1,0,-2,-1,-2,-1,2,-1,2,-1,1,0,-4,0,4,0,-4,0,-4,20,-4,20,4,20,4,20,4,0,-4,20,-3,28,-3,28,3,28,4,20,3,28,-3,28,-3,46,-3,46,-2,47,-2,47,0,48,0,48,2,47,2,47,3,46,3,46,3,28,-2,47,2,47,-4,19,4,19,-4,1,4,1,-3,0,-4,-1,-4,-1,-2,-1,3,0,4,-1,4,-1,2,-1,2,-1,2,0,-2,-1,-2,0,-4,19,-6,2,-6,2,-6,1,-6,1,-4,1,4,1,6,1,6,1,6,2,6,2,4,19,-3,29,3,29,-3,46,3,46,0,19,-1,1,0,19,1,1,
	};
	private double scale=0.125;
	private void renderAmmo(Camera c, double rx, double ry){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(rx), c.$cy(ry), 0);
		GL11.glRotatef(180, 0, 0, 1);
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
	}

	public void render(Camera c) {
		//this.renderCollisionBounds(c, 8);
		if(dwt%1>0.5){
			if(isEmpty()){
				CharGraphics.drawRedHeaderString("OUT", (int)c.$cx(x)-27, (int)c.$cy(y+20), 1f);
			}else if(ammo<4){
				CharGraphics.drawRedHeaderString("LOW", (int)c.$cx(x)-27, (int)c.$cy(y+20), 1f);
			}
		}
		
		int r=ammo;
		for(int ii=0;ii<4;ii++){
			for(int i=0;i<1+ii;i++){
				if(r<1){
					break;
				}
				renderAmmo(c, x-(6*ii)+(i*12), y+4+(5*ii));
				r--;
			}
		}
	}
	
	public boolean isCollidable(){
		return intact && invt<=0;
	}

	public void onDeath() {
		if(ammo>0){
			game.$currentState().addEntity(new AnnihilatingExplosionCluster(game,x,y+4,this,ammo));
		}
		ammo=0;
		intact=false;
		active=true;
	}

}
