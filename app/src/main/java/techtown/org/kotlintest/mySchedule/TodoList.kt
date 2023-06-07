package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import techtown.org.kotlintest.MyDecoration2
import techtown.org.kotlintest.MyTodoAdapter
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityTodoListBinding
import techtown.org.kotlintest.databinding.ItemDayListBinding


class TodoList : AppCompatActivity()
{
    lateinit var myAdapter: MyTodoAdapter
    lateinit var myAdapter2: MyTodoAdapter

    lateinit var dao: TodoDao
    var datas = mutableListOf<TodoData>()
    var datas2 = mutableListOf<TodoData>()

    lateinit var sKey: String
    lateinit var sName: String
    lateinit var sPlace: String
    lateinit var sDate: String
    lateinit var eDate: String
    var diffDay: Int = 0
    var flag: Int = 0

    var sTravelWhom = arrayListOf<String>()
    var sTravelStyle = arrayListOf<String>()

    var todoDB = Firebase.database.reference.child("todo")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTodoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*todoViewModel.todoList.observe(this) {
            todoAdapter.update(it)
        }*/

        //dao 초기화
        dao = TodoDao()

        val layoutManager = LinearLayoutManager(this)
        binding.todoRecycle.layoutManager = layoutManager
        myAdapter = MyTodoAdapter(this)
        binding.todoRecycle.adapter = myAdapter
        binding.todoRecycle.addItemDecoration(MyDecoration2(this as Context))

        /*val layoutManager2 = LinearLayoutManager(this)
        binding.todo2Recycle.layoutManager = layoutManager2
        myAdapter2 = MyTodoAdapter(this)
        binding.todo2Recycle.adapter = myAdapter2
        binding.todo2Recycle.addItemDecoration(MyDecoration2(this as Context))*/

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

            //데이터 담기
            //key = travelKey
            sKey = intent.getStringExtra("key")!!
            sName = intent.getStringExtra("name")!!
            sPlace = intent.getStringExtra("place")!!
            sDate = intent.getStringExtra("sDate")!!
            eDate = intent.getStringExtra("eDate")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!
            sTravelWhom = intent.getStringArrayListExtra("travelWhom")!!
            sTravelStyle = intent.getStringArrayListExtra("travelStyle")!!

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
            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            intent.putExtra("travelWhom", sTravelWhom)
            intent.putExtra("travelStyle", sTravelStyle)
            Toast.makeText(this, "${sKey}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }))

        binding.btnSupplies.setOnClickListener(({
            val intent = Intent(this, SuppliesList::class.java)
            intent.putExtra("key", sKey)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            intent.putExtra("travelWhom", sTravelWhom)
            intent.putExtra("travelStyle", sTravelStyle)
            Toast.makeText(this, "${sKey}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }))

        binding.addTodo.setOnClickListener(({
            val intent = Intent(this, AddTodoActivity::class.java)
            intent.putExtra("key", sKey)
            startActivity(intent)
        }))


        /*myAdapter.setItemCheckBoxClickListener(object: MyTodoAdapter.ItemCheckBoxClickListener {
            override fun onClick(view: View, position: Int, itemId: String) {
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)
                    todo.isChecked = !todo.isChecked
                    todoViewModel.update(todo)
                }
            }
        })*/

        getTodoList()

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
                        val tKey = datas[position].todoKey

                        dao.todoDelete(key, tKey).addOnSuccessListener {
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
        }).attachToRecyclerView(binding.todoRecycle)

        /*if (flag == 1) {
            dao.add2(sKey, datas[0])
            dao.add2(sKey, datas[1])
            dao.add2(sKey, datas[2])
            dao.add2(sKey, datas[3])
            Toast.makeText(this, "${flag}", Toast.LENGTH_SHORT).show()
            flag = 0
        }*/
    }

    /*companion object {
        lateinit var selectCheckBoxPosition:HashMap<Int, Int>
            private set
    }*/

    private fun getTodoList(){
        dao.getTodoList(sKey)?.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()
                /*datas2.clear()*/

                datas.apply {
                    add(TodoData(sKey, "", "todo1", false))
                    add(TodoData(sKey, "", "todo2", false))
                    add(TodoData(sKey, "", "todo3", false))
                    add(TodoData(sKey, "", "todo4", false))
                    myAdapter.datas = datas
                    myAdapter.notifyDataSetChanged()
                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for(dataSnapshot in snapshot.children){
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val todoList = dataSnapshot.getValue(TodoData::class.java)
                    //키 값 가져오기
                    val key = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    todoList?.todoKey = key.toString()

                    if (todoList != null) {
                        todoDB.child(sKey).child(todoList.todoKey).child("todoKey").setValue(key.toString())
                    }

                    if (todoList != null) {
                        datas.add(todoList)
                    }
                }
                //데이터 적용
                myAdapter.notifyDataSetChanged()
                /*myAdapter2.notifyDataSetChanged()*/

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