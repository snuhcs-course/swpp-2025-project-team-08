package com.example.itda.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.loginUi.collectAsState()
    val scope = rememberCoroutineScope()

    LoginScreen(
        ui = ui,
        onLoginEmailChange = vm::onLoginEmailChange,
        onLoginPasswordChange = vm::onLoginPasswordChange,
        onSubmit = {
            scope.launch {
                if (vm.submitLogin()) onLoginSuccess()
            }
        },
        onSignUpClick = onSignUpClick
    )
}

@Composable
fun SignUpRoute(
    onLoginClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.signUpUi.collectAsState()
    val scope = rememberCoroutineScope()

    SignUpScreen(
        ui = ui,
        onSignUpEmailChange = vm::onSignUpEmailChange,
        onSignUpPasswordChange = vm::onSignUpPasswordChange,
        onSignUpConfirmChange = vm::onSignUpConfirmChange,
        onAgreeTermsChange = vm::onAgreeTermsChange,
        onLoginClick = onLoginClick,
        onSubmit = {
            scope.launch {
                val success = vm.submitSignUp()

                if (success) {
                    onSignUpSuccess()
                }
            }
        }
    )
}

@Composable
fun PersonalInfoRoute(
    onComplete: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val ui by vm.personalInfoUi.collectAsState()
    val scope = rememberCoroutineScope()

    PersonalInfoScreen(
        ui = ui,
        onNameChange = vm::onNameChange,
        onBirthDateChange = vm::onBirthDateChange,
        onGenderChange = vm::onGenderChange,
        onAddressChange = vm::onAddressChange,
        onSubmit = {
            scope.launch {
                if (vm.submitPersonalInfo()) {
                    onComplete()
                }
            }
        }
    )
}