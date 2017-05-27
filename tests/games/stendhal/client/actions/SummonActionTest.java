package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.entity.User;
import marauroa.client.ClientFramework;
import marauroa.common.game.RPAction;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

public class SummonActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ClientFramework clientFramework = new ClientFramework() {

			@Override
			public void send(RPAction arg0) {
				assertThat(arg0.get("creature"), is("fishing rod"));
			}

			@Override
			protected List<TransferContent> onTransferREQ(List<TransferContent> arg0) {
				return null;
			}

			@Override
			protected void onTransfer(List<TransferContent> arg0) {

			}

			@Override
			protected void onServerInfo(String[] arg0) {

			}

			@Override
			protected void onPreviousLogins(List<String> arg0) {

			}

			@Override
			protected void onPerception(MessageS2CPerception arg0) {

			}

			@Override
			protected void onAvailableCharacters(String[] arg0) {

			}

			@Override
			protected String getVersionNumber() {
				return null;
			}

			@Override
			protected String getGameName() {
				return null;
			}
		};
		ClientSingletonRepository.setClientFramework(clientFramework);
	}


	@Test
	public void testExecute() {
		new User();
		assertNotNull(User.get());
		String[] args = {"fishing rod"};
		new SummonAction().execute(args, null);
	}

	@Test
	public void testGetMaximumParameters() {
		assertThat(new SummonAction().getMaximumParameters(), is(9));
	}

	@Test
	public void testGetMinimumParameters() {
		assertThat(new SummonAction().getMinimumParameters(), is(1));
	}

}
