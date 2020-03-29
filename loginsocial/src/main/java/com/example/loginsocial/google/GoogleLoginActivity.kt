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
 *  * Copyright(c) Developed by John Alves at 2020/3/28 at 5:21:49 for quantic heart studios
 *
 */
@file:Suppress("unused")

package com.example.loginsocial.google

import android.app.Activity
import android.content.Intent
import android.view.View
import com.example.loginsocial.constants.LoginSocialConstants.googleLogin
import com.example.loginsocial.extentions.getStringByKey
import com.example.loginsocial.extentions.logW
import com.example.loginsocial.extentions.setSafeOnClickListener
import com.example.loginsocial.extentions.showMsg
import com.example.loginsocial.google.contants.GoogleConstants
import com.example.loginsocial.toolbox.entity.UserSocialData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


/**
 * Activity
 *
 *
 *
 */

private var mCallback: ((UserSocialData?, GoogleSignInAccount?) -> Unit)? = null

/**
 * Button Events
 */
fun View.startGoogleLogin(
    activity: Activity,
    googleCallback: ((UserSocialData?, GoogleSignInAccount?) -> Unit)? = null
) {
    googleCallback?.let { mCallback = it }

    setSafeOnClickListener {
        activity.googleLogout()
        activity.startGoogleLogin()
    }
}

/**
 * Google Login Activity Result
 */
fun Activity.onGoogleLoginActivityResult(
    requestCode: Int,
    data: Intent?,
    googleCallback: (UserSocialData?, GoogleSignInAccount?) -> Unit
) {
    mCallback = googleCallback
    data?.let {
        when (requestCode) {
            googleLogin -> {
                googleAccountData(data)
            }
        }
    } ?: run { "Not Google login detected!".logW() }
}

/**
 * Login Event
 */
fun Activity.startGoogleLogin() {
    getGoogleSignInClient()?.signInIntent?.let {
        startActivityForResult(it, googleLogin)
    } ?: run { Throwable("Error Google Login").showMsg() }
}

fun Activity.googleAccountData(
    data: Intent
) {
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
        task.getResult(ApiException::class.java)?.let {
            it.toString().logW()
            mCallback?.let { callback ->
                callback(getUserSocialData(it), it)
            }
        }
    } catch (e: ApiException) {
        e.printStackTrace()
    }
}

/**
 * Get User
 */
fun Activity.getGoogleUserData(): UserSocialData? {
    return GoogleSignIn.getLastSignedInAccount(this)?.let {
        getUserSocialData(it)
    } ?: run { null }
}

fun Activity.getGoogleAccountData(): GoogleSignInAccount? {
    return GoogleSignIn.getLastSignedInAccount(this)?.let {
        it
    } ?: run { null }
}

/**
 * verify user's login in active
 */
fun Activity.getGoogleUserActiveLogin(): Boolean {
    return GoogleSignIn.getLastSignedInAccount(this)?.let {
        true
    } ?: run { false }
}

/**
 * Logout event
 */
fun Activity.googleLogout() {
    getGoogleSignInClient()?.signOut()?.addOnCompleteListener(this) {
        "Google logout success!".logW()
    }
}

fun Activity.googleRevokeAccess() {
    getGoogleSignInClient()?.revokeAccess()?.addOnCompleteListener(this) {
    }
}

/**
 * Utils
 */
private fun Activity.getGoogleSignInClient(): GoogleSignInClient? {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()

    openJson()?.let {
        gso.requestIdToken(it)
    }
    val b = gso.build()
    return GoogleSignIn.getClient(this, b)
}

private fun getUserSocialData(it: GoogleSignInAccount) =
    UserSocialData(it.id, it.idToken, it.displayName, it.email, it.photoUrl.toString())

private fun Activity.openJson(): String? {
    return try {
        getStringByKey(GoogleConstants.googleStringClientID)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
