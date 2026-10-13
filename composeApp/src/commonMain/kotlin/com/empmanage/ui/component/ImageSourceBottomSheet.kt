package com.empmanage.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Bottom sheet offering "Take Photo" / "Choose from Gallery", per the spec's 2A. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSourceBottomSheet(
    onTakePhoto: () -> Unit,
    onChooseFromGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onTakePhoto()
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Text("Take Photo", modifier = Modifier.padding(top = 4.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onChooseFromGallery()
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Text("Choose from Gallery", modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
