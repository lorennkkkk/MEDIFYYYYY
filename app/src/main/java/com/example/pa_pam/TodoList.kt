package com.example.pa_pam

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.pa_pam.data.repositories.Todo
import com.example.pa_pam.viewmodel.ProfileViewModel
import com.example.pa_pam.viewmodel.TodoViewModel


@Composable
fun TodoScreen(
    vm: TodoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    profileVm: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onEditProfileClick: () -> Unit = {}
) {


    LaunchedEffect(Unit) {
        vm.loadTodos()
    }

    val username by profileVm.username.collectAsState()
    val todos by vm.todos.collectAsState()

    val teal = Color(0xFF0B7A81)

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFCEEAEA), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = username,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = teal,
                modifier = Modifier.clickable { onEditProfileClick() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(teal, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {


            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color.White,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = teal)
            }



            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(todos) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onChecked = { vm.deleteTodo(todo) }
                    )
                }
            }
        }
    }



    if (showDialog) {
        AddTodoDialog(
            onDismiss = { showDialog = false },
            onAdd = { title, time ->
                vm.addTodo(title, time)
                showDialog = false
            }
        )
    }
}



@Composable
fun TodoItemCard(
    todo: Todo,
    onChecked: () -> Unit
) {
    val cardBg = Color(0xFFDFF4F8)

    var checked by remember { mutableStateOf(false) }

    if (!checked) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onChecked()
                }
            )

            Text(
                text = todo.title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = todo.time,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}




@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {

    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Jadwal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Jadwal minum obat") }
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Waktu (contoh: 3:00 PM)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && time.isNotBlank()) {
                    onAdd(title, time)
                }
            }) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
