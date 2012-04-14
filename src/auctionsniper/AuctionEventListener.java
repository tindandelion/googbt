package auctionsniper;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {
    public enum PriceSource {
        FromSniper, FromOtherBidder
    }
    void auctionClosed();
    void currentPrice(int price, int increment, PriceSource source);
}
