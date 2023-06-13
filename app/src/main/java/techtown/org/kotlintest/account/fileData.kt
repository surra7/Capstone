package techtown.org.kotlintest.account

data class fileData(
    var Uid: String, //현재 id
    var key: String,
    var fileName: String,
){
    constructor(): this("", "", "")
}

