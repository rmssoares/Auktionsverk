package agentworld;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The AuctionHouseInterface is essential for the Java RMI mechanism.
 * 
 * Every method implemented to be called by a remote JVM must be specified
 * here, as this interface will specify what can be accessed remotely.
 */

public interface AuctionHouseInterface extends Remote
{
	public Shout receive (int accountId) throws RemoteException;
	public void send(Shout shout) throws RemoteException;
	
	public Good receiveGood (int accountId) throws RemoteException;
	public void sendGood(Good good) throws RemoteException;
	
	public int getAuctionId() throws RemoteException;
	public boolean isActive() throws RemoteException;
	public void isSeated() throws RemoteException;
	public void setSize(int size) throws RemoteException;
	public void print(String s) throws RemoteException;
	public void startTime(long time) throws RemoteException;
}