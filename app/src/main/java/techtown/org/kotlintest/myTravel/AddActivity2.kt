package techtown.org.kotlintest.myTravel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import java.text.SimpleDateFormat
import java.util.*

class AddActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityAdd2Binding
    /*lateinit var sDate: String
    lateinit var eDate: String*/

    //datepicker 팝업
    lateinit var Uid: String
    private var calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private lateinit var selectedDate: Calendar
    private lateinit var endDate: Calendar

    @SuppressLint("SetText|18n")//하드코딩 허용
    /* private val TAG = this.javaClass.simpleName
     //콜백 인스턴스 생성
     private val callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
             // 뒤로 버튼 이벤트 처리
             Log.e(TAG, "뒤로가기 클릭")
         }
     }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdd2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra("uid")) {
            Uid = intent.getStringExtra("uid")!!
        }

        /*binding.applyDate.setOnClickListener{
            val intent = Intent(this, Add_Country::class.java)
            requestLauncher.launch(intent)
        }*/

        /*//객체 생성
        val dayText: TextView = binding.startDate
        val datePicker: DatePicker = binding.datePicker

        val sYear: Int = datePicker.year
        val sMonth: Int = datePicker.month
        val sDay: Int = datePicker.dayOfMonth

        //날짜 변수에 담기
        dayText.text = "${sYear}/ ${sMonth + 1}/ ${sDay}"

        //calerdarView 날짜 변환 이벤트
        datePicker.setOnDateChangedListener{ view, year, month, dayOfMonth ->

            //날짜 변수에 담기
            dayText.text = "${year}/ ${month + 1}/ ${dayOfMonth}"
        }*/

        binding.startDateBtn.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                //선택한 날짜 담기
                selectedDate = Calendar.getInstance().apply { set(year, month, day) }

                binding.startDateBtn.text =
                    year.toString() + "/ " + (month + 1).toString() + "/ " + day.toString()

                /*val sYear = year.toString().format("yyyy")
                val sMonth = (month + 1).toString().format("MM")
                val sDay = day.toString().format("dd")

                sDate = "${sYear}.${sMonth}.${sDay}"*/

            }, year, month, day)
            datePickerDialog.show()
        }

        binding.endDateBtn.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                endDate = Calendar.getInstance().apply { set(year, month, day) }

                binding.endDateBtn.text =
                    year.toString() + "/ " + (month + 1).toString() + "/ " + day.toString()

                /*val eYear = year.toString()
                val eMonth = (month + 1).toString()
                val eDay = day.toString()

                eDate = "${eYear}.${eMonth}.${eDay}"*/

            }, year, month, day).apply {
                //위에서 저장한 selectedDate를 datePicker의 minDate로 적용
                datePicker.minDate = selectedDate.timeInMillis
            }
            datePickerDialog.show()
        }

        //데이터베이스 클래스 객체 생성
        val dao = TravelDao()

        binding.applyDate.setOnClickListener(({
            val name = binding.titleEdit.text.toString()

            var sDate = SimpleDateFormat("yyyy.MM.dd").format(selectedDate.time).toString()
            var eDate = SimpleDateFormat("yyyy.MM.dd").format(endDate.time).toString()

            var diffDay = (((endDate.timeInMillis - selectedDate.timeInMillis) / (60 * 60 * 24 * 1000)) + 1).toInt()

            /*val travel = TravelData("", name, "", sDate, eDate, diffDay)

            dao.add(travel)?.addOnSuccessListener {
                Toast.makeText(this, "등록 성공", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener {
                Toast.makeText(this, "등록 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }*/

            val intent = Intent(this, Add_Country::class.java)
            /*intent.putExtra("key", travel.travelKey)*/
            intent.putExtra("uid", Uid)
            intent.putExtra("name", name)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)
            /*Toast.makeText(this, "${travel.travelKey}", Toast.LENGTH_SHORT).show()*/
            startActivity(intent)
            finish()
        }))

    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId){
        android.R.id.home -> {
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
            true
        }
        else -> true
    }*/

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