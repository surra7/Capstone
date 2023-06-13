package techtown.org.kotlintest.account

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import techtown.org.kotlintest.MyPostAdapter
import techtown.org.kotlintest.R
import techtown.org.kotlintest.community.PostDao
import techtown.org.kotlintest.community.PostData
import techtown.org.kotlintest.databinding.ActivityInformationBinding
import techtown.org.kotlintest.databinding.ActivityMyPostsBinding

class myPostsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyPostsBinding
    lateinit var myAdapter: MyPostAdapter
    var datas = arrayListOf<PostData>()

    val db = FirebaseDatabase.getInstance()
    var mDbRef = db.getReference("user")
    var dao = PostDao()

    var myPost : String = ""

    val user = Firebase.auth.currentUser
    val myUid = user!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.myPostsRecycle.layoutManager = layoutManager
        myAdapter = MyPostAdapter(this)
        binding.myPostsRecycle.adapter = myAdapter

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.topBar.title = "My Posts"
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getPostList()
    }

    private fun getPostList() {
        dao.getPostList()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()

                datas.apply {
                    datas.sortByDescending { it.cntHeart }
                    myAdapter.datas = datas
                    myAdapter.notifyDataSetChanged()
                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for (dataSnapshot in snapshot.children) {
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val postList = dataSnapshot.getValue(PostData::class.java)
                    //키 값 가져오기
                    val key = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    postList?.key = key.toString()

                    if (postList!!.Uid == myUid){
                        datas.add(postList)
                        datas.sortByDescending { it.cntHeart }
                        myAdapter.notifyDataSetChanged()
                    }
                }
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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