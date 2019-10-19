package view.team.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sportsplayer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.pawegio.kandroid.visible
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_notifications_layout.*
import kotlinx.android.synthetic.main.notifications_card_match_request.view.*
import org.jetbrains.anko.find
import view.GlobalVariable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TeamRequestMatchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class TeamRequestMatchFragment(
    val team_A_Id: String,
    val team_A_Name: String,
    val team_A_Logo: String,
    val captainId_A: String
) : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var mAuth: FirebaseAuth? = null
    val matchInviteAdapter = GroupAdapter<ViewHolder>()
    val team_A_Squad = ArrayList<String>()//Creating an empty arraylist
    val team_B_Squad = ArrayList<String>()//Creating an empty arraylist

    private val currentPlayer = FirebaseAuth.getInstance().uid.toString()
    lateinit var changeDetailPopUpDialog: Dialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_layout, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        changeDetailPopUpDialog = Dialog(activity!!)  //Dialog Initialization
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
        matchInviteAdapter.clear()
        notifications_recycler_view?.removeAllViewsInLayout()
        fetchNotificationsFromDatabase()

    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    private fun setTeamASquad(teamA_id: String) {
        val teamSquadRef = FirebaseDatabase.getInstance().getReference("/Team/$teamA_id/TeamSquad")
        teamSquadRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {
                        val playerId = it.key.toString()
                        team_A_Squad.add(playerId)
                    }
                }

            }
        })

    }


    private fun setTeamBSquad(teamB_id: String) {
        val teamSquadRef = FirebaseDatabase.getInstance().getReference("/Team/$teamB_id/TeamSquad")
        teamSquadRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    p0.children.forEach {
                        val playerId = it.key.toString()
                        team_B_Squad.add(playerId)
                    }

                }
            }
        })

    }


    private fun changeMatchInviteDetailsDialog(
        mdate: String,
        mtime: String,
        mvenue: String,
        msquad: String,
        movers: String,
        mInviteId: String,
        mSender: String,
        mReceiver: String
    ) {

        changeDetailPopUpDialog.setCancelable(true)
        val view = activity?.layoutInflater?.inflate(R.layout.change_match_invite_details, null)
        changeDetailPopUpDialog.setContentView(view)

        fun setDate(view: View) {
            val date = view as EditText
            val cal = Calendar.getInstance()
            // cal.add(Calendar.YEAR)
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val myFormat = "dd.MM.yyyy" // mention the format you need
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    date.setText(sdf.format(cal.time))
                }

            DatePickerDialog(
                activity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()


        }


        fun setTime(view: View) {
            val time = view as EditText
            val matchHour = 0
            val matchMinute = 0
            val timePicker = TimePickerDialog(
                activity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    time.setText("$hourOfDay : $minutes")
                }, matchHour, matchMinute, false
            )
            timePicker.show()
        }


        val update = view?.find<Button>(R.id.update_button_change_details)
        val cancel = view?.find<Button>(R.id.cancelButton_change_details)
        val date = view?.find<EditText>(R.id.change_date_match_invite_detail)
        val time = view?.find<EditText>(R.id.change_time_match_invite_detail)
        val venue = view?.find<EditText>(R.id.change_venue_match_invite_detail)
        val squad = view?.find<EditText>(R.id.change_squad_count_match_invite_detail)
        val overs = view?.find<EditText>(R.id.change_overs_match_invite_detail)
        date?.setText(mdate)
        time?.setText(mtime)
        venue?.setText(mvenue)
        squad?.setText(msquad)
        overs?.setText(movers)

        date?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setDate(v)
            }
        }
        date?.setOnClickListener { v -> setDate(v) }

        time?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setTime(v)
            }
        }
        time?.setOnClickListener { setTime(it) }



        cancel?.setOnClickListener { changeDetailPopUpDialog.dismiss() }

        update?.setOnClickListener {

            val newDate = date?.text.toString().trim()
            val newTime = time?.text.toString().trim()
            val newVenue = venue?.text.toString().trim()
            val newSquad = squad?.text.toString().trim()
            val newOvers = overs?.text.toString().trim()

            Log.d("Sender", mSender)
            Log.d("Sender", mReceiver)
            Log.d("Sender", mInviteId)


            val newDatabaseReference = FirebaseDatabase.getInstance().reference
            val updateMatchInvite = HashMap<String, Any>()
            updateMatchInvite["/MatchInfo/$mInviteId/matchDate"] = newDate
            updateMatchInvite["/MatchInfo/$mInviteId/matchTime"] = newTime
            updateMatchInvite["/MatchInfo/$mInviteId/matchVenue"] = newVenue
            updateMatchInvite["/MatchInfo/$mInviteId/squadCount"] = newSquad
            updateMatchInvite["/MatchInfo/$mInviteId/matchOvers"] = newOvers
            if (mReceiver == currentPlayer) {
                updateMatchInvite["/MatchInfo/$mInviteId/sender"] = mReceiver
                updateMatchInvite["/MatchInfo/$mInviteId/receiver"] = mSender
            }

            newDatabaseReference.updateChildren(updateMatchInvite)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Invite is updated")
                        GlobalVariable.MATCH_OVERS = newOvers.toInt()
                        Log.d("Updated", "Invitation is Updated")
                        changeDetailPopUpDialog.cancel()
                    }
                }

        }

        changeDetailPopUpDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        changeDetailPopUpDialog.show()
    }

    private fun updateMatchInviteDetails(position: Int) {

        val item = matchInviteAdapter.getItem(position) as MyTeamsNotifications
        val date = item.matchDate
        val time = item.matchTime
        val venue = item.matchVenue
        val squad = item.squadCount
        val overs = item.matchOvers
        val inviteId = item.matchInviteId
        val sender = item.sender
        val receiver = item.receiver
        Log.d("Update", inviteId)

        changeMatchInviteDetailsDialog(
            date,
            time,
            venue,
            squad,
            overs,
            inviteId,
            sender,
            receiver
        )
    }

    private fun scheduleMatch(position: Int) {

        val v = matchInviteAdapter.getItem(position) as MyTeamsNotifications
        val matchId = v.matchInviteId
        val matchOvers = v.matchOvers
        val team_A_Id = v.team_A_Id
        setTeamASquad(team_A_Id)
        val team_B_Id = v.team_B_Id
        setTeamBSquad(team_B_Id)
        Log.d("TeamB", team_B_Id)

        GlobalVariable.MATCH_OVERS = matchOvers.toInt()
        notifications_recycler_view?.removeAllViewsInLayout()


        val newdataBaseRefTeam =
            FirebaseDatabase.getInstance().reference
        val setStatusUpcoming = HashMap<String,Any?>()
        setStatusUpcoming["TeamsMatchInfo/$team_A_Id/Upcoming/$matchId"]=true
        setStatusUpcoming["TeamsMatchInfo/$team_A_Id/Request/$matchId"]=null
        setStatusUpcoming["TeamsMatchInfo/$team_B_Id/Upcoming/$matchId"]=true
        setStatusUpcoming["TeamsMatchInfo/$team_B_Id/Request/$matchId"]=null

        newdataBaseRefTeam.updateChildren(setStatusUpcoming).addOnCompleteListener {
            task ->  if(task.isSuccessful)
        {
            val newdataBaseRef =
                FirebaseDatabase.getInstance().reference
            val newdataBaseRef_B =
                FirebaseDatabase.getInstance().reference

            newdataBaseRef.child("TeamsPlayer").child(team_A_Id)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()) {
                                val playerRef =
                                    FirebaseDatabase.getInstance()
                                        .reference
                                val setPlayerUpcomingMatch =
                                    HashMap<String, Any>()
                                p0.children.forEach {
                                    val p_id = it.key

                                    setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$matchId"] =
                                        true

                                    playerRef.updateChildren(
                                        setPlayerUpcomingMatch
                                    )
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d(
                                                    "Player",
                                                    "Team A Upcoming Match is Set in Player"
                                                )
                                            }
                                        }

                                }
                            }
                        }

                    }
                )



            newdataBaseRef_B.child("TeamsPlayer")
                .child(team_B_Id)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()) {
                                val playerRef =
                                    FirebaseDatabase.getInstance()
                                        .reference
                                val setPlayerUpcomingMatch =
                                    HashMap<String, Any>()
                                p0.children.forEach {
                                    val p_id = it.key


                                    setPlayerUpcomingMatch["PlayersMatchId/$p_id/Upcoming/$matchId"] =
                                        true
                                    playerRef.updateChildren(
                                        setPlayerUpcomingMatch
                                    )
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d(
                                                    "Player",
                                                    "Team B Upcoming Match is Set in Player"
                                                )
                                            }
                                        }

                                }
                            }
                        }

                    }
                )


            val newDatabaseSquad_A = FirebaseDatabase.getInstance().reference
            newDatabaseSquad_A.child("MatchInfo").child(matchId)
                .child("team_A_Squad")
                .setValue(team_A_Squad).addOnCompleteListener {
                    toast("Team A Squad is set")
                }


            val newDatabaseSquad_B = FirebaseDatabase.getInstance().reference
            newDatabaseSquad_B.child("MatchInfo").child(matchId)
                .child("team_B_Squad")
                .setValue(team_B_Squad).addOnCompleteListener {
                    toast("Team B Squad is set")
                }

            toast("Upcoming Match Id is sent to Team")
            Log.d(
                "Upcoming",
                "Upcoming Match Id is sent to Team"
            )

        }

    }

