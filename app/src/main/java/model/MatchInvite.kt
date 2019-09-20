package model

class MatchInvite(val matchType:String,
                  val matchOvers:String,
                  val matchCity:String,
                  val matchVenue:String,
                  val matchDate:String,
                  val matchTime:String,
                  val ballType:String,
                  val squadCount:String,
                  val team_A:String,
                  val team_B:String,
                  val matchInvite:String,
                  val sender:String,
                  val reciever:String
                  )
{
    constructor():this("","","","","","","","","","","","","")

}