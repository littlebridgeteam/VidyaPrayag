package com.littlebridge.enrollplus.ui.v2.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * VIcons — the curated icon set for the new UI.
 *
 * The design (`UI screens`) uses `lucide-react`. We map each lucide glyph to the closest stable
 * Material core icon (proven to compile in this project's existing UI). For glyphs without a safe
 * 1:1 Material core equivalent we hand-author a Material-style 24dp [ImageVector] (e.g. [Spinner],
 * [GraduationCap], [Target], [Wallet], [Sparkles]) so the set is fully self-contained and compiles
 * on every Compose target with no reliance on the extended icon pack's membership.
 */
object VIcons {
    // ── Direct Material core mappings (lucide → Material) ───────────────────────
    val Home get() = Icons.Filled.Home                       // Home
    val User get() = Icons.Filled.Person                     // User
    val Users get() = Icons.Filled.Person                    // Users (fallback to Person)
    val Search get() = Icons.Filled.Search                   // Search
    val Bell get() = Icons.Filled.Notifications              // Bell (Material fallback)
    val Calendar get() = Icons.Filled.CalendarMonth          // Calendar
    val Megaphone get() = Icons.Filled.Campaign              // Megaphone
    val School get() = Icons.Filled.School                   // (school plate)
    val MapPin get() = Icons.Filled.LocationOn               // MapPin
    val Phone get() = Icons.Filled.Phone                     // Phone
    val Mail get() = Icons.Filled.Email                      // Mail
    val Lock get() = Icons.Filled.Lock                       // Lock
    val Check get() = Icons.Filled.Check                     // Check
    val CheckCircle get() = Icons.Filled.CheckCircle         // CheckCircle
    val Close get() = Icons.Filled.Close                     // X
    val Plus get() = Icons.Filled.Add                        // Plus
    val Send get() = Icons.Filled.Send                       // Send
    val Share get() = Icons.Filled.Share                     // Share2
    val Star get() = Icons.Filled.Star                       // Star
    val Heart get() = Icons.Filled.Favorite                  // Heart
    val More get() = Icons.Filled.MoreHoriz                  // MoreHorizontal
    val Menu get() = Icons.Filled.Menu                       // PanelLeft / menu
    val Settings get() = Icons.Filled.Settings               // settings
    val Bookmark get() = Icons.Filled.Bookmark               // bookmark
    val ChevronDown get() = Icons.Filled.KeyboardArrowDown   // ChevronDown
    val ChevronUp get() = Icons.Filled.KeyboardArrowUp       // ChevronUp
    val ChevronLeft get() = Icons.Filled.KeyboardArrowLeft   // ChevronLeft
    val ChevronRight get() = Icons.Filled.KeyboardArrowRight // ChevronRight
    val ArrowLeft get() = Icons.AutoMirrored.Filled.ArrowBack    // ArrowLeft
    val ArrowRight get() = Icons.AutoMirrored.Filled.ArrowForward // ArrowRight
    val Chat get() = Icons.AutoMirrored.Filled.Chat          // MessageSquare
    val IdCard get() = Icons.Filled.Person                   // ID Card (fallback to Person)

    // ── Hand-authored Material-style vectors (no safe core equivalent) ──────────

    /** Bell (lucide) — stroke-based, crisper than Material filled Notifications. */
    val BellStroke: ImageVector by lazy {
        materialStroke("v_bell_stroke") {
            // Bell dome — arc from (6,8) to (18,8) approximated by two curves
            moveTo(6f, 8f)
            curveTo(6f, 4.69f, 8.69f, 2f, 12f, 2f)
            curveTo(15.31f, 2f, 18f, 4.69f, 18f, 8f)
            // Right side flaring out to (22,17)
            curveTo(18f, 15f, 21f, 17f, 22f, 17f)
            // Bottom rim
            lineTo(2f, 17f)
            // Left side back up to (6,8)
            curveTo(3f, 17f, 6f, 15f, 6f, 8f)
            // Clapper
            moveTo(10.3f, 21f)
            curveTo(10.72f, 21.8f, 11.55f, 22f, 12f, 22f)
            curveTo(12.45f, 22f, 13.28f, 21.8f, 13.7f, 21f)
        }
    }

    /** Indeterminate loading spinner (¾ ring) used by [VButton]'s loading phase. */
    val Spinner: ImageVector by lazy {
        materialStroke("v_spinner") {
            // arc approximated by an open ring: top-right gap
            moveTo(12f, 3f)
            // right side down
            curveTo(16.97f, 3f, 21f, 7.03f, 21f, 12f)
            curveTo(21f, 16.97f, 16.97f, 21f, 12f, 21f)
            curveTo(7.03f, 21f, 3f, 16.97f, 3f, 12f)
            curveTo(3f, 9.5f, 4.01f, 7.24f, 5.64f, 5.6f)
        }
    }

    /** Graduation cap (lucide GraduationCap). */
    val GraduationCap: ImageVector by lazy {
        materialFill("v_gradcap") {
            moveTo(12f, 3f)
            lineTo(1f, 9f)
            lineTo(12f, 15f)
            lineTo(21f, 10.09f)
            lineTo(21f, 17f)
            lineTo(23f, 17f)
            lineTo(23f, 9f)
            close()
            moveTo(5f, 13.18f)
            lineTo(5f, 17.18f)
            lineTo(12f, 21f)
            lineTo(19f, 17.18f)
            lineTo(19f, 13.18f)
            lineTo(12f, 17f)
            close()
        }
    }

    /** Target / GPS reticle (lucide Target). */
    val Target: ImageVector by lazy {
        materialStroke("v_target") {
            moveTo(12f, 3f); curveTo(7.03f, 3f, 3f, 7.03f, 3f, 12f); curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f); curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f); curveTo(21f, 7.03f, 16.97f, 3f, 12f, 3f); close()
            moveTo(12f, 7f); curveTo(9.24f, 7f, 7f, 9.24f, 7f, 12f); curveTo(7f, 14.76f, 9.24f, 17f, 12f, 17f); curveTo(14.76f, 17f, 17f, 14.76f, 17f, 12f); curveTo(17f, 9.24f, 14.76f, 7f, 12f, 7f); close()
            moveTo(12f, 10.5f); curveTo(11.17f, 10.5f, 10.5f, 11.17f, 10.5f, 12f); curveTo(10.5f, 12.83f, 11.17f, 13.5f, 12f, 13.5f); curveTo(12.83f, 13.5f, 13.5f, 12.83f, 13.5f, 12f); curveTo(13.5f, 11.17f, 12.83f, 10.5f, 12f, 10.5f); close()
        }
    }

    /** Wallet (lucide Wallet). */
    val Wallet: ImageVector by lazy {
        materialFill("v_wallet") {
            moveTo(21f, 7f)
            lineTo(21f, 6f)
            curveTo(21f, 4.9f, 20.1f, 4f, 19f, 4f)
            lineTo(5f, 4f)
            curveTo(3.9f, 4f, 3f, 4.9f, 3f, 6f)
            lineTo(3f, 18f)
            curveTo(3f, 19.1f, 3.9f, 20f, 5f, 20f)
            lineTo(19f, 20f)
            curveTo(20.1f, 20f, 21f, 19.1f, 21f, 18f)
            lineTo(21f, 17f)
            lineTo(12f, 17f)
            curveTo(10.9f, 17f, 10f, 16.1f, 10f, 15f)
            lineTo(10f, 9f)
            curveTo(10f, 7.9f, 10.9f, 7f, 12f, 7f)
            close()
            moveTo(12f, 9f)
            lineTo(22f, 9f)
            lineTo(22f, 15f)
            lineTo(12f, 15f)
            close()
            moveTo(16f, 13.5f)
            curveTo(15.17f, 13.5f, 14.5f, 12.83f, 14.5f, 12f)
            curveTo(14.5f, 11.17f, 15.17f, 10.5f, 16f, 10.5f)
            curveTo(16.83f, 10.5f, 17.5f, 11.17f, 17.5f, 12f)
            curveTo(17.5f, 12.83f, 16.83f, 13.5f, 16f, 13.5f)
            close()
        }
    }

    /** Sparkles (lucide Sparkles) — discovery accent. */
    val Sparkles: ImageVector by lazy {
        materialFill("v_sparkles") {
            moveTo(12f, 2f)
            lineTo(14f, 9f)
            lineTo(21f, 11f)
            lineTo(14f, 13f)
            lineTo(12f, 20f)
            lineTo(10f, 13f)
            lineTo(3f, 11f)
            lineTo(10f, 9f)
            close()
            moveTo(19f, 3f)
            lineTo(20f, 6f)
            lineTo(23f, 7f)
            lineTo(20f, 8f)
            lineTo(19f, 11f)
            lineTo(18f, 8f)
            lineTo(15f, 7f)
            lineTo(18f, 6f)
            close()
        }
    }

    // ── Additional lucide glyphs (2px stroke, rounded caps) ─────────────────────

    /** AlertCircle (lucide) — circle + exclamation. */
    val AlertCircle: ImageVector by lazy {
        materialStroke("v_alert_circle") {
            // ring
            moveTo(12f, 3f); curveTo(7.03f, 3f, 3f, 7.03f, 3f, 12f); curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f); curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f); curveTo(21f, 7.03f, 16.97f, 3f, 12f, 3f); close()
            // stem
            moveTo(12f, 8f); lineTo(12f, 12f)
            // dot
            moveTo(12f, 16f); lineTo(12.01f, 16f)
        }
    }

    /** AlertTriangle (lucide) — triangle + exclamation. */
    val AlertTriangle: ImageVector by lazy {
        materialStroke("v_alert_triangle") {
            moveTo(10.29f, 3.86f)
            lineTo(1.82f, 18f)
            curveTo(1.47f, 18.6f, 1.46f, 19.34f, 1.81f, 19.95f)
            curveTo(2.16f, 20.55f, 2.8f, 20.93f, 3.5f, 20.94f)
            lineTo(20.5f, 20.94f)
            curveTo(21.2f, 20.93f, 21.84f, 20.55f, 22.19f, 19.95f)
            curveTo(22.54f, 19.34f, 22.53f, 18.6f, 22.18f, 18f)
            lineTo(13.71f, 3.86f)
            curveTo(13.35f, 3.27f, 12.7f, 2.91f, 12f, 2.91f)
            curveTo(11.3f, 2.91f, 10.65f, 3.27f, 10.29f, 3.86f)
            close()
            moveTo(12f, 9f); lineTo(12f, 13f)
            moveTo(12f, 17f); lineTo(12.01f, 17f)
        }
    }

    /** Clock (lucide) — circle + hands. */
    val Clock: ImageVector by lazy {
        materialStroke("v_clock") {
            moveTo(12f, 3f); curveTo(7.03f, 3f, 3f, 7.03f, 3f, 12f); curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f); curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f); curveTo(21f, 7.03f, 16.97f, 3f, 12f, 3f); close()
            moveTo(12f, 6f); lineTo(12f, 12f); lineTo(16f, 14f)
        }
    }

    /** Upload (lucide) — tray + up arrow. */
    val Upload: ImageVector by lazy {
        materialStroke("v_upload") {
            // tray
            moveTo(21f, 15f); lineTo(21f, 19f); curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f); lineTo(5f, 21f); curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f); lineTo(3f, 15f)
            // arrow head
            moveTo(7f, 8f); lineTo(12f, 3f); lineTo(17f, 8f)
            // stem
            moveTo(12f, 3f); lineTo(12f, 15f)
        }
    }

    /** Download (lucide) — tray + down arrow. */
    val Download: ImageVector by lazy {
        materialStroke("v_download") {
            moveTo(21f, 15f); lineTo(21f, 19f); curveTo(21f, 20.1f, 20.1f, 21f, 19f, 21f); lineTo(5f, 21f); curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f); lineTo(3f, 15f)
            moveTo(7f, 10f); lineTo(12f, 15f); lineTo(17f, 10f)
            moveTo(12f, 15f); lineTo(12f, 3f)
        }
    }

    /** Edit3 (lucide) — pen line. */
    val Edit3: ImageVector by lazy {
        materialStroke("v_edit3") {
            moveTo(12f, 20f); lineTo(21f, 20f)
            moveTo(16.5f, 3.5f)
            curveTo(17.33f, 2.67f, 18.67f, 2.67f, 19.5f, 3.5f)
            curveTo(20.33f, 4.33f, 20.33f, 5.67f, 19.5f, 6.5f)
            lineTo(7f, 19f)
            lineTo(3f, 20f)
            lineTo(4f, 16f)
            close()
        }
    }

    /** Eye (lucide) — eye + pupil. */
    val Eye: ImageVector by lazy {
        materialStroke("v_eye") {
            moveTo(2f, 12f)
            curveTo(2f, 12f, 5f, 5f, 12f, 5f)
            curveTo(19f, 5f, 22f, 12f, 22f, 12f)
            curveTo(22f, 12f, 19f, 19f, 12f, 19f)
            curveTo(5f, 19f, 2f, 12f, 2f, 12f)
            close()
            moveTo(12f, 9f); curveTo(10.34f, 9f, 9f, 10.34f, 9f, 12f); curveTo(9f, 13.66f, 10.34f, 15f, 12f, 15f); curveTo(13.66f, 15f, 15f, 13.66f, 15f, 12f); curveTo(15f, 10.34f, 13.66f, 9f, 12f, 9f); close()
        }
    }

    /** ShieldCheck (lucide) — shield + tick. */
    val ShieldCheck: ImageVector by lazy {
        materialStroke("v_shield_check") {
            moveTo(12f, 2f)
            lineTo(4f, 5f)
            lineTo(4f, 11f)
            curveTo(4f, 16f, 7.4f, 20.5f, 12f, 22f)
            curveTo(16.6f, 20.5f, 20f, 16f, 20f, 11f)
            lineTo(20f, 5f)
            close()
            moveTo(9f, 12f); lineTo(11f, 14f); lineTo(15f, 10f)
        }
    }

    /** ClipboardList (lucide) — clipboard + lines. */
    val ClipboardList: ImageVector by lazy {
        materialStroke("v_clipboard_list") {
            // board
            moveTo(16f, 4f)
            lineTo(18f, 4f)
            curveTo(19.1f, 4f, 20f, 4.9f, 20f, 6f)
            lineTo(20f, 20f)
            curveTo(20f, 21.1f, 19.1f, 22f, 18f, 22f)
            lineTo(6f, 22f)
            curveTo(4.9f, 22f, 4f, 21.1f, 4f, 20f)
            lineTo(4f, 6f)
            curveTo(4f, 4.9f, 4.9f, 4f, 6f, 4f)
            lineTo(8f, 4f)
            // clip
            moveTo(9f, 2f)
            lineTo(15f, 2f)
            curveTo(15.55f, 2f, 16f, 2.45f, 16f, 3f)
            lineTo(16f, 5f)
            curveTo(16f, 5.55f, 15.55f, 6f, 15f, 6f)
            lineTo(9f, 6f)
            curveTo(8.45f, 6f, 8f, 5.55f, 8f, 5f)
            lineTo(8f, 3f)
            curveTo(8f, 2.45f, 8.45f, 2f, 9f, 2f)
            close()
            // list lines + bullets
            moveTo(8f, 11f); lineTo(8.01f, 11f)
            moveTo(11f, 11f); lineTo(16f, 11f)
            moveTo(8f, 16f); lineTo(8.01f, 16f)
            moveTo(11f, 16f); lineTo(16f, 16f)
        }
    }

    /** ListChecks (lucide) — checks + lines. */
    val ListChecks: ImageVector by lazy {
        materialStroke("v_list_checks") {
            // first check
            moveTo(3f, 7f); lineTo(5f, 9f); lineTo(9f, 5f)
            // first line
            moveTo(12f, 7f); lineTo(21f, 7f)
            // second check
            moveTo(3f, 17f); lineTo(5f, 19f); lineTo(9f, 15f)
            // second line
            moveTo(12f, 17f); lineTo(21f, 17f)
        }
    }

    /** FileText (lucide) — document + lines. */
    val FileText: ImageVector by lazy {
        materialStroke("v_file_text") {
            moveTo(14f, 2f)
            lineTo(6f, 2f)
            curveTo(4.9f, 2f, 4f, 2.9f, 4f, 4f)
            lineTo(4f, 20f)
            curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
            lineTo(18f, 22f)
            curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
            lineTo(20f, 8f)
            close()
            // fold
            moveTo(14f, 2f); lineTo(14f, 8f); lineTo(20f, 8f)
            // lines
            moveTo(8f, 13f); lineTo(16f, 13f)
            moveTo(8f, 17f); lineTo(16f, 17f)
            moveTo(8f, 9f); lineTo(10f, 9f)
        }
    }

    /** BookOpen (lucide) — open book. */
    val BookOpen: ImageVector by lazy {
        materialStroke("v_book_open") {
            moveTo(12f, 7f)
            curveTo(12f, 5.9f, 11.3f, 5f, 10f, 4.6f)
            curveTo(8.5f, 4.1f, 5.5f, 4f, 3f, 4f)
            lineTo(3f, 18f)
            curveTo(5.5f, 18f, 8.5f, 18.1f, 10f, 18.6f)
            curveTo(11.3f, 19f, 12f, 19.9f, 12f, 21f)
            moveTo(12f, 7f)
            curveTo(12f, 5.9f, 12.7f, 5f, 14f, 4.6f)
            curveTo(15.5f, 4.1f, 18.5f, 4f, 21f, 4f)
            lineTo(21f, 18f)
            curveTo(18.5f, 18f, 15.5f, 18.1f, 14f, 18.6f)
            curveTo(12.7f, 19f, 12f, 19.9f, 12f, 21f)
            moveTo(12f, 7f); lineTo(12f, 21f)
        }
    }

    /** Filter (lucide) — funnel. */
    val Filter: ImageVector by lazy {
        materialStroke("v_filter") {
            moveTo(22f, 3f)
            lineTo(2f, 3f)
            lineTo(10f, 12.46f)
            lineTo(10f, 19f)
            lineTo(14f, 21f)
            lineTo(14f, 12.46f)
            close()
        }
    }

    /** TrendingUp (lucide) — rising line + arrow. */
    val TrendingUp: ImageVector by lazy {
        materialStroke("v_trending_up") {
            moveTo(23f, 6f); lineTo(13.5f, 15.5f); lineTo(8.5f, 10.5f); lineTo(1f, 18f)
            moveTo(17f, 6f); lineTo(23f, 6f); lineTo(23f, 12f)
        }
    }

    /** TrendingDown (lucide) — falling line + arrow. */
    val TrendingDown: ImageVector by lazy {
        materialStroke("v_trending_down") {
            moveTo(23f, 18f); lineTo(13.5f, 8.5f); lineTo(8.5f, 13.5f); lineTo(1f, 6f)
            moveTo(17f, 18f); lineTo(23f, 18f); lineTo(23f, 12f)
        }
    }

    /** Activity / pulse (lucide) — heartbeat line. */
    val Activity: ImageVector by lazy {
        materialStroke("v_activity") {
            moveTo(22f, 12f); lineTo(18f, 12f); lineTo(15f, 21f); lineTo(9f, 3f); lineTo(6f, 12f); lineTo(2f, 12f)
        }
    }

    /** History / clock-back (lucide) — clock with counter-clockwise arrow. */
    val History: ImageVector by lazy {
        materialStroke("v_history") {
            moveTo(3f, 3f); lineTo(3f, 9f); lineTo(9f, 9f)
            moveTo(3.51f, 9f); curveTo(5.02f, 4.93f, 8.93f, 2f, 13.5f, 2f)
            curveTo(19.3f, 2f, 24f, 6.7f, 24f, 12f); curveTo(24f, 17.3f, 19.3f, 22f, 13.5f, 22f)
            curveTo(8.7f, 22f, 4f, 17.3f, 4f, 12f); lineTo(4f, 9f)
            moveTo(12f, 7f); lineTo(12f, 12f); lineTo(15f, 14f)
        }
    }

    /** Users (lucide) — three persons group. */
    val UsersGroup: ImageVector by lazy {
        materialStroke("v_users_group") {
            moveTo(17f, 21f)
            curveTo(17f, 19.67f, 16.33f, 18.67f, 15f, 18f)
            curveTo(13.67f, 17.33f, 12.33f, 17f, 11f, 17f)
            curveTo(9.67f, 17f, 8.33f, 17.33f, 7f, 18f)
            curveTo(5.67f, 18.67f, 5f, 19.67f, 5f, 21f)
            moveTo(11f, 11f)
            curveTo(9.34f, 11f, 8f, 9.66f, 8f, 8f)
            curveTo(8f, 6.34f, 9.34f, 5f, 11f, 5f)
            curveTo(12.66f, 5f, 14f, 6.34f, 14f, 8f)
            curveTo(14f, 9.66f, 12.66f, 11f, 11f, 11f)
            close()
            moveTo(17f, 11f)
            curveTo(17f, 9.34f, 16f, 8f, 14.5f, 8f)
            moveTo(21f, 21f)
            curveTo(21f, 19.67f, 20.5f, 18.67f, 19.5f, 18f)
            moveTo(14.5f, 5f)
            curveTo(15f, 4.84f, 15.5f, 4.5f, 16f, 4f)
            curveTo(16.5f, 3.5f, 16.75f, 2.75f, 16.75f, 2f)
        }
    }

    /** LayoutDashboard (lucide) — 4-quadrant grid. */
    val LayoutDashboard: ImageVector by lazy {
        materialStroke("v_layout_dashboard") {
            moveTo(3f, 3f); lineTo(11f, 3f); lineTo(11f, 11f); lineTo(3f, 11f); close()
            moveTo(13f, 3f); lineTo(21f, 3f); lineTo(21f, 8f); lineTo(13f, 8f); close()
            moveTo(13f, 10f); lineTo(21f, 10f); lineTo(21f, 21f); lineTo(13f, 21f); close()
            moveTo(3f, 13f); lineTo(11f, 13f); lineTo(11f, 21f); lineTo(3f, 21f); close()
        }
    }

    /** Minus (lucide) — horizontal line. */
    val Minus: ImageVector by lazy {
        materialStroke("v_minus") {
            moveTo(5f, 12f); lineTo(19f, 12f)
        }
    }

    /** Home (lucide) — stroke-based house with door. */
    val HomeStroke: ImageVector by lazy {
        materialStroke("v_home_stroke") {
            moveTo(3f, 10f); lineTo(12f, 3f); lineTo(21f, 10f)
            moveTo(5f, 9f); lineTo(5f, 20f); lineTo(19f, 20f); lineTo(19f, 9f)
            moveTo(9f, 20f); lineTo(9f, 14f); lineTo(15f, 14f); lineTo(15f, 20f)
        }
    }

    /** Megaphone (lucide) — stroke-based loudspeaker. */
    val MegaphoneStroke: ImageVector by lazy {
        materialStroke("v_megaphone_stroke") {
            moveTo(3f, 11f); lineTo(21f, 4f); lineTo(21f, 20f); lineTo(3f, 13f); close()
            moveTo(11.6f, 16.8f); curveTo(11.2f, 18f, 10f, 19f, 8.5f, 19f)
            curveTo(7f, 19f, 6f, 18f, 6f, 16.5f); curveTo(6f, 15f, 7f, 14f, 8.5f, 14f)
        }
    }

    /** Settings (lucide) — stroke-based gear with center circle. */
    val SettingsStroke: ImageVector by lazy {
        materialStroke("v_settings_stroke") {
            // Gear teeth — 8 spokes
            moveTo(12f, 2.5f); lineTo(12f, 5.5f)
            moveTo(12f, 18.5f); lineTo(12f, 21.5f)
            moveTo(2.5f, 12f); lineTo(5.5f, 12f)
            moveTo(18.5f, 12f); lineTo(21.5f, 12f)
            moveTo(5.3f, 5.3f); lineTo(7.4f, 7.4f)
            moveTo(16.6f, 16.6f); lineTo(18.7f, 18.7f)
            moveTo(5.3f, 18.7f); lineTo(7.4f, 16.6f)
            moveTo(16.6f, 7.4f); lineTo(18.7f, 5.3f)
            // Inner circle
            moveTo(12f, 8f); curveTo(9.79f, 8f, 8f, 9.79f, 8f, 12f)
            curveTo(8f, 14.21f, 9.79f, 16f, 12f, 16f)
            curveTo(14.21f, 16f, 16f, 14.21f, 16f, 12f)
            curveTo(16f, 9.79f, 14.21f, 8f, 12f, 8f)
            close()
        }
    }

    /** UserPlus (lucide) — person + plus sign. */
    val UserPlus: ImageVector by lazy {
        materialStroke("v_user_plus") {
            // body + head
            moveTo(16f, 21f); curveTo(16f, 18f, 14f, 16f, 12f, 16f); curveTo(10f, 16f, 8f, 18f, 8f, 21f)
            moveTo(12f, 11f); curveTo(9.5f, 11f, 7.5f, 9f, 7.5f, 6.5f); curveTo(7.5f, 4f, 9.5f, 2f, 12f, 2f); curveTo(14.5f, 2f, 16.5f, 4f, 16.5f, 6.5f); curveTo(16.5f, 9f, 14.5f, 11f, 12f, 11f)
            // plus sign
            moveTo(19f, 8f); lineTo(19f, 14f)
            moveTo(16f, 11f); lineTo(22f, 11f)
        }
    }

    /** Globe (lucide) — sphere with meridians and equator. */
    val Globe: ImageVector by lazy {
        materialStroke("v_globe") {
            moveTo(12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f); curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f); curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f); close()
            moveTo(2f, 12f); lineTo(22f, 12f)
            moveTo(12f, 2f); curveTo(15f, 5.5f, 15f, 18.5f, 12f, 22f)
            moveTo(12f, 2f); curveTo(9f, 5.5f, 9f, 18.5f, 12f, 22f)
        }
    }

    /** Palette (lucide) — artist palette with paint wells. */
    val Palette: ImageVector by lazy {
        materialStroke("v_palette") {
            // palette body
            moveTo(12f, 2f); curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f); curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f); curveTo(13.5f, 22f, 14.5f, 20.5f, 14f, 19f); curveTo(13.5f, 17.5f, 15f, 16f, 16.5f, 16f); curveTo(19.5f, 16f, 22f, 13.5f, 22f, 10f); curveTo(22f, 5f, 17.5f, 2f, 12f, 2f); close()
            // wells
            moveTo(7f, 12f); arcTo(1f, 1f, 0f, false, true, 9f, 12f); arcTo(1f, 1f, 0f, false, true, 7f, 12f); close()
            moveTo(11f, 7f); arcTo(1f, 1f, 0f, false, true, 13f, 7f); arcTo(1f, 1f, 0f, false, true, 11f, 7f); close()
            moveTo(15f, 12f); arcTo(1f, 1f, 0f, false, true, 17f, 12f); arcTo(1f, 1f, 0f, false, true, 15f, 12f); close()
        }
    }

    /** LogOut (lucide) — arrow exiting a box. */
    val LogOut: ImageVector by lazy {
        materialStroke("v_log_out") {
            moveTo(9f, 21f); lineTo(5f, 21f); curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f); lineTo(3f, 5f); curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f); lineTo(9f, 3f)
            moveTo(16f, 17f); lineTo(21f, 12f); lineTo(16f, 7f)
            moveTo(21f, 12f); lineTo(9f, 12f)
        }
    }

    /** Home (lucide) — premium stroke house. */
    val HomePremium: ImageVector by lazy {
        materialStroke("v_home_premium") {
            moveTo(3f, 9f); lineTo(12f, 2f); lineTo(21f, 9f)
            moveTo(5f, 10f); lineTo(5f, 19f); curveTo(5f, 20.1f, 5.9f, 21f, 7f, 21f); lineTo(17f, 21f); curveTo(18.1f, 21f, 19f, 20.1f, 19f, 19f); lineTo(19f, 10f)
            moveTo(9f, 21f); lineTo(9f, 13f); lineTo(15f, 13f); lineTo(15f, 21f)
        }
    }

    /** Academic (lucide GraduationCap) — premium stroke graduation cap. */
    val Academic: ImageVector by lazy {
        materialStroke("v_academic") {
            moveTo(22f, 10f); lineTo(12f, 3f); lineTo(2f, 10f); lineTo(12f, 17f); lineTo(22f, 10f)
            moveTo(2f, 10f); lineTo(2f, 16f)
            moveTo(6f, 12.5f); lineTo(6f, 16f); lineTo(12f, 19f); lineTo(18f, 16f); lineTo(18f, 12.5f)
        }
    }

    /** WalletPremium (lucide Wallet) — premium stroke wallet. */
    val WalletPremium: ImageVector by lazy {
        materialStroke("v_wallet_premium") {
            moveTo(19f, 7f); lineTo(19f, 4f); curveTo(19f, 2.9f, 18.1f, 2f, 17f, 2f); lineTo(5f, 2f); curveTo(3.9f, 2f, 3f, 2.9f, 3f, 4f); lineTo(3f, 18f); curveTo(3f, 19.1f, 3.9f, 20f, 5f, 20f); lineTo(20f, 20f); curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f); lineTo(22f, 10f); lineTo(17f, 10f); curveTo(15.9f, 10f, 15f, 10.9f, 15f, 12f); curveTo(15f, 13.1f, 15.9f, 14f, 17f, 14f); lineTo(22f, 14f)
        }
    }

    /** ChatPremium (lucide MessageSquare) — premium stroke chat bubble. */
    val ChatPremium: ImageVector by lazy {
        materialStroke("v_chat_premium") {
            moveTo(21f, 15f); curveTo(21f, 16.1f, 20.1f, 17f, 19f, 17f); lineTo(7f, 17f); lineTo(3f, 21f); lineTo(3f, 5f); curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f); lineTo(19f, 3f); curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f); lineTo(21f, 15f); close()
        }
    }

    /** UserPremium (lucide User) — premium stroke person. */
    val UserPremium: ImageVector by lazy {
        materialStroke("v_user_premium") {
            moveTo(20f, 21f); curveTo(20f, 17.13f, 16.42f, 14f, 12f, 14f); curveTo(7.58f, 14f, 4f, 17.13f, 4f, 21f)
            moveTo(12f, 11f); curveTo(14.21f, 11f, 16f, 9.21f, 16f, 7f); curveTo(16f, 4.79f, 14.21f, 3f, 12f, 3f); curveTo(9.79f, 3f, 8f, 4.79f, 8f, 7f); curveTo(8f, 9.21f, 9.79f, 11f, 12f, 11f); close()
        }
    }
}

// ── Builders ───────────────────────────────────────────────────────────────────

private inline fun materialFill(
    name: String,
    pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name, defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 24f, viewportHeight = 24f,
).apply {
    path(fill = SolidColor(Color.Black), pathBuilder = pathBuilder)
}.build()

private inline fun materialStroke(
    name: String,
    strokeWidth: Float = 2f,
    pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name, defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 24f, viewportHeight = 24f,
).apply {
    path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = strokeWidth,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBuilder,
    )
}.build()
