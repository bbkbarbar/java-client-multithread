package hu.barbar.comm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import hu.barbar.comm.util.Msg;

/**
 * This class made to handle communication with server
 * @author BA
 */
public abstract class ClientThread extends Thread {
	
	private Socket mySocket = null;
	private String host = null;
	private int port = 0;
	
	//private BufferedReader in = null;
	InputStream is = null;
	OutputStream os = null;
	private ObjectInputStream objIn = null;
	//private PrintWriter out = null;
	private ObjectOutputStream objOut = null;
	
	private boolean isInitialized = false;
	
	private boolean expectFurtherMessages = true;
	
	
	public ClientThread(Socket socket, String host, int port) {
		this.mySocket = socket;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * @return True when everything is ready for communication.
	 */
	public boolean isOK(){
		return (isInitialized && mySocket != null && mySocket.isConnected());
	}
	
	
	@Override
	public void run() {
		
		Msg receivedText = null;
		try {
			mySocket = new Socket(host, port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(mySocket.isClosed()){
			System.out.println("Socket is CLOSED.");
		}
		
		try {
			//in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			//out = new PrintWriter(mySocket.getOutputStream(), true);
			System.out.println("Init streams.");
			objIn = new ObjectInputStream(mySocket.getInputStream());
			objOut = new ObjectOutputStream(mySocket.getOutputStream());
			/**/
			System.out.println("Streams OK");
			isInitialized = true;
			
			int exceptionCounter = 0;
			
			while( expectFurtherMessages ){
				try{
					receivedText = (Msg) objIn.readObject();
					if(receivedText != null)
						handleReceivedMessage(receivedText);
				}catch(Exception e){
					exceptionCounter++;
					//System.out.println("Exception while try to read incoming message.");
					if(exceptionCounter >= 5){
						disconnect();
					}
				}
			}
			
		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}
		
	}

	public void disconnect(){
		this.expectFurtherMessages = false;
		this.interrupt();
	}
	
	public boolean sendMessageToServer(Msg message){
		
		int numberOfAttemps = 0;
		while(!isInitialized && numberOfAttemps++ < 5){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
		
		if (this.objOut != null) {
			try {
				objOut.writeObject(message);
				objOut.flush();
			} catch (IOException e) {
				return false;
			}
			return true;
		} else {
			System.out.println("ERROR: objOutput is NULL!");
			return false;
		}
	}
	
	public abstract boolean handleReceivedMessage(Msg message);
	
}
