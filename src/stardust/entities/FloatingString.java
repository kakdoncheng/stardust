package stardust.entities;

import engine.gfx.Camera;
import stardust.StardustGame;
import stardust.gfx.CharGraphics;

public class FloatingString extends StardustEntity{
	
	public FloatingString(StardustGame game, String label, double x, double y) {
		super(game);
		this.setXY(x, y);
		this.setSpeedVector(Math.PI, 60);
		this.setBoundRadius(0);
		this.setDirection(0);
		this.setTarget(null);
		this.label=label;
	}
	
	private String label;
	private double timer=1;
	private double scale=1;
	public void update(double dt) {
		if(!isActive()){
			return;
		}
		timer-=dt;
		scale+=dt;
		if(timer<=0){
			deactivate();
			return;
		}
		this.updatePosition(dt);
	}

	public void render(Camera c) {
		CharGraphics.drawStringD(label, c.$cx(x)-(int)(4.5*label.length()*scale), c.$cy(y), (float)scale);
	}

	public boolean isCollidable(){
		return false;
	}
	public void onDeath() {
		
	}

}
