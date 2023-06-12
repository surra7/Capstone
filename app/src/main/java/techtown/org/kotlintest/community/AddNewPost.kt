package techtown.org.kotlintest.community

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.GalleryAdapter
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddCountryBinding
import techtown.org.kotlintest.databinding.ActivityAddNewPostBinding
import techtown.org.kotlintest.mySchedule.ScheduleDao
import techtown.org.kotlintest.mySchedule.ScheduleData
import java.text.SimpleDateFormat

class AddNewPost : AppCompatActivity() {
    lateinit var binding: ActivityAddNewPostBinding
    lateinit var galleryAdapter: GalleryAdapter

    var imageList: ArrayList<Uri> = ArrayList()

    lateinit var Uid: String
    lateinit var name: String
    lateinit var id: String
    lateinit var postkey: String
    var postimg = arrayListOf<String>()

    var storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        galleryAdapter = GalleryAdapter(imageList, this)

        binding.imgRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imgRecycler.adapter = galleryAdapter

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.addImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResult.launch(intent)
        }

        val dao = PostDao()

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val postTime: String = formatter.format(java.util.Date())

        if(intent.hasExtra("uid") && intent.hasExtra("name")
            && intent.hasExtra("id")) {

            Uid = intent.getStringExtra("uid")!!
            name = intent.getStringExtra("name")!!
            id = intent.getStringExtra("id")!!

            //데이터 보여주기
            binding.userName.setText(name)
            binding.userId.setText(id)

            val profilePic = storage.reference.child("profile").child("photo").child("${id}.png")

            profilePic.downloadUrl.addOnSuccessListener(){
                Glide.with(this)
                    .load(it as Uri)
                    .into(binding.imgProfile)
            }
        }

        val formatte = SimpleDateFormat("yyyyMMHH_mmss")
        val posttime: String = formatte.format(java.util.Date())
        postkey = posttime + Uid

        binding.addNewPost.setOnClickListener{
            val context = binding.postContext.text.toString()

            for (i in 0 until imageList.size){
                uploadFile(imageList[i], i)
            }

            val post = PostData(Uid, "", postkey, name, id, context, "", postTime, 0, 0, 0, postimg)

            dao.add(post)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }

            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                //멀티 선택은 clipData
                if (it.data!!.clipData != null){
                    val count = it.data!!.clipData!!.itemCount

                    for (index in 0 until count){
                        val imageUri = it.data!!.clipData!!.getItemAt(index).uri
                        imageList.add(imageUri)
                    }
                } else{ //싱글 이미지
                    val imageUri = it.data!!.data
                    imageList.add(imageUri!!)
                }
                galleryAdapter.notifyDataSetChanged()
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadFile(uri: Uri, i: Int) {
        //업로드할 파일이 있으면 수행
        if (uri != null) {
            //업로드 진행 Dialog 보이기
            /*val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("uploading...")
            progressDialog.show()*/

            //storage
            val storage = FirebaseStorage.getInstance()

            //Unique한 파일명을 만들자.
            val formatter = SimpleDateFormat("yyyyMMHH_mmss")
            /*val now = LocalDateTime.now()*/
            val filename: String = formatter.format(java.util.Date())
            //storage 주소와 폴더 파일명을 지정해 준다.
            val storageRef = storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com/").child("posts").child(postkey).child(filename + i.toString())
            postimg.add(filename + i.toString())
            //올라가거라...
            storageRef.putFile(uri!!) //성공시
                .addOnSuccessListener {
                    /*progressDialog.dismiss() //업로드 진행 Dialog 상자 닫기*/
                    Toast.makeText(applicationContext, "업로드 완료!", Toast.LENGTH_SHORT).show()
                } //실패시
                .addOnFailureListener {
                    /*progressDialog.dismiss()*/
                    Toast.makeText(applicationContext, "업로드 실패!", Toast.LENGTH_SHORT).show()
                } //진행중
                /*.addOnProgressListener { taskSnapshot ->
                    val progress//이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                            =
                        (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toDouble()
                    //dialog에 진행률을 퍼센트로 출력해 준다
                   *//* progressDialog.setMessage("Uploaded " + progress.toInt() + "% ...")*//*
                }*/
        } else {
            Toast.makeText(applicationContext, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }
}