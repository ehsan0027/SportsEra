package view

import android.app.Application

class GlobalVariable : Application() {


    companion object {

        //Match
        var BATTING_TEAM_SQUAD = ArrayList<String>()
        var BOWLING_TEAM_SQUAD = ArrayList<String>()
        var YetToBat = ArrayList<String>()
        var Inning: String = "FirstInning"
        var TeamSquad:String = ""
        var TossWonTeamDecidedTo: String = ""
        var TossWonTeamName: String = ""
        var MatchCurrentDetails:String = ""
        var BATTING_TEAM_ID: String = ""
        var SELECTED_PLAYERS_ID_LIST = ArrayList<String>()
        var BATTING_TEAM_NAME: String = ""
        var BATTING_TEAM_LOGO: String = ""
        var BATTING_TEAM_Squad_Count: Int = 0
        var MATCH_ID: String = ""
        var BOWLING_TEAM_ID: String = ""
        var BOWLING_TEAM_NAME: String = ""
        var MATCH_OVERS: Int = 0
        var Wides: Int = 0
        var NoBalls: Int = 0
        var Byes: Int = 0
        var LegByes: Int = 0
        var FirstInningScore: Int = 0
        var FirstInningWickets: Int = 0
        var FirstInningOversPlayed: Int = 0
        var FirstInningOverBallsPlayed: Int = 0
        var BattingPosition: Int = 1
        var BowlerPosition: Int = 1


        //Team

        var Current_Over: Int = 0
        var CURRENT_BALL: Int = 0
        var CURRENT_TEAM_SCORE: Int = 0
        var TEAM_Extras: Int = 0
        var TEAM_WICKET: Int = 0
        //  var TOTAL_TEAM_OVERS: Int = 0
        var TEAM_CRR: Float = 00F
        var TEAM_RRR: Float = 00F

        //Striker
        var StrikerId:String = ""
        var CurrentBowler:String = ""
        //Batsman1

        var Batsman_1_ID: String = ""
        var Batsman_1_NAME: String = ""
        var Batsman_1_SCORE: Int = 0
        var Batsman_1_BALL: Int = 0
        var Batsman_1_SINGLES: Int = 0
        var Batsman_1_doubles: Int = 0
        var Batsman_1_TRIPLES: Int = 0
        var Batsman_1_NUM_FOUR: Int = 0
        var Batsman_1_NUM_SIX: Int = 0
        var Batsman_1_SR: Float = 00f
        var Batsman_1_DOT_BALLS_Played: Int = 0
        var Batsman_1_Img:String=""


        //Batsman2

        var Batsman_2_ID: String = ""
        var Batsman_2_NAME: String = ""
        var Batsman_2_Img:String=""
        var Batsman_2_SCORE: Int = 0
        var Batsman_2_BALL: Int = 0
        var Batsman_2_SINGLES: Int = 0
        var Batsman_2_doubles: Int = 0
        var Batsman_2_TRIPLES: Int = 0
        var Batsman_2_NUM_FOUR: Int = 0
        var Batsman_2_NUM_SIX: Int = 0
        var Batsman_2_SR: Float = 00f
        var Batsman_2_DOT_BALLS_Played: Int = 0


        //Partnership

        var CURRENT_PARTNERSHIP_RUNS: Int = 0
        var CURRENT_PARTNERSHIP_BALLS: Int = 0


        //Bowler

        var BOWLER_ID: String = ""
        var BOWLER_NAME: String = ""
        var BOWLER_Img:String=""
        var BOWLER_OVERS: Int = 0
        var BOWLER_MAIDEN: Int = 0
        var BOWLER_WICKET: Int = 0
        var BOWLER_Wide_ball: Int = 0
        var BOWLER_No_ball: Int = 0
        var BOWLER_ECONOMY: Float = 00f
        var num_sixes_conceded: Int = 0
        var num_fours_conceded: Int = 0
        var BOWLER_BALLS_BOWLED: Int = 0
        var BOWLER_RUNS_CONCEDED: Int = 0
        var DOT_BALLS_BOWLED: Int = 0


        var LiveScoreFirstInning: Int = 0
        var LiveScoreSecondInning: Int = 0
        var LiveWicketsFirstInning: Int = 0
        var LiveWicketsSecondInning: Int = 0
        var LiveOversFirstInning: Int = 0
        var LiveOversSecondInning: Int = 0
        var LiveOverBallsFirstInning: Int = 0
        var LiveOverBallsSecondInning: Int = 0
        var LiveMatchCurrentDetails: String = ""
        var LiveMatchCurrentInning: String = ""
        var LiveCRR: Float = 00f
        var LiveRRR: Float = 00f

        var this_Over_runs: Int = 0
        var this_Over_wickets: Int = 0
        var this_Over_extras: Int = 0


        var found: Boolean = false
        var num : Int = 0


        val listOfPakistanCities = listOf(
            "Karachi",
            "Lahore",
            "Layyah",
            "Faisalabad",
            "Rawalpindi",
            "Multan",
            "Hyderabad",
            "Gujranwala",
            "Peshawar",
            "Islamabad",
            "Bahawalpur",
            "Sargodha",
            "Sialkot",
            "Quetta",
            "Sukkur",
            "Jhang",
            "Shekhupura",
            "Mardan",
            "Gujrat",
            "Larkana",
            "Kasur",
            "Rahim Yar Khan",
            "Sahiwal",
            "Okara",
            "Wah Cantonment",
            "Dera Ghazi Khan",
            "Mingora",
            "Mirpur Khas",
            "Chiniot",
            "Nawabshah",
            "Kāmoke",
            "Burewala",
            "Jhelum",
            "Sadiqabad",
            "Khanewal",
            "Hafizabad",
            "Kohat",
            "Jacobabad",
            "Shikarpur",
            "Muzaffargarh",
            "Khanpur",
            "Gojra",
            "Bahawalnagar",
            "Abbottabad",
            "Muridke",
            "Pakpattan",
            "Khuzdar",
            "Jaranwala",
            "Chishtian",
            "Daska",
            "Bhalwal",
            "Mandi Bahauddin",
            "Ahmadpur East",
            "Kamalia",
            "Tando Adam",
            "Khairpur",
            "Dera Ismail Khan",
            "Vehari",
            "Nowshera",
            "Dadu",
            "Wazirabad",
            "Khushab",
            "Charsada",
            "Swabi",
            "Chakwal",
            "Mianwali",
            "Tando Allahyar",
            "Kot Adu",
            "Turbat"
        )

    }


}
