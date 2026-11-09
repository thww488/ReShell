package com.re.reshell

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun MainScreen(
    isConnected: Boolean,
    isRoot: Boolean,
    privilegeSource: String,
    currentUser: String,
    outputs: List<String>,
    bookmarks: List<String>,
    onExecute: (String) -> Unit,
    onReconnect: () -> Unit,
    onClear: () -> Unit,
    onAddBookmark: (String) -> Unit,
    onRemoveBookmark: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scrollBehavior = MiuixScrollBehavior()

    var showBookmarks by remember { mutableStateOf(false) }

    val subtitleText = if (!isConnected) {
        "正在连接..."
    } else if (privilegeSource == "Shizuku") {
        "已连接 Shizuku 特权 $currentUser"
    } else if (privilegeSource == "SU") {
        "已连接 SU 特权 $currentUser"
    } else {
        "已连接本地 $currentUser"
    }

    LaunchedEffect(outputs.size) {
        if (outputs.isNotEmpty()) {
            listState.animateScrollToItem(outputs.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "ReShell",
                largeTitle = "ReShell",
                subtitle = subtitleText,
                scrollBehavior = scrollBehavior,
                actions = {
                    Box {
                        IconButton(onClick = { showBookmarks = true }) {
                            Icon(Icons.Default.Star, contentDescription = "Bookmarks")
                        }
                        DropdownMenu(
                            expanded = showBookmarks,
                            onDismissRequest = { showBookmarks = false }
                        ) {
                            if (bookmarks.isEmpty()) {
                                DropdownMenuItem(text = { Text("暂无收藏") }, onClick = { showBookmarks = false })
                            } else {
                                bookmarks.forEach { cmd ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(cmd, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1)
                                                Spacer(Modifier.width(8.dp))
                                                IconButton(
                                                    onClick = { onRemoveBookmark(cmd) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Text("×", fontSize = 18.sp, color = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        },
                                        onClick = {
                                            showBookmarks = false
                                            onExecute(cmd)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Logs")
                    }

                    IconButton(onClick = onReconnect) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }

                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 12.dp),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "指令框",
                        style = MiuixTheme.textStyles.title4,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.fillMaxWidth(),
                        // 核心修复：直接传 String，千万不要加大括号
                        label = "输入 Shell 指令",
                        leadingIcon = {
                            Text(
                                text = if (isRoot) "#" else "$",
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (input.isNotBlank()) {
                                    onExecute(input)
                                    input = ""
                                }
                            }
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (input.isNotBlank()) onAddBookmark(input)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("收藏")
                        }
                        
                        Button(
                            onClick = {
                                if (input.isNotBlank()) {
                                    onExecute(input)
                                    input = ""
                                }
                            },
                            modifier = Modifier.weight(2f)
                        ) {
                            Text("执行")
                        }
                    }
                }
            }

            SelectionContainer {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    items(outputs) { text ->
                        Text(
                            text = AnsiParser.parse(text),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}