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
 *  * Copyright(c) Developed by John Alves at 2020/3/28 at 6:20:10 for quantic heart studios
 *
 */

@file:Suppress("UNUSED_VARIABLE", "unused", "DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")

package com.example.loginsocial.facebook

/* ktlint-disable no-wildcard-imports */
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.loginsocial.extentions.*
import com.example.loginsocial.toolbox.entity.UserSocialData
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import org.json.JSONObject

/**
 * Fragment
 *
 *
 *
 */

/**
 * Button Events
 */
private var callbackManager: CallbackManager? = null
private var mCallback: ((UserSocialData?, JSONObject?) -> Unit)? = null
private var mCallbackOnResult: ((UserSocialData?, JSONObject?) -> Unit)? = null

fun View.startFacebookLoginFragment(
    fragment: Fragment,
    callback: ((UserSocialData?, JSONObject?) -> Unit)? = null
) {
    callback?.let { mCallback = it }
    callbackManager = CallbackManager.Factory.create()
    LoginManager.getInstance()
        .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                loginResult?.accessToken?.let {
                    fragment.getFacebookUserData()
                }
            }

            override fun onCancel() {}

            override fun onError(exception: FacebookException) {}
        })
    this.setSafeOnClickListener {
        LoginManager.getInstance()
            .logInWithReadPermissions(fragment, listOf("email", "public_profile"))
    }
}

/**
 * Facebook Login Activity Result
 */
fun Fragment.onFacebookLoginActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
) {
    callbackManager?.onActivityResult(requestCode, resultCode, data)
}

fun Fragment.onFacebookLoginActivityResultCallback(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    callback: (UserSocialData?, JSONObject?) -> Unit
) {
    mCallbackOnResult = callback
    callbackManager?.onActivityResult(requestCode, resultCode, data)
}

/**
 * Get User
 */
fun Fragment.getFacebookUserData() {
    val graphRequest = GraphRequest.newMeRequest(getFaceBookAccessToken()) { jsonObject, response ->
        try {
            jsonObject.toString().logW()
            val fbId = jsonObject.getStringOrNull("id")
            val fbEmail = jsonObject.getStringOrNull("email")
            val verified = jsonObject.getBooleanOrNull("verified")
            val name = jsonObject.getStringOrNull("name")
            val firstName = jsonObject.getStringOrNull("first_name")
            val lastName = jsonObject.getStringOrNull("last_name")
            val gender = jsonObject.getStringOrNull("gender")
            val birthday = jsonObject.getStringOrNull("birthday")
            val link = jsonObject.getStringOrNull("link")
            val location = jsonObject.getJSONObjectOrNull("location")
            val idLocation = location.getStringOrNull("id")
            val nameLocation = location.getStringOrNull("name")
            val countryLocale = jsonObject.getStringOrNull("locale")
            val timezone = jsonObject.getIntOrNull("timezone")
            val updatedTime = jsonObject.getStringOrNull("updated_time")

            mCallbackOnResult?.let {
                it(
                    UserSocialData(
                        fbId,
                        getFaceBookAccessToken().token,
                        name,
                        fbEmail,
                        getFaceBookProfilePicUrl(fbId.toString())
                    ), jsonObject
                )
            } ?: run {
                mCallback?.let {
                    it(
                        UserSocialData(
                            fbId,
                            getFaceBookAccessToken().token,
                            name,
                            fbEmail,
                            getFaceBookProfilePicUrl(fbId.toString())
                        ), jsonObject
                    )
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
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

/**
 * verify user's login in active
 */
fun Fragment.verifyFacebookTokenIsValide(): Boolean {
    val accessToken = AccessToken.getCurrentAccessToken()
    return accessToken != null && !accessToken.isExpired
}

/**
 * Logout event
 */
fun Fragment.facebookLogout() {
    LoginManager.getInstance().logOut()
}

fun Fragment.deleteFaceBookAccessToken() {
    object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null)
                LoginManager.getInstance().logOut()
        }
    }
}

/**
 * Utils
 */
fun Fragment.getFaceBookProfilePicUrl(idFacebookProfile: String): String {
    return "https://graph.facebook.com/$idFacebookProfile/picture?type=large"
}

/**
 * get FaceBook Token
 */
fun Fragment.getFaceBookAccessToken(): AccessToken {
    return AccessToken.getCurrentAccessToken()
}