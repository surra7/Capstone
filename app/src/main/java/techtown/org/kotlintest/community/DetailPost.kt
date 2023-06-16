package techtown.org.kotlintest.community

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.*
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityDetailPostBinding
import techtown.org.kotlintest.databinding.ActivityUpdateBinding
import techtown.org.kotlintest.mySchedule.MapsActivity
import techtown.org.kotlintest.mySchedule.ScheduleDao
import techtown.org.kotlintest.mySchedule.ScheduleData
import techtown.org.kotlintest.myTravel.TravelData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DetailPost : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPostBinding
    lateinit var galleryAdapter: GalleryAdapter
    var imageList: ArrayList<Uri> = ArrayList()
    lateinit var myAdapter: CommentAdapter
    var datas = mutableListOf<CommentData>()
    val dao2 = CommentDao()

    var storage = Firebase.storage
    private lateinit var mDbRef: DatabaseReference
    var postDB = Firebase.database.reference.child("post")
    var userDB = Firebase.database.reference.child("user")
    var commentDB = Firebase.database.reference.child("comment")

    lateinit var Uid: String
    lateinit var key: String
    lateinit var postKey: String
    lateinit var name: String
    lateinit var id: String
    lateinit var context: String
    lateinit var location: String
    lateinit var time: String
    var heart: Int = 0
    var comment: Int = 0
    var bookmark: Int = 0
    var postimg = arrayListOf<String>()

    var myHeart: String = ""
    var myBookmark: String = ""
    var myId: String = ""

    /*lateinit var sLocation: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0*/

    val user = Firebase.auth.currentUser
    val myUid = user!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = FirebaseDatabase.getInstance()
        mDbRef = db.getReference("user")

        galleryAdapter = GalleryAdapter(imageList, this)

        binding.imgRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imgRecycler.adapter = galleryAdapter

        val layoutManager = LinearLayoutManager(this)
        binding.commentRecycle.layoutManager = layoutManager
        myAdapter = CommentAdapter(this)
        binding.commentRecycle.adapter = myAdapter

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
            && intent.hasExtra("time")
        ) {

            //데이터 담기
            //Uid = 글쓴 사람의 uid
            Uid = intent.getStringExtra("uid")!!
            key = intent.getStringExtra("key")!!
            postKey = intent.getStringExtra("postKey")!!
            name = intent.getStringExtra("name")!!
            id = intent.getStringExtra("id")!!
            context = intent.getStringExtra("context")!!
            location = intent.getStringExtra("location")!!
            time = intent.getStringExtra("time")!!
            heart = intent.getIntExtra("heart", 0)!!
            comment = intent.getIntExtra("comment", 0)!!
            bookmark = intent.getIntExtra("bookmark", 0)!!
            postimg = intent.getStringArrayListExtra("postimg")!!

            //데이터 보여주기
            binding.userName.setText(name)
            binding.userId.setText(id)
            binding.postContext.setText(context)
            binding.addLocation.setText(location)
            binding.postTime.setText(time)
            binding.cntHeart.setText(heart.toString())
            binding.cntComment.setText(comment.toString())
            binding.cntBookmark.setText(bookmark.toString())

            val profilePic = storage.reference.child("profile").child("photo").child("${id}.png")

            profilePic.downloadUrl.addOnSuccessListener() {
                Glide.with(this)
                    .load(it as Uri)
                    .into(binding.imgProfile)
            }
        }

        if (postimg.isEmpty() == false) {
            for (img in postimg) {
                val postPic = storage.reference.child("posts").child(postKey).child(img)
                postPic.downloadUrl.addOnSuccessListener() {
                    val imguri = it as Uri
                    imageList.add(imguri)
                    galleryAdapter.notifyDataSetChanged()
                }
            }
        }

        if (location == ""){
            binding.icLocation.isVisible = false
        }

        mDbRef.child(myUid).child("id").get().addOnSuccessListener {
            myId = it.getValue().toString()
        }

        mDbRef.child(myUid).child("heartList").child(key).get().addOnSuccessListener {
            myHeart = it.getValue().toString()
            if (myHeart == key) {
                binding.btnHeart.isSelected = true
            } else {
                binding.btnHeart.isSelected = false
            }
        }

        mDbRef.child(myUid).child("bookmarkList").child(key).get().addOnSuccessListener {
            myBookmark = it.getValue().toString()
            if (myBookmark == key) {
                binding.btnBookmark.isSelected = true
            } else {
                binding.btnBookmark.isSelected = false
            }
        }

        binding.btnHeart.setOnClickListener {
            binding.btnHeart.isSelected = !binding.btnHeart.isSelected

            if (binding.btnHeart.isSelected) {
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

            if (binding.btnBookmark.isSelected) {
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

        /*sLocation = ""*/

        /*binding.addLocation.setOnClickListener {
            *//*val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)*//*
            openActivityForResult()
        }*/

        if (Uid != myUid) {
            binding.delete.isVisible = false
        } else{
            binding.delete.isVisible = true
        }

        binding.delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete")
                .setMessage("Are You Sure Want To Delete This Posts?")
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        dao.postDelete(key).addOnSuccessListener {
                            Toast.makeText(applicationContext, "Delete Success", Toast.LENGTH_SHORT).show()
                            dao2.commentAllDelete(key)
                            mDbRef.child(myUid).child("heartList").child(key).get().addOnSuccessListener {
                                myHeart = it.getValue().toString()
                                if (myHeart == key) {
                                    userDB.child(myUid).child("heartList").child(key).removeValue()
                                }
                            }
                            mDbRef.child(myUid).child("bookmarkList").child(key).get().addOnSuccessListener {
                                myBookmark = it.getValue().toString()
                                if (myBookmark == key) {
                                    userDB.child(myUid).child("bookmarkList").child(key).removeValue()
                                }
                            }

                            val intent = intent
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, "Delete Fail: ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                .setNegativeButton("Cancle", null)
            // 다이얼로그를 띄워주기
            builder.show()
        }

        binding.addComment.setOnClickListener {
            val uComment = binding.editComment.text.toString()

            if (uComment.isEmpty() == false) {
                val comments = CommentData(myUid, "", key, myId, uComment)

                dao2.add(key, comments)?.addOnSuccessListener {
                    Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
                }
                comment += 1
                postDB.child(key).child("cntComment").setValue(comment)
                binding.cntComment.setText(comment.toString())

                binding.editComment.text = null
            }
        }

        myAdapter.setItemClickListener(object: CommentAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, commentKey: String) {
                // 클릭 시 이벤트 작성
                dao2.commentDelete(key, commentKey)
                comment -= 1
                postDB.child(key).child("cntComment").setValue(comment)
                binding.cntComment.setText(comment.toString())
            }
        })

        getCommentList()
    }

    private fun getCommentList() {
        dao2.getCommentList(key)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()

                datas.apply {
                    /*add(CommentData("","", key, name,"That's good!"))
                    add(CommentData("","", key, name,"So cute!!!!"))*/

                    myAdapter.datas = datas
                    myAdapter.notifyDataSetChanged()
                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for (dataSnapshot in snapshot.children) {
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val commentList = dataSnapshot.getValue(CommentData::class.java)
                    //키 값 가져오기
                    val ckey = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    commentList?.key = ckey.toString()

                    if (commentList != null) {
                        commentDB.child(key).child(commentList.key).child("key").setValue(ckey.toString())
                        datas.add(commentList)
                    }
                }
                //데이터 적용
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    /*fun openActivityForResult() {
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

    }*/

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