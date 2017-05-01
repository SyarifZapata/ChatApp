package ch.unibas.dmi.cs108.sand.network;

import java.io.Serializable;
import java.util.ArrayList;

/** Ein Objekt, das einen Befehl vom Typ CommandList beinhaltet und optional eine dazugehörige Message.*/
public class Message implements Serializable {
    private String message;
    private CommandList command;
    final char SEPARATOR = '%';

    /*Constructors*/
    /**
	 * @param message Text-Message to be sent
	 * creates Message-Object of type MESSAGE
	 * */
    public Message(String message) {
        this.message = message;
        command = CommandList.MESSAGE;//message as default
    }
    public Message(CommandList command, String message){
    	this.command = command;
    	this.message = message;
	}
	/**@param command simple CommandList, e.g. PING or PONG
	 * creates Message-Object with only Command and no additional String-Message */
	public Message(CommandList command){
		this.command = command;
		message = null;
	}

    public String getMessage() {
        return message;
    }
    public CommandList getCommand(){
    	return command;
	}

    public void setMessage(String message) {
        this.message =message;
    }
	public void setMessage(CommandList command, String message) {
		this.message = message;
		this.command = command;
	}

	/** return Message as String with correct Separator */
	public String toString() {
    	if(message!=null && message != ""){
			return command.toString()+SEPARATOR+message;
		} else{
			return command.toString();
		}
	}

	/** parse Strings from Stream into Message objects */
	public static ArrayList<Message> parse(String in){
		if(in==null || in==""){
			return null;
		}
		String pattern = "(?<!\\\\)%";
		String res[] = in.split(pattern);
		ArrayList<Message> out = new ArrayList<Message>();
		int n = -1;
		for(String a:res){
			try {
				CommandList c = CommandList.valueOf(a);
				//leaving here if it doensn't work
				//MEXICAN and TRUMP goes to message instead of command
				if(c==CommandList.MEXICAN ||c==CommandList.TRUMP){
					out.get(n).setMessage(c.toString());
				} else{//create a new Message-Object
					n++;
					out.add(n,new Message(c));
				}

			} catch (IllegalArgumentException e){
				//could not parse String to CommandList => setMessage of previous CommandList
				out.get(n).setMessage(a);
			}
		}
		return out;
	}
}