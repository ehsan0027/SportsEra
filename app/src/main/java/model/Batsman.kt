 package model

class Batsman (
    val name:String="",
    val runs:Int = 0,
    val balls_played:Int = 0,
    val dots:Int = 0,
    val singles:Int = 0,
    val doubles:Int = 0,
    val triples:Int = 0,
    val no_of_fours:Int = 0,
    val no_of_six:Int = 0,
    val strikeRate:Float = 0f,
    val battingPosition:Int = 0,
    val batsmanLogo:String = ""
    ){
constructor():this("",0,0,0,0,0,0,0,0,0f,0,"")}
