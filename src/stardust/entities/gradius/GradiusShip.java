package stardust.entities.gradius;

import stardust.StardustGame;
import stardust.entities.StardustEntity;

public abstract class GradiusShip extends StardustEntity{

	public static boolean oobsd=false;
	public static void destroyIfOutOfScreenBounds(boolean b){
		oobsd=b;
	}
	
	public GradiusShip(StardustGame game) {
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
