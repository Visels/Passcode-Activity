package com.mifos.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifos.compose.R
import com.mifos.compose.theme.forgotButtonStyle
import com.mifos.compose.theme.materialBlue
import com.mifos.compose.theme.materialBlueLight90
import com.mifos.compose.theme.skipButtonStyle

@Composable
fun PasscodeSkipButton(
    onSkipButton: () -> Unit,
    hasPassCode: Boolean
) {
    if (!hasPassCode) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
//            TextButton(
//                onClick = { onSkipButton.invoke() }
//            ) {
//                Text(text = stringResource(R.string.skip), style = skipButtonStyle)
//            }

            Button(
                onClick = {onSkipButton.invoke() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = materialBlueLight90,
                    contentColor = materialBlue
                )
            ) {
                Text(text = "Skip")
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun PassCodeSkipButtonPreview(){
    PasscodeSkipButton(onSkipButton = { }, hasPassCode = false )
    
}

@Composable
fun PasscodeForgotButton(
    onForgotButton: () -> Unit,
    hasPassCode: Boolean
) {
    if (hasPassCode) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(
                onClick = { onForgotButton.invoke() }
            ) {
                Text(
                    text = stringResource(R.string.forgot_passcode_login_manually),
                    style = forgotButtonStyle
                )
            }
        }
    }
}