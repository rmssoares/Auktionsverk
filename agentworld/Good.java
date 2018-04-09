package agentworld;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "Good" class represents an auction item.
 * It is serializable as it must be able to be exchanged
 * between Remote classes.
 * 
 */

public class Good implements Serializable{
    private static final long serialVersionUID = 1;
	
	private final NumberFormat df = NumberFormat.getCurrencyInstance();
	
    private String name;
	private int owner;
	private double finalPrice = 0;

	public Good(String name) {
		this.name = name;
	}

	public Good(String name, int owner) {
		this.name = name;
		this.owner = owner;
	}
	
	public String getName() {
		return name;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(double boughtPrice) {
		this.finalPrice = boughtPrice;
	}
	
	public String toString() {
		return name + " at a cost of " + df.format(finalPrice);
	}
}
