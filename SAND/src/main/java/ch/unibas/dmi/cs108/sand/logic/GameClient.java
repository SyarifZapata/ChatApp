package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.gui.ClientController;
import ch.unibas.dmi.cs108.sand.gui.SpriteAnimation;
import ch.unibas.dmi.cs108.sand.network.Client;
import ch.unibas.dmi.cs108.sand.network.CommandList;
import ch.unibas.dmi.cs108.sand.network.Message;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class GameClient {

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
	private Client client;
	static ConcurrentHashMap<Integer,Character> mexicans = new ConcurrentHashMap<>();
	static ConcurrentHashMap<Integer,Character> trumps = new ConcurrentHashMap<>();
	//static Map<Integer,Character> mexicans = new HashMap();
	//static Map<Integer,Character> trumps = new HashMap();
	private Mexican[] mexSelectionBar;        // Einheitenauswahl
	private Trump[] trumpSelectionBar;        // Einheitenauswahl
	private String selectedUnit;
	private String lastChanges;                // Änderungen, die noch nicht an den Server geschickt wurden
	private int burritoCount;
	//Random random = new Random();

	private Random random = new Random();
	private Image trumpImg = new Image(getClass().getResourceAsStream("/Sprite/TrumpSprite.png"));
	private Image mexicanImg = new Image(getClass().getResourceAsStream("/Sprite/SpriteMexican1.png"));



	/**
	 * the GameServer gets the Changes made by the Client here
	 */
	public Message getChanges() {
		//todo get updates from GameController
		//return new Message(CommandList.CHARACTER, "name:Mex,xPos:2.3,lane:2,health:20,damage:10;");
		return new Message(CommandList.CHARACTER);
	}


	public void newItem(String character){
		//TODO Syarif: wenn neues Objekt: newItem aufrufen
		//ArrayList<Character> c = Character.parse(character);
		Message d = new Message(CommandList.CHARACTER,character);
		ClientController.getClient().sendGame(d);
	}

	public BlockingQueue<Mexican> getMexicans1() {
		return mexicans1;
	}

	public BlockingQueue<Mexican> getMexicans2() {
		return mexicans2;
	}

	public BlockingQueue<Mexican> getMexicans3() {
		return mexicans3;
	}

	public BlockingQueue<Mexican> getMexicans4() {
		return mexicans4;
	}

	public BlockingQueue<Mexican> getMexicans5() {
		return mexicans5;
	}
	public static Map<Integer,Character> getMexicans() {
		return mexicans;
	}

	public BlockingQueue<Mexican> getMexicans6() {
		return mexicans6;
	}
	public static Map<Integer,Character> getTrumps() {
		return trumps;
	}

	public BlockingQueue<Trump> getTrumps1() {
		return trumps1;
	}

	public BlockingQueue<Trump> getTrumps2() {
		return trumps2;
	}

	public BlockingQueue<Trump> getTrumps3() {
		return trumps3;
	}

	public BlockingQueue<Trump> getTrumps4() {
		return trumps4;
	}

	public BlockingQueue<Trump> getTrumps5() {
		return trumps5;
	}

	public BlockingQueue<Trump> getTrumps6() {
		return trumps6;
	}

	/** Creating an ImageView
	 * @return imageView */

	public ImageView renderImage(int id, CommandList type, int lane){
		Image image = null;
		int width2D=0;
		int height2D=0;
		int column=0;
		double width =0;
		double height = 0;
		double direction = 0;
		double posX=0;
		double posY=0;

		if(type == CommandList.TRUMP){


			switch (lane){
				case(1):
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
			System.out.println("new trump's created");
			image = trumpImg;
			width2D = 629;
			height2D =500;
			//direction = 700;
			//direction = posX-0;
			direction = -10;
			column = 3;
			width = 130;
			height = 100;
		}else if(type == CommandList.MEXICAN){
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
			System.out.println("new mexican's created");
			image = mexicanImg;
			width2D =550;
			height2D = 762;
			//direction = -700;
			//direction = posX+0;
			direction = 10;
			column = 7;
			width = 100;
			height=100;
		}
		ImageView imageView = new ImageView(image);
		imageView.setViewport(new Rectangle2D(0, 0, width2D, height2D));
		Animation animation = new SpriteAnimation(imageView, Duration.millis(900), 20, column,0,0,width2D,height2D);
		animation.setCycleCount(Animation.INDEFINITE);
		animation.play();
		imageView.setFitHeight(height);
		imageView.setFitWidth(width);
		imageView.setX(posX);
		imageView.setY(posY);

		// to boost performance
		imageView.setCache(true);
		imageView.setCacheHint(CacheHint.SPEED);


		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.minutes(0.40));
		transition.setNode(imageView);
		transition.setToX(direction);
		if(type == CommandList.TRUMP){
			Trump trump = new Trump(imageView,transition);
			switch (lane){
				case 1:
					trumps1.add(trump);
					break;
				case 2:
					trumps2.add(trump);
					break;
				case 3:
					trumps3.add(trump);
					break;
				case 4:
					trumps4.add(trump);
					break;
				case 5:
					trumps5.add(trump);
					break;
				case 6:
					trumps6.add(trump);
					break;
			}
			trump.getTransition().play();
		}else if(type == CommandList.MEXICAN){
			Mexican mexican = new Mexican(imageView,transition);
			switch (lane){
				case 1:
					mexicans1.add(mexican);
					break;
				case 2:
					mexicans2.add(mexican);
					break;
				case 3:
					mexicans3.add(mexican);
					break;
				case 4:
					mexicans4.add(mexican);
					break;
				case 5:
					mexicans5.add(mexican);
					break;
				case 6:
					mexicans6.add(mexican);
					break;
			}
			mexican.getTransition().play();
		}
	return imageView;
	}

	public void updatePosition(Trump trump, Mexican mexican){
		if(trump != null){
				double position = trump.getImageView().getTranslateX()+200;
				trump.setXPos(position);

		}else if(mexican !=null){
				double position = mexican.getImageView().getTranslateX()+900;
				mexican.setXPos(position);
		}
	}

	public boolean isCollision(Trump trump, Mexican mexican, Bounds bounds){
		boolean collision = false;
		if(trump != null){
				collision = trump.getImageView().getBoundsInParent().intersects(bounds);
		}else if(mexican != null){
				collision=mexican.getImageView().getBoundsInParent().intersects(bounds);
		}

		return collision;
	}





}