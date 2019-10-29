package view.match.inningsTabs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sportsplayer.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.batsman_card_for_full_score_card.view.*
import kotlinx.android.synthetic.main.bowler_card_for_full_score_card.view.*
import kotlinx.android.synthetic.main.fragment_first_inning_tab.*

private const val ARG_PARAM1 = "param1"

class FirstInningTabFragment : Fragment() {

    private var matchId: String? = null
    private var battingTeamId: String? = null
    private var bowlingTeamId: String? = null
    private var newDBRef: DatabaseReference? = null
    private var newDBRefBatsman: DatabaseReference? = null
    private var newDBRefBowler: DatabaseReference? = null
    private var listener: OnFragmentInteractionListener? = null
    var nameFun:SecondInningTabFragment? = null
    private var lastScore: Int = 0
    private var lastBall: Int = 0

    private val batsmanAdapter = GroupAdapter<ViewHolder>()
    private val bowlerAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            matchId = it.getString(ARG_PARAM1)
        }

    }

    override fun onResume() {
        super.onResume()
        battingTeamId = ""
        bowlingTeamId = ""
        getInningData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    nameFun=SecondInningTabFragment()
        return inflater.inflate(R.layout.fragment_first_inning_tab, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(mID: String) =
            FirstInningTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, mID)
                }
            }
    }

    private fun getInningData(){
        newDBRef = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/FirstInning")
        newDBRef?.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()){

                val  totalRuns = p0.child("inningScore").value.toString().toInt()
                val  over_balls = p0.child("over_balls").value.toString().toInt()
                if (lastScore != totalRuns || lastBall != over_balls) {
                    batsmanAdapter.clear()
                    bowlerAdapter.clear()
                    batting_recycler_view_first_inning?.removeAllViewsInLayout()
                    bowling_recycler_view_first_inning?.removeAllViewsInLayout()

                    lastScore = totalRuns
                    lastBall = over_balls

                    val totalExtras = p0.child("extras").value.toString()
                    var wides = p0.child("Wides").value.toString()
                    var noBall = p0.child("NoBalls").value.toString()
                    var bye = p0.child("Byes").value.toString()
                    var legBye = p0.child("LegByes").value.toString()

                    val wickets = p0.child("wickets").value.toString()
                    val overs = p0.child("overs").value.toString()
                    val fow = p0.child("FOW$wickets").value.toString()
                    val fowInstance = p0.child("FOW_Instance$wickets").value.toString()


                    battingTeamId = p0.child("battingTeamId").value.toString()
                    bowlingTeamId = p0.child("bowlingTeamId").value.toString()
                    getBatsmanData()
                    getOutBatsmansData()
                    getBowlerData()
                    total_extras_first_inning.text = totalExtras
                    if (wides == "null") {
                        wides = "0"
                    }
                    if (noBall == "null") {
                        noBall = "0"
                    }
                    if (bye == "null") {
                        bye = "0"
                    }
                    if (legBye == "null") {
                        legBye = "0"
                    }
                    no_ball_score_first_inning.text = noBall
                    wide_score_first_inning.text = wides
                    bye_score_first_inning.text = bye
                    leg_byes_score_first_inning.text = legBye
                    total_runs_first_inning.text = totalRuns.toString()
                    wickets_first_inning.text = wickets
                    overs_bowled_first_inning.text = overs
                    over_balls_bowled_first_inning.text = over_balls.toString()
                    fall_of_wicket_first_inning.text = fow
                    fall_of_wicket_batsman_name_first_inning.text = fowInstance
                }
            }
            }
        })
    }

    private fun getBatsmanData(){
        newDBRefBatsman = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/FirstInning/$battingTeamId")
        newDBRefBatsman?.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i->
                        val childId=i.key.toString()
                        if(childId != "StrikerId"&& childId != "OutSquad" && childId !="YetToBat"){

                            val logo=i.child("batsmanLogo").value.toString()
                            val name=i.child("name").value.toString()
                            val btName = nameFun?.concatenateName(name)
                            val runs=i.child("runs").value.toString()
                            val ballsPlayed=i.child("balls_played").value.toString()
                            val fours=i.child("no_of_fours").value.toString()
                            val sixes=i.child("no_of_six").value.toString()
                            val strikeRate=i.child("strikeRate").value.toString()
                            val battingPosition = i.child("battingPosition").value.toString()


                            batsmanAdapter.add(BatsmanViewHolder(
                                logo,
                                btName,
                                runs,
                                ballsPlayed,
                                fours,
                                sixes,
                                strikeRate,
                                battingPosition
                                )
                            )

                        }

                    }
                    batting_recycler_view_first_inning?.adapter = batsmanAdapter
                }
            }
        })
    }


    private fun getOutBatsmansData(){
        newDBRefBatsman = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/FirstInning/$battingTeamId/OutSquad")
        newDBRefBatsman?.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i->

                            val logo=i.child("batsmanLogo").value.toString()
                            val name=i.child("name").value.toString()
                        val btName = nameFun?.concatenateName(name)
                            val runs=i.child("runs").value.toString()
                            val ballsPlayed=i.child("balls_played").value.toString()
                            val fours=i.child("no_of_fours").value.toString()
                            val sixes=i.child("no_of_six").value.toString()
                            val strikeRate=i.child("strikeRate").value.toString()
                            val battingPosition = i.child("battingPosition").value.toString()


                            batsmanAdapter.add(BatsmanViewHolder(
                                logo,
                                btName,
                                runs,
                                ballsPlayed,
                                fours,
                                sixes,
                                strikeRate,
                                battingPosition
                            )
                            )



                    }
                    batting_recycler_view_first_inning?.adapter = batsmanAdapter
                }
            }
        })
    }


    class BatsmanViewHolder(
        val batsmanPic:String,
        val batsmanName:String?,
        val batsmanRuns:String,
        val ballsPlayed:String,
        val noOfFours:String,
        val noOfSixes:String,
        val strikeRate:String,
        val battingPosition:String
    ) : Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.batsman_card_for_full_score_card
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.batsman_cardView
            val batsmanImage = viewHolder.itemView.findViewById<ImageView>(R.id.batsmanImage_batsmanCardView)
            Picasso.get().load(batsmanPic).into(batsmanImage)
            viewHolder.itemView.batsman_name_batsmanCardView.text =batsmanName
            viewHolder.itemView.batsman_runs.text =batsmanRuns
            viewHolder.itemView.batsman_balls_played.text =ballsPlayed
            viewHolder.itemView.batsman_number_4s.text =noOfFours
            viewHolder.itemView.batsman_number_6s.text =noOfSixes
            viewHolder.itemView.batsman_strike_rate.text =strikeRate
        }
    }


    private fun getBowlerData(){
        newDBRefBowler = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/FirstInning/$bowlingTeamId")
        newDBRefBowler?.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i ->
                        val childId = i.key.toString()
                        if (childId != "CurrentBowler") {


                            val img = i.child("bowlerImage").value.toString()
                            val name = i.child("name").value.toString()
                            val blName = nameFun?.concatenateName(name)
                            val overs = i.child("bowler_overs").value.toString()
                            val ballsBowled = i.child("balls_bowled").value.toString()
                            val maiden = i.child("maiden").value.toString()
                            val runsConceded = i.child("runs_conceded").value.toString()
                            val wickets = i.child("wickets").value.toString()
                            val economy = i.child("economy").value.toString()
                            val bowlerPosition = i.child("bowlerPosition").value.toString()


                            bowlerAdapter.add(
                                BowlerViewHolder(
                                    img,
                                    blName,
                                    overs,
                                    ballsBowled,
                                    maiden,
                                    runsConceded,
                                    wickets,
                                    economy,
                                    bowlerPosition
                                )
                            )

                        }
                    }
                    bowling_recycler_view_first_inning?.adapter = bowlerAdapter
                }
            }
        })
    }

    class BowlerViewHolder(
        val bowlerPic:String,
        val bowlerName:String?,
        val bowlerOvers:String,
        val ballsBowled:String,
        val maiden:String,
        val runsConceded:String,
        val wickets:String,
        val economy:String,
        val bowlerPosition:String

    ) : Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.bowler_card_for_full_score_card
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.bowler_cardView
            val bowlerImage = viewHolder.itemView.findViewById<ImageView>(R.id.bowlerImage_bowlerCardView)
            Picasso.get().load(bowlerPic).into(bowlerImage)
            viewHolder.itemView.bowler_name_bowlerCardView.text = bowlerName
            viewHolder.itemView.bowler_overs.text = bowlerOvers
            viewHolder.itemView.bowler_over_balls.text = ballsBowled
            viewHolder.itemView.bowler_maiden_overs.text = maiden
            viewHolder.itemView.bowler_runs_conceded.text = runsConceded
            viewHolder.itemView.bowler_wickets.text = wickets
            viewHolder.itemView.bowler_economy.text = economy
        }
    }
}
