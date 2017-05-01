package ch.unibas.dmi.cs108.sand.gui;

import ch.unibas.dmi.cs108.sand.logic.Character;
import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.Message;

/**
 * Created by Syarif on 4/1/2017.
 */
public class GameControllerMexican extends GameController{
	int characterCounter = 0;

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


	public void buttonRight11(){
		//Todo Test send a mexican field
		ClientController.getClient().testing("mexican,1");
		newMexicanOnLane(1);
	}

	public void buttonRight12(){
		ClientController.getClient().testing("mexican,2");
		newMexicanOnLane(2);
	}
	public void buttonRight13(){
		ClientController.getClient().testing("mexican,3");
		newMexicanOnLane(3);
	}
	public void buttonRight14(){
		ClientController.getClient().testing("mexican,4");
		newMexicanOnLane(4);
	}
	public void buttonRight15(){
		ClientController.getClient().testing("mexican,5");
		newMexicanOnLane(5);
	}
	public void buttonRight16(){
		ClientController.getClient().testing("mexican,6");
		newMexicanOnLane(6);
	}

	private void newMexicanOnLane(int lane){
		characterCounter++;
		Character c = new Character(characterCounter, CommandList.MEXICAN,lane);
		String newMexican = c.toString();
		//String newMexican = "id:"+characterCounter+",type:"+CommandList.MEXICAN+",xPos:900,lane:"+lane+",health:10,damage:10;";
		Message send = new Message(CommandList.CHARACTER,newMexican);
		ClientController.getClient().sendGame(send);
		System.out.println("c: sending "+newMexican);
	}

}
