package techtown.org.kotlintest

import android.net.Uri

data class User(
    var email: String,
    var uId: String,
    var id: String,
    var nickname: String,
    var passwordHashed: String,
    var profilePicUri: String
){
    constructor(): this("","","","", "", "")
}