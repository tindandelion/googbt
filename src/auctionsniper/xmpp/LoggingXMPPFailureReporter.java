package auctionsniper.xmpp;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    public LoggingXMPPFailureReporter() {
    }

    @Override
    public void cannotTranslateMessage(String sniperId, String message, Exception ex) {
    }
}
