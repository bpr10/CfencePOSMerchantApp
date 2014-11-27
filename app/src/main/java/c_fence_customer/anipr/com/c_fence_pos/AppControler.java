package c_fence_customer.anipr.com.c_fence_pos;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class AppControler extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("appcontroler", "running");
		Parse.initialize(this, "54exU9DzVeENPnZNbZqpGU4zahjMSjtX4n1eBrdj", "9C2q7jmHzi4OsuNVH2uGWypGdWFiyo55MfHpqAAO"); 
		ParseObject testObject = new ParseObject("TestObject");
		testObject.put("foo", "bar");
		testObject.saveInBackground();
		TypefaceUtils.overrideFont(getApplicationContext(), "SERIF",
				"fonts/RobotoCondensed-Regular.ttf");
        Parse.enableLocalDatastore(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        PushService.setDefaultPushCallback(this, MainActivity.class);

	}
}
 