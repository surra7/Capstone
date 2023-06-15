package techtown.org.kotlintest.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.googlecode.tesseract.android.TessBaseAPI

import android.os.Bundle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.MenuItem
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.ActivityOcrtestBinding
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class OcrActivity : AppCompatActivity() {
    private var image //사용되는 이미지
            : Bitmap? = null
    private var mTess //Tess API reference
            : TessBaseAPI? = null
    private var datapath = "" //언어데이터가 있는 경로
    lateinit var binding: ActivityOcrtestBinding
    private lateinit var mDbRef: DatabaseReference

    companion object {
        lateinit var SECRET_KEY: String// 암호화
        /* 업로드할 여권 데이터 */
        lateinit var type: String
        lateinit var countryCode: String
        lateinit var passportNo: String // 암호화 필요
        lateinit var surname: String
        lateinit var givenName: String
        //var nameInKorean: String
        lateinit var dateOfBirth: String
        lateinit var sex: String
        //var nationality: String
        //var authority: String
        lateinit var dateOfIssue: String
        lateinit var dateOfExpiry: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrtestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbRef = Firebase.database.reference.child("passport")
        val user = Firebase.auth.currentUser
        val uId = user!!.uid

        setSupportActionBar(binding.topBar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /*binding.topBar.title = "Information"*/


        //이미지 디코딩을 위한 초기화
        //image = imageUri as Bitmap
        image = BitmapFactory.decodeResource(resources, R.drawable.sampleimg) //샘플이미지파일

        //val imageView: ImageView = findViewById(R.id.imageView)
        //imageView.setImageBitmap(image)
        //언어파일 경로
        datapath = "$filesDir/tesseract/"

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(File(datapath + "tessdata/"))

        //Tesseract API 언어 세팅
        val lang = "eng"

        //OCR 세팅
        mTess = TessBaseAPI()
        mTess!!.init(datapath, lang)

        OCR()

        binding.saveOcrBtn.setOnClickListener {
            val keyString = binding.safeKeyString.text.toString().trim()
            val secretKey= "FORMORESECURIITY"
            SECRET_KEY = keyString + secretKey.substring(keyString.length)
            //upload at DB
            val passportNoHashed = passportNo.encryptECB()
            mDbRef.child(uId).setValue(OcrData(1, type, countryCode, passportNoHashed, getHash(SECRET_KEY), surname, givenName, dateOfBirth, sex, dateOfIssue, dateOfExpiry))
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "upload SUCCESS", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, InformationActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "upload FAILED", Toast.LENGTH_SHORT).show()
                    }
                }
        }
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

    @SuppressLint("SimpleDateFormat")
    fun OCR() {
        var OCRresult: String? = null

        mTess!!.setImage(image)
        OCRresult = mTess!!.utF8Text // ocr된 문자열
        var length = OCRresult.length

        var filteredString = "P"+OCRresult.substring(length-110).substringAfter("P").replace(" ", "")
        var firstStirng = filteredString.slice(0 until 45)
        lateinit var tempString:String
        var secondString = "M" + filteredString.substringAfter("<<<<<").substringAfter("M")

        // get needed data
        type = firstStirng.slice(0 until 2)
        countryCode = firstStirng.slice(2 until 5)
        tempString = firstStirng.substring(5)
        surname = tempString.substringBefore("<")
        givenName = tempString.substring(5).filter {it.isLetter()}
        passportNo = secondString.slice(0 until 9)
        dateOfBirth = secondString.slice(13 until 19)
        sex = secondString.slice(20 until 21)
        dateOfExpiry = secondString.slice(21 until 27)
        dateOfIssue = dateOfExpiry.drop(27)

        //show ocred data
        val inputFormat = SimpleDateFormat("yyMMdd")
        val outputFormat = SimpleDateFormat("yyyy/MM/dd")
        binding.typeEdit.hint = type
        binding.countryCodeEdit.hint = countryCode
        binding.surnameEdit.hint = surname
        binding.givenNameEdit.hint = givenName
        binding.passportNoEdit.hint = passportNo
        binding.sexEdit.hint = sex

        binding.dateOfBirthEdit.hint = dateOfBirth
        binding.dateOfIssueEdit.hint = dateOfIssue
        binding.dateOfExpiryEdit.hint = dateOfExpiry

        /*val birthDate = inputFormat.parse(dateOfBirth.toString())
        val issueDate = inputFormat.parse(dateOfIssue.toString())
        val expiryDate = inputFormat.parse(dateOfExpiry.toString())
        binding.dateOfBirthEdit.hint = outputFormat.format(birthDate)
        binding.dateOfIssueEdit.hint = outputFormat.format(issueDate)
        binding.dateOfExpiryEdit.hint = outputFormat.format(expiryDate)*/
    }

    /***
     * 언어 데이터 파일, 디바이스에 복사
     */

    // 언어 파일 이름
    private val langFileName = "eng.traineddata"
    private fun copyFiles() {
        try {
            val filepath = datapath + "tessdata/" + langFileName
            val assetManager = assets
            val instream: InputStream = assetManager.open(langFileName)
            val outstream: OutputStream = FileOutputStream(filepath)
            val buffer = ByteArray(1024)
            var read: Int
            while (instream.read(buffer).also { read = it } != -1) {
                outstream.write(buffer, 0, read)
            }
            outstream.flush()
            outstream.close()
            instream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /***
     * 디바이스에 언어 데이터 파일 존재 유무 체크
     * @param dir
     */
    private fun checkFile(dir: File) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles()
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            val datafilepath = datapath + "tessdata/" + langFileName
            val datafile = File(datafilepath)
            if (!datafile.exists()) {
                copyFiles()
            }
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