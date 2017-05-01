package ch.unibas.dmi.cs108.sand.network;

import ch.unibas.dmi.cs108.sand.gui.GameController;
import ch.unibas.dmi.cs108.sand.logic.Character;
import ch.unibas.dmi.cs108.sand.logic.GameClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {


	Socket mySocket = null;
	BufferedReader in = null;
	PrintWriter out = null;
	boolean connected = false;
	String userName = "anonymous";//default
	String myHost = "localhost";//default
	int myPort;
	final int TIMEOUT = 5000;
	PingPong pingPong;
	private ArrayList<String> availableGames = new ArrayList<String>();
	GameClient gameClient = null;

	/** call other constructor with default values */
	public Client() {
		this(9999 );
	}
	public Client(int port){
		connect(port);
		communicate(in,out);
		//writeToServer();
	}
	public Client(String myHost, int port){
		this.myHost= myHost;
		connect(port);
		communicate(in,out);
		//writeToServer();
	}
	public Client(String myHost,String userName, int port){
		this.myHost = myHost;
		this.userName = userName;
		connect(port);
		communicate(in,out);
		//writeToServer();
	}

	//falls über Konsole gestartet
	public static void main(String[] args) {
		String host = args[0].substring(0,args[0].length()-1);
		int port = Integer.parseInt(args[1]);
		Client c = new Client(host,port);
		c.writeToServer();
	}

	/** get all the available Games (GameServers) to list in Dropdown-Menu */
	public ArrayList<String> getAvailableGames(){
		return availableGames;
	}

	/** establish connection (Socket) with Server
	 * set Streams "in" and "out"
	 * default port 9999
	 */
	public void connect(){
		connect(9999);
	}
	/** establish connection (Socket) with Server
	 * set Streams "in" and "out"
	 * @param port given by Server
	 */
	public void connect(int port){
		myPort = port;
		try{
			mySocket = new Socket(myHost,port);
			connected = true;
		} catch (IOException e){
			System.err.println("Konnte nicht mit Server verbinden.");
		}

		try{
			out = new PrintWriter(mySocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		}
		catch(IOException e){
			System.err.println("Error getting In- and OutputStream of Server");
			e.printStackTrace();
		}
	}

	/** Listen to the incoming Messages and handle them
	 * Definition of protocol:
	 * see CommandList.java
	 *
	 * when loosing connection with the server: for now just shut down Client
	 * */
	private void communicate(BufferedReader in, PrintWriter out) {
		Thread communicate = new Thread() {
			//(new Thread(){
			public void run() {

				//Message line;
				String temp;
				ArrayList<Message> line;
				listen:
				while (true) {
					line = null;

					try {
						temp = in.readLine();
						line = Message.parse(temp);
					} catch (SocketException e) {
						System.err.println("Lost connection to Server");
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					//execute Commands in Message Object
					for(Message m:line){
						//System.out.println(m);
						switch (m.getCommand()) {
							case IDENTIFY:
								out.println(new Message(CommandList.IDENTIFYING, userName));
								break;

							case WELCOME:
								display(CommandList.WELCOME, m.getMessage());
//								startPingPong();
								break;

							case PING:
								out.println(new Message(CommandList.PONG));
								//System.out.println("sending pong");
								break;

							case PONG:
								pingPong.pong();
								//System.out.println("receiving pong");
								break;

							case MESSAGE:
								display(m.getCommand(), m.getMessage());
								break;

							case LOBBY_CHAT:
								displayLobbyChat(m.getMessage());
								break;

							case NAME_CHANGED:
								break;

							case AVAILABLE_GAMES:
								String avGames = m.getMessage();
								System.out.println("avGames:"+avGames);
								if(avGames==null|| avGames==""){//sollte null sein wenn nichts mitgeschickt
									availableGames.clear();
								}
								else{
									Pattern pattern = Pattern.compile("(\\w*),");
									Matcher matcher = pattern.matcher(avGames);
									ArrayList<String> gamesTemp = new ArrayList<String>();
									while (matcher.find()) {
										String game = matcher.group(1);
										gamesTemp.add(game);
									}
									availableGames = gamesTemp;
									System.out.println("C: available Games received: "+gamesTemp);
									//TODO: available Games in Dropdown-Menu auflisten, so dass man eiines auswählen und joinen kann
									//Join durch methode "enterGameServer"
									//enterGameServer(availableGames.get(0),true);
									showAvailableGames(availableGames);
								}
								break;

							//GameServer requests the updates from the GameClient
							case GET_GAME:
								Message gotGame = GameController.getGameClient().getChanges();//GameClient.java
								sendGame(gotGame);
								break;

							case GAME_READY:
								System.out.println("You're in CASE GameReady");
								System.out.println(m);
								CommandList playAs = null;
								try {
									playAs = CommandList.valueOf(m.getMessage());

								} catch (IllegalArgumentException e){
									System.err.println("Couldn't parse "+m);
									e.printStackTrace();
									return;
								}
								letsPlayGame(playAs);

								break;

							case CHARACTER:
								//Todo simple test update game
								//gameClient.implementFromServer(m);
								String toParse = m.getMessage();
								sendCharacter(toParse);
								break;

							case GOODBYE:
								logout();
								break listen;//break while-true-loop

							case SHOW_CLIENTS:
								String clients = m.getMessage();
								ArrayList<User> users = User.parseUsers(clients);
								showAvailableClients(users);
								break;

							case TEST:
								sendCharacter(m.getMessage());
								break;

						}
					}
				}
			}
		};
		communicate.start();
	}

	/** send a message to the server
	 * @param message as Message-object*/
	public void writeToServer(Message message){
		out.println(message.toString());

	}
	public void displayLobbyChat(String message){
		System.out.println(message);
	}

	/** Send whisper-messages to the other players in the lobby */
	public void lobbyChat(String in){
		out.println(new Message(CommandList.LOBBY_CHAT,in));
	}

	/** tell the Server, that you want to change your name */
	public void requestNewName(String newName){
		out.println(new Message(CommandList.NEW_NAME,newName).toString());
	}

	/** Der Client sagt dem Server, dass er einen neuen GameServer erstellen soll. */
	public void requestNewGameServer(String gameName){
		out.println(new Message(CommandList.CREATE_GAMESERVER,gameName));
		System.out.println("C requesting Game Server");
		//TODO: am richtigen Ort new GameClient aufrufen** NIls fragen wieso muss game Client hier
//		gameClient = new GameClient(this);
	}

	/** tell the Server that you are leaving */
	public void requestLogout(){
		out.println(CommandList.LOGOUT);
	}

	/** Send all changes (newly placed Characters etc.) to the Server */
	public void sendGame(Message updates){
		out.println(updates);
	}

	/** request to enter a game */
	public void enterGameServer(String game,boolean asMexican){
		out.println(new Message(CommandList.JOIN_GAME,game+"="+asMexican));
	}

	public void startGame(boolean asMexican){
		out.println(new Message(CommandList.GAME_READY,String.valueOf(asMexican)));
	}

	public void enterGameLobby(String gameName){
		out.println(new Message(CommandList.JOIN_LOBBY,String.valueOf(gameName)));
	}

	/** setup the PingPong instance.
	 * Define what needs to happen when not receiving a PING during TIMEOUT */
	private void startPingPong(){
		pingPong = new PingPong(in,out) {
			@Override
			void noPong() {
				display(CommandList.MESSAGE,"Lost connection with Server");
				close();//close while loops in PingPong
				logout();
			}
		};
		pingPong.start();

	}

	/** listen to inputs in console and send them to the server. */
	public void writeToServer(){
		Thread writeToServer = new Thread(){
			//(new Thread(){
			public void run(){
				BufferedReader cons = new BufferedReader(new InputStreamReader(System.in));
				Message message;
				while(true){
					try{
						message = new Message(CommandList.MESSAGE,cons.readLine());
						out.println(message.toString());
					} catch(IOException e){
						System.err.println("Couldn't write to Server");
						e.printStackTrace();
					}
				}
			}
		};
		writeToServer.start();
	}

	/** von ClientController überschrieben durch anonyme Klasse
	 * definiere, wie verschiedene Message-Object dargestellt werden */
	public void display(CommandList command, String msg){
		switch(command) {
			case WELCOME:
				//System.out.println("WELCOMED BY SERVER");
				break;
			case MESSAGE:
				//System.out.println("C: " + msg);
				break;
		}
	}

	/** display the available Games received from the Server*/
	public void showAvailableGames(ArrayList<String> availableGames){
		System.out.println(availableGames);
	}

	/**overridden by the GUI*/
	public void sendCharacter(String name){
		System.out.println("Server created "+name);
	}

//	public void letsPlayGame(CommandList c){
//		System.out.println("start a game");
//	}
	public void letsPlayGame(CommandList commandList){
	System.out.println("start a game");
}

	public void testing(String a){

	}

	/** Close connections.
	 * Needs to stay private. call requestLogout() from outside this class
	 * is triggered from incoming CommandList GOODBYE
	 * */
	private void logout(){
		try {
			pingPong.close();//close while-loops in PingPong
			in.close();
			out.close();
			mySocket.close();
			connected = false;
			System.exit(1);//closes ClientController-Window
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** This function send an update of collected tacos to the server
	 *  @param character we send all information using the character class*/
	public void updateResources(Character character){
		out.println(new Message(CommandList.UPDATE_RES,character.toString()));
	}

	/** This method will be overriden in ClientController
	 *  It will update the ListView in Chats with all available clients and their ids*/
	public void showAvailableClients(ArrayList<User> users){
		System.out.println("Show available Clients");
	}
}