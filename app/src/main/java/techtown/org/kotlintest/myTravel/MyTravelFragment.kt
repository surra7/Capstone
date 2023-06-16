package techtown.org.kotlintest.myTravel

import android.content.Context
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
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.FragmentMyTravelBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MyTravelFragment: Fragment(){
    lateinit var myAdapter: MyAdapter
    lateinit var myAdapter2: MyAdapter
    /*lateinit var myAdapter2: MyAdapter*/

    var datas = mutableListOf<TravelData>()
    var datas2 = mutableListOf<TravelData>()

    val currentDate: LocalDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val today = currentDate.format(formatter)

    var dao = TravelDao()

    var travelDB = Firebase.database.reference.child("travel")

    var Uid : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMyTravelBinding.inflate(inflater, container, false)
        Uid = arguments?.getString("uid")

        //dao 초기화
        dao = TravelDao()

        val layoutManager = LinearLayoutManager(activity)
        binding.upcomingRecycle.layoutManager = layoutManager
        myAdapter = MyAdapter(this)
        binding.upcomingRecycle.adapter = myAdapter
        binding.upcomingRecycle.addItemDecoration(MyDecoration(activity as Context))

        val layoutManager2 = LinearLayoutManager(activity)
        binding.pastRecycle.layoutManager = layoutManager2
        myAdapter2 = MyAdapter(this)
        binding.pastRecycle.adapter = myAdapter2
        binding.pastRecycle.addItemDecoration(MyDecoration(activity as Context))

        /*myAdapter.SetOnItemClickListener(object : MyAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ListData, pos : Int) {
                Intent(this, Recycle_Main::class.java).apply {
                    putExtra("data", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { ContextCompat.startActivity(this) }
            }

        })*/

        /*val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            *//*it.data!!.getStringExtra("result")?.let {
                datas?.add(ListData(it,it))
                myAdapter.notifyDataSetChanged()
            }*//*
        }

        binding.addNewPlan.setOnClickListener{
            val intent = Intent(context, AddActivity2::class.java)
            requestLauncher.launch(intent)
        }*/

        binding.addNewPlan.setOnClickListener(({
            val intent = Intent(context, AddActivity2::class.java)
            intent.putExtra("uid", Uid)
            startActivity(intent)
        }))

        getTravelList()

        return binding.root

    }

    private fun getTravelList(){
        Uid?.let {
            dao.getTravelList(it)?.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    datas.clear()
                    datas2.clear()

                    datas.apply {
                        datas.sortByDescending { it.sDate }
                        myAdapter.datas = datas
                        myAdapter.notifyDataSetChanged()
                    }

                    datas2.apply {
                        datas2.sortByDescending { it.sDate }
                        myAdapter2.datas = datas2
                        myAdapter2.notifyDataSetChanged()
                    }

                    //snapshot.children으로 dataSnapshot에 데이터 넣기
                    for(dataSnapshot in snapshot.children){
                        //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                        val travelList = dataSnapshot.getValue(TravelData::class.java)
                        //키 값 가져오기
                        val key = dataSnapshot.key
                        //schedule 정보에 키 값 담기
                        travelList?.travelKey = key.toString()

                        if (travelList != null) {
                            travelDB.child(it).child(travelList.travelKey).child("travelKey").setValue(key.toString())
                        }

                        if (travelList != null && travelList.sDate >= today) {
                            datas.add(travelList)
                            datas.apply { datas.sortByDescending { it.sDate } }
                        }

                        if (travelList != null && travelList.sDate < today) {
                            datas2.add(travelList)
                            datas2.apply { datas2.sortByDescending { it.sDate } }
                        }
                    }
                    //데이터 적용
                    myAdapter.notifyDataSetChanged()
                    myAdapter2.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}
