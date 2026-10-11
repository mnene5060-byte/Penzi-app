package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighlandGreen
import com.example.ui.theme.KenyanRed
import com.example.ui.theme.SavannahGold

@Composable
fun AuthScreen(
    onGoogleSignIn: () -> Unit,
    onEmailSignIn: (String, String, (String) -> Unit) -> Unit,
    onStartRegistration: (String, String, (String) -> Unit) -> Unit
) {
    var isEmailScreenVisible by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = isEmailScreenVisible,
        transitionSpec = {
            if (targetState) {
                (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
            } else {
                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
            }
        },
        label = "AuthScreenTransition"
    ) { showEmail ->
        if (showEmail) {
            EmailLoginScreen(
                onBack = { isEmailScreenVisible = false },
                onLogin = { email, pass, onError -> onEmailSignIn(email, pass, onError) },
                onSignUp = { email, pass, onError -> onStartRegistration(email, pass, onError) }
            )
        } else {
            WelcomeScreen(
                onGoogleClick = onGoogleSignIn,
                onEmailClick = { isEmailScreenVisible = true }
            )
        }
    }
}

@Composable
private fun WelcomeScreen(
    onGoogleClick: () -> Unit,
    onEmailClick: () -> Unit
) {
    var isGoogleLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3B0B14), // Premium Kenyan Burgundy
                        Color(0xFF1C0307)  // Deep Midnight Rose
                    )
                )
            )
    ) {
        // Kenyan Starlight & Ambient Sunset Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Star speckles across the night sky
            val starPositions = listOf(
                Offset(canvasWidth * 0.15f, canvasHeight * 0.10f) to 2.5f,
                Offset(canvasWidth * 0.85f, canvasHeight * 0.06f) to 3.0f,
                Offset(canvasWidth * 0.72f, canvasHeight * 0.16f) to 1.8f,
                Offset(canvasWidth * 0.20f, canvasHeight * 0.25f) to 2.2f,
                Offset(canvasWidth * 0.92f, canvasHeight * 0.35f) to 2.8f,
                Offset(canvasWidth * 0.08f, canvasHeight * 0.45f) to 3.2f,
                Offset(canvasWidth * 0.88f, canvasHeight * 0.60f) to 2.0f,
                Offset(canvasWidth * 0.15f, canvasHeight * 0.72f) to 2.4f,
                Offset(canvasWidth * 0.82f, canvasHeight * 0.80f) to 2.0f
            )

            starPositions.forEach { (pos, radius) ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = radius,
                    center = pos
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top App Pill Badge with App Name "PENZI" in Kenyan Golden Sunset style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(16.dp, RoundedCornerShape(32.dp), spotColor = KenyanRed)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    KenyanRed,
                                    Color(0xFFEA580C) // Bright Sunset Flame
                                )
                            )
                        )
                        .padding(horizontal = 36.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "PENZI KENYA 🇰🇪",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Center Glowing Sphere with Floating Orbs & Equalizer Core
            Box(
                modifier = Modifier.size(310.dp),
                contentAlignment = Alignment.Center
            ) {
                // Large Multi-gradient Outer Ring Sphere with Kenyan Color Accents
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF7F1D1D).copy(alpha = 0.9f), // Crimson Darkest
                                    Color(0xFF450A0A).copy(alpha = 0.95f),
                                    Color(0xFF180805)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    KenyanRed.copy(alpha = 0.8f),
                                    SavannahGold.copy(alpha = 0.9f),
                                    Color(0xFFEA580C).copy(alpha = 0.8f),
                                    KenyanRed.copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Floating Video Action Orb (Top Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 55.dp, y = 50.dp)
                        .size(52.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFF97316), Color(0xFFEA580C))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Floating Voice Action Orb (Top Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-55).dp, y = 50.dp)
                        .size(52.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFEA580C), Color(0xFF991B1B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Audio Room",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Core Glow Sphere with Equalizer Bars
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .shadow(20.dp, CircleShape, spotColor = KenyanRed)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF450A0A),
                                    Color(0xFF180502)
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color(0xFFF59E0B),
                                    Color(0xFFFFD600),
                                    KenyanRed,
                                    Color(0xFFF59E0B)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Stylized equalizer bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(18.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFFED7AA))
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFFFD600))
                        )
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(42.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(KenyanRed)
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFFFD600))
                        )
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(18.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFFED7AA))
                        )
                    }
                }

                // Floating Music / Vibe Orb (Bottom Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 55.dp, y = (-65).dp)
                        .size(52.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF0EA5E9), Color(0xFF0284C7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = "Music & Vibes",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title Text under central sphere
                Text(
                    text = "Authentic Connections & Matches",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-10).dp)
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Continue with Google Button
                Button(
                    onClick = {
                        isGoogleLoading = true
                        onGoogleClick()
                    },
                    enabled = !isGoogleLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("google_signin_button"),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF111827)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = KenyanRed,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            // Google "G" Badge Icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF1F2)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = KenyanRed
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(36.dp))
                        }
                    }
                }

                // Continue with Email Button (In beautiful app color Passion Rose)
                Button(
                    onClick = onEmailClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("email_signin_button"),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KenyanRed,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Email Icon Container
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFBE123C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Continue with Email",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(36.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 18+ & Terms Footer
                Text(
                    text = "18+ Only • By continuing, you agree to PENZI's\nTerms of Service  •  Privacy Policy",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }
        }
    }
}

