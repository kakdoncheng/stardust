package stardust.entities.sinistar;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import engine.gfx.Camera;
import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.gfx.VectorGraphics;

public class Sigil extends SinisterEntity{
	
	public int points() {
		return 0;
	}
	
	private ArrayList<SacrificialServitor> servitors;

	public Sigil(StardustGame game, double x, double y) {
		super(game);
		this.setBoundRadius(0);
		this.setXY(x, y);
		servitors=new ArrayList<SacrificialServitor>();
		for(int i=0;i<pxy.length;i+=2){
			SacrificialServitor e=new SacrificialServitor(game, $x()+pxy[i]*scale, $y()+pxy[i+1]*scale);
			game.$currentState().addEntity(e);
			servitors.add(e);
		}
		
	}
	
	private boolean activated=false;

	public void update(double dt) {
		if(!activated){
			boolean set=true;
			for(SacrificialServitor se:servitors){
				if(!se.isSet()){
					set=false;
					break;
				}
			}
			if(set){
				ralpha=1.0;
				activated=true;
				Audio.playSoundEffect("scream-0", 4, 1);
			}
		}
		blip();
		if(ralpha>0&&ralpha>0.3){
			ralpha-=dt;
			if(ralpha<0.3){
				ralpha=0.3;
			}
		}
	}

	private double ralpha=0;
	private double l[]={
		-10,0,-12,-2,-12,-2,-14,0,-14,0,-12,2,-12,2,-10,0,10,0,12,-2,12,-2,14,0,14,0,12,2,12,2,10,0,0,-10,-2,-12,-2,-12,0,-14,0,-14,2,-12,2,-12,0,-10,0,10,-2,12,-2,12,0,14,0,14,2,12,2,12,0,10,-10,-8,-8,-10,-10,-8,-8,-6,-8,-6,-6,-8,-6,-8,-8,-10,-6,8,-8,6,-8,6,-10,8,-10,8,-8,10,-8,10,-6,8,6,-8,8,-10,8,-10,10,-8,10,-8,8,-6,8,-6,6,-8,8,6,6,8,6,8,8,10,8,10,10,8,10,8,8,6,-6,8,6,8,8,6,8,-6,6,-8,-6,-8,-8,-6,-8,6,-11,-1,-1,-11,1,-11,11,-1,11,1,1,11,-1,11,-11,1,
	};
	private double pxy[]={
		0,-12,8,-8,12,0,8,8,0,12,-8,8,-12,0,-8,-8,
	};
	private double scale=6;
	public void render(Camera c) {
		// render
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotated(Math.toDegrees(t), 0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(ralpha);
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		VectorGraphics.beginVectorRender();
		VectorGraphics.renderVectorCircle(x, y, 50, 64, c);
		VectorGraphics.renderVectorCircle(x, y, 55, 64, c);
		VectorGraphics.renderVectorCircle(x, y, 84, 64, c);
		VectorGraphics.renderVectorCircle(x, y, 89, 64, c);
		VectorGraphics.endVectorRender();
		
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}

}
