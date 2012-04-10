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
    private String itemId;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
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
        driver.showsSniperStatus("", 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.LOST));
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAuction(int winningPrice) {
        driver.showsSniperStatus(itemId, winningPrice, winningPrice, SnipersTableModel.textFor(SniperState.WON));
    }
}
