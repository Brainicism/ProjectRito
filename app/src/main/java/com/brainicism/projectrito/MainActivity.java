package com.brainicism.projectrito;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.robrua.orianna.api.core.MatchAPI;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.api.dto.BaseRiotAPI;
import com.robrua.orianna.store.Cache;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.currentgame.Participant;
import com.robrua.orianna.type.core.game.Game;
import com.robrua.orianna.type.core.league.League;
import com.robrua.orianna.type.core.league.LeagueEntry;
import com.robrua.orianna.type.core.league.MiniSeries;
import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;
import com.squareup.picasso.Picasso;

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
    public static String prevServer;
    public static final String TAG = MainActivity.class.getName();
    public boolean notFirstRun = false;

    public static ListAdapter listAdapter; //array adapter for list of matches
    public static GameAdapter gameListAdapter;
    public static LinearLayout summonerHeader; //header at the top of the list, displaying summoner information
    public static LinearLayout promoSeries;
    public static LinearLayout headerRankedInfo;
    public static LinearLayout splashScreen;
    public static LinearLayout currentGameInfo;
    public static RelativeLayout progressScreen;
    public static AutoResizeTextView headerSummonerName; //textview on summonerHeader, display summoner name
    public static TextView headerSummonerRank;
    public static TextView headerSummonerWL;
    public static TextView headerSummonerLP;
    public static TextView currChamp, currMap, currMode;
    public static ImageView summonerRank; //image showing summoner's rank
    public static ImageView promoGame1, promoGame2, promoGame3, promoGame4, promoGame5;
    public static ImageView currChampImage;
    public static ImageView gameProgression;
    public static ListView matchHistory; //listview of matches
    public static Button splashSearch, splashSettings;
    public static SharedPreferences prefs;
    public static MenuItem searchItem;

    public static Summoner summoner;
    public static League summonerLeague;
    public static LeagueEntry summonerLeagueEntry;
    public static MiniSeries promos;
    public static List<MatchReference> matchRefList = new ArrayList<>();
    public static List<Game> gameList = new ArrayList<>();
    public static List<League> listLeague = new ArrayList<>();
    public static List<MatchReference> cleanedRefList = new ArrayList<>();
    public static List<Match> matchList;
    public static List<String> versionsList;
    public static pl.droidsonroids.gif.GifImageView progressImage;
    public static boolean ranked = false; //false = normals
    public static Summoner adapterSummoner;
    public static long currGameTime = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: // intents to preference activity
                Intent i = new Intent(this, PreferenceActivity.class);
                startActivity(i);
                try {
                    this.getCurrentFocus().clearFocus();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.refresh_button: {
                if (notFirstRun) //if a summoner has already done a search
                {
                    CheckValidSummoner check = new CheckValidSummoner();
                    check.execute();
                    try {
                        this.getCurrentFocus().clearFocus();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case R.id.about_page:
                Intent j = new Intent(this, AboutPage.class); //about page
                startActivity(j);
                try {
                    this.getCurrentFocus().clearFocus();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //inflates options menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);
        final android.support.v7.widget.SearchView searchView;
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverRegion = prefs.getString("serverRegion", "NA"); //updates query hint with current search region
                searchView.setQueryHint("Current Server: " + serverRegion);
            }
        });
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //on entry of summoner name
                if (!isNetworkAvailable()) { //checks for internet connection
                    Toast.makeText(MainActivity.this, "ERROR: No internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    summonerName = String.valueOf(searchView.getQuery()); //gets summoner name from search bar
                    CheckValidSummoner check = new CheckValidSummoner(); // begins to search for summmoner from server
                    check.execute();
                    searchItem.collapseActionView(); //collapses search bar
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
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        splashSearch = (Button) findViewById(R.id.splashStartButton); //initializes splash screen
        splashSearch.setOnClickListener(new View.OnClickListener() { //expands search bar when pressed
            @Override
            public void onClick(View view) {
                searchItem.expandActionView();
            }
        });
        splashSettings = (Button) findViewById(R.id.splashSettingsButton);
        splashSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, PreferenceActivity.class);
                startActivity(i);
            }
        });
        splashScreen = (LinearLayout) findViewById(R.id.splashScreen);
        progressScreen = (RelativeLayout) findViewById(R.id.progressScreen);
        progressImage = (pl.droidsonroids.gif.GifImageView) findViewById(R.id.progressImage);
        splashScreen.setVisibility(View.VISIBLE);
        gameModePreference = prefs.getString("gameModePreference", "RECENT_10"); //gets data from shared preferences
        serverRegion = prefs.getString("serverRegion", "NA");
        matchHistoryLength = prefs.getInt("matchHistoryLength", 5);
        MiscMethods.initialAPISetup(); //updates API settings
        MiscMethods.regionSetup();
        FetchData fetch = new FetchData(); //preload data
        fetch.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig); //re-shows summoners info
        if (notFirstRun || !summonerName.equals("")) {
            CheckValidSummoner check = new CheckValidSummoner();
            check.execute();
        }
    }

    private class CheckValidSummoner extends AsyncTask<String, Void, Void> {

        Summoner prevSummoner = summoner;
        boolean summonerFound = true;
        boolean serverDown = false;
        boolean invalidKey = false;

        @Override
        protected void onPreExecute() {
            hideKeyboard();
            screenRotation = getRotation(getBaseContext());
            gameModePreference = prefs.getString("gameModePreference", "RECENT_10"); //gets data from shared preferences
            serverRegion = prefs.getString("serverRegion", "NA");
            matchHistoryLength = prefs.getInt("matchHistoryLength", 5);
            ranked = !gameModePreference.equals("RECENT_10");
            MiscMethods.regionSetup();
            if (prevServer == null) {
                prevServer = serverRegion;
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (!prevServer.equals(serverRegion)) { //resets cache when changing servers
                RiotAPI.setDataStore(new Cache());
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Updating region data for " + serverRegion, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            prevServer = serverRegion;
            try {
                summoner = RiotAPI.getSummonerByName(summonerName); //attempts to get summoner from input summoner name
            } catch (APIException e) //called when searched summoner does no exist
            {
                //various possible error messages
                if (String.valueOf(e.getStatus()).equals("NOT_FOUND")) {
                    summoner = null;
                    summonerFound = false;
                } else if (String.valueOf(e.getStatus()).equals("SERVICE_UNAVAILABLE") || String.valueOf(e.getStatus()).equals("INTERNAL_SERVER_ERROR")) {
                    summoner = null;
                    summonerFound = false;
                    serverDown = true;
                } else if (String.valueOf(e.getStatus()).equals("UNAUTHORIZED")) {
                    invalidKey = true;
                }
            }
            if (ranked) { //if ranked is selected
                try {
                    matchRefList = RiotAPI.getMatchList(summoner.getID());//gets summoner's ranked match list
                } catch (NullPointerException e) { //catches exception when summoner has no matches played
                    e.printStackTrace();
                    matchRefList = null;
                } catch (APIException e) {
                    if (String.valueOf(e.getStatus()).equals("SERVICE_UNAVAILABLE") || String.valueOf(e.getStatus()).equals("INTERNAL_SERVER_ERROR")) {
                        serverDown = true;
                        matchRefList = null;
                    }
                }
            } else { //if recent 10
                try {
                    gameList = RiotAPI.getRecentGames(summoner);
                } catch (NullPointerException e) { //catches exception when summoner has no matches played
                    e.printStackTrace();
                    gameList = null;
                } catch (APIException e) {
                    if (String.valueOf(e.getStatus()).equals("SERVICE_UNAVAILABLE") || String.valueOf(e.getStatus()).equals("INTERNAL_SERVER_ERROR")) {
                        serverDown = true;
                        gameList = null;
                    }
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (serverDown) { //if the API server is down
                Toast.makeText(MainActivity.this, "ERROR: API might be down", Toast.LENGTH_SHORT).show();
                serverDown = false;
            } else if (invalidKey) {
                Toast.makeText(MainActivity.this, "ERROR: Invalid API key", Toast.LENGTH_SHORT).show();
            } else {
                if (!summonerFound) { //if summoner was not found (does not exist)
                    {
                        Toast.makeText(MainActivity.this, "ERROR: Summoner does not exist", Toast.LENGTH_SHORT).show();
                        if (notFirstRun)
                            matchHistory.setVisibility(View.INVISIBLE);
                        if (prevSummoner != null) { //resets currently shown summoner to previous valid summoner
                            summoner = prevSummoner;
                            summonerName = summoner.getName();
                        }
                    }
                } else {
                    if (ranked && matchRefList == null) {
                        Toast.makeText(MainActivity.this, "ERROR: Summoner has no ranked games played", Toast.LENGTH_SHORT).show();
                        if (matchHistory != null)
                            matchHistory.setVisibility(View.INVISIBLE); //hides match list while getting data
                        if (prevSummoner != null) {  //resets currently shown summoner to previous valid summoner
                            summoner = prevSummoner;
                            summonerName = summoner.getName();
                        }

                    } else if (!ranked && gameList == null) {
                        Toast.makeText(MainActivity.this, "ERROR: Summoner has no games played", Toast.LENGTH_SHORT).show();
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

        public void retrieveSummonerData() {
            hideKeyboard();
            GetSummonerData retrieve = new GetSummonerData();
            retrieve.execute();
        }
    }


    private class GetSummonerData extends AsyncTask<Void, Void, List<Match>> { //async task for retrieving match data from API
        boolean emptyMatch = false;
        public CurrentGame currentGame;
        long currGameID;
        Participant currParticipant = null;
        long start;
        Date gameStart;

        @Override
        protected void onPreExecute() {
            if (notFirstRun) {
                matchHistory.setVisibility(View.INVISIBLE); //hides match list while getting data
            }
            splashScreen.setVisibility(View.GONE); //hides splash screen
            progressImage.setImageResource(MiscMethods.getLoadingImageResource());
            progressScreen.setVisibility(View.VISIBLE);
            adapterSummoner = summoner;
        }

        @Override
        protected List<Match> doInBackground(Void... voids) { //
            cleanedRefList.clear();
            currentRealmsVer = BaseRiotAPI.getRealm().getV(); //gets current version of ddragon server
            int matchesFound = 0; //count of how many matches of the specified game mode is found
            versionsList = BaseRiotAPI.getVersions();
            currentGame = RiotAPI.getCurrentGame(summoner);
            currParticipant = null;
            if (currentGame != null) { //if there is a current game
                List<Participant> participants = currentGame.getParticipants();
                for (int i = 0; i < participants.size(); i++) {
                    if (participants.get(i).getSummonerID() == summoner.getID()) { //find specific player
                        currParticipant = participants.get(i);
                        break;
                    }
                }
                gameStart = currentGame.getStartTime(); //calculates current time in game
                currGameTime = (new Date().getTime() - gameStart.getTime()) / 1000;
                currGameID = currentGame.getID();
                start = System.currentTimeMillis();
            }

            switch (gameModePreference) {
                case "RANKED_SOLO_5x5": {
                    for (int i = 0; i < matchRefList.size(); i++) {
                        if (matchesFound >= matchHistoryLength) //if match history length is met
                            break;
                        if (matchRefList.get(i).getQueueType().toString().equals("RANKED_SOLO_5x5")) {
                            cleanedRefList.add(matchRefList.get(i));
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
                            matchesFound++;
                        }
                    }
                    break;
                }
                case "ALL_RANKED": {
                    for (int i = 0; i < matchRefList.size(); i++) {
                        if (matchesFound >= matchHistoryLength)
                            break;
                        cleanedRefList.add(matchRefList.get(i));
                        matchesFound++;
                    }
                    break;
                }

                //case not needed for recent_10
            }


            if (ranked) { //convert matchreferences to match objects
                try {
                    matchList = MatchAPI.getMatchesByReference(cleanedRefList);
                } catch (APIException e) {
                    e.printStackTrace();
                }

                if ((matchList == null && ranked) || (gameList == null && !ranked))
                    emptyMatch = true;

            }

            try {
                listLeague = summoner.getLeagueEntries(); //gets the user's leagues
            } catch (APIException e) {
                listLeague = null;
            }
            summonerLeague = null;
            summonerLeagueEntry = null;
            promos = null;
            if (listLeague != null) {
                for (int i = 0; i < listLeague.size(); i++) {
                    if (listLeague.get(i).getQueueType().toString().equals("RANKED_SOLO_5x5")) { //gets user's solo queue rank
                        summonerLeague = listLeague.get(i);
                        summonerLeagueEntry = listLeague.get(i).getParticipantEntry();
                        break;
                    }
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
            return matchList;
        }

        @Override
        protected void onPostExecute(List<Match> changedMatches) {
            if (cleanedRefList.size() == 0) //called if no matches of the specified game mode is found
            {
                switch (gameModePreference) {
                    case "RANKED_SOLO_5x5":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Solo 5v5 played. Try switching game mode preference", Toast.LENGTH_LONG).show();
                        break;
                    case "RANKED_TEAM_5x5":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Team 5v5 played. Try switching game mode preference", Toast.LENGTH_LONG).show();
                        break;
                    case "RANKED_TEAM_3x3":
                        Toast.makeText(MainActivity.this, "User has no games of Ranked Team 3v3 played. Try switching game mode preference", Toast.LENGTH_LONG).show();
                        break;
                }
            }
            if (emptyMatch)
                Toast.makeText(MainActivity.this, "ERROR: Something went wrong", Toast.LENGTH_SHORT).show();

            matchHistory = (ListView) findViewById(R.id.matchHistoryList);
            matchHistory.setVisibility(View.VISIBLE); //unhides the match history view
            progressScreen.setVisibility(View.GONE);

            if (!notFirstRun) {
                summonerHeader = (LinearLayout) View.inflate(MainActivity.this, R.layout.header_layout, null); //creates summoner banner to the top of the list view
                matchHistory.addHeaderView(summonerHeader, null, false); //appends the top banner onto the top of the list view
                notFirstRun = true;
            }
            if (ranked) {
                listAdapter = new MatchAdapter(MainActivity.this, changedMatches); //sets adapter for ranked matches
                matchHistory.setAdapter(listAdapter);
            } else {
                gameListAdapter = new GameAdapter(MainActivity.this, gameList); //sets adapter for recent matches
                matchHistory.setAdapter(gameListAdapter);
            }

            matchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() { //detects on click on a specific item

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { //sends user to detailed match history on click
                    String fullURL = ((TextView) view.findViewById(R.id.matchURI)).getText().toString();
                    Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                    intent.putExtra("URL", fullURL);
                    startActivity(intent);
                }
            });

            headerSummonerName = (AutoResizeTextView) findViewById(R.id.headerSummonerName);
            headerSummonerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, summoner.getName(), Toast.LENGTH_SHORT).show();

                }
            });
            headerSummonerRank = (TextView) findViewById(R.id.headerSummonerRank);
            headerSummonerWL = (TextView) findViewById(R.id.headerSummonerWL);
            headerSummonerLP = (TextView) findViewById(R.id.headerSummonerLP);
            headerRankedInfo = (LinearLayout) findViewById(R.id.headerRankedInfo);

            currentGameInfo = (LinearLayout) findViewById(R.id.currGameInfo);
            currChampImage = (ImageView) findViewById(R.id.currChampImage);
            currChamp = (TextView) findViewById(R.id.currChamp);
            currMap = (TextView) findViewById(R.id.currMap);
            currMode = (TextView) findViewById(R.id.currMode);
            gameProgression = (ImageView) findViewById(R.id.gameProgression);
            gameProgression.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //updates in game time on click
                    UpdateTime fetch = new UpdateTime(); //preload data
                    fetch.execute();
                }
            });
            currentGameInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { //sends user to lolnexus for detailed current game info
                    String fullURL = "http://www.lolnexus.com/" + serverRegion + "/search?name=" + currParticipant.getSummonerName();
                    Intent intent = new Intent(getBaseContext(), InternalWebView.class);
                    intent.putExtra("URL", fullURL);
                    startActivity(intent);
                }
            });
            updateTimeIndicator(); //update color of current game progress indicator

            if (currentGame != null) { //current game information box
                currentGameInfo.setVisibility(View.VISIBLE);
                currChamp.setText(currParticipant.getChampion().getName());
                if (!MiscMethods.normalizeMap(String.valueOf(currentGame.getMap())).equals("Unknown")) //set map name
                    currMap.setText(MiscMethods.normalizeMap(String.valueOf(currentGame.getMap())));
                else
                    currMap.setText(currentGame.getMap().toString());

                if (!MiscMethods.normalizeQueueType(String.valueOf(currentGame.getQueueType())).equals("Unknown")) //set queue type
                    currMode.setText(MiscMethods.normalizeQueueType(String.valueOf(currentGame.getQueueType())));
                else
                    currMode.setText(currentGame.getQueueType().toString());
                Picasso.with(getBaseContext()).load("http://ddragon.leagueoflegends.com/cdn/" + currentRealmsVer + "/img/champion/" + currParticipant.getChampion().getKey() + ".png").into(currChampImage);
            } else {
                currentGameInfo.setVisibility(View.GONE); //hide box if no current game
            }

            if (summonerSoloRank.equals("UNRANKED")) { //info box for unranked
                headerSummonerName.setText(summoner.getName());
                headerSummonerRank.setText("UNRANKED");
                headerSummonerWL.setText("LEVEL " + String.valueOf(summoner.getLevel()));
                headerSummonerLP.setText("");
            } else {
                headerSummonerName.setText(summoner.getName()); //info box for ranked
                headerSummonerRank.setText(summonerSoloRank);
                headerSummonerWL.setText(summonerWL);
                headerSummonerLP.setText(String.valueOf(summonerLP) + " LP");
            }

            summonerRank = (ImageView) findViewById(R.id.summonerRank);
            summonerRank.setImageResource(MiscMethods.getRankResource(summonerSoloRank));
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
                    MiscMethods.promoWinLoss(promoProgress);

                } else if (promoProgress.length() == 5) {
                    promoGame4.setVisibility(View.VISIBLE);
                    promoGame5.setVisibility(View.VISIBLE);
                    MiscMethods.promoWinLoss(promoProgress);
                }
            }
        }

        private class UpdateTime extends AsyncTask<String, Void, Void> {
            boolean currGame = true;
            boolean newGame = false;

            @Override
            protected void onPreExecute() {
                if (gameStart.getTime() != 0) {//updates current ingame time based on device
                    currGameTime += ((System.currentTimeMillis() - start) / 1000);
                    start = System.currentTimeMillis();
                }
            }

            @Override
            protected Void doInBackground(String... strings) {
                CurrentGame temp = RiotAPI.getCurrentGame(summoner);
                if (temp == null) { //if game has ended
                    currGame = false;
                }
                else if (currGameID == temp.getID())// if still the same game as when previously checked, update time
                {
                    if (gameStart.getTime() == 0) { //if game hasnt begun, wait for game to start and get new time
                        gameStart = temp.getStartTime();
                        currGameTime = (new Date().getTime() - gameStart.getTime()) / 1000;
                    }
                } else //if in a different game than when previously checked
                    newGame = true;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (!currGame) { //if no longer in a game
                    currentGameInfo.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Game has ended", Toast.LENGTH_SHORT).show();
                } else { //if in a game, update time
                    if (newGame) { // if user is now in a different game, reload data
                        Toast.makeText(MainActivity.this, "New game started: loading new game", Toast.LENGTH_SHORT).show();
                        GetSummonerData retrieve = new GetSummonerData();
                        retrieve.execute();
                    } else { //if user is in same game, update time
                        int minutes = (int) currGameTime / 60;
                        int seconds = ((int) currGameTime % 60);
                        String str = String.format("%d:%02d", minutes, seconds);
                        if (gameStart.getTime() == 0)
                            Toast.makeText(MainActivity.this, "Game has not yet started", Toast.LENGTH_SHORT).show();
                        else if (currGameTime < 900)
                            Toast.makeText(MainActivity.this, str + " (Early Game)", Toast.LENGTH_SHORT).show();
                        else if (currGameTime < 1800)
                            Toast.makeText(MainActivity.this, str + " (Mid Game)", Toast.LENGTH_SHORT).show();
                        else if (currGameTime >= 1800)
                            Toast.makeText(MainActivity.this, str + " (Late Game)", Toast.LENGTH_SHORT).show();
                        updateTimeIndicator();
                    }
                }

            }

        }

        public void updateTimeIndicator() {
            if (gameStart != null) {
                if (gameStart.getTime() == 0)
                    gameProgression.setImageResource(R.drawable.grey_dot);
                else if (currGameTime < 900)
                    gameProgression.setImageResource(R.drawable.green_dot);
                else if (currGameTime < 1800)
                    gameProgression.setImageResource(R.drawable.yellow_dot);
                else if (currGameTime >= 1800)
                    gameProgression.setImageResource(R.drawable.red_dot);
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


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private class FetchData extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... strings) {
            RiotAPI.getChampions();
            RiotAPI.getItems();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Retrieved latest data for " + serverRegion, Toast.LENGTH_SHORT).show();
        }
    }


}

