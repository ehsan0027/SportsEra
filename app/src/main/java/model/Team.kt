package model

import model.player.PlayerBasicProfile

data class Team( var teamLogo:String?=null,
                val teamName:String,
                 val shortName:String,
                 val captainId:String,
                val city:String,
                val level:String,
                 val teamId:String,
                var team_score:Int=0,
                var over_played:Int=0
               )
{
    constructor():this("","","","","","","")
    var player_BasicProfile_list:List<PlayerBasicProfile>?=null
    var out_playerBasicProfile:List<PlayerBasicProfile>?=null
    var striker: PlayerBasicProfile?=null
    var non_striker: PlayerBasicProfile?=null
    var bowler: PlayerBasicProfile?=null

    //fun selectPlayer(p:List<PlayerBasicProfile>):PlayerBasicProfile

    fun addPlayer(p: PlayerBasicProfile){}
    fun removePlayer(p: PlayerBasicProfile){}
    fun replacePlayer(p1: PlayerBasicProfile, p2: PlayerBasicProfile){}
    fun striker(){}
    fun nonStriker(){}
    fun bowler(){}

}