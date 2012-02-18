package net.kichon.artatart;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ArtAtArt extends Activity {
	private LocationManager locationService_;
	private ArrayAdapter<String> adapter;
	private String s;
	private ProgressDialog progressDialog;
	private Location location;
	private ListView list;
	private Handler mHandler = new Handler();
	final private String MY_AD_UNIT_ID = "a14dffff4544d8d";
	private static final int MENU1_ID = Menu.FIRST;
	private static final int MENU2_ID = Menu.FIRST+1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        locationService_ = (LocationManager)getSystemService(LOCATION_SERVICE);
 
        Criteria criteria = new Criteria();

        final String bestProvider_ = locationService_.getBestProvider(criteria, true);
        location = locationService_.getLastKnownLocation(bestProvider_);
       
        list = (ListView)findViewById(R.id.listview_id);
        adapter = new ArrayAdapter<String>(this, R.layout.list);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("処理を実行中です...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        
        final LocationListener listener = new LocationListener() {
        	
        	@Override
        	public void onLocationChanged(final Location location) {

        		(new Thread(runnable)).start();
        	}

        	@Override
			public void onProviderDisabled(String provider) {
			}
			@Override
			public void onProviderEnabled(String provider) {
			}
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
        };

        //15秒(15000msec)か1m以上の間隔を空けて送信する。
        //locationService_.requestLocationUpdates(bestProvider_, 15000, 1, listener);

        //1時間か500m以上の間隔を空けて送信する。
        locationService_.requestLocationUpdates(bestProvider_, 3600000, 500, listener);

        
        //locationService_.requestLocationUpdates(bestProvider_, 0, 0, listener);
        
        //ListView選択後の処理
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
				intent.putExtra("xml_list", s);
				intent.putExtra("id", id);
				startActivity(intent);
			}
        	
        });
        
    	//広告表示
    	printAd();        

    }

    private Runnable runnable = new Runnable() {
    	@Override
    	public void run() {
    		
    		//1秒間待たせる
    		try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			Log.e("Runnable", "InterruputedException");
    		}
    		
       		BigDecimal l1 = new BigDecimal(String.valueOf(location.getLatitude()));
       		BigDecimal l2 = new BigDecimal(String.valueOf(location.getLongitude()));
            		
       		double latitude = l1.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
       		double longitude = l2.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            
       		String url = "http://www.tokyoartbeat.com/list/event_searchNear?Latitude=" + Double.toString(latitude) + "&Longitude=" + Double.toString(longitude) + "&free=1&SearchRange=3000m";
       		//String url = "http://www.tokyoartbeat.com/list/event_searchNear?Latitude=35.671208&Longitude=139.76517&free=1&SearchRange=3000m";

       		s = getXml(url);
            		
           	XmlPullParser a = Xml.newPullParser();
           	try {
        		a.setInput(new StringReader(s));
        	} catch (XmlPullParserException e) {
        		e.printStackTrace();
        	}
                	
        	int eventType = 0;
         	try {
        		eventType = a.getEventType();
        	} catch (XmlPullParserException e) {
        		e.printStackTrace();
        	}
                	

        	while (eventType != XmlPullParser.END_DOCUMENT) {
        		if (eventType == XmlPullParser.START_DOCUMENT) {
        			//txt.append("Start document\n");
        		} else if (eventType == XmlPullParser.END_DOCUMENT) {
            		//txt.append("End document\n");
        		} else if (eventType == XmlPullParser.START_TAG) {
                			
        			if (a.getName().equals("Name")) {
        				try {
							if (a.getDepth() == 3) {
								adapter.add(a.nextText());
							}
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
        			}
              	} else if (eventType == XmlPullParser.END_TAG) {
              	} else if (eventType == XmlPullParser.TEXT) {
                			
        		}

                		
        		try {
        			eventType = a.next();
        		} catch (XmlPullParserException e) {
        			e.printStackTrace();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}

        	mHandler.post(new Runnable() {
        		public void run() {
                	list.setAdapter(adapter);
        		}
        	});
        	
	
    		progressDialog.dismiss();
    	}
    };
    
	private String createUrl(Location location) {
		return null;
	}

	private String getXml(String url) {
		try {
			HttpGet method = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			
			method.setHeader("Connection", "Keep-Alive");
			 
			HttpResponse response = client.execute(method);
			int status = response.getStatusLine().getStatusCode();
			
			if (status != HttpStatus.SC_OK) {
				throw new Exception("");
			}
			
			return EntityUtils.toString(response.getEntity(), "UTF-8");
			
		} catch(Exception e) {
			return null;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		boolean result = super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU1_ID, Menu.NONE, R.string.menu1);	
		menu.add(0, MENU2_ID, Menu.NONE, R.string.menu2);
		
		return result;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		case MENU1_ID:
			break;
		case MENU2_ID:
			createInfo();
			break;
		}
		return true;
	}
	
	private void createInfo() {
		Intent info_intent = new Intent(getApplicationContext(), InfoActivity.class);
		info_intent.putExtra("id", "hogehoge");
		startActivity(info_intent);		
	}

	//広告表示(AdMob)
	private void printAd() {
        // Create the adView
        AdView adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout);
        // Add the adView to it
        layout.addView(adView);
        //AdRequest request = new AdRequest();
        //request.setTesting(true);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        //adView.loadAd(request);
	}
}