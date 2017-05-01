package ch.unibas.dmi.cs108.sand.network;

/** Definition of protocol
 * IDENTIFY: send userName, e.g.
 * IDENTIFY%mein Username
 *
 * IDENTIFYING: Client submits his username, e.g.
 * IDENTIFYING%mein erster Username
 *
 * WELCOME: confirmation that connected, e.g.
 * WELCOME
 *
 * MESSAGE: incoming message of server, e.g.
 * MESSAGE%Hallo Jack. Wie geht es dir?
 *
 * NEW_NAME: Client requests new name, e.g.
 * NEW_NAME%mein neuer Client-Name
 *
 * NAME_CHANGED: confirm that your username did change, e.g.
 * NAME_CHANGED
 *
 * LOGOUT: Client tells Server that he wants to leave, e.g.
 * LOGOUT
 *
 * GOODBYE: tells Client to logout and close connection, e.g.
 * GOODBYE
 *
 * PING: Server asks, if Client is still here, or vice-versa, e.g.
 * PING
 *
 * PONG: Client confirms that he is still here, e.g.
 * PONG
 *
 * when loosing connection with the server: for now just shut down Client
 * */
public enum CommandList {

    IDENTIFY("Server invites Client to submit his username"),
    IDENTIFYING("Client submits his username"),
    WELCOME("Server confirms that Client has been added"),
    MESSAGE("Messages sent between Server and Clients"),

    CREATE_GAMESERVER("Client asks Server to create a new gameServer"),
    AVAILABLE_GAMES("Server sends an available Game to the Clients"),
    SHOW_CLIENTS("Server sends all registered clients"),

    //TODO client muss andere clients in lobby sehen und in ganzem chat.

    JOIN_GAME("Client requests to enter a certain game"),
    JOIN_LOBBY("Enter the Lobby"),
    LOBBY_CHAT("Chatting in the Lobby"),
    WHISPER_CHAT("Whisper without playing a game"),
    CHARACTER("Character with its attributes or Array of Characters (JSON)"),
    GET_GAME("the actions from the Client in the game are requested from the Server"),
    GAME_READY("GameServer tells its Clients that everyone has joined and Game can be started"),
    MEXICAN("play as Mexican"),
    TRUMP("play as Trump"),

    NEW_NAME("Client requests new name"),
    NAME_CHANGED("Confirm that Clientname has changed"),
    LOGOUT("Client wants to leave"),
    GOODBYE("Server confirms that he is logging out Client"),
    PING("ask for confirmation, if C/S is still here"),
    PONG("send confirmation, that you are still here"),

    TEST("Command for testing purpose"),
    UPDATE_RES("Update resources of each player. Trump(Money), Mexican(Tacos)");



    private final String description;

    CommandList(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}