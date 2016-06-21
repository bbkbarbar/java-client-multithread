package hu.barbar.comm.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import hu.barbar.comm.util.Commands;
import hu.barbar.comm.util.Msg;

public abstract class Client extends Thread {

	public static final int versionCode = 101;
	public static final String version = "1.0.1";
	
	
	protected int TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS = 1000;
	private static final int DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS = 50;
			
	private Client me;
	protected SenderThread sender = null;
    protected ReceiverThread receiver = null;
    
    private InputStream is = null;
    private OutputStream os = null;
    private ObjectInputStream objIn = null;
    private ObjectOutputStream objOut = null;
	
	private String host = null;
	private int port = 0;
	
	private Socket socket = null;
	private boolean initialized = false;
	private boolean wantToDisconnect = false;
	
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
		this.TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS = timeOutForIsOK;
	}
	
	@Override
	public void run() {
		this.connect(host);
		super.run();
	}
	
	/**
     *  Connect to specified host
     * @param host
     */
	protected void connect(String host) {
		
		if(host == null){
			
			return;
		}
		
		this.wantToDisconnect = true;
		
		try {
            
        	/**
        	 *  Connect to Server
        	 */
			socket = new Socket(host, port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			objOut = new ObjectOutputStream(os);
			objIn = new ObjectInputStream(is);								
			
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
		 *  Create and start Receiver thread
		 */
		this.receiver = new ReceiverThread(objIn, Client.this) {
			@Override
			protected void handleMessage(Msg message) {
				handleRecievedMessage(message);
			}
			
		};
		receiver.start();
		
		initialized = true;
		
		//TODO: maybe removable:
		/**
		 *  Get current user-count..
		 */
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.onConnected();
		
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
	
	
	public boolean waitWhileIsInitialized(){
		int maxTries = TIMEOUT_WAIT_WHILE_INITIALIZED_IN_MS / DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS;
		if(maxTries<1){
			maxTries = 1;
		}
		try {
			int attempCount = 0;
			while(this.isInitialized() == false && attempCount < maxTries){
				Thread.sleep(DELAY_BETWEEN_CHECKS_FOR_INITIALIZED_STATE_IN_MS);
				attempCount++;
			}
		} catch (InterruptedException e) {
			// Do nothing..
		}
		return this.initialized;
	}
	

	public boolean sendMessage(Msg msg) {
		if(msg == null)
			return false;
		sender.sendMsg(msg);
		return true;
	}

	public void disconnect() {
		
		this.wantToDisconnect = true;
		Msg byeMsg = new Msg(Commands.CLIENT_EXIT, Msg.Types.COMMAND);
		this.sendMessage(byeMsg);
		
		receiver.interrupt();
		sender.interrupt();
		
		try {
			objIn.close();
		} catch (IOException e) {}
		try {
			objOut.close();
		} catch (IOException e) {}
		try {
			is.close();
		} catch (IOException e) {}
		try {
			os.close();
		} catch (IOException e) {}
		try {
			socket.close();
		} catch (IOException e) {}
		
		
		this.initialized = false;
		
		this.onDisconnected();
		
	}
	
	public boolean getWantToDisconnect(){
		return this.wantToDisconnect;
	}

}
