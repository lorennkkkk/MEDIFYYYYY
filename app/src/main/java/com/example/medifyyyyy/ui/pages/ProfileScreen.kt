package com.example.medifyyyyy.ui.pages

import android.net.http.SslCertificate.saveState
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.medifyyyyy.ui.viewmodel.ProfileViewModel
import com.example.medifyyyyy.ui.viewmodel.SaveProfileState

@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    onBack: () -> Unit = {},
    onLogoutNavigate: () -> Unit = {}
) {

    val teal = Color(0xFF0B7A81)
    val lightPink = Color(0xFFFFD4D4)


    LaunchedEffect(true) {
        vm.loadProfile()
    }

    val firstname by vm.firstname.collectAsState()
    val lastname by vm.lastname.collectAsState()
    val username by vm.username.collectAsState()
    val email by vm.email.collectAsState()
    val imageUrl by vm.imageUrl.collectAsState()

    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }

    val context = LocalContext.current

    val saveState by vm.saveState.collectAsState()
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveProfileState.Success -> {
                Toast.makeText(
                    context,
                    "Perubahan berhasil disimpan",
                    Toast.LENGTH_SHORT
                ).show()

                // Ambil data baru
                vm.loadProfile()

                // JANGAN reset selectedImage di sini
                vm.resetSaveState()
            }

            is SaveProfileState.Error -> {
                Toast.makeText(
                    context,
                    (saveState as SaveProfileState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                vm.resetSaveState()
            }

            else -> Unit
        }
    }

    LaunchedEffect(imageUrl) {
        if (!imageUrl.isNullOrBlank()) {
            selectedImage = null
        }
    }



    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val stream = context.contentResolver.openInputStream(uri)
                selectedImage = stream?.readBytes()
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
            .padding(16.dp)
    ) {


        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier.clickable { onBack() }
            )
            Text(
                text = "Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {

                when {
                    selectedImage != null -> {
                        AsyncImage(
                            model = selectedImage,
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    imageUrl != null && imageUrl!!.isNotBlank() -> {
                        key(imageUrl) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    else -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            tint = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = {
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(teal, CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        // EDITABLE FIELDS
        EditableField("Nama Depan", firstname, vm::updateFirstname)
        EditableField("Nama Belakang", lastname, vm::updateLastname)
        EditableField("Username", username, vm::updateUsername)

        ProfileFieldReadOnly("Email", email, Icons.Default.Email)

        Spacer(modifier = Modifier.height(45.dp))

        // SAVE BUTTON
        Button(
            onClick = { vm.saveProfileChanges(selectedImage) },
            enabled = saveState !is SaveProfileState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = teal),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (saveState is SaveProfileState.Loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Save", color = Color.White)
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        // LOGOUT
        Button(
            onClick = {
                vm.logout()
                onLogoutNavigate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = lightPink),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Keluar", color = Color.Red)
        }
    }
}



@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    val teal = Color(0xFF0B7A81)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = teal,
            focusedBorderColor = teal
        ),
        label = { Text(label) }
    )
}

@Composable
fun ProfileFieldReadOnly(label: String, value: String, icon: ImageVector) {
    val teal = Color(0xFF0B7A81)

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            Icon(icon, contentDescription = null, tint = teal)
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = teal,
            focusedBorderColor = teal
        ),
        label = { Text(label) }
    )
}


