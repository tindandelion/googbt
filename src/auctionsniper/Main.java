package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.ui.SwingThreadSniperListener;
import auctionsniper.xmpp.XMPPAuction;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private MainWindow ui;
    private final SnipersTableModel snipers = new SnipersTableModel();
    private static int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    @SuppressWarnings("unused")
    private List<Chat> notToBeGCd = new ArrayList<Chat>();

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);

        main.disconnectWhenUICloses(connection);
        for (int i = 3; i < args.length; i++) {
            main.joinAuction(connection, args[i]);
        }
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
        safelyAddItemToModel(itemId);
        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection), null);

        Auction auction = new XMPPAuction(chat);
        notToBeGCd.add(chat);

        chat.addMessageListener(
                new AuctionMessageTranslator(connection.getUser(),
                        new AuctionSniper(itemId, auction,
                                new SwingThreadSniperListener(snipers))));
        auction.join();
    }

    private void safelyAddItemToModel(final String itemId) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                snipers.addSniper(SniperSnapshot.joining(itemId));
            }
        });
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private static XMPPConnection connection(String hostname, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password);
        return connection;
    }
}
