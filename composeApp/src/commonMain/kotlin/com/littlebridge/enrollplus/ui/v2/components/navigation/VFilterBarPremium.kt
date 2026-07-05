package com.littlebridge.enrollplus.ui.v2.components.navigation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.ui.v2.components.form.VSearchField

@Composable
fun VFilterBarPremium(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    filters: List<Pair<String, Boolean>> = emptyList(),
    onFilterClick: (Int) -> Unit = {},
    showSearch: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showSearch) {
            VSearchField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }
        if (filters.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                filters.forEachIndexed { index, (label, active) ->
                    VFilterChip(
                        label = label,
                        active = active,
                        onClick = { onFilterClick(index) },
                    )
                }
            }
        }
    }
}
