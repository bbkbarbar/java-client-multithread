package hu.barbar.comm.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import hu.barbar.comm.util.Msg;

public abstract class ReceiverThread extends Thread {

	private ObjectInputStream objIn = null;
	private Client myParent = null;
	
	/**
	 * @param in ObjectInputStream from client's socket.
	 */
	public ReceiverThread(ObjectInputStream in, Client parent){
		this.objIn = in;
		this.myParent = parent;
	}
	
	@Override
	public void run() {
		
		Msg msg = null;
		
		try {

			while( (msg = (Msg) objIn.readObject()) != null ){
				handleMessage(msg);
			}
			
		} catch (IOException e) {
			if(myParent.getWantToDisconnect() == false){
				System.out.println("IOException while try to read message from server..");
				System.out.println("Client.Receiver.run() -> IOException");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Client.Receiver.run() -> ClassNotFoundException");
			e.printStackTrace();
		}
		
	}
	
	/**
	 *  Abstract function to handle incoming Message objects in place of using Receiver object.
	 * @param message
	 */
	protected abstract void handleMessage(Msg message);
	
}
