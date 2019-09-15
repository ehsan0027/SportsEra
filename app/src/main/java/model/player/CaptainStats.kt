package model.player

data class CaptainStats(
    var matches_as_captain: Int = 0,
    var captain_toss_won: Int = 0,
    var win_percentage: Int = 0,
    var loss_percentage: Int = 0
) {
    constructor() : this(0, 0, 0, 0)
}