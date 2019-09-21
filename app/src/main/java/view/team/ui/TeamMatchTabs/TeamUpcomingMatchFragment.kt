package view.team.ui.TeamMatchTabs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_team_upcoming_match_card.view.*
import kotlinx.android.synthetic.main.fragment_team_upcoming_match_recycler_view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TeamUpcomingMatchFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class TeamUpcomingMatchFragment (private val teamId:String)  : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    val upcomingMatchAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_team_upcoming_match_recycler_view,
            container,
            false
        )
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
        fetchUpcomingMatchDetails(teamId)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun fetchUpcomingMatchDetails(teamId: String) {
        val teamUpcomingMatch = "TeamUpcomingMatch"
        val newDatabaseRef =
            FirebaseDatabase.getInstance().getReference("Team/$teamId/$teamUpcomingMatch")
        newDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val scheduleMatchRef =
                        FirebaseDatabase.getInstance().getReference("ScheduledMatch")
                    p0.children.forEach {

                        val upcomingMatchId = it.key
                        scheduleMatchRef.child("$upcomingMatchId").addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val match_type = p0.child("matchType").value.toString()
                                val match_date = p0.child("matchDate").value.toString()
                                val match_time = p0.child("matchTime").value.toString()
                                val match_venue = p0.child("matchVenue").value.toString()
                                val match_city = p0.child("matchCity").value.toString()
                                val team_A_Name = p0.child("team_A_Name").value.toString()
                                val team_B_Name = p0.child("team_B_Name").value.toString()
                                val team_A_Logo = p0.child("team_A_Logo").value.toString()
                                val team_B_Logo = p0.child("team_B_Logo").value.toString()

                                upcomingMatchAdapter.add(UpcomingMatchViewHolder(match_type,match_date,match_time,match_venue,match_city,team_A_Name,team_B_Name,team_A_Logo,team_B_Logo))
                            }
                        })

                    }
                    upcoming_match_recycler_view.adapter=upcomingMatchAdapter
                }
            }
        })

    }

    class UpcomingMatchViewHolder(

        var match_type: String,
        var match_date: String,
        var match_time: String,
        var match_venue: String,
        var match_city: String,
        var team_A_Name: String,
        var team_B_Name: String,
        var team_A_Logo: String,
        var team_B_Logo: String
    ) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.fragment_team_upcoming_match_card
        }


        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.upcoming_match_cardView

            viewHolder.itemView.title_of_match_upcoming.text = match_type
            viewHolder.itemView.date_of_match_upcoming.text = match_date
            viewHolder.itemView.starting_time_of_match_upcoming.text = match_time
            viewHolder.itemView.venue_of_match_on_match_card_upcoming.text = match_venue
            viewHolder.itemView.city_of_match_on_match_card_upcoming.text = match_city
            viewHolder.itemView.team_A_name_match_card_upcoming.text = team_A_Name
            viewHolder.itemView.team_B_name_match_card_upcoming.text = team_B_Name


            val logo_team_A=viewHolder.itemView.findViewById<ImageView>(R.id.team_A_logo_match_card_upcoming)
            Picasso.get().load(team_A_Logo).into(logo_team_A)

            val logo_team_B=viewHolder.itemView.findViewById<ImageView>(R.id.team_B_logo_match_card_upcoming)
            Picasso.get().load(team_B_Logo).into(logo_team_B)

        }


    }
}