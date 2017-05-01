package ch.unibas.dmi.cs108.sand.network;

import java.io.BufferedReader;
import java.io.PrintWriter;

/** Both, Server and Client initialize PingPong to keep checking that the connection wasn't lost */
public abstract class PingPong extends Thread{
	private BufferedReader in;
	private PrintWriter out;
	private boolean waitingForPong = false;
	private final int TIMEOUT = 5000;
	boolean running=true;

	public PingPong(BufferedReader in, PrintWriter out){
		this.in = in;
		this.out = out;
	}

	/** in while-true-loops:
	 * send a PING every 5 sec (TIMEOUT) and
	 * keep cheking if receiving pong
	 */
	public void run() {
		Thread a = new Thread(this::pingPong);
		a.start();
	}

	/** check every 5 sec (TIMEOUT) if I have to either send a PING or if I should have received a PONG */
	private void pingPong(){
		try {
			while (running) {
				if (!waitingForPong) {
					out.println(new Message(CommandList.PING).toString());
					//System.out.println("sending ping");
					waitingForPong = true;
				} else if (waitingForPong) {
					noPong();
				}
				sleep(TIMEOUT);
			}
		} catch (InterruptedException e){
			System.err.println("PingPong interrupted");
			noPong();
		}
	}

	/** When the Server/Clients receives a PONG, he needs to call this function */
	public void pong(){
		waitingForPong = false;
	}

	/** define (in anonymous class) what happens when connection is lost */
	abstract void noPong();

	/** to stop the while-loops => stopping PingPong when Client leaves */
	public void close(){
		running = false;
	}
}