package com.brainicism.projectrito;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.robrua.orianna.type.core.match.Match;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;


public class MatchAdapter extends ArrayAdapter<Match> {
    public MatchAdapter(Context context, List<Match> matches) {
        super(context, R.layout.match_layout, matches);
    }

    private int textColor;
    private double summonerGold;
    private Long kills, deaths, assists; //various stats
    private Long[] itemID = new Long[7]; //item id for each item
    private Long cs;
    public static final String TAG = MatchAdapter.class.getName();
    private String[] itemURL = new String[7]; //image url for each item
    private String champIconURL; //image url for champion image
    private String[] summonerSpellKey = new String[2];
    private String[] summonerSpellURL = new String[2];
    private String matchParticipantID;
    private String matchHistoryURL;
    private String date;
    private String queueTypeText;
    String correctVers;
    private SimpleDateFormat dt = new SimpleDateFormat("MMMM d, yyyy");
    private Boolean win;

    private TextView championName; //text view for the summoner's played champ
    private TextView score;  //text view for the summoner's kda
    private TextView matchDate;
    private TextView csText;
    private TextView queueType;
    private TextView matchURI;
    private ImageView item0, item1, item2, item3, item4, item5, item6; //image views of each item slot
    private ImageView champImage; //image view for champion played by the summoner
    private ImageView summonerSpell1, summonerSpell2;
    private ImageView goldIcon;
    private ImageView csIcon;
    private TextView goldText;
    private View divider;
    private LinearLayout goldCs;
    private LinearLayout summonerSpellBox;


