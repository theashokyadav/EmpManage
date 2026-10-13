package com.empmanage.ui.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow

/**
 * Renders [text] with every case-insensitive occurrence of [query]
 * highlighted — the search-result highlighting bonus for Section 4.
 */
@Composable
fun HighlightedText(
    text: String,
    query: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val annotated = buildAnnotatedString {
        if (query.isBlank()) {
            append(text)
        } else {
            val needle = query.trim().lowercase()
            val haystack = text.lowercase()
            var start = 0
            while (start < text.length) {
                val matchIndex = haystack.indexOf(needle, start)
                if (matchIndex < 0) {
                    append(text.substring(start))
                    break
                }
                append(text.substring(start, matchIndex))
                withStyle(
                    SpanStyle(
                        background = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                ) {
                    append(text.substring(matchIndex, matchIndex + needle.length))
                }
                start = matchIndex + needle.length
            }
        }
    }
    Text(
        text = annotated,
        style = LocalTextStyle.current,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow
    )
}
