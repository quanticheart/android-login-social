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
 *  * Copyright(c) Developed by John Alves at 2020/3/29 at 3:32:27 for quantic heart studios
 *
 */

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.example.loginsocial.apple

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import com.example.loginsocial.R
import com.example.loginsocial.apple.accessToken.AppleRequestForAccessToken
import com.example.loginsocial.apple.entity.AppleData
import com.example.loginsocial.apple.entity.toUserSocialData
import com.example.loginsocial.apple.extentions.initAppleWebView
import com.example.loginsocial.extentions.setSafeOnClickListener
import com.example.loginsocial.toolbox.entity.UserSocialData
import com.example.loginsocial.ui.AppleButton
import kotlinx.android.synthetic.main.layout_apple_dialog.view.*

private var appledialog: Dialog? = null
private var mCallback: ((UserSocialData?, AppleData?) -> Unit)? = null

internal fun AppleButton.startAppleLoginFragment(
    fragment: Fragment,
    callback: ((UserSocialData?, AppleData?) -> Unit)? = null
) {
    callback?.let { mCallback = it }
    setSafeOnClickListener {
        appledialog = null
        fragment.requireActivity().startAppleLogin()
    }
}

internal fun AppleButton.startAppleLogin(
    activity: Activity,
    callback: ((UserSocialData?, AppleData?) -> Unit)? = null
) {
    callback?.let { mCallback = it }
    setSafeOnClickListener {
        appledialog = null
        activity.startAppleLogin()
    }
}

private fun Activity.startAppleLogin() {
    appledialog = Dialog(this)
    appledialog?.setContentView(getAppleDialogView())
    appledialog?.show()
}

@SuppressLint("SetJavaScriptEnabled", "InflateParams")
private fun Activity.getAppleDialogView(): View {
    val v = LayoutInflater.from(this).inflate(R.layout.layout_apple_dialog, null, false)
    v.appleWebView.initAppleWebView(this) {
        it?.let {
            // Exchange the Auth Code for Access Token
            AppleRequestForAccessToken(this, it) { finalData ->
                finalData?.let {
                    mCallback?.let { it1 -> it1(it.toUserSocialData(), it) }
                } ?: run { mCallback?.let { it1 -> it1(null, null) } }
            }.execute()
        } ?: run {
            mCallback?.let { it1 -> it1(null, null) }
        }
    }
    return v
}

fun Activity.onAppleLoginActivityResult(
    requestCode: Int,
    data: Intent?,
    appleCallback: (UserSocialData?, AppleData?) -> Unit
) {
    mCallback = appleCallback
}

fun Fragment.onAppleLoginActivityResult(
    requestCode: Int,
    data: Intent?,
    appleCallback: (UserSocialData?, AppleData?) -> Unit
) {
    mCallback = appleCallback
}

