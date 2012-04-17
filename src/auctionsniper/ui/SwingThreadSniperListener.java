package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

class SwingThreadSniperListener implements SniperListener {
    private SniperListener sniperListener;
    public SwingThreadSniperListener(SniperListener listener) {
        this.sniperListener = listener;
    }
    @Override
    public void sniperStateChanged(final SniperSnapshot snapshot) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                sniperListener.sniperStateChanged(snapshot);
            }
        });
    }
}
