package it.rainbowbreeze.smsforfree.ui;

import it.rainbowbreeze.smsforfree.R;
import it.rainbowbreeze.smsforfree.providers.AimonDictionary;
import it.rainbowbreeze.smsforfree.providers.AimonProvider;
import it.rainbowbreeze.smsforfree.providers.JacksmsProvider;
import it.rainbowbreeze.smsforfree.providers.OldJacksmsService;
import android.app.Activity;
import android.os.Bundle;

public class FrmMain extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        JacksmsService.instance().LOADTEMPLATESERVICES();
//        JACKSMSSERVICE.INSTANCE().LOADUSERSERVICE();
//        JACKSMSSERVICE.INSTANCE().LOADCREDENTIALS();
//        JACKSMSSERVICE.INSTANCE().SENDSMS(62, "+393927686894", "CIAO DA ME CHE SONO IL RE!");
        
    }
}