package com.mifos.compose.component

import android.provider.CalendarContract.Colors
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.compose.PasscodeRepository
import com.mifos.compose.theme.SurfaceBackgroundColor
import com.mifos.compose.theme.blueTint
import com.mifos.compose.theme.borderGreen
import com.mifos.compose.theme.circleGreen
import com.mifos.compose.theme.lightGreen
import com.mifos.compose.theme.limeGreen
import com.mifos.compose.theme.materialBlue
import com.mifos.compose.theme.materialBlueLight67
import com.mifos.compose.theme.materialBlueLight90
import com.mifos.compose.utility.Constants.PASSCODE_LENGTH
import com.mifos.compose.utility.PreferenceManager
import com.mifos.compose.utility.ShakeAnimation.performShakeAnimation
import com.mifos.compose.utility.VibrationFeedback.vibrateFeedback
import com.mifos.compose.viewmodels.PasscodeViewModel

/**
 * @author pratyush
 * @since 15/3/24
 */

@Composable
fun PasscodeScreen(
    viewModel: PasscodeViewModel = hiltViewModel(),
    onForgotButton: () -> Unit,
    onSkipButton: () -> Unit,
    onPasscodeConfirm: (String) -> Unit,
    onPasscodeRejected: () -> Unit,
    biometricAuthentication:() -> Unit
) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val activeStep by viewModel.activeStep.collectAsStateWithLifecycle()
    val filledDots by viewModel.filledDots.collectAsStateWithLifecycle()
    val passcodeVisible by viewModel.passcodeVisible.collectAsStateWithLifecycle()
    val currentPasscode by viewModel.currentPasscodeInput.collectAsStateWithLifecycle()
    val xShake = remember { Animatable(initialValue = 0.0F) }
    var passcodeRejectedDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel.onPasscodeConfirmed) {
        viewModel.onPasscodeConfirmed.collect {
            onPasscodeConfirm(it)
        }
    }
    LaunchedEffect(key1 = viewModel.onPasscodeRejected) {
        viewModel.onPasscodeRejected.collect {
            passcodeRejectedDialogVisible = true
            vibrateFeedback(context)
            performShakeAnimation(xShake)
            onPasscodeRejected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Column (
            modifier = Modifier.weight(0.05f)
        )
        {
            PasscodeToolbar(activeStep = activeStep, preferenceManager.hasPasscode)
            PasscodeSkipButton(onSkipButton = { onSkipButton.invoke() },hasPassCode = preferenceManager.hasPasscode)
        }
//        MifosIcon(modifier = Modifier.fillMaxWidth())


        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(0.2f)
            .padding(top = 16.dp, bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center)
        {

            PasscodeHeader(
                activeStep = activeStep,
                isPasscodeAlreadySet = preferenceManager.hasPasscode
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasscodeView(
                filledDots = filledDots,
                currentPasscode = currentPasscode,
                passcodeVisible = passcodeVisible,
                togglePasscodeVisibility = { viewModel.togglePasscodeVisibility() },
                restart = { viewModel.restart() },
                passcodeRejectedDialogVisible = passcodeRejectedDialogVisible,
                onDismissDialog = { passcodeRejectedDialogVisible = false },
                xShake = xShake,
                hasPassCode = preferenceManager.hasPasscode
            )
        }



        Column (
            modifier = Modifier.weight(0.22f)
        ){
            Spacer(modifier = Modifier.height(6.dp))
            PasscodeKeys(
                enterKey = { viewModel.enterKey(it) },
                deleteKey = { viewModel.deleteKey() },
                deleteAllKeys = { viewModel.deleteAllKeys() },
                modifier = Modifier.padding(horizontal = 12.dp),
                hasPassCode = preferenceManager.hasPasscode,
                launchBiometricPrompt = biometricAuthentication
            )
        }

        Column (modifier = Modifier.weight(0.05f)) {
            Spacer(modifier = Modifier.height(8.dp))
            PasscodeForgotButton(
                onForgotButton = { onForgotButton.invoke() },
                hasPassCode = preferenceManager.hasPasscode
            )
        }
      

    }
}

@Composable
private fun PasscodeView(
    modifier: Modifier = Modifier,
    restart: () -> Unit,
    togglePasscodeVisibility: () -> Unit,
    filledDots: Int,
    passcodeVisible: Boolean,
    currentPasscode: String,
    passcodeRejectedDialogVisible: Boolean,
    onDismissDialog: () -> Unit,
    xShake: Animatable<Float, *>,
    hasPassCode: Boolean
) {
    PasscodeMismatchedDialog(
        visible = passcodeRejectedDialogVisible,
        onDismiss = {
            onDismissDialog.invoke()
            restart()
        }
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier.offset(x = xShake.value.dp),
            horizontalArrangement = Arrangement.spacedBy(
                space = 27.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(PASSCODE_LENGTH) { dotIndex ->
                if (passcodeVisible && dotIndex < currentPasscode.length) {
                    Text(
                        text = currentPasscode[dotIndex].toString(),
                        color = Color.Black
                    )
                } else {
                    val isFilledDot = dotIndex + 1 <= filledDots
                    val dotColor = animateColorAsState(
                        if (isFilledDot) borderGreen else materialBlueLight67, label = ""
                    )
                    val valueColor = animateColorAsState(
                        if (isFilledDot) circleGreen else Color.White, label = ""
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(
                                width = 2.dp,
                                color = dotColor.value,
                                shape = RoundedCornerShape(8.dp)
                            )
//                            .background(
//                                color = dotColor.value,
//                                shape = RoundedCornerShape(5.dp)
//                            )

                    ){
                        Box(
                            modifier = Modifier.padding(12.dp).size(20.dp).background(
                                color = valueColor.value,
                                shape = RoundedCornerShape(10.dp)
                            )
                        )
                    }
                }
            }
        }
//        IconButton(
//            onClick = { togglePasscodeVisibility.invoke() },
//            modifier = Modifier.padding(start = 10.dp)
//        ) {
//            Icon(
//                imageVector = if (passcodeVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
//                contentDescription = null
//            )
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasscodeScreenPreview() {
    PasscodeScreen(
        viewModel = PasscodeViewModel(object : PasscodeRepository {
            override fun getSavedPasscode(): String {
                return ""
            }

            override val hasPasscode: Boolean
                get() = true

            override fun setHasPassCode(hasPassCode: Boolean) {}

            override fun savePasscode(passcode: String) {}

        }),
        {}, {}, {}, {},{}
    )
}