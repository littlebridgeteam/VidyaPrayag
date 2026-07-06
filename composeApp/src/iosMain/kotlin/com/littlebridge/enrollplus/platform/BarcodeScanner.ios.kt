package com.littlebridge.enrollplus.platform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField

@Composable
actual fun rememberBarcodeScanner(
    onScanned: (String) -> Unit,
): BarcodeScanner = remember { IosBarcodeScanner(onScanned) }

@Composable
actual fun BarcodeScannerView(
    onScanned: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier,
) {
    var manualBarcode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Enter Barcode")
            TextButton(onClick = onClose) { Text("Close") }
        }

        OutlinedTextField(
            value = manualBarcode,
            onValueChange = { manualBarcode = it },
            label = { Text("Barcode") },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                if (manualBarcode.isNotBlank()) {
                    onScanned(manualBarcode)
                    manualBarcode = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Submit") }
    }
}

private class IosBarcodeScanner(
    private val onScanned: (String) -> Unit,
) : BarcodeScanner {
    override val hasCamera: Boolean = true
    override fun start() {}
    override fun stop() {}
}
