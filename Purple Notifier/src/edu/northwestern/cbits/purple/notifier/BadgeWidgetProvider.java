package edu.northwestern.cbits.purple.notifier;

import java.io.IOException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class BadgeWidgetProvider extends PurpleWidgetProvider
{
	public static final String NAME = "BADGE_WIDGET_UPDATE";
	public static final String WIDGET_LAUNCH = "config_widget_badge_launch";

	public static void setupWidget(Context context, int widgetId, Intent intent) 
	{
		Log.e("PN", "SETUP BADGE");
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_badge_widget);
		
		Bundle extras = intent.getExtras();
		AppWidgetManager widgets = AppWidgetManager.getInstance(context);

		String title = extras.getString("title");
		String message = extras.getString("message");
		String image = extras.getString("image");
		String badge = extras.getString("badge");
		
		int color = Color.WHITE;
		
		if (extras.containsKey("color"))
		{
			color = Color.parseColor(extras.getString("color"));
			
			Log.e("PN", "GOT COLOR: " + extras.getString("color") + " " + color);
		}

		double ratio = 0.75;
		
		if (extras.containsKey("fill"))
		{
			try
			{
				ratio = Double.parseDouble(extras.getString("fill") + "");
			}
			catch (ClassCastException e)
			{
				Log.e("PN", "SETTING RATIO MANUALLY");
				ratio = 0.75;
			}
		}
		
		if (ratio < 0.25)
			ratio = 0.25;
		
		Uri imageUri = null;
		
		try
		{
			if (image.trim().length() > 0)
				imageUri = Uri.parse(image);
		}
		catch (NullPointerException e)
		{

		}
		
		try
		{
			Bitmap b = PurpleWidgetProvider.badgedBitmapForUri(context, imageUri, badge, ratio, color);
			
			remoteViews.setImageViewBitmap(R.id.widget_badge_image, b);
		}
		catch (IOException e)
		{
			remoteViews.setImageViewResource(R.id.widget_badge_image, R.drawable.ic_launcher);
		}

		remoteViews.setTextViewText(R.id.widget_badge_title_text, title);
		remoteViews.setTextViewText(R.id.widget_badge_message_text, message);
		
		Intent tapIntent = new Intent(WidgetIntentService.WIDGET_ACTION);
		tapIntent.putExtras(intent);
		tapIntent.putExtra("widget_action", "tap");

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, tapIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_badge_layout, pendingIntent);
		
		try
		{
			widgets.updateAppWidget(widgetId, remoteViews);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}
}
