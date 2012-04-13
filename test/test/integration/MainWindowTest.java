package test.integration;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import test.endtoend.auctionsniper.AuctionSniperDriver;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
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
