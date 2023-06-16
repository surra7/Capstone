package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import techtown.org.kotlintest.databinding.ActivityUpdateBinding
import techtown.org.kotlintest.databinding.ActivityUpdateTodoBinding

class UpdateTodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTodoBinding

    lateinit var Uid: String
    lateinit var Key: String
    lateinit var sKey: String
    lateinit var sTodo: String
    var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTodoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        /*setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        *//*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*//*
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        //데이터베이스 객체
        val dao = TodoDao()

        //데이터 null체크
        if (intent.hasExtra("sKey") && intent.hasExtra("todo")
            && intent.hasExtra("isChecked")
        ) {

            //데이터 담기
            Uid = intent.getStringExtra("uid")!!
            Key = intent.getStringExtra("key")!!
            sKey = intent.getStringExtra("sKey")!!
            sTodo = intent.getStringExtra("todo")!!
            isChecked = intent.getBooleanExtra("isChecked", false)!!
        }

        //데이터 보여주기
        binding.etTodoContent.setText(sTodo)

        //수정버튼 이벤트
        binding.btnSave.setOnClickListener {

            //입력값
            val uTodo = binding.etTodoContent.text.toString()

            //파라미터 셋팅
            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["todo"] = uTodo
            hashMap["isChecked"] = isChecked

            dao.todoUpdate(Uid, Key, sKey, hashMap).addOnSuccessListener {
                Toast.makeText(applicationContext, "Edit Success", Toast.LENGTH_SHORT).show()

                /*//목록으로 이동
                val intent = Intent(this, TodoList::class.java)
                intent.putExtra("key", sKey)
                startActivity(intent)
                finish()*/

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Edit Fail: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    /* override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
         android.R.id.home -> {
             val intent = intent
             setResult(Activity.RESULT_OK, intent)
             finish()
             true
         }
         else -> true
     }*/
}