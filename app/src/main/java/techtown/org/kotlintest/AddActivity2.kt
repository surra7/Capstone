package techtown.org.kotlintest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityAddBinding

class AddActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityAdd2Binding

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
        binding = ActivityAdd2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*binding.applyDate.setOnClickListener{
            val intent = Intent(this, Add_Country::class.java)
            requestLauncher.launch(intent)
        }*/
        binding.applyDate.setOnClickListener(({
            val intent = Intent(this, Add_Country::class.java)
            startActivity(intent)
        }))

        /*this.onBackPressedDispatcher.addCallback(this, callback)*/
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