package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static String[] STATUS_TEXT = {
            "Joining",
            "Bidding",
            "Winning",
            "Lost",
            "Won"
    };
    private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).title;
    }

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                snapshots.set(i, newSnapshot);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new RuntimeException("No sniper for item: " + newSnapshot.itemId);
    }

    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        fireTableRowsInserted(0, 0);
    }
}
