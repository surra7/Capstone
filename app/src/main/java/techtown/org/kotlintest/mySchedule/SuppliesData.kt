package techtown.org.kotlintest.mySchedule

data class SuppliesData(
    var Uid: String,
    var travelKey: String,
    var suppliesKey: String,
    var suppliesType: String,
    var supplies: String,
    var isChecked: Boolean,
) {
    constructor() : this("", "","", "", "",false)
}
