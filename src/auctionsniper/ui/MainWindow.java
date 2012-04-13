package auctionsniper.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper Main";
    public static final String NEW_ITEM_ID_NAME = "new item id";
    public static final String JOIN_BUTTON_NAME = "join";

    private static final String SNIPERS_TABLE_NAME = "snipers table";

    private final SnipersTableModel snipers;
    private List<UserRequestListener> listeners = new ArrayList<UserRequestListener>();

    public MainWindow(SnipersTableModel snipers) throws HeadlessException {
        super("Auction Sniper");
        this.snipers = snipers;

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

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        controls.add(joinAuctionButton);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (UserRequestListener l : listeners) {
                    l.joinAuction(itemIdField.getText());
                }
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
        JTable table = new JTable(snipers);
        table.setName(SNIPERS_TABLE_NAME);
        return table;
    }

    public void addUserRequestListener(UserRequestListener listener) {
        listeners.add(listener);
    }
}
