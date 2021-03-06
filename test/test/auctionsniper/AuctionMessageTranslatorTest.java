package test.auctionsniper;

import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.AuctionMessageTranslator;
import auctionsniper.xmpp.XMPPFailureReporter;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.AuctionEventListener.PriceSource;

@RunWith(JMock.class)
public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniper";

    private final Mockery context = new Mockery();
    private final AuctionEventListener listener = context.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = context.mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() throws Exception {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});

        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
        }});

        Message message = new Message();
        message.setBody("SOLVersion: 1.1;" +
                "Event: PRICE; CurrentPrice: 192;" +
                "Increment: 7; Bidder: Someone else;");
        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() throws Exception {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7, PriceSource.FromSniper);
        }});

        Message message = new Message();
        message.setBody("SOLVersion: 1.1;" +
                "Event: PRICE; CurrentPrice: 192;" +
                "Increment: 7; Bidder: " + SNIPER_ID + ";");
        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() throws Exception {
        final String badMessage = "a bad message";

        expectsFailureWithMessage(badMessage);

        Message message = new Message();
        message.setBody(badMessage);
        translator.processMessage(UNUSED_CHAT, message);
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() throws Exception {
        String badMessage = "SOLVersion: 1.1;" +
                "CurrentPrice: 192;" +
                "Increment: 7; Bidder: " + SNIPER_ID + ";";

        expectsFailureWithMessage(badMessage);

        Message message = new Message();
        message.setBody(badMessage);
        translator.processMessage(UNUSED_CHAT, message);
    }

    private void expectsFailureWithMessage(final String badMessage) {
        context.checking(new Expectations() {{
            oneOf(listener).auctionFailed();
            oneOf(failureReporter).cannotTranslateMessage(
                    with(SNIPER_ID), with(badMessage), with(any(Exception.class)));
        }});
    }
}
