
/*
 *   Copyright (c) 2014 HENRY-PASCAl ELDIN
 *   eldin@eldin.net
 *   Gestion de l'ouverture de portail Motostar
 *   via Raspberry Pi + carte Piface
 *
 * Licensed under the GNU Licence 3
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * V 0.1  11-02-2014  mise en place
 * V 0.1a 18-02-2014  ajout fichier config
 * V 0.1.b
 * V 0.1c 02-03-2014 modif gestion recup url
 * v 0.2a 04-03-2014 modif pour android 4
 * v 0.2b 07-03-2014 recup param asynctask
 *                   mot de passe masqué
 */


package com.app.piportail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.net.NetworkInfo;
import android.content.Context;
import android.widget.Button;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class PiPortail extends ActionBarActivity implements View.OnClickListener {


    // suivi des versions
	public static final String VERSION_PI_PORTAIL = "0.2b";


     public   String URL_SRV;



    // pour le debuggage
    public static final String LOG_TAG = "Pi-Portail";

     // stockage des données sur la partie sdcard
	public static final String PATH_DATA = Environment.getExternalStorageDirectory() + "/pi-portail/";

    // fichier de config
    public static final String PATH_CFG = "pi-portail.cfg";

    // chemin complet du fichier de config
    public static final String PATH_CONFIG = PATH_DATA+PATH_CFG;




    // pour eviter les conflits
    public int action_en_cours=0;

    final Context context = this;

    // la configuration
    public static class ClassConfig {
            public String srv_internet;
            public String srv_intranet;
            public String port;
            public String url;
            public String status;
            public String ouvrir;
            public String login;
            public String pwd;
    }


    public static ClassConfig Config = new ClassConfig();



    // definition des bouttons
    public  Button bouttonquitter ;

    public  Button bouttonstatus;
    public  Button bouttonouvrir;
    public  Button bouttonconfig;


    public String ValeurRetour;


    // La boîte en elle-même
    private ProgressDialog mDialog = null;

    // a la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // on teste si le stockage usb est accessible sinon on sort
    	if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_UNMOUNTED)){
    		Toast.makeText(this, "Pi-Portail\n\nBonjour,\n\n\nPas de support de stockage détecté.", Toast.LENGTH_LONG).show();
    		 finish();
    	 	 }

        Configuration();      // test si une connexion est possible
        	if ( ! isOnline() ) {
    		Toast.makeText(this, "Pi-Portail\n\nBonjour,\n\n\nVous ne pouvez vous connecter !!", Toast.LENGTH_LONG).show();
      		 finish();
    	     }

    	//affiche l'ecran principal
        setContentView(R.layout.activity_pi_portail);
        // affichage du titre + version
        TextView piportail;
        piportail=(TextView)findViewById(R.id.piportail);
        piportail.setText("Pi-Portail V"+VERSION_PI_PORTAIL);

        // affecte les  bouttons
        bouttonquitter = (Button) findViewById(R.id.bouttonquitter);

        bouttonstatus = (Button) findViewById(R.id.bouttonstatus);
        bouttonouvrir = (Button) findViewById(R.id.bouttonouvrir);
        bouttonconfig = (Button) findViewById(R.id.bouttonconf);
        ValeurRetour="                                                                           ";

        //boutton quitter
        bouttonquitter.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	  	Toast.makeText(PiPortail.this, "Pi-Portail\nAu revoir...", Toast.LENGTH_LONG).show();
                 //Pour fermer l'application il suffit de faire finish()
        	    finish();
            }
        });



        // boutton status
        bouttonstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="";
                if (action_en_cours == 0) {
                    action_en_cours = 1;
                        try {
                            msg=RecupStatusPortail();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        action_en_cours = 0;
                } else {
                    msg="Une action est en cours, Recommencez";
                }
                TextView piportail;
                piportail=(TextView)findViewById(R.id.status);
                piportail.setText("Portail : "+msg);

            }


        });

        // boutton ouvrir
        bouttonouvrir.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        	    String msg="";
                if (action_en_cours == 0) {
                    action_en_cours = 1;
                     try {
    	  	      	 msg=OuvrirPortail();
                      } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                     action_en_cours = 0;
                } else {
                    msg="Une action est en cours, Recommencez";
                }
                TextView piportail1;
                piportail1=(TextView)findViewById(R.id.status);
                piportail1.setText("Portail : "+msg);
            }
        });


        // boutton Configuration
        bouttonconfig.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
                MConfiguration();
            }
        });



    }   // fin activité principal



    public void MConfiguration(){
        Intent i = new Intent(this,MajConfiguration.class);
        startActivity(i);
    }




       //  pour tester si la connexion est possible
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //For 3G check
        boolean is3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        //For WiFi Check
        boolean isWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        Log.e("Pi-Portail cnx ", "3g="+is3g+"  Wifi="+isWifi);
        if ( is3g ){
              URL_SRV = Config.srv_internet;
           }
        if ( isWifi ) {
              URL_SRV = Config.srv_intranet;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
                return true;
        }
        return false;
    }



    //page de configuration
    public void Configuration(){
        Log.e("Pi-Portail configuration ",PATH_CONFIG );
        File fcfg = new File(PATH_CONFIG);
          // on teste le repertoire de stockage sinon on le crée
        File   repertoire = new File(	PATH_DATA);
        if ( ! repertoire.isDirectory() ){
               Log.e("Pi-Portail configuration ","on crée le repertoire "+ PATH_DATA );
               repertoire.mkdirs();
        }
            //si le fichier n'existe pas on initialise la config
        if (! fcfg.exists() ){
                Log.e("Pi-Portail configuration ","pas de fichier de config "+ PATH_CONFIG);
                InitConfiguration(PATH_CONFIG);
                MConfiguration();
            }else{
                Log.e("Pi-Portail configuration ","fichier de config existe "+ PATH_CONFIG);
                LireConfiguration(PATH_CONFIG);
        }
    }  // fin configuration




