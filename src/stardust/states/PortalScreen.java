package stardust.states;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import stardust.StardustGame;
import stardust.entities.ElectromagneticPulse;
import stardust.entities.PortalScreenPlayer;
import stardust.entities.StardustEntity;
import stardust.gfx.CharGraphics;
import engine.GameFlags;
import engine.State;
import engine.gfx.Camera;

public class PortalScreen extends StardustState{

	public PortalScreen(StardustGame game) {
		super(game);
		reset();
	}

	// internal flags/var
	private double ft;
	
	// entities
	private StardustEntity player;
	
	public void reset() {
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
		ec.clear();
		ec.setRenderDistance(StardustGame.BOUNDS);
		sparks.clear();
		sparks.setRenderDistance(StardustGame.BOUNDS);
		//game.$camera().hardCenterOnPoint(0, 0);
		
		player=new PortalScreenPlayer(game, game.$prng().$double(-360, 360), game.$prng().$double(-360, 360));
		//ec.addEntity(player);
		
		ft=0;
		if(GameFlags.is("flash-hiscore")){
			GameFlags.setFlag("flash-hiscore", 0);
			ft=8.999;
		}
	}

	public void update(double dt) {
		player.update(dt);
		
		ft-=dt;
		
		game.$camera().centerOnEntity(player, dt);
		
		if(Mouse.isButtonDown(0)){ //Keyboard.isKeyDown(Keyboard.KEY_SPACE)
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
		CharGraphics.drawTitleString("stardust.", //Звездна�?.Пыль звездна�?.пыль
				(-game.$displayWidth()/2)+36,
				(+game.$displayHeight()/2)-48,
				1f);
		CharGraphics.drawStringD(StardustGame.version, //Звездна�?.Пыль звездна�?.пыль
				(game.$displayWidth()/2)-(9*StardustGame.version.length()),
				(game.$displayHeight()/2)-18,
				1f);
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB) || ft%1>=0.5){
			CharGraphics.drawHeaderString(ft>0?game.$hiscore()+"!":game.$hiscore(), //"ЛР лр"ft>0?game.$hiscore()+" новый!":
					(-game.$displayWidth()/2)+18,
					(-game.$displayHeight()/2)+18,
					1);
		}
		
	}

}
