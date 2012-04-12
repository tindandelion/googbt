package test.auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import auctionsniper.ui.Column;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@RunWith(JMock.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private final SnipersTableModel model = new SnipersTableModel();
    private final TableModelListener listener = context.mock(TableModelListener.class);

    @Before
    public void setUp() throws Exception {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() throws Exception {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() throws Exception {
        final SniperSnapshot bidding = new SniperSnapshot("item id", 555, 666, SniperState.BIDDING);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));

            one(listener).tableChanged(with(aChangeInRow(0)));
        }});
        model.addSniper(bidding);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(bidding);
    }


    @Test
    public void notifiesListenersWhenAddingASniper() throws Exception {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});
        assertEquals(0, model.getRowCount());

        model.addSniper(joining);
        assertEquals(1, model.getRowCount());

        assertRowMatchesSnapshot(joining);
    }

    @Test
    public void holdsSnipersInAdditionOrder() throws Exception {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    public void updatesCorrectRowForSniper() throws Exception {
        final SniperSnapshot snapshot = SniperSnapshot.joining("item 1");
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));

            one(listener).tableChanged(with(aChangeInRow(1)));
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(snapshot);

        model.sniperStateChanged(snapshot.bidding(100, 100));
    }

    @Test(expected = RuntimeException.class)
    public void throwsExceptionIfNoExistingSniperForUpdate() throws Exception {
        model.sniperStateChanged(SniperSnapshot.joining("item 1"));
    }

    private Object cellValue(int row, Column column) {
        return model.getValueAt(row, column.ordinal());
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(
                new TableModelEvent(model, row, row,
                        TableModelEvent.ALL_COLUMNS,
                        TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private void assertRowMatchesSnapshot(SniperSnapshot bidding) {
        assertColumnEquals(Column.ITEM_IDENTIFIER, bidding.itemId);
        assertColumnEquals(Column.LAST_PRICE, bidding.lastPrice);
        assertColumnEquals(Column.LAST_BID, bidding.lastBid);
        assertColumnEquals(Column.SNIPER_STATE, SnipersTableModel.textFor(bidding.state));
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}
