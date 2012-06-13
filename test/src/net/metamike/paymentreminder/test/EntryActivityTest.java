package net.metamike.paymentreminder.test;

import net.metamike.paymentreminder.EntryActivity;
import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
		assertNotNull("Root view is null", activity.findViewById(net.metamike.paymentreminder.R.id.root));
		
	}

	public void testLayout() {
		View mainView = activity.findViewById(net.metamike.paymentreminder.R.id.root);
		TableLayout entryView = (TableLayout) mainView.findViewById(net.metamike.paymentreminder.R.id.entryView);
		assertNotNull(entryView);
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_account));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_amount_due));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_amount_paid));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_confirmation));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_due_date));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_pay_reminder));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_transfer_date));
		assertNotNull(entryView.findViewById(net.metamike.paymentreminder.R.id.field_transfer_reminder));
		
		/* Buttons */
		LinearLayout buttonsView = (LinearLayout)mainView.findViewById(net.metamike.paymentreminder.R.id.buttonsView);
		assertNotNull(buttonsView);
		
		Button cancel = (Button)buttonsView.findViewById(net.metamike.paymentreminder.R.id.button_cancel);
		Button save = (Button)buttonsView.findViewById(net.metamike.paymentreminder.R.id.button_save);
		Button clear = (Button)buttonsView.findViewById(net.metamike.paymentreminder.R.id.button_clear);
		
		assertNotNull(cancel);
		assertNotNull(save);
		assertNotNull(clear);
		
		/* Test that button is visible on the screen */
		int[] mainLocation = new int[2];
		mainView.getLocationOnScreen(mainLocation);
		
		int[] buttonLocation = new int[2];
		
		cancel.getLocationOnScreen(buttonLocation);
		assertTrue(buttonLocation[0] + cancel.getWidth() <= mainLocation[0] + mainView.getWidth());
		assertTrue(buttonLocation[1] + cancel.getHeight() <= mainLocation[1] + mainView.getHeight());
		
		save.getLocationOnScreen(buttonLocation);
		assertTrue(buttonLocation[0] + save.getWidth() <= mainLocation[0] + mainView.getWidth());
		assertTrue(buttonLocation[1] + save.getHeight() <= mainLocation[1] + mainView.getHeight());
		
		clear.getLocationOnScreen(buttonLocation);
		assertTrue(buttonLocation[0] + clear.getWidth() <= mainLocation[0] + mainView.getWidth());
		assertTrue(buttonLocation[1] + clear.getHeight() <= mainLocation[1] + mainView.getHeight());		
	}
}
