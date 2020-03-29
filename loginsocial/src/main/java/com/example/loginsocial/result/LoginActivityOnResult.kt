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
 *  * Copyright(c) Developed by John Alves at 2020/3/28 at 4:51:55 for quantic heart studios
 *
 */

package com.example.loginsocial.result

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.loginsocial.apple.entity.AppleData
import com.example.loginsocial.apple.onAppleLoginActivityResult
import com.example.loginsocial.constants.LoginSocialConstants.googleLogin
import com.example.loginsocial.facebook.onFacebookLoginActivityResultCallback
import com.example.loginsocial.google.onGoogleLoginActivityResult
import com.example.loginsocial.toolbox.entity.UserSocialData
import com.facebook.internal.CallbackManagerImpl
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONObject

fun Activity.onLoginSocialActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    googleCallback: (UserSocialData?, GoogleSignInAccount?) -> Unit,
    facebookCallback: (UserSocialData?, JSONObject?) -> Unit,
    appleCallback: (UserSocialData?, AppleData?) -> Unit
) {
    when (requestCode) {
        googleLogin -> {
            onGoogleLoginActivityResult(requestCode, data, googleCallback)
        }
        CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() -> {
            onFacebookLoginActivityResultCallback(requestCode, resultCode, data, facebookCallback)
        }
    }
    onAppleLoginActivityResult(requestCode, data, appleCallback)
}

fun Fragment.onLoginSocialActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    googleCallback: (UserSocialData?, GoogleSignInAccount?) -> Unit,
    facebookCallback: (UserSocialData?, JSONObject?) -> Unit,
    appleCallback: (UserSocialData?, AppleData?) -> Unit
) {
    when (requestCode) {
        googleLogin -> {
            onGoogleLoginActivityResult(requestCode, data, googleCallback)
        }
        CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode() -> {
            onFacebookLoginActivityResultCallback(requestCode, resultCode, data, facebookCallback)
        }
    }
    onAppleLoginActivityResult(requestCode, data, appleCallback)
}