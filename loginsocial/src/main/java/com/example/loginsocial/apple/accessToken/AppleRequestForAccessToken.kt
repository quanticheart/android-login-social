/*
 *
 *  *                                     /@
 *  *                      __        __   /\/
 *  *                     /==\      /  \_/\/
 *  *                   /======\    \/\__ \__
 *  *                 /==/\  /\==\    /\_|__ \
 *  *              /==/    ||    \=\ / / / /_/
 *  *            /=/    /\ || /\   \=\/ /
 *  *         /===/   /   \||/   \   \===\
 *  *       /===/   /_________________ \===\
 *  *    /====/   / |                /  \====\
 *  *  /====/   /   |  _________    /      \===\
 *  *  /==/   /     | /   /  \ / / /         /===/
 *  * |===| /       |/   /____/ / /         /===/
 *  *  \==\             /\   / / /          /===/
 *  *  \===\__    \    /  \ / / /   /      /===/   ____                    __  _         __  __                __
 *  *    \==\ \    \\ /____/   /_\ //     /===/   / __ \__  ______  ____ _/ /_(_)____   / / / /__  ____ ______/ /_
 *  *    \===\ \   \\\\\\\/   ///////     /===/  / / / / / / / __ \/ __ `/ __/ / ___/  / /_/ / _ \/ __ `/ ___/ __/
 *  *      \==\/     \\\\/ / //////       /==/  / /_/ / /_/ / / / / /_/ / /_/ / /__   / __  /  __/ /_/ / /  / /_
 *  *      \==\     _ \\/ / /////        |==/   \___\_\__,_/_/ /_/\__,_/\__/_/\___/  /_/ /_/\___/\__,_/_/   \__/
 *  *        \==\  / \ / / ///          /===/
 *  *        \==\ /   / / /________/    /==/
 *  *          \==\  /               | /==/
 *  *          \=\  /________________|/=/
 *  *            \==\     _____     /==/
 *  *           / \===\   \   /   /===/
 *  *          / / /\===\  \_/  /===/
 *  *         / / /   \====\ /====/
 *  *        / / /      \===|===/
 *  *        |/_/         \===/
 *  *                       =
 *  *
 *  * Copyright(c) Developed by John Alves at 2020/3/29 at 3:41:33 for quantic heart studios
 *
 */

package com.example.loginsocial.apple.accessToken

import android.app.Activity
import android.os.AsyncTask
import android.util.Base64
import com.example.loginsocial.apple.contants.AppleConstants
import com.example.loginsocial.apple.entity.AppleData
import com.example.loginsocial.extentions.getIntOrNull
import com.example.loginsocial.extentions.getStringByKey
import com.example.loginsocial.extentions.getStringOrNull
import org.json.JSONObject
import org.json.JSONTokener
import java.io.OutputStreamWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class AppleRequestForAccessToken(
    private val activity: Activity?,
    private val appleData: AppleData,
    private val callback: (AppleData?) -> Unit
) : AsyncTask<Void, Void, Void>() {

    private var code = ""
    private var clientsecret = ""
    private val grantType = "authorization_code"

    init {
        this.code = appleData.code
        this.clientsecret = appleData.clientSecret
    }

    private val postParamsForAuth =
        "grant_type=$grantType&code=$code&redirect_uri=" + activity?.getStringByKey(
            AppleConstants.redirectUri
        ) + "&client_id=" + activity?.getStringByKey(AppleConstants.clientId) + "&client_secret=" + clientsecret

    //val postParamsForRefreshToken = "grant_type=" + grantType + "&client_id=" + AppleConstants.CLIENT_ID + "&client_secret=" + clientsecret + "&refresh_token" + "REFRESH_TOKEN_FROM_THE_AUTH"

    override fun doInBackground(vararg params: Void): Void? {
        try {
            val url = URL(AppleConstants.tokenUrl)
            val httpsURLConnection = url.openConnection() as HttpsURLConnection
            httpsURLConnection.requestMethod = "POST"
            httpsURLConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            )
            httpsURLConnection.doInput = true
            httpsURLConnection.doOutput = true
            val outputStreamWriter = OutputStreamWriter(httpsURLConnection.outputStream)
            outputStreamWriter.write(postParamsForAuth)
            outputStreamWriter.flush()
            val response = httpsURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8

            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            appleData.accessToken =
                jsonObject.getStringOrNull("access_token") //Here is the access token
            appleData.expiresIn =
                jsonObject.getIntOrNull("expires_in") //When the access token expires
            appleData.refreshToken =
                jsonObject.getStringOrNull("refresh_token") // The refresh token used to regenerate new access tokens. Store this token securely on your server.
            appleData.idToken =
                jsonObject.getStringOrNull("id_token") // A JSON Web Token that contains the user’s identity information.

            // Get encoded user id by spliting idToken and taking the 2nd piece
            val encodedUserID = appleData.idToken?.split(".")?.get(1)

            //Decode encodedUserID to JSON
            val decodedUserData = String(Base64.decode(encodedUserID, Base64.DEFAULT))
            val userDataJsonObject = JSONObject(decodedUserData)

            // Get User's ID
            appleData.iat = userDataJsonObject.getStringOrNull("iat")
            callback(appleData)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        if (activity == null) return
    }
}