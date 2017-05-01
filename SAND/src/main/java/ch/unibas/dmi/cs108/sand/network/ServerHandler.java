package ch.unibas.dmi.cs108.sand.network;

import ch.unibas.dmi.cs108.sand.logic.Character;
import ch.unibas.dmi.cs108.sand.logic.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.unibas.dmi.cs108.sand.network.Server.clientNames;

/** ServerHandler class that defines how to interact with clients
 * protocol:
 * IDENTIFY: invite client to define his username
 * WELCOME: confirm to client that he is part of the chat
 * MESSAGE: send chat message
 * NEWNAME: client request new username
 * */
public class ServerHandler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    public boolean running = true;
    GameServer myGameServer = null;
    private Server server;
    private int id;

    ServerHandler(Socket socket, Server server, int id) {
        this.server = server;
        this.socket = socket;
        this.id = id;
    }

    public void run () {
        try{
            out = new PrintWriter(socket.getOutputStream(),true);
            //in = new BufferedReader(socket.getInputStream());
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));


            while (running) {
                // fordere Client auf, einen Username anzugeben
                out.println(CommandList.IDENTIFY);
                //System.out.println("S: IDENTIFY");

                ArrayList<Message> temp = Message.parse(in.readLine());
                for(Message m:temp){
                    if(m.getCommand()== CommandList.IDENTIFYING) clientName = m.getMessage();
                }
                //Message temp = (Message)in.readObject();
                //clientName = temp.getMessage();

                if (clientName == null) {
                    return;
                }

                // behandelt Namensduplikate mittels Nummerierung
                synchronized (clientNames) {
                    if (clientName == null) {
                        return;
                    }
                    int c = 0;
                    while( clientNames.containsValue(clientName) ){
                        String name = clientName;
                        c += 1;
                        if (c != 1) {
                            name = name.substring(0, name.length()-1) + String.valueOf(c);
                        } else {
                            name += String.valueOf(c);
                        }
                        clientName = name;
                    }
                    clientNames.put(id,clientName);

                    break;
                }
            }

            // in broadcast aufnehmen
            out.println(new Message(CommandList.WELCOME,String.valueOf(id)));
            server.broadcast.put(id,out);
            updateAvailableClients();
            server.display("Welcome, "+clientName);

            // auf Clients hören und messages broadcasten
            ArrayList<Message> input;

            PingPong pingPong = new PingPong(in,out) {
                @Override
                void noPong() {
                    server.display("Lost connection with "+clientName);
                    try{
                        removeClient(clientName);
                        close();//close while loops in PingPong
                    } catch (IOException e){
                        clientNames.remove(clientName);
                        server.broadcast.remove(out);
                        try {
                            socket.close();
                        } catch (IOException f) {
                            f.printStackTrace();
                        }
                    }

                }
            };
