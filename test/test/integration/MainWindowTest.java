package test.integration;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    public static final String ITEM_ID = "an item-id";
    public static final int STOP_PRICE = 1000;
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<Item>(equalTo(new Item(ITEM_ID, STOP_PRICE)), "join request");

        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(Item item) {
                itemProbe.setReceivedValue(item);
            }
        });

        driver.startBiddingFor(ITEM_ID, STOP_PRICE);
        driver.check(itemProbe);
    }
}
