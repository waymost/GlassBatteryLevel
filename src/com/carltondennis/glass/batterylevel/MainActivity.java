package com.carltondennis.glass.batterylevel;

import android.os.Bundle;
import android.app.Activity;
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
	
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		private List<Card> cards;
		private CardScrollView view;
		
		@Override
		public void onReceive(Context c, Intent i) {
			// get battery stats
			int level          = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale          = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int health         = i.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
			int plugged        = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			int status         = i.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			int temp           = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
			int voltage        = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
			
			Card levelCard = new Card(c);
			levelCard.setText(getPercentageString(level,scale));
			levelCard.setInfo(R.string.info_bat_level);
			
			Card healthCard = new Card(c);
			healthCard.setText(getHealthString(health));
			healthCard.setInfo(R.string.info_bat_health);
			
			Card statusCard = new Card(c);
			statusCard.setText(getStatusString(plugged,status));
			statusCard.setInfo(R.string.info_bat_status);
			
			Card tempCard = new Card(c);
			tempCard.setText(getTemperatureString(temp));
			tempCard.setInfo(R.string.info_bat_temp);
			
			Card voltCard = new Card(c);
			voltCard.setText(getVoltageString(voltage));
			voltCard.setInfo(R.string.info_bat_voltage);
			
			cards = new ArrayList<Card>();
			cards.add(levelCard);
			cards.add(statusCard);
			cards.add(healthCard);
			cards.add(tempCard);
			cards.add(voltCard);
			
			view = new CardScrollView(c);
	        CardListScrollAdapter adapter = new CardListScrollAdapter();
	        view.setAdapter(adapter);
	        view.activate();
	        setContentView(view);
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
				statusString = getString(R.string.text_bat_status_charging) + "(" + getPluggedString(plugged) + ")";
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
			return Integer.valueOf(temp).toString() + "\u2109";
		}
		
		private String getVoltageString(int voltage) {
			return Integer.valueOf(voltage).toString() + "mV";
		}
		
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
	};

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
	
}
