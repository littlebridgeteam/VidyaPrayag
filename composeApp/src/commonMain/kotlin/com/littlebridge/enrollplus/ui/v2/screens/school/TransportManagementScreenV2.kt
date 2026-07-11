package com.littlebridge.enrollplus.ui.v2.screens.school

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.littlebridge.enrollplus.feature.transport.domain.model.CreateAssignmentRequest
import com.littlebridge.enrollplus.feature.transport.domain.model.CreateRouteRequest
import com.littlebridge.enrollplus.feature.transport.domain.model.CreateVehicleRequest
import com.littlebridge.enrollplus.feature.transport.domain.model.TransportRoute
import com.littlebridge.enrollplus.feature.transport.domain.model.TransportVehicle
import com.littlebridge.enrollplus.feature.transport.domain.model.TransportAssignment
import com.littlebridge.enrollplus.feature.transport.presentation.TransportViewModel
import com.littlebridge.enrollplus.ui.v2.components.VBackHeader
import com.littlebridge.enrollplus.ui.v2.components.VBadge
import com.littlebridge.enrollplus.ui.v2.components.VBadgeTone
import com.littlebridge.enrollplus.ui.v2.components.VButton
import com.littlebridge.enrollplus.ui.v2.components.VButtonSize
import com.littlebridge.enrollplus.ui.v2.components.VButtonVariant
import com.littlebridge.enrollplus.ui.v2.components.VCard
import com.littlebridge.enrollplus.ui.v2.components.VInput
import com.littlebridge.enrollplus.ui.v2.screens.VSectionHeader
import com.littlebridge.enrollplus.ui.v2.screens.VStateHost
import com.littlebridge.enrollplus.ui.v2.screens.SkeletonList
import com.littlebridge.enrollplus.ui.v2.screens.collectAsStateV2
import com.littlebridge.enrollplus.ui.v2.theme.staggeredItemEntrance
import com.littlebridge.enrollplus.ui.tokens.VColors
import com.littlebridge.enrollplus.ui.tokens.VTypography
import com.littlebridge.enrollplus.core.locale.StringKeys
import com.littlebridge.enrollplus.ui.v2.locale.appString
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransportManagementScreenV2(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: TransportViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateV2()
    var showRouteForm by remember { mutableStateOf(false) }
    var showVehicleForm by remember { mutableStateOf(false) }
    var showAssignmentForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadRoutes()
        viewModel.loadVehicles()
        viewModel.loadAssignments()
    }

    LaunchedEffect(state.infoMessage) {
        if (state.infoMessage != null) {
            showRouteForm = false
            showVehicleForm = false
            showAssignmentForm = false
            viewModel.clearMessages()
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        VBackHeader(title = appString(StringKeys.TRANS_TITLE), onBack = onBack, pinRouteId = "overlay_transport")

        VStateHost(
            loading = state.isLoading,
            error = state.error,
            isEmpty = false,
            onRetry = { viewModel.loadRoutes() },
            modifier = Modifier.fillMaxSize(),
            skeleton = { SkeletonList(rows = 6) },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ── Routes section ──────────────────────────────────────────
                item {
                    VSectionHeader(
                        title = appString(StringKeys.TRANS_ROUTES).replace("{count}", state.routes.size.toString()),
                        action = {
                            VButton(
                                text = appString(StringKeys.TRANS_ADD_ROUTE),
                                variant = VButtonVariant.Primary,
                                size = VButtonSize.Sm,
                                onClick = { showRouteForm = !showRouteForm },
                            )
                        },
                    )
                }
                if (showRouteForm) {
                    item { CreateRouteForm(viewModel = viewModel) }
                }
                itemsIndexed(state.routes, key = { _, it -> it.id }) { index, route ->
                    RouteCard(
                        route = route,
                        onDelete = { viewModel.deleteRoute(route.id) },
                        modifier = Modifier.staggeredItemEntrance(index, state.routes.isNotEmpty()),
                    )
                }

                // ── Vehicles section ────────────────────────────────────────
                item {
                    Spacer(Modifier.height(8.dp))
                    VSectionHeader(
                        title = appString(StringKeys.TRANS_VEHICLES).replace("{count}", state.vehicles.size.toString()),
                        action = {
                            VButton(
                                text = appString(StringKeys.TRANS_ADD_VEHICLE),
                                variant = VButtonVariant.Primary,
                                size = VButtonSize.Sm,
                                onClick = { showVehicleForm = !showVehicleForm },
                            )
                        },
                    )
                }
                if (showVehicleForm) {
                    item { CreateVehicleForm(viewModel = viewModel, routes = state.routes) }
                }
                itemsIndexed(state.vehicles, key = { _, it -> it.id }) { index, vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onDelete = { viewModel.deleteVehicle(vehicle.id) },
                        modifier = Modifier.staggeredItemEntrance(index, state.vehicles.isNotEmpty()),
                    )
                }

                // ── Assignments section ─────────────────────────────────────
                item {
                    Spacer(Modifier.height(8.dp))
                    VSectionHeader(
                        title = appString(StringKeys.TRANS_ASSIGNMENTS).replace("{count}", state.assignments.size.toString()),
                        action = {
                            VButton(
                                text = appString(StringKeys.TRANS_ASSIGN),
                                variant = VButtonVariant.Primary,
                                size = VButtonSize.Sm,
                                onClick = { showAssignmentForm = !showAssignmentForm },
                            )
                        },
                    )
                }
                if (showAssignmentForm) {
                    item { CreateAssignmentForm(viewModel = viewModel, routes = state.routes, vehicles = state.vehicles) }
                }
                itemsIndexed(state.assignments, key = { _, it -> it.id }) { index, assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onDeactivate = { viewModel.deactivateAssignment(assignment.id) },
                        modifier = Modifier.staggeredItemEntrance(index, state.assignments.isNotEmpty()),
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun CreateRouteForm(viewModel: TransportViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(appString(StringKeys.TRANS_NEW_ROUTE), style = VTypography.h3, color = VColors.ink)
            VInput(
                value = name,
                onValueChange = { name = it },
                label = appString(StringKeys.TRANS_ROUTE_NAME),
                placeholder = appString(StringKeys.TRANS_ROUTE_PLACE),
                modifier = Modifier.fillMaxWidth(),
            )
            VInput(
                value = description,
                onValueChange = { description = it },
                label = appString(StringKeys.TRANS_DESC_OPTIONAL),
                placeholder = appString(StringKeys.TRANS_DESC_PLACE),
                modifier = Modifier.fillMaxWidth(),
            )
            VButton(
                text = appString(StringKeys.TRANS_CREATE_ROUTE),
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createRoute(CreateRouteRequest(name = name, description = description.ifBlank { null }))
                    }
                },
            )
        }
    }
}

