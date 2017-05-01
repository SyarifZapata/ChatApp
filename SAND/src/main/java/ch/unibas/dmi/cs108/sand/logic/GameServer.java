package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.ServerHandler;
import ch.unibas.dmi.cs108.sand.network.Message;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer extends Thread{

	//static HashSet<String> games = new HashSet<String>();
	//list Games. Games contain connected Players/Clients.
	//ServerHandler chatServer;
	private BlockingQueue<Mexican> mexicans1 = new LinkedBlockingQueue<>();
	private BlockingQueue<Mexican> mexicans2 = new LinkedBlockingQueue<>();
	private BlockingQueue<Mexican> mexicans3 = new LinkedBlockingQueue<>();
	private BlockingQueue<Mexican> mexicans4 = new LinkedBlockingQueue<>();
	private BlockingQueue<Mexican> mexicans5 = new LinkedBlockingQueue<>();
	private BlockingQueue<Mexican> mexicans6 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps1 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps2 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps3 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps4 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps5 = new LinkedBlockingQueue<>();
	private BlockingQueue<Trump> trumps6 = new LinkedBlockingQueue<>();

	private ServerHandler chatServer;
	private final int TIMEOUT = 1000;
	private String gameName;
	private boolean isFull = false;
	private boolean ongoing = false;
	private boolean finished = false;
	private PrintWriter mexican = null;
	private PrintWriter trump = null;
	private PrintWriter player1;
	private PrintWriter player2;
	private long startTime;
	private int resourceCountTrump = 20;
	private int resourceCountMexican =20;
	static ConcurrentHashMap<Integer,Lane> lanes = new ConcurrentHashMap<>();
	private final int NUMBER_OF_LANES = 6;
	//static ConcurrentHashMap<Integer,Character> mexicans = new ConcurrentHashMap<>();
	//static ConcurrentHashMap<Integer,Character> trumps = new ConcurrentHashMap<>();

	//array von 6 lanes für je Trumps und Mexicans

	//static ConcurrentLinkedQueue<Character>[] laneMex = new ConcurrentLinkedQueue[6];
	//static ConcurrentLinkedQueue<Character>[] laneTrump = new ConcurrentLinkedQueue[6];

	/** GameServer braucht das in und out vom ChatServer */
	public GameServer(ServerHandler chatServer, String gameName){
		this.chatServer = chatServer;
		this.gameName = gameName;
		startTime = System.currentTimeMillis();
		//create 6 new lanes
		for(int i = 1;i<=NUMBER_OF_LANES;i++){
			Lane lane = new Lane(i);
			lanes.put(i,lane);
		}
	}

	public void run() {
		//Todo wieso gibt hier nichts?
	}


	public long getStartTime(){
		return startTime;
	}

	private void startGame(){
		Thread a = new Thread(this::requesting);//rufe listen() funktion auf als Thread (hat eine while-true-Schleife)
		//Todo wieder starten irgendwann
		//a.start();
	}

	public String getGameName(){
		return gameName;
	}
	/** returns if all the slots of the GameServer are already taken */
	public boolean isFull(){
		return isFull;
	}
	public boolean isOngoing(){
		return ongoing;
	}
	public boolean isFinished(){
		return finished;
	}

	/** a client can join a Game here
	 * If I want to play as Mexican, but the other player already joined as Mexican, I will play as Trump
	 * @param playAsMexican true if playing as Mexican, false if playing as Trump*/
	public void join(PrintWriter out, boolean playAsMexican){
		if(playAsMexican){
			if(mexican==null){
				mexican = out;
				System.out.println("Client joined as mex");
			} else{
				trump = out;
				System.out.println("Client joined as trump");
			}

		} else if(!playAsMexican){
			if(trump==null){
				trump = out;
				System.out.println("Client joined as trump");
			} else{
				mexican = out;
				System.out.println("Client joined as mex");
			}
		}
		if(mexican !=null && trump!=null){
			//Todo Wieder Aktivieren irgendwan
			mexican.println(new Message(CommandList.GAME_READY,"MEXICAN"));
			trump.println(new Message(CommandList.GAME_READY,"TRUMP"));
			isFull = true;
			ongoing = true;
			//Todo startGame() wieder auskomentieren
//			startGame();
		}
	}

	/** Enter Game Lobby
	 * @param player whoever enter the game lobby first, will be the player1*/
	public void enterGameLobby(PrintWriter player){
		if(player1 == null && player2 == null){
			player1 = player;
		}else if(player1 != null){
			player2 = player;
		}
	}

	/** Fordere Client alle Sekunden auf, ein Update zu schicken. */
	private void requesting(){
		while(ongoing){

			//chatServer.requestGetGame();
			/*
			mexican.println(new Message(CommandList.GET_GAME));
			trump.println(new Message(CommandList.GET_GAME));*/
			//update();//TODO run update with saved Maps -> make Characters move
			try {
				sleep(TIMEOUT);
			} catch (InterruptedException e) {
				System.err.println("Error: GameServer-Thread was interrupted");
				e.printStackTrace();
			}
		}
	}

	/** accepts the updates on the game from a Client and will send calculated updates to all Clients */
	public void update(Message in){
		if(in.getMessage().equals("")||in.getMessage()==null){
			return;
		}

		Character newCharacter = Character.parse(in.getMessage());
		if(newCharacter==null){
			return;
		}
		//else
		System.out.println("GServer receives "+newCharacter.toString());

		int lane = newCharacter.getLane();
		ArrayList<Message> updatedList = new ArrayList<Message>();
		if(newCharacter.getType()== CommandList.MEXICAN){
			lanes.get(lane).addMex(newCharacter);
		}
		else if(newCharacter.getType()==CommandList.TRUMP){
			lanes.get(lane).addTrump(newCharacter);
		}

		for(Map.Entry<Integer,Lane> entry : lanes.entrySet()) {
			//int key = entry.getKey();
			Lane l = entry.getValue();
			updatedList.addAll(l.move());
			//l.collision()...
		}

		broadcastUpdates(updatedList);
	}

	/** send updated game to the players */
	public void broadcastUpdates(ArrayList<Message> update){
		System.out.println("broadcasting"+update);
		if(mexican!=null){
			for(Message m:update){
				mexican.println(m);
			}
		}

		if(trump!=null){
			for(Message m:update){
				trump.println(m);
			}
		}
	}

	/** Whisper-Chat just within the GameServer */
	public void lobbyChat(String clientName, Message in){
		player1.println(new Message(CommandList.LOBBY_CHAT,clientName+": "+in.getMessage()));
		player2.println(new Message(CommandList.LOBBY_CHAT,clientName+": "+in.getMessage()));
	}

	/*public void whisper(String clientName, Message in){
		in.setMessage(clientName+": "+in.getMessage());
		mexican.println(in);
		trump.println(in);
	}*/

	public void startPlayingGame(boolean asMexican){

	}

	//todo just for testing purpose
	public void testing(Message in){
		String delims = "[,]";
		String[] values = in.getMessage().split(delims);
		String character = values[0];
		int lane = Integer.valueOf(values[1]);

		double posX = 0;
		double posY = 0;

		if(character.equals("trump")) {
			Trump trump = null;
			switch (lane) {
				case (1):
					posX = 200;
					posY = 40;

					break;
				case (2):
					posX = 200;
					posY = 150;

					break;
				case (3):
					posX = 200;
					posY = 260;

					break;
				case (4):
					posX = 200;
					posY = 370;

					break;
				case (5):
					posX = 200;
					posY = 480;

					break;
				case (6):
					posX = 200;
					posY = 590;

					break;
			}

		}else if(character.equals("mexican")) {
			Mexican mexican = null;
			switch (lane) {
				case (1):
					posX = 900;
					posY = 40;

					break;
				case (2):
					posX = 900;
					posY = 150;

					break;
				case (3):
					posX = 900;
					posY = 260;

					break;
				case (4):
					posX = 900;
					posY = 370;

					break;
				case (5):
					posX = 900;
					posY = 480;

					break;
				case (6):
					posX = 900;
					posY = 590;

					break;
			}
		}

		mexican.println(new Message(CommandList.TEST,in.getMessage()));
		trump.println(new Message(CommandList.TEST,in.getMessage()));
	}

	/** increment resources of player 1 or 2
	 * @param name to identify who are updating */
	public void updateResource(String name, int resourceValue){
		if(name.equals("trump")){
			resourceCountTrump += resourceValue;
			System.out.println("Trump has collected Tacos, his resources is updated to "+ resourceCountTrump);
		}
		if(name.equals("mexican")){
			resourceCountMexican += resourceValue;
			System.out.println("Mexican has collected Tacos, his resources is updated to "+ resourceCountMexican);
		}
	}

	public int getResourceCountTrump() {
		return resourceCountTrump;
	}

	public int getResourceCountMexican() {
		return resourceCountMexican;
	}

	public void updatePosition() {
		//todo
	}
}