package com.ancabi.avisosmilanuncios;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	AvisosFragment avisos;
	
	ServiceBuscador serviceBuscador;
	
	MainActivity main=this;
	
	Messenger mService = null;
	
	boolean mIsBound=false;
	
	NotificationManager nm;
	
	final Messenger mMessenger = new Messenger(new IncomingHandler());

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle b=msg.getData();
            
            ArrayList<String> nuevos=b.getStringArrayList("nuevos");
            ArrayList<String> rebajados=b.getStringArrayList("rebajados");
            
            if(!nuevos.isEmpty()){
            	avisos.addItems(nuevos, main);
            	Intent notificationIntent = new Intent(main, MainActivity.class);
				
				PendingIntent contentIntent = PendingIntent.getActivity(main, 0, notificationIntent, 0);
				
				Notification notificacion = new NotificationCompat.Builder(main)
		         .setContentTitle("Nuevos anuncios")
		         .setContentText("Tiene "+nuevos.size()+" nuevos anuncios de BMW")
		         .setSmallIcon(R.drawable.ic_launcher)
		         .setContentIntent(contentIntent)
		         .build();
				
				notificacion.defaults |= Notification.DEFAULT_VIBRATE;
				
				notificacion.defaults |= Notification.DEFAULT_SOUND;
				
				notificacion.defaults |= Notification.FLAG_AUTO_CANCEL;
				
				notificacion.defaults |= Notification.FLAG_ONLY_ALERT_ONCE;
				
				nm.notify(1, notificacion);
            }
            
            if(!rebajados.isEmpty()){
            	avisos.addItems(rebajados, main);
            	Intent notificationIntent = new Intent(main, MainActivity.class);
				
				PendingIntent contentIntent = PendingIntent.getActivity(main, 0, notificationIntent, 0);
		        
		        Notification notificacion = new NotificationCompat.Builder(main)
		         .setContentTitle("Anuncios rebajados")
		         .setContentText("Tiene "+rebajados.size()+" nuevos anuncios rebajados de BMW")
		         .setSmallIcon(R.drawable.ic_launcher)
		         .setContentIntent(contentIntent)
		         .build();
				
				notificacion.defaults |= Notification.DEFAULT_VIBRATE;
				
				notificacion.defaults |= Notification.DEFAULT_SOUND;
				
				notificacion.defaults |= Notification.FLAG_AUTO_CANCEL;
				
				notificacion.defaults |= Notification.FLAG_ONLY_ALERT_ONCE;
				
				
				
				nm.notify(2, notificacion);
            }
        }
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	Log.e("puto", "Se llama a onserviceConnected");
            mService = new Messenger(service);
            try {
                Message msg = Message.obtain(null, 1);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
            //textStatus.setText("Attached.");
            
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            //textStatus.setText("Disconnected.");
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if(savedInstanceState==null){
        	avisos=new AvisosFragment();

        }else{
        	avisos = (AvisosFragment) getFragmentManager().getFragment(savedInstanceState,"myfragment");
        }

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		nm=(NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
		
		//checkIfServiceIsRunning();
	}
	
	private void sendMessageToService(int intvaluetosend) {
        if (mIsBound) {
        	Log.e("ancabi", ""+(mService!=null));
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, 1, intvaluetosend, 0);
                    msg.replyTo = mMessenger;
                    Log.e("ancabi", ""+mMessenger);
                    mService.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }
	
	public void putaso(){
		Intent in = new Intent(MainActivity.this,ServiceBuscador.class);
        in.putExtra("url", "http://www.milanuncios.com/bmw-de-segunda-mano/?hasta=6000&anod=2002&kms=160000&combustible=diesel");
		if(!ServiceBuscador.isRunning())
            MainActivity.this.startService(in);
		
		bindService(in, mConnection, Context.BIND_AUTO_CREATE);
		mIsBound=true;
		sendMessageToService(1);
		
		//checkIfServiceIsRunning();
	}
	
	public void checkIfServiceIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
		Log.e("ancabi", ""+ServiceBuscador.isRunning());
        if (ServiceBuscador.isRunning()) {
            doBindService();
            sendMessageToService(1);
        }
    }
	
	void doBindService() {
        bindService(new Intent(this, ServiceBuscador.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        //textStatus.setText("Binding.");
    }
	
	@Override
    public void onSaveInstanceState(Bundle outState){
    	super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState,"myfragment",avisos);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if(position==0)
				return avisos;
			
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			
			
			return rootView;
		}
	}

}
