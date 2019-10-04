package com.example.steamflexer

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val REALM_PARAM = "SteamFlexer"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var webview: WebView = login_page
        webview.settings.javaScriptEnabled = true
        webview!!.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                title = url
                var Url = Uri.parse(url)
                if(Url.authority.equals(REALM_PARAM.toLowerCase())){
                    // That means that authentication is finished and the url contains user's id.
                    webview.stopLoading()


                    // Extracts user id.
                   var userAccountUrl  = Uri.parse(Url.getQueryParameter("openid.identity"))
                   var steamID64  = userAccountUrl.lastPathSegment
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("login", steamID64)
                    finish()
                    startActivity(intent)
                }
            }
        }
        val url = "https://steamcommunity.com/openid/login?" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.mode=checkid_setup&" +
                "openid.realm=https://" + REALM_PARAM + "&" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.return_to=https://" + REALM_PARAM + "/signin/"

        webview.loadUrl(url)
    }
}

