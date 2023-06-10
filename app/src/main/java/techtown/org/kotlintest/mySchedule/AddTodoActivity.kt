package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddTodoBinding
import techtown.org.kotlintest.myTravel.Add_Country
import techtown.org.kotlintest.myTravel.TravelDao
import techtown.org.kotlintest.myTravel.TravelData
import java.text.SimpleDateFormat

class AddTodoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddTodoBinding

    lateinit var Uid: String
    lateinit var sKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        /*setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        *//*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*//*
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        val dao = TodoDao()

        if (intent.hasExtra("key")) {

            //데이터 담기
            sKey = intent.getStringExtra("key")!!
            Uid = intent.getStringExtra("uid")!!
        }

        binding.btnSave.setOnClickListener(({
            val todo = binding.etTodoContent.text.toString()

            val todoList = TodoData(Uid, sKey, "", todo, false)

            dao.add2(Uid, sKey, todoList)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }

            /*val intent = Intent(this, Add_Country::class.java)
            *//*intent.putExtra("key", travel.travelKey)*//*
            intent.putExtra("name", name)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            Toast.makeText(this, "${diffDay}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()*/
        }))

    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
    }*/
}