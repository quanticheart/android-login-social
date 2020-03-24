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
 *  * Copyright(c) Developed by John Alves at 2020/3/23 at 8:40:5 for quantic heart studios
 *
 */
package com.quanticheart.loginsocial.google

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.quanticheart.loginsocial.toolbox.constants.LoginSocialConstants
import com.quanticheart.loginsocial.toolbox.entity.UserSocialData

/**
 * Created by John on 10/05/2018.
 */
//    entre no link e crie seu app
//    https://developers.google.com/identity/sign-in/android/start-integrating
//
//    Uns dos passos é adicionar hash sha1 do seu certificado local
//
//    1 Execute seu projeto
//    2 Clique no menu Gradle
//    3 Clique em projeto(root) -> Tasks
//    4 Clique duas vezes em android-> signingReporte veja a mágica
//    5 Ele lhe dirá tudo na guia Executar
//
//    https://stackoverflow.com/questions/15727912/sha-1-fingerprint-of-keystore-certificate/15727931
//
//    Uma lista ira aparecer assim :
//    Enter keystore password:
//    Alias name: androiddebugkey
//    Creation date: Nov 28, 2017
//    Entry type: PrivateKeyEntry
//    Certificate chain length: 1
//    Certificate[1]:
//    Owner: C=US, O=Android, CN=Android Debug
//    Issuer: C=US, O=Android, CN=Android Debug
//    Serial number: 1
//    Valid from: Tue Nov 28 18:54:16 BRST 2017 until: Thu Nov 21 18:54:16 BRST 2047
//    Certificate fingerprints:
//    MD5:  xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//    SHA1: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//    SHA256: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//    Signature algorithm name: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//    Subject Public Key Algorithm: 1024-bit RSA key
//    Version: 1
//
//    Pegue o código sha1 e adicione no google
//
//    Clique em Configurar um projeto e adicione os dados
//
//    baixe o credentials.json e coloqueo dentro da pasta "app" do projeto
//    pronto!!
//

fun SignInButton.startGoogleLogin(activity: Activity) {
    setOnClickListener {
        activity.startGoogleLogin()
    }
}

fun Activity.startGoogleLogin() {
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    val signInIntent = mGoogleSignInClient.signInIntent
    startActivityForResult(signInIntent, LoginSocialConstants.googleLogin)
}

fun Activity.googleAccountData(data: Intent, callback: (UserSocialData?) -> Unit) {
    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
        task.getResult(ApiException::class.java)?.let {
            callback(UserSocialData(it.id, it.idToken, it.displayName, it.email))
        } ?: run {
            callback(null)
        }
    } catch (e: ApiException) {
//        Este erro 12500 pode ser resolvido adicionando um endereço de e-mail de
//        suporte ao seu projeto nas configurações do projeto. Abra o link https://console.firebase.google.com/
        e.printStackTrace()
        callback(null)
    }
}