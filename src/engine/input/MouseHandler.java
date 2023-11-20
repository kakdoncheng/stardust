package engine.input;

import org.lwjgl.input.Mouse;

import engine.gfx.Camera;

public class MouseHandler {
	private static Camera c;
	private static double dw, dh;
	
	public static void setDisplayDimensions(int w, int h){
		MouseHandler.dw=w;
		MouseHandler.dh=h;
	}
	public static void focusOnCamera(Camera c){
		MouseHandler.c=c;
	}
	
	// actual screen mouse coords
	public static double $ax(){
		return -(dw/2)+Mouse.getX();
	}
	public static double $ay(){
		return (dh/2)-Mouse.getY();
	}
	
	// world mouse coords
	public static double $mx(){
		return c.$cmx($ax());
	}
	public static double $my(){
		return c.$cmy($ay());
	}

}
