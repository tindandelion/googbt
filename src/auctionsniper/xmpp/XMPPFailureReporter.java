package auctionsniper.xmpp;

public interface XMPPFailureReporter {
    void cannotTranslateMessage(String sniperId, String message, Exception ex);
}
