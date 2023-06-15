package techtown.org.kotlintest.account

data class OcrData(
    var init: Int,
    var type: String,
    var countryCode: String,
    var passportNo: String, // 암호화 필요
    var hashedSecretKey: String,
    var surname: String,
    var givenName: String,
    var dateOfBirth: String,
    var sex: String,
    var dateOfIssue: String,
    var dateOfExpiry: String,
){
    constructor(): this(0, "", "", "", "", "", "", "", "", "","")
}
