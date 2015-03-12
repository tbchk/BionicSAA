package com.example.camaleon.bionicsaa;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camaleon.bionicsaa.bluetooth.bluetoothCManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        btm = new bluetoothCManager(remoteMAC,btm.pairedByMAC);
        voiceR = SpeechRecognizer.createSpeechRecognizer(this);

        Button speakButton = (Button) findViewById(R.id.btnVoiceRecognition);


        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

    }
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Control por voz");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            int breaker = 0;
            byte[] orden = {0};
            for (int m=0; m < matches.size() && breaker==0; m++){
                switch (matches.get(m)){
                    case "izquierda":
                        orden[0] = 48;
                        breaker=1;
                        break;
                    case "derecha":
                        orden[0] = 49;
                        breaker=1;
                        // do something else
                        break;
                    case "flexion":
                        orden[0] = 50;
                        breaker=1;
                        // i'm lazy, do nothing
                        break;
                    case "extension":
                        orden[0]=51;
                        breaker=1;
                        Log.e("Recon","extension");
                        // i'm lazy, do nothing
                        break;
                    case "centro":
                        breaker=1;
                        orden[0] = 55;
                        // i'm lazy, do nothing
                        break;
                    case "adentro":
                        breaker=54;
                        // i'm lazy, do nothing
                        break;
                    case "afuera":
                        breaker=56;
                        // i'm lazy, do nothing
                        break;
                    case "cerrar":
                        // i'm lazy, do nothing
                        breaker=1;
                        orden[0] = 53;
                        break;
                    case "abrir":
                        orden[0] = 52;
                        breaker=1;
                        // i'm lazy, do nothing
                        break;
                    case "mano":
                        orden[0] = 57;
                        breaker=1;
                        // i'm lazy, do nothing
                        break;

                }
            }
           if(breaker==1)movementSender(orden);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
         * Returns a new instance of this fragment for the given section
         * number.
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void btnConnect(View v){

        int pairStatus = btm.getPairedStatus();

        if (pairStatus == btm.DEVICE_PAIRED)
            Toast.makeText(getBaseContext(), "Está sincronizado con ese dispositivo", Toast.LENGTH_LONG).show();
        else if (pairStatus == btm.DEVICE_UNPAIRED) {
            Toast.makeText(getBaseContext(), "No está sincronizado con ese dispositivo", Toast.LENGTH_LONG).show();
            return;
        }

        //Intento de conexion
        btm.connect();

        int cStatus = btm.getConnectionStatus();


        if(cStatus == btm.CONNECTING)
            Toast.makeText(getBaseContext(), "Está conectando con ese dispositivo", Toast.LENGTH_LONG).show();
        if(cStatus == btm.CONNECTED)
            Toast.makeText(getBaseContext(), "Está conectado con ese dispositivo", Toast.LENGTH_LONG).show();
        if(cStatus == btm.CONNECTION_ERROR)
            Toast.makeText(getBaseContext(), "Error de conexion", Toast.LENGTH_LONG).show();


    }
    public void btnSync(View v){



    }

    public void btnGrade(View v){

        byte[] orden = {0};
        switch (v.getId()) {
            case R.id.btnHombroMenos:
                orden[0] = 48;
                break;
            case R.id.btnHombroMas:
                orden[0] = 49;
                // do something else
                break;
            case R.id.btnCodoMenos:
                orden[0] = 50;
                // i'm lazy, do nothing
                break;
            case R.id.btnCodoMas:
                orden[0]=51;
                // i'm lazy, do nothing
                break;
            case R.id.btnMuMenos:
                // i'm lazy, do nothing
                break;
            case R.id.btnMuMas:
                // i'm lazy, do nothing
                break;
            case R.id.btnPinMenos:
                // i'm lazy, do nothing
                orden[0] = 53;
                break;
            case R.id.btnPinMas:
                orden[0] = 52;
                // i'm lazy, do nothing
                break;
        }


        movementSender(orden);

    }


    private void movementSender(byte[] orden){

        btm.send(orden);
    }



    private bluetoothCManager btm;
    private static String remoteMAC = "20:13:11:26:04:58";
    private static String remoteName = "HC-05";

    private SpeechRecognizer voiceR;
    private static final int REQUEST_CODE = 1234;

}
