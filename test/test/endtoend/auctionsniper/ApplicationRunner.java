package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@localhost/Smack";
    public static final String SNIPER_PASSWORD = "sniper";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startBiddingIn(auctions, Integer.MAX_VALUE);
    }

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startBiddingIn(new FakeAuctionServer[]{auction}, stopPrice);
    }

    private void startBiddingIn(final FakeAuctionServer[] auctions, int stopPrice) {
        startSniper();
        for (FakeAuctionServer auction : auctions) {
            final String itemId = auction.getItemId();
            driver.startBiddingFor(itemId, stopPrice);
            driver.showsSniperStatus(itemId, 0, 0,
                    SnipersTableModel.textFor(SniperState.JOINING));
        }
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice,
                lastBid, SnipersTableModel.textFor(SniperState.LOST));
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice,
                lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid,
                winningBid, SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int winningPrice) {
        driver.showsSniperStatus(auction.getItemId(), winningPrice,
                winningPrice, SnipersTableModel.textFor(SniperState.WON));
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int currentPrice, int bid) {
        driver.showsSniperStatus(auction.getItemId(), currentPrice, bid, SnipersTableModel.textFor(SniperState.LOSING));
    }

    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), 0, 0, SnipersTableModel.textFor(SniperState.FAILED));
    }

    public void reportInvalidMessage(FakeAuctionServer auction, String message) {
    }
}
