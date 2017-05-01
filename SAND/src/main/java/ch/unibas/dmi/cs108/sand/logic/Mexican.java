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
public class Mexican extends Character {
    private boolean isAttacked = false;

    public Mexican(ImageView imageView, TranslateTransition transition){
        super("Sombrero",100,1,5,10,imageView,transition);
    }

    /** default constructor for gameServer
     * without saving ImageView and Transition*/

    public Mexican(int id,CommandList type,int lane,double xPos,int health,int damage){
        super(id,type,lane,xPos,health,damage);
    }
    /** Set attacked based on character destruction power */
    public Timeline attacked(Character character){
        isAttacked = true;
        return new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                setHealth(getHealth()-character.getDamage());
                System.out.println("Mexican " + getHealth());
            }
        }));
    }

    public boolean isAttacked() {
        return isAttacked;
    }
}
