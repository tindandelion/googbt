package auctionsniper.ui;

import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel {
    private SniperState sniperState = new SniperState("", 0, 0);

    public enum Column {
        ITEM_IDENTIFIER,
        LAST_PRICE,
        LAST_BID,
        SNIPER_STATUS;

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
            case ITEM_IDENTIFIER: return sniperState.itemId;
            case LAST_PRICE: return sniperState.lastPrice;
            case LAST_BID: return sniperState.lastBid;
            case SNIPER_STATUS: return statusText;
        }
        throw new IllegalArgumentException("No column at: " + columnIndex);
    }

    public void setStatusText(String value) {
        statusText = value;
        fireTableRowsUpdated(0, 0);
    }

    public void sniperStatusChanged(SniperState state, String statusText) {
        sniperState = state;
        this.statusText = statusText;
        fireTableRowsUpdated(0, 0);
    }
}
