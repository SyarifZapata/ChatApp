package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adalsteinn on 4/1/2017.
 */
public class Character implements Comparable<Character>{
    private int id;
    private CommandList type;
	private String name;
	//private String type;
    private int health;
    private int damage;
    private int speed;
    private int cost;
    private double xPosition;
    private double yPosition;
    private int lane;
    private ImageView imageView;
    private TranslateTransition transition;
    private int resourceValue;

    /**Default Constructor*/
    public Character(){

    }

    public Character(int x,int y){

    }

    public Character(String type, int health, int damage, int speed, int cost, ImageView imageView, TranslateTransition trainsition){

    }

	/** Constructor for action with default value such as
	 * updating resources (Tacos)*/
	public Character(String name, int resourceValue){
		this.name = name;
		this.resourceValue = resourceValue;
	}

    /** je nach typ: setze verschiedene Werte für health/damage/xPosition etc. */
    public Character(int id, CommandList type, int lane){
        this.id = id;
        this.type = type;
        this.lane = lane;
        switch (type){
            case MEXICAN:
                this.xPosition = 900;
                this.health = 10;
                this.damage = 10;
                break;
            case TRUMP:
                this.xPosition = 200;
                this.health = 10;
                this.damage = 10;
                break;
            default:
                //TODO neues Enum für Character types
                throw new IllegalArgumentException("Character type needs to be MEXICAN or TRUMP");
        }
    }

    public Character(int id, CommandList type, int health, int damage, int speed, int cost, ImageView imageView, TranslateTransition transition) {
        this.id = id;
        this.type = type;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.cost = cost;
        this.imageView = imageView;
        this.transition = transition;
    }

    //TODO imaegview und transition?
    /** Instantiate Character with its attributes
     * @param id unique id from a counter
     * @param type what kind of Character, e.g. MEXICAN or TRUMP
     * @param lane which lane, starting at 1
     * */
    public Character(int id, CommandList type,int lane,double xPos,int health,int damage){
        this.id = id;
        this.type = type;
        this.lane=lane;
        this.xPosition = xPos;
        this.health = health;
        this.damage = damage;
        this.resourceValue = resourceValue;
    }

    /** Enables sorting a List (Map) of Characters. Sorting by xPosition
     */
    @Override
    public int compareTo(Character a){
        return this.xPosition > a.xPosition ? 1 : (this.xPosition < a.xPosition ? -1 : 0);
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return id;
    }

    public void setType(CommandList type){
        this.type = type;
    }
    public CommandList getType(){
        return type;
    }
    //public void setType(String type){this.type= type;}

    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getXPos() {
        return xPosition;
    }
    public void setXPos(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPos() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    /** Surrounding Rectangle to detect collision */
    public Rectangle getBounds(){
        return new Rectangle(getXPos(), getYPos(),75,75);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setLane(int lane){
        this.lane = lane;
    }
    public int getLane() {
        return lane;
    }

    public void setResourceValue(int resourceValue){
    	this.resourceValue = resourceValue;
	}

    public TranslateTransition getTransition(){
        return transition;
    }

    /** Serialize a Character to a String
     * @return serialized Character
     * */
    public int getResourceValue() {
        return resourceValue;
    }

    public String toString(){
        //String out = "{";
        String out = "id:"+id+",";
        out += "type:"+type+",";
        out += "xPos:"+xPosition+",";
        out += "lane:"+lane+",";
        out += "health:"+health+",";
        out += "damage:"+damage+",";
        out += "resourceValue:"+resourceValue;
        //out += "}";
        //out += ";";
        return out;
    }

    /** parse a String to a Character.
     * @return Exported Character
     */
    public static Character parse(String in){
        //TODO exception handling
        if(in=="" || in==null){
            return null;
        }
        String data[] = in.split(",");
        Character c = new Character();
        for (String i : data) {
            String j[] = i.split(":");
            String key = j[0];
            String val = j[1];
            switch (key) {
                case "id":
                    c.setId(Integer.parseInt(val));
                    break;
                case "type":
                    c.setType(CommandList.valueOf(val));
                    break;
                case "xPos":
                    c.setXPos(Double.parseDouble(val));
                    break;
                case "lane":
                    c.setLane(Integer.parseInt(val));
                    break;
                case "health":
                    c.setHealth(Integer.parseInt(val));
                    break;
                case "damage":
                    c.setDamage(Integer.parseInt(val));
				case "resourceValue":
					c.setResourceValue(Integer.parseInt(val));
					break;
                default:
                    //TODO warum geht es noch in die default rein?
                    //System.err.println("error parsing characters attribute: "+key);
            }
        }
        return c;
    }
}
