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

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.example.loginsocial.constants.LoginSocialConstants.googleLogin
import com.example.loginsocial.extentions.logW
import com.example.loginsocial.extentions.setSafeOnClickListener
import com.example.loginsocial.extentions.showMsg
import com.example.loginsocial.toolbox.entity.UserSocialData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

/**
 * Fragment
 *
 *
 *
 */

private var mCallback: ((UserSocialData?, GoogleSignInAccount?) -> Unit)? = null

/**
 * Button Events
 */
fun View.startGoogleLoginFragment(
    fragment: Fragment,
    googleCallback: ((UserSocialData?, GoogleSignInAccount?) -> Unit)? = null
) {
    googleCallback?.let { mCallback = it }
    setSafeOnClickListener {
        fragment.startGoogleLogin()
    }
}

/**
 * Google Login Activity Result
 */
fun Fragment.onGoogleLoginActivityResult(
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
fun Fragment.startGoogleLogin() {
    getGoogleSignInClient()?.signInIntent?.let {
        startActivityForResult(it, googleLogin)
    } ?: run { Throwable("Error Google Login").showMsg() }
}

fun Fragment.googleAccountData(
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
fun Fragment.getGoogleUserData(): UserSocialData? {
    return GoogleSignIn.getLastSignedInAccount(this.requireActivity())?.let {
        getUserSocialData(it)
    } ?: run { null }
}

fun Fragment.getGoogleAccountData(): GoogleSignInAccount? {
    return GoogleSignIn.getLastSignedInAccount(this.requireActivity())?.let {
        it
    } ?: run { null }
}

/**
 * verify user's login in active
 */
fun Fragment.getGoogleUserActiveLogin(): Boolean {
    return GoogleSignIn.getLastSignedInAccount(this.requireActivity())?.let {
        true
    } ?: run { false }
}

/**
 * Logout event
 */
fun Fragment.googleLogout() {
    getGoogleSignInClient()?.signOut()?.addOnCompleteListener(this.requireActivity()) {
        "Google logout success!".logW()
    }
}

fun Fragment.googleRevokeAccess() {
    getGoogleSignInClient()?.revokeAccess()?.addOnCompleteListener(this.requireActivity()) {
    }
}

/**
 * Utils
 */
private fun Fragment.getGoogleSignInClient(): GoogleSignInClient? {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()

    openJson()?.let {
        gso.requestIdToken(it)
    }
    val b = gso.build()
    return GoogleSignIn.getClient(this.requireActivity(), b)
}

private fun getUserSocialData(it: GoogleSignInAccount) =
    UserSocialData(it.id, it.idToken, it.displayName, it.email, it.photoUrl.toString())

private fun Fragment.openJson(): String? {
    return try {
        val resId = resources.getIdentifier(
            "default_web_client_id",
            "string",
            requireActivity().packageName
        )
        resources.getString(resId)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}