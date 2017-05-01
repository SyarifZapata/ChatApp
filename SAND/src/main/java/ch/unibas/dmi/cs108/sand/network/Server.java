package ch.unibas.dmi.cs108.sand.network;
import ch.unibas.dmi.cs108.sand.gui.LoginController;
import ch.unibas.dmi.cs108.sand.logic.GameServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public class Server{
	private ServerSocket server = null;
	private ServerHandler serverHandler = null;

	static HashMap<Integer,String> clientNames = new HashMap<>();
	static HashMap<Integer,PrintWriter> broadcast = new HashMap<Integer,PrintWriter>();
	static HashSet<GameServer> games= new HashSet<GameServer>();
	static int gameCounter = 0;
	private int id;


	/** call other constructor with default values */
	public Server(){
		this(9999);
	}

	/** create ServerSocket
	 * listening-loop needs to be started separately by start()
	 * @param port what port to listen on*/
	public Server(int port){
		try {
			server = new ServerSocket(port);
		} catch(IOException e){
			System.err.println("Couldn't open Server");
			e.printStackTrace();
		}finally {
			/*try{
				server.close();
			} catch(IOException f){
				System.out.println(f);
			}*/
		}
	}

	/** Start Server
	 * (outside Constructor so that reference in ServerHandler works) */
	public void start(){
		//(new Thread(){
		//public void run(){
		while(true){
			try {
				id++;
				serverHandler = new ServerHandler(server.accept(),this,id);//hört auf Clients, die sich am Port andocken.
				serverHandler.start();
			} catch (IOException e) {
				System.err.println("Couldn't add Client to Server");
				e.printStackTrace();
			}
		}
		//}
		//}).start();
	}
	public static void main(String[] args) {
		new Server(Integer.parseInt(args[0]));
	}

	/** Für den ServerController. Messages sollen im Window des Servers gezeigt werden. */
	public void display(String in){
		System.out.println(in);
	}

	/* fuer den Milestone3 */
	public HashSet<GameServer> getOpenGames(){
		HashSet<GameServer> openGames = new HashSet<GameServer>();
		for(GameServer g:games) {
			if(!g.isFull()){
				openGames.add(g);
			}
		}
		return openGames;
	}
	public HashSet<GameServer> getOngoingGames(){
		HashSet<GameServer> ongoingGames = new HashSet<GameServer>();
		for(GameServer g:games) {
			if(g.isOngoing()){
				ongoingGames.add(g);
			}
		}
		return ongoingGames;
	}
	public HashSet<GameServer> getFinishedGames(){
		HashSet<GameServer> finishedGames = new HashSet<GameServer>();
		for(GameServer g:games) {
			if(g.isFinished()){
				finishedGames.add(g);
			}
		}
		return finishedGames;
	}

	/** write Informations (like how long the game was played) persistently in a file*/
	private void writeStatistics(){
		long timeNow = System.currentTimeMillis( );
		String finishedGames = "";
		BufferedWriter bw=null;
		FileWriter fw=null;
		//final String FILENAME = "src/main/resources/stats.txt";//stat file to save permanently
		final URL stats = LoginController.class.getResource("/stats.txt");
		File statFile = null;
		try {
			statFile = new File(stats.toURI());
		} catch (URISyntaxException e) {
			System.err.println("File 'stats.txt' not found");
		}

		for(GameServer g:games){
			long duration = timeNow-g.getStartTime();
			duration = duration / 60/60;//secs
			finishedGames += "game "+g.getGameName()+" was running for "+duration+" seconds\n";
		}
		try{
			fw = new FileWriter(statFile,true);//true damit appenden
			//fw = new FileWriter(FILENAME,true);//true damit appenden
			bw = new BufferedWriter(fw);
			bw.write(finishedGames);
		} catch (IOException e){
			System.err.println("Error writing Statistics file");
			e.printStackTrace();
		}finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException f) {
				f.printStackTrace();
			}
		}
	}


	/** Close Server
	 * Closes all connected Clients (with GOODBYE)
	 * closes Server Streams etc. */
	public void logout(){
		long timeNow = System.currentTimeMillis( );
		String finishedGames = "";
		BufferedWriter bw=null;
		FileWriter fw=null;
		final String FILENAME = "src/main/resources/stats.txt";//stat file to save permanently

		for(GameServer g:games){
			long duration = timeNow-g.getStartTime();
			duration = duration / 60/60;//secs
			finishedGames += "game "+g.getGameName()+" was running for "+duration+" seconds\n";
		}
		try {
			fw = new FileWriter(FILENAME,true);//true damit appenden
			bw = new BufferedWriter(fw);
			bw.write(finishedGames);

			//TODO: for now: every Client shuts down when server is manually closed. This will probably not be meaningful in the future.
			for (PrintWriter myClient : broadcast.values()) {
				//tell every Client to logout
				myClient.println(CommandList.GOODBYE);
			}
			broadcast.clear();
			if(serverHandler!=null){serverHandler.close();}
			if(server!=null){server.close();}
			//System.exit(1);
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}finally {
			try {
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}