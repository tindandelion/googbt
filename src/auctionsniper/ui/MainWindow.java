package auctionsniper.ui;

import auctionsniper.SniperPortfolio;
import auctionsniper.util.Announcer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper Main";
    public static final String NEW_ITEM_ID_NAME = "new item id";
    public static final String STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join";

    private static final String SNIPERS_TABLE_NAME = "snipers table";

    private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
    private SniperPortfolio portfolio;

    public MainWindow(SniperPortfolio portfolio) {
        super("Auction Sniper");
        this.portfolio = portfolio;

        setName(APPLICATION_TITLE);
        setTitle(APPLICATION_TITLE);
        fillContentsPane(makeSnipersTable(), makeControls());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        final JTextField stopPriceField = new JTextField();
        stopPriceField.setName(STOP_PRICE_NAME);
        stopPriceField.setColumns(10);
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequests.announce().joinAuction(itemIdField.getText());
            }
        });

        return controls;
    }

    private void fillContentsPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        SnipersTableModel snipers = new SnipersTableModel();
        portfolio.addPortfolioListener(snipers);
        JTable table = new JTable(snipers);
        table.setName(SNIPERS_TABLE_NAME);
        return table;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
}
