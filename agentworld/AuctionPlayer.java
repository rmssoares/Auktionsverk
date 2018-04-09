package agentworld;

import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "AuctionPlayer" class represents the Agents of our system.
 * These Agents will have the power to auction or to bid different
 * items, according to the protocol they are enacting.
 * 
 * The Auctioneer Protocol and the Bidder Protocol are based in the
 * FIPA Interaction Protocols. 
 * 
 * These simulate a Dutch Auction; if
 * it reaches a conflict case where several people bid at the same
 * time in the Dutch Auction, an English Auction ensues with these
 * people.
 * 
 * cravings -> The Desires the Agent has as a Bidder.
 * belongings -> The Auction Items the Agent will auction as an
 * Auctioneer.
 * obtained -> The Auction Items the Agent obtained as a Bidder.
 * 
 */


public class AuctionPlayer {

	private AuctionHouseInterface auktionsverk;
	private final NumberFormat df = NumberFormat.getCurrencyInstance();
	
	private final int accountId;
	private final String name;

	private double credit;
	private Map<String, Desire> cravings;
	private List<AuctionFile> belongings;
	private List<Good> obtained;
	private Shout shout;
	
	//Added for Evaluation
	private int savings=0;
	private int profit=0;

	public AuctionPlayer(int accountId, String name, AuctionHouseInterface auktionsverk) {
		this.auktionsverk = auktionsverk;
		this.accountId = accountId;
		this.name = name;
		this.credit = AuctionFile.getCredit(accountId);
		this.cravings = AuctionFile.getDesireList(accountId);
		this.belongings = AuctionFile.getAuctionItems(accountId);
		this.obtained = new ArrayList<>();
	}
	
	/**
	 * Defines what protocol will be enacted.
	 * The decision process implemented is very rudimentary:
	 * if the Agent has belongings, he will auction. If he
	 * doesn't, he will bid.
	 */
	public void conversation() throws RemoteException, InterruptedException {
		if (!belongings.isEmpty())
			auctioneerProtocol();
		else
			bidderProtocol();
	}
	/**
	 * The Auctioneer Protocol.
	 * The Auctioneer will auction every single one of his
	 * belongings, and then he will switch protocol.
	 */

	private void auctioneerProtocol() throws RemoteException, InterruptedException {

		List<Integer> participants;
		List<Shout> shouts;
		List<AuctionFile> notSold = new ArrayList<>();

		for (AuctionFile sell : belongings) {
			Good good = sell.getAuctionGood();
			double price = sell.getInitialPrice();
			double reserve = sell.getReserve();
			double increase = sell.getIncrease();
			int auctionId = auktionsverk.getAuctionId();
			String identifier = "{" + auctionId + "} (Act) " + name + "> ";
			participants = new ArrayList<>(AuctionFile.getAccountIds());
			participants.remove(participants.indexOf(accountId));
			int bidder = 0; // [Bidder, Bid]
			double bid = 0;
			System.out.println(identifier + "Started the auction " + auctionId + " for " + good.getName()
					+ " valued at " + df.format(price) + ".");
			for (int PId : participants) {
				auktionsverk.send(new Shout(2, accountId, PId, auctionId, "Start"));
				auktionsverk.send(new Shout(1, accountId, PId, auctionId, good.getName(), price));
			}
			boolean auctionOn = true;
			while (auctionOn) {
				Thread.sleep(200);
				shouts = new ArrayList<>();
				while ((shout = auktionsverk.receive(accountId)) != null)
					if (shout.getMessageType() == 3 && shout.getAuctionId() == auctionId && shout.getPrice() == price)
						shouts.add(shout);
				if (shouts.isEmpty()) {
					price -= price * increase;
					if (price < reserve) {
						auctionOn = false;
						System.out.println(identifier + "Auction for " + good.getName() + " is unsuccessful, with no bids.");
						notSold.add(sell);
						for (int PId : participants)
							auktionsverk.send(new Shout(2, accountId, PId, auctionId, "over"));
					} else
						System.out.println(identifier + good.getName() + " for " + df.format(price) + ", any takers?");
					for (int PId : participants)
						auktionsverk.send(new Shout(1, accountId, PId, auctionId, good.getName(), price));

				} else {
					auctionOn = false;
					Shout dutchWin = shouts.get(0);
					bidder = dutchWin.getSender();
					bid = dutchWin.getPrice();
					if (shouts.size() > 1) {
						increase /= 4;
						System.out.println(identifier+ "The Auction will now proceed according to the ENGLISH PROTOCOL.");
						List<Integer> englishmen = new ArrayList<>();
						for (Shout s : shouts)
							englishmen.add(s.getSender());
						for (int EId : englishmen) {
							auktionsverk.send(new Shout(2, accountId, EId, auctionId, "English"));
							auktionsverk.send(new Shout(1, accountId, EId, auctionId, good.getName(), price));
						}
						boolean englishOn = true;
						while (englishOn) {
							Thread.sleep(200);
							shouts = new ArrayList<>();
							while ((shout = auktionsverk.receive(accountId)) != null)
								if (shout.getMessageType() == 3 && shout.getAuctionId() == auctionId
										&& shout.getPrice() > price)
									shouts.add(shout);
							if (shouts.isEmpty()) {
								englishOn = false;
							} else {
								Collections.sort(shouts);
								Shout bestShout = shouts.get(0);
								shouts.remove(0);
								bidder = bestShout.getSender();
								bid = bestShout.getPrice();
								auktionsverk.send(new Shout(4, accountId, bidder, auctionId, bid));
								for (Shout s : shouts)
									auktionsverk.send(new Shout(5, accountId, s.getSender(), auctionId));
								price = (bid > price + (price * increase)) ? bid : price + (price * increase);
								System.out.println(
										identifier + good.getName() + " for " + df.format(price) + ", any takers?");
								for (int EId : englishmen)
									auktionsverk.send(new Shout(1, accountId, EId, auctionId, good.getName(), price));
							}
						}

					} else {
						auktionsverk.send(new Shout(4, accountId, bidder, auctionId, bid));
					}
					System.out.println(identifier + "Sold " + good.getName() + " to Gentleman "
							+ AuctionFile.getName(bidder) + " for " + df.format(bid) + ".");
					
					participants.remove(participants.indexOf(bidder));
					good.setOwner(bidder);
					good.setFinalPrice(bid);
					auktionsverk.sendGood(good);
					auktionsverk.send(new Shout(6, accountId, bidder, auctionId, bid));
					//belongings.remove(belongings.indexOf(sell));
					profit += bid - sell.getReserve();
					credit += bid;
					for (int PId : participants) {
						auktionsverk.send(new Shout(2, accountId, PId, auctionId, "over"));
						auktionsverk.send(new Shout(2, accountId, PId, auctionId, String.valueOf(bidder)));

					}
				}

			}
		}
		belongings = notSold;
		System.out.println(name + "> Sat down to bid.");
		bidderProtocol();
		
	}
	
