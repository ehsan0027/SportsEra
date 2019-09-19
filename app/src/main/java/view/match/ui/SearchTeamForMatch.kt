package view.match.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsplayer.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.*
import com.pawegio.kandroid.inflateLayout
import com.pawegio.kandroid.onQueryChange
import com.pawegio.kandroid.toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_search_team_for_match.*
import model.Team
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class SearchTeamForMatch :AppCompatActivity() {
    private var firebaseDatabase: FirebaseDatabase? = null
    lateinit var ref: DatabaseReference
    lateinit var team_Id: String
    lateinit var team_name: String
    lateinit var team_logo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search_team_for_match)

        firebaseDatabase = FirebaseDatabase.getInstance()
        ref = FirebaseDatabase.getInstance().reference.child("Team")


    }

    override fun onStart() {
        super.onStart()

        searchView_SearchTeamForMatch?.onQueryChange { query ->
            run {
                if (query.isNotEmpty()) {
                    searchTeam(query)
                } else {
                    recyclerView_SearchTeamForMatch.removeAllViewsInLayout()
                }
            }
        }

    }



    private fun searchTeam(inputText:String)
    {
        val allTeamDatabaseRef=firebaseDatabase?.getReference("Team")
        val query: Query?=allTeamDatabaseRef?.orderByChild("teamName")?.
            startAt(inputText)?.endAt(inputText +"\uf8ff")

        //query is a reference to the specific Node
        val option = FirebaseRecyclerOptions.Builder<Team>()
            .setQuery(query!!,Team::class.java)
            .setLifecycleOwner(this)
            .build()
        var card:MaterialCardView

        val firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Team, SearchTeamViewHolder>(option)
        {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTeamViewHolder {

                /**team search list view may changed **/
                val itemView=inflateLayout(R.layout.search_team_list_for_match ,parent)
                return SearchTeamViewHolder(itemView)
            }

            override fun onBindViewHolder(teamViewHolder: SearchTeamViewHolder, position: Int, model:Team) {

                    card=teamViewHolder.itemView.find(R.id.cardView_SearchTeamListForMatch)
                    card.setOnClickListener { view ->
                        run {
                            val v = view as MaterialCardView
                            v.isChecked = !v.isChecked
                            if (v.isChecked) {
                                toast("select Team")
                                team_Id = model.teamId
                                team_name = model.teamName
                                team_logo = model.teamLogo!!

                               alert("Do you want to select $team_name","Confirmation"){

                             yesButton{dialog ->

                                 val intent=Intent()
                                 intent.putExtra("teamName",team_name)
                                 intent.putExtra("teamLogo",team_logo)
                                 intent.putExtra("teamId",team_Id)
                                 setResult(Activity.RESULT_OK,intent)
                                 finish()
                             }
                             noButton { dialog ->
                                 dialog.dismiss()
                             }

                               }.show()


                            }

                }

                }

                val newTeam = getRef(position).key.toString()
                ref.child(newTeam).addValueEventListener(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        toast("Error ${p0.toException()}")
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        //mpb_progressbar.visibility = if(itemCount == 0) View.VISIBLE else View.GONE

                        Picasso.get().load(model.teamLogo).into(teamViewHolder.tLogo)
                        teamViewHolder.tName.text = model.teamName
                        teamViewHolder.tCity.text=model.city

                    }
                })
            }

        }
        recyclerView_SearchTeamForMatch.adapter= firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()

    }


    class SearchTeamViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!)
    {

        internal  var tLogo=itemView!!.findViewById<ImageView>(R.id.teamLogo__SearchTeamListForMatch)
        internal var tName=itemView!!.findViewById<TextView>(R.id.teamName_SearchTeamListForMatch)
        internal var tAdmin=itemView!!.findViewById<TextView>(R.id.teamAdmin_SearchTeamListForMatch)
        internal var tCity=itemView!!.findViewById<TextView>(R.id.teamCity_SearchTeamListForMatch)


    }




}
