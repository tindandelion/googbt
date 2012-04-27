package test.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.AuctionEventListener.PriceSource;
import static auctionsniper.SniperState.*;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JMock.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item-123";
    private static int STOP_PRICE = 1234;

    private final Mockery context = new Mockery();
    private final States sniperState = context.states("sniper");
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener = context.mock(SniperListener.class);

    private final Item item = new Item(ITEM_ID, STOP_PRICE);
    private final AuctionSniper sniper = new AuctionSniper(item, auction);

    @Before
    public void setUp() throws Exception {
        sniper.addListener(sniperListener);
    }

    @Test
    public void reportLostWhenAuctionClosesImmediately() throws Exception {
        context.checking(new Expectations() {{
            one(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    public void reportsLostWhenAuctionClosesWhileBidding() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
                when(sniperState.is("bidding"));
        }

        });

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWonWhenAuctionClosesWhenWinning() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
                when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWinningWhenTheCurrentPriceComesFromTheSniper() throws Exception {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
    }

    @Test
    public void switchesFromWinningToBiddingState() throws Exception {
        final int otherPrice = 146;
        final int increment = 40;
        final int sniperBid = otherPrice + increment;

        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));

            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, otherPrice, sniperBid, BIDDING));
                when(sniperState.is("winning"));
        }});
        sniper.currentPrice(100, 45, PriceSource.FromSniper);
        sniper.currentPrice(otherPrice, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrivesFromOtherBidder() throws Exception {
        final int price = 100;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            one(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
        }});

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() throws Exception {
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
                then(sniperState.is("bidding"));
            allowing(auction).bid(bid);

            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }

    private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state), "sniper that is", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
