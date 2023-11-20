package stardust.entities.demonstar;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public abstract class MartianShip extends StardustEntity{

	public static boolean oobsd=false;
	public static void destroyIfOutOfScreenBounds(boolean b){
		oobsd=b;
	}
	
	public MartianShip(StardustGame game) {
		super(game);
	}
	
	public void deactivateIfOutOfBounds(){
		if(oobsd){
			super.deactivateIfOutOfScreenBounds();
		}else{
			super.deactivateIfOutOfBounds();
		}
	}

}
