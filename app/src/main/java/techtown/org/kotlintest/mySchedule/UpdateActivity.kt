package techtown.org.kotlintest.mySchedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityUpdateBinding
import java.util.*
import kotlin.collections.HashMap

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding

    lateinit var Uid: String
    lateinit var Key: String
    lateinit var sKey: String
    lateinit var sPlace: String
    lateinit var sTime: String
    lateinit var sDay: String
    lateinit var sMemo: String
    lateinit var sLocation: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var diffDay: Int = 0

    var c: Calendar = Calendar.getInstance()
    var HourOfDay = c.get(Calendar.HOUR_OF_DAY)
    var minute = c.get(Calendar.MINUTE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonview = findViewById<LinearLayout>(R.id.day_btn)

        /*val dynamicButton = Button(this)*/
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        setSupportActionBar(binding.addToolbar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //데이터베이스 객체
        val dao = ScheduleDao()

        //데이터 null체크
        if (intent.hasExtra("key") && intent.hasExtra("place")
            && intent.hasExtra("time") && intent.hasExtra("day")
            && intent.hasExtra("diffDay")) {

            //데이터 담기
            Uid = intent.getStringExtra("uid")!!
            Key = intent.getStringExtra("key")!!
            sKey = intent.getStringExtra("sKey")!!
            sDay = intent.getStringExtra("day")!!
            sPlace = intent.getStringExtra("place")!!
            sTime = intent.getStringExtra("time")!!
            sMemo = intent.getStringExtra("memo")!!
            sLocation = intent.getStringExtra("location")!!
            latitude = intent.getDoubleExtra("latitude", 0.0)!!
            longitude = intent.getDoubleExtra("longitude", 0.0)!!
            diffDay = intent.getIntExtra("diffDay", 0)!!

            //데이터 보여주기
            binding.placeEdit.setText(sPlace)
            binding.memoEdit.setText(sMemo)
            binding.locationEdit.setText(sLocation)
        }

        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        if (sTime == "") {
            sTime = String.format("%02d", HourOfDay) + ":" + String.format("%02d", minute)
        }

        timePicker.setOnTimeChangedListener { timePicker, hourOfDay, minute ->
            sTime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute)
        }

        for (id: Int in 0 until diffDay){
            val dynamicDayButton = Button(this).apply {
                this.id = id + 1
                this.text = "Day ${id + 1}"
                this.background = getDrawable(R.drawable.ic_button)
                setOnClickListener{
                    btnAction(id)
                }
                this.layoutParams = layoutParams
                layoutParams.setMargins(changeDP(5), changeDP(5), changeDP(5), 10)
            }
            buttonview.addView(dynamicDayButton)
        }

        binding.locationEdit.setOnClickListener{
            /*val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)*/
            openActivityForResult()
        }

        //수정버튼 이벤트
        binding.upApplyBtn.setOnClickListener {

            //입력값
            val uPlace = binding.placeEdit.text.toString()
            /*val uTime = binding.timeEdit.text.toString()*/
            val uMemo = binding.memoEdit.text.toString()

            //파라미터 셋팅
            val hashMap: HashMap<String, Any> = HashMap()
            hashMap["place"] = uPlace
            hashMap["time"] = sTime
            hashMap["day"] = sDay
            hashMap["memo"] = uMemo
            hashMap["location"] = sLocation
            hashMap["diffDay"] = diffDay

            dao.scheduleUpdate(Uid, Key, sKey, hashMap).addOnSuccessListener {
                Toast.makeText(applicationContext, "Edit Success", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Edit Fail: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //삭제버튼 이벤트
        binding.upDeleteBtn.setOnClickListener {

            dao.scheduleDelete(Uid, Key, sKey).addOnSuccessListener {
                Toast.makeText(applicationContext, "Edit Success", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Edit Fail: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun openActivityForResult() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("location", sLocation)
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivityForResult(intent, 123)
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            sLocation = data?.getStringExtra("location")!!
            latitude = data.getDoubleExtra("latitude", 0.0)!!
            longitude = data.getDoubleExtra("longitude", 0.0)!!
            Toast.makeText(this, "${sLocation}", Toast.LENGTH_SHORT).show()
            binding.locationEdit.text= sLocation
        }

    }

    private fun changeDP(value: Int): Int {
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    fun btnAction(id: Int) {
        sDay = "${id + 1}"
        Toast.makeText(this, "${sDay}", Toast.LENGTH_SHORT).show()
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