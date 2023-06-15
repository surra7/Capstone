package techtown.org.kotlintest.account

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.MediaStore
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import techtown.org.kotlintest.FileAdapter
import techtown.org.kotlintest.SignupActivity
import techtown.org.kotlintest.community.CommentData
import techtown.org.kotlintest.databinding.ActivityInformationBinding
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Suppress("DEPRECATION")
class InformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInformationBinding
    lateinit var mDbRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth

    var filePath : Uri? = null
    var datas = mutableListOf<fileData>()
    lateinit var myAdapter: FileAdapter
    val dao = fileDao()
    var fileDB = Firebase.database.reference.child("file")

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val CAMERA_CODE = 98

    //var context: Context = this

    val user = Firebase.auth.currentUser
    private val uId = user!!.uid
    val db = FirebaseDatabase.getInstance()

    companion object {
        lateinit var SECRET_KEY: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = db.getReference("passport")

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topBar.title = "Information"
        /*toggle = ActionBarDrawerToggle(this, binding.btnSave, R.string.drawer_opened,
            R.string.drawer_closed
        )*/

        val layoutManager = LinearLayoutManager(this)
        binding.fileRecycle.layoutManager = layoutManager
        myAdapter = FileAdapter(this)
        binding.fileRecycle.adapter = myAdapter

        binding.editBtn.setOnClickListener(({
            val intent = Intent(this@InformationActivity, EditInformation::class.java)
            startActivity(intent)
        }))
        binding.passportCameraBtn.setOnClickListener(({
            /*if (checkPermission(CAMERA)) {
                val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(itt, CAMERA_CODE)
            }*/
            val intent = Intent(this@InformationActivity, OcrActivity::class.java)
            /*intent.putExtra("passportUri",uri!! as String)*/
            startActivity(intent)
        }))

        binding.showPInfoBtn.setOnClickListener(({
            val keyString = binding.keyStringCheck.text.toString().trim()
            val secretKey= "FORMORESECURIITY"
            SECRET_KEY = keyString + secretKey.substring(keyString.length)
            mDbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isInit =
                        snapshot.child(uId).child("init").value.toString()
                    if (isInit == "1") {
                        val hashedSecretKey = snapshot.child(uId).child("hashedSecretKey").value.toString()
                        if(getHash(SECRET_KEY) == hashedSecretKey) {
                            showOCR()
                            Toast.makeText(this@InformationActivity, "Success", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(this@InformationActivity, "Check key", Toast.LENGTH_LONG).show()
                        }
                    }
                    else {
                        Toast.makeText(
                            this@InformationActivity,
                            "${isInit}No Passport Info yet",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }))

        binding.btnUpload.setOnClickListener(View.OnClickListener { //이미지를 선택

            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 0)
        })

        myAdapter.setItemClickListener(object: FileAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int, fileName: String) {
                // 클릭 시 이벤트 작성
                downloadFile(fileName)
            }
        })

        getFileList()
    }

    private fun getFileList() {
        dao.getFileList(uId)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datas.clear()

                datas.apply {
                    /*add(fileData(uId,"", "20230610_1257.pdf"))
                    add(fileData(uId,"", "20230610_1226.pdf"))*/
                    myAdapter.datas = datas
                    myAdapter.notifyDataSetChanged()
                }

                //snapshot.children으로 dataSnapshot에 데이터 넣기
                for (dataSnapshot in snapshot.children) {
                    //담긴 데이터를 ScheduleData 클래스 타입으로 바꿈
                    val fileList = dataSnapshot.getValue(fileData::class.java)
                    //키 값 가져오기
                    val fkey = dataSnapshot.key
                    //schedule 정보에 키 값 담기
                    fileList?.key = fkey.toString()

                    if (fileList != null) {
                        fileDB.child(uId).child(fileList.key).child("key").setValue(fkey.toString())
                        datas.add(fileList)
                    }
                }
                //데이터 적용
                myAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
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

    // 비밀번호 SHA-256 Hashing
    fun getHash(str: String): String {
        var digest: String = ""
        digest = try {
            //암호화
            val sh = MessageDigest.getInstance("SHA-256") // SHA-256 해시함수를 사용
            sh.update(str.toByteArray()) // str의 문자열을 해싱하여 sh에 저장
            val byteData = sh.digest() // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 hex string으로 변환
            val hexChars = "0123456789ABCDEF"
            val hex = CharArray(byteData.size * 2)
            for (i in byteData.indices) {
                val v = byteData[i].toInt() and 0xff
                hex[i * 2] = hexChars[v shr 4]
                hex[i * 2 + 1] = hexChars[v and 0xf]
            }

            String(hex) //최종 결과를 String 으로 변환

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            "" //오류 뜰경우 stirng은 blank값임
        }
        return digest
    }

    fun showOCR() {
        val inputFormat = SimpleDateFormat("yyMMdd")
        val outputFormat = SimpleDateFormat("yyyy/MM/dd")
        mDbRef = db.getReference("passport")

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.type.text = snapshot.child(uId)
                    .child("type").value.toString()
                binding.countryCode.text = snapshot.child(uId)
                    .child("countryCode").value.toString()
                binding.passportNo.text = snapshot.child(uId)
                    .child("passportNo").value.toString().decryptECB()
                binding.surname.text = snapshot.child(uId)
                    .child("surname").value.toString()
                binding.givenName.text = snapshot.child(uId)
                    .child("givenName").value.toString()
                binding.sex.text = snapshot.child(uId)
                    .child("sex").value.toString()
                binding.dateOfBirth.text = snapshot.child(uId)
                    .child("dateOfBirth").value.toString()
                binding.dateOfIssue.text = snapshot.child(uId)
                    .child("dateOfIssue").value.toString()
                binding.dateOfExpiry.text = snapshot.child(uId)
                    .child("dateOfExpiry").value.toString()

                /*val birthDate = inputFormat.parse(snapshot.child(uId).child("dateOfBirth").value.toString())
                val issueDate = inputFormat.parse(snapshot.child(uId).child("dateOfIssue").value.toString())
                val expiryDate = inputFormat.parse(snapshot.child(uId).child("dateOfExpiry").value.toString())
                binding.dateOfBirth.text = outputFormat.format(birthDate)
                binding.dateOfIssue.text = outputFormat.format(issueDate)
                binding.dateOfExpiry.text = outputFormat.format(expiryDate)*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * ECB 암호화
     */
    private fun String.encryptECB(): String{
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")    /// 키
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")     //싸이퍼
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)       // 암호화/복호화 모드
        val ciphertext = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.encode(ciphertext, Base64.DEFAULT)
        return String(encodedByte)
    }

    /**
     * ECB 복호화
     */
    private fun String.decryptECB(): String {
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        var decodedByte: ByteArray = Base64.decode(this, Base64.DEFAULT)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val output = cipher.doFinal(decodedByte)

        return String(output)
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
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            //storage
            val storage = FirebaseStorage.getInstance()

            //Unique한 파일명을 만들자.
            val formatter = SimpleDateFormat("yyyyMMHH_mmss")
            /*val now = LocalDateTime.now()*/
            var filename: String = formatter.format(java.util.Date())
            //storage 주소와 폴더 파일명을 지정해 준다.
            val storageRef = getFileExtention(filePath!!)?.let {
                storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com/").child("files").child(uId).child(filename + "." + it)
            }
            //올라가거라...
            if (storageRef != null) {
                storageRef.putFile(filePath!!) //성공시
                    .addOnSuccessListener {
                        progressDialog.dismiss() //업로드 진행 Dialog 상자 닫기
                        val files = getFileExtention(filePath!!)?.let { it1 ->
                            fileData(uId, "", filename + "." + it1)
                        }

                        if (files != null) {
                            dao.add(uId, files)
                        }
                        Toast.makeText(applicationContext, filename + "." + getFileExtention(filePath!!), Toast.LENGTH_SHORT).show()
                    } //실패시
                    .addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "Upload Fail!", Toast.LENGTH_SHORT).show()
                    } //진행중
                    .addOnProgressListener { taskSnapshot ->
                        val progress//이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                                =
                            (100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toDouble()
                        //dialog에 진행률을 퍼센트로 출력해 준다
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "% ...")
                    }
            }
        } else {
            Toast.makeText(applicationContext, "Choose file.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtention(uri: Uri): String? {
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    //다운로드 안됨...ㅜ
    private fun downloadFile(fileName: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://test-b6cf3.appspot.com")
        val downloadFile = fileName
        val downloadSuffix = downloadFile.substringAfter(".")
        var downloadReference = storageRef.child("files").child(uId).child(downloadFile)
        val destinationPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
        val localFile = File.createTempFile("downtemp",".${downloadSuffix}",destinationPath)
        downloadReference.getFile(localFile).addOnSuccessListener {
            Toast.makeText(applicationContext, "다운로드 완료!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext, "다운로드 실패", Toast.LENGTH_SHORT).show()
        }
    }
}