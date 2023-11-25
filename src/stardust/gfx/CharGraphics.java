package stardust.gfx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class CharGraphics {
	
	private static char nul='?';
	private static String alphabet=" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~Ï€Ð°Ð±Ð²Ð³Ð´ÐµÑ‘Ð¶Ð·Ð¸Ð¹ÐºÐ»Ð¼Ð½Ð¾Ð¿Ñ€Ñ�Ñ‚ÑƒÑ„Ñ…Ñ†Ñ‡ÑˆÑ‰ÑŠÑ‹ÑŒÑ�ÑŽÑ�Ð�Ð‘Ð’Ð“Ð”Ð•Ð�Ð–Ð—Ð˜Ð™ÐšÐ›ÐœÐ�ÐžÐŸÐ Ð¡Ð¢Ð£Ð¤Ð¥Ð¦Ð§Ð¨Ð©ÐªÐ«Ð¬Ð­Ð®Ð¯АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя";
	private static HashMap<Character, Texture> mapd, map, map2, map3, mapR, map2R;
	
	public static void registerFont(String path){
		//InputStream is = StardustGame.class.getResourceAsStream(path);
		InputStream is = null;
		try {
			is = new FileInputStream(new File(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Font uniFont = null;
		try {
			uniFont = Font.createFont(Font.TRUETYPE_FONT,is);
		} catch (FontFormatException | IOException e) {
			System.out.print("WARNING: Exception loading font "+path+".\n");
			e.printStackTrace();
			return;
		}
		Font f = uniFont.deriveFont(24f);
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(f);
	}
	
	private static void loadCharTexture(char c){
		BufferedImage img=TextureLoader.newTransparentImage(16, 16);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Lucida Console", Font.BOLD, 16));
		g.setColor(Color.WHITE);
		g.drawString(c+"", 0, 14);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		map.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	private static void loadCharTextureD(char c){
		BufferedImage img=TextureLoader.newTransparentImage(16, 16);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 16));
		g.setColor(new Color(0, 128, 0));
		g.drawString(c+"", 0, 14);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		mapd.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	private static void loadCharTexture2(char c){
		BufferedImage img=TextureLoader.newTransparentImage(32, 32);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 32));
		g.setColor(Color.WHITE);
		g.drawString(c+"", 0, 28);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		map2.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	private static void loadCharTexture3(char c){
		BufferedImage img=TextureLoader.newTransparentImage(64, 64);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 64));
		g.setColor(Color.WHITE);
		g.drawString(c+"", 0, 56);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		map3.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	private static void loadRedCharTexture(char c){
		BufferedImage img=TextureLoader.newTransparentImage(16, 16);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 16));
		g.setColor(Color.RED);
		g.drawString(c+"", 0, 14);
		g.dispose();
		//System.out.println(c+" "+img);
		TextureLoader.loadTexture(c+"", img);
		mapR.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	private static void loadRedCharTexture2(char c){
		BufferedImage img=TextureLoader.newTransparentImage(32, 32);
		Graphics g=img.createGraphics();
		g.setFont(new Font("Unifont", Font.PLAIN, 32));
		g.setColor(Color.RED);
		g.drawString(c+"", 0, 28);
		g.dispose();
		TextureLoader.loadTexture(c+"", img);
		map2R.put(c, TextureLoader.$texture(c+""));
		TextureLoader.removeTexture(c+"");
	}
	
	//new Font("Lucida Console", Font.BOLD, 16);
	// !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
	public static void init(){
		registerFont("./fonts/unifont-15.1.04.otf");
		map=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTexture(c);
		}
		mapd=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTextureD(c);
		}
		map2=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTexture2(c);
		}
		map3=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadCharTexture3(c);
		}
		
		mapR=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadRedCharTexture(c);
		}
		map2R=new HashMap<Character, Texture>();
		for(char c:alphabet.toCharArray()){
			loadRedCharTexture2(c);
		}
	}
	
	public static void drawString(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			map.getOrDefault(s.charAt(i), map.get(nul)).render(x+8+(int)dx, y+8,0,scale);
			dx+=(11*scale);
		}
	}
	public static void drawStringD(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			mapd.getOrDefault(s.charAt(i), mapd.get(nul)).render(x+8+(int)dx, y+8,0,scale);
			dx+=(9*scale);
		}
	}
	
	public static void drawHeaderString(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			map2.getOrDefault(s.charAt(i), map2.get(nul)).render(x+8+(int)dx, y+8,0,scale);
			dx+=(18*scale);
		}
	}
	public static void drawTitleString(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			map3.getOrDefault(s.charAt(i), map2.get(nul)).render(x+8+(int)dx, y+8,0,scale);
			dx+=(36*scale);
		}
	}
	
	public static void drawRedString(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			mapR.getOrDefault(s.charAt(i), mapR.get(nul)).render(x+8+(int)dx, y+8,0,scale);
			dx+=(9*scale);
		}
	}
	public static void drawRedHeaderString(String s, int x, int y, float scale){
		double dx=0;
		for(int i=0; i<s.length(); i++){
			map2R.getOrDefault(s.charAt(i), map2R.get(nul)).render(x+16+(int)dx, y+16,0,scale);
			dx+=(18*scale);
		}
	}

}