	/**
	 * The Bidder Protocol.
	 * The Bidder will pay attention to every auction that he is informed of.
	 * If he hadn't heard of the auction's start, he will ignore such message.
	 */

	private void bidderProtocol() throws RemoteException {
		auktionsverk.isSeated();
		Map<Integer, Integer> auctions = new HashMap<>(); // Auctioneer, Auction
		Map<Integer, Integer> englishAuctions = new HashMap<>();

		while (credit > 0) {
			if ((shout = auktionsverk.receive(accountId)) != null) {
				int auctioneerId = shout.getSender();
				int auctionId = shout.getAuctionId();
				String identifier = "{" + auctionId + "} (Bd) " + name + " > ";
				double price = shout.getPrice();
				switch (shout.getMessageType()) {
				case 1: {// Call-for-proposal
					Desire craving;
					if (!auctions.containsValue(auctionId))
						auktionsverk.send(new Shout(0, accountId, auctioneerId, auctionId));
					else {
						if ((craving = cravings.get(shout.getAuctionItem())) != null && price <= craving.getLimitValue()
								&& price <= credit) {
							if (englishAuctions.containsValue(auctionId)) {
								double inc = craving.getIncrease();
								price += price * inc;
								price = (price > craving.getLimitValue()) ? craving.getLimitValue() : price;

							}
							auktionsverk.send(new Shout(3, accountId, auctioneerId, auctionId, price));
							System.out.println(identifier + "Offered " + df.format(price) + " for the "
									+ shout.getAuctionItem() + ".");
						}
					}

					break;
				}
				case 2: {// Inform.
					String info = shout.getMessage();
					if (info.equalsIgnoreCase("Start")) {
						auctions.put(auctioneerId, auctionId);
					} else if (info.equalsIgnoreCase("English")) {
						System.out.println(
								identifier + "Joined " + AuctionFile.getName(auctioneerId) + "'s English Auction.");
						englishAuctions.put(auctioneerId, auctionId);
					} else if (info.equalsIgnoreCase("over")) {
						auctions.remove(auctioneerId);
						if (englishAuctions.containsKey(auctioneerId))
							englishAuctions.remove(auctioneerId);
					}
					break;
				}
				case 6: // Request.
					credit -= price;
					System.out
							.println(identifier + "Paid " + df.format(price) + ", now has " + df.format(credit) + ".");
					Good o = auktionsverk.receiveGood(accountId);
					obtained.add(o);
					try {
					Desire d = cravings.get(o.getName());
					savings += d.getLimitValue()-price; //Discover how much was saved.
					cravings.remove(o.getName());
					System.out.println(identifier + "Got the good.");
					}
					catch (NullPointerException e) {
						savings -= price;
						System.out.println(identifier + "Got the good, begrudgingly. Greedy bidder, duplicate item!");
						
					}
				
					break;

				default: {// Misunderstood,Propose,Accept,Reject
					break;
				}
				}
			}
		}
		if (credit < 0)
			System.out.println(name + "> is owing the auktionsverk " + Math.abs(credit) + "!!! SEIZE HIM!\n" + name
					+ "> has been terminated.");

	}

	public double getCredit() {
		return credit;
	}

	public List<AuctionFile> getBelongings() {
		return belongings;
	}

	public List<Good> getObtained() {
		return obtained;
	}
	
	public double getSavings(){
		return savings;
	}
	
	public double getProfit() {
		return profit;
	}
}