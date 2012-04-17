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
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        sniperCollector.addSniper(sniper);
        auction.join();
    }
}
