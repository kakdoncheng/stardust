package stardust.unused;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.gfx.CharGraphics;
import stardust.states.StardustState;
import engine.GameFlags;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class PongState extends StardustState{

	public PongState(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double bgT;
	private double delay;
	private int pscore;
	private int cscore;
	
	// entities
	private double py;
	private double cy;
	private double px;
	private double cx;
	private double pw;
	private PongBall ball;
	
	private void resetPos(){
		px=game.$leftScreenEdge()*0.9;
		if(py<game.$topScreenEdge()*0.9){
			py=game.$topScreenEdge()*0.9;
		}
		if(py>game.$bottomScreenEdge()*0.9){
			py=game.$bottomScreenEdge()*0.9;
		}
		
		cx=game.$rightScreenEdge()*0.9;
		if(cy<game.$topScreenEdge()*0.9){
			cy=game.$topScreenEdge()*0.9;
		}
		if(cy>game.$bottomScreenEdge()*0.9){
			cy=game.$bottomScreenEdge()*0.9;
		}
	}
	
	private void softReset(){
		if(ball!=null){
			ball.deactivate();
		}
		delay=1;
		pw=6;
		ball=new PongBall(game, game.$prng().$double(0, 1)>0.5?game.$prng().$double(Math.PI*1.25, Math.PI*1.75):game.$prng().$double(Math.PI*0.25, Math.PI*0.75));
		ec.addEntity(ball);
		resetPos();
	}
	
	public void reset() {
		GameFlags.setFlag("warp", 1);
		clearBackgroundText();
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgT=0;
		pscore=0;
		cscore=0;
		py=0;
		cy=0;
		softReset();
	}

	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			updateBackgroundText();
			return;
		}
		
		// player input
		if (Keyboard.isKeyDown(Keyboard.KEY_W)){
			py-=120*dt;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)){
			py+=120*dt;
		}
		
		// cpu input
		if(ball.$speedt()<Math.PI*2 && ball.$speedt()>Math.PI){
			if(Math.abs(cy-ball.$y())<120*dt){
				cy=ball.$y();
			}
			if(cy<ball.$y()){
				cy+=120*dt;
			}
			if(cy>ball.$y()){
				cy-=120*dt;
			}
		}
		
		
		// attempt to return ball
		resetPos();
		// player
		if(ball.$x()<px && ball.$x()>px-pw && ball.$y()<py+(5*game.$camera().$zoom()*scale) && ball.$y()>py-(5*game.$camera().$zoom()*scale)){
			//ball.bounce(Vector.directionFromTo(px-pw, py, ball.$x(), ball.$y()));
			ball.bounce(game.$prng().$double(Math.PI*1.25, Math.PI*1.75));
			Audio.addSoundEffect("pongf5", 1);
		}
		// cpu
		if(ball.$x()<cx && ball.$x()>cx-pw && ball.$y()<cy+(5*game.$camera().$zoom()*scale) && ball.$y()>cy-(5*game.$camera().$zoom()*scale)){
			ball.bounce(game.$prng().$double(Math.PI*0.25, Math.PI*0.75));
			Audio.addSoundEffect("pongf5", 1);
		}
				
		ec.update(dt);
		
		// check score
		if(ball.$x()<game.$leftScreenEdge()){
			if(delay>=1){
				cscore++;
				Audio.addSoundEffect("blip", 1);
			}
			delay-=dt;
		}
		if(ball.$x()>game.$rightScreenEdge()){
			if(delay>=1){
				pscore++;
				Audio.addSoundEffect("blip", 1);
			}
			delay-=dt;
		}
		// soft reset
		if(delay<=0){
			softReset();
		}
	}
	
	private double pl[]={
		0,-10,0,10,0,10,-4,10,-4,10,-4,-10,-4,-10,0,-10,
	};
	private double scale=1;

	public void render(Camera c) {
		if(bgT<0.5){
			if(bgT<0.125){
				renderBackgroundText();
			}
			return;
		}
		
		// render net
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,0.5);
		double l=10;
		for(int i=0;i<120;i++){
			if(i%2!=0){
				GL11.glVertex2d(0, (i*l)-game.$displayHeight()/2);
				GL11.glVertex2d(0, ((i+1)*l)-game.$displayHeight()/2);
			}
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render player
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(px), c.$cy(py), 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,1);
		for(int i=0; i<pl.length; i+=4){
			GL11.glVertex2d(pl[i]*c.$zoom()*scale, pl[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(pl[i+2]*c.$zoom()*scale, pl[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// render cpu
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(cx), c.$cy(cy), 0);
		//GL11.glRotated(180, 0, 1, 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,1,1,1);
		for(int i=0; i<pl.length; i+=4){
			GL11.glVertex2d(pl[i]*c.$zoom()*scale, pl[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(pl[i+2]*c.$zoom()*scale, pl[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		ec.render(c);
		
		// score
		CharGraphics.drawTitleString(pscore+"",
				-90,
				(-game.$displayHeight()/2)+18,
				1);
		CharGraphics.drawTitleString(cscore+"",
				100,
				(-game.$displayHeight()/2)+18,
				1);
	}

}
