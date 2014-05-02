package com.ancabi.avisosmilanuncios;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoriasFragment extends ListFragment{
	

	private ArrayList<String> list;
    
    private ArrayAdapter<String> adapter;
    private View rootView;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.categorias_fragment, container, false);  
        
        list = new ArrayList<String>();
        
        if(savedInstanceState!=null){
        	
			for(int x=0; x<(savedInstanceState.size()-1); x++){
				
				list.add(savedInstanceState.getString("cat"+x));
			}
        	
        }else{
        	
        	File sd=Environment.getExternalStorageDirectory();
        	File carpeta=new File(sd.getAbsolutePath()+"/anuncios");
        	
        	String[] categorias=carpeta.list();
        	
        	for(int x=0; x<categorias.length; x++){
        		
        		list.add(categorias[x]);
        		
        	}
        	
        }
        	
        
        
        adapter = new ArrayAdapter<String>(inflater.getContext(), R.layout.fila_categorias, list);
        
        setListAdapter(adapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse((String) ((TextView) view).getText()));
				startActivity(intent);
				
				((TextView) view).setTextColor(Color.RED);
				
			}
        	
        	
        	
		});
        
        
        final Button buttonOne = (Button) rootView.findViewById(R.id.buttonStart);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	((MainActivity) v.getContext()).putaso();
            	
            	buttonOne.setVisibility(View.INVISIBLE);
            	
            }
        });
         
        return rootView;
    }
	
	
	public void addItem(String categoria){

		
		adapter.add(categoria);
		
		adapter.notifyDataSetChanged();
		
	}


	public void addItems(ArrayList<String> result, final MainActivity mainActivity) {
		
		
        
        adapter.addAll(result);
        
        
        adapter.notifyDataSetChanged();
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

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
	    	
	    	outState.putString("cat"+x, adapter.getItem(x));
	    	
	    }


	}
	
	

}
