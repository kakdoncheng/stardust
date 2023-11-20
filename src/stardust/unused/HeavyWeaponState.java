package stardust.unused;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.RadarScan;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import stardust.gfx.VectorGraphics;
import stardust.states.StardustState;
import engine.GameFlags;
import engine.Vector;
import engine.gfx.Camera;

public class HeavyWeaponState extends StardustState{

	public HeavyWeaponState(StardustGame game) {
		super(game);
		reset();
	}
	
	private RadarScan rc;
	private StardustEntity player;
	
	private double bgT;
	private double lx, ly;
	private double ly2;
	private ArrayList<StardustEntity> ground;

	public void reset() {
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		game.$camera().hardCenterOnPoint(0, 0);
		bgT=0;
		
		rc=new RadarScan(game, 0, 0);
		player=new PlayerTank(game, 0, 155);
		rc.lockOnEntity(player);
		ec.addEntity(player);
		
		lx=-game.$displayWidth()/2;
		ly=160;
		ly2=80;
		ground=new ArrayList<StardustEntity>();
		
	}
	
	private void generateGround(){
		double nx=lx+game.$prng().$double(2, 8);
		double ny=160+game.$prng().$double(-1, 1);
		double ny2=160+game.$prng().$double(-6, 0);
		//System.out.printf("%.2f %.2f %.2f %.2f\n",lx,ly,nx,ny);
		StardustEntity e=new TracerLine(game,lx,ly,nx,ny,1);
		ground.add(e);
		ec.addEntity(e);
		e=new TracerLine(game,lx,ly2,nx,ny2,0.5);
		ground.add(e);
		ec.addEntity(e);
		lx=nx;
		ly=ny;
		ly2=ny2;
	}
	public void update(double dt) {
		if(bgT<0.5){
			bgT+=dt;
			return;
		}
		
		// move to right
		game.$camera().dxy(30*dt, 0);
		
		// spawn enemies
		if(game.$prng().$double(0, 1)<0.025){
			double t=game.$prng().$double(Math.PI*0.625, Math.PI*1.325);
			StardustEntity e=new Fighter(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
			e.setTarget(player);
			ec.addEntity(e);
		}
		if(game.$prng().$double(0, 1)<0.0125){
			double t=game.$prng().$double(Math.PI*0.625, Math.PI*1.325);
			StardustEntity e=new Bomber(game,player.$x()+Vector.vectorToDx(t,StardustGame.BOUNDS/2),player.$y()+Vector.vectorToDy(t,StardustGame.BOUNDS/2));
			e.setTarget(player);
			ec.addEntity(e);
		}
		
		ec.update(dt);
		rc.update(dt);
		
		// update ground
		while(ground.size()<1 || ground.get(ground.size()-1).$x()<game.$camera().$dx()+game.$displayWidth()/2){
			generateGround();
		}
		while(ground.get(0).$x()<-game.$camera().$dx()-20-game.$displayWidth()/2){
			ground.get(0).deactivate();
			ground.remove(0);
		}
		for(StardustEntity e:ec.$entities()){
			if(e instanceof Bomb){
				if(e.$y()>160){
					e.deactivate();
					ec.addEntity(new Explosion(game, e.$x(), e.$y(), (int)(e.$r()*game.$prng().$double(1, 3))));
				}
			}
		}
	}

	public void render(Camera c) {
		if(bgT<0.5){
			return;
		}
		
		// blackout floor
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(0), c.$cy(0), 0);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4d(0, 0, 0, 1);
		GL11.glVertex2d(-340*c.$zoom(), 160*c.$zoom());
		GL11.glVertex2d(340*c.$zoom(), 160*c.$zoom());
		GL11.glVertex2d(340*c.$zoom(), 280*c.$zoom());
		GL11.glVertex2d(-340*c.$zoom(), 280*c.$zoom());
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		ec.render(c);
		rc.render(c);
		
		VectorGraphics.renderCrosshairCursor();
		
		if(GameFlags.is("score")){
			CharGraphics.drawHeaderString(game.$obfScore(),
					(-game.$displayWidth()/2)+24,
					(-game.$displayHeight()/2)+24,
					1);
		}
	}

}
