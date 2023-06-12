package techtown.org.kotlintest.community

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.GalleryAdapter
import techtown.org.kotlintest.GalleryAdapter2
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityDetailPostBinding
import techtown.org.kotlintest.databinding.ActivityUpdateBinding
import techtown.org.kotlintest.mySchedule.MapsActivity
import techtown.org.kotlintest.mySchedule.ScheduleDao
import techtown.org.kotlintest.myTravel.TravelData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetailPost : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPostBinding
    lateinit var galleryAdapter: GalleryAdapter2
    var imageList: ArrayList<Uri> = ArrayList()

    var storage = Firebase.storage
    private lateinit var mDbRef: DatabaseReference
    var postDB = Firebase.database.reference.child("post")
    var userDB = Firebase.database.reference.child("user")

    lateinit var Uid: String
    lateinit var key: String
    lateinit var name: String
    lateinit var id: String
    lateinit var context: String
    lateinit var time: String
    var heart: Int = 0
    var comment: Int = 0
    var bookmark: Int = 0
    var postimg = arrayListOf<String>()

    var myHeart: String = ""
    var myBookmark: String = ""

    lateinit var sLocation: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = FirebaseDatabase.getInstance()
        mDbRef = db.getReference("user")
        val user = Firebase.auth.currentUser
        val myUid = user!!.uid

        galleryAdapter = GalleryAdapter2(imageList, this)

        binding.imgRecycler1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imgRecycler1.adapter = galleryAdapter

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //데이터베이스 객체
        val dao = PostDao()

        //데이터 null체크
        if (intent.hasExtra("uid") && intent.hasExtra("name")
            && intent.hasExtra("id") && intent.hasExtra("context")
            && intent.hasExtra("time")) {

            //데이터 담기
            //Uid = 글쓴 사람의 uid
            Uid = intent.getStringExtra("uid")!!
            key = intent.getStringExtra("key")!!
            name = intent.getStringExtra("name")!!
            id = intent.getStringExtra("id")!!
            context = intent.getStringExtra("context")!!
            time = intent.getStringExtra("time")!!
            heart = intent.getIntExtra("heart", 0)!!
            comment = intent.getIntExtra("comment", 0)!!
            bookmark = intent.getIntExtra("bookmark", 0)!!
            postimg = intent.getStringArrayListExtra("postimg")!!

            //데이터 보여주기
            binding.userName.setText(name)
            binding.userId.setText(id)
            binding.postContext.setText(context)
            binding.postTime.setText(time)
            binding.cntHeart.setText(heart.toString())
            binding.cntComment.setText(comment.toString())
            binding.cntBookmark.setText(bookmark.toString())

            val profilePic = storage.reference.child("profile").child("photo").child("${id}.png")

            profilePic.downloadUrl.addOnSuccessListener(){
                Glide.with(this)
                    .load(it as Uri)
                    .into(binding.imgProfile)
            }
        }

        for (img in postimg){
            Toast.makeText(this, "${key}", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "${img}", Toast.LENGTH_SHORT).show()
            val postPic = storage.reference.child("posts").child(key).child(img)
            postPic.downloadUrl.addOnSuccessListener(){
                val imguri = it as Uri
                imageList.add(imguri)
                galleryAdapter.notifyDataSetChanged()
            }
        }

        mDbRef.child(Uid!!).child("heartList").child(key).get().addOnSuccessListener {
            myHeart = it.getValue().toString()
            if (myHeart == key){
                binding.btnHeart.isSelected = true
            } else {
                binding.btnHeart.isSelected = false
            }
            Toast.makeText(this, "${myHeart}", Toast.LENGTH_SHORT).show()
        }

        mDbRef.child(Uid!!).child("bookmarkList").child(key).get().addOnSuccessListener {
            myBookmark = it.getValue().toString()
            if (myBookmark == key){
                binding.btnBookmark.isSelected = true
            } else {
                binding.btnBookmark.isSelected = false
            }
            Toast.makeText(this, "${myBookmark}", Toast.LENGTH_SHORT).show()
        }

        binding.btnHeart.setOnClickListener {
            binding.btnHeart.isSelected = !binding.btnHeart.isSelected

            if (binding.btnHeart.isSelected){
                heart += 1
                postDB.child(key).child("cntHeart").setValue(heart)
                binding.cntHeart.setText(heart.toString())
                userDB.child(myUid).child("heartList").child(key).setValue(key)
            } else {
                userDB.child(myUid).child("heartList").child(key).removeValue()
                heart -= 1
                postDB.child(key).child("cntHeart").setValue(heart)
                binding.cntHeart.setText(heart.toString())
            }
        }

        binding.btnBookmark.setOnClickListener {
            binding.btnBookmark.isSelected = !binding.btnBookmark.isSelected

            if (binding.btnBookmark.isSelected){
                bookmark += 1
                postDB.child(key).child("cntBookmark").setValue(bookmark)
                binding.cntBookmark.setText(bookmark.toString())
                userDB.child(myUid).child("bookmarkList").child(key).setValue(key)
            } else {
                userDB.child(myUid).child("bookmarkList").child(key).removeValue()
                bookmark -= 1
                postDB.child(key).child("cntBookmark").setValue(bookmark)
                binding.cntBookmark.setText(bookmark.toString())
            }
        }

        sLocation = ""

        binding.addLocation.setOnClickListener {
            /*val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)*/
            openActivityForResult()
        }

    }

    fun openActivityForResult() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("location", sLocation)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivityForResult(intent, 123)
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            sLocation = data?.getStringExtra("location")!!
            latitude = data.getDoubleExtra("latitude", 0.0)!!
            longitude = data.getDoubleExtra("longitude", 0.0)!!
            Toast.makeText(this, "${sLocation}", Toast.LENGTH_SHORT).show()
            binding.addLocation.text= sLocation
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }
}