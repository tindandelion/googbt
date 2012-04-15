package auctionsniper.ui;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener {
    private List<Auction> notToBeGCd = new ArrayList<Auction>();
    private AuctionHouse auctionHouse;
    private SnipersTableModel snipers;

    public SniperLauncher(AuctionHouse auctionHouse, SnipersTableModel snipers) {
        this.auctionHouse = auctionHouse;
        this.snipers = snipers;
    }

    @Override
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));

        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);

        auction.addAuctionEventListener(
                new AuctionSniper(itemId, auction,
                        new SwingThreadSniperListener(snipers)));
        auction.join();
    }
}
