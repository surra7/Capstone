package techtown.org.kotlintest.mySchedule

data class ScheduleListData(
    var travelKey: String,
    var scheduleListKey: String,
    var scheduleDay: String,
    var scheduleList: MutableList<ScheduleData> = mutableListOf()
    /*var scheduleList: MutableMap<String, Any> = HashMap()*/
){
    constructor(): this("", "", "", mutableListOf<ScheduleData>())

}
