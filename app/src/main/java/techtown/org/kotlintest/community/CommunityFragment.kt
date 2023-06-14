package techtown.org.kotlintest.community

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import techtown.org.kotlintest.*
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.FragmentCommunityBinding
import techtown.org.kotlintest.myTravel.TravelDao
import techtown.org.kotlintest.myTravel.TravelData

class CommunityFragment : Fragment() {
    var dao = PostDao()
    var postDB = Firebase.database.reference.child("post")
    lateinit var myAdapter: MyPostAdapter
    var datas = arrayListOf<PostData>()

    private lateinit var mDbRef: DatabaseReference

    var Uid: String? = null
    lateinit var nickname: String
    lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCommunityBinding.inflate(inflater, container, false)
        Uid = arguments?.getString("uid")
        val db = FirebaseDatabase.getInstance()
        mDbRef = db.getReference("user")

        val layoutManager = LinearLayoutManager(activity)
        binding.postsRecycle.layoutManager = layoutManager
        myAdapter = MyPostAdapter(requireContext())
        binding.postsRecycle.adapter = myAdapter

        /*nickname = userDB.child(Uid!!).child("nickname").toString()
        id = userDB.child(Uid!!).child("id").toString()*/

        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nickname = snapshot.child(Uid!!).child("nickname").value.toString()
                id = snapshot.child(Uid!!).child("id").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        /*binding.postsRecycle.addItemDecoration(MyDecoration(activity as Context))*/

        binding.addNewPost.setOnClickListener(({
            val intent = Intent(context, AddNewPost::class.java)
            intent.putExtra("uid", Uid)
            intent.putExtra("name", nickname)
            intent.putExtra("id", id)
            startActivity(intent)
        }))

        getPostList()

        binding.btnNewHotPost.isSelected = true

        binding.btnNewHotPost.setOnClickListener {
            binding.btnNewHotPost.isSelected = !binding.btnNewHotPost.isSelected
            getPostList()
        }

        binding.btnLatestPost.setOnClickListener {
            binding.btnLatestPost.isSelected = !binding.btnLatestPost.isSelected
            getPostList2()
        }

        binding.searchPosts.setOnClickListener{
            val intent = Intent(context, SearchPost::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getPostList() {
        dao.getPostList()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()

                datas.apply {
                    /*add(
                        PostData(
                            Uid!!, "", "", "mochi", "subin", "If you want to try monja-yaki,\n" +
                                    "\ti really recommend here!\n" +
                                    "The servers are really kind\n" +
                                    "\tand you can talk with them in English.\n" +
                                    "Foods are awsome\n" +
                                    "\tbut highball is not that good.\n" +
                                    "beers are better.", "subin.png", "2023/06/10 12:00", 12, 2, 0, arrayListOf()
                        ))
                    add(
                        PostData(
                            Uid!!, "", "", "duckky", "qqq", "Is there any great place to have dinner\n" +
                                    "at late night?\n" +
                                    "We are near of Sensoji Temple now\n" +
                                    "but can go anywhere if we go by walk.\n" +
                                    "It would be better\tif we can have beer or so.", "qqq.png", "2023/05/15 14:00", 20, 4, 6, arrayListOf()
                        ))*/
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
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getPostList2() {
        dao.getPostList()?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()

                datas.apply {
                    datas.sortByDescending { it.postTime }
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
                        datas.sortByDescending { it.postTime }
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
}