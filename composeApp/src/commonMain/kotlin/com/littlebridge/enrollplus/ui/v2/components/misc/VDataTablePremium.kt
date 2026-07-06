package com.littlebridge.enrollplus.ui.v2.components.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.tokens.VColors
import com.littlebridge.enrollplus.ui.v2.tokens.VShapes
import com.littlebridge.enrollplus.ui.v2.tokens.VTypography

data class VDataTableRowPremium(val cells: List<String>)

@Composable
fun VDataTablePremium(
    headers: List<String>,
    rows: List<VDataTableRowPremium>,
    modifier: Modifier = Modifier,
    onRowClick: ((Int) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(VShapes.Xl)
            .background(VColors.SurfaceContainerLowest),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VColors.SurfaceContainerHigh)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            headers.forEachIndexed { index, header ->
                Text(
                    text = header,
                    style = VTypography.HeroStatLabel.copy(color = VColors.OnSurfaceVariant),
                    textAlign = if (index == 0) TextAlign.Start else TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        LazyColumn {
            items(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VColors.SurfaceContainerLowest)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    row.cells.forEachIndexed { index, cell ->
                        Text(
                            text = cell,
                            style = VTypography.ThreadPreview.copy(
                                color = if (index == 0) VColors.OnSurface else VColors.OnSurfaceVariant,
                            ),
                            textAlign = if (index == 0) TextAlign.Start else TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
