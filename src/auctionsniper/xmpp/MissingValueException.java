package auctionsniper.xmpp;

public class MissingValueException extends Exception {
    public MissingValueException(String name) {
        super("Missing value with name: " + name);
    }
}
