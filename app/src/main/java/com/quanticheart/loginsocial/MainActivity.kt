package com.quanticheart.loginsocial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsocial.facebook.startFacebookLogin
import com.example.loginsocial.google.startGoogleLogin
import com.example.loginsocial.result.onLoginSocialActivityResult
import com.example.loginsocial.toolbox.entity.log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        facebookBtn.startFacebookLogin(this)

        googleBtn.startGoogleLogin(this)

        btnFacebookLib.init(this)

        btnGoogleLib.init(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLoginSocialActivityResult(requestCode, resultCode, data, { googleReturn, googleAccount ->
            googleReturn.log()
        }, { facebookReturn, jsonObject ->
            facebookReturn.log()
        })
    }
}
