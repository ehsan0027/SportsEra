package model

class Inning(
    val inningScore:Int=0,
    val overs:Int=0,
    val over_balls:Int=0,
    val wickets:Int=0,
    val extras:Int=0
) {
    constructor():this(0,0,0,0,0)
}