Log.d("reject", " Scheduled")
}


private fun rejectInvite(position: Int) {


    val item = matchInviteAdapter.getItem(position) as MyTeamsNotifications
    val invitationId = item.matchInviteId
    val team_A_Id = item.team_A_Id
    val team_B_Id = item.team_B_Id

    val newDatabaseReference = FirebaseDatabase.getInstance().reference
    val removeMatchInvite = HashMap<String, String?>()
    removeMatchInvite["/TeamsMatchInfo/$team_A_Id/Request/$invitationId"] = null
    removeMatchInvite["/TeamsMatchInfo/$team_B_Id/Request/$invitationId"] = null
    removeMatchInvite["/MatchInfo/$invitationId"] = null
    newDatabaseReference.updateChildren(removeMatchInvite as Map<String, Any?>)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                toast("Match Invitation is Removed")
                Log.d("reject", " Removed")
            }
        }
}

private fun fetchNotificationsFromDatabase() {
    mAuth = FirebaseAuth.getInstance()
    val playerId = mAuth?.uid.toString()
    val teamRef = FirebaseDatabase.getInstance()
    val teamsMatchInviteRef = FirebaseDatabase.getInstance()
    val playersTeamReference =
        FirebaseDatabase.getInstance().getReference("/PlayersTeam/$playerId")
    playersTeamReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {}
        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                Log.d("FetchMatch", "PlayerId Received")
                p0.children.forEach {
                    val teamId = it.key
                    teamRef.getReference("/TeamsMatchInfo/$teamId/Request").also { task ->
                        task.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()) {
                                    Log.d("FetchMatch", teamId)
                                    p0.children.forEach {
                                        val matchId = it.key
                                        teamsMatchInviteRef.getReference("/MatchInfo/$matchId")
                                            .also { task ->
                                                task.addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onCancelled(p0: DatabaseError) {
                                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                                    }


                                                    override fun onDataChange(p0: DataSnapshot) {

                                                        val ballType = p0.child("ballType")
                                                            .value.toString()
                                                        val matchCity =
                                                            p0.child("matchCity")
                                                                .value.toString()
                                                        val match_date =
                                                            p0.child("matchDate")
                                                                .value.toString()
                                                        val match_Invite_Id =
                                                            p0.child("matchId")
                                                                .value.toString()
                                                        val match_overs =
                                                            p0.child("matchOvers")
                                                                .value.toString()
                                                        GlobalVariable.MATCH_OVERS =
                                                            match_overs.toInt()
                                                        val match_time =
                                                            p0.child("matchTime")
                                                                .value.toString()
                                                        val matchType =
                                                            p0.child("matchType")
                                                                .value.toString()
                                                        val matchVenue =
                                                            p0.child("matchVenue")
                                                                .value.toString()
                                                        val squadCount =
                                                            p0.child("squadCount")
                                                                .value.toString()
                                                        val team_A_Id =
                                                            p0.child("team_A_Id")
                                                                .value.toString()
                                                        val team_B_Id =
                                                            p0.child("team_B_Id")
                                                                .value.toString()
                                                        val team_A_Name =
                                                            p0.child("team_A_Name")
                                                                .value.toString()
                                                        val team_B_Name =
                                                            p0.child("team_B_Name")
                                                                .value.toString()
                                                        val team_A_Logo =
                                                            p0.child("team_A_Logo")
                                                                .value.toString()
                                                        val team_B_Logo =
                                                            p0.child("team_B_Logo")
                                                                .value.toString()
                                                        val sender_Capatain =
                                                            p0.child("sender")
                                                                .value.toString()
                                                        val receiver_Captain =
                                                            p0.child("receiver")
                                                                .value.toString()
                                                        Log.d("Update2", match_Invite_Id)


                                                        matchInviteAdapter.add(
                                                            MyTeamsNotifications(
                                                                matchType,
                                                                match_overs,
                                                                matchCity,
                                                                matchVenue,
                                                                match_date,
                                                                match_time,
                                                                ballType,
                                                                squadCount,
                                                                team_A_Id,
                                                                team_B_Id,
                                                                team_A_Name,
                                                                team_B_Name,
                                                                team_A_Logo,
                                                                team_B_Logo,
                                                                match_Invite_Id,
                                                                sender_Capatain,
                                                                receiver_Captain,
                                                                this@TeamRequestMatchFragment
                                                            )
                                                        )
                                                    }
                                                })
                                            }
                                    }
                                }

                            }

                        })
                    }
                }

                notifications_recycler_view?.adapter = matchInviteAdapter

            }
        }
    })

}


