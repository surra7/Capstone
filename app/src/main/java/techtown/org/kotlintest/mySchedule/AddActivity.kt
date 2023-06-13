package techtown.org.kotlintest.mySchedule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityAddBinding
import java.sql.Date
import java.util.Calendar

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding

    lateinit var Uid: String
    lateinit var sKey: String
    lateinit var sDay: String
    var diffDay: Int = 0
    lateinit var time: String
    lateinit var location: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /*lateinit var sLocation: String
    lateinit var sLatLong: String*/

    var c: Calendar = Calendar.getInstance()
    var HourOfDay = c.get(Calendar.HOUR_OF_DAY)
    var minute = c.get(Calendar.MINUTE)

    /*override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = "현재 설정된 시간 : \n 시:분 | $hourOfDay : $minute"
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonview = findViewById<LinearLayout>(R.id.day_btn)

        /*val dynamicButton = Button(this)*/
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        /*dynamicButton.layoutParams = layoutParams
        layoutParams.setMargins(changeDP(10), changeDP(20), changeDP(10), 0)
        buttonview.addView(dynamicButton)*/


        /*binding.dayBtnList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dayBtnList.adapter = MyAdapter2(this, DataList)*/

        setSupportActionBar(binding.addToolbar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*sKey = intent.getStringExtra("key")!!*/
        sDay = "1"

        if(intent.hasExtra("key") && intent.hasExtra("diffDay")) {

            //데이터 담기
            //key = travelKey
            Uid = intent.getStringExtra("uid")!!
            sKey = intent.getStringExtra("key")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!
        }

        /*for (i: Int in 1..diffDay) {
            DataList.add(DayData("", "Day${i}", diffDay))
        }*/

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

        /*DataList.apply {
            print{}
            for (i: Int in 1..diffDay) {
                add(DayData("", "Day${i}"))
            }
        }*/

        time = ""
        location = ""

        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        time = String.format("%02d", HourOfDay) + ":" + String.format("%02d", minute)

        timePicker.setOnTimeChangedListener {view: TimePicker?, HourOfDay: Int, minute: Int ->
            time = String.format("%02d", HourOfDay) + ":" + String.format("%02d", minute)
        }

        binding.locationEdit.setOnClickListener{
            /*val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)*/
            openActivityForResult()
        }

        //데이터베이스 클래스 객체 생성
        val dao = ScheduleDao()

        binding.applyBtn.setOnClickListener{
            val place = binding.placeEdit.text.toString()
            /*val time = binding.timeEdit.text.toString()*/
            /*val day = binding.dayEdit.text.toString()*/
            val memo = binding.memoEdit.text.toString()

            val schedule = ScheduleData(Uid, sKey, "", sDay, place, time, memo, location, latitude, longitude, diffDay)

            dao.add2(Uid, sKey, schedule)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }

            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()

            /*val intent = Intent(this, Recycle_Main::class.java)
            startActivity(intent)
            finish()*/
        }

    }

    fun openActivityForResult() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, 123)
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == 123) {
            location = data?.getStringExtra("location")!!
            latitude = data.getDoubleExtra("latitude", 0.0)!!
            longitude = data.getDoubleExtra("longitude", 0.0)!!
            Toast.makeText(this, "${location}", Toast.LENGTH_SHORT).show()
            binding.locationEdit.text= location
        }

    }

    private fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
    }

    fun btnAction(id: Int){
        sDay = "${id + 1}"
        Toast.makeText(this, "${sDay}", Toast.LENGTH_SHORT).show()
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        /*R.id.menu_add_save -> {
            //데이터베이스 클래스 객체 생성
            val dao = ScheduleDao()

            val place = binding.placeEdit.text.toString()
            val time = binding.timeEdit.text.toString()

            val schedule = ScheduleData("", place, time)

            dao.add(schedule)?.addOnSuccessListener {
                Toast.makeText(this, "등록 성공", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }


            val intent = Intent(this, Recycle_Main::class.java)
            startActivity(intent)

            //val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }*/
        android.R.id.home -> {
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }
}