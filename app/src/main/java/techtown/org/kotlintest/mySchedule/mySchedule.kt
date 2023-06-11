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
import androidx.core.view.isVisible
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
    lateinit var myAdapter2: MyAdapter1
    lateinit var myAdapter3: OutRecyclerViewAdapter

    lateinit var dao: ScheduleDao
    lateinit var dao2: TravelDao
    lateinit var dao3: SuppliesDao
    lateinit var dao4: TodoDao


    val data = mutableListOf<ScheduleListData>()
    val datas2 = mutableListOf<ScheduleData>()

    lateinit var binding: ActivityMyScheduleBinding

    var travelDB = Firebase.database.reference.child("travel")

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

    var recommend = ScheduleData()
    var recommendList = arrayListOf<ScheduleData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //dao 초기화
        dao = ScheduleDao()
        dao2 = TravelDao()
        dao3 = SuppliesDao()
        dao4 = TodoDao()

        val layoutManager2 = LinearLayoutManager(this)
        binding.recRecycle.layoutManager=layoutManager2
        myAdapter2 = MyAdapter1(this, datas2)
        binding.recRecycle.adapter = myAdapter2
        binding.recRecycle.addItemDecoration(MyDecoration(this as Context))

        val layoutManager3 = LinearLayoutManager(this)
        binding.dayRecycle.layoutManager=layoutManager3
        myAdapter3 = OutRecyclerViewAdapter(this, data)
        binding.dayRecycle.adapter = myAdapter3
        binding.dayRecycle.addItemDecoration(MyDecoration2(this as Context))

        setSupportActionBar(binding.toolbar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //데이터 null체크
        if(intent.hasExtra("key") && intent.hasExtra("name")
            && intent.hasExtra("sDate") && intent.hasExtra("eDate")
            && intent.hasExtra("diffDay")) {

            //데이터 담기
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
            diffDay = intent.getIntExtra("diffDay", 0)!!
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

        setRecommendData()

        binding.fab.setOnClickListener(({
            val intent = Intent(this, AddActivity::class.java)

            intent.putExtra("uid", Uid)
            intent.putExtra("key", sKey)
            intent.putExtra("diffDay", diffDay)
            startActivity(intent)
        }))

        binding.btnSupplies.setOnClickListener(({
            val intent = Intent(this, SuppliesList::class.java)
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

        binding.btnRecommend.isSelected = true

        binding.btnRecommend.setOnClickListener {
            binding.btnRecommend.isSelected = !binding.btnRecommend.isSelected

            if (binding.btnRecommend.isSelected){
                binding.recRecycle.isVisible = true
            } else {
                binding.recRecycle.isVisible = false
            }
        }

        getScheduleList()
    }

    private fun getScheduleList(){
        dao.getScheduleList(Uid, sKey)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                data.clear()
                datas2.clear()

                data.apply {
                    for (i: Int in 1..diffDay) {
                        add( ScheduleListData(
                            sKey, "", i.toString(), mutableListOf()
                        )
                        )
                    }
                    /*myAdapter3.itemList = data
                    myAdapter3.notifyDataSetChanged()*/
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

                    if (schedule != null && schedule.day == "0"){
                        datas2.add(schedule)
                    }

                    for (id: Int in 0 until diffDay) {
                        if (schedule != null && data[id].scheduleDay == schedule.day) {
                            data[id].scheduleList.apply{ add(schedule) }
                            data[id].scheduleList.apply{ sortBy { it.time } }
                        }
                    }
                    //데이터 적용
                    myAdapter3.notifyDataSetChanged()
                    myAdapter2.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //디폴트 데이터 -> 추천목록
    private fun setRecommendData() {
        if (flags == 0){
            if (sPlace == "Tokyo"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Meiji Shrine", "", "", "1-1 Yoyogikamizonocho, Shibuya City, Tokyo 151-8557 Japan", 35.6766328849435, 139.6993902678707, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Takeshita Street Square", "", "", "1 Chome-16-4 Jingumae, 渋谷区 Shibuya City, Tokyo 150-0001 Japan", 35.671363973594495, 139.7043695948532, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shibuya Crossing", "", "", "Shibuya City, Tokyo, Japan", 35.65965198397277, 139.70057569331755, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Akihabara", "", "", "1 Chome Sotokanda, Chiyoda City, Tokyo, Japan", 35.698539804297646, 139.77305023718245, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Disneyland", "", "", "1-1 Maihama, Urayasu, Chiba 279-0031 Japan", 35.633131813157746, 139.88042648135968, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Odaiba", "", "", "1-chōme-4 Daiba, Minato City, Tokyo 135-0091 Japan", 35.63300141117911, 139.77848304555985, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ueno Zoo", "", "", "9-83 Uenokoen, Taito City, Tokyo 110-8711 Japan", 35.71663640142538, 139.7713176948559, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Sea Life Park", "", "", "6 Chome-2-3 Rinkaicho, Edogawa City, Tokyo 134-8587 Japan", 35.64010643478522, 139.86222733903247, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Asakusa", "", "", "1 Chome-1-3 Asakusa, Taito City, Tokyo 111-0032 Japan", 35.7109578913971, 139.79785865438149, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Skytree", "", "", "1 Chome-1-2 Oshiage, Sumida City, Tokyo 131-0045 Japan", 35.71029788562682, 139.81070039485562, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Disneyland", "", "", "1-1 Maihama, Urayasu, Chiba 279-0031 Japan", 35.633131813157746, 139.88042648135968, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Dome City", "", "", "1 Chome-3-61 Koraku, Bunkyo City, Tokyo 112-8575 Japan", 35.70456066156449, 139.75337798795846, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shibuya Crossing", "", "", "Shibuya City, Tokyo, Japan", 35.65965198397277, 139.70057569331755, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "TeamLab Borderless", "", "", "Japan 〒135-0064 Tokyo, Koto City, Aomi, 1 Chome−3−8 お台場パレットタウン２階", 35.62643854600497, 139.78294119485045, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tsukiji Fish Market", "", "", "4 Chome-13 Tsukiji, Chuo City, Tokyo 104-0045 Japan", 35.66551303522583, 139.7709934832151, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Golden Gai", "", "", "Japan 〒160-0021 Tokyo, Shinjuku City, Kabukicho, 1 Chome−1−6 2F", 35.69430346603984, 139.70477182369072, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Meiji Shrine", "", "", "1-1 Yoyogikamizonocho, Shibuya City, Tokyo 151-8557 Japan", 35.6766328849435, 139.6993902678707, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Tower", "", "", "4 Chome-2-8 Shibakoen, Minato City, Tokyo 105-0011 Japan", 35.65873738255573, 139.74530414881795, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shinjuku Gyoen National Garden", "", "", "11 Naitomachi, Shinjuku City, Tokyo 160-0014 Japan", 35.68539413044188, 139.71005169485403, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Odaiba Seaside Park", "", "", "1 Chome-4 Daiba, Minato City, Tokyo 135-0091 Japan", 35.63024061992273, 139.77570192368697, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chidorigafuchi Park", "", "", "Japan 〒102-0082 Tokyo, Chiyoda City, Kojimachi, 1 Chome−２", 35.68633395674825, 139.74496913718173, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Skytree", "", "", "1 Chome-1-2 Oshiage, Sumida City, Tokyo 131-0045 Japan", 35.71029788562682, 139.81070039485562, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ginza", "", "", "4 Chome-1-2 Ginza, Chuo City, Tokyo 104-0061 Japan", 35.67197387343831, 139.76439315516757, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Takeshita Street Square", "", "", "1 Chome-16-4 Jingumae, 渋谷区 Shibuya City, Tokyo 150-0001 Japan", 35.671904448027504, 139.70421088803326, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Asakusa", "", "", "1 Chome-1-3 Asakusa, Taito City, Tokyo 111-0032 Japan", 35.71102577567039, 139.79776726638534, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Edo-Tokyo Museum", "", "", "1 Chome-4-1 Yokoami, Sumida City, Tokyo 130-0015 Japan", 35.69664073787807, 139.79650607106194, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Tower", "", "", "4 Chome-2-8 Shibakoen, Minato City, Tokyo 105-0011 Japan", 35.65873738255573, 139.74530414881795, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Disneyland", "", "", "1-1 Maihama, Urayasu, Chiba 279-0031 Japan", 35.633131813157746, 139.88042648135968, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Asakusa", "", "", "1 Chome-1-3 Asakusa, Taito City, Tokyo 111-0032 Japan", 35.7109578913971, 139.79785865438149, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tokyo Skytree", "", "", "1 Chome-1-2 Oshiage, Sumida City, Tokyo 131-0045 Japan", 35.71029788562682, 139.81070039485562, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Fukuoka"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Tower", "", "", "2 Chome-3-26 Momochihama, Sawara Ward, Fukuoka, 814-0001 Japan", 33.59342756465011, 130.35154218124111, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ohori Park", "", "", "Japan 〒810-0051 Fukuoka, Chuo Ward, Ohorikoen, 公園管理事務所", 33.58645672859308, 130.3765289677493, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Canal City Hakata", "", "", "1 Chome-2 Sumiyoshi, Hakata Ward, Fukuoka, 812-0018 Japan", 33.5899909943274, 130.41111352356845, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nakasu", "", "", "2 Nakasu, Hakata Ward, Fukuoka, 810-0801 Japan", 33.5926903140698, 130.4074206696031, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dazaifu Tenmangu Shrine", "", "", "4 Chome-7-1 Saifu, Dazaifu, Fukuoka 818-0117 Japan", 33.52158433895024, 130.5347380640387, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nokonoshima Island", "", "", "Japan 〒819-0012 Fukuoka, Nishi Ward, 能古島", 33.63150776752718, 130.30161606404488, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka City Science Museum", "", "", "4 Chome-2-1 Ropponmatsu, Chuo Ward, Fukuoka, 810-0044 Japan", 33.57771378562816, 130.37818837938667, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Yahuoku! Dome", "", "", "2 Chome-2-2 Jigyohama, Chuo Ward, Fukuoka, 810-8660 Japan", 33.59559971829321, 130.36224121193092, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kushida Shrine", "", "", "1-41 Kamikawabatamachi, Hakata Ward, Fukuoka, 812-0026 Japan", 33.59312437616444, 130.41038379287892, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Art Museum", "", "", "1-6 Ohorikoen, Chuo Ward, Fukuoka, 810-0051 Japan", 33.58431551767246, 130.37967179473196, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Tower", "", "", "2 Chome-3-26 Momochihama, Sawara Ward, Fukuoka, 814-0001 Japan", 33.59342756465011, 130.35154218124111, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tenjin Central Park", "", "", "1 Chome-1 Tenjin, Chuo Ward, Fukuoka, 810-0001 Japan", 33.59107900624001, 130.40301034258056, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yanagibashi Rengo Market", "", "", "1 Chome-5 Haruyoshi, Chuo Ward, Fukuoka, 810-0003 Japan", 33.583202823911655, 130.40845948124047, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hakata Traditional Craft and Design Museum", "", "", "3 Chome-1-1 Momochihama, Sawara Ward, Fukuoka, 814-0001 Japan", 33.58961169519196, 130.3540297254218, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hakata Ramen", "", "", "9-151 Kamikawabatamachi, Hakata Ward, Fukuoka, 812-0026 Japan", 33.59374034407719, 130.40868860866237, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yatai Street Food Stalls", "", "", "Japan 〒810-0801 Fukuoka, Hakata Ward, Nakasu, 8, 那珂川 通り", 33.59107186686672, 130.40812259488527, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ohori Park", "", "", "Japan 〒810-0051 Fukuoka, Chuo Ward, Ohorikoen, 公園管理事務所", 33.58645672859308, 130.3765289677493, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dazaifu Tenmangu Shrine", "", "", "4 Chome-7-1 Saifu, Dazaifu, Fukuoka 818-0117 Japan", 33.52158433895024, 130.5347380640387, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nanzoin Temple", "", "", "1035 Sasaguri, Kasuya District, Fukuoka 811-2405 Japan", 33.620034525815385, 130.57302121007876, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Uminonakamichi Seaside Park", "", "", "18-25 Saitozaki, Higashi Ward, Fukuoka, 811-0321 Japan", 33.66453743146941, 130.36159999473645, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nokonoshima Island", "", "", "Japan 〒819-0012 Fukuoka, Nishi Ward, 能古島", 33.63150776752718, 130.30161606404488, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Keya no Oto", "", "", "Shimakeya, Itoshima, Fukuoka 819-1335 일본", 33.585795557164694, 130.10838498124065, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tenjin Underground Shopping Mall", "", "", "Japan 〒810-0001 Fukuoka, Chuo Ward, Tenjin, 2 Chome, 地下1・2・3号", 33.5901468447134, 130.3996082830943, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Canal City Hakata", "", "", "1 Chome-2 Sumiyoshi, Hakata Ward, Fukuoka, 812-0018 Japan", 33.5899909943274, 130.41111352356845, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kushida Shrine", "", "", "1-41 Kamikawabatamachi, Hakata Ward, Fukuoka, 812-0026 Japan", 33.59312437616444, 130.41038379287892, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Art Museum", "", "", "1-6 Ohorikoen, Chuo Ward, Fukuoka, 810-0051 Japan", 33.58431551767246, 130.37967179473196, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Fukuoka Tower", "", "", "2 Chome-3-26 Momochihama, Sawara Ward, Fukuoka, 814-0001 Japan", 33.59342756465011, 130.35154218124111, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kushida Shrine", "", "", "1-41 Kamikawabatamachi, Hakata Ward, Fukuoka, 812-0026 Japan", 33.59312437616444, 130.41038379287892, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ohori Park", "", "", "Japan 〒810-0051 Fukuoka, Chuo Ward, Ohorikoen, 公園管理事務所", 33.58645672859308, 130.3765289677493, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nokonoshima Island", "", "", "Japan 〒819-0012 Fukuoka, Nishi Ward, 能古島", 33.63150776752718, 130.30161606404488, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Osaka"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dotonbori", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.66891750018655, 135.501297094794, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shinsekai", "", "", "Japan 〒556-0002 Osaka, Naniwa Ward, Ebisuhigashi, 2 Chome−5−1 ニューマルコ", 34.65177340301096, 135.5060688528434, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Universal Studios Japan", "", "", "2-chōme-1 Sakurajima, Konohana Ward, Osaka, 554-0031 Japan", 34.66931020351604, 135.43270567944936, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Umeda Sky Building", "", "", "1 Chome-1-88 Oyodonaka, Kita Ward, Osaka, 531-6023 Japan", 34.70544593418838, 135.48966342363244, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Aquarium Kaiyukan", "", "", "1 Chome-1-10 Kaigandori, Minato Ward, Osaka, 552-0022 Japan", 34.654712333473256, 135.4289430371208, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Bay Cruise", "", "", "1 Tenmabashikyomachi, Chuo Ward, Osaka, 540-0032 Japan", 34.69187984803593, 135.51557845447138, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Legoland Discovery Center", "", "", "Japan 〒552-0022 Osaka, Minato Ward, Kaigandori, 1 Chome−1−10 天保山マーケットプレース3階", 34.65639078017338, 135.4303413543192, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tennoji Zoo", "", "", "1-108 Chausuyamacho, Tennoji Ward, Osaka, 543-0063 Japan", 34.650939594105225, 135.50905462362925, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sumiyoshi Taisha Shrine", "", "", "2 Chome-9-89 Sumiyoshi, Sumiyoshi Ward, Osaka, 558-0045 Japan", 34.612562242238155, 135.49383629664425, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shitennoji Temple", "", "", "1 Chome-11-18 Shitennoji, Tennoji Ward, Osaka, 543-0051 Japan", 34.654643408133886, 135.51665264082774, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Universal Studios Japan", "", "", "2-chōme-1 Sakurajima, Konohana Ward, Osaka, 554-0031 Japan", 34.66931020351604, 135.43270567944936, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Aquarium Kaiyukan", "", "", "1 Chome-1-10 Kaigandori, Minato Ward, Osaka, 552-0022 Japan", 34.654712333473256, 135.4289430371208, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dotonbori", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.66891750018655, 135.501297094794, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Umeda Sky Building", "", "", "1 Chome-1-88 Oyodonaka, Kita Ward, Osaka, 531-6023 Japan", 34.70544593418838, 135.48966342363244, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kuromon Ichiba Market", "", "", "2 Chome Nipponbashi, Chuo Ward, Osaka, 542-0073 Japan", 34.66550568647107, 135.5062044371215, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dotonbori", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.66891750018655, 135.501297094794, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Castle", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.68742912895057, 135.5258170491365, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tsutenkaku Tower", "", "", "1 Chome-18-6 Ebisuhigashi, Naniwa Ward, Osaka, 556-0002 Japan", 34.652772769360325, 135.50628433712083, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Spa World", "", "", "3 Chome-4-24 Ebisuhigashi, Naniwa Ward, Osaka, 556-0002 Japan", 34.65005291445019, 135.50561852362915, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nakanoshima Park", "", "", "1 Chome-1 Nakanoshima, Kita Ward, Osaka, 530-0005 Japan", 34.692004905593144, 135.50983700453875, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Mint Bureau", "", "", "1 Chome-1-79 Tenma, Kita Ward, Osaka, 530-0043 Japan", 34.697346554485655, 135.5211695947957, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Castle Park", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.686700224763854, 135.52634014082966, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shinsaibashi Shopping Street", "", "", "2 Chome-2-22 Shinsaibashisuji, Chuo Ward, Osaka, 542-0085 Japan", 34.671117595030026, 135.50157763712187, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Namba Parks", "", "", "2 Chome-10-70 Nanbanaka, Naniwa Ward, Osaka, 556-0011 Japan", 34.66175829325721, 135.5019670813024, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Museum of History", "", "", "4 Chome-1-32 Otemae, Chuo Ward, Osaka, 540-0008 Japan", 34.68294932719611, 135.52081895246724, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sumiyoshi Taisha Shrine", "", "", "2 Chome-9-89 Sumiyoshi, Sumiyoshi Ward, Osaka, 558-0045 Japan", 34.612562242238155, 135.49377192362698, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dotonbori", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.66891750018655, 135.501297094794, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Umeda Sky Building", "", "", "1 Chome-1-88 Oyodonaka, Kita Ward, Osaka, 531-6023 Japan", 34.70544593418838, 135.48966342363244, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osaka Castle", "", "", "1-1 Osakajo, Chuo Ward, Osaka, 540-0002 Japan", 34.68742912895057, 135.5258170491365, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Universal Studios Japan", "", "", "2-chōme-1 Sakurajima, Konohana Ward, Osaka, 554-0031 Japan", 34.66931020351604, 135.43270567944936, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Shizuoka"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Fuji", "", "", "Japan 〒418-0112 Kitayama, Fujinomiya City, Shizuoka Prefecture", 35.36237315487689, 138.72719140600776, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Miho no Matsubara", "", "", "1338-45 Miho, Shimizu Ward, Shizuoka, 424-0901 Japan", 34.99437555593832, 138.5227799966666, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shimizu Port", "", "", "1-1 Masagocho, Shimizu Ward, Shizuoka, 424-0816 Japan", 35.02433690251369, 138.4891615039599, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nihondaira Ropeway", "", "", "Japan Shizuoka, 日本平ロープウェイ", 34.97359477948543, 138.4638429361045, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Izu Peninsula Geopark", "", "", "1638 Suzaki, Shimoda, Shizuoka 415-0014 Japan", 34.65269638961546, 138.96503059664667, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kunozan Toshogu Shrine", "", "", "390 Negoya, Suruga Ward, Shizuoka, 422-8011 Japan", 34.96506336512667, 138.46751662179412, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nihondaira Zoo", "", "", "1767-6 Ikeda, Suruga Ward, Shizuoka, 422-8005 Japan", 34.979998340400954, 138.44008877946743, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Miho Masaki Beach", "", "", "2375 Miho, Shimizu Ward, Shizuoka, 424-0901 Japan", 35.02006015830648, 138.52123527975382, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sunpu Castle Park", "", "", "1-1 Sunpujokoen, Aoi Ward, Shizuoka, 420-0855 Japan", 34.9792178424957, 138.3830570217949, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka Sengen Shrine", "", "", "102-1 Miyagasakicho, Aoi Ward, Shizuoka, 420-0868 Japan", 34.98399424909644, 138.37617948317458, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Fuji", "", "", "Japan 〒418-0112 Kitayama, Fujinomiya City, Shizuoka Prefecture", 35.36237315487689, 138.72719140600776, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shiraito Falls", "", "", "Japan 〒418-0103 Kamiide, Fujinomiya City, Shizuoka Prefecture", 35.31528361979638, 138.58709384444649, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Miho no Matsubara", "", "", "1338-45 Miho, Shimizu Ward, Shizuoka, 424-0901 Japan", 34.99437555593832, 138.5227799966666, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Numazu Port and Fish Market", "", "", "1905-29 Hon, Numazu, Shizuoka 410-0867 Japan", 35.08190757910857, 138.85417535615375, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Numazu Port and Fish Market", "", "", "1905-29 Hon, Numazu, Shizuoka 410-0867 Japan", 35.08190757910857, 138.85417535615375, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kunozan Toshogu Shrine", "", "", "390 Negoya, Suruga Ward, Shizuoka, 422-8011 Japan", 34.96498423440172, 138.4676024524838, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sunpu Castle Park", "", "", "1-1 Sunpujokoen, Aoi Ward, Shizuoka, 420-0855 Japan", 34.9792178424957, 138.3830570217949, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Atami Onsen", "", "", "11-11 Tawarahoncho, Atami, Shizuoka 413-0011 Japan", 35.10400894272969, 139.07777680645748, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kawazu Cherry Blossom", "", "", "48-6 Sasahara, Kawazu, Kamo District, Shizuoka 413-0512 Japan", 34.754189481040065, 138.98697292178184, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Izu Peninsula Geopark", "", "", "1638 Suzaki, Shimoda, Shizuoka 415-0014 Japan", 34.65269638961546, 138.96503059664667, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka Sengen Shrine", "", "", "102-1 Miyagasakicho, Aoi Ward, Shizuoka, 420-0868 Japan", 34.98399424909644, 138.37617948317458, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka Shimizu Fish Market", "", "", "149 Shimazakicho, Shimizu Ward, Shizuoka, 424-0823 Japan", 35.021480152666, 138.4906403506336, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Minato Yokocho", "", "", "2-chōme-17 Mochimune, Suruga Ward, Shizuoka, 421-0122 Japan", 34.924281589846814, 138.36618085248145, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nihondaira Ropeway", "", "", "Japan Shizuoka, 日本平ロープウェイ", 34.97359477948543, 138.4638429361045, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka City Tokaido Hiroshige Museum of Art", "", "", "297-1 Yui, Shimizu Ward, Shizuoka, 421-3103 Japan", 35.10821381877405, 138.56703489296623, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Izu Peninsula Geopark", "", "", "1638 Suzaki, Shimoda, Shizuoka 415-0014 Japan", 34.65269638961546, 138.96503059664667, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka Shimizu Fish Market", "", "", "149 Shimazakicho, Shimizu Ward, Shizuoka, 424-0823 Japan", 35.021480152666, 138.4906403506336, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shizuoka Sengen Shrine", "", "", "102-1 Miyagasakicho, Aoi Ward, Shizuoka, 420-0868 Japan", 34.98399424909644, 138.37617948317458, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Fuji", "", "", "Japan 〒418-0112 Kitayama, Fujinomiya City, Shizuoka Prefecture", 35.36237315487689, 138.72719140600776, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Nagoya"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Castle", "", "", "1-1 Honmaru, Naka Ward, Nagoya, Aichi 460-0031 Japan", 35.184960520195844, 136.89968829482427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osu Shopping District", "", "", "Osu, Naka Ward, Nagoya, Aichi 460-0011 Japan", 35.159194215468936, 136.903436023659, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osu Shopping District", "", "", "Osu, Naka Ward, Nagoya, Aichi 460-0011 Japan", 35.159194215468936, 136.903436023659, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya MIRAI TOWER", "", "", "3 Chome-6-１５先 Nishiki, Naka Ward, Nagoya, Aichi 460-0003 Japan", 35.172564422292616, 136.90831653715114, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Atsuta Shrine", "", "", "1 Chome-1-1 Jingu, Atsuta Ward, Nagoya, Aichi 456-8585 Japan", 35.12755091907922, 136.90874843900198, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sakae District", "", "", "3 Chome-5-12先 Sakae, Naka Ward, Nagoya, Aichi 460-0008 Japan", 35.170170255993575, 136.90856438133204, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Port Aquarium", "", "", "1-3 Minatomachi, Minato Ward, Nagoya, Aichi 455-0033 Japan", 35.09071068519327, 136.87840550831018, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "LEGOLAND Japan", "", "", "LEGOLAND Japan Limited, 1 Kinjoufutou, Nagoya-shi, 2 Chome-2, Minato Ward, Aichi 455-8605 Japan", 35.0508084839509, 136.8437541929629, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Castle", "", "", "1-1 Honmaru, Naka Ward, Nagoya, Aichi 460-0031 Japan", 35.184960520195844, 136.89968829482427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya City Science Museum", "", "", "Japan 〒460-0008 Aichi, Nagoya, Naka Ward, Sakae, 2 Chome−17−1 芸術と科学の杜・白川公園内", 35.16540129091891, 136.89960603529724, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "SCMAGLEV and Railway Park", "", "", "3 Chome-2-2 Kinjofuto, Minato Ward, Nagoya, Aichi 455-0848 Japan", 35.04928768809442, 136.85096883899737, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Port Aquarium", "", "", "1-3 Minatomachi, Minato Ward, Nagoya, Aichi 455-0033 Japan", 35.09071068519327, 136.87840550831018, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Castle", "", "", "1-1 Honmaru, Naka Ward, Nagoya, Aichi 460-0031 Japan", 35.184960520195844, 136.89968829482427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sakae Oasis 21", "", "", "1 Chome-11-1 Higashisakura, Higashi Ward, Nagoya, Aichi 461-0005 Japan", 35.17111365605174, 136.9096097083149, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Meshiya", "", "", "2 Chome-9-4 Kamimaezu, Naka Ward, Nagoya, Aichi 460-0013 Japan", 35.15575466761879, 136.90588358133115, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Central Market Comprehensive Food Center", "", "", "4 Chome-15-2 Meieki, Nakamura Ward, Nagoya, Aichi 450-0002 Japan", 35.17155046830535, 136.88801825447135, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osu Kannon Temple", "", "", "2 Chome-21-47 Osu, Naka Ward, Nagoya, Aichi 460-0011 Japan", 35.159873371057586, 136.89941859482275, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya MIRAI TOWER", "", "", "3 Chome-6-１５先 Nishiki, Naka Ward, Nagoya, Aichi 460-0003 Japan", 35.172564422292616, 136.90831653715114, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shirotori Garden", "", "", "2-5 Atsuta Nishimachi, Atsuta Ward, Nagoya, Aichi 456-0036 Japan", 35.12622842232604, 136.90112395249324, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Atsuta Shrine", "", "", "1 Chome-1-1 Jingu, Atsuta Ward, Nagoya, Aichi 456-8585 Japan", 35.12755091907922, 136.90874843900198, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Port Bluebonnet Wildflower Garden", "", "", "42 Shiomicho, Minato Ward, Nagoya, Aichi 455-0028 Japan", 35.07150102193538, 136.880119303362, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sakae District", "", "", "3 Chome-5-12先 Sakae, Naka Ward, Nagoya, Aichi 460-0008 Japan", 35.170170255993575, 136.90856438133204, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osu Shopping District", "", "", "Osu, Naka Ward, Nagoya, Aichi 460-0011 Japan", 35.159194215468936, 136.903436023659, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya City Museum", "", "", "1 Chome-27-1 Mizuhotori, Mizuho Ward, Nagoya, Aichi 467-0806 Japan", 35.13648790235991, 136.93497683900247, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Castle", "", "", "1-1 Honmaru, Naka Ward, Nagoya, Aichi 460-0031 Japan", 35.184960520195844, 136.89968829482427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya Castle", "", "", "1-1 Honmaru, Naka Ward, Nagoya, Aichi 460-0031 Japan", 35.184960520195844, 136.89968829482427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sakae District", "", "", "3 Chome-5-12先 Sakae, Naka Ward, Nagoya, Aichi 460-0008 Japan", 35.170170255993575, 136.90856438133204, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Osu Shopping District", "", "", "Osu, Naka Ward, Nagoya, Aichi 460-0011 Japan", 35.159194215468936, 136.903436023659, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nagoya MIRAI TOWER", "", "", "3 Chome-6-１５先 Nishiki, Naka Ward, Nagoya, Aichi 460-0003 Japan", 35.172564422292616, 136.90831653715114, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Okinawa"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shuri Castle", "", "", "1 Chome-2 Shurikinjocho, Naha, Okinawa 903-0815 Japan", 26.21724700449975, 127.71938673483511, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yachimun Street", "", "", "1 Chome-16 Tsuboya, Naha, Okinawa 902-0065 Japan", 26.213266535047907, 127.69125593668836, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Churaumi Aquarium", "", "", "424 Ishikawa, Motobu, Kunigami District, Okinawa 905-0206 Japan", 26.694510512164683, 127.8779916367099, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Sunset Beach", "", "", "8Q74+C4, Mihama, 北谷町 沖縄県 Japan", 26.313716350905818, 127.7553232232014, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cape Manzamo", "", "", "Onna, Kunigami District, Okinawa 904-0411 Japan", 26.505160302799624, 127.85017768088233, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Taketomi Island", "", "", "Japan 〒907-1101 Taketomi Taketomi, Yaeyama District, Okinawa Prefecture", 24.326856084128938, 124.08911632782925, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa World", "", "", "Maekawa-1336 Tamagusuku, Nanjo, Okinawa 901-0616 Japan", 26.140731094546663, 127.7489876097025, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Zoo and Museum", "", "", "5 Chome-7-1 Goya, Okinawa, 904-0021 Japan", 26.327546764490037, 127.8034154636762, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shikinaen Garden", "", "", "421-7 Maaji, Naha, Okinawa 902-0072 Japan", 26.204056827416526, 127.71538153854141, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ryukyu Village", "", "", "1130 Yamada, Onna, Kunigami District, Okinawa 904-0416 Japan", 26.429689948901903, 127.7751946213531, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cape Maeda", "", "", "469-1 Maeda, Onna, Kunigami District, Okinawa 904-0417 Japan", 26.44621260492948, 127.7720256523793, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yanbaru Forest", "", "", "Hentona, Kunigami, Kunigami District, Okinawa 905-1411 Japan", 26.72640373711499, 128.22743350713478, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Churaumi Aquarium", "", "", "424 Ishikawa, Motobu, Kunigami District, Okinawa 905-0206 Japan", 26.694510512164683, 127.8779916367099, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kabira Bay", "", "", "Ishigaki, Okinawa, Japan", 24.461368063044375, 124.14342049243272, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Naha City Makishi Public Market", "", "", "2 Chome-10-1 Matsuo, Naha, Okinawa 900-0014 Japan", 26.21478701739623, 127.68843970519552, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yachimun Street", "", "", "1 Chome-16 Tsuboya, Naha, Okinawa 902-0065 Japan", 26.213266535047907, 127.69125593668836, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shuri Castle", "", "", "1 Chome-2 Shurikinjocho, Naha, Okinawa 903-0815 Japan", 26.21724700449975, 127.71938673483511, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Peace Memorial Park", "", "", "444 Mabuni, Itoman, Okinawa 901-0333 Japan", 26.095321015927585, 127.7235049808642, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Sunset Beach", "", "", "8Q74+C4, Mihama, 北谷町 沖縄県 Japan", 26.313716350905818, 127.7553232232014, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yomitan Pottery Village", "", "", "2653-1番地 Zakimi, Yomitan, Nakagami District, Okinawa 904-0301 Japan", 26.411012032787568, 127.75268684811442, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cape Manzamo", "", "", "Onna, Kunigami District, Okinawa 904-0411 Japan", 26.505160302799624, 127.85017768088233, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Churaumi Aquarium", "", "", "424 Ishikawa, Motobu, Kunigami District, Okinawa 905-0206 Japan", 26.694510512164683, 127.8779916367099, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yachimun Street", "", "", "1 Chome-16 Tsuboya, Naha, Okinawa 902-0065 Japan", 26.213266535047907, 127.69125593668836, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "American Village", "", "", "Mihama, Chatan, Nakagami District, Okinawa 904-0115 Japan", 26.31711626323653, 127.7579039385465, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Prefectural Museum and Art Museum", "", "", "3 Chome-1-1 Omoromachi, Naha, Okinawa 900-0006 Japan", 26.227467765331973, 127.69389180970626, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tamaudun Mausoleum", "", "", "Japan 〒903-0815 Okinawa, Naha, Shurikinjocho, 1 Chome−３", 26.21855750230769, 127.7146832213438, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Yachimun Street", "", "", "1 Chome-16 Tsuboya, Naha, Okinawa 902-0065 Japan", 26.213266535047907, 127.69125593668836, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cape Manzamo", "", "", "Onna, Kunigami District, Okinawa 904-0411 Japan", 26.505160302799624, 127.85017768088233, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Okinawa Churaumi Aquarium", "", "", "424 Ishikawa, Motobu, Kunigami District, Okinawa 905-0206 Japan", 26.694510512164683, 127.8779916367099, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shuri Castle", "", "", "1 Chome-2 Shurikinjocho, Naha, Okinawa 903-0815 Japan", 26.21724700449975, 127.71938673483511, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Sapporo"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Odori Park", "", "", "Japan 〒060-0042 Hokkaido, Sapporo, Chuo Ward, Odorinishi, 2 Chome, 地下街 オーロラタウン", 43.06102841398017, 141.35491459533688, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sapporo Beer Museum", "", "", "9 Chome-1-1 Kita 7 Johigashi, Higashi Ward, Hokkaido 065-8633 Japan", 43.071615982218404, 141.36894458184622, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanuki Koji Shopping Arcade", "", "", "Japan 〒060-0062 Hokkaido, Sapporo, Chuo Ward, 南2・3条西1～7丁目", 43.05765250405054, 141.3507973118235, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Moerenuma Park", "", "", "1-1 Moerenumakoen, Higashi Ward, Sapporo, Hokkaido 007-0011 Japan", 43.12333707227961, 141.42657849348782, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mt. Moiwa Ropeway", "", "", "Japan 〒005-0041 Hokkaido, Sapporo, Minami Ward, Moiwayama, もいわ中腹駅", 43.02226197708586, 141.3256487567135, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Otaru Canal", "", "", "Japan 〒047-0031 Hokkaido, Otaru, Ironai, 1 Chome−1−12 小樽運河ターミナル 1F", 43.19727890466159, 141.00235662417674, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "SAPPORO SNOW VISION", "", "", "Japan 〒060-0042 Hokkaido, Sapporo, Chuo Ward, Odorinishi, 4 Chome, 地下鉄南北線大通駅コンコース", 43.05995567161565, 141.35218729533673, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sapporo Science Center", "", "", "Japan 〒004-0051 Hokkaido, Sapporo, Atsubetsu Ward, Atsubetsuchuo 1 Jo, 5 Chome−2−20 札幌市青少年科学館", 43.036252226153906, 141.47224365300747, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Historic Village of Hokkaido", "", "", "Konopporo-50-1 Atsubetsucho, Atsubetsu Ward, Sapporo, Hokkaido 004-0006 Japan", 43.04838067907407, 141.49697403766353, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shiroi Koibito Park", "", "", "2 Chome-11-36 Miyanosawa 2 Jo, Nishi Ward, Sapporo, Hokkaido 063-0052 Japan", 43.089133632552155, 141.2717578395198, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mt. Moiwa", "", "", "Japan 〒005-0041 Mt. Moiwa, Minami Ward, Sapporo City, Hokkaido", 43.02477229542105, 141.32245313019672, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nakajima Park", "", "", "1 Nakajimakoen, Chuo Ward, Sapporo, Hokkaido 064-0931 Japan", 43.044708315549634, 141.35446833951664, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sapporo TV Tower", "", "", "1 Chome Odorinishi, Chuo Ward, Sapporo, Hokkaido 060-0042 Japan", 43.06124576864669, 141.35639240882824, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanuki Koji Shopping Arcade", "", "", "Japan 〒060-0062 Hokkaido, Sapporo, Chuo Ward, 南2・3条西1～7丁目", 43.05765250405054, 141.3507973118235, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sapporo Ramen Alley", "", "", "Japan 〒064-0805 Hokkaido, Sapporo, Chuo Ward, Minami 5 Jonishi, 3 Chome−８−9 Ｎ・グランデビル 1F", 43.05492288319736, 141.354365937664, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nijo Market", "", "", "Japan 〒060-0052 Hokkaido, Sapporo, Chuo Ward, Minami 3 Johigashi, 1 Chome, 〜２丁目", 43.05867579184903, 141.35861363766426, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Odori Park", "", "", "Japan 〒060-0042 Hokkaido, Sapporo, Chuo Ward, Odorinishi, 2 Chome, 地下街 オーロラタウン", 43.06102841398017, 141.35491459533688, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hokkaido Shrine", "", "", "474 Miyagaoka, Chuo Ward, Sapporo, Hokkaido 064-8505 Japan", 43.0544819238432, 141.30785716835365, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jozankei Onsen", "", "", "Japan 〒061-2302 Jozankei Onsen Higashi 4, Minami Ward, Sapporo City, Hokkaido", 42.964517531654906, 141.1629672816957, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Maruyama Park", "", "", "Japan 〒064-0959 Hokkaido, Sapporo, Chuo Ward, Miyagaoka, ３", 43.055956935836825, 141.31258583766413, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Shikotsu-Toya National Park", "", "", "Minami Ward, ニセコ町 Abuta District, Hokkaido 048-1541 Japan", 42.727544203345126, 141.2761654953133, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Moerenuma Park", "", "", "1-1 Moerenumakoen, Higashi Ward, Sapporo, Hokkaido 007-0011 Japan", 43.12333707227961, 141.42657849348782, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanuki Koji Shopping Arcade", "", "", "Japan 〒060-0062 Hokkaido, Sapporo, Chuo Ward, 南2・3条西1～7丁目", 43.05765250405054, 141.3507973118235, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sapporo Factory Atrium", "", "", "4 Chome Kita 2 Johigashi, Chuo Ward, Sapporo, Hokkaido 060-0032 Japan", 43.06551449704273, 141.36313977020782, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Historic Village of Hokkaido", "", "", "Konopporo-50-1 Atsubetsucho, Atsubetsu Ward, Sapporo, Hokkaido 004-0006 Japan", 43.04838067907407, 141.49697403766353, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hokkaido Museum", "", "", "Konopporo-53-2 Atsubetsucho, Atsubetsu Ward, Sapporo, Hokkaido 004-0006 Japan", 43.05331428581083, 141.49671863951733, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanuki Koji Shopping Arcade", "", "", "Japan 〒060-0062 Hokkaido, Sapporo, Chuo Ward, 南2・3条西1～7丁目", 43.05765250405054, 141.3507973118235, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Moerenuma Park", "", "", "1-1 Moerenumakoen, Higashi Ward, Sapporo, Hokkaido 007-0011 Japan", 43.12333707227961, 141.42657849348782, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Odori Park", "", "", "Japan 〒060-0042 Hokkaido, Sapporo, Chuo Ward, Odorinishi, 2 Chome, 地下街 オーロラタウン", 43.06102841398017, 141.35491459533688, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Historic Village of Hokkaido", "", "", "Konopporo-50-1 Atsubetsucho, Atsubetsu Ward, Sapporo, Hokkaido 004-0006 Japan", 43.04838067907407, 141.49697403766353, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Bangkok"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Arun", "", "", "158 Thanon Wang Doem, Wat Arun, Bangkok Yai, Bangkok 10600 Thailand", 13.744198678929834, 100.48848690929394, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chatuchak Weekend Market", "", "", "Min Buri, Bangkok 10510 Thailand", 13.815991147689775, 100.72512612144115, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Khao San Road", "", "", "Pak Kret, Pak Kret District, Nonthaburi 11120 Thailand", 13.915935093255435, 100.49542519580633, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chatuchak Weekend Market", "", "", "Min Buri, Bangkok 10510 Thailand", 13.815991147689775, 100.72512612144115, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chao Phraya", "", "", "Mueang Ang Thong District, Ang Thong 14000 Thailand", 14.5909802221247, 100.46294145905449, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jim Thompson House", "", "", "J6 Kasem San 2 Alley, Wang Mai, Pathum Wan, Bangkok 10330 Thailand", 13.749436671778561, 100.52831977278545, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sea Life Bangkok Ocean World", "", "", "ชั้น บี1-บี2 สยามพารากอน 991 Rama I Rd, Pathum Wan, Bangkok 10330 Thailand", 13.74621543119793, 100.53522167067337, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Siam Park City", "", "", "203 Suan Sayam Rd, Khan Na Yao, Bangkok 10230 Thailand", 13.806907763273573, 100.69240539580402, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Phra Kaew", "", "", "QF2V+M34, Na Phra Lan Rd, Phra Borom Maha Ratchawang, Phra Nakhon, Bangkok 10200 Thailand", 13.75183016806806, 100.49281874765204, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Damnoen Saduak Floating Market", "", "", "Damnoen Saduak, Damnoen Saduak District, Ratchaburi 70130 Thailand", 13.520470900548002, 99.95857220743572, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bang Krachao", "", "", "MH62+73J, Song Khanong, Phra Pradaeng District, Samut Prakan 10130 Thailand", 13.660974645137312, 100.55025125347325, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Unicorn Café", "", "", "44/1 Soi Sathon 8, Silom, Bang Rak, Bangkok 10500 Thailand", 13.72364889244135, 100.53138658045734, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Talad Neon Night Market", "", "", "1087, 1 Phetchaburi Rd, Makkasan, Ratchathewi, Bangkok 10400 Thailand", 13.750019099582602, 100.54374807860444, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chinatown Food Tour", "", "", "PGR5+4W6 ถ. เยาวราช Khwaeng Samphanthawong, Khet Samphanthawong, Bangkok 10100 Thailand", 13.740529709723976, 100.50976769394907, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Floating Market Tour", "", "", "PFVV+44X, Phra Borom Maha Ratchawang, Phra Nakhon, Bangkok 10200 Thailand", 13.74322982011477, 100.4928150786043, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Pho", "", "", "2 Sanam Chai Rd, Phra Borom Maha Ratchawang, Phra Nakhon, Bangkok 10200 Thailand", 13.7466529601223, 100.49273037860435, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "The Golden Mount", "", "", "344 Thanon Chakkraphatdi Phong, Ban Bat, Pom Prap Sattru Phai, Bangkok 10100 Thailand", 13.754113795204395, 100.5064815209321, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chao Phraya River", "", "", "Pak Nam, Mueang Samut Prakan District, Samut Prakan 10270 Thailand", 13.591938806616325, 100.59992079079439, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Lumpini Park", "", "", "Lumphini, Pathum Wan, Bangkok 10330 Thailand", 13.731573997308884, 100.54169839394886, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chatuchak Weekend Market", "", "", "Min Buri, Bangkok 10510 Thailand", 13.815991147689775, 100.72512612144115, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Siam Paragon", "", "", "991 Rama I Rd, Pathum Wan, Bangkok 10330 Thailand", 13.746512046404277, 100.53464363442329, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chatuchak Weekend Market", "", "", "Min Buri, Bangkok 10510 Thailand", 13.815991147689775, 100.72512612144115, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Siam Park City", "", "", "203 Suan Sayam Rd, Khan Na Yao, Bangkok 10230 Thailand", 13.806907763273573, 100.69240539580402, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Arun", "", "", "158 Thanon Wang Doem, Wat Arun, Bangkok Yai, Bangkok 10600 Thailand", 13.744198678929834, 100.48848690929394, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Talad Neon Night Market", "", "", "1087, 1 Phetchaburi Rd, Makkasan, Ratchathewi, Bangkok 10400 Thailand", 13.750019099582602, 100.54374807860444, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Bali"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ubud Monkey Forest", "", "", "F7J6+889, Ubud, Kecamatan Ubud, Kabupaten Gianyar, Bali 80571 Indonesia", -8.519011082292284, 115.2608905804416, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tegalalang Rice Terrace", "", "", "Jl. Raya Tegallalang, Tegallalang, Kec. Tegallalang, Kabupaten Gianyar, Bali 80561 Indonesia", -8.43154138558023, 115.27926007858638, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Seminyak Beach", "", "", "Gg. Drona No.1, Seminyak, Kec. Kuta, Kabupaten Badung, Bali, Indonesia", -8.69198120615366, 115.15812613626416, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanah Lot Temple", "", "", "93HP+GPH, Kabupaten Tabanan, Bali 82121 Indonesia", -8.620964839092593, 115.08685826509887, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Uluwatu Temple", "", "", "Pecatu, South Kuta, Badung District, Bali, Indonesia, ", -8.82891136960229, 115.08498970372375, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nusa Penida Island", "", "", "Sampalan Batununggul 11 Nusapenida, Kec. Nusa Penida, Bali 80771 Indonesia", -8.677932347097569, 115.57256789393627, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Waterbom Bali", "", "", "Jl. Kartika Plaza, Tuban, Kec. Kuta, Kabupaten Badung, Bali 80361 Indonesia", -8.72841321798985, 115.16934363811835, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bali Safari and Marine Park", "", "", "Jl. Prof. Dr. Ida Bagus Mantra No.Km. 19, Serongga, Kec. Gianyar, Kabupaten Gianyar, Bali 80551 Indonesia", -8.580756034086386, 115.34530879578767, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tirta Empul Temple", "", "", "Jl. Raya Tegallalang No.5758, Tegallalang, Kec. Tegallalang, Kabupaten Gianyar, Bali 80561 Indonesia", -8.415652552008197, 115.3150480516034, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ubud Art Market", "", "", "Jl. Raya Ubud No.35, Ubud, Kecamatan Ubud, Kabupaten Gianyar, Bali 80571 Indonesia", -8.506951675618243, 115.26310123996726, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Batur Sunrise Trek", "", "", "Jl. Songan, Songan A, Kec. Kintamani, Kabupaten Bangli, Bali 80652 Indonesia", -8.246027236543041, 115.39993559392751, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ayung River", "", "", "East Denpasar, Denpasar City, Bali Indonesia", -8.65166506733097, 115.25652870301327, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bali Swing", "", "", "Jl. Dewi Saraswati No.7, Bongkasa Pertiwi, Kec. Abiansemal, Kabupaten Badung, Bali 80352 Indonesia", -8.488738744624964, 115.23991340742378, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tegenungan Waterfall", "", "", "Kemenuh, Sukawati, Gianil Bali, Indonesia", -8.575139304585276, 115.2889277074255, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Eat Street in Seminyak", "", "", "Jl. Kayu Aya No.1, Seminyak, Kec. Kuta, Kabupaten Badung, Bali 80361 Indonesia", -8.68336868642486, 115.1570736632466, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jimbaran Seafood", "", "", "Jl. Bukit Permai No.5a, Jimbaran, Kec. Kuta Sel., Kabupaten Badung, Bali 80361 Indonesia", -8.780991126523165, 115.16387163441247, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanah Lot Temple", "", "", "93HP+GPH, Kabupaten Tabanan, Bali 82121 Indonesia", -8.620964839092593, 115.08685826509887, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Uluwatu Temple", "", "", "Pecatu, South Kuta, Badung District, Bali, Indonesia, ", -8.82891136960229, 115.08498970372375, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tegalalang Rice Terrace", "", "", "Jl. Raya Tegallalang, Tegallalang, Kec. Tegallalang, Kabupaten Gianyar, Bali 80561 Indonesia", -8.43154138558023, 115.27926007858638, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nusa Penida Island", "", "", "Sampalan Batununggul 11 Nusapenida, Kec. Nusa Penida, Bali 80771 Indonesia", -8.677932347097569, 115.57256789393627, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nusa Penida Island", "", "", "Sampalan Batununggul 11 Nusapenida, Kec. Nusa Penida, Bali 80771 Indonesia", -8.677932347097569, 115.57256789393627, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Seminyak Shopping", "", "", "Jl. Kayu Jati No.8, Seminyak, Kec. Kuta, Kabupaten Badung, Bali 80361 Indonesia", -8.683145815089107, 115.15695882091913, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nusa Penida Island", "", "", "Sampalan Batununggul 11 Nusapenida, Kec. Nusa Penida, Bali 80771 Indonesia", -8.677932347097569, 115.57256789393627, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tegalalang Rice Terrace", "", "", "Jl. Raya Tegallalang, Tegallalang, Kec. Tegallalang, Kabupaten Gianyar, Bali 80561 Indonesia", -8.43154138558023, 115.27926007858638, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Uluwatu Temple", "", "", "Pecatu, South Kuta, Badung District, Bali, Indonesia, ", -8.82891136960229, 115.08498970372375, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tanah Lot Temple", "", "", "93HP+GPH, Kabupaten Tabanan, Bali 82121 Indonesia", -8.620964839092593, 115.08685826509887, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Phuket"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Patong Beach", "", "", "83150 Phuket Kathu District, Patong, Thailand", 7.896772591534929, 98.2955903213682, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Big Buddha", "", "", "Karon, Mueang Phuket District, Phuket 83100 Thailand", 7.827810127224173, 98.31290666687092, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phi Phi Islands", "", "", "Mueang Krabi District, Krabi Thailand", 7.743400080396748, 98.77656641937521, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bangla Road", "", "", "83150 Phuket Kathu District, Patong, Thailand", 7.893827717142842, 98.29673952083708, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Promthep Cape", "", "", "แหลมพรหมเทพ, Rawai, Mueang Phuket District, Phuket 83100 Thailand", 7.762081525089347, 98.30544525337886, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Old Phuket Town", "", "", "V9MQ+W2J, Thalang Rd, Taladyai Mueang Phuket District, Phuket 83000 Thailand", 7.885059267527673, 98.38756793803536, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Fantasea", "", "", "99, Kamala, Kathu District, Phuket 83150 Thailand", 7.956786130801789, 98.2873619208378, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Splash Jungle Water Park", "", "", "65 179 Soi Mai Khao 4, Mai Khao, Thalang District, Phuket 83110 Thailand", 8.117812082574442, 98.30626684967574, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phang Nga Bay", "", "", "3H4W+4V9, Ko Yao Yai, Ko Yao District, Phang-nga, Thailand", 8.055586558196884, 98.59731932454578, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Chalong", "", "", "70 หมู่ที่ 6 Chao Fah Tawan Tok Rd, Chalong, Mueang Phuket District, Phuket 83000 Thailand", 7.847055273150061, 98.33689037850904, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Elephant Sanctuary", "", "", "100, Moo 2,, Paklok, Thalang, 83110 Thailand", 8.02609738062494, 98.40164329385577, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Wake Park", "", "", "หมู่ที่ 7 112 Kathu, Kathu District, Phuket 83120 Thailand", 7.9275503708119235, 98.33028939385468, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Old Town Street Art", "", "", "เลขที่ 371, 56 Yaowarad Rd, Mueang Phuket District, Phuket 83000 Thailand", 7.885314312596758, 98.3876262803629, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Freedom Beach", "", "", "ถนนศิริราช Pa Tong, Kathu District, Phuket 83100 Thailand", 7.8754684450582735, 98.2758871668715, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Night Market", "", "", "32 Prachanukhro Rd, Pa Tong, Kathu District, Phuket 83150 Thailand", 7.884448195022388, 98.29297781292016, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Rawai Seafood Market", "", "", "ตลาดปลาริมหาดราไวย์,หมู่ที่2,บ้านชาวเล,ท่าเทียบเรือราไวย์ Wiset Rd, Rawai, Mueang Phuket District, Phuket 83100 Thailand", 7.776010146800332, 98.32887977850832, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Big Buddha", "", "", "Karon, Mueang Phuket District, Phuket 83100 Thailand", 7.827810127224173, 98.31290666687092, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Chalong", "", "", "70 หมู่ที่ 6 Chao Fah Tawan Tok Rd, Chalong, Mueang Phuket District, Phuket 83000 Thailand", 7.847055273150061, 98.33689037850904, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Meditation Center", "", "", "69/509, Moo 1 Soi Chaofah 5, Chalong, Mueang Phuket District, Phuket 83130 Thailand", 7.846737658505038, 98.35951168036247, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phang Nga Bay", "", "", "3H4W+4V9, Ko Yao Yai, Ko Yao District, Phang-nga, Thailand", 8.055586558196884, 98.59731932454578, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Promthep Cape", "", "", "แหลมพรหมเทพ, Rawai, Mueang Phuket District, Phuket 83100 Thailand", 7.762081525089347, 98.30544525337886, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jungceylon Shopping Mall", "", "", "ถนน ราษฎร์อุทิศ 200 Pa Tong, Kathu District, Phuket 83150 Thailand", 7.8914338278870195, 98.29944542454403, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Naka Weekend Market", "", "", "V9J8+77Q, Wirat Hong Yok Rd, Wichit, Mueang Phuket District, Phuket 83000 Thailand", 7.880938597263323, 98.36577160919906, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Old Phuket Town", "", "", "V9MQ+W2J, Thalang Rd, Taladyai Mueang Phuket District, Phuket 83000 Thailand", 7.885059267527673, 98.38756793803536, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phuket Thai Hua Museum", "", "", "28 Krabi, Tambon Talat Nuea, Mueang Phuket District, Phuket 83000 Thailand", 7.885128574306146, 98.38681888036288, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Old Phuket Town", "", "", "V9MQ+W2J, Thalang Rd, Taladyai Mueang Phuket District, Phuket 83000 Thailand", 7.885059267527673, 98.38756793803536, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Promthep Cape", "", "", "แหลมพรหมเทพ, Rawai, Mueang Phuket District, Phuket 83100 Thailand", 7.762081525089347, 98.30544525337886, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phang Nga Bay", "", "", "3H4W+4V9, Ko Yao Yai, Ko Yao District, Phang-nga, Thailand", 8.055586558196884, 98.59731932454578, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Big Buddha", "", "", "Karon, Mueang Phuket District, Phuket 83100 Thailand", 7.827810127224173, 98.31290666687092, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Singapore"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Gardens by the Bay", "", "", "18 Marina Gardens Dr, Singapore 018953", 1.2817399171416592, 103.8636453803335, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Marina Bay Sands Skypark", "", "", "10 Bayfront Ave, Singapore 018956", 1.285472552117276, 103.86104202266097, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Clarke Quay", "", "", "3 River Valley Rd, Singapore 179024", 1.2907954687516983, 103.84650638033345, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Universal Studios Singapore", "", "", "8 Sentosa Gateway, Singapore 098269", 1.2545534905003204, 103.82370981306853, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Gardens by the Bay", "", "", "18 Marina Gardens Dr, Singapore 018953", 1.2817399171416592, 103.8636453803335, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sentosa Island", "", "", "Sentosa, Singapore 098942", 1.248187203431298, 103.8285101496439, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Singapore Zoo", "", "", "80 Mandai Lake Rd, Singapore 729826", 1.4045201083953258, 103.79309809567808, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Adventure Cove Waterpark", "", "", "8 Sentosa Gateway, Sentosa Island, Singapore 098269", 1.2578436137476479, 103.81791703615242, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chinatown Point", "", "", "133 New Bridge Rd, Singapore 059413", 1.285639604371969, 103.84499389197133, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "National Orchid Garden", "", "", "1 Cluny Rd, Singapore 259569", 1.3116150109842812, 103.81482980916958, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Adventure Cove Waterpark", "", "", "8 Sentosa Gateway, Sentosa Island, Singapore 098269", 1.2578436137476479, 103.81791703615242, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Indoor Skydiving at iFly Singapore", "", "", "43 Siloso Beach Walk, #01-01, iFly, Singapore 099010", 1.252281124219563, 103.81752947848005, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Marina Bay Sands Skypark", "", "", "10 Bayfront Ave, Singapore 018956", 1.285472552117276, 103.86104202266097, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Haji Lane", "", "", "672-660 North Bridge Rd, Singapore 188803", 1.3017090328638419, 103.85870880916968, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chinatown Food Street", "", "", "41 Smith St, Singapore 058953", 1.2824668693904135, 103.84390693615245, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Newton Food Centre", "", "", "Newton, Singapore", 1.3121711410705046, 103.839584922661, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Gardens by the Bay", "", "", "18 Marina Gardens Dr, Singapore 018953", 1.2817399171416592, 103.8636453803335, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Singapore Botanic Gardens", "", "", "Opposite 1, Tyersall Ave, Singapore", 1.3122239232284938, 103.81223872080749, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Botanic Gardens", "", "", "1 Cluny Rd, Singapore 259569", 1.3141614791176734, 103.81587067847993, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "East Coast Park", "", "", "E Coast Park Service Rd, Singapore", 1.300987994035464, 103.912197322661, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Southern Ridges", "", "", "Southern Ridges, Singapore", 1.2791683159249438, 103.80947206758292, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Singapore River Cruise", "", "", "CLARK QUAY, JETTY, Singapore 058282", 1.2907962732085414, 103.84615980916962, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Orchard Road", "", "", "Orchard Rd, Singapore", 1.3050135676518841, 103.83227349567825, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bugis Street", "", "", "3 New Bugis Street, Singapore 188867", 1.3008436419040958, 103.85505915335071, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "National Museum of Singapore", "", "", "93 Stamford Rd, Singapore 178897", 1.2967738900242944, 103.84838034779028, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Little India", "", "", "Little India, Singapore", 1.3055490063614814, 103.85302642712027, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Adventure Cove Waterpark", "", "", "8 Sentosa Gateway, Sentosa Island, Singapore 098269", 1.2578436137476479, 103.81791703615242, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Gardens by the Bay", "", "", "18 Marina Gardens Dr, Singapore 018953", 1.2817399171416592, 103.8636453803335, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Universal Studios Singapore", "", "", "8 Sentosa Gateway, Singapore 098269", 1.2545534905003204, 103.82370981306853, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Hanoi"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoan Kiem Lake", "", "", "Hang Trong, Hoan Kiem Hanoi, Vietnam", 21.02785335385327, 105.85225738818596, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Old Quarter", "", "", "Tạ Hiện, P. Lương Ngọc Quyến, Hàng Buồm, Hoàn Kiếm, Hà Nội 100000 Vietnam", 21.035040624982525, 105.85209632113866, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bia Hoi Corner", "", "", "14 P. Đông Thái, Hàng Buồm, Hoàn Kiếm, Hà Nội, Vietnam", 21.036369728847905, 105.85287332113867, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Thang Long Water Puppet Theater", "", "", "57B P. Đinh Tiên Hoàng, Hàng Bạc, Hoàn Kiếm, Hà Nội, Vietnam", 21.031922918840056, 105.85335732299194, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ho Chi Minh Mausoleum Complex", "", "", "Hùng Vương, Điện Biên, Ba Đình, Hà Nội, Vietnam", 21.03711778885952, 105.83465415643364, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "West Lake", "", "", "Vietnam Hanoi, Tay Ho", 21.052984453025047, 105.81798503685253, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vietnam Museum of Ethnology", "", "", "Đ. Nguyễn Văn Huyên, Quan Hoa, Cầu Giấy, Hà Nội 100000 Vietnam", 21.040522423819933, 105.79867102113883, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Thien Son Suoi Nga", "", "", "399J+XQW, Ba Vì, Hà Nội, Vietnam", 21.070344403021124, 105.381875498273, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Temple of Literature", "", "", "58 P. Quốc Tử Giám, Văn Miếu, Đống Đa, Hà Nội, Vietnam", 21.028237652620398, 105.83562627881082, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoa Lo Prison", "", "", "1 P. Hoả Lò, Trần Hưng Đạo, Hoàn Kiếm, Hà Nội, Vietnam", 21.025509941992013, 105.84651028066418, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ha Long Bay", "", "", "Thành phố Hạ Long, Quang Ninh Vietnam", 20.91331620008893, 107.16127681658033, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Train Street", "", "", "74A, đường tàu, P. Trần Phú, Cửa Đông, Hoàn Kiếm, Hà Nội, Vietnam", 21.031018591971904, 105.8447803806644, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "St. Joseph's Cathedral", "", "", "2RHX+FHX, Hàng Trống, Hoàn Kiếm, Hà Nội, Vietnam", 21.029006252146594, 105.84901478066438, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Street Food Tour in the Old Quarter", "", "", "78a Đ. Trần Nhật Duật, Đồng Xuân, Hoàn Kiếm, Hà Nội, Vietnam", 21.037837454476236, 105.85252233648353, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ho Chi Minh Mausoleum Complex", "", "", "Hùng Vương, Điện Biên, Ba Đình, Hà Nội, Vietnam", 21.03711778885952, 105.83465415643364, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoan Kiem Lake and Ngoc Son Temple", "", "", "P. Đinh Tiên Hoàng, Hàng Trống, Hoàn Kiếm, Hà Nội 100000 Vietnam", 21.03083317886428, 105.85244536717305, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Temple of Literature", "", "", "58 P. Quốc Tử Giám, Văn Miếu, Đống Đa, Hà Nội, Vietnam", 21.028237652620398, 105.83562627881082, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoan Kiem Lake", "", "", "Hang Trong, Hoan Kiem Hanoi, Vietnam", 21.02785335385327, 105.85225738818596, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Long Bien Bridge", "", "", "2VV6+P92, Cầu Long Biên, Ngọc Thụy, Hoàn Kiếm, Hà Nội, Vietnam", 21.044503485276472, 105.86094426717351, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tran Quoc Pagoda", "", "", "46 Đ. Thanh Niên, Trúc Bạch, Tây Hồ, Hà Nội, Vietnam", 21.04810260573877, 105.8367025076477, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dong Xuan Market", "", "", "Đồng Xuân, Hoan Kiem Hanoi 100000 Vietnam", 21.038393722066047, 105.85012452484563, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hang Gai Street", "", "", "64 P. Hàng Gai, Hàng Gai, Hoàn Kiếm, Hà Nội, Vietnam", 21.032278904186292, 105.85010745182825, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vietnam Museum of Ethnology", "", "", "Đ. Nguyễn Văn Huyên, Quan Hoa, Cầu Giấy, Hà Nội 100000 Vietnam", 21.040522423819933, 105.79867102113883, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoa Lo Prison", "", "", "1 P. Hoả Lò, Trần Hưng Đạo, Hoàn Kiếm, Hà Nội, Vietnam", 21.025509941992013, 105.84651028066418, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoa Lo Prison", "", "", "1 P. Hoả Lò, Trần Hưng Đạo, Hoàn Kiếm, Hà Nội, Vietnam", 21.025509941992013, 105.84651028066418, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Temple of Literature", "", "", "58 P. Quốc Tử Giám, Văn Miếu, Đống Đa, Hà Nội, Vietnam", 21.028237652620398, 105.83562627881082, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoan Kiem Lake", "", "", "Hang Trong, Hoan Kiem Hanoi, Vietnam", 21.02785335385327, 105.85225738818596, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ho Chi Minh Mausoleum Complex", "", "", "Hùng Vương, Điện Biên, Ba Đình, Hà Nội, Vietnam", 21.03711778885952, 105.83465415643364, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Boracay"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "White Beach", "", "", "Puerto Galera, Mindoro Oriental Philippines", 13.50505076741466, 120.90206523914848, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Puka Shell Beach", "", "", "Puka Shell Beach, Philippine Malay", 11.994973302804453, 121.91222446575269, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bulabog Beach", "", "", "Bulabog Beach, Philippine Malay", 11.96996574692456, 121.92731602800583, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Boracay Pub Crawl", "", "", "Boracay BeachPub, Station 2, Balabag, Boracay Island, Malay, 5608 Aklan, Philippine", 11.963319904530872, 121.92382299391345, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ariel's Point", "", "", "Batason, Buruanga, 5609 Aklan, Philippine", 11.865009481773996, 121.87813928227382, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Diniwid Beach", "", "", "Diniwid Beach, Philippine Aklan, Malay", 11.976505194277454, 121.91165696507754, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Luho", "", "", "Mount Luho, Philippine Aklan Malai", 11.980657873014167, 121.9274707239649, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Willy's Rock", "", "", "XW99+7FW, Malay, Aklan, Philippine", 11.968445596166518, 121.91863829206015, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Helmet Diving", "", "", "WWWG+H4, Banwa it Malay, Lalawigan ng Aklan, Philippine", 11.94669938563019, 121.92527345158567, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "D'Mall", "", "", "D'mall de Boracay, Malay, Aklan, Philippine", 11.963004172004284, 121.92499109844965, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "D'Talipapa Market", "", "", "D'Talipapa Market, Malay, Aklan, Philippine", 11.958660304157794, 121.92831708286813, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jonah's Fruit Shake & Snack Bar", "", "", "XW8F+2HQ, Malay, Aklan, Philippine", 11.965332190191543, 121.9239744362411, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Luho", "", "", "Mount Luho, Philippine Aklan Malai", 11.980657873014167, 121.9274707239649, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "White Beach", "", "", "White Beach, Boracay, Aklan, Philippine", 11.953618622904994, 121.92918445462323, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mandala Spa and Resort Villas", "", "", "Station 3, Boracay Island, Malay, 5608 Aklan, Philippine", 11.950742503224095, 121.93330457856845, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Luho", "", "", "Mount Luho, Philippine Aklan Malai", 11.980657873014167, 121.9274707239649, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ariel's Point", "", "", "Batason, Buruanga, 5609 Aklan, Philippine", 11.865009481773996, 121.87813928227382, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "D'Mall", "", "", "D'mall de Boracay, Malay, Aklan, Philippine", 11.963004172004284, 121.92499109844965, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "D'Talipapa Market", "", "", "D'Talipapa Market, Malay, Aklan, Philippine", 11.958660304157794, 121.92831708286813, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Our Lady of the Most Holy Rosary Church", "", "", "XW9C+94Q, Boracay Hwy Central, Malay, Aklan, Philippine", 11.96867950036439, 121.9204049804222, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Motag Living Museum", "", "", "WW2F+QGC, Banwa it Malay, Lalawigan ng Aklan, Philippine", 11.902197944661987, 121.92379480925723, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "D'Mall", "", "", "D'mall de Boracay, Malay, Aklan, Philippine", 11.963004172004284, 121.92499109844965, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Luho", "", "", "Mount Luho, Philippine Aklan Malai", 11.980657873014167, 121.9274707239649, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ariel's Point", "", "", "Batason, Buruanga, 5609 Aklan, Philippine", 11.865009481773996, 121.87813928227382, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "White Beach", "", "", "Puerto Galera, Mindoro Oriental Philippines", 13.50505076741466, 120.90206523914848, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Boracay"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Doi Suthep", "", "", "Suthep, Mueang Chiang Mai District, Chiang Mai 50200 Thailand", 18.817524822597438, 98.89220151105559, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nimmanhaemin Road", "", "", "Suthep, Mueang Chiang Mai District, Chiang Mai Thailand", 18.796078699509813, 98.965854509427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Elephant Nature Park", "", "", "289 Kuet Chang, Mae Taeng District, Chiang Mai 50150 Thailand", 19.21603980349741, 98.85875603642283, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Night Bazaar", "", "", "Q2P2+C7M, Changklan Rd, Chang Moi Sub-district, Mueang Chiang Mai District, Chiang Mai 50100 Thailand", 18.786236341488795, 99.00064860757314, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bua Thong Waterfalls", "", "", "Mae Ho Phra, Mae Taeng District, Chiang Mai 50150 Thailand", 19.069452084067027, 99.07944797874582, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chiang Mai Old City", "", "", "QXWV+3C3, Si Phum Sub-district, Mueang Chiang Mai District, Chiang Mai 50300 Thailand", 18.795289249993846, 98.99358285636607, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mount Luho", "", "", "100 Huay Kaew Rd, Tambon Su Thep, Mueang Chiang Mai District, Chiang Mai 50200 Thailand", 18.81076469829566, 98.94787742106527, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chiang Mai Night Safari", "", "", "33, Nong Kwai, Hang Dong District, Chiang Mai 50230 Thailand", 18.74260428009879, 98.91723826339076, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Doi Inthanon National Park", "", "", "119 Ban Luang, Chom Thong District, Chiang Mai 50160 Thailand", 18.53591100638488, 98.52220972476377, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Phra Singh", "", "", "2 Samlarn Rd, Phra Sing, Mueang Chiang Mai District, Chiang Mai 50280 Thailand", 18.78883411044075, 98.9819642147286, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Rong Khun", "", "", "Pa O Don Chai, Mueang Chiang Rai District, Chiang Rai 57000 Thailand", 19.823499022115953, 99.76265484993381, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bua Thong Waterfalls", "", "", "Mae Ho Phra, Mae Taeng District, Chiang Mai 50150 Thailand", 19.069452084067027, 99.07944797874582, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chiang Mai Street Food", "", "", "266/42 Moo 3 4a soi 4/1, Ban Wang Tan, San Phak Wan, Hang Dong District, Chiang Mai 50230 Thailand", 18.731624014885874, 98.97070678244219, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chiang Mai Sunday Night Market", "", "", "Rachadamnoen Rd, Tambon Si Phum, Mueang Chiang Mai District, Chiang Mai 50200 Thailand", 18.788403439614893, 98.9881538382629, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Phra That Doi Suthep", "", "", "Suthep, Mueang Chiang Mai District, Chiang Mai 50200 Thailand", 18.80524278567674, 98.92177316895311, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Phra Singh", "", "", "2 Samlarn Rd, Phra Sing, Mueang Chiang Mai District, Chiang Mai 50280 Thailand", 18.78883411044075, 98.9819642147286, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Doi Inthanon National Park", "", "", "119 Ban Luang, Chom Thong District, Chiang Mai 50160 Thailand", 18.535788939063735, 98.52216680941893, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Mae Sa Waterfall", "", "", "Soi Namtok Mae Sa 4, Mae Raem, Mae Rim District, Chiang Mai 50180 Thailand", 18.907498434826735, 98.897398384621, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tha Pae Walking Street", "", "", "Rachadamnoen Rd, Tambon Si Phum, Mueang Chiang Mai District, Chiang Mai 50200 Thailand", 18.788128525632658, 98.99142139593529, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nimmanhaemin Road", "", "", "Suthep, Mueang Chiang Mai District, Chiang Mai Thailand", 18.796078699509813, 98.965854509427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Tribal VILLAGE MUSEUM", "", "", "RXCF+28M, Chang Phueak, Mueang Chiang Mai District, Chiang Mai 50300 Thailand", 18.82028868678429, 98.97334470942766, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nimmanhaemin Road", "", "", "Suthep, Mueang Chiang Mai District, Chiang Mai Thailand", 18.796078699509813, 98.965854509427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Wat Phra Singh", "", "", "2 Samlarn Rd, Phra Sing, Mueang Chiang Mai District, Chiang Mai 50280 Thailand", 18.78883411044075, 98.9819642147286, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Chiang Mai Old City", "", "", "QXWV+3C3, Si Phum Sub-district, Mueang Chiang Mai District, Chiang Mai 50300 Thailand", 18.795289249993846, 98.99358285636607, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bua Thong Waterfalls", "", "", "Mae Ho Phra, Mae Taeng District, Chiang Mai 50150 Thailand", 19.069452084067027, 99.07944797874582, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Kuala Lumpur"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Petronas Twin Towers", "", "", "Petronas Twin Tower, Lower Ground (Concourse) Level, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Malaysia", 3.1579384623562756, 101.71164204082675, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Batu Caves", "", "", "Gombak, 68100 Batu Caves, Selangor, Malaysia", 3.2381186227422543, 101.68397698800601, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bukit Bintang", "", "", "Jln Bukit Bintang, Bukit Bintang, 55100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.145911999526137, 101.7102980938248, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jalan Alor", "", "", "Malaysia Federal Territory of Kuala Lumpur Kuala Lumpur Bukit Bintang", 3.146134650158735, 101.70897309382482, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Thean Hou Temple", "", "", "65, Persiaran Endah, Taman Persiaran Desa, 50460 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.122241745462964, 101.68789309938508, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "KLCC Park", "", "", "KLCC, Lot No. 241, Level 2, Suria, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Malaysia", 3.1547134106470556, 101.71520535149726, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sunway Lagoon Theme Park", "", "", "47500 Selangor Subang Jaya Bandar Sunway, Malaysia", 3.0692110524051825, 101.60673010731607, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Aquaria KLCC", "", "", "Kuala Lumpur Convention Centre, Jalan Pinang, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.153854720967288, 101.71318584990227, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Islamic Arts Museum Malaysia", "", "", "Islamic Arts Museum Malaysia, Jalan Lembah, Tasik Perdana, 50480 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.14203862558512, 101.6900528380058, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Central Market", "", "", "Malaysia 50050 Kuala Lumpur Federal Territory Kuala Lumpur City Center", 3.146079388175134, 101.69556672266093, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "SkyTrex Adventure", "", "", "Batu 20, 1/2, Jalan Sungai Congkak, Kampung Padang, 43100 Hulu Langat, Selangor, Malaysia", 3.209220861024826, 101.84229945335076, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Genting Highlands Theme Park", "", "", "Genting Highlands, 69000 Genting Highlands, Pahang, Malaysia", 3.4227968517216576, 101.79563940731671, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Thean Hou Temple", "", "", "65, Persiaran Endah, Taman Persiaran Desa, 50460 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.122241745462964, 101.68789309938508, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Petaling Street", "", "", "Jalan Petaling, City Centre, 50000 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.144566499748658, 101.69760667847987, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jalan Alor", "", "", "Malaysia Federal Territory of Kuala Lumpur Kuala Lumpur Bukit Bintang", 3.146134650158735, 101.70897309382482, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Central Market", "", "", "Malaysia 50050 Kuala Lumpur Federal Territory Kuala Lumpur City Center", 3.146079388175134, 101.69556672266093, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Petronas Twin Towers", "", "", "Petronas Twin Tower, Lower Ground (Concourse) Level, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Malaysia", 3.1579384623562756, 101.71164204082675, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Kuala Lumpur Tower", "", "", "2 Jalan Punchak, Off, Jalan P. Ramlee, 50250 Kuala Lumpur, Malaysia", 3.153154862069943, 101.70377543033342, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Healing")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Taman Botani Negara", "", "", "40000, Taman Pertanian Malaysia, 40170 Shah Alam, Selangor, Malaysia", 3.0961867921670203, 101.51189749567823, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "KL Forest Eco Park", "", "", "Jalan Puncak, Kuala Lumpur, 50250 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.1545542232557913, 101.70443813615243, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Perdana Botanical Gardens", "", "", "Jalan Kebun Bunga, Tasik Perdana, 55100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.1433944888121528, 101.6850120803334, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bukit Bintang", "", "", "Jln Bukit Bintang, Bukit Bintang, 55100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.145911999526137, 101.7102980938248, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Suria KLCC", "", "", "241, Petronas Twin Tower, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.1579243112138577, 101.71214448800583, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "National Museum of Malaysia", "", "", "Jabatan Muzium Malaysia, Jln Damansara, Perdana Botanical Gardens, 50566 Kuala Lumpur, Federal Territory of Kuala Lumpur, Malaysia", 3.1379057468373364, 101.68737671658347, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Islamic Arts Museum Malaysia", "", "", "Islamic Arts Museum Malaysia, Jalan Lembah, Tasik Perdana, 50480 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.14203862558512, 101.6900528380058, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bukit Bintang", "", "", "Jln Bukit Bintang, Bukit Bintang, 55100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.145911999526137, 101.7102980938248, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Perdana Botanical Gardens", "", "", "Jalan Kebun Bunga, Tasik Perdana, 55100 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur, Malaysia", 3.1433944888121528, 101.6850120803334, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Petronas Twin Towers", "", "", "Petronas Twin Tower, Lower Ground (Concourse) Level, Kuala Lumpur City Centre, 50088 Kuala Lumpur, Malaysia", 3.1579384623562756, 101.71164204082675, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Jalan Alor", "", "", "Malaysia Federal Territory of Kuala Lumpur Kuala Lumpur Bukit Bintang", 3.146134650158735, 101.70897309382482, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Kuala Lumpur"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Bai Truong", "", "", "Đường Trần Hưng Đạo, Dương Tơ, Phú Quốc, Kiên Giang 92500 Vietnam", 10.18161920739451, 103.96694273435789, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc National Park", "", "", "Phu Quoc, Kien Giang, Vietnam", 10.331810203158465, 104.0304177227223, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sao Beach", "", "", "Phu Quoc, Kien Giang Vietnam", 10.057651563879125, 104.0363588111413, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vinpearl Land Phu Quoc", "", "", "8VP4+6R2, Khu Bãi Dài, Phú Quốc, Kiên Giang, Vietnam", 10.335598927746089, 103.85699263621368, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dinh Cau Rock", "", "", "6X84+VHM, Khu phố 2, Phú Quốc, Kiên Giang, Vietnam", 10.217600261603334, 103.95643132272056, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ham Ninh Fishing Village", "", "", "TL47, Hàm Ninh, Phú Quốc, Kiên Giang, Vietnam", 10.18114598792938, 104.04834297668548, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vinpearl Safari Phu Quoc", "", "", "Bãi Dài, Gành Dầu, Phú Quốc, Kiên Giang 922200 Vietnam", 10.33732699300158, 103.89139277854129, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Pearl Farm", "", "", "Đường Trần Hưng Đạo, Dương Tơ, Phú Quốc, Kiên Giang, Vietnam", 10.170385695058929, 103.97005166504741, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Night Market", "", "", "54 Đường Nguyễn Trãi, Khu 1, Phú Quốc, Kiên Giang, Vietnam", 10.218505297784665, 103.95959069573775, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dinh Cau", "", "", "6X84+VHM, Khu phố 2, Phú Quốc, Kiên Giang, Vietnam", 10.217484116003062, 103.9564742380654, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vinpearl Land Phu Quoc", "", "", "8VP4+6R2, Khu Bãi Dài, Phú Quốc, Kiên Giang, Vietnam", 10.335598927746089, 103.85699263621368, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Starfish Beach", "", "", "Rạch Vẹm, Phú Quốc, Kien Giang Vietnam", 10.373808928733833, 103.93757339842385, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Prison", "", "", "350 Đ. Nguyễn Văn Cừ, An Thới, Phú Quốc, Kiên Giang, Vietnam", 10.043518420841636, 104.0187692957352, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dinh Cau", "", "", "6X84+VHM, Khu phố 2, Phú Quốc, Kiên Giang, Vietnam", 10.217484116003062, 103.9564742380654, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Fish Sauce", "", "", "471 Đ. Nguyễn Văn Cừ, Khu 2, Phú Quốc, Kiên Giang, Vietnam", 10.045103641898006, 104.01699228039038, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc National Park", "", "", "Phu Quoc, Kien Giang, Vietnam", 10.331810203158465, 104.0304177227223, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Suoi Tranh", "", "", "Phường 4, Thành phố Đà Lạt, Lam Dong, Vietnam", 11.89919994574623, 108.434219680421, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Cable Car", "", "", "Bãi Đất Đỏ, An Thới, Phú Quốc, Kiên Giang, Vietnam", 10.02727339429669, 104.00739879573499, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ganh Dau Cape", "", "", "Gành Dầu, Phu Quoc, Kien Giang Vietnam", 10.36860776769751, 103.83384789048753, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Duong Dong Market", "", "", "6X96+827, TT. Dương Đông, Phú Quốc, Kiên Giang, Vietnam", 10.218559608872184, 103.95999546933903, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Night Market", "", "", "54 Đường Nguyễn Trãi, Khu 1, Phú Quốc, Kiên Giang, Vietnam", 10.218505297784665, 103.95959069573775, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Nguyen Trung Truc Temple", "", "", "07 Nguyễn Công Trứ, Vĩnh Thanh, Rạch Giá, Kiên Giang, Vietnam", 10.011889168291724, 105.07958841107957, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cao Dai Temple", "", "", "194 Ngô Quyền, Phường 8, Quận 10, Thành phố Hồ Chí Minh, Vietnam", 10.763642530169845, 106.66468813622036, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc Night Market", "", "", "54 Đường Nguyễn Trãi, Khu 1, Phú Quốc, Kiên Giang, Vietnam", 10.218505297784665, 103.95959069573775, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Phu Quoc National Park", "", "", "Phu Quoc, Kien Giang, Vietnam", 10.331810203158465, 104.0304177227223, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dinh Cau", "", "", "6X84+VHM, Khu phố 2, Phú Quốc, Kiên Giang, Vietnam", 10.217484116003062, 103.9564742380654, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vinpearl Land Phu Quoc", "", "", "8VP4+6R2, Khu Bãi Dài, Phú Quốc, Kiên Giang, Vietnam", 10.335598927746089, 103.85699263621368, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }
            if (sPlace == "Danang"){
                if (sTravelWhom.contains("Alone")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "My Khe Beach", "", "", "Phước Mỹ, Son Tra District Da Nang 550000 Vietnam", 16.048978374685802, 108.24965629115223, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Marble Mountains", "", "", "81 Huyền Trân Công Chúa, Hoà Hải, Ngũ Hành Sơn, Đà Nẵng 550000 Vietnam", 16.003671532269767, 108.26462320934766, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Friend")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ba Na Hills", "", "", "Hòa Ninh, Hòa Vang, Da Nang, Vietnam", 15.995269859686468, 107.99613859400262, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Han River Bridge", "", "", "36CG+RC6, Hải Châu 1, Hải Châu, Đà Nẵng 550000 Vietnam", 16.072209745856203, 108.22594732098734, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Couple")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoi An Ancient Town", "", "", "Thành phố Hội An, Vietnam", 15.88041358850626, 108.33837751480002, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sun World Danang Wonders", "", "", "1 Phan Đăng Lưu, Hoà Cường Bắc, Hải Châu, Đà Nẵng, Vietnam", 16.03871203261462, 108.22667509400364, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Kids")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Sun World Danang Wonders", "", "", "1 Phan Đăng Lưu, Hoà Cường Bắc, Hải Châu, Đà Nẵng, Vietnam", 16.03871203261462, 108.22667509400364, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Danang Children's Cultural Palace", "", "", "03 Cách Mạng Tháng 8, Quyết Thắng, Thành phố Biên Hòa, Đồng Nai, Vietnam", 10.946213730149042, 106.81726940924074, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Parents")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Son Tra Marina", "", "", "Đường Hồ Xanh, Thọ Quang, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.09732401549515, 108.26772807866034, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dragon Bridge", "", "", "27 Cầu Rồng Đà Nẵng, An Hải Trung, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.061283955491135, 108.22749549400427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Activities")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hai Van Pass", "", "", "Lăng Cô, Phú Lộc, Thuan Thien Hue Vietnam", 16.201647200957375, 108.13359010623685, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Ba Na Hills Adventure Park", "", "", "Hòa Ninh, Hòa Vang, Da Nang, Vietnam", 15.99536268083239, 107.99614932283883, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("SNS Hot Place")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Golden Bridge", "", "", "XXVW+WCQ, Hoà Phú, Hòa Vang, Đà Nẵng, Vietnam", 15.99505324621571, 107.99667503531536, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dragon Bridge", "", "", "27 Cầu Rồng Đà Nẵng, An Hải Trung, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.061283955491135, 108.22749549400427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Food Tour")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Danang Street Food Tour", "", "", "93 Lưu Quý Kỳ, Hoà Cường Nam, Hải Châu, Đà Nẵng 550000 Vietnam", 16.03648927943761, 108.22343513818466, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Con Market", "", "", "290 Hùng Vương, Vĩnh Trung, Hải Châu, Đà Nẵng 550000 Vietnam", 16.06825226874374, 108.21431892284058, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Tourist Attractions")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "My Son Sanctuary", "", "", "Duy Phú, Duy Xuyên District, Quảng Nam, Vietnam", 15.773900081088827, 108.10918256516067, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Han Market", "", "", "119 Đ. Trần Phú, Hải Châu 1, Hải Châu, Đà Nẵng 550000 Vietnam", 16.068495207254276, 108.22430118051308, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Scenery")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Son Tra Marina", "", "", "Đường Hồ Xanh, Thọ Quang, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.09732401549515, 108.26772807866034, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Marble Mountains", "", "", "81 Huyền Trân Công Chúa, Hoà Hải, Ngũ Hành Sơn, Đà Nẵng 550000 Vietnam", 16.003671532269767, 108.26462320934766, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Shopping")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Vincom Plaza", "", "", "910A Ngô Quyền, An Hải Bắc, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.07168503202081, 108.2301695940046, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Han Market", "", "", "119 Đ. Trần Phú, Hải Châu 1, Hải Châu, Đà Nẵng 550000 Vietnam", 16.068495207254276, 108.22430118051308, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelStyle.contains("Culture / Art / History")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Cham Museum", "", "", "Số 02 Đ. 2 Tháng 9, Bình Hiên, Hải Châu, Đà Nẵng 550000 Vietnam", 16.060558436920708, 108.22333206702153, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Hoi An Ancient Town", "", "", "Thành phố Hội An, Vietnam", 15.88041358850626, 108.33837751480002, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
                if (sTravelWhom.contains("Etc") || sTravelStyle.contains("Etc")){
                    recommend = ScheduleData(Uid, sKey, "", "0", "Son Tra Marina", "", "", "Đường Hồ Xanh, Thọ Quang, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.09732401549515, 108.26772807866034, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Marble Mountains", "", "", "81 Huyền Trân Công Chúa, Hoà Hải, Ngũ Hành Sơn, Đà Nẵng 550000 Vietnam", 16.003671532269767, 108.26462320934766, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Han Market", "", "", "119 Đ. Trần Phú, Hải Châu 1, Hải Châu, Đà Nẵng 550000 Vietnam", 16.068495207254276, 108.22430118051308, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                    recommend = ScheduleData(Uid, sKey, "", "0", "Dragon Bridge", "", "", "27 Cầu Rồng Đà Nẵng, An Hải Trung, Sơn Trà, Đà Nẵng 550000 Vietnam", 16.061283955491135, 108.22749549400427, diffDay)
                    if (!recommendList.contains(recommend)){ recommendList.add(recommend) }
                }
            }

            for (item in recommendList){
                dao.add2(Uid, sKey, item)
            }

            travelDB.child(Uid).child(sKey).child("flags").setValue(1)
            flags = 1
        }
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