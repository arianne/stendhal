package games.stendhal.bot.postman;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

/**
 * tests for CiaHandler
 *
 * @author hendrik
 */
public class CiaHandlerTest {

	/**
	 * tests for parsing
	 *
	 * @throws IOException not supposed to happen
	 */
	@Test
	public void parse() throws IOException {
		assertThat(new CiaHandler().read(CiaHandlerTest.class.getResourceAsStream("cia.txt")).toString(),
				equalTo("[CiaMessage [author=kiheru, branch=null, revision=null, module=stendhal, project=arianne_rpg, files=[tiled/tileset/refactoring, tiled/tileset/building/decoration/fireplace.xcf.bz2, tiled/tileset/building/decoration/coal_fire.png], message=Redrawn coal_fire]]"));
		assertThat(new CiaHandler().read(CiaHandlerTest.class.getResourceAsStream("cia2.txt")).toString(),
				equalTo("[CiaMessage [author=nhnb, branch=perception_json, revision=db478a18607f, module=marauroa, project=arianne_rpg, files=[src/marauroa/server/net/INetworkServerManager.java, src/marauroa/server/net/validator/ConnectionValidator.java], message=cleaned up imports]]"));
	}

}
