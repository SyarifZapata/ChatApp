package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CharacterTest {
	@Test
	public void toString1() throws Exception {
		Character c = new Character(1,CommandList.TRUMP,2,3.3,10,10);
		String toString = c.toString();
		assertEquals(toString,"id:1,type:TRUMP,xPos:3.3,lane:2,health:10,damage:10");
	}

	@Test
	public void parse() throws Exception {
		String toParse = "id:1,type:TRUMP,xPos:3.3,lane:2,health:10,damage:10";
		Character c = Character.parse(toParse);
		//ArrayList<Character> cc = Character.parse(toParse);
		//Character c = cc.get(0);
		assertEquals(c.getType(), CommandList.TRUMP);
		//assertEquals(c.getXPos(),3.3);
		assertEquals(c.getLane(),2);
		assertEquals(c.getHealth(),10);
		assertEquals(c.getDamage(),10);
	}
	@Test
	public void parse2(){
		String toParse = "";
		Character c = Character.parse(toParse);
		assertEquals(c,null);
	}

}