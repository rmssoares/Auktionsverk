package agentworld;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "AuctionFile" class is a Data Structure that represents the
 * Auction Item to be auctioned, in its constructor.
 * It also simulates a database, as its static structures are populated
 * according to the information provided by the text file read in the
 * "Session Generator".
 * 
 * All the Getters will be called by each "AuctionPlayer"'s constructor.
 */

public class AuctionFile {
	private final NumberFormat df = NumberFormat.getCurrencyInstance();
	private static boolean random;
	// Structure - Account Id, Account Name
	static private Map<Integer, String> invitees = new HashMap<>();

	// Structure - Account Id, Credit
	static private Map<Integer, Double> creditProven = new HashMap<>();

	// Structure - String[] with auction items.
	static private List<String[]> auctionItems = new ArrayList<>();

	// Structure - Account Id, list of Desires
	static private Map<Integer, List<Desire>> intentionList = new HashMap<>();

	/**
	 * 
	 * @param pathname - The text file's path.
	 * @param randomizer - For slightly random tweaks.
	 * 
	 * This method would read the file provided as an argument in the 
	 * "SessionGenerator". 
	 * 
	 * After perusing the text file's format and comprehending it, keep
	 * in mind that if the text file does not have any Desires, then 
	 * populateDesires will be called, where every Bidder will have a
	 * Randomized Desire over EVERY Auction Item.
	 * 
	 */
	
	static public void readFile(String pathname, boolean randomizer) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(pathname));
		String line;
		random = randomizer;
		boolean massiveDesires = true;
		while ((line = br.readLine()) != null) {

			String[] values = line.split(",");
			if (values.length > 2) {
				Integer accountId = Integer.parseInt(values[0]);
				switch (values.length) {
				// values[] = [AccountId, Name, Credit]
				case 3: { // AccountId & Credit
					invitees.put(accountId, values[1]);
					creditProven.put(accountId, Double.parseDouble(values[2]));
					break;
				}
				// values[] = [AccountId, GoodName, HighestPrice, Increase]
				case 4: {// DESIRES.
					massiveDesires = false;
					double limitValue = Double.parseDouble(values[2]);
					double increase = Double.parseDouble(values[3]);
					if (randomizer) {
						limitValue*=(0.8 + Math.random() * 0.4); //80% to 120% of the value.
						increase*=(0.8 + Math.random() * 0.4);
					}
					Desire d = new Desire(values[1], limitValue, increase);
					if (intentionList.containsKey(accountId))
						intentionList.get(accountId).add(d);
					else
						intentionList.put(accountId, new ArrayList<Desire>(Arrays.asList(d)));
					break;
				}
				// values[] = [AccountId, GoodName, InitialPrice, Reserve, Increase]
				case 5: { // BELONGINGS.
					auctionItems.add(values);
					break;
				}
				default: {

					break;
				}
				}
			}
		}
		if (massiveDesires)
			populateDesires();
		br.close();
	}

	// add bolean rand after
	private static void populateDesires() {
		for (Integer accountId : invitees.keySet()) {
			List<Desire> desires = new ArrayList<>();
			for (String[] item : auctionItems) {
				if (accountId != Integer.parseInt(item[0])) {
					double increase = 0.2 + Math.random() * 0.6; // Ranges between 0.2 and 0.8.
					double initialPrice = Double.parseDouble(item[3]); // Reserve price
					double highestLimit = initialPrice + ((initialPrice / 2) * Math.random());

					Desire d = new Desire(item[1], highestLimit, increase);

					desires.add(d);
				}
			}
			intentionList.put(accountId, desires);
		}
	}

	static public Map<String, Desire> getDesireList(int accountId) {
		Map<String, Desire> desireList = new HashMap<>();
		List<Desire> desires;
		if ((desires = intentionList.get(accountId)) != null)
			for (Desire d : desires)
				desireList.put(d.getItem(), d);
		return desireList;
	}

	public static List<AuctionFile> getAuctionItems(int accountId) {
		List<AuctionFile> belongings = new ArrayList<>();
		for (String[] item : auctionItems)
			if (Integer.parseInt(item[0]) == accountId)
				belongings.add(new AuctionFile(item));

		return belongings;
	}

	public static Double getCredit(int accountId) {
		return creditProven.get(accountId);
	}

	static public Map<Integer, String> getInvitees() {
		return invitees;
	}

	static String getName(int accountId) {
		return invitees.get(accountId);
	}

	static List<Integer> getAccountIds() {
		List<Integer> ids = new ArrayList<>();
		for (Integer key : invitees.keySet()) {
			ids.add(key);
		}
		return ids;
	}

	private Good auctionGood;
	private double initialPrice;
	private double reserve;
	private double increase;

	public AuctionFile(String[] itemFile) {
		this.auctionGood = new Good(itemFile[1], Integer.parseInt(itemFile[0]));
		this.initialPrice = Double.parseDouble(itemFile[2]);
		this.reserve = Double.parseDouble(itemFile[3]);
		this.increase = Double.parseDouble(itemFile[4]);
		if(random) {
			System.out.println("IT WORKS, FRIEND!");
			initialPrice *= (0.8 + Math.random() * 0.4);
			reserve *= (0.8 + Math.random() * 0.4);
			increase *= (0.8 + Math.random() * 0.4);
		}
	}

	public Good getAuctionGood() {
		return auctionGood;
	}

	public double getInitialPrice() {
		return initialPrice;
	}

	public double getReserve() {
		return reserve;
	}

	public double getIncrease() {
		return increase;
	}

	public String toString() {
		return auctionGood.getName() + " valued at " + df.format(initialPrice);
	}

}