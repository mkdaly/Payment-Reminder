package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.EntryActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

public class EntryActivityTest extends ActivityInstrumentationTestCase2<EntryActivity> {
	private EntryActivity activity;
	
	
	public EntryActivityTest() {
		super("net.metamike.paymentreminder", EntryActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		activity = this.getActivity();

	}
	
	public void testPreconditions() {
		assertNotNull("Entry view is null", activity.findViewById(net.metamike.paymentreminder.R.id.entryView));
		
	}

	public void testLayout() {
		TableLayout entryView = (TableLayout) activity.findViewById(net.metamike.paymentreminder.R.id.entryView);
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.button_cancel));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.button_save));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_account));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_amount_due));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_amount_paid));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_confirmation));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_due_date));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_pay_reminder));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_transfer_date));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_transfer_reminder));
	}
}
