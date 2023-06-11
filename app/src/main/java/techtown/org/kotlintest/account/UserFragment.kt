package techtown.org.kotlintest.account

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.FragmentUserBinding

class UserFragment: Fragment() {
    private lateinit var binding : FragmentUserBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentUserBinding.inflate(inflater, container, false)
        lateinit var mAuth: FirebaseAuth
        val db = FirebaseDatabase.getInstance()
        storage = Firebase.storage
        mDbRef = db.getReference("user")
        val user = Firebase.auth.currentUser
        val uId = user!!.uid

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.child(uId).child("nickname").value.toString()
                val id = snapshot.child(uId).child("id").value.toString()
                //val profilePic = snapshot.child(uId).child("profilePicUri").value
                val profilePic = storage.reference.child("profile").child("photo").child("${id}.png")
                val clipboard: ClipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", id)

                profilePic.downloadUrl.addOnSuccessListener(){
                    Glide.with(this@UserFragment)
                        .load(it as Uri)
                        .into(binding.imageProfile)
                }
                binding.nicknameText.text = nickname
                binding.idText.text = id
                binding.copyIdButton.setOnClickListener {
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(requireActivity(), "Copied", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.infoBtn.setOnClickListener(({
            val intent = Intent(context, InformationActivity::class.java)
            startActivity(intent)
        }))

        binding.friendsBtn.setOnClickListener(({
            val intent = Intent(context, FriendsActivity::class.java)
            startActivity(intent)
        }))

        binding.myPostBtn.setOnClickListener(({
            val intent = Intent(context, myPostsActivity::class.java)
            startActivity(intent)
        }))

        binding.savedBtn.setOnClickListener(({
            val intent = Intent(context, SavedActivity::class.java)
            startActivity(intent)
        }))

        binding.likedBtn.setOnClickListener(({
            val intent = Intent(context, LikedActivity::class.java)
            startActivity(intent)
        }))

        binding.logOutBtn.setOnClickListener(({
            mAuth = Firebase.auth
            mAuth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }))

        binding.deleteAccountBtn.setOnClickListener(({
            val user = Firebase.auth.currentUser!!

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User account deleted.")
                    }
                }
            val intent = Intent(context, SignupActivity::class.java)
            startActivity(intent)
        }))

        return binding.root
    }
}