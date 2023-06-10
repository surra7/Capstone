package techtown.org.kotlintest.mySchedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddSuppliesBinding
import techtown.org.kotlintest.databinding.ActivityAddTodoBinding
import techtown.org.kotlintest.myTravel.Add_Country
import techtown.org.kotlintest.myTravel.TravelDao
import techtown.org.kotlintest.myTravel.TravelData
import java.text.SimpleDateFormat

class AddSuppliesActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddSuppliesBinding

    lateinit var Uid: String
    lateinit var sKey: String
    lateinit var sType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSuppliesBinding.inflate(layoutInflater)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.root)

        /*setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        *//*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*//*
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        val dao = SuppliesDao()

        if (intent.hasExtra("key")) {

            //데이터 담기
            Uid = intent.getStringExtra("uid")!!
            sKey = intent.getStringExtra("key")!!
        }

        sType = "Checked Baggage"

        binding.checkedBaggage.setOnClickListener{

            //입력값
            sType = binding.checkedBaggage.text.toString()
            Toast.makeText(this, "${sType}", Toast.LENGTH_SHORT).show()
        }

        binding.carryOnBaggage.setOnClickListener {

            //입력값
            sType = binding.carryOnBaggage.text.toString()
            Toast.makeText(this, "${sType}", Toast.LENGTH_SHORT).show()
        }

        binding.btnSave.setOnClickListener(({
            val supplies = binding.etSuppliesContent.text.toString()

            val suppliesList = SuppliesData(Uid, sKey, "", sType, supplies,false)

            dao.add2(Uid, sKey, suppliesList)?.addOnSuccessListener {
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