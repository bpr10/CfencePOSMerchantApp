package c_fence_customer.anipr.com.c_fence_pos;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

public class POSHome extends ActionBarActivity {

	EditText cardNo, amount;
	Button send;
	AlertDialog dialog;
	ProgressDialog pDialog;
	EditText cvv;
    TextView expiery;
    String OTP = "DEFAULT";
    String exp = "";
	protected void onStart() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poshome);
		cardNo = (EditText) findViewById(R.id.card_no);
		cvv = (EditText) findViewById(R.id.cvv);
		expiery = (TextView) findViewById(R.id.expiery);
		amount =(EditText)findViewById(R.id.amount);
		send = (Button) findViewById(R.id.send);
        expiery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exp = "";
                expiery.setText("");
                new DatePickerDialog(POSHome.this, picListner, Calendar.getInstance()
                        .get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cardNo.getText().toString().length() != 0
						&& expiery.getText().toString().length() != 0
						&& cvv.getText().toString().length() != 0&&amount.getText().toString().length()!=0) {
					
					SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
					try {

							pDialog = new ProgressDialog(POSHome.this);
							pDialog.setMessage("Please wait...");
							pDialog.setCancelable(false);
							pDialog.show();

							Long card_no;
							if (cardNo.getText().toString().length() == 16) {
								card_no = Long.parseLong(cardNo.getText().toString());
								ParseQuery<ParseObject> query = ParseQuery
										.getQuery(ParseConstants.cardsObject);
								query.whereEqualTo(ParseConstants.cardNumber, card_no);

								query.findInBackground(new FindCallback<ParseObject>() {

									@Override
									public void done(List<ParseObject> object,
											ParseException e) {
										if (e == null) {

											if (object.size() != 0) {

												if (object.get(0).getBoolean("status")) {
                                                    String objId = object.get(0).getObjectId();
                                                    OTP = String.valueOf((int)(Math.random()*9000)+1000);
                                                    try {
                                                        SmsManager smsManager = SmsManager.getDefault();
                                                        smsManager.sendTextMessage(object.get(0).get(ParseConstants.card_phoneNo) + "", null, getString(R.string.sms_text)+ " "+ OTP, null, null);
                                                        ParseQuery<ParseObject> updateQuery = ParseQuery
                                                                .getQuery(ParseConstants.cardsObject);
                                                        updateQuery.getInBackground(objId,new GetCallback<ParseObject>() {
                                                            @Override
                                                            public void done(ParseObject cardObject, ParseException e) {
                                                                if(e==null){

                                                                    cardObject.put(ParseConstants.lastOtp,Integer.parseInt(OTP));
                                                                    cardObject.saveInBackground();
                                                                    if (pDialog.isShowing()) {
                                                                        pDialog.dismiss();
                                                                    }
                                                                    AlertDialog.Builder passwordBulider = new AlertDialog.Builder(
                                                                            POSHome.this);
                                                                    passwordBulider
                                                                            .setTitle("PASSWORD");
                                                                    passwordBulider
                                                                            .setMessage(getString(R.string.enter_otp));
                                                                    final EditText input = new EditText(
                                                                            POSHome.this);
                                                                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                                                    input.setLayoutParams(lp);
                                                                    passwordBulider
                                                                            .setView(input);
                                                                    passwordBulider.setCancelable(false);
                                                                    passwordBulider
                                                                            .setPositiveButton(
                                                                                    "Submit",
                                                                                    new DialogInterface.OnClickListener() {

                                                                                        @Override
                                                                                        public void onClick(
                                                                                                DialogInterface dialog,
                                                                                                int which) {
                                                                                            String password = input
                                                                                                    .getText()
                                                                                                    .toString();
                                                                                            if (password
                                                                                                    .equals(OTP)) {

                                                                                                AlertDialog.Builder bulider = new AlertDialog.Builder(
                                                                                                        POSHome.this);
                                                                                                bulider.setMessage(
                                                                                                        "The following transaction was Approved on your card : Amt: CCY "+amount.getText().toString()+" , Merchant: XYZ Gizmos")
                                                                                                        .setTitle("POS Response")
                                                                                                        .setPositiveButton("ok", null);
                                                                                                AlertDialog dialog1 = bulider.create();
                                                                                                dialog1.show();
                                                                                            } else {
                                                                                                if (pDialog
                                                                                                        .isShowing()) {
                                                                                                    pDialog.dismiss();
                                                                                                }
                                                                                                AlertDialog.Builder bulider = new AlertDialog.Builder(
                                                                                                        POSHome.this);
                                                                                                bulider.setMessage(
                                                                                                        "You have entered a wrong OTP.")
                                                                                                        .setTitle("POS Response")
                                                                                                        .setPositiveButton("ok", null);
                                                                                                AlertDialog dialog1 = bulider.create();
                                                                                                dialog1.show();

                                                                                            }
                                                                                        }
                                                                                    });
                                                                    AlertDialog passwordDialog = passwordBulider
                                                                            .create();
                                                                    passwordDialog.show();

                                                                }
                                                                else{

                                                                    if (pDialog.isShowing()) {
                                                                        pDialog.dismiss();
                                                                    }
                                                                    AlertDialog.Builder bulider = new AlertDialog.Builder(
                                                                            POSHome.this);
                                                                    bulider.setMessage(
                                                                            "Internal Error")
                                                                            .setTitle("POS Response")
                                                                            .setPositiveButton("ok", null);
                                                                    AlertDialog dialog = bulider.create();
                                                                    dialog.show();

                                                                }
                                                            }
                                                        });
                                                    }catch(Exception i)
                                                    {
                                                        AlertDialog.Builder bulider = new AlertDialog.Builder(
                                                                POSHome.this);
                                                        bulider.setMessage(
                                                                "Could not send OTP. try again later")
                                                                .setTitle("POS Response")
                                                                .setPositiveButton("ok", null);
                                                        AlertDialog dialog = bulider.create();
                                                        dialog.show();
                                                        i.printStackTrace();
                                                    }



												} else {
													if (pDialog.isShowing()) {
														pDialog.dismiss();
													}
													AlertDialog.Builder bulider = new AlertDialog.Builder(
															POSHome.this);
													bulider.setMessage(
															"The following transaction was declined on your card because the card was deactivated by you: Amt: CCY "+amount.getText().toString()+" , Merchant: XYZ Gizmos")
															.setTitle("POS Response")
															.setPositiveButton("ok", null);
													AlertDialog dialog = bulider.create();
													dialog.show();

												}
											} else {
												if (pDialog.isShowing()) {
													pDialog.dismiss();
												}
												AlertDialog.Builder bulider = new AlertDialog.Builder(
														POSHome.this);
												bulider.setMessage(
														"No card found for the given card no")
														.setTitle("POS Response")
														.setPositiveButton("ok", null);
												AlertDialog dialog = bulider.create();
												dialog.show();

											}

										} else {
											if (pDialog.isShowing()) {
												pDialog.dismiss();
											}
                                            AlertDialog.Builder bulider = new AlertDialog.Builder(
                                                    POSHome.this);
                                            bulider.setMessage(
                                                    "Internal Error")
                                                    .setTitle("POS Response")
                                                    .setPositiveButton("ok", null);
                                            AlertDialog dialog = bulider.create();
                                            dialog.show();
										}
									}
								});
							} else {
								if (pDialog.isShowing()) {
									pDialog.dismiss();
								}
								AlertDialog.Builder bulider = new AlertDialog.Builder(
										POSHome.this);
								bulider.setMessage("Please enter a valid card no")
										.setTitle("POS Response")
										.setPositiveButton("ok", null);
								AlertDialog dialog = bulider.create();
								dialog.show();
							}



					} catch (Exception e) {
						AlertDialog.Builder bulider = new AlertDialog.Builder(
								POSHome.this);
						bulider.setMessage("Internal Error")
								.setTitle("POS Response")
								.setPositiveButton("ok", null);
						AlertDialog dialog = bulider.create();
						dialog.show();
												e.printStackTrace();
					}
					sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));



				}else {
					AlertDialog.Builder bulider = new AlertDialog.Builder(
							POSHome.this);
					bulider.setMessage("Please Fill all the Fields")
							.setTitle("POS Response")
							.setPositiveButton("ok", null);
					AlertDialog dialog = bulider.create();
					dialog.show();
					
				}
				
			}
		});
	}
    DatePickerDialog.OnDateSetListener picListner = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, final int year,
                              final int monthOfYear, final int dayOfMonth) {
            Calendar mycal = Calendar.getInstance();
            int noOfTimesCalled =0;
            if (noOfTimesCalled % 2 == 0) {

                Log.d("Working", "datepicker");
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, monthOfYear);
                mycal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if ((mycal.getTimeInMillis() < Calendar.getInstance()
                            .getTimeInMillis())) {
                        Toast.makeText(POSHome.this,
                                "Expiry date can't be set to a past date."+noOfTimesCalled,
                                Toast.LENGTH_SHORT).show();
                    } else {
                          expiery.setText(mycal.get(Calendar.MONTH)+"/"+mycal.get(Calendar.YEAR));
                    exp = mycal.get(Calendar.MONTH)+"/"+mycal.get(Calendar.YEAR);
                    }


            }
            noOfTimesCalled++;

        }
    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_logout) {
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();

		}
		return super.onOptionsItemSelected(item);
	}
}
