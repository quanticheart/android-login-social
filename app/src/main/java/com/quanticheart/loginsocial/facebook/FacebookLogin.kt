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
 *  * Copyright(c) Developed by John Alves at 2020/3/23 at 10:41:57 for quantic heart studios
 *
 */

package com.quanticheart.loginsocial.facebook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.quanticheart.loginsocial.toolbox.entity.UserSocialData
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Created by John on 10/05/2018.
 */

//     https://developers.facebook.com/docs/facebook-login/android/
//https://developers.facebook.com/apps/120703732131322/fb-login/quickstart/
//    //==============================================================================================
//    //
//    // Structure Response
//    //
//    //==============================================================================================
//
//    //Json Structure Example
////
////    {
////            "id": "579156356",
////            "name": "Vishal Jethani",
////            "first_name": "Vishal",
////            "last_name": "Jethani",
////            "link": "https://www.facebook.com/vishal.jethani",
////            "username": "vishal.jethani",
////
////   "hometown": {
////            "id": "106442706060302",
////            "name": "Pune, Maharashtra"
////    },
////   "location": {
////        "id": "106377336067638",
////        "name": "Bangalore, India"
////    },
////            "bio": "bye bye to bad characters",
////            "gender": "male",
////            "relationship_status": "Single",
////            "timezone": 5.5,
////            "locale": "en_GB",
////            "verified": true,
////            "updated_time": "2012-06-15T05:33:31+0000",
////            "type": "user"
////    }
//
////    W/FB: {Response:  responseCode: 200, graphObject: {"id":"2037594996552257","name":"Jonatas Alves","email":"jonny255d@hotmail.com","last_name":"Alves","first_name":"Jonatas"}, error: null}
//


fun Context.generateFacebookHashKey() {
    val tag = "FaceBook Hash"
    try {
        val info: PackageInfo = packageManager
            .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            val hashKey = String(Base64.encode(md.digest(), 0))
            Log.i(tag, "printHashKey() Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        Log.e(tag, "printHashKey()", e)
    } catch (e: Exception) {
        Log.e(tag, "printHashKey()", e)
    }
}

@SuppressLint("LongLogTag")
fun Activity.verifieFacebookHash() {
    Log.e("activity:", packageName)
    Log.e("activity:", localClassName)
    try {
        @SuppressLint("PackageManagerGetSignatures") val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.w("KeyHash for FacebookData:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    } catch (e: PackageManager.NameNotFoundException) {
    } catch (e: NoSuchAlgorithmException) {
    }
}

fun Activity.deleteFaceBookAccessToken() {
    object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) { //User logged out
                LoginManager.getInstance().logOut()
            }
        }
    }
}

fun getFaceBookProfilePicUrl(idFacebookProfile: String): String {
    return "https://graph.facebook.com/$idFacebookProfile/picture?type=large"
}

fun verifyFacebookToken(): AccessToken? {
    return AccessToken.getCurrentAccessToken()
}

fun verifyFacebookTokenIsValide(): Boolean {
    val accessToken = AccessToken.getCurrentAccessToken()
    val isLoggedIn = accessToken != null && !accessToken.isExpired
    return isLoggedIn
}

fun facebookLogout() {
    LoginManager.getInstance().logOut()
}

fun LoginButton.startLoginFacebook(callback: (UserSocialData?) -> Unit) {
    val callbackManager = CallbackManager.Factory.create()
    setReadPermissions("email");
    registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
        override fun onSuccess(loginResult: LoginResult) {
            println("SUCESSO!")
            val accessToken = loginResult.accessToken
            callGraphFacebook(accessToken, callback)
        }

        override fun onCancel() {
            println("CANCELADO!")
        }

        override fun onError(error: FacebookException) {
            println("ERRO")
        }
    })
}

private fun callGraphFacebook(accessToken: AccessToken, callback: (UserSocialData?) -> Unit) {
    val graphRequest = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
        try {
            val fb_id = jsonObject.getString("id")
            val fb_email = jsonObject.getString("email")
            val verified = jsonObject.getBoolean("verified")
            val name = jsonObject.getString("name")
            val first_name = jsonObject.getString("first_name")
            val last_name = jsonObject.getString("last_name")
            val gender = jsonObject.getString("gender")
            val birthday = jsonObject.getString("birthday")
            val link = jsonObject.getString("link")
            val location = jsonObject.getJSONObject("location")
            val id_location = location.getString("id")
            val name_location = location.getString("name")
            val country_locale = jsonObject.getString("locale")
            val timezone = jsonObject.getInt("timezone")
            val updated_time = jsonObject.getString("updated_time")

            callback(UserSocialData(fb_id, null, name, fb_email))

        } catch (e: JSONException) {
            e.printStackTrace()
            callback(null)
        }
    }
    val parameters = Bundle()
    parameters.putString(
        "fields",
        "id,name,link,email,gender,last_name,first_name,locale,timezone,updated_time,verified"
    )
    graphRequest.parameters = parameters
    graphRequest.executeAsync()
}