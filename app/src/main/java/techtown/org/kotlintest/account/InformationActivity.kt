package techtown.org.kotlintest.account

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import techtown.org.kotlintest.databinding.ActivityInformationBinding
import java.io.File
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int


class InformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInformationBinding

    var filePath : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.topBar.title = "Information"
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.editBtn.setOnClickListener(({
            val intent = Intent(this@InformationActivity, EditInformation::class.java)
            startActivity(intent)
        }))
        binding.passportCameraBtn.setOnClickListener(({
            val intent = Intent(this@InformationActivity, ocrtestActivity::class.java)
            startActivity(intent)
        }))

        binding.btnUpload.setOnClickListener(View.OnClickListener { //이미지를 선택

            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 0)
        })

        binding.download.setOnClickListener(View.OnClickListener { //이미지를 선택

            downloadImg()
        })

    }

    //결과 처리
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if (requestCode == 0 && resultCode == RESULT_OK) {
            filePath = data?.data
            /*Log.d(TAG, "uri:" + String.valueOf(filePath))*/
            try {
                /*//Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivPreview.setImageBitmap(bitmap)*/
                uploadFile();
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadFile() {
        //업로드할 파일이 있으면 수행
        if (filePath != null) {
            //업로드 진행 Dialog 보이기
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("업로드중...")
            progressDialog.show()

            //storage
            val storage = FirebaseStorage.getInstance()

            //Unique한 파일명을 만들자.
            val formatter = SimpleDateFormat("yyyyMMHH_mmss")
            /*val now = LocalDateTime.now()*/
            val filename: String = formatter.format(java.util.Date())
            //storage 주소와 폴더 파일명을 지정해 준다.
            val storageRef = storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com/").child("files").child(filename)
            //올라가거라...
            storageRef.putFile(filePath!!) //성공시
                .addOnSuccessListener {
                    progressDialog.dismiss() //업로드 진행 Dialog 상자 닫기
                    Toast.makeText(applicationContext, "업로드 완료!", Toast.LENGTH_SHORT).show()
                } //실패시
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "업로드 실패!", Toast.LENGTH_SHORT).show()
                } //진행중
                .addOnProgressListener { taskSnapshot ->
                    val progress//이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                            =
                        (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toDouble()
                    //dialog에 진행률을 퍼센트로 출력해 준다
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "% ...")
                }
        } else {
            Toast.makeText(applicationContext, "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    //다운로드 안됨...ㅜ
    private fun downloadImg() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com/")
        val localFile = File.createTempFile("images", "jpg")
        storageRef.child("files").child("20230610_1226.pdf").getFile(localFile).addOnSuccessListener {

            Toast.makeText(applicationContext, "다운로드 완료!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{

        }
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