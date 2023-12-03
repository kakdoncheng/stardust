package stardust.entities.invaders;

import java.util.ArrayList;

import engine.Vector;
import stardust.StardustGame;
import stardust.entities.StardustEntity;

public class BlastShield {
	
	private double x;
	private double y;
	
	private ArrayList<BlastShieldBit> activeBits;
	private BlastShieldBit[][] bits;
	public BlastShieldBit[][] $bits(){
		return bits;
	}
	
	public BlastShield(StardustGame game, double x, double y) {
		// create mask
		int[][] mask={
				{0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
				{0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
				{0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
				{0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
				{0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1},
				{1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
				{1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1},
				{1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1},
		};
		
		// calculate offsets
		this.x=x;
		this.y=y;
		double xOff=x-(mask[0].length*BlastShieldBit.HALF_WIDTH);
		double yOff=y-(mask.length*BlastShieldBit.HALF_WIDTH);
		
		// create bit maps 
		bits=new BlastShieldBit[mask.length][mask[0].length];
		activeBits=new ArrayList<BlastShieldBit>();
		
		// create bits
		for(int iy=0;iy<bits.length;iy++) {
			for(int ix=0;ix<bits[0].length;ix++) {
				if(mask[iy][ix]<1) {
					bits[iy][ix]=null;
					continue;
				}
				double ex=xOff+(BlastShieldBit.HALF_WIDTH*2*ix);
				double ey=yOff+(BlastShieldBit.HALF_WIDTH*2*iy);
				bits[iy][ix]=new BlastShieldBit(game, ix, iy, ex, ey);
				activeBits.add(bits[iy][ix]);
				game.$currentState().addEntity(bits[iy][ix]);
			}
		}
		
		// reset edges
		resolveEdges();
	}
	
	public void resolveCollisionsWith(ArrayList<StardustEntity> entities) {
		for(StardustEntity e:entities) {
			if(e==null || !e.isActive()) {
				continue;
			}
			double dist=Vector.distanceFromTo(x, y, e.$x(), e.$y());
			if(dist<64) {
				if(e instanceof AlienProjectile) {
					boolean hit=false;
					for(BlastShieldBit bit:activeBits) {
						hit=bit.checkForCollisionWith((AlienProjectile)e, bits);
						if(hit) {
							break;
						}
					}
					if(hit) {
						resolveEdges();
					}
				}else if(e instanceof ShellProjectile) {
					boolean hit=false;
					for(BlastShieldBit bit:activeBits) {
						hit=bit.checkForCollisionWith((ShellProjectile)e);
						if(hit) {
							break;
						}
					}
				}else if(e instanceof Alien) {
					boolean hit=false;
					for(BlastShieldBit bit:activeBits) {
						hit=bit.checkForCollisionWith((Alien)e)||hit;
					}
					if(hit) {
						resolveEdges();
					}
				}
			}
		}
	}
	
	private void resolveEdges() {
		// remove non-active bits
		for(int i=0;i<activeBits.size();i++) {
			if(!activeBits.get(i).isActive()) {
				activeBits.remove(i);
				i--;
			}
		}
		// check each bit edge
		for(BlastShieldBit bit:activeBits) {
			bit.checkEdges(bits);
		}
	}
}
