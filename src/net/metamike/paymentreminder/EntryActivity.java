package net.metamike.paymentreminder;

import java.math.BigDecimal;
import java.util.Date;

import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EntryActivity extends Activity {
	
	private TextView accountField;
	private TextView amountField;
	private TextView dueDateField;
	private TextView transferDateField;
	private TextView confirmationField;
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
        
        accountField = (TextView)findViewById(R.id.field_account);
        amountField = (TextView)findViewById(R.id.field_amount);
        dueDateField = (TextView)findViewById(R.id.field_due_date);
        transferDateField = (TextView)findViewById(R.id.field_transfer_date);
        confirmationField = (TextView)findViewById(R.id.field_confirmation);
        
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
    	String account = (String)accountField.getText();
    	//TODO: Should an empty string be allowed
    	if (account == null) {
    		//TODO: show dialog
    		return;
    	}		
    }
}