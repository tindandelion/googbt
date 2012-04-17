package auctionsniper;

import auctionsniper.util.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector {
    private final List<AuctionSniper> snipers = new ArrayList<AuctionSniper>();
    private final Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);


    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        listeners.addListener(listener);
    }
}
