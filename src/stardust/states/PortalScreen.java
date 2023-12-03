package stardust.states;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;
import engine.sfx.Audio;
import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.PortalScreenPlayer;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;

public class PortalScreen extends StardustState{

	public PortalScreen(StardustGame game) {
		super(game);
		titles=new ArrayList<String>();
		titles.add("stardust.");
		titles.add("three word limit.");
		titles.add("at any cost.");
		titles.add("back to dust.");
		titles.add("dust to dust.");
		titles.add("it must end.");
		titles.add("here and now.");
		titles.add("slow and steady.");
		titles.add("meaningless thoughts.");
		titles.add("hope is fleeting.");
		titles.add("fear nothing.");
		titles.add("nothing is empty.");
		titles.add("dare to hope.");
		titles.add("death is temporary.");
		titles.add("slow is smooth.");
		titles.add("smooth is fast.");
		titles.add("despair is futile.");
		titles.add("time is meaningless.");
		titles.add("time is eternal.");
		titles.add("change is constant.");
		titles.add("constantly changing.");
		titles.add("an endless sky.");
		titles.add("act swiftly.");
		titles.add("panic invites death.");
		titles.add("fear inspires death.");
		titles.add("a new beginning.");
		titles.add("hear the silence.");
		titles.add("a false angel.");
		titles.add("a silent void.");
		titles.add("actions are loud.");
		titles.add("against infinity.");
		titles.add("break the rules.");
		titles.add("born from dust.");
		titles.add("an endless struggle.");
		titles.add("stay strong.");
		titles.add("keep fighting.");
		titles.add("believe in yourself.");
		titles.add("begin again.");
		titles.add("filled with determination.");
		titles.add("persistence is key.");
		titles.add("whatever it takes.");
		titles.add("trust your eyes.");
		titles.add("trust your feelings.");
		titles.add("once more.");
		titles.add("unto the breach.");
		titles.add("the past echoes.");
		titles.add("a flat circle.");
		titles.add("a sharp breath.");
		titles.add("for a loop.");
		titles.add("narrow your scope.");
		titles.add("break the world.");
		titles.add("never forget.");
		titles.add("fear is exploitable.");
		titles.add("stand tall.");
		titles.add("a dark past.");
		titles.add("forget the past.");
		titles.add("the past warns.");
		titles.add("a terrifying shadow.");
		titles.add("panic kills.");
		titles.add("breathe.");
		titles.add("breathe. relax.");
		titles.add("breathe out.");
		titles.add("a lost soul.");
		titles.add("the wanderer searches.");
		titles.add("time is fleeting.");
		titles.add("panic inspires fear.");
		titles.add("heed the warnings.");
		titles.add("an endless world.");
		titles.add("it never ends.");
		titles.add("time flies.");
		titles.add("recklessness kills.");
		titles.add("beware what's hidden.");
		titles.add("always keep moving.");
		titles.add("keep hidden.");
		titles.add("they whisper.");
		titles.add("the dark whispers.");
		titles.add("the angel whispers.");
		titles.add("false whispers.");
		titles.add("stay strong.");
		titles.add("stay hidden.");
		titles.add("keep moving forward.");
		titles.add("never stop trying.");
		titles.add("failure is temporary.");
		titles.add("an idle mind.");
		titles.add("keep going.");
		titles.add("pursue the truth.");
		titles.add("light is blinding.");
		titles.add("seeking truth.");
		titles.add("silence is eternal.");
		titles.add("a cautious step.");
		titles.add("take caution.");
		titles.add("tread carefully.");
		titles.add("beware the past.");
		titles.add("death teaches.");
		titles.add("circles.");
		titles.add("endless circles.");
		titles.add("endless loops.");
		titles.add("looping time.");
		titles.add("destroy them all.");
		titles.add("hello world.");
		ti=0;
		reset();
	}

	// internal flags/var
	private double ft;
	private boolean delay;
	
	// dynamic title
	private int ti;
	private double tt;
	private ArrayList<String> titles;
	private StringBuilder title;
	
	// entities
	private StardustEntity player;
	
