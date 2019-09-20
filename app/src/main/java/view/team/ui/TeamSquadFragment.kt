package view.team.ui

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pawegio.kandroid.toast
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_squad_card_remove.view.*
import kotlinx.android.synthetic.main.fragment_team_squad_card_add.view.*
import kotlinx.android.synthetic.main.fragment_team_squad_recycler_view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TeamSquadFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class TeamSquadFragment(val teamId:String) : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    val benchAdapter= GroupAdapter<ViewHolder>() //groupi Adapter
    val squadAdapter= GroupAdapter<ViewHolder>() //groupi Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_squad_recycler_view, container, false)
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
        squadAdapter.clear()
        benchAdapter.clear()
        fetchTeamBench(teamId)
        fetchTeamSquad(teamId)


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

    private fun fetchTeamBench(teamId:String)
    {
        benchAdapter.clear()
        val teamRef= FirebaseDatabase.getInstance()
        val teamsPlayerRef= FirebaseDatabase.getInstance().getReference("/Team/$teamId/TeamBench")
        teamsPlayerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    p0.children.forEach{
                        val playerId=it.key
                        Log.d("TeamMember_ID",playerId)
                        teamRef.getReference("/PlayerBasicProfile/$playerId").also { task ->
                            task.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    //get the actual player

                                    //get the actual player
                                    val playing_role=p0.child("playing_role").value.toString()
                                    val playerName=p0.child("name").value.toString()
                                    val player_id=p0.child("playerId").value.toString()
                                    val playerImage=p0.child("profile_img").value.toString()

                                    //cardView color

                                    benchAdapter.add(TeamsPlayerBench(playerImage,playerName,playing_role,player_id,this@TeamSquadFragment))
                                }

                            })
                        }
                    }

                    bench_squad_recycler_view.adapter=benchAdapter

                }
            }
        })


    }



    class TeamsPlayerBench(var playerImage:String,
                      var playerName:String,
                      var playerRole:String,
                      val playerId: String,
                           val ctx:TeamSquadFragment): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.fragment_squad_card_remove
        }

        private fun addToSquad(position: Int){
            val item=ctx.benchAdapter.getItem(position) as TeamsPlayerBench

            val player_Id=item.playerId

            val newDatabaseRef=FirebaseDatabase.getInstance().reference
            val teamBench="TeamBench"
            val teamSquad="TeamSquad"
            val switchPlayer=HashMap<String,Any?>()
            switchPlayer["/Team/${ctx.teamId}/$teamBench/$player_Id"]=null
            switchPlayer["/Team/${ctx.teamId}/$teamSquad/$player_Id"]=true
            newDatabaseRef.updateChildren(switchPlayer).addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    ctx.toast("Player is Added to Squad")
                }
            }


            Log.d("Button",player_Id)

        }


        override fun bind(viewHolder: ViewHolder, position: Int) {

            ctx.bench_count_squad.text=ctx.benchAdapter.itemCount.toString()
            val image=viewHolder.itemView.findViewById<ImageView>(R.id.playerImage_squad_card_remove)
            Picasso.get().load(playerImage).into(image)
            viewHolder.itemView.playerName_squad_card_remove.text=playerName
            viewHolder.itemView.playerRole_squad_card_remove.text=playerRole
            viewHolder.itemView.add_player_to_squad.setOnClickListener {
                addToSquad(position)
            }


        }


    }

    //FetchTeam Squad
    private fun fetchTeamSquad(teamId:String)
    {
        squadAdapter.clear()
        val teamRef= FirebaseDatabase.getInstance()
        val teamsPlayerRef= FirebaseDatabase.getInstance().getReference("/Team/$teamId/TeamSquad")
        teamsPlayerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    p0.children.forEach{
                        val playerId=it.key
                        Log.d("TeamMember_ID",playerId)
                        teamRef.getReference("/PlayerBasicProfile/$playerId").also { task ->
                            task.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    //get the actual player

                                    //get the actual player
                                    val playing_role=p0.child("playing_role").value.toString()
                                    val playerName=p0.child("name").value.toString()
                                    val player_id=p0.child("playerId").value.toString()
                                    val playerImage=p0.child("profile_img").value.toString()

                                    //cardView color

                                    squadAdapter.add(TeamsPlayerSquad(playerImage,playerName,playing_role,player_id,this@TeamSquadFragment))
                                }

                            })
                        }
                    }

                    playing_squad_recycler_view.adapter=squadAdapter

                }
            }
        })


    }

fun getItemCount()
{
    squad_count_squad.text= squadAdapter.itemCount.toString()
    bench_count_squad.text= benchAdapter.itemCount.toString()

Log.d("Total Count","${squadAdapter.itemCount}")
Log.d("Total Count","${benchAdapter.itemCount}")
}

    class TeamsPlayerSquad(var playerImage:String,
                           var playerName:String,
                           var playerRole:String,
                           val playerId: String,
                           val ctx:TeamSquadFragment): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.fragment_team_squad_card_add
        }

        private fun removeFromSquad(position: Int){
            val item=ctx.squadAdapter.getItem(position) as TeamsPlayerSquad

            val player_Id=item.playerId

            val newDatabaseRef=FirebaseDatabase.getInstance().reference
            val teamBench="TeamBench"
            val teamSquad="TeamSquad"
            val switchPlayer=HashMap<String,Any?>()
            switchPlayer["/Team/${ctx.teamId}/$teamBench/$player_Id"]=true
            switchPlayer["/Team/${ctx.teamId}/$teamSquad/$player_Id"]=null
            newDatabaseRef.updateChildren(switchPlayer).addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    ctx.toast("Player Removed From Squad")
                }
            }

            Log.d("Button",player_Id)

        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            ctx.squad_count_squad.text=ctx.squadAdapter.itemCount.toString()

            val image=viewHolder.itemView.findViewById<ImageView>(R.id.playerImage_squad_card_add)
            Picasso.get().load(playerImage).into(image)
            viewHolder.itemView.playerName_squad_card_add.text=playerName
            viewHolder.itemView.playerRole_squad_card_add.text=playerRole
            viewHolder.itemView.remove_player_from_squad.setOnClickListener {
                removeFromSquad(position)
            }
        }


    }

}
