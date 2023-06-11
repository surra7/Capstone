package techtown.org.kotlintest.community

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.databinding.ActivityAddNewPostBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

class AddNewPost : AppCompatActivity() {
    lateinit var binding: ActivityAddNewPostBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    companion object{
        lateinit var getPicView: Any
        lateinit var uri: Any
        lateinit var postName: String
        lateinit var id: String
        lateinit var postingTime: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseDatabase.getInstance()
        storage = Firebase.storage
        mDbRef = db.getReference("post") // DB 대분류
        val user = Firebase.auth.currentUser
        val uId = user!!.uid

        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        val dataFormat = SimpleDateFormat("_yyMMddE_HHmmss") // 년(20XX) 월 일 요일 시(0~23) 분 초
        postingTime = dataFormat.format(currentTime)

        mDbRef.addValueEventListener(object: ValueEventListener {
            // show current user
            override fun onDataChange(snapshot: DataSnapshot) {
                val nickname = snapshot.child(uId).child("nickname").value.toString()
                id = snapshot.child(uId).child("id").value.toString()
                val profilePic =
                    storage.reference.child("profile").child("photo").child("${id}.png")
                profilePic.downloadUrl.addOnSuccessListener() {
                    Glide.with(this@AddNewPost)
                        .load(it as Uri)
                        .into(binding.postProfilePic)
                }
                binding.currentUserName.text = nickname
                binding.currentUserId.text = id

                postName = "${id}+${postingTime}"
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.getPic1.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            var postPic1Name = "${postName}_pic1"
            var pic = getImageUri(this, uri as Bitmap)
            storage.reference.child("post/${postName}/photo").child(postPic1Name).putFile(pic!!)
            getPicView = binding.getPic1
            activityResult.launch(intent)
        }
        binding.getPic2.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            var postPic2Name = "${postName}_pic2"
            var pic = getImageUri(this, uri as Bitmap)
            storage.reference.child("post/photo/${postName}").child(postPic2Name).putFile(pic!!)
            getPicView = binding.getPic2
            activityResult.launch(intent)
        }
        binding.getPic3.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            var postPic3Name = "${postName}_pic3"
            var pic = getImageUri(this, uri as Bitmap)
            storage.reference.child("post/photo/${postName}").child(postPic3Name).putFile(pic!!)
            getPicView = binding.getPic3
            activityResult.launch(intent)
        }

        binding.addNewPostBtn.setOnClickListener {
            var postText = binding.postTextEdit.text.toString().trim()
            mDbRef.child(postName).child("text").setValue(postText) // post 하위에 postName(id+업로드(날짜,시간)) 하위에 text 하위에 postText 저장
            mDbRef.child(postName).child("location").setValue("temp location String") // 위에서 text 말고 location 하위에 임시로 String 업로드(사유: 지도 위치를 모름..ㅠ)
        }

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK && it.data != null) {
            uri = it.data!!.data!!
            Glide.with(this)
                .load(uri)
                .into(getPicView as ImageView)
        }
    }

    // Bitmap to Uri
    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                val intent = intent
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//                return true
//            }
//            else -> {}
//        }
//        return super.onOptionsItemSelected(item)
//    }
}