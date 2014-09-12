package com.ancabi.avisosmilanuncios;

import java.io.IOException;
import java.util.ArrayList;

import buscador.Buscador;
import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class ServiceBuscador extends Service{
	
	private static ServiceBuscador instance  = null;
	final Messenger mMessenger = new Messenger(new IncomingHandler());
    static Messenger MessMain;
    private Buscador buscador;
    
    
	public ServiceBuscador(){
		super();
	}
	
	public static boolean isRunning() { 
	      return instance != null; 
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	static class IncomingHandler extends Handler { // Handler of incoming messages from clients.
        @Override
        public void handleMessage(Message msg) {
            
                MessMain=msg.replyTo;
                
                ///super.handleMessage(msg);
            
        }
    }
	
	private void sendMessageToUI(Bundle b) {

		Message msg = Message.obtain();
		msg.setData(b);
		try {
			
			if(!MainActivity.active){
				
				
				Intent dialogIntent = new Intent(this, MainActivity.class);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				this.startActivity(dialogIntent);
				
			}
			
			MessMain.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            
    }
    

	@Override
	public void onCreate() {
		Toast.makeText(getApplicationContext(), "Servicio ServiceBuscador creado", Toast.LENGTH_LONG).show();
		
		instance=this;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Servicio ServiceBuscador destruido", Toast.LENGTH_LONG).show();
		
		instance = null;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(getApplicationContext(), "Servicio ServiceBuscador iniciado!!", Toast.LENGTH_LONG).show();
		
		try {
			String url=intent.getStringExtra("url");
			buscador = new Buscador(url);
			lanzarNotificacion();
			
			CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 120000) {

		        // This is called every interval. (Every 10 seconds in this example)
		        public void onTick(long millisUntilFinished) {
		            try {
						lanzarNotificacion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }

		        public void onFinish() {
		        	try {
						lanzarNotificacion();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}        
		            start();
		        }
		     }.start();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	 void lanzarNotificacion() throws InterruptedException{
		
			Connection c=new Connection();
			c.execute();
		 
	 }
	 
	private class Connection extends AsyncTask<Context,Void, ArrayList<ArrayList<String>>> {

			@Override
			protected ArrayList<ArrayList<String>> doInBackground(Context... params) {
				
				
				try {
					return buscador.leerBusqueda();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

				
			}
			
			@Override
			protected void onProgressUpdate(Void... values) {
				
				
				
		    }
			
			protected void onPostExecute(ArrayList<ArrayList<String>> result) {
				
				Bundle b = new Bundle();
				b.putStringArrayList("nuevos", result.get(0));
				b.putStringArrayList("rebajados", result.get(1));
				sendMessageToUI(b);         
		         
		         
		     }
	 
	    }
}
