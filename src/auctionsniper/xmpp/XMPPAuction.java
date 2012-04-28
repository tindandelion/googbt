package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    private final Announcer<AuctionEventListener> listeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(Chat chat, String sniperId) {
        this.chat = chat;
        AuctionMessageTranslator translator = new AuctionMessageTranslator(
                sniperId, listeners.announce(), failureReporter());
        chat.addMessageListener(translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    private XMPPFailureReporter failureReporter() {
        return new LoggingXMPPFailureReporter();
    }

    private AuctionEventListener chatDisconnectorFor(final MessageListener listener) {
        return new AuctionEventListener() {
            @Override
            public void auctionFailed() {
                chat.removeMessageListener(listener);
            }

            @Override
            public void auctionClosed() {
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource source) {
            }
        };
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }


    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
