package techtown.org.kotlintest.account

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import techtown.org.kotlintest.SignupActivity
import techtown.org.kotlintest.databinding.ActivityMainBinding
import techtown.org.kotlintest.databinding.CameraBinding

@Suppress("DEPRECATION")
class camera : AppCompatActivity() {

    private val CAMERA = arrayOf(Manifest.permission.CAMERA)
    private val CAMERA_CODE = 98

    private fun checkPermission(permissions: Array<out String>): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, CAMERA_CODE)
                    return false;
                }
            }
        }

        return true;
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Please accept permission of camera", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun CallCamera()
    {
        if (checkPermission(CAMERA)) {
            val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(itt, CAMERA_CODE)
        }
    }

    private lateinit var binding: CameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.takeNewPicBtn.setOnClickListener() {
            CallCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_CODE -> {
                    if (data?.extras?.get("data") != null) {
                        val img = data?.extras?.get("data") as Bitmap
                        //binding.cameraPicView.setImageBitmap(img)
                        val intent = Intent(this@camera, SignupActivity::class.java)
                        startActivity(intent)
                        val into = Glide.with(this)
                            .load(img)
                            .into(binding.cameraPicView) as Bitmap

                    }
                }
            }
        }
    }
}