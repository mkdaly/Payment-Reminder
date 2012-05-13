package net.metamike.paymentreminder;

import java.text.DateFormat;
import java.text.NumberFormat;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.TimeFormatException;
import android.widget.TextView;

public class ViewActivity extends Activity {
	
	private TextView account;
	private TextView due;
	private TextView paid;

	private Time time = new Time();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        
        Intent i = getIntent();
        if (!ListActivity.VIEW_INTENT.equals(i.getAction()) || (i.getExtras() == null)) {
        	//TODO: Make a const.
        	finishActivity(0);
        }
        
        String account = i.getStringExtra(PaymentDBAdapter.KEY_ACCOUNT);
        String amount =  i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_DUE);
        String dueDate =  i.getStringExtra(PaymentDBAdapter.KEY_DATE_DUE);
        String paid =  i.getStringExtra(PaymentDBAdapter.KEY_AMOUNT_PAID);
        String paidDate =  i.getStringExtra(PaymentDBAdapter.KEY_DATE_TRANSFER);
        
        if (TextUtils.isEmpty(account)) {
        	//TODO: Make a const.
        	finishActivity(0);
        	return;
        }
        ((TextView)findViewById(R.id.view_field_account)).setText(account);
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        
        ((TextView)findViewById(R.id.view_field_due)).setText(
        		getAmountText(currency, amount) + getDateText(dueDate));
        
        ((TextView)findViewById(R.id.view_field_paid)).setText(
        		getAmountText(currency, paid)  + getDateText(paidDate));

        //DateFormat date = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        

		
	}
	
	private String getAmountText(NumberFormat currencyFormat, String s) {
		if (TextUtils.isEmpty(s)) {
			return "";
		} else {
			try {
				return currencyFormat.format(Long.valueOf(s)) + " ";
			} catch (NumberFormatException nfe) {
				return "";
			}
		}
	}
	
	private String getDateText(String s) {
		if (TextUtils.isEmpty(s)) {
			return "none";
		} else {
			try {
				time.parse3339(s);
				return "on " + DateUtils.formatDateTime(getApplicationContext(),
						time.toMillis(false),
						DateUtils.FORMAT_ABBREV_RELATIVE);
			} catch (TimeFormatException tfe) {
				return "none";
			}
		}
	}

}
