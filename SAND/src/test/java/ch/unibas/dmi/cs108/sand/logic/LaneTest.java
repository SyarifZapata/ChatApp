package ch.unibas.dmi.cs108.sand.logic;

import ch.unibas.dmi.cs108.sand.network.CommandList;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import org.junit.Test;

import static org.junit.Assert.*;

public class LaneTest {
	@Test
	public void addMex() throws Exception {
	}

	@Test
	public void addTrump() throws Exception {
	}

	@Test
	public void getFirstTrump() throws Exception {
		Lane lane = new Lane(1);
		Mexican a = new Mexican(1, CommandList.MEXICAN,2,200,5,10);
		Mexican b = new Mexican(1, CommandList.MEXICAN,2,200,10,10);
		lane.addMex(a);
		lane.addMex(b);
		Character c = lane.getFirstMex();
		assertEquals(c.getHealth(),5);

	//@Test
	//public void getFirstMex() throws Exception {}
	}

}