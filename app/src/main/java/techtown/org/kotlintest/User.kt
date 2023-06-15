package techtown.org.kotlintest

data class User(
    var email: String,
    var uId: String,
    var id: String,
    var nickname: String,
    var passwordHashed: String,
    var profilePicUri: String,
    var heartList: ArrayList<String>,
    var bookmarkList: ArrayList<String>,
){
    constructor(): this("","","","", "", "", arrayListOf(), arrayListOf())
}
