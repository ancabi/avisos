package com.ancabi.avisosmilanuncios;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;



public class AvisosFragment extends ListFragment {

	private ArrayList<String> list;
    
    private transient ArrayAdapter<String> adapter;
    private transient ListView listView;
    private transient View rootView;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.avisos_fragment, container, false);  
        
        list = new ArrayList<String>();
        
        if(savedInstanceState!=null){
        	
			for(int x=0; x<(savedInstanceState.size()-1); x++){
				
				list.add(savedInstanceState.getString("anuncio"+x));
			}
        	
        }
        	
        
        
        adapter = new ArrayAdapter<String>(inflater.getContext(), R.layout.fila_avisos, list);
        
        setListAdapter(adapter);
        
        ImageButton trash=(ImageButton) rootView.findViewById(R.id.imageButton1);
        
        trash.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				adapter.clear();
				
				adapter.notifyDataSetChanged();
				
			}
		});
        
        Button buttonOne = (Button) rootView.findViewById(R.id.button1);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	((MainActivity) v.getContext()).putaso();
            }
        });
        
        Button buttonTwo = (Button) rootView.findViewById(R.id.button2);
        buttonTwo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	((MainActivity) v.getContext()).checkIfServiceIsRunning();
            }
        });
         
        return rootView;
    }
	
	
	public void addItem(String anuncio){

		
		adapter.add(anuncio);
		
		adapter.notifyDataSetChanged();
		
	}


	public void addItems(ArrayList<String> result, final MainActivity mainActivity) {
		
		
        
        adapter.addAll(result);
        
        
        adapter.notifyDataSetChanged();
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Toast.makeText(mainActivity, ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse((String) ((TextView) view).getText()));
				startActivity(intent);
				
				((TextView) view).setTextColor(Color.RED);
				
			}
        	
        	
        	
		});
		
	}
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    
	    for(int x=0; x<adapter.getCount(); x++){
	    	
	    	outState.putString("anuncio"+x, adapter.getItem(x));
	    	
	    }


	}



}
