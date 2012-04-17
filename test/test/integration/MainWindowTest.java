package test.integration;

import auctionsniper.SniperPortfolio;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() throws Exception {
        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
}
