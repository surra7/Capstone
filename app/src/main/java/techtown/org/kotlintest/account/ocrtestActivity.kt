package techtown.org.kotlintest.account

import com.googlecode.tesseract.android.TessBaseAPI

import android.graphics.BitmapFactory

import android.os.Bundle

import android.widget.TextView

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import techtown.org.kotlintest.R
import java.io.*

class ocrtestActivity : AppCompatActivity() {
    var image //사용되는 이미지
            : Bitmap? = null
    private var mTess //Tess API reference
            : TessBaseAPI? = null
    var datapath = "" //언어데이터가 있는 경로
    var OCRTextView // OCR 결과뷰
            : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ocrtest)
        OCRTextView = findViewById(R.id.OCRTextView)

        //이미지 디코딩을 위한 초기화
        image = BitmapFactory.decodeResource(resources, R.drawable.samplepassport) //샘플이미지파일

        val imageView: ImageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(image)
        //언어파일 경로
        datapath = "$filesDir/tesseract/"

        //트레이닝데이터가 카피되어 있는지 체크
        checkFile(File(datapath + "tessdata/"))

        //Tesseract API 언어 세팅
        val lang = "eng"

        //OCR 세팅
        mTess = TessBaseAPI()
        mTess!!.init(datapath, lang)
    }

    /***
     * 이미지에서 텍스트 읽기
     */
    fun processImage(view: View?) {
        var OCRresult: String? = null
        mTess!!.setImage(image)
        OCRresult = mTess!!.utF8Text
        OCRTextView!!.text = OCRresult
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
}