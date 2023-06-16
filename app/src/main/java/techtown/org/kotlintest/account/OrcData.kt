package techtown.org.kotlintest.account

data class OcrData(
    var init: Int,
    var type: String,
    var countryCode: String,
    var passportNo: String, // 암호화 필요
    var hashedSecretKey: String,
    var surname: String,
    var givenName: String,
    var dateOfBirth: Int,
    var sex: String,
    var dateOfIssue: Int,
    var dateOfExpiry: Int,
){
    constructor(): this(0, "", "", "", "", "", "", 0, "", 0, 0)
}