//				pingPong.start();

            while (running) {
                input = Message.parse(in.readLine());
                if (input == null) {
                    return;
                }
                for(Message m:input){
                    System.out.println(m);
                    switch(m.getCommand()){
                        case MESSAGE:
                            String msg = m.getMessage();
                            for (PrintWriter myClient : server.broadcast.values()) {
                                myClient.println(new Message(CommandList.MESSAGE,clientName + ": " + msg).toString());
                            }
                            break;

                        case NEW_NAME:
                            String newName = m.getMessage();
                            System.out.println("S: NEWNAME: "+clientName+" to "+newName);
                            String oldName = clientName;
                            updateClientName(newName);
                            for (PrintWriter myClient : server.broadcast.values()) {//informiere alle Clients wenn jemand seinen Namen ändert
                                myClient.println(new Message(CommandList.MESSAGE," "+oldName+" changed his name to "+clientName).toString());
                            }
                            server.display(oldName+" changed his name to "+newName);
                            out.println(new Message(CommandList.NAME_CHANGED).toString());//confirm that the name was changed
                            break;

                        case PING:
                            out.println(new Message(CommandList.PONG).toString());
                            //System.out.println("sending pong");
                            break;

                        case PONG:
								pingPong.pong();
                            //System.out.println("receiving pong");
                            break;

                        case CREATE_GAMESERVER:
                            server.gameCounter++;
                            myGameServer = new GameServer(this,m.getMessage());//chatServer und Game-Name übergeben
                            myGameServer.start();
                            //myGameServer.join(out,Boolean.valueOf(m.getMessage()));//m.getMessage ist entweder true oder false
                            server.games.add(myGameServer);
                            broadcastGames();
                            System.out.println("created GameServer GameServer"+server.gameCounter+", broadcasting games");
                            break;

                        case JOIN_LOBBY:
                            String lobbyName = m.getMessage();
                            for(GameServer g:server.games){
                                if(g.getGameName().equals(lobbyName)){
                                    System.out.println(clientName+" joining "+g.getGameName());
                                    g.enterGameLobby(out);
                                    myGameServer = g;
                                }
                            }

                            break;

                        case JOIN_GAME:
                            //TODO debug
                            String gameToJoin= m.getMessage();
                            Pattern pattern = Pattern.compile("(.*)=(.*)");
                            Matcher matcher = pattern.matcher(gameToJoin);
                            String gameName="";
                            //TODO kontrollieren, dass nicht beide Trump/Mexican sind
                            boolean asMexican = false;
                            while (matcher.find()) {
                                gameName = matcher.group(1);
                                String asMex = matcher.group(2);
                                if(asMex=="true") asMexican = true;
                            }
                            //TODO: geht vielleicht etwas einfacher..
                            for(GameServer g:server.games){
                                if(g.getGameName().equals(gameName)){
                                    System.out.println(clientName+" joining "+g.getGameName());
                                    g.join(out,asMexican);
                                    myGameServer = g;
                                }
                            }
                            broadcastGames();
                            break;

                        case LOBBY_CHAT:
                            myGameServer.lobbyChat(clientName,m);
                            break;

                        case GAME_READY:
                            boolean mex = Boolean.valueOf(m.getMessage());
                            myGameServer.join(out,mex);
                            break;

                        case WHISPER_CHAT:
                            String message = m.getMessage();
                            System.out.println(message);
                            String[] messages = message.split("\\$");
                            PrintWriter sender = server.broadcast.get(Integer.valueOf(messages[0]));
                            PrintWriter receiver = server.broadcast.get(Integer.valueOf(messages[2]));

                            sender.println(new Message(CommandList.MESSAGE,clientName + ": " + messages[1]).toString());
                            receiver.println(new Message(CommandList.MESSAGE,clientName + ": " + messages[1]).toString());
                            break;

                        //der Server bekoomt ein Game-Update von (nur) einem Client mit new incoming Characters etc.
                        case CHARACTER:
                            //String toParse = m.getMessage();//JSON oder was auch immer zu parsen
                            //Todo test
//								myGameServer.update(m);
                            //System.out.println(toParse);
                            //System.out.println("chatServer got"+toParse);
                            //gameServer.update(m);//lass den GameServer das Update auswerten
                            break;

                        case LOGOUT:
                            removeClient(clientName);
							pingPong.close();
                            break;

                        case TEST:
                            myGameServer.testing(m);
                            break;

                        case UPDATE_RES:
                            Character character = Character.parse(m.getMessage());
                            //myGameServer.updateResource(character.getName(),character.getResourceValue());
                            break;
                    }
                }
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        // Server zurücksetzen und schliessen
        finally {
            if (clientName != null) {
                clientNames.remove(clientName);
            }
            if (out != null) {
                server.broadcast.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeClient(String clientName) throws IOException{
        server.display("Goodbye, "+clientName);
        synchronized (clientNames) {
            out.println(new Message(CommandList.GOODBYE).toString());
            running = false;
            clientNames.remove(id);
            server.broadcast.remove(id);
            updateAvailableClients();
            socket.close();
        }
    }

    public void updateAvailableClients(){
        String availableClients = User.hashMapToString(server.clientNames);
        for (PrintWriter myClient : server.broadcast.values()) {
            myClient.println(new Message(CommandList.SHOW_CLIENTS, availableClients));
        }
    }

    /** ändere einen Clientnamen in der Hashtable */
    public void updateClientName(String newName){
        //System.out.println(clientNames);
        clientNames.remove(clientName);
        int c = 0;
        while( clientNames.containsValue(newName) ){
            c += 1;
            if (c != 1) {
                newName = newName.substring(0, newName.length()-1) + String.valueOf(c);
            } else {
                newName += String.valueOf(c);
            }
        }
        clientNames.put(id,newName);
        clientName = newName;
    }

    /** schicke alle verfügbaren Games mit freien Plätzen an alle Clients */
    public void broadcastGames(){
        ArrayList<String> list = new ArrayList<String>();
        for(GameServer g:server.games){
            if(!g.isFull()){
                list.add(g.getGameName());
            }
        }
        String implode = "";
        for(String h:list){
            implode += h+",";
        }
        System.out.println("broadcasting games: AVAILABLEGAMES%"+implode);
        for (PrintWriter myClient : server.broadcast.values()) {
            myClient.println(new Message(CommandList.AVAILABLE_GAMES,implode));
        }
    }

    /** schliesst Server */
    public void close(){
        running = false;
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void setServer(Server server) {
        this.server = server;
    }
}//handler