class MyTeamsNotifications(
    val matchType: String,
    val matchOvers: String,
    val matchCity: String,
    val matchVenue: String,
    val matchDate: String,
    val matchTime: String,
    val ballType: String,
    val squadCount: String,
    val team_A_Id: String,
    val team_B_Id: String,
    val team_A_Name: String,
    val team_B_Name: String,
    val team_A_Logo: String,
    val team_B_Logo: String,
    val matchInviteId: String,
    val sender: String,
    val receiver: String,
    val ctx: TeamRequestMatchFragment
) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.notifications_card_match_request
    }

    private fun makeViewsvisible(vararg view: View) {
        for (v in view) {
            v.visible = true
        }
    }

    private fun makeViewsInvisible(vararg view: View) {
        for (v in view) {
            v.visible = false
        }
    }


    override fun bind(viewHolder: ViewHolder, position: Int) {

        if (ctx.currentPlayer == sender) {
            makeViewsInvisible(viewHolder.itemView.accept_match_challenge)
        } else if (ctx.currentPlayer != sender && ctx.currentPlayer != receiver) {
            makeViewsInvisible(
                viewHolder.itemView.accept_match_challenge,
                viewHolder.itemView.change_details_match_challenge,
                viewHolder.itemView.reject_match_challenge
            )
        }


        viewHolder.itemView.notification_cardView
        viewHolder.itemView.match_type_notification_card.text = matchType
        viewHolder.itemView.ball_type_of_match.text = ballType
        viewHolder.itemView.date_of_match.text = matchDate
        viewHolder.itemView.starting_time_of_match.text = matchTime
        viewHolder.itemView.squad_count_notification_card.text = squadCount
        viewHolder.itemView.overs_count_notification_card.text = matchOvers
        viewHolder.itemView.venue_notification_card.text = matchVenue
        viewHolder.itemView.city_notification_card.text = matchCity

        viewHolder.itemView.team_A_name_notification_card.text = team_A_Name
        val logo_team_A =
            viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_notification_card)
        Picasso.get().load(team_A_Logo).into(logo_team_A)

        viewHolder.itemView.team_B_name_notification_card.text = team_B_Name
        val logo_team_B =
            viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_notification_card)
        Picasso.get().load(team_B_Logo).into(logo_team_B)

        viewHolder.itemView.accept_match_challenge.setOnClickListener {
            ctx.scheduleMatch(position)
        }

        viewHolder.itemView.change_details_match_challenge.setOnClickListener {
            ctx.updateMatchInviteDetails(position)
        }


        viewHolder.itemView.reject_match_challenge.setOnClickListener {
            ctx.rejectInvite(position)
        }

    }
}
}


