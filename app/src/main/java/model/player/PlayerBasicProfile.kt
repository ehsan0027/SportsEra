package model.player

data class PlayerBasicProfile(
    val profile_img: String? = null,
    val name: String,
    val phoneNumber: String,
    val playerId: String,
    val dateOfBirth: String? = "",
    val city: String? = "",
    var playing_role: String? = "",
    var batting_style: String? = "",
    var bowling_style: String? = "",
    var gender: String = ""
) {
    constructor() : this("", "", "", "")


    //fun selectPlayer(list: List<PlayerBasicProfile>):PlayerBasicProfile{}
    //fun nextPlayer(list:List<PlayerBasicProfile>):PlayerBasicProfile{}

    /**
    fun scoreIncrement(runScore:Int)
    {
    when(runScore){
    1->score++
    2-> score += 2
    3-> score += 3
    4-> score += 4
    }
    }
    fun makeRequestToPlay(){}
     **/
    //fun createSchedule(date: Date,time:Time,ground:String):MatchSchudle{}
    /**
    fun countFour()=num_four++
    fun countSix()=num_six++
    fun countOver()=over_taken++
    fun countWide()=wide++
    fun countNoBall()=no_ball++
    fun countWicket()=wicket++
    fun playedBall()=ball_played++

    fun requestToEnroll(teamCaptain:PlayerBasicProfile){}

     **/
}