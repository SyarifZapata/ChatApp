package ch.unibas.dmi.cs108.sand.network;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;

public class MessageTest {
	@Test
	public void toString1() {
		Message m = new Message(CommandList.MESSAGE,"hallo");
		String parsed = m.toString();
		assertEquals("MESSAGE%hallo",parsed);
	}

	@Test
	public void toString2() {
		Message m = new Message(CommandList.GAME_READY,"MEXICAN");
		String parsed = m.toString();
		assertEquals("GAME_READY%MEXICAN",parsed);
	}

	@Test
	public void parse() throws Exception {
		String toParse = "MESSAGE%hallo";
		Message m = Message.parse(toParse).get(0);
		assertEquals(toParse,m.toString());
	}
	@Test
	public void parse2() throws Exception {
		String toParse = "GAME_READY%MEXICAN";
		Message m = Message.parse(toParse).get(0);
		assertEquals(toParse,m.toString());
	}
	@Test
	public void parse3() throws Exception{
		String toParse = "CHARACTER%id:1%CHARACTER%id:2";
		ArrayList<Message> m = Message.parse(toParse);
		//System.out.println(m);
		assertEquals(m.get(0).getMessage(),"id:1");
		assertEquals(m.get(0).getCommand(),CommandList.CHARACTER);
	}

}