public static void LireConfiguration(String pcfg ){
        Log.e("Pi-Portail LireConfiguration ", pcfg );
        InputStream input = null;
    try {
                    Properties prop = new Properties();
                    input = new FileInputStream(pcfg);
                    // lecture du fichier
                    prop.load(input);
                    // rempli la structure de config
                    Config.srv_internet=prop.getProperty("SRVINTERNET");
                    Config.srv_intranet=prop.getProperty("SRVINTRANET");
                    Config.port=prop.getProperty("PORT");
                    Config.url=prop.getProperty("URL");
                    Config.status=prop.getProperty("CMDSTATUS");
                    Config.ouvrir=prop.getProperty("CMDOUVRIR");
                    Config.login=prop.getProperty("LOGIN");
                    Config.pwd=prop.getProperty("PWD");
                      } catch (IOException ex) {
		                    ex.printStackTrace();
	                  } finally {
		                    if (input != null) {
			                    try {
				                    input.close();
			                    } catch (IOException e) {
				                        e.printStackTrace();
			                    }
		                    }
                      }
}    // fin LireConfiguration

public static void EcrireConfiguration(){
        String pcfg = PATH_CONFIG;
        Log.e("Pi-Portail EcrireConfiguration ", pcfg );
        OutputStream output = null;

        try {
                    Properties prop = new Properties();
                    output = new FileOutputStream(pcfg);
                    prop.setProperty("SRVINTERNET",Config.srv_internet);
                    prop.setProperty("SRVINTRANET", Config.srv_intranet);
                    prop.setProperty("PORT",Config.port);
                    prop.setProperty("URL",Config.url);
                    prop.setProperty("CMDSTATUS",Config.status);
                    prop.setProperty("CMDOUVRIR",Config.ouvrir);
                    prop.setProperty("LOGIN",Config.login);
                    prop.setProperty("PWD",Config.pwd);
                    prop.store(output, null);
                    } catch (IOException io) {
		                io.printStackTrace();
	                } finally {
		            if (output != null) {
			            try {
				            output.close();
			                } catch (IOException e) {
				                e.printStackTrace();
			                }
		                }
                    }
}


