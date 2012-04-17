package auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(JMock.class)
public class SniperLauncherTest {
    private final Mockery context = new Mockery();
    private final States auctionState = context.states("auction state").startsAs("not joined");
    private final Auction auction = context.mock(Auction.class);
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final SniperCollector collector = context.mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, collector);

    @Test
    public void initial() throws Exception {
        final String itemId = "item 123";
        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId);
                will(returnValue(auction));
            oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));
            oneOf(collector).addSniper(with(sniperForItem(itemId)));
                when(auctionState.is("not joined"));
            one(auction).join();
                then(auctionState.is("joined"));
        }});

        launcher.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with id", "was") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.itemId();
            }
        };
    }
}
