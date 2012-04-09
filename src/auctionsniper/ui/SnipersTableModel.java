package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private static String[] STATUS_TEXT = {
            MainWindow.STATUS_JOINING,
            MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING
    };
    private SniperSnapshot snapshot = new SniperSnapshot("", 0, 0, SniperState.JOINING);

    public enum Column {
        ITEM_IDENTIFIER,
        LAST_PRICE,
        LAST_BID,
        SNIPER_STATE;

        public static Column at(int offset) {
            return values()[offset];
        }
    }
    private String statusText = MainWindow.STATUS_JOINING;


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
        switch (Column.at(columnIndex)) {
            case ITEM_IDENTIFIER: return snapshot.itemId;
            case LAST_PRICE: return snapshot.lastPrice;
            case LAST_BID: return snapshot.lastBid;
            case SNIPER_STATE: return statusText;
        }
        throw new IllegalArgumentException("No column at: " + columnIndex);
    }

    public void setStatusText(String value) {
        statusText = value;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperSnapshot snapshot) {
        this.snapshot = snapshot;
        this.statusText = STATUS_TEXT[snapshot.state.ordinal()];
        fireTableRowsUpdated(0, 0);
    }
}
