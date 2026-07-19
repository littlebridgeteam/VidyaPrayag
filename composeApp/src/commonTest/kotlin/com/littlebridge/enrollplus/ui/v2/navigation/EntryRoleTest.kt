package com.littlebridge.enrollplus.ui.v2.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class EntryRoleTest {

    @Test
    fun from_null_returns_Unknown() {
        assertEquals(EntryRole.Unknown, EntryRole.from(null))
    }

    @Test
    fun from_blank_returns_Unknown() {
        assertEquals(EntryRole.Unknown, EntryRole.from(""))
        assertEquals(EntryRole.Unknown, EntryRole.from("   "))
    }

    @Test
    fun from_PARENT_returns_Parent() {
        assertEquals(EntryRole.Parent, EntryRole.from("PARENT"))
    }

    @Test
    fun from_parent_caseInsensitive_returns_Parent() {
        assertEquals(EntryRole.Parent, EntryRole.from("parent"))
        assertEquals(EntryRole.Parent, EntryRole.from("Parent"))
        assertEquals(EntryRole.Parent, EntryRole.from("  parent  "))
    }

    @Test
    fun from_ADMIN_returns_SchoolAdmin() {
        assertEquals(EntryRole.SchoolAdmin, EntryRole.from("ADMIN"))
    }

    @Test
    fun from_SCHOOL_ADMIN_returns_SchoolAdmin() {
        assertEquals(EntryRole.SchoolAdmin, EntryRole.from("SCHOOL_ADMIN"))
    }

    @Test
    fun from_SCHOOLADMIN_returns_SchoolAdmin() {
        assertEquals(EntryRole.SchoolAdmin, EntryRole.from("SCHOOLADMIN"))
    }

    @Test
    fun from_school_admin_caseInsensitive_returns_SchoolAdmin() {
        assertEquals(EntryRole.SchoolAdmin, EntryRole.from("school_admin"))
        assertEquals(EntryRole.SchoolAdmin, EntryRole.from("SchoolAdmin"))
    }

    @Test
    fun from_SUPER_ADMIN_returns_SuperAdmin() {
        assertEquals(EntryRole.SuperAdmin, EntryRole.from("SUPER_ADMIN"))
    }

    @Test
    fun from_SUPERADMIN_returns_SuperAdmin() {
        assertEquals(EntryRole.SuperAdmin, EntryRole.from("SUPERADMIN"))
    }

    @Test
    fun from_TEACHER_returns_Teacher() {
        assertEquals(EntryRole.Teacher, EntryRole.from("TEACHER"))
        assertEquals(EntryRole.Teacher, EntryRole.from("teacher"))
    }

    @Test
    fun from_ALUMNI_returns_Alumni() {
        assertEquals(EntryRole.Alumni, EntryRole.from("ALUMNI"))
        assertEquals(EntryRole.Alumni, EntryRole.from("alumni"))
    }

    @Test
    fun from_unknownString_returns_Unknown() {
        assertEquals(EntryRole.Unknown, EntryRole.from("STUDENT"))
        assertEquals(EntryRole.Unknown, EntryRole.from("GUEST"))
        assertEquals(EntryRole.Unknown, EntryRole.from("xyz"))
    }
}
