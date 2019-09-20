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
import model.MatchInvite
import org.jetbrains.anko.find
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
    val teamId: String,
    val teamName: String,
    val teamLogo: String,
    val captainId: String
) : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var mAuth: FirebaseAuth? = null
    val matchInviteAdapter = GroupAdapter<ViewHolder>()


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
        fetchNotificationsFromDatabase()


    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }




    private fun changeMatchInviteDetailsDialog(
        mdate: String,
        mtime: String,
        mvenue: String,
        msquad: String,
        movers: String,
        mInviteId: String
    ) {
        changeDetailPopUpDialog.setCancelable(true)
        val view = activity?.layoutInflater?.inflate(R.layout.change_match_invite_details, null)
        changeDetailPopUpDialog.setContentView(view)

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
        }
        time?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
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

            date?.setOnClickListener {
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
        }

        time?.setOnClickListener {
            val matchHour = 0
            val matchMinute = 0
            val timePicker = TimePickerDialog(activity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
                    time.setText("$hourOfDay : $minutes")
                }, matchHour, matchMinute, false
            )
            timePicker.show()
        }



        cancel?.setOnClickListener { changeDetailPopUpDialog.dismiss() }

        update?.setOnClickListener {

            val newDate = date?.text.toString().trim()
            val newTime = time?.text.toString().trim()
            val newVenue = venue?.text.toString().trim()
            val newSquad = squad?.text.toString().trim()
            val newOvers = overs?.text.toString().trim()

            val newDatabaseReference = FirebaseDatabase.getInstance().reference
            val updateMatchInvite = HashMap<String, Any>()
            updateMatchInvite["/MatchInvite/$mInviteId/matchDate"] = newDate
            updateMatchInvite["/MatchInvite/$mInviteId/matchTime"] = newTime
            updateMatchInvite["/MatchInvite/$mInviteId/matchVenue"] = newVenue
            updateMatchInvite["/MatchInvite/$mInviteId/squadCount"] = newSquad
            updateMatchInvite["/MatchInvite/$mInviteId/matchOvers"] = newOvers

            newDatabaseReference.updateChildren(updateMatchInvite).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toast("Invite is updated")
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

        changeMatchInviteDetailsDialog(date, time, venue, squad, overs, inviteId)
    }

    private fun scheduleMatch(position: Int) {

        val v = matchInviteAdapter.getItem(position) as MyTeamsNotifications
        val invite_Id = v.matchInviteId
        val matchType = v.matchType
        val matchOvers = v.matchOvers
        val ballType = v.ballType
        val squadCount = v.squadCount
        val matchCity = v.matchCity
        val matchDate = v.matchDate
        val matchTime = v.matchTime
        val matchVenue = v.matchVenue
        val team_A = v.team_A
        val team_B = v.team_B
        val sender = v.sender
        val receiver = v.receiver

        Log.d("Invitation_Id", invite_Id)
        val match = MatchInvite(
            matchType,
            matchOvers,
            matchCity,
            matchVenue,
            matchDate,
            matchTime,
            ballType,
            squadCount,
            team_A,
            team_B,
            invite_Id,
            sender,
            receiver
        )
        val teamsMatchScheduleRef = FirebaseDatabase.getInstance().reference
        teamsMatchScheduleRef.child("ScheduledMatch").child(invite_Id).setValue(match)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //remove invitation

                    val newDatabaseReference = FirebaseDatabase.getInstance().reference
                    val removeMatchInvite = HashMap<String, String?>()
                    removeMatchInvite["/TeamsMatchInvite/$team_A/$invite_Id"] = null
                    removeMatchInvite["/TeamsMatchInvite/$team_B/$invite_Id"] = null
                    removeMatchInvite["/MatchInvite/$invite_Id"] = null
                    newDatabaseReference.updateChildren(removeMatchInvite as Map<String, Any?>)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Match is Scheduled")
                                Log.d("reject", " Scheduled")
                            }
                        }

                }
            }.addOnFailureListener { exception ->
                toast(exception.localizedMessage)
            }


    }

    private fun rejectInvite(position: Int) {


        val item = matchInviteAdapter.getItem(position) as MyTeamsNotifications
        val invitationId = item.matchInviteId
        val team_A_Id = item.team_A
        val team_B_Id = item.team_B

        val newDatabaseReference = FirebaseDatabase.getInstance().reference
        val removeMatchInvite = HashMap<String, String?>()
        removeMatchInvite["/TeamsMatchInvite/$team_A_Id/$invitationId"] = null
        removeMatchInvite["/TeamsMatchInvite/$team_B_Id/$invitationId"] = null
        removeMatchInvite["/MatchInvite/$invitationId"] = null
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
                        teamRef.getReference("/TeamsMatchInvite/$teamId").also { task ->
                            task.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if (p0.exists()) {
                                        Log.d("FetchMatch", teamId)
                                        p0.children.forEach {
                                            val matchInviteId = it.key
                                            teamsMatchInviteRef.getReference("/MatchInvite/$matchInviteId")
                                                .also { task ->
                                                    task.addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onCancelled(p0: DatabaseError) {
                                                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                                        }


                                                        override fun onDataChange(p0: DataSnapshot) {

                                                            val ballType = p0.child("ballType")
                                                                .value.toString()
                                                            val matchCity = p0.child("matchCity")
                                                                .value.toString()
                                                            val match_date = p0.child("matchDate")
                                                                .value.toString()
                                                            val match_Invite_Id =
                                                                p0.child("matchId").value.toString()
                                                            val match_overs = p0.child("matchOvers")
                                                                .value.toString()
                                                            val match_time = p0.child("matchTime")
                                                                .value.toString()
                                                            val matchType = p0.child("matchType")
                                                                .value.toString()
                                                            val matchVenue = p0.child("matchVenue")
                                                                .value.toString()
                                                            val squadCount = p0.child("squadCount")
                                                                .value.toString()
                                                            val team_A_Id =
                                                                p0.child("team_A").value.toString()
                                                            val team_B_Id =
                                                                p0.child("team_B").value.toString()
                                                            val sender_Capatain =
                                                                p0.child("sender").value.toString()
                                                            val reciever_Captain =
                                                                p0.child("reciever").value.toString()


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
                                                                    match_Invite_Id,
                                                                    sender_Capatain,
                                                                    reciever_Captain,
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

                    notifications_recycler_view.adapter = matchInviteAdapter

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
        val team_A: String,
        val team_B: String,
        val matchInviteId: String,
        val sender:String,
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

            if(ctx.currentPlayer==sender)
            { makeViewsInvisible(viewHolder.itemView.accept_match_challenge) }






            viewHolder.itemView.notification_cardView
            viewHolder.itemView.match_type_notification_card.text = matchType
            viewHolder.itemView.ball_type_of_match.text = ballType
            viewHolder.itemView.date_of_match.text = matchDate
            viewHolder.itemView.starting_time_of_match.text = matchTime
            viewHolder.itemView.squad_count_notification_card.text = squadCount
            viewHolder.itemView.overs_count_notification_card.text = matchOvers
            viewHolder.itemView.venue_notification_card.text = matchVenue
            viewHolder.itemView.city_notification_card.text = matchCity

            viewHolder.itemView.accept_match_challenge.setOnClickListener {
                ctx.scheduleMatch(position)
            }

            viewHolder.itemView.change_details_match_challenge.setOnClickListener {
                ctx.updateMatchInviteDetails(position)
            }


            viewHolder.itemView.reject_match_challenge.setOnClickListener {
                ctx.rejectInvite(position)
            }


            val teamARef = FirebaseDatabase.getInstance().getReference("/Team/$team_A")
            teamARef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val nameTeamA = p0.child("teamName").value.toString()
                    val logoTeamA = p0.child("teamLogo").value.toString()
                    Log.d("FetchMatch", nameTeamA)

                    viewHolder.itemView.team_A_name_notification_card.text = nameTeamA
                    val logo_team_A =
                        viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_notification_card)
                    Picasso.get().load(logoTeamA).into(logo_team_A)

                }
            })


            val teamBRef = FirebaseDatabase.getInstance().getReference("/Team/$team_B")
            teamBRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val nameTeamB = p0.child("teamName").value.toString()
                    val logoTeamB = p0.child("teamLogo").value.toString()


                    Log.d("FetchMatch", nameTeamB)

                    viewHolder.itemView.team_B_name_notification_card.text = nameTeamB
                    val logo_team_B =
                        viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_notification_card)
                    Picasso.get().load(logoTeamB).into(logo_team_B)

                }
            })

        }
    }
}


