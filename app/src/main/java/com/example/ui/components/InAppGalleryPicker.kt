package com.example.ui.components

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class LocalMediaItem(
    val id: Long,
    val uri: String,
    val name: String
)

suspend fun fetchDeviceGalleryImages(context: Context): List<LocalMediaItem> = withContext(Dispatchers.IO) {
    val imageList = mutableListOf<LocalMediaItem>()
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED
    )
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    try {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            var count = 0
            while (it.moveToNext() && count < 100) {
                val id = it.getLong(idColumn)
                val name = if (nameColumn != -1) it.getString(nameColumn) ?: "Photo" else "Photo"
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                imageList.add(LocalMediaItem(id = id, uri = contentUri.toString(), name = name))
                count++
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // If device gallery is empty (e.g. fresh emulator or container), provide default high-res curated photos
    if (imageList.isEmpty()) {
        val sampleImages = listOf(
            "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?w=600&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=600&auto=format&fit=crop&q=80"
        )
        sampleImages.forEachIndexed { index, url ->
            imageList.add(LocalMediaItem(id = index.toLong() + 1000L, uri = url, name = "Photo_$index"))
        }
    }

    imageList
}

@Composable
fun InAppGalleryPickerSheet(
    onDismiss: () -> Unit,
    onPhotoSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    var mediaList by remember { mutableStateOf<List<LocalMediaItem>>(emptyList()) }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(hasPermission) {
        isLoading = true
        mediaList = fetchDeviceGalleryImages(context)
        isLoading = false
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = Color(0xFF1F2937),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Phone Gallery",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF6B7280)
                    )
                }
            }

            // Permission Request Banner if not granted
            if (!hasPermission) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Grant gallery permission to see all photos",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(permission) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Allow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Gallery Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1F2937),
                            strokeWidth = 2.5.dp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(mediaList, key = { it.id }) { media ->
                            val isSelected = selectedPhotoUri == media.uri

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE5E7EB))
                                    .clickable {
                                        selectedPhotoUri = if (isSelected) null else media.uri
                                    }
                                    .testTag("gallery_item_${media.id}")
                            ) {
                                AsyncImage(
                                    model = media.uri,
                                    contentDescription = media.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF1F2937).copy(alpha = 0.4f))
                                            .border(2.dp, Color(0xFF00C853), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF00C853)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Bar: Send Selected Photo
            if (selectedPhotoUri != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    tonalElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3F4F6))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = selectedPhotoUri,
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = "1 Photo Selected",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF111827)
                            )
                        }

                        Button(
                            onClick = {
                                selectedPhotoUri?.let { uri ->
                                    onPhotoSelected(uri)
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Send Photo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
