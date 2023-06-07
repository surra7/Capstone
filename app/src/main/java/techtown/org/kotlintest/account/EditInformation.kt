package techtown.org.kotlintest.account

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.User
import techtown.org.kotlintest.databinding.ActivityEditInfoBinding
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


@Suppress("DEPRECATION")
class EditInformation : AppCompatActivity() {
    lateinit var binding: ActivityEditInfoBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storage: FirebaseStorage

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val CAMERA_CODE = 98

    companion object {
        lateinit var uri:Any
        var isEmail = 0
        var isId = 0
        var isNickname = 0
        var isPassword = 0
    }

    // 페이지 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인증 초기화
        mAuth = Firebase.auth
        // 데베 초기화
        mDbRef = Firebase.database.reference
        // 저장소 초기화
        storage = Firebase.storage

        // 갤러리 버튼
        binding.galleryBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            activityResult.launch(intent)
        }

        // view init
        lateinit var mAuth: FirebaseAuth
        val db = FirebaseDatabase.getInstance()
        storage = Firebase.storage
        mDbRef = db.getReference("user")
        val user = Firebase.auth.currentUser
        val uId = user!!.uid

        mDbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val email = snapshot.child(uId).child("email").value.toString()
                val id = snapshot.child(uId).child("id").value.toString()
                val nickname = snapshot.child(uId).child("nickname").value.toString()
                //val profilePic = snapshot.child(uId).child("profilePicUri").value
                val profilePic =
                    storage.reference.child("profile").child("photo").child("${id}.png")

                profilePic.downloadUrl.addOnSuccessListener(){
                    Glide.with(this@EditInformation)
                        .load(it as Uri)
                        .into(binding.profileView)
                }
                binding.emailEdit.hint = email
                binding.idEdit.hint = id
                binding.nicknameEdit.hint = nickname
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // 카메라 버튼
        binding.newPicBtn.setOnClickListener() {
            if (checkPermission(CAMERA)) {
                val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(itt, CAMERA_CODE)
            }
        }

        // email 체크
        binding.emailEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.emailEdit.text.toString().trim()!=null) {
                    isEmail = 1
                }
                else
                    isEmail = 0
                isSignUpBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isSignUpBtn()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.emailEdit.text.toString().trim()!=null) {
                    isEmail = 1
                }
                else
                    isEmail = 0
                isSignUpBtn()
            }
        })

        // id 체크
        binding.idEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.idEdit.text.toString().trim()!=null) {
                    isId = 1
                }
                else
                    isId = 0
                isSignUpBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isSignUpBtn()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.idEdit.text.toString().trim()!=null) {
                    isId = 1
                }
                else
                    isId = 0
                isSignUpBtn()
            }
        })

        // nickname 체크
        binding.nicknameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.nicknameEdit.text.toString().trim()!=null) {
                    isNickname = 1
                }
                else
                    isNickname = 0
                isSignUpBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isSignUpBtn()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.nicknameEdit.text.toString().trim()!=null) {
                    isNickname = 1
                }
                else
                    isNickname = 0
                isSignUpBtn()
            }
        })

        // 비밀번호 체크
        binding.passwordEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.passwordEdit.text.toString().trim() == binding.passwordCheckEdit.text.toString().trim() && binding.passwordEdit.text.toString().trim() != null){
                    binding.passwordConfirm.text = "password matches!"
                    isPassword = 1
                }
                else{
                    binding.passwordConfirm.text = "password does not match"
                    isPassword = 0
                }
                isSignUpBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.passwordConfirm.text = "password didn't set yet"
                isPassword = 0
                isSignUpBtn()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.passwordEdit.text.toString().trim() == binding.passwordCheckEdit.text.toString().trim() && binding.passwordEdit.text.toString().trim() != null){
                    binding.passwordConfirm.text = "password matches!"
                    isPassword = 1
                }
                else{
                    binding.passwordConfirm.text = "password does not match"
                    isPassword = 0
                }
                isSignUpBtn()
            }
        })
        binding.passwordCheckEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.passwordEdit.text.toString().trim() == binding.passwordCheckEdit.text.toString().trim() && binding.passwordEdit.text.toString().trim() != null){
                    binding.passwordConfirm.text = "password matches!"
                    isPassword = 1
                }
                else{
                    binding.passwordConfirm.text = "password does not match"
                    isPassword = 0
                }
                isSignUpBtn()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isSignUpBtn()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.passwordEdit.text.toString().trim() == binding.passwordCheckEdit.text.toString().trim() && binding.passwordEdit.text.toString().trim() != null){
                    binding.passwordConfirm.text = "password matches!"
                    isPassword = 1
                }
                else{
                    binding.passwordConfirm.text = "password does not match"
                    isPassword = 0
                }
                isSignUpBtn()
            }
        })

        // 저장 버튼
        binding.signupBtn.setOnClickListener {
            val email = binding.emailEdit.text.toString().trim()
            val password = binding.passwordEdit.text.toString().trim()
            val id = binding.idEdit.text.toString().trim()
            val nickname = binding.nicknameEdit.text.toString().trim()
            val profilePicUri = getImageUri(this, uri as Bitmap)
            saveInfo(email, password, id, nickname, profilePicUri!!)
        }
    }

    // 갤러리 작동
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK && it.data != null) {
            uri = it.data!!.data!!
            Glide.with(this)
                .load(uri)
                .into(binding.profileView)
        }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    uri = data?.extras?.get("data")!!
                    if (uri != null) {
                        Glide.with(this)
                            .load(uri)
                            .into(binding.profileView)
                    }
                }
            }
        }
    }

    // Bitmap to Uri
    private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    // 회원가입 버튼 활성화
    private fun isSignUpBtn() {
        binding.signupBtn.isEnabled = (isEmail ==1 && isId ==1 && isNickname ==1 && isPassword ==1)
    }

    //회원가입 작동
    private fun saveInfo(email: String, password: String, id: String, nickname: String, profilePicUri: Uri){
        val passwordHashed = getHash(password)
        val profileName = "${id}.png"
        val currentUser = Firebase.auth.currentUser
        val uId = currentUser!!.uid
        val mDbRef = Firebase.database.reference.child("user").child(uId)
        val user = mutableMapOf<String,Any>()
        user["email"] = email
        user["passwordHashed"] = passwordHashed
        user["id"] = id
        user["nickname"] = nickname
        user["profilePicUri"] = profilePicUri
        mDbRef.updateChildren(user)
        storage.reference.child("profile/photo").child(profileName).putFile(profilePicUri)
        val intent = Intent(this@EditInformation, UserFragment::class.java)
        startActivity(intent)
    }

    // DB 저장
    private fun addUserToDatabase(email: String, uId: String, id: String, nickname: String, passwordHashed: String, profilePicUri: String){
        mDbRef.child("user").child(uId).setValue(User(email, uId, id, nickname, passwordHashed, profilePicUri))
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

    // 기능 종료
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