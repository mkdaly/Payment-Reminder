package net.metamike.paymentreminder;

import java.math.BigDecimal;
import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.TimeFormatException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EntryActivity extends Activity {
	
	private EditText accountField;
	private EditText amountDueField;
	private EditText dueDateField;
	private EditText transferDateField;
	private EditText confirmationField;
	//TODO:
	//Reminder paid
	//Reminder xfer
		
	private Button saveButton;
	private Button cancelButton;
	
	
	private PaymentDBAdapter dbAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        dbAdapter = new PaymentDBAdapter(this);
        dbAdapter.open();
        
        accountField = (EditText)findViewById(R.id.field_account);
        amountDueField = (EditText)findViewById(R.id.field_amount);
        dueDateField = (EditText)findViewById(R.id.field_due_date);
        transferDateField = (EditText)findViewById(R.id.field_transfer_date);
        confirmationField = (EditText)findViewById(R.id.field_confirmation);
        
        saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveEntry(v);
			}
		});
        
        cancelButton = (Button)findViewById(R.id.button_cancel);
    }
    
    private void saveEntry(View button) {
    	String account = accountField.getText().toString();
    	//TODO: Should an empty string be allowed
    	if (account == null) {
    		//TODO: show dialog
    		return;
    	}
    	Long amt_due = null;
    	Long dt_due = null;
    	try {
			 amt_due = dbAdapter.convertStringToLongMill(amountDueField.getText().toString());
			 dt_due = dbAdapter.convertDateStringToMilliseconds(dueDateField.getText().toString());
		} catch (NumberFormatException nfe) {
			// TODO: Show Dialog
			nfe.printStackTrace();
		} catch (TimeFormatException tfe) {
			// TODO: Show Dialog
			tfe.printStackTrace();			
		}
    	
    	Long dt_xfer= null;
    	try {
			 dt_xfer = dbAdapter.convertDateStringToMilliseconds(transferDateField.getText().toString());
		} catch (TimeFormatException tfe) {
			// TODO: Show Dialog
			tfe.printStackTrace();			
		}
    	
    	String conf = confirmationField.getText().toString();
    	dbAdapter.insertPayment(account, amt_due, dt_due, null, dt_xfer, conf);
    }

	@Override
	protected void onDestroy() {
		if (dbAdapter != null)
			dbAdapter.close();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
}