package net.metamike.paymentreminder;

import java.math.BigDecimal;
import java.util.Date;

import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
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
	private EditText amountPaidField;
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
        amountDueField = (EditText)findViewById(R.id.field_amount_due);
        dueDateField = (EditText)findViewById(R.id.field_due_date);
        amountPaidField = (EditText)findViewById(R.id.field_amount_paid);
        transferDateField = (EditText)findViewById(R.id.field_transfer_date);
        confirmationField = (EditText)findViewById(R.id.field_confirmation);
        
        saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener( new View.OnClickListener() {
			@Override public void onClick(View v) { saveEntry(v); } });
        
        cancelButton = (Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener( new View.OnClickListener() {
			@Override public void onClick(View v) { clearFields(v); } });
    }
    
    private void clearFields(View button) {
    	accountField.getText().clear();
    	amountDueField.getText().clear();
    	dueDateField.getText().clear();
    	amountPaidField.getText().clear();
    	transferDateField.getText().clear();
    	confirmationField.getText().clear();
    	accountField.requestFocus();
    }
    
    private void saveEntry(View button) {
    	Bundle b = new Bundle();
    	b.putString(PaymentDBAdapter.KEY_ACCOUNT, accountField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, amountDueField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, dueDateField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, amountPaidField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, transferDateField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, confirmationField.getText().toString());
    	dbAdapter.insertPayment(new Payment(b));
    }

	@Override
	protected void onDestroy() {
		if (dbAdapter != null)
			dbAdapter.close();
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
}