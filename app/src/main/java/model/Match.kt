package model

import kotlin.collections.ArrayList

class Match(
    val matchType:String,
    val matchOver:String,
    val matchGround:String,
    val matchDate:String,
    val matchTime:String,
    val ballType:String,
    var team_A_id:String,
    var team_B_id:String,
    val team_A_Squad: ArrayList<String>? =null,
    val team_B_Squad: ArrayList<String>? =null,
    val matchUpmire:String,
    val matchId:String)
{
constructor():this("","","","","","","","",null,null,"","")

//    var current_score:Int
//    var current_over:Int
//    var current_ball:Int
//    var inning_team:Team?=null
//    var bowling_team:Team?=null



    fun startMatch(){}
    fun setInningTeam(t:Team) {}















}