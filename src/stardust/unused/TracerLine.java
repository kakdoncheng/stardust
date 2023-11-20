package stardust.unused;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class TracerLine extends StardustEntity{

	public TracerLine(StardustGame game, double x1, double y1, double x2, double y2, double alpha) {
		super(game);
		setXY(x1, y1);
		setBoundRadius(1);
		this.x2=x2;
		this.y2=y2;
		this.alpha=alpha;
	}

	private double x2, y2;
	private double alpha;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
	}

	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(alpha);
		GL11.glVertex2d(c.$cx(x), c.$cy(y));
		GL11.glVertex2d(c.$cx(x2), c.$cy(y2));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}

}
