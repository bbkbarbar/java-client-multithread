package hu.barbar.client;

import hu.barbar.comm.client.Client;
import hu.barbar.comm.util.Msg;

public class ClientApp {

	private static ClientApp me = null;

	private static final boolean DEBUG_MODE = false;

	public static String ver = "0.1";
	public static final String DEFAULT_SERVER_HOSTNAME = (DEBUG_MODE ? "localhost" : "localhost");
	public static final int DEFAULT_SERVER_PORT = (DEBUG_MODE ? 13003 : 10713);

	public String SERVER_HOSTNAME = DEFAULT_SERVER_HOSTNAME;
	public int SERVER_PORT = -1;

	private Client myClient = null;
	
	//TODO
	boolean responseReceived = false;
	
	public static void main(String[] args) {

		if (DEBUG_MODE) {
			ver = ver + " DEBUG MODE";
		}

		me = new ClientApp();
		me.start(args);
	}

	private void start(String[] args) {

		myClient = new Client(DEFAULT_SERVER_HOSTNAME, DEFAULT_SERVER_PORT) {
			
			@Override
			protected void showOutput(String text) {
				System.out.println(text);
			}
			
			@Override
			protected void handleRecievedMessage(Msg message) {
				System.out.println("Message received from SERVER: " + message);
			}
			
		};
		myClient.start();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(myClient.sendMessage(new Msg("dateTime"))){
			System.out.println("Sent: " + "dateTime");
		}
		if(myClient.sendMessage(new Msg("c:LEDS_ON"))){
			System.out.println("Sent: " + "c:LEDS_ON");
		}
		
		myClient.disconnect();
		
		/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String custMsg = "ERROR";
		try {
			custMsg = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		myClient.sendMessage(custMsg);
		/**/

	}
	
	public void showOutput(String text){
		System.out.println(text);
	}

}