    private Match selectedMatch; //the current match on the list
    private List<Participant> listOfParticipants; //list of participants within the match
    private Participant summoner; //the main participant of the match
    private Champion champ; //champion played by the main summoner


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());     //inflates layout
        View theView = inflater.inflate(R.layout.match_layout, parent, false);

        score = (TextView) theView.findViewById(R.id.scoreText); //gets reference to views
        championName = (TextView) theView.findViewById(R.id.championNameText);
        champImage = (ImageView) theView.findViewById(R.id.champImage);
        item0 = (ImageView) theView.findViewById(R.id.item0);
        item1 = (ImageView) theView.findViewById(R.id.item1);
        item2 = (ImageView) theView.findViewById(R.id.item2);
        item3 = (ImageView) theView.findViewById(R.id.item3);
        item4 = (ImageView) theView.findViewById(R.id.item4);
        item5 = (ImageView) theView.findViewById(R.id.item5);
        item6 = (ImageView) theView.findViewById(R.id.item6);
        summonerSpell1 = (ImageView) theView.findViewById(R.id.summonerSpell1);
        summonerSpell2 = (ImageView) theView.findViewById(R.id.summonerSpell2);
        goldCs = (LinearLayout) theView.findViewById(R.id.goldCs);
        goldIcon = (ImageView) theView.findViewById(R.id.goldIcon);
        goldText = (TextView) theView.findViewById(R.id.goldText);
        csIcon = (ImageView) theView.findViewById(R.id.csIcon);
        csText = (TextView) theView.findViewById(R.id.csText);
        matchDate = (TextView) theView.findViewById(R.id.matchDate);
        queueType = (TextView) theView.findViewById(R.id.queueType);
        matchURI = (TextView) theView.findViewById(R.id.matchURI);
        summonerSpellBox = (LinearLayout) theView.findViewById(R.id.summonerSpellBox);
        selectedMatch = getItem(position); //gets the match based on the index on the list
        divider = theView.findViewById(R.id.divider);
        listOfParticipants = selectedMatch.getParticipants(); //gets a list of the participants in the match
        for (int i = 0; i < listOfParticipants.size(); i++) {
            if (listOfParticipants.get(i).getSummonerID() == MainActivity.adapterSummoner.getID()) { //finds the summoner searched within the list of participants
                summoner = listOfParticipants.get(i);
                break;
            }
        }
        win = summoner.getStats().getWinner();  //gathers stats of the summoner being searched
        champ = summoner.getChampion();
        kills = summoner.getStats().getKills();
        deaths = summoner.getStats().getDeaths();
        assists = summoner.getStats().getAssists();
        summonerGold = (double) summoner.getStats().getGoldEarned();
        summonerGold = ((double) Math.round(summonerGold / 1000 * 10)) / 10;
        cs = summoner.getStats().getMinionsKilled() + summoner.getStats().getNeutralMinionsKilledEnemyJungle() + summoner.getStats().getNeutralMinionsKilledTeamJungle();
        queueTypeText = String.valueOf(selectedMatch.getQueueType());
        itemID[0] = summoner.getStats().getItem0ID(); //gets item id's
        itemID[1] = summoner.getStats().getItem1ID();
        itemID[2] = summoner.getStats().getItem2ID();
        itemID[3] = summoner.getStats().getItem3ID();
        itemID[4] = summoner.getStats().getItem4ID();
        itemID[5] = summoner.getStats().getItem5ID();
        itemID[6] = summoner.getStats().getItem6ID();
        summonerSpellKey[0] = summoner.getSummonerSpell1().getKey(); //gets item summoner spell keys
        summonerSpellKey[1] = summoner.getSummonerSpell2().getKey();
        matchParticipantID = summoner.getMatchHistoryURI(); //gets the second parameter for detailed match history
        matchParticipantID = matchParticipantID.replaceAll("[^-?0-9]+", "");
        matchParticipantID = matchParticipantID.substring(2, matchParticipantID.length());


        int currentCutoff = selectedMatch.getVersion().indexOf(".", selectedMatch.getVersion().indexOf(".") + 1);
        for (int i = 0; i < MainActivity.versionsList.size(); i++) {
            int cutoff = MainActivity.versionsList.get(i).indexOf(".", MainActivity.versionsList.get(0).indexOf(".") + 1); //cutoff for realms version number
            if (MainActivity.versionsList.get(i).substring(0, cutoff).equals(selectedMatch.getVersion().substring(0, currentCutoff))) {
                correctVers = MainActivity.versionsList.get(i);
                break;
            }
        }

        //INDEPENDENT OF TYPE
        switch (MainActivity.serverRegion) {
            case "NA": {
                matchHistoryURL = "http://matchhistory.na.leagueoflegends.com/en/#match-details/NA1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "KR": {
                matchHistoryURL = "http://matchhistory.leagueoflegends.co.kr/ko/#match-details/KR/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "EUW": {
                matchHistoryURL = "http://matchhistory.euw.leagueoflegends.com/en/#match-details/EUW1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "EUNE": {
                matchHistoryURL = "http://matchhistory.eune.leagueoflegends.com/en/#match-details/EUN1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "BR": {
                matchHistoryURL = "http://matchhistory.br.leagueoflegends.com/pt/#match-details/BR1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "TR": {
                matchHistoryURL = "http://matchhistory.tr.leagueoflegends.com/tr/#match-details/TR1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "LAS": {
                matchHistoryURL = "http://matchhistory.las.leagueoflegends.com/es/#match-details/LA2/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "LAN": {
                matchHistoryURL = "http://matchhistory.lan.leagueoflegends.com/es/#match-details/LA1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "OCE": {
                matchHistoryURL = "http://matchhistory.oce.leagueoflegends.com/en/#match-details/OC1/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
            }
            case "RU":
                matchHistoryURL = "http://matchhistory.ru.leagueoflegends.com/ru/#match-details/RU/" + selectedMatch.getID() + "/" + matchParticipantID + "?tab=overview";
                break;
        }


        itemURL[0] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[0] + ".png"; //gets image urls
        itemURL[1] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[1] + ".png";
        itemURL[2] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[2] + ".png";
        itemURL[3] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[3] + ".png";
        itemURL[4] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[4] + ".png";
        itemURL[5] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[5] + ".png";
        itemURL[6] = "http://ddragon.leagueoflegends.com/cdn/" + correctVers + "/img/item/" + itemID[6] + ".png";
        summonerSpellURL[0] = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.currentRealmsVer + "/img/spell/" + summonerSpellKey[0] + ".png ";
        summonerSpellURL[1] = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.currentRealmsVer + "/img/spell/" + summonerSpellKey[1] + ".png";
        champIconURL = "http://ddragon.leagueoflegends.com/cdn/" + MainActivity.currentRealmsVer + "/img/champion/" + champ.getKey() + ".png";
        Picasso.with(getContext()).load(champIconURL).into(champImage); //loads image into UI
        for (int i = 0; i < 7; i++) //check for empty item slots
        {
            if (itemID[i] == 0) {
                itemURL[i] = "http://i.imgur.com/M3e1IqG.png"; //shows empty item slot
            }
        }


        Picasso.with(getContext()).load(itemURL[0]).into(item0); //loads image url's into image views
        Picasso.with(getContext()).load(itemURL[1]).into(item1);
        Picasso.with(getContext()).load(itemURL[2]).into(item2);
        Picasso.with(getContext()).load(itemURL[3]).into(item3);
        Picasso.with(getContext()).load(itemURL[4]).into(item4);
        Picasso.with(getContext()).load(itemURL[5]).into(item5);
        Picasso.with(getContext()).load(itemURL[6]).into(item6);
        Picasso.with(getContext()).load(summonerSpellURL[0]).into(summonerSpell1);
        Picasso.with(getContext()).load(summonerSpellURL[1]).into(summonerSpell2);
        if (MainActivity.screenRotation.equals("landscape")) { //changes layout based on orientation of the device
            item6.setVisibility(View.VISIBLE);
            summonerSpellBox.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            goldCs.setVisibility(View.VISIBLE);
        } else {
            item6.setVisibility(View.GONE);
            summonerSpellBox.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            goldCs.setVisibility(View.GONE);
        }
        if (win) { //changes colors of layout based on win/victory
            textColor = Color.parseColor("#4CAF50");
            goldIcon.setImageResource(R.drawable.ic_control_point_duplicate_black_48dp_green);
            csIcon.setImageResource(R.drawable.ic_android_black_48dp_green);
        } else {
            textColor = Color.parseColor("#B71C1C");
            goldIcon.setImageResource(R.drawable.ic_control_point_duplicate_black_48dp_red);
            csIcon.setImageResource(R.drawable.ic_android_black_48dp_red);
        }
        if (MainActivity.screenRotation.equals("landscape")) {
            if (deaths != 0) {
                double kdaValue = ((double) kills + (double) assists) / (double) deaths;
                kdaValue = Math.round(kdaValue * 100.0) / 100.0;
                score.setText(String.valueOf(kills) + "/" + String.valueOf(deaths) + "/" + String.valueOf(assists) + " (" + String.valueOf(kdaValue) + ")");
            } else {
                score.setText(String.valueOf(kills) + "/" + String.valueOf(deaths) + "/" + String.valueOf(assists) + "(∞)");

            }
        } else {
            score.setText(String.valueOf(kills) + "/" + String.valueOf(deaths) + "/" + String.valueOf(assists));

        }
        goldText.setText(String.valueOf(summonerGold) + "k");
        goldText.setTextColor(textColor);
        date = dt.format(selectedMatch.getCreation());

        matchDate.setText(date);
        csText.setText(String.valueOf(cs));
        csText.setTextColor(textColor);
        score.setTextColor(textColor);
        championName.setText(String.valueOf(champ.getName()));
        championName.setTextColor(textColor);
        matchURI.setText(matchHistoryURL);
        switch (queueTypeText) {
            case "RANKED_TEAM_5x5":
                queueType.setText("Ranked Team 5v5");
                break;
            case "RANKED_SOLO_5x5":
                queueType.setText("Ranked Solo 5v5");
                break;
            case "RANKED_TEAM_3x3":
                queueType.setText("Ranked Team 3v3");
                break;
        }

        return theView;
    }

}
