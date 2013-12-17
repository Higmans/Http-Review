package biz.lungo.httpreview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.Activity;
import android.content.Entity;

public class MainActivity extends Activity implements OnClickListener {
	EditText etLogin;
	EditText etPassword;
	Button b;
	RadioGroup rg;
	TextView tvResponse;
	Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_main);
		etLogin = (EditText) findViewById(R.id.editTextLogin);
		etPassword = (EditText) findViewById(R.id.editTextPassword);
		b = (Button) findViewById(R.id.button1);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		tvResponse = (TextView) findViewById(R.id.textViewResponse);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		sendRequestByHttp(rg.getCheckedRadioButtonId());		
	}

	private void sendRequestByHttp(int method) {
		switch (method){
		case R.id.radioGet:			
			new Thread(new Runnable() {				
				@Override
				public void run() {
					HttpClient client = new DefaultHttpClient();
					String requestGet = "http://httpbin.org/get?Login=" + 
										etLogin.getText().toString() + "&" + 
										"Password?" + etPassword.getText().toString();
					HttpGet get = new HttpGet(requestGet);
					HttpResponse response = null;
					try {
						response = client.execute(get);
					} catch (Exception e) {
						e.printStackTrace();
					} 
					HttpEntity entity = response.getEntity();
					InputStream is = null;
					try {
						is = entity.getContent();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String responseString = "";
					try {
						responseString = inputStreamToString(is);
					} catch (IOException e) {
						e.printStackTrace();
					}
					final String  rtst = responseString;
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvResponse.setText(rtst);
							
						}
					});
					
				}
			}).start();
			
			break;
		case R.id.radioPost:
			new Thread(new Runnable() {				
				@Override
				public void run() {
					
				}
			}).start();
			break;
		}
	}
	private String inputStreamToString(InputStream in) throws IOException {
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
	    StringBuilder stringBuilder = new StringBuilder();
	    String line = null;

	    while ((line = bufferedReader.readLine()) != null) {
	        stringBuilder.append(line + "\n");
	    }

	    bufferedReader.close();
	    return stringBuilder.toString();
	}
}