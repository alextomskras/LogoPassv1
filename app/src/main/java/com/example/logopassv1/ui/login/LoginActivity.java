package com.example.logopassv1.ui.login;

import static com.example.logopassv1.R.color.purple_700;
import static com.example.logopassv1.R.string.login_failed;
import static com.example.logopassv1.R.string.results_text;
import static com.example.logopassv1.R.string.resultstext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.MediaRouteButton;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logopassv1.R;
import com.example.logopassv1.ui.login.LoginViewModel;
import com.example.logopassv1.ui.login.LoginViewModelFactory;
import com.example.logopassv1.databinding.ActivityLoginBinding;

import java.security.AccessController;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//check all bindings for UI
        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
//настраиваем соответствие полей XML и объектов
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final TextView resultsTextView = binding.resultstextview;
//прячем надпись
        resultsTextView.setVisibility(View.GONE);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError(),resultsTextView);
//                    cleanLoginPass(usernameEditText,passwordEditText);
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess(),resultsTextView);
//                    cleanLoginPass(usernameEditText,passwordEditText);
                }

//                setResult(Activity.RESULT_OK);
//
//                //Complete and destroy login activity once successful
//                finish();

            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            //настраиваем реакцию на события в полях ввода
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }
//передаем данные для проверки после их ввода
            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //обработка события нажатия на вирт клаве кнопки DONE
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
//обработка кнопки LOGIN
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //рисуем прогресс барр - имитация работы с задержкой
                loadingProgressBar.setVisibility(View.VISIBLE);
                //передаем данные во вьюмодель
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    private void updateUiWithUser(LoggedInUserView model, TextView rt) {
        String welcome = getString(R.string.welcome)+ " " + model.getDisplayName();
        //  initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            rt.setTextColor(Color.green(Color.GREEN));
//        }

        rt.setTextColor(Color.GREEN);
//        rt.setTextSize(33);
        rt.setText(resultstext);
//        rt.setVisibility(View.VISIBLE);
        showResultsText(rt);
        
    }

    private void showLoginFailed(@StringRes Integer errorString,TextView rt) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
        rt.setTextColor(Color.RED);
//        rt.setTextSize(33);
        rt.setText(login_failed);
//        rt.setVisibility(View.VISIBLE);
        showResultsText(rt);
    }
    private void showResultsText(TextView rt) {
        rt.setTextSize(33);
        rt.setVisibility(View.VISIBLE);
        cleanLoginPass();
    }

    private void cleanLoginPass(){
        EditText usrTe = findViewById(R.id.username);
        EditText passTe = findViewById(R.id.password);
        usrTe.setText("");
        passTe.setText("");
    }
// скрываем клавиатуру при нажатии в любом месте кроме EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }


        return super.dispatchTouchEvent(ev);
    }
}