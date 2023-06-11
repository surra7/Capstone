package techtown.org.kotlintest.community

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddCountryBinding
import techtown.org.kotlintest.databinding.ActivityAddNewPostBinding
import techtown.org.kotlintest.mySchedule.ScheduleDao
import techtown.org.kotlintest.mySchedule.ScheduleData
import java.text.SimpleDateFormat

class AddNewPost : AppCompatActivity() {
    lateinit var binding: ActivityAddNewPostBinding

    lateinit var Uid: String
    lateinit var name: String
    lateinit var id: String

    var storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        binding.addNewPost.setOnClickListener{
            val context = binding.postContext.text.toString()
            /*val time = binding.timeEdit.text.toString()*/
            /*val day = binding.dayEdit.text.toString()*/
            /*val memo = binding.memoEdit.text.toString()*/

            val post = PostData(Uid, "", name, id, context, "", postTime, 0, 0, 0)

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