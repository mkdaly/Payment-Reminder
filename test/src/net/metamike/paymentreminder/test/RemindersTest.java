package net.metamike.paymentreminder.test;

import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter.ReminderType;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import net.metamike.paymentreminder.data.Reminders;
import net.metamike.paymentreminder.test.mocks.NullCursor;
import net.metamike.paymentreminder.test.mocks.MockCursor;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class RemindersTest extends AndroidTestCase {
	Cursor nullCursor;

	protected void setUp() throws Exception {
		super.setUp();
		nullCursor = new NullCursor();
	}

	
	public void testGetPaymentID() {
		Integer ex = 1;
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Reminders.getPaymentID(knownCursor));
	}
	
	public void testGetType() {
		ReminderType ex = ReminderType.PAY;
		Cursor knownCursor = new KnownCursor(ex);
		assertEquals(ex, Reminders.getType(knownCursor));
	}

	public void testGetTime() {
		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals(ex, Reminders.getTime(knownCursor));
	}

	public void testGetTimeAsLong() {
		Date ex = new Date();
		Cursor knownCursor = new KnownCursor(ex.getTime());
		assertEquals((Long)ex.getTime(), Reminders.getTimeAsLong(knownCursor));

	}

	private class KnownCursor extends MockCursor {
		private final int PAYMENT_ID = 1;
		private final int TYPE = 2;
		private final int TIME = 3;

		private long longValue = -1;
		private int intValue = -1;
		private ReminderType type = null;

		public KnownCursor(long longValue) {
			this.longValue = longValue;
		}

		public KnownCursor(ReminderType type) {
			this.type = type;
		}
		
		public KnownCursor(int intValue) {
			this.intValue = intValue;
		}

		@Override
		public int getColumnIndex(String columnName) {
			if (PaymentDBAdapter.KEY_PAYMENT_ID.equals(columnName)) {
				return PAYMENT_ID;
			} else if (PaymentDBAdapter.KEY_TYPE.equals(columnName)) {
				return TYPE;
			} else if (PaymentDBAdapter.KEY_TIME.equals(columnName)) {
				return TIME;
			} else {
				return super.getColumnIndex(columnName);
			}
		}

		@Override
		public long getLong(int columnIndex) {
			switch (columnIndex) {
				case TIME:
					return longValue;
				default:
					return super.getLong(columnIndex);
			}
		}

		@Override
		public int getInt(int columnIndex) {
			switch (columnIndex) {
				case PAYMENT_ID:
					return intValue;
				case TYPE:
					return type.ordinal();
				default:
					return super.getInt(columnIndex);
			}
		}
	}
}
