package com.shivamtaneja.covid_19updates;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.shivamtaneja.covid_19updates.Retrofit.ISuggestAPI;
import com.shivamtaneja.covid_19updates.Retrofit.RetrofitClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Search extends AppCompatActivity
{
    private TextView dateTextView, confirmed, deaths, recovered, changeInConfirmedCasesTextView,
            changeInDeathCasesTextView, changeInRecoveredCasesTextView;
    private String date, strCountry = null, strConfirmed = null, strDeaths = null, strRecovered = null;
    private boolean b = true;
    private int changeInConfirmedCases = 0, changeInDeathCases = 0, changeInRecoveredCases = 0;
    private MaterialSearchBar materialSearchBar;
    private ISuggestAPI myAPI;
    private CompositeDisposable compositeDisposable ;
    private PieChartView pieChartView1, pieChartView2;
    private List<String> suggestions = new ArrayList<>();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        compositeDisposable = new CompositeDisposable();

        confirmed = findViewById(R.id.confirmed);
        recovered = findViewById(R.id.recovered);
        deaths = findViewById(R.id.deaths);

        changeInConfirmedCasesTextView = findViewById(R.id.changeInConfirmedCases);
        changeInDeathCasesTextView = findViewById(R.id.changeInDeathCases);
        changeInRecoveredCasesTextView = findViewById(R.id.changeInRecoveredCases);
        dateTextView = findViewById(R.id.date);

        pieChartView1 = findViewById(R.id.chart1);
        pieChartView2 = findViewById(R.id.chart2);
        pieChartView1.setVisibility(View.GONE);
        pieChartView2.setVisibility(View.GONE);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

       AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.search_your_country);

        materialSearchBar = findViewById(R.id.countryName);

        myAPI = RetrofitClient.getInstance().create(ISuggestAPI.class);
        //init search bar
       materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener()
       {
           @Override
           public void onSearchStateChanged(boolean enabled) {

           }

           @Override
           public void onSearchConfirmed(CharSequence text)
           {

    //         Toast.makeText(Search.this, text.toString(),Toast.LENGTH_LONG).show();
               strCountry = text.toString();

               strCountry = strCountry.replaceAll("\\s+", "");
               strCountry = strCountry.replace(".", "");
               strCountry = strCountry.replace(",", "");
               strCountry = strCountry.replace("'", "");
               strCountry = strCountry.replace("-", "");

               if(strCountry.equalsIgnoreCase(getString(R.string.north_korea)))
               {
                   Toast.makeText(getApplicationContext(), R.string.sorry_msg,Toast.LENGTH_LONG).show();
               }
               else if(strCountry.equalsIgnoreCase("Uae"))
               {
                   Toast.makeText(getApplicationContext(),R.string.sorry_msg,Toast.LENGTH_LONG).show();
               }
               else if(strCountry.equalsIgnoreCase("unitedarabemirates"))
               {
                   Toast.makeText(getApplicationContext(),R.string.sorry_msg,Toast.LENGTH_LONG).show();
               }
               else
                  new GetCovidList().execute();
           }

           @Override
           public void onButtonClicked(int buttonCode) {

           }
       });

       materialSearchBar.addTextChangeListener(new TextWatcher()   {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }
           @Override
           public void onTextChanged(CharSequence charSequence , int start, int before, int count) {
               //set suggestion list load  from api
                getSuggestions(charSequence.toString(),
                        "chrome",
                        "en"    );
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

      }

    @Override
    protected void onDestroy() {
       compositeDisposable.clear();
        super.onDestroy();
    }

    private void getSuggestions(String query, String client, String language)
      {
            compositeDisposable.add(
                    myAPI.getSuggestFromGoogle(query, client, language)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //Here we will retrieve json array
                            if(suggestions.size()>0)suggestions.clear();
                            JSONArray mainJson = new JSONArray(s);
                            JSONArray suggestArray = new JSONArray(mainJson.getString(1));
                            //here we will use gson to convert json array to object
                            suggestions = new Gson().fromJson(mainJson.getString(1),
                                    new TypeToken<List<String>>(){}.getType());
                            //update
                            materialSearchBar.updateLastSuggestions(suggestions);


                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(Search.this,""  + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
            );
      }

    private class GetCovidList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://pomber.github.io/covid19/timeseries.json";
            String jsonStr = sh.makeServiceCall(url);

           // Log.e("TAGgu", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    String[] arrayOfCountries = {"Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua and Barbuda",
                            "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan",

                            "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin",
                            "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Brazil", "Brunei", "Bulgaria", "Burkina Faso",

                            "Cabo Verde", "Cambodia", "Cameroon", "Canada", "Central African Republic", "Chad", "Chile",
                            "China", "Colombia", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czechia",

                            "Denmark", "Djibouti", "Dominica", "Dominican Republic",

                            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Eswatini", "Ethiopia",

                            "Fiji", "Finland", "France",

                            "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala",
                            "Guinea", "Guinea-Bissau", "Guyana",

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
                    for(String country : arrayOfCountries)
                    {
                        String tempStr = country;
                        tempStr = tempStr.replaceAll("\\s+", "");
                        tempStr = tempStr.replace(".", "");
                        tempStr = tempStr.replace(",", "");
                        tempStr = tempStr.replace("'", "");
                        tempStr = tempStr.replace("-", "");

                        if (strCountry.equalsIgnoreCase("usa"))
                            strCountry = "us";
                        if (strCountry.equalsIgnoreCase("Southkorea"))
                            strCountry = "Koreasouth";
                        if (strCountry.equalsIgnoreCase("UK"))
                            strCountry = "UnitedKingdom";

//                        Log.e("country", strCountry);
//                        Log.e("country", strCountry + "  " + country);

                        if (strCountry.equalsIgnoreCase(tempStr))
                        {
                            JSONArray Country_Data = jsonObj.getJSONArray(country);
                            int i = Country_Data.length() - 1;
                            JSONObject countryData = Country_Data.getJSONObject(i);

                            date = countryData.getString("date");
                            strConfirmed = countryData.getString("confirmed");
                            strDeaths = countryData.getString("deaths");
                            strRecovered = countryData.getString("recovered");

                            int j = Country_Data.length() - 2;

                            changeInConfirmedCases = Integer.parseInt(strConfirmed) - Integer.parseInt(Country_Data.getJSONObject(j).getString("confirmed"));
                            changeInDeathCases = Integer.parseInt(strDeaths) - Integer.parseInt(Country_Data.getJSONObject(j).getString("deaths"));
                            changeInRecoveredCases = Integer.parseInt(strRecovered) - Integer.parseInt(Country_Data.getJSONObject(j).getString("recovered"));

                            b = false;
                            break;
                        }
                    }

                }
                catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Sorry for the error, soon we will fix it",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get Data from server. Try after sometime",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if(!b)
            {
                confirmed.setText("Confirmed Cases - " + strConfirmed);
                deaths.setText( "Death cases - " + strDeaths);
                recovered.setText("Recovered Cases - "+ strRecovered);
                dateTextView.setText("Data as on " + date + ", there are");

                List<SliceValue> pieData1 = new ArrayList<>();
                pieData1.add(new SliceValue(Integer.parseInt(strConfirmed), Color.parseColor("#CAB607")));
                pieData1.add(new SliceValue(Integer.parseInt(strDeaths), Color.parseColor("#B71C1C")));
                pieData1.add(new SliceValue(Integer.parseInt(strRecovered), Color.parseColor("#1A6742")));
          //      pieChartData1.setHasLabels(true).setValueLabelTextSize(12);
                PieChartData pieChartData1 = new PieChartData(pieData1);
                pieChartView1.setPieChartData(pieChartData1);

                List<SliceValue> pieData2 = new ArrayList<>();
                pieData2.add(new SliceValue(Math.abs(changeInConfirmedCases), Color.parseColor("#CAB607")));
                pieData2.add(new SliceValue(Math.abs(changeInDeathCases), Color.parseColor("#B71C1C")));
                pieData2.add(new SliceValue(Math.abs(changeInRecoveredCases), Color.parseColor("#1A6742")));
     //           pieChartData2.setHasLabels(true).setValueLabelTextSize(12);
                PieChartData pieChartData2 = new PieChartData(pieData2);
                pieChartView2.setPieChartData(pieChartData2);

                pieChartView1.setVisibility(View.VISIBLE);
                pieChartView2.setVisibility(View.VISIBLE);


                if(changeInConfirmedCases>=0)
                {
                    changeInConfirmedCasesTextView.setText(changeInConfirmedCases + " increase in confimed cases");
                }
                else
                {
                    changeInConfirmedCasesTextView.setText(Math.abs(changeInConfirmedCases) + " decrease in confimed cases");
                }
                if(changeInDeathCases>=0)
                {
                    changeInDeathCasesTextView.setText(changeInDeathCases + " increase in death cases");
                }
                else
                {
                    changeInDeathCasesTextView.setText(Math.abs(changeInDeathCases) + " decrease in death cases");
                }
                if(changeInRecoveredCases>=0)
                {
                    changeInRecoveredCasesTextView.setText(changeInRecoveredCases + " increase in recovered Cases");
                }
                else
                {
                    changeInRecoveredCasesTextView.setText(Math.abs(changeInRecoveredCases) + " decrease in recovered Cases");
                }

              //  Toast.makeText(getApplicationContext(), "Data fetched till " + date, Toast.LENGTH_SHORT).show();

                b=true;
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please Enter correct country name", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