	public void reset() {
		delay=false;
		Audio.clearBackgroundMusicQueue();
		Audio.clearBackgroundMusic();
		if(GameFlags.is("initial-startup")) {
			GameFlags.setFlag("initial-startup", 0);
			delay=true;
			Audio.queueBackgroundMusic("lifelike-126735/begin");
		} else {
			double rnd=game.$prng().$double(0, 1);
			if(rnd<0.4) {
				delay=true;
				Audio.queueBackgroundMusic("lifelike-126735/begin");
			} else if(rnd<0.8) {
				Audio.queueBackgroundMusic("password-infinity-123276/intro");
				Audio.queueBackgroundMusic("password-infinity-123276/loop-1");
			} else {
				Audio.queueBackgroundMusic("password-infinity-123276/loop-3-lo");
				Audio.queueBackgroundMusic("password-infinity-123276/loop-2");
			}
		}
		
		
		GameFlags.setFlag("begin", 0);
		GameFlags.setFlag("warp", 0);
		GameFlags.setFlag("asteroids", 0);
		GameFlags.setFlag("lunar", 0);
		GameFlags.setFlag("invaders", 0);
		GameFlags.setFlag("demonstar", 0);
		GameFlags.setFlag("gradius", 0);
		GameFlags.setFlag("gyruss", 0);
		GameFlags.setFlag("warhead", 0);
		GameFlags.setFlag("terra", 0);
		game.resetScore();
		game.resetWarpFlags();
		targetable.clear();
		targetable.setRenderDistance(StardustGame.BOUNDS);
		particles.clear();
		particles.setRenderDistance(StardustGame.BOUNDS);
		//game.$camera().hardCenterOnPoint(0, 0);
		
		player=new PortalScreenPlayer(game, game.$prng().$double(-360, 360), game.$prng().$double(-360, 360));
		//ec.addEntity(player);
		
		// do not reset ti, persistent
		title=new StringBuilder();
		tt=0;
		
		ft=0;
		if(GameFlags.is("flash-hiscore")){
			GameFlags.setFlag("flash-hiscore", 0);
			ft=8.999;
		}
	}

	public void update(double dt) {
		player.update(dt);
		
		// flashing hi score
		ft-=dt;
		
		// dynamic title
		if(ft<0) {
			tt+=dt;
			String t=titles.get(Math.max(ti, 0));
			int tl=title.length();
			if(tl<t.length() && tt>0.18) {
				char c=t.charAt(tl);
				title.append(c);
				tt=0;
			}
		}
		
		game.$camera().centerOnEntity(player, dt);
		
		if(Mouse.isButtonDown(0) || Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			if(ti<0) {
				ti++;
			} else {
				ti=game.$prng().$int(0, titles.size());
			}
			if(delay) {
				Audio.queueBackgroundMusic("lifelike-126735/loop-1");
				Audio.queueBackgroundMusic("lifelike-126735/loop-1");
				Audio.queueBackgroundMusic("lifelike-126735/loop-2");
			}
			GameFlags.setFlag("success", 1);
			GameFlags.setFlag("player-x", (int)player.$x());
			GameFlags.setFlag("player-y", (int)player.$y());
			GameFlags.setFlag("player-ft", (int)(player.$speedt()*10000));
			GameFlags.setFlag("player-speed", (int)player.$speed());
			State.setCurrentState(0);
			game.$currentState().reset();
			game.$currentState().addEntity(new ElectromagneticPulse(game,player.$x(),player.$y()));
			GameFlags.setFlag("player-ft", 0);
			GameFlags.setFlag("player-speed", 0);
		}
	}

	public void render(Camera c) {
		player.render(c);
		if(ft>0) {
			if(ft%1>=0.5) {
				String s="new record!";
				if(game.$histage()>8) {
					s="the final score.";
				}
				CharGraphics.drawTitleString(s, //Звездна�?.Пыль звездна�?.пыль
						(-game.$displayWidth()/2)+36,
						(+game.$displayHeight()/2)-48,
						1f);
			}
		} else {
			CharGraphics.drawTitleString(title.toString(), //Звездна�?.Пыль звездна�?.пыль
					(-game.$displayWidth()/2)+36,
					(+game.$displayHeight()/2)-48,
					1f);
		}
		if(ft%1>=0.5 || Keyboard.isKeyDown(Keyboard.KEY_TAB)){
			CharGraphics.drawHeaderString(game.$hiscore(),//ft>0?game.$hiscore()+"!":game.$hiscore(), //"ЛР лр"ft>0?game.$hiscore()+" новый!":
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		/*
		CharGraphics.drawStringD(StardustGame.version, //Звездна�?.Пыль звездна�?.пыль
				(game.$displayWidth()/2)-(9*StardustGame.version.length()),
				(game.$displayHeight()/2)-18,
				1f);
		//*/
		
	}

}
