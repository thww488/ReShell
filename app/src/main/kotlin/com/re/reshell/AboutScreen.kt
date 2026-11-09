package com.re.reshell

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val versionName = BuildConfig.VERSION_NAME

    Scaffold(
        topBar = {
            TopAppBar(
                title = "关于",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. 头部版本信息卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_reshell_mark),
                        contentDescription = "Icon",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("ReShell", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "版本 v$versionName",
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            // 2. 作者与链接跳转卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                cornerRadius = 16.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thww488"))
                                )
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("作者主页", fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Text(
                            "Northwing_thww",
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                    
                    // Miuix 风格的分割线（用 1dp 的 Box 实现）
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .background(MiuixTheme.colorScheme.dividerLine)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/thww488/ReShell"))
                                )
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("开源仓库", fontSize = 15.sp, modifier = Modifier.weight(1f))
                        Text(
                            "thww488/ReShell",
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            // 3. 开源致谢列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "开源致谢",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(12.dp))
                    
                    val libs = listOf(
                        Triple("Shizuku", "Apache License 2.0", "https://github.com/RikkaApps/Shizuku"),
                        Triple("Miuix", "Apache License 2.0", "https://github.com/miuix-kotlin-multiplatform/miuix"),
                        Triple("AndroidX · Compose", "Apache License 2.0", "https://cs.android.com/androidx/platform/frameworks/support"),
                        Triple("Kotlin", "Apache License 2.0", "https://github.com/JetBrains/kotlin")
                    )
                    
                    libs.forEachIndexed { index, (name, license, url) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Text(
                                license,
                                fontSize = 12.sp,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }
                        // 在除最后一项外的所有项下面画分割线
                        if (index != libs.lastIndex) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MiuixTheme.colorScheme.dividerLine)
                            )
                        }
                    }
                }
            }
        }
    }
}