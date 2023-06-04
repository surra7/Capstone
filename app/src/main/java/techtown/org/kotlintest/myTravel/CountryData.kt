package techtown.org.kotlintest.myTravel

data class CountryData(
    var travelKey: String,
    var countryName: String
){
    constructor(): this("","")
}
