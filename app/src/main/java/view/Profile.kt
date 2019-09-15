@file:Suppress("DEPRECATION")

package view

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import com.example.sportsplayer.R
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.profile_activity.*



class Profile:AppCompatActivity()
{
    var mAuth:FirebaseAuth?=null
    var firebaseDatabase:FirebaseDatabase?=null
   // var mystorage: FirebaseStorage? =null
    lateinit var current_user_id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        mAuth= FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance() //get the firebase database instance
        //getProfileData()




    }

    fun setUserDetail(userInfo:Array<String>)
    {
        if(userInfo.isNotEmpty()) {
            val url=userInfo[0]
            Log.d("downloadUrl ",url)
            val name=userInfo[1]
            val dob=userInfo[2]
            val city=userInfo[3]
            val phn=userInfo[4]

            Picasso.get().load(url).into(playerImage_ProfileActivity)
            playerName_ProfileActivity.text=name
            playerRoll_ProfileActivity.text=city


        }
    }

//get player detail from firebase database
private fun getProfileData()
    {
        val userInfo:Array<String> = Array(5) {"BosS"}
        val progressDialog: ProgressDialog = ProgressDialog.show(this, "getting Info", "please wait...")
        progressDialog.show()
        val user = mAuth?.uid

        if(user!=null)
        {
            val userRef=firebaseDatabase?.reference?.child("/PlayerBasicProfile") //by using firebase database instance we get reference to the specific Node
            userRef?.child(user)?.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        val image=p0.child("profile_img").value.toString()
                        val name=p0.child("name").value.toString()
                        val dob=p0.child("dateOfBirth").value.toString()
                        val city=p0.child("city").value.toString()
                        val phn=p0.child("phoneNumber").value.toString()
                        progressDialog.dismiss()
                        userInfo[0] = image
                        userInfo[1] = name
                        userInfo[2] = dob
                        userInfo[3] = city
                        userInfo[4] = phn
                        setUserDetail(userInfo)

                        progressDialog.dismiss()


                    }
                }

            }
            )




        }


    }
}