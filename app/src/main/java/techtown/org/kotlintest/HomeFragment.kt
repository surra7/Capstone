package techtown.org.kotlintest

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import techtown.org.kotlintest.account.InformationActivity
import techtown.org.kotlintest.community.PostDao
import techtown.org.kotlintest.community.PostData
import techtown.org.kotlintest.databinding.FragmentCommunityBinding
import techtown.org.kotlintest.databinding.FragmentHomeBinding
import techtown.org.kotlintest.myTravel.AddActivity2

class HomeFragment : Fragment() {
    var dao = PostDao()
    lateinit var myAdapter: MyPostAdapter
    var datas = arrayListOf<PostData>()

    var Uid : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        Uid = arguments?.getString("uid")

        Toast.makeText(context, "${Uid}", Toast.LENGTH_SHORT).show()

        val layoutManager = LinearLayoutManager(activity)
        binding.mainPostsRecycle.layoutManager = layoutManager
        myAdapter = MyPostAdapter(requireContext())
        binding.mainPostsRecycle.adapter = myAdapter

        binding.addNewTravel.setOnClickListener {
            val intent = Intent(context, AddActivity2::class.java)
            intent.putExtra("uid", Uid)
            startActivity(intent)
        }

        getPostList()

        return binding.root
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
}