package model

class Inning(
    val inningScore:Int=0,
    val overs:Int=0,
    val over_balls:Int=0,
    val wickets:Int=0,
    val extras:Int=0,
    val crr:Float=0f,
    val rrr:Float=0f
) {
    constructor():this(0,0,0,0,0,0f,0f)
}