package auctionsniper;

import auctionsniper.ui.UserRequestListener;

public class SniperLauncher implements UserRequestListener {
    private AuctionHouse auctionHouse;
    private final SniperCollector sniperCollector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.sniperCollector = collector;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        sniperCollector.addSniper(sniper);
        auction.join();
    }
}
