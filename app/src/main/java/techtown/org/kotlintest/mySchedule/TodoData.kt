package techtown.org.kotlintest.mySchedule

data class TodoData(
    var travelKey: String,
    var todoKey: String,
    var todo: String,
    var isChecked: Boolean,
){
    constructor(): this("","", "", false)
}