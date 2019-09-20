package model

class MatchInvite(val matchType:String,
                  val matchOvers:String,
                  val matchCity:String,
                  val matchVenue:String,
                  val matchDate:String,
                  val matchTime:String,
                  val ballType:String,
                  val squadCount:String,
                  val team_A_Id:String,
                  val team_B_Id:String,
                  val team_A_Name:String,
                  val team_B_Name:String,
                  val team_A_Logo:String,
                  val team_B_Logo:String,
                  val matchInvite:String,
                  val sender:String,
                  val reciever:String
                  )
{
    constructor():this("","","","","","","","","","","","","","","",""," ")

}