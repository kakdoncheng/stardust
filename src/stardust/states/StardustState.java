package stardust.states;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import engine.State;
import engine.entities.EntityCollection;
import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.entities.Explosion;
import stardust.entities.Spark;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;

public abstract class StardustState extends State{
	
	// core
	protected StardustGame game;
	protected EntityCollection<StardustEntity> targetable;
	protected EntityCollection<StardustEntity> particles;
	
	public void addEntity(StardustEntity e){
		if(e instanceof Spark || e instanceof Explosion){
			particles.addEntity(e);
		}else{
			targetable.addEntity(e);
		}
	}
	public ArrayList<StardustEntity> $targetableEntities(){
		return targetable.$entities();
	}
	public ArrayList<StardustEntity> $entities(){
		ArrayList<StardustEntity> entities=new ArrayList<StardustEntity>();
		entities.addAll(targetable.$entities());
		entities.addAll(particles.$entities());
		return entities;
	}
	
	public StardustState(StardustGame game) {
		this.game=game;
		this.targetable=new EntityCollection<StardustEntity>();
		this.particles=new EntityCollection<StardustEntity>();
		bgmSequences=new ArrayList<ArrayList<String>>();
		bgt=new ArrayList<String>();
		bgti=0;
	}
	
	// bgm
	private ArrayList<ArrayList<String>> bgmSequences;
	public void addBGMSequence(String key) {
		ArrayList<String> sequence=new ArrayList<String>();
		sequence.add(key);
		addBGMSequence(sequence);
	}
	public void addBGMSequence(ArrayList<String> sequence) {
		bgmSequences.add(sequence);
	}
	public void playBGMSequence(int index) {
		ArrayList<String> list=bgmSequences.get(index);
		for(String track:list) {
			Audio.queueBackgroundMusic(track);
		}
	}
	public void playRandomBGMSequence() {
		int index=game.$prng().$int(0, bgmSequences.size());
		playBGMSequence(index);
	}
	
	// transitions
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
