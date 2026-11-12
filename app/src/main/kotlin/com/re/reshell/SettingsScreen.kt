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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
                title = stringResource(id = R.string.settings),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            Card(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(id = R.string.enable_shizuku), style = MiuixTheme.textStyles.title4)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(id = R.string.enable_shizuku_desc),
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

            Card(cornerRadius = 16.dp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(id = R.string.default_dir), style = MiuixTheme.textStyles.title4)
                    Spacer(Modifier.height(8.dp))
                    
                    TextField(
                        value = dirInput,
                        onValueChange = { dirInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(id = R.string.default_dir_hint),
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
                        Text(stringResource(id = R.string.apply_dir))
                    }
                }
            }

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
                        Text(stringResource(id = R.string.about_reshell), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(stringResource(id = R.string.about_desc), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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