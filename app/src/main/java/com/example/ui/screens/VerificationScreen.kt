package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.data.ai.FaceVerificationService
import com.example.data.ai.VerificationResult
import com.example.model.UserAccount
import com.example.ui.theme.KenyanSunsetGradient
import com.example.ui.theme.PenziFullScreenGradient
import com.example.ui.theme.PenziHeaderGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class VerificationStage {
    INFO_SCREEN,
    CAMERA_VIEWFINDER,
    AI_ANALYZING,
    RESULT_SUCCESS,
    RESULT_FAILED
}

@Composable
fun VerificationScreen(
    user: UserAccount? = null,
    isAlreadyVerified: Boolean = false,
    onBack: () -> Unit,
    onVerifyComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentStage by remember {
        mutableStateOf(if (isAlreadyVerified) VerificationStage.RESULT_SUCCESS else VerificationStage.INFO_SCREEN)
    }

    var capturedSelfieBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var analysisProgress by remember { mutableFloatStateOf(0.1f) }
    var analysisStatusMessage by remember { mutableStateOf("Initializing Camera & Face Detection...") }
    var verificationResult by remember { mutableStateOf<VerificationResult?>(null) }

    // Camera state
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_FRONT) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isCapturingPhoto by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            currentStage = VerificationStage.CAMERA_VIEWFINDER
        } else {
            // Permission denied prompt
        }
    }

    fun startCameraFlow() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            currentStage = VerificationStage.CAMERA_VIEWFINDER
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun processCapturedPhoto(bitmap: Bitmap) {
        capturedSelfieBitmap = bitmap
        currentStage = VerificationStage.AI_ANALYZING
        analysisProgress = 0.15f
        analysisStatusMessage = "Analyzing live selfie geometry..."

        coroutineScope.launch {
            delay(700)
            analysisProgress = 0.45f
            analysisStatusMessage = "Checking human liveness & lighting..."
            delay(800)
            analysisProgress = 0.70f
            analysisStatusMessage = "AI comparing features with profile photo..."

            val profileUrl = user?.photoUrl ?: ""
            val result = FaceVerificationService.verifyFaces(context, profileUrl, bitmap)
            verificationResult = result

            delay(600)
            analysisProgress = 1.0f

            if (result.isMatch) {
                currentStage = VerificationStage.RESULT_SUCCESS
                onVerifyComplete()
            } else {
                currentStage = VerificationStage.RESULT_FAILED
            }
        }
    }

    when (currentStage) {
        VerificationStage.INFO_SCREEN -> {
            VerificationInfoContent(
                user = user,
                isAlreadyVerified = isAlreadyVerified,
                onBack = onBack,
                onStartVerification = { startCameraFlow() }
            )
        }

        VerificationStage.CAMERA_VIEWFINDER -> {
            InAppCameraViewfinder(
                lensFacing = lensFacing,
                isCapturing = isCapturingPhoto,
                onToggleLens = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        CameraSelector.LENS_FACING_BACK
                    } else {
                        CameraSelector.LENS_FACING_FRONT
                    }
                },
                onCapture = { imageCapture ->
                    if (isCapturingPhoto) return@InAppCameraViewfinder
                    isCapturingPhoto = true
                    imageCapture?.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val rotation = image.imageInfo.rotationDegrees
                                val rawBitmap = imageProxyToBitmap(image)
                                image.close()
                                isCapturingPhoto = false
                                if (rawBitmap != null) {
                                    val rotatedBitmap = rotateBitmap(rawBitmap, rotation.toFloat(), lensFacing == CameraSelector.LENS_FACING_FRONT)
                                    processCapturedPhoto(rotatedBitmap)
                                } else {
                                    // Fallback synthetic test bitmap
                                    val fallbackBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
                                    processCapturedPhoto(fallbackBitmap)
                                }
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("CameraVerify", "Photo capture failed: ${exception.message}", exception)
                                isCapturingPhoto = false
                            }
                        }
                    )
                },
                onCancel = {
                    currentStage = VerificationStage.INFO_SCREEN
                }
            )
        }

        VerificationStage.AI_ANALYZING -> {
            AiAnalyzingScreen(
                userProfileUrl = user?.photoUrl,
                capturedSelfie = capturedSelfieBitmap,
                progress = analysisProgress,
                statusMessage = analysisStatusMessage
            )
        }

        VerificationStage.RESULT_SUCCESS -> {
            VerificationSuccessScreen(
                user = user,
                confidence = verificationResult?.confidence ?: 0.98f,
                message = verificationResult?.message ?: "Face match confirmed! You now have the authentic verified badge.",
                onDone = onBack
            )
        }

        VerificationStage.RESULT_FAILED -> {
            VerificationFailedScreen(
                user = user,
                capturedSelfie = capturedSelfieBitmap,
                errorMessage = verificationResult?.message ?: "Faces did not match your profile photo.",
                onRetake = {
                    capturedSelfieBitmap = null
                    currentStage = VerificationStage.CAMERA_VIEWFINDER
                },
                onBack = {
                    currentStage = VerificationStage.INFO_SCREEN
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 1. INFO SCREEN
// ---------------------------------------------------------------------------
@Composable
private fun VerificationInfoContent(
    user: UserAccount?,
    isAlreadyVerified: Boolean,
    onBack: () -> Unit,
    onStartVerification: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PenziFullScreenGradient),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    if (isAlreadyVerified) {
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("verify_done_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A859)),
                            shape = RoundedCornerShape(27.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verified & Done",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        Button(
                            onClick = onStartVerification,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("verify_now_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A859) // M-Pesa Green / Success Green
                            ),
                            shape = RoundedCornerShape(27.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verify Now (Open Camera)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PenziHeaderGradient)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("verification_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Identity Verification",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Verification Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF1E3A8A) // Deep Royal Indigo Blue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 26.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User Profile Photo with Blue Ring
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user?.photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = user?.photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, Color(0xFF60A5FA), CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF3B82F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Face",
                                        tint = Color.White,
                                        modifier = Modifier.size(44.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isAlreadyVerified) "You Are Verified!" else "Verify You Are a Real Person",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAlreadyVerified)
                                "Congratulations! Your identity has been verified with AI. Your profile features the verified authenticity checkmark."
                            else
                                "Take a live selfie inside Penzi. Our AI will compare your facial features with your profile photo in real-time.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.92f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Verification Guidelines Section
            Text(
                text = "How Verification Works",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GuidelineItemCard(
                    stepNumber = "1",
                    title = "Profile Photo Requirement",
                    subtitle = "Your profile picture must be a clear photo of yourself. Non-human photos (animals, cars, cartoons) will fail AI match."
                )

                GuidelineItemCard(
                    stepNumber = "2",
                    title = "Live In-App Camera Selfie",
                    subtitle = "Tapping 'Verify Now' opens your camera inside Penzi. Position your face in the oval frame and snap a photo."
                )

                GuidelineItemCard(
                    stepNumber = "3",
                    title = "AI Face Comparison",
                    subtitle = "AI scans facial landmarks and biometric similarity. If it's a match, your profile is immediately granted the verified tick!"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// 2. IN-APP CAMERA VIEWFINDER
// ---------------------------------------------------------------------------
@Composable
private fun InAppCameraViewfinder(
    lensFacing: Int,
    isCapturing: Boolean,
    onToggleLens: () -> Unit,
    onCapture: (ImageCapture?) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    // Pulsing face guide animation
    val infiniteTransition = rememberInfiniteTransition(label = "face_guide")
    val guideAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "guide_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Live Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(previewView.display?.rotation ?: 0)
                        .build()
                    imageCapture = capture

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        Log.e("CameraViewfinder", "Camera binding failed: ${e.message}", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            update = { previewView ->
                // Rebind when lensFacing changes
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = capture

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture
                        )
                    } catch (e: Exception) {
                        Log.e("CameraViewfinder", "Rebind camera failed: ${e.message}")
                    }
                }, ContextCompat.getMainExecutor(context))
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Bar: Back & Flip Camera
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.White
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Text(
                        text = "AI Face Scan",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = onToggleLens,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.FlipCameraAndroid,
                    contentDescription = "Flip Camera",
                    tint = Color.White
                )
            }
        }

        // Center Biometric Oval Face Alignment Frame
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 250.dp, height = 310.dp)
                    .clip(RoundedCornerShape(130.dp))
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFF59E0B).copy(alpha = guideAlpha),
                                Color(0xFF00E676).copy(alpha = guideAlpha),
                                Color(0xFF3B82F6).copy(alpha = guideAlpha),
                                Color(0xFFF59E0B).copy(alpha = guideAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(130.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Corner target brackets
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Position your face inside the frame & look directly at camera",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Bottom Capture Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            // Shutter Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .clickable(enabled = !isCapturing) {
                        onCapture(imageCapture)
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isCapturing) Color.Gray else Color.White)
                        .border(3.dp, Color(0xFFF59E0B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = Color(0xFFB91C1C),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Capture",
                            tint = Color(0xFFB91C1C),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. AI ANALYZING SCREEN
// ---------------------------------------------------------------------------
@Composable
private fun AiAnalyzingScreen(
    userProfileUrl: String?,
    capturedSelfie: Bitmap?,
    progress: Float,
    statusMessage: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_scan")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617)))),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AI Biometric Verification",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Comparing live selfie with your profile photo",
                fontSize = 13.5.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Side-by-side comparison boxes with scanning laser
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Photo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, Color(0xFF3B82F6), RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!userProfileUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = userProfileUrl,
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Profile Photo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Compare Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                // Live Selfie with Laser Scanner
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, Color(0xFF00E676), RoundedCornerShape(20.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedSelfie != null) {
                            Image(
                                bitmap = capturedSelfie.asImageBitmap(),
                                contentDescription = "Live Selfie",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Laser Scanning Line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (130 * laserOffset).dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, Color(0xFF00E676), Color.White, Color(0xFF00E676), Color.Transparent)
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live Selfie",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00E676),
                trackColor = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Status message
            Text(
                text = statusMessage,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 4. VERIFICATION SUCCESS SCREEN
// ---------------------------------------------------------------------------
@Composable
private fun VerificationSuccessScreen(
    user: UserAccount?,
    confidence: Float,
    message: String,
    onDone: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22)))),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("verification_success_done"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        shape = RoundedCornerShape(27.dp)
                    ) {
                        Text(
                            text = "Done & Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF022C22)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Big Glowing Green Check
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E676).copy(alpha = 0.2f))
                    .border(3.dp, Color(0xFF00E676), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Identity Verified!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFF065F46),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "AI Match Score: ${(confidence * 100).toInt()}% Confidence",
                    color = Color(0xFFA7F3D0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFE2E8F0),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 5. VERIFICATION FAILED SCREEN
// ---------------------------------------------------------------------------
@Composable
private fun VerificationFailedScreen(
    user: UserAccount?,
    capturedSelfie: Bitmap?,
    errorMessage: String,
    onRetake: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF7F1D1D), Color(0xFF450A0A)))),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRetake,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("verification_retake_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF7F1D1D),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Retake Live Selfie",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7F1D1D)
                        )
                    }

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Cancel & Return",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Warning Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(3.dp, Color(0xFFFCA5A5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = "Failed",
                    tint = Color(0xFFFCA5A5),
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Verification Mismatch",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Color(0xFFFEE2E2),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = Color(0xFF991B1B).copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Tips for a successful verification:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "• Make sure your room is well lit and avoid backlighting",
                        fontSize = 12.sp,
                        color = Color(0xFFFEE2E2)
                    )
                    Text(
                        text = "• Hold the camera at eye level and look directly at it",
                        fontSize = 12.sp,
                        color = Color(0xFFFEE2E2)
                    )
                    Text(
                        text = "• Ensure your profile photo is a clear picture of you",
                        fontSize = 12.sp,
                        color = Color(0xFFFEE2E2)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// HELPER COMPOSABLES & BITMAP UTILS
// ---------------------------------------------------------------------------
@Composable
private fun GuidelineItemCard(
    stepNumber: String,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    color = Color(0xFF1E3A8A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF6B7280),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val planeProxy = image.planes[0]
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (e: Exception) {
        Log.e("ImageUtils", "Failed to convert ImageProxy to Bitmap: ${e.message}")
        null
    }
}

private fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Float, isFrontCamera: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(rotationDegrees)
    if (isFrontCamera) {
        // Mirror front camera selfie horizontally
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
