package agentworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "SessionGenerator" populates our system according to a text file,
 * by finding the reference of the "AuctionHouse" in the RMI registry
 * and populating it as expected.
 * 
 * The "SessionGenerator" also registers to the RMI registry.
 * It also initializes every Account according to the latter,
 * serving as a "Container" (More of this in Agent theory).
 * 
 */
public class SessionGenerator {

	AuctionHouseInterface auktionsverk;
	private final NumberFormat df = NumberFormat.getCurrencyInstance();

	private List<Account> accountList = new ArrayList<>();

	public SessionGenerator(String sessionName, String hostname, int registryport)
			throws NotBoundException, IOException {
		String regURL = "rmi://" + hostname + ":" + registryport + "/AuctionHouse";
		System.out.println("Generator> Looking up " + regURL);
		// ===============================================
		// lookup the remote service, it will return a reference to the stub:
		auktionsverk = (AuctionHouseInterface) Naming.lookup(regURL);

		// To measure time.
		long startTime = System.currentTimeMillis();
		auktionsverk.startTime(startTime);
		auktionsverk.print("Today's the " + sessionName + " session. Doors open.");

		auktionsverk.print("As such, gentlemen enter: ");
		Map<Integer, String> invitees = AuctionFile.getInvitees();
		for (Entry<Integer, String> entry : invitees.entrySet()) {
			Account invAccount = new Account(entry.getKey(), entry.getValue(), hostname, registryport);
			accountList.add(invAccount);
			new Thread(invAccount).start();
		}
		auktionsverk.setSize(invitees.size());
		consoleInput();
	}

	private void consoleInput() throws IOException, NotBoundException {
		// ===============================================
		// Console input:
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;

		while (auktionsverk.isActive()) {
			input = in.readLine();
			if (input.equalsIgnoreCase("exit"))
				break;
		}
		shutdownAccounts();
		System.out.println("Generator>The auktionsverk has closed.");
		System.exit(0);
	}
	
	/**
	 * shutdownAccounts terminates every thread.
	 * Although the prints are also not ideal, they serve the purpose.
	 */
	private void shutdownAccounts() throws RemoteException, MalformedURLException, NotBoundException {
		auktionsverk.print(" -------------- // Auction House Terminating! \\ ---------------");
		for (Account a : accountList) {
			a.shutdown();
		}

		auktionsverk.print("In total, the Auctioneers made a profit of " + df.format(Account.getTotalProfit()) + ".");
		auktionsverk.print("In total, the Bidders saved a total of " + df.format(Account.getTotalSavings()) + ".");
	}

	public static void main(String[] args) {
		// Specify the security policy and set the security manager.
		System.setProperty("java.security.policy", "security.policy");
		System.setSecurityManager(new SecurityManager());
		
		String sessionName = args[0];
		int registryport = Integer.parseInt(args[1]);
		try {
			if (sessionName.equalsIgnoreCase("rand"))
				AuctionFile.readFile(args[2], true);
			else
				AuctionFile.readFile(args[2], false);

			// =================================================================
			// get the hostname of the machine where this program is started
			String hostname = (InetAddress.getLocalHost()).getCanonicalHostName();

			new SessionGenerator(sessionName, hostname, registryport);

		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}

	}

}