package ch.unibas.dmi.cs108.sand.gui;

import ch.unibas.dmi.cs108.sand.logic.Character;
import ch.unibas.dmi.cs108.sand.logic.GameClient;
import ch.unibas.dmi.cs108.sand.logic.Mexican;
import ch.unibas.dmi.cs108.sand.logic.Trump;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Arkad on 4/18/2017.
 */
public class GameController {

    @FXML
    private Pane pane;
    @FXML
    private TextFlow money;

    private int tacosCollected=20;
    private Random random = new Random();
    private static GameClient gameClient = new GameClient();

    private Rectangle wall = new Rectangle(500,50,75,75);

    /** Update Games */
    void setPos(String name) {
        String delims = "[,]";
        String[] values = name.split(delims);
        String character = values[0];
        int lane = Integer.valueOf(values[1]);


       /* ImageView imageView = gameClient.renderImage(character,lane);
        Platform.runLater(
                () -> {
                    pane.getChildren().add(imageView);
                }
        );*/
    }

    void createWall(){
        Image mauerImg = new Image(getClass().getResourceAsStream("/img/wall.png"));
        ImageView mauer = new ImageView(mauerImg);
        mauer.setX(700);
        mauer.setY(70);
        mauer.setFitWidth(75);
        mauer.setFitHeight(75);
        pane.getChildren().add(mauer);
    }

    void setTacos(){
        Image tacosImg = new Image(getClass().getResourceAsStream("/img/tacos.png"));
        Timeline tacos = new Timeline(new KeyFrame(Duration.seconds(20), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                ImageView imageView = new ImageView(tacosImg);
                imageView.setX(400);
                imageView.setY(50);
                TranslateTransition transition = new TranslateTransition();

                imageView.setOnMouseClicked(e ->{
                    System.out.println("Tacos clicked");

                    /* remove node from scene */
                    tacosCollected += 10;
                    Text text = new Text("Collected Tacos \n" + String.valueOf(tacosCollected));
                    text.setStyle("-fx-font-size: 14; -fx-fill: #cb5217;");
                    money.getChildren().setAll(text);
                    pane.getChildren().remove(imageView);
                    transition.stop();
                    if(ClientController.isMexican()){
                        ClientController.getClient().updateResources(new Character("mexican",10));
                    }else{
                        ClientController.getClient().updateResources(new Character("trump",10));
                    }


                });
                transition.setDuration(Duration.seconds(5));
                transition.setNode(imageView);
                transition.setToX(random.nextInt(400));
                transition.setToY(random.nextInt(400));
                transition.setAutoReverse(true);
                transition.setCycleCount(5);
                transition.play();
                pane.getChildren().add(imageView);
                System.out.println("this is called every 30 seconds on UI thread");
            }
        }));
        tacos.setCycleCount(Timeline.INDEFINITE);
        tacos.play();
    }

    void initStartTacos(){
        Text text = new Text("Collected Tacos \n" + String.valueOf(tacosCollected));
        text.setStyle("-fx-font-size: 14; -fx-fill: #cb5217;");
        money.getChildren().add(text);
    }

    /** Check if all trumps collide with the first Mexican */
    void checkCollision(int lane){
        final BlockingQueue<Trump> trumps;
        final BlockingQueue<Mexican> mexicans;
        switch (lane){
            case 1:
                trumps = gameClient.getTrumps1();
                mexicans = gameClient.getMexicans1();
                break;
            case 2:
                trumps = gameClient.getTrumps2();
                mexicans = gameClient.getMexicans2();
                break;
            case 3:
                trumps = gameClient.getTrumps3();
                mexicans = gameClient.getMexicans3();
                break;
            case 4:
                trumps = gameClient.getTrumps4();
                mexicans = gameClient.getMexicans4();
                break;
            case 5:
                trumps = gameClient.getTrumps5();
                mexicans = gameClient.getMexicans5();
                break;
            case 6:
                trumps = gameClient.getTrumps6();
                mexicans = gameClient.getMexicans6();
                break;
            default:
                trumps = null;
                mexicans = null;
                break;
        }
        Timeline trump = new Timeline(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(trumps.size()!= 0){

                    for(Trump trump:trumps){
//                        gameClient.updatePosition(trump,null);

                        trump.getTransition().setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                ImageView imageView = trump.getImageView();
                                Platform.runLater(
                                        () -> {
                                            pane.getChildren().remove(imageView);

                                        }
                                );
                                try {
                                    Trump toDelete = trumps.take();
                                    toDelete = null;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        if(mexicans.size()!=0){
                            Mexican firstMexican = mexicans.peek();
                            if(gameClient.isCollision(trump,null,firstMexican.getImageView().getBoundsInParent())) {
                                trump.getTransition().pause();
                                int newHealth = trump.getHealth()-firstMexican.getDamage();
                                trump.setHealth(newHealth);
                                if(trump.getHealth()<= 0 ){
                                    // put platform runlater
                                    ImageView imageView = trump.getImageView();
                                    Platform.runLater(
                                            () -> {
                                                pane.getChildren().remove(imageView);

                                            }
                                    );
                                    try {
                                        Trump toDelete = trumps.take();
                                        toDelete = null;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }else{
                                trump.getTransition().play();

                            }
                        }else {
                            trump.getTransition().play();
                        }

                    }
                }

                if(mexicans.size()!= 0){
                    for(Mexican mexican:mexicans){
//                        gameClient.updatePosition(null,mexican);
                        // set onFinished action
                        mexican.getTransition().setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                ImageView imageView = mexican.getImageView();
                                Platform.runLater(
                                        () -> {
                                            pane.getChildren().remove(imageView);

                                        }
                                );
                                try {
                                    Mexican toDelete = mexicans.take();
                                    toDelete = null;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        if(trumps.size()!=0){
                            Trump firstTrump = trumps.peek();
                            if(gameClient.isCollision(null,mexican,firstTrump.getImageView().getBoundsInParent())) {
                                mexican.getTransition().pause();
                                int newHealth = mexican.getHealth()-firstTrump.getDamage();
                                mexican.setHealth(newHealth);

                                if(mexican.getHealth()<= 0){
                                    ImageView imageView = mexican.getImageView();
                                    Platform.runLater(
                                            () -> {
                                                pane.getChildren().remove(imageView);

                                            }
                                    );
                                    try {
                                        Mexican toDelete = mexicans.take();
                                        toDelete = null;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                mexican.getTransition().play();
                            }
                        }else{
                            mexican.getTransition().play();
                        }
                    }
                }
            }
        }));
        trump.setCycleCount(Timeline.INDEFINITE);
        trump.play();
    }



    public static GameClient getGameClient() {
        return gameClient;
    }

    public Rectangle getWall() {
        return wall;
    }


    public Pane getPane() {
        return pane;
    }


}
