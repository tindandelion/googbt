package auctionsniper.ui;

import auctionsniper.Item;

import java.util.EventListener;

public interface UserRequestListener extends EventListener {
    public void joinAuction(Item item);
}
