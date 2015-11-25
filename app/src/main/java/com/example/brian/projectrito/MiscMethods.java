package com.example.brian.projectrito;

import android.util.Log;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.api.RateLimit;
import com.robrua.orianna.type.core.common.Region;

public class MiscMethods {
    public static void initialAPISetup() {
        RiotAPI.setLoadPolicy(LoadPolicy.UPFRONT);
        RiotAPI.setRateLimit(new RateLimit(3000, 10), new RateLimit(180000, 600));
        RiotAPI.setAPIKey(MainActivity.apiKey);
    }

    public static void regionSetup(){
        //sets API settings based on server
        switch (MainActivity.serverRegion) {
            case "NA": {
                RiotAPI.setRegion(Region.NA);
                break;
            }
            case "KR": {
                RiotAPI.setRegion(Region.KR);
                break;
            }
            case "EUW": {
                RiotAPI.setRegion(Region.EUW);
                break;
            }
            case "EUNE": {
                RiotAPI.setRegion(Region.EUNE);
                break;
            }
            case "BR": {
                RiotAPI.setRegion(Region.BR);
                break;
            }
            case "TR": {
                RiotAPI.setRegion(Region.TR);
                break;
            }
            case "LAS": {
                RiotAPI.setRegion(Region.LAS);
                break;
            }
            case "LAN": {
                RiotAPI.setRegion(Region.LAN);
                break;
            }
            case "OCE": {
                RiotAPI.setRegion(Region.OCE);
                break;
            }
            case "RU": {
                RiotAPI.setRegion(Region.RU);
                break;
            }
            default: {
                RiotAPI.setRegion(Region.NA);
                Log.i("hello", "unknown server, using NA");
                break;
            }
        }
    }
    public static void promoWinLoss(String promoProgress) {
        if (promoProgress.length() == 3) {
            char gameOne = promoProgress.charAt(0); //gets w/l of promotion games
            char gameTwo = promoProgress.charAt(1);
            char gameThree = promoProgress.charAt(2);
            if (gameOne == 'W')
                MainActivity.promoGame1.setImageResource(R.drawable.promo_win);
            else if (gameOne == 'L')
                MainActivity.promoGame1.setImageResource(R.drawable.promo_loss);
            else if (gameOne == 'N')
                MainActivity.promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameTwo == 'W')
                MainActivity. promoGame2.setImageResource(R.drawable.promo_win);
            else if (gameTwo == 'L')
                MainActivity. promoGame2.setImageResource(R.drawable.promo_loss);
            else if (gameTwo == 'N')
                MainActivity.promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameThree == 'W')
                MainActivity. promoGame3.setImageResource(R.drawable.promo_win);
            else if (gameThree == 'L')
                MainActivity.promoGame3.setImageResource(R.drawable.promo_loss);
            else if (gameThree == 'N')
                MainActivity. promoGame3.setImageResource(R.drawable.promo_unplayed);
        } else if (promoProgress.length() == 5) {
            char gameOne = promoProgress.charAt(0);
            char gameTwo = promoProgress.charAt(1);
            char gameThree = promoProgress.charAt(2);
            char gameFour = promoProgress.charAt(3);
            char gameFive = promoProgress.charAt(4);
            if (gameOne == 'W')
                MainActivity.promoGame1.setImageResource(R.drawable.promo_win);
            else if (gameOne == 'L')
                MainActivity.promoGame1.setImageResource(R.drawable.promo_loss);
            else if (gameOne == 'N')
                MainActivity.promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameTwo == 'W')
                MainActivity. promoGame2.setImageResource(R.drawable.promo_win);
            else if (gameTwo == 'L')
                MainActivity.promoGame2.setImageResource(R.drawable.promo_loss);
            else if (gameTwo == 'N')
                MainActivity.promoGame2.setImageResource(R.drawable.promo_unplayed);

            if (gameThree == 'W')
                MainActivity.promoGame3.setImageResource(R.drawable.promo_win);
            else if (gameThree == 'L')
                MainActivity.promoGame3.setImageResource(R.drawable.promo_loss);
            else if (gameThree == 'N')
                MainActivity.promoGame3.setImageResource(R.drawable.promo_unplayed);

            if (gameFour == 'W')
                MainActivity.promoGame4.setImageResource(R.drawable.promo_win);
            else if (gameFour == 'L')
                MainActivity.promoGame4.setImageResource(R.drawable.promo_loss);
            else if (gameFour == 'N')
                MainActivity. promoGame4.setImageResource(R.drawable.promo_unplayed);

            if (gameFive == 'W')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_win);
            else if (gameFive == 'L')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_loss);
            else if (gameFive == 'N')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_unplayed);
        }


    }
    public static int getLoadingImageResource (){
        int rand = (int) (Math.random() * (6 - 1)) + 1;
        switch (rand){
            case 1:
                return R.drawable.dravenkatloading;
            case 2:
                return R.drawable.lululoading;
            case 3:
                return R.drawable.koggnarloading;
            case 4:
                return R.drawable.teemoloading;
            case 5:
                return R.drawable.lululoading2;

        }


        return 0;
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
