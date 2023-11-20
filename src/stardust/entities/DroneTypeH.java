package stardust.entities;

import engine.gfx.Camera;

import org.lwjgl.opengl.GL11;

import stardust.StardustGame;

public class DroneTypeH extends Starcraft{
	
	public int points() {
		return 75;
	}
	
	public DroneTypeH(StardustGame game, double x, double y) {
		super(game);
		setXY(x,y);
		setBoundRadius(8);
		setDirection(0);
	}
	
	private int ammo=0;
	private double reloadt=0;
	private double cooldown=0;
	
	public void update(double dt) {
		if(!active){
			return;
		}
		updateBlip(dt);
		
		// weapons
		t=directionTo(target);
		if(ammo<1){
			reloadt+=dt;
			if(reloadt>4){
				ammo=8;
				reloadt=0;
			}
		}
		if (distanceTo(target)<240 && ammo>0){
    		cooldown+=dt;
    		if(cooldown>0.125){
    			game.$currentState().addEntity(new StarcraftProjectile(game, t-0.025, this));
    			game.$currentState().addEntity(new StarcraftProjectile(game, t+0.025, this));
    			cooldown=0;
    			ammo--;
    		}
    	}
		
		// resolve movement
		updatePosition(dt);
		wraparoundIfOutOfBounds();
	}

	// legacy render code
	private double scale=1.5;
	private double[] cx={ 0, 0, 2, 2, 4, 4, 6, 6, 5, 5, 4, 4, 2, 2, 1, 1, 0,-1,-1,-2,-2,-4,-4,-5,-5,-6,-6,-4,-4,-2,-2};
	private double[] cy={-2,-2,-3,-2,-1,-3,-1, 2, 4, 2, 1, 0, 1, 2, 8, 3, 2, 3, 8, 2, 1, 0, 1, 2, 4, 2,-1,-3,-1,-2,-3};
	public void render(Camera c) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);	
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_LINES);
		setRadarColor(1);
		for(int i=0; i<cx.length; i++){
			GL11.glVertex2d(cx[i]*c.$zoom()*scale, cy[i]*c.$zoom()*scale);
			GL11.glVertex2d(cx[(i+1)%cx.length]*c.$zoom()*scale, cy[(i+1)%cx.length]*c.$zoom()*scale);
		}
		GL11.glEnd();
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void onDeath() {
		
	}
}
