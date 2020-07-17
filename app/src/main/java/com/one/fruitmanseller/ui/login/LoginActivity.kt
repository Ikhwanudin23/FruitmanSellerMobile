package com.one.fruitmanseller.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.one.fruitmanseller.R
import com.one.fruitmanseller.ui.main.MainActivity
import com.one.fruitmanseller.ui.register.RegisterActivity
import com.one.fruitmanseller.utils.Constants
import com.one.fruitmanseller.utils.extensions.gone
import com.one.fruitmanseller.utils.extensions.showToast
import com.one.fruitmanseller.utils.extensions.visible
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private val loginViewModel : LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        btn_goto_register.setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
        observer()
        login()
    }

    private fun observer() {
        loginViewModel.listenToState().observer(this, Observer { handleUiState(it) })
    }

    private fun handleUiState(it: LoginState) {
        when(it){
            is LoginState.Loading -> handleLoading(it.state)
            is LoginState.ShowToast -> showToast(it.message)
            is LoginState.Success -> handleSuccess(it.token)
            is LoginState.Validate -> handleValidate(it)
            is LoginState.Reset -> handleReset()
        }
    }

    private fun login(){
        btn_login.setOnClickListener {
            val email = et_email.text.toString().trim()
            val pass = et_password.text.toString().trim()
            if (loginViewModel.validate(email, pass)){
                loginViewModel.login(email, pass)
            }
        }
    }

    private fun handleReset() {
        setErrorEmail(null)
        setErrorPassword(null)
    }

    private fun handleValidate(validate: LoginState.Validate) {
        validate.email?.let { setErrorEmail(it) }
        validate.password?.let { setErrorEmail(it) }
    }

    private fun handleSuccess(token: String) {
        Constants.setToken(this@LoginActivity, "Bearer $token")
        startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }).also { finish() }
    }

    private fun handleLoading(state: Boolean) {
        btn_login.isEnabled = !state
        loading.isIndeterminate = state
    }

    private fun setErrorEmail(err : String?) { in_email.error = err }
    private fun setErrorPassword(err : String?) { in_password.error = err }

    override fun onResume() {
        super.onResume()
        if (!Constants.getToken(this@LoginActivity).equals("UNDEFINED")){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}
