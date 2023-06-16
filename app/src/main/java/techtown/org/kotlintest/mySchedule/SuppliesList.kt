package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.ActivitySuppliesListBinding
import techtown.org.kotlintest.myTravel.TravelDao

class SuppliesList : AppCompatActivity()
{
    lateinit var myAdapter: MySuppliesAdapter
    lateinit var myAdapter2: MySuppliesAdapter

    lateinit var dao: ScheduleDao
    lateinit var dao2: TravelDao
    lateinit var dao3: SuppliesDao
    lateinit var dao4: TodoDao

    var datas = mutableListOf<SuppliesData>()
    var datas2 = mutableListOf<SuppliesData>()

    lateinit var Uid: String
    lateinit var sKey: String
    lateinit var sName: String
    lateinit var sPlace: String
    lateinit var sDate: String
    lateinit var eDate: String
    var diffDay: Int = 0
    var flags: Int = 0

    var sTravelWhom = arrayListOf<String>()
    var sTravelStyle = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySuppliesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*todoViewModel.todoList.observe(this) {
            todoAdapter.update(it)
        }*/

        //dao 초기화
        dao = ScheduleDao()
        dao2 = TravelDao()
        dao3 = SuppliesDao()
        dao4 = TodoDao()

        val layoutManager = LinearLayoutManager(this)
        binding.suppliesRecycle1.layoutManager = layoutManager
        myAdapter = MySuppliesAdapter(this)
        binding.suppliesRecycle1.adapter = myAdapter
        binding.suppliesRecycle1.addItemDecoration(MyDecoration2(this as Context))

        val layoutManager2 = LinearLayoutManager(this)
        binding.suppliesRecycle2.layoutManager = layoutManager2
        myAdapter2 = MySuppliesAdapter(this)
        binding.suppliesRecycle2.adapter = myAdapter2
        binding.suppliesRecycle2.addItemDecoration(MyDecoration2(this as Context))

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
            && intent.hasExtra("sDate") && intent.hasExtra("eDate")) {

            //key = travelKey
            Uid = intent.getStringExtra("uid")!!
            sKey = intent.getStringExtra("key")!!
            sName = intent.getStringExtra("name")!!
            sPlace = intent.getStringExtra("place")!!
            sDate = intent.getStringExtra("sDate")!!
            eDate = intent.getStringExtra("eDate")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!
            sTravelWhom = intent.getStringArrayListExtra("travelWhom")!!
            sTravelStyle = intent.getStringArrayListExtra("travelStyle")!!
            flags = intent.getIntExtra("flags", 0)!!


            //데이터 보여주기
            binding.travelName.setText(sName)
            binding.place.setText(sPlace)
            binding.sDate.setText(sDate)
            binding.eDate.setText(eDate)

            var travelWhomList = ""
            for (item in sTravelWhom){
                if (item == sTravelWhom.last()){
                    travelWhomList += item
                } else {
                    travelWhomList += "${item} / "
                }
            }
            binding.travelWhom.setText(travelWhomList)

            var travelStyleList = ""
            for (item in sTravelStyle){
                if (item == sTravelStyle.last()){
                    travelStyleList += item
                } else {
                    travelStyleList += "${item} / "
                }
            }
            binding.travelStyle.setText(travelStyleList)
        }

        binding.btnMySchedule.setOnClickListener(({
            val intent = Intent(this, mySchedule::class.java)
            intent.putExtra("uid", Uid)
            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            intent.putExtra("travelWhom", sTravelWhom)
            intent.putExtra("travelStyle", sTravelStyle)
            intent.putExtra("flags", flags)

            startActivity(intent)
            finish()
        }))

        binding.btnTodo.setOnClickListener(({
            val intent = Intent(this, TodoList::class.java)
            intent.putExtra("uid", Uid)
            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            intent.putExtra("travelWhom", sTravelWhom)
            intent.putExtra("travelStyle", sTravelStyle)
            intent.putExtra("flags", flags)

            startActivity(intent)
            finish()
        }))

        binding.addSupplies.setOnClickListener(({
            val intent = Intent(this, AddSuppliesActivity::class.java)
            intent.putExtra("uid", Uid)
            intent.putExtra("key", sKey)
            startActivity(intent)
        }))

        getSuppliesList()

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean{
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //해당 위치 값 변수에 담기
                val position = viewHolder.bindingAdapterPosition

                when(direction){
                    ItemTouchHelper.LEFT -> {
                        val key = datas[position].travelKey
                        val tKey = datas[position].suppliesKey

                        dao3.suppliesDelete(Uid, key, tKey).addOnSuccessListener {
                            Toast.makeText(applicationContext, "Delete Success", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Toast.makeText(applicationContext, "Delete Fail: ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //스와이프 꾸미기
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("Delete")
                    .create()
                    .decorate()


                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.suppliesRecycle1)

        binding.suppliesRecycle1.adapter?.notifyDataSetChanged()

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean{
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //해당 위치 값 변수에 담기
                val position = viewHolder.bindingAdapterPosition

                when(direction){
                    ItemTouchHelper.LEFT -> {
                        val key = datas2[position].travelKey
                        val tKey = datas2[position].suppliesKey

                        dao3.suppliesDelete(Uid, key, tKey).addOnSuccessListener {
                            Toast.makeText(applicationContext, "Delete Success", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Toast.makeText(applicationContext, "Delete Fail: ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //스와이프 꾸미기
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.RED)
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftLabel("Delete")
                    .create()
                    .decorate()


                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(binding.suppliesRecycle2)

        binding.suppliesRecycle2.adapter?.notifyDataSetChanged()

    }

    private fun getSuppliesList(){
        dao3.getSuppliesList(Uid, sKey)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()
                datas2.clear()

                datas.apply {
                    myAdapter.datas = datas
                    myAdapter.notifyDataSetChanged()
                }

                datas2.apply {
                    myAdapter2.datas = datas2
                    myAdapter2.notifyDataSetChanged()
                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for(dataSnapshot in snapshot.children){
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val suppliesList = dataSnapshot.getValue(SuppliesData::class.java)
                    //키 값 가져오기
                    val key = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    suppliesList?.suppliesKey = key.toString()

                    if (suppliesList != null && suppliesList.suppliesType == "Checked Baggage") {
                        datas.add(suppliesList)
                    }

                    if (suppliesList != null && suppliesList.suppliesType == "Carry-on Baggage") {
                        datas2.add(suppliesList)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recycler_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //토글 버튼이 눌렀을 때 일어나는 이벤트
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("datas", ArrayList(datas))
    }
*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Delete")
                    .setMessage("Are You Sure Want To Delete This Schedule and Check List?")
                    .setPositiveButton("Delete",
                        DialogInterface.OnClickListener { dialog, id ->
                            dao2.travelDelete(Uid, sKey).addOnSuccessListener {
                                Toast.makeText(applicationContext, "Delete Success", Toast.LENGTH_SHORT).show()

                                dao.scheduleAllDelete(Uid, sKey)
                                dao3.suppliesAllDelete(Uid, sKey)
                                dao4.todoAllDelete(Uid, sKey)

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

}