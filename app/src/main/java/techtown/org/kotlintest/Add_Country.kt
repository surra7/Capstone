package techtown.org.kotlintest

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddCountryBinding

class Add_Country : AppCompatActivity() {
    lateinit var binding: ActivityAddCountryBinding

    private val TAG = this.javaClass.simpleName
    //콜백 인스턴스 생성
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로 버튼 이벤트 처리
            Log.e(TAG, "뒤로가기 클릭")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*this.onBackPressedDispatcher.addCallback(this, callback) //위에서 생성한 콜백 인스턴스 붙여주기*/
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