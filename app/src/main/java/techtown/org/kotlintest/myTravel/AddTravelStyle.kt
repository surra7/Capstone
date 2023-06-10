package techtown.org.kotlintest.myTravel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import techtown.org.kotlintest.MyAdapter2
import techtown.org.kotlintest.databinding.ActivityTravelStyleBinding
import techtown.org.kotlintest.mySchedule.ScheduleData

class AddTravelStyle : AppCompatActivity() {

    lateinit var binding: ActivityTravelStyleBinding

    lateinit var Uid: String
    lateinit var sName: String
    lateinit var sPlace: String
    lateinit var sDate: String
    lateinit var eDate: String
    var diffDay: Int = 0

    var sTravelWhom = arrayListOf<String>()
    var sTravelStyle = arrayListOf<String>()

    lateinit var dao: TravelDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTravelStyleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //데이터 null체크
        if (intent.hasExtra("name") && intent.hasExtra("place")
            && intent.hasExtra("sDate") && intent.hasExtra("eDate")
        ) {

            //데이터 담기
            Uid = intent.getStringExtra("uid")!!
            sName = intent.getStringExtra("name")!!
            sPlace = intent.getStringExtra("place")!!
            sDate = intent.getStringExtra("sDate")!!
            eDate = intent.getStringExtra("eDate")!!
            diffDay = intent.getIntExtra("diffDay", 0)!!
        }

        binding.applyDate.isEnabled = false

        binding.selectAlone.setOnClickListener {
            var whom = binding.selectAlone.text.toString()
            binding.selectAlone.isSelected = !binding.selectAlone.isSelected

            if (binding.selectAlone.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectFriend.setOnClickListener {
            var whom = binding.selectFriend.text.toString()
            binding.selectFriend.isSelected = !binding.selectFriend.isSelected

            if (binding.selectFriend.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectCouple.setOnClickListener {
            var whom = binding.selectCouple.text.toString()
            binding.selectCouple.isSelected = !binding.selectCouple.isSelected

            if (binding.selectCouple.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectKids.setOnClickListener {
            var whom = binding.selectKids.text.toString()
            binding.selectKids.isSelected = !binding.selectKids.isSelected

            if (binding.selectKids.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectParents.setOnClickListener {
            var whom = binding.selectParents.text.toString()
            binding.selectParents.isSelected = !binding.selectParents.isSelected

            if (binding.selectParents.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectEtc.setOnClickListener {
            var whom = binding.selectEtc.text.toString()
            binding.selectEtc.isSelected = !binding.selectEtc.isSelected

            if (binding.selectEtc.isSelected){
                sTravelWhom.add(whom)
            } else {
                sTravelWhom.remove(whom)
            }
            buttonCheck()
        }

        binding.selectActivity.setOnClickListener {
            var style = binding.selectActivity.text.toString()
            binding.selectActivity.isSelected = !binding.selectActivity.isSelected
            /*sTravelStyle = sTravelStyle.distinct().toMutableList()*/

            if (binding.selectActivity.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectSnsHot.setOnClickListener {
            var style = binding.selectSnsHot.text.toString()
            binding.selectSnsHot.isSelected = !binding.selectSnsHot.isSelected

            if (binding.selectSnsHot.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectFoodTour.setOnClickListener {
            var style = binding.selectFoodTour.text.toString()
            binding.selectFoodTour.isSelected = !binding.selectFoodTour.isSelected

            if (binding.selectFoodTour.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectTourist.setOnClickListener {
            var style = binding.selectTourist.text.toString()
            binding.selectTourist.isSelected = !binding.selectTourist.isSelected

            if (binding.selectTourist.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectHealing.setOnClickListener {
            var style = binding.selectHealing.text.toString()
            binding.selectHealing.isSelected = !binding.selectHealing.isSelected

            if (binding.selectHealing.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectScenery.setOnClickListener {
            var style = binding.selectScenery.text.toString()
            binding.selectScenery.isSelected = !binding.selectScenery.isSelected

            if (binding.selectScenery.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectShopping.setOnClickListener {
            var style = binding.selectShopping.text.toString()
            binding.selectShopping.isSelected = !binding.selectShopping.isSelected

            if (binding.selectShopping.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectCultureArt.setOnClickListener {
            var style = binding.selectCultureArt.text.toString()
            binding.selectCultureArt.isSelected = !binding.selectCultureArt.isSelected

            if (binding.selectCultureArt.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        binding.selectEtcStyle.setOnClickListener {
            var style = binding.selectEtcStyle.text.toString()
            binding.selectEtcStyle.isSelected = !binding.selectEtcStyle.isSelected

            if (binding.selectEtcStyle.isSelected){
                sTravelStyle.add(style)
            } else {
                sTravelStyle.remove(style)
            }
            buttonCheck()
        }

        //데이터베이스 객체
        val dao = TravelDao()

        binding.tvSkip.setOnClickListener {
            val travel = TravelData(Uid, "", sName, sPlace, sDate, eDate, diffDay, sTravelWhom, sTravelStyle, 0)

            dao.add(Uid, travel)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.applyDate.setOnClickListener {
            val travel = TravelData(Uid, "", sName, sPlace, sDate, eDate, diffDay, sTravelWhom, sTravelStyle)

            dao.add(Uid, travel)?.addOnSuccessListener {
                Toast.makeText(this, "Add SUCCESS", Toast.LENGTH_SHORT).show()

                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()

            }?.addOnFailureListener {
                Toast.makeText(this, "Add FAIL: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //travelWhom과 travelStyle 모두 선택해야 버튼 활성화
    private fun buttonCheck(){
        binding.applyDate.isEnabled = !(sTravelWhom.isEmpty() || sTravelStyle.isEmpty())
    }

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