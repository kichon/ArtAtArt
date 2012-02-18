package net.kichon.artatart;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailActivity extends Activity {
	
	String[] period = new String[2];
	String[] open_time = new String[2];
	final private String MY_AD_UNIT_ID = "a14dffff4544d8d";

	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.detail);
		
		Intent intent = getIntent();
		String xml = intent.getStringExtra("xml_list");
		long i = intent.getLongExtra("id", 0L);
		
		boolean flag = false;

    	Pattern pattern = Pattern.compile( ">[ \t\n\f\r]*<" );
        Matcher matcher = pattern.matcher(xml);
        xml = matcher.replaceAll( "><" );
        
    	XmlPullParser a = Xml.newPullParser();
    	try {
    		a.setInput(new StringReader(xml));
    	} catch (XmlPullParserException e) {
    		e.printStackTrace();
    	}
    	
    	int eventType = 0;
    	try {
    		eventType = a.getEventType();
    	} catch (XmlPullParserException e) {
    		e.printStackTrace();
    	}
    	
    	int event_num = 0;
    	int event_type = 99;
    	String image_src = null;
    	while (eventType != XmlPullParser.END_DOCUMENT) {
    		if (eventType == XmlPullParser.START_TAG) {
    			if (a.getName().equals("Event")) {
    				if (i == event_num) {
    					flag = true;
    				} else {
    					flag = false;
    				}
    				event_num++;
    			}
    			if (flag) {
    				if (a.getDepth() == 3 && a.getName().equals("Name")) {
    					try {
    						TextView title = (TextView)findViewById(R.id.title);
    						title.setText(a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getDepth() == 4 && a.getName().equals("Name")) {
    					TextView place = (TextView)findViewById(R.id.place);
    					try {
    						place.setText("会場: " + a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getDepth() == 4 && a.getName().equals("Address")) {
    					TextView address = (TextView)findViewById(R.id.address);
    					try {
    						address.setText(a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getDepth() == 4 && a.getName().equals("OpeningHour")) {
    					try {
    						open_time[0] = a.nextText();
    					} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getDepth() == 4 && a.getName().equals("ClosingHour")) {
    					try {
    						open_time[1] = a.nextText();
    					} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				}
    				
    				else if (a.getName().equals("DateStart")) {
    					try {
    						period[0] = a.nextText();
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getName().equals("DateEnd")) {
    					try {
    						period[1] = a.nextText();
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getName().equals("Media")) {
    					TextView media = (TextView)findViewById(R.id.media);
    					try {
    						media.setText(a.nextText());
    					} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}						
    				} else if (a.getName().equals("Description")) {
    					TextView desc = (TextView)findViewById(R.id.desc);
    					
    					try {
    						desc.setText("詳細:\n\n" + a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getName().equals("Image") && Integer.valueOf(a.getAttributeValue(1)) == 170) {
    					image_src = a.getAttributeValue(0);
    					ImageView img = (ImageView)findViewById(R.id.img);
    					
						try {
							URL url = new URL(image_src);
	    					InputStream is = url.openStream();
	    					Bitmap bm = BitmapFactory.decodeStream(is);
	    					
	    					img.setImageBitmap(bm);
						} catch (Exception e) {
							e.printStackTrace();
						}
    					
    					
    				} else if (a.getName().equals("Price")) {
    					TextView price = (TextView)findViewById(R.id.price);
    					
    					try {
    						price.setText("入場料: " + a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else if (a.getDepth() == 4 && a.getName().equals("Access")) {
    					TextView access = (TextView)findViewById(R.id.access);
    					try {
							access.setText("アクセス: " + a.nextText());
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
    				} else {
    				}
    			}
    		}
    		try {
    			eventType = a.next();
    		} catch (XmlPullParserException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	TextView time = (TextView)findViewById(R.id.time);
    	time.setText("開館時間: " + open_time[0] + " ~ " + open_time[1]);
    	TextView holding_period = (TextView)findViewById(R.id.period);
    	holding_period.setText("開催期間: " + period[0] + " ~ " + period[1]);
    	
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