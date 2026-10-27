package com.zaroslikov.assistant.ui.chat

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zaroslikov.assistant.R
import com.zaroslikov.assistant.speech.SpeechState
import com.zaroslikov.assistant.ui.base.AssistantUiEvent
import com.zaroslikov.assistant.ui.components.ActionEditSheet
import com.zaroslikov.assistant.ui.components.AssistantConfirmDialog
import com.zaroslikov.assistant.ui.components.AssistantMenu
import com.zaroslikov.assistant.ui.components.AssistantTopBar
import com.zaroslikov.assistant.ui.components.ChatBubble
import com.zaroslikov.assistant.ui.components.ChatInputBar
import com.zaroslikov.assistant.ui.components.ThinkingIndicator
import com.zaroslikov.assistant.ui.theme.green_6
import com.zaroslikov.assistant.ui.theme.white

private const val THINKING_KEY = "thinking"

@Composable
fun AssistantChatScreen(
    navigateBack: () -> Unit,
    viewModel: AssistantChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val onIntent = viewModel::onIntent
    val helpQuery = stringResource(R.string.assistant_help_query)

    // Последний элемент списка: индикатор «думает» или последнее сообщение
    fun lastIndex(): Int = state.messages.size + (if (state.isThinking) 1 else 0) - 1

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AssistantUiEvent.NavigateBack -> navigateBack()
                is AssistantUiEvent.Message -> snackbarHostState.showSnackbar(context.getString(event.resId))
                AssistantUiEvent.ScrollToBottom -> {
                    val index = lastIndex()
                    if (index >= 0) listState.animateScrollToItem(index)
                }
            }
        }
    }

    LaunchedEffect(state.messages.size, state.isThinking) {
        val index = lastIndex()
        if (index >= 0) listState.animateScrollToItem(index)
    }

    // Уход экрана в фон (звонок, свёрнутое приложение) — микрофон выключаем
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) onIntent(AssistantChatIntent.StopListening)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onIntent(if (granted) AssistantChatIntent.MicClicked else AssistantChatIntent.MicPermissionDenied)
    }

    val onMic: () -> Unit = {
        val granted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        // Во время записи нажатие останавливает её — разрешение уже есть
        if (granted || state.speech is SpeechState.Listening)
            onIntent(AssistantChatIntent.MicClicked)
        else
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Scaffold(
        // Панель ввода поднимается над клавиатурой вместе со всем экраном
        modifier = Modifier.imePadding(),
        topBar = {
            AssistantTopBar(
                title = stringResource(R.string.assistant_title),
                subtitle = stringResource(R.string.assistant_subtitle_offline),
                navigateBack = navigateBack,
                onMenuClick = { onIntent(AssistantChatIntent.MenuToggled(true)) },
                menuContent = {
                    AssistantMenu(
                        expanded = state.showMenu,
                        autoConfirm = state.autoConfirm,
                        onDismiss = { onIntent(AssistantChatIntent.MenuToggled(false)) },
                        onAutoConfirmChange = { onIntent(AssistantChatIntent.AutoConfirmChanged(it)) },
                        onHelp = {
                            onIntent(AssistantChatIntent.MenuToggled(false))
                            onIntent(AssistantChatIntent.Send(text = helpQuery))
                        },
                        onClear = {
                            onIntent(AssistantChatIntent.MenuToggled(false))
                            onIntent(AssistantChatIntent.ClearDialogToggled(true))
                        }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ChatInputBar(
                value = state.input,
                onValueChange = { onIntent(AssistantChatIntent.InputChanged(it)) },
                onSend = { onIntent(AssistantChatIntent.Send()) },
                onMic = onMic,
                speech = state.speech,
                speechAvailable = state.speechAvailable,
                enabled = !state.isLoading
            )
        },
        containerColor = white,
        // Отступы под системные панели уже даёт внешний Scaffold проекта
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        if (state.isLoading)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = green_6)
            }
        else
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    ChatBubble(
                        message = message,
                        onSuggestion = { onIntent(AssistantChatIntent.Send(text = it)) },
                        onConfirm = { onIntent(AssistantChatIntent.ConfirmCard(it)) },
                        onCancel = { onIntent(AssistantChatIntent.CancelCard(it)) },
                        onEdit = { onIntent(AssistantChatIntent.EditCard(it)) },
                        onUndo = { onIntent(AssistantChatIntent.UndoCard(it)) }
                    )
                }
                if (state.isThinking)
                    item(key = THINKING_KEY) { ThinkingIndicator() }
            }
    }

    state.editForm?.let { form ->
        ActionEditSheet(
            form = form,
            onChange = { onIntent(AssistantChatIntent.EditFormChanged(it)) },
            onDismiss = { onIntent(AssistantChatIntent.EditFormDismissed) },
            onSave = { onIntent(AssistantChatIntent.EditFormSaved) }
        )
    }

    if (state.showClearDialog)
        AssistantConfirmDialog(
            title = stringResource(R.string.assistant_clear_title),
            text = stringResource(R.string.assistant_clear_text),
            confirmText = stringResource(R.string.assistant_clear_confirm),
            onConfirm = { onIntent(AssistantChatIntent.ClearChat) },
            onDismiss = { onIntent(AssistantChatIntent.ClearDialogToggled(false)) }
        )
}
