package net.metamike.paymentreminder.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.metamike.paymentreminder.EntryActivity;
import net.metamike.paymentreminder.ListActivity;
import net.metamike.paymentreminder.ViewActivity;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.widget.TextView;

public class ViewActivityUnitTest extends ActivityUnitTestCase<ViewActivity> {
	
	public ViewActivityUnitTest() {
		super(ViewActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testOnCreate() {
		String exAccount = "Test cancel";
		String exAmountDue = "2";
		String exAmountDueFormatted = "$2.00"; //For USD
		String exDateDue = "2012-03-15";
		String exAmountPaid = "3";
		String exAmountPaidFormatted = "$3.00";  //For USD
		String exTransferferDate = "2012-03-12";
		String exConf = "confirmed";
		String exID = "1";

		Intent viewIntent = new Intent(getInstrumentation().getTargetContext(), ViewActivity.class);
		viewIntent.setAction(ListActivity.EDIT_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ACCOUNT, exAccount);
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_DUE, exAmountDue);
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_DUE, exDateDue);
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_PAID, exAmountPaid);
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_TRANSFER, exTransferferDate);
		viewIntent.putExtra(PaymentDBAdapter.KEY_CONFIRMATION, exConf);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ID, exID);
		
		startActivity(viewIntent, null, null);
		ViewActivity activity = getActivity();
		
		TextView account = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_account);
		TextView due = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_due);
		TextView paid = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_paid);
		//TODO: Added section for reminders
		//TextView reminders = 
		
		assertEquals(exAccount, account.getText());
		assertEquals(exAmountDueFormatted + " on March 15", due.getText());
		assertEquals(exAmountPaidFormatted + " on March 12", paid.getText());		
	}
	
	public void testNoAccount() {
		Intent viewIntent = new Intent(getInstrumentation().getTargetContext(), ViewActivity.class);
		viewIntent.setAction(ListActivity.EDIT_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ACCOUNT, "");
		
		startActivity(viewIntent, null, null);
		assertTrue(isFinishCalled());		
	}
	
	public void testNoAmmountsNoDates() {
		Intent viewIntent = new Intent(getInstrumentation().getTargetContext(), ViewActivity.class);
		viewIntent.setAction(ListActivity.EDIT_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ACCOUNT, "Test");
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_DUE, "");
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_DUE, "");
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_PAID, "");
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_TRANSFER, "");
		
		startActivity(viewIntent, null, null);
		ViewActivity activity = getActivity();
		TextView due = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_due);
		TextView paid = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_paid);
		
		assertEquals("none", due.getText());
		assertEquals("none", paid.getText());		

	}

	public void testNoAmmounts() {
		Intent viewIntent = new Intent(getInstrumentation().getTargetContext(), ViewActivity.class);
		viewIntent.setAction(ListActivity.EDIT_INTENT);
		viewIntent.putExtra(PaymentDBAdapter.KEY_ACCOUNT, "Test");
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_DUE, "");
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_DUE, "2012-04-05");
		viewIntent.putExtra(PaymentDBAdapter.KEY_AMOUNT_PAID, "");
		viewIntent.putExtra(PaymentDBAdapter.KEY_DATE_TRANSFER, "2012-04-04");
		
		startActivity(viewIntent, null, null);
		ViewActivity activity = getActivity();
		TextView due = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_due);
		TextView paid = (TextView)activity.findViewById(net.metamike.paymentreminder.R.id.view_field_paid);
		
		assertEquals("on April 5", due.getText());
		assertEquals("on April 4", paid.getText());		

	}

	
	
	public void notestTime() {
		Time t = new Time();
		t.parse3339("2012-03-15");
		long now = new Date().getTime();
		String st = (String)(
				DateUtils.formatDateTime(getInstrumentation().getTargetContext(),
						t.toMillis(false),
						DateUtils.FORMAT_ABBREV_RELATIVE) + " (" +

				DateUtils.getRelativeTimeSpanString(
				t.toMillis(false),
				now,
				DateUtils.DAY_IN_MILLIS,
				DateUtils.FORMAT_ABBREV_ALL).toString() + ")");


		int i = 0;

	}

}
