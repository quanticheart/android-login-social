package com.quanticheart.loginsocial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.quanticheart.loginsocial.facebook.generateFacebookHashKey
import com.quanticheart.loginsocial.facebook.startLoginFacebook
import com.quanticheart.loginsocial.google.startGoogleLogin
import com.quanticheart.loginsocial.toolbox.activityResult.onLoginSocialActivityResult
import com.quanticheart.loginsocial.toolbox.entity.log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        generateFacebookHashKey()

        facebookBtn.startLoginFacebook {
            it.log()
        }

        googleBtn.startGoogleLogin(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onLoginSocialActivityResult(requestCode, data) { googleData ->
            googleData.log()
        }
    }
}
