package techtown.org.kotlintest.myTravel

data class TravelData (
    var travelKey: String = "",
    var name : String = "",
    var place : String = "",
    var sDate: String = "",
    var eDate: String = "",
    var diffDay: Int = 0,
    var travelWhom: String,
    var travelStyle: MutableList<String>,
    var flags: Int = 0,
    /*var schedule: MutableMap<String, Any> = HashMap()*/

    //val img : Int
){
    constructor(): this("","","", "", "", 0, "", mutableListOf(),0)

    /*fun toMap(): Map<String, Any?> {
        return mapOf(
            "travelKey" to travelKey,
            "name" to name,
            "place" to place,
            "sDate" to sDate,
            "eDate" to eDate,
        )
    }*/

}

