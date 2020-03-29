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
 *  * Copyright(c) Developed by John Alves at 2020/3/29 at 3:38:46 for quantic heart studios
 *
 */

package com.example.loginsocial.apple.extentions

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.loginsocial.apple.contants.AppleConstants
import com.example.loginsocial.apple.entity.AppleData
import com.example.loginsocial.extentions.getStringByKey
import java.util.*

@SuppressLint("SetJavaScriptEnabled")
internal fun WebView.initAppleWebView(activity: Activity, callback: (AppleData?) -> Unit) {
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    webViewClient = activity.getClient(callback)
    settings.javaScriptEnabled = true
    loadUrl(activity.getAppleAuthUrl())
}

private fun Activity.getAppleAuthUrl(): String {
    val state = UUID.randomUUID().toString()
    return AppleConstants.authUrl +
            "?response_type=code&v=1.1.6&response_mode=form_post&client_id=" +
            getStringByKey(AppleConstants.clientId) +
            "&scope=" +
            getStringByKey(AppleConstants.scope) +
            "&state=" +
            state +
            "&redirect_uri=" +
            getStringByKey(AppleConstants.redirectUri)
}

private fun Activity.getClient(callback: (AppleData?) -> Unit): WebViewClient {
    return object : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request!!.url.toString().startsWith(getStringByKey(AppleConstants.redirectUri))) {
                if (request.url.toString().contains("success=")) {
                    handleUrl(request.url.toString(), callback)
                } else {
                    callback(null)
                }
                return true
            }
            return true
        }

        // For API 19 and below
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(getStringByKey(AppleConstants.redirectUri))) {
                // Close the dialog after getting the authorization code
                if (url.contains("success=")) {
                    handleUrl(url, callback)
                } else {
                    callback(null)
                }
                return true
            }
            return false
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // retrieve display dimensions
            val displayRectangle = Rect()
            val window = this@getClient.window
            window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

            // Set height of the Dialog to 90% of the screen
            val layoutparms = view?.layoutParams
            layoutparms?.height = (displayRectangle.height() * 0.9f).toInt()
            view?.layoutParams = layoutparms
        }
    }
}

// Check webview url for access token code or error
@SuppressLint("LongLogTag")
private fun handleUrl(url: String, callback: (AppleData?) -> Unit) {
    val uri = Uri.parse(url)

    val success = uri.getQueryParameter("success")
    if (success == "true") {

        val appleData = AppleData()

        // Get the Authorization Code from the URL
        appleData.code = uri.getQueryParameter("code") ?: ""

        // Get the Client Secret from the URL
        appleData.clientSecret = uri.getQueryParameter("client_secret") ?: ""

        //Check if user gave access to the app for the first time by checking if the url contains their email
        if (url.contains("email")) {
            //Get user's email
            appleData.email = uri.getQueryParameter("email") ?: ""

            //Get user's First Name
            appleData.name =
                "${uri.getQueryParameter("first_name") ?: ""} ${uri.getQueryParameter("middle_name")
                    ?: ""} ${uri.getQueryParameter("last_name") ?: ""}".trim().replace("  ", " ")
        }
        callback(appleData)
    } else if (success == "false") {
        Log.e("Error", "We couldn't get the Auth Code")
    }
}