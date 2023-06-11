package techtown.org.kotlintest.account

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import techtown.org.kotlintest.SignupActivity
import techtown.org.kotlintest.databinding.ActivityInformationBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class InformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInformationBinding

    var filePath : Uri? = null

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val CAMERA_CODE = 98

    companion object {
        lateinit var uri: Any
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topBar.title = "Information"
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/

        binding.editBtn.setOnClickListener(({
            val intent = Intent(this@InformationActivity, EditInformation::class.java)
            startActivity(intent)
        }))
        binding.passportCameraBtn.setOnClickListener(({
            if (checkPermission(CAMERA)) {
                val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(itt, CAMERA_CODE)
            }
            val intent = Intent(this@InformationActivity, OcrActivity::class.java)
            intent.putExtra("passportUri",uri!! as String)
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

    // 카메라 권한 작동
    private fun checkPermission(permissions: Array<out String>): Boolean
    {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, CAMERA_CODE)
                return false
            }
        }

        return true
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Please accept permission for camera", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    // 카메라 작동
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    uri = data?.extras?.get("data")!!
//                    if (uri != null) {
//                        Glide.with(this)
//                            .load(uri)
//                            .into(binding.profileView)
//                    }
                }
                0 -> {
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
        val storageRef = storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com")
        var downloadReference = storageRef.child("files").child("20230610_1257.pdf")
        val destinationPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        val localFile = File.createTempFile("pdf",".pdf",destinationPath)
        downloadReference.getFile(localFile).addOnSuccessListener {
            Toast.makeText(applicationContext, "다운로드 완료!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext, "다운로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
}