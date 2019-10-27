package view.match

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sportsplayer.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.tab_layout_for_match_full_score_card.*
import view.GlobalVariable
import view.match.inningsTabs.FirstInningTabFragment
import view.match.inningsTabs.InningsSectionPager
import view.match.inningsTabs.SecondInningTabFragment

class FullScoreCardActivity: AppCompatActivity(), FirstInningTabFragment.OnFragmentInteractionListener,
    SecondInningTabFragment.OnFragmentInteractionListener{

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var ndb_Ref:FirebaseDatabase? =null
    var db_Ref: DatabaseReference?=null
    var new_db_Ref_Batsman: DatabaseReference?=null
    var new_db_Ref_Bowler: DatabaseReference?=null
    var nameFun:SecondInningTabFragment? = null

    lateinit var match_Id:String
    lateinit var team_A_Id:String
    lateinit var team_B_Id:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_layout_for_match_full_score_card)
        nameFun=SecondInningTabFragment()

    }


    override fun onStart() {
        super.onStart()

        match_Id = intent.getStringExtra("match_Id")
        team_A_Id = intent.getStringExtra("team_A_Id")
        team_B_Id = intent.getStringExtra("team_B_Id")

        val fragmentAdapter= InningsSectionPager(supportFragmentManager,match_Id)
        viewPagerFullScoreCard.adapter=fragmentAdapter
        tabLayout_match_full_scored.setupWithViewPager(viewPagerFullScoreCard)

    }

    override fun onResume() {
        super.onResume()
        fetchLiveMatchDetails()
        gettingLiveScores()
    }


    //Live Scores Starts

    private fun gettingLiveScores(){

        ndb_Ref= FirebaseDatabase.getInstance()
        //FirstInning
        db_Ref= ndb_Ref!!.getReference("/MatchScore/${match_Id}/FirstInning")
        db_Ref!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    val LiveScoreFirstInning=
                        p0.child("inningScore").value.toString().toInt()
                    val LiveWicketsFirstInning=
                        p0.child("wickets").value.toString().toInt()
                    val LiveOversFirstInning=
                        p0.child("overs").value.toString().toInt()
                    val LiveOverBallsFirstInning =
                        p0.child("over_balls").value.toString().toInt()
                    GlobalVariable.LiveMatchCurrentDetails =
                        p0.child("MatchCurrentDetail").value.toString()
                    GlobalVariable.LiveCRR =
                        p0.child("crr").value.toString().toFloat()
                    GlobalVariable.LiveRRR = 0f
                    val battingTeamId=
                        p0.child("battingTeamId").value.toString()
                    val battingTeamName=
                        p0.child("battingTeamName").value.toString()
                    val bowlingTeamId=
                        p0.child("bowlingTeamId").value.toString()
                    val bowlingTeamName=
                        p0.child("bowlingTeamName").value.toString()
                    val currentInning=
                        p0.child("CurrentInning").value.toString()


                    team_A_score_match_full_score_card.text = LiveScoreFirstInning.toString()
                    team_A_wickets_match_full_score_card.text = LiveWicketsFirstInning.toString()
                    team_A_current_over_match_full_score_card.text = LiveOversFirstInning.toString()
                    team_A_current_over_balls_match_full_score_card.text = LiveOverBallsFirstInning.toString()
if (currentInning=="FirstInning"){

    new_db_Ref_Batsman = ndb_Ref!!.getReference("/MatchScore/${match_Id}/${currentInning}/${battingTeamId}")
    new_db_Ref_Batsman!!.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                val strikerId=p0.child("StrikerId").value.toString()

                for (batsman in p0.children) {
                    val id=batsman.key.toString()

                    if(id != "OutSquad" && id != "StrikerId")
                    {
                        if (id==strikerId) {
                            val name = batsman.child("name").value.toString()
                            val btName = nameFun?.concatenateName(name)
                            val runs = batsman.child("runs").value.toString()
                            val balls_played = batsman.child("balls_played").value.toString()

                            batsman_A_on_match_full_score_card.text = btName
                            batsman_A_score_on_match_full_score_card.text = runs
                            batsman_A_balls_played_on_match_full_score_card.text = balls_played
                        }
                        else{
                            val name2 = batsman.child("name").value.toString()
                            val btName2 = nameFun?.concatenateName(name2)
                            val runs2 = batsman.child("runs").value.toString()
                            val balls_played2 = batsman.child("balls_played").value.toString()

                            batsman_B_on_match_card.text = btName2
                            batsman_B_score_on_match_card.text = runs2
                            batsman_B_balls_played_on_match_card.text = balls_played2
                        }

                    }
                }
            }
        }
    })


    new_db_Ref_Bowler = ndb_Ref!!.getReference("/MatchScore/${match_Id}/${currentInning}/${bowlingTeamId}")
    new_db_Ref_Bowler!!.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                val currentBowler=p0.child("CurrentBowler").value.toString()
                for (bowler in p0.children) {
                    val id=bowler.key.toString()
                    if(id == currentBowler)
                    {

                        val name = bowler.child("name").value.toString()
                        val blName = nameFun?.concatenateName(name)
                        val wickets = bowler.child("wickets").value.toString()
                        val runs_conceded = bowler.child("runs_conceded").value.toString()
                        val overs = bowler.child("bowler_overs").value.toString()
                        val overBalls = bowler.child("balls_bowled").value.toString()


                        bowler_A_Name.text = blName
                        bowler_A_wickets.text = wickets
                        bowler_A_runs_conceded.text = runs_conceded
                        bowler_A_bowled_overs.text = overs
                        bowler_A_bowled_overs_balls.text = overBalls
                    }
                }
            }
        }
    })

    match_detail_text_view_match_full_score_card.text = GlobalVariable.LiveMatchCurrentDetails
    current_run_rate_on_match_full_score_card.text = GlobalVariable.LiveCRR.toString()
    required_run_rate_on_match_full_score_card.text = GlobalVariable.LiveRRR.toString()

    batting_team_name.text=battingTeamName
    bowling_team_name.text=bowlingTeamName



}else{
//SecondInning
    db_Ref= ndb_Ref!!.getReference("/MatchScore/${match_Id}/SecondInning")


    db_Ref!!.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {

                val LiveScoreSecondInning=
                    p0.child("inningScore").value.toString().toInt()
                val LiveWicketsSecondInning=
                    p0.child("wickets").value.toString().toInt()
                val LiveOversSecondInning=
                    p0.child("overs").value.toString().toInt()
                val LiveOverBallsSecondInning =
                    p0.child("over_balls").value.toString().toInt()
                GlobalVariable.LiveMatchCurrentDetails =
                    p0.child("MatchCurrentDetail").value.toString()
                GlobalVariable.LiveCRR =
                    p0.child("crr").value.toString().toFloat()
                GlobalVariable.LiveRRR =
                    p0.child("rrr").value.toString().toFloat()
                val battingTeamId2=
                    p0.child("battingTeamId").value.toString()
                val battingTeamName2=
                    p0.child("battingTeamName").value.toString()
                val bowlingTeamId2=
                    p0.child("bowlingTeamId").value.toString()
                val bowlingTeamName2=
                    p0.child("bowlingTeamName").value.toString()

                new_db_Ref_Batsman = ndb_Ref!!.getReference("/MatchScore/${match_Id}/${currentInning}/${battingTeamId2}")
                new_db_Ref_Batsman!!.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val strikerId=p0.child("StrikerId").value.toString()

                            for (batsman in p0.children) {
                                val id=batsman.key.toString()

                                if(id != "OutSquad" && id != "StrikerId")
                                {
                                    if (id==strikerId) {
                                        val name = batsman.child("name").value.toString()
                                        val btName = nameFun?.concatenateName(name)
                                        val runs = batsman.child("runs").value.toString()
                                        val balls_played = batsman.child("balls_played").value.toString()

                                        batsman_A_on_match_full_score_card.text = btName
                                        batsman_A_score_on_match_full_score_card.text = runs
                                        batsman_A_balls_played_on_match_full_score_card.text = balls_played
                                    }
                                    else{
                                        val name2 = batsman.child("name").value.toString()
                                        val btName2 = nameFun?.concatenateName(name2)
                                        val runs2 = batsman.child("runs").value.toString()
                                        val balls_played2 = batsman.child("balls_played").value.toString()

                                        batsman_B_on_match_card.text = btName2
                                        batsman_B_score_on_match_card.text = runs2
                                        batsman_B_balls_played_on_match_card.text = balls_played2
                                    }

                                }
                            }
                        }
                    }
                })


                new_db_Ref_Bowler = ndb_Ref!!.getReference("/MatchScore/${match_Id}/${currentInning}/${bowlingTeamId2}")
                new_db_Ref_Bowler!!.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val currentBowler=p0.child("CurrentBowler").value.toString()
                            for (bowler in p0.children) {
                                val id=bowler.key.toString()
                                if(id == currentBowler)
                                {

                                    val name = bowler.child("name").value.toString()
                                    val blName = nameFun?.concatenateName(name)
                                    val wickets = bowler.child("wickets").value.toString()
                                    val runs_conceded = bowler.child("runs_conceded").value.toString()
                                    val overs = bowler.child("bowler_overs").value.toString()
                                    val overBalls = bowler.child("balls_bowled").value.toString()


                                    bowler_A_Name.text = blName
                                    bowler_A_wickets.text = wickets
                                    bowler_A_runs_conceded.text = runs_conceded
                                    bowler_A_bowled_overs.text = overs
                                    bowler_A_bowled_overs_balls.text = overBalls
                                }
                            }
                        }
                    }
                })


                team_B_score_match_full_score_card.text = LiveScoreSecondInning.toString()
                team_B_wickets_match_full_score_card.text = LiveWicketsSecondInning.toString()
                team_B_current_over_match_full_score_card.text = LiveOversSecondInning.toString()
                team_B_current_over_balls_match_full_score_card.text = LiveOverBallsSecondInning.toString()

                match_detail_text_view_match_full_score_card.text = GlobalVariable.LiveMatchCurrentDetails
                current_run_rate_on_match_full_score_card.text = GlobalVariable.LiveCRR.toString()
                required_run_rate_on_match_full_score_card.text = GlobalVariable.LiveRRR.toString()

                batting_team_name.text=battingTeamName2
                bowling_team_name.text=bowlingTeamName2
            }
        }
    })
}



                }
            }
        })

    }

    //Live Match Full Score Card Starts

    private fun fetchLiveMatchDetails() {

        val liveMatchRef =
            FirebaseDatabase.getInstance().getReference("MatchInfo")
        liveMatchRef.child(match_Id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val match_type = p0.child("matchType").value.toString()
                    val match_overs = p0.child("matchOvers").value.toString()
                    val battingTeamName = p0.child("battingTeamName").value.toString()
                    val team_A_Name = p0.child("team_A_Name").value.toString()
                    val team_B_Name = p0.child("team_B_Name").value.toString()
                    val team_A_Logo = p0.child("team_A_Logo").value.toString()
                    val team_B_Logo = p0.child("team_B_Logo").value.toString()

                    title_of_match_full_score_card.text = match_type
                    if (battingTeamName==team_A_Name) {
                        team_A_name_match_full_score_card.text = team_A_Name
                        Picasso.get().load(team_A_Logo).into(team_A_logo_match_full_score_card)
                        team_A_total_overs_match_full_score_card.text = match_overs
                        team_B_name_match_full_score_card.text = team_B_Name
                        Picasso.get().load(team_B_Logo).into(team_B_logo_match_full_score_card)
                        team_B_total_overs_match_full_score_card.text = match_overs
                    }
                    else{
                        team_A_name_match_full_score_card.text = team_B_Name
                        Picasso.get().load(team_B_Logo).into(team_A_logo_match_full_score_card)
                        team_A_total_overs_match_full_score_card.text = match_overs
                        team_B_name_match_full_score_card.text = team_A_Name
                        Picasso.get().load(team_A_Logo).into(team_B_logo_match_full_score_card)
                        team_B_total_overs_match_full_score_card.text = match_overs
                    }
                }
            }
        })
    }
    //Live Match Full Score Card End
}