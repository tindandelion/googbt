package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

import static auctionsniper.AuctionEventListener.PriceSource;
import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

public class AuctionMessageTranslator implements MessageListener {
    private AuctionEventListener listener;
    private String sniperId;
    private XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener, XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = failureReporter;
    }

    public void processMessage(Chat chat, Message message) {
        String messageBody = message.getBody();
        try {
            translate(messageBody);
        } catch (Exception exception) {
            failureReporter.cannotTranslateMessage(sniperId, messageBody, exception);
            listener.auctionFailed();
        }
    }

    private void translate(String body) throws MissingValueException {
        AuctionEvent event = AuctionEvent.from(body);

        String type = event.type();
        if ("CLOSE".equals(type)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
            listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(sniperId));
        }
    }

    private static class AuctionEvent {
        private final Map<String, String> fields = new HashMap<String, String>();

        public static AuctionEvent from(String message) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(message)) {
                event.addField(field);
            }
            return event;
        }

        public String type() throws MissingValueException {
            return get("Event");
        }

        public int currentPrice() throws MissingValueException {
            return getInt("CurrentPrice");
        }

        public int increment() throws MissingValueException {
            return getInt("Increment");
        }

        private String get(String key) throws MissingValueException {
            String value = fields.get(key);
            if (null == value) {
                throw new MissingValueException(key);
            }
            return value;
        }

        private int getInt(String key) throws MissingValueException {
            return Integer.parseInt(get(key));
        }


        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        private static String[] fieldsIn(String message) {
            return message.split(";");
        }

        public PriceSource isFrom(String sniperId) throws MissingValueException {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() throws MissingValueException {
            return get("Bidder");
        }
    }
}
