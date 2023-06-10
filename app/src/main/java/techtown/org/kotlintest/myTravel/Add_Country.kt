package techtown.org.kotlintest.myTravel

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddCountryBinding
import techtown.org.kotlintest.mySchedule.ScheduleData
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class Add_Country : AppCompatActivity()/*, View.OnClickListener*/ {
    val TAG = "Add_Country"

    lateinit var binding: ActivityAddCountryBinding
    lateinit var search_country: SearchView

    lateinit var myAdapter1: MyAdapter2
    lateinit var myAdapter2: MyAdapter2
    lateinit var myAdapter3: MyAdapter2
    lateinit var myAdapter4: MyAdapter2
    lateinit var myAdapter5: MyAdapter2
    lateinit var myAdapter6: MyAdapter2

    lateinit var Uid: String
    lateinit var sName: String
    lateinit var sPlace: String
    lateinit var sDate: String
    lateinit var eDate: String
    var diffDay: Int = 0

    lateinit var dao: TravelDao

    val datas1 = ArrayList<CountryData>()
    val datas2 = ArrayList<CountryData>()
    val datas3 = ArrayList<CountryData>()
    val datas4 = ArrayList<CountryData>()
    val datas5 = ArrayList<CountryData>()
    val datas6 = ArrayList<CountryData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //데이터 null체크
        if (intent.hasExtra("name")
            && intent.hasExtra("sDate") && intent.hasExtra("eDate")
        ) {

            //데이터 담기
            Uid = intent.getStringExtra("uid")!!
            sName = intent.getStringExtra("name")!!
            sDate = intent.getStringExtra("sDate")!!
            eDate = intent.getStringExtra("eDate")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!

            sPlace = "select country"
        }

        datas1.apply {
            add(CountryData("", "Tokyo"))
            add(CountryData("", "Fukuoka"))
            add(CountryData("", "Osaka"))
            add(CountryData("", "Shizuoka"))
            add(CountryData("", "Nagoya"))
            add(CountryData("", "Okinawa"))
            add(CountryData("", "Sapporo"))

            /*myAdapter1.itemList = datas1*/
            /*myAdapter1.notifyDataSetChanged()*/
        }

        datas2.apply {
            add(CountryData("", "Nha Trang"))
            add(CountryData("", "Manila"))
            add(CountryData("", "Myanmar"))
            add(CountryData("", "Chiang Mai"))
            add(CountryData("", "Palawan"))
            add(CountryData("", "Phu Quoc"))
            add(CountryData("", "Laos"))
            add(CountryData("", "Kuala Lumpur"))
            add(CountryData("", "Dalat"))
            add(CountryData("", "Danang"))
            add(CountryData("", "Bangkok"))
            add(CountryData("", "Singapore"))
            add(CountryData("", "Hanoi"))
            add(CountryData("", "Bali"))
            add(CountryData("", "Phuket"))
            add(CountryData("", "Boracay"))
            add(CountryData("", "Cebu"))

            /*myAdapter2.itemList = datas2*/
            /*myAdapter2.notifyDataSetChanged()*/
        }

        datas3.apply {
            add(CountryData("", "Sydney"))
            add(CountryData("", "Melbourne"))
            add(CountryData("", "Guam"))
            add(CountryData("", "Saipan"))

            /*myAdapter3.itemList = datas3*/
            /*myAdapter3.notifyDataSetChanged()*/
        }

        datas4.apply {
            add(CountryData("", "Lisbon"))
            add(CountryData("", "Milan"))
            add(CountryData("", "Brussels"))
            add(CountryData("", "Seville"))
            add(CountryData("", "Porto"))
            add(CountryData("", "Helsinki"))
            add(CountryData("", "Paris"))
            add(CountryData("", "Prague"))
            add(CountryData("", "Rome"))
            add(CountryData("", "London"))
            add(CountryData("", "Barcelona"))
            add(CountryData("", "Wein"))
            add(CountryData("", "Florence"))
            add(CountryData("", "Madrid"))
            add(CountryData("", "Budapest"))
            add(CountryData("", "Vladivosk"))
            add(CountryData("", "Zurich"))
            add(CountryData("", "Munich"))
            add(CountryData("", "Amsterdam"))
            add(CountryData("", "Berlin"))

            /*myAdapter4.itemList = datas4*/
            /*myAdapter4.notifyDataSetChanged()*/
        }

        datas5.apply {
            add(CountryData("", "Vancouver"))
            add(CountryData("", "San Francisco"))
            add(CountryData("", "Seattle"))
            add(CountryData("", "Toronto"))
            add(CountryData("", "Hawaii"))
            add(CountryData("", "New York"))
            add(CountryData("", "Los Angeles"))

            /*myAdapter5.itemList = datas5*/
            /*myAdapter5.notifyDataSetChanged()*/
        }

        datas6.apply {
            add(CountryData("", "Kaohsiung"))
            add(CountryData("", "Qingdao"))
            add(CountryData("", "Hainan"))
            add(CountryData("", "Hong Kong"))
            add(CountryData("", "Taipei"))
            add(CountryData("", "Shanghai"))
            add(CountryData("", "Beijing"))

            /*myAdapter6.itemList = datas6*/
            /*myAdapter6.notifyDataSetChanged()*/
        }

        val layoutManager1 = LinearLayoutManager(this)
        binding.JapanRecycle.layoutManager = layoutManager1
        myAdapter1 = MyAdapter2(this, datas1)
        binding.JapanRecycle.adapter = myAdapter1
        binding.JapanRecycle.addItemDecoration(MyDecoration3(this as Context))

        val layoutManager2 = LinearLayoutManager(this)
        binding.SARecycle.layoutManager = layoutManager2
        myAdapter2 = MyAdapter2(this, datas2)
        binding.SARecycle.adapter = myAdapter2
        binding.SARecycle.addItemDecoration(MyDecoration3(this as Context))

        val layoutManager3 = LinearLayoutManager(this)
        binding.SPRecycle.layoutManager = layoutManager3
        myAdapter3 = MyAdapter2(this, datas3)
        binding.SPRecycle.adapter = myAdapter3
        binding.SPRecycle.addItemDecoration(MyDecoration3(this as Context))

        val layoutManager4 = LinearLayoutManager(this)
        binding.EuropeRecycle.layoutManager = layoutManager4
        myAdapter4 = MyAdapter2(this, datas4)
        binding.EuropeRecycle.adapter = myAdapter4
        binding.EuropeRecycle.addItemDecoration(MyDecoration3(this as Context))

        val layoutManager5 = LinearLayoutManager(this)
        binding.AmericasRecycle.layoutManager = layoutManager5
        myAdapter5 = MyAdapter2(this, datas5)
        binding.AmericasRecycle.adapter = myAdapter5
        binding.AmericasRecycle.addItemDecoration(MyDecoration3(this as Context))

        val layoutManager6 = LinearLayoutManager(this)
        binding.ChinaRecycle.layoutManager = layoutManager6
        myAdapter6 = MyAdapter2(this, datas6)
        binding.ChinaRecycle.adapter = myAdapter6
        binding.ChinaRecycle.addItemDecoration(MyDecoration3(this as Context))

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        myAdapter1.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter1.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        myAdapter2.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter2.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        myAdapter3.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter3.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        myAdapter4.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter4.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        myAdapter5.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter5.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        myAdapter6.setItemClickListener(object: MyAdapter2.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                // 클릭 시 이벤트 작성
                sPlace = myAdapter6.filteredCountry[position].countryName
                Toast.makeText(this@Add_Country, "${sPlace}", Toast.LENGTH_SHORT).show()
            }
        })

        search_country = findViewById(R.id.search_country)

        search_country.setOnQueryTextListener(searchViewTextListener)

        //적용버튼 이벤트
        binding.countryApply.setOnClickListener {
            /*Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()*/
            /*val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()*/

            val intent = Intent(this, AddTravelStyle::class.java)
            intent.putExtra("uid", Uid)
            intent.putExtra("name", sName)
            intent.putExtra("place", sPlace)
            intent.putExtra("sDate", sDate)
            intent.putExtra("eDate", eDate)
            intent.putExtra("diffDay", diffDay)

            startActivity(intent)
            finish()

            /*val travel = TravelData("", sName, sPlace, sDate, eDate, diffDay)

            dao.add(travel)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                myAdapter1.getFilter().filter(s)
                myAdapter2.getFilter().filter(s)
                myAdapter3.getFilter().filter(s)
                myAdapter4.getFilter().filter(s)
                myAdapter5.getFilter().filter(s)
                myAdapter6.getFilter().filter(s)
                Log.d(TAG, "SearchVies Text is changed : $s")
                return false
            }
        }

    /*this.onBackPressedDispatcher.addCallback(this, callback) //위에서 생성한 콜백 인스턴스 붙여주기*/

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