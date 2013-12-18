package com.example.vcf_test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button btnStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		initListener();
	}

	private void initListener() {
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new myTask().execute();
			}
		});
	}

	class myTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			StringBuffer sb = new StringBuffer();
			File file = new File("/mnt/sdcard/vcard_info.txt");
			BufferedReader br;
			try {
				String line = "";
				br = new BufferedReader(new FileReader(file));
				while ((line = br.readLine()) != null) {
//					sb.append(line);
					try {
						JSONObject jsonObject = new JSONObject(line);
						String strAddress = jsonObject.getString("address");
						sb.append(strAddress);
						JSONArray nameArray = jsonObject.getJSONArray("name");
						for(int i=0;i<nameArray.length();i++)
						{
							String strName = nameArray.optString(i);
							sb.append(strName);
//					    	JSONObject jsonOb = (JSONObject)jsonArray.opt(i); 
//					    	int tel = jsonOb.getInt("tel");
						}
						JSONArray numberArray = jsonObject.getJSONArray("number");
						for (int i = 0; i < numberArray.length(); i++) {
							String strNumber = numberArray.optString(i);
							sb.append(strNumber);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
//					Gson gson = new Gson();
//					User user = gson.fromJson(line, User.class);
				}
				br.close();
				System.out.println(sb.toString());
			} catch (FileNotFoundException e1) {
			} catch (IOException e) {
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}
	private void initViews() {
		btnStart = (Button) this.findViewById(R.id.btnStart);
	}
	private void insertPhone() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断存储卡是否存在
		{
			OutputStreamWriter writer;
			File file = new File(Environment.getExternalStorageDirectory(),"example.vcf");
			// 得到存储卡的根路径，将example.vcf写入到根目录下
			try {
				writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
				// create a contact
				VCardComposer composer = new VCardComposer();
				ContactStruct contact1 = new ContactStruct();
				contact1.name = "John";
				contact1.company = "The Company";
				contact1.addPhone(Contacts.Phones.TYPE_MOBILE, "15651865008",null, true);
				// create vCard representation
				String vcardString;
				vcardString = composer.createVCard(contact1,VCardComposer.VERSION_VCARD21_INT);
				// write vCard to the output stream
				writer.write(vcardString);

				// writer.write("/n"); //add empty lines between contacts
				// repeat for other contacts
				// ...
				writer.close();
				Toast.makeText(this, "已成功导入SD卡中！", Toast.LENGTH_SHORT).show();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (VCardException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "写入失败，SD卡不存在！", Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
