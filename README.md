# Auktionsverk

The Auktionsverk simulates an Auction House, where Auctions happen concurrently. The name is inspired in the "Stockholm's Auktionsverk". Every auction starts as a [Dutch Auction](https://en.wikipedia.org/wiki/Dutch_auction), where an Auctioneer starts with a very high bid, and decreases the price until someone accepts the bid. If one person bids, he'll win the Auction. In the situation where more than one person have bid in the Dutch Auction, the Auction flips into an [English Auction](https://en.wikipedia.org/wiki/English_auction), where the Bidders offer their bids every round, and the price increases until one bid comes out without contest. *The only Bidders that can bid in the English Auction are the ones that have bid in the Dutch Auction*. The English Auction is the traditional method of auctioning that most people know.

## Getting Started

The software provided was entirely done in *Java*. To start, clone the present repository into your local machine. If you're unaware of how to achieve this, please become familiar with the mechanisms of [GitHub](https://help.github.com/articles/set-up-git) repositories.

```
git clone git@github.com:thyriki/Auktionsverk.git
```

### Prerequisites
Ensure that you have the last version of [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed and properly set up.

### Instructions

Start by opening a terminal, and navigate to the project's folder, where the **agentworld & data** folder and the **security.policy** file is. This is the path where every *terminal* must be, when running the commands.

Initialize the *RMI Registry* on a specified port. For the sake of explanation, the port used is going to be *50000*.

```
rmiregistry 50000
```

Open a second terminal, in the same directory. Don't forget to compile the classes:

```
javac agentworld/*.java
```

Two classes are going to be executed. [AuctionHouse.java](https://github.com/thyriki/Auktionsverk/blob/master/agentworld/AuctionHouse.java) and [SessionGenerator.java](https://github.com/thyriki/Auktionsverk/blob/master/agentworld/SessionGenerator.java). The *AuctionHouse* somehow mimicks a *Mailbox Server*, and it will be the remote class through which *AuctionPlayers* (Agents) will communicate. The *SessionGenerator* will be used as a *Container*, initializing several *AuctionPlayers*, and it also populates our system according to the information provided by a textfile.

In the second terminal, run the following command:

*Windows:*
```
java agentworld/AuctionHouse 50000
```
*Linux:*
```
java agentworld.AuctionHouse 50000
```

This will initialize the *AuctionHouse* and register it in the *RMI Registry*. The port specified *must be the same as the one previously used in the rmiregistry command*.

Start a third terminal and run the following command:

*Windows:*
```
java agentworld/SessionManager SessionName 50000 data/text.txt
```
*Linux:*
```
java agentworld.SessionManager SessionName 50000 data/text.txt
```

**SessionName** - Designated by the user. Can be anything. There is a special case: If the name is set to *rand*, the values of the *.txt* will be randomly tweaked, when added to the system.
**data/simpleauction.txt** - A possible pathname for a text file. It will populate our system according to the information depicted. 


## Extra Functionalities

Grow accustomed to the template of the textfiles. It represents three "tables":

* Each entry in the first table represents an *AuctionPlayer's Id*, *AuctionPlayer's name* and *AuctionPlayer's credit*.
* Each entry in the second table represents an *AuctionPlayer's Id*, and the *Auction Item's name, Auctioneer's initial price, Auctioneer's reserve price* and *Auctioneer's increase*. 
* Each entry in the third table represents an *AuctionPlayer's Id*, and the *Auction Item's name, Bideer's highest price* and *Bidder's increase*.

Pay attention to how the commas divide the different values, as the commas will be the separator of each line.

### Populate Massive Desires

*If the third table does not exist*, then, there are no preset "Desires" (representative of the things a Bidder wants). In this case, every Bidder will have a Desire for *every single Auction Item*. The values are randomized, yet constrained in specific ranges, as it can be found in the *AuctionFile* code.

### rand

*If the SessionName is replaced by "rand"*, every table will have its values slightly tweaked in a random fashion. If there are no desires, the randomization that happens in the desires is the one provided by the *Massive Desires* section.