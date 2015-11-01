package com.example.brian.projectrito;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.api.dto.BaseRiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.api.RateLimit;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.league.League;
import com.robrua.orianna.type.core.league.LeagueEntry;
import com.robrua.orianna.type.core.league.MiniSeries;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String apiKey = "YOUR API KEY HERE";
    public static int matchHistoryLength;
    public static int summonerLP;
    public static String summonerName = "";
    public static String currentRealmsVer; //current version of riot's data server
    public static String screenRotation;
    public static String summonerWL;
    public static String summonerSoloRank;
    public static String gameModePreference, serverRegion;
    public static final String TAG = MainActivity.class.getName();
    public boolean notFirstRun = false;
    public static Date bootRemap;

    public static ListAdapter listAdapter; //array adapter for list of matches
    public static LinearLayout summonerHeader; //header at the top of the list, displaying summoner information
    public static LinearLayout promoSeries;
    public static LinearLayout headerRankedInfo;
    public static LinearLayout splashScreen;
    public static AutoResizeTextView headerSummonerName; //textview on summonerHeader, display summoner name
    public static TextView headerSummonerRank;
    public static TextView headerSummonerWL;
    public static TextView headerSummonerLP;
    public static ImageView summonerRank; //image showing summoner's rank
    public static ImageView promoGame1, promoGame2, promoGame3, promoGame4, promoGame5;
    public static ListView matchHistory; //listview of matches
    public static Button splashStart, splashSettings;
    public static SharedPreferences prefs;
    public static MenuItem searchItem;

    public static Summoner summoner;
    public static League summonerLeague;
    public static LeagueEntry summonerLeagueEntry;
    public static MiniSeries promos;
    public static List<MatchReference> matchRefList;
    public static List<MatchReference> cleanedRefList = new ArrayList<>();
    public static List<Match> matchList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: // intents to preference activity
                Intent i = new Intent(this, MyPreferenceActivity.class);
                startActivity(i);
            case R.id.refresh_button: {
                if (notFirstRun) //if a summoner has already done a search
                {
                    checkValidSummoner check = new checkValidSummoner();
                    check.execute();
                }
            }
            default:
                return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //inflates options menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //on entry of summoner name
                if (!isNetworkAvailable()) { //checks for internet conenction
                    Toast.makeText(MainActivity.this, "Internet?", Toast.LENGTH_SHORT).show();
                } else {
                    summonerName = String.valueOf(searchView.getQuery()); //gets summoner name from search bar
                    checkValidSummoner check = new checkValidSummoner(); // begins to search for summmoner from server
                    check.execute();
                    searchItem.collapseActionView(); //collapses search var
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() { //collapse search bar when keyboard is not visible
            @Override
            public void onFocusChange(View view, boolean b) {
                searchItem.collapseActionView();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "ON CREATE");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        splashStart = (Button) findViewById(R.id.splashStartButton); //initializes splash screen
        splashStart.setOnClickListener(new View.OnClickListener() { //expands search bar when pressed
            @Override
            public void onClick(View view) {
                searchItem.expandActionView();
            }
        });
        splashSettings = (Button) findViewById(R.id.splashSettingsButton);
        splashSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyPreferenceActivity.class);
                startActivity(i);
            }
        });
        splashScreen = (LinearLayout) findViewById(R.id.splashScreen);
        splashScreen.setVisibility(View.VISIBLE);
        gameModePreference = prefs.getString("gameModePreference", "ALL"); //gets data from shared preferences
        serverRegion = prefs.getString("serverRegion", "NA");
        matchHistoryLength = prefs.getInt("matchHistoryLength", 5);
        initialAPISetup(); //updates API settings
        bootsRemapDate(); //creates date object for boots remap
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig); //re-shows summoners info
        if (notFirstRun || !summonerName.equals("")) {
            checkValidSummoner check = new checkValidSummoner();
            check.execute();
        }
        Log.i(TAG, "CONFIG CHANGED");
    }

    private class checkValidSummoner extends AsyncTask<String, Void, Void> {

        Summoner prevSummoner = summoner;
        boolean summonerFound = true;
        boolean serverDown = false;
        boolean invalidKey = false;

        @Override
        protected void onPreExecute() {
            hideKeyboard();
            screenRotation = getRotation(getBaseContext());
            gameModePreference = prefs.getString("gameModePreference", "ALL"); //gets data from shared preferences
            serverRegion = prefs.getString("serverRegion", "NA");
            matchHistoryLength = prefs.getInt("matchHistoryLength", 5);
            initialAPISetup(); //updates API settings
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                summoner = RiotAPI.getSummonerByName(summonerName); //attempts to get summoner from input summoner name
            } catch (APIException e) //called when searched summoner does no exist
            {
                Log.i("MainActivity", String.valueOf(e.getStatus()));
                if (String.valueOf(e.getStatus()).equals("NOT_FOUND")) { //various possible error messages
                    Log.i(TAG, "SUMMONER NOT FOUND");
                    summoner = null;
                    summonerFound = false;
                } else if (String.valueOf(e.getStatus()).equals("SERVICE_UNAVAILABLE") || String.valueOf(e.getStatus()).equals("INTERNAL_SERVER_ERROR")) {
                    Log.i(TAG, "API STATUS DOWN?");
                    summoner = null;
                    summonerFound = false;
                    serverDown = true;
                } else if (String.valueOf(e.getStatus()).equals("UNAUTHORIZED")) {
                    invalidKey = true;
                }
            }
            try {
                matchRefList = summoner.getMatchList();//gets summoner's ranked match list
            } catch (NullPointerException e) { //catches exception when summoner has no matches played
                e.printStackTrace();
                matchRefList = null;
            } catch (APIException e) {
                if (String.valueOf(e.getStatus()).equals("SERVICE_UNAVAILABLE") || String.valueOf(e.getStatus()).equals("INTERNAL_SERVER_ERROR")) {
                    serverDown = true;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (serverDown) { //if the API server is down
                Toast.makeText(MainActivity.this, "API might be down", Toast.LENGTH_SHORT).show();
            } else if (invalidKey) {
                Toast.makeText(MainActivity.this, "Invalid API key?", Toast.LENGTH_SHORT).show();
            } else {
                if (!summonerFound) { //if summoner was not found (does not exist)
                    {
                        Toast.makeText(MainActivity.this, "ERROR: Summoner does not exist?", Toast.LENGTH_SHORT).show();
                        if (prevSummoner != null) { //resets currently shown summoner to previous valid summoner
                            summoner = prevSummoner;
                            summonerName = summoner.getName();
                        }
                    }
                } else {
                    if (matchRefList == null) { //if summoner has never played a ranked game
                        Toast.makeText(MainActivity.this, "ERROR: Summoner has no ranked games played?", Toast.LENGTH_SHORT).show();
                        if (matchHistory != null)
                            matchHistory.setVisibility(View.INVISIBLE); //hides match list while getting data
                        if (prevSummoner != null) {  //resets currently shown summoner to previous valid summoner
                            summoner = prevSummoner;
                            summonerName = summoner.getName();
                        }

                    } else {
                        retrieveSummonerData(); //executes data request if requirements are satisfied
                    }
                }
            }
        }
    }

    public void retrieveSummonerData() {
        hideKeyboard();
        getSummonerData retrieve = new getSummonerData();
        retrieve.execute();
    }

    private class getSummonerData extends AsyncTask<Void, Void, List<Match>> { //async task for retrieving match data from API

        @Override
        protected void onPreExecute() {
            if (notFirstRun) {
                matchHistory.setVisibility(View.INVISIBLE); //hides match list while getting data
            }
            splashScreen.setVisibility(View.GONE); //hides splash screen
        }

        @Override
        protected List<Match> doInBackground(Void... voids) { //
            cleanedRefList.clear();
            currentRealmsVer = BaseRiotAPI.getRealm().getV(); //gets current version of ddragon server
            int matchesFound = 0; //count of how many matches of the specified game mode is found
            Log.i("MainActivity", String.valueOf(matchHistoryLength));
            switch (gameModePreference) {
                case "RANKED_SOLO_5x5": {
                    for (int i = 0; i < matchRefList.size(); i++) {

                        if (matchesFound >= matchHistoryLength) //if match history length is met
                            break;
                        if (matchRefList.get(i).getQueueType().toString().equals("RANKED_SOLO_5x5")) {
                            cleanedRefList.add(matchRefList.get(i));
                            Log.i(TAG, matchRefList.get(i).getQueueType().toString());
                            matchesFound++;
                        }
                    }
                    break;
                }
                case "RANKED_TEAM_5x5": {
                    for (int i = 0; i < matchRefList.size(); i++) {
                        if (matchesFound >= matchHistoryLength)
                            break;
                        if (matchRefList.get(i).getQueueType().toString().equals("RANKED_TEAM_5x5")) {
                            cleanedRefList.add(matchRefList.get(i));
                            Log.i(TAG, matchRefList.get(i).getQueueType().toString());
                            matchesFound++;
                        }
                    }
                    break;
                }
                case "RANKED_TEAM_3x3": {
                    for (int i = 0; i < matchRefList.size(); i++) {
                        if (matchesFound >= matchHistoryLength)
                            break;

                        if (matchRefList.get(i).getQueueType().toString().equals("RANKED_TEAM_3x3")) {
                            cleanedRefList.add(matchRefList.get(i));
                            Log.i(TAG, matchRefList.get(i).getQueueType().toString());
                            matchesFound++;
                        }
                    }
                    break;
                }
                case "ALL": {
                    for (int i = 0; i < matchRefList.size(); i++) {
                        if (matchesFound >= matchHistoryLength)
                            break;
                        cleanedRefList.add(matchRefList.get(i));
                        matchesFound++;
                    }
                    break;
                }
            }

            matchList = new ArrayList<>();
            matchList.clear();
            for (int i = 0; i < cleanedRefList.size(); i++) //converts match reference objects to match objects
            {
                Log.i(TAG, String.valueOf(cleanedRefList.get(i).getMatch().getID()) + " " + String.valueOf(i));
                matchList.add(cleanedRefList.get(i).getMatch());
            }
            List<League> listLeague = new ArrayList<>();
            try {
                listLeague = summoner.getLeagueEntries(); //gets the user's leagues
            } catch (APIException e) {
                Log.i(TAG, String.valueOf(e.getStatus()));
            }
            summonerLeague = null;
            summonerLeagueEntry = null;
            promos = null;
            for (int i = 0; i < listLeague.size(); i++) {
                if (listLeague.get(i).getQueueType().toString().equals("RANKED_SOLO_5x5")) { //gets user's solo queue rank
                    summonerLeague = listLeague.get(i);
                    summonerLeagueEntry = listLeague.get(i).getParticipantEntry();
                    break;
                }
            }
            if (summonerLeague == null) {
                summonerSoloRank = "UNRANKED";
            } else { //gets user's general solo queue ranked data
                summonerSoloRank = summonerLeague.getTier() + " " + summonerLeagueEntry.getDivision();
                summonerWL = summonerLeagueEntry.getWins() + "W/" + summonerLeagueEntry.getLosses() + "L";
                summonerLP = summonerLeagueEntry.getLeaguePoints();
                promos = summonerLeagueEntry.getMiniSeries();
            }
            Log.i(TAG, summonerSoloRank);


            return matchList;
        }

        @Override
        protected void onPostExecute(List<Match> changedMatches) {
            if (cleanedRefList.size() == 0) //called if no matches of the specified game mode is found
            {
                switch (gameModePreference) {
                    case "RANKED_SOLO_5x5":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Solo 5v5 played. Try switching game mode preference", Toast.LENGTH_LONG).show();
                    case "RANKED_TEAM_5x5":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Team 5v5 played. Try switching game mode preference", Toast.LENGTH_LONG).show();
                    case "RANKED_TEAM_3x3":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Team 3v3 played. Try switching game mode preference", Toast.LENGTH_LONG).show();

                }

            }
            listAdapter = new MatchAdapter(MainActivity.this, changedMatches); //creates list adapter for the array of matches

            matchHistory = (ListView) findViewById(R.id.matchHistoryList);
            matchHistory.setVisibility(View.VISIBLE); //unhides the match history view

            if (!notFirstRun) {
                summonerHeader = (LinearLayout) View.inflate(MainActivity.this, R.layout.header_layout, null); //creates summoner banner to the top of the list view
                matchHistory.addHeaderView(summonerHeader, null, false); //appends the top banner onto the top of the list view
                notFirstRun = true;
            }

            matchHistory.setAdapter(listAdapter); //sets the array adapter to the match history list view
            matchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() { //detects on click on a specific item

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String fullURL = ((TextView) view.findViewById(R.id.matchURI)).getText().toString();
                    Intent intent = new Intent(getBaseContext(), MatchDetailsWebView.class);
                    intent.putExtra("matchURL", fullURL);
                    startActivity(intent);
                }
            });
            headerSummonerName = (AutoResizeTextView) findViewById(R.id.headerSummonerName);
            headerSummonerName.setText(summoner.getName());

            headerSummonerRank = (TextView) findViewById(R.id.headerSummonerRank);
            headerSummonerRank.setText(summonerSoloRank);
            headerSummonerWL = (TextView) findViewById(R.id.headerSummonerWL);
            headerSummonerWL.setText(summonerWL);
            headerSummonerLP = (TextView) findViewById(R.id.headerSummonerLP);
            headerSummonerLP.setText(String.valueOf(summonerLP) + " LP");
            headerRankedInfo = (LinearLayout) findViewById(R.id.headerRankedInfo);
            if (summonerSoloRank.equals("UNRANKED")) {
                headerRankedInfo.setVisibility(View.GONE); //hides ranked information if summoner is unranked
            } else {
                headerRankedInfo.setVisibility(View.VISIBLE);
            }

            summonerRank = (ImageView) findViewById(R.id.summonerRank);
            summonerRank.setImageResource(getRankResource(summonerSoloRank));
            promoSeries = (LinearLayout) findViewById(R.id.promoSeries);

            promoGame1 = (ImageView) findViewById(R.id.promoGame1);
            promoGame2 = (ImageView) findViewById(R.id.promoGame2);
            promoGame3 = (ImageView) findViewById(R.id.promoGame3);
            promoGame4 = (ImageView) findViewById(R.id.promoGame4);
            promoGame5 = (ImageView) findViewById(R.id.promoGame5);
            if (promos == null) { //shows promotional game progress
                promoSeries.setVisibility(View.GONE);
            } else if (promos.getProgress().length() == 0) {
                promoSeries.setVisibility(View.GONE);
            } else {
                promoSeries.setVisibility(View.VISIBLE);
                String promoProgress = promos.getProgress();
                if (promoProgress.length() == 3) {
                    promoGame4.setVisibility(View.GONE);
                    promoGame5.setVisibility(View.GONE);
                    promoWinLoss(promoProgress);

                } else if (promoProgress.length() == 5) {
                    promoGame4.setVisibility(View.VISIBLE);
                    promoGame5.setVisibility(View.VISIBLE);
                    promoWinLoss(promoProgress);
                }
            }
        }

    }

    public String getRotation(Context context) {
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return "portrait";
            case Surface.ROTATION_90:
                return "landscape";
            case Surface.ROTATION_180:
                return "portrait";
            default:
                return "landscape";
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(getBaseContext().INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void bootsRemapDate() { //creates date object for when enchanted boot id's were changed
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = " 2015-07-07";
        try {
            bootRemap = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void promoWinLoss(String promoProgress) {
        if (promoProgress.length() == 3) {
            char gameOne = promoProgress.charAt(0); //gets w/l of promotion games
            char gameTwo = promoProgress.charAt(1);
            char gameThree = promoProgress.charAt(2);
            if (gameOne == 'W')
                promoGame1.setImageResource(R.drawable.promo_win);
            else if (gameOne == 'L')
                promoGame1.setImageResource(R.drawable.promo_loss);
            else if (gameOne == 'N')
                promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameTwo == 'W')
                promoGame2.setImageResource(R.drawable.promo_win);
            else if (gameTwo == 'L')
                promoGame2.setImageResource(R.drawable.promo_loss);
            else if (gameTwo == 'N')
                promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameThree == 'W')
                promoGame3.setImageResource(R.drawable.promo_win);
            else if (gameThree == 'L')
                promoGame3.setImageResource(R.drawable.promo_loss);
            else if (gameThree == 'N')
                promoGame3.setImageResource(R.drawable.promo_unplayed);
        } else if (promoProgress.length() == 5) {
            char gameOne = promoProgress.charAt(0);
            char gameTwo = promoProgress.charAt(1);
            char gameThree = promoProgress.charAt(2);
            char gameFour = promoProgress.charAt(3);
            char gameFive = promoProgress.charAt(4);
            if (gameOne == 'W')
                promoGame1.setImageResource(R.drawable.promo_win);
            else if (gameOne == 'L')
                promoGame1.setImageResource(R.drawable.promo_loss);
            else if (gameOne == 'N')
                promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameTwo == 'W')
                promoGame2.setImageResource(R.drawable.promo_win);
            else if (gameTwo == 'L')
                promoGame2.setImageResource(R.drawable.promo_loss);
            else if (gameTwo == 'N')
                promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameThree == 'W')
                promoGame3.setImageResource(R.drawable.promo_win);
            else if (gameThree == 'L')
                promoGame3.setImageResource(R.drawable.promo_loss);
            else if (gameThree == 'N')
                promoGame3.setImageResource(R.drawable.promo_unplayed);

            if (gameFour == 'W')
                promoGame4.setImageResource(R.drawable.promo_win);
            else if (gameFour == 'L')
                promoGame4.setImageResource(R.drawable.promo_loss);
            else if (gameFour == 'N')
                promoGame4.setImageResource(R.drawable.promo_unplayed);

            if (gameFive == 'W')
                promoGame5.setImageResource(R.drawable.promo_win);
            else if (gameFive == 'L')
                promoGame5.setImageResource(R.drawable.promo_loss);
            else if (gameFive == 'N')
                promoGame5.setImageResource(R.drawable.promo_unplayed);
        }


    }

    public static void initialAPISetup() {
        //sets API settings based on server
        switch (serverRegion) {
            case "NA": {
                RiotAPI.setMirror(Region.NA);
                RiotAPI.setRegion(Region.NA);
                Log.i(TAG, "set up na");
                break;
            }
            case "KR": {
                RiotAPI.setMirror(Region.KR);
                RiotAPI.setRegion(Region.KR);
                Log.i(TAG, "set up KR");
                break;
            }
            case "EUW": {
                RiotAPI.setMirror(Region.EUW);
                RiotAPI.setRegion(Region.EUW);
                Log.i(TAG, "set up EUW");
                break;
            }
            case "EUNE": {
                RiotAPI.setMirror(Region.EUNE);
                RiotAPI.setRegion(Region.EUNE);
                Log.i(TAG, "set up EUNE");
                break;
            }
            case "BR": {
                RiotAPI.setMirror(Region.BR);
                RiotAPI.setRegion(Region.BR);
                Log.i(TAG, "set up BR");
                break;
            }
            case "TR": {
                RiotAPI.setMirror(Region.TR);
                RiotAPI.setRegion(Region.TR);
                Log.i(TAG, "set up TR");
                break;
            }
            case "LAS": {
                RiotAPI.setMirror(Region.LAS);
                RiotAPI.setRegion(Region.LAS);
                Log.i(TAG, "set up LAS");
                break;
            }
            case "LAN": {
                RiotAPI.setMirror(Region.LAN);
                RiotAPI.setRegion(Region.LAN);
                Log.i(TAG, "set up LAN");
                break;
            }
            case "OCE": {
                RiotAPI.setMirror(Region.OCE);
                RiotAPI.setRegion(Region.OCE);
                Log.i(TAG, "set up OCE");
                break;
            }
            case "RU": {
                RiotAPI.setMirror(Region.RU);
                RiotAPI.setRegion(Region.RU);
                Log.i(TAG, "set up RU");
                break;
            }
            default: {
                RiotAPI.setMirror(Region.NA);
                RiotAPI.setRegion(Region.NA);
                Log.i(TAG, "unknown server, using NA");
                break;
            }
        }


        RiotAPI.setLoadPolicy(LoadPolicy.UPFRONT);
        RiotAPI.setRateLimit(new RateLimit(10, 10), new RateLimit(500, 600));
        RiotAPI.setAPIKey(apiKey);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getRankResource(String rank) { //returns appropriate image for summoner's rank
        switch (rank) {
            case "UNRANKED": {
                return R.drawable.provisional;
            }
            case "BRONZE V": {
                return R.drawable.bronze_v;
            }
            case "BRONZE IV": {
                return R.drawable.bronze_iv;
            }
            case "BRONZE III": {
                return R.drawable.bronze_iii;
            }
            case "BRONZE II": {
                return R.drawable.bronze_ii;
            }
            case "BRONZE I": {
                return R.drawable.bronze_i;
            }
            case "SILVER V": {
                return R.drawable.silver_v;
            }
            case "SILVER IV": {
                return R.drawable.silver_iv;
            }
            case "SILVER III": {
                return R.drawable.silver_iii;
            }
            case "SILVER II": {
                return R.drawable.silver_ii;
            }
            case "SILVER I": {
                return R.drawable.silver_i;
            }
            case "GOLD V": {
                return R.drawable.gold_v;
            }
            case "GOLD IV": {
                return R.drawable.gold_iv;
            }
            case "GOLD III": {
                return R.drawable.gold_iii;
            }
            case "GOLD II": {
                return R.drawable.gold_ii;
            }
            case "GOLD I": {
                return R.drawable.gold_i;
            }
            case "PLATINUM V": {
                return R.drawable.platinum_v;
            }
            case "PLATINUM IV": {
                return R.drawable.platinum_iv;
            }
            case "PLATINUM III": {
                return R.drawable.platinum_iii;
            }
            case "PLATINUM II": {
                return R.drawable.platinum_ii;
            }
            case "PLATINUM I": {
                return R.drawable.platinum_i;
            }
            case "DIAMOND V": {
                return R.drawable.diamond_v;
            }
            case "DIAMOND IV": {
                return R.drawable.diamond_iv;
            }
            case "DIAMOND III": {
                return R.drawable.diamond_iii;
            }
            case "DIAMOND II": {
                return R.drawable.diamond_ii;
            }
            case "DIAMOND I": {
                return R.drawable.diamond_i;
            }
            case "MASTER I": {
                return R.drawable.master;
            }
            case "CHALLENGER I": {
                return R.drawable.challenger;
            }
            default: {
                return R.drawable.unranked;
            }
        }
    }
}
