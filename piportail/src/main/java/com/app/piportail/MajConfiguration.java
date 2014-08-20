/*
 * Copyright (c) 2014.
 * Henry-Pascal ELDIN
 * eldin@eldin.net
 */

package com.app.piportail;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by eldin on 23/02/14.
 */
public class MajConfiguration extends Activity {

 //pour les valeurs saisies
    public   PiPortail.ClassConfig nConfig = new PiPortail.ClassConfig();

       // recup des zones de saisie
    public EditText edsrvinternet;
    public EditText edsrvintra ;
    public EditText edport ;
    public EditText edurl ;
    public EditText edstatus ;
    public EditText edouvrir ;
    public EditText edlogin ;
    public EditText edpwd ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



      Log.e("Pi-Portail MajConfiguration ", PiPortail.PATH_CONFIG);
      PiPortail.LireConfiguration(PiPortail.PATH_CONFIG);



      //on affiche l'écran de configuration
      setContentView(R.layout.pi_portail_config);
      // init des bouttons
      Button bouttonok;
      Button bouttonretour ;
      bouttonok = (Button)  findViewById(R.id.cfgvalid);
      bouttonretour = (Button) findViewById(R.id.cfgraz);
      // on pre rempli les champs
      ((TextView)findViewById(R.id.edsrvinternet)).setText(PiPortail.Config.srv_internet);
      ((TextView)findViewById(R.id.edsrvintra)).setText(PiPortail.Config.srv_intranet);
      ((TextView)findViewById(R.id.edport)).setText(PiPortail.Config.port);
      ((TextView)findViewById(R.id.edurl)).setText(PiPortail.Config.url);
      ((TextView)findViewById(R.id.edstatus)).setText(PiPortail.Config.status);
      ((TextView)findViewById(R.id.edouvrir)).setText(PiPortail.Config.ouvrir);
      ((TextView)findViewById(R.id.edlogin)).setText(PiPortail.Config.login);
      ((TextView)findViewById(R.id.edpwd)).setText(PiPortail.Config.pwd);

      // récupération de l'EditText grâce à  son ID
      edsrvinternet = (EditText) findViewById(R.id.edsrvinternet);
      edsrvintra =  (EditText) findViewById(R.id.edsrvintra);
      edport =  (EditText) findViewById(R.id.edport);
      edurl = (EditText) findViewById(R.id.edurl);
      edstatus = (EditText) findViewById(R.id.edstatus);
      edouvrir = (EditText) findViewById(R.id.edouvrir);
      edlogin = (EditText) findViewById(R.id.edlogin);
      edpwd = (EditText) findViewById(R.id.edpwd);

      // sur le bouton OK
      bouttonok.setOnClickListener( new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     int modif=0;
                      // on lit ce qui a été saisie
                      nConfig.srv_internet = edsrvinternet.getText().toString();
                      nConfig.srv_intranet = edsrvintra.getText().toString();
                      nConfig.port = edport.getText().toString();
                      nConfig.url = edurl.getText().toString();
                      nConfig.status = edstatus.getText().toString();
                      nConfig.ouvrir = edouvrir.getText().toString();
                      nConfig.login = edlogin.getText().toString();
                      nConfig.pwd = edpwd.getText().toString();
                      Log.e("Pi-Portail MajCfg 1 ", nConfig.srv_internet +"/"+nConfig.srv_intranet );
                      //on met a jour la structure de config
                      PiPortail.Config.srv_internet = nConfig.srv_internet;
                      PiPortail.Config.srv_intranet = nConfig.srv_intranet;
                      PiPortail.Config.port = nConfig.port;
                      PiPortail.Config.url = nConfig.url;
                      PiPortail.Config.status = nConfig.status;
                      PiPortail.Config.ouvrir = nConfig.ouvrir;
                      PiPortail.Config.login = nConfig.login;
                      PiPortail.Config.pwd = nConfig.pwd;
                      Log.e("Pi-Portail MajCfg 2 ",PiPortail.Config.srv_internet +"/"+PiPortail.Config.srv_intranet );
                      PiPortail.EcrireConfiguration();
                      // on quitte la maj de la config
                      finish();
                 }
            });


    bouttonretour.setOnClickListener( new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                    // on quitte la maj de la config
                    finish();
                  }
            });




}



}
