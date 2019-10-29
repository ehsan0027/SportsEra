package view.match.inningsTabs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.fragment_second_inning_tab.*
import view.GlobalVariable

private const val ARG_PARAM1 = "param1"

class SecondInningTabFragment : Fragment() {
    private var matchId: String? = null
    private var battingTeamId: String? = null
    private var bowlingTeamId: String? = null
    private var newDBRef: DatabaseReference? = null
    private var newDBRefBatsman: DatabaseReference? = null
    private var newDBRefBowler: DatabaseReference? = null
    private var listener: OnFragmentInteractionListener? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_inning_tab, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    override fun onResume() {
        super.onResume()
        battingTeamId = ""
        bowlingTeamId = ""
        getInningData()

    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(mId: String) =
            SecondInningTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, mId)
                }
            }
    }

    private fun toWords(name:String) =name.trim().splitToSequence(' ').filter { it.isNotEmpty() }.toList()

    fun concatenateName(name:String):String{
        var nameOfBatsman:String
        val nameArray=toWords(name)
        val index= nameArray.lastIndex
        when (index) {
            1 -> {
                val fw= if(name.contains(" ")){name.split(' ').first()}else{ return ""}
                val fc=fw.substring(0,1)
                nameOfBatsman = "$fc.${nameArray[1]}"
           return nameOfBatsman
            }
            2 -> {
                val fw= if(name.contains(" ")){name.split(' ').first()}else{ return ""}
                val fc=fw.substring(0,1)
                val sW= if(name.contains(" ")){name.split(' ')[1]}else{ return ""}
                val sC=sW.substring(0,1)
                nameOfBatsman = "$fc.$sC.${nameArray[2]}"
                return nameOfBatsman
            }
            3 -> {
                val fw= if(name.contains(" ")){name.split(' ').first()}else{ return ""}
                val fc=fw.substring(0,1)
                val sW= if(name.contains(" ")){name.split(' ')[1]}else{ return ""}
                val sC=sW.substring(0,1)
                val tW= if(name.contains(" ")){name.split(' ')[2]}else{ return ""}
                val tC=tW.substring(0,1)
                nameOfBatsman = "$fc.$sC.$tC.${nameArray[3]}"
                return nameOfBatsman
            }
            4 -> {
                val fw= if(name.contains(" ")){name.split(' ').first()}else{ return ""}
                val fc=fw.substring(0,1)
                val sW= if(name.contains(" ")){name.split(' ')[1]}else{ return ""}
                val sC=sW.substring(0,1)
                val tW= if(name.contains(" ")){name.split(' ')[2]}else{ return ""}
                val tC=tW.substring(0,1)
                val ftW= if(name.contains(" ")){name.split(' ')[3]}else{ return ""}
                val ftC=ftW.substring(0,1)
                nameOfBatsman = "$fc.$sC.$tC.$ftC.${nameArray[4]}"
                return nameOfBatsman
            }
       else->{return ""}
        }

    }

    private fun getInningData(){
        newDBRef = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/SecondInning")
        newDBRef?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val totalRuns = p0.child("inningScore").value.toString().toInt()
                    val over_balls = p0.child("over_balls").value.toString().toInt()

                    if(lastScore != totalRuns || lastBall != over_balls)
                    {
                        batsmanAdapter.clear()
                        bowlerAdapter.clear()
                        batting_recycler_view_second_inning?.removeAllViewsInLayout()
                        bowling_recycler_view_second_inning?.removeAllViewsInLayout()

                        lastScore=totalRuns
                        lastBall=over_balls
                        val totalExtras= p0.child("extras").value.toString()
                        var wides = p0.child("Wides").value.toString()
                        var noBall = p0.child("NoBalls").value.toString()
                        var bye = p0.child("Byes").value.toString()
                        var legBye = p0.child("LegByes").value.toString()
                        val wickets = p0.child("wickets").value.toString()
                        val fow = p0.child("FOW$wickets").value.toString()
                        val fowInstance = p0.child("FOW_Instance$wickets").value.toString()
                        val overs = p0.child("overs").value.toString()
                        battingTeamId = p0.child("battingTeamId").value.toString()
                        bowlingTeamId = p0.child("bowlingTeamId").value.toString()
                        getBatsmanData()
                        getOutBatsmansData()
                        getBowlerData()
                        if (wides=="null"){
                            wides = "0"
                        }
                        if (noBall=="null"){
                            noBall = "0"
                        }
                        if (bye=="null"){
                            bye = "0"
                        }
                        if (legBye =="null"){
                            legBye = "0"
                        }
                        total_extras_second_inning?.text = totalExtras
                        no_ball_score_second_inning?.text = noBall
                        wide_score_second_inning?.text = wides
                        bye_score_second_inning?.text = bye
                        leg_byes_score_second_inning?.text = legBye
                        total_runs_second_inning.text = totalRuns.toString()
                        wickets_second_inning.text = wickets
                        overs_bowled_second_inning.text = overs
                        over_balls_bowled_second_inning.text = over_balls.toString()
                        fall_of_wicket_second_inning.text = fow
                        fall_of_wicket_batsman_name_second_inning.text = fowInstance


                    }else{
                        Log.d("InningScore","Something went wrong")}


                }
            }
        })
    }

    private fun getBatsmanData(){
        newDBRefBatsman = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/SecondInning/$battingTeamId")
        newDBRefBatsman?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i->
                        val childId=i.key.toString()
                        if(childId != "StrikerId"&& childId != "OutSquad" && childId!="YetToBat"){

                            val logo=i.child("batsmanLogo").value.toString()
                            val name=i.child("name").value.toString()
                            val btName = concatenateName(name)
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
                    batting_recycler_view_second_inning?.adapter = batsmanAdapter
                }
            }
        })
    }


    private fun getOutBatsmansData(){
        newDBRefBatsman = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/SecondInning/$battingTeamId/OutSquad")
        newDBRefBatsman?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i->

                        val logo=i.child("batsmanLogo").value.toString()
                        val name=i.child("name").value.toString()
                        val btName = concatenateName(name)
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
                    batting_recycler_view_second_inning?.adapter = batsmanAdapter
                }
            }
        })
    }


    class BatsmanViewHolder(
        val batsmanPic:String,
        val batsmanName:String,
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
        newDBRefBowler = FirebaseDatabase.getInstance().getReference("/MatchScore/$matchId/SecondInning/$bowlingTeamId")
        newDBRefBowler?.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    p0.children.forEach { i ->
                        val childId = i.key.toString()
                        if (childId != "CurrentBowler") {
                            GlobalVariable.num = 1

                            val img = i.child("bowlerImage").value.toString()
                            val name = i.child("name").value.toString()
                            val blName = concatenateName(name)
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
                    bowling_recycler_view_second_inning?.adapter = bowlerAdapter
                }
            }
        })
    }

    class BowlerViewHolder(
        val bowlerPic:String,
        val bowlerName:String,
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
