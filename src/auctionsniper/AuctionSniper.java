package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private SniperListener listener;
    private Auction auction;
    private boolean isWinning = false;
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void auctionClosed() {
        if (isWinning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        isWinning = (source == PriceSource.FromSniper);
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }
        listener.sniperStateChanged(snapshot);
    }
}
