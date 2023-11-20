package stardust.unused;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.StardustEntity;
import stardust.gfx.VectorGraphics;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class PongBall extends StardustEntity{

	public PongBall(StardustGame game, double t) {
		super(game);
		setXY(0,0);
		setDirection(t);
		setBoundRadius(0);
		setSpeedVector(t, 180);
	}

	public void bounce(double t){
		setDirection(t);
		setSpeedVector(t, $speed()+5);
		//game.$currentState().addEntity(new RadarBlip(game, x, y));
	}
	public void update(double dt) {
		if(y<game.$topScreenEdge()){
			bounce(Math.PI-t);
			Audio.addSoundEffect("pongf4", 1);
		}
		if(y>game.$bottomScreenEdge()){
			bounce(Math.PI-t);
			Audio.addSoundEffect("pongf4", 1);
		}
		updatePosition(dt);
	}
	

	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,1);
		VectorGraphics.renderVectorCircle(x, y, 6/game.$camera().$zoom(), 16, c);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}

}
