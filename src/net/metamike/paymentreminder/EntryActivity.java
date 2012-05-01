package net.metamike.paymentreminder;

import java.util.regex.Pattern;

import net.metamike.paymentreminder.data.Payment;
import net.metamike.paymentreminder.data.PaymentDBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EntryActivity extends Activity {
	
	private EditText accountField;
	private EditText amountDueField;
	private EditText dueDateField;
	private EditText amountPaidField;
	private EditText transferDateField;
	private EditText confirmationField;
	private TextView entryID;

		
	private Button saveButton;
	private Button cancelButton;
	
	
	private PaymentDBAdapter dbAdapter;
	private Pattern isNumber = Pattern.compile("^-?\\d+$");
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        
        dbAdapter = new PaymentDBAdapter(this);
        dbAdapter.open();
        
        accountField = (EditText)findViewById(R.id.field_account);
        entryID = (TextView)findViewById(R.id.entry_id);
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
			@Override public void onClick(View v) { clearFields(); } });
        
		Intent i = getIntent();
		if (ListActivity.LOAD_INTENT.equals(i.getAction())) {
			loadFromBundle(i.getExtras());
		}
    }
    
    private void clearFields() {
    	accountField.getText().clear();
    	amountDueField.getText().clear();
    	dueDateField.getText().clear();
    	amountPaidField.getText().clear();
    	transferDateField.getText().clear();
    	confirmationField.getText().clear();
    	entryID.setText("");
    	accountField.requestFocus();
    }
    
    private void saveEntry(View button) {
    	Bundle b = new Bundle();
       	b.putString(PaymentDBAdapter.KEY_ID, entryID.getText().toString());
    	b.putString(PaymentDBAdapter.KEY_ACCOUNT, accountField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_DUE, amountDueField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_DATE_DUE, dueDateField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_AMOUNT_PAID, amountPaidField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_DATE_TRANSFER, transferDateField.getText().toString());
		b.putString(PaymentDBAdapter.KEY_CONFIRMATION, confirmationField.getText().toString());
		try {
			dbAdapter.insertPayment(new Payment(b));
			clearFields();
		} catch (IllegalArgumentException iae) {
			//TODO: Show dialog--only happens when account is invalid 
		}
    }
    
    private void loadFromBundle(Bundle b) {
		//TODO: Make a const. and check value in test.
    	if (b == null) {
    		this.finishActivity(0);
    		return;
    	}
    	String key = b.getString(PaymentDBAdapter.KEY_ID);
    	if (TextUtils.isEmpty(key) || !isNumber.matcher(key).matches() || (Payment.NO_ID.compareTo( Long.valueOf(key)) >= 0) ) { 
    		//TODO: Make a const. and check value in test.
    		this.finishActivity(0);
    		return;
    	}
    	
		accountField.setText(b.getString(PaymentDBAdapter.KEY_ACCOUNT));
		entryID.setText(key);
		amountDueField.setText(b.getString(PaymentDBAdapter.KEY_AMOUNT_DUE));
		dueDateField.setText(b.getString(PaymentDBAdapter.KEY_DATE_DUE));
		amountPaidField.setText(b.getString(PaymentDBAdapter.KEY_AMOUNT_PAID));
		transferDateField.setText(b.getString(PaymentDBAdapter.KEY_DATE_TRANSFER));
		confirmationField.setText(b.getString(PaymentDBAdapter.KEY_CONFIRMATION));

    }
    

	@Override
	protected void onDestroy() {
		if (dbAdapter != null)
			dbAdapter.close();
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
    
}