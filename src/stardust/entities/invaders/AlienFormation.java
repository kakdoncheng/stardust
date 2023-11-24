package stardust.entities.invaders;

import engine.gfx.Camera;
import engine.sfx.Audio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.StardustEntity;

public abstract class AlienFormation extends StardustEntity{
	
	public int points(){
		return 100;
	}
	
	public AlienFormation(StardustGame game, double x, double y, int[][] rank) {
		super(game);
		setXY(x,y);
		setBoundRadius(adl);
		
		aliens=new ArrayList<Alien>();
		alienr=new Alien[rank.length][rank[0].length];
		for(int dy=0;dy<rank.length;dy++){
			for(int dx=0;dx<rank[dy].length;dx++){
				double ddx=(double)dx*pad;
				double ddy=(double)dy*pad;
				Alien a;
				if(rank[dy][dx]==1){
					a=new AlienTypeIka(game,0,0);
				}else if(rank[dy][dx]==2){
					a=new AlienTypeKani(game,0,0);
				}else{
					a=new AlienTypeKura(game,0,0);
				}
				a.setXY(ddx+x-(pad*0.5*(rank[dy].length-1)), ddy+y-(pad*0.5*(rank.length-1)));
				aliens.add(a);
				alienr[dy][dx]=a;
				if(rank[dy][dx]>0){
					game.$currentState().addEntity(a);
				}else{
					a.deactivate();
				}
			}
		}
		aliens.sort(dRightFirst);
	}
	
	
	//alien & focus
	private Alien[][] alienr;
	private ArrayList<Alien> aliens;
	private Comparator<Alien> dLeftFirst=new Comparator<Alien>(){
		public int compare(Alien a, Alien b) {
			if(!(a!=null&&b!=null))
				return 0;
			if(!a.isActive()&&b.isActive()){
				return -1;
			}
			if(a.isActive()&&!b.isActive()){
				return 1;
			}
			if(a.$y()<b.$y())
				return 1;
			if(a.$y()>b.$y())
				return -1;
			if(a.$x()<b.$x())
				return -1;
			if(a.$x()>b.$x())
				return 1;
			return 0;
		}
	};
	private Comparator<Alien> dRightFirst=new Comparator<Alien>(){
		public int compare(Alien a, Alien b) {
			if(!(a!=null&&b!=null))
				return 0;
			if(!a.isActive()&&b.isActive()){
				return -1;
			}
			if(a.isActive()&&!b.isActive()){
				return 1;
			}
			if(a.$y()<b.$y())
				return 1;
			if(a.$y()>b.$y())
				return -1;
			if(a.$x()<b.$x())
				return 1;
			if(a.$x()>b.$x())
				return -1;
			return 0;
		}
	};
	
	private Alien $leftmostAlien(){
		Alien a=aliens.get(0);
		for(int i=1;i<aliens.size();i++){
			if(a.$x()>aliens.get(i).$x()){
				a=aliens.get(i);
			}
		}
		return a;
	}
	private Alien $rightmostAlien(){
		Alien a=aliens.get(0);
		for(int i=1;i<aliens.size();i++){
			if(a.$x()<aliens.get(i).$x()){
				a=aliens.get(i);
			}
		}
		return a;
	}
	
	private double pad=18.0;
	private int ai=0; //list index
	private int ami=1; //action index
	//private int adx=0;
	private int adl=120;
	private double at=0; //action time
	private double atl=1.0/60; //action time limit
	private int sfxi=0;
	
	private StardustEntity lastr=null;
	public StardustEntity $killedLastAlien(){
		return lastr;
	}
	public StardustEntity $lastAlien(){
		if(aliens!=null&&aliens.size()==1){
			return aliens.get(0);
		}
		return null;
	}
	
	public void update(double dt) {
		
		// deactivate if no more aliens
		if(aliens.size()<1){
			active=false;
		}
		if(!active){
			return;
		}
		
		// move aliens
		if(ai<aliens.size()){
			// nudge aliens
			at+=dt;
			while(at>=atl && ai<aliens.size()){
				at-=atl;
				if(aliens.get(ai).isActive()){
					if(ami==0){
						aliens.get(ai).nudgeLeft();
					}else if(ami==2){
						aliens.get(ai).nudgeRight();
					}else{
						aliens.get(ai).nudgeDown();
					}
				}
				ai++;
			}
		}else{
			// decide next step
			ai=0;
			
			// add sfx
			if(sfxi<1) {
				Audio.addSoundEffect("invaders-a", 1);
			} else if(sfxi<2) {
				Audio.addSoundEffect("invaders-b", 1);
			} else if(sfxi<3) {
				Audio.addSoundEffect("invaders-c", 1);
			} else {
				Audio.addSoundEffect("invaders-d", 1);
			}
			sfxi+=1;
			sfxi%=4;
			
			Alien last=null;
			Iterator<Alien> ie=aliens.iterator();
			while(ie.hasNext()){
				Alien e=ie.next();
				e.update(dt);
				if(!e.isActive()){
					ie.remove();
					last=e;
				}
			}
			if(aliens.size()<1){
				lastr=last;
				game.$currentState().addEntity(new ElectromagneticPulse(game, last.$x(), last.$y()));
			}
			if(ami==0){
				if(aliens.size()>0&&$leftmostAlien().$x()<=-adl+x){
					ami++;
					aliens.sort(dRightFirst);
				}
			}else if(ami==1){
				ami++;
			}else if(ami==2){
				if(aliens.size()>0&&$rightmostAlien().$x()>=adl+x){
					ami++;
					aliens.sort(dLeftFirst);
				}
			}else{
				ami++;
			}
			ami%=4;
		}
		
		//attempt to fire
		for(int dy=0;dy<alienr.length;dy++){
			for(int dx=0;dx<alienr[dy].length;dx++){
				if(alienr[dy][dx].isReadyToFire()){
					boolean clear=true;
					for(int ddy=dy+1;ddy<alienr.length;ddy++){
						if(alienr[ddy][dx].isActive()){
							clear=false;
						}
					}
					if(clear){
						game.$currentState().addEntity(new AlienProjectile(game, 0, alienr[dy][dx]));
					}
					alienr[dy][dx].resetCooldown();
				}
			}
		}
		
		// chomp target if exist
		if(aliens.size()>0){
			if(target!=null&&aliens.get(0).$y()+aliens.get(0).$r()>=target.$y()-target.$r()){
				aliens.get(0).anger();
				target.deactivate();
			}
		}
	}

	public void render(Camera c) {
		//debug render
		/*
		if(aliens.size()>0){
			$leftmostAlien().renderCollisionBounds(c, 16);
			$rightmostAlien().renderCollisionBounds(c, 16);
		}
		
		// move bounds
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,1);
		GL11.glVertex2d(c.$cx(adl+x), c.$cy(game.$displayHeight()/2));
		GL11.glVertex2d(c.$cx(adl+x), c.$cy(-game.$displayHeight()/2));
		GL11.glVertex2d(c.$cx(-adl+x), c.$cy(game.$displayHeight()/2));
		GL11.glVertex2d(c.$cx(-adl+x), c.$cy(-game.$displayHeight()/2));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//*/
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
	}
}
