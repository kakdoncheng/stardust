package stardust.states;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import engine.State;
import engine.entities.EntityCollection;
import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Spark;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;

public abstract class StardustState extends State{

	protected StardustGame game;
	protected EntityCollection<StardustEntity> ec;
	protected EntityCollection<StardustEntity> sparks;
	
	public void addEntity(StardustEntity e){
		if(e instanceof Spark || e instanceof Explosion){
			sparks.addEntity(e);
		}else{
			ec.addEntity(e);
		}
	}
	public ArrayList<StardustEntity> $targetableEntities(){
		return ec.$entities();
	}
	public ArrayList<StardustEntity> $entities(){
		ArrayList<StardustEntity> entities=new ArrayList<StardustEntity>();
		entities.addAll(ec.$entities());
		entities.addAll(sparks.$entities());
		return entities;
	}
	
	public StardustState(StardustGame game) {
		this.game=game;
		this.ec=new EntityCollection<StardustEntity>();
		this.sparks=new EntityCollection<StardustEntity>();
		bgt=new ArrayList<String>();
		bgti=0;
	}
	
	private ArrayList<String> bgt;
	private int bgti;
	protected void clearBackgroundText(){
		bgt.clear();
		bgt.add("");
		bgti=0;
	}
	protected void updateBackgroundText(){
		bgt.set(bgti, game.$prng().$string(8)+" "+game.$prng().$string(game.$prng().$int(1, 80)));
		bgt.add(game.$prng().$string(8)+" ");
		bgti++;
		int ln=game.$prng().$int(0, 8);
		for(int i=0;i<ln;i++){
			bgt.add(game.$prng().$string(8)+" "+game.$prng().$string(game.$prng().$int(0, 20)));
			bgti++;
		}
	}
	protected void renderBackgroundText(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		for(int i=0;i<bgt.size();i++){
			CharGraphics.drawStringD(bgt.get(i), -game.$displayWidth()/2, (i*14)-game.$displayHeight()/2, 1);
		}
	}

}
