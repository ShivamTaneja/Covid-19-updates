package com.shivamtaneja.covid_19updates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<HashMap<String, String>> covidList;
    private TextView totalCaseView, graphView, totalDeathsView, totalRecoveriesView;
    private AnimationDrawable animationDrawable;
    private ProgressBar progressBar;
    private String date;
    int[]  globalFigures= new int[3];
    private int sumOfTotalCases = 0, sumOfDeathCases = 0, sumOfRecoveredCases = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.list);
        graphView = findViewById(R.id.graph);

        totalCaseView =findViewById(R.id.totalcases);
        totalDeathsView = findViewById(R.id.totaldeaths);
        totalRecoveriesView = findViewById(R.id.totalrecovereies);

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        covidList = new ArrayList<>();

        //lv.setBackgroundColor(R.drawable.list_selector);

        // init constraintLayout
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        // initializing animation drawable by getting background from constraint layout
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(5000);
        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(2000);

        if(checkInternetConenction())
        {

            new GetCovidList().execute();
        }
        else
        {
            Toast.makeText(this, " Please connect to the internet and again open app", Toast.LENGTH_LONG).show();

        }

    }

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
   //         Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED  ) {
 //           Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
    }

private class GetCovidList extends AsyncTask<Void, Void, Void> {

        @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(MainActivity.this, R.string.Data_downloading,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "https://pomber.github.io/covid19/timeseries.json";
        String jsonStr = sh.makeServiceCall(url);

        //Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null)
        {
            try {

                String[] arrayOfCountries ={"Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda",
                        "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan",

                        "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
                        "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Brazil", "Brunei", "Bulgaria", "Burkina Faso",

                        "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile",
                        "China", "Colombia", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czechia",

                        "Denmark", "Djibouti", "Dominica", "Dominican Republic",

                        "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",  "Eswatini", "Ethiopia",

                        "Fiji", "Finland", "France",

                        "Gabon", "Gambia",  "Georgia",  "Germany", "Ghana", "Greece", "Grenada", "Guatemala",
                        "Guinea", "Guinea-Bissau","Guyana",

                        "Haiti", "Honduras", "Hungary",

                        "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",

                        "Jamaica", "Japan", "Jordan",

                        "Kazakhstan", "Kenya", "Korea, South", "Kuwait", "Kyrgyzstan",

                        "Laos", "Latvia", "Lebanon", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",

                        "Madagascar", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania",
                        "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique",

                        "Namibia", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "North Macedonia",
                        "Norway",

                        "Oman",

                        "Pakistan", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal",

                        "Qatar",

                        "Romania", "Russia", "Rwanda",

                        "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "San Marino", "Saudi Arabia",
                        "Senegal", "Serbia", "Seychelles", "Singapore", "Slovakia", "Slovenia", "Somalia", "South Africa",
                        "Spain", "Sri Lanka", "Sudan", "Suriname", "Sweden", "Switzerland", "Syria",

                        "Tanzania", "Thailand", "Timor-Leste", "Togo", "Trinidad and Tobago", "Tunisia", "Turkey",

                        "Uganda", "Ukraine", "United Kingdom", "US", "Uruguay", "Uzbekistan",

                        "Venezuela", "Vietnam",

                        "Zambia", "Zimbabwe"
                };

                JSONObject jsonObj = new JSONObject(jsonStr);

                for(String country:arrayOfCountries)
                {
                    HashMap<String, String> countryDataMapping = new HashMap<>();
                    JSONArray Country_Data = jsonObj.getJSONArray(country);

                    int i=Country_Data.length()-1;

                    JSONObject countryData = Country_Data.getJSONObject(i);

                    date = countryData.getString("date");
                    String confirmed = countryData.getString("confirmed");
                    if(confirmed!=null)
                        sumOfTotalCases +=Integer.parseInt(confirmed);
                    String deaths = countryData.getString("deaths");
                    if(deaths!=null)
                        sumOfDeathCases +=Integer.parseInt(deaths);
                    String recovered = countryData.getString("recovered");
                    if(recovered!=null)
                        sumOfRecoveredCases +=Integer.parseInt(recovered);

//                    Log.e("recdtttttt" , sumOfTotalCases +"");
//                    Log.e("recddddd" , sumOfDeathCases +"");
//                    Log.e("recdrrrrrrr" , sumOfRecoveredCases +"");
                    Log.e("country",country);
//
                    // adding each child node to HashMap key => value
                    //countryDataMapping.put("date", date);
                    countryDataMapping.put("country", country);
                    countryDataMapping.put("confirmed", "Confirmed Cases - " + confirmed);
                    countryDataMapping.put("recovered", "Recovered Cases - " + recovered);
                    countryDataMapping.put("deaths", "Death cases - " + deaths);

                    // adding country to country list
                    covidList.add(countryDataMapping);
                }


            }
            catch (final JSONException e) {
               // Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                R.string.sorry_msg2 ,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

        } else {
          //  Log.e(TAG, "Couldn't get json from server.");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            R.string.sorry_mfg3,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
        progressBar.setVisibility(View.INVISIBLE);

        totalCaseView.setText(sumOfTotalCases + "");
        totalDeathsView.setText(sumOfDeathCases + "");
        totalRecoveriesView.setText(sumOfRecoveredCases + "");

        graphView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Toast.makeText(getApplicationContext(),"Loading Data " ,Toast.LENGTH_SHORT).show();

            Intent myIntent = new Intent(MainActivity.this, Piechart.class);
            globalFigures[0]=sumOfTotalCases;
            globalFigures[1]=sumOfDeathCases;
            globalFigures[2]=sumOfRecoveredCases;
//                Log.e("krecd" ,sumOfDeathCases+"");
//                Log.e("lrecd" ,sumOfRecoveredCases+"");

            myIntent.putExtra("Figures", globalFigures); //Optional parameters

            MainActivity.this.startActivity(myIntent);
                //startActivity(myIntent);
            }
        });


        ListAdapter adapter = new SimpleAdapter(MainActivity.this, covidList,
                R.layout.list_item, new String[]{ "country","confirmed","deaths","recovered"},
                new int[]{R.id.country, R.id.confirmed, R.id.deaths, R.id.recovered});
        lv.setAdapter(adapter);

        Toast.makeText(getApplicationContext(),"Data fetched till this " + date + "(GMT+5.5)",Toast.LENGTH_SHORT).show();

    }
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.search:
            {
                if(checkInternetConenction())
                {
                    Intent intent=new Intent(getApplicationContext(),Search.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, " Please connect to the internet and again open app", Toast.LENGTH_LONG).show();
                }

                return true;
            }
            case R.id.covid:
            {
                Intent intent=new Intent(getApplicationContext(),AboutCovid.class);
                startActivity(intent);
                return true;
            }
            case R.id.about:
            {
                Intent intent=new Intent(getApplicationContext(),AboutUs.class);
                startActivity(intent);
                return true;
            }
            case R.id.help:
            {
                Toast.makeText(getApplicationContext(),R.string.e_mail_shivamtaneja1990_gmail_com,Toast.LENGTH_LONG).show();
                return true;
            }
            case R.id.exit:
            {
                finish();
                return true;
            }
           default:
                return true;
        }
    }
}




