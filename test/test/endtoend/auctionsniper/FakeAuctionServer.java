package test.endtoend.auctionsniper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FakeAuctionServer {
    public FakeAuctionServer(String s) {

    }

    public void startSellingItem() {
        throw new NotImplementedException();
    }

    public String getItemId() {
        throw new NotImplementedException();
    }

    public void hasReceivedJoinRequestFromSniper() {
        throw new NotImplementedException();
    }

    public void announceClosed() {
        throw new NotImplementedException();
    }

    public void stop() {
        throw new NotImplementedException();
    }
}
