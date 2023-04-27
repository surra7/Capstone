package techtown.org.kotlintest.myTravel

import java.io.Serializable

data class ScheduleData (
    val name : String,
    val time : String,
    //val img : Int
): Serializable