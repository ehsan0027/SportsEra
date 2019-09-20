package view.team.ui.TeamMatchTabs

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sportsplayer.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

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
class TeamUpcomingMatchFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    val upcomingMatchAdapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_upcoming_match_recycler_view, container, false)
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

    private fun fetchUpcomingMatchDetails(teamId:String){
        val teamUpcomingMatch = "TeamUpcomingMatch"
        val newDatabaseRef = FirebaseDatabase.getInstance().getReference("Team/$teamId/$teamUpcomingMatch")
        newDatabaseRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
val scheduleMatchRef=FirebaseDatabase.getInstance().getReference("ScheduledMatch")
                    p0.children.forEach {

                        val upcomingMatchId=it.key
                        scheduleMatchRef.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val match_type = p0.child("matchType")
                                val match_date = p0.child("matchDate")
                                val match_time= p0.child("matchTime")
                                val match_venue = p0.child("matchVenue")
                                val match_city= p0.child("matchCity")
                            }
                        })

                    }
                }
            }
        })

    }

    class UpcomingMatchViewHolder


}
