package ch.unibas.dmi.cs108.sand.gui;

import ch.unibas.dmi.cs108.sand.logic.Character;
import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.Message;

/**
 * Created by Syarif on 4/1/2017.
 */
public class GameControllerTrump extends GameController{
	private int characterCounter = 0;


	public void initialize(){
		initStartTacos();
		createWall();
		setTacos();

		checkCollision(1);
		checkCollision(2);
		checkCollision(3);
		checkCollision(4);
		checkCollision(5);
		checkCollision(6);
	}

	public void buttonLeft11(){
		//Todo Test send a trump in mexican field
		ClientController.getClient().testing("trump,1");
		newTrumpOnLane(1);
	}

	public void buttonLeft12(){
		ClientController.getClient().testing("trump,2");
		newTrumpOnLane(2);
	}
	public void buttonLeft13(){
		ClientController.getClient().testing("trump,3");
		newTrumpOnLane(3);
	}
	public void buttonLeft14(){
		ClientController.getClient().testing("trump,4");
		newTrumpOnLane(4);
	}
	public void buttonLeft15(){
		ClientController.getClient().testing("trump,5");
		newTrumpOnLane(5);
	}
	public void buttonLeft16(){
		ClientController.getClient().testing("trump,6");
		newTrumpOnLane(6);
	}

	private void newTrumpOnLane(int lane){
		characterCounter++;
		Character c  = new Character(characterCounter, CommandList.TRUMP,lane);
		String newTrump = c.toString();
		//String newTrump = "id:"+characterCounter+",type:"+CommandList.TRUMP+",xPos:200,lane:"+lane+",health:10,damage:10;";
		Message send = new Message(CommandList.CHARACTER,newTrump);
		ClientController.getClient().sendGame(send);
		System.out.println("c: sending "+newTrump);
	}
}
