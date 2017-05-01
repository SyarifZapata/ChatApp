package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class Lane {
	private int index = -1;


	ConcurrentSkipListMap<Integer,Character> mexicans = new ConcurrentSkipListMap<Integer,Character>();
	ConcurrentSkipListMap<Integer,Character> trumps = new ConcurrentSkipListMap<Integer,Character>();
	int mexCounter = 0;
	int trumpCounter = 0;

	/** Lane starting at 1 (to 6) */
	public Lane(int index){
		this.index = index;
	}

	public void addMex(Character m){
		mexCounter++;
		mexicans.put(mexCounter,m);
	}
	public void addTrump(Character t){
		trumpCounter++;
		trumps.put(trumpCounter,t);
	}

	public Character getFirstTrump(){
		return trumps.firstEntry().getValue();
	}
	public Character getFirstMex(){
		return mexicans.firstEntry().getValue();
	}

	public ArrayList<Message> move(){
		ArrayList<Message> updated = new ArrayList<Message>();
		for(Map.Entry<Integer,Character> entry : mexicans.entrySet()) {
			//int key = entry.getKey();
			Character val = entry.getValue();
			val.setXPos(val.getXPos()-10);
			updated.add(new Message(CommandList.CHARACTER,val.toString()));
		}
		for(Map.Entry<Integer,Character> entry : trumps.entrySet()) {
			//int key = entry.getKey();
			Character val = entry.getValue();
			val.setXPos(val.getXPos()+10);
			updated.add(new Message(CommandList.CHARACTER,val.toString()));
		}
		return updated;
	}

	public void update(){

	}


	//TODO: Collision detection
	//getFirstMex, getFirstTrump..
}
