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
import techtown.org.kotlintest.databinding.ActivitySearchPostBinding
import techtown.org.kotlintest.databinding.ActivityUpdateBinding
import techtown.org.kotlintest.mySchedule.MapsActivity
import techtown.org.kotlintest.mySchedule.ScheduleDao
import techtown.org.kotlintest.mySchedule.ScheduleData
import techtown.org.kotlintest.myTravel.TravelData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchPost : AppCompatActivity() {
    val TAG = "SearchPost"
    var datas = arrayListOf<PostData>()
    var dao = PostDao()
    lateinit var myAdapter: MySearchPostAdapter
    var postDB = Firebase.database.reference.child("post")
    lateinit var search_posts: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.postsRecycle.layoutManager = layoutManager
        myAdapter = MySearchPostAdapter(this)
        binding.postsRecycle.adapter = myAdapter

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myAdapter.notifyDataSetChanged()

        getPostList()

        search_posts = binding.searchPosts
        search_posts.setOnQueryTextListener(searchViewTextListener)
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

                    if (postList != null) {
                        postDB.child(postList.key).child("key").setValue(key.toString())
                    }

                    if (postList != null) {
                        datas.add(postList)
                        datas.sortByDescending { it.cntHeart }
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

    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                myAdapter.getFilter().filter(s)
                Log.d(TAG, "SearchVies Text is changed : $s")
                return false
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