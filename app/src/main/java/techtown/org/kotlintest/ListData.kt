package techtown.org.kotlintest

import android.os.Parcelable
import java.io.Serializable

data class ListData (
    val name : String,
    val place : String,
    //val img : Int
): Serializable