private void InitConfiguration(String pcfg){
          Log.e("Pi-Portail InitConfiguration ", pcfg );
          Config.srv_internet="SRV_INTERNET";
          Config.srv_intranet="SRV_INTRANET";
          Config.port="PORT";
          Config.url="URL";
          Config.status="CMD_STATUS";
          Config.ouvrir="CMD_OUVRIR";
          Config.login="LOGIN";
          Config.pwd="PWD";
          EcrireConfiguration();
}



    /*
    affichage du status du portail

    */


    public String RecupStatusPortail() throws ExecutionException, InterruptedException {
          // Affichage du status
         // url de connexion
         String url = "http://"+URL_SRV+":"+Config.port+Config.url+ Config.status;
         // le message a afficher
         String msg = "En attente";
         // le resultat a traité
         String st = null;
         Log.e("Pi-Portail", url);
         // nouvelle tache de type asynctask
         HttpGetter connexion = new HttpGetter();
         // les parametres, a priori doivent pas etre vide
         String[] params = new String[3];
         params[0] = url;
         params[1] = "piportail";
         params[2] = msg;
         Log.e("Pi-Portail", "appel connexion");
         // lancement de l'asynctask
         connexion.execute(params);
         // on attend la find e l'asynctask et on recupere le resultat
         st=connexion.get();
         // on prend que les 2 derniers caracteres
         st=st.substring(2,4);
         Log.e("Pi-Portail", "Appel passé result "+st);
         // on convertit le resultat en message
         if ( "00".equals(st) ) {
              msg="Fermé";
         }else  if ( "10".equals(st) ) {
              msg="Ouvert";
         } else if ( "11".equals(st) ) {
              msg="Ouvert en mouvement";
         } else  {
             msg="Erreur piface";
         }
         // renvoi le message du resultat
         return msg;
    }




    public String OuvrirPortail() throws ExecutionException, InterruptedException {
    // ouvrir
        String url = "http://"+URL_SRV+":"+Config.port+Config.url+Config.ouvrir;
        String status = null;
        String st = null;
        String msg = "en Attente";
        Log.e("Pi-Portail", url);
        HttpGetter connexion = new HttpGetter();
        String[] params = new String[3];
        params[0] = url;
        params[2] = msg;
        connexion.execute(params);
        st=connexion.get();
        st=st.substring(2,4);
        if ( "ON".equals(st)) {
           msg="Ouverture ok";
        }else if  ( "NO".equals(st) ){
           msg="déjà ouvert";
        }else  {
            msg="Erreur piface";
        }
        return msg;
    }

    // la tache asynctask en tache de fond
    public class HttpGetter extends AsyncTask<String, String, String> {

            private ProgressDialog progressDialog;
         // lancé au début de la tache
         @Override
            protected void onPreExecute() {
                //on affiche le message
                progressDialog = ProgressDialog.show(PiPortail.this, "",
                        "Connexion....", true, false);
            }
        // lancé à la fin de la tache
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Log.e("Pi-Portail", "onPostExecute "+s);
        }


        // la tache asynctask proprement dite
        @Override
        protected String doInBackground(String... url) {

            // TODO Auto-generated method stub
                        Log.e("Pi-Portail", "doInBackground "+url[0]);
                        // pour récuperer les resultats
                        StringBuilder builder = new StringBuilder();
                        // pour la connexion au site web
                        HttpClient client = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(url[0]);
                        // le resultat
                        String result;

                        // pour l'authentification http basic
                        String credentials = Config.login + ":" + Config.pwd;
                        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        httpGet.addHeader("Authorization", "Basic " + base64EncodedCredentials);
                        Log.e("Pi-Portail", base64EncodedCredentials);

                        // connexion au site
                        try {
                                HttpResponse response = client.execute(httpGet);
                                // recupere le code de la connexion
                                StatusLine statusLine = response.getStatusLine();
                                int statusCode = statusLine.getStatusCode();
                                Log.e("Pi-Portail","Status code"+ Integer.toString(statusCode));
                                // si code = 200 connexion réussit
                                if (statusCode == 200) {
                                        // recupération de la page web en retour
                                        HttpEntity entity = response.getEntity();
                                        InputStream content = entity.getContent();
                                        BufferedReader reader = new BufferedReader(
                                                        new InputStreamReader(content));
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                                builder.append(line);
                                        }
                                        // on le met dans result
                                        result=Html.fromHtml(builder.toString()).toString();
                                        Log.v("Pi-Portail", "recup: " + result); //response data
                                } else {
                                        Log.e("Pi-Portail", "erreur");
                                        result="Erreur";
                                }
                        } catch (ClientProtocolException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                        //renvoi le resultat
                        return Html.fromHtml(builder.toString()).toString();
                }
    }



// je sais pas trop a quoi ca sert ???
public void onClick(View view) {
		switch (view.getId()) {
			case R.id.cfgraz:
			break;
			case R.id.cfgvalid:
            break;
        }
}  // fin onclick






// fin du programme
}
