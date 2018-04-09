package agentworld;

/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "Desire" class represents the "Desire" of a Bidder.
 * According to a name, the Bidder has a certain desire or not.
 * The limitValue represents how high the Bidder would go for it.
 * The increase represents how fast he would increase such bid
 * throughout the rounds.
 * 
 *  There is only one instance of each "Good", and as such,
 *  the "Desire" is corresponded to a good NAME.
 */

public class Desire {
	private String goodName;
	private double limitValue;
	private double increase; //Increase
	
	//Desire is composed by an item, the agent's limit value, and increase.
	public Desire(String goodName, double limitValue, double increase) {
		this.goodName = goodName;
		this.limitValue = limitValue;
		this.increase = increase;
	}


	public String getItem() {
		return goodName;
	}
	
	public double getLimitValue() {
		return limitValue;
	}
	
	public double getIncrease() {
		return increase;
	}
	
	public String toString() {
		return goodName + " is desired. ";
	}

}