@Composable
private fun CreateVehicleForm(viewModel: TransportViewModel, routes: List<TransportRoute>) {
    var busNumber by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("40") }
    var driverName by remember { mutableStateOf("") }
    var driverPhone by remember { mutableStateOf("") }
    var selectedRouteId by remember { mutableStateOf("") }

    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(appString(StringKeys.TRANS_NEW_VEHICLE), style = VTypography.h3, color = VColors.ink)
            VInput(
                value = busNumber,
                onValueChange = { busNumber = it },
                label = appString(StringKeys.TRANS_BUS_NUMBER),
                placeholder = appString(StringKeys.TRANS_BUS_PLACE),
                modifier = Modifier.fillMaxWidth(),
            )
            VInput(
                value = capacity,
                onValueChange = { capacity = it },
                label = appString(StringKeys.TRANS_CAPACITY),
                placeholder = "40",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
            )
            VInput(
                value = driverName,
                onValueChange = { driverName = it },
                label = appString(StringKeys.TRANS_DRIVER_NAME),
                modifier = Modifier.fillMaxWidth(),
            )
            VInput(
                value = driverPhone,
                onValueChange = { driverPhone = it },
                label = appString(StringKeys.TRANS_DRIVER_PHONE),
                modifier = Modifier.fillMaxWidth(),
            )
            if (routes.isNotEmpty()) {
                Text(appString(StringKeys.TRANS_ASSIGN_ROUTE), style = VTypography.caption, color = VColors.ink2)
                routes.forEach { route ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRouteId = if (selectedRouteId == route.id) "" else route.id }
                            .padding(vertical = 4.dp),
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedRouteId == route.id,
                            onClick = { selectedRouteId = if (selectedRouteId == route.id) "" else route.id },
                        )
                        Text(route.name, style = VTypography.body, color = VColors.ink)
                    }
                }
            }
            VButton(
                text = appString(StringKeys.TRANS_CREATE_VEHICLE),
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                onClick = {
                    if (busNumber.isNotBlank()) {
                        viewModel.createVehicle(
                            CreateVehicleRequest(
                                busNumber = busNumber,
                                capacity = capacity.toIntOrNull() ?: 40,
                                routeId = selectedRouteId.ifBlank { null },
                                driverName = driverName.ifBlank { null },
                                driverPhone = driverPhone.ifBlank { null },
                            )
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun CreateAssignmentForm(
    viewModel: TransportViewModel,
    routes: List<TransportRoute>,
    vehicles: List<TransportVehicle>,
) {
    var studentId by remember { mutableStateOf("") }
    var selectedRouteId by remember { mutableStateOf("") }
    var selectedStopId by remember { mutableStateOf("") }
    var selectedVehicleId by remember { mutableStateOf("") }
    var feeAmount by remember { mutableStateOf("") }
    var feeDueDate by remember { mutableStateOf("") }

    val selectedRoute = routes.find { it.id == selectedRouteId }

    VCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(appString(StringKeys.TRANS_ASSIGN_STUDENT), style = VTypography.h3, color = VColors.ink)
            VInput(
                value = studentId,
                onValueChange = { studentId = it },
                label = appString(StringKeys.TRANS_STUDENT_ID),
                placeholder = appString(StringKeys.TRANS_STUDENT_ID_PLACE),
                modifier = Modifier.fillMaxWidth(),
            )
            if (routes.isNotEmpty()) {
                Text(appString(StringKeys.TRANS_SELECT_ROUTE), style = VTypography.caption, color = VColors.ink2)
                routes.forEach { route ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedRouteId = if (selectedRouteId == route.id) "" else route.id
                                selectedStopId = ""
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedRouteId == route.id,
                            onClick = {
                                selectedRouteId = if (selectedRouteId == route.id) "" else route.id
                                selectedStopId = ""
                            },
                        )
                        Text(route.name, style = VTypography.body, color = VColors.ink)
                    }
                }
            }
            if (selectedRoute != null && selectedRoute.stops.isNotEmpty()) {
                Text(appString(StringKeys.TRANS_SELECT_STOP), style = VTypography.caption, color = VColors.ink2)
                selectedRoute.stops.forEach { stop ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStopId = if (selectedStopId == stop.id) "" else stop.id
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedStopId == stop.id,
                            onClick = { selectedStopId = if (selectedStopId == stop.id) "" else stop.id },
                        )
                        Text("${stop.name} (#${stop.sequence})", style = VTypography.body, color = VColors.ink)
                    }
                }
            }
            if (vehicles.isNotEmpty()) {
                Text(appString(StringKeys.TRANS_SELECT_VEHICLE), style = VTypography.caption, color = VColors.ink2)
                vehicles.forEach { vehicle ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedVehicleId = if (selectedVehicleId == vehicle.id) "" else vehicle.id
                            }
                            .padding(vertical = 4.dp),
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = selectedVehicleId == vehicle.id,
                            onClick = { selectedVehicleId = if (selectedVehicleId == vehicle.id) "" else vehicle.id },
                        )
                        Text(vehicle.busNumber, style = VTypography.body, color = VColors.ink)
                    }
                }
            }
            VInput(
                value = feeAmount,
                onValueChange = { feeAmount = it },
                label = appString(StringKeys.TRANS_FEE_AMOUNT),
                placeholder = appString(StringKeys.TRANS_FEE_PLACE),
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                modifier = Modifier.fillMaxWidth(),
            )
            VInput(
                value = feeDueDate,
                onValueChange = { feeDueDate = it },
                label = appString(StringKeys.TRANS_FEE_DUE_DATE),
                placeholder = "YYYY-MM-DD",
                modifier = Modifier.fillMaxWidth(),
            )
            VButton(
                text = appString(StringKeys.TRANS_ASSIGN_BTN),
                variant = VButtonVariant.Primary,
                size = VButtonSize.Sm,
                enabled = studentId.isNotBlank() && selectedRouteId.isNotBlank() && selectedStopId.isNotBlank() && selectedVehicleId.isNotBlank(),
                onClick = {
                    viewModel.createAssignment(
                        CreateAssignmentRequest(
                            studentId = studentId,
                            routeId = selectedRouteId,
                            stopId = selectedStopId,
                            vehicleId = selectedVehicleId,
                            feeAmount = feeAmount.toDoubleOrNull(),
                            feeDueDate = feeDueDate.ifBlank { null },
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun RouteCard(
    route: TransportRoute,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(route.name, style = VTypography.h3, color = VColors.ink)
                route.description?.let {
                    Text(it, style = VTypography.caption, color = VColors.ink2)
                }
                Text(
                    appString(StringKeys.TRANS_STOPS).replace("{count}", route.stops.size.toString()),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
            }
            VBadge(
                text = if (route.isActive) appString(StringKeys.TRANS_ACTIVE) else appString(StringKeys.TRANS_INACTIVE),
                tone = if (route.isActive) VBadgeTone.Success else VBadgeTone.Neutral,
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VButton(
                text = appString(StringKeys.COMMON_BUTTON_DELETE),
                variant = VButtonVariant.Destructive,
                size = VButtonSize.Sm,
                onClick = onDelete,
            )
        }
    }
}

@Composable
private fun VehicleCard(
    vehicle: TransportVehicle,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(vehicle.busNumber, style = VTypography.h3, color = VColors.ink)
                Text(
                    appString(StringKeys.TRANS_CAPACITY_LABEL).replace("{count}", vehicle.capacity.toString()),
                    style = VTypography.caption,
                    color = VColors.ink2,
                )
                vehicle.driverName?.let {
                    Text(appString(StringKeys.TRANS_DRIVER_LABEL).replace("{name}", it), style = VTypography.caption, color = VColors.ink2)
                }
            }
            VBadge(
                text = if (vehicle.isActive) appString(StringKeys.TRANS_ACTIVE) else appString(StringKeys.TRANS_INACTIVE),
                tone = if (vehicle.isActive) VBadgeTone.Success else VBadgeTone.Neutral,
            )
        }
        Spacer(Modifier.height(8.dp))
        VButton(
            text = appString(StringKeys.COMMON_BUTTON_DELETE),
            variant = VButtonVariant.Destructive,
            size = VButtonSize.Sm,
            onClick = onDelete,
        )
    }
}

@Composable
private fun AssignmentCard(
    assignment: TransportAssignment,
    onDeactivate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                assignment.studentName ?: assignment.studentId,
                style = VTypography.h3,
                color = VColors.ink,
            )
            Text(
                appString(StringKeys.TRANS_ROUTE_LABEL).replace("{name}", assignment.routeName ?: assignment.routeId),
                style = VTypography.body,
                color = VColors.ink2,
            )
            Text(
                appString(StringKeys.TRANS_STOP_LABEL).replace("{name}", assignment.stopName ?: assignment.stopId),
                style = VTypography.caption,
                color = VColors.ink2,
            )
            Text(
                appString(StringKeys.TRANS_BUS_LABEL).replace("{name}", assignment.busNumber ?: assignment.vehicleId),
                style = VTypography.caption,
                color = VColors.ink2,
            )
            Spacer(Modifier.height(8.dp))
            VButton(
                text = appString(StringKeys.TRANS_DEACTIVATE),
                variant = VButtonVariant.Destructive,
                size = VButtonSize.Sm,
                onClick = onDeactivate,
            )
        }
    }
}
