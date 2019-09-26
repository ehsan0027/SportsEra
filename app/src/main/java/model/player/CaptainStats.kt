package model.player

class CaptainStats(

    //Captain Stats
    var matches_as_captain:Int=0,
    var captain_toss_won:Int=0,
    var win_percentage:Float=0f,
    var loss_percentage:Float=0f
) {

    constructor():this(0,0,0f,0f)

}