package hu.barbar.comm.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.sound.midi.Receiver;

import hu.barbar.comm.util.Msg;

public abstract class Client extends Thread {

	public static final int versionCode = 101;
	public static final String version = "1.0.1";
	
	
	protected int TIMEOUT_WAIT_WHILE_IS_OK_IN_MS = 1000;
			
	private Client me;
	protected SenderThread sender = null;
    protected ReceiverThread receiver = null;
    private ObjectInputStream objIn = null;
    private ObjectOutputStream objOut = null;
	
	private String host = null;
	private int port = 0;
	
	private Socket socket = null;
	private boolean initialized = false;
	
	private ClientThread myClientThread = null;

	
	
	public Client() {
		super();
		me = Client.this;
	}
	
	public Client(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		me = Client.this;
	}
	
	public Client(String host, int port, int timeOutForIsOK) {
		super();
		this.host = host;
		this.port = port;
		me = Client.this;
		this.TIMEOUT_WAIT_WHILE_IS_OK_IN_MS = timeOutForIsOK;
	}
	
	@Override
	public void run() {
		this.connect(host);
		//super.run();
	}
	
	/**
     *  Connect to specified host
     * @param host
     */
	protected void connect(String host) {
		
		if(host == null){
			
			return;
		}
		
		try {
            
        	/**
        	 *  Connect to Server
        	 */
			socket = new Socket(host, port);
			objOut = new ObjectOutputStream(socket.getOutputStream());
			objIn = new ObjectInputStream(socket.getInputStream());								
			
			System.out.println("Streams created.\nConnected to server " + host + " @ " + this.port);
           
        } catch (Exception ioe) {
        	System.err.println("Can not establish connection to " +  host + " @ " + port);
        	ioe.printStackTrace();
        	//System.exit(-1);
        	return;
        }
 
		
		/**
		 *  Create and start Sender thread
		 */
		//this.userName = getUsername();
        //sender = new Sender(out, this.userName);
		sender = new SenderThread(objOut);
		System.out.println("Sender created.");
        sender.setDaemon(true);
        sender.start();
 

        /**
		 *  Receiver
		 */
		this.receiver = new ReceiverThread(objIn) {
			@Override
			protected void handleMessage(Msg message) {
				handleRecievedMessage(message);
			}
			
		};
		receiver.start();
		
		initialized = true;
		
		/**
		 *  Get current user-count..
		 */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public boolean isInitialized(){
		return this.initialized;
	}
	
	public void onConnected() {}

	public void onDisconnected(){}
	
	protected abstract void handleRecievedMessage(Msg message);
	
	
	protected abstract void showOutput(String text);


	public boolean isConnected(){
		//TODO
		return false;
	}


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}


	public boolean setHost(String host) {
		if(socket == null || !socket.isConnected()){
			this.host = host;
			return true;
		}
		return false;
	}

	public boolean setPort(int port) {
		if(socket == null || !socket.isConnected()){
			this.port = port;
			return true;
		}
		return false;
	}

	
	public static int getVersioncode() {
		return versionCode;
	}

	public static String getVersion() {
		return version;
	}

	public boolean sendMessage(Msg msg) {
		if(msg == null)
			return false;
		sender.sendMsg(msg);
		return true;
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

}
