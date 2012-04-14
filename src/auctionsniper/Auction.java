package auctionsniper;

public interface Auction {
    void addAuctionEventListener(AuctionEventListener listener);
    void join();
    void bid(int newPrice);
}
