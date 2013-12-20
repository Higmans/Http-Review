package biz.lungo.httpreview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
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
					String requestGet = "http://ukr.net";
					/*String requestGet = "http://httpbin.org/get?Login=" + 
										etLogin.getText().toString() + "&" + 
										"Password?" + etPassword.getText().toString();*/
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
					Intent i = new Intent(activity, WebViewActivity.class);
					i.putExtra("code", rtst);
					startActivity(i);
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
					HttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost("http://httpbin.org/post");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			        nameValuePairs.add(new BasicNameValuePair("Login", etLogin.getText().toString()));
			        nameValuePairs.add(new BasicNameValuePair("Password", etPassword.getText().toString()));
			        nameValuePairs.add(new BasicNameValuePair("data", "some data"));
			        try {
						post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
			        HttpResponse response = null;
			        try {
						response = client.execute(post);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
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
					final HttpResponse finalResponse = response;
					final String  rtst = responseString;
					activity.runOnUiThread(new Runnable() {						
						@Override
						public void run() {
							tvResponse.setText(finalResponse.getStatusLine().toString());
						}
					});
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