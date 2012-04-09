package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private SniperListener listener;
    private Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
        this.snapshot = SniperSnapshot.joining(itemId);
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
        listener.sniperStateChanged(snapshot);
    }
}
