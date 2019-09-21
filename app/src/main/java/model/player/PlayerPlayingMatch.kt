package model.player

class PlayerPlayingMatch
    (
    //Batting
    var score: Int = 0,
    var dot_balls_played: Int=0,
    var singles: Int=0,
    var doubles: Int=0,
    var triples: Int=0,
    var num_four: Int = 0,
    var num_six: Int = 0,
    var ball_played: Int = 0,
    var out_cause: String? = "",
    var out_status: String? = "",
    var inning: Boolean = false,
    var thirty: Boolean = false,
    var fifty: Boolean = false,
    var hundred: Boolean = false,
    var overs_bowled: Int = 0,
    var runs_conceded: Int = 0,
    var maiden_overs: Int = 0,
    var economy: Int = 0,
    var wide: Int = 0,
    var no_ball: Int = 0,
    var dot_balls_bowled: Int = 0,
    var wicket: Int = 0,
    var num_fours_conceded: Int=0,
    var num_sixes_conceded: Int=0,
    var catches: Int = 0,
    var caught_behind: Int = 0,
    var runouts: Int = 0,
    var stumpings: Int = 0,
    var match_won: Boolean = false
) {
    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        "",
        "",
        false,
        false,
        false,
        false,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        false
    ) {}
}




