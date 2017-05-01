package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Created by Syarif on 4/7/2017.
 */
public class Trump extends Character {
    private boolean isAttacked=false;

    public Trump(ImageView imageView, TranslateTransition transition){
        //TODO call constructor with correct id. When is this constructor use?
        super(123, CommandList.TRUMP,10,10,5,2,imageView,transition);
        //super("Trump",20,3,20,100,imageView,transition);
    }
    public Trump(int id,CommandList type,int lane,double xPos,int health,int damage){
        super(id,type,lane,xPos,health,damage);
        //super("Trump",120,3,20,100,imageView,transition);
    }



    /** Set attacked based on character destruction power */

    public Timeline attacked(Character character){
        isAttacked = true;
        return new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                setHealth(getHealth()- character.getDamage());
                System.out.println("trump health: "+getHealth());
            }
        }));
    }
}
