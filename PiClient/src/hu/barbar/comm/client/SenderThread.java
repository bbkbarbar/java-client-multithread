package hu.barbar.comm.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

import hu.barbar.comm.util.Msg;

public class SenderThread extends Thread {

	private ObjectOutputStream objOut = null;
	
	public SenderThread(ObjectOutputStream aOut){
		this.objOut = aOut;
	}
	
	public void sendMsg(Msg msg) {
		
		try {
			
			objOut.writeObject( msg );
			return;
			
		} catch (IOException e) {
			System.out.println("Client.SenderThread.sendMsg() -> IOException catched.");
			e.printStackTrace();
		}
		
	}
	
	
}
