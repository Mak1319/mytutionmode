package io.mainak.mymode;
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.provider.*;
import android.service.quicksettings.*;
import android.widget.*;

public class MakQSTile extends TileService
{


	

	@Override
	public void onTileAdded()
	{
		NotificationManager notificationManger= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (!notificationManger.isNotificationPolicyAccessGranted())
		{getNotificationPermission(notificationManger);}

		boolean settingsCanWrite = Settings.System.canWrite(getApplicationContext());
		if (!settingsCanWrite)
		{
			getChangeSettionsPermission();
		}
		super.onTileAdded();
	}


	@Override
	public void onClick()
	{

		NotificationManager notificationManger= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


		getNotificationPermission(notificationManger);
		getChangeSettionsPermission();

		boolean settingsCanWrite = Settings.System.canWrite(getApplicationContext());


		if (notificationManger.isNotificationPolicyAccessGranted() & settingsCanWrite)
		{


			AudioManager audioManger = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			//audioManger.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			//int mode =audioManger.getRingerMode();
			Tile tile = getQsTile();
			int state = tile.getState();

			Context context= getApplicationContext();
			SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.sharedPrefId), Context.MODE_PRIVATE);
			//AudioManager audioMan= (AudioManager)getSystemService(Context.AUDIO_SERVICE);




			if (state == Tile.STATE_ACTIVE)
			{


				int value =sharedPref.getInt(getString(R.string.userDefaultTimeout), 2 * 60 * 1000);
				
				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, value);



				tile.setState(Tile.STATE_INACTIVE);
				audioManger.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				r.play();
			}
			else
			{


				SharedPreferences.Editor editor = sharedPref.edit();
				String defaultTimeOut=Settings.System.getString(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
				editor.putInt(getString(R.string.userDefaultTimeout), Integer.parseInt(defaultTimeOut));
				editor.apply();

				Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 30 * 60 * 1000);


				tile.setState(Tile.STATE_ACTIVE);
				audioManger.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}

			tile.updateTile();

			super.onClick();
		}
	}


	private void getNotificationPermission(NotificationManager notificationManger)
	{

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
		{
			if (!notificationManger.isNotificationPolicyAccessGranted())
			{
				//startActivityAndCollapse(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityAndCollapse(intent);
			}
		}
	}

	private void getChangeSettionsPermission()
	{
		boolean settingsCanWrite = Settings.System.canWrite(getApplicationContext());
		if (!settingsCanWrite)
		{
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
			{
				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivityAndCollapse(intent);
			}
		}

	}


}
