package hu.barbar.comm.util;

import java.io.Serializable;

public class Msg implements Serializable{
	
	public static final int versionCode = 100;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4035266283582076042L;


	public class Types{
		public static final int UNDEFINED = 0,
								COMMAND = 1,
								PLAIN_TEXT = 2, 
								RGB_COMMAND = 3;
	}

	private int type = Types.UNDEFINED; 
	private String line = null;
	
	
	public Msg(String text){
		this.line = text;
		this.type = Types.PLAIN_TEXT;
	}
	
	public Msg(String text, int type){
		this.line = text;
		this.type = type;
	}
	
	
	
	public int getType(){
		return this.type;
	}
	
	public String getLine(){
		return this.line;
	}
	
}
