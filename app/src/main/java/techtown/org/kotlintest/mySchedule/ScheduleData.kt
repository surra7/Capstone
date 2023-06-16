package techtown.org.kotlintest.mySchedule

data class ScheduleData (
    var Uid: String,
    var travelKey: String,
    var scheduleKey: String,
    var day: String,
    var place : String,
    var time : String,
    var memo : String,
    var location: String,
    var latitude: Double,
    var longitude: Double,
    var diffDay: Int = 0,
    //val img : Int

){
    constructor(): this("", "", "","","", "", "", "", 0.0, 0.0, 0)


}