@Composable
private fun EmailLoginScreen(
    onBack: () -> Unit,
    onLogin: (String, String, (String) -> Unit) -> Unit,
    onSignUp: (String, String, (String) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var isSigningUp by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDFBF7) // Warm premium Sand light backdrop
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Bar with Back Arrow and Centered "Email" title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .testTag("email_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E130F),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = "PENZI ACCOUNT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = KenyanRed,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Big Display Headline "Join Penzi"
                Text(
                    text = "Welcome to\nPenzi",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 44.sp,
                    color = Color(0xFF1E130F)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Helper text
                Text(
                    text = "Sign in or create a secure account with your email. Your credentials are fully protected in our secure database.",
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF6B5E55)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email Input
                Text(
                    text = "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E130F),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "enter email (e.g. juma@gmail.com)",
                            color = Color(0xFF9E9187),
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = KenyanRed,
                        unfocusedBorderColor = Color(0xFFEADBCE),
                        focusedTextColor = Color(0xFF1E130F),
                        unfocusedTextColor = Color(0xFF1E130F)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input with Eye Toggle
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E130F),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "minimum 6 characters",
                            color = Color(0xFF9E9187),
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password"),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide Password" else "Show Password",
                                tint = Color(0xFF6B5E55)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedBorderColor = KenyanRed,
                        unfocusedBorderColor = Color(0xFFEADBCE),
                        focusedTextColor = Color(0xFF1E130F),
                        unfocusedTextColor = Color(0xFF1E130F)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Log In Button (KenyanRed Gradient style)
                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        if (trimmedEmail.isBlank()) {
                            Toast.makeText(context, "Please enter your email address", Toast.LENGTH_SHORT).show()
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                            Toast.makeText(context, "Please enter a valid email address (e.g. name@domain.com)", Toast.LENGTH_SHORT).show()
                        } else if (password.isBlank()) {
                            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                        } else if (password.length < 6) {
                            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoggingIn = true
                            onLogin(trimmedEmail, password) { err ->
                                isLoggingIn = false
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isLoggingIn && !isSigningUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("login_submit_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KenyanRed,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (isLoggingIn) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Log In to My Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Elegant Outlined Sign Up Button
                Button(
                    onClick = {
                        val trimmedEmail = email.trim()
                        if (trimmedEmail.isBlank()) {
                            Toast.makeText(context, "Please enter an email address for sign up", Toast.LENGTH_SHORT).show()
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                            Toast.makeText(context, "Please enter a valid email address (e.g. name@domain.com)", Toast.LENGTH_SHORT).show()
                        } else if (password.isBlank()) {
                            Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
                        } else if (password.length < 6) {
                            Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                        } else {
                            isSigningUp = true
                            onSignUp(trimmedEmail, password) { err ->
                                isSigningUp = false
                                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isLoggingIn && !isSigningUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.5.dp, KenyanRed, RoundedCornerShape(28.dp))
                        .testTag("signup_submit_button"),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = KenyanRed
                    )
                ) {
                    if (isSigningUp) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = KenyanRed,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Create New Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Legal Terms Footer
            val legalTermsText = buildAnnotatedString {
                append("By signing up, you are agreeing to our ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = KenyanRed,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Terms of Service")
                }
                append(" & ")
                withStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = KenyanRed,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Privacy Policy")
                }
            }

            Text(
                text = legalTermsText,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF6B5E55),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    }
}
