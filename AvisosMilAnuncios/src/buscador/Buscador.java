package buscador;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import android.os.Environment;
import android.util.Log;

/**
 * 
 */

/**
 * @author ancabi
 *
 */
public class Buscador implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4855608621419376443L;

	private File sd=Environment.getExternalStorageDirectory();

	private String url;
	private String tokenPagina="&pagina=";
	private int numPaginas;
	private Document doc;
	private String categoria, busqueda="";
	private File carpeta, archivo;
	private boolean primeraVez=false, primeraEjecucion=true;
	private Map<String, Integer> anuncios=new TreeMap<String, Integer>();
    

    
	
	
	public Buscador(String url) {
		
		this.url = url;
		//obtengo de la url el lugar y tipo de busqueda para
		//utilizarlo como carpeta y los parametros de busqueda
		//para utilizarlos como nombre de fichero para separar
		//de otras busquedas
		getCategoria();
		getBusqueda();
	}
	
	public void getCategoria(){
		
		if(categoria==null){
		
			String inicio=url.substring(url.lastIndexOf("milanuncios.com/")+16);
			
			StringTokenizer st= new StringTokenizer(inicio,"/"); 
			
			categoria=st.nextToken();
			
			
		}
		
		
	}
	
	public void getBusqueda(){
		
		if(busqueda.equals("")){
			StringTokenizer st=new StringTokenizer(url, "=&");
			
			while(st.hasMoreElements()){
				st.nextToken();
				busqueda+=st.nextToken();
				
				if(st.hasMoreElements()){
					busqueda+="_";
				}
				
			}

		}
		
		
		
	}
	
	public void connect() throws IOException{
		//conecto a milanuncios y traigo la primera pagina
		
		doc=Jsoup.connect(url).get();
		
	}
	
	public void connect(int pagina) throws IOException{
		//conecto a milanuncios y traigo la pagina que trae como parametro
		doc=Jsoup.connect(url+tokenPagina+""+pagina).get();
		
	}
	
	
	public void getNumberPages(){
		
		
		Elements e=doc.select(".cat1");

		//se leen la cantidad de paginas que hay en la busqueda
		if(e.text().equals("")){
			numPaginas=0;
		}else if(e.text().length()<15){
			numPaginas=Integer.parseInt(e.text().substring(12, 13));
		}else if(e.text().length()==15){
			//numPaginas=Integer.parseInt(e.text().substring(12, 14));
			numPaginas=2;
		}
		
	}
	
	public void comprobarFichero() throws IOException{
		
		//en este metodo miro si existe la carpeta y sino la creo
		//lo mismo para el fichero y de esta manera compruebo si 
		//es la primera vez que lo busco o no, de esta manera
		//puedo saber si hace falta avisar por cada nueva coincidencia
		
		carpeta=new File(sd.getAbsolutePath()+"/anuncios/"+categoria);
		Log.e("ancabi", carpeta.getAbsolutePath());
		if(!carpeta.exists()){
			carpeta.mkdir();
		}
		
		archivo=new File(carpeta+"/"+busqueda+".txt");
			
		if(!archivo.exists()){
			archivo.createNewFile();
			primeraVez=true;
		}else{
			primeraVez=false;
		}
		
		
		
	}
	
	public boolean buscarAnuncioEnDisco(String anuncio) throws IOException{
		
		
		//leo desde el fichero los anuncios que hay guardados
		//si esta devuelvo true y si no esta false
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		String linea;//, temp;
		//StringTokenizer st;
		
		while((linea=br.readLine())!=null){
			
			//st=new StringTokenizer(linea, " ");
			
			//temp=st.nextToken();
			
			if(anuncio.equals(linea)){
				
				br.close();
				fr.close();
				
				return true;
			}
			
		}
		
		br.close();
		fr.close();
		
		return false;
		
	}
	
	
	
	private void guardarAnuncio(String anuncio, int precio) throws IOException {
		
		//guardo al final del fichero el id del anuncio
		FileWriter fichero = new FileWriter(archivo, true);
        PrintWriter pw = new PrintWriter(fichero);

        pw.println(anuncio+"_"+precio);
        
        pw.close();
        fichero.close();
		
	}

	/*private static Session crearSesion() {
        Session session = Session.getInstance(p,
          new javax.mail.Authenticator() {
            @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, pass);
                }
          });
        return session;
    }  */
	
	public ArrayList<ArrayList<String>> leerBusqueda() throws IOException {
		
		ArrayList<String> nuevos=new ArrayList<String>();
		ArrayList<String> rebajados=new ArrayList<String>();
		
		ArrayList<ArrayList<String>> listados=new ArrayList<ArrayList<String>>(2);
		
		listados.add(nuevos);
		listados.add(rebajados);
		
		//primero compruebo si el fichero existe y si es la primera vez
		comprobarFichero();
		
		connect();
		getNumberPages();
		
		if(primeraEjecucion){
			cargarAnuncios();
			primeraEjecucion=false;
		}
		
		//voy conectadome a las paginas del anuncio
		for(int y=0; y<=numPaginas; y++){
			
			//cuando no sea la primera pagina me descargo la siguiente
			if(y!=0){
				connect(y+1);
			}
		
			//busco las clases donde vienen los id
			Elements x5=doc.select(".x5");
			
			Elements pr=doc.select(".pr");
			
			//recorro los id's y los compruebo
			for(int x=0; x<x5.size(); x++){
				
				String anuncio=x5.get(x).text();
				String precioSinSigno=pr.get(x).text().substring(0, pr.get(x).text().length()-8);
				precioSinSigno=precioSinSigno.replace(".", "");
				int precio=Integer.parseInt(precioSinSigno);
				
				if(!anuncios.containsKey(anuncio)){
					
					guardarAnuncio(anuncio, precio);
					anuncios.put(anuncio, precio);
					
					if(!primeraVez){
						//mando mail
						
					    //enviarMail("Nuevo anuncio de "+categoria, anuncio);
						
						nuevos.add("http://www.milanuncios.com/anuncios/"+anuncio+".htm");


					}
					
				}else if(anuncios.get(anuncio) > precio){
					
					anuncios.put(anuncio, precio);
					
					reescribirFichero();
					
					rebajados.add("http://www.milanuncios.com/anuncios/"+anuncio+".htm");

				}
				
			}
			
		}
		
		return listados;
		
		
	}

	private void cargarAnuncios() throws IOException {
		
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		String linea;
		StringTokenizer st;
		
		while((linea=br.readLine())!=null){
			
			st=new StringTokenizer(linea, "_");
			
			String anuncio=st.nextToken();
			int precio=Integer.parseInt(st.nextToken());
			
			anuncios.put(anuncio, precio);
			
		}
		
		br.close();
		fr.close();
		
		
	}
	
	/*private void enviarMail(String asunto, String anuncio){
		
		
		//creamos la sesion
	    Session sesion = crearSesion();
	    
	    try {
	    	
	    	Message mensaje = new MimeMessage(sesion);
		
	    	mensaje.setSubject(asunto);
	    	
	    	mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
		
	    	mensaje.setText("http://www.milanuncios.com/anuncios/"+anuncio+".htm");
		    //Enviamos el Mensaje
		    Transport.send(mensaje);
			
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}*/

	
	public void reescribirFichero() throws NumberFormatException, IOException{
		
		// guardo al final del fichero el id del anuncio
		archivo.delete();
		archivo.createNewFile();
		FileWriter fichero = new FileWriter(archivo, true);
		PrintWriter pw = new PrintWriter(fichero);
		String anuncio;
		int precio;
		Iterator<String> i = anuncios.keySet().iterator();

		while (i.hasNext()) {

			anuncio = i.next();
			precio = anuncios.get(anuncio);

			pw.println(anuncio + "_" + precio);

		}

		pw.close();
		fichero.close();

	}


	
	
	
}
