package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static String[] STATUS_TEXT = {
            "Joining",
            "Bidding",
            "Winning",
            "Lost",
            "Won"
    };
    private SniperSnapshot snapshot = SniperSnapshot.joining("");

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).title;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    @Override
    public void sniperStateChanged(SniperSnapshot sniperSnapshot) {
        this.snapshot = sniperSnapshot;
        fireTableRowsUpdated(0, 0);
    }
}
