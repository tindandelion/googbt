package auctionsniper;

public interface SniperListener {
    void sniperLost();
    void sniperWon();
    void sniperWinning();
    void sniperBidding(SniperState sniperState);
}
