package stardust.entities;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;
import engine.GameFlags;
import engine.Vector;
import engine.entities.Entity;

public abstract class StardustEntity extends Entity{
	
	protected double alpha;
	protected StardustGame game;
	protected Entity target;
	protected Entity killer;
	
	public Entity $target(){
		return target;
	}
	public void setTarget(Entity e){
		target=e;
	}
	public Entity $killer(){
		return killer;
	}
	public void setKiller(Entity e){
		killer=e;
	}
	
	//radar color
	private static int ci=0;
	private static double[] drgb={0,1,0};
	private double[] rgb={0,0,0};
	
	public StardustEntity(StardustGame game) {
		super(game);
		this.game=game;
		alpha=0;
		target=null;
		killer=null;
		if(GameFlags.is("debugshow")){
			blip();
			alpha=1.2;
		}
	}
	
	public int points() {
		return 0;
	}
	
	private boolean isOutOfBounds(){
		if(Vector.distanceFromTo(0, 0, game.$camera().$cx(x)/game.$camera().$zoom(), game.$camera().$cy(y)/game.$camera().$zoom())>StardustGame.BOUNDS){
			return true;
		}
		return false;
	}
	public void deactivateIfOutOfBounds(){
		if(isOutOfBounds()){
			active=false;
		}
	}
	public boolean wraparoundIfOutOfBounds(){
		if(isOutOfBounds()){
			double dtt=Vector.constrainTheta(Vector.directionFromTo(0, 0, game.$camera().$cx(x)/game.$camera().$zoom(), game.$camera().$cy(y)/game.$camera().$zoom())+Math.PI);
			double ddx=Vector.vectorToDx(dtt, StardustGame.BOUNDS-r)+game.$camera().$dx();
			double ddy=Vector.vectorToDy(dtt, StardustGame.BOUNDS-r)+game.$camera().$dy();
			setXY(ddx, ddy);
			return true;
		}
		return false;
	}
	public void deactivateIfOutOfScreenBounds(){
		double dx=game.$displayWidth();
		double dy=game.$displayHeight();
		if(game.$camera().$cx(x)>dx/2){
			active=false;
		}else if(game.$camera().$cx(x)<-dx/2){
			active=false;
		}
		if(game.$camera().$cy(y)>dy/2){
			active=false;
		}else if(game.$camera().$cy(y)<-dy/2){
			active=false;
		}
	}
	public boolean wraparoundIfOutOfScreenBounds(){
		boolean warp=false;
		double dx=game.$displayWidth();
		double dy=game.$displayHeight();
		if(game.$camera().$cx(x)>dx/2){
			x-=dx/game.$camera().$zoom();
			warp=true;
		}else if(game.$camera().$cx(x)<-dx/2){
			x+=dx/game.$camera().$zoom();
			warp=true;
		}
		if(game.$camera().$cy(y)>dy/2){
			y-=dy/game.$camera().$zoom();
			warp=true;
		}else if(game.$camera().$cy(y)<-dy/2){
			y+=dy/game.$camera().$zoom();
			warp=true;
		}
		return warp;
	}
	
	
	//radar color methods
	public void blip(){
		rgb[0]=drgb[0];
		rgb[1]=drgb[1];
		rgb[2]=drgb[2];
		alpha=1.2;
	}
	public void updateBlip(double dt){
		if(alpha>0){
			alpha-=1.85*dt;
		}
		if(GameFlags.is("debugshow")){
			alpha=1.2;
		}
	}
	public static void shiftColor(double dt){
		//set indexes
		int cil=ci-1;
		if(cil==-1){
			cil=2;
		}
		int cir=ci+1;
		if(cir==3){
			cir=0;
		}
		
		double dc=dt;//*0.25;
		if(drgb[cil]>0){
			drgb[cil]-=dc;
			if(drgb[cil]<0){
				drgb[cil]=0;
			}
		}else if(drgb[cir]<1){
			drgb[cir]+=dc;
			if(drgb[cir]>1){
				drgb[cir]=1;
			}
		}else{
			ci=(ci+1)%3;
		}
	}
	public static void setActualRadarColor(double alpha){
		GL11.glColor4d(drgb[0],drgb[1],drgb[2],alpha);
	}
	public void setRadarColor(double alphaMod){
		GL11.glColor4d(rgb[0],rgb[1],rgb[2],alpha*alphaMod);
	}
	
}
