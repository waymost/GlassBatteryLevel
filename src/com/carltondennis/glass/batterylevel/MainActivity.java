package com.carltondennis.glass.batterylevel;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	
	private List<Card> cards;
	private CardScrollView view;
	
	private Card levelCard;
	private Card healthCard;
	private Card statusCard;
	private Card tempCard;
	private Card voltCard;
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context c, Intent i) {
			MainActivity.this.receivedBroadcast(i);
		}
	};

	class CardListScrollAdapter extends CardScrollAdapter {
        @Override
        public int findIdPosition(Object id) {
            return -1;
        }

        @Override
        public int findItemPosition(Object item) {
            return cards.indexOf(item);
        }

        @Override
        public int getCount() {
            return cards.size();
        }

        @Override
        public Object getItem(int position) {
            return cards.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return cards.get(position).toView();
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		cards = new ArrayList<Card>();

		levelCard = new Card(this);
		levelCard.setInfo(R.string.info_bat_level);
		cards.add(levelCard);

		healthCard = new Card(this);
		healthCard.setInfo(R.string.info_bat_health);
		cards.add(healthCard);

		statusCard = new Card(this);
		statusCard.setInfo(R.string.info_bat_status);
		cards.add(statusCard);
		
		tempCard = new Card(this);
		tempCard.setInfo(R.string.info_bat_temp);
		cards.add(tempCard);
		
		voltCard = new Card(this);
		voltCard.setInfo(R.string.info_bat_voltage);
		cards.add(voltCard);
		
		view = new CardScrollView(this);
        CardListScrollAdapter adapter = new CardListScrollAdapter();
        view.setAdapter(adapter);
        view.activate();
        setContentView(view);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mBatInfoReceiver, new IntentFilter(
		        Intent.ACTION_BATTERY_CHANGED));
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mBatInfoReceiver);
	}
	
	private void receivedBroadcast(Intent i) {
		// get battery stats
		int level          = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale          = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int health         = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
		int plugged        = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		int status         = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		int temp           = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
		int voltage        = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

		Log.i("MainActivity",level + " " + scale + " " + health + " " + plugged + " " + status + " " + temp + " " + voltage);
		
		levelCard.setText(getPercentageString(level,scale));
		healthCard.setText(getHealthString(health));
		statusCard.setText(getStatusString(plugged,status));
		tempCard.setText(getTemperatureString(temp));
		voltCard.setText(getVoltageString(voltage));
		
		view.updateViews(true);
	}
	
	private String getPercentageString(int level, int scale) {
		// calculate battery percentage, get numberformat
		double levelDouble = Integer.valueOf(level).doubleValue();
		double scaleDouble = Integer.valueOf(scale).doubleValue();
		double batteryFrac = levelDouble / scaleDouble;
		DecimalFormat df   = new DecimalFormat("#%");
		
		return df.format(batteryFrac);
	}
	
	private String getHealthString(int health) {
		// get battery health, convert to string
		String healthString;
		switch(health) {
			case BatteryManager.BATTERY_HEALTH_COLD:
				healthString = getString(R.string.text_bat_health_cold);
				break;
			case BatteryManager.BATTERY_HEALTH_DEAD:
				healthString = getString(R.string.text_bat_health_dead);
				break;
			case BatteryManager.BATTERY_HEALTH_GOOD:
				healthString = getString(R.string.text_bat_health_good);
				break;
			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				healthString = getString(R.string.text_bat_health_overvolt);
				break;
			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				healthString = getString(R.string.text_bat_health_overheat);
				break;
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				healthString = getString(R.string.text_bat_health_unknown);
				break;
			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				healthString = getString(R.string.text_bat_health_failure);
				break;
			default:
				healthString = getString(R.string.text_bat_error);
				break;
		}
		
		return healthString;
	}
	
	private String getStatusString(int plugged, int status) {
		String statusString;
		switch(status) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			statusString = getString(R.string.text_bat_status_charging) + " via " + getPluggedString(plugged);
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			statusString = getString(R.string.text_bat_status_discharging);
			break;
		case BatteryManager.BATTERY_STATUS_FULL:
			statusString = getString(R.string.text_bat_status_full);
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			statusString = getString(R.string.text_bat_status_not_charging);
			break;
		case BatteryManager.BATTERY_STATUS_UNKNOWN:
			statusString = getString(R.string.text_bat_status_unknown);
			break;
		default:
			statusString = getString(R.string.text_bat_error);
			break;
		}
		
		return statusString;
	}
	
	private String getPluggedString(int plugged) {
		String pluggedString;
		switch(plugged) {
			case BatteryManager.BATTERY_PLUGGED_AC:
				pluggedString = getString(R.string.text_bat_plugged_ac);
				break;
			case BatteryManager.BATTERY_PLUGGED_USB:
				pluggedString = getString(R.string.text_bat_plugged_usb);
				break;
			default:
				pluggedString = "";
				break;
		}
		
		return pluggedString;
	}
	
	private String getTemperatureString(int temp) {
		double degCelsius = Integer.valueOf(temp).doubleValue() / 10.0;
		DecimalFormat df = new DecimalFormat("#.#");
		return df.format(degCelsius) + "\u00b0C";
	}
	
	private String getVoltageString(int voltage) {
		return Integer.valueOf(voltage).toString() + "mV";
	}
	
}
