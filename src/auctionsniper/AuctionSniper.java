package auctionsniper;

import auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private Auction auction;
    private SniperSnapshot snapshot;
    private Item item;

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
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
    public void auctionFailed() {
        snapshot = snapshot.failed();
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
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                } else {
                    snapshot = snapshot.losing(price);
                }
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
}
