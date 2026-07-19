package com.littlebridge.enrollplus.platform

import kotlin.test.Test
import kotlin.test.assertEquals

class BiometricEnumsTest {

    // ── BiometricMethod wire values ────────────────────────────────────────

    @Test
    fun biometricMethod_biometric_wireIsBiometric() {
        assertEquals("biometric", BiometricMethod.Biometric.wire)
    }

    @Test
    fun biometricMethod_pin_wireIsPin() {
        assertEquals("pin", BiometricMethod.Pin.wire)
    }

    @Test
    fun biometricMethod_manual_wireIsManual() {
        assertEquals("manual", BiometricMethod.Manual.wire)
    }

    @Test
    fun biometricMethod_allWireValuesAreUnique() {
        val wires = BiometricMethod.entries.map { it.wire }
        assertEquals(wires.size, wires.toSet().size, "BiometricMethod wire values must be unique")
    }

    @Test
    fun biometricMethod_hasThreeEntries() {
        assertEquals(3, BiometricMethod.entries.size)
    }

    // ── BiometricCapability ────────────────────────────────────────────────

    @Test
    fun biometricCapability_hasThreeEntries() {
        assertEquals(3, BiometricCapability.entries.size)
    }

    @Test
    fun biometricCapability_containsBiometricAvailable() {
        assertEquals(
            BiometricCapability.BiometricAvailable,
            BiometricCapability.entries.find { it.name == "BiometricAvailable" },
        )
    }

    @Test
    fun biometricCapability_containsDeviceCredentialOnly() {
        assertEquals(
            BiometricCapability.DeviceCredentialOnly,
            BiometricCapability.entries.find { it.name == "DeviceCredentialOnly" },
        )
    }

    @Test
    fun biometricCapability_containsNone() {
        assertEquals(
            BiometricCapability.None,
            BiometricCapability.entries.find { it.name == "None" },
        )
    }

    // ── BiometricResult ────────────────────────────────────────────────────

    @Test
    fun biometricResult_success_holdsMethod() {
        val result = BiometricResult.Success(BiometricMethod.Biometric)
        assertEquals(BiometricMethod.Biometric, result.method)
    }

    @Test
    fun biometricResult_success_withPin() {
        val result = BiometricResult.Success(BiometricMethod.Pin)
        assertEquals(BiometricMethod.Pin, result.method)
    }

    @Test
    fun biometricResult_success_withManual() {
        val result = BiometricResult.Success(BiometricMethod.Manual)
        assertEquals(BiometricMethod.Manual, result.method)
    }

    @Test
    fun biometricResult_cancelled_isDataObject() {
        val result = BiometricResult.Cancelled
        assertEquals(BiometricResult.Cancelled, result)
    }

    @Test
    fun biometricResult_failed_holdsMessage() {
        val result = BiometricResult.Failed("Lockout")
        assertEquals("Lockout", result.message)
    }

    @Test
    fun biometricResult_failed_withEmptyMessage() {
        val result = BiometricResult.Failed("")
        assertEquals("", result.message)
    }
}
