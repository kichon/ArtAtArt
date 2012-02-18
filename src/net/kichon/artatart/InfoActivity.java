package net.kichon.artatart;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class InfoActivity extends Activity
{
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.info);

		TextView title = (TextView)findViewById(R.id.title);
		title.setText("Infomaion");

		TextView detail = (TextView)findViewById(R.id.detail);
		detail.setText("Support: info@kichon.net");

		String str = "<a href=\"http://www.tokyoartbeat.com/\">Powerd by Tokyo Art Beat</a>";
		CharSequence charSequence = Html.fromHtml(str);

		TextView powerdby = (TextView)findViewById(R.id.powerdby);
		powerdby.setText(charSequence);
		powerdby.setMovementMethod(LinkMovementMethod.getInstance());

	}
}