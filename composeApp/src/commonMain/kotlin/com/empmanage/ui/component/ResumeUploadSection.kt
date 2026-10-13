package com.empmanage.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.empmanage.domain.model.ResumeMetadata
import com.empmanage.ui.util.FileSizeFormatter

/**
 * Shows the selected resume (file-type icon, name, size, remove button)
 * per Section 2B, or an "Upload Resume" prompt when none is selected yet.
 */
@Composable
fun ResumeUploadSection(
    resumeMetadata: ResumeMetadata?,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (resumeMetadata == null) {
        OutlinedButton(onClick = onUploadClick, modifier = modifier.fillMaxWidth()) {
            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(20.dp))
            Text("Upload Resume", modifier = Modifier.padding(start = 8.dp))
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (resumeMetadata.mimeType == "application/pdf") {
                            Icons.Default.PictureAsPdf
                        } else {
                            Icons.Default.Description
                        },
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(resumeMetadata.fileName, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            FileSizeFormatter.format(resumeMetadata.fileSizeBytes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onRemoveClick) {
                    Icon(Icons.Default.Close, contentDescription = "Remove resume")
                }
            }
        }
    }
}
