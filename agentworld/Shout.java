package agentworld;

import java.io.Serializable;
/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The "Shout" class mimicks the behaviour of a message.
 * It is serializable as it must be able to be exchanged
 * between Remote classes.
 * 
 */

public class Shout implements Serializable, Comparable<Shout>
{
    private static final long serialVersionUID = 1;
    
    static public final int NOT_UNDERSTOOD = 0 ;
    static public final int CFP        = 1 ;
    static public final int INFORM	   = 2 ;
    static public final int PROPOSE	   = 3 ;
    static public final int ACCEPT     = 4 ;
    static public final int REJECT     = 5 ;
    static public final int REQUEST	   = 6 ;
    
    private int messageType;
    
    //Every Message (Shout) would need to implement these.
    private int sender;
    private int receiver;
    private int auctionId;
    
    //According to the type of message....
    private String auctionItem = null;
    private double price = 0;
    private String message = null;
    
    // Empty Constructor
    public Shout() {}

    // Message Constructor for protocol NOT UNDERSTOOD.
    public Shout(int messageType, int sender, int receiver, int auctionId)
    {
        this.messageType = messageType ;
        this.sender      = sender ;
        this.receiver    = receiver ;
        this.auctionId	 = auctionId;
    }
    
    // Message Constructor for CALL FOR PROPOSAL.
    public Shout(int messageType, int sender, int receiver, int auctionId, String auctionItem, double price)
    {
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.auctionId = auctionId;
        this.auctionItem = auctionItem;
        this.price = price;
    } 
    
    // Message Constructor for PROPOSE, ACCEPT and REJECT.
    public Shout(int messageType, int sender, int receiver, int auctionId, double price)
    {
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.auctionId = auctionId;
        this.price = price;
    } 
    
    //Message Constructor for INFORM.
    public Shout(int messageType, int sender, int receiver, int auctionId, String message)
    {
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
        this.auctionId = auctionId;
        this.message = message;
    }
    


    public int getReceiver() {
        return receiver;
    }
    
    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }
    public int getMessageType() {
        return messageType;
    }
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    public int getSender() {
        return sender;
    }
    
    public void setSender(int sender) {
        this.sender = sender;
    }
    public int getAuctionId() {
        return auctionId;
    }
    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getMessage() {
    	return message;
    }
    
    public void setMessage(String message) {
    	this.message = message;
    }
    
    public String getAuctionItem() {
        return auctionItem;
    }

    public void setAuctionItem(String auctionItem) {
        this.auctionItem = auctionItem;
    }

    // Descending Order.
	@Override
	public int compareTo(Shout o) {
        if (price < o.getPrice()) return 1;
        if (price > o.getPrice()) return -1;
        return 0;
	}

}