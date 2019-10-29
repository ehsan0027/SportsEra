 package model

class Batsman (
    var name:String="",
    var runs:Int = 0,
    var balls_played:Int = 0,
    var dots:Int = 0,
    var singles:Int = 0,
    var doubles:Int = 0,
    var triples:Int = 0,
    var no_of_fours:Int = 0,
    var no_of_six:Int = 0,
    var strikeRate:Float = 0f,
    var battingPosition:Int = 0,
    var batsmanLogo:String = ""
    ){
constructor():this("",0,0,0,0,0,0,0,0,0f,0,"")}
