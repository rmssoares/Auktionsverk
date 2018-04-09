package agentworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "AuctionHouse" class mimicks the behaviour of a Mailbox.
 * It registers itself in the RMI registry, to be accessed by other
 * remote JVMs. The "AuctionHouse" will be the class in which the
 * "AuctionPlayers" will exchange communication.
 * 
 * Besides receiving Shouts ("Messages"), the transactions of Goods
 * after a successful Auction will also occur through this class.
 * 
 * There are other functionalities, some for testing regarding elapsed
 * time, message counting and the number of active Bidders (seating),
 * and some to ensure the auction Id is incremental and unique for each
 * Auction (as every auction will obtain its Id through the specified method.
 * 
 */
public class AuctionHouse implements AuctionHouseInterface {
	private ArrayList<Shout> shoutList = new ArrayList<>();
	private ArrayList<Good> goodTransaction = new ArrayList<>();
	private int size = 0;
	
	private static int auctionId = 0;
	private static int seating = 0;
	private static boolean open = true;

	private String hostname;
	private int registryport;

	//Relevant for evaluation:
	private static int nMessages = 0;
	private long startTime;
	
	public AuctionHouse(String hostname, int registryport) throws IOException {
		this.hostname = hostname;
		this.registryport = registryport;

		System.out.println("Registering from " + this.hostname + " with registry port " + this.registryport);
		
		AuctionHouseInterface mystub = (AuctionHouseInterface) UnicastRemoteObject.exportObject(this, 0);
		
		Naming.rebind("rmi://" + this.hostname + ":" + this.registryport + "/AuctionHouse", mystub);
		System.out.println("AuctionHouse> It's a clear morning in the Stockholm's Auktionsverk.\n"
				+ "AuctionHouse> Start the Session Generator, to populate the Auction House.");

		consoleInput();
	}

	@Override
	public synchronized void send(Shout shout) throws RemoteException {
		shoutList.add(shout);
	}

	@Override
	public synchronized Shout receive(int accountId) throws RemoteException {
		Iterator<Shout> it = shoutList.iterator();

		while (it.hasNext()) {
			Shout m = it.next();
			if (m.getReceiver() == accountId) {
				nMessages++;
				it.remove();
				return m;

			}

		}

		return null;
	}

	@Override
	public synchronized void sendGood(Good good) throws RemoteException {
		goodTransaction.add(good);
	}

	@Override
	public synchronized Good receiveGood(int accountId) throws RemoteException {
		Iterator<Good> it = goodTransaction.iterator();

		while (it.hasNext()) {
			Good g = it.next();
			if (g.getOwner() == accountId) {
				System.out.println("AuctionHouse> "+ g.getName() + " transferred to account "
						+ g.getOwner() + ".\n");
				it.remove();
				return g;

			}

		}
		return null;
	}
	
	public void print(String s)
	{
		System.out.println("AuctionHouse> " + s);
	}

	
	private void consoleInput() throws IOException {
		// ===============================================
		// Console input:
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;

		while (open) {
			System.out.print("AuctionHouse> ");
			input = in.readLine();

			if (input.equalsIgnoreCase("exit"))
				break;
		}
		System.out.println("Generator>The auktionsverk has closed.");
		System.exit(0);
	}

	public int getAuctionId() {
		return auctionId++;
	}

	public boolean isActive() {
		return open;
	}

	public void isSeated() {
		seating += 1;
		if (seating == size) {
			open = false;
			calculateTime();
			System.out.println("AuctionHouse> Number of messages exchanged:" +nMessages);
			System.out.println(
					"AuctionHouse> The Auction House is closing.\n"
					+ "AuctionHouse> For results, press ENTER in the Session Generator.\n"
					+ "AuctionHouse> AFTER, press ENTER in this terminal, to properly terminate.");
		}
	}
	
	private void calculateTime() {
		System.out.println("AuctionHouse> Time elapsed: "+ (System.currentTimeMillis() - startTime));
	}
	
	public void setSize(int size) throws RemoteException {
		this.size = size;
		
	}

	static public void main(String args[]) {
		// Specify the security policy and set the security manager.
		System.setProperty("java.security.policy", "security.policy");
		System.setSecurityManager(new SecurityManager());

		// =====================================================================
		// this Java program requires two parameters
		int registryport = Integer.parseInt(args[0]);

		try {
			String hostname = (InetAddress.getLocalHost()).getCanonicalHostName();
			new AuctionHouse(hostname, registryport);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void startTime(long time) throws RemoteException {
		this.startTime = time;	
	}

}