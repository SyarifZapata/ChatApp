package ch.unibas.dmi.cs108.sand.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Syarif on 4/30/2017.
 */
public class User {
    private String name;
    private int id;

    public User(){

    }

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString(){
        String out = "name:"+name+",";
        out += "id:"+id;
        out +=";";
        return out;
    }

    public static String hashMapToString(HashMap<Integer,String> hashMap){
        String out = "";
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()){
            HashMap.Entry pair = (Map.Entry)it.next();
            out +=  "name:"+pair.getValue()+",";
            out += "id:"+pair.getKey();
            out +=";";
        }
        return out;
    }

    /** parse a String to a Character.
     * @return Exported Character
     */
    public static User parse(String in){
        if(in=="" || in==null){
            return null;
        }
        String data[] = in.split(",");
        User user = new User();
        for (String i : data) {
            String j[] = i.split(":");
            String key = j[0];
            String val = j[1];
            switch (key) {
                case "name":
                    user.setName(val);
                    break;
                case "id":
                    user.setId(Integer.parseInt(val));
                    break;
                default:
                   user = null;
            }
        }
        return user;
    }

    public static ArrayList<User> parseUsers(String in){
        String ins[] = in.split(";");
        ArrayList<User> users = new ArrayList<User>();
        for(String i:ins){
            String pattern="name:(\\w+),id:([0-9]+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(i);
            if(m.find()){
                String name = m.group(1);
                int id = Integer.parseInt(m.group(2));

                User user = new User(name,id);
                users.add(user);
            }
        }
        return users;
    }
}
