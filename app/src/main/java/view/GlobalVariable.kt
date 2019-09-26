package view

import android.app.Application

class GlobalVariable : Application() {


    companion object {

        //Match
        var BATTING_TEAM_ID: String = ""
        var BATTING_TEAM_NAME: String = ""
        var MATCH_ID: String = ""
        var BOWLING_TEAM_ID: String = ""
        var BOWLING_TEAM_NAME: String = ""
        var MATCH_OVERS: Int = 0


        //Team

        var CURRENT_OVER: Int = 0
        var CURRENT_BALL: Int = 0
        var CURRENT_TEAM_SCORE: Int = 0
        var TEAM_WICKET: Int = 0
        var TOTAL_TEAM_OVERS: Int = 0
        var TEAM_CRR: Float = 0F
        var TEAM_RRR: Float = 0F

        //Striker

        var STRIKER_ID: String = ""
        var STRIKER_NAME: String = ""
        var STRIKER_SCORE: Int = 0
        var STRIKER_BALL: Int = 0
        var STRIKER_doubles: Int = 0
        var STRIKER_TRIPLES: Int = 0
        var STRIKER_NUM_FOUR: Int = 0
        var STRIKER_NUM_SIX: Int = 0
        var STRIKER_SR: Float = 0f
        var Striker_DOT_BALLS_Played:Int=0


        //Non Striker

        var NON_STRIKER_ID: String = ""
        var NON_STRIKER_NAME: String = ""
        var NON_STRIKER_SCORE: Int = 0
        var NON_STRIKER_BALL: Int = 0
        var non_STRIKER_doubles: Int = 0
        var non_STRIKER_TRIPLES: Int = 0
        var NON_STRIKER_NUM_FOUR: Int = 0
        var NON_STRIKER_NUM_SIX: Int = 0
        var NON_STRIKER_SR: Float = 0f
       var Non_Striker_DOT_BALLS_Played:Int=0


        //Partnership

        var CURRENT_PARTNERSHIP_RUNS: Int = 0
        var CURRENT_PARTNERSHIP_BALLS: Int = 0
        var bowler_selection: Int=1


        //Bowler

        var BOWLER_ID: String = ""
        var BOWLER_NAME: String = ""
        var BOWLER_OVERS: Int = 0
        var BOWLER_MAIDEN: Int = 0
        var BOWLER_WICKET: Int = 0
        var BOWLER_ECONEMY: Float = 0f
        var num_sixes_conceded: Int = 0
        var num_fours_conceded: Int = 0
        var BOWLER_BALLS_BOWLED: Int = 0
        var BOWLER_RUNS_CONCEDED: Int = 0
        var DOT_BALLS_BOWLED:Int=0

        var BOWLING_TEAM_SQUAD = ArrayList<String>()
        var BATTING_TEAM_SQUAD = ArrayList<String>()


    }
}
