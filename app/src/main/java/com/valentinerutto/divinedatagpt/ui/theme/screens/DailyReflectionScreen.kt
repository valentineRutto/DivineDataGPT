package com.valentinerutto.divinedatagpt.ui.theme.screens

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.valentinerutto.divinedatagpt.DivineDataViewModel
import com.valentinerutto.divinedatagpt.ui.theme.CardBackground
import com.valentinerutto.divinedatagpt.ui.theme.DarkBackground
import com.valentinerutto.divinedatagpt.ui.theme.PurpleAccent
import com.valentinerutto.divinedatagpt.ui.theme.PurplePrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextPrimary
import com.valentinerutto.divinedatagpt.ui.theme.ReflectionTheme.TextSecondary
import com.valentinerutto.divinedatagpt.ui.theme.TextMuted
import com.valentinerutto.divinedatagpt.util.shareReflection
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReflectionScreen(
    onBack: () -> Unit,
    viewModel: DivineDataViewModel = koinViewModel()
) {
    val uiState by viewModel.dailyUiState.collectAsState()

    val context = LocalContext.current
    val rootView = LocalView.current
    val cardBounds = androidx.compose.runtime.remember { mutableStateOf<Rect?>(null) }


    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "DAILY REFLECTION", color = TextPrimary,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ChevronLeft, contentDescription = "Back",
                            tint = TextPrimary, modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {

                        viewModel.loadVerseOfDay()

                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.isLoading) {
                Box(Modifier
                    .fillMaxWidth()
                    .height(400.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PurplePrimary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Preparing your reflection...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                uiState.reflection?.let { reflection ->
                    // ── Scripture Card ───────────────────────────


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .heightIn(min = 380.dp)
                            .onGloballyPositioned { coordinates ->
                                cardBounds.value = coordinates.boundsInWindow()
                            }
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF8B4513), Color(0xFF2C4A2E), Color(0xFF1A1A3E))
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(3.dp)
                                        .background(PurplePrimary, RoundedCornerShape(2.dp))
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "SCRIPTURE",
                                    color = Color.White.copy(0.7f),
                                    fontSize = 11.sp,
                                    letterSpacing = 2.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "\"${reflection.verse}\"",
                                    color = TextPrimary,
                                    fontSize = 26.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 36.sp
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "— ${reflection.reference}",
                                    color = Color.White.copy(0.85f),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(Modifier.height(22.dp))

                            // AI Insight
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(0.5f))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("✦", color = PurpleAccent, fontSize = 14.sp)
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "Divine INSIGHT",
                                            color = PurpleAccent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = reflection.insight,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        lineHeight = 21.sp
                                    )
                                }
                            }

                            Spacer(Modifier.height(18.dp))

                            Text(
                                text = "✨ Daily Reflection from Divine AI",
                                color = Color.White.copy(0.82f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Share Your Light ─────────────────────────
                    Text(
                        text = "SHARE YOUR LIGHT",
                        color = TextMuted,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        ShareButton(
                            color = Color(0xFF25D366),
                            icon = Icons.Default.Message,
                            label = "WhatsApp",
                            onClick = {
                                captureScriptureCardBitmap(
                                    rootView,
                                    cardBounds.value
                                )?.let { bitmap ->
                                    shareScriptureCardToWhatsApp(context, bitmap)
                                } ?: Toast.makeText(
                                    context,
                                    "Card is not ready to share yet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        )
                        Spacer(Modifier.width(20.dp))
                        ShareButton(
                            color = CardBackground,
                            icon = Icons.Default.MoreHoriz,
                            label = "More",
                            onClick = { shareReflection(context, reflection) }

                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // ── Save Button ──────────────────────────────
                    Button(
                        onClick = {
                            captureScriptureCardBitmap(rootView, cardBounds.value)?.let { bitmap ->
                                saveScriptureCardToGallery(context, bitmap)
                            } ?: Toast.makeText(
                                context,
                                "Card is not ready to save yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Download, contentDescription = null,
                            tint = TextPrimary, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Save to Gallery", color = TextPrimary,
                            fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }

                uiState.error?.let { error ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "⚠️ Couldn't load reflection",
                                color = TextSecondary,
                                fontSize = 16.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                error,
                                color = TextMuted,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadVerseOfDay() },
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareButton(
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = TextSecondary, fontSize = 12.sp)
    }
}

private fun captureScriptureCardBitmap(
    rootView: android.view.View,
    bounds: Rect?
): Bitmap? {
    if (bounds == null) return null

    val fullBitmap = rootView.drawToBitmap()
    val x = bounds.left.roundToInt().coerceIn(0, fullBitmap.width - 1)
    val y = bounds.top.roundToInt().coerceIn(0, fullBitmap.height - 1)
    val right = bounds.right.roundToInt().coerceIn(x + 1, fullBitmap.width)
    val bottom = bounds.bottom.roundToInt().coerceIn(y + 1, fullBitmap.height)

    return Bitmap.createBitmap(
        fullBitmap,
        x,
        y,
        right - x,
        bottom - y
    )
}

private fun saveScriptureCardToGallery(
    context: Context,
    bitmap: Bitmap
) {
    val fileName = "DivineData_${System.currentTimeMillis()}.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            "${Environment.DIRECTORY_PICTURES}/DivineData"
        )
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    if (uri == null) {
        Toast.makeText(context, "Could not save image", Toast.LENGTH_SHORT).show()
        return
    }

    runCatching {
        resolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        Toast.makeText(context, "Saved to gallery", Toast.LENGTH_SHORT).show()
    }.onFailure {
        resolver.delete(uri, null, null)
        Toast.makeText(context, "Could not save image", Toast.LENGTH_SHORT).show()
    }
}

private fun shareScriptureCardToWhatsApp(
    context: Context,
    bitmap: Bitmap
) {
    val imageDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val imageFile = File(imageDir, "scripture_card.png")
    FileOutputStream(imageFile).use { output ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
    }

    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        setPackage("com.whatsapp")
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            "Share scripture card"
        )
        context.startActivity(chooser)
    }
}
