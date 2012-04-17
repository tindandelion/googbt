package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private Auction auction;
    private SniperSnapshot snapshot;
    private String itemId;

    public AuctionSniper(String itemId, Auction auction) {
        this.itemId = itemId;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void addListener(SniperListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        switch (source) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }

    public String itemId() {
        return itemId;
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
}
