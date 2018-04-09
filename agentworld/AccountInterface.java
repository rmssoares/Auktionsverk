package agentworld;

import java.rmi.Remote;
/**
 * 
 * @author Ricardo Soares
 * For the course of:
 * Software Agents and Multi-Agent Systems.
 * 
 * University of Aberdeen
 * 2018 - MSc Artificial Intelligence
 *
 * The AccountInterface is essential for the Java RMI mechanism.
 * Although empty, it allows the "Account" class to register in the RMI Registry.
 */
public interface AccountInterface extends Remote
{

}