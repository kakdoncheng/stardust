package stardust.entities.boss;

import engine.gfx.Camera;
import engine.sfx.Audio;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;

public class LascannonBeam extends StardustEntity{

	public LascannonBeam(StardustGame game, double x, double y, double t, StardustEntity owner) {
		super(game);
		setOXY(x, y);
		setDirection(t);
		setBoundRadius(3);
		this.owner=owner;
		Audio.addSoundEffect("fire-lascannon", 3);
	}

	private double aa=1.2;
	private double ox;
	private double oy;
	private StardustEntity owner;
	
	public void setOXY(double x, double y){
		this.x=x;
		this.y=y;
		ox=x;
		oy=y;
	}
	
	public void update(double dt) {
		if(aa==1.2){
			// kill things here
			setSpeedVector(t, r);
			for(int i=0;i<640/r;i++){
				updatePosition(1);
				for(StardustEntity e:game.$currentState().$entities()){
					if(e==this||e==owner||!e.isCollidable()||!e.isActive()){
						continue;
					}
					if(distanceTo(e)<this.r+e.$r()){
						e.setKiller(owner);
						e.deactivate();
						game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), (int)e.$r()*1));
					}
				}
			}
			x=ox;
			y=oy;
		}
		aa-=dt*10;
		if(aa<=0){
			active=false;
		}
	}

	public void render(Camera c) {
		double rr=2.5-(2-(aa*1.2));
		double rrl=640;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		// render beam
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		setActualRadarColor(aa);
		GL11.glVertex2d((-rr/2)*c.$zoom(), (-rr/2)*c.$zoom());
		GL11.glVertex2d((-rr/2)*c.$zoom(), rrl*c.$zoom());
		GL11.glVertex2d((rr/2)*c.$zoom(), rrl*c.$zoom());
		GL11.glVertex2d((rr/2)*c.$zoom(), -(rr/2)*c.$zoom());
		GL11.glEnd();
		GL11.glPopMatrix();
		
		// render head
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t)+45, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		setActualRadarColor(aa);
		GL11.glVertex2d((-rr)*c.$zoom(), (-rr)*c.$zoom());
		GL11.glVertex2d((-rr)*c.$zoom(), (rr)*c.$zoom());
		GL11.glVertex2d((rr)*c.$zoom(), (rr)*c.$zoom());
		GL11.glVertex2d((rr)*c.$zoom(), -(rr)*c.$zoom());
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
