package stardust.entities.invaders;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.StardustEntity;

public class BlastShieldBit extends StardustEntity {
	
	// collision width
	public static double HALF_WIDTH=0.625;
	
	// track location in shield
	private int ix;
	private int iy;
	
	// track exposed edges
	private boolean top;
	private boolean left;
	private boolean bottom;
	private boolean right;

	public BlastShieldBit(StardustGame game, int ix, int iy, double x, double y){
		super(game);
		setXY(x, y);
		setBoundRadius(HALF_WIDTH);
		alpha=1;
		top=false;
		left=false;
		bottom=false;
		right=false;
		this.ix=ix;
		this.iy=iy;
	}
	
	// collision methods
	// on collision with alien projectile,
	// destroy projectile and self
	public boolean checkForCollisionWith(AlienProjectile e, BlastShieldBit[][] bits) {
		if(!top) {
			return false;
		}
		if(this.intersects(e)) {
			e.deactivate();
			game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), 16));
			this.deactivate();
			// chance of destroying neighboring bits 
			// based on distance
			destroyAdjacentBits(bits);
			return true;
		}
		return false;
	}
	// on collision with player projectile,
	// destroy projectile
	public boolean checkForCollisionWith(ShellProjectile e) {
		if(!bottom) {
			return false;
		}
		if(this.intersects(e)) {
			e.deactivate();
			game.$currentState().addEntity(new Explosion(game, e.$x(), e.$y(), 8));
			return true;
		}
		return false;
	}
	// on collision with alien,
	// destroy self
	public boolean checkForCollisionWith(Alien e) {
		if(this.intersects(e)) {
			this.deactivate();
			return true;
		}
		return false;
	}
	
	// helper methods for shield destruction
	private void destroyAdjacentBits(BlastShieldBit[][] bits) {
		for(int iiy=0;iiy<bits.length;iiy++) {
			for(int iix=0;iix<bits[0].length;iix++) {
				if(bits[iiy][iix]==null || !bits[iiy][iix].isActive()) {
					continue;
				}
				double threshold=Vector.distanceFromTo(ix, iy, iix, iiy)*0.4;
				if(threshold<1) {
					bits[iiy][iix].deactivate();
				} else {
					double roll=game.$prng().$double(0, 1);
					threshold=1/(threshold*threshold*threshold);
					if(threshold>=0.125 && roll<threshold) {
						bits[iiy][iix].deactivate();
					}
				}
				
				
			}
		}	
	}
	
	// check if bit is edge bit
	public void checkEdges(BlastShieldBit[][] bits){
		// top
		int ixt=ix;
		int iyt=iy-1;
		if(iyt<0 || bits[iyt][ixt]==null || !bits[iyt][ixt].isActive()) {
			top=true;
		}
		
		// left
		ixt=ix-1;
		iyt=iy;
		if(ixt<0 || bits[iyt][ixt]==null || !bits[iyt][ixt].isActive()) {
			left=true;
		}
		
		// right
		ixt=ix+1;
		iyt=iy;
		if(ixt>bits[iyt].length-1 || bits[iyt][ixt]==null || !bits[iyt][ixt].isActive()) {
			right=true;
		}
		
		// bottom
		ixt=ix;
		iyt=iy+1;
		if(iyt>bits.length-1 || bits[iyt][ixt]==null || !bits[iyt][ixt].isActive()) {
			bottom=true;
		}
	}
	
	
	public void update(double dt) {
		return;
	}

	public void render(Camera c){
		// 4 lines for a square
		// check neighboring bit slots,
		// if side is adjacent to active shield bit, dont render
		double rr=r*c.$zoom();
		double cx=c.$cx(x);
		double cy=c.$cy(y);
		double lx=cx-rr;
		double rx=cx+rr;
		double ty=cy-rr;
		double by=cy+rr;

		// begin render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		setRadarColor(1);
		
		GL11.glBegin(GL11.GL_LINES);
		
		if(top) {
			GL11.glVertex2d(lx, ty);
			GL11.glVertex2d(rx, ty);
		}
		if(left) {
			GL11.glVertex2d(lx, ty);
			GL11.glVertex2d(lx, by);
		}
		if(right) {
			GL11.glVertex2d(rx, ty);
			GL11.glVertex2d(rx, by);
		}
		if(bottom) {
			GL11.glVertex2d(rx, by);
			GL11.glVertex2d(lx, by);
		}

		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public boolean isCollidable() {
		return false;
	}

	public void onDeath() {
		return;
	}
	
	
}
