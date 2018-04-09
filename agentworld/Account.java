package agentworld;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
 * The "Account" class implements the thread methods and adds a reference to the RMI registry.
 * It wraps the AuctionPlayer.
 * 
 */
public class Account implements AccountInterface, Runnable {
	private AuctionPlayer gent;
	private int accountId;
	private String name;
	private String myURL;
	private AuctionHouseInterface auktionsverk;
	private static double totalProfit = 0;
	private static double totalSavings = 0;
	private final NumberFormat df = NumberFormat.getCurrencyInstance();

	/**
	 * 
	 * @param id - The unique AccountPlayer Id
	 * @param name - The AccountPlayer Name, no need to be unique.
	 * @param hostname - The 
	 * @param registryport
	 */
	public Account(int id, String name, String hostname, int registryport) throws RemoteException, MalformedURLException, NotBoundException {
		this.accountId = id;
		this.name = name;
        // register in the RMI registry.
        myURL = "rmi://" + hostname + ":" + registryport + "/" + id;
        AccountInterface mystub = (AccountInterface) UnicastRemoteObject.exportObject( this, 0 ) ;
        Naming.rebind( myURL, mystub );
        // Obtain the service reference from the RMI registry.
        // listening at rmi://hostname:registryport/AuctionHouse.
        String regURL = "rmi://" + hostname + ":" + registryport + "/AuctionHouse";
        // Returns a reference to the stub:
        auktionsverk = (AuctionHouseInterface) Naming.lookup( regURL );
	}
	
	/**
	 * 
	 * Thread running. Initializes the AccountPlayer.
	 */
    @Override
    public void run()
    {
        gent = new AuctionPlayer(accountId, name, auktionsverk);
        try {
        	auktionsverk.print("The Gentleman " + name + " has entered the Auktionsverk.");
			gent.conversation();
		} catch (RemoteException | InterruptedException e) {
			e.printStackTrace();
		}
    }
    
   /**
    * 
    * Shutdown of thread. Although not perfect, the collection of attributes is done here.
    */
    public void shutdown() throws RemoteException, MalformedURLException, NotBoundException
    {
    	String s;
    	
    	//Obtained items.
    	//---------------------
    	s = name+" left the auktionsverk.\n";
    	if (!gent.getBelongings().isEmpty())
    		s += name+ "> Couldn't sell " + gent.getBelongings() + ".\n";
    	if (!gent.getObtained().isEmpty()) 
    		s += name + "> Obtained "+ gent.getObtained() + " in the auktionsverk.\n";
    	
    	//Savings.
    	//---------------------
    	if (gent.getSavings()>0) {
    		s += name + "> Saved a total of " + df.format(gent.getSavings()) +"!\n";
    		totalSavings += gent.getSavings();
    	}
    	else if (gent.getSavings()<0) {
    	s+=name +"> Lost money, due to a duplicate purchase. A deficit of "+ df.format(gent.getSavings())+".\n";
    	totalSavings += gent.getSavings();
    	}
    	else if (gent.getObtained().isEmpty())
    	s += name + "> No wins, hence can't calculate savings.\n";
    	else s+= name + "> Always paid highest price! No savings!\n";
    	
    	//Profit.
    	//---------------------
    	if (gent.getProfit()>0) {
    		s += name + "> Made a profit of " + df.format(gent.getProfit()) +"!\n";
    		totalProfit += gent.getProfit();
    	}
    	else if (gent.getBelongings().isEmpty())
    	s += name + "> No sales / No profit.\n";
    	else s+= name + "> Always sold at reserve price! No profit! \n";
    	auktionsverk.print(s);
        Naming.unbind(myURL);
    }
    
    
    /**
     * 
     * @return the total savings of every AuctionPlayer.
     */
    public static double getTotalSavings() {
    	return totalSavings;
    }
    
    /**
     * 
     * @return the total profit of every AuctionPlayer.
     */
    public static double getTotalProfit() {
    	return totalProfit;
    }
}

