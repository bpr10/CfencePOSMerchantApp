package c_fence_customer.anipr.com.c_fence_pos;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	EditText mobileNo,otp;
	Button signIn,forgotPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mobileNo = (EditText)findViewById(R.id.phone_no);
		otp = (EditText)findViewById(R.id.otp);
		signIn = (Button)findViewById(R.id.sign_in);
		forgotPwd = (Button)findViewById(R.id.forgot_pwd);
		Log.d("appcontrolder", "passed");
		signIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((mobileNo.getText().toString().equals("pos"))&&(otp.getText().toString().equals("pos"))){
					Intent i = new Intent(MainActivity.this,POSHome.class);
					startActivity(i);
				}
			
				else{
					AlertDialog.Builder bulider = new AlertDialog.Builder(
							MainActivity.this);
					bulider.setMessage(
							"Invalid Credentials")
							.setTitle("POS Response")
							.setPositiveButton("ok", null);
					AlertDialog dialog = bulider.create();
					dialog.show();
				}
			}
		});
		
		
	}
	
}
