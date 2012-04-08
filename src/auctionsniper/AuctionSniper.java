package auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private SniperListener listener;
    private Auction auction;
    private boolean isWinning = false;
    private String itemId;

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.itemId = itemId;
        this.auction = auction;
        this.listener = listener;
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
            listener.sniperWinning();
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            listener.sniperBidding(new SniperState(itemId, price, bid));
        }
    }
}
