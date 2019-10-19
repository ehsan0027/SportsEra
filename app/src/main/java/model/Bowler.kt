package model

class Bowler(
    val name:String="",
    val balls_bowled:Int=0,
    val bowler_overs:Int=0,
    val wickets:Int=0,
    val runs_conceded:Int=0,
    val num_fours_conceded:Int=0,
    val num_sixes_conceded:Int=0,
    val no_of_no_balls_by_bowler:Int=0,
    val no_of_wide_balls_by_bowler:Int=0,
    val economy:Float=0f,
    val maiden:Int=0,
    val bowlerPosition: Int=0,
    val bowlerImage:String=""
){
    constructor():this("",0,0,0,0,0,0,0,0,0f,0,0,"")
}