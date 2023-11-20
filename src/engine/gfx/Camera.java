package engine.gfx;

import engine.Vector;
import engine.entities.Entity;

public class Camera {
	private double dx, dy;
	private double zoom, minz;
	
	public Camera(){
		dx=0;
		dy=0;
		zoom=1;
		minz=0.125;
	}
	
	public void hardCenterOnPoint(double x, double y){
		dx=x;
		dy=y;
	}
	
	public void centerOnEntity(Entity e, double dt){
		if((int)dx!=(int)e.$x()||(int)dy!=(int)e.$y()){
			double dist=Vector.distanceFromTo(dx, dy, e.$x(), e.$y());
			double t=Vector.directionFromTo(dx, dy, e.$x(), e.$y());
			double speed=dist*5;
			if(speed<30){
				speed=30;
			}
			dx+=Vector.vectorToDx(t, speed*dt);
			dy+=Vector.vectorToDy(t, speed*dt);
		}
	}
	
	public void dZoom(double amt){
		zoom+=amt;
		if(zoom<minz){
			zoom=minz;
		}
	}
	public void setZoom(double amt){
		zoom=amt;
		if(zoom<minz){
			zoom=minz;
		}
	}
	
	public void dxy(double x, double y){
		dx+=x;
		dy+=y;
	}
	
	public double $dx(){
		return dx;
	}
	public double $dy(){
		return dy;
	}
	
	public int $cmx(double x){
		return (int)(x/zoom+dx);
	}
	public int $cmy(double y){
		return (int)(y/zoom+dy);
	}
	public int $cx(double x){
		return (int)((x-dx)*zoom);
	}
	public int $cy(double y){
		return (int)((y-dy)*zoom);
	}
	public double $zoom(){
		return zoom;
	}
	
	public String toString(){
		return super.toString()+" dx:"+String.format("%.1f",dx)+" dy:"+String.format("%.1f",dy)+" z:"+String.format("%.1f",zoom);
	}
}
