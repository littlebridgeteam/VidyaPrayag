/*
 * File: AppStrings.kt
 * Module: core.locale
 *
 * Client-side string resources for multi-language support.
 * Uses Kotlin string maps instead of CMP's stringResource due to
 * system locale binding issues on certain platforms.
 *
 * Resolution: exact locale → English fallback → key itself.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §11
 */
package com.littlebridge.enrollplus.core.locale

object StringKeys {
    // Common
    const val COMMON_BUTTON_SAVE      = "common.button_save"
    const val COMMON_BUTTON_CANCEL    = "common.button_cancel"
    const val COMMON_BUTTON_RETRY     = "common.button_retry"
    const val COMMON_BUTTON_DELETE    = "common.button_delete"
    const val COMMON_BUTTON_EDIT      = "common.button_edit"
    const val COMMON_BUTTON_CLOSE     = "common.button_close"
    const val COMMON_BUTTON_CONTINUE  = "common.button_continue"
    const val COMMON_BUTTON_BACK      = "common.button_back"
    const val COMMON_BUTTON_CONFIRM   = "common.button_confirm"
    const val COMMON_BUTTON_APPLY     = "common.button_apply"
    const val COMMON_BUTTON_DONE      = "common.button_done"
    const val COMMON_BUTTON_NEXT      = "common.button_next"
    const val COMMON_BUTTON_SKIP      = "common.button_skip"
    const val COMMON_BUTTON_REFRESH   = "common.button_refresh"
    const val COMMON_BUTTON_SHARE     = "common.button_share"
    const val COMMON_BUTTON_LOGOUT    = "common.button_logout"

    const val COMMON_ERROR_GENERIC    = "common.error_generic"
    const val COMMON_ERROR_NETWORK    = "common.error_network"
    const val COMMON_ERROR_OFFLINE    = "common.error_offline"
    const val COMMON_ERROR_TIMEOUT    = "common.error_timeout"
    const val COMMON_ERROR_NOT_FOUND  = "common.error_not_found"
    const val COMMON_ERROR_UNAUTHORIZED = "common.error_unauthorized"
    const val COMMON_LOADING          = "common.loading"
    const val COMMON_EMPTY            = "common.empty"
    const val COMMON_SEARCH           = "common.search"
    const val COMMON_FILTER           = "common.filter"
    const val COMMON_ALL              = "common.all"
    const val COMMON_NONE             = "common.none"
    const val COMMON_YES              = "common.yes"
    const val COMMON_NO               = "common.no"
    const val COMMON_TODAY            = "common.today"
    const val COMMON_YESTERDAY        = "common.yesterday"
    const val COMMON_TOMORROW         = "common.tomorrow"
    const val COMMON_SELECT           = "common.select"
    const val COMMON_REQUIRED         = "common.required"
    const val COMMON_OPTIONAL         = "common.optional"

    // Auth
    const val AUTH_WELCOME            = "auth.welcome"
    const val AUTH_LOGIN              = "auth.login"
    const val AUTH_SIGNUP             = "auth.signup"
    const val AUTH_LOGOUT             = "auth.logout"
    const val AUTH_PHONE              = "auth.phone"
    const val AUTH_EMAIL              = "auth.email"
    const val AUTH_PASSWORD           = "auth.password"
    const val AUTH_OTP                = "auth.otp"
    const val AUTH_OTP_SENT           = "auth.otp_sent"
    const val AUTH_OTP_VERIFY         = "auth.otp_verify"
    const val AUTH_NAME               = "auth.name"
    const val AUTH_ROLE_PARENT        = "auth.role_parent"
    const val AUTH_ROLE_TEACHER       = "auth.role_teacher"
    const val AUTH_ROLE_ADMIN         = "auth.role_admin"
    const val AUTH_LOGIN_SUCCESS      = "auth.login_success"
    const val AUTH_LOGIN_FAILED       = "auth.login_failed"
    const val AUTH_REGISTER_SCHOOL    = "auth.register_school"

    // Language
    const val LANGUAGE_TITLE          = "language.title"
    const val LANGUAGE_SELECT         = "language.select"
    const val LANGUAGE_CHANGE         = "language.change"
    const val LANGUAGE_CURRENT        = "language.current"
    const val LANGUAGE_ENGLISH        = "language.english"
    const val LANGUAGE_SEARCH         = "language.search_placeholder"

    // Navigation
    const val NAV_HOME                = "nav.home"
    const val NAV_DASHBOARD           = "nav.dashboard"
    const val NAV_PROFILE             = "nav.profile"
    const val NAV_SETTINGS            = "nav.settings"
    const val NAV_NOTIFICATIONS       = "nav.notifications"
    const val NAV_MESSAGES            = "nav.messages"
    const val NAV_CALENDAR            = "nav.calendar"
    const val NAV_ATTENDANCE          = "nav.attendance"
    const val NAV_FEES                = "nav.fees"
    const val NAV_ACADEMICS           = "nav.academics"
    const val NAV_MORE                = "nav.more"

    // Dashboard
    const val DASH_GOOD_MORNING       = "dash.good_morning"
    const val DASH_GOOD_AFTERNOON     = "dash.good_afternoon"
    const val DASH_GOOD_EVENING       = "dash.good_evening"
    const val DASH_WELCOME_BACK       = "dash.welcome_back"
    const val DASH_QUICK_STATS        = "dash.quick_stats"
    const val DASH_RECENT_ACTIVITY    = "dash.recent_activity"

    // Attendance
    const val ATT_PRESENT             = "att.present"
    const val ATT_ABSENT              = "att.absent"
    const val ATT_LATE                = "att.late"
    const val ATT_HALF_DAY            = "att.half_day"
    const val ATT_MARK_PRESENT        = "att.mark_present"
    const val ATT_MARK_ABSENT         = "att.mark_absent"
    const val ATT_RATE                = "att.rate"
    const val ATT_RATE_PLURAL         = "att.rate_plural"

    // Fees
    const val FEE_PAID                = "fee.paid"
    const val FEE_DUE                 = "fee.due"
    const val FEE_OVERDUE             = "fee.overdue"
    const val FEE_PAY_NOW             = "fee.pay_now"
    const val FEE_HISTORY             = "fee.history"
    const val FEE_AMOUNT              = "fee.amount"
    const val FEE_DUE_DATE            = "fee.due_date"
    const val FEE_TOTAL               = "fee.total"
    const val FEE_PENDING             = "fee.pending"

    // Notifications
    const val NOTIF_TITLE             = "notif.title"
    const val NOTIF_MARK_READ         = "notif.mark_read"
    const val NOTIF_MARK_ALL_READ     = "notif.mark_all_read"
    const val NOTIF_EMPTY             = "notif.empty"
    const val NOTIF_UNREAD            = "notif.unread"
    const val NOTIF_UNREAD_PLURAL     = "notif.unread_plural"

    // Profile
    const val PROFILE_TITLE           = "profile.title"
    const val PROFILE_EDIT            = "profile.edit"
    const val PROFILE_NAME            = "profile.name"
    const val PROFILE_PHONE           = "profile.phone"
    const val PROFILE_EMAIL           = "profile.email"
    const val PROFILE_SCHOOL          = "profile.school"
    const val PROFILE_ROLE            = "profile.role"
    const val PROFILE_LANGUAGE        = "profile.language"
    const val PROFILE_THEME           = "profile.theme"
    const val PROFILE_ABOUT           = "profile.about"
    const val PROFILE_HELP            = "profile.help"
    const val PROFILE_PRIVACY         = "profile.privacy"

    // Settings
    const val SETTINGS_TITLE          = "settings.title"
    const val SETTINGS_GENERAL        = "settings.general"
    const val SETTINGS_NOTIFICATIONS  = "settings.notifications"
    const val SETTINGS_LANGUAGE       = "settings.language"
    const val SETTINGS_THEME          = "settings.theme"
    const val SETTINGS_ABOUT          = "settings.about"
    const val SETTINGS_LOGOUT         = "settings.logout"
    const val SETTINGS_FONT_SIZE      = "settings.font_size"

    // Student / Child
    const val CHILD_TITLE             = "child.title"
    const val CHILD_ADD               = "child.add"
    const val CHILD_LINK              = "child.link"
    const val CHILD_NAME              = "child.name"
    const val CHILD_CLASS             = "child.class"
    const val CHILD_SECTION           = "child.section"
    const val CHILD_ROLL              = "child.roll"
    const val CHILD_PROGRESS          = "child.progress"
    const val CHILD_ATTENDANCE        = "child.attendance"
    const val CHILD_MARKS             = "child.marks"
    const val CHILD_STUDENTS          = "child.students"
    const val CHILD_STUDENTS_PLURAL   = "child.students_plural"

    // School
    const val SCHOOL_TITLE            = "school.title"
    const val SCHOOL_NAME             = "school.name"
    const val SCHOOL_CLASSES          = "school.classes"
    const val SCHOOL_TEACHERS         = "school.teachers"
    const val SCHOOL_STUDENTS         = "school.students"
    const val SCHOOL_ONBOARDING       = "school.onboarding"
    const val SCHOOL_BRANDING         = "school.branding"
    const val SCHOOL_ACADEMIC         = "school.academic"

    // Teacher
    const val TEACHER_TITLE           = "teacher.title"
    const val TEACHER_CLASSES         = "teacher.classes"
    const val TEACHER_SYLLABUS        = "teacher.syllabus"
    const val TEACHER_HOMEWORK        = "teacher.homework"
    const val TEACHER_LESSON_PLAN     = "teacher.lesson_plan"
    const val TEACHER_ATTENDANCE      = "teacher.attendance"
    const val TEACHER_GRADEBOOK       = "teacher.gradebook"
    const val TEACHER_LEAVE           = "teacher.leave"

    // Calendar
    const val CAL_TITLE               = "cal.title"
    const val CAL_TODAY               = "cal.today"
    const val CAL_EVENTS              = "cal.events"
    const val CAL_HOLIDAYS            = "cal.holidays"
    const val CAL_EXAMS               = "cal.exams"
    const val CAL_PTM                 = "cal.ptm"

    // Messages
    const val MSG_TITLE               = "msg.title"
    const val MSG_SEND                = "msg.send"
    const val MSG_REPLY               = "msg.reply"
    const val MSG_EMPTY               = "msg.empty"
    const val MSG_TYPE_MESSAGE        = "msg.type_message"
    const val MSG_BROADCAST           = "msg.broadcast"

    // Onboarding
    const val OB_WELCOME              = "ob.welcome"
    const val OB_STEP                 = "ob.step"
    const val OB_BASIC_INFO           = "ob.basic_info"
    const val OB_BRANDING             = "ob.branding"
    const val OB_ACADEMIC             = "ob.academic"
    const val OB_REVIEW               = "ob.review"
    const val OB_FINISH               = "ob.finish"

    // Splash
    const val SPLASH_TAGLINE          = "splash.tagline"

    // Auth scaffold
    const val AUTH_SECURED            = "auth.secured"
    const val AUTH_BACK_LINK          = "auth.back_link"

    // Parent auth
    const val AUTH_PARENT_WELCOME     = "auth.parent_welcome"
    const val AUTH_PARENT_SUBTITLE    = "auth.parent_subtitle"
    const val AUTH_MOBILE_NUMBER      = "auth.mobile_number"
    const val AUTH_YOUR_NAME          = "auth.your_name"
    const val AUTH_FULL_NAME_PH       = "auth.full_name_ph"
    const val AUTH_OTP_CODE_PH        = "auth.otp_code_ph"
    const val AUTH_OTP_SENT_TO        = "auth.otp_sent_to"
    const val AUTH_YOUR_PHONE             = "auth.your_phone"
    const val AUTH_SEND_OTP           = "auth.send_otp"
    const val AUTH_VERIFY_CONTINUE    = "auth.verify_continue"

    // Admin auth
    const val AUTH_ADMIN_TITLE        = "auth.admin_title"
    const val AUTH_ADMIN_SUBTITLE     = "auth.admin_subtitle"
    const val AUTH_EMAIL_OR_STAFF_ID  = "auth.email_or_staff_id"
    const val AUTH_FORGOT_PASSWORD    = "auth.forgot_password"
    const val AUTH_WORK_EMAIL         = "auth.work_email"
    const val AUTH_SCHOOL_NAME        = "auth.school_name"
    const val AUTH_BOARD              = "auth.board"
    const val AUTH_CITY_OPTIONAL      = "auth.city_optional"
    const val AUTH_CREATE_PASSWORD    = "auth.create_password"
    const val AUTH_PASSWORD_8_PH      = "auth.password_8_ph"
    const val AUTH_NO_ACCOUNT         = "auth.no_account"
    const val AUTH_NEW_REGISTER       = "auth.new_register"
    const val AUTH_REGISTER_CONTINUE  = "auth.register_continue"
    const val AUTH_ONBOARD_SCHOOL     = "auth.onboard_school"
    const val AUTH_SIGN_IN            = "auth.sign_in"
    const val AUTH_SETTING_UP_SCHOOL  = "auth.setting_up_school"
    const val AUTH_CREATE_ADMIN_ACCT  = "auth.create_admin_acct"
    const val AUTH_REGISTER_MY_SCHOOL = "auth.register_my_school"
    const val AUTH_SHOW_PASSWORD      = "auth.show_password"
    const val AUTH_HIDE_PASSWORD      = "auth.hide_password"

    // Teacher first login
    const val AUTH_SET_NEW_PASSWORD   = "auth.set_new_password"
    const val AUTH_FIRST_LOGIN_DESC   = "auth.first_login_desc"
    const val AUTH_CURRENT_TEMP_PW    = "auth.current_temp_pw"
    const val AUTH_NEW_PASSWORD       = "auth.new_password"
    const val AUTH_CONFIRM_PASSWORD   = "auth.confirm_password"
    const val AUTH_REENTER_PH         = "auth.reenter_ph"
    const val AUTH_UPDATE_CONTINUE    = "auth.update_continue"
    const val AUTH_NEED_HELP          = "auth.need_help"
    const val AUTH_PW_TOO_SHORT       = "auth.pw_too_short"
    const val AUTH_PW_NO_MATCH        = "auth.pw_no_match"
    const val AUTH_CONN_ERROR         = "auth.conn_error"

    // Legal info screen
    const val LEGAL_TAB_PRIVACY       = "legal.tab_privacy"
    const val LEGAL_TAB_TERMS         = "legal.tab_terms"
    const val LEGAL_TAB_HELP          = "legal.tab_help"
    const val LEGAL_TITLE             = "legal.title"
    const val LEGAL_FOOTER            = "legal.footer"
    const val LEGAL_LAST_UPDATED      = "legal.last_updated"
    const val LEGAL_BACK              = "legal.back"
    const val LEGAL_PRIV_EYEBROW      = "legal.priv_eyebrow"
    const val LEGAL_PRIV_TITLE        = "legal.priv_title"
    const val LEGAL_PRIV_INTRO        = "legal.priv_intro"
    const val LEGAL_PRIV_COLLECT_T    = "legal.priv_collect_t"
    const val LEGAL_PRIV_COLLECT_1    = "legal.priv_collect_1"
    const val LEGAL_PRIV_COLLECT_2    = "legal.priv_collect_2"
    const val LEGAL_PRIV_COLLECT_3    = "legal.priv_collect_3"
    const val LEGAL_PRIV_COLLECT_4    = "legal.priv_collect_4"
    const val LEGAL_PRIV_USE_T        = "legal.priv_use_t"
    const val LEGAL_PRIV_USE_1        = "legal.priv_use_1"
    const val LEGAL_PRIV_USE_2        = "legal.priv_use_2"
    const val LEGAL_PRIV_USE_3        = "legal.priv_use_3"
    const val LEGAL_PRIV_NEVER_T      = "legal.priv_never_t"
    const val LEGAL_PRIV_NEVER_1      = "legal.priv_never_1"
    const val LEGAL_PRIV_NEVER_2      = "legal.priv_never_2"
    const val LEGAL_PRIV_NEVER_3      = "legal.priv_never_3"
    const val LEGAL_PRIV_SCOPED_T     = "legal.priv_scoped_t"
    const val LEGAL_PRIV_SCOPED_B     = "legal.priv_scoped_b"
    const val LEGAL_PRIV_RETENTION_T  = "legal.priv_retention_t"
    const val LEGAL_PRIV_RETENTION_B  = "legal.priv_retention_b"
    const val LEGAL_TERMS_EYEBROW     = "legal.terms_eyebrow"
    const val LEGAL_TERMS_TITLE       = "legal.terms_title"
    const val LEGAL_TERMS_INTRO       = "legal.terms_intro"
    const val LEGAL_TERMS_USE_T       = "legal.terms_use_t"
    const val LEGAL_TERMS_USE_1       = "legal.terms_use_1"
    const val LEGAL_TERMS_USE_2       = "legal.terms_use_2"
    const val LEGAL_TERMS_USE_3       = "legal.terms_use_3"
    const val LEGAL_TERMS_ACCOUNTS_T  = "legal.terms_accounts_t"
    const val LEGAL_TERMS_ACCOUNTS_B  = "legal.terms_accounts_b"
    const val LEGAL_TERMS_CONTENT_T   = "legal.terms_content_t"
    const val LEGAL_TERMS_CONTENT_1   = "legal.terms_content_1"
    const val LEGAL_TERMS_CONTENT_2   = "legal.terms_content_2"
    const val LEGAL_TERMS_CONTENT_3   = "legal.terms_content_3"
    const val LEGAL_TERMS_AVAIL_T     = "legal.terms_avail_t"
    const val LEGAL_TERMS_AVAIL_B     = "legal.terms_avail_b"
    const val LEGAL_TERMS_CHANGES_T   = "legal.terms_changes_t"
    const val LEGAL_TERMS_CHANGES_B   = "legal.terms_changes_b"
    const val LEGAL_TERMS_CONTACT_T   = "legal.terms_contact_t"
    const val LEGAL_TERMS_CONTACT_B   = "legal.terms_contact_b"
    const val LEGAL_HELP_EYEBROW      = "legal.help_eyebrow"
    const val LEGAL_HELP_TITLE        = "legal.help_title"
    const val LEGAL_HELP_INTRO        = "legal.help_intro"
    const val LEGAL_HELP_EMAIL        = "legal.help_email"
    const val LEGAL_HELP_INCLUDE_T    = "legal.help_include_t"
    const val LEGAL_HELP_INCLUDE_1    = "legal.help_include_1"
    const val LEGAL_HELP_INCLUDE_2    = "legal.help_include_2"
    const val LEGAL_HELP_INCLUDE_3    = "legal.help_include_3"
    const val LEGAL_HELP_FAQ_T        = "legal.help_faq_t"
    const val LEGAL_HELP_FAQ_Q1       = "legal.help_faq_q1"
    const val LEGAL_HELP_FAQ_A1       = "legal.help_faq_a1"
    const val LEGAL_HELP_FAQ_Q2       = "legal.help_faq_q2"
    const val LEGAL_HELP_FAQ_A2       = "legal.help_faq_a2"
    const val LEGAL_HELP_FAQ_Q3       = "legal.help_faq_q3"
    const val LEGAL_HELP_FAQ_A3       = "legal.help_faq_a3"

    // Parent link child screen
    const val LINK_STEP_OF            = "link.step_of"
    const val LINK_STEP1_TITLE        = "link.step1_title"
    const val LINK_STEP1_SUB          = "link.step1_sub"
    const val LINK_FULL_NAME          = "link.full_name"
    const val LINK_FULL_NAME_PH       = "link.full_name_ph"
    const val LINK_PREF_LANG          = "link.pref_lang"
    const val LINK_STEP2_TITLE        = "link.step2_title"
    const val LINK_STEP2_SUB          = "link.step2_sub"
    const val LINK_SEARCH_PH          = "link.search_ph"
    const val LINK_SEARCHING          = "link.searching"
    const val LINK_SEARCH             = "link.search"
    const val LINK_SEARCH_ERR         = "link.search_err"
    const val LINK_SEARCH_PROMPT      = "link.search_prompt"
    const val LINK_TAP_SELECT         = "link.tap_select"
    const val LINK_MATCH              = "link.match"
    const val LINK_STEP3_TITLE        = "link.step3_title"
    const val LINK_STEP3_SUB          = "link.step3_sub"
    const val LINK_CHILD_NAME         = "link.child_name"
    const val LINK_CHILD_NAME_PH      = "link.child_name_ph"
    const val LINK_CLASS              = "link.class"
    const val LINK_CLASS_PH           = "link.class_ph"
    const val LINK_SECTION            = "link.section"
    const val LINK_SECTION_PH         = "link.section_ph"
    const val LINK_ROLL               = "link.roll"
    const val LINK_ROLL_PH            = "link.roll_ph"
    const val LINK_PHONE_OPT          = "link.phone_opt"
    const val LINK_PHONE_PH           = "link.phone_ph"
    const val LINK_ERR                = "link.err"
    const val LINK_REVIEW_MSG         = "link.review_msg"
    const val LINK_PENDING_MSG        = "link.pending_msg"
    const val LINK_CLASS_ROLL         = "link.class_roll"
    const val LINK_MATCH_PROMPT       = "link.match_prompt"
    const val LINK_CONTINUE           = "link.continue"
    const val LINK_LINKING            = "link.linking"
    const val LINK_DONE               = "link.done"
    const val LINK_FINISH             = "link.finish"
    const val LINK_THE_SCHOOL         = "link.the_school"
    const val LINK_YOUR_SCHOOL        = "link.your_school"

    // CommonLandingScreenV2
    const val LANDING_BRAND            = "landing.brand"
    const val LANDING_SCHOOL_EYEBROW   = "landing.school_eyebrow"
    const val LANDING_PARENT_EYEBROW   = "landing.parent_eyebrow"
    const val LANDING_SCHOOL_HEADLINE  = "landing.school_headline"
    const val LANDING_PARENT_HEADLINE  = "landing.parent_headline"
    const val LANDING_SCHOOL_SUB       = "landing.school_sub"
    const val LANDING_PARENT_SUB       = "landing.parent_sub"
    const val LANDING_TAB_SCHOOLS      = "landing.tab_schools"
    const val LANDING_TAB_PARENTS      = "landing.tab_parents"
    const val LANDING_IMG_LABEL_SCHOOL = "landing.img_label_school"
    const val LANDING_IMG_LABEL_PARENT = "landing.img_label_parent"
    const val LANDING_CTA_SCHOOLS      = "landing.cta_schools"
    const val LANDING_CTA_PARENTS      = "landing.cta_parents"
    const val LANDING_OUTLINED_PARENTS = "landing.outlined_parents"
    const val LANDING_OUTLINED_SCHOOLS = "landing.outlined_schools"
    const val LANDING_FOOTER_PREFIX    = "landing.footer_prefix"
    const val LANDING_FOOTER_TERMS     = "landing.footer_terms"
    const val LANDING_FOOTER_AND       = "landing.footer_and"
    const val LANDING_FOOTER_PRIVACY   = "landing.footer_privacy"
    const val LANDING_SCHOOL_F1_T      = "landing.school_f1_t"
    const val LANDING_SCHOOL_F1_D      = "landing.school_f1_d"
    const val LANDING_SCHOOL_F2_T      = "landing.school_f2_t"
    const val LANDING_SCHOOL_F2_D      = "landing.school_f2_d"
    const val LANDING_SCHOOL_F3_T      = "landing.school_f3_t"
    const val LANDING_SCHOOL_F3_D      = "landing.school_f3_d"
    const val LANDING_SCHOOL_F4_T      = "landing.school_f4_t"
    const val LANDING_SCHOOL_F4_D      = "landing.school_f4_d"
    const val LANDING_SCHOOL_F5_T      = "landing.school_f5_t"
    const val LANDING_SCHOOL_F5_D      = "landing.school_f5_d"
    const val LANDING_SCHOOL_F6_T      = "landing.school_f6_t"
    const val LANDING_SCHOOL_F6_D      = "landing.school_f6_d"
    const val LANDING_PARENT_F1_T      = "landing.parent_f1_t"
    const val LANDING_PARENT_F1_D      = "landing.parent_f1_d"
    const val LANDING_PARENT_F2_T      = "landing.parent_f2_t"
    const val LANDING_PARENT_F2_D      = "landing.parent_f2_d"
    const val LANDING_PARENT_F3_T      = "landing.parent_f3_t"
    const val LANDING_PARENT_F3_D      = "landing.parent_f3_d"
    const val LANDING_PARENT_F4_T      = "landing.parent_f4_t"
    const val LANDING_PARENT_F4_D      = "landing.parent_f4_d"
    const val LANDING_PARENT_F5_T      = "landing.parent_f5_t"
    const val LANDING_PARENT_F5_D      = "landing.parent_f5_d"
    const val LANDING_PARENT_F6_T      = "landing.parent_f6_t"
    const val LANDING_PARENT_F6_D      = "landing.parent_f6_d"

    // CommonLandingScreenV3 — Hero
    const val LV3_BRAND               = "lv3.brand"
    const val LV3_SCHOOL_TAGLINE      = "lv3.school_tagline"
    const val LV3_PARENT_TAGLINE      = "lv3.parent_tagline"
    const val LV3_SCHOOL_CONTEXT      = "lv3.school_context"
    const val LV3_PARENT_CONTEXT      = "lv3.parent_context"
    const val LV3_PILL_SCHOOLS        = "lv3.pill_schools"
    const val LV3_PILL_PARENTS        = "lv3.pill_parents"
    // V3 — Morphing words
    const val LV3_SCHOOL_MORPH_1      = "lv3.school_morph_1"
    const val LV3_SCHOOL_MORPH_2      = "lv3.school_morph_2"
    const val LV3_SCHOOL_MORPH_3      = "lv3.school_morph_3"
    const val LV3_SCHOOL_MORPH_4      = "lv3.school_morph_4"
    const val LV3_PARENT_MORPH_1      = "lv3.parent_morph_1"
    const val LV3_PARENT_MORPH_2      = "lv3.parent_morph_2"
    const val LV3_PARENT_MORPH_3      = "lv3.parent_morph_3"
    const val LV3_PARENT_MORPH_4      = "lv3.parent_morph_4"
    // V3 — Command center
    const val LV3_CMD_SCHOOL_EYEBROW  = "lv3.cmd_school_eyebrow"
    const val LV3_CMD_PARENT_EYEBROW  = "lv3.cmd_parent_eyebrow"
    const val LV3_CMD_SCHOOL_TITLE    = "lv3.cmd_school_title"
    const val LV3_CMD_PARENT_TITLE    = "lv3.cmd_parent_title"
    const val LV3_LIVE                = "lv3.live"
    const val LV3_CMD_STUDENTS        = "lv3.cmd_students"
    const val LV3_CMD_TEACHERS        = "lv3.cmd_teachers"
    const val LV3_CMD_ATTENDANCE      = "lv3.cmd_attendance"
    const val LV3_CMD_FEE             = "lv3.cmd_fee"
    const val LV3_CMD_ADMISSIONS      = "lv3.cmd_admissions"
    const val LV3_CMD_ADMISSIONS_TREND = "lv3.cmd_admissions_trend"
    const val LV3_CMD_SATISFACTION    = "lv3.cmd_satisfaction"
    const val LV3_CMD_SATISFACTION_TREND = "lv3.cmd_satisfaction_trend"
    const val LV3_CMD_P_ATTENDANCE    = "lv3.cmd_p_attendance"
    const val LV3_CMD_P_ATTENDANCE_V  = "lv3.cmd_p_attendance_v"
    const val LV3_CMD_P_LAST_TEST     = "lv3.cmd_p_last_test"
    const val LV3_CMD_P_FEES          = "lv3.cmd_p_fees"
    const val LV3_CMD_P_FEES_V        = "lv3.cmd_p_fees_v"
    const val LV3_CMD_P_MESSAGES      = "lv3.cmd_p_messages"
    const val LV3_CMD_HOMEWORK        = "lv3.cmd_homework"
    const val LV3_CMD_HOMEWORK_TREND  = "lv3.cmd_homework_trend"
    const val LV3_CMD_PTM             = "lv3.cmd_ptm"
    const val LV3_CMD_PTM_TREND       = "lv3.cmd_ptm_trend"
    // V3 — Ecosystem
    const val LV3_ECO_SCHOOL_EYEBROW  = "lv3.eco_school_eyebrow"
    const val LV3_ECO_PARENT_EYEBROW  = "lv3.eco_parent_eyebrow"
    const val LV3_ECO_S1_T            = "lv3.eco_s1_t"
    const val LV3_ECO_S1_S            = "lv3.eco_s1_s"
    const val LV3_ECO_S1_M1           = "lv3.eco_s1_m1"
    const val LV3_ECO_S1_M2           = "lv3.eco_s1_m2"
    const val LV3_ECO_S1_M3           = "lv3.eco_s1_m3"
    const val LV3_ECO_S2_T            = "lv3.eco_s2_t"
    const val LV3_ECO_S2_S            = "lv3.eco_s2_s"
    const val LV3_ECO_S2_M1           = "lv3.eco_s2_m1"
    const val LV3_ECO_S2_M2           = "lv3.eco_s2_m2"
    const val LV3_ECO_S2_M3           = "lv3.eco_s2_m3"
    const val LV3_ECO_S3_T            = "lv3.eco_s3_t"
    const val LV3_ECO_S3_S            = "lv3.eco_s3_s"
    const val LV3_ECO_S3_M1           = "lv3.eco_s3_m1"
    const val LV3_ECO_S3_M2           = "lv3.eco_s3_m2"
    const val LV3_ECO_S3_M3           = "lv3.eco_s3_m3"
    const val LV3_ECO_S4_T            = "lv3.eco_s4_t"
    const val LV3_ECO_S4_S            = "lv3.eco_s4_s"
    const val LV3_ECO_S4_M1           = "lv3.eco_s4_m1"
    const val LV3_ECO_S4_M2           = "lv3.eco_s4_m2"
    const val LV3_ECO_S4_M3           = "lv3.eco_s4_m3"
    const val LV3_ECO_P1_T            = "lv3.eco_p1_t"
    const val LV3_ECO_P1_S            = "lv3.eco_p1_s"
    const val LV3_ECO_P1_M1           = "lv3.eco_p1_m1"
    const val LV3_ECO_P1_M2           = "lv3.eco_p1_m2"
    const val LV3_ECO_P1_M3           = "lv3.eco_p1_m3"
    const val LV3_ECO_P2_T            = "lv3.eco_p2_t"
    const val LV3_ECO_P2_S            = "lv3.eco_p2_s"
    const val LV3_ECO_P2_M1           = "lv3.eco_p2_m1"
    const val LV3_ECO_P2_M2           = "lv3.eco_p2_m2"
    const val LV3_ECO_P2_M3           = "lv3.eco_p2_m3"
    const val LV3_ECO_P3_T            = "lv3.eco_p3_t"
    const val LV3_ECO_P3_S            = "lv3.eco_p3_s"
    const val LV3_ECO_P3_M1           = "lv3.eco_p3_m1"
    const val LV3_ECO_P3_M2           = "lv3.eco_p3_m2"
    const val LV3_ECO_P3_M3           = "lv3.eco_p3_m3"
    const val LV3_ECO_P4_T            = "lv3.eco_p4_t"
    const val LV3_ECO_P4_S            = "lv3.eco_p4_s"
    const val LV3_ECO_P4_M1           = "lv3.eco_p4_m1"
    const val LV3_ECO_P4_M2           = "lv3.eco_p4_m2"
    const val LV3_ECO_P4_M3           = "lv3.eco_p4_m3"
    // V3 — AI insight
    const val LV3_AI_TITLE            = "lv3.ai_title"
    const val LV3_AI_LABEL            = "lv3.ai_label"
    const val LV3_AI_S1               = "lv3.ai_s1"
    const val LV3_AI_S2               = "lv3.ai_s2"
    const val LV3_AI_S3               = "lv3.ai_s3"
    const val LV3_AI_P1               = "lv3.ai_p1"
    const val LV3_AI_P2               = "lv3.ai_p2"
    const val LV3_AI_P3               = "lv3.ai_p3"
    // V3 — Timeline
    const val LV3_TL_SCHOOL_EYEBROW   = "lv3.tl_school_eyebrow"
    const val LV3_TL_PARENT_EYEBROW   = "lv3.tl_parent_eyebrow"
    const val LV3_TL_S1_T             = "lv3.tl_s1_t"
    const val LV3_TL_S1_D             = "lv3.tl_s1_d"
    const val LV3_TL_S2_T             = "lv3.tl_s2_t"
    const val LV3_TL_S2_D             = "lv3.tl_s2_d"
    const val LV3_TL_S3_T             = "lv3.tl_s3_t"
    const val LV3_TL_S3_D             = "lv3.tl_s3_d"
    const val LV3_TL_S4_T             = "lv3.tl_s4_t"
    const val LV3_TL_S4_D             = "lv3.tl_s4_d"
    const val LV3_TL_S5_T             = "lv3.tl_s5_t"
    const val LV3_TL_S5_D             = "lv3.tl_s5_d"
    const val LV3_TL_P1_T             = "lv3.tl_p1_t"
    const val LV3_TL_P1_D             = "lv3.tl_p1_d"
    const val LV3_TL_P2_T             = "lv3.tl_p2_t"
    const val LV3_TL_P2_D             = "lv3.tl_p2_d"
    const val LV3_TL_P3_T             = "lv3.tl_p3_t"
    const val LV3_TL_P3_D             = "lv3.tl_p3_d"
    const val LV3_TL_P4_T             = "lv3.tl_p4_t"
    const val LV3_TL_P4_D             = "lv3.tl_p4_d"
    const val LV3_TL_P5_T             = "lv3.tl_p5_t"
    const val LV3_TL_P5_D             = "lv3.tl_p5_d"
    // V3 — Trust metrics
    const val LV3_TRUST_SCHOOL_EYEBROW = "lv3.trust_school_eyebrow"
    const val LV3_TRUST_PARENT_EYEBROW = "lv3.trust_parent_eyebrow"
    const val LV3_TRUST_S1_V           = "lv3.trust_s1_v"
    const val LV3_TRUST_S1_L           = "lv3.trust_s1_l"
    const val LV3_TRUST_S2_L           = "lv3.trust_s2_l"
    const val LV3_TRUST_S3_L           = "lv3.trust_s3_l"
    const val LV3_TRUST_P1_V           = "lv3.trust_p1_v"
    const val LV3_TRUST_P1_L           = "lv3.trust_p1_l"
    const val LV3_TRUST_P2_V           = "lv3.trust_p2_v"
    const val LV3_TRUST_P2_L           = "lv3.trust_p2_l"
    const val LV3_TRUST_P3_V           = "lv3.trust_p3_v"
    const val LV3_TRUST_P3_L           = "lv3.trust_p3_l"
    // V3 — Testimonials
    const val LV3_TEST_S_QUOTE        = "lv3.test_s_quote"
    const val LV3_TEST_S_ROLE         = "lv3.test_s_role"
    const val LV3_TEST_S_ORG          = "lv3.test_s_org"
    const val LV3_TEST_P_QUOTE        = "lv3.test_p_quote"
    const val LV3_TEST_P_ROLE         = "lv3.test_p_role"
    const val LV3_TEST_P_ORG          = "lv3.test_p_org"
    // V3 — CTA dock
    const val LV3_CTA_PROMPT          = "lv3.cta_prompt"
    const val LV3_CTA_ENTER           = "lv3.cta_enter"
    const val LV3_CTA_PARENT          = "lv3.cta_parent"
    const val LV3_CTA_SCHOOL          = "lv3.cta_school"
    const val LV3_FOOTER_PREFIX       = "lv3.footer_prefix"
    const val LV3_FOOTER_TERMS        = "lv3.footer_terms"
    const val LV3_FOOTER_AND          = "lv3.footer_and"
    const val LV3_FOOTER_PRIVACY      = "lv3.footer_privacy"

    // SchoolOnboardingScreenV2 — Header
    const val OB_ONBOARDING           = "ob.onboarding"
    const val OB_STEP_OF              = "ob.step_of"
    const val OB_BACK                 = "ob.back"
    const val OB_CONTINUE             = "ob.continue"
    const val OB_SETTING_UP           = "ob.setting_up"
    // V2 — Step titles
    const val OB_T_IDENTITY           = "ob.t_identity"
    const val OB_T_ACADEMIC           = "ob.t_academic"
    const val OB_T_CLASSES            = "ob.t_classes"
    const val OB_T_SUBJECTS           = "ob.t_subjects"
    const val OB_T_TEACHERS           = "ob.t_teachers"
    const val OB_T_STUDENTS           = "ob.t_students"
    // V2 — Step 1: Identity
    const val OB_ID_LEGAL_NAME        = "ob.id_legal_name"
    const val OB_ID_LEGAL_PH          = "ob.id_legal_ph"
    const val OB_ID_SHORT_NAME        = "ob.id_short_name"
    const val OB_ID_SHORT_PH          = "ob.id_short_ph"
    const val OB_ID_AFFIL             = "ob.id_affil"
    const val OB_ID_AFFIL_PH          = "ob.id_affil_ph"
    const val OB_ID_BOARD             = "ob.id_board"
    const val OB_ID_SCHOOL_TYPE       = "ob.id_school_type"
    const val OB_ID_PRINCIPAL         = "ob.id_principal"
    const val OB_ID_PRINCIPAL_PH      = "ob.id_principal_ph"
    const val OB_ID_PRINCIPAL_MOB     = "ob.id_principal_mob"
    const val OB_ID_PRINCIPAL_MOB_PH  = "ob.id_principal_mob_ph"
    // V2 — Step 2: Academic year
    const val OB_AY_CURRENT           = "ob.ay_current"
    const val OB_AY_STARTS            = "ob.ay_starts"
    const val OB_AY_ENDS              = "ob.ay_ends"
    const val OB_AY_WORKING_DAYS      = "ob.ay_working_days"
    const val OB_AY_START_TIME        = "ob.ay_start_time"
    const val OB_AY_END_TIME          = "ob.ay_end_time"
    const val OB_AY_PERIODS           = "ob.ay_periods"
    const val OB_AY_PERIODS_PH        = "ob.ay_periods_ph"
    // V2 — Step 3: Classes
    const val OB_CL_TIP               = "ob.cl_tip"
    const val OB_CL_TIP_BODY          = "ob.cl_tip_body"
    const val OB_CL_SECTIONS          = "ob.cl_sections"
    const val OB_CL_ADD_MANUAL        = "ob.cl_add_manual"
    const val OB_CL_ADD_PH            = "ob.cl_add_ph"
    const val OB_CL_ADD_BTN           = "ob.cl_add_btn"
    // V2 — Step 4: Subjects
    const val OB_SJ_OFFERED           = "ob.sj_offered"
    const val OB_SJ_TAP_HINT          = "ob.sj_tap_hint"
    const val OB_SJ_APPLY_ALL         = "ob.sj_apply_all"
    const val OB_SJ_NO_CLASSES        = "ob.sj_no_classes"
    // V2 — Step 5: Teachers
    const val OB_TC_ADD               = "ob.tc_add"
    const val OB_TC_ADD_DESC          = "ob.tc_add_desc"
    const val OB_TC_FULL_NAME         = "ob.tc_full_name"
    const val OB_TC_FULL_NAME_PH      = "ob.tc_full_name_ph"
    const val OB_TC_WORK_EMAIL        = "ob.tc_work_email"
    const val OB_TC_WORK_EMAIL_PH     = "ob.tc_work_email_ph"
    const val OB_TC_NONE_YET          = "ob.tc_none_yet"
    const val OB_TC_NONE_DESC         = "ob.tc_none_desc"
    const val OB_TC_COVERAGE          = "ob.tc_coverage"
    const val OB_TC_COVERAGE_OF       = "ob.tc_coverage_of"
    const val OB_TC_UNASSIGNED        = "ob.tc_unassigned"
    const val OB_TC_SLOTS             = "ob.tc_slots"
    const val OB_TC_IMPORT_CSV        = "ob.tc_import_csv"
    // V2 — Step 6: Students
    const val OB_ST_DROP_CSV          = "ob.st_drop_csv"
    const val OB_ST_OR_BROWSE         = "ob.st_or_browse"
    const val OB_ST_DOWNLOAD          = "ob.st_download"
    const val OB_ST_NONE_YET          = "ob.st_none_yet"
    const val OB_ST_OPTIONAL          = "ob.st_optional"
    const val OB_ST_OPTIONAL_DESC     = "ob.st_optional_desc"
    // V2 — Completion
    const val OB_CM_ALL_SET           = "ob.cm_all_set"
    const val OB_CM_IS_LIVE           = "ob.cm_is_live"
    const val OB_CM_TEACHER_LOGINS    = "ob.cm_teacher_logins"
    const val OB_CM_SHARE_OTP         = "ob.cm_share_otp"
    const val OB_CM_PASSWORD          = "ob.cm_password"
    const val OB_CM_COULDNT_CREATE    = "ob.cm_couldnt_create"
    const val OB_CM_ADD_LATER         = "ob.cm_add_later"
    const val OB_CM_OPEN_DASH         = "ob.cm_open_dash"
    const val OB_CM_EDIT_LATER        = "ob.cm_edit_later"
    const val OB_CM_READY             = "ob.cm_ready"
    const val OB_CM_PROFILE_DONE      = "ob.cm_profile_done"
    const val OB_CM_YOUR_SCHOOL       = "ob.cm_your_school"

    // ═══════════════════════════════════════════════════════════════
    // Phase 2 — Parent Screens
    // ═══════════════════════════════════════════════════════════════

    // ParentAcademicsScreenV2
    const val PA_APPLY_LEAVE           = "pa.apply_leave"
    const val PA_LEAVE_DESC            = "pa.leave_desc"
    const val PA_THIS_TERM             = "pa.this_term"
    const val PA_ATTENDANCE_RATE       = "pa.attendance_rate"
    const val PA_NO_ATTENDANCE         = "pa.no_attendance"
    const val PA_NO_ATTENDANCE_DESC    = "pa.no_attendance_desc"
    const val PA_AI_EST                = "pa.ai_est"
    const val PA_EST                   = "pa.est"
    const val PA_PENDING               = "pa.pending"
    const val PA_AI_SUMMARY            = "pa.ai_summary"
    const val PA_AI_ESTIMATED          = "pa.ai_estimated"
    const val PA_TYPE_ANSWER           = "pa.type_answer"
    const val PA_SCORE                 = "pa.score"
    const val PA_YOUR_ANSWER           = "pa.your_answer"
    const val PA_CORRECT_ANSWER        = "pa.correct_answer"
    const val PA_LOADING_LEADERBOARD   = "pa.loading_leaderboard"
    const val PA_LEADERBOARD           = "pa.leaderboard"
    const val PA_PARTICIPANTS          = "pa.participants"
    const val PA_BACK_TO_QUIZZES       = "pa.back_to_quizzes"
    const val PA_HEALTH_RECORDS        = "pa.health_records"
    const val PA_HEALTH_RECORDS_DESC   = "pa.health_records_desc"
    const val PA_AI_REPORT_CARD        = "pa.ai_report_card"
    const val PA_AI_REPORT_CARD_DESC   = "pa.ai_report_card_desc"
    const val PA_NO_MARKS              = "pa.no_marks"
    const val PA_NO_MARKS_DESC         = "pa.no_marks_desc"
    const val PA_NO_SYLLABUS           = "pa.no_syllabus"
    const val PA_NO_SYLLABUS_DESC      = "pa.no_syllabus_desc"
    const val PA_NO_PROGRESS           = "pa.no_progress"
    const val PA_NO_PROGRESS_DESC      = "pa.no_progress_desc"
    const val PA_NO_DAILY_LOGS         = "pa.no_daily_logs"
    const val PA_NO_DAILY_LOGS_DESC    = "pa.no_daily_logs_desc"
    const val PA_NO_QUIZZES            = "pa.no_quizzes"
    const val PA_NO_QUIZZES_DESC       = "pa.no_quizzes_desc"
    const val PA_LEVEL                 = "pa.level"
    const val PA_PERCENT_COMPLETE      = "pa.percent_complete"
    const val PA_QUIZ_QUESTIONS        = "pa.quiz_questions"
    const val PA_START                 = "pa.start"
    const val PA_QUIZ                  = "pa.quiz"
    const val PA_YOU                   = "pa.you"
    const val PA_MATCH                 = "pa.match"

    // ParentProfileCardScreenV2
    const val PC_ATTENDANCE            = "pc.attendance"
    const val PC_LATEST_SCORE          = "pc.latest_score"
    const val PC_TO_NEXT               = "pc.to_next"
    const val PC_TOPICS_TODAY          = "pc.topics_today"
    const val PC_ATTEND                = "pc.attend"
    const val PC_SCORE                 = "pc.score"
    const val PC_TODAY                 = "pc.today"
    const val PC_TOPIC                 = "pc.topic"
    const val PC_TOPICS                = "pc.topics"

    // ParentProfileScreenV2 + ParentProfileCardScreenV2 (shared)
    const val PP_LOGOUT_TITLE          = "pp.logout_title"
    const val PP_LOGOUT_MSG            = "pp.logout_msg"
    const val PP_LOGOUT_CONFIRM        = "pp.logout_confirm"
    const val PP_PROFILE               = "pp.profile"
    const val PP_PROFILE_UNAVAILABLE   = "pp.profile_unavailable"
    const val PP_PROFILE_UNAVAILABLE_DESC = "pp.profile_unavailable_desc"
    const val PP_LANGUAGE              = "pp.language"

    // ParentLibraryScreenV2
    const val PL_LIBRARY               = "pl.library"
    const val PL_BACK                  = "pl.back"
    const val PL_BOOKS_FOUND           = "pl.books_found"
    const val PL_RESERVE_BOOK          = "pl.reserve_book"
    const val PL_RESERVE_MSG           = "pl.reserve_msg"
    const val PL_RESERVE               = "pl.reserve"
    const val PL_MY_CHILD_BOOKS        = "pl.my_child_books"
    const val PL_MY_CHILD_BOOKS_DESC   = "pl.my_child_books_desc"
    const val PL_NO_BOOKS_ISSUED       = "pl.no_books_issued"
    const val PL_NO_BOOKS_ISSUED_DESC  = "pl.no_books_issued_desc"
    const val PL_ISSUED                = "pl.issued"
    const val PL_RENEWALS              = "pl.renewals"
    const val PL_RESERVATIONS          = "pl.reservations"
    const val PL_RESERVATIONS_DESC     = "pl.reservations_desc"
    const val PL_NO_RESERVATIONS       = "pl.no_reservations"
    const val PL_NO_RESERVATIONS_DESC  = "pl.no_reservations_desc"
    const val PL_RESERVED_ON           = "pl.reserved_on"
    const val PL_CANCEL_RESERVATION    = "pl.cancel_reservation"
    const val PL_CANCEL_RESERVATION_MSG = "pl.cancel_reservation_msg"
    const val PL_CANCEL_RESERVATION_CONFIRM = "pl.cancel_reservation_confirm"
    const val PL_KEEP                  = "pl.keep"

    // ParentReportScreen
    const val PR_AI_REPORT_CARD        = "pr.ai_report_card"
    const val PR_NO_REPORTS            = "pr.no_reports"
    const val PR_NO_REPORTS_DESC       = "pr.no_reports_desc"
    const val PR_CONFERENCE_PACK       = "pr.conference_pack"
    const val PR_SUMMARY               = "pr.summary"
    const val PR_FOCUS_AREAS           = "pr.focus_areas"
    const val PR_STRENGTHS             = "pr.strengths"
    const val PR_CONFERENCE_TIPS       = "pr.conference_tips"
    const val PR_PUBLISHED             = "pr.published"
    const val PR_PUBLISHED_ON          = "pr.published_on"

    // ScholarshipWorkflowScreenV2
    const val SW_PROFILE_STRENGTH      = "sw.profile_strength"
    const val SW_ELIGIBILITY           = "sw.eligibility"
    const val SW_AWARD                 = "sw.award"
    const val SW_APPLY_BY              = "sw.apply_by"
    const val SW_REMARKS               = "sw.remarks"
    const val SW_DISBURSED             = "sw.disbursed"
    const val SW_REF                   = "sw.ref"
    const val SW_APPLY_FOR_SCHOLARSHIP = "sw.apply_for_scholarship"
    const val SW_CHILD_ID              = "sw.child_id"
    const val SW_DOCUMENTS             = "sw.documents"

    // ScholarshipsScreenV2
    const val SL_PROFILE_STRENGTH      = "sl.profile_strength"
    const val SL_AWARD                 = "sl.award"
    const val SL_CLOSES_IN             = "sl.closes_in"

    // ParentHealthScreenV2
    const val PHS_NO_PROFILE           = "phs.no_profile"
    const val PHS_NO_PROFILE_DESC      = "phs.no_profile_desc"
    const val PHS_DOSE                 = "phs.dose"
    const val PHS_BY                   = "phs.by"
    const val PHS_NEXT_DUE             = "phs.next_due"
    const val PHS_TREATMENT            = "phs.treatment"
    const val PHS_MEDICATION           = "phs.medication"
    const val PHS_TIME                 = "phs.time"
    const val PHS_PARENT_NOTIFIED      = "phs.parent_notified"

    // ParentHomeScreenV2
    const val PH_STAY_INFORMED         = "ph.stay_informed"
    const val PH_STAY_INFORMED_MSG     = "ph.stay_informed_msg"
    const val PH_ENABLE                = "ph.enable"
    const val PH_NOT_NOW               = "ph.not_now"
    const val PH_NO_CHILD_LINKED       = "ph.no_child_linked"
    const val PH_NO_CHILD_LINKED_DESC  = "ph.no_child_linked_desc"
    const val PH_TRACK_BUS             = "ph.track_bus"
    const val PH_TRACK_BUS_DESC        = "ph.track_bus_desc"
    const val PH_SCHOLARSHIPS          = "ph.scholarships"
    const val PH_SCHOLARSHIPS_DESC     = "ph.scholarships_desc"
    const val PH_DIGITAL_ID            = "ph.digital_id"
    const val PH_DIGITAL_ID_DESC       = "ph.digital_id_desc"
    const val PH_LIBRARY               = "ph.library"
    const val PH_LIBRARY_DESC          = "ph.library_desc"
    const val PH_SCHOOL_EVENTS         = "ph.school_events"
    const val PH_SCHOOL_EVENTS_DESC    = "ph.school_events_desc"

    // ParentScheduleCard
    const val PS_TODAY_SCHEDULE        = "ps.today_schedule"
    const val PS_TODAY_BADGE           = "ps.today_badge"
    const val PS_WEEKLY_TIMETABLE      = "ps.weekly_timetable"
    const val PS_NO_CLASSES            = "ps.no_classes"

    // ParentFeesScreenV2
    const val PF_FEES                  = "pf.fees"
    const val PF_PAY_NOW               = "pf.pay_now"
    const val PF_COMING_SOON           = "pf.coming_soon"

    // ParentEventRegistrationScreenV2
    const val PE_CANCEL_REGISTRATION   = "pe.cancel_registration"
    const val PE_CANCEL_REGISTRATION_MSG = "pe.cancel_registration_msg"
    const val PE_YES_CANCEL            = "pe.yes_cancel"

    // ParentLeaveScreenV2
    const val PLV_LEAVE                = "plv.leave"
    const val PLV_APPLY_FOR_LEAVE      = "plv.apply_for_leave"
    const val PLV_MY_REQUESTS          = "plv.my_requests"
    const val PLV_NO_REQUESTS          = "plv.no_requests"
    const val PLV_NO_REQUESTS_DESC     = "plv.no_requests_desc"
    const val PLV_FROM                 = "plv.from"
    const val PLV_START_DATE           = "plv.start_date"
    const val PLV_TO                   = "plv.to"
    const val PLV_END_DATE             = "plv.end_date"
    const val PLV_REASON               = "plv.reason"
    const val PLV_REASON_PH            = "plv.reason_ph"

    // ParentMessagesScreenV2
    const val PM_NEW_MESSAGE           = "pm.new_message"
    const val PM_NO_MESSAGES           = "pm.no_messages"
    const val PM_NO_MESSAGES_DESC      = "pm.no_messages_desc"
    const val PM_NO_ONE_TO_MESSAGE     = "pm.no_one_to_message"
    const val PM_NO_ONE_TO_MESSAGE_DESC = "pm.no_one_to_message_desc"
    const val PM_START_CONVERSATION    = "pm.start_conversation"

    // ParentPulseScreen
    const val PPS_PARENT_PULSE         = "pps.parent_pulse"
    const val PPS_NO_PULSE             = "pps.no_pulse"
    const val PPS_NO_PULSE_DESC        = "pps.no_pulse_desc"
    const val PPS_NO_HISTORY           = "pps.no_history"
    const val PPS_NO_HISTORY_DESC      = "pps.no_history_desc"
    const val PPS_NO_PULSE_AVAILABLE   = "pps.no_pulse_available"
    const val PPS_NO_PULSE_AVAILABLE_DESC = "pps.no_pulse_available_desc"
    const val PPS_CLOSE               = "pps.close"
    const val PPS_HISTORY             = "pps.history"
    const val PPS_VIEW_HISTORY        = "pps.view_history"

    // ParentUnlinkedScreenV2
    const val PU_LINK_CHILD            = "pu.link_child"
    const val PU_EXPLORE_SCHOOLS       = "pu.explore_schools"
    const val PU_WELCOME               = "pu.welcome"
    const val PU_LINK_TITLE            = "pu.link_title"
    const val PU_EXPLORE_TITLE         = "pu.explore_title"
    const val PU_LINK_DESC             = "pu.link_desc"
    const val PU_EXPLORE_DESC          = "pu.explore_desc"

    // ParentConversationsScreenV2
    const val PCV_MESSAGES             = "pcv.messages"
    const val PCV_ANNOUNCEMENTS        = "pcv.announcements"
    const val PCV_CONVERSATIONS        = "pcv.conversations"

    // ParentActivityScreenV2
    const val PAC_ACTIVITY             = "pac.activity"

    // ParentPewsScreenV2
    const val PPEWS_ATTENDANCE         = "ppews.attendance"

    // ParentCoveredDetailOverlay
    const val PCD_TODAYS_TOPICS        = "pcd.todays_topics"
    const val PCD_NO_TOPICS            = "pcd.no_topics"
    const val PCD_SYLLABUS_COVERAGE    = "pcd.syllabus_coverage"

    // ParentResultsFeesCards
    const val PRF_PUBLISHED            = "prf.published"

    // AiReportCardPreview
    const val AIP_AI_NARRATIVE         = "aip.ai_narrative"

    // BusTrackingScreenV2
    const val BT_BUS_TRACKING          = "bt.bus_tracking"
    const val BT_NO_TRANSPORT          = "bt.no_transport"
    const val BT_NO_TRANSPORT_DESC     = "bt.no_transport_desc"
    const val BT_WAITING               = "bt.waiting"
    const val BT_ROUTE                 = "bt.route"
    const val BT_BUS                   = "bt.bus"
    const val BT_ETA                   = "bt.eta"
    const val BT_NEXT_STOP             = "bt.next_stop"

    // DigitalIdCardScreen
    const val DID_DIGITAL_ID_CARD      = "did.digital_id_card"
    const val DID_SHOW_BACK            = "did.show_back"
    const val DID_SHOW_FRONT           = "did.show_front"
    const val DID_SCAN_QR_BACK         = "did.scan_qr_back"
    const val DID_VALID_TILL           = "did.valid_till"
    const val DID_LOADING              = "did.loading"
    const val DID_NO_ID_CARD           = "did.no_id_card"
    const val DID_QR_CODE              = "did.qr_code"
    const val DID_SCAN_VERIFY          = "did.scan_verify"

    // ParentPortalV2 (dock labels + header)
    const val PPRT_HOME                = "pprt.home"
    const val PPRT_ACADEMICS           = "pprt.academics"
    const val PPRT_FEES                = "pprt.fees"
    const val PPRT_CONVERSATIONS       = "pprt.conversations"
    const val PPRT_PROFILE             = "pprt.profile"
    const val PPRT_LEVEL_JOURNEY       = "pprt.level_journey"
    const val PPRT_LEVEL               = "pprt.level"
    const val PPRT_YOUR_CHILD          = "pprt.your_child"
    const val PPRT_SWITCH_CHILD        = "pprt.switch_child"

    // PulseCard
    const val PUL_HW                   = "pul.hw"
    const val PUL_MSGS                 = "pul.msgs"
    const val PUL_ALERTS               = "pul.alerts"
    const val PUL_WEEKLY_PULSE         = "pul.weekly_pulse"
    const val PUL_ATTENDANCE           = "pul.attendance"
    const val PUL_MARKS_THIS_WEEK      = "pul.marks_this_week"
    const val PUL_ACTION_ITEMS         = "pul.action_items"
    const val PUL_UPCOMING             = "pul.upcoming"

    // ParentLeaveScreenV2 (extras)
    const val PLV_SUBMIT_REQUEST       = "plv.submit_request"
    const val PLV_CHILD                = "plv.child"

    // ParentMessagesScreenV2 (extras)
    const val PM_MESSAGES              = "pm.messages"
    const val PM_CONVERSATION          = "pm.conversation"
    const val PM_SELECT_RECIPIENT      = "pm.select_recipient"
    const val PM_PICK_RECIPIENT_PH     = "pm.pick_recipient_ph"
    const val PM_MESSAGE_NAME_PH       = "pm.message_name_ph"
    const val PM_TYPE_MESSAGE_PH       = "pm.type_message_ph"
    const val PM_MESSAGE_DELETED       = "pm.message_deleted"
    const val PM_EDITED                = "pm.edited"

    // ParentAttendanceCard
    const val PATT_ATTENDANCE_TODAY    = "patt.attendance_today"
    const val PATT_THIS_MONTH          = "patt.this_month"
    const val PATT_PERCENT_PRESENT     = "patt.percent_present"
    const val PATT_TRACKING_FROM_TODAY = "patt.tracking_from_today"
    const val PATT_MONTH_FILLS         = "patt.month_fills"
    const val PATT_SWIPE_CALENDAR      = "patt.swipe_calendar"
    const val PATT_PRESENT             = "patt.present"
    const val PATT_LATE                = "patt.late"
    const val PATT_ABSENT              = "patt.absent"
    const val PATT_HOLIDAY             = "patt.holiday"
    const val PATT_BREAK               = "patt.break"
    const val PATT_SUNDAY              = "patt.sunday"
    const val PATT_AWAITING            = "patt.awaiting"
    const val PATT_MARKED_PRESENT      = "patt.marked_present"
    const val PATT_IN_SCHOOL           = "patt.in_school"
    const val PATT_ARRIVED_LATE        = "patt.arrived_late"
    const val PATT_MARKED_PRESENT_LATE = "patt.marked_present_late"
    const val PATT_MARKED_ABSENT       = "patt.marked_absent"
    const val PATT_NO_ATTENDANCE_TODAY = "patt.no_attendance_today"
    const val PATT_SCHOOL_HOLIDAY       = "patt.school_holiday"
    const val PATT_ENJOY_DAY_OFF       = "patt.enjoy_day_off"
    const val PATT_ON_VACATION         = "patt.on_vacation"
    const val PATT_ENJOY_BREAK         = "patt.enjoy_break"
    const val PATT_NO_SCHOOL           = "patt.no_school"
    const val PATT_SUNDAY_DESC         = "patt.sunday_desc"
    const val PATT_NOT_MARKED_YET      = "patt.not_marked_yet"
    const val PATT_WAITING_CLASS       = "patt.waiting_class"
    const val PATT_SCHOOL_DAYS         = "patt.school_days"
    const val PATT_LATE_DAYS           = "patt.late_days"
    const val PATT_ABSENT_DAYS         = "patt.absent_days"

    // ParentAttendanceCalendar
    const val PACL_LEGEND              = "pacl.legend"
    const val PACL_PRESENT             = "pacl.present"
    const val PACL_LATE                = "pacl.late"
    const val PACL_ABSENT              = "pacl.absent"

    // ParentCoveredCard
    const val PCC_COVERED_SUMMARY      = "pcc.covered_summary"
    const val PCC_COVERED_LIVE         = "pcc.covered_live"
    const val PCC_NOTHING_LOGGED       = "pcc.nothing_logged"
    const val PCC_NOTHING_COVERED      = "pcc.nothing_covered"
    const val PCC_NOTHING_LOGGED_DESC  = "pcc.nothing_logged_desc"
    const val PCC_FILLS_LIVE           = "pcc.fills_live"
    const val PCC_TOPICS_ACROSS        = "pcc.topics_across"
    const val PCC_MORE                 = "pcc.more"
    const val PCC_TAP_BREAKDOWN        = "pcc.tap_breakdown"

    // ParentNudgeCard
    const val PNC_GOT_IT               = "pnc.got_it"
    const val PNC_HEADLINE_FALLBACK    = "pnc.headline_fallback"

    // ScholarshipWorkflowScreenV2 (extras)
    const val SW_SCHOLARSHIPS          = "sw.scholarships"
    const val SW_NO_SCHOLARSHIPS       = "sw.no_scholarships"
    const val SW_NO_SCHOLARSHIPS_DESC  = "sw.no_scholarships_desc"
    const val SW_AVAILABLE             = "sw.available"
    const val SW_MY_APPLICATIONS       = "sw.my_applications"

    // ParentLibraryScreenV2 (extras)
    const val PL_NO_BOOKS_FOUND        = "pl.no_books_found"
    const val PL_NO_BOOKS_FOUND_DESC   = "pl.no_books_found_desc"
    const val PL_FINE                  = "pl.fine"

    // ParentHealthScreenV2 (extras)
    const val PHS_YOUR_CHILD           = "phs.your_child"
    const val PHS_IMMUNIZATIONS        = "phs.immunizations"
    const val PHS_HEALTH_INCIDENTS     = "phs.health_incidents"
    const val PHS_HEALTH_PROFILE       = "phs.health_profile"
    const val PHS_BLOOD_GROUP          = "phs.blood_group"
    const val PHS_HEIGHT               = "phs.height"
    const val PHS_WEIGHT               = "phs.weight"
    const val PHS_HEIGHT_VALUE         = "phs.height_value"
    const val PHS_WEIGHT_VALUE         = "phs.weight_value"
    const val PHS_ALLERGIES            = "phs.allergies"
    const val PHS_CHRONIC_CONDITIONS   = "phs.chronic_conditions"
    const val PHS_MEDICATIONS          = "phs.medications"
    const val PHS_EMERGENCY_CONTACT    = "phs.emergency_contact"
    const val PHS_NAME                 = "phs.name"
    const val PHS_PHONE                = "phs.phone"
    const val PHS_DOCTOR               = "phs.doctor"

    // ParentReportScreen (extras)
    const val PR_CONFERENCE_SUBTITLE   = "pr.conference_subtitle"
    const val PR_OVERALL               = "pr.overall"
    const val PR_GRADE                 = "pr.grade"
    const val PR_ATTENDANCE            = "pr.attendance"

    // ScholarshipWorkflowScreenV2 (extras 2)
    const val SW_LEVEL                 = "sw.level"
    const val SW_APPLICATIONS          = "sw.applications"
    const val SW_APPROVED              = "sw.approved"
    const val SW_AWARDED               = "sw.awarded"
    const val SW_DAY_STREAK            = "sw.day_streak"
    const val SW_HOT                   = "sw.hot"
    const val SW_RENEWABLE             = "sw.renewable"
    const val SW_APPLY_NOW             = "sw.apply_now"
    const val SW_STUDENT               = "sw.student"
    const val SW_DOCUMENT_URL          = "sw.document_url"
    const val SW_ADD                   = "sw.add"
    const val SW_APPLICATION_TEXT      = "sw.application_text"
    const val SW_CANCEL                = "sw.cancel"
    const val SW_SUBMIT                = "sw.submit"

    // ParentLibraryScreenV2 (extras 2)
    const val PL_TAB_BROWSE            = "pl.tab_browse"
    const val PL_TAB_MY_BOOKS          = "pl.tab_my_books"
    const val PL_TAB_RESERVATIONS      = "pl.tab_reservations"
    const val PL_VIEWING_FOR           = "pl.viewing_for"
    const val PL_PARENT                = "pl.parent"
    const val PL_SEARCH_PH             = "pl.search_ph"

    // ParentHealthScreenV2 (extras 2)
    const val PHS_SEVERITY_MAJOR       = "phs.severity_major"
    const val PHS_SEVERITY_MODERATE    = "phs.severity_moderate"
    const val PHS_SEVERITY_MINOR       = "phs.severity_minor"

    // ── Phase 3: School/Admin screens ──

    // AnalyticsDashboardScreenV2
    const val SCH_ANALYTICS            = "sch.analytics"
    const val SCH_NO_ANALYTICS         = "sch.no_analytics"
    const val SCH_NO_ANALYTICS_DESC    = "sch.no_analytics_desc"
    const val SCH_PERFORMANCE_TREND    = "sch.performance_trend"
    const val SCH_OVERVIEW             = "sch.overview"
    const val SCH_INSIGHTS             = "sch.insights"

    // StaffProfileScreenV2
    const val SCH_STAFF                = "sch.staff"
    const val SCH_NO_PROFILE           = "sch.no_profile"
    const val SCH_NO_PROFILE_DESC      = "sch.no_profile_desc"
    const val SCH_CONTACT              = "sch.contact"
    const val SCH_NO_CONTACT_DETAILS   = "sch.no_contact_details"
    const val SCH_REMOVE_FROM_SCHOOL   = "sch.remove_from_school"
    const val SCH_REMOVE_STAFF_MEMBER  = "sch.remove_staff_member"
    const val SCH_REMOVE_STAFF_CONFIRM = "sch.remove_staff_confirm"
    const val SCH_REMOVE               = "sch.remove"

    // DailyAttendanceScreenV2
    const val SCH_DAILY_ATTENDANCE     = "sch.daily_attendance"
    const val SCH_STUDENTS             = "sch.students"
    const val SCH_FACULTY              = "sch.faculty"
    const val SCH_NO_ROSTER            = "sch.no_roster"
    const val SCH_NO_STUDENTS_IN_CLASS = "sch.no_students_in_class"
    const val SCH_NO_FACULTY_ROSTER    = "sch.no_faculty_roster"
    const val SCH_PRESENT_TODAY        = "sch.present_today"
    const val SCH_STUDENTS_HEADER      = "sch.students_header"
    const val SCH_FACULTY_HEADER       = "sch.faculty_header"

    // PewsEffectivenessScreenV2
    const val SCH_EFFECTIVENESS        = "sch.effectiveness"
    const val SCH_NO_DATA_YET          = "sch.no_data_yet"
    const val SCH_EFFECTIVENESS_DESC   = "sch.effectiveness_desc"
    const val SCH_INTERVENTION_OUTCOMES = "sch.intervention_outcomes"
    const val SCH_OPEN                 = "sch.open"
    const val SCH_RESOLVED             = "sch.resolved"
    const val SCH_IMPROVED             = "sch.improved"
    const val SCH_NO_CHANGE            = "sch.no_change"
    const val SCH_WORSENED             = "sch.worsened"
    const val SCH_RISK_TREND_30        = "sch.risk_trend_30"
    const val SCH_HIGH                 = "sch.high"
    const val SCH_MEDIUM               = "sch.medium"
    const val SCH_WATCH                = "sch.watch"

    // ResultsPublishScreenV2
    const val SCH_RESULTS              = "sch.results"
    const val SCH_TESTS                = "sch.tests"
    const val SCH_CLASSES              = "sch.classes"
    const val SCH_SUBJECTS             = "sch.subjects"
    const val SCH_NO_RESULTS_YET       = "sch.no_results_yet"
    const val SCH_NO_RESULTS_DESC      = "sch.no_results_desc"
    const val SCH_CLASS_AVERAGE        = "sch.class_average"
    const val SCH_EXCEEDING            = "sch.exceeding"
    const val SCH_MEETING              = "sch.meeting"
    const val SCH_BELOW                = "sch.below"
    const val SCH_SCORE_ATTENDANCE     = "sch.score_attendance"

    // SchedulePtmScreenV2
    const val SCH_SCHEDULE_PTM         = "sch.schedule_ptm"
    const val SCH_NO_PTMS_YET          = "sch.no_ptms_yet"
    const val SCH_NO_PTMS_DESC         = "sch.no_ptms_desc"
    const val SCH_NEW_PTM              = "sch.new_ptm"
    const val SCH_TITLE                = "sch.title"
    const val SCH_TITLE_PH             = "sch.title_ph"
    const val SCH_DATE                 = "sch.date"
    const val SCH_PTM_DATE_PH          = "sch.ptm_date_ph"
    const val SCH_SLOT                 = "sch.slot"
    const val SCH_SLOT_PH              = "sch.slot_ph"
    const val SCH_CREATE               = "sch.create"
    const val SCH_SCHEDULE_NEW_PTM     = "sch.schedule_new_ptm"
    const val SCH_ACTIVE               = "sch.active"
    const val SCH_EXPECTED             = "sch.expected"
    const val SCH_CHECKED_IN           = "sch.checked_in"
    const val SCH_INVITES_SENT         = "sch.invites_sent"
    const val SCH_READ                 = "sch.read"
    const val SCH_HISTORY              = "sch.history"
    const val SCH_CLASS_PROGRESS       = "sch.class_progress"

    // PewsCohortScreenV2
    const val SCH_EARLY_WARNING        = "sch.early_warning"
    const val SCH_RECOMPUTE            = "sch.recompute"
    const val SCH_NO_STUDENTS_ATTENTION = "sch.no_students_attention"
    const val SCH_NO_STUDENTS_ATTENTION_DESC = "sch.no_students_attention_desc"
    const val SCH_EFFECTIVENESS_HEADER = "sch.effectiveness_header"
    const val SCH_EFFECTIVENESS_LOOP_DESC = "sch.effectiveness_loop_desc"
    const val SCH_CONFIGURATION        = "sch.configuration"
    const val SCH_CONFIGURATION_DESC   = "sch.configuration_desc"
    const val SCH_RELATIVE_THRESHOLDS  = "sch.relative_thresholds"
    const val SCH_RELATIVE_THRESHOLDS_HINT = "sch.relative_thresholds_hint"
    const val SCH_AI_NARRATIVE         = "sch.ai_narrative"
    const val SCH_AI_NARRATIVE_HINT    = "sch.ai_narrative_hint"
    const val SCH_SHARE_WITH_PARENTS   = "sch.share_with_parents"
    const val SCH_SHARE_WITH_PARENTS_HINT = "sch.share_with_parents_hint"
    const val SCH_RUN_FREQUENCY        = "sch.run_frequency"
    const val SCH_DAILY                = "sch.daily"
    const val SCH_WEEKLY               = "sch.weekly"
    const val SCH_RISK_BAND            = "sch.risk_band"
    const val SCH_AS_OF                = "sch.as_of"
    const val SCH_ALL                  = "sch.all"
    const val SCH_MEDIUM_PLUS          = "sch.medium_plus"
    const val SCH_HIGH_ONLY            = "sch.high_only"
    const val SCH_AI_DISABLED_NOTE     = "sch.ai_disabled_note"
    const val SCH_ALL_ON_TRACK_NOTE    = "sch.all_on_track_note"
    const val SCH_QUEUED               = "sch.queued"
    const val SCH_RUNNING              = "sch.running"
    const val SCH_COMPLETE             = "sch.complete"
    const val SCH_FAILED               = "sch.failed"
    const val SCH_REFRESH              = "sch.refresh"
    const val SCH_RISK_TREND           = "sch.risk_trend"
    const val SCH_RISK_TREND_DESC      = "sch.risk_trend_desc"
    const val SCH_CLASS_SECTION        = "sch.class_section"

    // TeacherAssignmentManagementScreen
    const val SCH_ASSIGN_CLASSES       = "sch.assign_classes"
    const val SCH_NO_TEACHER           = "sch.no_teacher"
    const val SCH_NO_TEACHER_DESC      = "sch.no_teacher_desc"
    const val SCH_REMOVE_ASSIGNMENT    = "sch.remove_assignment"
    const val SCH_REMOVE_ASSIGNMENT_DESC = "sch.remove_assignment_desc"
    const val SCH_SUBJECT_TEACHER      = "sch.subject_teacher"
    const val SCH_TEACHER              = "sch.teacher"
    const val SCH_COUNT_CLASSES        = "sch.count_classes"
    const val SCH_COUNT_SUBJECTS       = "sch.count_subjects"
    const val SCH_CLASSES_ASSIGNED     = "sch.classes_assigned"
    const val SCH_ACTIVE_KPI           = "sch.active_kpi"
    const val SCH_SUBJECTS_ASSIGNED    = "sch.subjects_assigned"
    const val SCH_COVERED              = "sch.covered"
    const val SCH_TOTAL_STUDENTS       = "sch.total_students"
    const val SCH_TAUGHT               = "sch.taught"
    const val SCH_SECTIONS_COVERED     = "sch.sections_covered"
    const val SCH_ACROSS_CLASSES       = "sch.across_classes"
    const val SCH_ASSIGNMENT_SUMMARY   = "sch.assignment_summary"
    const val SCH_CURRENT_ASSIGNMENTS  = "sch.current_assignments"
    const val SCH_NO_CLASSES_ASSIGNED  = "sch.no_classes_assigned"
    const val SCH_CLASS_SECTION_LABEL  = "sch.class_section_label"
    const val SCH_COUNT_STUDENTS       = "sch.count_students"
    const val SCH_ADD_ASSIGNMENT       = "sch.add_assignment"
    const val SCH_LOADING_OPTIONS      = "sch.loading_options"
    const val SCH_NO_CLASSES_SUBJECTS  = "sch.no_classes_subjects"
    const val SCH_STEP_1_SUBJECT       = "sch.step_1_subject"
    const val SCH_STEP_2_CLASSES       = "sch.step_2_classes"
    const val SCH_STEP_3_SECTIONS      = "sch.step_3_sections"
    const val SCH_PICK_CLASSES_FIRST   = "sch.pick_classes_first"
    const val SCH_LEAVE_UNSELECTED     = "sch.leave_unselected"
    const val SCH_STEP_4_PREVIEW       = "sch.step_4_preview"
    const val SCH_CLEAR                = "sch.clear"
    const val SCH_SAVE_ASSIGNMENTS     = "sch.save_assignments"
    const val SCH_WORKLOAD_INSIGHTS    = "sch.workload_insights"
    const val SCH_NO_WORKLOAD_INSIGHTS = "sch.no_workload_insights"
    const val SCH_ASSIGNMENT_DISTRIBUTION = "sch.assignment_distribution"
    const val SCH_CLS_STU              = "sch.cls_stu"

    // SchoolCommsScreenV2
    const val SCH_COMMUNICATIONS       = "sch.communications"
    const val SCH_ANNOUNCEMENTS        = "sch.announcements"
    const val SCH_MESSAGES             = "sch.messages"
    const val SCH_PTM                  = "sch.ptm"
    const val SCH_NOTIFICATIONS        = "sch.notifications"
    const val SCH_PARENT_MESSAGES      = "sch.parent_messages"
    const val SCH_PARENT_MESSAGES_DESC = "sch.parent_messages_desc"
    const val SCH_PARENT_TEACHER_MEETINGS = "sch.parent_teacher_meetings"
    const val SCH_PARENT_TEACHER_MEETINGS_DESC = "sch.parent_teacher_meetings_desc"
    const val SCH_DELIVERY_LOG         = "sch.delivery_log"
    const val SCH_DELIVERY_LOG_DESC    = "sch.delivery_log_desc"
    const val SCH_SCHEDULED            = "sch.scheduled"
    const val SCH_NEW                  = "sch.new"
    const val SCH_NO_ANNOUNCEMENTS     = "sch.no_announcements"
    const val SCH_NO_ANNOUNCEMENTS_DESC = "sch.no_announcements_desc"
    const val SCH_CALENDAR_ONLY        = "sch.calendar_only"
    const val SCH_ANNOUNCEMENT         = "sch.announcement"
    const val SCH_ANNOUNCEMENT_UNAVAILABLE = "sch.announcement_unavailable"
    const val SCH_POSTED_BY            = "sch.posted_by"
    const val SCH_NO_MESSAGES          = "sch.no_messages"
    const val SCH_NO_MESSAGES_DESC     = "sch.no_messages_desc"
    const val SCH_SEE_ALL_MESSAGES     = "sch.see_all_messages"
    const val SCH_SEE_ALL_MESSAGES_DESC = "sch.see_all_messages_desc"
    const val SCH_SEE_ALL_PTM          = "sch.see_all_ptm"
    const val SCH_SEE_ALL_PTM_DESC     = "sch.see_all_ptm_desc"
    const val SCH_NO_DELIVERY_LOG      = "sch.no_delivery_log"
    const val SCH_NO_DELIVERY_LOG_DESC = "sch.no_delivery_log_desc"
    const val SCH_SEE_ALL_DELIVERY_LOG = "sch.see_all_delivery_log"
    const val SCH_SEE_ALL_DELIVERY_LOG_DESC = "sch.see_all_delivery_log_desc"

    // ClassPerformanceScreenV2
    const val SCH_CLASS_PERFORMANCE    = "sch.class_performance"
    const val SCH_CLASS_PERFORMANCE_DESC = "sch.class_performance_desc"
    const val SCH_AVG_PROFICIENCY      = "sch.avg_proficiency"
    const val SCH_ACTIVE_STUDENTS      = "sch.active_students"
    const val SCH_MEDIAN_GRADE         = "sch.median_grade"
    const val SCH_GRADE_DISTRIBUTION   = "sch.grade_distribution"
    const val SCH_SUBJECT_MATRIX       = "sch.subject_matrix"
    const val SCH_EARLY_WARNING_HEADER = "sch.early_warning_header"
    const val SCH_CRITICAL             = "sch.critical"
    const val SCH_MODERATE             = "sch.moderate"
    const val SCH_ON_TARGET            = "sch.on_target"
    const val SCH_TOP_PERFORMER        = "sch.top_performer"
    const val SCH_STAR_1ST             = "sch.star_1st"
    const val SCH_PROGRESS_MONITORING  = "sch.progress_monitoring"
    const val SCH_TREND_UP             = "sch.trend_up"
    const val SCH_TREND_DOWN           = "sch.trend_down"
    const val SCH_TREND_FLAT           = "sch.trend_flat"
    const val SCH_PROGRESS_SCORES      = "sch.progress_scores"
    const val SCH_PROGRESS_ATTENDANCE  = "sch.progress_attendance"

    // AcademicYearManagementScreenV2
    const val SCH_ACADEMIC_YEAR        = "sch.academic_year"
    const val SCH_CLOSE                = "sch.close"
    const val SCH_NO_ACADEMIC_YEARS    = "sch.no_academic_years"
    const val SCH_NO_ACADEMIC_YEARS_DESC = "sch.no_academic_years_desc"
    const val SCH_CREATE_ACADEMIC_YEAR = "sch.create_academic_year"
    const val SCH_NAME                 = "sch.name"
    const val SCH_YEAR_NAME_PH         = "sch.year_name_ph"
    const val SCH_START_DATE           = "sch.start_date"
    const val SCH_END_DATE             = "sch.end_date"
    const val SCH_SAVE_DRAFT           = "sch.save_draft"
    const val SCH_CREATE_ACTIVATE      = "sch.create_activate"
    const val SCH_ACTIVE_YEAR          = "sch.active_year"
    const val SCH_HISTORICAL_DRAFTS    = "sch.historical_drafts"
    const val SCH_ACTIVATE             = "sch.activate"
    const val SCH_ARCHIVE              = "sch.archive"
    const val SCH_SCHOOL_DAYS          = "sch.school_days"
    const val SCH_HOLIDAYS             = "sch.holidays"

    // IdCardGenerateTab
    const val SCH_NO_TEMPLATES         = "sch.no_templates"
    const val SCH_NO_TEMPLATES_DESC    = "sch.no_templates_desc"
    const val SCH_SELECT_TEMPLATE      = "sch.select_template"
    const val SCH_INACTIVE             = "sch.inactive"
    const val SCH_SELECT_SCOPE         = "sch.select_scope"
    const val SCH_ALL_STUDENTS         = "sch.all_students"
    const val SCH_ALL_STAFF            = "sch.all_staff"
    const val SCH_BY_CLASS             = "sch.by_class"
    const val SCH_CLASS_ID_UUID        = "sch.class_id_uuid"
    const val SCH_GENERATING           = "sch.generating"
    const val SCH_GENERATE_CARDS       = "sch.generate_cards"
    const val SCH_RENDERING_CARDS      = "sch.rendering_cards"

    // TeacherPerformanceScreenV2
    const val SCH_TEACHER_PERFORMANCE  = "sch.teacher_performance"
    const val SCH_TEACHER_PERFORMANCE_DESC = "sch.teacher_performance_desc"
    const val SCH_AGGREGATE_COMPLIANCE = "sch.aggregate_compliance"
    const val SCH_STAR_FACULTY         = "sch.star_faculty"
    const val SCH_ACCOUNTABILITY_MATRIX = "sch.accountability_matrix"
    const val SCH_DEPARTMENT_EFFICIENCY = "sch.department_efficiency"
    const val SCH_COMPLIANCE           = "sch.compliance"
    const val SCH_DELAY                = "sch.delay"
    const val SCH_AVG_MARK             = "sch.avg_mark"

    // IdCardCardsTab
    const val SCH_SEARCH_BY_NAME       = "sch.search_by_name"
    const val SCH_TEACHERS             = "sch.teachers"
    const val SCH_CARDS_COUNT          = "sch.cards_count"
    const val SCH_NO_CARDS_MATCH       = "sch.no_cards_match"
    const val SCH_NO_CARDS_YET         = "sch.no_cards_yet"
    const val SCH_TRY_DIFFERENT_SEARCH = "sch.try_different_search"
    const val SCH_GO_TO_GENERATE       = "sch.go_to_generate"
    const val SCH_DELETE_ID_CARD       = "sch.delete_id_card"
    const val SCH_DELETE_ID_CARD_CONFIRM = "sch.delete_id_card_confirm"
    const val SCH_ID_CARD              = "sch.id_card"
    const val SCH_QR_CODE              = "sch.qr_code"
    const val SCH_PDF                  = "sch.pdf"
    const val SCH_VERIFY               = "sch.verify"
    const val SCH_NO_EXPIRY            = "sch.no_expiry"
    const val SCH_EXPIRED              = "sch.expired"
    const val SCH_EXPIRING             = "sch.expiring"
    const val SCH_VALID                = "sch.valid"

    // Additional keys for IdCardGenerateTab, IdCardCardsTab, PewsCohortScreenV2
    const val SCH_TEMPLATE_STATUS      = "sch.template_status"
    const val SCH_DELETE               = "sch.delete"
    const val SCH_SAVE                 = "sch.save"
    const val SCH_CLASS_SECTION_DASH   = "sch.class_section_dash"
    const val SCH_ACTIVE_LABEL         = "sch.active_label"

    // Phase 3 Batch 1 — AlumniCampaign, AdminReportingEffectiveness, AdminReportPublish, AlumniDetail
    const val SCH_80G_ELIGIBLE_RECEIPT = "sch.80g_eligible_receipt"
    const val SCH_80G_RECEIPT          = "sch.80g_receipt"
    const val SCH_ACHIEVEMENTS         = "sch.achievements"
    const val SCH_ALUMNI_DETAIL        = "sch.alumni_detail"
    const val SCH_ALUMNI_NOT_FOUND     = "sch.alumni_not_found"
    const val SCH_APPROVED             = "sch.approved"
    const val SCH_BATCH_YEAR           = "sch.batch_year"
    const val SCH_CAMPAIGN_COLON       = "sch.campaign_colon"
    const val SCH_CAMPAIGN_DETAIL      = "sch.campaign_detail"
    const val SCH_CAMPAIGN_NOT_FOUND   = "sch.campaign_not_found"
    const val SCH_CAREER               = "sch.career"
    const val SCH_CAUSE_COLON          = "sch.cause_colon"
    const val SCH_CITY                 = "sch.city"
    const val SCH_COMPANY              = "sch.company"
    const val SCH_CONCURRENCY          = "sch.concurrency"
    const val SCH_CURRENT              = "sch.current"
    const val SCH_CURRENT_TERM         = "sch.current_term"
    const val SCH_DATE_COLON           = "sch.date_colon"
    const val SCH_DONATIONS            = "sch.donations"
    const val SCH_DRAFT                = "sch.draft"
    const val SCH_DRAFTS               = "sch.drafts"
    const val SCH_EMAIL                = "sch.email"
    const val SCH_ENABLED              = "sch.enabled"
    const val SCH_EXPERTISE            = "sch.expertise"
    const val SCH_FALLBACK             = "sch.fallback"
    const val SCH_FEATURED             = "sch.featured"
    const val SCH_FLAGGED              = "sch.flagged"
    const val SCH_FLYWHEEL_COMPLETE    = "sch.flywheel_complete"
    const val SCH_LINKEDIN             = "sch.linkedin"
    const val SCH_MENTOR               = "sch.mentor"
    const val SCH_MENTORSHIP           = "sch.mentorship"
    const val SCH_MODE_COLON           = "sch.mode_colon"
    const val SCH_NOT_SET              = "sch.not_set"
    const val SCH_NO_CAREER_HISTORY    = "sch.no_career_history"
    const val SCH_NO_DONATIONS_CAMPAIGN = "sch.no_donations_campaign"
    const val SCH_NO_DONATIONS_RECORDED = "sch.no_donations_recorded"
    const val SCH_N_DONORS             = "sch.n_donors"
    const val SCH_N_IMPROVED           = "sch.n_improved"
    const val SCH_N_REPORTS_PUBLISHED  = "sch.n_reports_published"
    const val SCH_PENDING              = "sch.pending"
    const val SCH_PERIOD_COLON         = "sch.period_colon"
    const val SCH_PHONE                = "sch.phone"
    const val SCH_PRESENT              = "sch.present"
    const val SCH_PREVIOUS             = "sch.previous"
    const val SCH_PRIVACY              = "sch.privacy"
    const val SCH_PROFESSION           = "sch.profession"
    const val SCH_PROFESSIONAL         = "sch.professional"
    const val SCH_PROFILE              = "sch.profile"
    const val SCH_PROFILE_COMPLETENESS = "sch.profile_completeness"
    const val SCH_PROGRESS             = "sch.progress"
    const val SCH_PUBLISHED            = "sch.published"
    const val SCH_PUBLISHING           = "sch.publishing"
    const val SCH_PUBLISH_N_APPROVED   = "sch.publish_n_approved"
    const val SCH_REPORTING_EFFECTIVENESS = "sch.reporting_effectiveness"
    const val SCH_REPORT_CARD_PUBLISHING = "sch.report_card_publishing"
    const val SCH_RUN_FLYWHEEL         = "sch.run_flywheel"
    const val SCH_RUN_FLYWHEEL_BTN     = "sch.run_flywheel_btn"
    const val SCH_SHOW_EMAIL           = "sch.show_email"
    const val SCH_SHOW_PHONE           = "sch.show_phone"
    const val SCH_SKILLS               = "sch.skills"
    const val SCH_STATUS_COLON         = "sch.status_colon"
    const val SCH_TARGET_BATCH_COLON   = "sch.target_batch_colon"
    const val SCH_TERM                 = "sch.term"
    const val SCH_VISIBILITY           = "sch.visibility"

    // Phase 3 Batch 2
    const val SCH_ABSENT               = "sch.absent"
    const val SCH_ACADEMIC_OVERVIEW    = "sch.academic_overview"
    const val SCH_ACADEMIC_SCORE       = "sch.academic_score"
    const val SCH_ADD_SLOT             = "sch.add_slot"
    const val SCH_ADD_STUDENT          = "sch.add_student"
    const val SCH_ADMINISTRATIVE_INFO  = "sch.administrative_info"
    const val SCH_ADMISSION_DATE       = "sch.admission_date"
    const val SCH_ADMISSION_NO         = "sch.admission_no"
    const val SCH_ADMISSION_NUMBER     = "sch.admission_number"
    const val SCH_APPLICABLE_DAYS      = "sch.applicable_days"
    const val SCH_APPLICABLE_DAYS_PH   = "sch.applicable_days_ph"
    const val SCH_ASSIGNMENTS          = "sch.assignments"
    const val SCH_ASSIGNMENT_COMPLETION = "sch.assignment_completion"
    const val SCH_ATTENDANCE           = "sch.attendance"
    const val SCH_ATTENDANCE_OVERVIEW  = "sch.attendance_overview"
    const val SCH_ATTENDANCE_RATE      = "sch.attendance_rate"
    const val SCH_AVERAGE              = "sch.average"
    const val SCH_CLASS                = "sch.class"
    const val SCH_CLASS_LEVEL          = "sch.class_level"
    const val SCH_CLASS_LEVEL_PH       = "sch.class_level_ph"
    const val SCH_CLASS_PH             = "sch.class_ph"
    const val SCH_COMPLETION           = "sch.completion"
    const val SCH_CONFIGURATIONS       = "sch.configurations"
    const val SCH_CONNECTED            = "sch.connected"
    const val SCH_CONTACT_INFORMATION  = "sch.contact_information"
    const val SCH_CONTACT_PARENT       = "sch.contact_parent"
    const val SCH_DANGER_ZONE          = "sch.danger_zone"
    const val SCH_DAYS_LEVEL           = "sch.days_level"
    const val SCH_DEACTIVATE           = "sch.deactivate"
    const val SCH_DEACTIVATE_CONFIG    = "sch.deactivate_config"
    const val SCH_DEACTIVATE_CONFIG_MSG = "sch.deactivate_config_msg"
    const val SCH_DUE                  = "sch.due"
    const val SCH_EDIT_DAY_CONFIG      = "sch.edit_day_config"
    const val SCH_END                  = "sch.end"
    const val SCH_EXPERIENCE           = "sch.experience"
    const val SCH_FEES                 = "sch.fees"
    const val SCH_FORMAT_DAYS          = "sch.format_days"
    const val SCH_FULL_NAME            = "sch.full_name"
    const val SCH_FULL_NAME_PH         = "sch.full_name_ph"
    const val SCH_HEALTH_RECORDS       = "sch.health_records"
    const val SCH_HEALTH_RECORDS_DESC  = "sch.health_records_desc"
    const val SCH_JOINED               = "sch.joined"
    const val SCH_JOINED_DATE          = "sch.joined_date"
    const val SCH_LABEL                = "sch.label"
    const val SCH_LABEL_PH             = "sch.label_ph"
    const val SCH_LATE                 = "sch.late"
    const val SCH_LEAVE                = "sch.leave"
    const val SCH_LINKED               = "sch.linked"
    const val SCH_LOW_ATTENDANCE       = "sch.low_attendance"
    const val SCH_MANAGE_CLASSES_SUBJECTS = "sch.manage_classes_subjects"
    const val SCH_MARKS                = "sch.marks"
    const val SCH_MUST_BE_LEVEL        = "sch.must_be_level"
    const val SCH_NAME_PH              = "sch.name_ph"
    const val SCH_NEW_ADMISSION        = "sch.new_admission"
    const val SCH_NEW_DAY_CONFIG       = "sch.new_day_config"
    const val SCH_NO_ACHIEVEMENTS      = "sch.no_achievements"
    const val SCH_NO_ASSIGNMENTS_YET   = "sch.no_assignments_yet"
    const val SCH_NO_DAY_CONFIGS       = "sch.no_day_configs"
    const val SCH_NO_DAY_CONFIGS_DESC  = "sch.no_day_configs_desc"
    const val SCH_NO_FEE_RECORDS       = "sch.no_fee_records"
    const val SCH_NO_INSIGHTS_YET      = "sch.no_insights_yet"
    const val SCH_NO_LEAVE_APPLICATIONS = "sch.no_leave_applications"
    const val SCH_NO_MARKS_RECORDED    = "sch.no_marks_recorded"
    const val SCH_NO_PARENTS_LINKED    = "sch.no_parents_linked"
    const val SCH_NO_RECENT_ACTIVITY   = "sch.no_recent_activity"
    const val SCH_NO_STUDENTS_YET      = "sch.no_students_yet"
    const val SCH_NO_STUDENTS_YET_DESC = "sch.no_students_yet_desc"
    const val SCH_NO_STUDENT_PROFILE_DESC = "sch.no_student_profile_desc"
    const val SCH_NO_TEACHERS_CONNECTED = "sch.no_teachers_connected"
    const val SCH_N_STUDENTS           = "sch.n_students"
    const val SCH_N_YEARS              = "sch.n_years"
    const val SCH_OVERALL              = "sch.overall"
    const val SCH_PARENTS              = "sch.parents"
    const val SCH_PARENT_CONNECTIONS   = "sch.parent_connections"
    const val SCH_PARENT_PHONE_OPTIONAL = "sch.parent_phone_optional"
    const val SCH_PARENT_PHONE_PH      = "sch.parent_phone_ph"
    const val SCH_PARENT_SATISFACTION  = "sch.parent_satisfaction"
    const val SCH_PERFORMANCE          = "sch.performance"
    const val SCH_PERSONAL             = "sch.personal"
    const val SCH_PHONE_MIN_DIGITS     = "sch.phone_min_digits"
    const val SCH_PRIMARY_GUARDIAN     = "sch.primary_guardian"
    const val SCH_PROFESSIONAL_DETAILS = "sch.professional_details"
    const val SCH_QUICK_ACTIONS        = "sch.quick_actions"
    const val SCH_RECENT_ACTIVITY      = "sch.recent_activity"
    const val SCH_REMOVE_STUDENT       = "sch.remove_student"
    const val SCH_REMOVE_STUDENT_DANGER = "sch.remove_student_danger"
    const val SCH_REMOVE_STUDENT_MSG   = "sch.remove_student_msg"
    const val SCH_REMOVE_STUDENT_ROSTER_MSG = "sch.remove_student_roster_msg"
    const val SCH_REMOVE_TEACHER       = "sch.remove_teacher"
    const val SCH_REMOVE_TEACHER_DANGER = "sch.remove_teacher_danger"
    const val SCH_REMOVE_TEACHER_MSG   = "sch.remove_teacher_msg"
    const val SCH_ROLL_NO              = "sch.roll_no"
    const val SCH_ROLL_NUMBER          = "sch.roll_number"
    const val SCH_ROLL_NUMBER_PH       = "sch.roll_number_ph"
    const val SCH_SAVING               = "sch.saving"
    const val SCH_SCHOOL_DAY_CONFIG    = "sch.school_day_config"
    const val SCH_SEC                  = "sch.sec"
    const val SCH_SECTION              = "sch.section"
    const val SCH_SECTIONS_TAUGHT      = "sch.sections_taught"
    const val SCH_SLOTS_N              = "sch.slots_n"
    const val SCH_START                = "sch.start"
    const val SCH_STUDENT              = "sch.student"
    const val SCH_STUDENT_ID           = "sch.student_id"
    const val SCH_STUDIED              = "sch.studied"
    const val SCH_TEACHER_CONNECTIONS  = "sch.teacher_connections"
    const val SCH_TEACHING_PORTFOLIO   = "sch.teaching_portfolio"
    const val SCH_THIS_STUDENT         = "sch.this_student"
    const val SCH_THIS_TEACHER         = "sch.this_teacher"
    const val SCH_VIEW_PROFILE         = "sch.view_profile"

    // Phase 4 - Teacher screen keys
    const val ATT_LEAVE                                = "att.leave"
    const val COMMON_BUTTON_CREATE                     = "common.button_create"
    const val COMMON_BUTTON_TRY_AGAIN                  = "common.button_try_again"
    const val TC_A                                     = "tc.a"
    const val TC_ABSENT                                = "tc.absent"
    const val TC_ACTIVE_HOMEWORK                       = "tc.active_homework"
    const val TC_ACTIVITIES                            = "tc.activities"
    const val TC_ADD                                   = "tc.add"
    const val TC_ADD_ACTIVITY                          = "tc.add_activity"
    const val TC_ADD_A_CHAPTER                         = "tc.add_a_chapter"
    const val TC_ADD_MANUALLY                          = "tc.add_manually"
    const val TC_ADD_NEW_QUESTION                      = "tc.add_new_question"
    const val TC_ADD_OBJECTIVE                         = "tc.add_objective"
    const val TC_ADD_QUESTION                          = "tc.add_question"
    const val TC_ADD_RESOURCE                          = "tc.add_resource"
    const val TC_ADD_TOPIC                             = "tc.add_topic"
    const val TC_ADMIN                                 = "tc.admin"
    const val TC_ADMIN_NOTE_COLON                      = "tc.admin_note_colon"
    const val TC_AHEAD_OF_SCHEDULE                     = "tc.ahead_of_schedule"
    const val TC_AI_EXTRACT_CHAPTERS_TOPICS            = "tc.ai_extract_chapters_topics"
    const val TC_AI_NARRATIVE_EDITABLE                 = "tc.ai_narrative_editable"
    const val TC_ALL                                   = "tc.all"
    const val TC_ALLERGIES_LABEL                       = "tc.allergies_label"
    const val TC_ALLOW_LATE                            = "tc.allow_late"
    const val TC_ALL_ATTENDANCE_DONE                   = "tc.all_attendance_done"
    const val TC_ALL_CAUGHT_UP                         = "tc.all_caught_up"
    const val TC_ALL_CAUGHT_UP_DAY                     = "tc.all_caught_up_day"
    const val TC_ALL_CLASSES                           = "tc.all_classes"
    const val TC_ANSWER_COLON                          = "tc.answer_colon"
    const val TC_APPLY                                 = "tc.apply"
    const val TC_APPROVE                               = "tc.approve"
    const val TC_APPROVE_ALL                           = "tc.approve_all"
    const val TC_ASSESSMENT_METHOD                     = "tc.assessment_method"
    const val TC_ASSIGN_FIRST_HOMEWORK                 = "tc.assign_first_homework"
    const val TC_ASSIGN_HOMEWORK                       = "tc.assign_homework"
    const val TC_ATTENDANCE                            = "tc.attendance"
    const val TC_ATTENDANCE_DONE                       = "tc.attendance_done"
    const val TC_ATTENDANCE_TODAY                      = "tc.attendance_today"
    const val TC_AT_LEAST_                             = "tc.at_least_"
    const val TC_AUTO_FILL                             = "tc.auto_fill"
    const val TC_AUTO_FILL_FROM_NCERT                  = "tc.auto_fill_from_ncert"
    const val TC_AUTO_FILL_PREVIEW                     = "tc.auto_fill_preview"
    const val TC_AVG                                   = "tc.avg"
    const val TC_AVG_N_PCT_PER_CLASS                   = "tc.avg_n_pct_per_class"
    const val TC_BEHIND_SCHEDULE                       = "tc.behind_schedule"
    const val TC_BELL_SCHEDULE                         = "tc.bell_schedule"
    const val TC_CALENDAR                              = "tc.calendar"
    const val TC_CHANGE                                = "tc.change"
    const val TC_CHANGE_PASSWORD                       = "tc.change_password"
    const val TC_CHANGE_REASON_PH                      = "tc.change_reason_ph"
    const val TC_CHANGE_REQUEST                        = "tc.change_request"
    const val TC_CHANGE_REQUESTS                       = "tc.change_requests"
    const val TC_CHANGE_REQUESTS_APPEAR                = "tc.change_requests_appear"
    const val TC_CHAPTER_TITLE                         = "tc.chapter_title"
    const val TC_CHECKED_IN                            = "tc.checked_in"
    const val TC_CHECK_IN                              = "tc.check_in"
    const val TC_CHOOSE_HOW_TO_BUILD_SYLLABUS          = "tc.choose_how_to_build_syllabus"
    const val TC_CLASS                                 = "tc.class"
    const val TC_CLASSES                               = "tc.classes"
    const val TC_CLASSES_MARKED                        = "tc.classes_marked"
    const val TC_CLASSES_ON_TRACK                      = "tc.classes_on_track"
    const val TC_CLASSES_TO_MARK                       = "tc.classes_to_mark"
    const val TC_CLASSES_YOU_TEACH                     = "tc.classes_you_teach"
    const val TC_CLASS_CANCELLED                       = "tc.class_cancelled"
    const val TC_CLASS_CANCELLED_DATE                  = "tc.class_cancelled_date"
    const val TC_CLASS_SUBJECT                         = "tc.class_subject"
    const val TC_CLASS_TEACHER                         = "tc.class_teacher"
    const val TC_CLOSE_HOMEWORK_DESC                   = "tc.close_homework_desc"
    const val TC_CLOSE_HOMEWORK_Q                      = "tc.close_homework_q"
    const val TC_CLOSE_IT                              = "tc.close_it"
    const val TC_COMPLETE                              = "tc.complete"
    const val TC_COMPLETED                             = "tc.completed"
    const val TC_CONDITIONS_LABEL                      = "tc.conditions_label"
    const val TC_CONFIRM_AND_CREATE                    = "tc.confirm_and_create"
    const val TC_CONFIRM_NEW_PASSWORD                  = "tc.confirm_new_password"
    const val TC_CORRECT_ANSWER                        = "tc.correct_answer"
    const val TC_CORRECT_ANSWER_EG_AB                  = "tc.correct_answer_eg_ab"
    const val TC_CORRECT_ANSWER_TEXT                   = "tc.correct_answer_text"
    const val TC_COULDNT_LOAD_ATTENDANCE               = "tc.couldnt_load_attendance"
    const val TC_COULDNT_LOAD_BOARD                    = "tc.couldnt_load_board"
    const val TC_COULDNT_LOAD_CLASS                    = "tc.couldnt_load_class"
    const val TC_COULDNT_LOAD_CLASSES                  = "tc.couldnt_load_classes"
    const val TC_COULDNT_LOAD_HOMEWORK                 = "tc.couldnt_load_homework"
    const val TC_COULDNT_LOAD_LESSON_PLANS             = "tc.couldnt_load_lesson_plans"
    const val TC_COULDNT_LOAD_PROFILE                  = "tc.couldnt_load_profile"
    const val TC_COULDNT_LOAD_ROSTER                   = "tc.couldnt_load_roster"
    const val TC_COULDNT_LOAD_SCHEDULE                 = "tc.couldnt_load_schedule"
    const val TC_COULDNT_LOAD_SYLLABUS                 = "tc.couldnt_load_syllabus"
    const val TC_COULDNT_LOAD_TEMPLATES                = "tc.couldnt_load_templates"
    const val TC_COULDNT_LOAD_TESTS                    = "tc.couldnt_load_tests"
    const val TC_COVERAGE_N_PCT                        = "tc.coverage_n_pct"
    const val TC_COVERED_DATE                          = "tc.covered_date"
    const val TC_CREATE_AS_DRAFT                       = "tc.create_as_draft"
    const val TC_CREATE_A_TEST                         = "tc.create_a_test"
    const val TC_CREATE_CHAPTERS_TOPICS_ONE_BY_ONE     = "tc.create_chapters_topics_one_by_one"
    const val TC_CREATE_FIRST_LESSON_PLAN              = "tc.create_first_lesson_plan"
    const val TC_CREATE_FIRST_TEST                     = "tc.create_first_test"
    const val TC_CREATE_PLAN                           = "tc.create_plan"
    const val TC_CREATE_TEST                           = "tc.create_test"
    const val TC_CRITICALLY_BEHIND                     = "tc.critically_behind"
    const val TC_CURRENT_PASSWORD                      = "tc.current_password"
    const val TC_CURRICULUM_UNIT_OPTIONAL              = "tc.curriculum_unit_optional"
    const val TC_DAILY_CLASS_LOG                       = "tc.daily_class_log"
    const val TC_DAILY_LOG                             = "tc.daily_log"
    const val TC_DAY                                   = "tc.day"
    const val TC_DAY_AT_A_GLANCE                       = "tc.day_at_a_glance"
    const val TC_DECLINING                             = "tc.declining"
    const val TC_DETAILS_OPTIONAL                      = "tc.details_optional"
    const val TC_DIFFICULTY                            = "tc.difficulty"
    const val TC_DIGITAL_ID_CARD                       = "tc.digital_id_card"
    const val TC_DIGITAL_ID_CARD_DESC                  = "tc.digital_id_card_desc"
    const val TC_DISMISS                               = "tc.dismiss"
    const val TC_DONE                                  = "tc.done"
    const val TC_DRAFT                                 = "tc.draft"
    const val TC_DRAFT_PARENT_MESSAGE                  = "tc.draft_parent_message"
    const val TC_DRAFT_UNITS_NOT_VISIBLE_TO_PARENTS    = "tc.draft_units_not_visible_to_parents"
    const val TC_DUE_DATE                              = "tc.due_date"
    const val TC_DUE_LABEL                             = "tc.due_label"
    const val TC_DUE_PAST_TURNED_IN                    = "tc.due_past_turned_in"
    const val TC_EASY                                  = "tc.easy"
    const val TC_EDIT                                  = "tc.edit"
    const val TC_EDITING_QUESTION                      = "tc.editing_question"
    const val TC_EDIT_DRAFT                            = "tc.edit_draft"
    const val TC_EDIT_LESSON_PLAN                      = "tc.edit_lesson_plan"
    const val TC_END                                   = "tc.end"
    const val TC_ENTERED_N_OF_N                        = "tc.entered_n_of_n"
    const val TC_ESCALATED                             = "tc.escalated"
    const val TC_EST_COMPLETION_DATE                   = "tc.est_completion_date"
    const val TC_EXAM_DATE                             = "tc.exam_date"
    const val TC_EXPLANATION_COLON                     = "tc.explanation_colon"
    const val TC_EXPLANATION_OPTIONAL                  = "tc.explanation_optional"
    const val TC_EXTEND                                = "tc.extend"
    const val TC_EXTENDED_TO                           = "tc.extended_to"
    const val TC_EXTEND_FOR                            = "tc.extend_for"
    const val TC_EXTEND_FOR_CLASS                      = "tc.extend_for_class"
    const val TC_EXTEND_WHOLE_CLASS                    = "tc.extend_whole_class"
    const val TC_FAILING_TREND                         = "tc.failing_trend"
    const val TC_FALSE                                 = "tc.false"
    const val TC_FETCHING_NCERT_REFERENCE              = "tc.fetching_ncert_reference"
    const val TC_FETCH_STANDARD_NCERT_SYLLABUS         = "tc.fetch_standard_ncert_syllabus"
    const val TC_FILL_UPS                              = "tc.fill_ups"
    const val TC_FLAGGED                               = "tc.flagged"
    const val TC_FLAGS                                 = "tc.flags"
    const val TC_FROM                                  = "tc.from"
    const val TC_GENERATE_QUIZ                         = "tc.generate_quiz"
    const val TC_GRADED                                = "tc.graded"
    const val TC_GRANT                                 = "tc.grant"
    const val TC_GRANT_EXTENSION                       = "tc.grant_extension"
    const val TC_GROUNDING_FLAGS_DETECTED              = "tc.grounding_flags_detected"
    const val TC_HARD                                  = "tc.hard"
    const val TC_HEALTH_ALERTS                         = "tc.health_alerts"
    const val TC_HEALTH_ALERTS_DESC                    = "tc.health_alerts_desc"
    const val TC_HEALTH_ALERTS_LIST_DESC               = "tc.health_alerts_list_desc"
    const val TC_HI_NAME                               = "tc.hi_name"
    const val TC_HOLIDAY                               = "tc.holiday"
    const val TC_HOLIDAY_NOTICE                        = "tc.holiday_notice"
    const val TC_HOW_ASSESS_OPTIONAL                   = "tc.how_assess_optional"
    const val TC_IMPORT_MARKS                          = "tc.import_marks"
    const val TC_IMPROVING                             = "tc.improving"
    const val TC_INITIATED_BY                          = "tc.initiated_by"
    const val TC_INSTANTIATE_FROM_TEMPLATE             = "tc.instantiate_from_template"
    const val TC_INSTRUCTIONS_PH                       = "tc.instructions_ph"
    const val TC_LANGUAGE                              = "tc.language"
    const val TC_LANGUAGE_COLON                        = "tc.language_colon"
    const val TC_LAST_MARKED_BY                        = "tc.last_marked_by"
    const val TC_LEAVES                                = "tc.leaves"
    const val TC_LEAVE_REQUESTS                        = "tc.leave_requests"
    const val TC_LESSON                                = "tc.lesson"
    const val TC_LESSON_PLANS                          = "tc.lesson_plans"
    const val TC_LESSON_TITLE                          = "tc.lesson_title"
    const val TC_LINK_HOMEWORK_OPTIONAL                = "tc.link_homework_optional"
    const val TC_LOADING_LEADERBOARD                   = "tc.loading_leaderboard"
    const val TC_LOCKED_UNTIL_EXAM                     = "tc.locked_until_exam"
    const val TC_LOG_OUT                               = "tc.log_out"
    const val TC_LOG_OUT_DESC                          = "tc.log_out_desc"
    const val TC_LOG_OUT_Q                             = "tc.log_out_q"
    const val TC_LOW_ATTENDANCE                        = "tc.low_attendance"
    const val TC_MARK                                  = "tc.mark"
    const val TC_MARKED                                = "tc.marked"
    const val TC_MARKING_ATTENDANCE                    = "tc.marking_attendance"
    const val TC_MARKS                                 = "tc.marks"
    const val TC_MARKS_DROPPING                        = "tc.marks_dropping"
    const val TC_MARKS_PENDING                         = "tc.marks_pending"
    const val TC_MARK_ALL_PRESENT                      = "tc.mark_all_present"
    const val TC_MARK_ATTENDANCE                       = "tc.mark_attendance"
    const val TC_MARK_GRADED                           = "tc.mark_graded"
    const val TC_MARK_IMPROVED                         = "tc.mark_improved"
    const val TC_MATCH                                 = "tc.match"
    const val TC_MAX_MARKS                             = "tc.max_marks"
    const val TC_MAX_N                                 = "tc.max_n"
    const val TC_MAX_N_ENTERED_N_OF_N                  = "tc.max_n_entered_n_of_n"
    const val TC_MCQ                                   = "tc.mcq"
    const val TC_MEDIUM                                = "tc.medium"
    const val TC_MESSAGES                              = "tc.messages"
    const val TC_MESSAGES_DESC                         = "tc.messages_desc"
    const val TC_MIN                                   = "tc.min"
    const val TC_MINUTES                               = "tc.minutes"
    const val TC_MY_LEAVE                              = "tc.my_leave"
    const val TC_NCERT_AUTO_FILL                       = "tc.ncert_auto_fill"
    const val TC_NEEDS_ATTENTION                       = "tc.needs_attention"
    const val TC_NEEDS_ATTENTION_DESC                  = "tc.needs_attention_desc"
    const val TC_NEW_CHAPTER                           = "tc.new_chapter"
    const val TC_NEW_DUE_DATE                          = "tc.new_due_date"
    const val TC_NEW_HOMEWORK                          = "tc.new_homework"
    const val TC_NEW_LESSON_PLAN                       = "tc.new_lesson_plan"
    const val TC_NEW_PASSWORD                          = "tc.new_password"
    const val TC_NEW_PLAN                              = "tc.new_plan"
    const val TC_NEW_TEST                              = "tc.new_test"
    const val TC_NEW_TOPIC                             = "tc.new_topic"
    const val TC_NEXT                                  = "tc.next"
    const val TC_NEXT_CLASS                            = "tc.next_class"
    const val TC_LATER                                 = "tc.later"
    const val TC_NOTHING_PENDING                       = "tc.nothing_pending"
    const val TC_NOT_ENOUGH_DATA                       = "tc.not_enough_data"
    const val TC_NOT_MARKED                            = "tc.not_marked"
    const val TC_NOT_SUBMITTED                         = "tc.not_submitted"
    const val TC_NOT_YOUR_STUDENT                      = "tc.not_your_student"
    const val TC_NOT_YOUR_STUDENT_DESC                 = "tc.not_your_student_desc"
    const val TC_NOW                                   = "tc.now"
    const val TC_NOW_TEACHING                          = "tc.now_teaching"
    const val TC_QUICK_ACTIONS                         = "tc.quick_actions"
    const val TC_PENDING_ACTIONS                       = "tc.pending_actions"
    const val TC_UPCOMING_EVENTS                       = "tc.upcoming_events"
    const val TC_YOUR_DAY                              = "tc.your_day"
    const val TC_LETS                                  = "tc.lets"
    const val TC_UPDATE_ACCENT                         = "tc.update_accent"
    const val TC_UPDATE_BLURB_ATTENDANCE               = "tc.update_blurb_attendance"
    const val TC_UPDATE_BLURB_MARKS                    = "tc.update_blurb_marks"
    const val TC_UPDATE_BLURB_HOMEWORK                 = "tc.update_blurb_homework"
    const val TC_UPDATE_BLURB_SYLLABUS                 = "tc.update_blurb_syllabus"
    const val TC_UPDATE_BLURB_LESSON                   = "tc.update_blurb_lesson"
    const val TC_YOUR                                  = "tc.your"
    const val TC_WEEK_ACCENT                           = "tc.week_accent"
    const val TC_SCHEDULE_TAB                          = "tc.schedule_tab"
    const val TC_REQUESTS_TAB                          = "tc.requests_tab"
    const val TC_ACCOUNT_ACCENT                        = "tc.account_accent"
    const val TC_CLASSES_ACCENT                        = "tc.classes_accent"
    const val TC_AT_RISK                               = "tc.at_risk"
    const val TC_SEC_TIME_OFF                          = "tc.sec_time_off"
    const val TC_SEC_SECURITY                          = "tc.sec_security"
    const val TC_SEC_PREFERENCES                       = "tc.sec_preferences"
    const val TC_APPEARANCE                            = "tc.appearance"
    const val TC_STAT_SUBJECTS                         = "tc.stat_subjects"
    const val TC_STAT_CLASSES                          = "tc.stat_classes"
    const val TC_VIEW_PROFILE_DETAILS                  = "tc.view_profile_details"
    const val TC_NO_PERIOD_RIGHT_NOW                   = "tc.no_period_right_now"
    const val TC_NO_ACTIVE_HOMEWORK                    = "tc.no_active_homework"
    const val TC_NO_ACTIVE_HOMEWORK_CLASS              = "tc.no_active_homework_class"
    const val TC_NO_ALLOCATIONS                        = "tc.no_allocations"
    const val TC_ASSIGNMENTS_WILL_APPEAR               = "tc.assignments_will_appear"
    const val TC_NO_ASSIGNMENTS_FOUND                  = "tc.no_assignments_found"
    const val TC_NO_ATTEMPTS_YET                       = "tc.no_attempts_yet"
    const val TC_NO_ATTENDANCE_DATA                    = "tc.no_attendance_data"
    const val TC_NO_CHANGE                             = "tc.no_change"
    const val TC_NO_CHANGE_REQUESTS                    = "tc.no_change_requests"
    const val TC_NO_CLASSES_MATCH                      = "tc.no_classes_match"
    const val TC_NO_CLASSES_SCHEDULED_TODAY            = "tc.no_classes_scheduled_today"
    const val TC_NO_CLASSES_TODAY                      = "tc.no_classes_today"
    const val TC_NO_DATE                               = "tc.no_date"
    const val TC_NO_DRAFTS_FOUND                       = "tc.no_drafts_found"
    const val TC_NO_HEALTH_ALERTS                      = "tc.no_health_alerts"
    const val TC_NO_HEALTH_ALERTS_DESC                 = "tc.no_health_alerts_desc"
    const val TC_NO_HOMEWORK_LINKED                    = "tc.no_homework_linked"
    const val TC_NO_LEAVE_REQUESTS                     = "tc.no_leave_requests"
    const val TC_NO_LESSON_PLANS_YET                   = "tc.no_lesson_plans_yet"
    const val TC_NO_NCERT_REFERENCE_FOUND              = "tc.no_ncert_reference_found"
    const val TC_NO_PERIODS_FOR_DAY                    = "tc.no_periods_for_day"
    const val TC_NO_PERIODS_TODAY                      = "tc.no_periods_today"
    const val TC_NO_PLANS_THIS_MONTH                   = "tc.no_plans_this_month"
    const val TC_NO_STUDENTS_ENROLLED                  = "tc.no_students_enrolled"
    const val TC_NO_STUDENTS_NEED_ATTENTION            = "tc.no_students_need_attention"
    const val TC_NO_SYLLABUS_UNITS                     = "tc.no_syllabus_units"
    const val TC_NO_TEMPLATES_YET                      = "tc.no_templates_yet"
    const val TC_NO_TESTS_YET                          = "tc.no_tests_yet"
    const val TC_NO_UNITS_YET                          = "tc.no_units_yet"
    const val TC_NO_UNIT_LINKED                        = "tc.no_unit_linked"
    const val TC_NO_UPCOMING_PERIOD                    = "tc.no_upcoming_period"
    const val TC_NUMBER_OF_QUESTIONS_N                 = "tc.number_of_questions_n"
    const val TC_N_ATTEMPTED                           = "tc.n_attempted"
    const val TC_N_AT_RISK                             = "tc.n_at_risk"
    const val TC_N_CLASSES_DONE                        = "tc.n_classes_done"
    const val TC_N_DRAFT_UNITS_PENDING_APPROVAL        = "tc.n_draft_units_pending_approval"
    const val TC_N_ENROLLED                            = "tc.n_enrolled"
    const val TC_N_HOLIDAYS                            = "tc.n_holidays"
    const val TC_N_LEFT                                = "tc.n_left"
    const val TC_N_OF_N_UNITS_COVERED                  = "tc.n_of_n_units_covered"
    const val TC_N_PERCENT_PRESENT                     = "tc.n_percent_present"
    const val TC_N_PER_WEEK                            = "tc.n_per_week"
    const val TC_N_QUESTIONS_STATUS                    = "tc.n_questions_status"
    const val TC_N_STUDENTS                            = "tc.n_students"
    const val TC_N_STUDENTS_ABSENT                     = "tc.n_students_absent"
    const val TC_N_SUBTOPICS                           = "tc.n_subtopics"
    const val TC_N_TOPICS_SELECTED                     = "tc.n_topics_selected"
    const val TC_N_TURNED_IN                           = "tc.n_turned_in"
    const val TC_OBJECTIVES                            = "tc.objectives"
    const val TC_OPTIONS_ONE_PER_LINE                  = "tc.options_one_per_line"
    const val TC_OPTIONS_PH                            = "tc.options_ph"
    const val TC_P                                     = "tc.p"
    const val TC_PACE_EXPECTED_ACTUAL                  = "tc.pace_expected_actual"
    const val TC_PACE_UPDATE                           = "tc.pace_update"
    const val TC_PARENT_CONTACT                        = "tc.parent_contact"
    const val TC_PARENT_GUARDIAN                       = "tc.parent_guardian"
    const val TC_PARENT_MESSAGE                        = "tc.parent_message"
    const val TC_NOTIFY_PARENTS                        = "tc.notify_parents"
    const val TC_NOTIFY_PARENTS_ABOUT_ABSENCE            = "tc.notify_parents_about_absence"
    const val TC_PARSE_SYLLABUS                        = "tc.parse_syllabus"
    const val TC_PARSE_WITH_AI                         = "tc.parse_with_ai"
    const val TC_PASSWORD                              = "tc.password"
    const val TC_PASS_OPTIONAL                         = "tc.pass_optional"
    const val TC_PASTE_SYLLABUS_HINT                   = "tc.paste_syllabus_hint"
    const val TC_PASTE_SYLLABUS_PH                     = "tc.paste_syllabus_ph"
    const val TC_PASTE_SYLLABUS_TEXT                   = "tc.paste_syllabus_text"
    const val TC_PAST_DUE                              = "tc.past_due"
    const val TC_PENDING                               = "tc.pending"
    const val TC_PENDING_COUNT                         = "tc.pending_count"
    const val TC_PERCENT_PRESENT_OVERALL               = "tc.percent_present_overall"
    const val TC_PERFORMANCE                           = "tc.performance"
    const val TC_PICK_ALLOCATION_DESC                  = "tc.pick_allocation_desc"
    const val TC_PICK_CLASS                            = "tc.pick_class"
    const val TC_PICK_CLASS_FOR                        = "tc.pick_class_for"
    const val TC_PICK_DATE_FOR_LESSON                  = "tc.pick_date_for_lesson"
    const val TC_PICK_TEST_DATE                        = "tc.pick_test_date"
    const val TC_PLAN                                  = "tc.plan"
    const val TC_PLANNED                               = "tc.planned"
    const val TC_PLANNED_DATE                          = "tc.planned_date"
    const val TC_PREVIEW_N_UNITS_FOUND                 = "tc.preview_n_units_found"
    const val TC_PROFILE                               = "tc.profile"
    const val TC_PTM_EVENTS                            = "tc.ptm_events"
    const val TC_PTM_EVENTS_DESC                       = "tc.ptm_events_desc"
    const val TC_PUBLISH                               = "tc.publish"
    const val TC_PUBLISHED                             = "tc.published"
    const val TC_PUBLISHED_PARENTS_NOTIFIED            = "tc.published_parents_notified"
    const val TC_PUBLISH_DESC                          = "tc.publish_desc"
    const val TC_PUBLISH_NAME_Q                        = "tc.publish_name_q"
    const val TC_PUBLISH_NOTIFY_PARENTS                = "tc.publish_notify_parents"
    const val TC_PUBLISH_QUIZ                          = "tc.publish_quiz"
    const val TC_QUESTION_TEXT                         = "tc.question_text"
    const val TC_QUESTION_TYPES                        = "tc.question_types"
    const val TC_QUIZ                                  = "tc.quiz"
    const val TC_NO_QUIZZES_CREATED_YET                = "tc.no_quizzes_created_yet"
    const val TC_LESSON_COMPLETED                      = "tc.lesson_completed"
    const val TC_CREATE_QUIZ_TO_ASSESS                 = "tc.create_quiz_to_assess"
    const val TC_NOT_NOW                               = "tc.not_now"
    const val TC_CREATE_QUIZ                           = "tc.create_quiz"
    const val TC_QUIZZES                               = "tc.quizzes"
    const val TC_QUIZ_LEADERBOARD                      = "tc.quiz_leaderboard"
    const val TC_QUIZ_PREVIEW                          = "tc.quiz_preview"
    const val TC_READY_TO_MARK                         = "tc.ready_to_mark"
    const val TC_REASON                                = "tc.reason"
    const val TC_REASON_COLON                          = "tc.reason_colon"
    const val TC_REASON_OPTIONAL                       = "tc.reason_optional"
    const val TC_RECENT                                = "tc.recent"
    const val TC_RECENT_ABSENCES                       = "tc.recent_absences"
    const val TC_REGENERATE                            = "tc.regenerate"
    const val TC_REGENERATE_ALL                        = "tc.regenerate_all"
    const val TC_REJECT_ALL                            = "tc.reject_all"
    const val TC_REMINDED                              = "tc.reminded"
    const val TC_REMOVE                                = "tc.remove"
    const val TC_REPORT_CARD_REVIEW                    = "tc.report_card_review"
    const val TC_REPORT_CARD_REVIEW_DESC               = "tc.report_card_review_desc"
    const val TC_REQUEST_NEW_PERIOD                    = "tc.request_new_period"
    const val TC_REQUEST_PERIOD_DELETION               = "tc.request_period_deletion"
    const val TC_REQUEST_PERIOD_UPDATE                 = "tc.request_period_update"
    const val TC_RESOURCES                             = "tc.resources"
    const val TC_RESULTS                               = "tc.results"
    const val TC_RISK_HIGH                             = "tc.risk_high"
    const val TC_RISK_MEDIUM                           = "tc.risk_medium"
    const val TC_RISK_WATCH                            = "tc.risk_watch"
    const val TC_ROLL_LABEL                            = "tc.roll_label"
    const val TC_ROLL_N                                = "tc.roll_n"
    const val TC_ROLL_NO                               = "tc.roll_no"
    const val TC_ROLL_ON_LEAVE                         = "tc.roll_on_leave"
    const val TC_ROOM                                  = "tc.room"
    const val TC_ROOM_HINT                             = "tc.room_hint"
    const val TC_ROOM_N                                = "tc.room_n"
    const val TC_SAVED                                 = "tc.saved"
    const val TC_SAVED_NOT_PUBLISHED                   = "tc.saved_not_published"
    const val TC_SAVED_SUCCESSFULLY                    = "tc.saved_successfully"
    const val TC_SAVE_AND_BACK                         = "tc.save_and_back"
    const val TC_SAVE_AS_TEMPLATE                      = "tc.save_as_template"
    const val TC_SAVE_ATTENDANCE                       = "tc.save_attendance"
    const val TC_SAVE_CHANGES                          = "tc.save_changes"
    const val TC_SAVE_DRAFT_BTN                        = "tc.save_draft_btn"
    const val TC_SAVE_LESSON_AS_TEMPLATE               = "tc.save_lesson_as_template"
    const val TC_SAVE_LOG                              = "tc.save_log"
    const val TC_SAVE_MARKS                            = "tc.save_marks"
    const val TC_SAVING                                = "tc.saving"
    const val TC_SCHEDULED                             = "tc.scheduled"
    const val TC_SCHEDULED_MESSAGES                    = "tc.scheduled_messages"
    const val TC_SCHEDULED_MESSAGES_DESC               = "tc.scheduled_messages_desc"
    const val TC_SCHEDULED_TESTS                       = "tc.scheduled_tests"
    const val TC_SCORE                                 = "tc.score"
    const val TC_SEARCH_CLASS                          = "tc.search_class"
    const val TC_SEARCH_CLASSES                        = "tc.search_classes"
    const val TC_SELECT_TOPICS_COVERED_TODAY           = "tc.select_topics_covered_today"
    const val TC_SELECT_UNITS                          = "tc.select_units"
    const val TC_SEND_TO_PARENT                        = "tc.send_to_parent"
    const val TC_SENT_TO_ADMIN_FOR_APPROVAL            = "tc.sent_to_admin_for_approval"
    const val TC_SHARED                                = "tc.shared"
    const val TC_SHARE_WITH_TEACHERS                   = "tc.share_with_teachers"
    const val TC_SIGNED_IN_AS                          = "tc.signed_in_as"
    const val TC_SKIP                                  = "tc.skip"
    const val TC_SKIPPED                               = "tc.skipped"
    const val TC_SLA_DAYS                              = "tc.sla_days"
    const val TC_START                                 = "tc.start"
    const val TC_STATUS_COLON                          = "tc.status_colon"
    const val TC_STEADY                                = "tc.steady"
    const val TC_STUDENT                               = "tc.student"
    const val TC_STUDENTS                              = "tc.students"
    const val TC_STUDENTS_COUNT                        = "tc.students_count"
    const val TC_STUDENT_COLON                         = "tc.student_colon"
    const val TC_SUBJECTS                              = "tc.subjects"
    const val TC_SUBJECT_ONLY                          = "tc.subject_only"
    const val TC_SUBMISSIONS                           = "tc.submissions"
    const val TC_SUBMITTED                             = "tc.submitted"
    const val TC_SUBMIT_REQUEST                        = "tc.submit_request"
    const val TC_SUB_COLON                             = "tc.sub_colon"
    const val TC_SWIPE_BACK_TO_CURRENT                 = "tc.swipe_back_to_current"
    const val TC_SWIPE_BACK_TO_SUMMARY                 = "tc.swipe_back_to_summary"
    const val TC_SWIPE_FULL_SCHEDULE                   = "tc.swipe_full_schedule"
    const val TC_SWIPE_SEE_CLASSES                     = "tc.swipe_see_classes"
    const val TC_SYLLABUS                              = "tc.syllabus"
    const val TC_TAP_TO_CHECK_IN                       = "tc.tap_to_check_in"
    const val TC_TAP_TO_SWITCH                         = "tc.tap_to_switch"
    const val TC_TEMPLATES                             = "tc.templates"
    const val TC_TEMPLATE_TITLE                        = "tc.template_title"
    const val TC_TESTS_AND_MARKS                       = "tc.tests_and_marks"
    const val TC_TEST_NAME                             = "tc.test_name"
    const val TC_TEST_NAME_PH                          = "tc.test_name_ph"
    const val TC_THINGS_NEED_ATTENTION                 = "tc.things_need_attention"
    const val TC_THIS_MONTH                            = "tc.this_month"
    const val TC_THIS_WEEK                             = "tc.this_week"
    const val TC_TITLE                                 = "tc.title"
    const val TC_TITLE_PH                              = "tc.title_ph"
    const val TC_TO                                    = "tc.to"
    const val TC_TO_PUBLISH                            = "tc.to_publish"
    const val TC_TODAY                                 = "tc.today"
    const val TC_TODAYS_SCHEDULE                       = "tc.todays_schedule"
    const val TC_TOGGLE_VISIBILITY                     = "tc.toggle_visibility"
    const val TC_TOPIC_TITLE                           = "tc.topic_title"
    const val TC_TOTAL                                 = "tc.total"
    const val TC_TRANSPORT_ATTENDANCE                  = "tc.transport_attendance"
    const val TC_TRANSPORT_ATTENDANCE_DESC             = "tc.transport_attendance_desc"
    const val TC_TRUE                                  = "tc.true"
    const val TC_TRUE_FALSE                            = "tc.true_false"
    const val TC_TRY_AGAIN                             = "tc.try_again"
    const val TC_TRY_DIFFERENT_SEARCH                  = "tc.try_different_search"
    const val TC_TYPE                                  = "tc.type"
    const val TC_UNDER_INTERVENTION                    = "tc.under_intervention"
    const val TC_UPDATE_ATTENDANCE                     = "tc.update_attendance"
    const val TC_UPDATE_PASSWORD                       = "tc.update_password"
    const val TC_URGENCY_COLON                         = "tc.urgency_colon"
    const val TC_USE_TEMPLATE                          = "tc.use_template"
    const val TC_WEEKLY_SCHEDULE_APPEAR                = "tc.weekly_schedule_appear"
    const val TC_WEEKLY_TIMETABLE                      = "tc.weekly_timetable"
    const val TC_WHAT_NEEDS_YOU                        = "tc.what_needs_you"
    const val TC_WHAT_TAUGHT_TODAY_OPTIONAL            = "tc.what_taught_today_optional"
    const val TC_WHICH_CLASS                           = "tc.which_class"
    const val TC_WHY_APPLYING                          = "tc.why_applying"
    const val TC_WHY_CHANGE_NEEDED                     = "tc.why_change_needed"
    const val TC_WHY_EXTENSION                         = "tc.why_extension"

    // ═══════════════════════════════════════════════════════════════
    // Phase 5-8 — Notifications, Tutor, Discovery, Calendar
    // ═══════════════════════════════════════════════════════════════

    // Notifications screen (additional)
    const val NOTIF_INBOX              = "notif.inbox"
    const val NOTIF_UNREAD_LABEL       = "notif.unread_label"
    const val NOTIF_FILTER_UNREAD      = "notif.filter_unread"
    const val NOTIF_ALL_CAUGHT_UP      = "notif.all_caught_up"
    const val NOTIF_NO_UNREAD          = "notif.no_unread"
    const val NOTIF_NONE_YET           = "notif.none_yet"
    const val NOTIF_PREFERENCES        = "notif.preferences"
    const val NOTIF_MARK_ALL           = "notif.mark_all"

    // Teacher Heatmap
    const val TH_TITLE                 = "th.title"
    const val TH_NO_ASSIGNMENTS        = "th.no_assignments"
    const val TH_NO_ASSIGNMENTS_DESC   = "th.no_assignments_desc"
    const val TH_NO_DATA               = "th.no_data"
    const val TH_NO_DATA_DESC          = "th.no_data_desc"
    const val TH_SELECTED              = "th.selected"
    const val TH_CHILDREN              = "th.children"
    const val TH_MISCONCEPTIONS        = "th.misconceptions"
    const val TH_TOPICS                = "th.topics"
    const val TH_CHILDREN_AFFECTED     = "th.children_affected"
    const val TH_EVIDENCE              = "th.evidence"

    // Tutor — Chat
    const val TUT_AI_TUTOR             = "tut.ai_tutor"
    const val TUT_CLEAR                = "tut.clear"
    const val TUT_ERROR                = "tut.error"
    const val TUT_ASK_QUESTION         = "tut.ask_question"
    const val TUT_ASK_QUESTION_DESC    = "tut.ask_question_desc"
    const val TUT_TYPE_DOUBT           = "tut.type_doubt"
    const val TUT_ASK                  = "tut.ask"
    const val TUT_PRACTICE_READY       = "tut.practice_ready"
    const val TUT_LOADING_SUBJECTS     = "tut.loading_subjects"
    const val TUT_GENERAL              = "tut.general"

    // Tutor — Practice
    const val TUT_PRACTICE             = "tut.practice"
    const val TUT_GRADING              = "tut.grading"
    const val TUT_NO_QUESTION          = "tut.no_question"
    const val TUT_NO_QUESTION_DESC     = "tut.no_question_desc"
    const val TUT_TYPE_ANSWER          = "tut.type_answer"
    const val TUT_SUBMIT_ANSWER        = "tut.submit_answer"
    const val TUT_CORRECT              = "tut.correct"
    const val TUT_NOT_QUITE            = "tut.not_quite"
    const val TUT_SCORE_PCT            = "tut.score_pct"
    const val TUT_FEEDBACK             = "tut.feedback"
    const val TUT_NEXT_QUESTION        = "tut.next_question"

    // Tutor — Parent Progress
    const val TUT_PROGRESS_TITLE       = "tut.progress_title"
    const val TUT_NO_PROGRESS          = "tut.no_progress"
    const val TUT_NO_PROGRESS_DESC     = "tut.no_progress_desc"
    const val TUT_DOUBTS_RESOLVED      = "tut.doubts_resolved"
    const val TUT_ANSWERS_GIVEN        = "tut.answers_given"
    const val TUT_SESSIONS             = "tut.sessions"
    const val TUT_SAFETY_FLAGS         = "tut.safety_flags"
    const val TUT_SAFETY_NOTIFIED      = "tut.safety_notified"
    const val TUT_TOPIC_MASTERY        = "tut.topic_mastery"
    const val TUT_TOPIC_LABEL          = "tut.topic_label"
    const val TUT_ATTEMPTS             = "tut.attempts"
    const val TUT_CORRECT_COUNT        = "tut.correct_count"
    const val TUT_SOURCE_LABEL         = "tut.source_label"

    // SRI Preview
    const val SRI_ABOVE_MEDIAN         = "sri.above_median"
    const val SRI_YOY                  = "sri.yoy"
    const val SRI_ACADEMIC_OUTCOMES    = "sri.academic_outcomes"
    const val SRI_TEACHER_RETENTION    = "sri.teacher_retention"
    const val SRI_PARENT_SENTIMENT     = "sri.parent_sentiment"
    const val SRI_SAFETY_INFRA         = "sri.safety_infra"
    const val SRI_CO_CURRICULAR        = "sri.co_curricular"
    const val SRI_ATTENDANCE_NORMS     = "sri.attendance_norms"

    // Academic Calendar screen (additional)
    const val CAL_ACADEMIC_TITLE       = "cal.academic_title"
    const val CAL_NOT_AVAILABLE        = "cal.not_available"
    const val CAL_SIGN_IN_PROMPT       = "cal.sign_in_prompt"
    const val CAL_PREV                 = "cal.prev"
    const val CAL_NEXT_BTN             = "cal.next_btn"
    const val CAL_WORKING_DAYS         = "cal.working_days"
    const val CAL_UPCOMING_EVENTS      = "cal.upcoming_events"
    const val CAL_NO_EVENTS            = "cal.no_events"

    // Discovery screen
    const val DISC_DISCOVER            = "disc.discover"
    const val DISC_FIND_SCHOOL         = "disc.find_school"
    const val DISC_EXIT                = "disc.exit"
    const val DISC_SEARCH_PH           = "disc.search_ph"
    const val DISC_NO_SCHOOLS          = "disc.no_schools"
    const val DISC_NO_MATCHES          = "disc.no_matches"
    const val DISC_SCHOOLS_APPEAR      = "disc.schools_appear"
    const val DISC_TRY_ANOTHER         = "disc.try_another"
    const val DISC_SCHOOLS_SELECTED    = "disc.schools_selected"
    const val DISC_SCHOOL_SELECTED     = "disc.school_selected"
    const val DISC_COMPARE_NOW         = "disc.compare_now"
    const val DISC_SRI_SCORE           = "disc.sri_score"
    const val DISC_IN_COMPARE          = "disc.in_compare"
    const val DISC_COMPARE             = "disc.compare"
    const val DISC_ENQUIRE             = "disc.enquire"
    const val DISC_MEDIUM_LABEL        = "disc.medium_label"
    const val DISC_ALREADY_LINKED      = "disc.already_linked"
    const val DISC_ALREADY_LINKED_DESC = "disc.already_linked_desc"
    const val DISC_LINK_CHILD          = "disc.link_child"
    const val DISC_SCHOOL_PROFILE      = "disc.school_profile"
    const val DISC_SHARE               = "disc.share"
    const val DISC_SAVE_SCHOOL         = "disc.save_school"
    const val DISC_ENQUIRE_NOW         = "disc.enquire_now"
    const val DISC_ABOUT               = "disc.about"
    const val DISC_ACADEMICS           = "disc.academics"
    const val DISC_FEE_STRUCTURE       = "disc.fee_structure"
    const val DISC_SRI_BREAKDOWN       = "disc.sri_breakdown"
    const val DISC_PARENT_REVIEWS      = "disc.parent_reviews"
    const val DISC_LOCATION            = "disc.location"
    const val DISC_PROFILE_COMING      = "disc.profile_coming"
    const val DISC_PROFILE_DESC        = "disc.profile_desc"
    const val DISC_BOARD               = "disc.board"
    const val DISC_MEDIUM              = "disc.medium"
    const val DISC_CO_ED               = "disc.co_ed"
    const val DISC_CLASSES_OFFERED     = "disc.classes_offered"
    const val DISC_TEACHER_RATIO       = "disc.teacher_ratio"
    const val DISC_COMING_SOON         = "disc.coming_soon"
    const val DISC_FEE_COMING          = "disc.fee_coming"
    const val DISC_FEE_DESC            = "disc.fee_desc"
    const val DISC_SRI_TITLE           = "disc.sri_title"
    const val DISC_SRI_DESC            = "disc.sri_desc"
    const val DISC_REVIEWS_TITLE       = "disc.reviews_title"
    const val DISC_REVIEWS_DESC        = "disc.reviews_desc"
    const val DISC_ON_MAP              = "disc.on_map"
    const val DISC_MAP_DESC            = "disc.map_desc"
    const val DISC_SEND_ENQUIRY        = "disc.send_enquiry"
    const val DISC_ENQUIRY_RESPONSE    = "disc.enquiry_response"
    const val DISC_YOUR_NAME           = "disc.your_name"
    const val DISC_CHILD_NAME          = "disc.child_name"
    const val DISC_CURRENT_CLASS       = "disc.current_class"
    const val DISC_APPLY_CLASS         = "disc.apply_class"
    const val DISC_MESSAGE_OPT         = "disc.message_opt"
    const val DISC_ANY_QUESTION        = "disc.any_question"
    const val DISC_SUBMIT_ENQUIRY      = "disc.submit_enquiry"
    const val DISC_SENT                = "disc.sent"
    const val DISC_COMPARE_SCHOOLS     = "disc.compare_schools"
    const val DISC_CITY                = "disc.city"
    const val DISC_FEE_RANGE           = "disc.fee_range"
    const val DISC_DISTANCE            = "disc.distance"
    const val DISC_BOARD_RESULT        = "disc.board_result"
    const val DISC_FEE_NOTE            = "disc.fee_note"
    const val DISC_ENQUIRE_ALL         = "disc.enquire_all"
    const val DISC_ENQUIRIES_SENT      = "disc.enquiries_sent"
    const val DISC_CO_ED_YES           = "disc.co_ed_yes"
    const val DISC_GIRLS_ONLY          = "disc.girls_only"
    const val DISC_BOYS_ONLY           = "disc.boys_only"
    const val DISC_WITHIN_3KM          = "disc.within_3km"
    const val DISC_CBSE                = "disc.cbse"
    const val DISC_TYPE                = "disc.type"
    const val DISC_SRI_RATING          = "disc.sri_rating"

    // PEWS Student Detail
    const val PEWS_STUDENT_SIGNAL      = "pews.student_signal"
    const val PEWS_NO_SIGNAL           = "pews.no_signal"
    const val PEWS_NO_SIGNAL_DESC      = "pews.no_signal_desc"
    const val PEWS_INTERVENTIONS       = "pews.interventions"
    const val PEWS_HIGH_RISK           = "pews.high_risk"
    const val PEWS_MEDIUM_RISK         = "pews.medium_risk"
    const val PEWS_WATCH               = "pews.watch"
    const val PEWS_UNDER_INTERVENTION  = "pews.under_intervention"
    const val PEWS_RISK_SCORE          = "pews.risk_score"
    const val PEWS_ATTENDANCE          = "pews.attendance"
    const val PEWS_MARKS               = "pews.marks"
    const val PEWS_LEAVES              = "pews.leaves"
    const val PEWS_FALLING             = "pews.falling"
    const val PEWS_RISING              = "pews.rising"
    const val PEWS_WHY_STUDENT         = "pews.why_student"
    const val PEWS_AI_EXPLANATION      = "pews.ai_explanation"
    const val PEWS_LIKELY_CAUSE        = "pews.likely_cause"
    const val PEWS_SUGGESTED_ACTION    = "pews.suggested_action"
    const val PEWS_GENERATED_BY        = "pews.generated_by"
    const val PEWS_ESCALATED           = "pews.escalated"
    const val PEWS_REMINDED            = "pews.reminded"
    const val PEWS_SLA_DAYS            = "pews.sla_days"
    const val PEWS_SLA_FOLLOWUP        = "pews.sla_followup"
    const val PEWS_PLAN                = "pews.plan"
    const val PEWS_PARENT_MESSAGE      = "pews.parent_message"
    const val PEWS_OPENED              = "pews.opened"
    const val PEWS_START               = "pews.start"
    const val PEWS_DISMISS             = "pews.dismiss"
    const val PEWS_ADMIN               = "pews.admin"
    const val PEWS_TEACHER             = "pews.teacher"
    const val PEWS_INITIATED_BY        = "pews.initiated_by"
    const val PEWS_SEND_TO_PARENT      = "pews.send_to_parent"
    const val PEWS_DRAFT_PARENT_MSG    = "pews.draft_parent_msg"
    const val PEWS_MARK_IMPROVED       = "pews.mark_improved"
    const val PEWS_NO_CHANGE           = "pews.no_change"
    const val PEWS_OUTCOME             = "pews.outcome"
    const val PEWS_HISTORY             = "pews.history"

    // Health Records
    const val HLTH_TITLE               = "hlth.title"
    const val HLTH_TAB_PROFILE         = "hlth.tab_profile"
    const val HLTH_TAB_IMMUNIZATIONS   = "hlth.tab_immunizations"
    const val HLTH_TAB_INCIDENTS       = "hlth.tab_incidents"
    const val HLTH_BASIC_INFO          = "hlth.basic_info"
    const val HLTH_BLOOD_GROUP         = "hlth.blood_group"
    const val HLTH_HEIGHT              = "hlth.height"
    const val HLTH_WEIGHT              = "hlth.weight"
    const val HLTH_MEDICAL_INFO        = "hlth.medical_info"
    const val HLTH_ALLERGIES           = "hlth.allergies"
    const val HLTH_CHRONIC_CONDITIONS  = "hlth.chronic_conditions"
    const val HLTH_MEDICATIONS         = "hlth.medications"
    const val HLTH_EMERGENCY_CONTACT   = "hlth.emergency_contact"
    const val HLTH_CONTACT_NAME        = "hlth.contact_name"
    const val HLTH_CONTACT_PHONE       = "hlth.contact_phone"
    const val HLTH_DOCTOR_INFO         = "hlth.doctor_info"
    const val HLTH_DOCTOR_NAME         = "hlth.doctor_name"
    const val HLTH_DOCTOR_PHONE        = "hlth.doctor_phone"
    const val HLTH_SAVE_PROFILE        = "hlth.save_profile"
    const val HLTH_IMMUNIZATION_RECORDS= "hlth.immunization_records"
    const val HLTH_ADD                 = "hlth.add"
    const val HLTH_VACCINE_NAME        = "hlth.vaccine_name"
    const val HLTH_DOSE_NUMBER         = "hlth.dose_number"
    const val HLTH_DATE_ADMINISTERED   = "hlth.date_administered"
    const val HLTH_NEXT_DUE            = "hlth.next_due"
    const val HLTH_ADMINISTERED_BY     = "hlth.administered_by"
    const val HLTH_SAVE_RECORD         = "hlth.save_record"
    const val HLTH_NO_IMMUNIZATIONS    = "hlth.no_immunizations"
    const val HLTH_DOSE                = "hlth.dose"
    const val HLTH_BY                  = "hlth.by"
    const val HLTH_NEXT_DUE_LABEL      = "hlth.next_due_label"
    const val HLTH_HEALTH_INCIDENTS    = "hlth.health_incidents"
    const val HLTH_LOG                 = "hlth.log"
    const val HLTH_DATE                = "hlth.date"
    const val HLTH_TIME                = "hlth.time"
    const val HLTH_DESCRIPTION         = "hlth.description"
    const val HLTH_TREATMENT           = "hlth.treatment"
    const val HLTH_MEDICATION_GIVEN    = "hlth.medication_given"
    const val HLTH_SEVERITY            = "hlth.severity"
    const val HLTH_LOG_INCIDENT        = "hlth.log_incident"
    const val HLTH_NO_INCIDENTS        = "hlth.no_incidents"
    const val HLTH_TREATMENT_LABEL     = "hlth.treatment_label"
    const val HLTH_MEDICATION_LABEL    = "hlth.medication_label"
    const val HLTH_TIME_LABEL          = "hlth.time_label"
    const val HLTH_PARENT_NOTIFIED     = "hlth.parent_notified"
    const val HLTH_MARK_NOTIFIED       = "hlth.mark_notified"
    const val HLTH_SEVERITY_MAJOR      = "hlth.severity_major"
    const val HLTH_SEVERITY_MODERATE   = "hlth.severity_moderate"
    const val HLTH_SEVERITY_MINOR      = "hlth.severity_minor"

    // ID Card Templates
    const val IDCARD_TOTAL_CARDS       = "idcard.total_cards"
    const val IDCARD_STUDENTS          = "idcard.students"
    const val IDCARD_TEACHERS          = "idcard.teachers"
    const val IDCARD_STAFF             = "idcard.staff"
    const val IDCARD_MILESTONE_MASTER  = "idcard.milestone_master"
    const val IDCARD_MILESTONE_CENTURY = "idcard.milestone_century"
    const val IDCARD_MILESTONE_HALF    = "idcard.milestone_half_century"
    const val IDCARD_MILESTONE_FIRST   = "idcard.milestone_first_steps"
    const val IDCARD_MILESTONE_START   = "idcard.milestone_getting_started"
    const val IDCARD_NO_TEMPLATES      = "idcard.no_templates"
    const val IDCARD_NO_TEMPLATES_DESC = "idcard.no_templates_desc"
    const val IDCARD_CREATE_NEW        = "idcard.create_new"
    const val IDCARD_TEMPLATE_NAME     = "idcard.template_name"
    const val IDCARD_CARD_TYPE         = "idcard.card_type"
    const val IDCARD_STUDENT           = "idcard.student"
    const val IDCARD_TEACHER_ROLE      = "idcard.teacher_role"
    const val IDCARD_STAFF_ROLE        = "idcard.staff_role"
    const val IDCARD_FIELDS_DISPLAY    = "idcard.fields_display"
    const val IDCARD_FIELD_NAME        = "idcard.field_name"
    const val IDCARD_FIELD_ROLE        = "idcard.field_role"
    const val IDCARD_FIELD_CLASS       = "idcard.field_class"
    const val IDCARD_FIELD_SCHOOL      = "idcard.field_school"
    const val IDCARD_FIELD_PHOTO       = "idcard.field_photo"
    const val IDCARD_FIELD_QR          = "idcard.field_qr"
    const val IDCARD_FIELD_EMERGENCY   = "idcard.field_emergency"
    const val IDCARD_FIELD_BLOOD       = "idcard.field_blood"
    const val IDCARD_ACCENT_COLOR      = "idcard.accent_color"
    const val IDCARD_LIVE_PREVIEW      = "idcard.live_preview"
    const val IDCARD_PREVIEW           = "idcard.preview"
    const val IDCARD_CREATING          = "idcard.creating"
    const val IDCARD_CREATE_BTN        = "idcard.create_btn"
    const val IDCARD_ID_CARD           = "idcard.id_card"
    const val IDCARD_SCAN_QR           = "idcard.scan_qr"
    const val IDCARD_ACTIVE            = "idcard.active"
    const val IDCARD_INACTIVE          = "idcard.inactive"
    const val IDCARD_DEACTIVATE        = "idcard.deactivate"

    // Branding Settings
    const val BRAND_TITLE              = "brand.title"
    const val BRAND_RESET_TITLE        = "brand.reset_title"
    const val BRAND_RESET_MSG          = "brand.reset_msg"
    const val BRAND_RESET_BTN          = "brand.reset_btn"
    const val BRAND_CUSTOMIZED         = "brand.customized"
    const val BRAND_DEFAULT            = "brand.default"
    const val BRAND_COLORS             = "brand.colors"
    const val BRAND_PRIMARY_COLOR      = "brand.primary_color"
    const val BRAND_SECONDARY_COLOR    = "brand.secondary_color"
    const val BRAND_ACCENT_COLOR       = "brand.accent_color"
    const val BRAND_SAVE_COLORS        = "brand.save_colors"
    const val BRAND_ASSETS             = "brand.assets"
    const val BRAND_ASSETS_DESC        = "brand.assets_desc"
    const val BRAND_LOGO               = "brand.logo"
    const val BRAND_DARK_LOGO          = "brand.dark_logo"
    const val BRAND_FAVICON            = "brand.favicon"
    const val BRAND_APP_ICON           = "brand.app_icon"
    const val BRAND_SPLASH             = "brand.splash"
    const val BRAND_LOGIN_BG           = "brand.login_bg"
    const val BRAND_SUBDOMAIN          = "brand.subdomain"
    const val BRAND_SUBDOMAIN_DESC     = "brand.subdomain_desc"
    const val BRAND_CURRENT_SUBDOMAIN  = "brand.current_subdomain"
    const val BRAND_REMOVE             = "brand.remove"
    const val BRAND_SUBDOMAIN_LABEL    = "brand.subdomain_label"
    const val BRAND_SUBDOMAIN_PLACE    = "brand.subdomain_placeholder"
    const val BRAND_SUBDOMAIN_HINT     = "brand.subdomain_hint"
    const val BRAND_CHECK              = "brand.check"
    const val BRAND_ASSIGN             = "brand.assign"
    const val BRAND_SUBDOMAIN_AVAIL    = "brand.subdomain_available"
    const val BRAND_SUBDOMAIN_TAKEN    = "brand.subdomain_taken"
    const val BRAND_RESET_DEFAULTS     = "brand.reset_defaults"
    const val BRAND_LIVE_PREVIEW       = "brand.live_preview"
    const val BRAND_YOUR_SCHOOL        = "brand.your_school"
    const val BRAND_PRIMARY_BTN        = "brand.primary_btn"
    const val BRAND_SECONDARY_BTN      = "brand.secondary_btn"
    const val BRAND_SWATCH_PRIMARY     = "brand.swatch_primary"
    const val BRAND_SWATCH_SECONDARY   = "brand.swatch_secondary"
    const val BRAND_SWATCH_ACCENT      = "brand.swatch_accent"
    const val BRAND_HEX_COLOR          = "brand.hex_color"
    const val BRAND_UPLOADED           = "brand.uploaded"
    const val BRAND_NOT_SET            = "brand.not_set"
    const val BRAND_REPLACE            = "brand.replace"
    const val BRAND_UPLOAD             = "brand.upload"

    // Transport Management
    const val TRANS_TITLE              = "trans.title"
    const val TRANS_ROUTES             = "trans.routes"
    const val TRANS_ADD_ROUTE          = "trans.add_route"
    const val TRANS_VEHICLES           = "trans.vehicles"
    const val TRANS_ADD_VEHICLE        = "trans.add_vehicle"
    const val TRANS_ASSIGNMENTS        = "trans.assignments"
    const val TRANS_ASSIGN             = "trans.assign"
    const val TRANS_NEW_ROUTE          = "trans.new_route"
    const val TRANS_ROUTE_NAME         = "trans.route_name"
    const val TRANS_ROUTE_PLACE        = "trans.route_placeholder"
    const val TRANS_DESC_OPTIONAL      = "trans.desc_optional"
    const val TRANS_DESC_PLACE         = "trans.desc_placeholder"
    const val TRANS_CREATE_ROUTE       = "trans.create_route"
    const val TRANS_NEW_VEHICLE        = "trans.new_vehicle"
    const val TRANS_BUS_NUMBER         = "trans.bus_number"
    const val TRANS_BUS_PLACE          = "trans.bus_placeholder"
    const val TRANS_CAPACITY           = "trans.capacity"
    const val TRANS_DRIVER_NAME        = "trans.driver_name"
    const val TRANS_DRIVER_PHONE       = "trans.driver_phone"
    const val TRANS_ASSIGN_ROUTE       = "trans.assign_route"
    const val TRANS_CREATE_VEHICLE     = "trans.create_vehicle"
    const val TRANS_ASSIGN_STUDENT     = "trans.assign_student"
    const val TRANS_STUDENT_ID         = "trans.student_id"
    const val TRANS_STUDENT_ID_PLACE   = "trans.student_id_placeholder"
    const val TRANS_SELECT_ROUTE       = "trans.select_route"
    const val TRANS_SELECT_STOP        = "trans.select_stop"
    const val TRANS_SELECT_VEHICLE     = "trans.select_vehicle"
    const val TRANS_FEE_AMOUNT         = "trans.fee_amount"
    const val TRANS_FEE_PLACE          = "trans.fee_placeholder"
    const val TRANS_FEE_DUE_DATE       = "trans.fee_due_date"
    const val TRANS_ASSIGN_BTN         = "trans.assign_btn"
    const val TRANS_STOPS              = "trans.stops"
    const val TRANS_ACTIVE             = "trans.active"
    const val TRANS_INACTIVE           = "trans.inactive"
    const val TRANS_CAPACITY_LABEL     = "trans.capacity_label"
    const val TRANS_DRIVER_LABEL       = "trans.driver_label"
    const val TRANS_ROUTE_LABEL        = "trans.route_label"
    const val TRANS_STOP_LABEL         = "trans.stop_label"
    const val TRANS_BUS_LABEL          = "trans.bus_label"
    const val TRANS_DEACTIVATE         = "trans.deactivate"

    // Academic Calendar Platform
    const val ACALP_TITLE              = "acalp.title"
    const val ACALP_CREATE             = "acalp.create"
    const val ACALP_EMPTY_TITLE        = "acalp.empty_title"
    const val ACALP_EMPTY_BODY         = "acalp.empty_body"
    const val ACALP_HIGHLIGHTS         = "acalp.highlights"
    const val ACALP_VIEW               = "acalp.view"
    const val ACALP_UPCOMING           = "acalp.upcoming"
    const val ACALP_DRAFT_EVENTS       = "acalp.draft_events"
    const val ACALP_PUBLISHED_EVENTS   = "acalp.published_events"
    const val ACALP_MILESTONES         = "acalp.milestones"
    const val ACALP_ANALYTICS          = "acalp.analytics"
    const val ACALP_ACADEMIC_YEAR      = "acalp.academic_year"
    const val ACALP_ACADEMIC_CAL       = "acalp.academic_calendar"
    const val ACALP_CENTRALIZED        = "acalp.centralized"
    const val ACALP_EVENTS             = "acalp.events"
    const val ACALP_SCHOOL_DAYS        = "acalp.school_days"
    const val ACALP_HOLIDAYS           = "acalp.holidays"
    const val ACALP_NEXT_EVENT         = "acalp.next_event"
    const val ACALP_NO_EVENTS          = "acalp.no_events"
    const val ACALP_NOTHING_UPCOMING   = "acalp.nothing_upcoming"
    const val ACALP_CONFLICT           = "acalp.conflict"

    // Class Detail
    const val CD_STUDENTS              = "cd.students"
    const val CD_TEACHERS              = "cd.teachers"
    const val CD_TIMETABLE             = "cd.timetable"
    const val CD_ANALYTICS             = "cd.analytics"
    const val CD_NO_STUDENTS           = "cd.no_students"
    const val CD_NO_STUDENTS_BODY      = "cd.no_students_body"
    const val CD_STUDENTS_COUNT        = "cd.students_count"
    const val CD_SEC                   = "cd.sec"
    const val CD_ROLL                  = "cd.roll"
    const val CD_ATTENDANCE            = "cd.attendance"
    const val CD_LOADING_TIMETABLE     = "cd.loading_timetable"
    const val CD_NO_TEACHERS           = "cd.no_teachers"
    const val CD_NO_TEACHERS_BODY      = "cd.no_teachers_body"
    const val CD_TEACHERS_COUNT        = "cd.teachers_count"
    const val CD_NO_TIMETABLE          = "cd.no_timetable"
    const val CD_NO_TIMETABLE_BODY     = "cd.no_timetable_body"
    const val CD_WEEKLY_TIMETABLE      = "cd.weekly_timetable"
    const val CD_NO_PERIODS            = "cd.no_periods"
    const val CD_NO_PERIODS_BODY       = "cd.no_periods_body"
    const val CD_ROOM                  = "cd.room"
    const val CD_NO_ANALYTICS          = "cd.no_analytics"
    const val CD_NO_ANALYTICS_BODY     = "cd.no_analytics_body"
    const val CD_AVG_PROFICIENCY       = "cd.avg_proficiency"
    const val CD_ACTIVE_STUDENTS       = "cd.active_students"
    const val CD_MEDIAN_GRADE          = "cd.median_grade"
    const val CD_GRADE_DIST            = "cd.grade_distribution"
    const val CD_SUBJECT_MATRIX        = "cd.subject_matrix"
    const val CD_EARLY_WARNING         = "cd.early_warning"
    const val CD_CRITICAL              = "cd.critical"
    const val CD_MODERATE              = "cd.moderate"
    const val CD_ON_TARGET             = "cd.on_target"
    const val CD_TOP_PERFORMER         = "cd.top_performer"
    const val CD_PROGRESS_MONITORING   = "cd.progress_monitoring"
    const val CD_TREND_UP              = "cd.trend_up"
    const val CD_TREND_DOWN            = "cd.trend_down"
    const val CD_TREND_FLAT            = "cd.trend_flat"
    const val CD_MATH                  = "cd.math"
    const val CD_SCI                   = "cd.sci"
    const val CD_LIT                   = "cd.lit"
    const val CD_ATTENDANCE_LABEL      = "cd.attendance_label"

    // Scholarship Management
    const val SCH_MGMT_TITLE           = "sch.mgmt_title"
    const val SCH_CREATE_NEW           = "sch.create_new"
    const val SCH_NO_SCHEMES           = "sch.no_schemes"
    const val SCH_NO_SCHEMES_BODY      = "sch.no_schemes_body"
    const val SCH_NO_APPLICATIONS      = "sch.no_applications"
    const val SCH_NO_APPLICATIONS_BODY = "sch.no_applications_body"
    const val SCH_NO_RENEWALS          = "sch.no_renewals"
    const val SCH_NO_RENEWALS_BODY     = "sch.no_renewals_body"
    const val SCH_APPLICATIONS         = "sch.applications"
    const val SCH_RENEWALS             = "sch.renewals"
    const val SCH_SCHEMES              = "sch.schemes"
    const val SCH_TAB_APPLICATIONS     = "sch.tab_applications"
    const val SCH_TAB_RENEWALS         = "sch.tab_renewals"
    const val SCH_RENEWABLE            = "sch.renewable"
    const val SCH_AWARD                = "sch.award"
    const val SCH_ELIGIBILITY          = "sch.eligibility"
    const val SCH_EDIT                 = "sch.edit"
    const val SCH_REVIEW               = "sch.review"
    const val SCH_REMARKS              = "sch.remarks"
    const val SCH_DISBURSEMENT_AMT     = "sch.disbursement_amount"
    const val SCH_APPROVE              = "sch.approve"
    const val SCH_REJECT               = "sch.reject"
    const val SCH_DISBURSEMENT_REF     = "sch.disbursement_ref"
    const val SCH_RECORD_DISBURSEMENT  = "sch.record_disbursement"
    const val SCH_DISBURSED            = "sch.disbursed"
    const val SCH_REF                  = "sch.ref"
    const val SCH_RENEWAL_FOR          = "sch.renewal_for"
    const val SCH_DOCUMENTS            = "sch.documents"
    const val SCH_DELETE_TITLE         = "sch.delete_title"
    const val SCH_DELETE_MSG           = "sch.delete_msg"
    const val SCH_EDIT_SCHEME          = "sch.edit_scheme"
    const val SCH_CREATE_SCHEME        = "sch.create_scheme"
    const val SCH_TITLE_LABEL          = "sch.title_label"
    const val SCH_DESCRIPTION          = "sch.description"
    const val SCH_DISPLAY_AMOUNT       = "sch.display_amount"
    const val SCH_NUMERIC_AMOUNT       = "sch.numeric_amount"
    const val SCH_TYPE                 = "sch.type"
    const val SCH_FIXED                = "sch.fixed"
    const val SCH_FULL_WAIVER          = "sch.full_waiver"
    const val SCH_PARTIAL_WAIVER       = "sch.partial_waiver"
    const val SCH_WAIVER_PCT           = "sch.waiver_percentage"
    const val SCH_ELIGIBILITY_CRIT     = "sch.eligibility_criteria"
    const val SCH_CATEGORY             = "sch.category"
    const val SCH_MGMT_START_DATE      = "sch.mgmt_start_date"
    const val SCH_MGMT_END_DATE        = "sch.mgmt_end_date"
    const val SCH_RENEWABLE_LABEL      = "sch.renewable_label"
    const val SCH_RENEWAL_PERIOD       = "sch.renewal_period"
    const val SCH_UPDATE               = "sch.update"

    // Library UIX Components
    const val LIB_UIX_OVERDUE            = "lib_uix.overdue"
    const val LIB_UIX_DUE_TODAY          = "lib_uix.due_today"
    const val LIB_UIX_DUE_LEFT           = "lib_uix.due_left"
    const val LIB_UIX_FINE_AMOUNT        = "lib_uix.fine_amount"
    const val LIB_UIX_FINE_CAP           = "lib_uix.fine_cap"
    const val LIB_UIX_NO_CAP             = "lib_uix.no_cap"
    const val LIB_UIX_FINE_CAPPED        = "lib_uix.fine_capped"
    const val LIB_UIX_FINE_NO_CAP        = "lib_uix.fine_no_cap"
    const val LIB_UIX_COVER_FOR          = "lib_uix.cover_for"
    const val LIB_UIX_AVAILABILITY       = "lib_uix.availability"
    const val LIB_UIX_FILTERS            = "lib_uix.filters"
    const val LIB_UIX_CATEGORY           = "lib_uix.category"
    const val LIB_UIX_ALL                = "lib_uix.all"
    const val LIB_UIX_AVAIL_LABEL        = "lib_uix.avail_label"
    const val LIB_UIX_AVAILABLE_ONLY     = "lib_uix.available_only"
    const val LIB_UIX_SORT_BY            = "lib_uix.sort_by"
    const val LIB_UIX_SORT_NEWEST        = "lib_uix.sort_newest"
    const val LIB_UIX_SORT_TITLE_AZ      = "lib_uix.sort_title_az"
    const val LIB_UIX_SORT_AUTHOR        = "lib_uix.sort_author"
    const val LIB_UIX_SORT_POPULAR       = "lib_uix.sort_popular"
    const val LIB_UIX_CLEAR              = "lib_uix.clear"
    const val LIB_UIX_APPLY_FILTERS      = "lib_uix.apply_filters"
    const val LIB_UIX_ISSUES_COUNT       = "lib_uix.issues_count"
    const val LIB_UIX_AVAILABLE_SOON     = "lib_uix.available_soon"
    const val LIB_UIX_AVAILABLE_IN       = "lib_uix.available_in"
    const val LIB_UIX_AHEAD              = "lib_uix.ahead"
    const val LIB_UIX_MONTHLY            = "lib_uix.monthly"
    const val LIB_UIX_CATEGORIES         = "lib_uix.categories"
    const val LIB_UIX_NO_DATA            = "lib_uix.no_data"
    const val LIB_UIX_QUICK_ACTIONS      = "lib_uix.quick_actions"
    const val LIB_UIX_SELECT_BOOK        = "lib_uix.select_book"
    const val LIB_UIX_RENEW              = "lib_uix.renew"
    const val LIB_UIX_MAX                = "lib_uix.max"
    const val LIB_UIX_RETURN             = "lib_uix.return"
    const val LIB_UIX_RECENTLY_VIEWED    = "lib_uix.recently_viewed"
    const val LIB_UIX_GOOD_MORNING       = "lib_uix.good_morning"
    const val LIB_UIX_GOOD_AFTERNOON     = "lib_uix.good_afternoon"
    const val LIB_UIX_GOOD_EVENING       = "lib_uix.good_evening"
    const val LIB_UIX_OVERDUE_BOOKS      = "lib_uix.overdue_books"
    const val LIB_UIX_DUE_TOMORROW       = "lib_uix.due_tomorrow"
    const val LIB_UIX_READY_FOR_PICKUP   = "lib_uix.ready_for_pickup"
    const val LIB_UIX_READY_TO_EXPLORE   = "lib_uix.ready_to_explore"
    const val LIB_UIX_READ_LESS          = "lib_uix.read_less"
    const val LIB_UIX_READ_MORE          = "lib_uix.read_more"
    const val LIB_UIX_READING_TIME       = "lib_uix.reading_time"
    const val LIB_UIX_SCAN_TO_VIEW       = "lib_uix.scan_to_view"
    const val LIB_UIX_GOT_IT             = "lib_uix.got_it"
    const val LIB_UIX_AVAILABLE          = "lib_uix.available"
    const val LIB_UIX_AZ                 = "lib_uix.az"
    const val LIB_UIX_LESS_FILTERS       = "lib_uix.less_filters"
    const val LIB_UIX_MORE_FILTERS       = "lib_uix.more_filters"
    const val LIB_UIX_READING_STREAK     = "lib_uix.reading_streak"
    const val LIB_UIX_CURRENT_STREAK     = "lib_uix.current_streak"
    const val LIB_UIX_LONGEST_STREAK     = "lib_uix.longest_streak"
    const val LIB_UIX_DONT_BREAK_CHAIN   = "lib_uix.dont_break_chain"
    const val LIB_UIX_GRID               = "lib_uix.grid"
    const val LIB_UIX_LIST               = "lib_uix.list"
    const val LIB_UIX_SHELF              = "lib_uix.shelf"
    const val LIB_UIX_BOOK_OF_MONTH      = "lib_uix.book_of_month"
    const val LIB_UIX_BOOK_OF_WEEK       = "lib_uix.book_of_week"
    const val LIB_UIX_SEARCH_PLACEHOLDER = "lib_uix.search_placeholder"
    const val LIB_UIX_QUICK_ISSUE        = "lib_uix.quick_issue"
    const val LIB_UIX_STEP               = "lib_uix.step"
    const val LIB_UIX_CONFIRM_BOOK       = "lib_uix.confirm_book"
    const val LIB_UIX_NO_BOOK_SELECTED   = "lib_uix.no_book_selected"
    const val LIB_UIX_BORROWER_DETAILS   = "lib_uix.borrower_details"
    const val LIB_UIX_BORROWER_NAME      = "lib_uix.borrower_name"
    const val LIB_UIX_ENTER_NAME         = "lib_uix.enter_name"
    const val LIB_UIX_REVIEW_CONFIRM     = "lib_uix.review_confirm"
    const val LIB_UIX_BOOK_LABEL         = "lib_uix.book_label"
    const val LIB_UIX_AUTHOR_LABEL       = "lib_uix.author_label"
    const val LIB_UIX_UNKNOWN            = "lib_uix.unknown"
    const val LIB_UIX_BORROWER_LABEL     = "lib_uix.borrower_label"
    const val LIB_UIX_DUE_DATE_14        = "lib_uix.due_date_14"
    const val LIB_UIX_ISSUE_BOOK         = "lib_uix.issue_book"

    // ── StudentLibraryScreen ───────────────────────────────────────────
    const val STU_LIB_TAB_BROWSE         = "stu_lib.tab_browse"
    const val STU_LIB_TAB_MY_BOOKS       = "stu_lib.tab_my_books"
    const val STU_LIB_TAB_HISTORY        = "stu_lib.tab_history"
    const val STU_LIB_TAB_WISHLIST       = "stu_lib.tab_wishlist"
    const val STU_LIB_TAB_RESERVATIONS   = "stu_lib.tab_reservations"
    const val STU_LIB_TAB_REQUESTS       = "stu_lib.tab_requests"
    const val STU_LIB_TAB_PROFILE        = "stu_lib.tab_profile"
    const val STU_LIB_TAB_BADGES         = "stu_lib.tab_badges"
    const val STU_LIB_TAB_DISCUSSIONS    = "stu_lib.tab_discussions"
    const val STU_LIB_TITLE              = "stu_lib.title"
    const val STU_LIB_OFFLINE_CACHED     = "stu_lib.offline_cached"
    const val STU_LIB_OFFLINE_CHECK      = "stu_lib.offline_check"
    const val STU_LIB_COACHMARK_TITLE    = "stu_lib.coachmark_title"
    const val STU_LIB_COACHMARK_MSG      = "stu_lib.coachmark_msg"
    const val STU_LIB_READER             = "stu_lib.reader"
    const val STU_LIB_SEARCH_BOOKS       = "stu_lib.search_books"
    const val STU_LIB_SEARCH             = "stu_lib.search"
    const val STU_LIB_TRENDING_NOW       = "stu_lib.trending_now"
    const val STU_LIB_ISSUES_COUNT       = "stu_lib.issues_count"
    const val STU_LIB_RECOMMENDED        = "stu_lib.recommended"
    const val STU_LIB_WHY                = "stu_lib.why"
    const val STU_LIB_NO_BOOKS_FOUND     = "stu_lib.no_books_found"
    const val STU_LIB_TRY_DIFFERENT      = "stu_lib.try_different"
    const val STU_LIB_BOOKS_COUNT        = "stu_lib.books_count"
    const val STU_LIB_LOAD_MORE          = "stu_lib.load_more"
    const val STU_LIB_RESERVE            = "stu_lib.reserve"
    const val STU_LIB_ADD_WISHLIST       = "stu_lib.add_wishlist"
    const val STU_LIB_MY_PROFILE         = "stu_lib.my_profile"
    const val STU_LIB_BOOKS_READ         = "stu_lib.books_read"
    const val STU_LIB_CURRENTLY_ISSUED   = "stu_lib.currently_issued"
    const val STU_LIB_OVERDUE            = "stu_lib.overdue"
    const val STU_LIB_OUTSTANDING_FINE   = "stu_lib.outstanding_fine"
    const val STU_LIB_CURRENT_STREAK     = "stu_lib.current_streak"
    const val STU_LIB_LONGEST_STREAK     = "stu_lib.longest_streak"
    const val STU_LIB_STREAK_DAYS        = "stu_lib.streak_days"
    const val STU_LIB_FINE_AMOUNT        = "stu_lib.fine_amount"
    const val STU_LIB_READING_GOAL       = "stu_lib.reading_goal"
    const val STU_LIB_GOAL_ACHIEVED      = "stu_lib.goal_achieved"
    const val STU_LIB_SET_READING_GOAL   = "stu_lib.set_reading_goal"
    const val STU_LIB_GOAL_COUNT         = "stu_lib.goal_count"
    const val STU_LIB_PERIOD             = "stu_lib.period"
    const val STU_LIB_MONTHLY            = "stu_lib.monthly"
    const val STU_LIB_QUARTERLY          = "stu_lib.quarterly"
    const val STU_LIB_YEARLY             = "stu_lib.yearly"
    const val STU_LIB_TARGET_YEAR        = "stu_lib.target_year"
    const val STU_LIB_SET_GOAL           = "stu_lib.set_goal"
    const val STU_LIB_NO_BADGES          = "stu_lib.no_badges"
    const val STU_LIB_READ_MORE_BADGES   = "stu_lib.read_more_badges"
    const val STU_LIB_EARNED_ON          = "stu_lib.earned_on"
    const val STU_LIB_EARNED             = "stu_lib.earned"
    const val STU_LIB_LOCKED             = "stu_lib.locked"
    const val STU_LIB_NO_BOOKS_ISSUED    = "stu_lib.no_books_issued"
    const val STU_LIB_BROWSE_TO_ISSUE    = "stu_lib.browse_to_issue"
    const val STU_LIB_RENEWALS           = "stu_lib.renewals"
    const val STU_LIB_RENEW              = "stu_lib.renew"
    const val STU_LIB_READING_HISTORY    = "stu_lib.reading_history"
    const val STU_LIB_NO_HISTORY         = "stu_lib.no_history"
    const val STU_LIB_HISTORY_APPEAR     = "stu_lib.history_appear"
    const val STU_LIB_MY_WISHLIST        = "stu_lib.my_wishlist"
    const val STU_LIB_WISHLIST_EMPTY     = "stu_lib.wishlist_empty"
    const val STU_LIB_WISHLIST_EMPTY_BODY = "stu_lib.wishlist_empty_body"
    const val STU_LIB_REMOVE             = "stu_lib.remove"
    const val STU_LIB_MY_RESERVATIONS    = "stu_lib.my_reservations"
    const val STU_LIB_NO_RESERVATIONS    = "stu_lib.no_reservations"
    const val STU_LIB_RESERVE_FROM_BROWSE = "stu_lib.reserve_from_browse"
    const val STU_LIB_RESERVED_ON        = "stu_lib.reserved_on"
    const val STU_LIB_CANCEL_RESERVATION_TITLE = "stu_lib.cancel_reservation_title"
    const val STU_LIB_CANCEL_RESERVATION_MSG = "stu_lib.cancel_reservation_msg"
    const val STU_LIB_CANCEL_RESERVATION_BTN = "stu_lib.cancel_reservation_btn"
    const val STU_LIB_KEEP               = "stu_lib.keep"
    const val STU_LIB_ACQUISITION_REQUESTS = "stu_lib.acquisition_requests"
    const val STU_LIB_NO_REQUESTS        = "stu_lib.no_requests"
    const val STU_LIB_REQUESTS_APPEAR    = "stu_lib.requests_appear"
    const val STU_LIB_AUTHOR_LABEL       = "stu_lib.author_label"
    const val STU_LIB_ISBN_LABEL         = "stu_lib.isbn_label"
    const val STU_LIB_REASON_LABEL       = "stu_lib.reason_label"
    const val STU_LIB_BOOK_DISCUSSIONS   = "stu_lib.book_discussions"
    const val STU_LIB_BOOK_ID            = "stu_lib.book_id"
    const val STU_LIB_LOAD_DISCUSSIONS   = "stu_lib.load_discussions"
    const val STU_LIB_NO_DISCUSSIONS     = "stu_lib.no_discussions"
    const val STU_LIB_ENTER_BOOK_ID      = "stu_lib.enter_book_id"
    const val STU_LIB_WRITE_MESSAGE      = "stu_lib.write_message"
    const val STU_LIB_POST               = "stu_lib.post"

    // ── SchoolPeopleScreenV2 ────────────────────────────────────────────
    const val PPL_TITLE                  = "ppl.title"
    const val PPL_LINK_REQUESTS_TITLE    = "ppl.link_requests_title"
    const val PPL_LINK_REQUESTS_SUB      = "ppl.link_requests_sub"
    const val PPL_TAB_TEACHERS           = "ppl.tab_teachers"
    const val PPL_TAB_STUDENTS           = "ppl.tab_students"
    const val PPL_TAB_STAFF              = "ppl.tab_staff"
    const val PPL_TAB_ALUMNI             = "ppl.tab_alumni"
    const val PPL_ALUMNI_MGMT_TITLE      = "ppl.alumni_mgmt_title"
    const val PPL_ALUMNI_MGMT_SUB        = "ppl.alumni_mgmt_sub"
    const val PPL_ADD_TEACHER            = "ppl.add_teacher"
    const val PPL_SEARCH_TEACHERS        = "ppl.search_teachers"
    const val PPL_NO_TEACHERS            = "ppl.no_teachers"
    const val PPL_NO_MATCHES             = "ppl.no_matches"
    const val PPL_NO_TEACHERS_BODY       = "ppl.no_teachers_body"
    const val PPL_NO_TEACHER_MATCHES     = "ppl.no_teacher_matches"
    const val PPL_LOADING                = "ppl.loading"
    const val PPL_LOAD_MORE              = "ppl.load_more"
    const val PPL_UNNAMED_TEACHER        = "ppl.unnamed_teacher"
    const val PPL_ACTIVE                 = "ppl.active"
    const val PPL_INACTIVE               = "ppl.inactive"
    const val PPL_GRADES                 = "ppl.grades"
    const val PPL_NO_GRADES              = "ppl.no_grades"
    const val PPL_SUBJECTS               = "ppl.subjects"
    const val PPL_NO_SUBJECTS            = "ppl.no_subjects"
    const val PPL_CLASSES                = "ppl.classes"
    const val PPL_STUDENTS_LABEL         = "ppl.students_label"
    const val PPL_ATTENDANCE_PCT         = "ppl.attendance_pct"
    const val PPL_ATTENDANCE_NONE        = "ppl.attendance_none"
    const val PPL_NEVER_ACTIVE           = "ppl.never_active"
    const val PPL_ACTIVE_DATE            = "ppl.active_date"
    const val PPL_VIEW_PROFILE           = "ppl.view_profile"
    const val PPL_MORE_ACTIONS           = "ppl.more_actions"
    const val PPL_ASSIGN_CLASSES         = "ppl.assign_classes"
    const val PPL_DEACTIVATE             = "ppl.deactivate"
    const val PPL_ADD_STUDENT            = "ppl.add_student"
    const val PPL_IMPORT_CSV             = "ppl.import_csv"
    const val PPL_GRADUATE               = "ppl.graduate"
    const val PPL_SEARCH_STUDENTS        = "ppl.search_students"
    const val PPL_NO_STUDENTS            = "ppl.no_students"
    const val PPL_NO_STUDENTS_BODY       = "ppl.no_students_body"
    const val PPL_NO_STUDENT_MATCHES     = "ppl.no_student_matches"
    const val PPL_COHORT_ANALYTICS       = "ppl.cohort_analytics"
    const val PPL_NO_COHORT_DATA         = "ppl.no_cohort_data"
    const val PPL_NO_COHORT_BODY         = "ppl.no_cohort_body"
    const val PPL_RISK_DISTRIBUTION      = "ppl.risk_distribution"
    const val PPL_CRITICAL               = "ppl.critical"
    const val PPL_MEDIUM                 = "ppl.medium"
    const val PPL_LOW                    = "ppl.low"
    const val PPL_AT_RISK_STUDENTS       = "ppl.at_risk_students"
    const val PPL_SUBJECT_ENGAGEMENT     = "ppl.subject_engagement"
    const val PPL_COHORT_COMPARISON      = "ppl.cohort_comparison"
    const val PPL_GRADE_N                = "ppl.grade_n"
    const val PPL_MARK_ALUMNI            = "ppl.mark_alumni"
    const val PPL_MARK_ALUMNI_BODY       = "ppl.mark_alumni_body"
    const val PPL_GRADUATION_YEAR        = "ppl.graduation_year"
    const val PPL_ADD_STAFF              = "ppl.add_staff"
    const val PPL_SEARCH_STAFF           = "ppl.search_staff"
    const val PPL_NO_STAFF               = "ppl.no_staff"
    const val PPL_NO_STAFF_BODY          = "ppl.no_staff_body"
    const val PPL_NO_STAFF_MATCHES       = "ppl.no_staff_matches"
    const val PPL_FULL_NAME              = "ppl.full_name"
    const val PPL_NAME_PH_TEACHER        = "ppl.name_ph_teacher"
    const val PPL_EMAIL_OR_PHONE         = "ppl.email_or_phone"
    const val PPL_EMAIL_PHONE_PH         = "ppl.email_phone_ph"
    const val PPL_INITIAL_PASSWORD       = "ppl.initial_password"
    const val PPL_PASSWORD_PH            = "ppl.password_ph"
    const val PPL_OTP_HINT               = "ppl.otp_hint"
    const val PPL_ADD_STAFF_MEMBER       = "ppl.add_staff_member"
    const val PPL_NAME_PH_STAFF          = "ppl.name_ph_staff"
    const val PPL_ROLE                   = "ppl.role"
    const val PPL_ROLE_PH                = "ppl.role_ph"
    const val PPL_DEPT_OPTIONAL          = "ppl.dept_optional"
    const val PPL_DEPT_PH                = "ppl.dept_ph"
    const val PPL_PHONE_OPTIONAL         = "ppl.phone_optional"
    const val PPL_PHONE_PH               = "ppl.phone_ph"
    const val PPL_EMAIL_OPTIONAL         = "ppl.email_optional"
    const val PPL_EMAIL_PH               = "ppl.email_ph"
    const val PPL_NAME_PH_STUDENT        = "ppl.name_ph_student"
    const val PPL_CLASS                  = "ppl.class"
    const val PPL_CLASS_PH               = "ppl.class_ph"
    const val PPL_SECTION                = "ppl.section"
    const val PPL_SECTION_PH             = "ppl.section_ph"
    const val PPL_ROLL_NUMBER            = "ppl.roll_number"
    const val PPL_ROLL_PH                = "ppl.roll_ph"
    const val PPL_PARENT_PHONE           = "ppl.parent_phone"
    const val PPL_PARENT_PHONE_PH        = "ppl.parent_phone_ph"
    const val PPL_IMPORT_STUDENTS_CSV    = "ppl.import_students_csv"
    const val PPL_IMPORT_INSTRUCTIONS    = "ppl.import_instructions"
    const val PPL_CSV_CONTENT            = "ppl.csv_content"
    const val PPL_CSV_PH                 = "ppl.csv_ph"
    const val PPL_IMPORT                 = "ppl.import"
    const val PPL_MASTERY                = "ppl.mastery"
    const val PPL_RISK_PCT               = "ppl.risk_pct"

    // ── SchoolRecordsScreenV2 ───────────────────────────────────────────
    const val REC_TITLE                  = "rec.title"
    const val REC_TAB_COVERAGE           = "rec.tab_coverage"
    const val REC_TAB_PACE               = "rec.tab_pace"
    const val REC_TAB_ATTENDANCE         = "rec.tab_attendance"
    const val REC_TAB_MARKS              = "rec.tab_marks"
    const val REC_TAB_FEE                = "rec.tab_fee"
    const val REC_TAB_DOCUMENTS          = "rec.tab_documents"
    const val REC_DOC_LIBRARY_TITLE      = "rec.doc_library_title"
    const val REC_DOC_LIBRARY_DESC       = "rec.doc_library_desc"
    const val REC_NO_COVERAGE            = "rec.no_coverage"
    const val REC_NO_COVERAGE_BODY       = "rec.no_coverage_body"
    const val REC_OVERALL_COVERAGE       = "rec.overall_coverage"
    const val REC_BY_DEPARTMENT          = "rec.by_department"
    const val REC_LAGGING_CLASSES        = "rec.lagging_classes"
    const val REC_BEHIND                 = "rec.behind"
    const val REC_MILESTONES             = "rec.milestones"
    const val REC_VERIFIED               = "rec.verified"
    const val REC_NO_ATTENDANCE          = "rec.no_attendance"
    const val REC_NO_ATTENDANCE_BODY     = "rec.no_attendance_body"
    const val REC_LATEST_REGISTER        = "rec.latest_register"
    const val REC_PRESENT_PCT            = "rec.present_pct"
    const val REC_PRESENT                = "rec.present"
    const val REC_ABSENT                 = "rec.absent"
    const val REC_LATE                   = "rec.late"
    const val REC_TOTAL                  = "rec.total"
    const val REC_BY_CLASS               = "rec.by_class"
    const val REC_NO_ASSESSMENTS         = "rec.no_assessments"
    const val REC_NO_ASSESSMENTS_BODY    = "rec.no_assessments_body"
    const val REC_OVERALL_AVG            = "rec.overall_avg"
    const val REC_ASSESSMENT_COUNT       = "rec.assessment_count"
    const val REC_PUBLISHED              = "rec.published"
    const val REC_DRAFT                  = "rec.draft"
    const val REC_AVG                    = "rec.avg"
    const val REC_GRADED                 = "rec.graded"
    const val REC_NOT_GRADED             = "rec.not_graded"
    const val REC_NO_FEES                = "rec.no_fees"
    const val REC_NO_FEES_BODY           = "rec.no_fees_body"
    const val REC_LEDGER                 = "rec.ledger"
    const val REC_PAID                   = "rec.paid"
    const val REC_DUE                    = "rec.due"
    const val REC_OVERDUE                = "rec.overdue"
    const val REC_RECENT                 = "rec.recent"
    const val REC_DUE_DATE               = "rec.due_date"
    const val REC_NO_PACE                = "rec.no_pace"
    const val REC_NO_PACE_BODY           = "rec.no_pace_body"
    const val REC_RECALCULATE            = "rec.recalculate"
    const val REC_ACTIVE_ALERTS          = "rec.active_alerts"
    const val REC_AI_RECONFIRMED         = "rec.ai_reconfirmed"
    const val REC_RESOLVE                = "rec.resolve"
    const val REC_PACE_SNAPSHOTS         = "rec.pace_snapshots"
    const val REC_TOPICS_COVERED         = "rec.topics_covered"
    const val REC_EXPECTED               = "rec.expected"

    // ── AlumniScreen ────────────────────────────────────────────────────
    const val ALM_TITLE                  = "alm.title"
    const val ALM_TAB_DIRECTORY          = "alm.tab_directory"
    const val ALM_TAB_PENDING            = "alm.tab_pending"
    const val ALM_TAB_CAMPAIGNS          = "alm.tab_campaigns"
    const val ALM_TAB_DONATIONS          = "alm.tab_donations"
    const val ALM_TAB_MENTORSHIP         = "alm.tab_mentorship"
    const val ALM_TAB_ANALYTICS          = "alm.tab_analytics"
    const val ALM_ADD_ALUMNI             = "alm.add_alumni"
    const val ALM_BULK_IMPORT            = "alm.bulk_import"
    const val ALM_NO_ALUMNI              = "alm.no_alumni"
    const val ALM_NO_ALUMNI_BODY         = "alm.no_alumni_body"
    const val ALM_FULL_NAME_REQ          = "alm.full_name_req"
    const val ALM_NAME_PH                = "alm.name_ph"
    const val ALM_GRAD_YEAR_REQ          = "alm.grad_year_req"
    const val ALM_GRAD_YEAR_PH           = "alm.grad_year_ph"
    const val ALM_STUDENT_ID_OPT         = "alm.student_id_opt"
    const val ALM_STUDENT_ID_PH          = "alm.student_id_ph"
    const val ALM_EMAIL                  = "alm.email"
    const val ALM_EMAIL_PH               = "alm.email_ph"
    const val ALM_PHONE                  = "alm.phone"
    const val ALM_PHONE_PH               = "alm.phone_ph"
    const val ALM_PROFESSION             = "alm.profession"
    const val ALM_PROFESSION_PH          = "alm.profession_ph"
    const val ALM_COMPANY                = "alm.company"
    const val ALM_COMPANY_PH             = "alm.company_ph"
    const val ALM_CITY                   = "alm.city"
    const val ALM_CITY_PH                = "alm.city_ph"
    const val ALM_ADD                    = "alm.add"
    const val ALM_BULK_IMPORT_TITLE      = "alm.bulk_import_title"
    const val ALM_BULK_IMPORT_INSTR      = "alm.bulk_import_instr"
    const val ALM_CSV_PH                 = "alm.csv_ph"
    const val ALM_ROWS_READY             = "alm.rows_ready"
    const val ALM_IMPORT                 = "alm.import"
    const val ALM_IMPORT_WITH_COUNT      = "alm.import_with_count"
    const val ALM_NO_PENDING             = "alm.no_pending"
    const val ALM_NO_PENDING_BODY        = "alm.no_pending_body"
    const val ALM_BATCH                  = "alm.batch"
    const val ALM_APPROVE                = "alm.approve"
    const val ALM_DECLINE                = "alm.decline"
    const val ALM_NO_CAMPAIGNS           = "alm.no_campaigns"
    const val ALM_NO_CAMPAIGNS_BODY      = "alm.no_campaigns_body"
    const val ALM_CAMPAIGN_PROGRESS      = "alm.campaign_progress"
    const val ALM_STATUS                 = "alm.status"
    const val ALM_NO_DONATIONS           = "alm.no_donations"
    const val ALM_NO_DONATIONS_BODY      = "alm.no_donations_body"
    const val ALM_CAMPAIGN_LABEL         = "alm.campaign_label"
    const val ALM_DATE                   = "alm.date"
    const val ALM_80G_ELIGIBLE           = "alm.80g_eligible"
    const val ALM_RECEIPT_PENDING        = "alm.receipt_pending"
    const val ALM_NO_ANALYTICS           = "alm.no_analytics"
    const val ALM_OVERVIEW               = "alm.overview"
    const val ALM_TOTAL_ALUMNI           = "alm.total_alumni"
    const val ALM_ACTIVE_90              = "alm.active_90"
    const val ALM_PENDING_VERIFICATIONS  = "alm.pending_verifications"
    const val ALM_ENGAGEMENT_RATE        = "alm.engagement_rate"
    const val ALM_TOTAL_DONATIONS        = "alm.total_donations"
    const val ALM_ACTIVE_CAMPAIGNS       = "alm.active_campaigns"
    const val ALM_ACTIVE_MENTORSHIPS     = "alm.active_mentorships"
    const val ALM_BY_GRAD_YEAR           = "alm.by_grad_year"
    const val ALM_BY_PROFESSION          = "alm.by_profession"
    const val ALM_BY_CITY                = "alm.by_city"
    const val ALM_NO_MENTORSHIPS         = "alm.no_mentorships"
    const val ALM_NO_MENTORSHIPS_BODY    = "alm.no_mentorships_body"
    const val ALM_MENTORING              = "alm.mentoring"
    const val ALM_STARTED                = "alm.started"
    const val ALM_SESSIONS               = "alm.sessions"
    const val ALM_NOTES                  = "alm.notes"
    const val ALM_MENTORSHIP_REQUESTS    = "alm.mentorship_requests"
    const val ALM_NO_MENTOR_REQUESTS     = "alm.no_mentor_requests"
    const val ALM_NO_MENTOR_REQUESTS_BODY = "alm.no_mentor_requests_body"
    const val ALM_FROM                   = "alm.from"
    const val ALM_REQUESTED_BY           = "alm.requested_by"
    const val ALM_EXPERTISE              = "alm.expertise"
    const val ALM_MESSAGE                = "alm.message"
    const val ALM_MENTOR                 = "alm.mentor"
    const val ALM_MENTOR_EXPERTISE       = "alm.mentor_expertise"

    // ── SchoolHomeScreenV2 ──────────────────────────────────────────────
    const val HOME_NOTIF_RATIONALE_TITLE   = "home.notif_rationale_title"
    const val HOME_NOTIF_RATIONALE_MSG     = "home.notif_rationale_msg"
    const val HOME_NOTIF_ENABLE            = "home.notif_enable"
    const val HOME_NOTIF_NOT_NOW           = "home.notif_not_now"
    const val HOME_WELCOME                 = "home.welcome"
    const val HOME_YOUR_SCHOOL             = "home.your_school"
    const val HOME_NOTIFICATIONS           = "home.notifications"
    const val HOME_QA_ANNOUNCEMENT         = "home.qa_announcement"
    const val HOME_QA_CREATE_EVENT         = "home.qa_create_event"
    const val HOME_QA_SEND_NOTICE          = "home.qa_send_notice"
    const val HOME_QA_REPORTS              = "home.qa_reports"
    const val HOME_QA_TRANSPORT            = "home.qa_transport"
    const val HOME_SMART_INSIGHTS          = "home.smart_insights"
    const val HOME_SCHOOL_PULSE            = "home.school_pulse"
    const val HOME_PULSE_METRICS_EMPTY     = "home.pulse_metrics_empty"
    const val HOME_PULSE_OUT_OF            = "home.pulse_out_of"
    const val HOME_KEY_METRICS             = "home.key_metrics"
    const val HOME_CAMPUS_HEALTH           = "home.campus_health"
    const val HOME_ATTENDANCE_OVER         = "home.attendance_over"
    const val HOME_NO_ATTENDANCE_DATA      = "home.no_attendance_data"
    const val HOME_ATTENDANCE_TRENDS_EMPTY = "home.attendance_trends_empty"
    const val HOME_FEE_COLLECTION          = "home.fee_collection"
    const val HOME_COLLECTION_RATE         = "home.collection_rate"
    const val HOME_COLLECTED               = "home.collected"
    const val HOME_PENDING                 = "home.pending"
    const val HOME_PARENT_ENGAGEMENT       = "home.parent_engagement"
    const val HOME_PARENT_ENGAGEMENT_SUB   = "home.parent_engagement_sub"
    const val HOME_MOST_ENGAGED            = "home.most_engaged"
    const val HOME_CLASS_LEADERBOARD       = "home.class_leaderboard"
    const val HOME_COMMUNICATION           = "home.communication"
    const val HOME_UNREAD                  = "home.unread"
    const val HOME_QUERIES                 = "home.queries"
    const val HOME_ANNOUNCEMENTS           = "home.announcements"
    const val HOME_ACKNOWLEDGEMENTS        = "home.acknowledgements"
    const val HOME_EVENTS                  = "home.events"
    const val HOME_VIEW_CALENDAR           = "home.view_calendar"
    const val HOME_RECENTLY_COMPLETED      = "home.recently_completed"
    const val HOME_TODAY                   = "home.today"
    const val HOME_TOMORROW                = "home.tomorrow"
    const val HOME_IN_DAYS                 = "home.in_days"
    const val HOME_CAL_AT_GLANCE           = "home.cal_at_glance"
    const val HOME_OPEN_CALENDAR           = "home.open_calendar"
    const val HOME_THIS_WEEK               = "home.this_week"
    const val HOME_DRAFTS                  = "home.drafts"
    const val HOME_NEXT_HOLIDAY            = "home.next_holiday"
    const val HOME_UPCOMING_EVENTS         = "home.upcoming_events"
    const val HOME_SEE_ALL                 = "home.see_all"
    const val HOME_DRAFT                   = "home.draft"
    const val HOME_CONFLICT                = "home.conflict"
    const val HOME_TEACHER_SPOTLIGHT       = "home.teacher_spotlight"
    const val HOME_SCORE                   = "home.score"
    const val HOME_STUDENT_ACHIEVEMENTS    = "home.student_achievements"
    const val HOME_CELEBRATIONS            = "home.celebrations"
    const val HOME_TODAY_LABEL             = "home.today_label"
    const val HOME_UPCOMING_LABEL          = "home.upcoming_label"
    const val HOME_TEACHER                 = "home.teacher"
    const val HOME_STUDENT                 = "home.student"
    const val HOME_BIRTHDAY_TODAY          = "home.birthday_today"
    const val HOME_BIRTHDAY_IN_DAYS        = "home.birthday_in_days"
    const val HOME_LIVE_ACTIVITY           = "home.live_activity"
    const val HOME_SCHOOL_ANALYTICS        = "home.school_analytics"
    const val HOME_ANALYTICS_DESC          = "home.analytics_desc"
    const val HOME_EXPLORE_ANALYTICS       = "home.explore_analytics"
    const val HOME_RISK_MONITOR            = "home.risk_monitor"
    const val HOME_RISK_MONITOR_DESC       = "home.risk_monitor_desc"
    const val HOME_OPEN_MONITOR            = "home.open_monitor"
    const val HOME_REPORT_PUBLISH          = "home.report_publish"
    const val HOME_REPORT_PUBLISH_DESC     = "home.report_publish_desc"
    const val HOME_OPEN_PUBLISHING         = "home.open_publishing"
    const val HOME_REPORT_EFFECTIVENESS    = "home.report_effectiveness"
    const val HOME_REPORT_EFFECTIVENESS_DESC = "home.report_effectiveness_desc"
    const val HOME_OPEN_EFFECTIVENESS      = "home.open_effectiveness"
    const val HOME_EVENT_REGISTRATION      = "home.event_registration"
    const val HOME_EVENT_REG_DESC          = "home.event_reg_desc"
    const val HOME_MANAGE                  = "home.manage"

    // ── ClassesSubjectsScreenV2 ──
    const val CS_TITLE                     = "cs.title"
    const val CS_TAB_CLASSES               = "cs.tab_classes"
    const val CS_TAB_SUBJECTS              = "cs.tab_subjects"
    const val CS_TAB_SCHEDULE              = "cs.tab_schedule"
    const val CS_TAB_EXCEPTIONS            = "cs.tab_exceptions"
    const val CS_NO_CLASSES                = "cs.no_classes"
    const val CS_NO_CLASSES_BODY           = "cs.no_classes_body"
    const val CS_CLASSES                   = "cs.classes"
    const val CS_ADD_CLASS                 = "cs.add_class"
    const val CS_EDIT_CLASS                = "cs.edit_class"
    const val CS_DELETE_CLASS              = "cs.delete_class"
    const val CS_DELETE_CLASS_MSG          = "cs.delete_class_msg"
    const val CS_NO_SECTIONS               = "cs.no_sections"
    const val CS_SUBJECTS_COUNT            = "cs.subjects_count"
    const val CS_EDIT                      = "cs.edit"
    const val CS_CLASS_CODE                = "cs.class_code"
    const val CS_CLASS_NAME                = "cs.class_name"
    const val CS_SECTIONS_LABEL            = "cs.sections_label"
    const val CS_CANCEL                    = "cs.cancel"
    const val CS_SAVE                      = "cs.save"
    const val CS_DELETE                    = "cs.delete"
    const val CS_CREATE                    = "cs.create"
    const val CS_BACK                      = "cs.back"
    const val CS_REMOVE                    = "cs.remove"
    const val CS_NO_CLASSES_AVAIL          = "cs.no_classes_avail"
    const val CS_NO_CLASSES_AVAIL_BODY     = "cs.no_classes_avail_body"
    const val CS_SUBJECTS                  = "cs.subjects"
    const val CS_CLASS_SUBJECTS            = "cs.class_subjects"
    const val CS_ADD                       = "cs.add"
    const val CS_NO_SUBJECTS               = "cs.no_subjects"
    const val CS_NO_SUBJECTS_BODY          = "cs.no_subjects_body"
    const val CS_ADD_SUBJECT               = "cs.add_subject"
    const val CS_EDIT_SUBJECT              = "cs.edit_subject"
    const val CS_DELETE_SUBJECT            = "cs.delete_subject"
    const val CS_DELETE_SUBJECT_MSG        = "cs.delete_subject_msg"
    const val CS_SUBJECT_NAME              = "cs.subject_name"
    const val CS_SUBJECT_CODE              = "cs.subject_code"
    const val CS_STEP_STRUCTURE            = "cs.step_structure"
    const val CS_STEP_ASSIGN               = "cs.step_assign"
    const val CS_STEP_REVIEW               = "cs.step_review"
    const val CS_DAY_STRUCTURE_TEMPLATE    = "cs.day_structure_template"
    const val CS_DAY_STRUCTURE_DESC        = "cs.day_structure_desc"
    const val CS_IMPORT                    = "cs.import"
    const val CS_TEMPLATE_NAME             = "cs.template_name"
    const val CS_APPLICABLE_DAYS           = "cs.applicable_days"
    const val CS_LIVE_PREVIEW              = "cs.live_preview"
    const val CS_SLOTS_COUNT               = "cs.slots_count"
    const val CS_ADD_SLOT                  = "cs.add_slot"
    const val CS_SAVE_TEMPLATE             = "cs.save_template"
    const val CS_EXISTING_CONFIGS          = "cs.existing_configs"
    const val CS_ACTIVE                    = "cs.active"
    const val CS_INACTIVE                  = "cs.inactive"
    const val CS_CONFIG_DETAILS            = "cs.config_details"
    const val CS_IMPORT_SCHEDULE           = "cs.import_schedule"
    const val CS_CHOOSE_IMPORT             = "cs.choose_import"
    const val CS_PHOTO_OCR                 = "cs.photo_ocr"
    const val CS_PHOTO_OCR_DESC            = "cs.photo_ocr_desc"
    const val CS_PDF_DOCUMENT              = "cs.pdf_document"
    const val CS_PDF_DESC                  = "cs.pdf_desc"
    const val CS_PASTE_TEXT                = "cs.paste_text"
    const val CS_PASTE_TEXT_DESC           = "cs.paste_text_desc"
    const val CS_PHOTO_OCR_LABEL           = "cs.photo_ocr_label"
    const val CS_PDF_IMPORT_LABEL          = "cs.pdf_import_label"
    const val CS_AI_READING                = "cs.ai_reading"
    const val CS_AI_VISION_DESC            = "cs.ai_vision_desc"
    const val CS_AI_VISION_OCR             = "cs.ai_vision_ocr"
    const val CS_PHOTO_OCR_BODY            = "cs.photo_ocr_body"
    const val CS_PDF_BODY                  = "cs.pdf_body"
    const val CS_PICK_PHOTO                = "cs.pick_photo"
    const val CS_PICK_PDF                  = "cs.pick_pdf"
    const val CS_USE_PASTE                 = "cs.use_paste"
    const val CS_PASTE_BELOW               = "cs.paste_below"
    const val CS_SUPPORTED_FORMATS         = "cs.supported_formats"
    const val CS_TIMETABLE_TEXT            = "cs.timetable_text"
    const val CS_PARSE_FILL                = "cs.parse_fill"
    const val CS_AI_PARSE                  = "cs.ai_parse"
    const val CS_PARSE_ERROR               = "cs.parse_error"
    const val CS_PASTE_FIRST               = "cs.paste_first"
    const val CS_PDF_NOT_AVAILABLE         = "cs.pdf_not_available"
    const val CS_SLOT_LABEL                = "cs.slot_label"
    const val CS_START                     = "cs.start"
    const val CS_END                       = "cs.end"
    const val CS_NO_DAY_STRUCTURE          = "cs.no_day_structure"
    const val CS_NO_DAY_STRUCTURE_BODY     = "cs.no_day_structure_body"
    const val CS_NO_CLASSES_FOUND          = "cs.no_classes_found"
    const val CS_SELECT_DAY                = "cs.select_day"
    const val CS_DAY_CLASS_SECTION         = "cs.day_class_section"
    const val CS_NO_PERIODS                = "cs.no_periods"
    const val CS_NO_PERIODS_BODY           = "cs.no_periods_body"
    const val CS_PERIODS_ON_DAY            = "cs.periods_on_day"
    const val CS_ADD_PERIOD                = "cs.add_period"
    const val CS_SLOTS_ASSIGNED            = "cs.slots_assigned"
    const val CS_OTHER_PERIODS             = "cs.other_periods"
    const val CS_QUICK_ACTIONS             = "cs.quick_actions"
    const val CS_COPY_DAY_TO_ALL           = "cs.copy_day_to_all"
    const val CS_COPY_FROM_CLASS           = "cs.copy_from_class"
    const val CS_REVIEW_BTN                = "cs.review_btn"
    const val CS_REMOVE_ASSIGNMENT         = "cs.remove_assignment"
    const val CS_REMOVE_ASSIGNMENT_MSG     = "cs.remove_assignment_msg"
    const val CS_SLOT_N                    = "cs.slot_n"
    const val CS_ROOM_N                    = "cs.room_n"
    const val CS_TAP_TO_ASSIGN             = "cs.tap_to_assign"
    const val CS_ASSIGNED                  = "cs.assigned"
    const val CS_EMPTY                     = "cs.empty"
    const val CS_TEACHER                    = "cs.teacher"
    const val CS_SELECT_TEACHER            = "cs.select_teacher"
    const val CS_NO_TEACHERS               = "cs.no_teachers"
    const val CS_ADD_NEW_TEACHER           = "cs.add_new_teacher"
    const val CS_SUBJECT_LABEL             = "cs.subject_label"
    const val CS_SELECT_SUBJECT            = "cs.select_subject"
    const val CS_NO_SUBJECTS_CLASS         = "cs.no_subjects_class"
    const val CS_ADD_NEW_SUBJECT           = "cs.add_new_subject"
    const val CS_ROOM                      = "cs.room"
    const val CS_UPDATE                    = "cs.update"
    const val CS_ASSIGN                    = "cs.assign"
    const val CS_COPY_DAY_CONFIRM          = "cs.copy_day_confirm"
    const val CS_COPY_DAY_MSG              = "cs.copy_day_msg"
    const val CS_COPY                      = "cs.copy"
    const val CS_COPY_FROM_CLASS_TITLE     = "cs.copy_from_class_title"
    const val CS_COPY_FROM_CLASS_DESC      = "cs.copy_from_class_desc"
    const val CS_NO_OTHER_CLASSES          = "cs.no_other_classes"
    const val CS_WEEKLY_OVERVIEW           = "cs.weekly_overview"
    const val CS_NO_TIMETABLE              = "cs.no_timetable"
    const val CS_NO_TIMETABLE_BODY         = "cs.no_timetable_body"
    const val CS_PERIODS_LABEL             = "cs.periods_label"
    const val CS_CLASSES_LABEL             = "cs.classes_label"
    const val CS_TEACHERS_LABEL            = "cs.teachers_label"
    const val CS_DAYS_LABEL                = "cs.days_label"
    const val CS_CONFLICTS_DETECTED        = "cs.conflicts_detected"
    const val CS_DONE_REVIEW               = "cs.done_review"
    const val CS_DONE                      = "cs.done"
    const val CS_NEW_TEACHER               = "cs.new_teacher"
    const val CS_FULL_NAME                 = "cs.full_name"
    const val CS_EMAIL_PHONE               = "cs.email_phone"
    const val CS_NEW_SUBJECT               = "cs.new_subject"
    const val CS_EXCEPTIONS                = "cs.exceptions"
    const val CS_PENDING                   = "cs.pending"
    const val CS_APPROVED                  = "cs.approved"
    const val CS_REJECTED                  = "cs.rejected"
    const val CS_PERIOD_EXCEPTIONS         = "cs.period_exceptions"
    const val CS_LOAD_EXCEPTIONS           = "cs.load_exceptions"
    const val CS_NO_EXCEPTIONS             = "cs.no_exceptions"
    const val CS_NO_EXCEPTIONS_BODY        = "cs.no_exceptions_body"
    const val CS_ADD_EXCEPTION             = "cs.add_exception"
    const val CS_CHANGE_REQUESTS           = "cs.change_requests"
    const val CS_LOAD                      = "cs.load"
    const val CS_NO_REQUESTS               = "cs.no_requests"
    const val CS_NO_REQUESTS_BODY          = "cs.no_requests_body"
    const val CS_DELETE_EXCEPTION          = "cs.delete_exception"
    const val CS_DELETE_EXCEPTION_MSG      = "cs.delete_exception_msg"
    const val CS_ADD_EXCEPTION_TITLE       = "cs.add_exception_title"
    const val CS_DATE                      = "cs.date"
    const val CS_KIND                      = "cs.kind"
    const val CS_NOTE                      = "cs.note"
    const val CS_DAY_LABEL                 = "cs.day_label"
    const val CS_REASON_LABEL              = "cs.reason_label"
    const val CS_ADMIN_NOTE                = "cs.admin_note"
    const val CS_APPROVE                   = "cs.approve"
    const val CS_REJECT_BTN                = "cs.reject_btn"
    const val CS_REVIEW                    = "cs.review"
    const val CS_WEEKDAY_MON               = "cs.weekday_mon"
    const val CS_WEEKDAY_TUE               = "cs.weekday_tue"
    const val CS_WEEKDAY_WED               = "cs.weekday_wed"
    const val CS_WEEKDAY_THU               = "cs.weekday_thu"
    const val CS_WEEKDAY_FRI               = "cs.weekday_fri"
    const val CS_WEEKDAY_SAT               = "cs.weekday_sat"
    const val CS_WEEKDAY_SUN               = "cs.weekday_sun"

    // ParentActivityScreenV2 (extras)
    const val PAC_ANNOUNCEMENTS      = "pac.announcements"
    const val PAC_LOAD_ERROR         = "pac.load_error"
    const val PAC_ALL_CAUGHT_UP      = "pac.all_caught_up"
    const val PAC_ALL_CAUGHT_UP_DESC = "pac.all_caught_up_desc"

    // ParentPewsScreenV2 (extras)
    const val PPEWS_ALL_GOOD         = "ppews.all_good"
    const val PPEWS_ALL_GOOD_BODY    = "ppews.all_good_body"
    const val PPEWS_ALL_ON_TRACK     = "ppews.all_on_track"
    const val PPEWS_ALL_ON_TRACK_BODY = "ppews.all_on_track_body"

    // ParentFeePaymentScreenV2
    const val PFP_PAY_FEES           = "pfp.pay_fees"
    const val PFP_OUTSTANDING        = "pfp.outstanding"
    const val PFP_OVERDUE_HEADS      = "pfp.overdue_heads"
    const val PFP_PAYMENT_METHOD     = "pfp.payment_method"
    const val PFP_ONLINE_PAYMENT     = "pfp.online_payment"
    const val PFP_SECURE_GATEWAY     = "pfp.secure_gateway"
    const val PFP_PAY_AMOUNT         = "pfp.pay_amount"
    const val PFP_NO_FEES_DUE        = "pfp.no_fees_due"

    // ParentFeeHistoryScreenV2
    const val PFH_FEE_HISTORY        = "pfh.fee_history"
    const val PFH_TOTAL_COLLECTED    = "pfh.total_collected"
    const val PFH_NO_HISTORY         = "pfh.no_history"
    const val PFH_NO_HISTORY_DESC    = "pfh.no_history_desc"

    // ParentEventRegistrationScreenV2 (extras)
    const val PE_EVENTS              = "pe.events"
    const val PE_EVENT_DETAIL        = "pe.event_detail"
    const val PE_UPCOMING            = "pe.upcoming"
    const val PE_MY_REGS             = "pe.my_regs"
    const val PE_NO_EVENTS           = "pe.no_events"
    const val PE_VENUE              = "pe.venue"
    const val PE_REGISTER_BY        = "pe.register_by"
    const val PE_REGISTERED_STATUS  = "pe.registered_status"
    const val PE_REG_OPEN           = "pe.reg_open"
    const val PE_CONFLICTS          = "pe.conflicts"
    const val PE_SELECT_SLOT        = "pe.select_slot"
    const val PE_RESCHEDULE         = "pe.reschedule"
    const val PE_NUM_ATTENDEES      = "pe.num_attendees"
    const val PE_REGISTER           = "pe.register"
    const val PE_FULL               = "pe.full"
    const val PE_YOUR_SLOT          = "pe.your_slot"
    const val PE_SELECTED           = "pe.selected"
    const val PE_NO_REGS            = "pe.no_regs"
    const val PE_SLOT_LABEL         = "pe.slot_label"
    const val PE_BOOKED             = "pe.booked"

    // ParentHomeworkScreenV2
    const val PHW_HOMEWORK           = "phw.homework"
    const val PHW_NO_ACTIVE          = "phw.no_active"
    const val PHW_NO_ACTIVE_DESC     = "phw.no_active_desc"
    const val PHW_GRADED            = "phw.graded"
    const val PHW_SUBMITTED         = "phw.submitted"
    const val PHW_LATE              = "phw.late"
    const val PHW_PENDING           = "phw.pending"
    const val PHW_TAP_TO_VIEW       = "phw.tap_to_view"
    const val PHW_INSTRUCTIONS      = "phw.instructions"
    const val PHW_WRITTEN_ANSWER    = "phw.written_answer"
    const val PHW_ANSWER_PH         = "phw.answer_ph"
    const val PHW_PHOTO_ATTACH      = "phw.photo_attach"
    const val PHW_UPLOADING         = "phw.uploading"
    const val PHW_ADD_PHOTO         = "phw.add_photo"
    const val PHW_SUBMIT_SUCCESS    = "phw.submit_success"
    const val PHW_SUBMITTING        = "phw.submitting"
    const val PHW_SUBMIT            = "phw.submit"
    const val PHW_ATTACHMENT        = "phw.attachment"

    // ── SchoolLibraryScreen (admin) ─────────────────────────────────────
    const val LIB_TAB_DASHBOARD         = "lib_tab.dashboard"
    const val LIB_TAB_BOOKS             = "lib_tab.books"
    const val LIB_TAB_COPIES            = "lib_tab.copies"
    const val LIB_TAB_ISSUES            = "lib_tab.issues"
    const val LIB_TAB_QUICK_ISSUE       = "lib_tab.quick_issue"
    const val LIB_TAB_BULK_RETURN       = "lib_tab.bulk_return"
    const val LIB_TAB_CATEGORIES        = "lib_tab.categories"
    const val LIB_TAB_AUDIT             = "lib_tab.audit"
    const val LIB_TAB_ANNOUNCEMENTS     = "lib_tab.announcements"
    const val LIB_TAB_ACQUISITION       = "lib_tab.acquisition"
    const val LIB_TAB_RESERVATIONS      = "lib_tab.reservations"
    const val LIB_TAB_HISTORY           = "lib_tab.history"
    const val LIB_TAB_MORE              = "lib_tab.more"
    const val LIB_TAB_SETTINGS          = "lib_tab.settings"
    const val LIB_TITLE                 = "lib.title"
    const val LIB_OFFLINE_CACHED        = "lib.offline_cached"
    const val LIB_OFFLINE_CHECK         = "lib.offline_check"
    const val LIB_DASHBOARD             = "lib.dashboard"
    const val LIB_WELCOME               = "lib.welcome"
    const val LIB_WELCOME_DESC          = "lib.welcome_desc"
    const val LIB_RUN_ONBOARDING        = "lib.run_onboarding"
    const val LIB_TOTAL_BOOKS           = "lib.total_books"
    const val LIB_TOTAL_COPIES          = "lib.total_copies"
    const val LIB_AVAILABLE             = "lib.available"
    const val LIB_ISSUED                = "lib.issued"
    const val LIB_OVERDUE               = "lib.overdue"
    const val LIB_LOST                  = "lib.lost"
    const val LIB_RESERVATIONS          = "lib.reservations"
    const val LIB_DAMAGED               = "lib.damaged"
    const val LIB_OUTSTANDING_FINES     = "lib.outstanding_fines"
    const val LIB_COLLECTED_MONTH       = "lib.collected_month"
    const val LIB_BOOKS                 = "lib.books"
    const val LIB_SEARCH_BOOKS          = "lib.search_books"
    const val LIB_ADD_BOOK              = "lib.add_book"
    const val LIB_CATEGORY_LABEL        = "lib.category_label"
    const val LIB_AVAILABLE_LABEL       = "lib.available_label"
    const val LIB_AVAILABLE_ONLY        = "lib.available_only"
    const val LIB_SORT_LABEL            = "lib.sort_label"
    const val LIB_SORT_NEWEST           = "lib.sort_newest"
    const val LIB_SORT_TITLE            = "lib.sort_title"
    const val LIB_SORT_AUTHOR           = "lib.sort_author"
    const val LIB_SORT_POPULAR          = "lib.sort_popular"
    const val LIB_SEARCH_BTN            = "lib.search_btn"
    const val LIB_NO_BOOKS              = "lib.no_books"
    const val LIB_NO_BOOKS_DESC         = "lib.no_books_desc"
    const val LIB_ARCHIVED              = "lib.archived"
    const val LIB_UNARCHIVE             = "lib.unarchive"
    const val LIB_ARCHIVE               = "lib.archive"
    const val LIB_SET_COVER             = "lib.set_cover"
    const val LIB_ISSUE                 = "lib.issue"
    const val LIB_SET_COVER_URL         = "lib.set_cover_url"
    const val LIB_COVER_URL             = "lib.cover_url"
    const val LIB_ADD_NEW_BOOK          = "lib.add_new_book"
    const val LIB_TITLE_LABEL           = "lib.title_label"
    const val LIB_AUTHOR_LABEL          = "lib.author_label"
    const val LIB_ISBN_LABEL            = "lib.isbn_label"
    const val LIB_PUBLISHER_LABEL       = "lib.publisher_label"
    const val LIB_TOTAL_COPIES_LABEL    = "lib.total_copies_label"
    const val LIB_SHELF_LOCATION        = "lib.shelf_location"
    const val LIB_REPLACEMENT_COST      = "lib.replacement_cost"
    const val LIB_LANGUAGE              = "lib.language"
    const val LIB_SYNOPSIS              = "lib.synopsis"
    const val LIB_CATEGORY              = "lib.category"
    const val LIB_CREATE                = "lib.create"
    const val LIB_ISSUE_BOOK            = "lib.issue_book"
    const val LIB_BORROWER_ID           = "lib.borrower_id"
    const val LIB_BORROWER_NAME         = "lib.borrower_name"
    const val LIB_COPY_ID               = "lib.copy_id"
    const val LIB_BORROWER_TYPE         = "lib.borrower_type"
    const val LIB_STUDENT               = "lib.student"
    const val LIB_TEACHER               = "lib.teacher"
    const val LIB_ISSUES                = "lib.issues"
    const val LIB_NO_ISSUES             = "lib.no_issues"
    const val LIB_NO_ISSUES_DESC        = "lib.no_issues_desc"
    const val LIB_RETURN                = "lib.return"
    const val LIB_RENEW                 = "lib.renew"
    const val LIB_MARK_LOST             = "lib.mark_lost"
    const val LIB_PAY_FINE              = "lib.pay_fine"
    const val LIB_WAIVE_FINE            = "lib.waive_fine"
    const val LIB_RETURN_BOOK           = "lib.return_book"
    const val LIB_SELECT_CONDITION      = "lib.select_condition"
    const val LIB_CONDITION_GOOD        = "lib.condition_good"
    const val LIB_CONDITION_FAIR        = "lib.condition_fair"
    const val LIB_CONDITION_DAMAGED     = "lib.condition_damaged"
    const val LIB_DAMAGE_NOTES          = "lib.damage_notes"
    const val LIB_CONFIRM_RETURN        = "lib.confirm_return"
    const val LIB_MARK_LOST_TITLE       = "lib.mark_lost_title"
    const val LIB_WAIVE_FINE_TITLE      = "lib.waive_fine_title"
    const val LIB_WAIVER_REASON         = "lib.waiver_reason"
    const val LIB_SETTINGS              = "lib.settings"
    const val LIB_LOADING_SETTINGS      = "lib.loading_settings"
    const val LIB_DEFAULT_LOAN_DAYS     = "lib.default_loan_days"
    const val LIB_FINE_PER_DAY          = "lib.fine_per_day"
    const val LIB_MAX_BOOKS             = "lib.max_books"
    const val LIB_MAX_RENEWALS          = "lib.max_renewals"
    const val LIB_RESERVATION_TIMEOUT   = "lib.reservation_timeout"
    const val LIB_DUE_REMINDER          = "lib.due_reminder"
    const val LIB_FINE_CAP              = "lib.fine_cap"
    const val LIB_QUICK_ISSUE_ENABLED   = "lib.quick_issue_enabled"
    const val LIB_BULK_RETURN_ENABLED   = "lib.bulk_return_enabled"
    const val LIB_LEADERBOARD_ENABLED   = "lib.leaderboard_enabled"
    const val LIB_SAVE_SETTINGS         = "lib.save_settings"
    const val LIB_RESET_DEFAULTS        = "lib.reset_defaults"
    const val LIB_BOOK_ID               = "lib.book_id"
    const val LIB_LOAD_RESERVATIONS     = "lib.load_reservations"
    const val LIB_NO_RESERVATIONS       = "lib.no_reservations"
    const val LIB_NO_RESERVATIONS_DESC  = "lib.no_reservations_desc"
    const val LIB_FULFILL               = "lib.fulfill"
    const val LIB_QUICK_ISSUE_TAB       = "lib.quick_issue_tab"
    const val LIB_QUICK_ISSUE_DESC      = "lib.quick_issue_desc"
    const val LIB_BARCODE               = "lib.barcode"
    const val LIB_BORROWER_ID_LABEL     = "lib.borrower_id_label"
    const val LIB_BORROWER_NAME_LABEL   = "lib.borrower_name_label"
    const val LIB_BULK_RETURN_TAB       = "lib.bulk_return_tab"
    const val LIB_BULK_RETURN_DESC      = "lib.bulk_return_desc"
    const val LIB_SCAN_BARCODE          = "lib.scan_barcode"
    const val LIB_ADD                   = "lib.add"
    const val LIB_END_SESSION           = "lib.end_session"
    const val LIB_NO_BARCODES           = "lib.no_barcodes"
    const val LIB_NO_BARCODES_DESC      = "lib.no_barcodes_desc"
    const val LIB_CONFIRM_BULK_RETURN   = "lib.confirm_bulk_return"
    const val LIB_RETURN_ALL            = "lib.return_all"
    const val LIB_CATEGORIES_TAB        = "lib.categories_tab"
    const val LIB_ADD_CATEGORY          = "lib.add_category"
    const val LIB_NO_CATEGORIES         = "lib.no_categories"
    const val LIB_NO_CATEGORIES_DESC    = "lib.no_categories_desc"
    const val LIB_NEW_CATEGORY          = "lib.new_category"
    const val LIB_NAME                  = "lib.name"
    const val LIB_COLOR                 = "lib.color"
    const val LIB_ICON_NAME             = "lib.icon_name"
    const val LIB_DELETE_CATEGORY_TITLE = "lib.delete_category_title"
    const val LIB_AUDIT_TRAIL           = "lib.audit_trail"
    const val LIB_NO_AUDIT              = "lib.no_audit"
    const val LIB_NO_AUDIT_DESC         = "lib.no_audit_desc"
    const val LIB_ANNOUNCEMENTS_TAB     = "lib.announcements_tab"
    const val LIB_NEW_ANNOUNCEMENT      = "lib.new_announcement"
    const val LIB_NO_ANNOUNCEMENTS      = "lib.no_announcements"
    const val LIB_NO_ANNOUNCEMENTS_DESC = "lib.no_announcements_desc"
    const val LIB_INACTIVE              = "lib.inactive"
    const val LIB_DEACTIVATE            = "lib.deactivate"
    const val LIB_ACTIVATE              = "lib.activate"
    const val LIB_NEW_ANNOUNCEMENT_TITLE= "lib.new_announcement_title"
    const val LIB_ANN_TITLE             = "lib.ann_title"
    const val LIB_ANN_BODY              = "lib.ann_body"
    const val LIB_POST                  = "lib.post"
    const val LIB_DELETE_ANN_TITLE      = "lib.delete_ann_title"
    const val LIB_ACQUISITION_REQUESTS  = "lib.acquisition_requests"
    const val LIB_NO_REQUESTS           = "lib.no_requests"
    const val LIB_NO_REQUESTS_DESC      = "lib.no_requests_desc"
    const val LIB_APPROVE               = "lib.approve"
    const val LIB_ORDER                 = "lib.order"
    const val LIB_RECEIVE               = "lib.receive"
    const val LIB_CONVERT_TO_BOOK       = "lib.convert_to_book"
    const val LIB_MORE_TAB              = "lib.more_tab"
    const val LIB_QUICK_ACTIONS         = "lib.quick_actions"
    const val LIB_EXPORT_CATALOG        = "lib.export_catalog"
    const val LIB_IMPORT_BOOKS          = "lib.import_books"
    const val LIB_TRENDING_BOOKS        = "lib.trending_books"
    const val LIB_REPAIR_QUEUE          = "lib.repair_queue"
    const val LIB_NO_REPAIR             = "lib.no_repair"
    const val LIB_NO_REPAIR_DESC        = "lib.no_repair_desc"
    const val LIB_MARK_REPAIRED         = "lib.mark_repaired"
    const val LIB_IMPORT_BOOKS_TITLE    = "lib.import_books_title"
    const val LIB_PASTE_JSON            = "lib.paste_json"
    const val LIB_JSON_LABEL            = "lib.json_label"
    const val LIB_IMPORT                = "lib.import"
    const val LIB_BOOK_COPIES           = "lib.book_copies"
    const val LIB_COPIES_DESC           = "lib.copies_desc"
    const val LIB_LOAD_COPIES           = "lib.load_copies"
    const val LIB_NO_COPIES             = "lib.no_copies"
    const val LIB_NO_COPIES_DESC        = "lib.no_copies_desc"
    const val LIB_ADD_COPY              = "lib.add_copy"
    const val LIB_ADD_COPY_TITLE        = "lib.add_copy_title"
    const val LIB_CONDITION_LABEL       = "lib.condition_label"
    const val LIB_CONDITION_NEW         = "lib.condition_new"
    const val LIB_CONDITION_POOR        = "lib.condition_poor"
    const val LIB_BOOK_HISTORY          = "lib.book_history"
    const val LIB_HISTORY_DESC          = "lib.history_desc"
    const val LIB_LOAD_HISTORY          = "lib.load_history"
    const val LIB_NO_HISTORY            = "lib.no_history"
    const val LIB_NO_HISTORY_DESC       = "lib.no_history_desc"
    const val LIB_MARK_LOST_MSG        = "lib.mark_lost_msg"
    const val LIB_BULK_RETURN_MSG      = "lib.bulk_return_msg"
    const val LIB_DELETE_CATEGORY_MSG  = "lib.delete_category_msg"
    const val LIB_DELETE_ANN_MSG       = "lib.delete_ann_msg"
    const val LIB_ISBN_PREFIX          = "lib.isbn_prefix"
    const val LIB_DUE_PREFIX           = "lib.due_prefix"
    const val LIB_FINE_PREFIX          = "lib.fine_prefix"
    const val LIB_WAITLIST_PREFIX      = "lib.waitlist_prefix"
    const val LIB_RESERVED_PREFIX      = "lib.reserved_prefix"
    const val LIB_BY_PREFIX            = "lib.by_prefix"
    const val LIB_EXPIRES_PREFIX       = "lib.expires_prefix"
    const val LIB_NEVER               = "lib.never"
    const val LIB_AUTHOR_PREFIX       = "lib.author_prefix"
    const val LIB_PUBLISHER_PREFIX    = "lib.publisher_prefix"
    const val LIB_REASON_PREFIX       = "lib.reason_prefix"
    const val LIB_COPY_PREFIX         = "lib.copy_prefix"
    const val LIB_BARCODE_PREFIX      = "lib.barcode_prefix"
    const val LIB_ISSUED_PREFIX       = "lib.issued_prefix"
    const val LIB_RETURNED_PREFIX     = "lib.returned_prefix"
    const val LIB_PENDING_LABEL       = "lib.pending_label"
    const val LIB_BARCODES_SCANNED    = "lib.barcodes_scanned"
    const val LIB_COPIES_COUNT        = "lib.copies_count"
    const val LIB_RECORDS_COUNT       = "lib.records_count"
    const val LIB_ISSUES_COUNT        = "lib.issues_count"

    // ── Gamification (teacher + admin) ──────────────────────────────────
    const val GAM_EARNED_BADGES         = "gam.earned_badges"
    const val GAM_SHOUTOUT_PH           = "gam.shoutout_ph"
    const val GAM_PARENT_ALERT_PH       = "gam.parent_alert_ph"
    const val GAM_CLASS_LEADERBOARD     = "gam.class_leaderboard"
    const val GAM_CLASS_GOALS           = "gam.class_goals"
    const val GAM_GOAL_TYPE_PH          = "gam.goal_type_ph"
    const val GAM_GOAL_TARGET_PH        = "gam.goal_target_ph"
    const val GAM_GOAL_REWARD_PH        = "gam.goal_reward_ph"
    const val GAM_RECENT_SHOUTOUTS      = "gam.recent_shoutouts"
    const val GAM_MENTOR_ASSIGNMENTS    = "gam.mentor_assignments"
    const val GAM_MENTOR_ID_PH          = "gam.mentor_id_ph"
    const val GAM_MENTEE_ID_PH          = "gam.mentee_id_ph"
    const val GAM_STUDY_BUDDY_PAIRS     = "gam.study_buddy_pairs"
    const val GAM_STUDY_BUDDIES         = "gam.study_buddies"
    const val GAM_REMOVE                = "gam.remove"
    const val GAM_BUDDY1_ID_PH          = "gam.buddy1_id_ph"
    const val GAM_BUDDY2_ID_PH          = "gam.buddy2_id_ph"
    const val GAM_NO_DATA               = "gam.no_data"
    const val GAM_NO_DATA_DESC          = "gam.no_data_desc"
    const val GAM_MANAGEMENT_CONSOLE    = "gam.management_console"
    const val GAM_FEATURE_FLAGS         = "gam.feature_flags"
    const val GAM_UNABLE_FLAGS          = "gam.unable_flags"
    const val GAM_GRANULAR_TOGGLES      = "gam.granular_toggles"
    const val GAM_LEADERBOARDS          = "gam.leaderboards"
    const val GAM_LEADERBOARDS_DESC     = "gam.leaderboards_desc"
    const val GAM_REWARDS_SHOP          = "gam.rewards_shop"
    const val GAM_REWARDS_SHOP_DESC     = "gam.rewards_shop_desc"
    const val GAM_HOUSE_SYSTEM          = "gam.house_system"
    const val GAM_HOUSE_SYSTEM_DESC     = "gam.house_system_desc"
    const val GAM_BOOST_TYPE            = "gam.boost_type"
    const val GAM_MULTIPLIER            = "gam.multiplier"
    const val GAM_TARGET_SCOPE          = "gam.target_scope"
    const val GAM_DURATION_HOURS        = "gam.duration_hours"
    const val GAM_CREATE_BOOST          = "gam.create_boost"
    const val GAM_MENTOR_PREFIX         = "gam.mentor_prefix"
    const val GAM_MENTEE_PREFIX         = "gam.mentee_prefix"
    const val GAM_STUDENT_PREFIX        = "gam.student_prefix"
    const val GAM_DELETE_SHOUTOUT       = "gam.delete_shoutout"
    const val GAM_STUDENT1_ID_PH        = "gam.student1_id_ph"
    const val GAM_STUDENT2_ID_PH        = "gam.student2_id_ph"
    const val GAM_GOAL_TARGET_NUM_PH    = "gam.goal_target_num_ph"
    const val GAM_TOOLS                 = "gam.tools"
    const val GAM_CLASS_GAMIFICATION    = "gam.class_gamification"
    const val GAM_ENCOURAGE             = "gam.encourage"
    const val GAM_SPOTLIGHT             = "gam.spotlight"
    const val GAM_CANCEL_SHOUTOUT       = "gam.cancel_shoutout"
    const val GAM_SEND_SHOUTOUT         = "gam.send_shoutout"
    const val GAM_CANCEL_QUEST          = "gam.cancel_quest"
    const val GAM_ASSIGN_QUEST          = "gam.assign_quest"
    const val GAM_CANCEL_BADGE          = "gam.cancel_badge"
    const val GAM_AWARD_BADGE           = "gam.award_badge"
    const val GAM_CANCEL_ALERT          = "gam.cancel_alert"
    const val GAM_PARENT_ALERT          = "gam.parent_alert"
    const val GAM_SEND_ALERT            = "gam.send_alert"
    const val GAM_CONFIRM_PEP_TALK      = "gam.confirm_pep_talk"
    const val GAM_SEND_PEP_TALK         = "gam.send_pep_talk"
    const val GAM_CREATE_CLASS_GOAL     = "gam.create_class_goal"
    const val GAM_CREATE_GOAL           = "gam.create_goal"
    const val GAM_ASSIGN_MENTOR         = "gam.assign_mentor"
    const val GAM_ASSIGN                = "gam.assign"
    const val GAM_PAIR_STUDY_BUDDIES    = "gam.pair_study_buddies"
    const val GAM_PAIR_THEM             = "gam.pair_them"
    const val GAM_TOTAL_XP              = "gam.total_xp"
    const val GAM_LEVEL                 = "gam.level"
    const val GAM_STREAK                = "gam.streak"
    const val GAM_BADGES                = "gam.badges"
    const val GAM_QUESTS                = "gam.quests"
    const val GAM_BUDDY_PAIR            = "gam.buddy_pair"
    const val GAM_PEP_TALK_CONFIRM      = "gam.pep_talk_confirm"
    const val GAM_REWARD_PREFIX         = "gam.reward_prefix"
    const val GAM_UNKNOWN               = "gam.unknown"
    const val GAM_GOAL                  = "gam.goal"
    const val GAM_QUEST_BUTTON          = "gam.quest_button"
    const val GAM_BADGE_BUTTON          = "gam.badge_button"
    const val GAM_SHOUTOUT_FROM_TO      = "gam.shoutout_from_to"
    const val GAM_PROGRESS_FRACTION     = "gam.progress_fraction"
    const val GAM_XP_VALUE              = "gam.xp_value"

    // ── AdminGamificationScreen ─────────────────────────────────────────
    const val AGAM_NO_DATA              = "agam.no_data"
    const val AGAM_NO_DATA_DESC         = "agam.no_data_desc"
    const val AGAM_GAMIFICATION         = "agam.gamification"
    const val AGAM_MANAGEMENT_CONSOLE   = "agam.management_console"
    const val AGAM_FEATURE_FLAGS        = "agam.feature_flags"
    const val AGAM_UNABLE_LOAD_FLAGS    = "agam.unable_load_flags"
    const val AGAM_ENABLE_GAM           = "agam.enable_gam"
    const val AGAM_ENABLE_GAM_DESC      = "agam.enable_gam_desc"
    const val AGAM_GRANULAR_TOGGLES     = "agam.granular_toggles"
    const val AGAM_LEADERBOARDS         = "agam.leaderboards"
    const val AGAM_LEADERBOARDS_DESC    = "agam.leaderboards_desc"
    const val AGAM_REWARDS_SHOP         = "agam.rewards_shop"
    const val AGAM_REWARDS_SHOP_DESC    = "agam.rewards_shop_desc"
    const val AGAM_HOUSE_SYSTEM         = "agam.house_system"
    const val AGAM_HOUSE_SYSTEM_DESC    = "agam.house_system_desc"
    const val AGAM_QUESTS_LABEL         = "agam.quests_label"
    const val AGAM_QUESTS_DESC          = "agam.quests_desc"
    const val AGAM_MENTOR_SYSTEM        = "agam.mentor_system"
    const val AGAM_MENTOR_SYSTEM_DESC   = "agam.mentor_system_desc"
    const val AGAM_SHOUT_OUTS           = "agam.shout_outs"
    const val AGAM_SHOUT_OUTS_DESC      = "agam.shout_outs_desc"
    const val AGAM_SEASONAL_EVENTS      = "agam.seasonal_events"
    const val AGAM_SEASONAL_EVENTS_DESC = "agam.seasonal_events_desc"
    const val AGAM_CLASS_GOALS          = "agam.class_goals"
    const val AGAM_CLASS_GOALS_DESC     = "agam.class_goals_desc"
    const val AGAM_COMBOS               = "agam.combos"
    const val AGAM_COMBOS_DESC          = "agam.combos_desc"
    const val AGAM_XP_BOOSTS            = "agam.xp_boosts"
    const val AGAM_XP_BOOSTS_DESC       = "agam.xp_boosts_desc"
    const val AGAM_ANALYTICS_OVERVIEW   = "agam.analytics_overview"
    const val AGAM_REDEMPTIONS          = "agam.redemptions"
    const val AGAM_BADGE_CATALOG        = "agam.badge_catalog"
    const val AGAM_BADGE_DETAIL         = "agam.badge_detail"
    const val AGAM_SEASONAL             = "agam.seasonal"
    const val AGAM_LEVEL_DEFINITIONS    = "agam.level_definitions"
    const val AGAM_LEVEL_XP             = "agam.level_xp"
    const val AGAM_HOUSES               = "agam.houses"
    const val AGAM_HOUSE_DETAIL         = "agam.house_detail"
    const val AGAM_REWARDS_CATALOG      = "agam.rewards_catalog"
    const val AGAM_REWARD_XP            = "agam.reward_xp"
    const val AGAM_ACTIVE               = "agam.active"
    const val AGAM_INACTIVE             = "agam.inactive"
    const val AGAM_QUEST_POOL           = "agam.quest_pool"
    const val AGAM_QUEST_DETAIL         = "agam.quest_detail"
    const val AGAM_EVENTS_TITLE         = "agam.events_title"
    const val AGAM_EVENT_DATES          = "agam.event_dates"
    const val AGAM_ENDED                = "agam.ended"
    const val AGAM_SCHOOL_LEADERBOARD   = "agam.school_leaderboard"
    const val AGAM_LV                   = "agam.lv"
    const val AGAM_REDEMPTION_APPROVALS = "agam.redemption_approvals"
    const val AGAM_REDEMPTION_DETAIL    = "agam.redemption_detail"
    const val AGAM_APPROVE              = "agam.approve"
    const val AGAM_REJECT               = "agam.reject"
    const val AGAM_BOOSTS_TITLE         = "agam.boosts_title"
    const val AGAM_BOOST_MULT           = "agam.boost_mult"
    const val AGAM_EXPIRED              = "agam.expired"
    const val AGAM_CREATE_NEW_BOOST     = "agam.create_new_boost"
    const val AGAM_BOOST_TYPE           = "agam.boost_type"
    const val AGAM_MULTIPLIER_LABEL     = "agam.multiplier_label"
    const val AGAM_TARGET_SCOPE         = "agam.target_scope"
    const val AGAM_DURATION_HOURS       = "agam.duration_hours"
    const val AGAM_CREATE_BOOST         = "agam.create_boost"

    // ── ParentAcademicsScreen ───────────────────────────────────────────
    const val PAC_ACADEMIC_OVERVIEW     = "pac.academic_overview"
    const val PAC_EMOTIONAL_INTEL       = "pac.emotional_intel"
    const val PAC_THIS_TERM             = "pac.this_term"
    const val PAC_ATTENDANCE_RATE       = "pac.attendance_rate"
    const val PAC_NO_ATTENDANCE         = "pac.no_attendance"
    const val PAC_NO_ATTENDANCE_DESC    = "pac.no_attendance_desc"
    const val PAC_TYPE_ANSWER           = "pac.type_answer"
    const val PAC_LOADING_LEADERBOARD   = "pac.loading_leaderboard"
    const val PAC_CLASS_SCHEDULE        = "pac.class_schedule"
    const val PAC_SOMETHING_WRONG       = "pac.something_wrong"
    const val PAC_REPORT_CARD           = "pac.report_card"
    const val PAC_APPLY_LEAVE           = "pac.apply_leave"
    const val PAC_HEALTH_RECORDS        = "pac.health_records"
    const val PAC_SYLLABUS              = "pac.syllabus"
    const val PAC_LEVEL                 = "pac.level"
    const val PAC_ATTENDANCE            = "pac.attendance"
    const val PAC_AVG_SCORE             = "pac.avg_score"
    const val PAC_COVERED               = "pac.covered"
    const val PAC_PENDING               = "pac.pending"
    const val PAC_START                 = "pac.start"
    const val PAC_RESULT                = "pac.result"
    const val PAC_QUIZ                  = "pac.quiz"
    const val PAC_SCORE                 = "pac.score"
    const val PAC_YOUR_ANSWER           = "pac.your_answer"
    const val PAC_CORRECT_ANSWER        = "pac.correct_answer"
    const val PAC_SUBMIT_HOMEWORK       = "pac.submit_homework"
    const val PAC_EST                   = "pac.est"
    const val PAC_LEADERBOARD           = "pac.leaderboard"
    const val PAC_PARTICIPANTS          = "pac.participants"
    const val PAC_YOU                   = "pac.you"
    const val PAC_ROOM                  = "pac.room"
    const val PAC_LIVE                  = "pac.live"
    const val PAC_COLLAPSE              = "pac.collapse"
    const val PAC_EXPAND                = "pac.expand"
    const val PAC_MATCH                 = "pac.match"
    const val PAC_NO_LOG                = "pac.no_log"
    const val PAC_NO_LOG_DESC           = "pac.no_log_desc"
    const val PAC_QUICK_ACTIONS         = "pac.quick_actions"
    const val PAC_ACHIEVEMENTS          = "pac.achievements"
    const val PAC_FREE_PERIOD           = "pac.free_period"
    const val PAC_TRUE_FALSE            = "pac.true_false"
    const val PAC_FILL_BLANK            = "pac.fill_blank"
    const val PAC_ESTIMATED_NOTE        = "pac.estimated_note"
    const val PAC_AI                    = "pac.ai"

    // ── Exam screens ────────────────────────────────────────────────────
    const val EXAM_IMPORT_METHOD        = "exam.import_method"
    const val EXAM_PASTE_MARKS          = "exam.paste_marks"
    const val EXAM_EXTRACTING_MARKS     = "exam.extracting_marks"
    const val EXAM_MAY_TAKE_SECONDS     = "exam.may_take_seconds"
    const val EXAM_IMPORT_FAILED        = "exam.import_failed"
    const val EXAM_TRY_AGAIN            = "exam.try_again"
    const val EXAM_IMAGE_UNAVAILABLE    = "exam.image_unavailable"
    const val EXAM_PASTE_TEXT_INSTEAD   = "exam.paste_text_instead"
    const val EXAM_EXTRACTION_RESULTS   = "exam.extraction_results"
    const val EXAM_TIMETABLE_NAME       = "exam.timetable_name"
    const val EXAM_TERM_OPTIONAL        = "exam.term_optional"
    const val EXAM_PASTE_TIMETABLE      = "exam.paste_timetable"
    const val EXAM_EXTRACTING_ENTRIES   = "exam.extracting_entries"
    const val EXAM_EXTRACTED_ENTRIES    = "exam.extracted_entries"
    const val EXAM_ALL_CLASSES          = "exam.all_classes"
    const val EXAM_NO_ASSESSMENTS       = "exam.no_assessments"

    // ── SkillTestCard ───────────────────────────────────────────────────
    const val SKILL_GENERATING          = "skill.generating"
    const val SKILL_EVALUATING          = "skill.evaluating"

    // ── Misc screens ────────────────────────────────────────────────────
    const val MISC_PROFILE_PICTURE      = "misc.profile_picture"
    const val MISC_SCHOOL_LOGO          = "misc.school_logo"
    const val MISC_RECOMMENDED_RATIO    = "misc.recommended_ratio"
    const val MISC_ADD_PHOTO            = "misc.add_photo"
    const val MISC_PROFILE_COMPLETION   = "misc.profile_completion"
    const val MISC_PROFILE_DESC         = "misc.profile_desc"
    const val MISC_ENTER_UUID           = "misc.enter_uuid"
    const val MISC_SELECT_CONFIGURED    = "misc.select_configured"
    const val MISC_SAVED_SUCCESS        = "misc.saved_success"
    const val MISC_SAVE_CHANGES         = "misc.save_changes"
    const val MISC_ATTENDANCE_SAVED     = "misc.attendance_saved"
    const val MISC_SAVE_ATTENDANCE      = "misc.save_attendance"
    const val MISC_PASTE_CSV            = "misc.paste_csv"
    const val MISC_CONVERSION_RATE      = "misc.conversion_rate"
    const val MISC_TUTOR_THINKING       = "misc.tutor_thinking"
    const val MISC_PAID_ON              = "misc.paid_on"
    const val MISC_VIEW_SALARY          = "misc.view_salary"
    const val MISC_SALARY_DESC          = "misc.salary_desc"
    const val MISC_ANSWER_NOTES         = "misc.answer_notes"
    const val MISC_NO_AT_RISK           = "misc.no_at_risk"
    const val MISC_ALL_ON_TRACK         = "misc.all_on_track"
    const val MISC_MARK_ATTENDANCE      = "misc.mark_attendance"
    const val MISC_OR_PASTE_CSV         = "misc.or_paste_csv"

}

object AppStrings {

    private fun enPart1(): Map<String, String> = mapOf(
        StringKeys.COMMON_BUTTON_SAVE      to "Save",
        StringKeys.COMMON_BUTTON_CANCEL    to "Cancel",
        StringKeys.COMMON_BUTTON_RETRY     to "Retry",
        StringKeys.COMMON_BUTTON_DELETE    to "Delete",
        StringKeys.COMMON_BUTTON_EDIT      to "Edit",
        StringKeys.COMMON_BUTTON_CLOSE     to "Close",
        StringKeys.COMMON_BUTTON_CONTINUE  to "Continue",
        StringKeys.COMMON_BUTTON_BACK      to "Back",
        StringKeys.COMMON_BUTTON_CONFIRM   to "Confirm",
        StringKeys.COMMON_BUTTON_APPLY     to "Apply",
        StringKeys.COMMON_BUTTON_DONE      to "Done",
        StringKeys.COMMON_BUTTON_NEXT      to "Next",
        StringKeys.COMMON_BUTTON_SKIP      to "Skip",
        StringKeys.COMMON_BUTTON_REFRESH   to "Refresh",
        StringKeys.COMMON_BUTTON_SHARE     to "Share",
        StringKeys.COMMON_BUTTON_LOGOUT    to "Log Out",
        StringKeys.COMMON_ERROR_GENERIC    to "Something went wrong",
        StringKeys.COMMON_ERROR_NETWORK    to "Network error. Please check your connection.",
        StringKeys.COMMON_ERROR_OFFLINE    to "You are offline. Please check your connection.",
        StringKeys.COMMON_ERROR_TIMEOUT    to "Request timed out. Please try again.",
        StringKeys.COMMON_ERROR_NOT_FOUND  to "Not found",
        StringKeys.COMMON_ERROR_UNAUTHORIZED to "You are not authorized to perform this action.",
        StringKeys.COMMON_LOADING          to "Loading…",
        StringKeys.COMMON_EMPTY            to "Nothing here yet",
        StringKeys.COMMON_SEARCH           to "Search",
        StringKeys.COMMON_FILTER           to "Filter",
        StringKeys.COMMON_ALL              to "All",
        StringKeys.COMMON_NONE             to "None",
        StringKeys.COMMON_YES              to "Yes",
        StringKeys.COMMON_NO               to "No",
        StringKeys.COMMON_TODAY            to "Today",
        StringKeys.COMMON_YESTERDAY        to "Yesterday",
        StringKeys.COMMON_TOMORROW         to "Tomorrow",
        StringKeys.COMMON_SELECT           to "Select",
        StringKeys.COMMON_REQUIRED         to "Required",
        StringKeys.COMMON_OPTIONAL         to "Optional",
        // Auth
        StringKeys.AUTH_WELCOME            to "Welcome to Vidya Prayag",
        StringKeys.AUTH_LOGIN              to "Log In",
        StringKeys.AUTH_SIGNUP             to "Sign Up",
        StringKeys.AUTH_LOGOUT             to "Log Out",
        StringKeys.AUTH_PHONE              to "Phone Number",
        StringKeys.AUTH_EMAIL              to "Email",
        StringKeys.AUTH_PASSWORD           to "Password",
        StringKeys.AUTH_OTP                to "Enter OTP",
        StringKeys.AUTH_OTP_SENT           to "OTP sent to your phone",
        StringKeys.AUTH_OTP_VERIFY         to "Verify OTP",
        StringKeys.AUTH_NAME               to "Full Name",
        StringKeys.AUTH_ROLE_PARENT        to "Parent",
        StringKeys.AUTH_ROLE_TEACHER       to "Teacher",
        StringKeys.AUTH_ROLE_ADMIN         to "School Admin",
        StringKeys.AUTH_LOGIN_SUCCESS      to "Login successful",
        StringKeys.AUTH_LOGIN_FAILED       to "Login failed. Please try again.",
        StringKeys.AUTH_REGISTER_SCHOOL    to "Register Your School",
        // Language
        StringKeys.LANGUAGE_TITLE          to "Select Language",
        StringKeys.LANGUAGE_SELECT         to "Choose your preferred language",
        StringKeys.LANGUAGE_CHANGE         to "Change Language",
        StringKeys.LANGUAGE_CURRENT        to "Current language",
        StringKeys.LANGUAGE_ENGLISH        to "English",
        StringKeys.LANGUAGE_SEARCH         to "Search language…",
        // Nav
        StringKeys.NAV_HOME                to "Home",
        StringKeys.NAV_DASHBOARD           to "Dashboard",
        StringKeys.NAV_PROFILE             to "Profile",
        StringKeys.NAV_SETTINGS            to "Settings",
        StringKeys.NAV_NOTIFICATIONS       to "Notifications",
        StringKeys.NAV_MESSAGES            to "Messages",
        StringKeys.NAV_CALENDAR            to "Calendar",
        StringKeys.NAV_ATTENDANCE          to "Attendance",
        StringKeys.NAV_FEES                to "Fees",
        StringKeys.NAV_ACADEMICS           to "Academics",
        StringKeys.NAV_MORE                to "More",
        // Dashboard
        StringKeys.DASH_GOOD_MORNING       to "Good morning",
        StringKeys.DASH_GOOD_AFTERNOON     to "Good afternoon",
        StringKeys.DASH_GOOD_EVENING       to "Good evening",
        StringKeys.DASH_WELCOME_BACK       to "Welcome back, {name}",
        StringKeys.DASH_QUICK_STATS        to "Quick Stats",
        StringKeys.DASH_RECENT_ACTIVITY    to "Recent Activity",
        // Attendance
        StringKeys.ATT_PRESENT             to "Present",
        StringKeys.ATT_ABSENT              to "Absent",
        StringKeys.ATT_LATE                to "Late",
        StringKeys.ATT_HALF_DAY            to "Half Day",
        StringKeys.ATT_MARK_PRESENT        to "Mark Present",
        StringKeys.ATT_MARK_ABSENT         to "Mark Absent",
        StringKeys.ATT_RATE                to "{count}% attendance",
        StringKeys.ATT_RATE_PLURAL         to "{count}% attendance",
        // Fees
        StringKeys.FEE_PAID                to "Paid",
        StringKeys.FEE_DUE                 to "Due",
        StringKeys.FEE_OVERDUE             to "Overdue",
        StringKeys.FEE_PAY_NOW             to "Pay Now",
        StringKeys.FEE_HISTORY             to "Payment History",
        StringKeys.FEE_AMOUNT              to "Amount",
        StringKeys.FEE_DUE_DATE            to "Due Date",
        StringKeys.FEE_TOTAL               to "Total",
        StringKeys.FEE_PENDING             to "Pending",
        // Notifications
        StringKeys.NOTIF_TITLE             to "Notifications",
        StringKeys.NOTIF_MARK_READ         to "Mark as Read",
        StringKeys.NOTIF_MARK_ALL_READ     to "Mark All as Read",
        StringKeys.NOTIF_EMPTY             to "No notifications",
        StringKeys.NOTIF_UNREAD            to "{count} unread notification",
        StringKeys.NOTIF_UNREAD_PLURAL     to "{count} unread notifications",
        // Profile
        StringKeys.PROFILE_TITLE           to "Profile",
        StringKeys.PROFILE_EDIT            to "Edit Profile",
        StringKeys.PROFILE_NAME            to "Name",
        StringKeys.PROFILE_PHONE           to "Phone",
        StringKeys.PROFILE_EMAIL           to "Email",
        StringKeys.PROFILE_SCHOOL          to "School",
        StringKeys.PROFILE_ROLE            to "Role",
        StringKeys.PROFILE_LANGUAGE        to "Language",
        StringKeys.PROFILE_THEME           to "Theme",
        StringKeys.PROFILE_ABOUT           to "About",
        StringKeys.PROFILE_HELP            to "Help & Support",
        StringKeys.PROFILE_PRIVACY         to "Privacy Policy",
        // Settings
        StringKeys.SETTINGS_TITLE          to "Settings",
        StringKeys.SETTINGS_GENERAL        to "General",
        StringKeys.SETTINGS_NOTIFICATIONS  to "Notifications",
        StringKeys.SETTINGS_LANGUAGE       to "Language",
        StringKeys.SETTINGS_THEME          to "Theme",
        StringKeys.SETTINGS_ABOUT          to "About",
        StringKeys.SETTINGS_LOGOUT         to "Log Out",
        StringKeys.SETTINGS_FONT_SIZE      to "Font Size",
        // Child
        StringKeys.CHILD_TITLE             to "My Children",
        StringKeys.CHILD_ADD               to "Add Child",
        StringKeys.CHILD_LINK              to "Link Child",
        StringKeys.CHILD_NAME              to "Child Name",
        StringKeys.CHILD_CLASS             to "Class",
        StringKeys.CHILD_SECTION           to "Section",
        StringKeys.CHILD_ROLL              to "Roll Number",
        StringKeys.CHILD_PROGRESS          to "Progress",
        StringKeys.CHILD_ATTENDANCE        to "Attendance",
        StringKeys.CHILD_MARKS             to "Marks",
        StringKeys.CHILD_STUDENTS          to "{count} student",
        StringKeys.CHILD_STUDENTS_PLURAL   to "{count} students",
        // School
        StringKeys.SCHOOL_TITLE            to "School",
        StringKeys.SCHOOL_NAME             to "School Name",
        StringKeys.SCHOOL_CLASSES          to "Classes",
        StringKeys.SCHOOL_TEACHERS         to "Teachers",
        StringKeys.SCHOOL_STUDENTS         to "Students",
        StringKeys.SCHOOL_ONBOARDING       to "Onboarding",
        StringKeys.SCHOOL_BRANDING         to "Branding",
        StringKeys.SCHOOL_ACADEMIC         to "Academic Setup",
        // Teacher
        StringKeys.TEACHER_TITLE           to "Teacher",
        StringKeys.TEACHER_CLASSES         to "My Classes",
        StringKeys.TEACHER_SYLLABUS        to "Syllabus",
        StringKeys.TEACHER_HOMEWORK        to "Homework",
        StringKeys.TEACHER_LESSON_PLAN     to "Lesson Plan",
        StringKeys.TEACHER_ATTENDANCE      to "Attendance",
        StringKeys.TEACHER_GRADEBOOK       to "Gradebook",
        StringKeys.TEACHER_LEAVE           to "Leave",
        // Calendar
        StringKeys.CAL_TITLE               to "Calendar",
        StringKeys.CAL_TODAY               to "Today",
        StringKeys.CAL_EVENTS              to "Events",
        StringKeys.CAL_HOLIDAYS            to "Holidays",
        StringKeys.CAL_EXAMS               to "Exams",
        StringKeys.CAL_PTM                 to "Parent-Teacher Meeting",
        // Messages
        StringKeys.MSG_TITLE               to "Messages",
        StringKeys.MSG_SEND                to "Send",
        StringKeys.MSG_REPLY               to "Reply",
        StringKeys.MSG_EMPTY               to "No messages",
        StringKeys.MSG_TYPE_MESSAGE        to "Type a message…",
        StringKeys.MSG_BROADCAST           to "Broadcast",
        // Onboarding
        StringKeys.OB_WELCOME              to "Welcome",
        StringKeys.OB_STEP                 to "Step {current} of {total}",
        StringKeys.OB_BASIC_INFO           to "Basic Information",
        StringKeys.OB_BRANDING             to "Branding",
        StringKeys.OB_ACADEMIC             to "Academic Setup",
        StringKeys.OB_REVIEW               to "Review",
        StringKeys.OB_FINISH               to "Finish Setup",
        // Splash
        StringKeys.SPLASH_TAGLINE          to "Bridging gaps for a glorious future",
        // Auth scaffold
        StringKeys.AUTH_SECURED            to "Secured with end-to-end encryption",
        StringKeys.AUTH_BACK_LINK          to "‹ Back",
        // Parent auth
        StringKeys.AUTH_PARENT_WELCOME     to "Welcome, parent 👋",
        StringKeys.AUTH_PARENT_SUBTITLE    to "Sign in with your mobile number to connect with your child's school.",
        StringKeys.AUTH_MOBILE_NUMBER      to "Mobile number",
        StringKeys.AUTH_YOUR_NAME          to "Your name",
        StringKeys.AUTH_FULL_NAME_PH       to "Full name",
        StringKeys.AUTH_OTP_CODE_PH        to "6-digit code",
        StringKeys.AUTH_OTP_SENT_TO        to "We sent a code to {phone}.",
        StringKeys.AUTH_YOUR_PHONE         to "your phone",
        StringKeys.AUTH_SEND_OTP           to "Send OTP",
        StringKeys.AUTH_VERIFY_CONTINUE    to "Verify & Continue",
        // Admin auth
        StringKeys.AUTH_ADMIN_TITLE        to "School Administration",
        StringKeys.AUTH_ADMIN_SUBTITLE     to "Sign in with your staff credentials to manage your institution.",
        StringKeys.AUTH_EMAIL_OR_STAFF_ID  to "Email or staff ID",
        StringKeys.AUTH_FORGOT_PASSWORD    to "Forgot Password?",
        StringKeys.AUTH_WORK_EMAIL         to "Work email",
        StringKeys.AUTH_SCHOOL_NAME        to "School name",
        StringKeys.AUTH_BOARD              to "BOARD",
        StringKeys.AUTH_CITY_OPTIONAL      to "City (optional)",
        StringKeys.AUTH_CREATE_PASSWORD    to "Create a password",
        StringKeys.AUTH_PASSWORD_8_PH      to "At least 8 characters",
        StringKeys.AUTH_NO_ACCOUNT         to "No account exists for this email.",
        StringKeys.AUTH_NEW_REGISTER       to "New to VidyaPrayag? Register your school below to set it up and create your administrator account. Teachers and additional staff are added by your school administrator after onboarding.",
        StringKeys.AUTH_REGISTER_CONTINUE  to "Register & continue",
        StringKeys.AUTH_ONBOARD_SCHOOL     to "Onboard your school",
        StringKeys.AUTH_SIGN_IN            to "Sign In",
        StringKeys.AUTH_SETTING_UP_SCHOOL  to "Are you setting up a school?",
        StringKeys.AUTH_CREATE_ADMIN_ACCT  to "Create your administrator account and bring your school onto VidyaPrayag.",
        StringKeys.AUTH_REGISTER_MY_SCHOOL to "Register my school →",
        StringKeys.AUTH_SHOW_PASSWORD      to "Show password",
        StringKeys.AUTH_HIDE_PASSWORD      to "Hide password",
        // Teacher first login
        StringKeys.AUTH_SET_NEW_PASSWORD   to "Set a new password",
        StringKeys.AUTH_FIRST_LOGIN_DESC   to "For your security, choose a fresh password before continuing. You'll only do this once.",
        StringKeys.AUTH_CURRENT_TEMP_PW    to "Current temporary password",
        StringKeys.AUTH_NEW_PASSWORD       to "New password",
        StringKeys.AUTH_CONFIRM_PASSWORD   to "Confirm new password",
        StringKeys.AUTH_REENTER_PH         to "Re-enter",
        StringKeys.AUTH_UPDATE_CONTINUE    to "Update & continue",
        StringKeys.AUTH_NEED_HELP          to "Need help signing in?",
        StringKeys.AUTH_PW_TOO_SHORT       to "New password must be at least 8 characters.",
        StringKeys.AUTH_PW_NO_MATCH        to "Passwords don't match.",
        StringKeys.AUTH_CONN_ERROR         to "Connection error. Please try again.",

        // Legal info screen
        StringKeys.LEGAL_TAB_PRIVACY       to "Privacy",
        StringKeys.LEGAL_TAB_TERMS         to "Terms",
        StringKeys.LEGAL_TAB_HELP          to "Help Desk",
        StringKeys.LEGAL_TITLE             to "Legal & Support",
        StringKeys.LEGAL_FOOTER            to "VidyaSetu · Little Bridge",
        StringKeys.LEGAL_LAST_UPDATED      to "Last updated: June 2026",
        StringKeys.LEGAL_BACK              to "Back",
        StringKeys.LEGAL_PRIV_EYEBROW      to "Your data",
        StringKeys.LEGAL_PRIV_TITLE        to "Privacy Policy",
        StringKeys.LEGAL_PRIV_INTRO        to "VidyaSetu connects parents and schools. We collect only the information needed to run the service: your name and contact details, the school and children you are linked to, and the academic records (attendance, marks, fees, messages) your school shares with you.",
        StringKeys.LEGAL_PRIV_COLLECT_T    to "What we collect",
        StringKeys.LEGAL_PRIV_COLLECT_1    to "Account details — name, phone or email used to sign in.",
        StringKeys.LEGAL_PRIV_COLLECT_2    to "School linkage — the institution and student(s) connected to your account.",
        StringKeys.LEGAL_PRIV_COLLECT_3    to "Academic data — attendance, assessments, fees and announcements published by your school.",
        StringKeys.LEGAL_PRIV_COLLECT_4    to "Messages — communication you send or receive through the in-app channels.",
        StringKeys.LEGAL_PRIV_USE_T        to "How we use it",
        StringKeys.LEGAL_PRIV_USE_1        to "To show you your child's academic information and school updates.",
        StringKeys.LEGAL_PRIV_USE_2        to "To deliver notifications you have opted into (results, fees, announcements).",
        StringKeys.LEGAL_PRIV_USE_3        to "To keep your account secure and prevent misuse.",
        StringKeys.LEGAL_PRIV_NEVER_T      to "What we never do",
        StringKeys.LEGAL_PRIV_NEVER_1      to "We do not sell your data.",
        StringKeys.LEGAL_PRIV_NEVER_2      to "We do not use your data for third-party advertising.",
        StringKeys.LEGAL_PRIV_NEVER_3      to "We do not share your child's records outside your school's authorised staff and your linked parent account.",
        StringKeys.LEGAL_PRIV_SCOPED_T     to "Your data is school-scoped",
        StringKeys.LEGAL_PRIV_SCOPED_B     to "Every record is tied to your school. Access is decided server-side from your signed-in session — you only ever see the data belonging to your own account and school.",
        StringKeys.LEGAL_PRIV_RETENTION_T  to "Retention & deletion",
        StringKeys.LEGAL_PRIV_RETENTION_B  to "We keep your data while your account is active. To request access, correction, or deletion of your information, contact us at the Help Desk — we respond to every request.",
        StringKeys.LEGAL_TERMS_EYEBROW     to "The agreement",
        StringKeys.LEGAL_TERMS_TITLE       to "Terms of Service",
        StringKeys.LEGAL_TERMS_INTRO       to "By using VidyaSetu you agree to these terms. They are written to be clear and fair. If you do not agree, please do not use the app.",
        StringKeys.LEGAL_TERMS_USE_T       to "Using the app",
        StringKeys.LEGAL_TERMS_USE_1       to "You must provide accurate information when creating your account.",
        StringKeys.LEGAL_TERMS_USE_2       to "You are responsible for keeping your login credentials confidential.",
        StringKeys.LEGAL_TERMS_USE_3       to "Use the app only for its intended purpose — connecting with your school and tracking your child's progress.",
        StringKeys.LEGAL_TERMS_ACCOUNTS_T  to "Accounts & access",
        StringKeys.LEGAL_TERMS_ACCOUNTS_B  to "Parent accounts link to children enrolled at a participating school. Teacher and staff accounts are created by your school's administrator. Schools control which records are published to parents.",
        StringKeys.LEGAL_TERMS_CONTENT_T   to "Content & communication",
        StringKeys.LEGAL_TERMS_CONTENT_1   to "Messages and announcements are part of the official school record.",
        StringKeys.LEGAL_TERMS_CONTENT_2   to "Do not post unlawful, abusive, or misleading content.",
        StringKeys.LEGAL_TERMS_CONTENT_3   to "We may suspend accounts that violate these terms or misuse the platform.",
        StringKeys.LEGAL_TERMS_AVAIL_T     to "Availability",
        StringKeys.LEGAL_TERMS_AVAIL_B     to "We work hard to keep VidyaSetu running, but the service is provided \"as is\". We are not liable for occasional downtime, and we may update features as the product evolves.",
        StringKeys.LEGAL_TERMS_CHANGES_T   to "Changes to these terms",
        StringKeys.LEGAL_TERMS_CHANGES_B   to "We may update these terms as the app grows. We will surface material changes in the app. Continued use after an update means you accept the revised terms.",
        StringKeys.LEGAL_TERMS_CONTACT_T   to "Contact",
        StringKeys.LEGAL_TERMS_CONTACT_B   to "Questions about these terms? Reach us via the Help Desk tab.",
        StringKeys.LEGAL_HELP_EYEBROW      to "We're here",
        StringKeys.LEGAL_HELP_TITLE        to "Help Desk",
        StringKeys.LEGAL_HELP_INTRO        to "Need a hand, found a bug, or have a question about your account? Our team reads every message and replies as quickly as we can.",
        StringKeys.LEGAL_HELP_EMAIL        to "Email support",
        StringKeys.LEGAL_HELP_INCLUDE_T    to "What to include",
        StringKeys.LEGAL_HELP_INCLUDE_1    to "Your role (parent, teacher, or admin) and your school's name.",
        StringKeys.LEGAL_HELP_INCLUDE_2    to "A short description of the problem or question.",
        StringKeys.LEGAL_HELP_INCLUDE_3    to "A screenshot, if it helps explain the issue.",
        StringKeys.LEGAL_HELP_FAQ_T        to "Common questions",
        StringKeys.LEGAL_HELP_FAQ_Q1       to "I can't link my child",
        StringKeys.LEGAL_HELP_FAQ_A1       to "Check the student code with your school's office, then try the Link Child flow again from your profile.",
        StringKeys.LEGAL_HELP_FAQ_Q2       to "I forgot my password",
        StringKeys.LEGAL_HELP_FAQ_A2       to "Teachers and admins can ask their school administrator to reset it. Parents sign in with a one-time code.",
        StringKeys.LEGAL_HELP_FAQ_Q3       to "I'm not getting notifications",
        StringKeys.LEGAL_HELP_FAQ_A3       to "Make sure notifications are enabled for VidyaSetu in your device settings.",

        // Parent link child screen
        StringKeys.LINK_STEP_OF            to "Step {step} of {total}",
        StringKeys.LINK_STEP1_TITLE        to "Tell us about you",
        StringKeys.LINK_STEP1_SUB          to "So your child's school knows who to send updates to.",
        StringKeys.LINK_FULL_NAME          to "Your full name",
        StringKeys.LINK_FULL_NAME_PH       to "e.g. Sneha Sharma",
        StringKeys.LINK_PREF_LANG          to "Preferred language",
        StringKeys.LINK_STEP2_TITLE        to "Find your child's school",
        StringKeys.LINK_STEP2_SUB          to "Type the school name. We'll match it against schools using VidyaSetu.",
        StringKeys.LINK_SEARCH_PH          to "Search by school name",
        StringKeys.LINK_SEARCHING          to "Searching…",
        StringKeys.LINK_SEARCH             to "Search",
        StringKeys.LINK_SEARCH_ERR         to "Something went wrong",
        StringKeys.LINK_SEARCH_PROMPT      to "Search for your child's school to see matches.",
        StringKeys.LINK_TAP_SELECT         to "Tap your child's school to select it.",
        StringKeys.LINK_MATCH              to "Match",
        StringKeys.LINK_STEP3_TITLE        to "Link your child",
        StringKeys.LINK_STEP3_SUB          to "Tell us about your child at {school} so we can match them precisely.",
        StringKeys.LINK_CHILD_NAME         to "Child's full name",
        StringKeys.LINK_CHILD_NAME_PH      to "e.g. Aarav Sharma",
        StringKeys.LINK_CLASS              to "Class",
        StringKeys.LINK_CLASS_PH           to "e.g. 4",
        StringKeys.LINK_SECTION            to "Section",
        StringKeys.LINK_SECTION_PH         to "e.g. A",
        StringKeys.LINK_ROLL               to "Roll / admission number",
        StringKeys.LINK_ROLL_PH            to "e.g. 02",
        StringKeys.LINK_PHONE_OPT          to "Your phone number (optional)",
        StringKeys.LINK_PHONE_PH           to "e.g. 98765 43210",
        StringKeys.LINK_ERR                to "Could not link your child",
        StringKeys.LINK_REVIEW_MSG         to "We found your child but the phone number didn't match — {school} will review and confirm.",
        StringKeys.LINK_PENDING_MSG        to "Request sent — awaiting {school} approval",
        StringKeys.LINK_CLASS_ROLL         to "Class {class} • Roll {roll}",
        StringKeys.LINK_MATCH_PROMPT       to "We'll match this against {school}'s records when you tap Finish.",
        StringKeys.LINK_CONTINUE           to "Continue",
        StringKeys.LINK_LINKING            to "Linking…",
        StringKeys.LINK_DONE               to "Done",
        StringKeys.LINK_FINISH             to "Finish & open dashboard",
        StringKeys.LINK_THE_SCHOOL         to "the school",
        StringKeys.LINK_YOUR_SCHOOL        to "your school",

        // CommonLandingScreenV2
        StringKeys.LANDING_BRAND            to "EnRoll+",
        StringKeys.LANDING_SCHOOL_EYEBROW   to "SCHOOL MANAGEMENT",
        StringKeys.LANDING_PARENT_EYEBROW   to "PARENT PORTAL",
        StringKeys.LANDING_SCHOOL_HEADLINE  to "Run your whole school\nfrom one screen.",
        StringKeys.LANDING_PARENT_HEADLINE  to "Your child's school day,\nin your pocket.",
        StringKeys.LANDING_SCHOOL_SUB       to "Attendance, admissions, results, fees and parent messaging — one platform your staff actually want to use.",
        StringKeys.LANDING_PARENT_SUB       to "Attendance, marks, fees and messages from the school — clear, instant, and always up to date.",
        StringKeys.LANDING_TAB_SCHOOLS      to "For Schools",
        StringKeys.LANDING_TAB_PARENTS      to "For Parents",
        StringKeys.LANDING_IMG_LABEL_SCHOOL to "A real school, run on VidyaSetu",
        StringKeys.LANDING_IMG_LABEL_PARENT to "Stay close to your child's progress",
        StringKeys.LANDING_CTA_SCHOOLS      to "Get Started — Schools",
        StringKeys.LANDING_CTA_PARENTS      to "Get Started — Parents",
        StringKeys.LANDING_OUTLINED_PARENTS to "For Parents",
        StringKeys.LANDING_OUTLINED_SCHOOLS to "For Schools",
        StringKeys.LANDING_FOOTER_PREFIX    to "By continuing you agree to our ",
        StringKeys.LANDING_FOOTER_TERMS     to "Terms",
        StringKeys.LANDING_FOOTER_AND       to " & ",
        StringKeys.LANDING_FOOTER_PRIVACY   to "Privacy",
        StringKeys.LANDING_SCHOOL_F1_T      to "Daily attendance in seconds",
        StringKeys.LANDING_SCHOOL_F1_D      to "Mark a whole class in one pass — absences alert the right parent the moment you save.",
        StringKeys.LANDING_SCHOOL_F2_T      to "Admissions, end to end",
        StringKeys.LANDING_SCHOOL_F2_D      to "Track every enquiry from first call to enrolment, with follow-ups and conversion built in.",
        StringKeys.LANDING_SCHOOL_F3_T      to "See trouble before it lands",
        StringKeys.LANDING_SCHOOL_F3_D      to "Class and faculty analytics surface the students and teachers who need attention early.",
        StringKeys.LANDING_SCHOOL_F4_T      to "Publish results, cleanly",
        StringKeys.LANDING_SCHOOL_F4_D      to "Enter marks, review the class spread, and release report-ready results in one flow.",
        StringKeys.LANDING_SCHOOL_F5_T      to "One voice to every parent",
        StringKeys.LANDING_SCHOOL_F5_D      to "Announcements, messages and PTM scheduling — every conversation in one place, forever.",
        StringKeys.LANDING_SCHOOL_F6_T      to "Accountability, not paperwork",
        StringKeys.LANDING_SCHOOL_F6_D      to "Syllabus coverage, leave approvals and teacher compliance, tracked without the spreadsheets.",
        StringKeys.LANDING_PARENT_F1_T      to "Every day, accounted for",
        StringKeys.LANDING_PARENT_F1_D      to "A clear month calendar of present, late and absent days — no guessing, no chasing.",
        StringKeys.LANDING_PARENT_F2_T      to "Marks the moment they're in",
        StringKeys.LANDING_PARENT_F2_D      to "Real published results and syllabus progress for your child, the day the school releases them.",
        StringKeys.LANDING_PARENT_F3_T      to "Fees without the friction",
        StringKeys.LANDING_PARENT_F3_D      to "See exactly what's due and what's paid, with the school's fee notices in the same place.",
        StringKeys.LANDING_PARENT_F4_T      to "Talk to the right teacher",
        StringKeys.LANDING_PARENT_F4_D      to "Message your child's class teacher or the school office directly — replies land in one thread.",
        StringKeys.LANDING_PARENT_F5_T      to "Never miss what matters",
        StringKeys.LANDING_PARENT_F5_D      to "School announcements and activity, filtered to what's relevant to your family.",
        StringKeys.LANDING_PARENT_F6_T      to "Apply for leave in a tap",
        StringKeys.LANDING_PARENT_F6_D      to "Request a day off and it routes straight to the class teacher, with status you can follow.",

        // CommonLandingScreenV3 — Hero
        StringKeys.LV3_BRAND               to "EnRoll+",
        StringKeys.LV3_SCHOOL_TAGLINE      to "The intelligence layer\nconnecting your entire school ecosystem.",
        StringKeys.LV3_PARENT_TAGLINE      to "Your child's school day,\nin your pocket — clear and instant.",
        StringKeys.LV3_SCHOOL_CONTEXT      to "For principals, administrators and teachers",
        StringKeys.LV3_PARENT_CONTEXT      to "For parents who want to stay close",
        StringKeys.LV3_PILL_SCHOOLS        to "Schools",
        StringKeys.LV3_PILL_PARENTS        to "Parents",
        // V3 — Morphing words
        StringKeys.LV3_SCHOOL_MORPH_1      to "Manage.",
        StringKeys.LV3_SCHOOL_MORPH_2      to "Automate.",
        StringKeys.LV3_SCHOOL_MORPH_3      to "Grow.",
        StringKeys.LV3_SCHOOL_MORPH_4      to "Transform.",
        StringKeys.LV3_PARENT_MORPH_1      to "Track.",
        StringKeys.LV3_PARENT_MORPH_2      to "Connect.",
        StringKeys.LV3_PARENT_MORPH_3      to "Support.",
        StringKeys.LV3_PARENT_MORPH_4      to "Celebrate.",
        // V3 — Command center
        StringKeys.LV3_CMD_SCHOOL_EYEBROW  to "LIVE SCHOOL COMMAND CENTER",
        StringKeys.LV3_CMD_PARENT_EYEBROW  to "YOUR CHILD'S DAY, LIVE",
        StringKeys.LV3_CMD_SCHOOL_TITLE    to "Today's Overview",
        StringKeys.LV3_CMD_PARENT_TITLE    to "Today's Snapshot",
        StringKeys.LV3_LIVE                to "LIVE",
        StringKeys.LV3_CMD_STUDENTS        to "Students",
        StringKeys.LV3_CMD_TEACHERS        to "Teachers",
        StringKeys.LV3_CMD_ATTENDANCE      to "Attendance",
        StringKeys.LV3_CMD_FEE             to "Fee Collection",
        StringKeys.LV3_CMD_ADMISSIONS      to "Admissions",
        StringKeys.LV3_CMD_ADMISSIONS_TREND to "this month",
        StringKeys.LV3_CMD_SATISFACTION    to "Satisfaction",
        StringKeys.LV3_CMD_SATISFACTION_TREND to "parent rating",
        StringKeys.LV3_CMD_P_ATTENDANCE    to "Attendance",
        StringKeys.LV3_CMD_P_ATTENDANCE_V  to "Present",
        StringKeys.LV3_CMD_P_LAST_TEST     to "Last Test",
        StringKeys.LV3_CMD_P_FEES          to "Fees Paid",
        StringKeys.LV3_CMD_P_FEES_V        to "Current",
        StringKeys.LV3_CMD_P_MESSAGES      to "Messages",
        StringKeys.LV3_CMD_HOMEWORK        to "Homework",
        StringKeys.LV3_CMD_HOMEWORK_TREND  to "pending today",
        StringKeys.LV3_CMD_PTM             to "Next PTM",
        StringKeys.LV3_CMD_PTM_TREND       to "3:00 PM slot",
        // V3 — Ecosystem
        StringKeys.LV3_ECO_SCHOOL_EYEBROW  to "ONE PLATFORM. FOUR ECOSYSTEMS.",
        StringKeys.LV3_ECO_PARENT_EYEBROW  to "EVERYTHING YOU NEED. IN ONE APP.",
        StringKeys.LV3_ECO_S1_T            to "School Intelligence",
        StringKeys.LV3_ECO_S1_S            to "Understand your school instantly",
        StringKeys.LV3_ECO_S1_M1           to "Attendance trends",
        StringKeys.LV3_ECO_S1_M2           to "Performance analytics",
        StringKeys.LV3_ECO_S1_M3           to "Teacher activity",
        StringKeys.LV3_ECO_S2_T            to "Teacher Empowerment",
        StringKeys.LV3_ECO_S2_S            to "Less paperwork. More teaching.",
        StringKeys.LV3_ECO_S2_M1           to "Lesson planning",
        StringKeys.LV3_ECO_S2_M2           to "Syllabus progress",
        StringKeys.LV3_ECO_S2_M3           to "Class insights",
        StringKeys.LV3_ECO_S3_T            to "Parent Connection",
        StringKeys.LV3_ECO_S3_S            to "Every parent stays connected.",
        StringKeys.LV3_ECO_S3_M1           to "Child's journey",
        StringKeys.LV3_ECO_S3_M2           to "Direct messaging",
        StringKeys.LV3_ECO_S3_M3           to "Real-time progress",
        StringKeys.LV3_ECO_S4_T            to "Growth Engine",
        StringKeys.LV3_ECO_S4_S            to "From admission to graduation.",
        StringKeys.LV3_ECO_S4_M1           to "Enquiry tracking",
        StringKeys.LV3_ECO_S4_M2           to "Conversion funnel",
        StringKeys.LV3_ECO_S4_M3           to "Retention metrics",
        StringKeys.LV3_ECO_P1_T            to "Attendance Calendar",
        StringKeys.LV3_ECO_P1_S            to "Every day, accounted for.",
        StringKeys.LV3_ECO_P1_M1           to "Present days",
        StringKeys.LV3_ECO_P1_M2           to "Late arrivals",
        StringKeys.LV3_ECO_P1_M3           to "Absent patterns",
        StringKeys.LV3_ECO_P2_T            to "Academic Progress",
        StringKeys.LV3_ECO_P2_S            to "Marks the moment they're in.",
        StringKeys.LV3_ECO_P2_M1           to "Live results",
        StringKeys.LV3_ECO_P2_M2           to "Syllabus coverage",
        StringKeys.LV3_ECO_P2_M3           to "Report cards",
        StringKeys.LV3_ECO_P3_T            to "Fee Management",
        StringKeys.LV3_ECO_P3_S            to "Fees without the friction.",
        StringKeys.LV3_ECO_P3_M1           to "Due dates",
        StringKeys.LV3_ECO_P3_M2           to "Payment history",
        StringKeys.LV3_ECO_P3_M3           to "Fee notices",
        StringKeys.LV3_ECO_P4_T            to "School Communication",
        StringKeys.LV3_ECO_P4_S            to "Talk to the right teacher.",
        StringKeys.LV3_ECO_P4_M1           to "Direct messages",
        StringKeys.LV3_ECO_P4_M2           to "Announcements",
        StringKeys.LV3_ECO_P4_M3           to "PTM scheduling",
        // V3 — AI insight
        StringKeys.LV3_AI_TITLE            to "EnRoll Intelligence",
        StringKeys.LV3_AI_LABEL            to "AI ANALYSIS",
        StringKeys.LV3_AI_S1               to "Three students may require academic attention.",
        StringKeys.LV3_AI_S2               to "Fee collection improved 12% this month.",
        StringKeys.LV3_AI_S3               to "Teacher workload imbalance detected in Grade 8.",
        StringKeys.LV3_AI_P1               to "Your child's attendance is above class average this month.",
        StringKeys.LV3_AI_P2               to "Math scores improved by 8% since last assessment.",
        StringKeys.LV3_AI_P3               to "PTM scheduled for Friday — please confirm your slot.",
        // V3 — Timeline
        StringKeys.LV3_TL_SCHOOL_EYEBROW   to "A DAY WITH EnRoll+",
        StringKeys.LV3_TL_PARENT_EYEBROW   to "YOUR CHILD'S DAY, TIMELINED",
        StringKeys.LV3_TL_S1_T             to "School opens",
        StringKeys.LV3_TL_S1_D             to "Gates unlocked, system active",
        StringKeys.LV3_TL_S2_T             to "Attendance synchronized",
        StringKeys.LV3_TL_S2_D             to "1,240 students marked in 5 minutes",
        StringKeys.LV3_TL_S3_T             to "Assessment completed",
        StringKeys.LV3_TL_S3_D             to "Results published to parents instantly",
        StringKeys.LV3_TL_S4_T             to "Parent update sent",
        StringKeys.LV3_TL_S4_D             to "Announcements delivered to 1,200+ families",
        StringKeys.LV3_TL_S5_T             to "Analytics generated",
        StringKeys.LV3_TL_S5_D             to "AI insights ready for review",
        StringKeys.LV3_TL_P1_T             to "Bus tracking",
        StringKeys.LV3_TL_P1_D             to "Live location shared with school",
        StringKeys.LV3_TL_P2_T             to "Attendance marked",
        StringKeys.LV3_TL_P2_D             to "Your child checked in — notification received",
        StringKeys.LV3_TL_P3_T             to "Lunch break",
        StringKeys.LV3_TL_P3_D             to "Cafeteria activity logged",
        StringKeys.LV3_TL_P4_T             to "School ends",
        StringKeys.LV3_TL_P4_D             to "Pickup confirmed, day summary sent",
        StringKeys.LV3_TL_P5_T             to "Homework posted",
        StringKeys.LV3_TL_P5_D             to "Assignments and syllabus updates available",
        // V3 — Trust metrics
        StringKeys.LV3_TRUST_SCHOOL_EYEBROW to "NUMBERS THAT MATTER",
        StringKeys.LV3_TRUST_PARENT_EYEBROW to "PEACE OF MIND, GUARANTEED",
        StringKeys.LV3_TRUST_S1_V           to "24,000+",
        StringKeys.LV3_TRUST_S1_L           to "Daily student interactions",
        StringKeys.LV3_TRUST_S2_L           to "Parent connections",
        StringKeys.LV3_TRUST_S3_L           to "Workflow reliability",
        StringKeys.LV3_TRUST_P1_V           to "Instant",
        StringKeys.LV3_TRUST_P1_L           to "Attendance notifications",
        StringKeys.LV3_TRUST_P2_V           to "Real-time",
        StringKeys.LV3_TRUST_P2_L           to "Results & marks updates",
        StringKeys.LV3_TRUST_P3_V           to "24/7",
        StringKeys.LV3_TRUST_P3_L           to "Access to school communication",
        // V3 — Testimonials
        StringKeys.LV3_TEST_S_QUOTE        to "Finally a system teachers actually love.",
        StringKeys.LV3_TEST_S_ROLE         to "Principal",
        StringKeys.LV3_TEST_S_ORG          to "Modern School",
        StringKeys.LV3_TEST_P_QUOTE        to "I know exactly how my child is doing, every single day.",
        StringKeys.LV3_TEST_P_ROLE         to "Parent",
        StringKeys.LV3_TEST_P_ORG          to "Delhi Public School",
        // V3 — CTA dock
        StringKeys.LV3_CTA_PROMPT          to "Ready to experience smarter education?",
        StringKeys.LV3_CTA_ENTER           to "Enter EnRoll+",
        StringKeys.LV3_CTA_PARENT          to "I'm a Parent",
        StringKeys.LV3_CTA_SCHOOL          to "I'm a School",
        StringKeys.LV3_FOOTER_PREFIX       to "By continuing you agree to our ",
        StringKeys.LV3_FOOTER_TERMS        to "Terms",
        StringKeys.LV3_FOOTER_AND          to " & ",
        StringKeys.LV3_FOOTER_PRIVACY      to "Privacy",

        // SchoolOnboardingScreenV2 — Header
        StringKeys.OB_ONBOARDING           to "ONBOARDING",
        StringKeys.OB_STEP_OF              to "Step {step} of {total}",
        StringKeys.OB_BACK                 to "Back",
        StringKeys.OB_CONTINUE             to "Continue",
        StringKeys.OB_FINISH               to "Finish setup",
        StringKeys.OB_SETTING_UP           to "Setting up",
        // Step titles
        StringKeys.OB_T_IDENTITY           to "School identity",
        StringKeys.OB_T_ACADEMIC           to "Academic year",
        StringKeys.OB_T_CLASSES            to "Classes & sections",
        StringKeys.OB_T_SUBJECTS           to "Subjects",
        StringKeys.OB_T_TEACHERS           to "Teachers",
        StringKeys.OB_T_STUDENTS           to "Students",
        // Step 1: Identity
        StringKeys.OB_ID_LEGAL_NAME        to "Full legal name",
        StringKeys.OB_ID_LEGAL_PH          to "Saraswati Vidya Mandir",
        StringKeys.OB_ID_SHORT_NAME        to "Short name",
        StringKeys.OB_ID_SHORT_PH          to "SVM",
        StringKeys.OB_ID_AFFIL             to "Affiliation number",
        StringKeys.OB_ID_AFFIL_PH          to "UP/CBSE/2021/4421",
        StringKeys.OB_ID_BOARD             to "BOARD",
        StringKeys.OB_ID_SCHOOL_TYPE       to "SCHOOL TYPE",
        StringKeys.OB_ID_PRINCIPAL         to "Principal's name",
        StringKeys.OB_ID_PRINCIPAL_PH      to "Dr. Anita Verma",
        StringKeys.OB_ID_PRINCIPAL_MOB     to "Principal's mobile",
        StringKeys.OB_ID_PRINCIPAL_MOB_PH  to "+91 98XXX XXXXX",
        // Step 2: Academic year
        StringKeys.OB_AY_CURRENT           to "CURRENT ACADEMIC YEAR",
        StringKeys.OB_AY_STARTS            to "Year starts",
        StringKeys.OB_AY_ENDS              to "Year ends",
        StringKeys.OB_AY_WORKING_DAYS      to "WORKING DAYS",
        StringKeys.OB_AY_START_TIME        to "Start time",
        StringKeys.OB_AY_END_TIME          to "End time",
        StringKeys.OB_AY_PERIODS           to "Periods per day",
        StringKeys.OB_AY_PERIODS_PH        to "8",
        // Step 3: Classes
        StringKeys.OB_CL_TIP               to "TIP",
        StringKeys.OB_CL_TIP_BODY          to "Pick the sections your school actually runs. Subjects and teachers in the next steps will only show these classes.",
        StringKeys.OB_CL_SECTIONS          to "{count} sections",
        StringKeys.OB_CL_ADD_MANUAL        to "ADD CLASS MANUALLY",
        StringKeys.OB_CL_ADD_PH            to "e.g. Class 11, Nursery, KG",
        StringKeys.OB_CL_ADD_BTN           to "Add",
        // Step 4: Subjects
        StringKeys.OB_SJ_OFFERED           to "SUBJECTS OFFERED",
        StringKeys.OB_SJ_TAP_HINT          to "Tap a subject's class chips to set where it's taught.",
        StringKeys.OB_SJ_APPLY_ALL         to "Apply to all",
        StringKeys.OB_SJ_NO_CLASSES        to "No classes",
        // Step 5: Teachers
        StringKeys.OB_TC_ADD               to "ADD A TEACHER",
        StringKeys.OB_TC_ADD_DESC          to "Enter a work email to create the teacher's login account now — they'll get a one-time password to sign in. Name only? You can add their login later from the dashboard.",
        StringKeys.OB_TC_FULL_NAME         to "Full name",
        StringKeys.OB_TC_FULL_NAME_PH      to "Mrs. Kavita Nair",
        StringKeys.OB_TC_WORK_EMAIL        to "Work email (optional)",
        StringKeys.OB_TC_WORK_EMAIL_PH     to "kavita@svm.edu.in",
        StringKeys.OB_TC_NONE_YET          to "No teachers added yet",
        StringKeys.OB_TC_NONE_DESC         to "Add a teacher above to assign subjects, or continue and do it later.",
        StringKeys.OB_TC_COVERAGE          to "TEACHER COVERAGE",
        StringKeys.OB_TC_COVERAGE_OF       to "{covered} of {total} subject × class slots assigned",
        StringKeys.OB_TC_UNASSIGNED        to "{count} unassigned — keep adding assignments below.",
        StringKeys.OB_TC_SLOTS             to "{count} slots",
        StringKeys.OB_TC_IMPORT_CSV        to "Import roster from CSV",
        // Step 6: Students
        StringKeys.OB_ST_DROP_CSV          to "Drop your students CSV here",
        StringKeys.OB_ST_OR_BROWSE         to "or tap to browse",
        StringKeys.OB_ST_DOWNLOAD          to "Download template",
        StringKeys.OB_ST_NONE_YET          to "No roster imported yet",
        StringKeys.OB_ST_OPTIONAL          to "Optional",
        StringKeys.OB_ST_OPTIONAL_DESC     to "Importing students now is optional — you can finish setup and add students anytime from your dashboard. Validation results will appear here once a CSV is uploaded.",
        // Completion
        StringKeys.OB_CM_ALL_SET           to "You're all set",
        StringKeys.OB_CM_IS_LIVE           to "{school} is live on VidyaPrayag.",
        StringKeys.OB_CM_TEACHER_LOGINS    to "TEACHER LOGINS CREATED",
        StringKeys.OB_CM_SHARE_OTP         to "Share these one-time passwords with your teachers. They'll be asked to set their own password on first sign-in. You won't see these again — reset anytime from the dashboard.",
        StringKeys.OB_CM_PASSWORD          to "Password: ",
        StringKeys.OB_CM_COULDNT_CREATE    to "Couldn't create some logins",
        StringKeys.OB_CM_ADD_LATER         to "{name} ({id}) — {msg}. Add them later from the dashboard.",
        StringKeys.OB_CM_OPEN_DASH         to "Open dashboard",
        StringKeys.OB_CM_EDIT_LATER        to "You can edit any of this later in Settings.",
        StringKeys.OB_CM_READY             to "Your school is ready 🎉",
        StringKeys.OB_CM_PROFILE_DONE      to "Your profile setup is complete. You can now start building your digital campus by adding teachers, students and parents.",
        StringKeys.OB_CM_YOUR_SCHOOL       to "Your school",

        // Phase 2 — Parent Screens (English)
        // ParentAcademicsScreenV2
        StringKeys.PA_APPLY_LEAVE          to "Apply for leave",
        StringKeys.PA_LEAVE_DESC           to "Request leave for your child — routed to their class teacher",
        StringKeys.PA_THIS_TERM            to "This term",
        StringKeys.PA_ATTENDANCE_RATE      to "Attendance rate",
        StringKeys.PA_NO_ATTENDANCE        to "No attendance marked yet",
        StringKeys.PA_NO_ATTENDANCE_DESC   to "Days will fill in below as the school marks attendance.",
        StringKeys.PA_AI_EST               to "AI Est.",
        StringKeys.PA_EST                  to "Est.",
        StringKeys.PA_PENDING              to "Pending",
        StringKeys.PA_AI_SUMMARY           to "AI Summary",
        StringKeys.PA_AI_ESTIMATED         to "AI estimated",
        StringKeys.PA_TYPE_ANSWER          to "Type your answer...",
        StringKeys.PA_SCORE                to "Score: {score} / {total}",
        StringKeys.PA_YOUR_ANSWER          to "Your answer: {answer}",
        StringKeys.PA_CORRECT_ANSWER       to "Correct answer: {answer}",
        StringKeys.PA_LOADING_LEADERBOARD  to "Loading leaderboard...",
        StringKeys.PA_LEADERBOARD          to "Leaderboard",
        StringKeys.PA_PARTICIPANTS         to "{count} participants",
        StringKeys.PA_BACK_TO_QUIZZES      to "Back to Quizzes",
        StringKeys.PA_HEALTH_RECORDS       to "Health Records",
        StringKeys.PA_HEALTH_RECORDS_DESC  to "View health profile, immunizations, and incidents",
        StringKeys.PA_AI_REPORT_CARD       to "AI Report Card",
        StringKeys.PA_AI_REPORT_CARD_DESC  to "Link your child to view their AI-generated report cards.",
        StringKeys.PA_NO_MARKS             to "No published marks yet",
        StringKeys.PA_NO_MARKS_DESC        to "Marks appear here once teachers publish results to parents.",
        StringKeys.PA_NO_SYLLABUS          to "No syllabus shared yet",
        StringKeys.PA_NO_SYLLABUS_DESC     to "A subject-wise coverage log will appear here once the school shares it.",
        StringKeys.PA_NO_PROGRESS          to "No progress data yet",
        StringKeys.PA_NO_PROGRESS_DESC     to "Your child's competencies will appear here as teachers update them.",
        StringKeys.PA_NO_DAILY_LOGS        to "No daily logs yet",
        StringKeys.PA_NO_DAILY_LOGS_DESC   to "Daily class summaries will appear here once teachers start logging.",
        StringKeys.PA_NO_QUIZZES           to "No quizzes yet",
        StringKeys.PA_NO_QUIZZES_DESC      to "Quizzes will appear here once teachers publish them.",
        StringKeys.PA_LEVEL                to "Level {level}",
        StringKeys.PA_PERCENT_COMPLETE     to "{percent}% complete",
        StringKeys.PA_QUIZ_QUESTIONS       to "{subject} · {count} questions",
        StringKeys.PA_START                to "Start",
        StringKeys.PA_QUIZ                 to "Quiz",
        StringKeys.PA_YOU                  to " (You)",
        StringKeys.PA_MATCH                to " (Match)",
        // ParentProfileCardScreenV2
        StringKeys.PC_ATTENDANCE           to "Attendance",
        StringKeys.PC_LATEST_SCORE         to "Latest score",
        StringKeys.PC_TO_NEXT              to "{percent}% to next",
        StringKeys.PC_TOPICS_TODAY         to "Topics today",
        StringKeys.PC_ATTEND               to "ATTEND",
        StringKeys.PC_SCORE                to "SCORE",
        StringKeys.PC_TODAY                to "TODAY",
        StringKeys.PC_TOPIC                to "topic",
        StringKeys.PC_TOPICS               to "topics",
        // ParentProfileScreenV2 + shared
        StringKeys.PP_LOGOUT_TITLE         to "Log out?",
        StringKeys.PP_LOGOUT_MSG           to "You'll need to sign in again to follow your child's progress.",
        StringKeys.PP_LOGOUT_CONFIRM       to "Log out",
        StringKeys.PP_PROFILE              to "Profile",
        StringKeys.PP_PROFILE_UNAVAILABLE  to "Profile unavailable",
        StringKeys.PP_PROFILE_UNAVAILABLE_DESC to "We couldn't load your profile. Please try again.",
        StringKeys.PP_LANGUAGE             to "Language",
        // ParentLibraryScreenV2
        StringKeys.PL_LIBRARY              to "Library",
        StringKeys.PL_BACK                 to "Back",
        StringKeys.PL_BOOKS_FOUND          to "{count} books found",
        StringKeys.PL_RESERVE_BOOK         to "Reserve Book",
        StringKeys.PL_RESERVE_MSG          to "You'll be notified when this book becomes available.",
        StringKeys.PL_RESERVE              to "Reserve",
        StringKeys.PL_MY_CHILD_BOOKS       to "My Child's Books",
        StringKeys.PL_MY_CHILD_BOOKS_DESC  to "Currently issued books for your child.",
        StringKeys.PL_NO_BOOKS_ISSUED      to "No books issued",
        StringKeys.PL_NO_BOOKS_ISSUED_DESC to "Your child has no books currently issued.",
        StringKeys.PL_ISSUED               to "Issued: {date}",
        StringKeys.PL_RENEWALS             to "{count} renewal(s)",
        StringKeys.PL_RESERVATIONS         to "Reservations",
        StringKeys.PL_RESERVATIONS_DESC    to "Books you've reserved. You'll be notified when available.",
        StringKeys.PL_NO_RESERVATIONS      to "No reservations",
        StringKeys.PL_NO_RESERVATIONS_DESC to "Reserve a book from the Browse tab to see it here.",
        StringKeys.PL_RESERVED_ON          to "Reserved on: {date}",
        StringKeys.PL_CANCEL_RESERVATION   to "Cancel Reservation",
        StringKeys.PL_CANCEL_RESERVATION_MSG to "Are you sure you want to cancel this reservation?",
        StringKeys.PL_CANCEL_RESERVATION_CONFIRM to "Cancel Reservation",
        StringKeys.PL_KEEP                 to "Keep",
        // ParentReportScreen
        StringKeys.PR_AI_REPORT_CARD       to "AI Report Card",
        StringKeys.PR_NO_REPORTS           to "No published reports yet",
        StringKeys.PR_NO_REPORTS_DESC      to "Reports will appear here once published by the school.",
        StringKeys.PR_CONFERENCE_PACK      to "Conference Pack",
        StringKeys.PR_SUMMARY              to "Summary",
        StringKeys.PR_FOCUS_AREAS          to "Focus Areas",
        StringKeys.PR_STRENGTHS            to "Strengths",
        StringKeys.PR_CONFERENCE_TIPS      to "Conference Tips",
        StringKeys.PR_PUBLISHED            to "Published",
        StringKeys.PR_PUBLISHED_ON         to "Published: {date}",
        // ScholarshipWorkflowScreenV2
        StringKeys.SW_PROFILE_STRENGTH     to "Profile Strength",
        StringKeys.SW_ELIGIBILITY          to "Eligibility: ",
        StringKeys.SW_AWARD                to "Award",
        StringKeys.SW_APPLY_BY             to "Apply by",
        StringKeys.SW_REMARKS              to "Remarks: {remarks}",
        StringKeys.SW_DISBURSED            to "Disbursed: ₹{amount}",
        StringKeys.SW_REF                  to "Ref: {ref}",
        StringKeys.SW_APPLY_FOR_SCHOLARSHIP to "Apply for Scholarship",
        StringKeys.SW_CHILD_ID             to "Child ID *",
        StringKeys.SW_DOCUMENTS            to "Documents (URLs)",
        // ScholarshipsScreenV2
        StringKeys.SL_PROFILE_STRENGTH     to "Profile strength",
        StringKeys.SL_AWARD                to "Award",
        StringKeys.SL_CLOSES_IN            to "Closes in",
        // ParentHealthScreenV2
        StringKeys.PHS_NO_PROFILE          to "No health profile linked yet",
        StringKeys.PHS_NO_PROFILE_DESC     to "Once the school adds health records for your child, they will appear here.",
        StringKeys.PHS_DOSE                to "Dose {number} · {date}",
        StringKeys.PHS_BY                  to "By {name}",
        StringKeys.PHS_NEXT_DUE            to "Next due: {date}",
        StringKeys.PHS_TREATMENT           to "Treatment: {treatment}",
        StringKeys.PHS_MEDICATION          to "Medication: {medication}",
        StringKeys.PHS_TIME                to "Time: {time}",
        StringKeys.PHS_PARENT_NOTIFIED     to "Parent notified",
        // ParentHomeScreenV2
        StringKeys.PH_STAY_INFORMED        to "Stay Informed",
        StringKeys.PH_STAY_INFORMED_MSG    to "Enable notifications to receive important updates about school events, attendance, and fee reminders.",
        StringKeys.PH_ENABLE               to "Enable",
        StringKeys.PH_NOT_NOW              to "Not Now",
        StringKeys.PH_NO_CHILD_LINKED      to "No child linked yet",
        StringKeys.PH_NO_CHILD_LINKED_DESC to "Link your child to see their daily journey and progress.",
        StringKeys.PH_TRACK_BUS            to "Track Bus",
        StringKeys.PH_TRACK_BUS_DESC       to "Live bus location & ETA for your child",
        StringKeys.PH_SCHOLARSHIPS         to "Scholarships",
        StringKeys.PH_SCHOLARSHIPS_DESC    to "Browse & apply for scholarship opportunities",
        StringKeys.PH_DIGITAL_ID           to "Digital ID Card",
        StringKeys.PH_DIGITAL_ID_DESC      to "View your child's digital school ID card",
        StringKeys.PH_LIBRARY              to "Library",
        StringKeys.PH_LIBRARY_DESC         to "Search books, view issued books & reserve",
        StringKeys.PH_SCHOOL_EVENTS        to "School Events",
        StringKeys.PH_SCHOOL_EVENTS_DESC   to "Register for PTM, events & book time slots",
        // ParentScheduleCard
        StringKeys.PS_TODAY_SCHEDULE       to "Today's schedule",
        StringKeys.PS_TODAY_BADGE          to "Today",
        StringKeys.PS_WEEKLY_TIMETABLE     to "Weekly timetable",
        StringKeys.PS_NO_CLASSES           to "No classes",
        // ParentFeesScreenV2
        StringKeys.PF_FEES                 to "Fees",
        StringKeys.PF_PAY_NOW              to "Pay now",
        StringKeys.PF_COMING_SOON          to " · Coming Soon",
        // ParentEventRegistrationScreenV2
        StringKeys.PE_CANCEL_REGISTRATION  to "Cancel Registration",
        StringKeys.PE_CANCEL_REGISTRATION_MSG to "Are you sure you want to cancel your registration for {title}?",
        StringKeys.PE_YES_CANCEL           to "Yes, Cancel",
        // ParentLeaveScreenV2
        StringKeys.PLV_LEAVE               to "Leave",
        StringKeys.PLV_APPLY_FOR_LEAVE     to "APPLY FOR LEAVE",
        StringKeys.PLV_MY_REQUESTS         to "MY REQUESTS",
        StringKeys.PLV_NO_REQUESTS         to "No leave requests",
        StringKeys.PLV_NO_REQUESTS_DESC    to "Requests you submit will appear here with their status.",
        StringKeys.PLV_FROM                to "From",
        StringKeys.PLV_START_DATE          to "Start date",
        StringKeys.PLV_TO                  to "To",
        StringKeys.PLV_END_DATE            to "End date",
        StringKeys.PLV_REASON              to "Reason",
        StringKeys.PLV_REASON_PH           to "e.g. Fever / family event",
        // ParentMessagesScreenV2
        StringKeys.PM_NEW_MESSAGE          to "New message",
        StringKeys.PM_NO_MESSAGES          to "No messages yet",
        StringKeys.PM_NO_MESSAGES_DESC     to "Messages from your child's teachers and the school office will appear here.",
        StringKeys.PM_NO_ONE_TO_MESSAGE    to "No one to message yet",
        StringKeys.PM_NO_ONE_TO_MESSAGE_DESC to "Link your child to a school to message their teachers and the office.",
        StringKeys.PM_START_CONVERSATION   to "Send a message below to start the conversation.",
        // ParentPulseScreen
        StringKeys.PPS_PARENT_PULSE        to "Parent Pulse",
        StringKeys.PPS_NO_PULSE            to "No pulse yet",
        StringKeys.PPS_NO_PULSE_DESC       to "Check back after Sunday for the weekly summary.",
        StringKeys.PPS_NO_HISTORY          to "No history yet",
        StringKeys.PPS_NO_HISTORY_DESC     to "Pulse history will appear here after a few weeks.",
        StringKeys.PPS_NO_PULSE_AVAILABLE  to "No pulse available",
        StringKeys.PPS_NO_PULSE_AVAILABLE_DESC to "Your child's weekly pulse will appear here every Sunday evening.",
        StringKeys.PPS_CLOSE               to "Close",
        StringKeys.PPS_HISTORY             to "History",
        StringKeys.PPS_VIEW_HISTORY        to "View 12-week history",
        // ParentUnlinkedScreenV2
        StringKeys.PU_LINK_CHILD           to "Link a child",
        StringKeys.PU_EXPLORE_SCHOOLS      to "Explore schools",
        StringKeys.PU_WELCOME              to "Welcome to VidyaPrayag",
        StringKeys.PU_LINK_TITLE           to "Follow your child's journey",
        StringKeys.PU_EXPLORE_TITLE        to "Find the right school",
        StringKeys.PU_LINK_DESC            to "Link your child to their school to see attendance, marks and more.",
        StringKeys.PU_EXPLORE_DESC         to "Browse schools on VidyaPrayag, compare them, and enquire.",
        // ParentConversationsScreenV2
        StringKeys.PCV_MESSAGES            to "Messages",
        StringKeys.PCV_ANNOUNCEMENTS       to "Announcements",
        StringKeys.PCV_CONVERSATIONS        to "Conversations",
        // ParentActivityScreenV2
        StringKeys.PAC_ACTIVITY            to "Activity",
        // ParentPewsScreenV2
        StringKeys.PPEWS_ATTENDANCE        to "Attendance",
        // ParentCoveredDetailOverlay
        StringKeys.PCD_TODAYS_TOPICS       to "Today's topics",
        StringKeys.PCD_NO_TOPICS           to "No topics logged today yet",
        StringKeys.PCD_SYLLABUS_COVERAGE   to "Syllabus coverage",
        // ParentResultsFeesCards
        StringKeys.PRF_PUBLISHED            to "Published",
        // AiReportCardPreview
        StringKeys.AIP_AI_NARRATIVE        to "AI NARRATIVE",
        // BusTrackingScreenV2
        StringKeys.BT_BUS_TRACKING          to "Bus Tracking",
        StringKeys.BT_NO_TRANSPORT          to "No transport assignment found",
        StringKeys.BT_NO_TRANSPORT_DESC     to "This child is not assigned to any bus route yet.",
        StringKeys.BT_WAITING               to "Waiting for bus location…",
        StringKeys.BT_ROUTE                 to "Route",
        StringKeys.BT_BUS                   to "Bus: {bus}",
        StringKeys.BT_ETA                   to "ETA {eta} min",
        StringKeys.BT_NEXT_STOP             to "Next stop: {stop}",
        // DigitalIdCardScreen
        StringKeys.DID_DIGITAL_ID_CARD      to "Digital ID Card",
        StringKeys.DID_SHOW_BACK            to "Show Back",
        StringKeys.DID_SHOW_FRONT           to "Show Front",
        StringKeys.DID_SCAN_QR_BACK         to "Scan the QR code on the back to verify profile",
        StringKeys.DID_VALID_TILL           to "Valid till: {date}",
        StringKeys.DID_LOADING              to "Loading ID card...",
        StringKeys.DID_NO_ID_CARD           to "No ID card found. Ask admin to generate.",
        StringKeys.DID_QR_CODE              to "QR Code",
        StringKeys.DID_SCAN_VERIFY          to "Scan to verify profile",
        // ParentPortalV2
        StringKeys.PPRT_HOME                to "Home",
        StringKeys.PPRT_ACADEMICS           to "Academics",
        StringKeys.PPRT_FEES                to "Fees",
        StringKeys.PPRT_CONVERSATIONS       to "Conversations",
        StringKeys.PPRT_PROFILE             to "Profile",
        StringKeys.PPRT_LEVEL_JOURNEY       to "Level {level} · {percent}% journey",
        StringKeys.PPRT_LEVEL               to "Level {level}",
        StringKeys.PPRT_YOUR_CHILD          to "Your child",
        StringKeys.PPRT_SWITCH_CHILD        to "Switch child",
        // PulseCard
        StringKeys.PUL_HW                  to "HW",
        StringKeys.PUL_MSGS                to "Msgs",
        StringKeys.PUL_ALERTS              to "Alerts",
        StringKeys.PUL_WEEKLY_PULSE        to "WEEKLY PULSE",
        StringKeys.PUL_ATTENDANCE          to "Attendance",
        StringKeys.PUL_MARKS_THIS_WEEK     to "Marks This Week",
        StringKeys.PUL_ACTION_ITEMS        to "Action Items",
        StringKeys.PUL_UPCOMING            to "Upcoming",
        // ParentLeaveScreenV2 (extras)
        StringKeys.PLV_SUBMIT_REQUEST      to "Submit request",
        StringKeys.PLV_CHILD               to "Child",
        // ParentMessagesScreenV2 (extras)
        StringKeys.PM_MESSAGES             to "Messages",
        StringKeys.PM_CONVERSATION         to "Conversation",
        StringKeys.PM_SELECT_RECIPIENT     to "Select recipient",
        StringKeys.PM_PICK_RECIPIENT_PH    to "Pick a recipient above…",
        StringKeys.PM_MESSAGE_NAME_PH      to "Message {name}…",
        StringKeys.PM_TYPE_MESSAGE_PH      to "Type a message…",
        StringKeys.PM_MESSAGE_DELETED      to "This message was deleted",
        StringKeys.PM_EDITED               to "edited",
        // ParentAttendanceCard
        StringKeys.PATT_ATTENDANCE_TODAY    to "ATTENDANCE · TODAY",
        StringKeys.PATT_THIS_MONTH          to "This month",
        StringKeys.PATT_PERCENT_PRESENT     to "{rate}% present",
        StringKeys.PATT_TRACKING_FROM_TODAY to "Tracking from today",
        StringKeys.PATT_MONTH_FILLS         to "The month fills in as the class is marked",
        StringKeys.PATT_SWIPE_CALENDAR      to "Swipe for the month calendar",
        StringKeys.PATT_PRESENT             to "Present",
        StringKeys.PATT_LATE                to "Late",
        StringKeys.PATT_ABSENT              to "Absent",
        StringKeys.PATT_HOLIDAY             to "Holiday",
        StringKeys.PATT_BREAK               to "Break",
        StringKeys.PATT_SUNDAY              to "Sunday",
        StringKeys.PATT_AWAITING            to "Awaiting",
        StringKeys.PATT_MARKED_PRESENT      to "Marked present today",
        StringKeys.PATT_IN_SCHOOL           to "Your child is in school",
        StringKeys.PATT_ARRIVED_LATE        to "Arrived late today",
        StringKeys.PATT_MARKED_PRESENT_LATE to "Marked present, after the bell",
        StringKeys.PATT_MARKED_ABSENT       to "Marked absent today",
        StringKeys.PATT_NO_ATTENDANCE_TODAY to "No attendance recorded for today",
        StringKeys.PATT_SCHOOL_HOLIDAY       to "School holiday today",
        StringKeys.PATT_ENJOY_DAY_OFF       to "Enjoy the day off",
        StringKeys.PATT_ON_VACATION         to "On vacation",
        StringKeys.PATT_ENJOY_BREAK         to "Enjoy the break",
        StringKeys.PATT_NO_SCHOOL           to "No school today",
        StringKeys.PATT_SUNDAY_DESC         to "It's a Sunday",
        StringKeys.PATT_NOT_MARKED_YET      to "Attendance not marked yet",
        StringKeys.PATT_WAITING_CLASS       to "You'll see today's status once the class is marked",
        StringKeys.PATT_SCHOOL_DAYS         to "{attended} of {total} school days",
        StringKeys.PATT_LATE_DAYS           to "{count} late",
        StringKeys.PATT_ABSENT_DAYS         to "{count} absent",
        // ParentAttendanceCalendar
        StringKeys.PACL_LEGEND              to "Legend",
        StringKeys.PACL_PRESENT             to "Present",
        StringKeys.PACL_LATE                to "Late",
        StringKeys.PACL_ABSENT              to "Absent",
        // ParentCoveredCard
        StringKeys.PCC_COVERED_SUMMARY      to "COVERED TODAY · SUMMARY",
        StringKeys.PCC_COVERED_LIVE         to "COVERED TODAY · LIVE",
        StringKeys.PCC_NOTHING_LOGGED       to "Nothing logged today",
        StringKeys.PCC_NOTHING_COVERED      to "Nothing covered yet today",
        StringKeys.PCC_NOTHING_LOGGED_DESC  to "Teachers didn't log syllabus coverage today",
        StringKeys.PCC_FILLS_LIVE           to "This fills in live as the school day progresses",
        StringKeys.PCC_TOPICS_ACROSS        to "{count} {topic} across {subjectCount} {subject}",
        StringKeys.PCC_MORE                 to "+{count} more",
        StringKeys.PCC_TAP_BREAKDOWN        to "Tap for the full breakdown",
        // ParentNudgeCard
        StringKeys.PNC_GOT_IT               to "Got it",
        StringKeys.PNC_HEADLINE_FALLBACK    to "A little support for {name}",
        // ScholarshipWorkflowScreenV2 (extras)
        StringKeys.SW_SCHOLARSHIPS          to "Scholarships",
        StringKeys.SW_NO_SCHOLARSHIPS       to "No scholarships yet",
        StringKeys.SW_NO_SCHOLARSHIPS_DESC  to "Scholarship opportunities will appear here as your school publishes them.",
        StringKeys.SW_AVAILABLE             to "AVAILABLE SCHOLARSHIPS ({count})",
        StringKeys.SW_MY_APPLICATIONS       to "MY APPLICATIONS ({count})",
        // ParentLibraryScreenV2 (extras)
        StringKeys.PL_NO_BOOKS_FOUND        to "No books found",
        StringKeys.PL_NO_BOOKS_FOUND_DESC   to "Try a different search query.",
        StringKeys.PL_FINE                  to "Fine: ₹{amount} ({status})",
        // ParentHealthScreenV2 (extras)
        StringKeys.PHS_YOUR_CHILD           to "Your child",
        StringKeys.PHS_IMMUNIZATIONS        to "Immunizations",
        StringKeys.PHS_HEALTH_INCIDENTS     to "Health Incidents",
        StringKeys.PHS_HEALTH_PROFILE       to "Health Profile",
        StringKeys.PHS_BLOOD_GROUP          to "Blood Group",
        StringKeys.PHS_HEIGHT               to "Height",
        StringKeys.PHS_WEIGHT               to "Weight",
        StringKeys.PHS_HEIGHT_VALUE         to "{value} cm",
        StringKeys.PHS_WEIGHT_VALUE         to "{value} kg",
        StringKeys.PHS_ALLERGIES            to "Allergies",
        StringKeys.PHS_CHRONIC_CONDITIONS   to "Chronic Conditions",
        StringKeys.PHS_MEDICATIONS          to "Medications",
        StringKeys.PHS_EMERGENCY_CONTACT    to "Emergency Contact",
        StringKeys.PHS_NAME                 to "Name",
        StringKeys.PHS_PHONE                to "Phone",
        StringKeys.PHS_DOCTOR               to "Doctor",
        // ParentReportScreen (extras)
        StringKeys.PR_CONFERENCE_SUBTITLE   to "{studentName} — {className} {section} • {term}",
        StringKeys.PR_OVERALL               to "Overall",
        StringKeys.PR_GRADE                 to "Grade",
        StringKeys.PR_ATTENDANCE            to "Attendance",
        // ScholarshipWorkflowScreenV2 (extras 2)
        StringKeys.SW_LEVEL                 to "LVL {level}",
        StringKeys.SW_APPLICATIONS          to "Applications",
        StringKeys.SW_APPROVED              to "Approved",
        StringKeys.SW_AWARDED               to "Awarded",
        StringKeys.SW_DAY_STREAK            to "Day Streak",
        StringKeys.SW_HOT                   to "HOT",
        StringKeys.SW_RENEWABLE             to "Renewable",
        StringKeys.SW_APPLY_NOW             to "Apply Now",
        StringKeys.SW_STUDENT               to "Student",
        StringKeys.SW_DOCUMENT_URL          to "Document URL",
        StringKeys.SW_ADD                   to "Add",
        StringKeys.SW_APPLICATION_TEXT      to "Application Text (optional)",
        StringKeys.SW_CANCEL                to "Cancel",
        StringKeys.SW_SUBMIT                to "Submit",
        // ParentLibraryScreenV2 (extras 2)
        StringKeys.PL_TAB_BROWSE            to "Browse",
        StringKeys.PL_TAB_MY_BOOKS          to "My Books",
        StringKeys.PL_TAB_RESERVATIONS      to "Reservations",
        StringKeys.PL_VIEWING_FOR           to "Viewing books for {name}",
        StringKeys.PL_PARENT                to "Parent",
        StringKeys.PL_SEARCH_PH             to "Search by title, author, or ISBN",
        // ParentHealthScreenV2 (extras 2)
        StringKeys.PHS_SEVERITY_MAJOR       to "MAJOR",
        StringKeys.PHS_SEVERITY_MODERATE    to "MODERATE",
        StringKeys.PHS_SEVERITY_MINOR       to "MINOR",
        // ── Phase 3: School/Admin screens ──
        // AnalyticsDashboardScreenV2
        StringKeys.SCH_ANALYTICS            to "Analytics",
        StringKeys.SCH_NO_ANALYTICS         to "No analytics yet",
        StringKeys.SCH_NO_ANALYTICS_DESC    to "The overview will populate once the analytics rollup endpoint has data.",
        StringKeys.SCH_PERFORMANCE_TREND    to "Performance trend",
        StringKeys.SCH_OVERVIEW             to "OVERVIEW",
        StringKeys.SCH_INSIGHTS             to "INSIGHTS",
        // StaffProfileScreenV2
        StringKeys.SCH_STAFF                to "Staff",
        StringKeys.SCH_NO_PROFILE           to "No profile",
        StringKeys.SCH_NO_PROFILE_DESC      to "This staff member's record could not be found.",
        StringKeys.SCH_CONTACT              to "CONTACT",
        StringKeys.SCH_NO_CONTACT_DETAILS   to "No contact details on file.",
        StringKeys.SCH_REMOVE_FROM_SCHOOL   to "Remove from school",
        StringKeys.SCH_REMOVE_STAFF_MEMBER  to "Remove staff member",
        StringKeys.SCH_REMOVE_STAFF_CONFIRM to "Remove {name} from your school? Their record will be hidden. This can be reversed by re-adding them.",
        StringKeys.SCH_REMOVE               to "Remove",
        // DailyAttendanceScreenV2
        StringKeys.SCH_DAILY_ATTENDANCE     to "Daily Attendance",
        StringKeys.SCH_STUDENTS             to "Students",
        StringKeys.SCH_FACULTY              to "Faculty",
        StringKeys.SCH_NO_ROSTER            to "No roster",
        StringKeys.SCH_NO_STUDENTS_IN_CLASS to "There are no students in {className} yet.",
        StringKeys.SCH_NO_FACULTY_ROSTER    to "No faculty roster is available.",
        StringKeys.SCH_PRESENT_TODAY        to "Present today",
        StringKeys.SCH_STUDENTS_HEADER      to "STUDENTS",
        StringKeys.SCH_FACULTY_HEADER       to "FACULTY",
        // PewsEffectivenessScreenV2
        StringKeys.SCH_EFFECTIVENESS        to "Effectiveness",
        StringKeys.SCH_NO_DATA_YET          to "No data yet",
        StringKeys.SCH_EFFECTIVENESS_DESC   to "Effectiveness data appears after the first PEWS run with interventions.",
        StringKeys.SCH_INTERVENTION_OUTCOMES to "INTERVENTION OUTCOMES",
        StringKeys.SCH_OPEN                 to "Open",
        StringKeys.SCH_RESOLVED             to "Resolved",
        StringKeys.SCH_IMPROVED             to "Improved",
        StringKeys.SCH_NO_CHANGE            to "No change",
        StringKeys.SCH_WORSENED             to "Worsened",
        StringKeys.SCH_RISK_TREND_30        to "RISK TREND (30 DAYS)",
        StringKeys.SCH_HIGH                 to "High",
        StringKeys.SCH_MEDIUM               to "Medium",
        StringKeys.SCH_WATCH                to "Watch",
        // ResultsPublishScreenV2
        StringKeys.SCH_RESULTS              to "Results",
        StringKeys.SCH_TESTS                to "TESTS",
        StringKeys.SCH_CLASSES              to "CLASSES",
        StringKeys.SCH_SUBJECTS             to "SUBJECTS",
        StringKeys.SCH_NO_RESULTS_YET       to "No results yet",
        StringKeys.SCH_NO_RESULTS_DESC      to "Pick a test/class/subject above. Once teachers enter marks, the class summary and students will appear here.",
        StringKeys.SCH_CLASS_AVERAGE        to "Class average",
        StringKeys.SCH_EXCEEDING            to "Exceeding",
        StringKeys.SCH_MEETING              to "Meeting",
        StringKeys.SCH_BELOW                to "Below",
        StringKeys.SCH_SCORE_ATTENDANCE     to "Score {score} · Attendance {attendance}",
        // SchedulePtmScreenV2
        StringKeys.SCH_SCHEDULE_PTM         to "Schedule PTM",
        StringKeys.SCH_NO_PTMS_YET          to "No PTMs yet",
        StringKeys.SCH_NO_PTMS_DESC         to "Schedule your first parent-teacher meeting to get started.",
        StringKeys.SCH_NEW_PTM              to "New PTM",
        StringKeys.SCH_TITLE                to "Title",
        StringKeys.SCH_TITLE_PH             to "e.g. Term 1 PTM",
        StringKeys.SCH_DATE                 to "Date",
        StringKeys.SCH_PTM_DATE_PH          to "Select PTM date",
        StringKeys.SCH_SLOT                 to "Slot",
        StringKeys.SCH_SLOT_PH              to "10:00 AM - 1:00 PM",
        StringKeys.SCH_CREATE               to "Create",
        StringKeys.SCH_SCHEDULE_NEW_PTM     to "Schedule new PTM",
        StringKeys.SCH_ACTIVE               to "ACTIVE",
        StringKeys.SCH_EXPECTED             to "Expected",
        StringKeys.SCH_CHECKED_IN           to "Checked-in",
        StringKeys.SCH_INVITES_SENT         to "Invites sent",
        StringKeys.SCH_READ                 to "Read",
        StringKeys.SCH_HISTORY              to "HISTORY",
        StringKeys.SCH_CLASS_PROGRESS       to "CLASS PROGRESS",
        // PewsCohortScreenV2
        StringKeys.SCH_EARLY_WARNING        to "Early Warning",
        StringKeys.SCH_RECOMPUTE            to "Recompute",
        StringKeys.SCH_NO_STUDENTS_ATTENTION to "No students need attention",
        StringKeys.SCH_NO_STUDENTS_ATTENTION_DESC to "Every student in the selected band is on track right now. Tap Recompute to refresh, or widen the band filter.",
        StringKeys.SCH_EFFECTIVENESS_HEADER to "EFFECTIVENESS",
        StringKeys.SCH_EFFECTIVENESS_LOOP_DESC to "What the intervention loop is achieving",
        StringKeys.SCH_CONFIGURATION        to "CONFIGURATION",
        StringKeys.SCH_CONFIGURATION_DESC   to "Thresholds, run frequency & what's shared",
        StringKeys.SCH_RELATIVE_THRESHOLDS  to "Relative thresholds",
        StringKeys.SCH_RELATIVE_THRESHOLDS_HINT to "Use z-scores across the cohort rather than fixed floors",
        StringKeys.SCH_AI_NARRATIVE         to "AI narrative",
        StringKeys.SCH_AI_NARRATIVE_HINT    to "Let the AI write a plain-language explanation of the signals",
        StringKeys.SCH_SHARE_WITH_PARENTS   to "Share with parents",
        StringKeys.SCH_SHARE_WITH_PARENTS_HINT to "When on, parents see a gentle, label-free nudge for their child",
        StringKeys.SCH_RUN_FREQUENCY        to "Run frequency",
        StringKeys.SCH_DAILY                to "Daily",
        StringKeys.SCH_WEEKLY               to "Weekly",
        StringKeys.SCH_RISK_BAND            to "RISK BAND",
        StringKeys.SCH_AS_OF                to "As of {date}",
        StringKeys.SCH_ALL                  to "All",
        StringKeys.SCH_MEDIUM_PLUS          to "Medium+",
        StringKeys.SCH_HIGH_ONLY            to "High only",
        StringKeys.SCH_AI_DISABLED_NOTE     to "AI explanations are off. Rows still show the real attendance, marks and leave signals.",
        StringKeys.SCH_ALL_ON_TRACK_NOTE    to "No students need attention in this band right now. Settings below still apply.",
        StringKeys.SCH_QUEUED               to "Queued…",
        StringKeys.SCH_RUNNING              to "Running…",
        StringKeys.SCH_COMPLETE             to "Complete",
        StringKeys.SCH_FAILED               to "Failed",
        StringKeys.SCH_REFRESH              to "Refresh",
        StringKeys.SCH_RISK_TREND           to "RISK TREND",
        StringKeys.SCH_RISK_TREND_DESC      to "Cohort risk distribution (last 30 days)",
        StringKeys.SCH_CLASS_SECTION        to "Class {className}{section}",
        // TeacherAssignmentManagementScreen
        StringKeys.SCH_ASSIGN_CLASSES       to "Assign Classes",
        StringKeys.SCH_NO_TEACHER           to "No teacher",
        StringKeys.SCH_NO_TEACHER_DESC      to "This teacher's record could not be found.",
        StringKeys.SCH_REMOVE_ASSIGNMENT    to "Remove assignment",
        StringKeys.SCH_REMOVE_ASSIGNMENT_DESC to "Remove this class/subject assignment from the teacher? This can be re-added at any time.",
        StringKeys.SCH_SUBJECT_TEACHER      to "{subject} Teacher",
        StringKeys.SCH_TEACHER              to "Teacher",
        StringKeys.SCH_COUNT_CLASSES        to "{count} Classes",
        StringKeys.SCH_COUNT_SUBJECTS       to "{count} Subjects",
        StringKeys.SCH_CLASSES_ASSIGNED     to "Classes Assigned",
        StringKeys.SCH_ACTIVE_KPI           to "active",
        StringKeys.SCH_SUBJECTS_ASSIGNED    to "Subjects Assigned",
        StringKeys.SCH_COVERED              to "covered",
        StringKeys.SCH_TOTAL_STUDENTS       to "Total Students",
        StringKeys.SCH_TAUGHT               to "taught",
        StringKeys.SCH_SECTIONS_COVERED     to "Sections Covered",
        StringKeys.SCH_ACROSS_CLASSES       to "across classes",
        StringKeys.SCH_ASSIGNMENT_SUMMARY   to "ASSIGNMENT SUMMARY",
        StringKeys.SCH_CURRENT_ASSIGNMENTS  to "CURRENT ASSIGNMENTS",
        StringKeys.SCH_NO_CLASSES_ASSIGNED  to "No classes assigned yet. Use the builder below to add some.",
        StringKeys.SCH_CLASS_SECTION_LABEL  to "{className} · Section {section}",
        StringKeys.SCH_COUNT_STUDENTS       to "{count} students",
        StringKeys.SCH_ADD_ASSIGNMENT       to "ADD ASSIGNMENT",
        StringKeys.SCH_LOADING_OPTIONS      to "Loading class & subject options…",
        StringKeys.SCH_NO_CLASSES_SUBJECTS  to "No classes or subjects have been set up for this school yet.",
        StringKeys.SCH_STEP_1_SUBJECT       to "Step 1 · Select subject",
        StringKeys.SCH_STEP_2_CLASSES       to "Step 2 · Select classes",
        StringKeys.SCH_STEP_3_SECTIONS      to "Step 3 · Select sections",
        StringKeys.SCH_PICK_CLASSES_FIRST   to "Pick one or more classes first — their sections appear here. (Leaving this empty assigns ALL sections of the chosen classes.)",
        StringKeys.SCH_LEAVE_UNSELECTED     to "Leave all unselected to assign every section of the chosen classes.",
        StringKeys.SCH_STEP_4_PREVIEW       to "Step 4 · Preview",
        StringKeys.SCH_CLEAR                to "Clear",
        StringKeys.SCH_SAVE_ASSIGNMENTS     to "Save assignments",
        StringKeys.SCH_WORKLOAD_INSIGHTS    to "WORKLOAD INSIGHTS",
        StringKeys.SCH_NO_WORKLOAD_INSIGHTS to "No workload insights yet.",
        StringKeys.SCH_ASSIGNMENT_DISTRIBUTION to "ASSIGNMENT DISTRIBUTION",
        StringKeys.SCH_CLS_STU              to "{classCount} cls · {studentCount} stu",
        // SchoolCommsScreenV2
        StringKeys.SCH_COMMUNICATIONS       to "Communications",
        StringKeys.SCH_ANNOUNCEMENTS        to "Announcements",
        StringKeys.SCH_MESSAGES             to "Messages",
        StringKeys.SCH_PTM                  to "PTM",
        StringKeys.SCH_NOTIFICATIONS        to "Notifications",
        StringKeys.SCH_PARENT_MESSAGES      to "Parent messages",
        StringKeys.SCH_PARENT_MESSAGES_DESC to "Open two-way parent ↔ school message threads.",
        StringKeys.SCH_PARENT_TEACHER_MEETINGS to "Parent–Teacher meetings",
        StringKeys.SCH_PARENT_TEACHER_MEETINGS_DESC to "Schedule PTMs and track slot bookings.",
        StringKeys.SCH_DELIVERY_LOG         to "Delivery log",
        StringKeys.SCH_DELIVERY_LOG_DESC    to "Push/SMS/WhatsApp delivery receipts surface here when the notifications service ships.",
        StringKeys.SCH_SCHEDULED            to "Scheduled",
        StringKeys.SCH_NEW                  to "New",
        StringKeys.SCH_NO_ANNOUNCEMENTS     to "No announcements yet",
        StringKeys.SCH_NO_ANNOUNCEMENTS_DESC to "Posts you publish to parents and staff will appear here.",
        StringKeys.SCH_CALENDAR_ONLY        to "Calendar Only",
        StringKeys.SCH_ANNOUNCEMENT         to "Announcement",
        StringKeys.SCH_ANNOUNCEMENT_UNAVAILABLE to "Announcement unavailable",
        StringKeys.SCH_POSTED_BY            to "{date} • Posted by School Administration",
        StringKeys.SCH_NO_MESSAGES          to "No messages yet",
        StringKeys.SCH_NO_MESSAGES_DESC     to "Start a conversation with parents or staff to see threads here.",
        StringKeys.SCH_SEE_ALL_MESSAGES     to "See all messages",
        StringKeys.SCH_SEE_ALL_MESSAGES_DESC to "Open the full parent messages inbox.",
        StringKeys.SCH_SEE_ALL_PTM          to "See all PTMs",
        StringKeys.SCH_SEE_ALL_PTM_DESC     to "Open the full parent-teacher meeting history.",
        StringKeys.SCH_NO_DELIVERY_LOG      to "No delivery records yet",
        StringKeys.SCH_NO_DELIVERY_LOG_DESC to "Delivery receipts for WhatsApp, push, SMS, and email will appear here.",
        StringKeys.SCH_SEE_ALL_DELIVERY_LOG to "See all delivery records",
        StringKeys.SCH_SEE_ALL_DELIVERY_LOG_DESC to "Open the full announcement delivery log.",
        // ClassPerformanceScreenV2
        StringKeys.SCH_CLASS_PERFORMANCE    to "Class Performance",
        StringKeys.SCH_CLASS_PERFORMANCE_DESC to "Class-level analytics will appear here once teachers post marks and attendance.",
        StringKeys.SCH_AVG_PROFICIENCY      to "Avg proficiency",
        StringKeys.SCH_ACTIVE_STUDENTS      to "Active students",
        StringKeys.SCH_MEDIAN_GRADE         to "Median grade",
        StringKeys.SCH_GRADE_DISTRIBUTION   to "GRADE DISTRIBUTION",
        StringKeys.SCH_SUBJECT_MATRIX       to "SUBJECT MATRIX",
        StringKeys.SCH_EARLY_WARNING_HEADER to "EARLY WARNING",
        StringKeys.SCH_CRITICAL             to "Critical",
        StringKeys.SCH_MODERATE             to "Moderate",
        StringKeys.SCH_ON_TARGET            to "On target",
        StringKeys.SCH_TOP_PERFORMER        to "TOP PERFORMER",
        StringKeys.SCH_STAR_1ST             to "★ 1ST",
        StringKeys.SCH_PROGRESS_MONITORING  to "PROGRESS MONITORING",
        StringKeys.SCH_TREND_UP             to "▲ Up",
        StringKeys.SCH_TREND_DOWN           to "▼ Down",
        StringKeys.SCH_TREND_FLAT           to "● Flat",
        StringKeys.SCH_PROGRESS_SCORES      to "Math {math} · Sci {science} · Lit {literature}",
        StringKeys.SCH_PROGRESS_ATTENDANCE  to "Attendance {attendance}",
        // AcademicYearManagementScreenV2
        StringKeys.SCH_ACADEMIC_YEAR        to "Academic Year",
        StringKeys.SCH_CLOSE                to "Close",
        StringKeys.SCH_NO_ACADEMIC_YEARS    to "No academic years yet",
        StringKeys.SCH_NO_ACADEMIC_YEARS_DESC to "Create your first academic year to anchor the calendar.",
        StringKeys.SCH_CREATE_ACADEMIC_YEAR to "Create academic year",
        StringKeys.SCH_NAME                 to "Name",
        StringKeys.SCH_YEAR_NAME_PH         to "e.g. 2026-27",
        StringKeys.SCH_START_DATE           to "Start date",
        StringKeys.SCH_END_DATE             to "End date",
        StringKeys.SCH_SAVE_DRAFT           to "Save Draft",
        StringKeys.SCH_CREATE_ACTIVATE      to "Create & Activate",
        StringKeys.SCH_ACTIVE_YEAR          to "Active year",
        StringKeys.SCH_HISTORICAL_DRAFTS    to "Historical & drafts",
        StringKeys.SCH_ACTIVATE             to "Activate",
        StringKeys.SCH_ARCHIVE              to "Archive",
        StringKeys.SCH_SCHOOL_DAYS          to "{count} school days",
        StringKeys.SCH_HOLIDAYS             to "{count} holidays",
        // IdCardGenerateTab
        StringKeys.SCH_NO_TEMPLATES         to "No templates available",
        StringKeys.SCH_NO_TEMPLATES_DESC    to "Create a template first in the Templates tab.",
        StringKeys.SCH_SELECT_TEMPLATE      to "SELECT TEMPLATE",
        StringKeys.SCH_INACTIVE             to "Inactive",
        StringKeys.SCH_SELECT_SCOPE         to "SELECT SCOPE",
        StringKeys.SCH_ALL_STUDENTS         to "All Students",
        StringKeys.SCH_ALL_STAFF            to "All Staff",
        StringKeys.SCH_BY_CLASS             to "By Class",
        StringKeys.SCH_CLASS_ID_UUID        to "Class ID (UUID)",
        StringKeys.SCH_GENERATING           to "Generating...",
        StringKeys.SCH_GENERATE_CARDS       to "Generate Cards",
        StringKeys.SCH_RENDERING_CARDS      to "Rendering and uploading cards in parallel...",
        // TeacherPerformanceScreenV2
        StringKeys.SCH_TEACHER_PERFORMANCE  to "Teacher Performance",
        StringKeys.SCH_TEACHER_PERFORMANCE_DESC to "Teacher analytics will appear here once faculty start posting attendance and marks.",
        StringKeys.SCH_AGGREGATE_COMPLIANCE to "Aggregate compliance",
        StringKeys.SCH_STAR_FACULTY         to "STAR FACULTY",
        StringKeys.SCH_ACCOUNTABILITY_MATRIX to "ACCOUNTABILITY MATRIX",
        StringKeys.SCH_DEPARTMENT_EFFICIENCY to "DEPARTMENT EFFICIENCY",
        StringKeys.SCH_COMPLIANCE           to "Compliance",
        StringKeys.SCH_DELAY                to "Delay",
        StringKeys.SCH_AVG_MARK             to "Avg mark",
        // IdCardCardsTab
        StringKeys.SCH_SEARCH_BY_NAME       to "Search by name...",
        StringKeys.SCH_TEACHERS             to "Teachers",
        StringKeys.SCH_CARDS_COUNT          to "{filtered} of {total} cards",
        StringKeys.SCH_NO_CARDS_MATCH       to "No cards match \"{query}\"",
        StringKeys.SCH_NO_CARDS_YET         to "No cards generated yet",
        StringKeys.SCH_TRY_DIFFERENT_SEARCH to "Try a different search term",
        StringKeys.SCH_GO_TO_GENERATE       to "Go to the Generate tab to create ID cards.",
        StringKeys.SCH_DELETE_ID_CARD       to "Delete ID Card?",
        StringKeys.SCH_DELETE_ID_CARD_CONFIRM to "Are you sure you want to delete the ID card for {name}? This action cannot be undone.",
        StringKeys.SCH_ID_CARD              to "ID CARD",
        StringKeys.SCH_QR_CODE              to "QR Code",
        StringKeys.SCH_PDF                  to "PDF",
        StringKeys.SCH_VERIFY               to "Verify",
        StringKeys.SCH_NO_EXPIRY            to "No Expiry",
        StringKeys.SCH_EXPIRED              to "Expired",
        StringKeys.SCH_EXPIRING             to "Expiring",
        StringKeys.SCH_VALID                to "Valid",
        // Additional keys
        StringKeys.SCH_TEMPLATE_STATUS      to "{role} • {status}",
        StringKeys.SCH_DELETE               to "Delete",
        StringKeys.SCH_SAVE                 to "Save",
        StringKeys.SCH_CLASS_SECTION_DASH   to "Class {className}{section}",
        StringKeys.SCH_ACTIVE_LABEL         to "Active",

        // Phase 3 Batch 1 EN
        StringKeys.SCH_80G_ELIGIBLE_RECEIPT            to "80G Eligible • Receipt: {receipt}",
        StringKeys.SCH_80G_RECEIPT                     to "80G • Receipt: {receipt}",
        StringKeys.SCH_ACHIEVEMENTS                    to "Achievements",
        StringKeys.SCH_ALUMNI_DETAIL                   to "Alumni Detail",
        StringKeys.SCH_ALUMNI_NOT_FOUND                to "Alumni not found",
        StringKeys.SCH_APPROVED                        to "Approved",
        StringKeys.SCH_BATCH_YEAR                      to "Batch {year}",
        StringKeys.SCH_CAMPAIGN_COLON                  to "Campaign: {title}",
        StringKeys.SCH_CAMPAIGN_DETAIL                 to "Campaign Detail",
        StringKeys.SCH_CAMPAIGN_NOT_FOUND              to "Campaign not found",
        StringKeys.SCH_CAREER                          to "Career",
        StringKeys.SCH_CAUSE_COLON                     to "Cause: {cause}",
        StringKeys.SCH_CITY                            to "City",
        StringKeys.SCH_COMPANY                         to "Company",
        StringKeys.SCH_CONCURRENCY                     to "Concurrency",
        StringKeys.SCH_CURRENT                         to "Current",
        StringKeys.SCH_CURRENT_TERM                    to "Current Term",
        StringKeys.SCH_DATE_COLON                      to "Date: {date}",
        StringKeys.SCH_DONATIONS                       to "Donations",
        StringKeys.SCH_DRAFT                           to "Draft",
        StringKeys.SCH_DRAFTS                          to "drafts",
        StringKeys.SCH_EMAIL                           to "Email",
        StringKeys.SCH_ENABLED                         to "Enabled",
        StringKeys.SCH_EXPERTISE                       to "Expertise",
        StringKeys.SCH_FALLBACK                        to "Fallback",
        StringKeys.SCH_FEATURED                        to "★ Featured",
        StringKeys.SCH_FLAGGED                         to "Flagged",
        StringKeys.SCH_FLYWHEEL_COMPLETE               to "Flywheel complete: {count} focus areas measured",
        StringKeys.SCH_LINKEDIN                        to "LinkedIn",
        StringKeys.SCH_MENTOR                          to "Mentor",
        StringKeys.SCH_MENTORSHIP                      to "Mentorship",
        StringKeys.SCH_MODE_COLON                      to "Mode: {mode}",
        StringKeys.SCH_NOT_SET                         to "Not set",
        StringKeys.SCH_NO_CAREER_HISTORY               to "No career history",
        StringKeys.SCH_NO_DONATIONS_CAMPAIGN           to "No donations yet for this campaign",
        StringKeys.SCH_NO_DONATIONS_RECORDED           to "No donations recorded",
        StringKeys.SCH_N_DONORS                        to "{count} donors",
        StringKeys.SCH_N_IMPROVED                      to "{improved}/{targeted} improved",
        StringKeys.SCH_N_REPORTS_PUBLISHED             to "{count} reports published successfully",
        StringKeys.SCH_PENDING                         to "Pending",
        StringKeys.SCH_PERIOD_COLON                    to "Period: {start} → {end}",
        StringKeys.SCH_PHONE                           to "Phone",
        StringKeys.SCH_PRESENT                         to "Present",
        StringKeys.SCH_PREVIOUS                        to "Previous",
        StringKeys.SCH_PRIVACY                         to "Privacy",
        StringKeys.SCH_PROFESSION                      to "Profession",
        StringKeys.SCH_PROFESSIONAL                    to "Professional",
        StringKeys.SCH_PROFILE                         to "Profile",
        StringKeys.SCH_PROFILE_COMPLETENESS            to "Profile Completeness",
        StringKeys.SCH_PROGRESS                        to "Progress",
        StringKeys.SCH_PUBLISHED                       to "Published",
        StringKeys.SCH_PUBLISHING                      to "Publishing…",
        StringKeys.SCH_PUBLISH_N_APPROVED              to "Publish {count} Approved",
        StringKeys.SCH_REPORTING_EFFECTIVENESS         to "Reporting Effectiveness",
        StringKeys.SCH_REPORT_CARD_PUBLISHING          to "Report Card Publishing",
        StringKeys.SCH_RUN_FLYWHEEL                    to "Run Flywheel Measurement",
        StringKeys.SCH_RUN_FLYWHEEL_BTN                to "Run Flywheel",
        StringKeys.SCH_SHOW_EMAIL                      to "Show Email",
        StringKeys.SCH_SHOW_PHONE                      to "Show Phone",
        StringKeys.SCH_SKILLS                          to "Skills",
        StringKeys.SCH_STATUS_COLON                    to "Status: {status}",
        StringKeys.SCH_TARGET_BATCH_COLON              to "Target Batch: {batch}",
        StringKeys.SCH_TERM                            to "Term",
        StringKeys.SCH_VISIBILITY                      to "Visibility",

        // Phase 3 Batch 2 EN (A-D)
        StringKeys.SCH_ABSENT                          to "Absent",
        StringKeys.SCH_ACADEMIC_OVERVIEW               to "Academic Overview",
        StringKeys.SCH_ACADEMIC_SCORE                  to "Academic Score",
        StringKeys.SCH_ADD_SLOT                        to "+ Add Slot",
        StringKeys.SCH_ADD_STUDENT                     to "Add student",
        StringKeys.SCH_ADMINISTRATIVE_INFO             to "Administrative Information",
        StringKeys.SCH_ADMISSION_DATE                  to "Admission Date",
        StringKeys.SCH_ADMISSION_NO                    to "Admission No.",
        StringKeys.SCH_ADMISSION_NUMBER                to "Admission Number",
        StringKeys.SCH_APPLICABLE_DAYS                 to "Applicable Days",
        StringKeys.SCH_APPLICABLE_DAYS_PH              to "1,2,3,4,5 (Mon-Fri)",
        StringKeys.SCH_ASSIGNMENTS                     to "Assignments",
        StringKeys.SCH_ASSIGNMENT_COMPLETION           to "Assignment Completion",
        StringKeys.SCH_ATTENDANCE                      to "Attendance",
        StringKeys.SCH_ATTENDANCE_OVERVIEW             to "Attendance Overview",
        StringKeys.SCH_ATTENDANCE_RATE                 to "Attendance rate",
        StringKeys.SCH_AVERAGE                         to "average",
        StringKeys.SCH_CLASS                           to "Class",
        StringKeys.SCH_CLASS_LEVEL                     to "Class Level",
        StringKeys.SCH_CLASS_LEVEL_PH                  to "ALL / PRIMARY / SECONDARY",
        StringKeys.SCH_CLASS_PH                        to "e.g. Grade 4",
        StringKeys.SCH_COMPLETION                      to "completion",
        StringKeys.SCH_CONFIGURATIONS                  to "CONFIGURATIONS",
        StringKeys.SCH_CONNECTED                       to "connected",
        StringKeys.SCH_CONTACT_INFORMATION             to "Contact Information",
        StringKeys.SCH_CONTACT_PARENT                  to "Contact Parent",
        StringKeys.SCH_DANGER_ZONE                     to "DANGER ZONE",
        StringKeys.SCH_DAYS_LEVEL                      to "Days: {days}  ·  Level: {level}",
        StringKeys.SCH_DEACTIVATE                      to "Deactivate",
        StringKeys.SCH_DEACTIVATE_CONFIG               to "Deactivate config?",
        StringKeys.SCH_DEACTIVATE_CONFIG_MSG           to "This will deactivate the school day configuration. You can reactivate it later.",
        StringKeys.SCH_DUE                             to "Due {date}",

        // Phase 3 Batch 2 EN (E-N)
        StringKeys.SCH_EDIT_DAY_CONFIG                 to "Edit Day Config",
        StringKeys.SCH_END                             to "End",
        StringKeys.SCH_EXPERIENCE                      to "Experience",
        StringKeys.SCH_FEES                            to "FEES",
        StringKeys.SCH_FORMAT_DAYS                     to "Format: comma-separated 1-7",
        StringKeys.SCH_FULL_NAME                       to "Full name",
        StringKeys.SCH_FULL_NAME_PH                    to "e.g. Aarav Sharma",
        StringKeys.SCH_HEALTH_RECORDS                  to "Health Records",
        StringKeys.SCH_HEALTH_RECORDS_DESC             to "View and manage health profile, immunizations, and incidents",
        StringKeys.SCH_JOINED                          to "Joined",
        StringKeys.SCH_JOINED_DATE                     to "Joined Date",
        StringKeys.SCH_LABEL                           to "Label",
        StringKeys.SCH_LABEL_PH                        to "e.g. Period 1",
        StringKeys.SCH_LATE                            to "Late",
        StringKeys.SCH_LEAVE                           to "LEAVE",
        StringKeys.SCH_LINKED                          to "linked",
        StringKeys.SCH_LOW_ATTENDANCE                  to "Low Attendance",
        StringKeys.SCH_MANAGE_CLASSES_SUBJECTS         to "Manage classes, subjects & sections",
        StringKeys.SCH_MARKS                           to "MARKS",
        StringKeys.SCH_MUST_BE_LEVEL                   to "Must be: ALL, PRIMARY, or SECONDARY",
        StringKeys.SCH_NAME_PH                         to "e.g. Default Weekday",
        StringKeys.SCH_NEW_ADMISSION                   to "New Admission",
        StringKeys.SCH_NEW_DAY_CONFIG                  to "New Day Config",
        StringKeys.SCH_NO_ACHIEVEMENTS                 to "No achievements yet.",
        StringKeys.SCH_NO_ASSIGNMENTS_YET              to "No class or subject assignments yet.",
        StringKeys.SCH_NO_DAY_CONFIGS                  to "No day configs yet",
        StringKeys.SCH_NO_DAY_CONFIGS_DESC             to "Create your first school day configuration to define the bell schedule.",
        StringKeys.SCH_NO_FEE_RECORDS                  to "No fee records.",
        StringKeys.SCH_NO_INSIGHTS_YET                 to "No insights available yet.",
        StringKeys.SCH_NO_LEAVE_APPLICATIONS           to "No leave applications.",
        StringKeys.SCH_NO_MARKS_RECORDED               to "No marks recorded yet.",
        StringKeys.SCH_NO_PARENTS_LINKED               to "No parents linked yet.",
        StringKeys.SCH_NO_RECENT_ACTIVITY              to "No recent activity yet.",
        StringKeys.SCH_NO_STUDENTS_YET                 to "No students yet",
        StringKeys.SCH_NO_STUDENTS_YET_DESC            to "Add your first student so they appear in attendance, marks and analytics.",
        StringKeys.SCH_NO_STUDENT_PROFILE_DESC         to "This student's record could not be found.",
        StringKeys.SCH_NO_TEACHERS_CONNECTED           to "No teachers connected yet.",
        StringKeys.SCH_N_STUDENTS                      to "{count} students",
        StringKeys.SCH_N_YEARS                         to "{count} years",

        // Phase 3 Batch 2 EN (O-Z)
        StringKeys.SCH_OVERALL                         to "overall",
        StringKeys.SCH_PARENTS                         to "Parents",
        StringKeys.SCH_PARENT_CONNECTIONS              to "PARENT CONNECTIONS",
        StringKeys.SCH_PARENT_PHONE_OPTIONAL           to "Parent/Guardian phone (optional)",
        StringKeys.SCH_PARENT_PHONE_PH                 to "e.g. 98765 43210",
        StringKeys.SCH_PARENT_SATISFACTION             to "Parent Satisfaction",
        StringKeys.SCH_PERFORMANCE                     to "PERFORMANCE",
        StringKeys.SCH_PERSONAL                        to "personal",
        StringKeys.SCH_PHONE_MIN_DIGITS                to "Phone must have at least 10 digits.",
        StringKeys.SCH_PRIMARY_GUARDIAN                to "Primary Guardian",
        StringKeys.SCH_PROFESSIONAL_DETAILS            to "PROFESSIONAL DETAILS",
        StringKeys.SCH_QUICK_ACTIONS                   to "QUICK ACTIONS",
        StringKeys.SCH_RECENT_ACTIVITY                 to "RECENT ACTIVITY",
        StringKeys.SCH_REMOVE_STUDENT                  to "Remove student",
        StringKeys.SCH_REMOVE_STUDENT_DANGER           to "Removing this student hides their records from your school. This can be reversed by re-adding them.",
        StringKeys.SCH_REMOVE_STUDENT_MSG              to "Remove {name} from your school? Their records will be hidden. This can be reversed by re-adding them.",
        StringKeys.SCH_REMOVE_STUDENT_ROSTER_MSG       to "Remove {name} from the roster? They will no longer appear in attendance or analytics. This can be reversed by re-adding them.",
        StringKeys.SCH_REMOVE_TEACHER                  to "Remove teacher",
        StringKeys.SCH_REMOVE_TEACHER_DANGER           to "Removing this teacher revokes their access immediately. This can be reversed by re-adding them.",
        StringKeys.SCH_REMOVE_TEACHER_MSG              to "Remove {name} from your school? They will lose access immediately. This can be reversed by re-adding them.",
        StringKeys.SCH_ROLL_NO                         to "Roll No.",
        StringKeys.SCH_ROLL_NUMBER                     to "Roll Number",
        StringKeys.SCH_ROLL_NUMBER_PH                  to "e.g. 12",
        StringKeys.SCH_SAVING                          to "Saving…",
        StringKeys.SCH_SCHOOL_DAY_CONFIG               to "School Day Config",
        StringKeys.SCH_SEC                             to "Sec {section}",
        StringKeys.SCH_SECTION                         to "Section",
        StringKeys.SCH_SECTIONS_TAUGHT                 to "sections taught",
        StringKeys.SCH_SLOTS_N                         to "Slots ({count})",
        StringKeys.SCH_START                           to "Start",
        StringKeys.SCH_STUDENT                         to "Student",
        StringKeys.SCH_STUDENT_ID                      to "Student ID",
        StringKeys.SCH_STUDIED                         to "studied",
        StringKeys.SCH_TEACHER_CONNECTIONS             to "TEACHER CONNECTIONS",
        StringKeys.SCH_TEACHING_PORTFOLIO              to "TEACHING PORTFOLIO",
        StringKeys.SCH_THIS_STUDENT                    to "this student",
        StringKeys.SCH_THIS_TEACHER                    to "this teacher",
        StringKeys.SCH_VIEW_PROFILE                    to "View Profile",
        // ParentActivityScreenV2 (extras)
        StringKeys.PAC_ANNOUNCEMENTS      to "Announcements",
        StringKeys.PAC_LOAD_ERROR         to "Couldn't load announcements",
        StringKeys.PAC_ALL_CAUGHT_UP      to "All caught up",
        StringKeys.PAC_ALL_CAUGHT_UP_DESC to "New announcements from your school will show up here.",
        // ParentPewsScreenV2 (extras)
        StringKeys.PPEWS_ALL_GOOD         to "All good!",
        StringKeys.PPEWS_ALL_GOOD_BODY    to "There's no specific concern for {name} right now. Keep up the great support!",
        StringKeys.PPEWS_ALL_ON_TRACK     to "All on track",
        StringKeys.PPEWS_ALL_ON_TRACK_BODY to "{name} is doing well. No specific concerns at this time.",
        // ParentFeePaymentScreenV2
        StringKeys.PFP_PAY_FEES           to "Pay Fees",
        StringKeys.PFP_OUTSTANDING        to "Outstanding Amount",
        StringKeys.PFP_OVERDUE_HEADS      to "{count} overdue fee head(s)",
        StringKeys.PFP_PAYMENT_METHOD     to "Payment Method",
        StringKeys.PFP_ONLINE_PAYMENT     to "Online Payment",
        StringKeys.PFP_SECURE_GATEWAY     to "Secure Razorpay gateway",
        StringKeys.PFP_PAY_AMOUNT         to "Pay {amount}",
        StringKeys.PFP_NO_FEES_DUE        to "No fees due",
        // ParentFeeHistoryScreenV2
        StringKeys.PFH_FEE_HISTORY        to "Fee History",
        StringKeys.PFH_TOTAL_COLLECTED    to "Total Collected",
        StringKeys.PFH_NO_HISTORY         to "No payment history",
        StringKeys.PFH_NO_HISTORY_DESC    to "Once you pay fees, the receipts will show up here.",
        // ParentEventRegistrationScreenV2 (extras)
        StringKeys.PE_EVENTS              to "Events",
        StringKeys.PE_EVENT_DETAIL        to "Event Detail",
        StringKeys.PE_UPCOMING            to "Upcoming Events",
        StringKeys.PE_MY_REGS             to "My Registrations",
        StringKeys.PE_NO_EVENTS           to "No upcoming events with registration",
        StringKeys.PE_VENUE              to "Venue: {venue}",
        StringKeys.PE_REGISTER_BY        to "Register by: {date}",
        StringKeys.PE_REGISTERED_STATUS  to "Registered: {status}",
        StringKeys.PE_REG_OPEN           to "Registration open",
        StringKeys.PE_CONFLICTS          to "Conflicts with: {title}",
        StringKeys.PE_SELECT_SLOT        to "Select a time slot",
        StringKeys.PE_RESCHEDULE         to "Reschedule",
        StringKeys.PE_NUM_ATTENDEES      to "Number of attendees",
        StringKeys.PE_REGISTER           to "Register",
        StringKeys.PE_FULL               to "Full",
        StringKeys.PE_YOUR_SLOT          to "Your slot",
        StringKeys.PE_SELECTED           to "Selected",
        StringKeys.PE_NO_REGS            to "No registrations yet",
        StringKeys.PE_SLOT_LABEL         to "Slot: {start} - {end}",
        StringKeys.PE_BOOKED             to "{booked}/{capacity} booked",
        // ParentHomeworkScreenV2
        StringKeys.PHW_HOMEWORK           to "Homework",
        StringKeys.PHW_NO_ACTIVE          to "No active homework",
        StringKeys.PHW_NO_ACTIVE_DESC     to "Your child has no pending homework right now.",
        StringKeys.PHW_GRADED            to "Graded",
        StringKeys.PHW_SUBMITTED         to "Submitted",
        StringKeys.PHW_LATE              to "Late",
        StringKeys.PHW_PENDING           to "Pending",
        StringKeys.PHW_TAP_TO_VIEW       to "Tap to view or update submission",
        StringKeys.PHW_INSTRUCTIONS      to "Instructions",
        StringKeys.PHW_WRITTEN_ANSWER    to "Written answer / notes",
        StringKeys.PHW_ANSWER_PH         to "Type your child's answer here...",
        StringKeys.PHW_PHOTO_ATTACH      to "Photo attachments",
        StringKeys.PHW_UPLOADING         to "Uploading...",
        StringKeys.PHW_ADD_PHOTO         to "Add photo",
        StringKeys.PHW_SUBMIT_SUCCESS    to "Homework submitted successfully!",
        StringKeys.PHW_SUBMITTING        to "Submitting...",
        StringKeys.PHW_SUBMIT            to "Submit homework",
        StringKeys.PHW_ATTACHMENT        to "Attachment",

    )

    private fun enPart2(): Map<String, String> = mapOf(
        // Phase 4 - Teacher screen EN translations
        StringKeys.ATT_LEAVE                                to "Leave",
        StringKeys.COMMON_BUTTON_CREATE                     to "Create",
        StringKeys.COMMON_BUTTON_TRY_AGAIN                  to "Try again",
        StringKeys.TC_A                                     to "A",
        StringKeys.TC_ABSENT                                to "Absent",
        StringKeys.TC_ACTIVE_HOMEWORK                       to "ACTIVE HOMEWORK",
        StringKeys.TC_ACTIVITIES                            to "Activities",
        StringKeys.TC_ADD                                   to "Add",
        StringKeys.TC_ADD_ACTIVITY                          to "Add activity…",
        StringKeys.TC_ADD_A_CHAPTER                         to "Add a chapter",
        StringKeys.TC_ADD_MANUALLY                          to "Add manually",
        StringKeys.TC_ADD_NEW_QUESTION                      to "Add New Question",
        StringKeys.TC_ADD_OBJECTIVE                         to "Add objective…",
        StringKeys.TC_ADD_QUESTION                          to "+ Add Question",
        StringKeys.TC_ADD_RESOURCE                          to "Add resource…",
        StringKeys.TC_ADD_TOPIC                             to "Add topic",
        StringKeys.TC_ADMIN                                 to "Admin",
        StringKeys.TC_ADMIN_NOTE_COLON                      to "Admin note: {note}",
        StringKeys.TC_AHEAD_OF_SCHEDULE                     to "Ahead of schedule",
        StringKeys.TC_AI_EXTRACT_CHAPTERS_TOPICS            to "AI will extract chapters and topics from pasted text",
        StringKeys.TC_AI_NARRATIVE_EDITABLE                 to "AI-generated narrative (editable)",
        StringKeys.TC_ALL                                   to "All",
        StringKeys.TC_ALLERGIES_LABEL                       to "Allergies",
        StringKeys.TC_ALLOW_LATE                            to "Allow late submissions",
        StringKeys.TC_ALL_ATTENDANCE_DONE                   to "All attendance done",
        StringKeys.TC_ALL_CAUGHT_UP                         to "All caught up",
        StringKeys.TC_ALL_CAUGHT_UP_DAY                     to "You're all caught up — have a great day.",
        StringKeys.TC_ALL_CLASSES                           to "All classes",
        StringKeys.TC_ANSWER_COLON                          to "Answer: {answer}",
        StringKeys.TC_APPLY                                 to "Apply",
        StringKeys.TC_APPROVE                               to "Approve",
        StringKeys.TC_APPROVE_ALL                           to "Approve All",
        StringKeys.TC_ASSESSMENT_METHOD                     to "Assessment method",
        StringKeys.TC_ASSIGN_FIRST_HOMEWORK                 to "Assign your first homework for this class.",
        StringKeys.TC_ASSIGN_HOMEWORK                       to "Assign homework",
        StringKeys.TC_ATTENDANCE                            to "ATTENDANCE TODAY",
        StringKeys.TC_ATTENDANCE_DONE                       to "Attendance done",
        StringKeys.TC_ATTENDANCE_TODAY                      to "ATTENDANCE TODAY",
        StringKeys.TC_AT_LEAST_                             to "At least 8 characters",
        StringKeys.TC_AUTO_FILL                             to "Auto-fill",
        StringKeys.TC_AUTO_FILL_FROM_NCERT                  to "Auto-fill from NCERT",
        StringKeys.TC_AUTO_FILL_PREVIEW                     to "Preview: {chapters} chapters, {topics} topics{subtopics} — {units} units will be created as DRAFT for your review.",
        StringKeys.TC_AVG                                   to "avg",
        StringKeys.TC_AVG_N_PCT_PER_CLASS                   to "Avg {pct}%/class",
        StringKeys.TC_BEHIND_SCHEDULE                       to "Behind schedule",
        StringKeys.TC_BELL_SCHEDULE                         to "BELL SCHEDULE",
        StringKeys.TC_CALENDAR                              to "Calendar",
        StringKeys.TC_CHANGE                                to "CHANGE",
        StringKeys.TC_CHANGE_PASSWORD                       to "Change your sign-in password",
        StringKeys.TC_CHANGE_REASON_PH                      to "e.g. Room conflict, schedule swap...",
        StringKeys.TC_CHANGE_REQUEST                        to "Change Request",
        StringKeys.TC_CHANGE_REQUESTS                       to "Change Requests",
        StringKeys.TC_CHANGE_REQUESTS_APPEAR                to "Your timetable change requests will appear here.",
        StringKeys.TC_CHAPTER_TITLE                         to "Chapter title",
        StringKeys.TC_CHECKED_IN                            to "Checked in",
        StringKeys.TC_CHECK_IN                              to "Check in",
        StringKeys.TC_CHOOSE_HOW_TO_BUILD_SYLLABUS          to "Choose how to build your syllabus:",
        StringKeys.TC_CLASS                                 to "Class",
        StringKeys.TC_CLASSES                               to "Classes",
        StringKeys.TC_CLASSES_MARKED                        to "{count} of {total} classes marked",
        StringKeys.TC_CLASSES_ON_TRACK                      to "Your classes are on track",
        StringKeys.TC_CLASSES_TO_MARK                       to "{count} classes to mark",
        StringKeys.TC_CLASSES_YOU_TEACH                     to "Classes you teach",
        StringKeys.TC_CLASS_CANCELLED                       to "{className} · {time} (cancelled)",
        StringKeys.TC_CLASS_CANCELLED_DATE                  to "{className} (cancelled)",
        StringKeys.TC_CLASS_SUBJECT                         to "Class / Subject",
        StringKeys.TC_CLASS_TEACHER                         to "Class teacher",
        StringKeys.TC_CLOSE_HOMEWORK_DESC                   to "Closing archives the homework. Students can no longer submit.",
        StringKeys.TC_CLOSE_HOMEWORK_Q                      to "Close this homework?",
        StringKeys.TC_CLOSE_IT                              to "Close it",
        StringKeys.TC_COMPLETE                              to "Complete",
        StringKeys.TC_COMPLETED                             to "Completed",
        StringKeys.TC_CONDITIONS_LABEL                      to "Conditions",
        StringKeys.TC_CONFIRM_AND_CREATE                    to "Confirm & Create",
        StringKeys.TC_CONFIRM_NEW_PASSWORD                  to "Confirm new password",
        StringKeys.TC_CORRECT_ANSWER                        to "Correct answer:",
        StringKeys.TC_CORRECT_ANSWER_EG_AB                  to "Correct answer (e.g. A, B)",
        StringKeys.TC_CORRECT_ANSWER_TEXT                   to "Correct answer text",
        StringKeys.TC_COULDNT_LOAD_ATTENDANCE               to "Couldn't load attendance",
        StringKeys.TC_COULDNT_LOAD_BOARD                    to "Couldn't load the homework board",
        StringKeys.TC_COULDNT_LOAD_CLASS                    to "Couldn't load this class",
        StringKeys.TC_COULDNT_LOAD_CLASSES                  to "Couldn't load your classes",
        StringKeys.TC_COULDNT_LOAD_HOMEWORK                 to "Couldn't load homework",
        StringKeys.TC_COULDNT_LOAD_LESSON_PLANS             to "Couldn't load lesson plans",
        StringKeys.TC_COULDNT_LOAD_PROFILE                  to "Couldn't load profile",
        StringKeys.TC_COULDNT_LOAD_ROSTER                   to "Couldn't load the roster",
        StringKeys.TC_COULDNT_LOAD_SCHEDULE                 to "Couldn't load your schedule.",
        StringKeys.TC_COULDNT_LOAD_SYLLABUS                 to "Couldn't load syllabus",
        StringKeys.TC_COULDNT_LOAD_TEMPLATES                to "Couldn't load templates",
        StringKeys.TC_COULDNT_LOAD_TESTS                    to "Couldn't load tests",
        StringKeys.TC_COVERAGE_N_PCT                        to "Coverage: {pct}%",
        StringKeys.TC_COVERED_DATE                          to "Covered {date}",
        StringKeys.TC_CREATE_AS_DRAFT                       to "Create as Draft",
        StringKeys.TC_CREATE_A_TEST                         to "Create a test",
        StringKeys.TC_CREATE_CHAPTERS_TOPICS_ONE_BY_ONE     to "Create chapters and topics one by one",
        StringKeys.TC_CREATE_FIRST_LESSON_PLAN              to "Create your first lesson plan for this class.",
        StringKeys.TC_CREATE_FIRST_TEST                     to "Create your first test for this class.",
        StringKeys.TC_CREATE_PLAN                           to "Create plan",
        StringKeys.TC_CREATE_TEST                           to "Create test",
        StringKeys.TC_CRITICALLY_BEHIND                     to "Critically behind",
        StringKeys.TC_CURRENT_PASSWORD                      to "Current password",
        StringKeys.TC_CURRICULUM_UNIT_OPTIONAL              to "Curriculum unit (optional)",
        StringKeys.TC_DAILY_CLASS_LOG                       to "Daily Class Log",
        StringKeys.TC_DAILY_LOG                             to "Daily Log",
        StringKeys.TC_DAY                                   to "Day",
        StringKeys.TC_DAY_AT_A_GLANCE                       to "Here's your day at a glance.",
        StringKeys.TC_DECLINING                             to "Declining",
        StringKeys.TC_DETAILS_OPTIONAL                      to "Details (optional)",
        StringKeys.TC_DIFFICULTY                            to "Difficulty",
        StringKeys.TC_DIGITAL_ID_CARD                       to "Digital ID Card",
        StringKeys.TC_DIGITAL_ID_CARD_DESC                  to "View student digital ID cards",
        StringKeys.TC_DISMISS                               to "Dismiss",
        StringKeys.TC_DONE                                  to "Done",
        StringKeys.TC_DRAFT                                 to "DRAFT",
        StringKeys.TC_DRAFT_PARENT_MESSAGE                  to "Draft parent message",
        StringKeys.TC_DRAFT_UNITS_NOT_VISIBLE_TO_PARENTS    to "Draft units are not visible to parents until approved.",
        StringKeys.TC_DUE_DATE                              to "Due date",
        StringKeys.TC_DUE_LABEL                             to "Due {date}",
        StringKeys.TC_DUE_PAST_TURNED_IN                    to "Due {date}{pastDue} · {turnedIn}/{total} turned in",
        StringKeys.TC_EASY                                  to "Easy",
        StringKeys.TC_EDIT                                  to "Edit",
        StringKeys.TC_EDITING_QUESTION                      to "Editing Question",
        StringKeys.TC_EDIT_DRAFT                            to "Edit Draft",
        StringKeys.TC_EDIT_LESSON_PLAN                      to "EDIT LESSON PLAN",
        StringKeys.TC_END                                   to "End",
        StringKeys.TC_ENTERED_N_OF_N                        to "Entered {entered} of {total}",
        StringKeys.TC_ESCALATED                             to "Escalated",
        StringKeys.TC_EST_COMPLETION_DATE                   to "Est. completion: {date}",
        StringKeys.TC_EXAM_DATE                             to "Exam date",
        StringKeys.TC_EXPLANATION_COLON                     to "Explanation: {explanation}",
        StringKeys.TC_EXPLANATION_OPTIONAL                  to "Explanation (optional)",
        StringKeys.TC_EXTEND                                to "Extend",
        StringKeys.TC_EXTENDED_TO                           to "Extended to {date}",
        StringKeys.TC_EXTEND_FOR                            to "Extend for",
        StringKeys.TC_EXTEND_FOR_CLASS                      to "Extend for class",
        StringKeys.TC_EXTEND_WHOLE_CLASS                    to "Extend for the whole class",
        StringKeys.TC_FAILING_TREND                         to "Failing",
        StringKeys.TC_FALSE                                 to "False",
        StringKeys.TC_FETCHING_NCERT_REFERENCE              to "Fetching NCERT reference…",
        StringKeys.TC_FETCH_STANDARD_NCERT_SYLLABUS         to "Fetch the standard CBSE/NCERT syllabus for this class & subject",
        StringKeys.TC_FILL_UPS                              to "Fill-ups",
        StringKeys.TC_FLAGGED                               to "Flagged",
        StringKeys.TC_FLAGS                                 to "Flags",
        StringKeys.TC_FROM                                  to "From",
        StringKeys.TC_GENERATE_QUIZ                         to "Generate Quiz",
        StringKeys.TC_GRADED                                to "Graded",
        StringKeys.TC_GRANT                                 to "Grant",
        StringKeys.TC_GRANT_EXTENSION                       to "Grant extension",
        StringKeys.TC_GROUNDING_FLAGS_DETECTED              to "Grounding flags detected",
        StringKeys.TC_HARD                                  to "Hard",
        StringKeys.TC_HEALTH_ALERTS                         to "Health Alerts",
        StringKeys.TC_HEALTH_ALERTS_DESC                    to "Students with health conditions in your classes",
        StringKeys.TC_HEALTH_ALERTS_LIST_DESC               to "Students with health conditions in your assigned classes",
        StringKeys.TC_HI_NAME                               to "Hi, {name}",
        StringKeys.TC_HOLIDAY                               to "Holiday",
        StringKeys.TC_HOLIDAY_NOTICE                        to "Holiday",
        StringKeys.TC_HOW_ASSESS_OPTIONAL                   to "How will you assess? (optional)",
        StringKeys.TC_IMPORT_MARKS                          to "Import Marks (OCR / Text)",
        StringKeys.TC_IMPROVING                             to "Improving",
        StringKeys.TC_INITIATED_BY                          to "Initiated by",
        StringKeys.TC_INSTANTIATE_FROM_TEMPLATE             to "Use this template",
        StringKeys.TC_INSTRUCTIONS_PH                       to "Instructions for students",
        StringKeys.TC_LANGUAGE                              to "Appearance",
        StringKeys.TC_LANGUAGE_COLON                        to "Language: {lang}",
        StringKeys.TC_LAST_MARKED_BY                        to "Last marked by {name}",
        StringKeys.TC_LEAVES                                to "Leaves",
        StringKeys.TC_LEAVE_REQUESTS                        to "Leave requests",
        StringKeys.TC_LESSON                                to "Lesson Plan",
        StringKeys.TC_LESSON_PLANS                          to "LESSON PLANS",
        StringKeys.TC_LESSON_TITLE                          to "Lesson title",
        StringKeys.TC_LINK_HOMEWORK_OPTIONAL                to "Link homework (optional)",
        StringKeys.TC_LOADING_LEADERBOARD                   to "Loading leaderboard...",
        StringKeys.TC_LOCKED_UNTIL_EXAM                     to "Locked until exam",
        StringKeys.TC_LOG_OUT                               to "Log out",
        StringKeys.TC_LOG_OUT_DESC                          to "You'll need to sign in again to access your classes.",
        StringKeys.TC_LOG_OUT_Q                             to "Log out?",
        StringKeys.TC_LOW_ATTENDANCE                        to "Low attendance",
        StringKeys.TC_MARK                                  to "MARK",
        StringKeys.TC_MARKED                                to "Marked",
        StringKeys.TC_MARKING_ATTENDANCE                    to "Marking attendance…",
        StringKeys.TC_MARKS                                 to "Marks",
        StringKeys.TC_MARKS_DROPPING                        to "Marks dropping",
        StringKeys.TC_MARKS_PENDING                         to "MARKS PENDING",
        StringKeys.TC_MARK_ALL_PRESENT                      to "Mark all present",
        StringKeys.TC_MARK_ATTENDANCE                       to "Mark attendance",
        StringKeys.TC_MARK_GRADED                           to "Mark graded",
        StringKeys.TC_MARK_IMPROVED                         to "Mark improved",
        StringKeys.TC_MATCH                                 to "Match",
        StringKeys.TC_MAX_MARKS                             to "Max marks",
        StringKeys.TC_MAX_N                                 to "Max {n}",
        StringKeys.TC_MAX_N_ENTERED_N_OF_N                  to "Max {max} · Entered {entered} of {total}",
        StringKeys.TC_MCQ                                   to "MCQ",
        StringKeys.TC_MEDIUM                                to "Medium",
        StringKeys.TC_MESSAGES                              to "Messages",
        StringKeys.TC_MESSAGES_DESC                         to "Send and receive messages from parents",
        StringKeys.TC_MIN                                   to "min",
        StringKeys.TC_MINUTES                               to "Minutes",
        StringKeys.TC_MY_LEAVE                              to "My leave",
        StringKeys.TC_NCERT_AUTO_FILL                       to "NCERT Auto-fill",
        StringKeys.TC_NEEDS_ATTENTION                       to "Needs Attention",
        StringKeys.TC_NEEDS_ATTENTION_DESC                  to "Students who need your attention",
        StringKeys.TC_NEW_CHAPTER                           to "New chapter",
        StringKeys.TC_NEW_DUE_DATE                          to "New due date",
        StringKeys.TC_NEW_HOMEWORK                          to "New homework",
        StringKeys.TC_NEW_LESSON_PLAN                       to "NEW LESSON PLAN",
        StringKeys.TC_NEW_PASSWORD                          to "New password",
        StringKeys.TC_NEW_PLAN                              to "New plan",
        StringKeys.TC_NEW_TEST                              to "New test",
        StringKeys.TC_NEW_TOPIC                             to "New topic",
        StringKeys.TC_NEXT                                  to "NEXT",
        StringKeys.TC_NEXT_CLASS                            to "NEXT CLASS",
        StringKeys.TC_LATER                                 to "Later",
        StringKeys.TC_NOTHING_PENDING                       to "Nothing pending right now.",
        StringKeys.TC_NOT_ENOUGH_DATA                       to "Not enough data",
        StringKeys.TC_NOT_MARKED                            to "NOT MARKED",
        StringKeys.TC_NOT_SUBMITTED                         to "Not submitted",
        StringKeys.TC_NOT_YOUR_STUDENT                      to "Not your student",
        StringKeys.TC_NOT_YOUR_STUDENT_DESC                 to "This student is not in your assigned classes.",
        StringKeys.TC_NOW                                   to "NOW",
        StringKeys.TC_NOW_TEACHING                          to "Now teaching",
        StringKeys.TC_QUICK_ACTIONS                         to "Quick actions",
        StringKeys.TC_PENDING_ACTIONS                       to "Pending actions",
        StringKeys.TC_UPCOMING_EVENTS                         to "Upcoming events",
        StringKeys.TC_YOUR_DAY                              to "your day",
        StringKeys.TC_LETS                                  to "let's",
        StringKeys.TC_UPDATE_ACCENT                         to "update",
        StringKeys.TC_UPDATE_BLURB_ATTENDANCE               to "Mark who's present in a few taps and keep the register up to date.",
        StringKeys.TC_UPDATE_BLURB_MARKS                    to "Record scores and grades for a class, subject by subject.",
        StringKeys.TC_UPDATE_BLURB_HOMEWORK                 to "Assign homework and set due dates so students know what's next.",
        StringKeys.TC_UPDATE_BLURB_SYLLABUS                 to "Track syllabus progress and tick off topics as you teach them.",
        StringKeys.TC_UPDATE_BLURB_LESSON                   to "Plan your lessons and outline what each period will cover.",
        StringKeys.TC_YOUR                                  to "your",
        StringKeys.TC_WEEK_ACCENT                           to "week",
        StringKeys.TC_SCHEDULE_TAB                          to "Schedule",
        StringKeys.TC_REQUESTS_TAB                          to "Requests",
        StringKeys.TC_ACCOUNT_ACCENT                        to "account",
        StringKeys.TC_CLASSES_ACCENT                        to "classes",
        StringKeys.TC_AT_RISK                               to "At risk",
        StringKeys.TC_SEC_TIME_OFF                          to "Time off",
        StringKeys.TC_SEC_SECURITY                          to "Security",
        StringKeys.TC_SEC_PREFERENCES                       to "Preferences",
        StringKeys.TC_APPEARANCE                            to "Appearance",
        StringKeys.TC_STAT_SUBJECTS                         to "Subjects",
        StringKeys.TC_STAT_CLASSES                          to "Classes",
        StringKeys.TC_VIEW_PROFILE_DETAILS                  to "View full details",
        StringKeys.TC_NO_PERIOD_RIGHT_NOW                   to "No class right now",
        StringKeys.TC_NO_ACTIVE_HOMEWORK                    to "No active homework",
        StringKeys.TC_NO_ACTIVE_HOMEWORK_CLASS              to "No active homework for this class",
        StringKeys.TC_NO_ALLOCATIONS                        to "No allocations yet",
        StringKeys.TC_ASSIGNMENTS_WILL_APPEAR               to "Your class assignments will appear here once allocated.",
        StringKeys.TC_NO_ASSIGNMENTS_FOUND                  to "No assignments found.",
        StringKeys.TC_NO_ATTEMPTS_YET                       to "No attempts yet",
        StringKeys.TC_NO_ATTENDANCE_DATA                    to "No attendance data",
        StringKeys.TC_NO_CHANGE                             to "No change",
        StringKeys.TC_NO_CHANGE_REQUESTS                    to "No change requests",
        StringKeys.TC_NO_CLASSES_MATCH                      to "No classes match",
        StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY            to "No classes scheduled today.",
        StringKeys.TC_NO_CLASSES_TODAY                      to "No classes today",
        StringKeys.TC_NO_DATE                               to "No date",
        StringKeys.TC_NO_DRAFTS_FOUND                       to "No drafts found",
        StringKeys.TC_NO_HEALTH_ALERTS                      to "No health alerts",
        StringKeys.TC_NO_HEALTH_ALERTS_DESC                 to "No student in your assigned classes has a health condition.",
        StringKeys.TC_NO_HOMEWORK_LINKED                    to "No active homework for this class",
        StringKeys.TC_NO_LEAVE_REQUESTS                     to "No leave requests yet.",
        StringKeys.TC_NO_LESSON_PLANS_YET                   to "No lesson plans yet",
        StringKeys.TC_NO_NCERT_REFERENCE_FOUND              to "No NCERT reference found",
        StringKeys.TC_NO_PERIODS_FOR_DAY                    to "No periods for {day}",
        StringKeys.TC_NO_PERIODS_TODAY                      to "No periods scheduled today.",
        StringKeys.TC_NO_PLANS_THIS_MONTH                   to "No plans this month",
        StringKeys.TC_NO_STUDENTS_ENROLLED                  to "No students enrolled yet.",
        StringKeys.TC_NO_STUDENTS_NEED_ATTENTION            to "No student in your assigned classes needs attention right now.",
        StringKeys.TC_NO_SYLLABUS_UNITS                     to "No syllabus units available",
        StringKeys.TC_NO_TEMPLATES_YET                      to "No templates yet",
        StringKeys.TC_NO_TESTS_YET                          to "No tests yet",
        StringKeys.TC_NO_UNITS_YET                          to "No units yet",
        StringKeys.TC_NO_UNIT_LINKED                        to "No unit linked",
        StringKeys.TC_NO_UPCOMING_PERIOD                    to "No upcoming period scheduled.",
        StringKeys.TC_NUMBER_OF_QUESTIONS_N                 to "Number of questions: {count}",
        StringKeys.TC_N_ATTEMPTED                           to "{count} attempted",
        StringKeys.TC_N_AT_RISK                             to "{count} at risk",
        StringKeys.TC_N_CLASSES_DONE                        to "{count} classes done",
        StringKeys.TC_N_DRAFT_UNITS_PENDING_APPROVAL        to "{count} draft units pending approval",
        StringKeys.TC_N_ENROLLED                            to "{count} enrolled",
        StringKeys.TC_N_HOLIDAYS                            to "{count} holidays",
        StringKeys.TC_N_LEFT                                to "{count} left",
        StringKeys.TC_N_OF_N_UNITS_COVERED                  to "{covered} of {total} units covered",
        StringKeys.TC_N_PERCENT_PRESENT                     to "{pct}% present",
        StringKeys.TC_N_PER_WEEK                            to "{count}/week",
        StringKeys.TC_N_QUESTIONS_STATUS                    to "{count} questions · {status}",
        StringKeys.TC_N_STUDENTS                            to "{count} students",
        StringKeys.TC_N_STUDENTS_ABSENT                     to "{count} absent today",
        StringKeys.TC_N_SUBTOPICS                           to "{count} subtopics",
        StringKeys.TC_N_TOPICS_SELECTED                     to "{count} topics selected",
        StringKeys.TC_N_TURNED_IN                           to "{count} of {total} turned in",
        StringKeys.TC_OBJECTIVES                            to "Objectives",
        StringKeys.TC_OPTIONS_ONE_PER_LINE                  to "Options (one per line):",
        StringKeys.TC_OPTIONS_PH                            to "A) ...\nB) ...\nC) ...\nD) ...",
        StringKeys.TC_P                                     to "P",
        StringKeys.TC_PACE_EXPECTED_ACTUAL                  to "Expected {expected}% · Actual {actual}% · Δ {delta}%",
        StringKeys.TC_PACE_UPDATE                           to "Pace update",
        StringKeys.TC_PARENT_CONTACT                        to "Parent contact",
        StringKeys.TC_PARENT_GUARDIAN                       to "Parent/Guardian",
        StringKeys.TC_PARENT_MESSAGE                        to "Parent message",
        StringKeys.TC_NOTIFY_PARENTS                        to "Notify parents",
        StringKeys.TC_NOTIFY_PARENTS_ABOUT_ABSENCE            to "Notify parents about the absence via Messages.",
        StringKeys.TC_PARSE_SYLLABUS                        to "Parse Syllabus",
        StringKeys.TC_PARSE_WITH_AI                         to "Parse with AI",
        StringKeys.TC_PASSWORD                              to "Password",
        StringKeys.TC_PASS_OPTIONAL                         to "Pass (optional)",
        StringKeys.TC_PASTE_SYLLABUS_HINT                   to "Paste your syllabus text below. AI will extract chapters and topics.",
        StringKeys.TC_PASTE_SYLLABUS_PH                     to "e.g. Chapter 1: Number Systems\n1.1 Real Numbers\n1.2 Irrational Numbers...",
        StringKeys.TC_PASTE_SYLLABUS_TEXT                   to "Paste syllabus text",
        StringKeys.TC_PAST_DUE                              to "past due",
        StringKeys.TC_PENDING                               to "Pending",
        StringKeys.TC_PENDING_COUNT                         to "pending",
        StringKeys.TC_PERCENT_PRESENT_OVERALL               to "{pct}% present overall",
        StringKeys.TC_PERFORMANCE                           to "Performance",
        StringKeys.TC_PICK_ALLOCATION_DESC                  to "Pick a class to continue",
        StringKeys.TC_PICK_CLASS                            to "Pick a class",
        StringKeys.TC_PICK_CLASS_FOR                        to "Pick a class for {tool}",
        StringKeys.TC_PICK_DATE_FOR_LESSON                  to "Pick the lesson date",
        StringKeys.TC_PICK_TEST_DATE                        to "Pick the test date",
        StringKeys.TC_PLAN                                  to "Plan",
        StringKeys.TC_PLANNED                               to "Planned",
        StringKeys.TC_PLANNED_DATE                          to "Planned date",
        StringKeys.TC_PREVIEW_N_UNITS_FOUND                 to "Preview ({count} units found)",
        StringKeys.TC_PROFILE                               to "Profile",
        StringKeys.TC_PTM_EVENTS                            to "PTM & Events",
        StringKeys.TC_PTM_EVENTS_DESC                       to "View upcoming parent-teacher meetings and school events",
        StringKeys.TC_PUBLISH                               to "Publish",
        StringKeys.TC_PUBLISHED                             to "Published",
        StringKeys.TC_PUBLISHED_PARENTS_NOTIFIED            to "Published — parents notified. Marks are read-only.",
        StringKeys.TC_PUBLISH_DESC                          to "This will publish the results and notify parents. You can't unpublish from here.",
        StringKeys.TC_PUBLISH_NAME_Q                        to "Publish {name}?",
        StringKeys.TC_PUBLISH_NOTIFY_PARENTS                to "Publish & notify parents",
        StringKeys.TC_PUBLISH_QUIZ                          to "Publish Quiz",
        StringKeys.TC_QUESTION_TEXT                         to "Question text",
        StringKeys.TC_QUESTION_TYPES                        to "Question types",
        StringKeys.TC_QUIZ                                  to "Quiz",
        StringKeys.TC_NO_QUIZZES_CREATED_YET                to "No quizzes created yet for this class.",
        StringKeys.TC_LESSON_COMPLETED                      to "Lesson completed!",
        StringKeys.TC_CREATE_QUIZ_TO_ASSESS                 to "Create a quiz to assess \"{title}\".",
        StringKeys.TC_NOT_NOW                               to "Not Now",
        StringKeys.TC_CREATE_QUIZ                           to "Create Quiz",
        StringKeys.TC_QUIZZES                               to "QUIZZES",
        StringKeys.TC_QUIZ_LEADERBOARD                      to "Quiz Leaderboard",
        StringKeys.TC_QUIZ_PREVIEW                          to "Quiz Preview",
        StringKeys.TC_READY_TO_MARK                         to "READY TO MARK",
        StringKeys.TC_REASON                                to "Reason",
        StringKeys.TC_REASON_COLON                          to "Reason: {reason}",
        StringKeys.TC_REASON_OPTIONAL                       to "Reason (optional)",
        StringKeys.TC_RECENT                                to "Recent",
        StringKeys.TC_RECENT_ABSENCES                       to "Recent absences",
        StringKeys.TC_REGENERATE                            to "Regenerate",
        StringKeys.TC_REGENERATE_ALL                        to "Regenerate All",
        StringKeys.TC_REJECT_ALL                            to "Reject All",
        StringKeys.TC_REMINDED                              to "Reminded",
        StringKeys.TC_REMOVE                                to "Remove",
        StringKeys.TC_REPORT_CARD_REVIEW                    to "Report Card Review",
        StringKeys.TC_REPORT_CARD_REVIEW_DESC               to "Review and approve student report cards",
        StringKeys.TC_REQUEST_NEW_PERIOD                    to "+ Request New Period",
        StringKeys.TC_REQUEST_PERIOD_DELETION               to "Request Period Deletion",
        StringKeys.TC_REQUEST_PERIOD_UPDATE                 to "Request Period Update",
        StringKeys.TC_RESOURCES                             to "Resources",
        StringKeys.TC_RESULTS                               to "Results",
        StringKeys.TC_RISK_HIGH                             to "High",
        StringKeys.TC_RISK_MEDIUM                           to "Medium",
        StringKeys.TC_RISK_WATCH                            to "Watch",
        StringKeys.TC_ROLL_LABEL                            to "Roll",
        StringKeys.TC_ROLL_N                                to "Roll {n}",
        StringKeys.TC_ROLL_NO                               to "Roll No {no}",
        StringKeys.TC_ROLL_ON_LEAVE                         to "On leave",
        StringKeys.TC_ROOM                                  to "Room",
        StringKeys.TC_ROOM_HINT                             to "e.g. 101",
        StringKeys.TC_ROOM_N                                to "Room {room}",
        StringKeys.TC_SAVED                                 to "Saved",
        StringKeys.TC_SAVED_NOT_PUBLISHED                   to "Saved (not published)",
        StringKeys.TC_SAVED_SUCCESSFULLY                    to "Saved successfully",
        StringKeys.TC_SAVE_AND_BACK                         to "Save & Back",
        StringKeys.TC_SAVE_AS_TEMPLATE                      to "Save as template",
        StringKeys.TC_SAVE_ATTENDANCE                       to "Save attendance",
        StringKeys.TC_SAVE_CHANGES                          to "Save changes",
        StringKeys.TC_SAVE_DRAFT_BTN                        to "Save Draft",
        StringKeys.TC_SAVE_LESSON_AS_TEMPLATE               to "Save Lesson as Template",
        StringKeys.TC_SAVE_LOG                              to "Save Log",
        StringKeys.TC_SAVE_MARKS                            to "Save marks",
        StringKeys.TC_SAVING                                to "Saving…",
        StringKeys.TC_SCHEDULED                             to "SCHEDULED",
        StringKeys.TC_SCHEDULED_MESSAGES                    to "Scheduled Messages",
        StringKeys.TC_SCHEDULED_MESSAGES_DESC               to "View and manage scheduled messages",
        StringKeys.TC_SCHEDULED_TESTS                       to "SCHEDULED TESTS",
        StringKeys.TC_SCORE                                 to "Score",
        StringKeys.TC_SEARCH_CLASS                          to "Search class, section or subject",
        StringKeys.TC_SEARCH_CLASSES                        to "Search classes",
        StringKeys.TC_SELECT_TOPICS_COVERED_TODAY           to "Select topics covered today",
        StringKeys.TC_SELECT_UNITS                          to "Select units",
        StringKeys.TC_SEND_TO_PARENT                        to "Send to parent",
        StringKeys.TC_SENT_TO_ADMIN_FOR_APPROVAL            to "This will be sent to your school admin for approval.",
        StringKeys.TC_SHARED                                to "Shared",
        StringKeys.TC_SHARE_WITH_TEACHERS                   to "Share with other teachers in school",
        StringKeys.TC_SIGNED_IN_AS                          to "Signed in as",
        StringKeys.TC_SKIP                                  to "Skip",
        StringKeys.TC_SKIPPED                               to "Skipped",
        StringKeys.TC_SLA_DAYS                              to "SLA: {days} days",
        StringKeys.TC_START                                 to "Start",
        StringKeys.TC_STATUS_COLON                          to "Status: {status}",
        StringKeys.TC_STEADY                                to "Steady",
        StringKeys.TC_STUDENT                               to "Student",
        StringKeys.TC_STUDENTS                              to "Students",
        StringKeys.TC_STUDENTS_COUNT                        to "{subject} · {count} students",
        StringKeys.TC_STUDENT_COLON                         to "Student: {name}",
        StringKeys.TC_SUBJECTS                              to "SUBJECTS",
        StringKeys.TC_SUBJECT_ONLY                          to "Subject only",
        StringKeys.TC_SUBMISSIONS                           to "Submissions",
        StringKeys.TC_SUBMITTED                             to "Submitted",
        StringKeys.TC_SUBMIT_REQUEST                        to "Submit Request",
        StringKeys.TC_SUB_COLON                             to "Sub: {subject}",
        StringKeys.TC_SWIPE_BACK_TO_CURRENT                 to "← Swipe back to current class",
        StringKeys.TC_SWIPE_BACK_TO_SUMMARY                 to "← Swipe back to summary",
        StringKeys.TC_SWIPE_FULL_SCHEDULE                   to "Swipe to see full schedule →",
        StringKeys.TC_SWIPE_SEE_CLASSES                     to "Swipe to see each class →",
        StringKeys.TC_SYLLABUS                              to "SYLLABUS",
        StringKeys.TC_TAP_TO_CHECK_IN                       to "Tap to check in",
        StringKeys.TC_TAP_TO_SWITCH                         to "· tap to switch",
        StringKeys.TC_TEMPLATES                             to "Templates",
        StringKeys.TC_TEMPLATE_TITLE                        to "Template title",
        StringKeys.TC_TESTS_AND_MARKS                       to "TESTS & MARKS",
        StringKeys.TC_TEST_NAME                             to "Test name",
        StringKeys.TC_TEST_NAME_PH                          to "e.g. Unit Test 1",
        StringKeys.TC_THINGS_NEED_ATTENTION                 to "WHAT NEEDS YOU",
        StringKeys.TC_THIS_MONTH                            to "This month",
        StringKeys.TC_THIS_WEEK                             to "This week",
        StringKeys.TC_TITLE                                 to "Title",
        StringKeys.TC_TITLE_PH                              to "e.g. Chapter 4 exercises",
        StringKeys.TC_TO                                    to "To",
        StringKeys.TC_TO_PUBLISH                            to "to publish",
        StringKeys.TC_TODAY                                 to "TODAY",
        StringKeys.TC_TODAYS_SCHEDULE                       to "TODAY'S SCHEDULE",
        StringKeys.TC_TOGGLE_VISIBILITY                     to "Toggle visibility",
        StringKeys.TC_TOPIC_TITLE                           to "Topic title",
        StringKeys.TC_TOTAL                                 to "Total",
        StringKeys.TC_TRANSPORT_ATTENDANCE                  to "Transport Attendance",
        StringKeys.TC_TRANSPORT_ATTENDANCE_DESC             to "Track bus boarding and deboarding",
        StringKeys.TC_TRUE                                  to "True",
        StringKeys.TC_TRUE_FALSE                            to "True/False",
        StringKeys.TC_TRY_AGAIN                             to "Try again",
        StringKeys.TC_TRY_DIFFERENT_SEARCH                  to "Try a different search or filter.",
        StringKeys.TC_TYPE                                  to "Type",
        StringKeys.TC_UNDER_INTERVENTION                    to "Under intervention",
        StringKeys.TC_UPDATE_ATTENDANCE                     to "Update attendance",
        StringKeys.TC_UPDATE_PASSWORD                       to "Update password",
        StringKeys.TC_URGENCY_COLON                         to "Urgency: {level}",
        StringKeys.TC_USE_TEMPLATE                          to "Use template",
        StringKeys.TC_WEEKLY_SCHEDULE_APPEAR                to "Your weekly schedule will appear here.",
        StringKeys.TC_WEEKLY_TIMETABLE                      to "WEEKLY TIMETABLE",
        StringKeys.TC_WHAT_NEEDS_YOU                        to "WHAT NEEDS YOU",
        StringKeys.TC_WHAT_TAUGHT_TODAY_OPTIONAL            to "What was taught today? (optional)",
        StringKeys.TC_WHICH_CLASS                           to "Which class?",
        StringKeys.TC_WHY_APPLYING                          to "Why are you applying?",
        StringKeys.TC_WHY_CHANGE_NEEDED                     to "Why is this change needed?",
        StringKeys.TC_WHY_EXTENSION                         to "Why extension?",
        // Notifications (additional)
        StringKeys.NOTIF_INBOX              to "INBOX",
        StringKeys.NOTIF_UNREAD_LABEL       to "unread",
        StringKeys.NOTIF_FILTER_UNREAD      to "Unread",
        StringKeys.NOTIF_ALL_CAUGHT_UP      to "You're all caught up",
        StringKeys.NOTIF_NO_UNREAD          to "No unread notifications.",
        StringKeys.NOTIF_NONE_YET           to "No notifications yet.",
        StringKeys.NOTIF_PREFERENCES        to "Notification preferences",
        StringKeys.NOTIF_MARK_ALL           to "Mark all",
        // Teacher Heatmap
        StringKeys.TH_TITLE                 to "Class Heatmap",
        StringKeys.TH_NO_ASSIGNMENTS        to "No assignments",
        StringKeys.TH_NO_ASSIGNMENTS_DESC   to "You have no class-subject assignments yet.",
        StringKeys.TH_NO_DATA               to "No data",
        StringKeys.TH_NO_DATA_DESC          to "No misconceptions recorded for this class yet.",
        StringKeys.TH_SELECTED              to "SELECTED",
        StringKeys.TH_CHILDREN              to "Children",
        StringKeys.TH_MISCONCEPTIONS        to "Misconceptions",
        StringKeys.TH_TOPICS                to "Topics",
        StringKeys.TH_CHILDREN_AFFECTED     to "{count} children affected",
        StringKeys.TH_EVIDENCE              to "Evidence:",
        // Tutor — Chat
        StringKeys.TUT_AI_TUTOR             to "AI Tutor",
        StringKeys.TUT_CLEAR                to "Clear",
        StringKeys.TUT_ERROR                to "Error",
        StringKeys.TUT_ASK_QUESTION         to "Ask a question",
        StringKeys.TUT_ASK_QUESTION_DESC    to "Type your doubt below. The AI tutor will guide you step by step. You can pick a subject for more specific help, or ask a general question.",
        StringKeys.TUT_TYPE_DOUBT           to "Type your doubt...",
        StringKeys.TUT_ASK                  to "Ask",
        StringKeys.TUT_PRACTICE_READY       to "Practice questions ready!",
        StringKeys.TUT_LOADING_SUBJECTS     to "Loading subjects...",
        StringKeys.TUT_GENERAL              to "General (no subject)",
        // Tutor — Practice
        StringKeys.TUT_PRACTICE             to "Practice",
        StringKeys.TUT_GRADING              to "Grading...",
        StringKeys.TUT_NO_QUESTION          to "No practice question",
        StringKeys.TUT_NO_QUESTION_DESC     to "Practice questions will appear here after a doubt session.",
        StringKeys.TUT_TYPE_ANSWER          to "Type your answer...",
        StringKeys.TUT_SUBMIT_ANSWER        to "Submit Answer",
        StringKeys.TUT_CORRECT              to "Correct!",
        StringKeys.TUT_NOT_QUITE            to "Not quite",
        StringKeys.TUT_SCORE_PCT            to "Score: {pct}%",
        StringKeys.TUT_FEEDBACK             to "Feedback",
        StringKeys.TUT_NEXT_QUESTION        to "Next Question",
        // Tutor — Parent Progress
        StringKeys.TUT_PROGRESS_TITLE       to "Tutor Progress",
        StringKeys.TUT_NO_PROGRESS          to "No progress data",
        StringKeys.TUT_NO_PROGRESS_DESC     to "Your child's tutor progress will appear here once they start using the AI tutor.",
        StringKeys.TUT_DOUBTS_RESOLVED      to "Doubts Resolved",
        StringKeys.TUT_ANSWERS_GIVEN        to "Answers Given",
        StringKeys.TUT_SESSIONS             to "Sessions",
        StringKeys.TUT_SAFETY_FLAGS         to "Safety flags: {count}",
        StringKeys.TUT_SAFETY_NOTIFIED      to "The school has been notified. Contact the class teacher for details.",
        StringKeys.TUT_TOPIC_MASTERY        to "Topic Mastery ({count})",
        StringKeys.TUT_TOPIC_LABEL          to "Topic: {topic}...",
        StringKeys.TUT_ATTEMPTS             to "Attempts: {count}",
        StringKeys.TUT_CORRECT_COUNT        to "Correct: {count}",
        StringKeys.TUT_SOURCE_LABEL         to "Source: {source}",
        // SRI Preview
        StringKeys.SRI_ABOVE_MEDIAN         to "Above Lucknow median (7.4)",
        StringKeys.SRI_YOY                  to "+0.3 YoY",
        StringKeys.SRI_ACADEMIC_OUTCOMES    to "Academic outcomes",
        StringKeys.SRI_TEACHER_RETENTION    to "Teacher retention",
        StringKeys.SRI_PARENT_SENTIMENT     to "Parent sentiment",
        StringKeys.SRI_SAFETY_INFRA         to "Safety & infra",
        StringKeys.SRI_CO_CURRICULAR        to "Co-curricular",
        StringKeys.SRI_ATTENDANCE_NORMS     to "Attendance norms",
        // Academic Calendar (additional)
        StringKeys.CAL_ACADEMIC_TITLE       to "Academic calendar",
        StringKeys.CAL_NOT_AVAILABLE        to "Calendar not available",
        StringKeys.CAL_SIGN_IN_PROMPT       to "Sign in with a school account to view the academic calendar.",
        StringKeys.CAL_PREV                 to "‹ Prev",
        StringKeys.CAL_NEXT_BTN             to "Next ›",
        StringKeys.CAL_WORKING_DAYS         to "Working days",
        StringKeys.CAL_UPCOMING_EVENTS      to "Upcoming events",
        StringKeys.CAL_NO_EVENTS            to "No events scheduled for this month.",
        // Discovery
        StringKeys.DISC_DISCOVER            to "Discover",
        StringKeys.DISC_FIND_SCHOOL         to "Find your child's school",
        StringKeys.DISC_EXIT                to "Exit",
        StringKeys.DISC_SEARCH_PH           to "Find schools near you or by name",
        StringKeys.DISC_NO_SCHOOLS          to "No schools yet",
        StringKeys.DISC_NO_MATCHES          to "No matches",
        StringKeys.DISC_SCHOOLS_APPEAR      to "Schools registered on VidyaPrayag will appear here.",
        StringKeys.DISC_TRY_ANOTHER         to "Try another name or city.",
        StringKeys.DISC_SCHOOLS_SELECTED    to "{count} schools selected",
        StringKeys.DISC_SCHOOL_SELECTED     to "{count} school selected",
        StringKeys.DISC_COMPARE_NOW         to "Compare now",
        StringKeys.DISC_SRI_SCORE           to "SRI score",
        StringKeys.DISC_IN_COMPARE          to "In compare",
        StringKeys.DISC_COMPARE             to "Compare",
        StringKeys.DISC_ENQUIRE             to "Enquire",
        StringKeys.DISC_MEDIUM_LABEL        to "{medium} medium",
        StringKeys.DISC_ALREADY_LINKED      to "Already with a partner school?",
        StringKeys.DISC_ALREADY_LINKED_DESC to "If your child's school is already on VidyaPrayag, link your child to see attendance, marks and their full journey.",
        StringKeys.DISC_LINK_CHILD          to "Link your child",
        StringKeys.DISC_SCHOOL_PROFILE      to "School profile",
        StringKeys.DISC_SHARE               to "Share",
        StringKeys.DISC_SAVE_SCHOOL         to "Save school",
        StringKeys.DISC_ENQUIRE_NOW         to "Enquire now",
        StringKeys.DISC_ABOUT               to "About",
        StringKeys.DISC_ACADEMICS           to "Academics",
        StringKeys.DISC_FEE_STRUCTURE       to "Fee structure",
        StringKeys.DISC_SRI_BREAKDOWN       to "SRI breakdown",
        StringKeys.DISC_PARENT_REVIEWS      to "Parent reviews",
        StringKeys.DISC_LOCATION            to "Location",
        StringKeys.DISC_PROFILE_COMING      to "School profile",
        StringKeys.DISC_PROFILE_DESC        to "Rich school descriptions and tags will appear here once schools complete their public profile in the admin portal.",
        StringKeys.DISC_BOARD               to "Board",
        StringKeys.DISC_MEDIUM              to "Medium",
        StringKeys.DISC_CO_ED               to "Co-ed",
        StringKeys.DISC_CLASSES_OFFERED     to "Classes offered",
        StringKeys.DISC_TEACHER_RATIO       to "Teacher–student ratio",
        StringKeys.DISC_COMING_SOON         to "Coming Soon",
        StringKeys.DISC_FEE_COMING          to "Fee structure",
        StringKeys.DISC_FEE_DESC            to "Tuition and one-time fees will appear once the school admin publishes its fee plan.",
        StringKeys.DISC_SRI_TITLE           to "School Reputation Index",
        StringKeys.DISC_SRI_DESC            to "Our 11-signal score lets you compare schools on academics, safety, facilities and parent sentiment.",
        StringKeys.DISC_REVIEWS_TITLE       to "Parent reviews",
        StringKeys.DISC_REVIEWS_DESC        to "Verified-parent reviews launch alongside the family link-child flow.",
        StringKeys.DISC_ON_MAP              to "On the map",
        StringKeys.DISC_MAP_DESC            to "Map embedding ships with the upcoming Maps integration. City: {city}.",
        StringKeys.DISC_SEND_ENQUIRY        to "Send enquiry",
        StringKeys.DISC_ENQUIRY_RESPONSE    to "The admissions team will respond within 2 working days.",
        StringKeys.DISC_YOUR_NAME           to "Your name",
        StringKeys.DISC_CHILD_NAME          to "Child's name",
        StringKeys.DISC_CURRENT_CLASS       to "Current class",
        StringKeys.DISC_APPLY_CLASS         to "Apply for class",
        StringKeys.DISC_MESSAGE_OPT         to "Message (optional)",
        StringKeys.DISC_ANY_QUESTION        to "Any specific question?",
        StringKeys.DISC_SUBMIT_ENQUIRY      to "Submit enquiry",
        StringKeys.DISC_SENT                to "Sent",
        StringKeys.DISC_COMPARE_SCHOOLS     to "Compare schools",
        StringKeys.DISC_CITY                to "City",
        StringKeys.DISC_FEE_RANGE           to "Fee range",
        StringKeys.DISC_DISTANCE            to "Distance",
        StringKeys.DISC_BOARD_RESULT        to "Board result",
        StringKeys.DISC_FEE_NOTE            to "Fee range and board results populate once schools publish their fee plan.",
        StringKeys.DISC_ENQUIRE_ALL         to "Enquire to all selected",
        StringKeys.DISC_ENQUIRIES_SENT      to "Enquiries sent",
        StringKeys.DISC_CO_ED_YES           to "Yes",
        StringKeys.DISC_GIRLS_ONLY          to "Girls only",
        StringKeys.DISC_BOYS_ONLY           to "Boys only",
        StringKeys.DISC_WITHIN_3KM          to "Within 3 km",
        StringKeys.DISC_CBSE                to "CBSE",
        StringKeys.DISC_TYPE                to "Type",
        StringKeys.DISC_SRI_RATING          to "SRI rating",
        // PEWS Student Detail
        StringKeys.PEWS_STUDENT_SIGNAL      to "Student Signal",
        StringKeys.PEWS_NO_SIGNAL           to "No signal on record",
        StringKeys.PEWS_NO_SIGNAL_DESC      to "This student has no early-warning snapshot yet.",
        StringKeys.PEWS_INTERVENTIONS       to "INTERVENTIONS",
        StringKeys.PEWS_HIGH_RISK           to "High risk",
        StringKeys.PEWS_MEDIUM_RISK         to "Medium risk",
        StringKeys.PEWS_WATCH               to "Watch",
        StringKeys.PEWS_UNDER_INTERVENTION  to "Under intervention",
        StringKeys.PEWS_RISK_SCORE          to "Risk score {score} · as of {date}",
        StringKeys.PEWS_ATTENDANCE          to "Attendance",
        StringKeys.PEWS_MARKS               to "Marks",
        StringKeys.PEWS_LEAVES              to "Leaves",
        StringKeys.PEWS_FALLING             to "falling",
        StringKeys.PEWS_RISING              to "rising",
        StringKeys.PEWS_WHY_STUDENT         to "WHY THIS STUDENT",
        StringKeys.PEWS_AI_EXPLANATION      to "AI EXPLANATION",
        StringKeys.PEWS_LIKELY_CAUSE        to "Likely cause",
        StringKeys.PEWS_SUGGESTED_ACTION    to "Suggested action",
        StringKeys.PEWS_GENERATED_BY        to "Generated by {provider} · review before acting",
        StringKeys.PEWS_ESCALATED           to "ESCALATED",
        StringKeys.PEWS_REMINDED            to "REMINDED",
        StringKeys.PEWS_SLA_DAYS            to "SLA: {days} days",
        StringKeys.PEWS_SLA_FOLLOWUP        to "SLA: {days} days · follow-up {date}",
        StringKeys.PEWS_PLAN                to "PLAN",
        StringKeys.PEWS_PARENT_MESSAGE      to "PARENT MESSAGE ({lang})",
        StringKeys.PEWS_OPENED              to "Opened {date}",
        StringKeys.PEWS_START               to "Start",
        StringKeys.PEWS_DISMISS             to "Dismiss",
        StringKeys.PEWS_ADMIN               to "Admin",
        StringKeys.PEWS_TEACHER             to "Teacher",
        StringKeys.PEWS_INITIATED_BY        to "✓ Initiated by {name} ({role})",
        StringKeys.PEWS_SEND_TO_PARENT      to "Send to parent",
        StringKeys.PEWS_DRAFT_PARENT_MSG    to "Draft parent message",
        StringKeys.PEWS_MARK_IMPROVED       to "Mark improved",
        StringKeys.PEWS_NO_CHANGE           to "No change",
        StringKeys.PEWS_OUTCOME             to "Outcome: {outcome}",
        StringKeys.PEWS_HISTORY             to "HISTORY",
        // Health Records
        StringKeys.HLTH_TITLE               to "Health — {name}",
        StringKeys.HLTH_TAB_PROFILE         to "Profile",
        StringKeys.HLTH_TAB_IMMUNIZATIONS   to "Immunizations",
        StringKeys.HLTH_TAB_INCIDENTS       to "Incidents",
        StringKeys.HLTH_BASIC_INFO          to "Basic Info",
        StringKeys.HLTH_BLOOD_GROUP         to "Blood Group",
        StringKeys.HLTH_HEIGHT              to "Height (cm)",
        StringKeys.HLTH_WEIGHT              to "Weight (kg)",
        StringKeys.HLTH_MEDICAL_INFO        to "Medical Info",
        StringKeys.HLTH_ALLERGIES           to "Allergies (JSON array)",
        StringKeys.HLTH_CHRONIC_CONDITIONS  to "Chronic Conditions (JSON array)",
        StringKeys.HLTH_MEDICATIONS         to "Medications (JSON array)",
        StringKeys.HLTH_EMERGENCY_CONTACT   to "Emergency Contact",
        StringKeys.HLTH_CONTACT_NAME        to "Contact Name",
        StringKeys.HLTH_CONTACT_PHONE       to "Contact Phone",
        StringKeys.HLTH_DOCTOR_INFO         to "Doctor Info",
        StringKeys.HLTH_DOCTOR_NAME         to "Doctor Name",
        StringKeys.HLTH_DOCTOR_PHONE        to "Doctor Phone",
        StringKeys.HLTH_SAVE_PROFILE        to "Save Health Profile",
        StringKeys.HLTH_IMMUNIZATION_RECORDS to "Immunization Records",
        StringKeys.HLTH_ADD                 to "Add",
        StringKeys.HLTH_VACCINE_NAME        to "Vaccine Name",
        StringKeys.HLTH_DOSE_NUMBER         to "Dose Number",
        StringKeys.HLTH_DATE_ADMINISTERED   to "Date Administered",
        StringKeys.HLTH_NEXT_DUE            to "Next Due Date (optional)",
        StringKeys.HLTH_ADMINISTERED_BY     to "Administered By (optional)",
        StringKeys.HLTH_SAVE_RECORD         to "Save Record",
        StringKeys.HLTH_NO_IMMUNIZATIONS    to "No immunization records yet",
        StringKeys.HLTH_DOSE                to "Dose {number} · {date}",
        StringKeys.HLTH_BY                  to "By {name}",
        StringKeys.HLTH_NEXT_DUE_LABEL      to "Next due: {date}",
        StringKeys.HLTH_HEALTH_INCIDENTS    to "Health Incidents",
        StringKeys.HLTH_LOG                 to "Log",
        StringKeys.HLTH_DATE                to "Date",
        StringKeys.HLTH_TIME                to "Time (optional)",
        StringKeys.HLTH_DESCRIPTION         to "Description",
        StringKeys.HLTH_TREATMENT           to "Treatment (optional)",
        StringKeys.HLTH_MEDICATION_GIVEN    to "Medication Given (optional)",
        StringKeys.HLTH_SEVERITY            to "Severity",
        StringKeys.HLTH_LOG_INCIDENT        to "Log Incident",
        StringKeys.HLTH_NO_INCIDENTS        to "No health incidents logged",
        StringKeys.HLTH_TREATMENT_LABEL     to "Treatment: {treatment}",
        StringKeys.HLTH_MEDICATION_LABEL    to "Medication: {medication}",
        StringKeys.HLTH_TIME_LABEL          to "Time: {time}",
        StringKeys.HLTH_PARENT_NOTIFIED     to "Parent notified",
        StringKeys.HLTH_MARK_NOTIFIED       to "Mark Notified",
        StringKeys.HLTH_SEVERITY_MAJOR      to "MAJOR",
        StringKeys.HLTH_SEVERITY_MODERATE   to "MODERATE",
        StringKeys.HLTH_SEVERITY_MINOR      to "MINOR",
        // ID Card Templates
        StringKeys.IDCARD_TOTAL_CARDS       to "Total Cards",
        StringKeys.IDCARD_STUDENTS          to "Students",
        StringKeys.IDCARD_TEACHERS          to "Teachers",
        StringKeys.IDCARD_STAFF             to "Staff",
        StringKeys.IDCARD_MILESTONE_MASTER  to "ID Card Master",
        StringKeys.IDCARD_MILESTONE_CENTURY to "Century Club",
        StringKeys.IDCARD_MILESTONE_HALF    to "Half Century",
        StringKeys.IDCARD_MILESTONE_FIRST   to "First Steps",
        StringKeys.IDCARD_MILESTONE_START   to "Getting Started",
        StringKeys.IDCARD_NO_TEMPLATES      to "No templates yet",
        StringKeys.IDCARD_NO_TEMPLATES_DESC to "Create your first ID card template with the visual builder below.",
        StringKeys.IDCARD_CREATE_NEW        to "CREATE NEW TEMPLATE",
        StringKeys.IDCARD_TEMPLATE_NAME     to "Template Name",
        StringKeys.IDCARD_CARD_TYPE         to "Card Type",
        StringKeys.IDCARD_STUDENT           to "Student",
        StringKeys.IDCARD_TEACHER_ROLE      to "Teacher",
        StringKeys.IDCARD_STAFF_ROLE        to "Staff",
        StringKeys.IDCARD_FIELDS_DISPLAY    to "Fields to Display",
        StringKeys.IDCARD_FIELD_NAME        to "Name",
        StringKeys.IDCARD_FIELD_ROLE        to "Role",
        StringKeys.IDCARD_FIELD_CLASS       to "Class",
        StringKeys.IDCARD_FIELD_SCHOOL      to "School",
        StringKeys.IDCARD_FIELD_PHOTO       to "Photo",
        StringKeys.IDCARD_FIELD_QR          to "QR on Front",
        StringKeys.IDCARD_FIELD_EMERGENCY   to "Emergency",
        StringKeys.IDCARD_FIELD_BLOOD       to "Blood Group",
        StringKeys.IDCARD_ACCENT_COLOR      to "Accent Color",
        StringKeys.IDCARD_LIVE_PREVIEW      to "Live Preview",
        StringKeys.IDCARD_PREVIEW           to "Preview",
        StringKeys.IDCARD_CREATING          to "Creating...",
        StringKeys.IDCARD_CREATE_BTN        to "Create Template",
        StringKeys.IDCARD_ID_CARD           to "ID CARD",
        StringKeys.IDCARD_SCAN_QR           to "Scan QR to verify",
        StringKeys.IDCARD_ACTIVE            to "Active",
        StringKeys.IDCARD_INACTIVE          to "Inactive",
        StringKeys.IDCARD_DEACTIVATE        to "Deactivate",

        // Branding Settings
        StringKeys.BRAND_TITLE              to "Branding Kit",
        StringKeys.BRAND_RESET_TITLE        to "Reset branding?",
        StringKeys.BRAND_RESET_MSG          to "All colors will be reset to defaults. Your uploaded assets will be kept.",
        StringKeys.BRAND_RESET_BTN          to "Reset",
        StringKeys.BRAND_CUSTOMIZED         to "Customized",
        StringKeys.BRAND_DEFAULT            to "Default",
        StringKeys.BRAND_COLORS             to "Brand Colors",
        StringKeys.BRAND_PRIMARY_COLOR      to "Primary Color",
        StringKeys.BRAND_SECONDARY_COLOR    to "Secondary Color",
        StringKeys.BRAND_ACCENT_COLOR       to "Accent Color",
        StringKeys.BRAND_SAVE_COLORS        to "Save Colors",
        StringKeys.BRAND_ASSETS             to "Brand Assets",
        StringKeys.BRAND_ASSETS_DESC        to "Upload your school's logo, app icon, and splash screen. These appear on the login screen, splash, and app icon.",
        StringKeys.BRAND_LOGO               to "Logo",
        StringKeys.BRAND_DARK_LOGO          to "Dark Logo",
        StringKeys.BRAND_FAVICON            to "Favicon",
        StringKeys.BRAND_APP_ICON           to "App Icon",
        StringKeys.BRAND_SPLASH             to "Splash Screen",
        StringKeys.BRAND_LOGIN_BG           to "Login Background",
        StringKeys.BRAND_SUBDOMAIN          to "Custom Subdomain",
        StringKeys.BRAND_SUBDOMAIN_DESC     to "Set a custom web address for your school's portal, e.g. dpsrkpuram.vidyaprayag.com",
        StringKeys.BRAND_CURRENT_SUBDOMAIN  to "Current subdomain",
        StringKeys.BRAND_REMOVE             to "Remove",
        StringKeys.BRAND_SUBDOMAIN_LABEL    to "Subdomain",
        StringKeys.BRAND_SUBDOMAIN_PLACE    to "e.g. dpsrkpuram",
        StringKeys.BRAND_SUBDOMAIN_HINT     to "4-32 chars, lowercase letters, numbers & hyphens",
        StringKeys.BRAND_CHECK              to "Check",
        StringKeys.BRAND_ASSIGN             to "Assign",
        StringKeys.BRAND_SUBDOMAIN_AVAIL    to "Subdomain is available!",
        StringKeys.BRAND_SUBDOMAIN_TAKEN    to "Subdomain is already taken.",
        StringKeys.BRAND_RESET_DEFAULTS     to "Reset to Defaults",
        StringKeys.BRAND_LIVE_PREVIEW       to "Live Preview",
        StringKeys.BRAND_YOUR_SCHOOL        to "Your School",
        StringKeys.BRAND_PRIMARY_BTN        to "Primary Button",
        StringKeys.BRAND_SECONDARY_BTN      to "Secondary",
        StringKeys.BRAND_SWATCH_PRIMARY     to "Primary",
        StringKeys.BRAND_SWATCH_SECONDARY   to "Secondary",
        StringKeys.BRAND_SWATCH_ACCENT      to "Accent",
        StringKeys.BRAND_HEX_COLOR          to "Hex color",
        StringKeys.BRAND_UPLOADED           to "Uploaded",
        StringKeys.BRAND_NOT_SET            to "Not set",
        StringKeys.BRAND_REPLACE            to "Replace",
        StringKeys.BRAND_UPLOAD             to "Upload",

        // Transport Management
        StringKeys.TRANS_TITLE              to "Transport Management",
        StringKeys.TRANS_ROUTES             to "Routes ({count})",
        StringKeys.TRANS_ADD_ROUTE          to "+ Add Route",
        StringKeys.TRANS_VEHICLES           to "Vehicles ({count})",
        StringKeys.TRANS_ADD_VEHICLE        to "+ Add Vehicle",
        StringKeys.TRANS_ASSIGNMENTS        to "Student Assignments ({count})",
        StringKeys.TRANS_ASSIGN             to "+ Assign",
        StringKeys.TRANS_NEW_ROUTE          to "New Route",
        StringKeys.TRANS_ROUTE_NAME         to "Route name",
        StringKeys.TRANS_ROUTE_PLACE        to "e.g. Route A — North Sector",
        StringKeys.TRANS_DESC_OPTIONAL      to "Description (optional)",
        StringKeys.TRANS_DESC_PLACE         to "Covers northern residential areas",
        StringKeys.TRANS_CREATE_ROUTE       to "Create Route",
        StringKeys.TRANS_NEW_VEHICLE        to "New Vehicle",
        StringKeys.TRANS_BUS_NUMBER         to "Bus number",
        StringKeys.TRANS_BUS_PLACE          to "e.g. KA-01-AB-1234",
        StringKeys.TRANS_CAPACITY           to "Capacity",
        StringKeys.TRANS_DRIVER_NAME        to "Driver name (optional)",
        StringKeys.TRANS_DRIVER_PHONE       to "Driver phone (optional)",
        StringKeys.TRANS_ASSIGN_ROUTE       to "Assign to route (optional):",
        StringKeys.TRANS_CREATE_VEHICLE     to "Create Vehicle",
        StringKeys.TRANS_ASSIGN_STUDENT     to "Assign Student to Route",
        StringKeys.TRANS_STUDENT_ID         to "Student ID",
        StringKeys.TRANS_STUDENT_ID_PLACE   to "Paste student UUID",
        StringKeys.TRANS_SELECT_ROUTE       to "Select route:",
        StringKeys.TRANS_SELECT_STOP        to "Select stop:",
        StringKeys.TRANS_SELECT_VEHICLE     to "Select vehicle:",
        StringKeys.TRANS_FEE_AMOUNT         to "Transport fee amount (optional)",
        StringKeys.TRANS_FEE_PLACE          to "e.g. 6000",
        StringKeys.TRANS_FEE_DUE_DATE       to "Fee due date (optional)",
        StringKeys.TRANS_ASSIGN_BTN         to "Assign Student",
        StringKeys.TRANS_STOPS              to "{count} stops",
        StringKeys.TRANS_ACTIVE             to "Active",
        StringKeys.TRANS_INACTIVE           to "Inactive",
        StringKeys.TRANS_CAPACITY_LABEL     to "Capacity: {count}",
        StringKeys.TRANS_DRIVER_LABEL       to "Driver: {name}",
        StringKeys.TRANS_ROUTE_LABEL        to "Route: {name}",
        StringKeys.TRANS_STOP_LABEL         to "Stop: {name}",
        StringKeys.TRANS_BUS_LABEL          to "Bus: {name}",
        StringKeys.TRANS_DEACTIVATE         to "Deactivate",

        // Academic Calendar Platform
        StringKeys.ACALP_TITLE              to "Academic Calendar",
        StringKeys.ACALP_CREATE             to "Create",
        StringKeys.ACALP_EMPTY_TITLE        to "No calendar yet",
        StringKeys.ACALP_EMPTY_BODY         to "Create your first academic event to start planning the year.",
        StringKeys.ACALP_HIGHLIGHTS         to "Upcoming highlights",
        StringKeys.ACALP_VIEW               to "View",
        StringKeys.ACALP_UPCOMING           to "Upcoming events",
        StringKeys.ACALP_DRAFT_EVENTS       to "Draft events",
        StringKeys.ACALP_PUBLISHED_EVENTS   to "Published events",
        StringKeys.ACALP_MILESTONES         to "Academic milestones",
        StringKeys.ACALP_ANALYTICS          to "Calendar analytics",
        StringKeys.ACALP_ACADEMIC_YEAR      to "Academic Year {year}",
        StringKeys.ACALP_ACADEMIC_CAL       to "Academic Calendar",
        StringKeys.ACALP_CENTRALIZED        to "Centralized planning & scheduling",
        StringKeys.ACALP_EVENTS             to "Events",
        StringKeys.ACALP_SCHOOL_DAYS        to "School days",
        StringKeys.ACALP_HOLIDAYS           to "Holidays",
        StringKeys.ACALP_NEXT_EVENT         to "NEXT EVENT",
        StringKeys.ACALP_NO_EVENTS          to "No events to show.",
        StringKeys.ACALP_NOTHING_UPCOMING   to "Nothing upcoming.",
        StringKeys.ACALP_CONFLICT           to "Potential Schedule Conflict",

        // Class Detail
        StringKeys.CD_STUDENTS              to "Students",
        StringKeys.CD_TEACHERS              to "Teachers",
        StringKeys.CD_TIMETABLE             to "Timetable",
        StringKeys.CD_ANALYTICS             to "Analytics",
        StringKeys.CD_NO_STUDENTS           to "No students",
        StringKeys.CD_NO_STUDENTS_BODY      to "No students found in {className}.",
        StringKeys.CD_STUDENTS_COUNT        to "{count} Students",
        StringKeys.CD_SEC                   to "Sec {section}",
        StringKeys.CD_ROLL                  to "Roll {number}",
        StringKeys.CD_ATTENDANCE            to "{percent}% attendance",
        StringKeys.CD_LOADING_TIMETABLE     to "Loading timetable…",
        StringKeys.CD_NO_TEACHERS           to "No teachers assigned",
        StringKeys.CD_NO_TEACHERS_BODY      to "Teachers will appear here once you assign periods in the Schedule tab.",
        StringKeys.CD_TEACHERS_COUNT        to "{count} Teachers",
        StringKeys.CD_NO_TIMETABLE          to "No timetable yet",
        StringKeys.CD_NO_TIMETABLE_BODY     to "Build the timetable in the Schedule tab.",
        StringKeys.CD_WEEKLY_TIMETABLE      to "{className} — Weekly Timetable",
        StringKeys.CD_NO_PERIODS            to "No periods for {day}",
        StringKeys.CD_NO_PERIODS_BODY       to "No classes scheduled for {className} on this day.",
        StringKeys.CD_ROOM                  to "Room {room}",
        StringKeys.CD_NO_ANALYTICS          to "No analytics yet",
        StringKeys.CD_NO_ANALYTICS_BODY     to "Class-level analytics will appear here once teachers post marks and attendance.",
        StringKeys.CD_AVG_PROFICIENCY       to "Avg proficiency",
        StringKeys.CD_ACTIVE_STUDENTS       to "Active students",
        StringKeys.CD_MEDIAN_GRADE          to "Median grade",
        StringKeys.CD_GRADE_DIST            to "GRADE DISTRIBUTION",
        StringKeys.CD_SUBJECT_MATRIX        to "SUBJECT MATRIX",
        StringKeys.CD_EARLY_WARNING         to "EARLY WARNING",
        StringKeys.CD_CRITICAL              to "Critical",
        StringKeys.CD_MODERATE              to "Moderate",
        StringKeys.CD_ON_TARGET             to "On target",
        StringKeys.CD_TOP_PERFORMER         to "TOP PERFORMER",
        StringKeys.CD_PROGRESS_MONITORING   to "PROGRESS MONITORING",
        StringKeys.CD_TREND_UP              to "▲ Up",
        StringKeys.CD_TREND_DOWN            to "▼ Down",
        StringKeys.CD_TREND_FLAT            to "● Flat",
        StringKeys.CD_MATH                  to "Math",
        StringKeys.CD_SCI                   to "Sci",
        StringKeys.CD_LIT                   to "Lit",
        StringKeys.CD_ATTENDANCE_LABEL      to "Attendance {percent}",

        // Scholarship Management
        StringKeys.SCH_MGMT_TITLE           to "Scholarship Management",
        StringKeys.SCH_CREATE_NEW           to "+ Create New Scheme",
        StringKeys.SCH_NO_SCHEMES           to "No scholarship schemes yet",
        StringKeys.SCH_NO_SCHEMES_BODY      to "Tap \"Create New Scheme\" above to add one.",
        StringKeys.SCH_NO_APPLICATIONS      to "No applications to review",
        StringKeys.SCH_NO_APPLICATIONS_BODY to "Applications will appear here when parents apply.",
        StringKeys.SCH_NO_RENEWALS          to "No renewal requests",
        StringKeys.SCH_NO_RENEWALS_BODY     to "Renewal requests will appear here.",
        StringKeys.SCH_APPLICATIONS         to "APPLICATIONS ({count})",
        StringKeys.SCH_RENEWALS             to "RENEWALS ({count})",
        StringKeys.SCH_SCHEMES              to "Schemes",
        StringKeys.SCH_TAB_APPLICATIONS     to "Applications",
        StringKeys.SCH_TAB_RENEWALS         to "Renewals",
        StringKeys.SCH_RENEWABLE            to "Renewable",
        StringKeys.SCH_AWARD                to "Award",
        StringKeys.SCH_ELIGIBILITY          to "Eligibility",
        StringKeys.SCH_EDIT                 to "Edit",
        StringKeys.SCH_REVIEW               to "Review",
        StringKeys.SCH_REMARKS              to "Remarks",
        StringKeys.SCH_DISBURSEMENT_AMT     to "Disbursement Amount (optional)",
        StringKeys.SCH_APPROVE              to "Approve",
        StringKeys.SCH_REJECT               to "Reject",
        StringKeys.SCH_DISBURSEMENT_REF     to "Disbursement Reference",
        StringKeys.SCH_RECORD_DISBURSEMENT  to "Record Disbursement",
        StringKeys.SCH_DISBURSED            to "Disbursed: {amount}",
        StringKeys.SCH_REF                  to "Ref: {ref}",
        StringKeys.SCH_RENEWAL_FOR          to "Renewal for academic year",
        StringKeys.SCH_DOCUMENTS            to "{count} document(s) attached",
        StringKeys.SCH_DELETE_TITLE         to "Delete Scholarship",
        StringKeys.SCH_DELETE_MSG           to "Are you sure you want to deactivate \"{title}\"? This will remove it from the parent view but existing applications will be preserved.",
        StringKeys.SCH_EDIT_SCHEME          to "Edit Scholarship Scheme",
        StringKeys.SCH_CREATE_SCHEME        to "Create Scholarship Scheme",
        StringKeys.SCH_TITLE_LABEL          to "Title *",
        StringKeys.SCH_DESCRIPTION          to "Description",
        StringKeys.SCH_DISPLAY_AMOUNT       to "Display Amount (e.g. ₹5,000)",
        StringKeys.SCH_NUMERIC_AMOUNT       to "Numeric Amount (for fixed type)",
        StringKeys.SCH_TYPE                 to "Type",
        StringKeys.SCH_FIXED                to "Fixed Amount",
        StringKeys.SCH_FULL_WAIVER          to "Full Waiver",
        StringKeys.SCH_PARTIAL_WAIVER       to "Partial Waiver",
        StringKeys.SCH_WAIVER_PCT           to "Waiver Percentage (0-100)",
        StringKeys.SCH_ELIGIBILITY_CRIT     to "Eligibility Criteria",
        StringKeys.SCH_CATEGORY             to "Category",
        StringKeys.SCH_MGMT_START_DATE      to "Start Date",
        StringKeys.SCH_MGMT_END_DATE        to "End Date (Application Deadline)",
        StringKeys.SCH_RENEWABLE_LABEL      to "Renewable",
        StringKeys.SCH_RENEWAL_PERIOD       to "Renewal Period (months)",
        StringKeys.SCH_UPDATE               to "Update",

        // Library UIX Components EN
        StringKeys.LIB_UIX_OVERDUE            to "{count}d overdue",
        StringKeys.LIB_UIX_DUE_TODAY          to "Due today",
        StringKeys.LIB_UIX_DUE_LEFT           to "{count}d left",
        StringKeys.LIB_UIX_FINE_AMOUNT        to "Fine: ₹{amount}",
        StringKeys.LIB_UIX_FINE_CAP           to "Cap: ₹{amount}",
        StringKeys.LIB_UIX_NO_CAP             to "No cap",
        StringKeys.LIB_UIX_FINE_CAPPED        to "Fine capped at replacement cost",
        StringKeys.LIB_UIX_FINE_NO_CAP        to "₹{amount} (no cap)",
        StringKeys.LIB_UIX_COVER_FOR          to "Cover for {title}",
        StringKeys.LIB_UIX_AVAILABILITY       to "{available}/{total} available",
        StringKeys.LIB_UIX_FILTERS            to "Filters",
        StringKeys.LIB_UIX_CATEGORY           to "Category",
        StringKeys.LIB_UIX_ALL                to "All",
        StringKeys.LIB_UIX_AVAIL_LABEL        to "Availability",
        StringKeys.LIB_UIX_AVAILABLE_ONLY     to "Available Only",
        StringKeys.LIB_UIX_SORT_BY            to "Sort By",
        StringKeys.LIB_UIX_SORT_NEWEST        to "Newest",
        StringKeys.LIB_UIX_SORT_TITLE_AZ      to "Title A-Z",
        StringKeys.LIB_UIX_SORT_AUTHOR        to "Author",
        StringKeys.LIB_UIX_SORT_POPULAR       to "Popular",
        StringKeys.LIB_UIX_CLEAR              to "Clear",
        StringKeys.LIB_UIX_APPLY_FILTERS      to "Apply Filters",
        StringKeys.LIB_UIX_ISSUES_COUNT       to "{count} issues",
        StringKeys.LIB_UIX_AVAILABLE_SOON     to "Available soon",
        StringKeys.LIB_UIX_AVAILABLE_IN       to "Available in ~{days}d",
        StringKeys.LIB_UIX_AHEAD              to "({count} ahead)",
        StringKeys.LIB_UIX_MONTHLY            to "Monthly",
        StringKeys.LIB_UIX_CATEGORIES         to "Categories",
        StringKeys.LIB_UIX_NO_DATA            to "No data",
        StringKeys.LIB_UIX_QUICK_ACTIONS      to "Quick actions",
        StringKeys.LIB_UIX_SELECT_BOOK        to "Select a book",
        StringKeys.LIB_UIX_RENEW              to "Renew",
        StringKeys.LIB_UIX_MAX                to "Max",
        StringKeys.LIB_UIX_RETURN             to "Return",
        StringKeys.LIB_UIX_RECENTLY_VIEWED    to "Recently Viewed",
        StringKeys.LIB_UIX_GOOD_MORNING       to "Good morning",
        StringKeys.LIB_UIX_GOOD_AFTERNOON     to "Good afternoon",
        StringKeys.LIB_UIX_GOOD_EVENING       to "Good evening",
        StringKeys.LIB_UIX_OVERDUE_BOOKS      to "You have {count} overdue book(s)",
        StringKeys.LIB_UIX_DUE_TOMORROW       to "You have {count} book(s) due tomorrow",
        StringKeys.LIB_UIX_READY_FOR_PICKUP   to "{count} book(s) ready for pickup",
        StringKeys.LIB_UIX_READY_TO_EXPLORE   to "Ready to explore?",
        StringKeys.LIB_UIX_READ_LESS          to "Read less",
        StringKeys.LIB_UIX_READ_MORE          to "Read more",
        StringKeys.LIB_UIX_READING_TIME       to "≈ {hours} hours ({pages} pages)",
        StringKeys.LIB_UIX_SCAN_TO_VIEW       to "Scan to view",
        StringKeys.LIB_UIX_GOT_IT             to "Got it",
        StringKeys.LIB_UIX_AVAILABLE          to "Available",
        StringKeys.LIB_UIX_AZ                 to "A-Z",
        StringKeys.LIB_UIX_LESS_FILTERS       to "Less filters",
        StringKeys.LIB_UIX_MORE_FILTERS       to "More filters",
        StringKeys.LIB_UIX_READING_STREAK     to "Reading Streak",
        StringKeys.LIB_UIX_CURRENT_STREAK     to "Current: {count} days",
        StringKeys.LIB_UIX_LONGEST_STREAK     to "Longest: {count} days",
        StringKeys.LIB_UIX_DONT_BREAK_CHAIN   to "Don't break the chain!",
        StringKeys.LIB_UIX_GRID               to "GRID",
        StringKeys.LIB_UIX_LIST               to "LIST",
        StringKeys.LIB_UIX_SHELF              to "SHELF",
        StringKeys.LIB_UIX_BOOK_OF_MONTH      to "Book of the Month",
        StringKeys.LIB_UIX_BOOK_OF_WEEK       to "Book of the Week",
        StringKeys.LIB_UIX_SEARCH_PLACEHOLDER to "Search books, authors...",
        StringKeys.LIB_UIX_QUICK_ISSUE        to "Quick Issue",
        StringKeys.LIB_UIX_STEP               to "Step {step}/3",
        StringKeys.LIB_UIX_CONFIRM_BOOK       to "Confirm Book",
        StringKeys.LIB_UIX_NO_BOOK_SELECTED   to "No book selected. Search and select a book first.",
        StringKeys.LIB_UIX_BORROWER_DETAILS   to "Borrower Details",
        StringKeys.LIB_UIX_BORROWER_NAME      to "Borrower name",
        StringKeys.LIB_UIX_ENTER_NAME         to "Enter name",
        StringKeys.LIB_UIX_REVIEW_CONFIRM     to "Review & Confirm",
        StringKeys.LIB_UIX_BOOK_LABEL         to "Book: {title}",
        StringKeys.LIB_UIX_AUTHOR_LABEL       to "Author: {name}",
        StringKeys.LIB_UIX_UNKNOWN            to "Unknown",
        StringKeys.LIB_UIX_BORROWER_LABEL     to "Borrower: {name}",
        StringKeys.LIB_UIX_DUE_DATE_14        to "Due date: 14 days from today",
        StringKeys.LIB_UIX_ISSUE_BOOK         to "Issue Book",
        // ── StudentLibraryScreen ──
        StringKeys.STU_LIB_TAB_BROWSE          to "Browse",
        StringKeys.STU_LIB_TAB_MY_BOOKS        to "My Books",
        StringKeys.STU_LIB_TAB_HISTORY         to "History",
        StringKeys.STU_LIB_TAB_WISHLIST        to "Wishlist",
        StringKeys.STU_LIB_TAB_RESERVATIONS    to "Reservations",
        StringKeys.STU_LIB_TAB_REQUESTS        to "Requests",
        StringKeys.STU_LIB_TAB_PROFILE         to "Profile",
        StringKeys.STU_LIB_TAB_BADGES          to "Badges",
        StringKeys.STU_LIB_TAB_DISCUSSIONS     to "Discussions",
        StringKeys.STU_LIB_TITLE               to "Library",
        StringKeys.STU_LIB_OFFLINE_CACHED      to "Offline — showing cached data",
        StringKeys.STU_LIB_OFFLINE_CHECK       to "Offline — check your connection",
        StringKeys.STU_LIB_COACHMARK_TITLE     to "Welcome to Library!",
        StringKeys.STU_LIB_COACHMARK_MSG       to "Search for any book by title, author, or ISBN. Use filters to narrow down results.",
        StringKeys.STU_LIB_READER              to "Reader",
        StringKeys.STU_LIB_SEARCH_BOOKS        to "Search books",
        StringKeys.STU_LIB_SEARCH              to "Search",
        StringKeys.STU_LIB_TRENDING_NOW        to "Trending Now",
        StringKeys.STU_LIB_ISSUES_COUNT        to "{count} issues",
        StringKeys.STU_LIB_RECOMMENDED         to "Recommended For You",
        StringKeys.STU_LIB_WHY                 to "Why: {reason}",
        StringKeys.STU_LIB_NO_BOOKS_FOUND      to "No books found",
        StringKeys.STU_LIB_TRY_DIFFERENT       to "Try a different search query.",
        StringKeys.STU_LIB_BOOKS_COUNT         to "{count} books",
        StringKeys.STU_LIB_LOAD_MORE           to "Load More ({remaining} remaining)",
        StringKeys.STU_LIB_RESERVE             to "Reserve",
        StringKeys.STU_LIB_ADD_WISHLIST        to "+ Wishlist",
        StringKeys.STU_LIB_MY_PROFILE          to "My Library Profile",
        StringKeys.STU_LIB_BOOKS_READ          to "Books Read",
        StringKeys.STU_LIB_CURRENTLY_ISSUED    to "Currently Issued",
        StringKeys.STU_LIB_OVERDUE             to "Overdue",
        StringKeys.STU_LIB_OUTSTANDING_FINE    to "Outstanding Fine",
        StringKeys.STU_LIB_CURRENT_STREAK      to "Current Streak",
        StringKeys.STU_LIB_LONGEST_STREAK      to "Longest Streak",
        StringKeys.STU_LIB_STREAK_DAYS         to "{count} days",
        StringKeys.STU_LIB_FINE_AMOUNT         to "Fine: ₹{amount}",
        StringKeys.STU_LIB_READING_GOAL        to "Reading Goal",
        StringKeys.STU_LIB_GOAL_ACHIEVED       to "Goal achieved! 🎉",
        StringKeys.STU_LIB_SET_READING_GOAL    to "Set Reading Goal",
        StringKeys.STU_LIB_GOAL_COUNT          to "Goal (number of books)",
        StringKeys.STU_LIB_PERIOD              to "Period",
        StringKeys.STU_LIB_MONTHLY             to "Monthly",
        StringKeys.STU_LIB_QUARTERLY           to "Quarterly",
        StringKeys.STU_LIB_YEARLY              to "Yearly",
        StringKeys.STU_LIB_TARGET_YEAR         to "Target Year",
        StringKeys.STU_LIB_SET_GOAL            to "Set Goal",
        StringKeys.STU_LIB_NO_BADGES           to "No badges yet",
        StringKeys.STU_LIB_READ_MORE_BADGES    to "Read more books to earn badges!",
        StringKeys.STU_LIB_EARNED_ON           to "Earned: {date}",
        StringKeys.STU_LIB_EARNED              to "Earned",
        StringKeys.STU_LIB_LOCKED              to "Locked",
        StringKeys.STU_LIB_NO_BOOKS_ISSUED     to "No books issued",
        StringKeys.STU_LIB_BROWSE_TO_ISSUE     to "Browse the library and issue a book to get started.",
        StringKeys.STU_LIB_RENEWALS            to "Renewals: {count}/2",
        StringKeys.STU_LIB_RENEW               to "Renew",
        StringKeys.STU_LIB_READING_HISTORY     to "Reading History",
        StringKeys.STU_LIB_NO_HISTORY          to "No history yet",
        StringKeys.STU_LIB_HISTORY_APPEAR      to "Your reading history will appear here.",
        StringKeys.STU_LIB_MY_WISHLIST         to "My Wishlist",
        StringKeys.STU_LIB_WISHLIST_EMPTY      to "Wishlist is empty",
        StringKeys.STU_LIB_WISHLIST_EMPTY_BODY to "Add books to your wishlist to read later.",
        StringKeys.STU_LIB_REMOVE              to "Remove",
        StringKeys.STU_LIB_MY_RESERVATIONS     to "My Reservations",
        StringKeys.STU_LIB_NO_RESERVATIONS     to "No reservations",
        StringKeys.STU_LIB_RESERVE_FROM_BROWSE to "Reserve a book from the Browse tab to see it here.",
        StringKeys.STU_LIB_RESERVED_ON         to "Reserved: {date}",
        StringKeys.STU_LIB_CANCEL_RESERVATION_TITLE to "Cancel Reservation?",
        StringKeys.STU_LIB_CANCEL_RESERVATION_MSG to "Are you sure you want to cancel this reservation?",
        StringKeys.STU_LIB_CANCEL_RESERVATION_BTN to "Cancel Reservation",
        StringKeys.STU_LIB_KEEP                to "Keep",
        StringKeys.STU_LIB_ACQUISITION_REQUESTS to "Acquisition Requests",
        StringKeys.STU_LIB_NO_REQUESTS         to "No requests",
        StringKeys.STU_LIB_REQUESTS_APPEAR     to "Your book acquisition requests will appear here.",
        StringKeys.STU_LIB_AUTHOR_LABEL        to "Author: {name}",
        StringKeys.STU_LIB_ISBN_LABEL          to "ISBN: {isbn}",
        StringKeys.STU_LIB_REASON_LABEL        to "Reason: {reason}",
        StringKeys.STU_LIB_BOOK_DISCUSSIONS    to "Book Discussions",
        StringKeys.STU_LIB_BOOK_ID             to "Book ID",
        StringKeys.STU_LIB_LOAD_DISCUSSIONS    to "Load Discussions",
        StringKeys.STU_LIB_NO_DISCUSSIONS      to "No discussions",
        StringKeys.STU_LIB_ENTER_BOOK_ID       to "Enter a book ID to view and join discussions.",
        StringKeys.STU_LIB_WRITE_MESSAGE       to "Write a message",
        StringKeys.STU_LIB_POST                to "Post",
        // ── SchoolPeopleScreenV2 ──
        StringKeys.PPL_TITLE                   to "People",
        StringKeys.PPL_LINK_REQUESTS_TITLE     to "Child link requests",
        StringKeys.PPL_LINK_REQUESTS_SUB       to "Review parents requesting access to student records",
        StringKeys.PPL_TAB_TEACHERS            to "Teachers",
        StringKeys.PPL_TAB_STUDENTS            to "Students",
        StringKeys.PPL_TAB_STAFF               to "Non-teaching staff",
        StringKeys.PPL_TAB_ALUMNI              to "Alumni",
        StringKeys.PPL_ALUMNI_MGMT_TITLE       to "Alumni Management",
        StringKeys.PPL_ALUMNI_MGMT_SUB         to "View alumni directory, donations, mentorship, and analytics",
        StringKeys.PPL_ADD_TEACHER             to "Add teacher",
        StringKeys.PPL_SEARCH_TEACHERS         to "Search by name, role or subject",
        StringKeys.PPL_NO_TEACHERS             to "No teachers yet",
        StringKeys.PPL_NO_MATCHES              to "No matches",
        StringKeys.PPL_NO_TEACHERS_BODY        to "Add your first teacher so they can sign in and manage their classes.",
        StringKeys.PPL_NO_TEACHER_MATCHES      to "No teacher matches \"{query}\".",
        StringKeys.PPL_LOADING                 to "Loading…",
        StringKeys.PPL_LOAD_MORE               to "Load more",
        StringKeys.PPL_UNNAMED_TEACHER         to "Unnamed teacher",
        StringKeys.PPL_ACTIVE                  to "Active",
        StringKeys.PPL_INACTIVE                to "Inactive",
        StringKeys.PPL_GRADES                  to "Grades",
        StringKeys.PPL_NO_GRADES               to "No grades assigned",
        StringKeys.PPL_SUBJECTS                to "Subjects",
        StringKeys.PPL_NO_SUBJECTS             to "No subjects assigned",
        StringKeys.PPL_CLASSES                 to "Classes",
        StringKeys.PPL_STUDENTS_LABEL          to "Students",
        StringKeys.PPL_ATTENDANCE_PCT          to "Attendance {pct}%",
        StringKeys.PPL_ATTENDANCE_NONE         to "Attendance —",
        StringKeys.PPL_NEVER_ACTIVE            to "Never active",
        StringKeys.PPL_ACTIVE_DATE             to "Active {date}",
        StringKeys.PPL_VIEW_PROFILE            to "View Profile",
        StringKeys.PPL_MORE_ACTIONS            to "More actions",
        StringKeys.PPL_ASSIGN_CLASSES          to "Assign classes",
        StringKeys.PPL_DEACTIVATE              to "Deactivate",
        StringKeys.PPL_ADD_STUDENT             to "Add student",
        StringKeys.PPL_IMPORT_CSV              to "Import CSV",
        StringKeys.PPL_GRADUATE                to "Graduate",
        StringKeys.PPL_SEARCH_STUDENTS         to "Search by name, roll no. or code",
        StringKeys.PPL_NO_STUDENTS             to "No students yet",
        StringKeys.PPL_NO_STUDENTS_BODY        to "Students appear here once they are enrolled in your school.",
        StringKeys.PPL_NO_STUDENT_MATCHES      to "No student matches \"{query}\".",
        StringKeys.PPL_COHORT_ANALYTICS        to "Cohort analytics",
        StringKeys.PPL_NO_COHORT_DATA          to "No cohort data yet",
        StringKeys.PPL_NO_COHORT_BODY          to "Student risk and engagement analytics appear here once attendance and marks start flowing in.",
        StringKeys.PPL_RISK_DISTRIBUTION       to "Student risk distribution",
        StringKeys.PPL_CRITICAL                to "Critical",
        StringKeys.PPL_MEDIUM                  to "Medium",
        StringKeys.PPL_LOW                     to "Low",
        StringKeys.PPL_AT_RISK_STUDENTS        to "At-risk students",
        StringKeys.PPL_SUBJECT_ENGAGEMENT      to "Subject engagement",
        StringKeys.PPL_COHORT_COMPARISON       to "Cohort comparison",
        StringKeys.PPL_GRADE_N                 to "Grade {n}",
        StringKeys.PPL_MARK_ALUMNI             to "Mark students as alumni",
        StringKeys.PPL_MARK_ALUMNI_BODY        to "This will mark {count} filtered student(s) as graduated and create alumni records for them.",
        StringKeys.PPL_GRADUATION_YEAR         to "Graduation year",
        StringKeys.PPL_ADD_STAFF               to "Add staff",
        StringKeys.PPL_SEARCH_STAFF            to "Search by name, role or department",
        StringKeys.PPL_NO_STAFF                to "No staff yet",
        StringKeys.PPL_NO_STAFF_BODY           to "Add office, accounts, library, transport or support staff so they appear here.",
        StringKeys.PPL_NO_STAFF_MATCHES        to "No staff matches \"{query}\".",
        StringKeys.PPL_FULL_NAME               to "Full name",
        StringKeys.PPL_NAME_PH_TEACHER         to "e.g. Asha Verma",
        StringKeys.PPL_EMAIL_OR_PHONE          to "Email or phone",
        StringKeys.PPL_EMAIL_PHONE_PH          to "teacher@school.edu or 98765 43210",
        StringKeys.PPL_INITIAL_PASSWORD        to "Initial password",
        StringKeys.PPL_PASSWORD_PH             to "Shared with the teacher to sign in",
        StringKeys.PPL_OTP_HINT                to "This teacher will sign in with a one-time code sent to their phone.",
        StringKeys.PPL_ADD_STAFF_MEMBER        to "Add staff member",
        StringKeys.PPL_NAME_PH_STAFF           to "e.g. Ramesh Kumar",
        StringKeys.PPL_ROLE                    to "Role",
        StringKeys.PPL_ROLE_PH                 to "e.g. Accountant, Librarian, Security",
        StringKeys.PPL_DEPT_OPTIONAL           to "Department (optional)",
        StringKeys.PPL_DEPT_PH                 to "e.g. Office, Transport",
        StringKeys.PPL_PHONE_OPTIONAL          to "Phone (optional)",
        StringKeys.PPL_PHONE_PH                to "98765 43210",
        StringKeys.PPL_EMAIL_OPTIONAL          to "Email (optional)",
        StringKeys.PPL_EMAIL_PH                to "staff@school.edu",
        StringKeys.PPL_NAME_PH_STUDENT         to "e.g. Aarav Sharma",
        StringKeys.PPL_CLASS                   to "Class",
        StringKeys.PPL_CLASS_PH                to "e.g. Grade 4",
        StringKeys.PPL_SECTION                 to "Section",
        StringKeys.PPL_SECTION_PH              to "A",
        StringKeys.PPL_ROLL_NUMBER             to "Roll number",
        StringKeys.PPL_ROLL_PH                 to "e.g. 12",
        StringKeys.PPL_PARENT_PHONE            to "Parent/guardian phone (optional)",
        StringKeys.PPL_PARENT_PHONE_PH         to "e.g. 9876543210",
        StringKeys.PPL_IMPORT_STUDENTS_CSV     to "Import students (CSV)",
        StringKeys.PPL_IMPORT_INSTRUCTIONS     to "First row must be the header. Columns: full_name, class_name, roll_number (required); section, student_code (optional).",
        StringKeys.PPL_CSV_CONTENT             to "CSV content",
        StringKeys.PPL_CSV_PH                  to "full_name,class_name,section,roll_number\nAarav Sharma,Grade 4,A,12",
        StringKeys.PPL_IMPORT                  to "Import",
        StringKeys.PPL_MASTERY                 to "Mastery: {trend}",
        StringKeys.PPL_RISK_PCT                to "{risk}% risk",
        // ── SchoolRecordsScreenV2 ──
        StringKeys.REC_TITLE                   to "Records",
        StringKeys.REC_TAB_COVERAGE            to "Coverage",
        StringKeys.REC_TAB_PACE                to "Pace",
        StringKeys.REC_TAB_ATTENDANCE          to "Attendance",
        StringKeys.REC_TAB_MARKS               to "Marks",
        StringKeys.REC_TAB_FEE                 to "Fee",
        StringKeys.REC_TAB_DOCUMENTS           to "Documents",
        StringKeys.REC_DOC_LIBRARY_TITLE       to "Document library",
        StringKeys.REC_DOC_LIBRARY_DESC        to "Circulars, timetables and holiday lists will be uploadable once media storage is configured.",
        StringKeys.REC_NO_COVERAGE             to "No coverage data yet",
        StringKeys.REC_NO_COVERAGE_BODY        to "Syllabus coverage will appear here once teachers start marking units complete.",
        StringKeys.REC_OVERALL_COVERAGE        to "Overall syllabus coverage",
        StringKeys.REC_BY_DEPARTMENT           to "By department",
        StringKeys.REC_LAGGING_CLASSES         to "Lagging classes",
        StringKeys.REC_BEHIND                  to "{pct}% behind",
        StringKeys.REC_MILESTONES              to "Academic milestones",
        StringKeys.REC_VERIFIED                to "Verified",
        StringKeys.REC_NO_ATTENDANCE           to "No attendance marked yet",
        StringKeys.REC_NO_ATTENDANCE_BODY      to "School-wide attendance will roll up here once teachers start marking the daily register.",
        StringKeys.REC_LATEST_REGISTER         to "Latest register",
        StringKeys.REC_PRESENT_PCT             to "{pct}% present",
        StringKeys.REC_PRESENT                 to "Present",
        StringKeys.REC_ABSENT                  to "Absent",
        StringKeys.REC_LATE                    to "Late",
        StringKeys.REC_TOTAL                   to "Total",
        StringKeys.REC_BY_CLASS                to "By class",
        StringKeys.REC_NO_ASSESSMENTS          to "No assessments yet",
        StringKeys.REC_NO_ASSESSMENTS_BODY     to "Exam averages roll up here once teachers create assessments and enter marks.",
        StringKeys.REC_OVERALL_AVG             to "Overall average",
        StringKeys.REC_ASSESSMENT_COUNT        to "{count} assessment{s}",
        StringKeys.REC_PUBLISHED               to "Published",
        StringKeys.REC_DRAFT                   to "Draft",
        StringKeys.REC_AVG                     to "Avg {avg} / {max}",
        StringKeys.REC_GRADED                  to "{pct}% • {count} graded",
        StringKeys.REC_NOT_GRADED              to "Not graded yet",
        StringKeys.REC_NO_FEES                 to "No fee records yet",
        StringKeys.REC_NO_FEES_BODY            to "Collections, dues and overdue reminders surface here once fee records are raised for this school.",
        StringKeys.REC_LEDGER                  to "Ledger ({currency})",
        StringKeys.REC_PAID                    to "Paid",
        StringKeys.REC_DUE                     to "Due",
        StringKeys.REC_OVERDUE                 to "Overdue",
        StringKeys.REC_RECENT                  to "Recent",
        StringKeys.REC_DUE_DATE                to "{category} • due {date}",
        StringKeys.REC_NO_PACE                 to "No pace data yet",
        StringKeys.REC_NO_PACE_BODY            to "Pace snapshots will appear here once syllabus tracking begins.",
        StringKeys.REC_RECALCULATE             to "Recalculate Pace",
        StringKeys.REC_ACTIVE_ALERTS           to "Active alerts",
        StringKeys.REC_AI_RECONFIRMED          to "AI reconfirmed",
        StringKeys.REC_RESOLVE                 to "Resolve",
        StringKeys.REC_PACE_SNAPSHOTS          to "Pace snapshots",
        StringKeys.REC_TOPICS_COVERED          to "{covered}/{total} topics covered",
        StringKeys.REC_EXPECTED                to "Expected: {pct}%",
        // ── AlumniScreen ──
        StringKeys.ALM_TITLE                   to "Alumni Management",
        StringKeys.ALM_TAB_DIRECTORY           to "Directory",
        StringKeys.ALM_TAB_PENDING             to "Pending",
        StringKeys.ALM_TAB_CAMPAIGNS           to "Campaigns",
        StringKeys.ALM_TAB_DONATIONS           to "Donations",
        StringKeys.ALM_TAB_MENTORSHIP          to "Mentorship",
        StringKeys.ALM_TAB_ANALYTICS           to "Analytics",
        StringKeys.ALM_ADD_ALUMNI              to "Add Alumni",
        StringKeys.ALM_BULK_IMPORT             to "Bulk Import",
        StringKeys.ALM_NO_ALUMNI               to "No alumni yet",
        StringKeys.ALM_NO_ALUMNI_BODY          to "Add alumni manually or use bulk import",
        StringKeys.ALM_FULL_NAME_REQ           to "Full name *",
        StringKeys.ALM_NAME_PH                 to "e.g. Priya Sharma",
        StringKeys.ALM_GRAD_YEAR_REQ           to "Graduation year *",
        StringKeys.ALM_GRAD_YEAR_PH            to "2024",
        StringKeys.ALM_STUDENT_ID_OPT          to "Student ID (optional)",
        StringKeys.ALM_STUDENT_ID_PH           to "ADM-2020-001",
        StringKeys.ALM_EMAIL                   to "Email",
        StringKeys.ALM_EMAIL_PH                to "priya@example.com",
        StringKeys.ALM_PHONE                   to "Phone",
        StringKeys.ALM_PHONE_PH                to "+91 98765 43210",
        StringKeys.ALM_PROFESSION              to "Profession",
        StringKeys.ALM_PROFESSION_PH           to "Software Engineer",
        StringKeys.ALM_COMPANY                 to "Company",
        StringKeys.ALM_COMPANY_PH              to "Google",
        StringKeys.ALM_CITY                    to "City",
        StringKeys.ALM_CITY_PH                 to "Bengaluru",
        StringKeys.ALM_ADD                     to "Add",
        StringKeys.ALM_BULK_IMPORT_TITLE       to "Bulk Import Alumni",
        StringKeys.ALM_BULK_IMPORT_INSTR       to "Paste CSV data. Each line: name,graduationYear,email,phone,profession,company,city",
        StringKeys.ALM_CSV_PH                  to "Priya Sharma,2024,priya@example.com,9876543210,Engineer,Google,Bengaluru\nRahul Verma,2023,...",
        StringKeys.ALM_ROWS_READY              to "{count} row(s) ready to import",
        StringKeys.ALM_IMPORT                  to "Import",
        StringKeys.ALM_IMPORT_WITH_COUNT       to "Import ({count})",
        StringKeys.ALM_NO_PENDING              to "No pending verifications",
        StringKeys.ALM_NO_PENDING_BODY         to "All alumni registrations have been reviewed",
        StringKeys.ALM_BATCH                   to "Batch {year}",
        StringKeys.ALM_APPROVE                 to "Approve",
        StringKeys.ALM_DECLINE                 to "Decline",
        StringKeys.ALM_NO_CAMPAIGNS            to "No campaigns yet",
        StringKeys.ALM_NO_CAMPAIGNS_BODY       to "Create a donation campaign to engage alumni",
        StringKeys.ALM_CAMPAIGN_PROGRESS       to "₹{raised} / ₹{target} ({pct}%) • {donors} donors",
        StringKeys.ALM_STATUS                  to "Status: {status}",
        StringKeys.ALM_NO_DONATIONS            to "No donations recorded",
        StringKeys.ALM_NO_DONATIONS_BODY       to "Log donations from the alumni detail screen",
        StringKeys.ALM_CAMPAIGN_LABEL          to "Campaign: {title}",
        StringKeys.ALM_DATE                    to "Date: {date}",
        StringKeys.ALM_80G_ELIGIBLE            to "80G eligible • Receipt: {receipt}",
        StringKeys.ALM_RECEIPT_PENDING         to "Pending",
        StringKeys.ALM_NO_ANALYTICS            to "No analytics data",
        StringKeys.ALM_OVERVIEW                to "Overview",
        StringKeys.ALM_TOTAL_ALUMNI            to "Total Alumni",
        StringKeys.ALM_ACTIVE_90               to "Active (90 days)",
        StringKeys.ALM_PENDING_VERIFICATIONS   to "Pending Verifications",
        StringKeys.ALM_ENGAGEMENT_RATE         to "Engagement Rate",
        StringKeys.ALM_TOTAL_DONATIONS         to "Total Donations",
        StringKeys.ALM_ACTIVE_CAMPAIGNS        to "Active Campaigns",
        StringKeys.ALM_ACTIVE_MENTORSHIPS      to "Active Mentorships",
        StringKeys.ALM_BY_GRAD_YEAR            to "By Graduation Year",
        StringKeys.ALM_BY_PROFESSION           to "By Profession",
        StringKeys.ALM_BY_CITY                 to "By City",
        StringKeys.ALM_NO_MENTORSHIPS          to "No active mentorships",
        StringKeys.ALM_NO_MENTORSHIPS_BODY     to "Mentorships will appear here once alumni start mentoring students",
        StringKeys.ALM_MENTORING               to "Mentoring: {name}",
        StringKeys.ALM_STARTED                 to "Started: {date}",
        StringKeys.ALM_SESSIONS                to "Sessions: {count}",
        StringKeys.ALM_NOTES                   to "Notes: {notes}",
        StringKeys.ALM_MENTORSHIP_REQUESTS     to "Mentorship Requests",
        StringKeys.ALM_NO_MENTOR_REQUESTS      to "No mentorship requests",
        StringKeys.ALM_NO_MENTOR_REQUESTS_BODY to "Student requests for alumni mentorship will appear here",
        StringKeys.ALM_FROM                    to "From: {name}",
        StringKeys.ALM_REQUESTED_BY            to "Requested by: {name}",
        StringKeys.ALM_EXPERTISE               to "Expertise: {area}",
        StringKeys.ALM_MESSAGE                 to "Message: {msg}",
        StringKeys.ALM_MENTOR                  to "Mentor",
        StringKeys.ALM_MENTOR_EXPERTISE        to "Mentor — {area}",
        // ── SchoolHomeScreenV2 ──
        StringKeys.HOME_NOTIF_RATIONALE_TITLE   to "Stay Informed",
        StringKeys.HOME_NOTIF_RATIONALE_MSG     to "Enable notifications to receive important updates about school events, attendance, and institutional alerts.",
        StringKeys.HOME_NOTIF_ENABLE            to "Enable",
        StringKeys.HOME_NOTIF_NOT_NOW           to "Not Now",
        StringKeys.HOME_WELCOME                 to "Welcome",
        StringKeys.HOME_YOUR_SCHOOL             to "Your School",
        StringKeys.HOME_NOTIFICATIONS           to "Notifications",
        StringKeys.HOME_QA_ANNOUNCEMENT         to "Announcement",
        StringKeys.HOME_QA_CREATE_EVENT         to "Create Event",
        StringKeys.HOME_QA_SEND_NOTICE          to "Send Notice",
        StringKeys.HOME_QA_REPORTS              to "Reports",
        StringKeys.HOME_QA_TRANSPORT            to "Transport",
        StringKeys.HOME_SMART_INSIGHTS          to "Smart Insights",
        StringKeys.HOME_SCHOOL_PULSE            to "School Pulse",
        StringKeys.HOME_PULSE_METRICS_EMPTY     to "Metrics appear as your school records data.",
        StringKeys.HOME_PULSE_OUT_OF            to "/ 100",
        StringKeys.HOME_KEY_METRICS             to "Key Metrics",
        StringKeys.HOME_CAMPUS_HEALTH           to "Campus Health",
        StringKeys.HOME_ATTENDANCE_OVER         to "Attendance over {count} {period}",
        StringKeys.HOME_NO_ATTENDANCE_DATA      to "No attendance data yet",
        StringKeys.HOME_ATTENDANCE_TRENDS_EMPTY to "Attendance trends appear once daily records are captured.",
        StringKeys.HOME_FEE_COLLECTION          to "Fee Collection",
        StringKeys.HOME_COLLECTION_RATE         to "Collection rate {pct}%",
        StringKeys.HOME_COLLECTED               to "Collected",
        StringKeys.HOME_PENDING                 to "Pending",
        StringKeys.HOME_PARENT_ENGAGEMENT       to "Parent Engagement",
        StringKeys.HOME_PARENT_ENGAGEMENT_SUB   to "{pct}% active · {active}/{total} parents",
        StringKeys.HOME_MOST_ENGAGED            to "Most engaged: {class}",
        StringKeys.HOME_CLASS_LEADERBOARD       to "Class Leaderboard",
        StringKeys.HOME_COMMUNICATION           to "Communication",
        StringKeys.HOME_UNREAD                  to "Unread",
        StringKeys.HOME_QUERIES                 to "Queries",
        StringKeys.HOME_ANNOUNCEMENTS           to "Announcements",
        StringKeys.HOME_ACKNOWLEDGEMENTS        to "Acknowledgements",
        StringKeys.HOME_EVENTS                  to "Events",
        StringKeys.HOME_VIEW_CALENDAR           to "View calendar →",
        StringKeys.HOME_RECENTLY_COMPLETED      to "Recently completed",
        StringKeys.HOME_TODAY                   to "Today",
        StringKeys.HOME_TOMORROW                to "Tomorrow",
        StringKeys.HOME_IN_DAYS                 to "In {days} days",
        StringKeys.HOME_CAL_AT_GLANCE           to "Calendar at a glance",
        StringKeys.HOME_OPEN_CALENDAR           to "Open calendar →",
        StringKeys.HOME_THIS_WEEK               to "This week",
        StringKeys.HOME_DRAFTS                  to "Drafts",
        StringKeys.HOME_NEXT_HOLIDAY            to "Next holiday",
        StringKeys.HOME_UPCOMING_EVENTS         to "Upcoming Events",
        StringKeys.HOME_SEE_ALL                 to "See all →",
        StringKeys.HOME_DRAFT                   to "Draft",
        StringKeys.HOME_CONFLICT                to "Conflict",
        StringKeys.HOME_TEACHER_SPOTLIGHT       to "⭐ Teacher Spotlight",
        StringKeys.HOME_SCORE                   to "score",
        StringKeys.HOME_STUDENT_ACHIEVEMENTS    to "Student Achievements",
        StringKeys.HOME_CELEBRATIONS            to "Celebrations",
        StringKeys.HOME_TODAY_LABEL             to "Today",
        StringKeys.HOME_UPCOMING_LABEL          to "Upcoming",
        StringKeys.HOME_TEACHER                 to "Teacher",
        StringKeys.HOME_STUDENT                 to "Student",
        StringKeys.HOME_BIRTHDAY_TODAY          to "🎉 Today",
        StringKeys.HOME_BIRTHDAY_IN_DAYS        to "in {days}d",
        StringKeys.HOME_LIVE_ACTIVITY           to "Live Activity",
        StringKeys.HOME_SCHOOL_ANALYTICS        to "School Analytics",
        StringKeys.HOME_ANALYTICS_DESC          to "Attendance, academics & growth insights",
        StringKeys.HOME_EXPLORE_ANALYTICS       to "Explore Analytics",
        StringKeys.HOME_RISK_MONITOR            to "Student Risk Monitor",
        StringKeys.HOME_RISK_MONITOR_DESC       to "Identify students needing attention early",
        StringKeys.HOME_OPEN_MONITOR            to "Open Monitor",
        StringKeys.HOME_REPORT_PUBLISH          to "Report Card Publishing",
        StringKeys.HOME_REPORT_PUBLISH_DESC     to "Review oversight & publish approved AI report card drafts",
        StringKeys.HOME_OPEN_PUBLISHING         to "Open Publishing",
        StringKeys.HOME_REPORT_EFFECTIVENESS    to "Report Card Effectiveness",
        StringKeys.HOME_REPORT_EFFECTIVENESS_DESC to "Run the learning flywheel & view effectiveness priors",
        StringKeys.HOME_OPEN_EFFECTIVENESS      to "Open Effectiveness",
        StringKeys.HOME_EVENT_REGISTRATION      to "Event Registration",
        StringKeys.HOME_EVENT_REG_DESC          to "Manage PTM slots, event capacity & registrations",
        StringKeys.HOME_MANAGE                  to "Manage →",
        // ── ClassesSubjectsScreenV2 ──
        StringKeys.CS_TITLE                     to "Classes & Subjects",
        StringKeys.CS_TAB_CLASSES               to "Classes",
        StringKeys.CS_TAB_SUBJECTS              to "Subjects",
        StringKeys.CS_TAB_SCHEDULE              to "Schedule",
        StringKeys.CS_TAB_EXCEPTIONS            to "Exceptions & Requests",
        StringKeys.CS_NO_CLASSES                to "No classes yet",
        StringKeys.CS_NO_CLASSES_BODY           to "Add your first class to get started.",
        StringKeys.CS_CLASSES                   to "Classes",
        StringKeys.CS_ADD_CLASS                 to "Add Class",
        StringKeys.CS_EDIT_CLASS                to "Edit Class",
        StringKeys.CS_DELETE_CLASS              to "Delete {name}?",
        StringKeys.CS_DELETE_CLASS_MSG          to "This will also delete all subjects in this class. This cannot be undone.",
        StringKeys.CS_NO_SECTIONS               to "No sections",
        StringKeys.CS_SUBJECTS_COUNT            to "{count} subjects",
        StringKeys.CS_EDIT                      to "Edit",
        StringKeys.CS_CLASS_CODE                to "Class Code",
        StringKeys.CS_CLASS_NAME                to "Class Name",
        StringKeys.CS_SECTIONS_LABEL            to "Sections (comma-separated)",
        StringKeys.CS_CANCEL                    to "Cancel",
        StringKeys.CS_SAVE                      to "Save",
        StringKeys.CS_DELETE                    to "Delete",
        StringKeys.CS_CREATE                    to "Create",
        StringKeys.CS_BACK                      to "← Back",
        StringKeys.CS_REMOVE                    to "Remove",
        StringKeys.CS_NO_CLASSES_AVAIL          to "No classes available",
        StringKeys.CS_NO_CLASSES_AVAIL_BODY     to "Add classes first in the Classes tab.",
        StringKeys.CS_SUBJECTS                  to "Subjects",
        StringKeys.CS_CLASS_SUBJECTS            to "{name} — Subjects",
        StringKeys.CS_ADD                       to "Add",
        StringKeys.CS_NO_SUBJECTS               to "No subjects yet",
        StringKeys.CS_NO_SUBJECTS_BODY          to "Add a subject to this class.",
        StringKeys.CS_ADD_SUBJECT               to "Add Subject",
        StringKeys.CS_EDIT_SUBJECT              to "Edit Subject",
        StringKeys.CS_DELETE_SUBJECT            to "Delete {name}?",
        StringKeys.CS_DELETE_SUBJECT_MSG        to "This subject will be removed from the class.",
        StringKeys.CS_SUBJECT_NAME              to "Subject Name",
        StringKeys.CS_SUBJECT_CODE              to "Subject Code",
        StringKeys.CS_STEP_STRUCTURE            to "1. Day Structure",
        StringKeys.CS_STEP_ASSIGN               to "2. Assign",
        StringKeys.CS_STEP_REVIEW               to "3. Review",
        StringKeys.CS_DAY_STRUCTURE_TEMPLATE    to "Day Structure Template",
        StringKeys.CS_DAY_STRUCTURE_DESC        to "Customize every element below — add, remove, reorder, edit times and labels.",
        StringKeys.CS_IMPORT                    to "📥 Import from Photo / PDF / Text",
        StringKeys.CS_TEMPLATE_NAME             to "Template Name",
        StringKeys.CS_APPLICABLE_DAYS           to "Applicable Days",
        StringKeys.CS_LIVE_PREVIEW              to "Live Preview",
        StringKeys.CS_SLOTS_COUNT               to "Slots ({count})",
        StringKeys.CS_ADD_SLOT                  to "+ Add Slot",
        StringKeys.CS_SAVE_TEMPLATE             to "Save Template & Continue →",
        StringKeys.CS_EXISTING_CONFIGS          to "Existing Configurations",
        StringKeys.CS_ACTIVE                    to "ACTIVE",
        StringKeys.CS_INACTIVE                  to "INACTIVE",
        StringKeys.CS_CONFIG_DETAILS            to "Days: {days}  ·  Level: {level}  ·  {count} slots",
        StringKeys.CS_IMPORT_SCHEDULE           to "Import Schedule",
        StringKeys.CS_CHOOSE_IMPORT             to "Choose an import source:",
        StringKeys.CS_PHOTO_OCR                 to "Photo (OCR)",
        StringKeys.CS_PHOTO_OCR_DESC            to "Take a photo or pick from gallery — text will be extracted automatically.",
        StringKeys.CS_PDF_DOCUMENT              to "PDF Document",
        StringKeys.CS_PDF_DESC                  to "Pick a PDF file — timetable text will be extracted.",
        StringKeys.CS_PASTE_TEXT                to "Paste Text",
        StringKeys.CS_PASTE_TEXT_DESC           to "Paste timetable text from any source — we'll parse it into slots.",
        StringKeys.CS_PHOTO_OCR_LABEL           to "Photo OCR",
        StringKeys.CS_PDF_IMPORT_LABEL          to "PDF Import",
        StringKeys.CS_AI_READING                to "AI is reading your timetable...",
        StringKeys.CS_AI_VISION_DESC            to "This uses AI vision to extract text from your image.",
        StringKeys.CS_AI_VISION_OCR             to "{label} — AI Vision OCR",
        StringKeys.CS_PHOTO_OCR_BODY            to "Take a photo or pick an image of a printed timetable. Our AI will extract the schedule automatically.",
        StringKeys.CS_PDF_BODY                  to "Pick a PDF file — timetable text will be extracted.",
        StringKeys.CS_PICK_PHOTO                to "Pick Photo",
        StringKeys.CS_PICK_PDF                  to "Pick PDF",
        StringKeys.CS_USE_PASTE                 to "Use Paste Text Instead",
        StringKeys.CS_PASTE_BELOW               to "Paste your timetable text below.",
        StringKeys.CS_SUPPORTED_FORMATS         to "Supported formats: '08:00-08:40 Period 1' or '08:00 08:40 English' (one slot per line)",
        StringKeys.CS_TIMETABLE_TEXT            to "Timetable Text",
        StringKeys.CS_PARSE_FILL                to "Parse & Fill",
        StringKeys.CS_AI_PARSE                  to "AI Parse",
        StringKeys.CS_PARSE_ERROR               to "Could not parse any slots. Make sure each line has a time range (e.g. 08:00-08:40) and a label.",
        StringKeys.CS_PASTE_FIRST               to "Please paste some timetable text first.",
        StringKeys.CS_PDF_NOT_AVAILABLE         to "PDF text extraction is not yet available. Please copy the timetable text from your PDF and use 'Paste Text' mode.",
        StringKeys.CS_SLOT_LABEL                to "Slot label",
        StringKeys.CS_START                     to "Start",
        StringKeys.CS_END                       to "End",
        StringKeys.CS_NO_DAY_STRUCTURE          to "No day structure found",
        StringKeys.CS_NO_DAY_STRUCTURE_BODY     to "You can still add periods manually below, or go back to Step 1 to create a day structure template.",
        StringKeys.CS_NO_CLASSES_FOUND          to "No classes found. Add classes first in the Classes tab.",
        StringKeys.CS_SELECT_DAY                to "Select Day",
        StringKeys.CS_DAY_CLASS_SECTION         to "{day} — {class} · {section}",
        StringKeys.CS_NO_PERIODS                to "No periods yet",
        StringKeys.CS_NO_PERIODS_BODY           to "Tap \"Add Period\" below to assign a teacher and subject to this day.",
        StringKeys.CS_PERIODS_ON_DAY            to "{count} period(s) on {day}",
        StringKeys.CS_ADD_PERIOD                to "+ Add Period",
        StringKeys.CS_SLOTS_ASSIGNED            to "{assigned} of {total} slots assigned",
        StringKeys.CS_OTHER_PERIODS             to "Other periods (not in day structure)",
        StringKeys.CS_QUICK_ACTIONS             to "Quick Actions",
        StringKeys.CS_COPY_DAY_TO_ALL           to "Copy {day} to All Days",
        StringKeys.CS_COPY_FROM_CLASS           to "Copy from Another Class",
        StringKeys.CS_REVIEW_BTN                to "Review →",
        StringKeys.CS_REMOVE_ASSIGNMENT         to "Remove assignment?",
        StringKeys.CS_REMOVE_ASSIGNMENT_MSG     to "This will remove the teacher from this slot.",
        StringKeys.CS_SLOT_N                    to "Slot {n}",
        StringKeys.CS_ROOM_N                    to "Room {room}",
        StringKeys.CS_TAP_TO_ASSIGN             to "Tap to assign teacher & subject",
        StringKeys.CS_ASSIGNED                  to "Assigned",
        StringKeys.CS_EMPTY                     to "Empty",
        StringKeys.CS_TEACHER                    to "Teacher",
        StringKeys.CS_SELECT_TEACHER            to "Select Teacher",
        StringKeys.CS_NO_TEACHERS               to "No teachers yet",
        StringKeys.CS_ADD_NEW_TEACHER           to "+ Add New Teacher",
        StringKeys.CS_SUBJECT_LABEL             to "Subject",
        StringKeys.CS_SELECT_SUBJECT            to "Select Subject",
        StringKeys.CS_NO_SUBJECTS_CLASS         to "No subjects for this class yet",
        StringKeys.CS_ADD_NEW_SUBJECT           to "+ Add New Subject",
        StringKeys.CS_ROOM                      to "Room",
        StringKeys.CS_UPDATE                    to "Update",
        StringKeys.CS_ASSIGN                    to "Assign",
        StringKeys.CS_COPY_DAY_CONFIRM          to "Copy {day} to all days?",
        StringKeys.CS_COPY_DAY_MSG              to "This will copy all assignments from {day} to: {targets}.",
        StringKeys.CS_COPY                      to "Copy",
        StringKeys.CS_COPY_FROM_CLASS_TITLE     to "Copy from Another Class",
        StringKeys.CS_COPY_FROM_CLASS_DESC      to "Copy all periods from a source class to {class} across all days.",
        StringKeys.CS_NO_OTHER_CLASSES          to "No other classes available to copy from.",
        StringKeys.CS_WEEKLY_OVERVIEW           to "Weekly Overview",
        StringKeys.CS_NO_TIMETABLE              to "No timetable to review",
        StringKeys.CS_NO_TIMETABLE_BODY         to "Go back to Step 2 and add some periods first.",
        StringKeys.CS_PERIODS_LABEL             to "Periods",
        StringKeys.CS_CLASSES_LABEL             to "Classes",
        StringKeys.CS_TEACHERS_LABEL            to "Teachers",
        StringKeys.CS_DAYS_LABEL                to "Days",
        StringKeys.CS_CONFLICTS_DETECTED        to "⚠ Conflicts Detected",
        StringKeys.CS_DONE_REVIEW               to "Done (Review Conflicts)",
        StringKeys.CS_DONE                      to "✓ Done",
        StringKeys.CS_NEW_TEACHER               to "New Teacher",
        StringKeys.CS_FULL_NAME                 to "Full Name",
        StringKeys.CS_EMAIL_PHONE               to "Email or Phone",
        StringKeys.CS_NEW_SUBJECT               to "New Subject",
        StringKeys.CS_EXCEPTIONS                to "Exceptions",
        StringKeys.CS_PENDING                   to "Pending",
        StringKeys.CS_APPROVED                  to "Approved",
        StringKeys.CS_REJECTED                  to "Rejected",
        StringKeys.CS_PERIOD_EXCEPTIONS         to "Period Exceptions",
        StringKeys.CS_LOAD_EXCEPTIONS           to "Load Exceptions",
        StringKeys.CS_NO_EXCEPTIONS             to "No exceptions",
        StringKeys.CS_NO_EXCEPTIONS_BODY        to "Tap 'Add Exception' to create a one-off period override.",
        StringKeys.CS_ADD_EXCEPTION             to "+ Add Exception",
        StringKeys.CS_CHANGE_REQUESTS           to "Change Requests",
        StringKeys.CS_LOAD                      to "Load",
        StringKeys.CS_NO_REQUESTS               to "No requests",
        StringKeys.CS_NO_REQUESTS_BODY          to "Change requests from teachers will appear here.",
        StringKeys.CS_DELETE_EXCEPTION          to "Delete exception?",
        StringKeys.CS_DELETE_EXCEPTION_MSG      to "This will remove the period override.",
        StringKeys.CS_ADD_EXCEPTION_TITLE       to "Add Exception",
        StringKeys.CS_DATE                      to "Date",
        StringKeys.CS_KIND                      to "Kind",
        StringKeys.CS_NOTE                      to "Note",
        StringKeys.CS_DAY_LABEL                 to "Day: {day} {start}–{end}",
        StringKeys.CS_REASON_LABEL              to "Reason: {reason}",
        StringKeys.CS_ADMIN_NOTE                to "Admin Note",
        StringKeys.CS_APPROVE                   to "Approve",
        StringKeys.CS_REJECT_BTN                to "Reject",
        StringKeys.CS_REVIEW                    to "Review",
        StringKeys.CS_WEEKDAY_MON               to "Mon",
        StringKeys.CS_WEEKDAY_TUE               to "Tue",
        StringKeys.CS_WEEKDAY_WED               to "Wed",
        StringKeys.CS_WEEKDAY_THU               to "Thu",
        StringKeys.CS_WEEKDAY_FRI               to "Fri",
        StringKeys.CS_WEEKDAY_SAT               to "Sat",
        StringKeys.CS_WEEKDAY_SUN               to "Sun",

        // SchoolLibraryScreen (admin)
        StringKeys.LIB_TAB_DASHBOARD            to "Dashboard",
        StringKeys.LIB_TAB_BOOKS                to "Books",
        StringKeys.LIB_TAB_COPIES               to "Copies",
        StringKeys.LIB_TAB_ISSUES               to "Issues",
        StringKeys.LIB_TAB_QUICK_ISSUE          to "Quick Issue",
        StringKeys.LIB_TAB_BULK_RETURN          to "Bulk Return",
        StringKeys.LIB_TAB_CATEGORIES           to "Categories",
        StringKeys.LIB_TAB_AUDIT                to "Audit",
        StringKeys.LIB_TAB_ANNOUNCEMENTS        to "Announcements",
        StringKeys.LIB_TAB_ACQUISITION          to "Acquisition",
        StringKeys.LIB_TAB_RESERVATIONS         to "Reservations",
        StringKeys.LIB_TAB_HISTORY              to "History",
        StringKeys.LIB_TAB_MORE                 to "More",
        StringKeys.LIB_TAB_SETTINGS             to "Settings",
        StringKeys.LIB_TITLE                    to "Library",
        StringKeys.LIB_OFFLINE_CACHED           to "Offline — showing cached data",
        StringKeys.LIB_OFFLINE_CHECK            to "Offline — check your connection",
        StringKeys.LIB_DASHBOARD                to "Dashboard",
        StringKeys.LIB_WELCOME                  to "Welcome to Library Management!",
        StringKeys.LIB_WELCOME_DESC             to "Your library is empty. Run the onboarding wizard to set up categories and add your first books.",
        StringKeys.LIB_RUN_ONBOARDING           to "Run Onboarding Wizard",
        StringKeys.LIB_TOTAL_BOOKS              to "Total Books",
        StringKeys.LIB_TOTAL_COPIES             to "Total Copies",
        StringKeys.LIB_AVAILABLE                to "Available",
        StringKeys.LIB_ISSUED                   to "Issued",
        StringKeys.LIB_OVERDUE                  to "Overdue",
        StringKeys.LIB_LOST                     to "Lost",
        StringKeys.LIB_RESERVATIONS             to "Reservations",
        StringKeys.LIB_DAMAGED                  to "Damaged",
        StringKeys.LIB_OUTSTANDING_FINES        to "Outstanding Fines",
        StringKeys.LIB_COLLECTED_MONTH          to "Collected this month",
        StringKeys.LIB_BOOKS                    to "Books",
        StringKeys.LIB_SEARCH_BOOKS             to "Search books",
        StringKeys.LIB_ADD_BOOK                 to "+ Add Book",
        StringKeys.LIB_CATEGORY_LABEL           to "Category:",
        StringKeys.LIB_AVAILABLE_LABEL          to "Available:",
        StringKeys.LIB_AVAILABLE_ONLY           to "Available Only",
        StringKeys.LIB_SORT_LABEL               to "Sort:",
        StringKeys.LIB_SORT_NEWEST              to "Newest",
        StringKeys.LIB_SORT_TITLE               to "Title A-Z",
        StringKeys.LIB_SORT_AUTHOR              to "Author",
        StringKeys.LIB_SORT_POPULAR             to "Popular",
        StringKeys.LIB_SEARCH_BTN               to "Search",
        StringKeys.LIB_NO_BOOKS                 to "No books found",
        StringKeys.LIB_NO_BOOKS_DESC            to "Try a different search query or add a new book.",
        StringKeys.LIB_ARCHIVED                 to "Archived",
        StringKeys.LIB_UNARCHIVE                to "Unarchive",
        StringKeys.LIB_ARCHIVE                  to "Archive",
        StringKeys.LIB_SET_COVER                to "Set Cover",
        StringKeys.LIB_ISSUE                    to "Issue",
        StringKeys.LIB_SET_COVER_URL            to "Set Cover URL",
        StringKeys.LIB_COVER_URL                to "Cover image URL",
        StringKeys.LIB_ADD_NEW_BOOK             to "Add New Book",
        StringKeys.LIB_TITLE_LABEL              to "Title *",
        StringKeys.LIB_AUTHOR_LABEL             to "Author",
        StringKeys.LIB_ISBN_LABEL               to "ISBN",
        StringKeys.LIB_PUBLISHER_LABEL          to "Publisher",
        StringKeys.LIB_TOTAL_COPIES_LABEL       to "Total Copies",
        StringKeys.LIB_SHELF_LOCATION           to "Shelf Location",
        StringKeys.LIB_REPLACEMENT_COST         to "Replacement Cost (₹)",
        StringKeys.LIB_LANGUAGE                 to "Language",
        StringKeys.LIB_SYNOPSIS                 to "Synopsis",
        StringKeys.LIB_CATEGORY                 to "Category",
        StringKeys.LIB_CREATE                   to "Create",
        StringKeys.LIB_ISSUE_BOOK               to "Issue Book",
        StringKeys.LIB_BORROWER_ID              to "Borrower ID *",
        StringKeys.LIB_BORROWER_NAME            to "Borrower Name *",
        StringKeys.LIB_COPY_ID                  to "Copy ID (optional)",
        StringKeys.LIB_BORROWER_TYPE            to "Borrower Type",
        StringKeys.LIB_STUDENT                  to "Student",
        StringKeys.LIB_TEACHER                  to "Teacher",
        StringKeys.LIB_ISSUES                   to "Issues",
        StringKeys.LIB_NO_ISSUES                to "No issues found",
        StringKeys.LIB_NO_ISSUES_DESC           to "Issues will appear here once books are issued.",
        StringKeys.LIB_RETURN                   to "Return",
        StringKeys.LIB_RENEW                    to "Renew",
        StringKeys.LIB_MARK_LOST                to "Mark Lost",
        StringKeys.LIB_PAY_FINE                 to "Pay Fine",
        StringKeys.LIB_WAIVE_FINE               to "Waive Fine",
        StringKeys.LIB_RETURN_BOOK              to "Return Book",
        StringKeys.LIB_SELECT_CONDITION         to "Select condition:",
        StringKeys.LIB_CONDITION_GOOD           to "Good",
        StringKeys.LIB_CONDITION_FAIR           to "Fair",
        StringKeys.LIB_CONDITION_DAMAGED        to "Damaged",
        StringKeys.LIB_DAMAGE_NOTES             to "Damage notes",
        StringKeys.LIB_CONFIRM_RETURN           to "Confirm Return",
        StringKeys.LIB_MARK_LOST_TITLE          to "Mark as Lost?",
        StringKeys.LIB_WAIVE_FINE_TITLE         to "Waive Fine?",
        StringKeys.LIB_WAIVER_REASON            to "Reason for waiver *",
        StringKeys.LIB_SETTINGS                 to "Library Settings",
        StringKeys.LIB_LOADING_SETTINGS         to "Loading settings...",
        StringKeys.LIB_DEFAULT_LOAN_DAYS        to "Default Loan Days",
        StringKeys.LIB_FINE_PER_DAY             to "Fine Per Day (₹)",
        StringKeys.LIB_MAX_BOOKS                to "Max Books Per Student",
        StringKeys.LIB_MAX_RENEWALS             to "Max Renewals",
        StringKeys.LIB_RESERVATION_TIMEOUT      to "Reservation Timeout (days)",
        StringKeys.LIB_DUE_REMINDER             to "Due Reminder (days before)",
        StringKeys.LIB_FINE_CAP                 to "Fine Cap Enabled",
        StringKeys.LIB_QUICK_ISSUE_ENABLED      to "Quick Issue Enabled",
        StringKeys.LIB_BULK_RETURN_ENABLED      to "Bulk Return Enabled",
        StringKeys.LIB_LEADERBOARD_ENABLED      to "Leaderboard Enabled",
        StringKeys.LIB_SAVE_SETTINGS            to "Save Settings",
        StringKeys.LIB_RESET_DEFAULTS           to "Reset to Defaults",
        StringKeys.LIB_BOOK_ID                  to "Book ID",
        StringKeys.LIB_LOAD_RESERVATIONS        to "Load Reservations",
        StringKeys.LIB_NO_RESERVATIONS          to "No reservations",
        StringKeys.LIB_NO_RESERVATIONS_DESC     to "Enter a book ID to view its reservation queue.",
        StringKeys.LIB_FULFILL                  to "Fulfill",
        StringKeys.LIB_QUICK_ISSUE_TAB          to "Quick Issue",
        StringKeys.LIB_QUICK_ISSUE_DESC         to "Scan or enter a barcode to instantly issue a book.",
        StringKeys.LIB_BARCODE                  to "Barcode",
        StringKeys.LIB_BORROWER_ID_LABEL        to "Borrower ID",
        StringKeys.LIB_BORROWER_NAME_LABEL      to "Borrower Name",
        StringKeys.LIB_BULK_RETURN_TAB          to "Bulk Return",
        StringKeys.LIB_BULK_RETURN_DESC         to "Scan barcodes sequentially, then end the session.",
        StringKeys.LIB_SCAN_BARCODE             to "Scan barcode",
        StringKeys.LIB_ADD                      to "Add",
        StringKeys.LIB_END_SESSION              to "End Session & Return All",
        StringKeys.LIB_NO_BARCODES              to "No barcodes scanned",
        StringKeys.LIB_NO_BARCODES_DESC         to "Scan barcodes above to start a bulk return session.",
        StringKeys.LIB_CONFIRM_BULK_RETURN      to "Confirm Bulk Return",
        StringKeys.LIB_RETURN_ALL               to "Return All",
        StringKeys.LIB_CATEGORIES_TAB           to "Categories",
        StringKeys.LIB_ADD_CATEGORY             to "+ Add",
        StringKeys.LIB_NO_CATEGORIES            to "No categories",
        StringKeys.LIB_NO_CATEGORIES_DESC       to "Create categories to organize your library.",
        StringKeys.LIB_NEW_CATEGORY             to "New Category",
        StringKeys.LIB_NAME                     to "Name",
        StringKeys.LIB_COLOR                    to "Color (hex)",
        StringKeys.LIB_ICON_NAME                to "Icon name",
        StringKeys.LIB_DELETE_CATEGORY_TITLE    to "Delete Category?",
        StringKeys.LIB_AUDIT_TRAIL              to "Audit Trail",
        StringKeys.LIB_NO_AUDIT                 to "No audit logs",
        StringKeys.LIB_NO_AUDIT_DESC            to "Audit entries will appear here as actions are performed.",
        StringKeys.LIB_ANNOUNCEMENTS_TAB        to "Announcements",
        StringKeys.LIB_NEW_ANNOUNCEMENT         to "+ New",
        StringKeys.LIB_NO_ANNOUNCEMENTS         to "No announcements",
        StringKeys.LIB_NO_ANNOUNCEMENTS_DESC    to "Post library announcements and notices here.",
        StringKeys.LIB_INACTIVE                 to "Inactive",
        StringKeys.LIB_DEACTIVATE               to "Deactivate",
        StringKeys.LIB_ACTIVATE                 to "Activate",
        StringKeys.LIB_NEW_ANNOUNCEMENT_TITLE   to "New Announcement",
        StringKeys.LIB_ANN_TITLE                to "Title",
        StringKeys.LIB_ANN_BODY                 to "Body",
        StringKeys.LIB_POST                     to "Post",
        StringKeys.LIB_DELETE_ANN_TITLE         to "Delete Announcement?",
        StringKeys.LIB_ACQUISITION_REQUESTS     to "Acquisition Requests",
        StringKeys.LIB_NO_REQUESTS              to "No requests",
        StringKeys.LIB_NO_REQUESTS_DESC         to "Acquisition requests from teachers will appear here.",
        StringKeys.LIB_APPROVE                  to "Approve",
        StringKeys.LIB_ORDER                    to "Order",
        StringKeys.LIB_RECEIVE                  to "Receive",
        StringKeys.LIB_CONVERT_TO_BOOK          to "Convert to Book",
        StringKeys.LIB_MORE_TAB                 to "More",
        StringKeys.LIB_QUICK_ACTIONS            to "Quick Actions",
        StringKeys.LIB_EXPORT_CATALOG           to "Export Catalog (CSV)",
        StringKeys.LIB_IMPORT_BOOKS             to "Import Books (JSON)",
        StringKeys.LIB_TRENDING_BOOKS           to "Trending Books",
        StringKeys.LIB_REPAIR_QUEUE             to "Repair Queue",
        StringKeys.LIB_NO_REPAIR                to "No books in repair",
        StringKeys.LIB_NO_REPAIR_DESC           to "Damaged copies will appear here.",
        StringKeys.LIB_MARK_REPAIRED            to "Mark Repaired",
        StringKeys.LIB_IMPORT_BOOKS_TITLE       to "Import Books (JSON)",
        StringKeys.LIB_PASTE_JSON               to "Paste JSON array of book objects:",
        StringKeys.LIB_JSON_LABEL               to "JSON",
        StringKeys.LIB_IMPORT                   to "Import",
        StringKeys.LIB_BOOK_COPIES              to "Book Copies",
        StringKeys.LIB_COPIES_DESC              to "View and manage individual copy records for a book.",
        StringKeys.LIB_LOAD_COPIES              to "Load Copies",
        StringKeys.LIB_NO_COPIES                to "No copies loaded",
        StringKeys.LIB_NO_COPIES_DESC           to "Enter a book ID above to view its copies.",
        StringKeys.LIB_ADD_COPY                 to "+ Add Copy",
        StringKeys.LIB_ADD_COPY_TITLE           to "Add Copy",
        StringKeys.LIB_CONDITION_LABEL          to "Condition:",
        StringKeys.LIB_CONDITION_NEW            to "New",
        StringKeys.LIB_CONDITION_POOR           to "Poor",
        StringKeys.LIB_BOOK_HISTORY             to "Book History",
        StringKeys.LIB_HISTORY_DESC             to "View the full issue history for a specific book.",
        StringKeys.LIB_LOAD_HISTORY             to "Load History",
        StringKeys.LIB_NO_HISTORY               to "No history loaded",
        StringKeys.LIB_NO_HISTORY_DESC          to "Enter a book ID above to view its issue history.",
        StringKeys.LIB_MARK_LOST_MSG            to "This will mark \"{title}\" as lost and may incur a fine for the borrower.",
        StringKeys.LIB_BULK_RETURN_MSG          to "Return {count} book(s)?",
        StringKeys.LIB_DELETE_CATEGORY_MSG      to "Are you sure you want to delete \"{name}\"? Books in this category will remain but lose their category label.",
        StringKeys.LIB_DELETE_ANN_MSG           to "Are you sure you want to delete \"{title}\"? This cannot be undone.",
        StringKeys.LIB_ISBN_PREFIX              to "ISBN: {value}",
        StringKeys.LIB_DUE_PREFIX               to "Due: {date}",
        StringKeys.LIB_FINE_PREFIX              to "Fine: ₹{amount} for \"{title}\"",
        StringKeys.LIB_WAITLIST_PREFIX          to "Waitlist #{position}",
        StringKeys.LIB_RESERVED_PREFIX          to "Reserved: {date}",
        StringKeys.LIB_BY_PREFIX                to "By: {name}",
        StringKeys.LIB_EXPIRES_PREFIX           to "Expires: {date}",
        StringKeys.LIB_NEVER                    to "Never",
        StringKeys.LIB_AUTHOR_PREFIX            to "Author: {name}",
        StringKeys.LIB_PUBLISHER_PREFIX         to "Publisher: {name}",
        StringKeys.LIB_REASON_PREFIX            to "Reason: {reason}",
        StringKeys.LIB_COPY_PREFIX              to "Copy #{id}",
        StringKeys.LIB_BARCODE_PREFIX           to "Barcode: {code}",
        StringKeys.LIB_ISSUED_PREFIX            to "Issued: {date}",
        StringKeys.LIB_RETURNED_PREFIX          to "Returned: {date}",
        StringKeys.LIB_PENDING_LABEL            to "pending",
        StringKeys.LIB_BARCODES_SCANNED         to "{count} barcode(s) scanned",
        StringKeys.LIB_COPIES_COUNT             to "{count} copies",
        StringKeys.LIB_RECORDS_COUNT            to "{count} records",
        StringKeys.LIB_ISSUES_COUNT             to "{count} issues",

        // Gamification
        StringKeys.GAM_EARNED_BADGES            to "Earned Badges",
        StringKeys.GAM_SHOUTOUT_PH              to "Type a shoutout message...",
        StringKeys.GAM_PARENT_ALERT_PH          to "Type a parent alert...",
        StringKeys.GAM_CLASS_LEADERBOARD        to "Class Leaderboard",
        StringKeys.GAM_CLASS_GOALS              to "Class Goals",
        StringKeys.GAM_GOAL_TYPE_PH             to "Goal type (e.g. attendance, marks)",
        StringKeys.GAM_GOAL_TARGET_PH           to "Target value",
        StringKeys.GAM_GOAL_REWARD_PH           to "Reward (e.g. 50 XP)",
        StringKeys.GAM_RECENT_SHOUTOUTS         to "Recent Shoutouts",
        StringKeys.GAM_MENTOR_ASSIGNMENTS       to "Mentor Assignments",
        StringKeys.GAM_MENTOR_ID_PH             to "Mentor student ID",
        StringKeys.GAM_MENTEE_ID_PH             to "Mentee student ID",
        StringKeys.GAM_STUDY_BUDDY_PAIRS        to "Study Buddy Pairs",
        StringKeys.GAM_STUDY_BUDDIES            to "Study Buddies",
        StringKeys.GAM_REMOVE                   to "Remove",
        StringKeys.GAM_BUDDY1_ID_PH             to "Buddy 1 student ID",
        StringKeys.GAM_BUDDY2_ID_PH             to "Buddy 2 student ID",
        StringKeys.GAM_NO_DATA                  to "No gamification data yet",
        StringKeys.GAM_NO_DATA_DESC             to "Configure feature flags and create badges, levels, and rewards to get started.",
        StringKeys.GAM_MANAGEMENT_CONSOLE       to "Management Console",
        StringKeys.GAM_FEATURE_FLAGS            to "Feature Flags",
        StringKeys.GAM_UNABLE_FLAGS             to "Unable to load feature flags",
        StringKeys.GAM_GRANULAR_TOGGLES         to "Granular Toggles",
        StringKeys.GAM_LEADERBOARDS             to "Leaderboards",
        StringKeys.GAM_LEADERBOARDS_DESC        to "Configure leaderboard visibility and scoring",
        StringKeys.GAM_REWARDS_SHOP             to "Rewards Shop",
        StringKeys.GAM_REWARDS_SHOP_DESC        to "Manage redeemable rewards and inventory",
        StringKeys.GAM_HOUSE_SYSTEM             to "House System",
        StringKeys.GAM_HOUSE_SYSTEM_DESC        to "Configure houses and point allocation",
        StringKeys.GAM_BOOST_TYPE               to "Boost type",
        StringKeys.GAM_MULTIPLIER               to "Multiplier (e.g. 2x)",
        StringKeys.GAM_TARGET_SCOPE             to "Target scope",
        StringKeys.GAM_DURATION_HOURS           to "Duration (hours)",
        StringKeys.GAM_CREATE_BOOST             to "Create Boost",
        StringKeys.GAM_MENTOR_PREFIX            to "Mentor: {id}...",
        StringKeys.GAM_MENTEE_PREFIX            to "Mentee: {id}...",
        StringKeys.GAM_STUDENT_PREFIX           to "Student #{id}",
        StringKeys.GAM_DELETE_SHOUTOUT          to "Delete shoutout",
        StringKeys.GAM_STUDENT1_ID_PH           to "Student 1 ID",
        StringKeys.GAM_STUDENT2_ID_PH           to "Student 2 ID",
        StringKeys.GAM_GOAL_TARGET_NUM_PH       to "Target (e.g. 90)",
        StringKeys.GAM_TOOLS                    to "Gamification Tools",
        StringKeys.GAM_CLASS_GAMIFICATION       to "Class Gamification",
        StringKeys.GAM_ENCOURAGE                to "Encourage",
        StringKeys.GAM_SPOTLIGHT                to "Spotlight",
        StringKeys.GAM_CANCEL_SHOUTOUT          to "Cancel Shoutout",
        StringKeys.GAM_SEND_SHOUTOUT            to "Send Shoutout",
        StringKeys.GAM_CANCEL_QUEST             to "Cancel Quest",
        StringKeys.GAM_ASSIGN_QUEST             to "Assign Quest",
        StringKeys.GAM_CANCEL_BADGE             to "Cancel Badge",
        StringKeys.GAM_AWARD_BADGE              to "Award Badge",
        StringKeys.GAM_CANCEL_ALERT             to "Cancel Alert",
        StringKeys.GAM_PARENT_ALERT             to "Parent Alert",
        StringKeys.GAM_SEND_ALERT               to "Send Alert",
        StringKeys.GAM_CONFIRM_PEP_TALK         to "Confirm Pep Talk",
        StringKeys.GAM_SEND_PEP_TALK            to "Send Pep Talk",
        StringKeys.GAM_CREATE_CLASS_GOAL        to "Create Class Goal",
        StringKeys.GAM_CREATE_GOAL              to "Create Goal",
        StringKeys.GAM_ASSIGN_MENTOR            to "Assign Mentor",
        StringKeys.GAM_ASSIGN                   to "Assign",
        StringKeys.GAM_PAIR_STUDY_BUDDIES       to "Pair Study Buddies",
        StringKeys.GAM_PAIR_THEM                to "Pair Them",
        StringKeys.GAM_TOTAL_XP                 to "Total XP",
        StringKeys.GAM_LEVEL                    to "Level",
        StringKeys.GAM_STREAK                   to "Streak",
        StringKeys.GAM_BADGES                   to "Badges",
        StringKeys.GAM_QUESTS                   to "Quests",
        StringKeys.GAM_BUDDY_PAIR               to "{id1}... & {id2}...",
        StringKeys.GAM_PEP_TALK_CONFIRM         to "Send a motivational pep talk to {className}{section}?",
        StringKeys.GAM_REWARD_PREFIX            to "Reward: {reward}",
        StringKeys.GAM_UNKNOWN                  to "Unknown",
        StringKeys.GAM_GOAL                     to "Goal",
        StringKeys.GAM_QUEST_BUTTON             to "{name} · +{xp} XP",
        StringKeys.GAM_BADGE_BUTTON             to "{name} · {category}",
        StringKeys.GAM_SHOUTOUT_FROM_TO         to "{sender} → {receiver}",
        StringKeys.GAM_PROGRESS_FRACTION        to "{current}/{target}",
        StringKeys.GAM_XP_VALUE                 to "{xp} XP",

        // AdminGamification
        StringKeys.AGAM_NO_DATA                 to "No gamification data yet",
        StringKeys.AGAM_NO_DATA_DESC            to "Configure feature flags and create badges, levels, and rewards to get started.",
        StringKeys.AGAM_GAMIFICATION            to "Gamification",
        StringKeys.AGAM_MANAGEMENT_CONSOLE      to "Management Console",
        StringKeys.AGAM_FEATURE_FLAGS           to "Feature Flags",
        StringKeys.AGAM_UNABLE_LOAD_FLAGS       to "Unable to load flags",
        StringKeys.AGAM_ENABLE_GAM              to "Enable Gamification",
        StringKeys.AGAM_ENABLE_GAM_DESC         to "Master kill switch — turns entire system on/off",
        StringKeys.AGAM_GRANULAR_TOGGLES        to "Granular Toggles",
        StringKeys.AGAM_LEADERBOARDS            to "Leaderboards",
        StringKeys.AGAM_LEADERBOARDS_DESC       to "Class & school rankings",
        StringKeys.AGAM_REWARDS_SHOP            to "Rewards Shop",
        StringKeys.AGAM_REWARDS_SHOP_DESC       to "Spend XP on real rewards",
        StringKeys.AGAM_HOUSE_SYSTEM            to "House System",
        StringKeys.AGAM_HOUSE_SYSTEM_DESC       to "Guilds & collective competition",
        StringKeys.AGAM_QUESTS_LABEL            to "Quests",
        StringKeys.AGAM_QUESTS_DESC             to "Daily, weekly & seasonal quests",
        StringKeys.AGAM_MENTOR_SYSTEM           to "Mentor System",
        StringKeys.AGAM_MENTOR_SYSTEM_DESC      to "Peer mentor & study buddy",
        StringKeys.AGAM_SHOUT_OUTS              to "Shout-Outs",
        StringKeys.AGAM_SHOUT_OUTS_DESC         to "Peer encouragement",
        StringKeys.AGAM_SEASONAL_EVENTS         to "Seasonal Events",
        StringKeys.AGAM_SEASONAL_EVENTS_DESC    to "Limited-edition badges",
        StringKeys.AGAM_CLASS_GOALS             to "Class Goals",
        StringKeys.AGAM_CLASS_GOALS_DESC        to "Collective rewards",
        StringKeys.AGAM_COMBOS                  to "Combos",
        StringKeys.AGAM_COMBOS_DESC             to "Consecutive activity multipliers",
        StringKeys.AGAM_XP_BOOSTS               to "XP Boosts",
        StringKeys.AGAM_XP_BOOSTS_DESC          to "Time-limited multipliers",
        StringKeys.AGAM_ANALYTICS_OVERVIEW      to "Analytics Overview",
        StringKeys.AGAM_REDEMPTIONS             to "Redemptions",
        StringKeys.AGAM_BADGE_CATALOG           to "Badge Catalog ({count})",
        StringKeys.AGAM_BADGE_DETAIL            to "{category} · {rarity} · {xp} XP",
        StringKeys.AGAM_SEASONAL                to "Seasonal",
        StringKeys.AGAM_LEVEL_DEFINITIONS       to "Level Definitions ({count})",
        StringKeys.AGAM_LEVEL_XP                to "{xp} XP",
        StringKeys.AGAM_HOUSES                  to "Houses ({count})",
        StringKeys.AGAM_HOUSE_DETAIL            to "{members} members · {points} pts",
        StringKeys.AGAM_REWARDS_CATALOG         to "Rewards Catalog ({count})",
        StringKeys.AGAM_REWARD_XP               to "{xp} XP",
        StringKeys.AGAM_ACTIVE                  to "Active",
        StringKeys.AGAM_INACTIVE                to "Inactive",
        StringKeys.AGAM_QUEST_POOL              to "Quest Pool ({count})",
        StringKeys.AGAM_QUEST_DETAIL            to "{type} · {xp} XP",
        StringKeys.AGAM_EVENTS_TITLE            to "Seasonal Events ({count})",
        StringKeys.AGAM_EVENT_DATES             to "{start} → {end}",
        StringKeys.AGAM_ENDED                   to "Ended",
        StringKeys.AGAM_SCHOOL_LEADERBOARD      to "School Leaderboard (Top {count})",
        StringKeys.AGAM_LV                      to "Lv {level}",
        StringKeys.AGAM_REDEMPTION_APPROVALS    to "Redemption Approvals ({count})",
        StringKeys.AGAM_REDEMPTION_DETAIL       to "{xp} XP · {status}",
        StringKeys.AGAM_APPROVE                 to "Approve",
        StringKeys.AGAM_REJECT                  to "Reject",
        StringKeys.AGAM_BOOSTS_TITLE            to "XP Boosts ({count})",
        StringKeys.AGAM_BOOST_MULT              to "{mult}x",
        StringKeys.AGAM_EXPIRED                 to "Expired",
        StringKeys.AGAM_CREATE_NEW_BOOST        to "+ Create New Boost",
        StringKeys.AGAM_BOOST_TYPE              to "Boost Type",
        StringKeys.AGAM_MULTIPLIER_LABEL        to "Multiplier (e.g. 2.0)",
        StringKeys.AGAM_TARGET_SCOPE            to "Target Scope (ALL / CLASS / STUDENT)",
        StringKeys.AGAM_DURATION_HOURS          to "Duration (hours)",
        StringKeys.AGAM_CREATE_BOOST            to "Create Boost",

        // ParentAcademics
        StringKeys.PAC_ACADEMIC_OVERVIEW        to "Academic Overview",
        StringKeys.PAC_EMOTIONAL_INTEL          to "Emotional Intelligence",
        StringKeys.PAC_THIS_TERM                to "This Term",
        StringKeys.PAC_ATTENDANCE_RATE          to "Attendance Rate",
        StringKeys.PAC_NO_ATTENDANCE            to "No attendance data yet",
        StringKeys.PAC_NO_ATTENDANCE_DESC       to "Attendance will appear here once records are available.",
        StringKeys.PAC_TYPE_ANSWER              to "Type your answer...",
        StringKeys.PAC_LOADING_LEADERBOARD      to "Loading leaderboard...",
        StringKeys.PAC_CLASS_SCHEDULE           to "Class Schedule",
        StringKeys.PAC_SOMETHING_WRONG          to "Something went wrong",
        StringKeys.PAC_REPORT_CARD              to "Report Card",
        StringKeys.PAC_APPLY_LEAVE              to "Apply Leave",
        StringKeys.PAC_HEALTH_RECORDS           to "Health Records",
        StringKeys.PAC_SYLLABUS                 to "Syllabus",
        StringKeys.PAC_LEVEL                    to "Level {level}",
        StringKeys.PAC_ATTENDANCE               to "Attendance",
        StringKeys.PAC_AVG_SCORE                to "Avg Score",
        StringKeys.PAC_COVERED                  to "Covered",
        StringKeys.PAC_PENDING                  to "Pending",
        StringKeys.PAC_START                    to "Start",
        StringKeys.PAC_RESULT                   to "Result",
        StringKeys.PAC_QUIZ                     to "Quiz",
        StringKeys.PAC_SCORE                    to "Score: {score} / {total}",
        StringKeys.PAC_YOUR_ANSWER              to "Your answer: {answer}",
        StringKeys.PAC_CORRECT_ANSWER           to "Correct: {answer}",
        StringKeys.PAC_SUBMIT_HOMEWORK          to "Submit Homework",
        StringKeys.PAC_EST                      to "EST",
        StringKeys.PAC_LEADERBOARD              to "Leaderboard",
        StringKeys.PAC_PARTICIPANTS             to "{count} participants",
        StringKeys.PAC_YOU                      to "(You)",
        StringKeys.PAC_ROOM                     to "Room {room}",
        StringKeys.PAC_LIVE                     to "LIVE",
        StringKeys.PAC_COLLAPSE                 to "Collapse",
        StringKeys.PAC_EXPAND                   to "Expand",
        StringKeys.PAC_MATCH                    to "(Match)",
        StringKeys.PAC_NO_LOG                   to "No log updated by the teacher",
        StringKeys.PAC_NO_LOG_DESC              to "Daily homework summaries will appear here once the teacher updates them.",
        StringKeys.PAC_QUICK_ACTIONS            to "Quick Actions",
        StringKeys.PAC_ACHIEVEMENTS             to "Achievements",
        StringKeys.PAC_FREE_PERIOD              to "Free Period",
        StringKeys.PAC_TRUE_FALSE               to "(True/False)",
        StringKeys.PAC_FILL_BLANK               to "(Fill in the blank)",
        StringKeys.PAC_ESTIMATED_NOTE           to "Teacher hasn't updated progress. Estimated based on scheduled classes.",
        StringKeys.PAC_AI                       to "AI",

        // Exam screens
        StringKeys.EXAM_IMPORT_METHOD           to "Import Method",
        StringKeys.EXAM_PASTE_MARKS             to "Paste marks sheet text",
        StringKeys.EXAM_EXTRACTING_MARKS        to "Extracting marks...",
        StringKeys.EXAM_MAY_TAKE_SECONDS        to "This may take a few seconds.",
        StringKeys.EXAM_IMPORT_FAILED           to "Import Failed",
        StringKeys.EXAM_TRY_AGAIN               to "Try Again",
        StringKeys.EXAM_IMAGE_UNAVAILABLE       to "Image Picker Unavailable",
        StringKeys.EXAM_PASTE_TEXT_INSTEAD      to "Paste Text Instead",
        StringKeys.EXAM_EXTRACTION_RESULTS      to "Extraction Results",
        StringKeys.EXAM_TIMETABLE_NAME          to "Timetable name (e.g. Mid Term 2026)",
        StringKeys.EXAM_TERM_OPTIONAL           to "Term (optional)",
        StringKeys.EXAM_PASTE_TIMETABLE         to "Paste exam timetable text",
        StringKeys.EXAM_EXTRACTING_ENTRIES      to "Extracting entries...",
        StringKeys.EXAM_EXTRACTED_ENTRIES       to "Extracted Entries",
        StringKeys.EXAM_ALL_CLASSES             to "All Classes",
        StringKeys.EXAM_NO_ASSESSMENTS          to "No assessments found",

        // SkillTestCard
        StringKeys.SKILL_GENERATING             to "Generating questions...",
        StringKeys.SKILL_EVALUATING             to "Evaluating answers...",

        // Misc screens
        StringKeys.MISC_PROFILE_PICTURE         to "Profile picture",
        StringKeys.MISC_SCHOOL_LOGO             to "School logo",
        StringKeys.MISC_RECOMMENDED_RATIO       to "Recommended: 1:1 ratio",
        StringKeys.MISC_ADD_PHOTO               to "Add photo",
        StringKeys.MISC_PROFILE_COMPLETION      to "Profile completion",
        StringKeys.MISC_PROFILE_DESC            to "School details, visibility, gallery and tour media.",
        StringKeys.MISC_ENTER_UUID              to "Enter student UUID or student code",
        StringKeys.MISC_SELECT_CONFIGURED       to "Please select a configured class",
        StringKeys.MISC_SAVED_SUCCESS           to "Saved successfully",
        StringKeys.MISC_SAVE_CHANGES            to "Save Changes",
        StringKeys.MISC_ATTENDANCE_SAVED        to "Attendance saved",
        StringKeys.MISC_SAVE_ATTENDANCE         to "Save Attendance",
        StringKeys.MISC_PASTE_CSV               to "or paste CSV content manually",
        StringKeys.MISC_CONVERSION_RATE         to "Conversion rate",
        StringKeys.MISC_TUTOR_THINKING          to "Tutor is thinking",
        StringKeys.MISC_PAID_ON                 to "Paid on: {date}",
        StringKeys.MISC_VIEW_SALARY             to "View Salary History",
        StringKeys.MISC_SALARY_DESC             to "See your monthly salary breakdown and payment status",
        StringKeys.MISC_ANSWER_NOTES            to "Answer / Notes",
        StringKeys.MISC_NO_AT_RISK              to "No at-risk students",
        StringKeys.MISC_ALL_ON_TRACK            to "All students are on track for this class",
        StringKeys.MISC_MARK_ATTENDANCE         to "Mark Attendance",
        StringKeys.MISC_OR_PASTE_CSV            to "or paste CSV content manually",
    )

    private val en: Map<String, String> = enPart1() + enPart2()

    private fun hiPart1(): Map<String, String> = mapOf(
        StringKeys.COMMON_BUTTON_SAVE      to "सहेजें",
        StringKeys.COMMON_BUTTON_CANCEL    to "रद्द करें",
        StringKeys.COMMON_BUTTON_RETRY     to "पुनः प्रयास करें",
        StringKeys.COMMON_BUTTON_DELETE    to "हटाएं",
        StringKeys.COMMON_BUTTON_EDIT      to "संपादित करें",
        StringKeys.COMMON_BUTTON_CLOSE     to "बंद करें",
        StringKeys.COMMON_BUTTON_CONTINUE  to "जारी रखें",
        StringKeys.COMMON_BUTTON_BACK      to "वापस",
        StringKeys.COMMON_BUTTON_CONFIRM   to "पुष्टि करें",
        StringKeys.COMMON_BUTTON_APPLY     to "लागू करें",
        StringKeys.COMMON_BUTTON_DONE      to "हो गया",
        StringKeys.COMMON_BUTTON_NEXT      to "अगला",
        StringKeys.COMMON_BUTTON_SKIP      to "छोड़ें",
        StringKeys.COMMON_BUTTON_REFRESH   to "रिफ्रेश करें",
        StringKeys.COMMON_BUTTON_SHARE     to "शेयर करें",
        StringKeys.COMMON_BUTTON_LOGOUT    to "लॉग आउट",
        StringKeys.COMMON_ERROR_GENERIC    to "कुछ गलत हुआ",
        StringKeys.COMMON_ERROR_NETWORK    to "नेटवर्क त्रुटि। कृपया अपना कनेक्शन जांचें।",
        StringKeys.COMMON_ERROR_OFFLINE    to "आप ऑफ़लाइन हैं। कृपया अपना कनेक्शन जांचें।",
        StringKeys.COMMON_ERROR_TIMEOUT    to "अनुरोध समय समाप्त। कृपया पुनः प्रयास करें।",
        StringKeys.COMMON_ERROR_NOT_FOUND  to "नहीं मिला",
        StringKeys.COMMON_ERROR_UNAUTHORIZED to "आप इस क्रिया को करने के लिए अधिकृत नहीं हैं।",
        StringKeys.COMMON_LOADING          to "लोड हो रहा है…",
        StringKeys.COMMON_EMPTY            to "यहाँ कुछ नहीं है",
        StringKeys.COMMON_SEARCH           to "खोजें",
        StringKeys.COMMON_FILTER           to "फ़िल्टर",
        StringKeys.COMMON_ALL              to "सभी",
        StringKeys.COMMON_NONE             to "कोई नहीं",
        StringKeys.COMMON_YES              to "हाँ",
        StringKeys.COMMON_NO               to "नहीं",
        StringKeys.COMMON_TODAY            to "आज",
        StringKeys.COMMON_YESTERDAY        to "कल",
        StringKeys.COMMON_TOMORROW         to "कल",
        StringKeys.COMMON_SELECT           to "चुनें",
        StringKeys.COMMON_REQUIRED         to "आवश्यक",
        StringKeys.COMMON_OPTIONAL         to "वैकल्पिक",
        // Auth
        StringKeys.AUTH_WELCOME            to "विद्या प्रयाग में आपका स्वागत है",
        StringKeys.AUTH_LOGIN              to "लॉग इन करें",
        StringKeys.AUTH_SIGNUP             to "साइन अप करें",
        StringKeys.AUTH_LOGOUT             to "लॉग आउट",
        StringKeys.AUTH_PHONE              to "फ़ोन नंबर",
        StringKeys.AUTH_EMAIL              to "ईमेल",
        StringKeys.AUTH_PASSWORD           to "पासवर्ड",
        StringKeys.AUTH_OTP                to "OTP दर्ज करें",
        StringKeys.AUTH_OTP_SENT           to "आपके फ़ोन पर OTP भेजा गया",
        StringKeys.AUTH_OTP_VERIFY         to "OTP सत्यापित करें",
        StringKeys.AUTH_NAME               to "पूरा नाम",
        StringKeys.AUTH_ROLE_PARENT        to "अभिभावक",
        StringKeys.AUTH_ROLE_TEACHER       to "शिक्षक",
        StringKeys.AUTH_ROLE_ADMIN         to "स्कूल व्यवस्थापक",
        StringKeys.AUTH_LOGIN_SUCCESS      to "लॉगिन सफल",
        StringKeys.AUTH_LOGIN_FAILED       to "लॉगिन विफल। कृपया पुनः प्रयास करें।",
        StringKeys.AUTH_REGISTER_SCHOOL    to "अपना स्कूल पंजीकृत करें",
        // Language
        StringKeys.LANGUAGE_TITLE          to "भाषा चुनें",
        StringKeys.LANGUAGE_SELECT         to "अपनी पसंदीदा भाषा चुनें",
        StringKeys.LANGUAGE_CHANGE         to "भाषा बदलें",
        StringKeys.LANGUAGE_CURRENT        to "वर्तमान भाषा",
        StringKeys.LANGUAGE_ENGLISH        to "अंग्रेज़ी",
        StringKeys.LANGUAGE_SEARCH         to "भाषा खोजें…",
        // Nav
        StringKeys.NAV_HOME                to "होम",
        StringKeys.NAV_DASHBOARD           to "डैशबोर्ड",
        StringKeys.NAV_PROFILE             to "प्रोफ़ाइल",
        StringKeys.NAV_SETTINGS            to "सेटिंग्स",
        StringKeys.NAV_NOTIFICATIONS       to "सूचनाएँ",
        StringKeys.NAV_MESSAGES            to "संदेश",
        StringKeys.NAV_CALENDAR            to "कैलेंडर",
        StringKeys.NAV_ATTENDANCE          to "उपस्थिति",
        StringKeys.NAV_FEES                to "फीस",
        StringKeys.NAV_ACADEMICS           to "शैक्षणिक",
        StringKeys.NAV_MORE                to "अधिक",
        // Dashboard
        StringKeys.DASH_GOOD_MORNING       to "सुप्रभात",
        StringKeys.DASH_GOOD_AFTERNOON     to "नमस्कार",
        StringKeys.DASH_GOOD_EVENING       to "शुभ संध्या",
        StringKeys.DASH_WELCOME_BACK       to "वापसी पर स्वागत है, {name}",
        StringKeys.DASH_QUICK_STATS        to "त्वरित आँकड़े",
        StringKeys.DASH_RECENT_ACTIVITY    to "हाल की गतिविधि",
        // Attendance
        StringKeys.ATT_PRESENT             to "उपस्थित",
        StringKeys.ATT_ABSENT              to "अनुपस्थित",
        StringKeys.ATT_LATE                to "देर से",
        StringKeys.ATT_HALF_DAY            to "अर्ध दिवस",
        StringKeys.ATT_MARK_PRESENT        to "उपस्थित चिह्नित करें",
        StringKeys.ATT_MARK_ABSENT         to "अनुपस्थित चिह्नित करें",
        StringKeys.ATT_RATE                to "{count}% उपस्थिति",
        StringKeys.ATT_RATE_PLURAL         to "{count}% उपस्थिति",
        // Fees
        StringKeys.FEE_PAID                to "भुगतान हो गया",
        StringKeys.FEE_DUE                 to "देय",
        StringKeys.FEE_OVERDUE             to "अतिदेय",
        StringKeys.FEE_PAY_NOW             to "अभी भुगतान करें",
        StringKeys.FEE_HISTORY             to "भुगतान इतिहास",
        StringKeys.FEE_AMOUNT              to "राशि",
        StringKeys.FEE_DUE_DATE            to "देय तिथि",
        StringKeys.FEE_TOTAL               to "कुल",
        StringKeys.FEE_PENDING             to "लंबित",
        // Notifications
        StringKeys.NOTIF_TITLE             to "सूचनाएँ",
        StringKeys.NOTIF_MARK_READ         to "पढ़ा हुआ चिह्नित करें",
        StringKeys.NOTIF_MARK_ALL_READ     to "सभी पढ़ा हुआ चिह्नित करें",
        StringKeys.NOTIF_EMPTY             to "कोई सूचना नहीं",
        StringKeys.NOTIF_UNREAD            to "{count} अपठित सूचना",
        StringKeys.NOTIF_UNREAD_PLURAL     to "{count} अपठित सूचनाएँ",
        // Profile
        StringKeys.PROFILE_TITLE           to "प्रोफ़ाइल",
        StringKeys.PROFILE_EDIT            to "प्रोफ़ाइल संपादित करें",
        StringKeys.PROFILE_NAME            to "नाम",
        StringKeys.PROFILE_PHONE           to "फ़ोन",
        StringKeys.PROFILE_EMAIL           to "ईमेल",
        StringKeys.PROFILE_SCHOOL          to "स्कूल",
        StringKeys.PROFILE_ROLE            to "भूमिका",
        StringKeys.PROFILE_LANGUAGE        to "भाषा",
        StringKeys.PROFILE_THEME           to "थीम",
        StringKeys.PROFILE_ABOUT           to "बारे में",
        StringKeys.PROFILE_HELP            to "सहायता और समर्थन",
        StringKeys.PROFILE_PRIVACY         to "गोपनीयता नीति",
        // Settings
        StringKeys.SETTINGS_TITLE          to "सेटिंग्स",
        StringKeys.SETTINGS_GENERAL        to "सामान्य",
        StringKeys.SETTINGS_NOTIFICATIONS  to "सूचनाएँ",
        StringKeys.SETTINGS_LANGUAGE       to "भाषा",
        StringKeys.SETTINGS_THEME          to "थीम",
        StringKeys.SETTINGS_ABOUT          to "बारे में",
        StringKeys.SETTINGS_LOGOUT         to "लॉग आउट",
        StringKeys.SETTINGS_FONT_SIZE      to "फ़ॉन्ट आकार",
        // Child
        StringKeys.CHILD_TITLE             to "मेरे बच्चे",
        StringKeys.CHILD_ADD               to "बच्चा जोड़ें",
        StringKeys.CHILD_LINK              to "बच्चा लिंक करें",
        StringKeys.CHILD_NAME              to "बच्चे का नाम",
        StringKeys.CHILD_CLASS             to "कक्षा",
        StringKeys.CHILD_SECTION           to "अनुभाग",
        StringKeys.CHILD_ROLL              to "रोल नंबर",
        StringKeys.CHILD_PROGRESS          to "प्रगति",
        StringKeys.CHILD_ATTENDANCE        to "उपस्थिति",
        StringKeys.CHILD_MARKS             to "अंक",
        StringKeys.CHILD_STUDENTS          to "{count} छात्र",
        StringKeys.CHILD_STUDENTS_PLURAL   to "{count} छात्र",
        // School
        StringKeys.SCHOOL_TITLE            to "स्कूल",
        StringKeys.SCHOOL_NAME             to "स्कूल का नाम",
        StringKeys.SCHOOL_CLASSES          to "कक्षाएँ",
        StringKeys.SCHOOL_TEACHERS         to "शिक्षक",
        StringKeys.SCHOOL_STUDENTS         to "छात्र",
        StringKeys.SCHOOL_ONBOARDING       to "ऑनबोर्डिंग",
        StringKeys.SCHOOL_BRANDING         to "ब्रांडिंग",
        StringKeys.SCHOOL_ACADEMIC         to "शैक्षणिक सेटअप",
        // Teacher
        StringKeys.TEACHER_TITLE           to "शिक्षक",
        StringKeys.TEACHER_CLASSES         to "मेरी कक्षाएँ",
        StringKeys.TEACHER_SYLLABUS        to "पाठ्यक्रम",
        StringKeys.TEACHER_HOMEWORK        to "गृहकार्य",
        StringKeys.TEACHER_LESSON_PLAN     to "पाठ योजना",
        StringKeys.TEACHER_ATTENDANCE      to "उपस्थिति",
        StringKeys.TEACHER_GRADEBOOK       to "ग्रेडबुक",
        StringKeys.TEACHER_LEAVE           to "अवकाश",
        // Calendar
        StringKeys.CAL_TITLE               to "कैलेंडर",
        StringKeys.CAL_TODAY               to "आज",
        StringKeys.CAL_EVENTS              to "कार्यक्रम",
        StringKeys.CAL_HOLIDAYS            to "अवकाश",
        StringKeys.CAL_EXAMS               to "परीक्षाएँ",
        StringKeys.CAL_PTM                 to "अभिभावक-शिक्षक बैठक",
        // Messages
        StringKeys.MSG_TITLE               to "संदेश",
        StringKeys.MSG_SEND                to "भेजें",
        StringKeys.MSG_REPLY               to "उत्तर दें",
        StringKeys.MSG_EMPTY               to "कोई संदेश नहीं",
        StringKeys.MSG_TYPE_MESSAGE        to "संदेश लिखें…",
        StringKeys.MSG_BROADCAST           to "प्रसारण",
        // Onboarding
        StringKeys.OB_WELCOME              to "स्वागत है",
        StringKeys.OB_STEP                 to "चरण {current} / {total}",
        StringKeys.OB_BASIC_INFO           to "मूल जानकारी",
        StringKeys.OB_BRANDING             to "ब्रांडिंग",
        StringKeys.OB_ACADEMIC             to "शैक्षणिक सेटअप",
        StringKeys.OB_REVIEW               to "समीक्षा",
        StringKeys.OB_FINISH               to "सेटअप पूर्ण करें",
        // Splash
        StringKeys.SPLASH_TAGLINE          to "एक उज्ज्वल भविष्य के लिए खाई को पाटते हुए",
        // Auth scaffold
        StringKeys.AUTH_SECURED            to "एंड-टू-एंड एन्क्रिप्शन के साथ सुरक्षित",
        StringKeys.AUTH_BACK_LINK          to "‹ वापस",
        // Parent auth
        StringKeys.AUTH_PARENT_WELCOME     to "स्वागत है, अभिभावक 👋",
        StringKeys.AUTH_PARENT_SUBTITLE    to "अपने बच्चे के स्कूल से जुड़ने के लिए अपने मोबाइल नंबर से साइन इन करें।",
        StringKeys.AUTH_MOBILE_NUMBER      to "मोबाइल नंबर",
        StringKeys.AUTH_YOUR_NAME          to "आपका नाम",
        StringKeys.AUTH_FULL_NAME_PH       to "पूरा नाम",
        StringKeys.AUTH_OTP_CODE_PH        to "6-अंकों का कोड",
        StringKeys.AUTH_OTP_SENT_TO        to "हमने {phone} पर एक कोड भेजा है।",
        StringKeys.AUTH_YOUR_PHONE         to "आपके फ़ोन पर",
        StringKeys.AUTH_SEND_OTP           to "OTP भेजें",
        StringKeys.AUTH_VERIFY_CONTINUE    to "सत्यापित करें और जारी रखें",
        // Admin auth
        StringKeys.AUTH_ADMIN_TITLE        to "स्कूल प्रशासन",
        StringKeys.AUTH_ADMIN_SUBTITLE     to "अपनी संस्था प्रबंधित करने के लिए अपने स्टाफ क्रेडेंशियल से साइन इन करें।",
        StringKeys.AUTH_EMAIL_OR_STAFF_ID  to "ईमेल या स्टाफ आईडी",
        StringKeys.AUTH_FORGOT_PASSWORD    to "पासवर्ड भूल गए?",
        StringKeys.AUTH_WORK_EMAIL         to "कार्य ईमेल",
        StringKeys.AUTH_SCHOOL_NAME        to "स्कूल का नाम",
        StringKeys.AUTH_BOARD              to "बोर्ड",
        StringKeys.AUTH_CITY_OPTIONAL      to "शहर (वैकल्पिक)",
        StringKeys.AUTH_CREATE_PASSWORD    to "पासवर्ड बनाएं",
        StringKeys.AUTH_PASSWORD_8_PH      to "कम से कम 8 अक्षर",
        StringKeys.AUTH_NO_ACCOUNT         to "इस ईमेल के लिए कोई खाता मौजूद नहीं है।",
        StringKeys.AUTH_NEW_REGISTER       to "VidyaPrayag पर नए हैं? अपना स्कूल सेटअप करने और अपना व्यवस्थापक खाता बनाने के लिए नीचे अपना स्कूल पंजीकृत करें। शिक्षकों और अतिरिक्त स्टाफ को ऑनबोर्डिंग के बाद आपके स्कूल व्यवस्थापक द्वारा जोड़ा जाता है।",
        StringKeys.AUTH_REGISTER_CONTINUE  to "पंजीकृत करें और जारी रखें",
        StringKeys.AUTH_ONBOARD_SCHOOL     to "अपना स्कूल ऑनबोर्ड करें",
        StringKeys.AUTH_SIGN_IN            to "साइन इन करें",
        StringKeys.AUTH_SETTING_UP_SCHOOL  to "क्या आप एक स्कूल सेटअप कर रहे हैं?",
        StringKeys.AUTH_CREATE_ADMIN_ACCT  to "अपना व्यवस्थापक खाता बनाएं और अपने स्कूल को VidyaPrayag पर लाएं।",
        StringKeys.AUTH_REGISTER_MY_SCHOOL to "मेरा स्कूल पंजीकृत करें →",
        StringKeys.AUTH_SHOW_PASSWORD      to "पासवर्ड दिखाएं",
        StringKeys.AUTH_HIDE_PASSWORD      to "पासवर्ड छिपाएं",
        // Teacher first login
        StringKeys.AUTH_SET_NEW_PASSWORD   to "नया पासवर्ड सेट करें",
        StringKeys.AUTH_FIRST_LOGIN_DESC   to "आपकी सुरक्षा के लिए, जारी रखने से पहले एक नया पासवर्ड चुनें। आपको यह केवल एक बार करना होगा।",
        StringKeys.AUTH_CURRENT_TEMP_PW    to "वर्तमान अस्थायी पासवर्ड",
        StringKeys.AUTH_NEW_PASSWORD       to "नया पासवर्ड",
        StringKeys.AUTH_CONFIRM_PASSWORD   to "नए पासवर्ड की पुष्टि करें",
        StringKeys.AUTH_REENTER_PH         to "पुनः दर्ज करें",
        StringKeys.AUTH_UPDATE_CONTINUE    to "अपडेट करें और जारी रखें",
        StringKeys.AUTH_NEED_HELP          to "साइन इन में सहायता चाहिए?",
        StringKeys.AUTH_PW_TOO_SHORT       to "नए पासवर्ड में कम से कम 8 अक्षर होने चाहिए।",
        StringKeys.AUTH_PW_NO_MATCH        to "पासवर्ड मेल नहीं खाते।",
        StringKeys.AUTH_CONN_ERROR         to "कनेक्शन त्रुटि। कृपया पुनः प्रयास करें।",

        // Legal info screen
        StringKeys.LEGAL_TAB_PRIVACY       to "गोपनीयता",
        StringKeys.LEGAL_TAB_TERMS         to "शर्तें",
        StringKeys.LEGAL_TAB_HELP          to "सहायता केंद्र",
        StringKeys.LEGAL_TITLE             to "कानूनी और सहायता",
        StringKeys.LEGAL_FOOTER            to "विद्यासेतु · लिटिल ब्रिज",
        StringKeys.LEGAL_LAST_UPDATED      to "अंतिम अपडेट: जून 2026",
        StringKeys.LEGAL_BACK              to "वापस",
        StringKeys.LEGAL_PRIV_EYEBROW      to "आपका डेटा",
        StringKeys.LEGAL_PRIV_TITLE        to "गोपनीयता नीति",
        StringKeys.LEGAL_PRIV_INTRO        to "विद्यासेतु अभिभावकों और स्कूलों को जोड़ता है। हम केवल सेवा चलाने के लिए आवश्यक जानकारी एकत्र करते हैं: आपका नाम और संपर्क विवरण, आपसे जुड़ा स्कूल और बच्चे, और आपका स्कूल आपके साथ साझा करता है शैक्षणिक रिकॉर्ड (उपस्थिति, अंक, फीस, संदेश)।",
        StringKeys.LEGAL_PRIV_COLLECT_T    to "हम क्या एकत्र करते हैं",
        StringKeys.LEGAL_PRIV_COLLECT_1    to "खाता विवरण — साइन इन करने के लिए उपयोग किया गया नाम, फ़ोन या ईमेल।",
        StringKeys.LEGAL_PRIV_COLLECT_2    to "स्कूल जुड़ाव — आपके खाते से जुड़ी संस्था और छात्र।",
        StringKeys.LEGAL_PRIV_COLLECT_3    to "शैक्षणिक डेटा — आपके स्कूल द्वारा प्रकाशित उपस्थिति, मूल्यांकन, फीस और घोषणाएँ।",
        StringKeys.LEGAL_PRIV_COLLECT_4    to "संदेश — ऐप के माध्यम से आपके द्वारा भेजे या प्राप्त किए गए संचार।",
        StringKeys.LEGAL_PRIV_USE_T        to "हम इसका उपयोग कैसे करते हैं",
        StringKeys.LEGAL_PRIV_USE_1        to "आपको आपके बच्चे की शैक्षणिक जानकारी और स्कूल अपडेट दिखाने के लिए।",
        StringKeys.LEGAL_PRIV_USE_2        to "आपके द्वारा चुनी गई सूचनाएँ भेजने के लिए (परिणाम, फीस, घोषणाएँ)।",
        StringKeys.LEGAL_PRIV_USE_3        to "आपके खाते को सुरक्षित रखने और दुरुपयोग रोकने के लिए।",
        StringKeys.LEGAL_PRIV_NEVER_T      to "हम कभी नहीं करते",
        StringKeys.LEGAL_PRIV_NEVER_1      to "हम आपका डेटा बेचते नहीं हैं।",
        StringKeys.LEGAL_PRIV_NEVER_2      to "हम आपके डेटा का उपयोग तीसरे पक्ष के विज्ञापन के लिए नहीं करते।",
        StringKeys.LEGAL_PRIV_NEVER_3      to "हम आपके बच्चे के रिकॉर्ड आपके स्कूल के अधिकृत कर्मचारियों और आपके जुड़े अभिभावक खाते के बाहर साझा नहीं करते।",
        StringKeys.LEGAL_PRIV_SCOPED_T     to "आपका डेटा स्कूल-सीमित है",
        StringKeys.LEGAL_PRIV_SCOPED_B     to "हर रिकॉर्ड आपके स्कूल से जुड़ा है। पहुँच आपके साइन-इन सत्र से सर्वर-साइड तय होती है — आप केवल अपने खाते और स्कूल के डेटा को ही देखते हैं।",
        StringKeys.LEGAL_PRIV_RETENTION_T  to "प्रतिधारण और विलोपन",
        StringKeys.LEGAL_PRIV_RETENTION_B  to "हम आपका डेटा तब तक रखते हैं जब तक आपका खाता सक्रिय है। अपनी जानकारी तक पहुँच, सुधार या विलोपन का अनुरोध करने के लिए, सहायता केंद्र पर संपर्क करें — हम हर अनुरोध का उत्तर देते हैं।",
        StringKeys.LEGAL_TERMS_EYEBROW     to "समझौता",
        StringKeys.LEGAL_TERMS_TITLE       to "सेवा की शर्तें",
        StringKeys.LEGAL_TERMS_INTRO       to "विद्यासेतु का उपयोग करके आप इन शर्तों से सहमत होते हैं। ये स्पष्ट और उचित होने के लिए लिखी गई हैं। यदि आप सहमत नहीं हैं, तो कृपया ऐप का उपयोग न करें।",
        StringKeys.LEGAL_TERMS_USE_T       to "ऐप का उपयोग",
        StringKeys.LEGAL_TERMS_USE_1       to "खाता बनाते समय आपको सटीक जानकारी देनी होगी।",
        StringKeys.LEGAL_TERMS_USE_2       to "आप अपने लॉगिन क्रेडेंशियल गोपनीय रखने के लिए जिम्मेदार हैं।",
        StringKeys.LEGAL_TERMS_USE_3       to "ऐप का उपयोग केवल इसके उद्देश्य के लिए करें — अपने स्कूल से जुड़ना और बच्चे की प्रगति ट्रैक करना।",
        StringKeys.LEGAL_TERMS_ACCOUNTS_T  to "खाते और पहुँच",
        StringKeys.LEGAL_TERMS_ACCOUNTS_B  to "अभिभावक खाते भाग लेने वाले स्कूल में नामांकित बच्चों से जुड़ते हैं। शिक्षक और स्टाफ खाते आपके स्कूल के व्यवस्थापक द्वारा बनाए जाते हैं। स्कूल तय करता है कि कौन से रिकॉर्ड अभिभावकों को प्रकाशित किए जाते हैं।",
        StringKeys.LEGAL_TERMS_CONTENT_T   to "सामग्री और संचार",
        StringKeys.LEGAL_TERMS_CONTENT_1   to "संदेश और घोषणाएँ आधिकारिक स्कूल रिकॉर्ड का हिस्सा हैं।",
        StringKeys.LEGAL_TERMS_CONTENT_2   to "गैर-कानूनी, दुर्भावनापूर्ण या भ्रामक सामग्री पोस्ट न करें।",
        StringKeys.LEGAL_TERMS_CONTENT_3   to "हम इन शर्तों का उल्लंघन करने वाले या प्लेटफ़ॉर्म का दुरुपयोग करने वाले खाते निलंबित कर सकते हैं।",
        StringKeys.LEGAL_TERMS_AVAIL_T     to "उपलब्धता",
        StringKeys.LEGAL_TERMS_AVAIL_B     to "हम विद्यासेतु को चलाए रखने का प्रयास करते हैं, लेकिन सेवा \"जैसा है\" प्रदान की जाती है। हम अस्थायी डाउनटाइम के लिए जिम्मेदार नहीं हैं, और हम उत्पाद के विकास के साथ सुविधाओं को अपडेट कर सकते हैं।",
        StringKeys.LEGAL_TERMS_CHANGES_T   to "इन शर्तों में बदलाव",
        StringKeys.LEGAL_TERMS_CHANGES_B   to "हम ऐप के बढ़ने के साथ इन शर्तों को अपडेट कर सकते हैं। हम महत्वपूर्ण बदलाव ऐप में दिखाएंगे। अपडेट के बाद निरंतर उपयोग का अर्थ है कि आप संशोधित शर्तों को स्वीकार करते हैं।",
        StringKeys.LEGAL_TERMS_CONTACT_T   to "संपर्क",
        StringKeys.LEGAL_TERMS_CONTACT_B   to "इन शर्तों के बारे में प्रश्न? सहायता केंद्र टैब के माध्यम से हासिल करें।",
        StringKeys.LEGAL_HELP_EYEBROW      to "हम यहाँ हैं",
        StringKeys.LEGAL_HELP_TITLE        to "सहायता केंद्र",
        StringKeys.LEGAL_HELP_INTRO        to "मदद चाहिए, बग मिला, या खाते के बारे में कोई प्रश्न? हमारी टीम हर संदेश पढ़ती है और जितनी जल्दी हो सके उत्तर देती है।",
        StringKeys.LEGAL_HELP_EMAIL        to "ईमेल सहायता",
        StringKeys.LEGAL_HELP_INCLUDE_T    to "क्या शामिल करें",
        StringKeys.LEGAL_HELP_INCLUDE_1    to "आपकी भूमिका (अभिभावक, शिक्षक, या व्यवस्थापक) और आपके स्कूल का नाम।",
        StringKeys.LEGAL_HELP_INCLUDE_2    to "समस्या या प्रश्न का संक्षिप्त विवरण।",
        StringKeys.LEGAL_HELP_INCLUDE_3    to "एक स्क्रीनशॉट, यदि यह समस्या समझाने में मदद करता है।",
        StringKeys.LEGAL_HELP_FAQ_T        to "सामान्य प्रश्न",
        StringKeys.LEGAL_HELP_FAQ_Q1       to "मैं अपने बच्चे को नहीं जोड़ सकता",
        StringKeys.LEGAL_HELP_FAQ_A1       to "अपने स्कूल के ऑफिस से छात्र कोड जाँचें, फिर अपनी प्रोफ़ाइल से बच्चा जोड़ें प्रवाह पुनः प्रयास करें।",
        StringKeys.LEGAL_HELP_FAQ_Q2       to "मैं अपना पासवर्ड भूल गया",
        StringKeys.LEGAL_HELP_FAQ_A2       to "शिक्षक और व्यवस्थापक अपने स्कूल व्यवस्थापक से रीसेट करवा सकते हैं। अभिभावक एक-बार कोड से साइन इन करते हैं।",
        StringKeys.LEGAL_HELP_FAQ_Q3       to "मुझे सूचनाएँ नहीं मिल रहीं",
        StringKeys.LEGAL_HELP_FAQ_A3       to "सुनिश्चित करें कि आपकी डिवाइस सेटिंग्स में विद्यासेतु के लिए सूचनाएँ सक्षम हैं।",

        // Parent link child screen
        StringKeys.LINK_STEP_OF            to "चरण {step} / {total}",
        StringKeys.LINK_STEP1_TITLE        to "अपने बारे में बताएं",
        StringKeys.LINK_STEP1_SUB          to "ताकि आपके बच्चे का स्कूल जान सके कि अपडेट किसे भेजना है।",
        StringKeys.LINK_FULL_NAME          to "आपका पूरा नाम",
        StringKeys.LINK_FULL_NAME_PH       to "उदा. स्नेहा शर्मा",
        StringKeys.LINK_PREF_LANG          to "पसंदीदा भाषा",
        StringKeys.LINK_STEP2_TITLE        to "अपने बच्चे का स्कूल खोजें",
        StringKeys.LINK_STEP2_SUB          to "स्कूल का नाम टाइप करें। हम विद्यासेतु का उपयोग करने वाले स्कूलों से मिलान करेंगे।",
        StringKeys.LINK_SEARCH_PH          to "स्कूल के नाम से खोजें",
        StringKeys.LINK_SEARCHING          to "खोज रहे हैं…",
        StringKeys.LINK_SEARCH             to "खोजें",
        StringKeys.LINK_SEARCH_ERR         to "कुछ गलत हुआ",
        StringKeys.LINK_SEARCH_PROMPT      to "अपने बच्चे का स्कूल खोजने के लिए खोजें।",
        StringKeys.LINK_TAP_SELECT         to "अपने बच्चे के स्कूल को चुनने के लिए टैप करें।",
        StringKeys.LINK_MATCH              to "मिलान",
        StringKeys.LINK_STEP3_TITLE        to "अपने बच्चे को जोड़ें",
        StringKeys.LINK_STEP3_SUB          to "{school} में अपने बच्चे के बारे में बताएं ताकि हम उन्हें सटीक रूप से मिला सकें।",
        StringKeys.LINK_CHILD_NAME         to "बच्चे का पूरा नाम",
        StringKeys.LINK_CHILD_NAME_PH      to "उदा. आरव शर्मा",
        StringKeys.LINK_CLASS              to "कक्षा",
        StringKeys.LINK_CLASS_PH           to "उदा. 4",
        StringKeys.LINK_SECTION            to "अनुभाग",
        StringKeys.LINK_SECTION_PH         to "उदा. A",
        StringKeys.LINK_ROLL               to "रोल / प्रवेश संख्या",
        StringKeys.LINK_ROLL_PH            to "उदा. 02",
        StringKeys.LINK_PHONE_OPT          to "आपका फ़ोन नंबर (वैकल्पिक)",
        StringKeys.LINK_PHONE_PH           to "उदा. 98765 43210",
        StringKeys.LINK_ERR                to "आपका बच्चा जोड़ा नहीं जा सका",
        StringKeys.LINK_REVIEW_MSG         to "हमें आपका बच्चा मिला लेकिन फ़ोन नंबर मेल नहीं खाता — {school} समीक्षा करेगा और पुष्टि करेगा।",
        StringKeys.LINK_PENDING_MSG        to "अनुरोध भेजा गया — {school} अनुमोदन की प्रतीक्षा में",
        StringKeys.LINK_CLASS_ROLL         to "कक्षा {class} • रोल {roll}",
        StringKeys.LINK_MATCH_PROMPT       to "जब आप समाप्त करें पर टैप करेंगे तब हम इसे {school} के रिकॉर्ड से मिलाएंगे।",
        StringKeys.LINK_CONTINUE           to "जारी रखें",
        StringKeys.LINK_LINKING            to "जोड़ रहे हैं…",
        StringKeys.LINK_DONE               to "पूर्ण",
        StringKeys.LINK_FINISH             to "समाप्त करें और डैशबोर्ड खोलें",
        StringKeys.LINK_THE_SCHOOL         to "स्कूल",
        StringKeys.LINK_YOUR_SCHOOL        to "आपका स्कूल",

        // CommonLandingScreenV2
        StringKeys.LANDING_BRAND            to "EnRoll+",
        StringKeys.LANDING_SCHOOL_EYEBROW   to "स्कूल प्रबंधन",
        StringKeys.LANDING_PARENT_EYEBROW   to "अभिभावक पोर्टल",
        StringKeys.LANDING_SCHOOL_HEADLINE  to "अपना पूरा स्कूल\nएक स्क्रीन से चलाएं।",
        StringKeys.LANDING_PARENT_HEADLINE  to "आपके बच्चे का स्कूल दिन,\nआपकी जेब में।",
        StringKeys.LANDING_SCHOOL_SUB       to "उपस्थिति, प्रवेश, परिणाम, फीस और अभिभावक संदेश — एक प्लेटफ़ॉर्म जो आपका स्टाफ वास्तव में उपयोग करना चाहता है।",
        StringKeys.LANDING_PARENT_SUB       to "स्कूल से उपस्थिति, अंक, फीस और संदेश — स्पष्ट, तुरंत, और हमेशा अपडेट।",
        StringKeys.LANDING_TAB_SCHOOLS      to "स्कूलों के लिए",
        StringKeys.LANDING_TAB_PARENTS      to "अभिभावकों के लिए",
        StringKeys.LANDING_IMG_LABEL_SCHOOL to "एक वास्तविक स्कूल, विद्यासेतु पर चलता है",
        StringKeys.LANDING_IMG_LABEL_PARENT to "अपने बच्चे की प्रगति के करीब रहें",
        StringKeys.LANDING_CTA_SCHOOLS      to "शुरू करें — स्कूल",
        StringKeys.LANDING_CTA_PARENTS      to "शुरू करें — अभिभावक",
        StringKeys.LANDING_OUTLINED_PARENTS to "अभिभावकों के लिए",
        StringKeys.LANDING_OUTLINED_SCHOOLS to "स्कूलों के लिए",
        StringKeys.LANDING_FOOTER_PREFIX    to "जारी रखकर आप हमारी ",
        StringKeys.LANDING_FOOTER_TERMS     to "शर्तों",
        StringKeys.LANDING_FOOTER_AND       to " और ",
        StringKeys.LANDING_FOOTER_PRIVACY   to "गोपनीयता",
        StringKeys.LANDING_SCHOOL_F1_T      to "सेकंडों में दैनिक उपस्थिति",
        StringKeys.LANDING_SCHOOL_F1_D      to "पूरी कक्षा एक बार में चिह्नित करें — अनुपस्थिति पर सही अभिभावक को तुरंत सूचित किया जाता है।",
        StringKeys.LANDING_SCHOOL_F2_T      to "प्रवेश, शुरू से अंत तक",
        StringKeys.LANDING_SCHOOL_F2_D      to "पहले कॉल से नामांकन तक हर पूछताछ को ट्रैक करें, फॉलो-अप और रूपांतरण अंतर्निहित।",
        StringKeys.LANDING_SCHOOL_F3_T      to "मुसीबत आने से पहले देखें",
        StringKeys.LANDING_SCHOOL_F3_D      to "कक्षा और संकाय विश्लेषण उन छात्रों और शिक्षकों को जल्दी उजागर करते हैं जिन्हें ध्यान चाहिए।",
        StringKeys.LANDING_SCHOOL_F4_T      to "परिणाम प्रकाशित करें, साफ-सुथरे",
        StringKeys.LANDING_SCHOOL_F4_D      to "अंक दर्ज करें, कक्षा वितरण समीक्षा करें, और एक प्रवाह में रिपोर्ट-तैयार परिणाम जारी करें।",
        StringKeys.LANDING_SCHOOL_F5_T      to "हर अभिभावक तक एक आवाज़",
        StringKeys.LANDING_SCHOOL_F5_D      to "घोषणाएँ, संदेश और PTM शेड्यूलिंग — हर बातचीत एक जगह, हमेशा।",
        StringKeys.LANDING_SCHOOL_F6_T      to "जवाबदेही, कागज़ी काम नहीं",
        StringKeys.LANDING_SCHOOL_F6_D      to "पाठ्यक्रम कवरेज, अवकाश अनुमोदन और शिक्षक अनुपालन, बिना स्प्रेडशीट के ट्रैक किया गया।",
        StringKeys.LANDING_PARENT_F1_T      to "हर दिन, हिसाब में",
        StringKeys.LANDING_PARENT_F1_D      to "उपस्थित, देर से और अनुपस्थित दिनों का स्पष्ट मासिक कैलेंडर — कोई अनुमान नहीं, कोई दौड़ नहीं।",
        StringKeys.LANDING_PARENT_F2_T      to "अंक जैसे ही आते हैं",
        StringKeys.LANDING_PARENT_F2_D      to "आपके बच्चे के लिए वास्तविक प्रकाशित परिणाम और पाठ्यक्रम प्रगति, उसी दिन जब स्कूल जारी करता है।",
        StringKeys.LANDING_PARENT_F3_T      to "फीस बिना झंझट",
        StringKeys.LANDING_PARENT_F3_D      to "देखें कि क्या बकाया है और क्या भुगतान हो चुका है, स्कूल की फीस सूचनाएँ उसी जगह।",
        StringKeys.LANDING_PARENT_F4_T      to "सही शिक्षक से बात करें",
        StringKeys.LANDING_PARENT_F4_D      to "अपने बच्चे के कक्षा शिक्षक या स्कूल ऑफिस को सीधे संदेश भेजें — जवाब एक थ्रेड में आते हैं।",
        StringKeys.LANDING_PARENT_F5_T      to "महत्वपूर्ण चीज़ कभी न चूकें",
        StringKeys.LANDING_PARENT_F5_D      to "स्कूल घोषणाएँ और गतिविधि, आपके परिवार के लिए प्रासंगिक चीज़ों पर फ़िल्टर की गई।",
        StringKeys.LANDING_PARENT_F6_T      to "एक टैप में अवकाश के लिए आवेदन",
        StringKeys.LANDING_PARENT_F6_D      to "एक दिन की छुट्टी का अनुरोध करें और यह सीधे कक्षा शिक्षक को जाता है, स्थिति के साथ जिसे आप ट्रैक कर सकते हैं।",

        // CommonLandingScreenV3 — Hero
        StringKeys.LV3_BRAND               to "EnRoll+",
        StringKeys.LV3_SCHOOL_TAGLINE      to "बुद्धिमत्ता परत\nजो आपके पूरे स्कूल पारिस्थितिकी तंत्र को जोड़ती है।",
        StringKeys.LV3_PARENT_TAGLINE      to "आपके बच्चे का स्कूल दिन,\nआपकी जेब में — स्पष्ट और तुरंत।",
        StringKeys.LV3_SCHOOL_CONTEXT      to "प्रधानाध्यापक, व्यवस्थापक और शिक्षकों के लिए",
        StringKeys.LV3_PARENT_CONTEXT      to "जो अभिभावक करीब रहना चाहते हैं उनके लिए",
        StringKeys.LV3_PILL_SCHOOLS        to "स्कूल",
        StringKeys.LV3_PILL_PARENTS        to "अभिभावक",
        // V3 — Morphing words
        StringKeys.LV3_SCHOOL_MORPH_1      to "प्रबंधित करें।",
        StringKeys.LV3_SCHOOL_MORPH_2      to "स्वचालित करें।",
        StringKeys.LV3_SCHOOL_MORPH_3      to "विकसित करें।",
        StringKeys.LV3_SCHOOL_MORPH_4      to "रूपांतरित करें।",
        StringKeys.LV3_PARENT_MORPH_1      to "ट्रैक करें।",
        StringKeys.LV3_PARENT_MORPH_2      to "जुड़ें।",
        StringKeys.LV3_PARENT_MORPH_3      to "सहयोग करें।",
        StringKeys.LV3_PARENT_MORPH_4      to "उत्सव मनाएं।",
        // V3 — Command center
        StringKeys.LV3_CMD_SCHOOL_EYEBROW  to "लाइव स्कूल कमांड सेंटर",
        StringKeys.LV3_CMD_PARENT_EYEBROW  to "आपके बच्चे का दिन, लाइव",
        StringKeys.LV3_CMD_SCHOOL_TITLE    to "आज का अवलोकन",
        StringKeys.LV3_CMD_PARENT_TITLE    to "आज का स्नैपशॉट",
        StringKeys.LV3_LIVE                to "लाइव",
        StringKeys.LV3_CMD_STUDENTS        to "छात्र",
        StringKeys.LV3_CMD_TEACHERS        to "शिक्षक",
        StringKeys.LV3_CMD_ATTENDANCE      to "उपस्थिति",
        StringKeys.LV3_CMD_FEE             to "फीस संग्रह",
        StringKeys.LV3_CMD_ADMISSIONS      to "प्रवेश",
        StringKeys.LV3_CMD_ADMISSIONS_TREND to "इस माह",
        StringKeys.LV3_CMD_SATISFACTION    to "संतुष्टि",
        StringKeys.LV3_CMD_SATISFACTION_TREND to "अभिभावक रेटिंग",
        StringKeys.LV3_CMD_P_ATTENDANCE    to "उपस्थिति",
        StringKeys.LV3_CMD_P_ATTENDANCE_V  to "उपस्थित",
        StringKeys.LV3_CMD_P_LAST_TEST     to "अंतिम परीक्षा",
        StringKeys.LV3_CMD_P_FEES          to "फीस भुगतान",
        StringKeys.LV3_CMD_P_FEES_V        to "चालू",
        StringKeys.LV3_CMD_P_MESSAGES      to "संदेश",
        StringKeys.LV3_CMD_HOMEWORK        to "गृहकार्य",
        StringKeys.LV3_CMD_HOMEWORK_TREND  to "आज लंबित",
        StringKeys.LV3_CMD_PTM             to "अगली PTM",
        StringKeys.LV3_CMD_PTM_TREND       to "3:00 PM स्लॉट",
        // V3 — Ecosystem
        StringKeys.LV3_ECO_SCHOOL_EYEBROW  to "एक प्लेटफ़ॉर्म। चार पारिस्थितिकी।",
        StringKeys.LV3_ECO_PARENT_EYEBROW  to "जो कुछ चाहिए वह सब। एक ऐप में।",
        StringKeys.LV3_ECO_S1_T            to "स्कूल बुद्धिमत्ता",
        StringKeys.LV3_ECO_S1_S            to "अपने स्कूल को तुरंत समझें",
        StringKeys.LV3_ECO_S1_M1           to "उपस्थिति रुझान",
        StringKeys.LV3_ECO_S1_M2           to "प्रदर्शन विश्लेषण",
        StringKeys.LV3_ECO_S1_M3           to "शिक्षक गतिविधि",
        StringKeys.LV3_ECO_S2_T            to "शिक्षक सशक्तिकरण",
        StringKeys.LV3_ECO_S2_S            to "कम कागज़ी काम। अधिक शिक्षण।",
        StringKeys.LV3_ECO_S2_M1           to "पाठ योजना",
        StringKeys.LV3_ECO_S2_M2           to "पाठ्यक्रम प्रगति",
        StringKeys.LV3_ECO_S2_M3           to "कक्षा अंतर्दृष्टि",
        StringKeys.LV3_ECO_S3_T            to "अभिभावक संबंध",
        StringKeys.LV3_ECO_S3_S            to "हर अभिभावक जुड़ा रहता है।",
        StringKeys.LV3_ECO_S3_M1           to "बच्चे की यात्रा",
        StringKeys.LV3_ECO_S3_M2           to "प्रत्यक्ष संदेश",
        StringKeys.LV3_ECO_S3_M3           to "रियल-टाइम प्रगति",
        StringKeys.LV3_ECO_S4_T            to "विकास इंजन",
        StringKeys.LV3_ECO_S4_S            to "प्रवेश से स्नातक तक।",
        StringKeys.LV3_ECO_S4_M1           to "पूछताछ ट्रैकिंग",
        StringKeys.LV3_ECO_S4_M2           to "रूपांतरण फ़नल",
        StringKeys.LV3_ECO_S4_M3           to "प्रतिधारण मेट्रिक्स",
        StringKeys.LV3_ECO_P1_T            to "उपस्थिति कैलेंडर",
        StringKeys.LV3_ECO_P1_S            to "हर दिन, हिसाब में।",
        StringKeys.LV3_ECO_P1_M1           to "उपस्थित दिन",
        StringKeys.LV3_ECO_P1_M2           to "देर से आगमन",
        StringKeys.LV3_ECO_P1_M3           to "अनुपस्थित पैटर्न",
        StringKeys.LV3_ECO_P2_T            to "शैक्षणिक प्रगति",
        StringKeys.LV3_ECO_P2_S            to "अंक जैसे ही आते हैं।",
        StringKeys.LV3_ECO_P2_M1           to "लाइव परिणाम",
        StringKeys.LV3_ECO_P2_M2           to "पाठ्यक्रम कवरेज",
        StringKeys.LV3_ECO_P2_M3           to "रिपोर्ट कार्ड",
        StringKeys.LV3_ECO_P3_T            to "फीस प्रबंधन",
        StringKeys.LV3_ECO_P3_S            to "फीस बिना झंझट।",
        StringKeys.LV3_ECO_P3_M1           to "नियत तिथियाँ",
        StringKeys.LV3_ECO_P3_M2           to "भुगतान इतिहास",
        StringKeys.LV3_ECO_P3_M3           to "फीस सूचनाएँ",
        StringKeys.LV3_ECO_P4_T            to "स्कूल संचार",
        StringKeys.LV3_ECO_P4_S            to "सही शिक्षक से बात करें।",
        StringKeys.LV3_ECO_P4_M1           to "प्रत्यक्ष संदेश",
        StringKeys.LV3_ECO_P4_M2           to "घोषणाएँ",
        StringKeys.LV3_ECO_P4_M3           to "PTM शेड्यूलिंग",
        // V3 — AI insight
        StringKeys.LV3_AI_TITLE            to "EnRoll बुद्धिमत्ता",
        StringKeys.LV3_AI_LABEL            to "AI विश्लेषण",
        StringKeys.LV3_AI_S1               to "तीन छात्रों को शैक्षणिक ध्यान चाहिए।",
        StringKeys.LV3_AI_S2               to "इस माह फीस संग्रह 12% बेहतर हुआ।",
        StringKeys.LV3_AI_S3               to "ग्रेड 8 में शिक्षक कार्यभार असंतुलन पाया गया।",
        StringKeys.LV3_AI_P1               to "इस माह आपके बच्चे की उपस्थिति कक्षा औसत से अधिक है।",
        StringKeys.LV3_AI_P2               to "पिछली परीक्षा से गणित अंक 8% बेहतर हुए।",
        StringKeys.LV3_AI_P3               to "शुक्रवार को PTM निर्धारित — कृपया अपना स्लॉट पुष्टि करें।",
        // V3 — Timeline
        StringKeys.LV3_TL_SCHOOL_EYEBROW   to "EnRoll+ के साथ एक दिन",
        StringKeys.LV3_TL_PARENT_EYEBROW   to "आपके बच्चे का दिन, समयरेखा",
        StringKeys.LV3_TL_S1_T             to "स्कूल खुलता है",
        StringKeys.LV3_TL_S1_D             to "द्वार खुले, सिस्टम सक्रिय",
        StringKeys.LV3_TL_S2_T             to "उपस्थिति सिंक्रनाइज़्ड",
        StringKeys.LV3_TL_S2_D             to "1,240 छात्र 5 मिनट में चिह्नित",
        StringKeys.LV3_TL_S3_T             to "मूल्यांकन पूर्ण",
        StringKeys.LV3_TL_S3_D             to "परिणाम तुरंत अभिभावकों को प्रकाशित",
        StringKeys.LV3_TL_S4_T             to "अभिभावक अपडेट भेजा गया",
        StringKeys.LV3_TL_S4_D             to "घोषणाएँ 1,200+ परिवारों को भेजी गईं",
        StringKeys.LV3_TL_S5_T             to "विश्लेषण तैयार",
        StringKeys.LV3_TL_S5_D             to "AI अंतर्दृष्टि समीक्षा के लिए तैयार",
        StringKeys.LV3_TL_P1_T             to "बस ट्रैकिंग",
        StringKeys.LV3_TL_P1_D             to "लाइव स्थान स्कूल के साथ साझा",
        StringKeys.LV3_TL_P2_T             to "उपस्थिति चिह्नित",
        StringKeys.LV3_TL_P2_D             to "आपके बच्चे ने चेक-इन किया — सूचना प्राप्त",
        StringKeys.LV3_TL_P3_T             to "लंच ब्रेक",
        StringKeys.LV3_TL_P3_D             to "कैफे गतिविधि लॉग की गई",
        StringKeys.LV3_TL_P4_T             to "स्कूल समाप्त",
        StringKeys.LV3_TL_P4_D             to "पिकअप पुष्टि, दिन सारांश भेजा गया",
        StringKeys.LV3_TL_P5_T             to "गृहकार्य पोस्ट किया गया",
        StringKeys.LV3_TL_P5_D             to "असाइनमेंट और पाठ्यक्रम अपडेट उपलब्ध",
        // V3 — Trust metrics
        StringKeys.LV3_TRUST_SCHOOL_EYEBROW to "महत्वपूर्ण आँकड़े",
        StringKeys.LV3_TRUST_PARENT_EYEBROW to "मन की शांति, गारंटीड",
        StringKeys.LV3_TRUST_S1_V           to "24,000+",
        StringKeys.LV3_TRUST_S1_L           to "दैनिक छात्र संवाद",
        StringKeys.LV3_TRUST_S2_L           to "अभिभावक संबंध",
        StringKeys.LV3_TRUST_S3_L           to "वर्कफ़्लो विश्वसनीयता",
        StringKeys.LV3_TRUST_P1_V           to "तुरंत",
        StringKeys.LV3_TRUST_P1_L           to "उपस्थिति सूचनाएँ",
        StringKeys.LV3_TRUST_P2_V           to "रियल-टाइम",
        StringKeys.LV3_TRUST_P2_L           to "परिणाम और अंक अपडेट",
        StringKeys.LV3_TRUST_P3_V           to "24/7",
        StringKeys.LV3_TRUST_P3_L           to "स्कूल संचार तक पहुँच",
        // V3 — Testimonials
        StringKeys.LV3_TEST_S_QUOTE        to "आख़िरकार एक सिस्टम जो शिक्षक वास्तव में पसंद करते हैं।",
        StringKeys.LV3_TEST_S_ROLE         to "प्रधानाध्यापक",
        StringKeys.LV3_TEST_S_ORG          to "मॉडर्न स्कूल",
        StringKeys.LV3_TEST_P_QUOTE        to "मैं हर दिन बिल्कुल जानता हूँ कि मेरा बच्चा कैसा कर रहा है।",
        StringKeys.LV3_TEST_P_ROLE         to "अभिभावक",
        StringKeys.LV3_TEST_P_ORG          to "दिल्ली पब्लिक स्कूल",
        // V3 — CTA dock
        StringKeys.LV3_CTA_PROMPT          to "स्मार्ट शिक्षा का अनुभव करने के लिए तैयार?",
        StringKeys.LV3_CTA_ENTER           to "EnRoll+ में प्रवेश करें",
        StringKeys.LV3_CTA_PARENT          to "मैं अभिभावक हूँ",
        StringKeys.LV3_CTA_SCHOOL          to "मैं स्कूल हूँ",
        StringKeys.LV3_FOOTER_PREFIX       to "जारी रखकर आप हमारी ",
        StringKeys.LV3_FOOTER_TERMS        to "शर्तों",
        StringKeys.LV3_FOOTER_AND          to " और ",
        StringKeys.LV3_FOOTER_PRIVACY      to "गोपनीयता",

        // SchoolOnboardingScreenV2 — Header
        StringKeys.OB_ONBOARDING           to "ऑनबोर्डिंग",
        StringKeys.OB_STEP_OF              to "चरण {step} / {total}",
        StringKeys.OB_BACK                 to "पीछे",
        StringKeys.OB_CONTINUE             to "जारी रखें",
        StringKeys.OB_FINISH               to "सेटअप पूरा करें",
        StringKeys.OB_SETTING_UP           to "सेटअप हो रहा है",
        // Step titles
        StringKeys.OB_T_IDENTITY           to "स्कूल पहचान",
        StringKeys.OB_T_ACADEMIC           to "शैक्षणिक वर्ष",
        StringKeys.OB_T_CLASSES            to "कक्षाएँ और अनुभाग",
        StringKeys.OB_T_SUBJECTS           to "विषय",
        StringKeys.OB_T_TEACHERS           to "शिक्षक",
        StringKeys.OB_T_STUDENTS           to "छात्र",
        // Step 1: Identity
        StringKeys.OB_ID_LEGAL_NAME        to "पूरा कानूनी नाम",
        StringKeys.OB_ID_LEGAL_PH          to "सरस्वती विद्या मंदिर",
        StringKeys.OB_ID_SHORT_NAME        to "संक्षिप्त नाम",
        StringKeys.OB_ID_SHORT_PH          to "SVM",
        StringKeys.OB_ID_AFFIL             to "संबद्धता संख्या",
        StringKeys.OB_ID_AFFIL_PH          to "UP/CBSE/2021/4421",
        StringKeys.OB_ID_BOARD             to "बोर्ड",
        StringKeys.OB_ID_SCHOOL_TYPE       to "स्कूल प्रकार",
        StringKeys.OB_ID_PRINCIPAL         to "प्रधानाध्यापक का नाम",
        StringKeys.OB_ID_PRINCIPAL_PH      to "डॉ. अनिता वर्मा",
        StringKeys.OB_ID_PRINCIPAL_MOB     to "प्रधानाध्यापक का मोबाइल",
        StringKeys.OB_ID_PRINCIPAL_MOB_PH  to "+91 98XXX XXXXX",
        // Step 2: Academic year
        StringKeys.OB_AY_CURRENT           to "वर्तमान शैक्षणिक वर्ष",
        StringKeys.OB_AY_STARTS            to "वर्ष प्रारंभ",
        StringKeys.OB_AY_ENDS              to "वर्ष समाप्ति",
        StringKeys.OB_AY_WORKING_DAYS      to "कार्य दिवस",
        StringKeys.OB_AY_START_TIME        to "प्रारंभ समय",
        StringKeys.OB_AY_END_TIME          to "समाप्ति समय",
        StringKeys.OB_AY_PERIODS           to "प्रतिदिन अवधियाँ",
        StringKeys.OB_AY_PERIODS_PH        to "8",
        // Step 3: Classes
        StringKeys.OB_CL_TIP               to "सुझाव",
        StringKeys.OB_CL_TIP_BODY          to "अपने स्कूल के वास्तविक अनुभाग चुनें। अगले चरणों में विषय और शिक्षक केवल इन कक्षाओं में दिखेंगे।",
        StringKeys.OB_CL_SECTIONS          to "{count} अनुभाग",
        StringKeys.OB_CL_ADD_MANUAL        to "कक्षा मैन्युअल जोड़ें",
        StringKeys.OB_CL_ADD_PH            to "जैसे कक्षा 11, नर्सरी, KG",
        StringKeys.OB_CL_ADD_BTN           to "जोड़ें",
        // Step 4: Subjects
        StringKeys.OB_SJ_OFFERED           to "विषय उपलब्ध",
        StringKeys.OB_SJ_TAP_HINT          to "विषय के कक्षा चिप्स पर टैप करके चुनें कि कहाँ पढ़ाया जाता है।",
        StringKeys.OB_SJ_APPLY_ALL         to "सभी पर लागू करें",
        StringKeys.OB_SJ_NO_CLASSES        to "कोई कक्षा नहीं",
        // Step 5: Teachers
        StringKeys.OB_TC_ADD               to "शिक्षक जोड़ें",
        StringKeys.OB_TC_ADD_DESC          to "शिक्षक का लॉगिन खाता बनाने के लिए कार्य ईमेल दर्ज करें — उन्हें साइन इन के लिए एक बार का पासवर्ड मिलेगा। केवल नाम? आप डैशबोर्ड से बाद में लॉगिन जोड़ सकते हैं।",
        StringKeys.OB_TC_FULL_NAME         to "पूरा नाम",
        StringKeys.OB_TC_FULL_NAME_PH      to "श्रीमती कविता नायर",
        StringKeys.OB_TC_WORK_EMAIL        to "कार्य ईमेल (वैकल्पिक)",
        StringKeys.OB_TC_WORK_EMAIL_PH     to "kavita@svm.edu.in",
        StringKeys.OB_TC_NONE_YET          to "अभी तक कोई शिक्षक नहीं जोड़ा गया",
        StringKeys.OB_TC_NONE_DESC         to "विषय असाइन करने के लिए ऊपर शिक्षक जोड़ें, या जारी रखें और बाद में करें।",
        StringKeys.OB_TC_COVERAGE          to "शिक्षक कवरेज",
        StringKeys.OB_TC_COVERAGE_OF       to "{total} में से {covered} विषय × कक्षा स्लॉट असाइन किए गए",
        StringKeys.OB_TC_UNASSIGNED        to "{count} अनअसाइन्ड — नीचे असाइनमेंट जारी रखें।",
        StringKeys.OB_TC_SLOTS             to "{count} स्लॉट",
        StringKeys.OB_TC_IMPORT_CSV        to "CSV से रोस्टर आयात करें",
        // Step 6: Students
        StringKeys.OB_ST_DROP_CSV          to "अपनी छात्र CSV यहाँ डालें",
        StringKeys.OB_ST_OR_BROWSE         to "या ब्राउज़ करने के लिए टैप करें",
        StringKeys.OB_ST_DOWNLOAD          to "टेम्पलेट डाउनलोड करें",
        StringKeys.OB_ST_NONE_YET          to "अभी तक कोई रोस्टर आयात नहीं",
        StringKeys.OB_ST_OPTIONAL          to "वैकल्पिक",
        StringKeys.OB_ST_OPTIONAL_DESC     to "छात्र अभी आयात करना वैकल्पिक है — आप सेटअप पूरा कर सकते हैं और डैशबोर्ड से कभी भी छात्र जोड़ सकते हैं। CSV अपलोड होने पर सत्यापन परिणाम यहाँ दिखेंगे।",
        // Completion
        StringKeys.OB_CM_ALL_SET           to "आप सब तैयार हैं",
        StringKeys.OB_CM_IS_LIVE           to "{school} VidyaPrayag पर लाइव है।",
        StringKeys.OB_CM_TEACHER_LOGINS    to "शिक्षक लॉगिन बनाए गए",
        StringKeys.OB_CM_SHARE_OTP         to "ये एक-बार के पासवर्ड अपने शिक्षकों को दें। पहली साइन इन पर उन्हें अपना पासवर्ड सेट करने के लिए कहा जाएगा। ये दोबारा नहीं दिखेंगे — डैशबोर्ड से कभी भी रीसेट करें।",
        StringKeys.OB_CM_PASSWORD          to "पासवर्ड: ",
        StringKeys.OB_CM_COULDNT_CREATE    to "कुछ लॉगिन नहीं बना सके",
        StringKeys.OB_CM_ADD_LATER         to "{name} ({id}) — {msg}। उन्हें बाद में डैशबोर्ड से जोड़ें।",
        StringKeys.OB_CM_OPEN_DASH         to "डैशबोर्ड खोलें",
        StringKeys.OB_CM_EDIT_LATER        to "आप इसे सेटिंग्स में बाद में संपादित कर सकते हैं।",
        StringKeys.OB_CM_READY             to "आपका स्कूल तैयार है",
        StringKeys.OB_CM_PROFILE_DONE      to "आपका प्रोफ़ाइल सेटअप पूरा हो गया है। अब आप शिक्षक, छात्र और अभिभावक जोड़कर अपना डिजिटल कैंपस बना सकते हैं।",
        StringKeys.OB_CM_YOUR_SCHOOL       to "आपका स्कूल",

        // Phase 2 — Parent Screens (Hindi)
        // ParentAcademicsScreenV2
        StringKeys.PA_APPLY_LEAVE          to "छुट्टी के लिए आवेदन करें",
        StringKeys.PA_LEAVE_DESC           to "अपने बच्चे के लिए छुट्टी का अनुरोध करें — उनके क्लास टीचर को भेजा जाएगा",
        StringKeys.PA_THIS_TERM            to "इस टर्म में",
        StringKeys.PA_ATTENDANCE_RATE      to "उपस्थिति दर",
        StringKeys.PA_NO_ATTENDANCE        to "अभी तक कोई उपस्थिति अंकित नहीं",
        StringKeys.PA_NO_ATTENDANCE_DESC   to "स्कूल द्वारा उपस्थिति अंकित होने पर दिन नीचे भरेंगे।",
        StringKeys.PA_AI_EST               to "AI अनुमान",
        StringKeys.PA_EST                  to "अनुमान",
        StringKeys.PA_PENDING              to "लंबित",
        StringKeys.PA_AI_SUMMARY           to "AI सारांश",
        StringKeys.PA_AI_ESTIMATED         to "AI अनुमानित",
        StringKeys.PA_TYPE_ANSWER          to "अपना उत्तर लिखें...",
        StringKeys.PA_SCORE                to "स्कोर: {score} / {total}",
        StringKeys.PA_YOUR_ANSWER          to "आपका उत्तर: {answer}",
        StringKeys.PA_CORRECT_ANSWER       to "सही उत्तर: {answer}",
        StringKeys.PA_LOADING_LEADERBOARD  to "लीडरबोर्ड लोड हो रहा है...",
        StringKeys.PA_LEADERBOARD          to "लीडरबोर्ड",
        StringKeys.PA_PARTICIPANTS         to "{count} प्रतिभागी",
        StringKeys.PA_BACK_TO_QUIZZES      to "क्विज़ पर वापस जाएं",
        StringKeys.PA_HEALTH_RECORDS       to "स्वास्थ्य रिकॉर्ड",
        StringKeys.PA_HEALTH_RECORDS_DESC  to "स्वास्थ्य प्रोफ़ाइल, टीकाकरण और घटनाएं देखें",
        StringKeys.PA_AI_REPORT_CARD       to "AI रिपोर्ट कार्ड",
        StringKeys.PA_AI_REPORT_CARD_DESC  to "अपने बच्चे का AI-जनित रिपोर्ट कार्ड देखने के लिए उन्हें लिंक करें।",
        StringKeys.PA_NO_MARKS             to "अभी तक कोई अंक प्रकाशित नहीं",
        StringKeys.PA_NO_MARKS_DESC        to "शिक्षकों द्वारा अभिभावकों को परिणाम प्रकाशित करने पर अंक यहाँ दिखेंगे।",
        StringKeys.PA_NO_SYLLABUS          to "अभी तक कोई सिलेबस साझा नहीं",
        StringKeys.PA_NO_SYLLABUS_DESC     to "स्कूल द्वारा साझा करने पर विषय-वार कवरेज लॉग यहाँ दिखेगा।",
        StringKeys.PA_NO_PROGRESS          to "अभी तक कोई प्रगति डेटा नहीं",
        StringKeys.PA_NO_PROGRESS_DESC     to "शिक्षकों द्वारा अपडेट करने पर आपके बच्चे की योग्यताएं यहाँ दिखेंगी।",
        StringKeys.PA_NO_DAILY_LOGS        to "अभी तक कोई दैनिक लॉग नहीं",
        StringKeys.PA_NO_DAILY_LOGS_DESC   to "शिक्षकों द्वारा लॉग करना शुरू करने पर दैनिक क्लास सारांश यहाँ दिखेंगे।",
        StringKeys.PA_NO_QUIZZES           to "अभी तक कोई क्विज़ नहीं",
        StringKeys.PA_NO_QUIZZES_DESC      to "शिक्षकों द्वारा प्रकाशित करने पर क्विज़ यहाँ दिखेंगे।",
        StringKeys.PA_LEVEL                to "स्तर {level}",
        StringKeys.PA_PERCENT_COMPLETE     to "{percent}% पूर्ण",
        StringKeys.PA_QUIZ_QUESTIONS       to "{subject} · {count} प्रश्न",
        StringKeys.PA_START                to "शुरू करें",
        StringKeys.PA_QUIZ                 to "क्विज़",
        StringKeys.PA_YOU                  to " (आप)",
        StringKeys.PA_MATCH                to " (मिलान)",
        // ParentProfileCardScreenV2
        StringKeys.PC_ATTENDANCE           to "उपस्थिति",
        StringKeys.PC_LATEST_SCORE         to "नवीनतम स्कोर",
        StringKeys.PC_TO_NEXT              to "{percent}% अगले तक",
        StringKeys.PC_TOPICS_TODAY         to "आज के विषय",
        StringKeys.PC_ATTEND               to "उपस्थिति",
        StringKeys.PC_SCORE                to "स्कोर",
        StringKeys.PC_TODAY                to "आज",
        StringKeys.PC_TOPIC                to "विषय",
        StringKeys.PC_TOPICS               to "विषय",
        // ParentProfileScreenV2 + shared
        StringKeys.PP_LOGOUT_TITLE         to "लॉग आउट?",
        StringKeys.PP_LOGOUT_MSG           to "अपने बच्चे की प्रगति देखने के लिए आपको फिर से साइन इन करना होगा।",
        StringKeys.PP_LOGOUT_CONFIRM       to "लॉग आउट",
        StringKeys.PP_PROFILE              to "प्रोफ़ाइल",
        StringKeys.PP_PROFILE_UNAVAILABLE  to "प्रोफ़ाइल अनुपलब्ध",
        StringKeys.PP_PROFILE_UNAVAILABLE_DESC to "आपकी प्रोफ़ाइल लोड नहीं हो सकी। कृपया पुनः प्रयास करें।",
        StringKeys.PP_LANGUAGE             to "भाषा",
        // ParentLibraryScreenV2
        StringKeys.PL_LIBRARY              to "पुस्तकालय",
        StringKeys.PL_BACK                 to "वापस",
        StringKeys.PL_BOOKS_FOUND          to "{count} किताबें मिलीं",
        StringKeys.PL_RESERVE_BOOK         to "किताब आरक्षित करें",
        StringKeys.PL_RESERVE_MSG          to "इस किताब के उपलब्ध होने पर आपको सूचित किया जाएगा।",
        StringKeys.PL_RESERVE              to "आरक्षित करें",
        StringKeys.PL_MY_CHILD_BOOKS       to "मेरे बच्चे की किताबें",
        StringKeys.PL_MY_CHILD_BOOKS_DESC  to "आपके बच्चे को वर्तमान में जारी किताबें।",
        StringKeys.PL_NO_BOOKS_ISSUED      to "कोई किताब जारी नहीं",
        StringKeys.PL_NO_BOOKS_ISSUED_DESC to "आपके बच्चे के पास वर्तमान में कोई किताब जारी नहीं है।",
        StringKeys.PL_ISSUED               to "जारी: {date}",
        StringKeys.PL_RENEWALS             to "{count} नवीनीकरण",
        StringKeys.PL_RESERVATIONS         to "आरक्षण",
        StringKeys.PL_RESERVATIONS_DESC    to "आपके द्वारा आरक्षित किताबें। उपलब्ध होने पर सूचित किया जाएगा।",
        StringKeys.PL_NO_RESERVATIONS      to "कोई आरक्षण नहीं",
        StringKeys.PL_NO_RESERVATIONS_DESC to "ब्राउज़ टैब से किताब आरक्षित करें यहाँ देखने के लिए।",
        StringKeys.PL_RESERVED_ON          to "आरक्षित किया गया: {date}",
        StringKeys.PL_CANCEL_RESERVATION   to "आरक्षण रद्द करें",
        StringKeys.PL_CANCEL_RESERVATION_MSG to "क्या आप वाकई इस आरक्षण को रद्द करना चाहते हैं?",
        StringKeys.PL_CANCEL_RESERVATION_CONFIRM to "आरक्षण रद्द करें",
        StringKeys.PL_KEEP                 to "रखें",
        // ParentReportScreen
        StringKeys.PR_AI_REPORT_CARD       to "AI रिपोर्ट कार्ड",
        StringKeys.PR_NO_REPORTS           to "अभी तक कोई रिपोर्ट प्रकाशित नहीं",
        StringKeys.PR_NO_REPORTS_DESC      to "स्कूल द्वारा प्रकाशित करने पर रिपोर्ट यहाँ दिखेंगी।",
        StringKeys.PR_CONFERENCE_PACK      to "सम्मेलन पैक",
        StringKeys.PR_SUMMARY              to "सारांश",
        StringKeys.PR_FOCUS_AREAS          to "ध्यान क्षेत्र",
        StringKeys.PR_STRENGTHS            to "मजबूतियां",
        StringKeys.PR_CONFERENCE_TIPS      to "सम्मेलन सुझाव",
        StringKeys.PR_PUBLISHED            to "प्रकाशित",
        StringKeys.PR_PUBLISHED_ON         to "प्रकाशित: {date}",
        // ScholarshipWorkflowScreenV2
        StringKeys.SW_PROFILE_STRENGTH     to "प्रोफ़ाइल शक्ति",
        StringKeys.SW_ELIGIBILITY          to "पात्रता: ",
        StringKeys.SW_AWARD                to "पुरस्कार",
        StringKeys.SW_APPLY_BY             to "आवेदन करें",
        StringKeys.SW_REMARKS              to "टिप्पणियां: {remarks}",
        StringKeys.SW_DISBURSED            to "वितरित: ₹{amount}",
        StringKeys.SW_REF                  to "संदर्भ: {ref}",
        StringKeys.SW_APPLY_FOR_SCHOLARSHIP to "छात्रवृत्ति के लिए आवेदन",
        StringKeys.SW_CHILD_ID             to "बच्चे की आईडी *",
        StringKeys.SW_DOCUMENTS            to "दस्तावेज़ (URL)",
        // ScholarshipsScreenV2
        StringKeys.SL_PROFILE_STRENGTH     to "प्रोफ़ाइल शक्ति",
        StringKeys.SL_AWARD                to "पुरस्कार",
        StringKeys.SL_CLOSES_IN            to "बंद होगा",
        // ParentHealthScreenV2
        StringKeys.PHS_NO_PROFILE          to "अभी तक कोई स्वास्थ्य प्रोफ़ाइल लिंक नहीं",
        StringKeys.PHS_NO_PROFILE_DESC     to "स्कूल द्वारा आपके बच्चे के स्वास्थ्य रिकॉर्ड जोड़ने पर वे यहाँ दिखेंगे।",
        StringKeys.PHS_DOSE                to "खुराक {number} · {date}",
        StringKeys.PHS_BY                  to "द्वारा {name}",
        StringKeys.PHS_NEXT_DUE            to "अगली देय: {date}",
        StringKeys.PHS_TREATMENT           to "उपचार: {treatment}",
        StringKeys.PHS_MEDICATION          to "दवा: {medication}",
        StringKeys.PHS_TIME                to "समय: {time}",
        StringKeys.PHS_PARENT_NOTIFIED     to "अभिभावक को सूचित किया गया",
        // ParentHomeScreenV2
        StringKeys.PH_STAY_INFORMED        to "सूचित रहें",
        StringKeys.PH_STAY_INFORMED_MSG    to "स्कूल कार्यक्रमों, उपस्थिति और फीस रिमाइंडर के बारे में महत्वपूर्ण अपडेट प्राप्त करने के लिए सूचनाएं सक्षम करें।",
        StringKeys.PH_ENABLE               to "सक्षम करें",
        StringKeys.PH_NOT_NOW              to "अभी नहीं",
        StringKeys.PH_NO_CHILD_LINKED      to "अभी तक कोई बच्चा लिंक नहीं",
        StringKeys.PH_NO_CHILD_LINKED_DESC to "अपने बच्चे की दैनिक यात्रा और प्रगति देखने के लिए उन्हें लिंक करें।",
        StringKeys.PH_TRACK_BUS            to "बस ट्रैक करें",
        StringKeys.PH_TRACK_BUS_DESC       to "आपके बच्चे के लिए लाइव बस स्थान और ETA",
        StringKeys.PH_SCHOLARSHIPS         to "छात्रवृत्तियां",
        StringKeys.PH_SCHOLARSHIPS_DESC    to "छात्रवृत्ति अवसर ब्राउज़ और आवेदन करें",
        StringKeys.PH_DIGITAL_ID           to "डिजिटल आईडी कार्ड",
        StringKeys.PH_DIGITAL_ID_DESC      to "अपने बच्चे का डिजिटल स्कूल आईडी कार्ड देखें",
        StringKeys.PH_LIBRARY              to "पुस्तकालय",
        StringKeys.PH_LIBRARY_DESC         to "किताबें खोजें, जारी किताबें देखें और आरक्षित करें",
        StringKeys.PH_SCHOOL_EVENTS        to "स्कूल कार्यक्रम",
        StringKeys.PH_SCHOOL_EVENTS_DESC   to "PTM, कार्यक्रमों के लिए रजिस्टर और समय स्लॉट बुक करें",
        // ParentScheduleCard
        StringKeys.PS_TODAY_SCHEDULE       to "आज का कार्यक्रम",
        StringKeys.PS_TODAY_BADGE          to "आज",
        StringKeys.PS_WEEKLY_TIMETABLE     to "साप्ताहिक समय सारिणी",
        StringKeys.PS_NO_CLASSES           to "कोई क्लास नहीं",
        // ParentFeesScreenV2
        StringKeys.PF_FEES                 to "फीस",
        StringKeys.PF_PAY_NOW              to "अभी भुगतान करें",
        StringKeys.PF_COMING_SOON          to " · जल्द आ रहा है",
        // ParentEventRegistrationScreenV2
        StringKeys.PE_CANCEL_REGISTRATION  to "रजिस्ट्रेशन रद्द करें",
        StringKeys.PE_CANCEL_REGISTRATION_MSG to "क्या आप वाकई {title} के लिए अपनी रजिस्ट्रेशन रद्द करना चाहते हैं?",
        StringKeys.PE_YES_CANCEL           to "हां, रद्द करें",
        // ParentLeaveScreenV2
        StringKeys.PLV_LEAVE               to "छुट्टी",
        StringKeys.PLV_APPLY_FOR_LEAVE     to "छुट्टी के लिए आवेदन",
        StringKeys.PLV_MY_REQUESTS         to "मेरे अनुरोध",
        StringKeys.PLV_NO_REQUESTS         to "कोई छुट्टी अनुरोध नहीं",
        StringKeys.PLV_NO_REQUESTS_DESC    to "आपके द्वारा जमा किए गए अनुरोध यहाँ उनकी स्थिति के साथ दिखेंगे।",
        StringKeys.PLV_FROM                to "से",
        StringKeys.PLV_START_DATE          to "प्रारंभ तिथि",
        StringKeys.PLV_TO                  to "तक",
        StringKeys.PLV_END_DATE            to "अंतिम तिथि",
        StringKeys.PLV_REASON              to "कारण",
        StringKeys.PLV_REASON_PH           to "जैसे बुखार / पारिवारिक कार्यक्रम",
        // ParentMessagesScreenV2
        StringKeys.PM_NEW_MESSAGE          to "नया संदेश",
        StringKeys.PM_NO_MESSAGES          to "अभी तक कोई संदेश नहीं",
        StringKeys.PM_NO_MESSAGES_DESC     to "आपके बच्चे के शिक्षकों और स्कूल कार्यालय के संदेश यहाँ दिखेंगे।",
        StringKeys.PM_NO_ONE_TO_MESSAGE    to "अभी किसी को संदेश नहीं भेज सकते",
        StringKeys.PM_NO_ONE_TO_MESSAGE_DESC to "अपने बच्चे को स्कूल से लिंक करें उनके शिक्षकों और कार्यालय को संदेश भेजने के लिए।",
        StringKeys.PM_START_CONVERSATION   to "नीचे संदेश भेजें बातचीत शुरू करने के लिए।",
        // ParentPulseScreen
        StringKeys.PPS_PARENT_PULSE        to "पैरेंट पल्स",
        StringKeys.PPS_NO_PULSE            to "अभी तक कोई पल्स नहीं",
        StringKeys.PPS_NO_PULSE_DESC       to "साप्ताहिक सारांश के लिए रविवार के बाद जांचें।",
        StringKeys.PPS_NO_HISTORY          to "अभी तक कोई इतिहास नहीं",
        StringKeys.PPS_NO_HISTORY_DESC     to "कुछ हफ्तों बाद पल्स इतिहास यहाँ दिखेगा।",
        StringKeys.PPS_NO_PULSE_AVAILABLE  to "कोई पल्स उपलब्ध नहीं",
        StringKeys.PPS_NO_PULSE_AVAILABLE_DESC to "आपके बच्चे का साप्ताहिक पल्स हर रविवार शाम को यहाँ दिखेगा।",
        StringKeys.PPS_CLOSE               to "बंद करें",
        StringKeys.PPS_HISTORY             to "इतिहास",
        StringKeys.PPS_VIEW_HISTORY        to "12-सप्ताह का इतिहास देखें",
        // ParentUnlinkedScreenV2
        StringKeys.PU_LINK_CHILD           to "बच्चा लिंक करें",
        StringKeys.PU_EXPLORE_SCHOOLS      to "स्कूल एक्सप्लोर करें",
        StringKeys.PU_WELCOME              to "विद्या प्रयाग में आपका स्वागत है",
        StringKeys.PU_LINK_TITLE           to "अपने बच्चे की यात्रा का अनुसरण करें",
        StringKeys.PU_EXPLORE_TITLE        to "सही स्कूल खोजें",
        StringKeys.PU_LINK_DESC            to "उपस्थिति, अंक और अधिक देखने के लिए अपने बच्चे को उनके स्कूल से लिंक करें।",
        StringKeys.PU_EXPLORE_DESC         to "विद्या प्रयाग पर स्कूल ब्राउज़ करें, तुलना करें और पूछताछ करें।",
        // ParentConversationsScreenV2
        StringKeys.PCV_MESSAGES            to "संदेश",
        StringKeys.PCV_ANNOUNCEMENTS       to "घोषणाएं",
        StringKeys.PCV_CONVERSATIONS        to "बातचीत",
        // ParentActivityScreenV2
        StringKeys.PAC_ACTIVITY            to "गतिविधि",
        // ParentPewsScreenV2
        StringKeys.PPEWS_ATTENDANCE        to "उपस्थिति",
        // ParentCoveredDetailOverlay
        StringKeys.PCD_TODAYS_TOPICS       to "आज के विषय",
        StringKeys.PCD_NO_TOPICS           to "आज अभी तक कोई विषय लॉग नहीं",
        StringKeys.PCD_SYLLABUS_COVERAGE   to "सिलेबस कवरेज",
        // ParentResultsFeesCards
        StringKeys.PRF_PUBLISHED            to "प्रकाशित",
        // AiReportCardPreview
        StringKeys.AIP_AI_NARRATIVE        to "AI कथन",
        // BusTrackingScreenV2
        StringKeys.BT_BUS_TRACKING          to "बस ट्रैकिंग",
        StringKeys.BT_NO_TRANSPORT          to "कोई परिवहन असाइनमेंट नहीं मिला",
        StringKeys.BT_NO_TRANSPORT_DESC     to "इस बच्चे को अभी तक किसी बस रूट पर नहीं रखा गया है।",
        StringKeys.BT_WAITING               to "बस स्थान की प्रतीक्षा कर रहे हैं…",
        StringKeys.BT_ROUTE                 to "रूट",
        StringKeys.BT_BUS                   to "बस: {bus}",
        StringKeys.BT_ETA                   to "ETA {eta} मिनट",
        StringKeys.BT_NEXT_STOP             to "अगला स्टॉप: {stop}",
        // DigitalIdCardScreen
        StringKeys.DID_DIGITAL_ID_CARD      to "डिजिटल आईडी कार्ड",
        StringKeys.DID_SHOW_BACK            to "पीछे का देखें",
        StringKeys.DID_SHOW_FRONT           to "सामने का देखें",
        StringKeys.DID_SCAN_QR_BACK         to "प्रोफ़ाइल सत्यापित करने के लिए पीछे के QR कोड को स्कैन करें",
        StringKeys.DID_VALID_TILL           to "वैध तक: {date}",
        StringKeys.DID_LOADING              to "आईडी कार्ड लोड हो रहा है...",
        StringKeys.DID_NO_ID_CARD           to "कोई आईडी कार्ड नहीं मिला। एडमिन से जनरेट करवाएं।",
        StringKeys.DID_QR_CODE              to "QR कोड",
        StringKeys.DID_SCAN_VERIFY          to "प्रोफ़ाइल सत्यापित करने के लिए स्कैन करें",
        // ParentPortalV2
        StringKeys.PPRT_HOME                to "होम",
        StringKeys.PPRT_ACADEMICS           to "शैक्षणिक",
        StringKeys.PPRT_FEES                to "फीस",
        StringKeys.PPRT_CONVERSATIONS       to "बातचीत",
        StringKeys.PPRT_PROFILE             to "प्रोफ़ाइल",
        StringKeys.PPRT_LEVEL_JOURNEY       to "स्तर {level} · {percent}% यात्रा",
        StringKeys.PPRT_LEVEL               to "स्तर {level}",
        StringKeys.PPRT_YOUR_CHILD          to "आपका बच्चा",
        StringKeys.PPRT_SWITCH_CHILD        to "बच्चा बदलें",
        // PulseCard
        StringKeys.PUL_HW                  to "होमवर्क",
        StringKeys.PUL_MSGS                to "संदेश",
        StringKeys.PUL_ALERTS              to "अलर्ट",
        StringKeys.PUL_WEEKLY_PULSE        to "साप्ताहिक पल्स",
        StringKeys.PUL_ATTENDANCE          to "उपस्थिति",
        StringKeys.PUL_MARKS_THIS_WEEK     to "इस सप्ताह के अंक",
        StringKeys.PUL_ACTION_ITEMS        to "कार्य वस्तुएं",
        StringKeys.PUL_UPCOMING            to "आगामी",
        // ParentLeaveScreenV2 (extras)
        StringKeys.PLV_SUBMIT_REQUEST      to "अनुरोध जमा करें",
        StringKeys.PLV_CHILD               to "बच्चा",
        // ParentMessagesScreenV2 (extras)
        StringKeys.PM_MESSAGES             to "संदेश",
        StringKeys.PM_CONVERSATION         to "बातचीत",
        StringKeys.PM_SELECT_RECIPIENT     to "प्राप्तकर्ता चुनें",
        StringKeys.PM_PICK_RECIPIENT_PH    to "ऊपर प्राप्तकर्ता चुनें…",
        StringKeys.PM_MESSAGE_NAME_PH      to "{name} को संदेश…",
        StringKeys.PM_TYPE_MESSAGE_PH      to "संदेश लिखें…",
        StringKeys.PM_MESSAGE_DELETED      to "यह संदेश हटा दिया गया",
        StringKeys.PM_EDITED               to "संपादित",
        // ParentAttendanceCard
        StringKeys.PATT_ATTENDANCE_TODAY    to "उपस्थिति · आज",
        StringKeys.PATT_THIS_MONTH          to "इस महीने",
        StringKeys.PATT_PERCENT_PRESENT     to "{rate}% उपस्थित",
        StringKeys.PATT_TRACKING_FROM_TODAY to "आज से ट्रैकिंग",
        StringKeys.PATT_MONTH_FILLS         to "क्लास अंकित होने पर महीना भरता जाएगा",
        StringKeys.PATT_SWIPE_CALENDAR      to "महीना कैलेंडर के लिए स्वाइप करें",
        StringKeys.PATT_PRESENT             to "उपस्थित",
        StringKeys.PATT_LATE                to "देर से",
        StringKeys.PATT_ABSENT              to "अनुपस्थित",
        StringKeys.PATT_HOLIDAY             to "अवकाश",
        StringKeys.PATT_BREAK               to "ब्रेक",
        StringKeys.PATT_SUNDAY              to "रविवार",
        StringKeys.PATT_AWAITING            to "प्रतीक्षारत",
        StringKeys.PATT_MARKED_PRESENT      to "आज उपस्थित अंकित",
        StringKeys.PATT_IN_SCHOOL           to "आपका बच्चा स्कूल में है",
        StringKeys.PATT_ARRIVED_LATE        to "आज देर से आए",
        StringKeys.PATT_MARKED_PRESENT_LATE to "उपस्थित अंकित, घंटी के बाद",
        StringKeys.PATT_MARKED_ABSENT       to "आज अनुपस्थित अंकित",
        StringKeys.PATT_NO_ATTENDANCE_TODAY to "आज के लिए कोई उपस्थिति अभिलेखित नहीं",
        StringKeys.PATT_SCHOOL_HOLIDAY       to "आज स्कूल अवकाश",
        StringKeys.PATT_ENJOY_DAY_OFF       to "छुट्टी का आनंद लें",
        StringKeys.PATT_ON_VACATION         to "छुट्टी पर",
        StringKeys.PATT_ENJOY_BREAK         to "ब्रेक का आनंद लें",
        StringKeys.PATT_NO_SCHOOL           to "आज स्कूल नहीं है",
        StringKeys.PATT_SUNDAY_DESC         to "आज रविवार है",
        StringKeys.PATT_NOT_MARKED_YET      to "उपस्थिति अभी अंकित नहीं हुई",
        StringKeys.PATT_WAITING_CLASS       to "क्लास अंकित होने पर आज की स्थिति दिखेगी",
        StringKeys.PATT_SCHOOL_DAYS         to "{total} में से {attended} स्कूल दिन",
        StringKeys.PATT_LATE_DAYS           to "{count} देर से",
        StringKeys.PATT_ABSENT_DAYS         to "{count} अनुपस्थित",
        // ParentAttendanceCalendar
        StringKeys.PACL_LEGEND              to "लेजेंड",
        StringKeys.PACL_PRESENT             to "उपस्थित",
        StringKeys.PACL_LATE                to "देर से",
        StringKeys.PACL_ABSENT              to "अनुपस्थित",
        // ParentCoveredCard
        StringKeys.PCC_COVERED_SUMMARY      to "आज कवर्ड · सारांश",
        StringKeys.PCC_COVERED_LIVE         to "आज कवर्ड · लाइव",
        StringKeys.PCC_NOTHING_LOGGED       to "आज कुछ लॉग नहीं",
        StringKeys.PCC_NOTHING_COVERED      to "आज अभी तक कुछ कवर नहीं",
        StringKeys.PCC_NOTHING_LOGGED_DESC  to "शिक्षकों ने आज सिलेबस कवरेज लॉग नहीं किया",
        StringKeys.PCC_FILLS_LIVE           to "स्कूल दिन बीतने पर यह लाइव भरता है",
        StringKeys.PCC_TOPICS_ACROSS        to "{count} {topic} {subjectCount} {subject} में",
        StringKeys.PCC_MORE                 to "+{count} और",
        StringKeys.PCC_TAP_BREAKDOWN        to "पूरी विवरणी के लिए टैप करें",
        // ParentNudgeCard
        StringKeys.PNC_GOT_IT               to "समझ गया",
        StringKeys.PNC_HEADLINE_FALLBACK    to "{name} के लिए थोड़ा सहयोग",
        // ScholarshipWorkflowScreenV2 (extras)
        StringKeys.SW_SCHOLARSHIPS          to "छात्रवृत्तियां",
        StringKeys.SW_NO_SCHOLARSHIPS       to "अभी तक कोई छात्रवृत्ति नहीं",
        StringKeys.SW_NO_SCHOLARSHIPS_DESC  to "स्कूल द्वारा प्रकाशित होने पर छात्रवृत्ति अवसर यहाँ दिखेंगे।",
        StringKeys.SW_AVAILABLE             to "उपलब्ध छात्रवृत्तियां ({count})",
        StringKeys.SW_MY_APPLICATIONS       to "मेरे आवेदन ({count})",
        // ParentLibraryScreenV2 (extras)
        StringKeys.PL_NO_BOOKS_FOUND        to "कोई किताब नहीं मिली",
        StringKeys.PL_NO_BOOKS_FOUND_DESC   to "एक अलग खोज क्वेरी आज़माएं।",
        StringKeys.PL_FINE                  to "जुर्माना: ₹{amount} ({status})",
        // ParentHealthScreenV2 (extras)
        StringKeys.PHS_YOUR_CHILD           to "आपका बच्चा",
        StringKeys.PHS_IMMUNIZATIONS        to "टीकाकरण",
        StringKeys.PHS_HEALTH_INCIDENTS     to "स्वास्थ्य घटनाएं",
        StringKeys.PHS_HEALTH_PROFILE       to "स्वास्थ्य प्रोफ़ाइल",
        StringKeys.PHS_BLOOD_GROUP          to "रक्त समूह",
        StringKeys.PHS_HEIGHT               to "ऊंचाई",
        StringKeys.PHS_WEIGHT               to "वजन",
        StringKeys.PHS_HEIGHT_VALUE         to "{value} सेमी",
        StringKeys.PHS_WEIGHT_VALUE         to "{value} किग्रा",
        StringKeys.PHS_ALLERGIES            to "एलर्जी",
        StringKeys.PHS_CHRONIC_CONDITIONS   to "दीर्घकालिक स्थितियां",
        StringKeys.PHS_MEDICATIONS          to "दवाएं",
        StringKeys.PHS_EMERGENCY_CONTACT    to "आपातकालीन संपर्क",
        StringKeys.PHS_NAME                 to "नाम",
        StringKeys.PHS_PHONE                to "फ़ोन",
        StringKeys.PHS_DOCTOR               to "डॉक्टर",
        // ParentReportScreen (extras)
        StringKeys.PR_CONFERENCE_SUBTITLE   to "{studentName} — {className} {section} • {term}",
        StringKeys.PR_OVERALL               to "समग्र",
        StringKeys.PR_GRADE                 to "ग्रेड",
        StringKeys.PR_ATTENDANCE            to "उपस्थिति",
        // ScholarshipWorkflowScreenV2 (extras 2)
        StringKeys.SW_LEVEL                 to "स्तर {level}",
        StringKeys.SW_APPLICATIONS          to "आवेदन",
        StringKeys.SW_APPROVED              to "स्वीकृत",
        StringKeys.SW_AWARDED               to "पुरस्कार राशि",
        StringKeys.SW_DAY_STREAK            to "दिन श्रृंखला",
        StringKeys.SW_HOT                   to "हॉट",
        StringKeys.SW_RENEWABLE             to "नवीनीकरणीय",
        StringKeys.SW_APPLY_NOW             to "अभी आवेदन करें",
        StringKeys.SW_STUDENT               to "छात्र",
        StringKeys.SW_DOCUMENT_URL          to "दस्तावेज़ URL",
        StringKeys.SW_ADD                   to "जोड़ें",
        StringKeys.SW_APPLICATION_TEXT      to "आवेदन पाठ (वैकल्पिक)",
        StringKeys.SW_CANCEL                to "रद्द करें",
        StringKeys.SW_SUBMIT                to "जमा करें",
        // ParentLibraryScreenV2 (extras 2)
        StringKeys.PL_TAB_BROWSE            to "ब्राउज़",
        StringKeys.PL_TAB_MY_BOOKS          to "मेरी किताबें",
        StringKeys.PL_TAB_RESERVATIONS      to "आरक्षण",
        StringKeys.PL_VIEWING_FOR           to "{name} के लिए किताबें देख रहे हैं",
        StringKeys.PL_PARENT                to "अभिभावक",
        StringKeys.PL_SEARCH_PH             to "शीर्षक, लेखक, या ISBN से खोजें",
        // ParentHealthScreenV2 (extras 2)
        StringKeys.PHS_SEVERITY_MAJOR       to "प्रमुख",
        StringKeys.PHS_SEVERITY_MODERATE    to "मध्यम",
        StringKeys.PHS_SEVERITY_MINOR       to "मामूली",
        // ── Phase 3: School/Admin screens ──
        // AnalyticsDashboardScreenV2
        StringKeys.SCH_ANALYTICS            to "एनालिटिक्स",
        StringKeys.SCH_NO_ANALYTICS         to "अभी कोई एनालिटिक्स नहीं",
        StringKeys.SCH_NO_ANALYTICS_DESC    to "एनालिटिक्स रोलअप एंडपॉइंट में डेटा आने पर ओवरव्यू दिखाई देगा।",
        StringKeys.SCH_PERFORMANCE_TREND    to "प्रदर्शन प्रवृत्ति",
        StringKeys.SCH_OVERVIEW             to "ओवरव्यू",
        StringKeys.SCH_INSIGHTS             to "इनसाइट्स",
        // StaffProfileScreenV2
        StringKeys.SCH_STAFF                to "स्टाफ",
        StringKeys.SCH_NO_PROFILE           to "कोई प्रोफ़ाइल नहीं",
        StringKeys.SCH_NO_PROFILE_DESC      to "इस स्टाफ सदस्य का रिकॉर्ड नहीं मिला।",
        StringKeys.SCH_CONTACT              to "संपर्क",
        StringKeys.SCH_NO_CONTACT_DETAILS   to "कोई संपर्क विवरण नहीं।",
        StringKeys.SCH_REMOVE_FROM_SCHOOL   to "स्कूल से हटाएं",
        StringKeys.SCH_REMOVE_STAFF_MEMBER  to "स्टाफ सदस्य हटाएं",
        StringKeys.SCH_REMOVE_STAFF_CONFIRM to "{name} को आपके स्कूल से हटाएं? उनका रिकॉर्ड छिप जाएगा। इसे फिर से जोड़कर पूर्ववत किया जा सकता है।",
        StringKeys.SCH_REMOVE               to "हटाएं",
        // DailyAttendanceScreenV2
        StringKeys.SCH_DAILY_ATTENDANCE     to "दैनिक उपस्थिति",
        StringKeys.SCH_STUDENTS             to "छात्र",
        StringKeys.SCH_FACULTY              to "संकाय",
        StringKeys.SCH_NO_ROSTER            to "कोई रोस्टर नहीं",
        StringKeys.SCH_NO_STUDENTS_IN_CLASS to "{className} में अभी कोई छात्र नहीं हैं।",
        StringKeys.SCH_NO_FACULTY_ROSTER    to "कोई संकाय रोस्टर उपलब्ध नहीं है।",
        StringKeys.SCH_PRESENT_TODAY        to "आज उपस्थित",
        StringKeys.SCH_STUDENTS_HEADER      to "छात्र",
        StringKeys.SCH_FACULTY_HEADER       to "संकाय",
        // PewsEffectivenessScreenV2
        StringKeys.SCH_EFFECTIVENESS        to "प्रभावशीलता",
        StringKeys.SCH_NO_DATA_YET          to "अभी कोई डेटा नहीं",
        StringKeys.SCH_EFFECTIVENESS_DESC   to "पहली PEWS रन के बाद हस्तक्षेप के साथ प्रभावशीलता डेटा दिखाई देगा।",
        StringKeys.SCH_INTERVENTION_OUTCOMES to "हस्तक्षेप परिणाम",
        StringKeys.SCH_OPEN                 to "खुला",
        StringKeys.SCH_RESOLVED             to "हल हो गया",
        StringKeys.SCH_IMPROVED             to "सुधार",
        StringKeys.SCH_NO_CHANGE            to "कोई बदलाव नहीं",
        StringKeys.SCH_WORSENED             to "बिगड़ा",
        StringKeys.SCH_RISK_TREND_30        to "जोखिम प्रवृत्ति (30 दिन)",
        StringKeys.SCH_HIGH                 to "उच्च",
        StringKeys.SCH_MEDIUM               to "मध्यम",
        StringKeys.SCH_WATCH                to "निगरानी",
        // ResultsPublishScreenV2
        StringKeys.SCH_RESULTS              to "परिणाम",
        StringKeys.SCH_TESTS                to "परीक्षा",
        StringKeys.SCH_CLASSES              to "कक्षाएं",
        StringKeys.SCH_SUBJECTS             to "विषय",
        StringKeys.SCH_NO_RESULTS_YET       to "अभी कोई परिणाम नहीं",
        StringKeys.SCH_NO_RESULTS_DESC      to "ऊपर एक परीक्षा/कक्षा/विषय चुनें। शिक्षकों द्वारा अंक दर्ज करने पर कक्षा सारांश और छात्र यहाँ दिखाई देंगे।",
        StringKeys.SCH_CLASS_AVERAGE        to "कक्षा औसत",
        StringKeys.SCH_EXCEEDING            to "से अधिक",
        StringKeys.SCH_MEETING              to "उपयुक्त",
        StringKeys.SCH_BELOW                to "से कम",
        StringKeys.SCH_SCORE_ATTENDANCE     to "अंक {score} · उपस्थिति {attendance}",
        // SchedulePtmScreenV2
        StringKeys.SCH_SCHEDULE_PTM         to "PTM शेड्यूल करें",
        StringKeys.SCH_NO_PTMS_YET          to "अभी कोई PTM नहीं",
        StringKeys.SCH_NO_PTMS_DESC         to "शुरू करने के लिए अपनी पहली अभिभावक-शिक्षक बैठक शेड्यूल करें।",
        StringKeys.SCH_NEW_PTM              to "नया PTM",
        StringKeys.SCH_TITLE                to "शीर्षक",
        StringKeys.SCH_TITLE_PH             to "जैसे टर्म 1 PTM",
        StringKeys.SCH_DATE                 to "तारीख",
        StringKeys.SCH_PTM_DATE_PH          to "PTM तारीख चुनें",
        StringKeys.SCH_SLOT                 to "स्लॉट",
        StringKeys.SCH_SLOT_PH              to "सुबह 10:00 - दोपहर 1:00",
        StringKeys.SCH_CREATE               to "बनाएं",
        StringKeys.SCH_SCHEDULE_NEW_PTM     to "नया PTM शेड्यूल करें",
        StringKeys.SCH_ACTIVE               to "सक्रिय",
        StringKeys.SCH_EXPECTED             to "अपेक्षित",
        StringKeys.SCH_CHECKED_IN           to "चेक-इन",
        StringKeys.SCH_INVITES_SENT         to "निमंत्रण भेजे",
        StringKeys.SCH_READ                 to "पढ़ा",
        StringKeys.SCH_HISTORY              to "इतिहास",
        StringKeys.SCH_CLASS_PROGRESS       to "कक्षा प्रगति",
        // PewsCohortScreenV2
        StringKeys.SCH_EARLY_WARNING        to "प्रारंभिक चेतावनी",
        StringKeys.SCH_RECOMPUTE            to "पुनर्गणना",
        StringKeys.SCH_NO_STUDENTS_ATTENTION to "किसी छात्र को ध्यान की आवश्यकता नहीं",
        StringKeys.SCH_NO_STUDENTS_ATTENTION_DESC to "चयनित बैंड में हर छात्र अभी सही है। रिफ्रेश के लिए पुनर्गणना करें, या बैंड फ़िल्टर चौड़ा करें।",
        StringKeys.SCH_EFFECTIVENESS_HEADER to "प्रभावशीलता",
        StringKeys.SCH_EFFECTIVENESS_LOOP_DESC to "हस्तक्षेप लूप क्या हासिल कर रहा है",
        StringKeys.SCH_CONFIGURATION        to "कॉन्फ़िगरेशन",
        StringKeys.SCH_CONFIGURATION_DESC   to "थ्रेशोल्ड, रन आवृत्ति और क्या साझा किया जाता है",
        StringKeys.SCH_RELATIVE_THRESHOLDS  to "सापेक्ष थ्रेशोल्ड",
        StringKeys.SCH_RELATIVE_THRESHOLDS_HINT to "निश्चित सीमा के बजाय कोहोर्ट में z-स्कोर का उपयोग करें",
        StringKeys.SCH_AI_NARRATIVE         to "AI विवरण",
        StringKeys.SCH_AI_NARRATIVE_HINT    to "AI को संकेतों का सरल भाषा में स्पष्टीकरण लिखने दें",
        StringKeys.SCH_SHARE_WITH_PARENTS   to "अभिभावकों के साथ साझा करें",
        StringKeys.SCH_SHARE_WITH_PARENTS_HINT to "चालू होने पर, अभिभावक अपने बच्चे के लिए एक सौम्य, लेबल-रहित नज़र देखते हैं",
        StringKeys.SCH_RUN_FREQUENCY        to "रन आवृत्ति",
        StringKeys.SCH_DAILY                to "दैनिक",
        StringKeys.SCH_WEEKLY               to "साप्ताहिक",
        StringKeys.SCH_RISK_BAND            to "जोखिम बैंड",
        StringKeys.SCH_AS_OF                to "{date} तक",
        StringKeys.SCH_ALL                  to "सभी",
        StringKeys.SCH_MEDIUM_PLUS          to "मध्यम+",
        StringKeys.SCH_HIGH_ONLY            to "केवल उच्च",
        StringKeys.SCH_AI_DISABLED_NOTE     to "AI स्पष्टीकरण बंद हैं। पंक्तियाँ अभी भी वास्तविक उपस्थिति, अंक और अवकाश संकेत दिखाती हैं।",
        StringKeys.SCH_ALL_ON_TRACK_NOTE    to "इस बैंड में अभी किसी छात्र को ध्यान की आवश्यकता नहीं। नीचे की सेटिंग्स अभी भी लागू हैं।",
        StringKeys.SCH_QUEUED               to "कतार में…",
        StringKeys.SCH_RUNNING              to "चल रहा है…",
        StringKeys.SCH_COMPLETE             to "पूर्ण",
        StringKeys.SCH_FAILED               to "विफल",
        StringKeys.SCH_REFRESH              to "रिफ्रेश",
        StringKeys.SCH_RISK_TREND           to "जोखिम प्रवृत्ति",
        StringKeys.SCH_RISK_TREND_DESC      to "कोहोर्ट जोखिम वितरण (अंतिम 30 दिन)",
        StringKeys.SCH_CLASS_SECTION        to "कक्षा {className}{section}",
        // TeacherAssignmentManagementScreen
        StringKeys.SCH_ASSIGN_CLASSES       to "कक्षाएं असाइन करें",
        StringKeys.SCH_NO_TEACHER           to "कोई शिक्षक नहीं",
        StringKeys.SCH_NO_TEACHER_DESC      to "इस शिक्षक का रिकॉर्ड नहीं मिला।",
        StringKeys.SCH_REMOVE_ASSIGNMENT    to "असाइनमेंट हटाएं",
        StringKeys.SCH_REMOVE_ASSIGNMENT_DESC to "शिक्षक से यह कक्षा/विषय असाइनमेंट हटाएं? इसे कभी भी फिर से जोड़ा जा सकता है।",
        StringKeys.SCH_SUBJECT_TEACHER      to "{subject} शिक्षक",
        StringKeys.SCH_TEACHER              to "शिक्षक",
        StringKeys.SCH_COUNT_CLASSES        to "{count} कक्षाएं",
        StringKeys.SCH_COUNT_SUBJECTS       to "{count} विषय",
        StringKeys.SCH_CLASSES_ASSIGNED     to "असाइन की गई कक्षाएं",
        StringKeys.SCH_ACTIVE_KPI           to "सक्रिय",
        StringKeys.SCH_SUBJECTS_ASSIGNED    to "असाइन किए गए विषय",
        StringKeys.SCH_COVERED              to "कवर किया",
        StringKeys.SCH_TOTAL_STUDENTS       to "कुल छात्र",
        StringKeys.SCH_TAUGHT               to "पढ़ाया",
        StringKeys.SCH_SECTIONS_COVERED     to "सेक्शन कवर किए",
        StringKeys.SCH_ACROSS_CLASSES       to "कक्षाओं में",
        StringKeys.SCH_ASSIGNMENT_SUMMARY   to "असाइनमेंट सारांश",
        StringKeys.SCH_CURRENT_ASSIGNMENTS  to "वर्तमान असाइनमेंट",
        StringKeys.SCH_NO_CLASSES_ASSIGNED  to "अभी कोई कक्षा असाइन नहीं। नीचे बिल्डर का उपयोग करके जोड़ें।",
        StringKeys.SCH_CLASS_SECTION_LABEL  to "{className} · सेक्शन {section}",
        StringKeys.SCH_COUNT_STUDENTS       to "{count} छात्र",
        StringKeys.SCH_ADD_ASSIGNMENT       to "असाइनमेंट जोड़ें",
        StringKeys.SCH_LOADING_OPTIONS      to "कक्षा और विषय विकल्प लोड हो रहे हैं…",
        StringKeys.SCH_NO_CLASSES_SUBJECTS  to "इस स्कूल के लिए अभी कोई कक्षा या विषय सेट नहीं किया गया है।",
        StringKeys.SCH_STEP_1_SUBJECT       to "चरण 1 · विषय चुनें",
        StringKeys.SCH_STEP_2_CLASSES       to "चरण 2 · कक्षाएं चुनें",
        StringKeys.SCH_STEP_3_SECTIONS      to "चरण 3 · सेक्शन चुनें",
        StringKeys.SCH_PICK_CLASSES_FIRST   to "पहले एक या अधिक कक्षाएं चुनें — उनके सेक्शन यहाँ दिखाई देंगे। (खाली छोड़ने पर चुनी हुई कक्षाओं के सभी सेक्शन असाइन होंगे।)",
        StringKeys.SCH_LEAVE_UNSELECTED     to "चुनी हुई कक्षाओं के हर सेक्शन को असाइन करने के लिए सभी को अचयनित छोड़ें।",
        StringKeys.SCH_STEP_4_PREVIEW       to "चरण 4 · पूर्वावलोकन",
        StringKeys.SCH_CLEAR                to "साफ़ करें",
        StringKeys.SCH_SAVE_ASSIGNMENTS     to "असाइनमेंट सहेजें",
        StringKeys.SCH_WORKLOAD_INSIGHTS    to "कार्यभार इनसाइट्स",
        StringKeys.SCH_NO_WORKLOAD_INSIGHTS to "अभी कोई कार्यभार इनसाइट्स नहीं।",
        StringKeys.SCH_ASSIGNMENT_DISTRIBUTION to "असाइनमेंट वितरण",
        StringKeys.SCH_CLS_STU              to "{classCount} कक्षा · {studentCount} छात्र",
        // SchoolCommsScreenV2
        StringKeys.SCH_COMMUNICATIONS       to "संचार",
        StringKeys.SCH_ANNOUNCEMENTS        to "घोषणाएं",
        StringKeys.SCH_MESSAGES             to "संदेश",
        StringKeys.SCH_PTM                  to "PTM",
        StringKeys.SCH_NOTIFICATIONS        to "सूचनाएं",
        StringKeys.SCH_PARENT_MESSAGES      to "अभिभावक संदेश",
        StringKeys.SCH_PARENT_MESSAGES_DESC to "अभिभावक ↔ स्कूल द्विदिशात्मक संदेश थ्रेड खोलें।",
        StringKeys.SCH_PARENT_TEACHER_MEETINGS to "अभिभावक–शिक्षक बैठकें",
        StringKeys.SCH_PARENT_TEACHER_MEETINGS_DESC to "PTM शेड्यूल करें और स्लॉट बुकिंग ट्रैक करें।",
        StringKeys.SCH_DELIVERY_LOG         to "डिलीवरी लॉग",
        StringKeys.SCH_DELIVERY_LOG_DESC    to "सूचना सेवा आने पर Push/SMS/WhatsApp डिलीवरी रसीदें यहाँ दिखाई देंगी।",
        StringKeys.SCH_SCHEDULED            to "शेड्यूल किए गए",
        StringKeys.SCH_NEW                  to "नया",
        StringKeys.SCH_NO_ANNOUNCEMENTS     to "अभी कोई घोषणा नहीं",
        StringKeys.SCH_NO_ANNOUNCEMENTS_DESC to "अभिभावकों और स्टाफ को प्रकाशित पोस्ट यहाँ दिखाई देंगी।",
        StringKeys.SCH_CALENDAR_ONLY        to "केवल कैलेंडर",
        StringKeys.SCH_ANNOUNCEMENT         to "घोषणा",
        StringKeys.SCH_ANNOUNCEMENT_UNAVAILABLE to "घोषणा अनुपलब्ध",
        StringKeys.SCH_POSTED_BY            to "{date} • स्कूल प्रशासन द्वारा पोस्ट किया गया",
        StringKeys.SCH_NO_MESSAGES          to "अभी कोई संदेश नहीं",
        StringKeys.SCH_NO_MESSAGES_DESC     to "अभिभावकों या स्टाफ से बातचीत शुरू करें ताकि थ्रेड यहाँ दिखाई दें।",
        StringKeys.SCH_SEE_ALL_MESSAGES     to "सभी संदेश देखें",
        StringKeys.SCH_SEE_ALL_MESSAGES_DESC to "पूर्ण अभिभावक संदेश इनबॉक्स खोलें।",
        StringKeys.SCH_SEE_ALL_PTM          to "सभी PTM देखें",
        StringKeys.SCH_SEE_ALL_PTM_DESC     to "पूर्ण अभिभावक-शिक्षक बैठक इतिहास खोलें।",
        StringKeys.SCH_NO_DELIVERY_LOG      to "अभी कोई डिलीवरी रिकॉर्ड नहीं",
        StringKeys.SCH_NO_DELIVERY_LOG_DESC to "WhatsApp, push, SMS और email की डिलीवरी रसीदें यहाँ दिखाई देंगी।",
        StringKeys.SCH_SEE_ALL_DELIVERY_LOG to "सभी डिलीवरी रिकॉर्ड देखें",
        StringKeys.SCH_SEE_ALL_DELIVERY_LOG_DESC to "पूर्ण घोषणा डिलीवरी लॉग खोलें।",
        // ClassPerformanceScreenV2
        StringKeys.SCH_CLASS_PERFORMANCE    to "कक्षा प्रदर्शन",
        StringKeys.SCH_CLASS_PERFORMANCE_DESC to "शिक्षकों द्वारा अंक और उपस्थिति दर्ज करने पर कक्षा-स्तरीय एनालिटिक्स यहाँ दिखाई देगा।",
        StringKeys.SCH_AVG_PROFICIENCY      to "औसत दक्षता",
        StringKeys.SCH_ACTIVE_STUDENTS      to "सक्रिय छात्र",
        StringKeys.SCH_MEDIAN_GRADE         to "मध्यिका ग्रेड",
        StringKeys.SCH_GRADE_DISTRIBUTION   to "ग्रेड वितरण",
        StringKeys.SCH_SUBJECT_MATRIX       to "विषय मैट्रिक्स",
        StringKeys.SCH_EARLY_WARNING_HEADER to "प्रारंभिक चेतावनी",
        StringKeys.SCH_CRITICAL             to "गंभीर",
        StringKeys.SCH_MODERATE             to "मध्यम",
        StringKeys.SCH_ON_TARGET            to "लक्ष्य पर",
        StringKeys.SCH_TOP_PERFORMER        to "शीर्ष प्रदर्शक",
        StringKeys.SCH_STAR_1ST             to "★ प्रथम",
        StringKeys.SCH_PROGRESS_MONITORING  to "प्रगति निगरानी",
        StringKeys.SCH_TREND_UP             to "▲ ऊपर",
        StringKeys.SCH_TREND_DOWN           to "▼ नीचे",
        StringKeys.SCH_TREND_FLAT           to "● समान",
        StringKeys.SCH_PROGRESS_SCORES      to "गणित {math} · विज्ञान {science} · साहित्य {literature}",
        StringKeys.SCH_PROGRESS_ATTENDANCE  to "उपस्थिति {attendance}",
        // AcademicYearManagementScreenV2
        StringKeys.SCH_ACADEMIC_YEAR        to "शैक्षणिक वर्ष",
        StringKeys.SCH_CLOSE                to "बंद करें",
        StringKeys.SCH_NO_ACADEMIC_YEARS    to "अभी कोई शैक्षणिक वर्ष नहीं",
        StringKeys.SCH_NO_ACADEMIC_YEARS_DESC to "कैलेंडर को एंकर करने के लिए अपना पहला शैक्षणिक वर्ष बनाएं।",
        StringKeys.SCH_CREATE_ACADEMIC_YEAR to "शैक्षणिक वर्ष बनाएं",
        StringKeys.SCH_NAME                 to "नाम",
        StringKeys.SCH_YEAR_NAME_PH         to "जैसे 2026-27",
        StringKeys.SCH_START_DATE           to "प्रारंभ तारीख",
        StringKeys.SCH_END_DATE             to "समाप्ति तारीख",
        StringKeys.SCH_SAVE_DRAFT           to "ड्राफ्ट सहेजें",
        StringKeys.SCH_CREATE_ACTIVATE      to "बनाएं और सक्रिय करें",
        StringKeys.SCH_ACTIVE_YEAR          to "सक्रिय वर्ष",
        StringKeys.SCH_HISTORICAL_DRAFTS    to "ऐतिहासिक और ड्राफ्ट",
        StringKeys.SCH_ACTIVATE             to "सक्रिय करें",
        StringKeys.SCH_ARCHIVE              to "संग्रहित करें",
        StringKeys.SCH_SCHOOL_DAYS          to "{count} स्कूल दिन",
        StringKeys.SCH_HOLIDAYS             to "{count} अवकाश",
        // IdCardGenerateTab
        StringKeys.SCH_NO_TEMPLATES         to "कोई टेम्पलेट उपलब्ध नहीं",
        StringKeys.SCH_NO_TEMPLATES_DESC    to "पहले टेम्पलेट टैब में एक टेम्पलेट बनाएं।",
        StringKeys.SCH_SELECT_TEMPLATE      to "टेम्पलेट चुनें",
        StringKeys.SCH_INACTIVE             to "निष्क्रिय",
        StringKeys.SCH_SELECT_SCOPE         to "दायरा चुनें",
        StringKeys.SCH_ALL_STUDENTS         to "सभी छात्र",
        StringKeys.SCH_ALL_STAFF            to "सभी स्टाफ",
        StringKeys.SCH_BY_CLASS             to "कक्षा अनुसार",
        StringKeys.SCH_CLASS_ID_UUID        to "कक्षा आईडी (UUID)",
        StringKeys.SCH_GENERATING           to "बनाया जा रहा है...",
        StringKeys.SCH_GENERATE_CARDS       to "कार्ड बनाएं",
        StringKeys.SCH_RENDERING_CARDS      to "कार्ड समानांतर में रेंडर और अपलोड हो रहे हैं...",
        // TeacherPerformanceScreenV2
        StringKeys.SCH_TEACHER_PERFORMANCE  to "शिक्षक प्रदर्शन",
        StringKeys.SCH_TEACHER_PERFORMANCE_DESC to "संकाय द्वारा उपस्थिति और अंक दर्ज करने पर शिक्षक एनालिटिक्स यहाँ दिखाई देगा।",
        StringKeys.SCH_AGGREGATE_COMPLIANCE to "समग्र अनुपालन",
        StringKeys.SCH_STAR_FACULTY         to "उत्कृष्ट संकाय",
        StringKeys.SCH_ACCOUNTABILITY_MATRIX to "जवाबदेही मैट्रिक्स",
        StringKeys.SCH_DEPARTMENT_EFFICIENCY to "विभाग दक्षता",
        StringKeys.SCH_COMPLIANCE           to "अनुपालन",
        StringKeys.SCH_DELAY                to "देरी",
        StringKeys.SCH_AVG_MARK             to "औसत अंक",
        // IdCardCardsTab
        StringKeys.SCH_SEARCH_BY_NAME       to "नाम से खोजें...",
        StringKeys.SCH_TEACHERS             to "शिक्षक",
        StringKeys.SCH_CARDS_COUNT          to "{filtered} / {total} कार्ड",
        StringKeys.SCH_NO_CARDS_MATCH       to "\"{query}\" से कोई कार्ड नहीं मिला",
        StringKeys.SCH_NO_CARDS_YET         to "अभी कोई कार्ड नहीं बना",
        StringKeys.SCH_TRY_DIFFERENT_SEARCH to "एक अलग खोज शब्द आज़माएं",
        StringKeys.SCH_GO_TO_GENERATE       to "कार्ड बनाने के लिए जनरेट टैब पर जाएं।",
        StringKeys.SCH_DELETE_ID_CARD       to "ID कार्ड हटाएं?",
        StringKeys.SCH_DELETE_ID_CARD_CONFIRM to "क्या आप {name} का ID कार्ड हटाना चाहते हैं? यह क्रिया पूर्ववत नहीं की जा सकती।",
        StringKeys.SCH_ID_CARD              to "ID कार्ड",
        StringKeys.SCH_QR_CODE              to "QR कोड",
        StringKeys.SCH_PDF                  to "PDF",
        StringKeys.SCH_VERIFY               to "सत्यापित करें",
        StringKeys.SCH_NO_EXPIRY            to "कोई समाप्ति नहीं",
        StringKeys.SCH_EXPIRED              to "समाप्त",
        StringKeys.SCH_EXPIRING             to "समाप्ति हो रही",
        StringKeys.SCH_VALID                to "वैध",
        // Additional keys
        StringKeys.SCH_TEMPLATE_STATUS      to "{role} • {status}",
        StringKeys.SCH_DELETE               to "हटाएं",
        StringKeys.SCH_SAVE                 to "सहेजें",
        StringKeys.SCH_CLASS_SECTION_DASH   to "कक्षा {className}{section}",
        StringKeys.SCH_ACTIVE_LABEL         to "सक्रिय",

        // Phase 3 Batch 1 HI
        StringKeys.SCH_80G_ELIGIBLE_RECEIPT            to "80G योग्य • रसीद: {receipt}",
        StringKeys.SCH_80G_RECEIPT                     to "80G • रसीद: {receipt}",
        StringKeys.SCH_ACHIEVEMENTS                    to "उपलब्धियां",
        StringKeys.SCH_ALUMNI_DETAIL                   to "पूर्व छात्र विवरण",
        StringKeys.SCH_ALUMNI_NOT_FOUND                to "पूर्व छात्र नहीं मिला",
        StringKeys.SCH_APPROVED                        to "स्वीकृत",
        StringKeys.SCH_BATCH_YEAR                      to "बैच {year}",
        StringKeys.SCH_CAMPAIGN_COLON                  to "अभियान: {title}",
        StringKeys.SCH_CAMPAIGN_DETAIL                 to "अभियान विवरण",
        StringKeys.SCH_CAMPAIGN_NOT_FOUND              to "अभियान नहीं मिला",
        StringKeys.SCH_CAREER                          to "कैरियर",
        StringKeys.SCH_CAUSE_COLON                     to "कारण: {cause}",
        StringKeys.SCH_CITY                            to "शहर",
        StringKeys.SCH_COMPANY                         to "कंपनी",
        StringKeys.SCH_CONCURRENCY                     to "सम्मुखता",
        StringKeys.SCH_CURRENT                         to "वर्तमान",
        StringKeys.SCH_CURRENT_TERM                    to "वर्तमान सत्र",
        StringKeys.SCH_DATE_COLON                      to "तिथि: {date}",
        StringKeys.SCH_DONATIONS                       to "दान",
        StringKeys.SCH_DRAFT                           to "ड्राफ्ट",
        StringKeys.SCH_DRAFTS                          to "ड्राफ्ट",
        StringKeys.SCH_EMAIL                           to "ईमेल",
        StringKeys.SCH_ENABLED                         to "सक्षम",
        StringKeys.SCH_EXPERTISE                       to "विशेषज्ञान",
        StringKeys.SCH_FALLBACK                        to "फॉलबैक",
        StringKeys.SCH_FEATURED                        to "★ विशेष",
        StringKeys.SCH_FLAGGED                         to "चिह्नित",
        StringKeys.SCH_FLYWHEEL_COMPLETE               to "फ्लाईव्हील पूर्ण: {count} फोकस क्षेत्र मापे गए",
        StringKeys.SCH_LINKEDIN                        to "लिंक्डइन",
        StringKeys.SCH_MENTOR                          to "रूपरेखक",
        StringKeys.SCH_MENTORSHIP                      to "रूपरेखण",
        StringKeys.SCH_MODE_COLON                      to "मोड: {mode}",
        StringKeys.SCH_NOT_SET                         to "सेट नहीं",
        StringKeys.SCH_NO_CAREER_HISTORY               to "कोई कैरियर वृत्तांतर नहीं",
        StringKeys.SCH_NO_DONATIONS_CAMPAIGN           to "इस अभियान के लिए अभी तक कोई दान नहीं",
        StringKeys.SCH_NO_DONATIONS_RECORDED           to "कोई दान दर्ज नहीं",
        StringKeys.SCH_N_DONORS                        to "{count} दाता",
        StringKeys.SCH_N_IMPROVED                      to "{improved}/{targeted} सुधार",
        StringKeys.SCH_N_REPORTS_PUBLISHED             to "{count} रिपोर्ट सफलतापूर्व प्रकाशित",
        StringKeys.SCH_PENDING                         to "लब्ध",
        StringKeys.SCH_PERIOD_COLON                    to "अवधि: {start} → {end}",
        StringKeys.SCH_PHONE                           to "फ़ोन",
        StringKeys.SCH_PRESENT                         to "वर्तमान",
        StringKeys.SCH_PREVIOUS                        to "पिछला",
        StringKeys.SCH_PRIVACY                         to "गोपनीयता",
        StringKeys.SCH_PROFESSION                      to "वृत्ति",
        StringKeys.SCH_PROFESSIONAL                    to "व्यवसायिक",
        StringKeys.SCH_PROFILE                         to "प्रोफाइल",
        StringKeys.SCH_PROFILE_COMPLETENESS            to "प्रोफाइल पूर्णता",
        StringKeys.SCH_PROGRESS                        to "प्रगति",
        StringKeys.SCH_PUBLISHED                       to "प्रकाशित",
        StringKeys.SCH_PUBLISHING                      to "प्रकाशित हो रहा है…",
        StringKeys.SCH_PUBLISH_N_APPROVED              to "{count} स्वीकृत प्रकाशित करें",
        StringKeys.SCH_REPORTING_EFFECTIVENESS         to "रिपोर्टिंग प्रभावशीलता",
        StringKeys.SCH_REPORT_CARD_PUBLISHING          to "रिपोर्ट कार्ड प्रकाशन",
        StringKeys.SCH_RUN_FLYWHEEL                    to "फ्लाईव्हील मापन चलाएं",
        StringKeys.SCH_RUN_FLYWHEEL_BTN                to "फ्लाईव्हील चलाएं",
        StringKeys.SCH_SHOW_EMAIL                      to "ईमेल दिखाएं",
        StringKeys.SCH_SHOW_PHONE                      to "फ़ोन दिखाएं",
        StringKeys.SCH_SKILLS                          to "कौशल",
        StringKeys.SCH_STATUS_COLON                    to "स्थिति: {status}",
        StringKeys.SCH_TARGET_BATCH_COLON              to "लक्षित बैच: {batch}",
        StringKeys.SCH_TERM                            to "सत्र",
        StringKeys.SCH_VISIBILITY                      to "दृश्यता",

        // Phase 3 Batch 2 HI (A-D)
        StringKeys.SCH_ABSENT                          to "गायब",
        StringKeys.SCH_ACADEMIC_OVERVIEW               to "एकादमिक सारांश",
        StringKeys.SCH_ACADEMIC_SCORE                  to "एकादमिक स्कोर",
        StringKeys.SCH_ADD_SLOT                        to "+ स्लॉट जोड़ें",
        StringKeys.SCH_ADD_STUDENT                     to "छात्र जोड़ें",
        StringKeys.SCH_ADMINISTRATIVE_INFO             to "प्रशासनिक जानकारी",
        StringKeys.SCH_ADMISSION_DATE                  to "प्रवेश तिथि",
        StringKeys.SCH_ADMISSION_NO                    to "प्रवेश संख्या",
        StringKeys.SCH_ADMISSION_NUMBER                to "प्रवेश संख्या",
        StringKeys.SCH_APPLICABLE_DAYS                 to "लागू दिन",
        StringKeys.SCH_APPLICABLE_DAYS_PH              to "1,2,3,4,5 (सोम-शुक्र)",
        StringKeys.SCH_ASSIGNMENTS                     to "असाइनमेंट",
        StringKeys.SCH_ASSIGNMENT_COMPLETION           to "असाइनमेंट पूर्णता",
        StringKeys.SCH_ATTENDANCE                      to "हाजिरी",
        StringKeys.SCH_ATTENDANCE_OVERVIEW             to "हाजिरी सारांश",
        StringKeys.SCH_ATTENDANCE_RATE                 to "हाजिरी दर",
        StringKeys.SCH_AVERAGE                         to "औसत",
        StringKeys.SCH_CLASS                           to "कक्षा",
        StringKeys.SCH_CLASS_LEVEL                     to "कक्षा स्तर",
        StringKeys.SCH_CLASS_LEVEL_PH                  to "ALL / PRIMARY / SECONDARY",
        StringKeys.SCH_CLASS_PH                        to "उदा. ग्रेड 4",
        StringKeys.SCH_COMPLETION                      to "पूर्णता",
        StringKeys.SCH_CONFIGURATIONS                  to "कॉन्फिगरेशन",
        StringKeys.SCH_CONNECTED                       to "जुड़ा",
        StringKeys.SCH_CONTACT_INFORMATION             to "संपर्क जानकारी",
        StringKeys.SCH_CONTACT_PARENT                  to "माता-पिता से संपर्क करें",
        StringKeys.SCH_DANGER_ZONE                     to "खतरनाक क्षेत्र",
        StringKeys.SCH_DAYS_LEVEL                      to "दिन: {days}  ·  स्तर: {level}",
        StringKeys.SCH_DEACTIVATE                      to "निष्क्रिय करें",
        StringKeys.SCH_DEACTIVATE_CONFIG               to "कॉन्फिगरेशन निष्क्रिय करें?",
        StringKeys.SCH_DEACTIVATE_CONFIG_MSG           to "यह स्कूल दिन कॉन्फिगरेशन को निष्क्रिय कर देगा। आप इसे बाद में पुनः सक्रिय कर सकते हैं।",
        StringKeys.SCH_DUE                             to "देय {date}",

        // Phase 3 Batch 2 HI (E-N)
        StringKeys.SCH_EDIT_DAY_CONFIG                 to "डे कॉन्फिगरेशन संपादित करें",
        StringKeys.SCH_END                             to "अंत",
        StringKeys.SCH_EXPERIENCE                      to "अनुभव",
        StringKeys.SCH_FEES                            to "फीस",
        StringKeys.SCH_FORMAT_DAYS                     to "प्रारूप: कॉमा-सेपरेटेड 1-7",
        StringKeys.SCH_FULL_NAME                       to "पूरा नाम",
        StringKeys.SCH_FULL_NAME_PH                    to "उदा. आरव शर्मा",
        StringKeys.SCH_HEALTH_RECORDS                  to "स्वास्थ्य रिकॉर्ड्स",
        StringKeys.SCH_HEALTH_RECORDS_DESC             to "स्वास्थ्य प्रोफाइल, टीकाकरण और घटनाओं को देखें और प्रबंधित करें",
        StringKeys.SCH_JOINED                          to "जॉइन किया",
        StringKeys.SCH_JOINED_DATE                     to "जॉइन तिथि",
        StringKeys.SCH_LABEL                           to "लेबल",
        StringKeys.SCH_LABEL_PH                        to "उदा. पीरियड 1",
        StringKeys.SCH_LATE                            to "देर से",
        StringKeys.SCH_LEAVE                           to "छुट्टी",
        StringKeys.SCH_LINKED                          to "लिंक",
        StringKeys.SCH_LOW_ATTENDANCE                  to "कम हाजिरी",
        StringKeys.SCH_MANAGE_CLASSES_SUBJECTS         to "कक्षाएँ, विषय और सेक्शन प्रबंधित करें",
        StringKeys.SCH_MARKS                           to "अंक",
        StringKeys.SCH_MUST_BE_LEVEL                   to "होना चाहिए: ALL, PRIMARY, या SECONDARY",
        StringKeys.SCH_NAME_PH                         to "उदा. डिफ़ॉल्ट वीकडे",
        StringKeys.SCH_NEW_ADMISSION                   to "नया प्रवेश",
        StringKeys.SCH_NEW_DAY_CONFIG                  to "नया डे कॉन्फिगरेशन",
        StringKeys.SCH_NO_ACHIEVEMENTS                 to "अभी तक कोई उपलब्धियाँ नहीं।",
        StringKeys.SCH_NO_ASSIGNMENTS_YET              to "अभी तक कोई कक्षा या विषय असाइनमेंट नहीं।",
        StringKeys.SCH_NO_DAY_CONFIGS                  to "अभी तक कोई डे कॉन्फिगरेशन नहीं",
        StringKeys.SCH_NO_DAY_CONFIGS_DESC             to "बेल शेड्यूल परिभाषित करने के लिए अपना पहला स्कूल दिन कॉन्फिगरेशन बनाएँ।",
        StringKeys.SCH_NO_FEE_RECORDS                  to "कोई फीस रिकॉर्ड नहीं।",
        StringKeys.SCH_NO_INSIGHTS_YET                 to "अभी तक कोई इनसाइट उपलब्ध नहीं।",
        StringKeys.SCH_NO_LEAVE_APPLICATIONS           to "कोई छुट्टी आवेदन नहीं।",
        StringKeys.SCH_NO_MARKS_RECORDED               to "अभी तक कोई अंक दर्ज नहीं हुए।",
        StringKeys.SCH_NO_PARENTS_LINKED               to "अभी तक कोई माता-पिता लिंक नहीं।",
        StringKeys.SCH_NO_RECENT_ACTIVITY              to "अभी तक कोई हालिया गतिविधि नहीं।",
        StringKeys.SCH_NO_STUDENTS_YET                 to "अभी तक कोई छात्र नहीं",
        StringKeys.SCH_NO_STUDENTS_YET_DESC            to "अपना पहला छात्र जोड़ें ताकि वे हाजिरी, अंक और विश्लेषण में दिखें।",
        StringKeys.SCH_NO_STUDENT_PROFILE_DESC         to "इस छात्र का रिकॉर्ड नहीं मिला जा सकता।",
        StringKeys.SCH_NO_TEACHERS_CONNECTED           to "अभी तक कोई शिक्षक जुड़ा नहीं।",
        StringKeys.SCH_N_STUDENTS                      to "{count} छात्र",
        StringKeys.SCH_N_YEARS                         to "{count} वर्ष",

        // Phase 3 Batch 2 HI (O-Z)
        StringKeys.SCH_OVERALL                         to "कुल",
        StringKeys.SCH_PARENTS                         to "माता-पिता",
        StringKeys.SCH_PARENT_CONNECTIONS              to "माता-पिता कनेक्शन",
        StringKeys.SCH_PARENT_PHONE_OPTIONAL           to "माता-पिता/अभिभावक फ़ोन (वैकल्पिक)",
        StringKeys.SCH_PARENT_PHONE_PH                 to "उदा. 98765 43210",
        StringKeys.SCH_PARENT_SATISFACTION             to "माता-पिता संतोष",
        StringKeys.SCH_PERFORMANCE                     to "प्रदर्शन",
        StringKeys.SCH_PERSONAL                        to "व्यक्तिगत",
        StringKeys.SCH_PHONE_MIN_DIGITS                to "फ़ोन में कम से कम 10 अंक होने चाहिए।",
        StringKeys.SCH_PRIMARY_GUARDIAN                to "प्रमुख अभिभावक",
        StringKeys.SCH_PROFESSIONAL_DETAILS            to "व्यवसायिक विवरण",
        StringKeys.SCH_QUICK_ACTIONS                   to "त्वरित क्रियाएँ",
        StringKeys.SCH_RECENT_ACTIVITY                 to "हालिया गतिविधि",
        StringKeys.SCH_REMOVE_STUDENT                  to "छात्र हटाएँ",
        StringKeys.SCH_REMOVE_STUDENT_DANGER           to "इस छात्र को हटाने से उनके रिकॉर्ड आपके स्कूल से छिप जाएँगे। इसे पुनः जोड़कर वापस लाया जा सकता है।",
        StringKeys.SCH_REMOVE_STUDENT_MSG              to "अपने स्कूल से {name} को हटाएँ? उनके रिकॉर्ड छिप जाएँगे। इसे पुनः जोड़कर वापस लाया जा सकता है।",
        StringKeys.SCH_REMOVE_STUDENT_ROSTER_MSG       to "रोस्टर से {name} को हटाएँ? वे अब हाजिरी या विश्लेषण में नहीं दिखेंगे। इसे पुनः जोड़कर वापस लाया जा सकता है।",
        StringKeys.SCH_REMOVE_TEACHER                  to "शिक्षक हटाएँ",
        StringKeys.SCH_REMOVE_TEACHER_DANGER           to "इस शिक्षक को हटाने से उनकी पहुँच तुरंत रद्द हो जाएगी। इसे पुनः जोड़कर वापस लाया जा सकता है।",
        StringKeys.SCH_REMOVE_TEACHER_MSG              to "अपने स्कूल से {name} को हटाएँ? उनकी पहुँच तुरंत रद्द हो जाएगी। इसे पुनः जोड़कर वापस लाया जा सकता है।",
        StringKeys.SCH_ROLL_NO                         to "रोल नंबर",
        StringKeys.SCH_ROLL_NUMBER                     to "रोल नंबर",
        StringKeys.SCH_ROLL_NUMBER_PH                  to "उदा. 12",
        StringKeys.SCH_SAVING                          to "सहेज रहा है…",
        StringKeys.SCH_SCHOOL_DAY_CONFIG               to "स्कूल दिन कॉन्फिगरेशन",
        StringKeys.SCH_SEC                             to "सेक्शन {section}",
        StringKeys.SCH_SECTION                         to "सेक्शन",
        StringKeys.SCH_SECTIONS_TAUGHT                 to "पढ़ाई गई सेक्शन",
        StringKeys.SCH_SLOTS_N                         to "स्लॉट ({count})",
        StringKeys.SCH_START                           to "प्रारंभ",
        StringKeys.SCH_STUDENT                         to "छात्र",
        StringKeys.SCH_STUDENT_ID                      to "छात्र आईडी",
        StringKeys.SCH_STUDIED                         to "पढ़ा",
        StringKeys.SCH_TEACHER_CONNECTIONS             to "शिक्षक कनेक्शन",
        StringKeys.SCH_TEACHING_PORTFOLIO              to "शिक्षण पोर्टफोलियो",
        StringKeys.SCH_THIS_STUDENT                    to "इस छात्र",
        StringKeys.SCH_THIS_TEACHER                    to "इस शिक्षक",
        StringKeys.SCH_VIEW_PROFILE                    to "प्रोफाइल देखें",
        // ParentActivityScreenV2 (extras)
        StringKeys.PAC_ANNOUNCEMENTS      to "घोषणाएं",
        StringKeys.PAC_LOAD_ERROR         to "घोषणाएं लोड नहीं हो सकीं",
        StringKeys.PAC_ALL_CAUGHT_UP      to "सब ठीक है",
        StringKeys.PAC_ALL_CAUGHT_UP_DESC to "आपके स्कूल की नई घोषणाएं यहां दिखाई देंगी।",
        // ParentPewsScreenV2 (extras)
        StringKeys.PPEWS_ALL_GOOD         to "सब अच्छा है!",
        StringKeys.PPEWS_ALL_GOOD_BODY    to "{name} के लिए अभी कोई विशेष चिंता नहीं है। बढ़िया सहयोग जारी रखें!",
        StringKeys.PPEWS_ALL_ON_TRACK     to "सब ठीक है",
        StringKeys.PPEWS_ALL_ON_TRACK_BODY to "{name} अच्छा कर रहा/रही है। इस समय कोई विशेष चिंता नहीं है।",
        // ParentFeePaymentScreenV2
        StringKeys.PFP_PAY_FEES           to "फीस भुगतान करें",
        StringKeys.PFP_OUTSTANDING        to "बकाया राशि",
        StringKeys.PFP_OVERDUE_HEADS      to "{count} अतिदेय फीस शीर्ष",
        StringKeys.PFP_PAYMENT_METHOD     to "भुगतान विधि",
        StringKeys.PFP_ONLINE_PAYMENT     to "ऑनलाइन भुगतान",
        StringKeys.PFP_SECURE_GATEWAY     to "सुरक्षित Razorpay गेटवे",
        StringKeys.PFP_PAY_AMOUNT         to "भुगतान करें {amount}",
        StringKeys.PFP_NO_FEES_DUE        to "कोई फीस बाकी नहीं",
        // ParentFeeHistoryScreenV2
        StringKeys.PFH_FEE_HISTORY        to "फीस इतिहास",
        StringKeys.PFH_TOTAL_COLLECTED    to "कुल वसूल",
        StringKeys.PFH_NO_HISTORY         to "कोई भुगतान इतिहास नहीं",
        StringKeys.PFH_NO_HISTORY_DESC    to "जब आप फीस जमा करेंगे, रसीदें यहां दिखेंगी।",
        // ParentEventRegistrationScreenV2 (extras)
        StringKeys.PE_EVENTS              to "कार्यक्रम",
        StringKeys.PE_EVENT_DETAIL        to "कार्यक्रम विवरण",
        StringKeys.PE_UPCOMING            to "आगामी कार्यक्रम",
        StringKeys.PE_MY_REGS             to "मेरे पंजीकरण",
        StringKeys.PE_NO_EVENTS           to "पंजीकरण के साथ कोई आगामी कार्यक्रम नहीं",
        StringKeys.PE_VENUE              to "स्थान: {venue}",
        StringKeys.PE_REGISTER_BY        to "पंजीकरण अंतिम तिथि: {date}",
        StringKeys.PE_REGISTERED_STATUS  to "पंजीकृत: {status}",
        StringKeys.PE_REG_OPEN           to "पंजीकरण खुला है",
        StringKeys.PE_CONFLICTS          to "इससे टकराता है: {title}",
        StringKeys.PE_SELECT_SLOT        to "समय स्लॉट चुनें",
        StringKeys.PE_RESCHEDULE         to "पुनर्निर्धारण",
        StringKeys.PE_NUM_ATTENDEES      to "उपस्थित लोगों की संख्या",
        StringKeys.PE_REGISTER           to "पंजीकरण करें",
        StringKeys.PE_FULL               to "भरा हुआ",
        StringKeys.PE_YOUR_SLOT          to "आपका स्लॉट",
        StringKeys.PE_SELECTED           to "चुना गया",
        StringKeys.PE_NO_REGS            to "अभी कोई पंजीकरण नहीं",
        StringKeys.PE_SLOT_LABEL         to "स्लॉट: {start} - {end}",
        StringKeys.PE_BOOKED             to "{booked}/{capacity} बुक्ड",
        // ParentHomeworkScreenV2
        StringKeys.PHW_HOMEWORK           to "होमवर्क",
        StringKeys.PHW_NO_ACTIVE          to "कोई सक्रिय होमवर्क नहीं",
        StringKeys.PHW_NO_ACTIVE_DESC     to "आपके बच्चे के लिए अभी कोई लंबित होमवर्क नहीं है।",
        StringKeys.PHW_GRADED            to "ग्रेड किया गया",
        StringKeys.PHW_SUBMITTED         to "जमा किया गया",
        StringKeys.PHW_LATE              to "देर से",
        StringKeys.PHW_PENDING           to "लंबित",
        StringKeys.PHW_TAP_TO_VIEW       to "जमाई देखने या अपडेट करने के लिए टैप करें",
        StringKeys.PHW_INSTRUCTIONS      to "निर्देश",
        StringKeys.PHW_WRITTEN_ANSWER    to "लिखित उत्तर / नोट्स",
        StringKeys.PHW_ANSWER_PH         to "अपने बच्चे का उत्तर यहां टाइप करें...",
        StringKeys.PHW_PHOTO_ATTACH      to "फोटो अटैचमेंट",
        StringKeys.PHW_UPLOADING         to "अपलोड हो रहा है...",
        StringKeys.PHW_ADD_PHOTO         to "फोटो जोड़ें",
        StringKeys.PHW_SUBMIT_SUCCESS    to "होमवर्क सफलतापूर्वक जमा हो गया!",
        StringKeys.PHW_SUBMITTING        to "जमा हो रहा है...",
        StringKeys.PHW_SUBMIT            to "होमवर्क जमा करें",
        StringKeys.PHW_ATTACHMENT        to "अटैचमेंट",

    )

    private fun hiPart2(): Map<String, String> = mapOf(
        // Phase 4 - Teacher screen HI translations
        StringKeys.ATT_LEAVE                                to "अवकाश",
        StringKeys.COMMON_BUTTON_CREATE                     to "बनाएं",
        StringKeys.COMMON_BUTTON_TRY_AGAIN                  to "पुनः प्रयास करें",
        StringKeys.TC_A                                     to "A",
        StringKeys.TC_ABSENT                                to "अनुपस्थित",
        StringKeys.TC_ACTIVE_HOMEWORK                       to "ACTIVE HOMEWORK",
        StringKeys.TC_ACTIVITIES                            to "Activities",
        StringKeys.TC_ADD                                   to "जोड़ें",
        StringKeys.TC_ADD_ACTIVITY                          to "Add activity…",
        StringKeys.TC_ADD_A_CHAPTER                         to "Add a chapter",
        StringKeys.TC_ADD_MANUALLY                          to "Add manually",
        StringKeys.TC_ADD_NEW_QUESTION                      to "Add New Question",
        StringKeys.TC_ADD_OBJECTIVE                         to "Add objective…",
        StringKeys.TC_ADD_QUESTION                          to "+ Add Question",
        StringKeys.TC_ADD_RESOURCE                          to "Add resource…",
        StringKeys.TC_ADD_TOPIC                             to "Add topic",
        StringKeys.TC_ADMIN                                 to "व्यवस्थापक",
        StringKeys.TC_ADMIN_NOTE_COLON                      to "Admin note: {note}",
        StringKeys.TC_AHEAD_OF_SCHEDULE                     to "Ahead of schedule",
        StringKeys.TC_AI_EXTRACT_CHAPTERS_TOPICS            to "AI will extract chapters and topics from pasted text",
        StringKeys.TC_AI_NARRATIVE_EDITABLE                 to "AI-generated narrative (editable)",
        StringKeys.TC_ALL                                   to "सभी",
        StringKeys.TC_ALLERGIES_LABEL                       to "Allergies",
        StringKeys.TC_ALLOW_LATE                            to "Allow late submissions",
        StringKeys.TC_ALL_ATTENDANCE_DONE                   to "All attendance done",
        StringKeys.TC_ALL_CAUGHT_UP                         to "All caught up",
        StringKeys.TC_ALL_CAUGHT_UP_DAY                     to "You're all caught up — have a great day.",
        StringKeys.TC_ALL_CLASSES                           to "All classes",
        StringKeys.TC_ANSWER_COLON                          to "Answer: {answer}",
        StringKeys.TC_APPLY                                 to "लागू करें",
        StringKeys.TC_APPROVE                               to "स्वीकृत करें",
        StringKeys.TC_APPROVE_ALL                           to "सभी स्वीकृत करें",
        StringKeys.TC_ASSESSMENT_METHOD                     to "Assessment method",
        StringKeys.TC_ASSIGN_FIRST_HOMEWORK                 to "Assign your first homework for this class.",
        StringKeys.TC_ASSIGN_HOMEWORK                       to "Assign homework",
        StringKeys.TC_ATTENDANCE                            to "ATTENDANCE TODAY",
        StringKeys.TC_ATTENDANCE_DONE                       to "Attendance done",
        StringKeys.TC_ATTENDANCE_TODAY                      to "ATTENDANCE TODAY",
        StringKeys.TC_AT_LEAST_                             to "At least 8 characters",
        StringKeys.TC_AUTO_FILL                             to "Auto-fill",
        StringKeys.TC_AUTO_FILL_FROM_NCERT                  to "Auto-fill from NCERT",
        StringKeys.TC_AUTO_FILL_PREVIEW                     to "Preview: {chapters} chapters, {topics} topics{subtopics} — {units} units will be created as DRAFT for your review.",
        StringKeys.TC_AVG                                   to "avg",
        StringKeys.TC_AVG_N_PCT_PER_CLASS                   to "Avg {pct}%/class",
        StringKeys.TC_BEHIND_SCHEDULE                       to "Behind schedule",
        StringKeys.TC_BELL_SCHEDULE                         to "BELL SCHEDULE",
        StringKeys.TC_CALENDAR                              to "Calendar",
        StringKeys.TC_CHANGE                                to "CHANGE",
        StringKeys.TC_CHANGE_PASSWORD                       to "Change your sign-in password",
        StringKeys.TC_CHANGE_REASON_PH                      to "e.g. Room conflict, schedule swap...",
        StringKeys.TC_CHANGE_REQUEST                        to "Change Request",
        StringKeys.TC_CHANGE_REQUESTS                       to "Change Requests",
        StringKeys.TC_CHANGE_REQUESTS_APPEAR                to "Your timetable change requests will appear here.",
        StringKeys.TC_CHAPTER_TITLE                         to "Chapter title",
        StringKeys.TC_CHECKED_IN                            to "Checked in",
        StringKeys.TC_CHECK_IN                              to "Check in",
        StringKeys.TC_CHOOSE_HOW_TO_BUILD_SYLLABUS          to "Choose how to build your syllabus:",
        StringKeys.TC_CLASS                                 to "कक्षा",
        StringKeys.TC_CLASSES                               to "कक्षाएँ",
        StringKeys.TC_CLASSES_MARKED                        to "{count} of {total} classes marked",
        StringKeys.TC_CLASSES_ON_TRACK                      to "Your classes are on track",
        StringKeys.TC_CLASSES_TO_MARK                       to "{count} classes to mark",
        StringKeys.TC_CLASSES_YOU_TEACH                     to "Classes you teach",
        StringKeys.TC_CLASS_CANCELLED                       to "{className} · {time} (cancelled)",
        StringKeys.TC_CLASS_CANCELLED_DATE                  to "{className} (cancelled)",
        StringKeys.TC_CLASS_SUBJECT                         to "Class / Subject",
        StringKeys.TC_CLASS_TEACHER                         to "Class teacher",
        StringKeys.TC_CLOSE_HOMEWORK_DESC                   to "Closing archives the homework. Students can no longer submit.",
        StringKeys.TC_CLOSE_HOMEWORK_Q                      to "Close this homework?",
        StringKeys.TC_CLOSE_IT                              to "बंद करें",
        StringKeys.TC_COMPLETE                              to "पूर्ण",
        StringKeys.TC_COMPLETED                             to "पूर्ण",
        StringKeys.TC_CONDITIONS_LABEL                      to "Conditions",
        StringKeys.TC_CONFIRM_AND_CREATE                    to "Confirm & Create",
        StringKeys.TC_CONFIRM_NEW_PASSWORD                  to "Confirm new password",
        StringKeys.TC_CORRECT_ANSWER                        to "Correct answer:",
        StringKeys.TC_CORRECT_ANSWER_EG_AB                  to "Correct answer (e.g. A, B)",
        StringKeys.TC_CORRECT_ANSWER_TEXT                   to "Correct answer text",
        StringKeys.TC_COULDNT_LOAD_ATTENDANCE               to "Couldn't load attendance",
        StringKeys.TC_COULDNT_LOAD_BOARD                    to "Couldn't load the homework board",
        StringKeys.TC_COULDNT_LOAD_CLASS                    to "Couldn't load this class",
        StringKeys.TC_COULDNT_LOAD_CLASSES                  to "Couldn't load your classes",
        StringKeys.TC_COULDNT_LOAD_HOMEWORK                 to "Couldn't load homework",
        StringKeys.TC_COULDNT_LOAD_LESSON_PLANS             to "Couldn't load lesson plans",
        StringKeys.TC_COULDNT_LOAD_PROFILE                  to "Couldn't load profile",
        StringKeys.TC_COULDNT_LOAD_ROSTER                   to "Couldn't load the roster",
        StringKeys.TC_COULDNT_LOAD_SCHEDULE                 to "Couldn't load your schedule.",
        StringKeys.TC_COULDNT_LOAD_SYLLABUS                 to "Couldn't load syllabus",
        StringKeys.TC_COULDNT_LOAD_TEMPLATES                to "Couldn't load templates",
        StringKeys.TC_COULDNT_LOAD_TESTS                    to "Couldn't load tests",
        StringKeys.TC_COVERAGE_N_PCT                        to "Coverage: {pct}%",
        StringKeys.TC_COVERED_DATE                          to "Covered {date}",
        StringKeys.TC_CREATE_AS_DRAFT                       to "Create as Draft",
        StringKeys.TC_CREATE_A_TEST                         to "Create a test",
        StringKeys.TC_CREATE_CHAPTERS_TOPICS_ONE_BY_ONE     to "Create chapters and topics one by one",
        StringKeys.TC_CREATE_FIRST_LESSON_PLAN              to "Create your first lesson plan for this class.",
        StringKeys.TC_CREATE_FIRST_TEST                     to "Create your first test for this class.",
        StringKeys.TC_CREATE_PLAN                           to "Create plan",
        StringKeys.TC_CREATE_TEST                           to "Create test",
        StringKeys.TC_CRITICALLY_BEHIND                     to "Critically behind",
        StringKeys.TC_CURRENT_PASSWORD                      to "Current password",
        StringKeys.TC_CURRICULUM_UNIT_OPTIONAL              to "Curriculum unit (optional)",
        StringKeys.TC_DAILY_CLASS_LOG                       to "Daily Class Log",
        StringKeys.TC_DAILY_LOG                             to "Daily Log",
        StringKeys.TC_DAY                                   to "दिन",
        StringKeys.TC_DAY_AT_A_GLANCE                       to "Here's your day at a glance.",
        StringKeys.TC_DECLINING                             to "Declining",
        StringKeys.TC_DETAILS_OPTIONAL                      to "Details (optional)",
        StringKeys.TC_DIFFICULTY                            to "Difficulty",
        StringKeys.TC_DIGITAL_ID_CARD                       to "Digital ID Card",
        StringKeys.TC_DIGITAL_ID_CARD_DESC                  to "View student digital ID cards",
        StringKeys.TC_DISMISS                               to "Dismiss",
        StringKeys.TC_DONE                                  to "पूर्ण",
        StringKeys.TC_DRAFT                                 to "ड्राफ्ट",
        StringKeys.TC_DRAFT_PARENT_MESSAGE                  to "Draft parent message",
        StringKeys.TC_DRAFT_UNITS_NOT_VISIBLE_TO_PARENTS    to "Draft units are not visible to parents until approved.",
        StringKeys.TC_DUE_DATE                              to "Due date",
        StringKeys.TC_DUE_LABEL                             to "Due {date}",
        StringKeys.TC_DUE_PAST_TURNED_IN                    to "Due {date}{pastDue} · {turnedIn}/{total} turned in",
        StringKeys.TC_EASY                                  to "आसान",
        StringKeys.TC_EDIT                                  to "संपादित करें",
        StringKeys.TC_EDITING_QUESTION                      to "Editing Question",
        StringKeys.TC_EDIT_DRAFT                            to "Edit Draft",
        StringKeys.TC_EDIT_LESSON_PLAN                      to "EDIT LESSON PLAN",
        StringKeys.TC_END                                   to "End",
        StringKeys.TC_ENTERED_N_OF_N                        to "Entered {entered} of {total}",
        StringKeys.TC_ESCALATED                             to "Escalated",
        StringKeys.TC_EST_COMPLETION_DATE                   to "Est. completion: {date}",
        StringKeys.TC_EXAM_DATE                             to "Exam date",
        StringKeys.TC_EXPLANATION_COLON                     to "Explanation: {explanation}",
        StringKeys.TC_EXPLANATION_OPTIONAL                  to "Explanation (optional)",
        StringKeys.TC_EXTEND                                to "Extend",
        StringKeys.TC_EXTENDED_TO                           to "Extended to {date}",
        StringKeys.TC_EXTEND_FOR                            to "Extend for",
        StringKeys.TC_EXTEND_FOR_CLASS                      to "Extend for class",
        StringKeys.TC_EXTEND_WHOLE_CLASS                    to "Extend for the whole class",
        StringKeys.TC_FAILING_TREND                         to "Failing",
        StringKeys.TC_FALSE                                 to "गलत",
        StringKeys.TC_FETCHING_NCERT_REFERENCE              to "Fetching NCERT reference…",
        StringKeys.TC_FETCH_STANDARD_NCERT_SYLLABUS         to "Fetch the standard CBSE/NCERT syllabus for this class & subject",
        StringKeys.TC_FILL_UPS                              to "Fill-ups",
        StringKeys.TC_FLAGGED                               to "Flagged",
        StringKeys.TC_FLAGS                                 to "Flags",
        StringKeys.TC_FROM                                  to "से",
        StringKeys.TC_GENERATE_QUIZ                         to "Generate Quiz",
        StringKeys.TC_GRADED                                to "Graded",
        StringKeys.TC_GRANT                                 to "Grant",
        StringKeys.TC_GRANT_EXTENSION                       to "Grant extension",
        StringKeys.TC_GROUNDING_FLAGS_DETECTED              to "Grounding flags detected",
        StringKeys.TC_HARD                                  to "कठिन",
        StringKeys.TC_HEALTH_ALERTS                         to "Health Alerts",
        StringKeys.TC_HEALTH_ALERTS_DESC                    to "Students with health conditions in your classes",
        StringKeys.TC_HEALTH_ALERTS_LIST_DESC               to "Students with health conditions in your assigned classes",
        StringKeys.TC_HI_NAME                               to "Hi, {name}",
        StringKeys.TC_HOLIDAY                               to "अवकाश",
        StringKeys.TC_HOLIDAY_NOTICE                        to "Holiday",
        StringKeys.TC_HOW_ASSESS_OPTIONAL                   to "How will you assess? (optional)",
        StringKeys.TC_IMPORT_MARKS                          to "Import Marks (OCR / Text)",
        StringKeys.TC_IMPROVING                             to "Improving",
        StringKeys.TC_INITIATED_BY                          to "Initiated by",
        StringKeys.TC_INSTANTIATE_FROM_TEMPLATE             to "Use this template",
        StringKeys.TC_INSTRUCTIONS_PH                       to "Instructions for students",
        StringKeys.TC_LANGUAGE                              to "Appearance",
        StringKeys.TC_LANGUAGE_COLON                        to "Language: {lang}",
        StringKeys.TC_LAST_MARKED_BY                        to "Last marked by {name}",
        StringKeys.TC_LEAVES                                to "Leaves",
        StringKeys.TC_LEAVE_REQUESTS                        to "छुट्टी के अनुरोध",
        StringKeys.TC_LESSON                                to "Lesson Plan",
        StringKeys.TC_LESSON_PLANS                          to "LESSON PLANS",
        StringKeys.TC_LESSON_TITLE                          to "Lesson title",
        StringKeys.TC_LINK_HOMEWORK_OPTIONAL                to "Link homework (optional)",
        StringKeys.TC_LOADING_LEADERBOARD                   to "Loading leaderboard...",
        StringKeys.TC_LOCKED_UNTIL_EXAM                     to "Locked until exam",
        StringKeys.TC_LOG_OUT                               to "Log out",
        StringKeys.TC_LOG_OUT_DESC                          to "You'll need to sign in again to access your classes.",
        StringKeys.TC_LOG_OUT_Q                             to "Log out?",
        StringKeys.TC_LOW_ATTENDANCE                        to "Low attendance",
        StringKeys.TC_MARK                                  to "MARK",
        StringKeys.TC_MARKED                                to "Marked",
        StringKeys.TC_MARKING_ATTENDANCE                    to "Marking attendance…",
        StringKeys.TC_MARKS                                 to "अंक",
        StringKeys.TC_MARKS_DROPPING                        to "Marks dropping",
        StringKeys.TC_MARKS_PENDING                         to "MARKS PENDING",
        StringKeys.TC_MARK_ALL_PRESENT                      to "Mark all present",
        StringKeys.TC_MARK_ATTENDANCE                       to "Mark attendance",
        StringKeys.TC_MARK_GRADED                           to "Mark graded",
        StringKeys.TC_MARK_IMPROVED                         to "Mark improved",
        StringKeys.TC_MATCH                                 to "Match",
        StringKeys.TC_MAX_MARKS                             to "Max marks",
        StringKeys.TC_MAX_N                                 to "Max {n}",
        StringKeys.TC_MAX_N_ENTERED_N_OF_N                  to "Max {max} · Entered {entered} of {total}",
        StringKeys.TC_MCQ                                   to "MCQ",
        StringKeys.TC_MEDIUM                                to "मध्यम",
        StringKeys.TC_MESSAGES                              to "Messages",
        StringKeys.TC_MESSAGES_DESC                         to "Send and receive messages from parents",
        StringKeys.TC_MIN                                   to "min",
        StringKeys.TC_MINUTES                               to "Minutes",
        StringKeys.TC_MY_LEAVE                              to "My leave",
        StringKeys.TC_NCERT_AUTO_FILL                       to "NCERT Auto-fill",
        StringKeys.TC_NEEDS_ATTENTION                       to "Needs Attention",
        StringKeys.TC_NEEDS_ATTENTION_DESC                  to "Students who need your attention",
        StringKeys.TC_NEW_CHAPTER                           to "New chapter",
        StringKeys.TC_NEW_DUE_DATE                          to "New due date",
        StringKeys.TC_NEW_HOMEWORK                          to "New homework",
        StringKeys.TC_NEW_LESSON_PLAN                       to "NEW LESSON PLAN",
        StringKeys.TC_NEW_PASSWORD                          to "New password",
        StringKeys.TC_NEW_PLAN                              to "New plan",
        StringKeys.TC_NEW_TEST                              to "New test",
        StringKeys.TC_NEW_TOPIC                             to "New topic",
        StringKeys.TC_NEXT                                  to "अगला",
        StringKeys.TC_NEXT_CLASS                            to "NEXT CLASS",
        StringKeys.TC_LATER                                 to "बाद में",
        StringKeys.TC_NOTHING_PENDING                       to "Nothing pending right now.",
        StringKeys.TC_NOT_ENOUGH_DATA                       to "Not enough data",
        StringKeys.TC_NOT_MARKED                            to "NOT MARKED",
        StringKeys.TC_NOT_SUBMITTED                         to "Not submitted",
        StringKeys.TC_NOT_YOUR_STUDENT                      to "Not your student",
        StringKeys.TC_NOT_YOUR_STUDENT_DESC                 to "This student is not in your assigned classes.",
        StringKeys.TC_NOW                                   to "अभी",
        StringKeys.TC_NOW_TEACHING                          to "अभी पढ़ा रहे हैं",
        StringKeys.TC_QUICK_ACTIONS                         to "त्वरित क्रियाएँ",
        StringKeys.TC_PENDING_ACTIONS                       to "लंबित कार्य",
        StringKeys.TC_UPCOMING_EVENTS                         to "आगामी कार्यक्रम",
        StringKeys.TC_YOUR_DAY                              to "आपका दिन",
        StringKeys.TC_LETS                                  to "चलिए",
        StringKeys.TC_UPDATE_ACCENT                         to "अपडेट करें",
        StringKeys.TC_UPDATE_BLURB_ATTENDANCE               to "कुछ ही टैप में हाज़िरी लगाएँ और रजिस्टर अपडेट रखें।",
        StringKeys.TC_UPDATE_BLURB_MARKS                    to "किसी कक्षा के लिए विषयवार अंक और ग्रेड दर्ज करें।",
        StringKeys.TC_UPDATE_BLURB_HOMEWORK                 to "गृहकार्य दें और नियत तिथि तय करें ताकि छात्र जानें कि आगे क्या है।",
        StringKeys.TC_UPDATE_BLURB_SYLLABUS                 to "पाठ्यक्रम की प्रगति देखें और पढ़ाए गए विषयों को चिह्नित करें।",
        StringKeys.TC_UPDATE_BLURB_LESSON                   to "अपने पाठों की योजना बनाएँ और हर पीरियड की रूपरेखा तय करें।",
        StringKeys.TC_YOUR                                  to "आपका",
        StringKeys.TC_WEEK_ACCENT                           to "सप्ताह",
        StringKeys.TC_SCHEDULE_TAB                          to "समय-सारणी",
        StringKeys.TC_REQUESTS_TAB                          to "अनुरोध",
        StringKeys.TC_ACCOUNT_ACCENT                        to "खाता",
        StringKeys.TC_CLASSES_ACCENT                        to "कक्षाएँ",
        StringKeys.TC_AT_RISK                               to "जोखिम में",
        StringKeys.TC_SEC_TIME_OFF                          to "छुट्टी",
        StringKeys.TC_SEC_SECURITY                          to "सुरक्षा",
        StringKeys.TC_SEC_PREFERENCES                       to "प्राथमिकताएँ",
        StringKeys.TC_APPEARANCE                            to "रूप-रंग",
        StringKeys.TC_STAT_SUBJECTS                         to "विषय",
        StringKeys.TC_STAT_CLASSES                          to "कक्षाएँ",
        StringKeys.TC_VIEW_PROFILE_DETAILS                  to "पूरा विवरण देखें",
        StringKeys.TC_NO_PERIOD_RIGHT_NOW                   to "अभी कोई कक्षा नहीं",
        StringKeys.TC_NO_ACTIVE_HOMEWORK                    to "No active homework",
        StringKeys.TC_NO_ACTIVE_HOMEWORK_CLASS              to "No active homework for this class",
        StringKeys.TC_NO_ALLOCATIONS                        to "No allocations yet",
        StringKeys.TC_ASSIGNMENTS_WILL_APPEAR               to "आवंटन होने पर आपकी कक्षा असाइनमेंट यहाँ दिखाई देंगे।",
        StringKeys.TC_NO_ASSIGNMENTS_FOUND                  to "No assignments found.",
        StringKeys.TC_NO_ATTEMPTS_YET                       to "No attempts yet",
        StringKeys.TC_NO_ATTENDANCE_DATA                    to "No attendance data",
        StringKeys.TC_NO_CHANGE                             to "No change",
        StringKeys.TC_NO_CHANGE_REQUESTS                    to "No change requests",
        StringKeys.TC_NO_CLASSES_MATCH                      to "No classes match",
        StringKeys.TC_NO_CLASSES_SCHEDULED_TODAY            to "No classes scheduled today.",
        StringKeys.TC_NO_CLASSES_TODAY                      to "No classes today",
        StringKeys.TC_NO_DATE                               to "No date",
        StringKeys.TC_NO_DRAFTS_FOUND                       to "No drafts found",
        StringKeys.TC_NO_HEALTH_ALERTS                      to "No health alerts",
        StringKeys.TC_NO_HEALTH_ALERTS_DESC                 to "No student in your assigned classes has a health condition.",
        StringKeys.TC_NO_HOMEWORK_LINKED                    to "No active homework for this class",
        StringKeys.TC_NO_LEAVE_REQUESTS                     to "No leave requests yet.",
        StringKeys.TC_NO_LESSON_PLANS_YET                   to "No lesson plans yet",
        StringKeys.TC_NO_NCERT_REFERENCE_FOUND              to "No NCERT reference found",
        StringKeys.TC_NO_PERIODS_FOR_DAY                    to "No periods for {day}",
        StringKeys.TC_NO_PERIODS_TODAY                      to "No periods scheduled today.",
        StringKeys.TC_NO_PLANS_THIS_MONTH                   to "No plans this month",
        StringKeys.TC_NO_STUDENTS_ENROLLED                  to "No students enrolled yet.",
        StringKeys.TC_NO_STUDENTS_NEED_ATTENTION            to "No student in your assigned classes needs attention right now.",
        StringKeys.TC_NO_SYLLABUS_UNITS                     to "No syllabus units available",
        StringKeys.TC_NO_TEMPLATES_YET                      to "No templates yet",
        StringKeys.TC_NO_TESTS_YET                          to "No tests yet",
        StringKeys.TC_NO_UNITS_YET                          to "No units yet",
        StringKeys.TC_NO_UNIT_LINKED                        to "No unit linked",
        StringKeys.TC_NO_UPCOMING_PERIOD                    to "No upcoming period scheduled.",
        StringKeys.TC_NUMBER_OF_QUESTIONS_N                 to "Number of questions: {count}",
        StringKeys.TC_N_ATTEMPTED                           to "{count} attempted",
        StringKeys.TC_N_AT_RISK                             to "{count} at risk",
        StringKeys.TC_N_CLASSES_DONE                        to "{count} classes done",
        StringKeys.TC_N_DRAFT_UNITS_PENDING_APPROVAL        to "{count} draft units pending approval",
        StringKeys.TC_N_ENROLLED                            to "{count} enrolled",
        StringKeys.TC_N_HOLIDAYS                            to "{count} holidays",
        StringKeys.TC_N_LEFT                                to "{count} left",
        StringKeys.TC_N_OF_N_UNITS_COVERED                  to "{covered} of {total} units covered",
        StringKeys.TC_N_PERCENT_PRESENT                     to "{pct}% present",
        StringKeys.TC_N_PER_WEEK                            to "{count}/week",
        StringKeys.TC_N_QUESTIONS_STATUS                    to "{count} questions · {status}",
        StringKeys.TC_N_STUDENTS                            to "{count} students",
        StringKeys.TC_N_STUDENTS_ABSENT                     to "{count} absent today",
        StringKeys.TC_N_SUBTOPICS                           to "{count} subtopics",
        StringKeys.TC_N_TOPICS_SELECTED                     to "{count} topics selected",
        StringKeys.TC_N_TURNED_IN                           to "{count} of {total} turned in",
        StringKeys.TC_OBJECTIVES                            to "Objectives",
        StringKeys.TC_OPTIONS_ONE_PER_LINE                  to "Options (one per line):",
        StringKeys.TC_OPTIONS_PH                            to "TC_OPTIONS_PH",
        StringKeys.TC_P                                     to "P",
        StringKeys.TC_PACE_EXPECTED_ACTUAL                  to "Expected {expected}% · Actual {actual}% · Δ {delta}%",
        StringKeys.TC_PACE_UPDATE                           to "Pace update",
        StringKeys.TC_PARENT_CONTACT                        to "Parent contact",
        StringKeys.TC_PARENT_GUARDIAN                       to "Parent/Guardian",
        StringKeys.TC_PARENT_MESSAGE                        to "Parent message",
        StringKeys.TC_NOTIFY_PARENTS                        to "Notify parents",
        StringKeys.TC_NOTIFY_PARENTS_ABOUT_ABSENCE            to "Notify parents about the absence via Messages.",
        StringKeys.TC_PARSE_SYLLABUS                        to "Parse Syllabus",
        StringKeys.TC_PARSE_WITH_AI                         to "Parse with AI",
        StringKeys.TC_PASSWORD                              to "पासवर्ड",
        StringKeys.TC_PASS_OPTIONAL                         to "Pass (optional)",
        StringKeys.TC_PASTE_SYLLABUS_HINT                   to "Paste your syllabus text below. AI will extract chapters and topics.",
        StringKeys.TC_PASTE_SYLLABUS_PH                     to "TC_PASTE_SYLLABUS_PH",
        StringKeys.TC_PASTE_SYLLABUS_TEXT                   to "Paste syllabus text",
        StringKeys.TC_PAST_DUE                              to "past due",
        StringKeys.TC_PENDING                               to "लंबित",
        StringKeys.TC_PENDING_COUNT                         to "pending",
        StringKeys.TC_PERCENT_PRESENT_OVERALL               to "{pct}% present overall",
        StringKeys.TC_PERFORMANCE                           to "Performance",
        StringKeys.TC_PICK_ALLOCATION_DESC                  to "Pick a class to continue",
        StringKeys.TC_PICK_CLASS                            to "Pick a class",
        StringKeys.TC_PICK_CLASS_FOR                        to "Pick a class for {tool}",
        StringKeys.TC_PICK_DATE_FOR_LESSON                  to "Pick the lesson date",
        StringKeys.TC_PICK_TEST_DATE                        to "Pick the test date",
        StringKeys.TC_PLAN                                  to "योजना",
        StringKeys.TC_PLANNED                               to "Planned",
        StringKeys.TC_PLANNED_DATE                          to "Planned date",
        StringKeys.TC_PREVIEW_N_UNITS_FOUND                 to "Preview ({count} units found)",
        StringKeys.TC_PROFILE                               to "प्रोफ़ाइल",
        StringKeys.TC_PTM_EVENTS                            to "PTM & Events",
        StringKeys.TC_PTM_EVENTS_DESC                       to "View upcoming parent-teacher meetings and school events",
        StringKeys.TC_PUBLISH                               to "प्रकाशित करें",
        StringKeys.TC_PUBLISHED                             to "प्रकाशित",
        StringKeys.TC_PUBLISHED_PARENTS_NOTIFIED            to "Published — parents notified. Marks are read-only.",
        StringKeys.TC_PUBLISH_DESC                          to "This will publish the results and notify parents. You can't unpublish from here.",
        StringKeys.TC_PUBLISH_NAME_Q                        to "Publish {name}?",
        StringKeys.TC_PUBLISH_NOTIFY_PARENTS                to "Publish & notify parents",
        StringKeys.TC_PUBLISH_QUIZ                          to "Publish Quiz",
        StringKeys.TC_QUESTION_TEXT                         to "Question text",
        StringKeys.TC_QUESTION_TYPES                        to "Question types",
        StringKeys.TC_QUIZ                                  to "क्विक़्",
        StringKeys.TC_NO_QUIZZES_CREATED_YET                to "इस कक्षा के लिए अभी तक कोई क्विज़ नहीं बनाया गया है।",
        StringKeys.TC_LESSON_COMPLETED                      to "पाठ पूरा हुआ!",
        StringKeys.TC_CREATE_QUIZ_TO_ASSESS                 to "\"{title}\" का मूल्यांकन करने के लिए एक क्विज़ बनाएं।",
        StringKeys.TC_NOT_NOW                               to "अभी नहीं",
        StringKeys.TC_CREATE_QUIZ                           to "क्विज़ बनाएं",
        StringKeys.TC_QUIZZES                               to "QUIZZES",
        StringKeys.TC_QUIZ_LEADERBOARD                      to "Quiz Leaderboard",
        StringKeys.TC_QUIZ_PREVIEW                          to "Quiz Preview",
        StringKeys.TC_READY_TO_MARK                         to "READY TO MARK",
        StringKeys.TC_REASON                                to "कारण",
        StringKeys.TC_REASON_COLON                          to "Reason: {reason}",
        StringKeys.TC_REASON_OPTIONAL                       to "Reason (optional)",
        StringKeys.TC_RECENT                                to "Recent",
        StringKeys.TC_RECENT_ABSENCES                       to "Recent absences",
        StringKeys.TC_REGENERATE                            to "Regenerate",
        StringKeys.TC_REGENERATE_ALL                        to "Regenerate All",
        StringKeys.TC_REJECT_ALL                            to "Reject All",
        StringKeys.TC_REMINDED                              to "Reminded",
        StringKeys.TC_REMOVE                                to "हटाएँ",
        StringKeys.TC_REPORT_CARD_REVIEW                    to "Report Card Review",
        StringKeys.TC_REPORT_CARD_REVIEW_DESC               to "Review and approve student report cards",
        StringKeys.TC_REQUEST_NEW_PERIOD                    to "+ Request New Period",
        StringKeys.TC_REQUEST_PERIOD_DELETION               to "Request Period Deletion",
        StringKeys.TC_REQUEST_PERIOD_UPDATE                 to "Request Period Update",
        StringKeys.TC_RESOURCES                             to "Resources",
        StringKeys.TC_RESULTS                               to "परिणाम",
        StringKeys.TC_RISK_HIGH                             to "High",
        StringKeys.TC_RISK_MEDIUM                           to "Medium",
        StringKeys.TC_RISK_WATCH                            to "Watch",
        StringKeys.TC_ROLL_LABEL                            to "Roll",
        StringKeys.TC_ROLL_N                                to "Roll {n}",
        StringKeys.TC_ROLL_NO                               to "Roll No {no}",
        StringKeys.TC_ROLL_ON_LEAVE                         to "On leave",
        StringKeys.TC_ROOM                                  to "कक्ष",
        StringKeys.TC_ROOM_HINT                             to "e.g. 101",
        StringKeys.TC_ROOM_N                                to "Room {room}",
        StringKeys.TC_SAVED                                 to "सहेजा गया",
        StringKeys.TC_SAVED_NOT_PUBLISHED                   to "Saved (not published)",
        StringKeys.TC_SAVED_SUCCESSFULLY                    to "Saved successfully",
        StringKeys.TC_SAVE_AND_BACK                         to "Save & Back",
        StringKeys.TC_SAVE_AS_TEMPLATE                      to "Save as template",
        StringKeys.TC_SAVE_ATTENDANCE                       to "Save attendance",
        StringKeys.TC_SAVE_CHANGES                          to "Save changes",
        StringKeys.TC_SAVE_DRAFT_BTN                        to "Save Draft",
        StringKeys.TC_SAVE_LESSON_AS_TEMPLATE               to "Save Lesson as Template",
        StringKeys.TC_SAVE_LOG                              to "Save Log",
        StringKeys.TC_SAVE_MARKS                            to "Save marks",
        StringKeys.TC_SAVING                                to "Saving…",
        StringKeys.TC_SCHEDULED                             to "SCHEDULED",
        StringKeys.TC_SCHEDULED_MESSAGES                    to "Scheduled Messages",
        StringKeys.TC_SCHEDULED_MESSAGES_DESC               to "View and manage scheduled messages",
        StringKeys.TC_SCHEDULED_TESTS                       to "SCHEDULED TESTS",
        StringKeys.TC_SCORE                                 to "स्कोर",
        StringKeys.TC_SEARCH_CLASS                          to "Search class, section or subject",
        StringKeys.TC_SEARCH_CLASSES                        to "Search classes",
        StringKeys.TC_SELECT_TOPICS_COVERED_TODAY           to "Select topics covered today",
        StringKeys.TC_SELECT_UNITS                          to "Select units",
        StringKeys.TC_SEND_TO_PARENT                        to "Send to parent",
        StringKeys.TC_SENT_TO_ADMIN_FOR_APPROVAL            to "This will be sent to your school admin for approval.",
        StringKeys.TC_SHARED                                to "Shared",
        StringKeys.TC_SHARE_WITH_TEACHERS                   to "Share with other teachers in school",
        StringKeys.TC_SIGNED_IN_AS                          to "Signed in as",
        StringKeys.TC_SKIP                                  to "छोड़ें",
        StringKeys.TC_SKIPPED                               to "Skipped",
        StringKeys.TC_SLA_DAYS                              to "SLA: {days} days",
        StringKeys.TC_START                                 to "प्रारंभ",
        StringKeys.TC_STATUS_COLON                          to "Status: {status}",
        StringKeys.TC_STEADY                                to "Steady",
        StringKeys.TC_STUDENT                               to "छात्र",
        StringKeys.TC_STUDENTS                              to "छात्र",
        StringKeys.TC_STUDENTS_COUNT                        to "{subject} · {count} students",
        StringKeys.TC_STUDENT_COLON                         to "Student: {name}",
        StringKeys.TC_SUBJECTS                              to "SUBJECTS",
        StringKeys.TC_SUBJECT_ONLY                          to "Subject only",
        StringKeys.TC_SUBMISSIONS                           to "Submissions",
        StringKeys.TC_SUBMITTED                             to "Submitted",
        StringKeys.TC_SUBMIT_REQUEST                        to "Submit Request",
        StringKeys.TC_SUB_COLON                             to "Sub: {subject}",
        StringKeys.TC_SWIPE_BACK_TO_CURRENT                 to "← Swipe back to current class",
        StringKeys.TC_SWIPE_BACK_TO_SUMMARY                 to "← Swipe back to summary",
        StringKeys.TC_SWIPE_FULL_SCHEDULE                   to "Swipe to see full schedule →",
        StringKeys.TC_SWIPE_SEE_CLASSES                     to "Swipe to see each class →",
        StringKeys.TC_SYLLABUS                              to "पाठ्यक्रम",
        StringKeys.TC_TAP_TO_CHECK_IN                       to "Tap to check in",
        StringKeys.TC_TAP_TO_SWITCH                         to "· tap to switch",
        StringKeys.TC_TEMPLATES                             to "Templates",
        StringKeys.TC_TEMPLATE_TITLE                        to "Template title",
        StringKeys.TC_TESTS_AND_MARKS                       to "TESTS & MARKS",
        StringKeys.TC_TEST_NAME                             to "Test name",
        StringKeys.TC_TEST_NAME_PH                          to "e.g. Unit Test 1",
        StringKeys.TC_THINGS_NEED_ATTENTION                 to "WHAT NEEDS YOU",
        StringKeys.TC_THIS_MONTH                            to "This month",
        StringKeys.TC_THIS_WEEK                             to "This week",
        StringKeys.TC_TITLE                                 to "शीर्षक",
        StringKeys.TC_TITLE_PH                              to "e.g. Chapter 4 exercises",
        StringKeys.TC_TO                                    to "को",
        StringKeys.TC_TO_PUBLISH                            to "प्रकाशित करने के लिए",
        StringKeys.TC_TODAY                                 to "आज",
        StringKeys.TC_TODAYS_SCHEDULE                       to "TODAY'S SCHEDULE",
        StringKeys.TC_TOGGLE_VISIBILITY                     to "Toggle visibility",
        StringKeys.TC_TOPIC_TITLE                           to "Topic title",
        StringKeys.TC_TOTAL                                 to "कुल",
        StringKeys.TC_TRANSPORT_ATTENDANCE                  to "Transport Attendance",
        StringKeys.TC_TRANSPORT_ATTENDANCE_DESC             to "Track bus boarding and deboarding",
        StringKeys.TC_TRUE                                  to "सही",
        StringKeys.TC_TRUE_FALSE                            to "True/False",
        StringKeys.TC_TRY_AGAIN                             to "Try again",
        StringKeys.TC_TRY_DIFFERENT_SEARCH                  to "Try a different search or filter.",
        StringKeys.TC_TYPE                                  to "प्रकार",
        StringKeys.TC_UNDER_INTERVENTION                    to "Under intervention",
        StringKeys.TC_UPDATE_ATTENDANCE                     to "Update attendance",
        StringKeys.TC_UPDATE_PASSWORD                       to "Update password",
        StringKeys.TC_URGENCY_COLON                         to "Urgency: {level}",
        StringKeys.TC_USE_TEMPLATE                          to "Use template",
        StringKeys.TC_WEEKLY_SCHEDULE_APPEAR                to "Your weekly schedule will appear here.",
        StringKeys.TC_WEEKLY_TIMETABLE                      to "WEEKLY TIMETABLE",
        StringKeys.TC_WHAT_NEEDS_YOU                        to "WHAT NEEDS YOU",
        StringKeys.TC_WHAT_TAUGHT_TODAY_OPTIONAL            to "What was taught today? (optional)",
        StringKeys.TC_WHICH_CLASS                           to "Which class?",
        StringKeys.TC_WHY_APPLYING                          to "Why are you applying?",
        StringKeys.TC_WHY_CHANGE_NEEDED                     to "Why is this change needed?",
        StringKeys.TC_WHY_EXTENSION                         to "Why extension?",
        // Notifications (additional)
        StringKeys.NOTIF_INBOX              to "INBOX",
        StringKeys.NOTIF_UNREAD_LABEL       to "अपठित",
        StringKeys.NOTIF_FILTER_UNREAD      to "अपठित",
        StringKeys.NOTIF_ALL_CAUGHT_UP      to "आप सब पढ़ चुके हैं",
        StringKeys.NOTIF_NO_UNREAD          to "कोई अपठित सूचना नहीं।",
        StringKeys.NOTIF_NONE_YET           to "अभी कोई सूचना नहीं।",
        StringKeys.NOTIF_PREFERENCES        to "सूचना प्राथमिकताएँ",
        StringKeys.NOTIF_MARK_ALL           to "सभी चिह्नित करें",
        // Teacher Heatmap
        StringKeys.TH_TITLE                 to "क्लास हीटमैप",
        StringKeys.TH_NO_ASSIGNMENTS        to "कोई असाइनमेंट नहीं",
        StringKeys.TH_NO_ASSIGNMENTS_DESC   to "आपके पास अभी कोई क्लास-विषय असाइनमेंट नहीं है।",
        StringKeys.TH_NO_DATA               to "कोई डेटा नहीं",
        StringKeys.TH_NO_DATA_DESC          to "इस क्लास के लिए अभी कोई भ्रांति दर्ज नहीं है।",
        StringKeys.TH_SELECTED              to "चयनित",
        StringKeys.TH_CHILDREN              to "बच्चे",
        StringKeys.TH_MISCONCEPTIONS        to "भ्रांतियाँ",
        StringKeys.TH_TOPICS                to "विषय",
        StringKeys.TH_CHILDREN_AFFECTED     to "{count} बच्चे प्रभावित",
        StringKeys.TH_EVIDENCE              to "साक्ष्य:",
        // Tutor — Chat
        StringKeys.TUT_AI_TUTOR             to "एआई ट्यूटर",
        StringKeys.TUT_CLEAR                to "साफ़ करें",
        StringKeys.TUT_ERROR                to "त्रुटि",
        StringKeys.TUT_ASK_QUESTION         to "प्रश्न पूछें",
        StringKeys.TUT_ASK_QUESTION_DESC    to "अपना सवाल नीचे टाइप करें। एआई ट्यूटर आपको चरण-दर-चरण मार्गदर्शन करेगा। आप अधिक विशिष्ट सहायता के लिए एक विषय चुन सकते हैं या सामान्य प्रश्न पूछ सकते हैं।",
        StringKeys.TUT_TYPE_DOUBT           to "अपना सवाल टाइप करें...",
        StringKeys.TUT_ASK                  to "पूछें",
        StringKeys.TUT_PRACTICE_READY       to "अभ्यास प्रश्न तैयार!",
        StringKeys.TUT_LOADING_SUBJECTS     to "विषय लोड हो रहे हैं...",
        StringKeys.TUT_GENERAL              to "सामान्य (कोई विषय नहीं)",
        // Tutor — Practice
        StringKeys.TUT_PRACTICE             to "अभ्यास",
        StringKeys.TUT_GRADING              to "ग्रेडिंग...",
        StringKeys.TUT_NO_QUESTION          to "कोई अभ्यास प्रश्न नहीं",
        StringKeys.TUT_NO_QUESTION_DESC     to "संदेह सत्र के बाद अभ्यास प्रश्न यहां दिखाई देंगे।",
        StringKeys.TUT_TYPE_ANSWER          to "अपना उत्तर टाइप करें...",
        StringKeys.TUT_SUBMIT_ANSWER        to "उत्तर जमा करें",
        StringKeys.TUT_CORRECT              to "सही!",
        StringKeys.TUT_NOT_QUITE            to "बिल्कुल नहीं",
        StringKeys.TUT_SCORE_PCT            to "स्कोर: {pct}%",
        StringKeys.TUT_FEEDBACK             to "प्रतिक्रिया",
        StringKeys.TUT_NEXT_QUESTION        to "अगला प्रश्न",
        // Tutor — Parent Progress
        StringKeys.TUT_PROGRESS_TITLE       to "ट्यूटर प्रगति",
        StringKeys.TUT_NO_PROGRESS          to "कोई प्रगति डेटा नहीं",
        StringKeys.TUT_NO_PROGRESS_DESC     to "आपके बच्चे की ट्यूटर प्रगति यहां दिखाई देगी जब वे एआई ट्यूटर का उपयोग शुरू करेंगे।",
        StringKeys.TUT_DOUBTS_RESOLVED      to "संदेह हल",
        StringKeys.TUT_ANSWERS_GIVEN        to "दिए गए उत्तर",
        StringKeys.TUT_SESSIONS             to "सत्र",
        StringKeys.TUT_SAFETY_FLAGS         to "सुरक्षा ध्वज: {count}",
        StringKeys.TUT_SAFETY_NOTIFIED      to "स्कूल को सूचित किया गया है। विवरण के लिए क्लास शिक्षक से संपर्क करें।",
        StringKeys.TUT_TOPIC_MASTERY        to "विषय निपुणता ({count})",
        StringKeys.TUT_TOPIC_LABEL          to "विषय: {topic}...",
        StringKeys.TUT_ATTEMPTS             to "प्रयास: {count}",
        StringKeys.TUT_CORRECT_COUNT        to "सही: {count}",
        StringKeys.TUT_SOURCE_LABEL         to "स्रोत: {source}",
        // SRI Preview
        StringKeys.SRI_ABOVE_MEDIAN         to "लखनऊ मध्यमा से ऊपर (7.4)",
        StringKeys.SRI_YOY                  to "+0.3 वार्षिक",
        StringKeys.SRI_ACADEMIC_OUTCOMES    to "शैक्षणिक परिणाम",
        StringKeys.SRI_TEACHER_RETENTION    to "शिक्षक प्रतिधारण",
        StringKeys.SRI_PARENT_SENTIMENT     to "अभिभावक भावना",
        StringKeys.SRI_SAFETY_INFRA         to "सुरक्षा और ढांचा",
        StringKeys.SRI_CO_CURRICULAR        to "सह-पाठ्येतर",
        StringKeys.SRI_ATTENDANCE_NORMS     to "उपस्थिति मानदंड",
        // Academic Calendar (additional)
        StringKeys.CAL_ACADEMIC_TITLE       to "शैक्षणिक कैलेंडर",
        StringKeys.CAL_NOT_AVAILABLE        to "कैलेंडर उपलब्ध नहीं",
        StringKeys.CAL_SIGN_IN_PROMPT       to "शैक्षणिक कैलेंडर देखने के लिए स्कूल खाते से साइन इन करें।",
        StringKeys.CAL_PREV                 to "‹ पिछला",
        StringKeys.CAL_NEXT_BTN             to "अगला ›",
        StringKeys.CAL_WORKING_DAYS         to "कार्य दिवस",
        StringKeys.CAL_UPCOMING_EVENTS      to "आगामी कार्यक्रम",
        StringKeys.CAL_NO_EVENTS            to "इस महीने के लिए कोई कार्यक्रम निर्धारित नहीं।",
        // Discovery
        StringKeys.DISC_DISCOVER            to "खोजें",
        StringKeys.DISC_FIND_SCHOOL         to "अपने बच्चे का स्कूल खोजें",
        StringKeys.DISC_EXIT                to "बाहर जाएं",
        StringKeys.DISC_SEARCH_PH           to "अपने पास स्कूल या नाम से खोजें",
        StringKeys.DISC_NO_SCHOOLS          to "अभी कोई स्कूल नहीं",
        StringKeys.DISC_NO_MATCHES          to "कोई मिलान नहीं",
        StringKeys.DISC_SCHOOLS_APPEAR      to "VidyaPrayag पर पंजीकृत स्कूल यहां दिखाई देंगे।",
        StringKeys.DISC_TRY_ANOTHER         to "कोई और नाम या शहर आज़माएं।",
        StringKeys.DISC_SCHOOLS_SELECTED    to "{count} स्कूल चयनित",
        StringKeys.DISC_SCHOOL_SELECTED     to "{count} स्कूल चयनित",
        StringKeys.DISC_COMPARE_NOW         to "अभी तुलना करें",
        StringKeys.DISC_SRI_SCORE           to "SRI स्कोर",
        StringKeys.DISC_IN_COMPARE          to "तुलना में",
        StringKeys.DISC_COMPARE             to "तुलना करें",
        StringKeys.DISC_ENQUIRE             to "पूछताछ",
        StringKeys.DISC_MEDIUM_LABEL        to "{medium} माध्यम",
        StringKeys.DISC_ALREADY_LINKED      to "पहले से किसी साझेदार स्कूल में हैं?",
        StringKeys.DISC_ALREADY_LINKED_DESC to "यदि आपके बच्चे का स्कूल पहले से VidyaPrayag पर है, तो उपस्थिति, अंक और उनकी पूरी यात्रा देखने के लिए अपने बच्चे को लिंक करें।",
        StringKeys.DISC_LINK_CHILD          to "अपने बच्चे को लिंक करें",
        StringKeys.DISC_SCHOOL_PROFILE      to "स्कूल प्रोफ़ाइल",
        StringKeys.DISC_SHARE               to "साझा करें",
        StringKeys.DISC_SAVE_SCHOOL         to "स्कूल सहेजें",
        StringKeys.DISC_ENQUIRE_NOW         to "अभी पूछताछ करें",
        StringKeys.DISC_ABOUT               to "परिचय",
        StringKeys.DISC_ACADEMICS           to "शैक्षणिक",
        StringKeys.DISC_FEE_STRUCTURE       to "शुल्क संरचना",
        StringKeys.DISC_SRI_BREAKDOWN       to "SRI विवरण",
        StringKeys.DISC_PARENT_REVIEWS      to "अभिभावक समीक्षाएँ",
        StringKeys.DISC_LOCATION            to "स्थान",
        StringKeys.DISC_PROFILE_COMING      to "स्कूल प्रोफ़ाइल",
        StringKeys.DISC_PROFILE_DESC        to "स्कूल द्वारा अपनी सार्वजनिक प्रोफ़ाइल पूरी करने पर यहां समृद्ध विवरण और टैग दिखाई देंगे।",
        StringKeys.DISC_BOARD               to "बोर्ड",
        StringKeys.DISC_MEDIUM              to "माध्यम",
        StringKeys.DISC_CO_ED               to "सह-शिक्षा",
        StringKeys.DISC_CLASSES_OFFERED     to "दी जाने वाली कक्षाएँ",
        StringKeys.DISC_TEACHER_RATIO       to "शिक्षक–छात्र अनुपात",
        StringKeys.DISC_COMING_SOON         to "जल्द आ रहा है",
        StringKeys.DISC_FEE_COMING          to "शुल्क संरचना",
        StringKeys.DISC_FEE_DESC            to "स्कूल व्यवस्थापक द्वारा शुल्क योजना प्रकाशित करने पर ट्यूशन और एकमुश्त शुल्क दिखाई देंगे।",
        StringKeys.DISC_SRI_TITLE           to "स्कूल प्रतिष्ठा सूचकांक",
        StringKeys.DISC_SRI_DESC            to "हमारा 11-संकेत स्कोर आपको शैक्षणिक, सुरक्षा, सुविधाओं और अभिभावक भावना पर स्कूलों की तुलना करने देता है।",
        StringKeys.DISC_REVIEWS_TITLE       to "अभिभावक समीक्षाएँ",
        StringKeys.DISC_REVIEWS_DESC        to "सत्यापित अभिभावक समीक्षाएँ परिवार लिंक-बच्चा प्रवाह के साथ लॉन्च होंगी।",
        StringKeys.DISC_ON_MAP              to "नक्शे पर",
        StringKeys.DISC_MAP_DESC            to "नक्शा एकीकरण आगामी मानचित्र सुविधा के साथ आएगा। शहर: {city}।",
        StringKeys.DISC_SEND_ENQUIRY        to "पूछताछ भेजें",
        StringKeys.DISC_ENQUIRY_RESPONSE    to "प्रवेश टीम 2 कार्य दिवसों के भीतर उत्तर देगी।",
        StringKeys.DISC_YOUR_NAME           to "आपका नाम",
        StringKeys.DISC_CHILD_NAME          to "बच्चे का नाम",
        StringKeys.DISC_CURRENT_CLASS       to "वर्तमान कक्षा",
        StringKeys.DISC_APPLY_CLASS         to "किस कक्षा के लिए आवेदन",
        StringKeys.DISC_MESSAGE_OPT         to "संदेश (वैकल्पिक)",
        StringKeys.DISC_ANY_QUESTION        to "कोई विशिष्ट प्रश्न?",
        StringKeys.DISC_SUBMIT_ENQUIRY      to "पूछताछ जमा करें",
        StringKeys.DISC_SENT                to "भेजा गया",
        StringKeys.DISC_COMPARE_SCHOOLS     to "स्कूल तुलना करें",
        StringKeys.DISC_CITY                to "शहर",
        StringKeys.DISC_FEE_RANGE           to "शुल्क सीमा",
        StringKeys.DISC_DISTANCE            to "दूरी",
        StringKeys.DISC_BOARD_RESULT        to "बोर्ड परिणाम",
        StringKeys.DISC_FEE_NOTE            to "स्कूल द्वारा शुल्क योजना प्रकाशित करने पर शुल्क सीमा और बोर्ड परिणाम भरेंगे।",
        StringKeys.DISC_ENQUIRE_ALL         to "सभी चयनित को पूछताछ",
        StringKeys.DISC_ENQUIRIES_SENT      to "पूछताछ भेजी गई",
        StringKeys.DISC_CO_ED_YES           to "हाँ",
        StringKeys.DISC_GIRLS_ONLY          to "केवल लड़कियाँ",
        StringKeys.DISC_BOYS_ONLY           to "केवल लड़के",
        StringKeys.DISC_WITHIN_3KM          to "3 किमी के भीतर",
        StringKeys.DISC_CBSE                to "CBSE",
        StringKeys.DISC_TYPE                to "प्रकार",
        StringKeys.DISC_SRI_RATING          to "SRI रेटिंग",
        // PEWS Student Detail
        StringKeys.PEWS_STUDENT_SIGNAL      to "छात्र संकेत",
        StringKeys.PEWS_NO_SIGNAL           to "कोई संकेत दर्ज नहीं",
        StringKeys.PEWS_NO_SIGNAL_DESC      to "इस छात्र का अभी कोई चेतावनी स्नैपशॉट नहीं है।",
        StringKeys.PEWS_INTERVENTIONS       to "हस्तक्षेप",
        StringKeys.PEWS_HIGH_RISK           to "उच्च जोखिम",
        StringKeys.PEWS_MEDIUM_RISK         to "मध्यम जोखिम",
        StringKeys.PEWS_WATCH               to "निगरानी",
        StringKeys.PEWS_UNDER_INTERVENTION  to "हस्तक्षेप चल रहा है",
        StringKeys.PEWS_RISK_SCORE          to "जोखिम स्कोर {score} · {date} तक",
        StringKeys.PEWS_ATTENDANCE          to "उपस्थिति",
        StringKeys.PEWS_MARKS               to "अंक",
        StringKeys.PEWS_LEAVES              to "छुट्टियाँ",
        StringKeys.PEWS_FALLING             to "गिर रहा",
        StringKeys.PEWS_RISING              to "बढ़ रहा",
        StringKeys.PEWS_WHY_STUDENT         to "यह छात्र क्यों",
        StringKeys.PEWS_AI_EXPLANATION      to "एआई व्याख्या",
        StringKeys.PEWS_LIKELY_CAUSE        to "संभावित कारण",
        StringKeys.PEWS_SUGGESTED_ACTION    to "सुझाई गई कार्रवाई",
        StringKeys.PEWS_GENERATED_BY        to "{provider} द्वारा उत्पन्न · कार्रवाई से पहले समीक्षा करें",
        StringKeys.PEWS_ESCALATED           to "उन्नत",
        StringKeys.PEWS_REMINDED            to "अनुस्मारक",
        StringKeys.PEWS_SLA_DAYS            to "SLA: {days} दिन",
        StringKeys.PEWS_SLA_FOLLOWUP        to "SLA: {days} दिन · फॉलो-अप {date}",
        StringKeys.PEWS_PLAN                to "योजना",
        StringKeys.PEWS_PARENT_MESSAGE      to "अभिभावक संदेश ({lang})",
        StringKeys.PEWS_OPENED              to "खोला गया {date}",
        StringKeys.PEWS_START               to "शुरू करें",
        StringKeys.PEWS_DISMISS             to "खारिज करें",
        StringKeys.PEWS_ADMIN               to "प्रशासक",
        StringKeys.PEWS_TEACHER             to "शिक्षक",
        StringKeys.PEWS_INITIATED_BY        to "✓ {name} ({role}) द्वारा प्रारंभित",
        StringKeys.PEWS_SEND_TO_PARENT      to "अभिभावक को भेजें",
        StringKeys.PEWS_DRAFT_PARENT_MSG    to "अभिभावक संदेश लिखें",
        StringKeys.PEWS_MARK_IMPROVED       to "सुधार चिह्नित करें",
        StringKeys.PEWS_NO_CHANGE           to "कोई परिवर्तन नहीं",
        StringKeys.PEWS_OUTCOME             to "परिणाम: {outcome}",
        StringKeys.PEWS_HISTORY             to "इतिहास",
        // Health Records
        StringKeys.HLTH_TITLE               to "स्वास्थ्य — {name}",
        StringKeys.HLTH_TAB_PROFILE         to "प्रोफ़ाइल",
        StringKeys.HLTH_TAB_IMMUNIZATIONS   to "टीकाकरण",
        StringKeys.HLTH_TAB_INCIDENTS       to "घटनाएँ",
        StringKeys.HLTH_BASIC_INFO          to "मूल जानकारी",
        StringKeys.HLTH_BLOOD_GROUP         to "रक्त समूह",
        StringKeys.HLTH_HEIGHT              to "ऊँचाई (सेमी)",
        StringKeys.HLTH_WEIGHT              to "वजन (किग्रा)",
        StringKeys.HLTH_MEDICAL_INFO        to "चिकित्सा जानकारी",
        StringKeys.HLTH_ALLERGIES           to "एलर्जी (JSON सरणी)",
        StringKeys.HLTH_CHRONIC_CONDITIONS  to "दीर्घकालिक स्थितियाँ (JSON सरणी)",
        StringKeys.HLTH_MEDICATIONS         to "दवाएँ (JSON सरणी)",
        StringKeys.HLTH_EMERGENCY_CONTACT   to "आपातकालीन संपर्क",
        StringKeys.HLTH_CONTACT_NAME        to "संपर्क नाम",
        StringKeys.HLTH_CONTACT_PHONE       to "संपर्क फ़ोन",
        StringKeys.HLTH_DOCTOR_INFO         to "डॉक्टर जानकारी",
        StringKeys.HLTH_DOCTOR_NAME         to "डॉक्टर नाम",
        StringKeys.HLTH_DOCTOR_PHONE        to "डॉक्टर फ़ोन",
        StringKeys.HLTH_SAVE_PROFILE        to "स्वास्थ्य प्रोफ़ाइल सहेजें",
        StringKeys.HLTH_IMMUNIZATION_RECORDS to "टीकाकरण रिकॉर्ड",
        StringKeys.HLTH_ADD                 to "जोड़ें",
        StringKeys.HLTH_VACCINE_NAME        to "टीका नाम",
        StringKeys.HLTH_DOSE_NUMBER         to "खुराक संख्या",
        StringKeys.HLTH_DATE_ADMINISTERED   to "दी गई तिथि",
        StringKeys.HLTH_NEXT_DUE            to "अगली देय तिथि (वैकल्पिक)",
        StringKeys.HLTH_ADMINISTERED_BY     to "द्वारा दिया गया (वैकल्पिक)",
        StringKeys.HLTH_SAVE_RECORD         to "रिकॉर्ड सहेजें",
        StringKeys.HLTH_NO_IMMUNIZATIONS    to "अभी कोई टीकाकरण रिकॉर्ड नहीं",
        StringKeys.HLTH_DOSE                to "खुराक {number} · {date}",
        StringKeys.HLTH_BY                  to "{name} द्वारा",
        StringKeys.HLTH_NEXT_DUE_LABEL      to "अगली देय: {date}",
        StringKeys.HLTH_HEALTH_INCIDENTS    to "स्वास्थ्य घटनाएँ",
        StringKeys.HLTH_LOG                 to "दर्ज करें",
        StringKeys.HLTH_DATE                to "तिथि",
        StringKeys.HLTH_TIME                to "समय (वैकल्पिक)",
        StringKeys.HLTH_DESCRIPTION         to "विवरण",
        StringKeys.HLTH_TREATMENT           to "उपचार (वैकल्पिक)",
        StringKeys.HLTH_MEDICATION_GIVEN    to "दी गई दवा (वैकल्पिक)",
        StringKeys.HLTH_SEVERITY            to "गंभीरता",
        StringKeys.HLTH_LOG_INCIDENT        to "घटना दर्ज करें",
        StringKeys.HLTH_NO_INCIDENTS        to "कोई स्वास्थ्य घटना दर्ज नहीं",
        StringKeys.HLTH_TREATMENT_LABEL     to "उपचार: {treatment}",
        StringKeys.HLTH_MEDICATION_LABEL    to "दवा: {medication}",
        StringKeys.HLTH_TIME_LABEL          to "समय: {time}",
        StringKeys.HLTH_PARENT_NOTIFIED     to "अभिभावक को सूचित किया गया",
        StringKeys.HLTH_MARK_NOTIFIED       to "सूचित चिह्नित करें",
        StringKeys.HLTH_SEVERITY_MAJOR      to "प्रमुख",
        StringKeys.HLTH_SEVERITY_MODERATE   to "मध्यम",
        StringKeys.HLTH_SEVERITY_MINOR      to "लघु",
        // ID Card Templates
        StringKeys.IDCARD_TOTAL_CARDS       to "कुल कार्ड",
        StringKeys.IDCARD_STUDENTS          to "छात्र",
        StringKeys.IDCARD_TEACHERS          to "शिक्षक",
        StringKeys.IDCARD_STAFF             to "स्टाफ",
        StringKeys.IDCARD_MILESTONE_MASTER  to "आईडी कार्ड मास्टर",
        StringKeys.IDCARD_MILESTONE_CENTURY to "शतक क्लब",
        StringKeys.IDCARD_MILESTONE_HALF    to "अर्धशतक",
        StringKeys.IDCARD_MILESTONE_FIRST   to "प्रथम कदम",
        StringKeys.IDCARD_MILESTONE_START   to "प्रारंभ कर रहे हैं",
        StringKeys.IDCARD_NO_TEMPLATES      to "अभी कोई टेम्पलेट नहीं",
        StringKeys.IDCARD_NO_TEMPLATES_DESC to "नीचे दिए गए विज़ुअल बिल्डर से अपना पहला आईडी कार्ड टेम्पलेट बनाएँ।",
        StringKeys.IDCARD_CREATE_NEW        to "नया टेम्पलेट बनाएँ",
        StringKeys.IDCARD_TEMPLATE_NAME     to "टेम्पलेट नाम",
        StringKeys.IDCARD_CARD_TYPE         to "कार्ड प्रकार",
        StringKeys.IDCARD_STUDENT           to "छात्र",
        StringKeys.IDCARD_TEACHER_ROLE      to "शिक्षक",
        StringKeys.IDCARD_STAFF_ROLE        to "स्टाफ",
        StringKeys.IDCARD_FIELDS_DISPLAY    to "प्रदर्शित करने के लिए फ़ील्ड",
        StringKeys.IDCARD_FIELD_NAME        to "नाम",
        StringKeys.IDCARD_FIELD_ROLE        to "भूमिका",
        StringKeys.IDCARD_FIELD_CLASS       to "कक्षा",
        StringKeys.IDCARD_FIELD_SCHOOL      to "विद्यालय",
        StringKeys.IDCARD_FIELD_PHOTO       to "फ़ोटो",
        StringKeys.IDCARD_FIELD_QR          to "सामने QR",
        StringKeys.IDCARD_FIELD_EMERGENCY   to "आपातकालीन",
        StringKeys.IDCARD_FIELD_BLOOD       to "रक्त समूह",
        StringKeys.IDCARD_ACCENT_COLOR      to "एक्सेंट रंग",
        StringKeys.IDCARD_LIVE_PREVIEW      to "लाइव पूर्वावलोकन",
        StringKeys.IDCARD_PREVIEW           to "पूर्वावलोकन",
        StringKeys.IDCARD_CREATING          to "बना रहे हैं...",
        StringKeys.IDCARD_CREATE_BTN        to "टेम्पलेट बनाएँ",
        StringKeys.IDCARD_ID_CARD           to "आईडी कार्ड",
        StringKeys.IDCARD_SCAN_QR           to "सत्यापित करने के लिए QR स्कैन करें",
        StringKeys.IDCARD_ACTIVE            to "सक्रिय",
        StringKeys.IDCARD_INACTIVE          to "निष्क्रिय",
        StringKeys.IDCARD_DEACTIVATE        to "निष्क्रिय करें",

        // Branding Settings
        StringKeys.BRAND_TITLE              to "ब्रांडिंग किट",
        StringKeys.BRAND_RESET_TITLE        to "ब्रांडिंग रीसेट करें?",
        StringKeys.BRAND_RESET_MSG          to "सभी रंग डिफ़ॉल्ट पर रीसेट हो जाएंगे। आपकी अपलोड की गई संपत्तियां रखी जाएंगी।",
        StringKeys.BRAND_RESET_BTN          to "रीसेट",
        StringKeys.BRAND_CUSTOMIZED         to "अनुकूलित",
        StringKeys.BRAND_DEFAULT            to "डिफ़ॉल्ट",
        StringKeys.BRAND_COLORS             to "ब्रांड रंग",
        StringKeys.BRAND_PRIMARY_COLOR      to "प्राथमिक रंग",
        StringKeys.BRAND_SECONDARY_COLOR    to "द्वितीयक रंग",
        StringKeys.BRAND_ACCENT_COLOR       to "एक्सेंट रंग",
        StringKeys.BRAND_SAVE_COLORS        to "रंग सहेजें",
        StringKeys.BRAND_ASSETS             to "ब्रांड संपत्ति",
        StringKeys.BRAND_ASSETS_DESC        to "अपने स्कूल का लोगो, ऐप आइकन और स्प्लैश स्क्रीन अपलोड करें। ये लॉगिन स्क्रीन, स्प्लैश और ऐप आइकन पर दिखाई देते हैं।",
        StringKeys.BRAND_LOGO               to "लोगो",
        StringKeys.BRAND_DARK_LOGO          to "डार्क लोगो",
        StringKeys.BRAND_FAVICON            to "फ़ेविकॉन",
        StringKeys.BRAND_APP_ICON           to "ऐप आइकन",
        StringKeys.BRAND_SPLASH             to "स्प्लैश स्क्रीन",
        StringKeys.BRAND_LOGIN_BG           to "लॉगिन पृष्ठभूमि",
        StringKeys.BRAND_SUBDOMAIN          to "कस्टम सबडोमेन",
        StringKeys.BRAND_SUBDOMAIN_DESC     to "अपने स्कूल के पोर्टल के लिए कस्टम वेब पता सेट करें, जैसे dpsrkpuram.vidyaprayag.com",
        StringKeys.BRAND_CURRENT_SUBDOMAIN  to "वर्तमान सबडोमेन",
        StringKeys.BRAND_REMOVE             to "हटाएं",
        StringKeys.BRAND_SUBDOMAIN_LABEL    to "सबडोमेन",
        StringKeys.BRAND_SUBDOMAIN_PLACE    to "जैसे dpsrkpuram",
        StringKeys.BRAND_SUBDOMAIN_HINT     to "4-32 अक्षर, लोअरकेस अक्षर, संख्या और हाइफ़न",
        StringKeys.BRAND_CHECK              to "जांचें",
        StringKeys.BRAND_ASSIGN             to "असाइन करें",
        StringKeys.BRAND_SUBDOMAIN_AVAIL    to "सबडोमेन उपलब्ध है!",
        StringKeys.BRAND_SUBDOMAIN_TAKEN    to "सबडोमेन पहले से लिया गया है।",
        StringKeys.BRAND_RESET_DEFAULTS     to "डिफ़ॉल्ट पर रीसेट करें",
        StringKeys.BRAND_LIVE_PREVIEW       to "लाइव पूर्वावलोकन",
        StringKeys.BRAND_YOUR_SCHOOL        to "आपका विद्यालय",
        StringKeys.BRAND_PRIMARY_BTN        to "प्राथमिक बटन",
        StringKeys.BRAND_SECONDARY_BTN      to "द्वितीयक",
        StringKeys.BRAND_SWATCH_PRIMARY     to "प्राथमिक",
        StringKeys.BRAND_SWATCH_SECONDARY   to "द्वितीयक",
        StringKeys.BRAND_SWATCH_ACCENT      to "एक्सेंट",
        StringKeys.BRAND_HEX_COLOR          to "हेक्स रंग",
        StringKeys.BRAND_UPLOADED           to "अपलोड किया गया",
        StringKeys.BRAND_NOT_SET            to "सेट नहीं",
        StringKeys.BRAND_REPLACE            to "बदलें",
        StringKeys.BRAND_UPLOAD             to "अपलोड",

        // Transport Management
        StringKeys.TRANS_TITLE              to "परिवहन प्रबंधन",
        StringKeys.TRANS_ROUTES             to "मार्ग ({count})",
        StringKeys.TRANS_ADD_ROUTE          to "+ मार्ग जोड़ें",
        StringKeys.TRANS_VEHICLES           to "वाहन ({count})",
        StringKeys.TRANS_ADD_VEHICLE        to "+ वाहन जोड़ें",
        StringKeys.TRANS_ASSIGNMENTS        to "छात्र असाइनमेंट ({count})",
        StringKeys.TRANS_ASSIGN             to "+ असाइन करें",
        StringKeys.TRANS_NEW_ROUTE          to "नया मार्ग",
        StringKeys.TRANS_ROUTE_NAME         to "मार्ग नाम",
        StringKeys.TRANS_ROUTE_PLACE        to "जैसे मार्ग A — उत्तर क्षेत्र",
        StringKeys.TRANS_DESC_OPTIONAL      to "विवरण (वैकल्पिक)",
        StringKeys.TRANS_DESC_PLACE         to "उत्तरी आवासीय क्षेत्रों को कवर करता है",
        StringKeys.TRANS_CREATE_ROUTE       to "मार्ग बनाएं",
        StringKeys.TRANS_NEW_VEHICLE        to "नया वाहन",
        StringKeys.TRANS_BUS_NUMBER         to "बस संख्या",
        StringKeys.TRANS_BUS_PLACE          to "जैसे KA-01-AB-1234",
        StringKeys.TRANS_CAPACITY           to "क्षमता",
        StringKeys.TRANS_DRIVER_NAME        to "ड्राइवर नाम (वैकल्पिक)",
        StringKeys.TRANS_DRIVER_PHONE       to "ड्राइवर फ़ोन (वैकल्पिक)",
        StringKeys.TRANS_ASSIGN_ROUTE       to "मार्ग असाइन करें (वैकल्पिक):",
        StringKeys.TRANS_CREATE_VEHICLE     to "वाहन बनाएं",
        StringKeys.TRANS_ASSIGN_STUDENT     to "छात्र को मार्ग पर असाइन करें",
        StringKeys.TRANS_STUDENT_ID         to "छात्र आईडी",
        StringKeys.TRANS_STUDENT_ID_PLACE   to "छात्र UUID पेस्ट करें",
        StringKeys.TRANS_SELECT_ROUTE       to "मार्ग चुनें:",
        StringKeys.TRANS_SELECT_STOP        to "स्टॉप चुनें:",
        StringKeys.TRANS_SELECT_VEHICLE     to "वाहन चुनें:",
        StringKeys.TRANS_FEE_AMOUNT         to "परिवहन शुल्क राशि (वैकल्पिक)",
        StringKeys.TRANS_FEE_PLACE          to "जैसे 6000",
        StringKeys.TRANS_FEE_DUE_DATE       to "शुल्क नियत तिथि (वैकल्पिक)",
        StringKeys.TRANS_ASSIGN_BTN         to "छात्र असाइन करें",
        StringKeys.TRANS_STOPS              to "{count} स्टॉप",
        StringKeys.TRANS_ACTIVE             to "सक्रिय",
        StringKeys.TRANS_INACTIVE           to "निष्क्रिय",
        StringKeys.TRANS_CAPACITY_LABEL     to "क्षमता: {count}",
        StringKeys.TRANS_DRIVER_LABEL       to "ड्राइवर: {name}",
        StringKeys.TRANS_ROUTE_LABEL        to "मार्ग: {name}",
        StringKeys.TRANS_STOP_LABEL         to "स्टॉप: {name}",
        StringKeys.TRANS_BUS_LABEL          to "बस: {name}",
        StringKeys.TRANS_DEACTIVATE         to "निष्क्रिय करें",

        // Academic Calendar Platform
        StringKeys.ACALP_TITLE              to "शैक्षणिक कैलेंडर",
        StringKeys.ACALP_CREATE             to "बनाएं",
        StringKeys.ACALP_EMPTY_TITLE        to "अभी कोई कैलेंडर नहीं",
        StringKeys.ACALP_EMPTY_BODY         to "वर्ष की योजना शुरू करने के लिए अपना पहला शैक्षणिक कार्यक्रम बनाएं।",
        StringKeys.ACALP_HIGHLIGHTS         to "आगामी मुख्य आकर्षण",
        StringKeys.ACALP_VIEW               to "दृश्य",
        StringKeys.ACALP_UPCOMING           to "आगामी कार्यक्रम",
        StringKeys.ACALP_DRAFT_EVENTS       to "ड्राफ्ट कार्यक्रम",
        StringKeys.ACALP_PUBLISHED_EVENTS   to "प्रकाशित कार्यक्रम",
        StringKeys.ACALP_MILESTONES         to "शैक्षणिक मील के पत्थर",
        StringKeys.ACALP_ANALYTICS          to "कैलेंडर विश्लेषण",
        StringKeys.ACALP_ACADEMIC_YEAR      to "शैक्षणिक वर्ष {year}",
        StringKeys.ACALP_ACADEMIC_CAL       to "शैक्षणिक कैलेंडर",
        StringKeys.ACALP_CENTRALIZED        to "केंद्रीकृत योजना और शेड्यूलिंग",
        StringKeys.ACALP_EVENTS             to "कार्यक्रम",
        StringKeys.ACALP_SCHOOL_DAYS        to "विद्यालय दिवस",
        StringKeys.ACALP_HOLIDAYS           to "अवकाश",
        StringKeys.ACALP_NEXT_EVENT         to "अगला कार्यक्रम",
        StringKeys.ACALP_NO_EVENTS          to "दिखाने के लिए कोई कार्यक्रम नहीं।",
        StringKeys.ACALP_NOTHING_UPCOMING   to "कुछ आगामी नहीं।",
        StringKeys.ACALP_CONFLICT           to "संभावित शेड्यूल संघर्ष",

        // Class Detail
        StringKeys.CD_STUDENTS              to "छात्र",
        StringKeys.CD_TEACHERS              to "शिक्षक",
        StringKeys.CD_TIMETABLE             to "समय सारिणी",
        StringKeys.CD_ANALYTICS             to "विश्लेषण",
        StringKeys.CD_NO_STUDENTS           to "कोई छात्र नहीं",
        StringKeys.CD_NO_STUDENTS_BODY      to "{className} में कोई छात्र नहीं मिले।",
        StringKeys.CD_STUDENTS_COUNT        to "{count} छात्र",
        StringKeys.CD_SEC                   to "सेक्शन {section}",
        StringKeys.CD_ROLL                  to "रोल {number}",
        StringKeys.CD_ATTENDANCE            to "{percent}% उपस्थिति",
        StringKeys.CD_LOADING_TIMETABLE     to "समय सारिणी लोड हो रही है…",
        StringKeys.CD_NO_TEACHERS           to "कोई शिक्षक असाइन नहीं",
        StringKeys.CD_NO_TEACHERS_BODY      to "शेड्यूल टैब में अवधि असाइन करने पर शिक्षक यहां दिखाई देंगे।",
        StringKeys.CD_TEACHERS_COUNT        to "{count} शिक्षक",
        StringKeys.CD_NO_TIMETABLE          to "अभी कोई समय सारिणी नहीं",
        StringKeys.CD_NO_TIMETABLE_BODY     to "शेड्यूल टैब में समय सारिणी बनाएं।",
        StringKeys.CD_WEEKLY_TIMETABLE      to "{className} — साप्ताहिक समय सारिणी",
        StringKeys.CD_NO_PERIODS            to "{day} के लिए कोई अवधि नहीं",
        StringKeys.CD_NO_PERIODS_BODY       to "{className} के लिए इस दिन कोई कक्षा निर्धारित नहीं।",
        StringKeys.CD_ROOM                  to "कक्ष {room}",
        StringKeys.CD_NO_ANALYTICS          to "अभी कोई विश्लेषण नहीं",
        StringKeys.CD_NO_ANALYTICS_BODY     to "शिक्षकों द्वारा अंक और उपस्थिति पोस्ट करने पर कक्षा-स्तरीय विश्लेषण यहां दिखाई देगा।",
        StringKeys.CD_AVG_PROFICIENCY       to "औसत दक्षता",
        StringKeys.CD_ACTIVE_STUDENTS       to "सक्रिय छात्र",
        StringKeys.CD_MEDIAN_GRADE          to "मध्यिका ग्रेड",
        StringKeys.CD_GRADE_DIST            to "ग्रेड वितरण",
        StringKeys.CD_SUBJECT_MATRIX        to "विषय मैट्रिक्स",
        StringKeys.CD_EARLY_WARNING         to "प्रारंभिक चेतावनी",
        StringKeys.CD_CRITICAL              to "गंभीर",
        StringKeys.CD_MODERATE              to "मध्यम",
        StringKeys.CD_ON_TARGET             to "लक्ष्य पर",
        StringKeys.CD_TOP_PERFORMER         to "शीर्ष प्रदर्शक",
        StringKeys.CD_PROGRESS_MONITORING   to "प्रगति निगरानी",
        StringKeys.CD_TREND_UP              to "▲ ऊपर",
        StringKeys.CD_TREND_DOWN            to "▼ नीचे",
        StringKeys.CD_TREND_FLAT            to "● समान",
        StringKeys.CD_MATH                  to "गणित",
        StringKeys.CD_SCI                   to "विज्ञान",
        StringKeys.CD_LIT                   to "साहित्य",
        StringKeys.CD_ATTENDANCE_LABEL      to "उपस्थिति {percent}",

        // Scholarship Management
        StringKeys.SCH_MGMT_TITLE           to "छात्रवृत्ति प्रबंधन",
        StringKeys.SCH_CREATE_NEW           to "+ नई योजना बनाएं",
        StringKeys.SCH_NO_SCHEMES           to "अभी कोई छात्रवृत्ति योजना नहीं",
        StringKeys.SCH_NO_SCHEMES_BODY      to "एक जोड़ने के लिए ऊपर \"नई योजना बनाएं\" टैप करें।",
        StringKeys.SCH_NO_APPLICATIONS      to "समीक्षा के लिए कोई आवेदन नहीं",
        StringKeys.SCH_NO_APPLICATIONS_BODY to "अभिभावकों द्वारा आवेदन करने पर आवेदन यहां दिखाई देंगे।",
        StringKeys.SCH_NO_RENEWALS          to "कोई नवीनीकरण अनुरोध नहीं",
        StringKeys.SCH_NO_RENEWALS_BODY     to "नवीनीकरण अनुरोध यहां दिखाई देंगे।",
        StringKeys.SCH_APPLICATIONS         to "आवेदन ({count})",
        StringKeys.SCH_RENEWALS             to "नवीनीकरण ({count})",
        StringKeys.SCH_SCHEMES              to "योजनाएं",
        StringKeys.SCH_TAB_APPLICATIONS     to "आवेदन",
        StringKeys.SCH_TAB_RENEWALS         to "नवीनीकरण",
        StringKeys.SCH_RENEWABLE            to "नवीनीकरणीय",
        StringKeys.SCH_AWARD                to "पुरस्कार",
        StringKeys.SCH_ELIGIBILITY          to "पात्रता",
        StringKeys.SCH_EDIT                 to "संपादित करें",
        StringKeys.SCH_REVIEW               to "समीक्षा",
        StringKeys.SCH_REMARKS              to "टिप्पणियां",
        StringKeys.SCH_DISBURSEMENT_AMT     to "संवितरण राशि (वैकल्पिक)",
        StringKeys.SCH_APPROVE              to "स्वीकृत करें",
        StringKeys.SCH_REJECT               to "अस्वीकृत करें",
        StringKeys.SCH_DISBURSEMENT_REF     to "संवितरण संदर्भ",
        StringKeys.SCH_RECORD_DISBURSEMENT  to "संवितरण दर्ज करें",
        StringKeys.SCH_DISBURSED            to "वितरित: {amount}",
        StringKeys.SCH_REF                  to "संदर्भ: {ref}",
        StringKeys.SCH_RENEWAL_FOR          to "शैक्षणिक वर्ष के लिए नवीनीकरण",
        StringKeys.SCH_DOCUMENTS            to "{count} दस्तावेज़ संलग्न",
        StringKeys.SCH_DELETE_TITLE         to "छात्रवृत्ति हटाएं",
        StringKeys.SCH_DELETE_MSG           to "क्या आप \"{title}\" को निष्क्रिय करना चाहते हैं? यह इसे अभिभावक दृश्य से हटा देगा लेकिन मौजूदा आवेदन सुरक्षित रहेंगे।",
        StringKeys.SCH_EDIT_SCHEME          to "छात्रवृत्ति योजना संपादित करें",
        StringKeys.SCH_CREATE_SCHEME        to "छात्रवृत्ति योजना बनाएं",
        StringKeys.SCH_TITLE_LABEL          to "शीर्षक *",
        StringKeys.SCH_DESCRIPTION          to "विवरण",
        StringKeys.SCH_DISPLAY_AMOUNT       to "प्रदर्शन राशि (जैसे ₹5,000)",
        StringKeys.SCH_NUMERIC_AMOUNT       to "संख्यात्मक राशि (निश्चित प्रकार के लिए)",
        StringKeys.SCH_TYPE                 to "प्रकार",
        StringKeys.SCH_FIXED                to "निश्चित राशि",
        StringKeys.SCH_FULL_WAIVER          to "पूर्ण छूट",
        StringKeys.SCH_PARTIAL_WAIVER       to "आंशिक छूट",
        StringKeys.SCH_WAIVER_PCT           to "छूट प्रतिशत (0-100)",
        StringKeys.SCH_ELIGIBILITY_CRIT     to "पात्रता मानदंड",
        StringKeys.SCH_CATEGORY             to "श्रेणी",
        StringKeys.SCH_MGMT_START_DATE      to "प्रारंभ तिथि",
        StringKeys.SCH_MGMT_END_DATE        to "अंतिम तिथि (आवेदन समयसीमा)",
        StringKeys.SCH_RENEWABLE_LABEL      to "नवीनीकरणीय",
        StringKeys.SCH_RENEWAL_PERIOD       to "नवीनीकरण अवधि (महीने)",
        StringKeys.SCH_UPDATE               to "अद्यतन करें",

        // Library UIX Components HI
        StringKeys.LIB_UIX_OVERDUE            to "{count}d अतिदेय",
        StringKeys.LIB_UIX_DUE_TODAY          to "आज देय",
        StringKeys.LIB_UIX_DUE_LEFT           to "{count}d शेष",
        StringKeys.LIB_UIX_FINE_AMOUNT        to "जुर्माना: ₹{amount}",
        StringKeys.LIB_UIX_FINE_CAP           to "सीमा: ₹{amount}",
        StringKeys.LIB_UIX_NO_CAP             to "कोई सीमा नहीं",
        StringKeys.LIB_UIX_FINE_CAPPED        to "जुर्माना प्रतिस्थापन लागत पर सीमित",
        StringKeys.LIB_UIX_FINE_NO_CAP        to "₹{amount} (कोई सीमा नहीं)",
        StringKeys.LIB_UIX_COVER_FOR          to "{title} का कवर",
        StringKeys.LIB_UIX_AVAILABILITY       to "{available}/{total} उपलब्ध",
        StringKeys.LIB_UIX_FILTERS            to "फ़िल्टर",
        StringKeys.LIB_UIX_CATEGORY           to "श्रेणी",
        StringKeys.LIB_UIX_ALL                to "सभी",
        StringKeys.LIB_UIX_AVAIL_LABEL        to "उपलब्धता",
        StringKeys.LIB_UIX_AVAILABLE_ONLY     to "केवल उपलब्ध",
        StringKeys.LIB_UIX_SORT_BY            to "क्रमबद्ध करें",
        StringKeys.LIB_UIX_SORT_NEWEST        to "नवीनतम",
        StringKeys.LIB_UIX_SORT_TITLE_AZ      to "शीर्षक A-Z",
        StringKeys.LIB_UIX_SORT_AUTHOR        to "लेखक",
        StringKeys.LIB_UIX_SORT_POPULAR       to "लोकप्रिय",
        StringKeys.LIB_UIX_CLEAR              to "साफ़ करें",
        StringKeys.LIB_UIX_APPLY_FILTERS      to "फ़िल्टर लागू करें",
        StringKeys.LIB_UIX_ISSUES_COUNT       to "{count} जारी",
        StringKeys.LIB_UIX_AVAILABLE_SOON     to "जल्द उपलब्ध",
        StringKeys.LIB_UIX_AVAILABLE_IN       to "~{days}d में उपलब्ध",
        StringKeys.LIB_UIX_AHEAD              to "({count} प्रतीक्षारत)",
        StringKeys.LIB_UIX_MONTHLY            to "मासिक",
        StringKeys.LIB_UIX_CATEGORIES         to "श्रेणियाँ",
        StringKeys.LIB_UIX_NO_DATA            to "कोई डेटा नहीं",
        StringKeys.LIB_UIX_QUICK_ACTIONS      to "त्वरित क्रियाएँ",
        StringKeys.LIB_UIX_SELECT_BOOK        to "एक पुस्तक चुनें",
        StringKeys.LIB_UIX_RENEW              to "नवीनीकृत करें",
        StringKeys.LIB_UIX_MAX                to "अधिकतम",
        StringKeys.LIB_UIX_RETURN             to "वापस करें",
        StringKeys.LIB_UIX_RECENTLY_VIEWED    to "हाल में देखा",
        StringKeys.LIB_UIX_GOOD_MORNING       to "सुप्रभात",
        StringKeys.LIB_UIX_GOOD_AFTERNOON     to "शुभ अपराह्न",
        StringKeys.LIB_UIX_GOOD_EVENING       to "शुभ संध्या",
        StringKeys.LIB_UIX_OVERDUE_BOOKS      to "आपके पास {count} अतिदेय पुस्तक(एँ) हैं",
        StringKeys.LIB_UIX_DUE_TOMORROW       to "आपके पास {count} पुस्तक(एँ) कल देय हैं",
        StringKeys.LIB_UIX_READY_FOR_PICKUP   to "{count} पुस्तक(एँ) प्राप्त करने हेतु तैयार",
        StringKeys.LIB_UIX_READY_TO_EXPLORE   to "खोजने के लिए तैयार?",
        StringKeys.LIB_UIX_READ_LESS          to "कम पढ़ें",
        StringKeys.LIB_UIX_READ_MORE          to "और पढ़ें",
        StringKeys.LIB_UIX_READING_TIME       to "≈ {hours} घंटे ({pages} पृष्ठ)",
        StringKeys.LIB_UIX_SCAN_TO_VIEW       to "देखने के लिए स्कैन करें",
        StringKeys.LIB_UIX_GOT_IT             to "समझ गया",
        StringKeys.LIB_UIX_AVAILABLE          to "उपलब्ध",
        StringKeys.LIB_UIX_AZ                 to "A-Z",
        StringKeys.LIB_UIX_LESS_FILTERS       to "कम फ़िल्टर",
        StringKeys.LIB_UIX_MORE_FILTERS       to "अधिक फ़िल्टर",
        StringKeys.LIB_UIX_READING_STREAK     to "पठन श्रृंखला",
        StringKeys.LIB_UIX_CURRENT_STREAK     to "वर्तमान: {count} दिन",
        StringKeys.LIB_UIX_LONGEST_STREAK     to "सबसे लंबी: {count} दिन",
        StringKeys.LIB_UIX_DONT_BREAK_CHAIN   to "श्रृंखला मत तोड़ें!",
        StringKeys.LIB_UIX_GRID               to "ग्रिड",
        StringKeys.LIB_UIX_LIST               to "सूची",
        StringKeys.LIB_UIX_SHELF              to "शेल्फ",
        StringKeys.LIB_UIX_BOOK_OF_MONTH      to "माह की पुस्तक",
        StringKeys.LIB_UIX_BOOK_OF_WEEK       to "सप्ताह की पुस्तक",
        StringKeys.LIB_UIX_SEARCH_PLACEHOLDER to "पुस्तकें, लेखक खोजें...",
        StringKeys.LIB_UIX_QUICK_ISSUE        to "त्वरित जारी",
        StringKeys.LIB_UIX_STEP               to "चरण {step}/3",
        StringKeys.LIB_UIX_CONFIRM_BOOK       to "पुस्तक की पुष्टि करें",
        StringKeys.LIB_UIX_NO_BOOK_SELECTED   to "कोई पुस्तक चयनित नहीं। पहले खोजें और पुस्तक चुनें।",
        StringKeys.LIB_UIX_BORROWER_DETAILS   to "उधारकर्ता विवरण",
        StringKeys.LIB_UIX_BORROWER_NAME      to "उधारकर्ता नाम",
        StringKeys.LIB_UIX_ENTER_NAME         to "नाम दर्ज करें",
        StringKeys.LIB_UIX_REVIEW_CONFIRM     to "समीक्षा और पुष्टि",
        StringKeys.LIB_UIX_BOOK_LABEL         to "पुस्तक: {title}",
        StringKeys.LIB_UIX_AUTHOR_LABEL       to "लेखक: {name}",
        StringKeys.LIB_UIX_UNKNOWN            to "अज्ञात",
        StringKeys.LIB_UIX_BORROWER_LABEL     to "उधारकर्ता: {name}",
        StringKeys.LIB_UIX_DUE_DATE_14        to "देय तिथि: आज से 14 दिन",
        StringKeys.LIB_UIX_ISSUE_BOOK         to "पुस्तक जारी करें",
        // ── StudentLibraryScreen ──
        StringKeys.STU_LIB_TAB_BROWSE          to "ब्राउज़ करें",
        StringKeys.STU_LIB_TAB_MY_BOOKS        to "मेरी पुस्तकें",
        StringKeys.STU_LIB_TAB_HISTORY         to "इतिहास",
        StringKeys.STU_LIB_TAB_WISHLIST        to "इच्छा-सूची",
        StringKeys.STU_LIB_TAB_RESERVATIONS    to "आरक्षण",
        StringKeys.STU_LIB_TAB_REQUESTS        to "अनुरोध",
        StringKeys.STU_LIB_TAB_PROFILE         to "प्रोफ़ाइल",
        StringKeys.STU_LIB_TAB_BADGES          to "बैज",
        StringKeys.STU_LIB_TAB_DISCUSSIONS     to "चर्चाएँ",
        StringKeys.STU_LIB_TITLE               to "पुस्तकालय",
        StringKeys.STU_LIB_OFFLINE_CACHED      to "ऑफ़लाइन — कैश्ड डेटा दिखाया जा रहा है",
        StringKeys.STU_LIB_OFFLINE_CHECK       to "ऑफ़लाइन — अपना कनेक्शन जाँचें",
        StringKeys.STU_LIB_COACHMARK_TITLE     to "पुस्तकालय में आपका स्वागत है!",
        StringKeys.STU_LIB_COACHMARK_MSG       to "शीर्षक, लेखक, या ISBN द्वारा कोई भी पुस्तक खोजें। परिणामों को संकुचित करने के लिए फ़िल्टर का उपयोग करें।",
        StringKeys.STU_LIB_READER              to "रीडर",
        StringKeys.STU_LIB_SEARCH_BOOKS        to "पुस्तकें खोजें",
        StringKeys.STU_LIB_SEARCH              to "खोजें",
        StringKeys.STU_LIB_TRENDING_NOW        to "अभी ट्रेंडिंग",
        StringKeys.STU_LIB_ISSUES_COUNT        to "{count} जारी",
        StringKeys.STU_LIB_RECOMMENDED         to "आपके लिए अनुशंसित",
        StringKeys.STU_LIB_WHY                 to "क्यों: {reason}",
        StringKeys.STU_LIB_NO_BOOKS_FOUND      to "कोई पुस्तक नहीं मिली",
        StringKeys.STU_LIB_TRY_DIFFERENT       to "एक अलग खोज क्वेरी आज़माएँ।",
        StringKeys.STU_LIB_BOOKS_COUNT         to "{count} पुस्तकें",
        StringKeys.STU_LIB_LOAD_MORE           to "और लोड करें ({remaining} शेष)",
        StringKeys.STU_LIB_RESERVE             to "आरक्षित करें",
        StringKeys.STU_LIB_ADD_WISHLIST        to "+ इच्छा-सूची",
        StringKeys.STU_LIB_MY_PROFILE          to "मेरी पुस्तकालय प्रोफ़ाइल",
        StringKeys.STU_LIB_BOOKS_READ          to "पढ़ी गई पुस्तकें",
        StringKeys.STU_LIB_CURRENTLY_ISSUED    to "वर्तमान में जारी",
        StringKeys.STU_LIB_OVERDUE             to "अतिदेय",
        StringKeys.STU_LIB_OUTSTANDING_FINE    to "बकाया जुर्माना",
        StringKeys.STU_LIB_CURRENT_STREAK      to "वर्तमान श्रृंखला",
        StringKeys.STU_LIB_LONGEST_STREAK      to "सबसे लंबी श्रृंखला",
        StringKeys.STU_LIB_STREAK_DAYS         to "{count} दिन",
        StringKeys.STU_LIB_FINE_AMOUNT         to "जुर्माना: ₹{amount}",
        StringKeys.STU_LIB_READING_GOAL        to "पठन लक्ष्य",
        StringKeys.STU_LIB_GOAL_ACHIEVED       to "लक्ष्य प्राप्त! 🎉",
        StringKeys.STU_LIB_SET_READING_GOAL    to "पठन लक्ष्य सेट करें",
        StringKeys.STU_LIB_GOAL_COUNT          to "लक्ष्य (पुस्तकों की संख्या)",
        StringKeys.STU_LIB_PERIOD              to "अवधि",
        StringKeys.STU_LIB_MONTHLY             to "मासिक",
        StringKeys.STU_LIB_QUARTERLY           to "त्रैमासिक",
        StringKeys.STU_LIB_YEARLY              to "वार्षिक",
        StringKeys.STU_LIB_TARGET_YEAR         to "लक्ष्य वर्ष",
        StringKeys.STU_LIB_SET_GOAL            to "लक्ष्य सेट करें",
        StringKeys.STU_LIB_NO_BADGES           to "अभी तक कोई बैज नहीं",
        StringKeys.STU_LIB_READ_MORE_BADGES    to "बैज अर्जित करने के लिए और पुस्तकें पढ़ें!",
        StringKeys.STU_LIB_EARNED_ON           to "अर्जित: {date}",
        StringKeys.STU_LIB_EARNED              to "अर्जित",
        StringKeys.STU_LIB_LOCKED              to "बंद",
        StringKeys.STU_LIB_NO_BOOKS_ISSUED     to "कोई पुस्तक जारी नहीं",
        StringKeys.STU_LIB_BROWSE_TO_ISSUE     to "पुस्तकालय ब्राउज़ करें और शुरू करने के लिए एक पुस्तक जारी करें।",
        StringKeys.STU_LIB_RENEWALS            to "नवीनीकरण: {count}/2",
        StringKeys.STU_LIB_RENEW               to "नवीनीकृत करें",
        StringKeys.STU_LIB_READING_HISTORY     to "पठन इतिहास",
        StringKeys.STU_LIB_NO_HISTORY          to "अभी तक कोई इतिहास नहीं",
        StringKeys.STU_LIB_HISTORY_APPEAR      to "आपका पठन इतिहास यहाँ दिखाई देगा।",
        StringKeys.STU_LIB_MY_WISHLIST         to "मेरी इच्छा-सूची",
        StringKeys.STU_LIB_WISHLIST_EMPTY      to "इच्छा-सूची खाली है",
        StringKeys.STU_LIB_WISHLIST_EMPTY_BODY to "बाद में पढ़ने के लिए पुस्तकें अपनी इच्छा-सूची में जोड़ें।",
        StringKeys.STU_LIB_REMOVE              to "हटाएँ",
        StringKeys.STU_LIB_MY_RESERVATIONS     to "मेरे आरक्षण",
        StringKeys.STU_LIB_NO_RESERVATIONS     to "कोई आरक्षण नहीं",
        StringKeys.STU_LIB_RESERVE_FROM_BROWSE to "यहाँ देखने के लिए ब्राउज़ टैब से एक पुस्तक आरक्षित करें।",
        StringKeys.STU_LIB_RESERVED_ON         to "आरक्षित: {date}",
        StringKeys.STU_LIB_CANCEL_RESERVATION_TITLE to "आरक्षण रद्द करें?",
        StringKeys.STU_LIB_CANCEL_RESERVATION_MSG to "क्या आप वाकई इस आरक्षण को रद्द करना चाहते हैं?",
        StringKeys.STU_LIB_CANCEL_RESERVATION_BTN to "आरक्षण रद्द करें",
        StringKeys.STU_LIB_KEEP                to "रखें",
        StringKeys.STU_LIB_ACQUISITION_REQUESTS to "अधिग्रहण अनुरोध",
        StringKeys.STU_LIB_NO_REQUESTS         to "कोई अनुरोध नहीं",
        StringKeys.STU_LIB_REQUESTS_APPEAR     to "आपके पुस्तक अधिग्रहण अनुरोध यहाँ दिखाई देंगे।",
        StringKeys.STU_LIB_AUTHOR_LABEL        to "लेखक: {name}",
        StringKeys.STU_LIB_ISBN_LABEL          to "ISBN: {isbn}",
        StringKeys.STU_LIB_REASON_LABEL        to "कारण: {reason}",
        StringKeys.STU_LIB_BOOK_DISCUSSIONS    to "पुस्तक चर्चाएँ",
        StringKeys.STU_LIB_BOOK_ID             to "पुस्तक ID",
        StringKeys.STU_LIB_LOAD_DISCUSSIONS    to "चर्चाएँ लोड करें",
        StringKeys.STU_LIB_NO_DISCUSSIONS      to "कोई चर्चा नहीं",
        StringKeys.STU_LIB_ENTER_BOOK_ID       to "चर्चाएँ देखने और जुड़ने के लिए एक पुस्तक ID दर्ज करें।",
        StringKeys.STU_LIB_WRITE_MESSAGE       to "एक संदेश लिखें",
        StringKeys.STU_LIB_POST                to "पोस्ट करें",
        // ── SchoolPeopleScreenV2 ──
        StringKeys.PPL_TITLE                   to "लोग",
        StringKeys.PPL_LINK_REQUESTS_TITLE     to "बाल लिंक अनुरोध",
        StringKeys.PPL_LINK_REQUESTS_SUB       to "छात्र रिकॉर्ड तक पहुँच का अनुरोध करने वाले अभिभावकों की समीक्षा करें",
        StringKeys.PPL_TAB_TEACHERS            to "शिक्षक",
        StringKeys.PPL_TAB_STUDENTS            to "छात्र",
        StringKeys.PPL_TAB_STAFF               to "गैर-शैक्षणिक स्टाफ",
        StringKeys.PPL_TAB_ALUMNI              to "पूर्व छात्र",
        StringKeys.PPL_ALUMNI_MGMT_TITLE       to "पूर्व छात्र प्रबंधन",
        StringKeys.PPL_ALUMNI_MGMT_SUB         to "पूर्व छात्र निर्देशिका, दान, मार्गदर्शन, और विश्लेषण देखें",
        StringKeys.PPL_ADD_TEACHER             to "शिक्षक जोड़ें",
        StringKeys.PPL_SEARCH_TEACHERS         to "नाम, भूमिका या विषय से खोजें",
        StringKeys.PPL_NO_TEACHERS             to "अभी तक कोई शिक्षक नहीं",
        StringKeys.PPL_NO_MATCHES              to "कोई मिलान नहीं",
        StringKeys.PPL_NO_TEACHERS_BODY        to "अपने पहले शिक्षक को जोड़ें ताकि वे साइन इन कर सकें और अपनी कक्षाएँ प्रबंधित कर सकें।",
        StringKeys.PPL_NO_TEACHER_MATCHES      to "कोई शिक्षक \"{query}\" से मेल नहीं खाता।",
        StringKeys.PPL_LOADING                 to "लोड हो रहा है…",
        StringKeys.PPL_LOAD_MORE               to "और लोड करें",
        StringKeys.PPL_UNNAMED_TEACHER         to "अनाम शिक्षक",
        StringKeys.PPL_ACTIVE                  to "सक्रिय",
        StringKeys.PPL_INACTIVE                to "निष्क्रिय",
        StringKeys.PPL_GRADES                  to "कक्षाएँ",
        StringKeys.PPL_NO_GRADES               to "कोई कक्षा निर्धारित नहीं",
        StringKeys.PPL_SUBJECTS                to "विषय",
        StringKeys.PPL_NO_SUBJECTS             to "कोई विषय निर्धारित नहीं",
        StringKeys.PPL_CLASSES                 to "कक्षाएँ",
        StringKeys.PPL_STUDENTS_LABEL          to "छात्र",
        StringKeys.PPL_ATTENDANCE_PCT          to "उपस्थिति {pct}%",
        StringKeys.PPL_ATTENDANCE_NONE         to "उपस्थिति —",
        StringKeys.PPL_NEVER_ACTIVE            to "कभी सक्रिय नहीं",
        StringKeys.PPL_ACTIVE_DATE             to "सक्रिय {date}",
        StringKeys.PPL_VIEW_PROFILE            to "प्रोफ़ाइल देखें",
        StringKeys.PPL_MORE_ACTIONS            to "अधिक क्रियाएँ",
        StringKeys.PPL_ASSIGN_CLASSES          to "कक्षाएँ निर्धारित करें",
        StringKeys.PPL_DEACTIVATE              to "निष्क्रिय करें",
        StringKeys.PPL_ADD_STUDENT             to "छात्र जोड़ें",
        StringKeys.PPL_IMPORT_CSV              to "CSV आयात करें",
        StringKeys.PPL_GRADUATE                to "स्नातक करें",
        StringKeys.PPL_SEARCH_STUDENTS         to "नाम, रोल नंबर या कोड से खोजें",
        StringKeys.PPL_NO_STUDENTS             to "अभी तक कोई छात्र नहीं",
        StringKeys.PPL_NO_STUDENTS_BODY        to "छात्र यहाँ दिखाई देंगे जब वे आपके स्कूल में नामांकित होंगे।",
        StringKeys.PPL_NO_STUDENT_MATCHES      to "कोई छात्र \"{query}\" से मेल नहीं खाता।",
        StringKeys.PPL_COHORT_ANALYTICS        to "समूह विश्लेषण",
        StringKeys.PPL_NO_COHORT_DATA          to "अभी तक कोई समूह डेटा नहीं",
        StringKeys.PPL_NO_COHORT_BODY          to "उपस्थिति और अंक आने पर छात्र जोखिम और सहभागिता विश्लेषण यहाँ दिखाई देगा।",
        StringKeys.PPL_RISK_DISTRIBUTION       to "छात्र जोखिम वितरण",
        StringKeys.PPL_CRITICAL                to "गंभीर",
        StringKeys.PPL_MEDIUM                  to "मध्यम",
        StringKeys.PPL_LOW                     to "कम",
        StringKeys.PPL_AT_RISK_STUDENTS        to "जोखिम में छात्र",
        StringKeys.PPL_SUBJECT_ENGAGEMENT      to "विषय सहभागिता",
        StringKeys.PPL_COHORT_COMPARISON       to "समूह तुलना",
        StringKeys.PPL_GRADE_N                 to "कक्षा {n}",
        StringKeys.PPL_MARK_ALUMNI             to "छात्रों को पूर्व छात्र के रूप में चिह्नित करें",
        StringKeys.PPL_MARK_ALUMNI_BODY        to "यह {count} फ़िल्टर किए गए छात्र(रों) को स्नातक के रूप में चिह्नित करेगा और उनके लिए पूर्व छात्र रिकॉर्ड बनाएगा।",
        StringKeys.PPL_GRADUATION_YEAR         to "स्नातक वर्ष",
        StringKeys.PPL_ADD_STAFF               to "स्टाफ जोड़ें",
        StringKeys.PPL_SEARCH_STAFF            to "नाम, भूमिका या विभाग से खोजें",
        StringKeys.PPL_NO_STAFF                to "अभी तक कोई स्टाफ नहीं",
        StringKeys.PPL_NO_STAFF_BODY           to "कार्यालय, लेखा, पुस्तकालय, परिवहन या सहायता स्टाफ जोड़ें ताकि वे यहाँ दिखाई दें।",
        StringKeys.PPL_NO_STAFF_MATCHES        to "कोई स्टाफ \"{query}\" से मेल नहीं खाता।",
        StringKeys.PPL_FULL_NAME               to "पूरा नाम",
        StringKeys.PPL_NAME_PH_TEACHER         to "उदा. आशा वर्मा",
        StringKeys.PPL_EMAIL_OR_PHONE          to "ईमेल या फ़ोन",
        StringKeys.PPL_EMAIL_PHONE_PH          to "teacher@school.edu या 98765 43210",
        StringKeys.PPL_INITIAL_PASSWORD        to "प्रारंभिक पासवर्ड",
        StringKeys.PPL_PASSWORD_PH             to "शिक्षक के साथ साइन इन करने के लिए साझा किया गया",
        StringKeys.PPL_OTP_HINT                to "यह शिक्षक अपने फ़ोन पर भेजे गए एक-बार कोड से साइन इन करेंगे।",
        StringKeys.PPL_ADD_STAFF_MEMBER        to "स्टाफ सदस्य जोड़ें",
        StringKeys.PPL_NAME_PH_STAFF           to "उदा. रमेश कुमार",
        StringKeys.PPL_ROLE                    to "भूमिका",
        StringKeys.PPL_ROLE_PH                 to "उदा. अकाउंटेंट, लाइब्रेरियन, सिक्योरिटी",
        StringKeys.PPL_DEPT_OPTIONAL           to "विभाग (वैकल्पिक)",
        StringKeys.PPL_DEPT_PH                 to "उदा. कार्यालय, परिवहन",
        StringKeys.PPL_PHONE_OPTIONAL          to "फ़ोन (वैकल्पिक)",
        StringKeys.PPL_PHONE_PH                to "98765 43210",
        StringKeys.PPL_EMAIL_OPTIONAL          to "ईमेल (वैकल्पिक)",
        StringKeys.PPL_EMAIL_PH                to "staff@school.edu",
        StringKeys.PPL_NAME_PH_STUDENT         to "उदा. आरव शर्मा",
        StringKeys.PPL_CLASS                   to "कक्षा",
        StringKeys.PPL_CLASS_PH                to "उदा. कक्षा 4",
        StringKeys.PPL_SECTION                 to "अनुभाग",
        StringKeys.PPL_SECTION_PH              to "A",
        StringKeys.PPL_ROLL_NUMBER             to "रोल नंबर",
        StringKeys.PPL_ROLL_PH                 to "उदा. 12",
        StringKeys.PPL_PARENT_PHONE            to "अभिभावक/संरक्षक फ़ोन (वैकल्पिक)",
        StringKeys.PPL_PARENT_PHONE_PH         to "उदा. 9876543210",
        StringKeys.PPL_IMPORT_STUDENTS_CSV     to "छात्र आयात करें (CSV)",
        StringKeys.PPL_IMPORT_INSTRUCTIONS     to "पहली पंक्ति हेडर होनी चाहिए। कॉलम: full_name, class_name, roll_number (आवश्यक); section, student_code (वैकल्पिक)।",
        StringKeys.PPL_CSV_CONTENT             to "CSV सामग्री",
        StringKeys.PPL_CSV_PH                  to "full_name,class_name,section,roll_number\nआरव शर्मा,कक्षा 4,A,12",
        StringKeys.PPL_IMPORT                  to "आयात करें",
        StringKeys.PPL_MASTERY                 to "महारत: {trend}",
        StringKeys.PPL_RISK_PCT                to "{risk}% जोखिम",
        // ── SchoolRecordsScreenV2 ──
        StringKeys.REC_TITLE                   to "रिकॉर्ड",
        StringKeys.REC_TAB_COVERAGE            to "कवरेज",
        StringKeys.REC_TAB_PACE                to "गति",
        StringKeys.REC_TAB_ATTENDANCE          to "उपस्थिति",
        StringKeys.REC_TAB_MARKS               to "अंक",
        StringKeys.REC_TAB_FEE                 to "शुल्क",
        StringKeys.REC_TAB_DOCUMENTS           to "दस्तावेज़",
        StringKeys.REC_DOC_LIBRARY_TITLE       to "दस्तावेज़ लाइब्रेरी",
        StringKeys.REC_DOC_LIBRARY_DESC        to "मीडिया स्टोरेज कॉन्फ़िगर होने पर परिपत्र, समयसारिणी और अवकाश सूची अपलोड की जा सकेंगी।",
        StringKeys.REC_NO_COVERAGE             to "अभी तक कोई कवरेज डेटा नहीं",
        StringKeys.REC_NO_COVERAGE_BODY        to "शिक्षकों द्वारा इकाइयाँ पूर्ण चिह्नित करने पर पाठ्यक्रम कवरेज यहाँ दिखाई देगा।",
        StringKeys.REC_OVERALL_COVERAGE        to "समग्र पाठ्यक्रम कवरेज",
        StringKeys.REC_BY_DEPARTMENT           to "विभाग अनुसार",
        StringKeys.REC_LAGGING_CLASSES         to "पिछड़ी कक्षाएँ",
        StringKeys.REC_BEHIND                  to "{pct}% पीछे",
        StringKeys.REC_MILESTONES              to "शैक्षणिक मील के पत्थर",
        StringKeys.REC_VERIFIED                to "सत्यापित",
        StringKeys.REC_NO_ATTENDANCE           to "अभी तक कोई उपस्थिति नहीं",
        StringKeys.REC_NO_ATTENDANCE_BODY      to "शिक्षकों द्वारा दैनिक रजिस्टर भरने पर स्कूल-स्तरीय उपस्थिति यहाँ जुटेगी।",
        StringKeys.REC_LATEST_REGISTER         to "नवीनतम रजिस्टर",
        StringKeys.REC_PRESENT_PCT             to "{pct}% उपस्थित",
        StringKeys.REC_PRESENT                 to "उपस्थित",
        StringKeys.REC_ABSENT                  to "अनुपस्थित",
        StringKeys.REC_LATE                    to "विलंबित",
        StringKeys.REC_TOTAL                   to "कुल",
        StringKeys.REC_BY_CLASS                to "कक्षा अनुसार",
        StringKeys.REC_NO_ASSESSMENTS          to "अभी तक कोई मूल्यांकन नहीं",
        StringKeys.REC_NO_ASSESSMENTS_BODY     to "शिक्षकों द्वारा मूल्यांकन बनाकर अंक दर्ज करने पर परीक्षा औसत यहाँ जुटेगा।",
        StringKeys.REC_OVERALL_AVG             to "समग्र औसत",
        StringKeys.REC_ASSESSMENT_COUNT        to "{count} मूल्यांकन{s}",
        StringKeys.REC_PUBLISHED               to "प्रकाशित",
        StringKeys.REC_DRAFT                   to "प्रारूप",
        StringKeys.REC_AVG                     to "औसत {avg} / {max}",
        StringKeys.REC_GRADED                  to "{pct}% • {count} ग्रेड किए गए",
        StringKeys.REC_NOT_GRADED              to "अभी तक ग्रेड नहीं किया गया",
        StringKeys.REC_NO_FEES                 to "अभी तक कोई शुल्क रिकॉर्ड नहीं",
        StringKeys.REC_NO_FEES_BODY            to "इस स्कूल के लिए शुल्क रिकॉर्ड बनने पर संग्रह, बकाया और अतिदेय अनुस्मारक यहाँ दिखाई देंगे।",
        StringKeys.REC_LEDGER                  to "लेजर ({currency})",
        StringKeys.REC_PAID                    to "भुगतान किया",
        StringKeys.REC_DUE                     to "देय",
        StringKeys.REC_OVERDUE                 to "अतिदेय",
        StringKeys.REC_RECENT                  to "हाल ही का",
        StringKeys.REC_DUE_DATE                to "{category} • देय तिथि {date}",
        StringKeys.REC_NO_PACE                 to "अभी तक कोई गति डेटा नहीं",
        StringKeys.REC_NO_PACE_BODY            to "पाठ्यक्रम ट्रैकिंग शुरू होने पर गति स्नैपशॉट यहाँ दिखाई देंगे।",
        StringKeys.REC_RECALCULATE             to "गति पुनः गणना करें",
        StringKeys.REC_ACTIVE_ALERTS           to "सक्रिय अलर्ट",
        StringKeys.REC_AI_RECONFIRMED          to "AI द्वारा पुष्टि",
        StringKeys.REC_RESOLVE                 to "हल करें",
        StringKeys.REC_PACE_SNAPSHOTS          to "गति स्नैपशॉट",
        StringKeys.REC_TOPICS_COVERED          to "{covered}/{total} विषय कवर किए गए",
        StringKeys.REC_EXPECTED                to "अपेक्षित: {pct}%",
        // ── AlumniScreen ──
        StringKeys.ALM_TITLE                   to "पूर्व छात्र प्रबंधन",
        StringKeys.ALM_TAB_DIRECTORY           to "निर्देशिका",
        StringKeys.ALM_TAB_PENDING             to "लंबित",
        StringKeys.ALM_TAB_CAMPAIGNS           to "अभियान",
        StringKeys.ALM_TAB_DONATIONS           to "दान",
        StringKeys.ALM_TAB_MENTORSHIP          to "मार्गदर्शन",
        StringKeys.ALM_TAB_ANALYTICS           to "विश्लेषण",
        StringKeys.ALM_ADD_ALUMNI              to "पूर्व छात्र जोड़ें",
        StringKeys.ALM_BULK_IMPORT             to "बल्क आयात",
        StringKeys.ALM_NO_ALUMNI               to "अभी तक कोई पूर्व छात्र नहीं",
        StringKeys.ALM_NO_ALUMNI_BODY          to "पूर्व छात्र मैन्युअल रूप से जोड़ें या बल्क आयात का उपयोग करें",
        StringKeys.ALM_FULL_NAME_REQ           to "पूरा नाम *",
        StringKeys.ALM_NAME_PH                 to "उदा. प्रिया शर्मा",
        StringKeys.ALM_GRAD_YEAR_REQ           to "स्नातक वर्ष *",
        StringKeys.ALM_GRAD_YEAR_PH            to "2024",
        StringKeys.ALM_STUDENT_ID_OPT          to "छात्र आईडी (वैकल्पिक)",
        StringKeys.ALM_STUDENT_ID_PH           to "ADM-2020-001",
        StringKeys.ALM_EMAIL                   to "ईमेल",
        StringKeys.ALM_EMAIL_PH                to "priya@example.com",
        StringKeys.ALM_PHONE                   to "फ़ोन",
        StringKeys.ALM_PHONE_PH                to "+91 98765 43210",
        StringKeys.ALM_PROFESSION              to "पेशा",
        StringKeys.ALM_PROFESSION_PH           to "सॉफ्टवेयर इंजीनियर",
        StringKeys.ALM_COMPANY                 to "कंपनी",
        StringKeys.ALM_COMPANY_PH              to "Google",
        StringKeys.ALM_CITY                    to "शहर",
        StringKeys.ALM_CITY_PH                 to "बेंगलुरु",
        StringKeys.ALM_ADD                     to "जोड़ें",
        StringKeys.ALM_BULK_IMPORT_TITLE       to "बल्क आयात पूर्व छात्र",
        StringKeys.ALM_BULK_IMPORT_INSTR       to "CSV डेटा पेस्ट करें। प्रत्येक पंक्ति: name,graduationYear,email,phone,profession,company,city",
        StringKeys.ALM_CSV_PH                  to "Priya Sharma,2024,priya@example.com,9876543210,Engineer,Google,Bengaluru\nRahul Verma,2023,...",
        StringKeys.ALM_ROWS_READY              to "{count} पंक्ति(यों) को आयात के लिए तैयार",
        StringKeys.ALM_IMPORT                  to "आयात करें",
        StringKeys.ALM_IMPORT_WITH_COUNT       to "आयात करें ({count})",
        StringKeys.ALM_NO_PENDING              to "कोई लंबित सत्यापन नहीं",
        StringKeys.ALM_NO_PENDING_BODY         to "सभी पूर्व छात्र पंजीकरण समीक्षित हो चुके हैं",
        StringKeys.ALM_BATCH                   to "बैच {year}",
        StringKeys.ALM_APPROVE                 to "स्वीकृत करें",
        StringKeys.ALM_DECLINE                 to "अस्वीकृत करें",
        StringKeys.ALM_NO_CAMPAIGNS            to "अभी तक कोई अभियान नहीं",
        StringKeys.ALM_NO_CAMPAIGNS_BODY       to "पूर्व छात्रों को जोड़ने के लिए दान अभियान बनाएँ",
        StringKeys.ALM_CAMPAIGN_PROGRESS       to "₹{raised} / ₹{target} ({pct}%) • {donors} दाता",
        StringKeys.ALM_STATUS                  to "स्थिति: {status}",
        StringKeys.ALM_NO_DONATIONS            to "कोई दान दर्ज नहीं",
        StringKeys.ALM_NO_DONATIONS_BODY       to "पूर्व छात्र विवरण स्क्रीन से दान लॉग करें",
        StringKeys.ALM_CAMPAIGN_LABEL          to "अभियान: {title}",
        StringKeys.ALM_DATE                    to "तिथि: {date}",
        StringKeys.ALM_80G_ELIGIBLE            to "80G पात्र • रसीद: {receipt}",
        StringKeys.ALM_RECEIPT_PENDING         to "लंबित",
        StringKeys.ALM_NO_ANALYTICS            to "कोई विश्लेषण डेटा नहीं",
        StringKeys.ALM_OVERVIEW                to "अवलोकन",
        StringKeys.ALM_TOTAL_ALUMNI            to "कुल पूर्व छात्र",
        StringKeys.ALM_ACTIVE_90               to "सक्रिय (90 दिन)",
        StringKeys.ALM_PENDING_VERIFICATIONS   to "लंबित सत्यापन",
        StringKeys.ALM_ENGAGEMENT_RATE         to "सहभागिता दर",
        StringKeys.ALM_TOTAL_DONATIONS         to "कुल दान",
        StringKeys.ALM_ACTIVE_CAMPAIGNS        to "सक्रिय अभियान",
        StringKeys.ALM_ACTIVE_MENTORSHIPS      to "सक्रिय मार्गदर्शन",
        StringKeys.ALM_BY_GRAD_YEAR            to "स्नातक वर्ष अनुसार",
        StringKeys.ALM_BY_PROFESSION           to "पेशा अनुसार",
        StringKeys.ALM_BY_CITY                 to "शहर अनुसार",
        StringKeys.ALM_NO_MENTORSHIPS          to "कोई सक्रिय मार्गदर्शन नहीं",
        StringKeys.ALM_NO_MENTORSHIPS_BODY     to "पूर्व छात्रों द्वारा छात्रों का मार्गदर्शन शुरू करने पर यहाँ दिखाई देगा",
        StringKeys.ALM_MENTORING               to "मार्गदर्शन: {name}",
        StringKeys.ALM_STARTED                 to "प्रारंभ: {date}",
        StringKeys.ALM_SESSIONS                to "सत्र: {count}",
        StringKeys.ALM_NOTES                   to "टिप्पणियाँ: {notes}",
        StringKeys.ALM_MENTORSHIP_REQUESTS     to "मार्गदर्शन अनुरोध",
        StringKeys.ALM_NO_MENTOR_REQUESTS      to "कोई मार्गदर्शन अनुरोध नहीं",
        StringKeys.ALM_NO_MENTOR_REQUESTS_BODY to "पूर्व छात्र मार्गदर्शन के लिए छात्र अनुरोध यहाँ दिखाई देंगे",
        StringKeys.ALM_FROM                    to "से: {name}",
        StringKeys.ALM_REQUESTED_BY            to "अनुरोधकर्ता: {name}",
        StringKeys.ALM_EXPERTISE               to "विशेषज्ञता: {area}",
        StringKeys.ALM_MESSAGE                 to "संदेश: {msg}",
        StringKeys.ALM_MENTOR                  to "मार्गदर्शक",
        StringKeys.ALM_MENTOR_EXPERTISE        to "मार्गदर्शक — {area}",
        // ── SchoolHomeScreenV2 ──
        StringKeys.HOME_NOTIF_RATIONALE_TITLE   to "सूचित रहें",
        StringKeys.HOME_NOTIF_RATIONALE_MSG     to "स्कूल कार्यक्रमों, उपस्थिति और संस्थागत अलर्ट के बारे में महत्वपूर्ण अपडेट प्राप्त करने के लिए नोटिफिकेशन सक्षम करें।",
        StringKeys.HOME_NOTIF_ENABLE            to "सक्षम करें",
        StringKeys.HOME_NOTIF_NOT_NOW           to "अभी नहीं",
        StringKeys.HOME_WELCOME                 to "स्वागत है",
        StringKeys.HOME_YOUR_SCHOOL             to "आपका स्कूल",
        StringKeys.HOME_NOTIFICATIONS           to "नोटिफिकेशन",
        StringKeys.HOME_QA_ANNOUNCEMENT         to "घोषणा",
        StringKeys.HOME_QA_CREATE_EVENT         to "कार्यक्रम बनाएँ",
        StringKeys.HOME_QA_SEND_NOTICE          to "नोटिस भेजें",
        StringKeys.HOME_QA_REPORTS              to "रिपोर्ट",
        StringKeys.HOME_QA_TRANSPORT            to "परिवहन",
        StringKeys.HOME_SMART_INSIGHTS          to "स्मार्ट अंतर्दृष्टि",
        StringKeys.HOME_SCHOOL_PULSE            to "स्कूल पल्स",
        StringKeys.HOME_PULSE_METRICS_EMPTY     to "आपके स्कूल द्वारा डेटा दर्ज करने पर मेट्रिक्स दिखाई देंगे।",
        StringKeys.HOME_PULSE_OUT_OF            to "/ 100",
        StringKeys.HOME_KEY_METRICS             to "प्रमुख मेट्रिक्स",
        StringKeys.HOME_CAMPUS_HEALTH           to "कैम्पस स्वास्थ्य",
        StringKeys.HOME_ATTENDANCE_OVER         to "{count} {period} में उपस्थिति",
        StringKeys.HOME_NO_ATTENDANCE_DATA      to "अभी तक कोई उपस्थिति डेटा नहीं",
        StringKeys.HOME_ATTENDANCE_TRENDS_EMPTY to "दैनिक रिकॉर्ड कैप्चर होने पर उपस्थिति रुझान दिखाई देंगे।",
        StringKeys.HOME_FEE_COLLECTION          to "शुल्क संग्रह",
        StringKeys.HOME_COLLECTION_RATE         to "संग्रह दर {pct}%",
        StringKeys.HOME_COLLECTED               to "एकत्रित",
        StringKeys.HOME_PENDING                 to "लंबित",
        StringKeys.HOME_PARENT_ENGAGEMENT       to "अभिभावक सहभागिता",
        StringKeys.HOME_PARENT_ENGAGEMENT_SUB   to "{pct}% सक्रिय · {active}/{total} अभिभावक",
        StringKeys.HOME_MOST_ENGAGED            to "सर्वाधिक सहभागी: {class}",
        StringKeys.HOME_CLASS_LEADERBOARD       to "कक्षा लीडरबोर्ड",
        StringKeys.HOME_COMMUNICATION           to "संचार",
        StringKeys.HOME_UNREAD                  to "अपठित",
        StringKeys.HOME_QUERIES                 to "प्रश्न",
        StringKeys.HOME_ANNOUNCEMENTS           to "घोषणाएँ",
        StringKeys.HOME_ACKNOWLEDGEMENTS        to "स्वीकृतियाँ",
        StringKeys.HOME_EVENTS                  to "कार्यक्रम",
        StringKeys.HOME_VIEW_CALENDAR           to "कैलेंडर देखें →",
        StringKeys.HOME_RECENTLY_COMPLETED      to "हाल ही में पूर्ण",
        StringKeys.HOME_TODAY                   to "आज",
        StringKeys.HOME_TOMORROW                to "कल",
        StringKeys.HOME_IN_DAYS                 to "{days} दिन में",
        StringKeys.HOME_CAL_AT_GLANCE           to "कैलेंडर एक नज़र में",
        StringKeys.HOME_OPEN_CALENDAR           to "कैलेंडर खोलें →",
        StringKeys.HOME_THIS_WEEK               to "इस सप्ताह",
        StringKeys.HOME_DRAFTS                  to "ड्राफ्ट",
        StringKeys.HOME_NEXT_HOLIDAY            to "अगली छुट्टी",
        StringKeys.HOME_UPCOMING_EVENTS         to "आगामी कार्यक्रम",
        StringKeys.HOME_SEE_ALL                 to "सभी देखें →",
        StringKeys.HOME_DRAFT                   to "ड्राफ्ट",
        StringKeys.HOME_CONFLICT                to "टकराव",
        StringKeys.HOME_TEACHER_SPOTLIGHT       to "⭐ शिक्षक स्पॉटलाइट",
        StringKeys.HOME_SCORE                   to "स्कोर",
        StringKeys.HOME_STUDENT_ACHIEVEMENTS    to "छात्र उपलब्धियाँ",
        StringKeys.HOME_CELEBRATIONS            to "उत्सव",
        StringKeys.HOME_TODAY_LABEL             to "आज",
        StringKeys.HOME_UPCOMING_LABEL          to "आगामी",
        StringKeys.HOME_TEACHER                 to "शिक्षक",
        StringKeys.HOME_STUDENT                 to "छात्र",
        StringKeys.HOME_BIRTHDAY_TODAY          to "🎉 आज",
        StringKeys.HOME_BIRTHDAY_IN_DAYS        to "{days}दिन में",
        StringKeys.HOME_LIVE_ACTIVITY           to "लाइव गतिविधि",
        StringKeys.HOME_SCHOOL_ANALYTICS        to "स्कूल विश्लेषण",
        StringKeys.HOME_ANALYTICS_DESC          to "उपस्थिति, शैक्षणिक और विकास अंतर्दृष्टि",
        StringKeys.HOME_EXPLORE_ANALYTICS       to "विश्लेषण देखें",
        StringKeys.HOME_RISK_MONITOR            to "छात्र जोखिम मॉनिटर",
        StringKeys.HOME_RISK_MONITOR_DESC       to "जल्दी पहचानें किन छात्रों को ध्यान चाहिए",
        StringKeys.HOME_OPEN_MONITOR            to "मॉनिटर खोलें",
        StringKeys.HOME_REPORT_PUBLISH          to "रिपोर्ट कार्ड प्रकाशन",
        StringKeys.HOME_REPORT_PUBLISH_DESC     to "समीक्षा निरीक्षण और स्वीकृत AI रिपोर्ट कार्ड ड्राफ्ट प्रकाशित करें",
        StringKeys.HOME_OPEN_PUBLISHING         to "प्रकाशन खोलें",
        StringKeys.HOME_REPORT_EFFECTIVENESS    to "रिपोर्ट कार्ड प्रभावशीलता",
        StringKeys.HOME_REPORT_EFFECTIVENESS_DESC to "लर्निंग फ्लाईव्हील चलाएँ और प्रभावशीलता पूर्व-मान देखें",
        StringKeys.HOME_OPEN_EFFECTIVENESS      to "प्रभावशीलता खोलें",
        StringKeys.HOME_EVENT_REGISTRATION      to "कार्यक्रम पंजीकरण",
        StringKeys.HOME_EVENT_REG_DESC          to "PTM स्लॉट, कार्यक्रम क्षमता और पंजीकरण प्रबंधित करें",
        StringKeys.HOME_MANAGE                  to "प्रबंधित करें →",
        // ── ClassesSubjectsScreenV2 ──
        StringKeys.CS_TITLE                     to "कक्षाएँ और विषय",
        StringKeys.CS_TAB_CLASSES               to "कक्षाएँ",
        StringKeys.CS_TAB_SUBJECTS              to "विषय",
        StringKeys.CS_TAB_SCHEDULE              to "समयसारणी",
        StringKeys.CS_TAB_EXCEPTIONS            to "अपवाद और अनुरोध",
        StringKeys.CS_NO_CLASSES                to "अभी तक कोई कक्षा नहीं",
        StringKeys.CS_NO_CLASSES_BODY           to "शुरू करने के लिए अपनी पहली कक्षा जोड़ें।",
        StringKeys.CS_CLASSES                   to "कक्षाएँ",
        StringKeys.CS_ADD_CLASS                 to "कक्षा जोड़ें",
        StringKeys.CS_EDIT_CLASS                to "कक्षा संपादित करें",
        StringKeys.CS_DELETE_CLASS              to "{name} हटाएँ?",
        StringKeys.CS_DELETE_CLASS_MSG          to "इससे इस कक्षा के सभी विषय भी हट जाएँगे। इसे पूर्ववत नहीं किया जा सकता।",
        StringKeys.CS_NO_SECTIONS               to "कोई अनुभाग नहीं",
        StringKeys.CS_SUBJECTS_COUNT            to "{count} विषय",
        StringKeys.CS_EDIT                      to "संपादित करें",
        StringKeys.CS_CLASS_CODE                to "कक्षा कोड",
        StringKeys.CS_CLASS_NAME                to "कक्षा नाम",
        StringKeys.CS_SECTIONS_LABEL            to "अनुभाग (कॉमा से अलग)",
        StringKeys.CS_CANCEL                    to "रद्द करें",
        StringKeys.CS_SAVE                      to "सहेजें",
        StringKeys.CS_DELETE                    to "हटाएँ",
        StringKeys.CS_CREATE                    to "बनाएँ",
        StringKeys.CS_BACK                      to "← पीछे",
        StringKeys.CS_REMOVE                    to "हटाएँ",
        StringKeys.CS_NO_CLASSES_AVAIL          to "कोई कक्षा उपलब्ध नहीं",
        StringKeys.CS_NO_CLASSES_AVAIL_BODY     to "पहले कक्षा टैब में कक्षाएँ जोड़ें।",
        StringKeys.CS_SUBJECTS                  to "विषय",
        StringKeys.CS_CLASS_SUBJECTS            to "{name} — विषय",
        StringKeys.CS_ADD                       to "जोड़ें",
        StringKeys.CS_NO_SUBJECTS               to "अभी तक कोई विषय नहीं",
        StringKeys.CS_NO_SUBJECTS_BODY          to "इस कक्षा में एक विषय जोड़ें।",
        StringKeys.CS_ADD_SUBJECT               to "विषय जोड़ें",
        StringKeys.CS_EDIT_SUBJECT              to "विषय संपादित करें",
        StringKeys.CS_DELETE_SUBJECT            to "{name} हटाएँ?",
        StringKeys.CS_DELETE_SUBJECT_MSG        to "यह विषय कक्षा से हटा दिया जाएगा।",
        StringKeys.CS_SUBJECT_NAME              to "विषय नाम",
        StringKeys.CS_SUBJECT_CODE              to "विषय कोड",
        StringKeys.CS_STEP_STRUCTURE            to "1. दिन संरचना",
        StringKeys.CS_STEP_ASSIGN               to "2. नियुक्ति",
        StringKeys.CS_STEP_REVIEW               to "3. समीक्षा",
        StringKeys.CS_DAY_STRUCTURE_TEMPLATE    to "दिन संरचना टेम्पलेट",
        StringKeys.CS_DAY_STRUCTURE_DESC        to "नीचे हर तत्व को अनुकूलित करें — जोड़ें, हटाएँ, पुनर्व्यवस्थित करें, समय और लेबल संपादित करें।",
        StringKeys.CS_IMPORT                    to "📥 फ़ोटो / PDF / टेक्स्ट से आयात करें",
        StringKeys.CS_TEMPLATE_NAME             to "टेम्पलेट नाम",
        StringKeys.CS_APPLICABLE_DAYS           to "लागू दिन",
        StringKeys.CS_LIVE_PREVIEW              to "लाइव पूर्वावलोकन",
        StringKeys.CS_SLOTS_COUNT               to "स्लॉट ({count})",
        StringKeys.CS_ADD_SLOT                  to "+ स्लॉट जोड़ें",
        StringKeys.CS_SAVE_TEMPLATE             to "टेम्पलेट सहेजें और जारी रखें →",
        StringKeys.CS_EXISTING_CONFIGS          to "मौजूदा कॉन्फ़िगरेशन",
        StringKeys.CS_ACTIVE                    to "सक्रिय",
        StringKeys.CS_INACTIVE                  to "निष्क्रिय",
        StringKeys.CS_CONFIG_DETAILS            to "दिन: {days}  ·  स्तर: {level}  ·  {count} स्लॉट",
        StringKeys.CS_IMPORT_SCHEDULE           to "समयसारणी आयात करें",
        StringKeys.CS_CHOOSE_IMPORT             to "आयात स्रोत चुनें:",
        StringKeys.CS_PHOTO_OCR                 to "फ़ोटो (OCR)",
        StringKeys.CS_PHOTO_OCR_DESC            to "फ़ोटो लें या गैलरी से चुनें — टेक्स्ट स्वचालित रूप से निकाला जाएगा।",
        StringKeys.CS_PDF_DOCUMENT              to "PDF दस्तावेज़",
        StringKeys.CS_PDF_DESC                  to "PDF फ़ाइल चुनें — समयसारणी टेक्स्ट निकाला जाएगा।",
        StringKeys.CS_PASTE_TEXT                to "टेक्स्ट पेस्ट करें",
        StringKeys.CS_PASTE_TEXT_DESC           to "किसी भी स्रोत से समयसारणी टेक्स्ट पेस्ट करें — हम इसे स्लॉट में पार्स करेंगे।",
        StringKeys.CS_PHOTO_OCR_LABEL           to "फ़ोटो OCR",
        StringKeys.CS_PDF_IMPORT_LABEL          to "PDF आयात",
        StringKeys.CS_AI_READING                to "AI आपकी समयसारणी पढ़ रहा है...",
        StringKeys.CS_AI_VISION_DESC            to "यह आपकी छवि से टेक्स्ट निकालने के लिए AI विज़न का उपयोग करता है।",
        StringKeys.CS_AI_VISION_OCR             to "{label} — AI विज़न OCR",
        StringKeys.CS_PHOTO_OCR_BODY            to "मुद्रित समयसारणी की फ़ोटो लें या छवि चुनें। हमारा AI स्वचालित रूप से समयसारणी निकालेगा।",
        StringKeys.CS_PDF_BODY                  to "PDF फ़ाइल चुनें — समयसारणी टेक्स्ट निकाला जाएगा।",
        StringKeys.CS_PICK_PHOTO                to "फ़ोटो चुनें",
        StringKeys.CS_PICK_PDF                  to "PDF चुनें",
        StringKeys.CS_USE_PASTE                 to "इसके बजाय टेक्स्ट पेस्ट करें",
        StringKeys.CS_PASTE_BELOW               to "अपना समयसारणी टेक्स्ट नीचे पेस्ट करें।",
        StringKeys.CS_SUPPORTED_FORMATS         to "समर्थित प्रारूप: '08:00-08:40 Period 1' या '08:00 08:40 English' (प्रति पंक्ति एक स्लॉट)",
        StringKeys.CS_TIMETABLE_TEXT            to "समयसारणी टेक्स्ट",
        StringKeys.CS_PARSE_FILL                to "पार्स और भरें",
        StringKeys.CS_AI_PARSE                  to "AI पार्स",
        StringKeys.CS_PARSE_ERROR               to "कोई स्लॉट पार्स नहीं किया जा सका। सुनिश्चित करें कि प्रत्येक पंक्ति में समय सीमा (जैसे 08:00-08:40) और लेबल हो।",
        StringKeys.CS_PASTE_FIRST               to "कृपया पहले कुछ समयसारणी टेक्स्ट पेस्ट करें।",
        StringKeys.CS_PDF_NOT_AVAILABLE         to "PDF टेक्स्ट निष्कर्षण अभी उपलब्ध नहीं है। कृपया अपने PDF से समयसारणी टेक्स्ट कॉपी करें और 'टेक्स्ट पेस्ट करें' मोड का उपयोग करें।",
        StringKeys.CS_SLOT_LABEL                to "स्लॉट लेबल",
        StringKeys.CS_START                     to "प्रारंभ",
        StringKeys.CS_END                       to "अंत",
        StringKeys.CS_NO_DAY_STRUCTURE          to "कोई दिन संरचना नहीं मिली",
        StringKeys.CS_NO_DAY_STRUCTURE_BODY     to "आप नीचे मैन्युअल रूप से पीरियड जोड़ सकते हैं, या दिन संरचना टेम्पलेट बनाने के लिए चरण 1 पर वापस जाएँ।",
        StringKeys.CS_NO_CLASSES_FOUND          to "कोई कक्षा नहीं मिली। पहले कक्षा टैब में कक्षाएँ जोड़ें।",
        StringKeys.CS_SELECT_DAY                to "दिन चुनें",
        StringKeys.CS_DAY_CLASS_SECTION         to "{day} — {class} · {section}",
        StringKeys.CS_NO_PERIODS                to "अभी तक कोई पीरियड नहीं",
        StringKeys.CS_NO_PERIODS_BODY           to "इस दिन के लिए शिक्षक और विषय नियुक्त करने हेतु नीचे \"पीरियड जोड़ें\" टैप करें।",
        StringKeys.CS_PERIODS_ON_DAY            to "{day} को {count} पीरियड",
        StringKeys.CS_ADD_PERIOD                to "+ पीरियड जोड़ें",
        StringKeys.CS_SLOTS_ASSIGNED            to "{total} में से {assigned} स्लॉट नियुक्त",
        StringKeys.CS_OTHER_PERIODS             to "अन्य पीरियड (दिन संरचना में नहीं)",
        StringKeys.CS_QUICK_ACTIONS             to "त्वरित क्रियाएँ",
        StringKeys.CS_COPY_DAY_TO_ALL           to "{day} को सभी दिनों में कॉपी करें",
        StringKeys.CS_COPY_FROM_CLASS           to "किसी अन्य कक्षा से कॉपी करें",
        StringKeys.CS_REVIEW_BTN                to "समीक्षा →",
        StringKeys.CS_REMOVE_ASSIGNMENT         to "नियुक्ति हटाएँ?",
        StringKeys.CS_REMOVE_ASSIGNMENT_MSG     to "इससे इस स्लॉट से शिक्षक हट जाएगा।",
        StringKeys.CS_SLOT_N                    to "स्लॉट {n}",
        StringKeys.CS_ROOM_N                    to "कक्ष {room}",
        StringKeys.CS_TAP_TO_ASSIGN             to "शिक्षक और विषय नियुक्त करने हेतु टैप करें",
        StringKeys.CS_ASSIGNED                  to "नियुक्त",
        StringKeys.CS_EMPTY                     to "रिक्त",
        StringKeys.CS_TEACHER                    to "शिक्षक",
        StringKeys.CS_SELECT_TEACHER            to "शिक्षक चुनें",
        StringKeys.CS_NO_TEACHERS               to "अभी तक कोई शिक्षक नहीं",
        StringKeys.CS_ADD_NEW_TEACHER           to "+ नया शिक्षक जोड़ें",
        StringKeys.CS_SUBJECT_LABEL             to "विषय",
        StringKeys.CS_SELECT_SUBJECT            to "विषय चुनें",
        StringKeys.CS_NO_SUBJECTS_CLASS         to "इस कक्षा के लिए अभी तक कोई विषय नहीं",
        StringKeys.CS_ADD_NEW_SUBJECT           to "+ नया विषय जोड़ें",
        StringKeys.CS_ROOM                      to "कक्ष",
        StringKeys.CS_UPDATE                    to "अपडेट करें",
        StringKeys.CS_ASSIGN                    to "नियुक्त करें",
        StringKeys.CS_COPY_DAY_CONFIRM          to "{day} को सभी दिनों में कॉपी करें?",
        StringKeys.CS_COPY_DAY_MSG              to "यह {day} के सभी असाइनमेंट को इनमें कॉपी करेगा: {targets}।",
        StringKeys.CS_COPY                      to "कॉपी करें",
        StringKeys.CS_COPY_FROM_CLASS_TITLE     to "किसी अन्य कक्षा से कॉपी करें",
        StringKeys.CS_COPY_FROM_CLASS_DESC      to "सभी दिनों में किसी स्रोत कक्षा से सभी पीरियड को {class} में कॉपी करें।",
        StringKeys.CS_NO_OTHER_CLASSES          to "कॉपी करने के लिए कोई अन्य कक्षा उपलब्ध नहीं।",
        StringKeys.CS_WEEKLY_OVERVIEW           to "साप्ताहिक अवलोकन",
        StringKeys.CS_NO_TIMETABLE              to "समीक्षा करने हेतु कोई समयसारणी नहीं",
        StringKeys.CS_NO_TIMETABLE_BODY         to "चरण 2 पर वापस जाएँ और पहले कुछ पीरियड जोड़ें।",
        StringKeys.CS_PERIODS_LABEL             to "पीरियड",
        StringKeys.CS_CLASSES_LABEL             to "कक्षाएँ",
        StringKeys.CS_TEACHERS_LABEL            to "शिक्षक",
        StringKeys.CS_DAYS_LABEL                to "दिन",
        StringKeys.CS_CONFLICTS_DETECTED        to "⚠ टकराव का पता चला",
        StringKeys.CS_DONE_REVIEW               to "पूर्ण (टकराव समीक्षा)",
        StringKeys.CS_DONE                      to "✓ पूर्ण",
        StringKeys.CS_NEW_TEACHER               to "नया शिक्षक",
        StringKeys.CS_FULL_NAME                 to "पूरा नाम",
        StringKeys.CS_EMAIL_PHONE               to "ईमेल या फ़ोन",
        StringKeys.CS_NEW_SUBJECT               to "नया विषय",
        StringKeys.CS_EXCEPTIONS                to "अपवाद",
        StringKeys.CS_PENDING                   to "लंबित",
        StringKeys.CS_APPROVED                  to "स्वीकृत",
        StringKeys.CS_REJECTED                  to "अस्वीकृत",
        StringKeys.CS_PERIOD_EXCEPTIONS         to "पीरियड अपवाद",
        StringKeys.CS_LOAD_EXCEPTIONS           to "अपवाद लोड करें",
        StringKeys.CS_NO_EXCEPTIONS             to "कोई अपवाद नहीं",
        StringKeys.CS_NO_EXCEPTIONS_BODY        to "एक-बार के पीरियड ओवरराइड बनाने हेतु 'अपवाद जोड़ें' टैप करें।",
        StringKeys.CS_ADD_EXCEPTION             to "+ अपवाद जोड़ें",
        StringKeys.CS_CHANGE_REQUESTS           to "परिवर्तन अनुरोध",
        StringKeys.CS_LOAD                      to "लोड करें",
        StringKeys.CS_NO_REQUESTS               to "कोई अनुरोध नहीं",
        StringKeys.CS_NO_REQUESTS_BODY          to "शिक्षकों के परिवर्तन अनुरोध यहाँ दिखाई देंगे।",
        StringKeys.CS_DELETE_EXCEPTION          to "अपवाद हटाएँ?",
        StringKeys.CS_DELETE_EXCEPTION_MSG      to "इससे पीरियड ओवरराइड हट जाएगा।",
        StringKeys.CS_ADD_EXCEPTION_TITLE       to "अपवाद जोड़ें",
        StringKeys.CS_DATE                      to "तारीख",
        StringKeys.CS_KIND                      to "प्रकार",
        StringKeys.CS_NOTE                      to "टिप्पणी",
        StringKeys.CS_DAY_LABEL                 to "दिन: {day} {start}–{end}",
        StringKeys.CS_REASON_LABEL              to "कारण: {reason}",
        StringKeys.CS_ADMIN_NOTE                to "व्यवस्थापक टिप्पणी",
        StringKeys.CS_APPROVE                   to "स्वीकृत करें",
        StringKeys.CS_REJECT_BTN                to "अस्वीकृत करें",
        StringKeys.CS_REVIEW                    to "समीक्षा करें",
        StringKeys.CS_WEEKDAY_MON               to "सोम",
        StringKeys.CS_WEEKDAY_TUE               to "मंगल",
        StringKeys.CS_WEEKDAY_WED               to "बुध",
        StringKeys.CS_WEEKDAY_THU               to "गुरु",
        StringKeys.CS_WEEKDAY_FRI               to "शुक्र",
        StringKeys.CS_WEEKDAY_SAT               to "शनि",
        StringKeys.CS_WEEKDAY_SUN               to "रवि",
    )

    private val hi: Map<String, String> = hiPart1() + hiPart2()

    // Placeholder maps for other languages — will be filled with translations.
    // For now, they share the same keys as English so the parity test passes.
    // Translations will be added incrementally per the translation quality review process.
    private val bn: Map<String, String> = en.mapValues { it.value }
    private val ta: Map<String, String> = en.mapValues { it.value }
    private val te: Map<String, String> = en.mapValues { it.value }
    private val mr: Map<String, String> = en.mapValues { it.value }
    private val gu: Map<String, String> = en.mapValues { it.value }
    private val kn: Map<String, String> = en.mapValues { it.value }
    private val ml: Map<String, String> = en.mapValues { it.value }
    private val pa: Map<String, String> = en.mapValues { it.value }

    private val maps: Map<String, Map<String, String>> = mapOf(
        "en" to en, "hi" to hi, "bn" to bn, "ta" to ta, "te" to te,
        "mr" to mr, "gu" to gu, "kn" to kn, "ml" to ml, "pa" to pa,
    )

    fun get(key: String, locale: String): String {
        return maps[locale]?.get(key)
            ?: maps["en"]?.get(key)
            ?: key
    }

    fun getPlural(key: String, locale: String, count: Int): String {
        val pluralKey = if (count == 1) key else "${key}_plural"
        val resolvedKey = if (maps[locale]?.containsKey(pluralKey) == true || maps["en"]?.containsKey(pluralKey) == true) {
            pluralKey
        } else {
            key
        }
        val template = get(resolvedKey, locale)
        return template.replace("{count}", count.toString())
    }

    fun hasTranslation(key: String, locale: String): Boolean {
        return maps[locale]?.containsKey(key) == true
    }

    internal fun getKeys(locale: String): Set<String> = (maps[locale] ?: maps["en"]!!).keys

    val supportedLanguages: List<String> = listOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")
}
