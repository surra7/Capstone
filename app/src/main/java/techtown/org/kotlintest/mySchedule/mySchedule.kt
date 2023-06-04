package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.ActivityMyScheduleBinding
import techtown.org.kotlintest.databinding.ItemDayListBinding
import techtown.org.kotlintest.myTravel.TravelDao
import techtown.org.kotlintest.myTravel.TravelData

class mySchedule : AppCompatActivity()
{
    lateinit var myAdapter: InRecyclerViewAdapter
    lateinit var myAdapter3: OutRecyclerViewAdapter

    lateinit var dao: ScheduleDao
    lateinit var dao2: TravelDao

    val data = mutableListOf<ScheduleListData>()
    val datas2 = mutableListOf<ScheduleData>()

    lateinit var binding: ActivityMyScheduleBinding

    var travelDB = Firebase.database.reference.child("travel")

    lateinit var sKey: String
    lateinit var sName: String
    lateinit var sPlace: String
    lateinit var sDate: String
    lateinit var eDate: String
    var diffDay: Int = 0
    var flags: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var binding2 = ItemDayListBinding.inflate(layoutInflater)

        //dao 초기화
        dao = ScheduleDao()
        dao2 = TravelDao()

        /*val layoutManager = LinearLayoutManager(this)
        binding.dayRecycle.layoutManager=layoutManager
        myAdapter = InRecyclerViewAdapter(this, datas)
        binding.dayRecycle.adapter = myAdapter
        binding.dayRecycle.addItemDecoration(MyDecoration2(this as Context))*/

        val layoutManager2 = LinearLayoutManager(this)
        binding.dayRecycle.layoutManager=layoutManager2
        myAdapter = InRecyclerViewAdapter(this, datas2)
        binding.dayRecycle.adapter = myAdapter
        binding.dayRecycle.addItemDecoration(MyDecoration(this as Context))

        val layoutManager3 = LinearLayoutManager(this)
        binding.dayRecycle.layoutManager=layoutManager3
        myAdapter3 = OutRecyclerViewAdapter(this, data)
        binding.dayRecycle.adapter = myAdapter3
        binding.dayRecycle.addItemDecoration(MyDecoration2(this as Context))

        setSupportActionBar(binding.toolbar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toggle.syncState()

        //데이터 null체크
        if(intent.hasExtra("key") && intent.hasExtra("name")
            && intent.hasExtra("sDate") && intent.hasExtra("eDate")
            && intent.hasExtra("diffDay")) {

            //데이터 담기
            //key = travelKey
            sKey = intent.getStringExtra("key")!!
            sName = intent.getStringExtra("name")!!
            sPlace = intent.getStringExtra("place")!!
            sDate = intent.getStringExtra("sDate")!!
            eDate = intent.getStringExtra("eDate")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!
            flags = intent.getIntExtra("flags", 0)!!

            //데이터 보여주기
            binding.travelName.setText(sName)
            binding.place.setText(sPlace)
            binding.sDate.setText(sDate)
            binding.eDate.setText(eDate)

        }

        binding.fab.setOnClickListener(({
            val intent = Intent(this, AddActivity::class.java)
            if(intent.hasExtra("key") && intent.hasExtra("diffDay")) {

                //데이터 담기
                //key = travelKey
                sKey = intent.getStringExtra("key")!!
                diffDay = intent.getIntExtra("diffDay", 0)!!
            }
            intent.putExtra("key", sKey)
            intent.putExtra("diffDay", diffDay)
            Toast.makeText(this, "${diffDay}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }))

        binding.btnSupplies.setOnClickListener(({
            val intent = Intent(this, SuppliesList::class.java)

            Toast.makeText(this, "${sKey}", Toast.LENGTH_SHORT).show()
            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)

            startActivity(intent)
            finish()
        }))

        binding.btnTodo.setOnClickListener(({
            val intent = Intent(this, TodoList::class.java)

            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)

            startActivity(intent)
            finish()
        }))

        //디폴트 데이터 -> 추천목록
        if (flags == 0){
            val schedule = ScheduleData(sKey, "", "1", "place", "12:00", "memo", "", diffDay)
            dao.add2(sKey, schedule)
            travelDB.child(sKey).child("flags").setValue(1)
        }

        /*getScheduleList()*/
        getScheduleList2()

    }

    private fun getScheduleList2(){
        dao.getScheduleList(sKey)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                datas2.clear()

                data.apply {
                    for (i: Int in 1..diffDay) {
                        add(
                            ScheduleListData(
                                sKey, "", i.toString(), mutableListOf()
                            )
                        )
                    }
                    myAdapter3.itemList = data
                    myAdapter3.notifyDataSetChanged()

                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for(dataSnapshot in snapshot.children) {
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val schedule = dataSnapshot.getValue(ScheduleData::class.java)
                    /*val daySchedule = schedule?.scheduleList*/
                    //키 값 가져오기
                    val key = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    schedule?.scheduleKey = key.toString()

                    for (id: Int in 0 until diffDay) {
                        if (schedule != null && data[id].scheduleDay == schedule.day) {
                            data[id].scheduleList.apply{ add(schedule) }
                            data[id].scheduleList.apply{ sortBy { it.time } }
                        }
                    }
                    //데이터 적용
                    myAdapter3.notifyDataSetChanged()
                    myAdapter.notifyDataSetChanged()
                    /*myAdapter2.notifyDataSetChanged()*/
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.recycler_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete")
                    .setMessage("Are You Sure Want To Delete This Schedule?")
                    .setPositiveButton("Delete",
                        DialogInterface.OnClickListener { dialog, id ->
                            dao2.travelDelete(sKey).addOnSuccessListener {
                                Toast.makeText(applicationContext, "Delete Success", Toast.LENGTH_SHORT).show()

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

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.recycler_menu, menu)

        //사용자가 검색한 내용을 받기
        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                //사용자가 검색창에 글자를 입력할 때마다 동작하는 함수
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("kkang", "search text: $query")
                return true
            }
        })
        return true
    }*/

}