package auctionsniper.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String APPLICATION_TITLE = "Auction Sniper Main";
    private static final String SNIPERS_TABLE_NAME = "snipers table";

    private final SnipersTableModel snipers;

    public MainWindow(SnipersTableModel snipers) throws HeadlessException {
        super("Auction Sniper");
        this.snipers = snipers;

        setName(APPLICATION_TITLE);
        setTitle(APPLICATION_TITLE);
        fillContentsPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentsPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        JTable table = new JTable(snipers);
        table.setName(SNIPERS_TABLE_NAME);
        return table;
    }
}
