package engine;

import java.util.HashMap;

public class GameFlags {
	private static HashMap<String, Integer> flags;
	public static void init(){
		flags=new HashMap<String, Integer>();
	}
	public static void addFlag(String flag){
		flags.put(flag, 0);
	}
	public static void markFlag(String flag){
		int f=flags.getOrDefault(flag, -1);
		if(f>-1){
			flags.put(flag, f+1);
		}
	}
	public static void setFlag(String flag, int i){
		flags.put(flag, i);
	}
	public static void resetFlags(){
		for(String flag:flags.keySet()){
			if(!flag.contains("debug")){
				flags.put(flag, 0);
			}
		}
	}
	public static boolean is(String name){
		return flags.getOrDefault(name, -1)>0;
	}
	public static int valueOf(String name){
		return flags.getOrDefault(name, -1);
	}
	
	public static void printAllFlags(){
		for(String flag:flags.keySet()){
			System.out.printf("%s: %d\n", flag, valueOf(flag));
		}
	}
}
