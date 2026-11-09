package com.re.reshell

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight // 核心修复：补上 FontWeight 导入
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme // 核心修复：补上 MiuixTheme 导入

@Composable
fun SettingsScreen(
    isShizukuEnabled: Boolean,
    currentDefaultDir: String,
    onBack: () -> Unit,
    onShizukuToggle: (Boolean) -> Unit,
    onUpdateDir: (String) -> Unit,
    onOpenAbout: () -> Unit
) {
    var dirInput by remember { mutableStateOf(currentDefaultDir) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "设置",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
        ) {
            // 1. Shizuku 开关
            Card(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("启用 Shizuku 特权", style = MiuixTheme.textStyles.title4)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "关闭后 App 将不再请求授权，直接以本地普通应用权限运行。",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Switch(
                        checked = isShizukuEnabled,
                        onCheckedChange = onShizukuToggle
                    )
                }
            }

            // 2. 启动目录设置
            Card(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("默认启动目录", style = MiuixTheme.textStyles.title4)
                    Spacer(Modifier.height(8.dp))
                    
                    TextField(
                        value = dirInput,
                        onValueChange = { dirInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        // 修复：直接传 String
                        label = "输入路径", 
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (dirInput.isNotBlank() && dirInput.startsWith("/")) {
                                    onUpdateDir(dirInput)
                                }
                            }
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (dirInput.isNotBlank() && dirInput.startsWith("/")) {
                                onUpdateDir(dirInput)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("应用目录")
                    }
                }
            }

            // 3. 关于入口
            Card(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp,
                onClick = onOpenAbout
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "About",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("关于 ReShell", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("版本、作者与开源许可", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}