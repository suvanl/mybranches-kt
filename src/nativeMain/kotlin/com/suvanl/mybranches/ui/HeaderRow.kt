package com.suvanl.mybranches.ui

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.layout.background
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.suvanl.mybranches.ui.theme.ThemeColor

@Composable
fun HeaderRow(
    branchPattern: String,
    help: String,
    showHelp: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2),
        modifier = modifier,
    ) {
        Text(
            value = branchPattern,
            color = ThemeColor.onPrimaryContainerDark,
            modifier = Modifier
                .background(color = ThemeColor.primaryContainerDark)
                .padding(horizontal = 1),
        )

        Text(
            value = if (showHelp) help else "(? for help)",
        )
    }
}
