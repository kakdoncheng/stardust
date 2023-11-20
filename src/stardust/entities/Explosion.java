package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.Vector;
import engine.gfx.Camera;
import engine.sfx.Audio;

public class Explosion extends StardustEntity{
	
	private double dr;
	//private ArrayList<Spark> sparks;

	public Explosion(StardustGame game, double x, double y, int r) {
		super(game);
		setXY(x,y);
		setBoundRadius(r/2);
		blip();
		
		dr=r*2;
		//if(dr>36){
		//	game.$currentState().addEntity(new ElectromagneticPulse(game,$x(),$y()));
		//}
		
		//sparks=new ArrayList<Spark>();
		for(int i=0;i<dr;i++){
			//sparks.add(new Spark(game, x,y, (int)game.$prng().$double(dr*4, dr*8)));
			game.$currentState().addEntity(new Spark(game, x,y, (int)game.$prng().$double(dr*4, dr*8)));
		}
		
		float v=(float)dr/36;
		if(v>1){
			v=1f;
		}
		Audio.addSoundEffect(game.$prng().$double(0,1)<0.5?"explosion-1":"explosion-2", v);
		//Audio.playSoundEffect("explosion-2", (float)dr/36);
	}

	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		if(r<dr){
			r+=dr*dt;
		}
		if(r>dr){
			deactivate();
		}
		//for(Spark e:sparks){
		//	e.update(dt);
		//}
	}

	public void render(Camera c) {
		int seg=16;
		double ci=2*Math.PI;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1-(r/dr));
		for(double i=0;i<ci;i+=ci/seg){
			double dxyx1=Vector.vectorToDx(i,r), 
					dxyy1=Vector.vectorToDy(i,r), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r);
			GL11.glVertex2d(c.$cx(x+dxyx1), c.$cy(y+dxyy1));
			GL11.glVertex2d(c.$cx(x+dxyx2), c.$cy(y+dxyy2));
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		//for(Spark e:sparks){
		//	e.render(c);
		//}
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
