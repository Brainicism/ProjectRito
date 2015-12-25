package com.brainicism.projectrito;


import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.api.LoadPolicy;
import com.robrua.orianna.type.api.RateLimit;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.exception.APIException;

public class MiscMethods {
    public static final String TAG = MiscMethods.class.getName();

    public static void initialAPISetup() {
        RiotAPI.setLoadPolicy(LoadPolicy.UPFRONT);
        RiotAPI.setRateLimit(new RateLimit(3000, 10), new RateLimit(180000, 600));
        RiotAPI.setAPIKey(MainActivity.apiKey);
    }
    public static String normalizeMap(String raw){
        switch (raw){
            case "BUTCHER'S BRIDGE":
                return "Butcher's Bridge";
            case "HOWLING_ABYSS":
                return "Howling Abyss";
            case "SUMMONERS_RIFT":
                return "Summoner's Rift";
            case "SUMMONERS_RIFT_AUTUMN":
                return "Summoner's Rift";
            case "SUMMONERS_RIFT_SUMMER":
                return "Summoner's Rift";
            case "THE_CRYSTAL_SCAR":
                return "The Crystal Scar";
            case "THE_PROVING_GROUNDS":
                return "The Proving Grounds";
            case "TWISTED_TREELINE":
                return "Twisted Treeline";
            case "TWISTED_TREELINE_ORIGINAL":
                return "Twisted Treeline";
            default:
                return "Unknown";
        }
    }
    public static String normalizeQueueType(String raw){
        switch (raw){
            case "CUSTOM":
                return "Custom";
            case "NORMAL_3x3":
                return "Normal 3v3";
            case "NORMAL_5x5_BLIND":
                return "Normal 5v5 (Blind Pick)";
            case "NORMAL_5x5_DRAFT":
                return "Normal 5v5 (Draft Pick)";
            case "RANKED_SOLO_5x5":
                return "Ranked Solo 5v5";
            case "RANKED_TEAM_3x3":
                return "Ranked Team 3v3";
            case "RANKED_TEAM_5x5":
                return "Ranked Team 5v5";
            case "ODIN_5x5_BLIND":
                return "Dominion 5v5 (Blind Pick)";
            case "ODIN_5x5_DRAFT":
                return "Dominion 5v5 (Draft Pick)";
            case "BOT_ODIN_5x5":
                return "Bots";
            case "BOT_5x5_INTRO":
                return "Bots";
            case "BOT_5x5_BEGINNER":
                return "Bots";
            case "BOT_5x5_INTERMEDIATE":
                return "Bots";
            case "BOT_TT_3x3":
                return "Bots";
            case "GROUP_FINDER_5x5":
                return "Team Builder 5v5";
            case "ARAM_5x5":
                return "ARAM";
            case "ONEFORALL_5x5":
                return "One For All";
            case "FIRSTBLOOD_1x1":
                return "Snowdown Showdown (1v1)";
            case "FIRSTBLOOD_2x2":
                return "Snowdown Showdown (2v2)";
            case "SR_6x6":
                return "Hexakill";
            case "URF_5x5":
                return "URF";
            case "BOT_URF_5x5":
                return "URF Bots";
            case "NIGHTMARE_BOT_5x5_RANK1":
                return "Nightmare Bots";
            case "NIGHTMARE_BOT_5x5_RANK2":
                return "Nightmare Bots";
            case "NIGHTMARE_BOT_5x5_RANK5":
                return "Nightmare Bots";
            case "ASCENSION_5x5":
                return "Ascension";
            case "HEXAKILL":
                return "Hexakill";
            case "BILGEWATER_ARAM_5x5":
                return "Bilgewater Aram";
            case "KING_PORO_5x5":
                return "Legend of the Poro King";
            case "COUNTER_PICK":
                return "Nemesis";
            case "BILGEWATER_5x5":
                return "Black Market Brawlers";
            default:
                return "Unknown";

        }
    }
    public static String normalizeSubType(String raw) {
        switch (raw) {
            case "ARAM_UNRANKED_5x5":
                return ("ARAM");
            case "ASCENSION":
                return ("ASCENSION");
            case "BILGEWATER":
                return ("Black Market Brawlers");
            case "BOT":
                return ("Bots 5v5");
            case "BOT_3x3":
                return ("Bots 5v5");
            case "CAP_5x5":
                return ("5v5 Team Builder");
            case "COUNTER_PICK":
                return ("Nemesis");
            case "FIRSTBLOOD_1x1":
                return ("Snowdown Showdown 1v1");
            case "FIRSTBLOOD_2x2":
                return ("Snowdown Showdown 2v2");
            case "HEXAKILL":
                return ("Hexakill");
            case "KING_PORO":
                return ("Legend of the Poro King");
            case "NIGHTMARE_BOT":
                return ("Doom Bots");
            case "NONE":
                return ("Custom");
            case "NORMAL":
                return ("Normal 5v5");
            case "NORMAL_3x3":
                return ("Normal 3v3");
            case "ODIN_UNRANKED":
                return ("Dominion");
            case "ONEFORALL_5x5":
                return ("One For All");
            case "RANKED_SOLO_5x5":
                return ("Ranked Solo 5v5");
            case "RANKED_TEAM_3x3":
                return ("Ranked Teams 3v3");
            case "RANKED_TEAM_5x5":
                return ("Ranked Teams 5v5");
            case "SR_6x6":
                return ("Hexakill");
            case "URF":
                return ("Ultra Rapid Fire");
            case "URF BOT":
                return ("Ultra Rapid Fire Bots");
            default:
                return ("Unknown");

        }
    }

    public static void regionSetup() {
        //sets API settings based on server
        try {
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
                    break;
                }
            }
        }
        catch (APIException e)
        {
            e.printStackTrace();
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
                MainActivity.promoGame2.setImageResource(R.drawable.promo_win);
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
                MainActivity.promoGame2.setImageResource(R.drawable.promo_win);
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
                MainActivity.promoGame4.setImageResource(R.drawable.promo_unplayed);

            if (gameFive == 'W')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_win);
            else if (gameFive == 'L')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_loss);
            else if (gameFive == 'N')
                MainActivity.promoGame5.setImageResource(R.drawable.promo_unplayed);
        }


    }

    public static int getLoadingImageResource() {
        int rand = (int) (Math.random() * (7 - 1)) + 1;
        switch (rand) {
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
            case 6:
                return R.drawable.zedloading;
        }
        return 0;
    }

    public static int getRankResource(String rank) { //returns appropriate image for summoner's rank
        switch (rank) {
            case "UNRANKED": {
                return R.drawable.unranked;
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
