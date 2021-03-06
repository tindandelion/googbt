package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

public enum Column {
    ITEM_IDENTIFIER("auctionsniper.Item") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.itemId;
        }
    },
    LAST_PRICE("Last Price") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastPrice;
        }
    },
    LAST_BID("Last Bid") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastBid;
        }
    },
    SNIPER_STATE("State") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return SnipersTableModel.textFor(snapshot.state);
        }
    };

    public final String title;

    Column(String title) {
        this.title = title;
    }

    public static Column at(int offset) {
        return values()[offset];
    }

    abstract public Object valueIn(SniperSnapshot snapshot);
}
