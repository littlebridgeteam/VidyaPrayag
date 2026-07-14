/*
 * File: ServerStrings.kt
 * Module: feature.i18n
 *
 * Pre-translated notification template strings for server-side multi-language
 * support. Same approach as the client AppStrings — Kotlin string maps compiled
 * into the server binary, with DB-backed overrides for Super Admin editing
 * (FR-018).
 *
 * Resolution order: DB override → compiled default → English → key itself.
 *
 * Spec ref: MULTI_LANGUAGE_SPEC.md §8.3
 */
package com.littlebridge.enrollplus.feature.i18n

import java.util.concurrent.ConcurrentHashMap

object ServerStrings {

    // Compiled Kotlin defaults — shipped with server code
    private val templates: Map<String, Map<String, String>> = mapOf(
        "en" to mapOf(
            "notification.fee_reminder.title" to "Fee Reminder",
            "notification.fee_reminder.body" to "Fee of ₹{amount} is due for {student_name} on {due_date}.",
            "notification.attendance_alert.title" to "Attendance Alert",
            "notification.attendance_alert.body" to "{student_name} was absent on {date}.",
            "notification.announcement.title" to "New Announcement",
            "notification.announcement.body" to "{school_name} published a new announcement.",
            "notification.link_approved.title" to "Child Link Approved",
            "notification.link_approved.body" to "Your request to link with {student_name} has been approved.",
            "notification.link_rejected.title" to "Child Link Request Declined",
            "notification.link_rejected.body" to "Your request to link with {student_name} has been declined.",
            "notification.exam_reminder.title" to "Exam Reminder",
            "notification.exam_reminder.body" to "{exam_name} for {student_name} starts on {date}.",
            "notification.leave_approved.title" to "Leave Approved",
            "notification.leave_approved.body" to "Leave request for {student_name} has been approved.",
            "notification.leave_rejected.title" to "Leave Rejected",
            "notification.leave_rejected.body" to "Leave request for {student_name} has been declined.",
            "notification.homework_assigned.title" to "New Homework",
            "notification.homework_assigned.body" to "{subject} homework assigned for {class_name} {section}.",
            "notification.marks_published.title" to "Marks Published",
            "notification.marks_published.body" to "{exam_name} marks have been published for {student_name}.",
            "notification.ptm_scheduled.title" to "PTM Scheduled",
            "notification.ptm_scheduled.body" to "Parent-Teacher Meeting scheduled on {date} at {time}.",
            "notification.transport_delay.title" to "Transport Delay",
            "notification.transport_delay.body" to "Bus {bus_number} is running {delay_minutes} minutes late.",
        ),
        "hi" to mapOf(
            "notification.fee_reminder.title" to "फीस रिमाइंडर",
            "notification.fee_reminder.body" to "{student_name} के लिए ₹{amount} की फीस {due_date} को देय है।",
            "notification.attendance_alert.title" to "उपस्थिति अलर्ट",
            "notification.attendance_alert.body" to "{student_name} {date} को अनुपस्थित थे।",
            "notification.announcement.title" to "नई घोषणा",
            "notification.announcement.body" to "{school_name} ने एक नई घोषणा प्रकाशित की है।",
            "notification.link_approved.title" to "बाल लिंक स्वीकृत",
            "notification.link_approved.body" to "{student_name} के साथ लिंक करने का आपका अनुरोध स्वीकृत हो गया है।",
            "notification.link_rejected.title" to "बाल लिंक अनुरोध अस्वीकृत",
            "notification.link_rejected.body" to "{student_name} के साथ लिंक करने का आपका अनुरोध अस्वीकृत हो गया है।",
            "notification.exam_reminder.title" to "परीक्षा रिमाइंडर",
            "notification.exam_reminder.body" to "{student_name} की {exam_name} {date} से शुरू होगी।",
            "notification.leave_approved.title" to "अवकाश स्वीकृत",
            "notification.leave_approved.body" to "{student_name} के लिए अवकाश अनुरोध स्वीकृत हो गया है।",
            "notification.leave_rejected.title" to "अवकाश अस्वीकृत",
            "notification.leave_rejected.body" to "{student_name} के लिए अवकाश अनुरोध अस्वीकृत हो गया है।",
            "notification.homework_assigned.title" to "नया गृहकार्य",
            "notification.homework_assigned.body" to "{class_name} {section} के लिए {subject} गृहकार्य दिया गया है।",
            "notification.marks_published.title" to "अंक प्रकाशित",
            "notification.marks_published.body" to "{student_name} के {exam_name} अंक प्रकाशित हो गए हैं।",
            "notification.ptm_scheduled.title" to "पीटीएम निर्धारित",
            "notification.ptm_scheduled.body" to "अभिभावक-शिक्षक बैठक {date} को {time} पर निर्धारित है।",
            "notification.transport_delay.title" to "परिवहन में देरी",
            "notification.transport_delay.body" to "बस {bus_number} {delay_minutes} मिनट देरी से चल रही है।",
        ),
        "bn" to mapOf(
            "notification.fee_reminder.title" to "ফি রিমাইন্ডার",
            "notification.fee_reminder.body" to "{student_name}-এর জন্য ₹{amount} ফি {due_date} তারিখে দেয়।",
            "notification.attendance_alert.title" to "উপস্থিতি সতর্কতা",
            "notification.attendance_alert.body" to "{student_name} {date} তারিখে অনুপস্থিত ছিল।",
            "notification.announcement.title" to "নতুন ঘোষণা",
            "notification.announcement.body" to "{school_name} একটি নতুন ঘোষণা প্রকাশ করেছে।",
            "notification.link_approved.title" to "শিশু লিংক অনুমোদিত",
            "notification.link_approved.body" to "{student_name}-এর সাথে লিংক করার অনুরোধ অনুমোদিত হয়েছে।",
            "notification.link_rejected.title" to "শিশু লিংক অনুরোধ প্রত্যাখ্যাত",
            "notification.link_rejected.body" to "{student_name}-এর সাথে লিংক করার অনুরোধ প্রত্যাখ্যাত হয়েছে।",
            "notification.exam_reminder.title" to "পরীক্ষা রিমাইন্ডার",
            "notification.exam_reminder.body" to "{student_name}-এর {exam_name} {date} তারিখে শুরু হবে।",
            "notification.leave_approved.title" to "ছুটি অনুমোদিত",
            "notification.leave_approved.body" to "{student_name}-এর জন্য ছুটির অনুরোধ অনুমোদিত হয়েছে।",
            "notification.leave_rejected.title" to "ছুটি প্রত্যাখ্যাত",
            "notification.leave_rejected.body" to "{student_name}-এর জন্য ছুটির অনুরোধ প্রত্যাখ্যাত হয়েছে।",
            "notification.homework_assigned.title" to "নতুন বাড়ির কাজ",
            "notification.homework_assigned.body" to "{class_name} {section}-এর জন্য {subject} বাড়ির কাজ দেওয়া হয়েছে।",
            "notification.marks_published.title" to "নম্বর প্রকাশিত",
            "notification.marks_published.body" to "{student_name}-এর {exam_name} নম্বর প্রকাশিত হয়েছে।",
            "notification.ptm_scheduled.title" to "পিটিএম নির্ধারিত",
            "notification.ptm_scheduled.body" to "অভিভাবক-শিক্ষক সভা {date} তারিখে {time} এ নির্ধারিত।",
            "notification.transport_delay.title" to "পরিবহন বিলম্ব",
            "notification.transport_delay.body" to "বাস {bus_number} {delay_minutes} মিনিট দেরিতে চলছে।",
        ),
        "ta" to mapOf(
            "notification.fee_reminder.title" to "கட்டண நினைவூட்டல்",
            "notification.fee_reminder.body" to "{student_name}-க்கு ₹{amount} கட்டணம் {due_date} அன்று செலுத்த வேண்டும்.",
            "notification.attendance_alert.title" to "வருகை எச்சரிக்கை",
            "notification.attendance_alert.body" to "{student_name} {date} அன்று வரவில்லை.",
            "notification.announcement.title" to "புதிய அறிவிப்பு",
            "notification.announcement.body" to "{school_name} ஒரு புதிய அறிவிப்பை வெளியிட்டது.",
            "notification.link_approved.title" to "குழந்தை இணைப்பு அங்கீகரிக்கப்பட்டது",
            "notification.link_approved.body" to "{student_name}-உடன் இணைக்க உங்கள் கோரிக்கை அங்கீகரிக்கப்பட்டது.",
            "notification.link_rejected.title" to "குழந்தை இணைப்பு கோரிக்கை நிராகரிக்கப்பட்டது",
            "notification.link_rejected.body" to "{student_name}-உடன் இணைக்க உங்கள் கோரிக்கை நிராகரிக்கப்பட்டது.",
            "notification.exam_reminder.title" to "தேர்வு நினைவூட்டல்",
            "notification.exam_reminder.body" to "{student_name}-ன் {exam_name} {date} அன்று தொடங்கும்.",
            "notification.leave_approved.title" to "விடுப்பு அங்கீகரிக்கப்பட்டது",
            "notification.leave_approved.body" to "{student_name}-க்கான விடுப்பு கோரிக்கை அங்கீகரிக்கப்பட்டது.",
            "notification.leave_rejected.title" to "விடுப்பு நிராகரிக்கப்பட்டது",
            "notification.leave_rejected.body" to "{student_name}-க்கான விடுப்பு கோரிக்கை நிராகரிக்கப்பட்டது.",
            "notification.homework_assigned.title" to "புதிய வீட்டுப்பாடு",
            "notification.homework_assigned.body" to "{class_name} {section}-க்கு {subject} வீட்டுப்பாடு வழங்கப்பட்டது.",
            "notification.marks_published.title" to "மதிப்பெண்கள் வெளியிடப்பட்டன",
            "notification.marks_published.body" to "{student_name}-ன் {exam_name} மதிப்பெண்கள் வெளியிடப்பட்டன.",
            "notification.ptm_scheduled.title" to "பிடிஎம் திட்டமிடப்பட்டது",
            "notification.ptm_scheduled.body" to "பெற்றோர்-ஆசிரியர் கூட்டம் {date} அன்று {time} மணிக்கு திட்டமிடப்பட்டது.",
            "notification.transport_delay.title" to "போக்குவரத்து தாமதம்",
            "notification.transport_delay.body" to "பேருந்து {bus_number} {delay_minutes} நிமிடம் தாமதமாக இயங்குகிறது.",
        ),
        "te" to mapOf(
            "notification.fee_reminder.title" to "ఫీజు రిమైండర్",
            "notification.fee_reminder.body" to "{student_name} కోసం ₹{amount} ఫీజు {due_date} నాడు చెల్లించాలి.",
            "notification.attendance_alert.title" to "హాజరు హెచ్చరిక",
            "notification.attendance_alert.body" to "{student_name} {date} నాడు గైరుం.",
            "notification.announcement.title" to "కొత్త ప్రకటన",
            "notification.announcement.body" to "{school_name} ఒక కొత్త ప్రకటన ప్రచురించింది.",
            "notification.link_approved.title" to "చైల్డ్ లింక్ ఆమోదించబడింది",
            "notification.link_approved.body" to "{student_name}తో లింక్ చేయడానికి మీ అభ్యర్థన ఆమోదించబడింది.",
            "notification.link_rejected.title" to "చైల్డ్ లింక్ అభ్యర్థన తిరస్కరించబడింది",
            "notification.link_rejected.body" to "{student_name}తో లింక్ చేయడానికి మీ అభ్యర్థన తిరస్కరించబడింది.",
            "notification.exam_reminder.title" to "పరీక్ష రిమైండర్",
            "notification.exam_reminder.body" to "{student_name} యొక్క {exam_name} {date} నాడు ప్రారంభమవుతుంది.",
            "notification.leave_approved.title" to "సెలవు ఆమోదించబడింది",
            "notification.leave_approved.body" to "{student_name} కోసం సెలవు అభ్యర్థన ఆమోదించబడింది.",
            "notification.leave_rejected.title" to "సెలవు తిరస్కరించబడింది",
            "notification.leave_rejected.body" to "{student_name} కోసం సెలవు అభ్యర్థన తిరస్కరించబడింది.",
            "notification.homework_assigned.title" to "కొత్త హోంవర్క్",
            "notification.homework_assigned.body" to "{class_name} {section} కోసం {subject} హోంవర్క్ ఇవ్వబడింది.",
            "notification.marks_published.title" to "మార్కులు ప్రచురించబడ్డాయి",
            "notification.marks_published.body" to "{student_name} యొక్క {exam_name} మార్కులు ప్రచురించబడ్డాయి.",
            "notification.ptm_scheduled.title" to "పిటిఎం షెడ్యూల్ చేయబడింది",
            "notification.ptm_scheduled.body" to "తల్లిదండ్రులు-ఉపాధ్యాయుల సమావేశం {date} నాడు {time} కి షెడ్యూల్ చేయబడింది.",
            "notification.transport_delay.title" to "రవాణా ఆలస్యం",
            "notification.transport_delay.body" to "బస్సు {bus_number} {delay_minutes} నిమిషాలు ఆలస్యంగా నడుస్తోంది.",
        ),
        "mr" to mapOf(
            "notification.fee_reminder.title" to "फीस आठवण",
            "notification.fee_reminder.body" to "{student_name} साठी ₹{amount} फीस {due_date} रोजी देय आहे.",
            "notification.attendance_alert.title" to "उपस्थिती सूचना",
            "notification.attendance_alert.body" to "{student_name} {date} रोजी अनुपस्थित होते.",
            "notification.announcement.title" to "नवीन घोषणा",
            "notification.announcement.body" to "{school_name} ने एक नवीन घोषणा प्रकाशित केली आहे.",
            "notification.link_approved.title" to "बाल लिंक मंजूर",
            "notification.link_approved.body" to "{student_name} सोबत लिंक करण्याची तुमची विनंती मंजूर झाली आहे.",
            "notification.link_rejected.title" to "बाल लिंक विनंती नाकारली",
            "notification.link_rejected.body" to "{student_name} सोबत लिंक करण्याची तुमची विनंती नाकारली आहे.",
            "notification.exam_reminder.title" to "परीक्षा आठवण",
            "notification.exam_reminder.body" to "{student_name} ची {exam_name} {date} पासून सुरू होईल.",
            "notification.leave_approved.title" to "सुट्टी मंजूर",
            "notification.leave_approved.body" to "{student_name} साठी सुट्टी विनंती मंजूर झाली आहे.",
            "notification.leave_rejected.title" to "सुट्टी नाकारली",
            "notification.leave_rejected.body" to "{student_name} साठी सुट्टी विनंती नाकारली आहे.",
            "notification.homework_assigned.title" to "नवीन गृहपाठ",
            "notification.homework_assigned.body" to "{class_name} {section} साठी {subject} गृहपाठ दिला आहे.",
            "notification.marks_published.title" to "गुण प्रकाशित",
            "notification.marks_published.body" to "{student_name} चे {exam_name} गुण प्रकाशित झाले आहेत.",
            "notification.ptm_scheduled.title" to "पीटीएम नियोजित",
            "notification.ptm_scheduled.body" to "पालक-शिक्षक बैठक {date} रोजी {time} वाजता नियोजित आहे.",
            "notification.transport_delay.title" to "वाहतूक उशीर",
            "notification.transport_delay.body" to "बस {bus_number} {delay_minutes} मिनिटे उशीराने चालू आहे.",
        ),
        "gu" to mapOf(
            "notification.fee_reminder.title" to "ફી રિમાઇન્ડર",
            "notification.fee_reminder.body" to "{student_name} માટે ₹{amount} ની ફી {due_date} ના રોજ દેય છે.",
            "notification.attendance_alert.title" to "હાજરી ચેતવણી",
            "notification.attendance_alert.body" to "{student_name} {date} ના રોજ ગેરહાજર હતા.",
            "notification.announcement.title" to "નવી જાહેરાત",
            "notification.announcement.body" to "{school_name} એ નવી જાહેરાત પ્રકાશિત કરી છે.",
            "notification.link_approved.title" to "બાળ લિંક મંજૂર",
            "notification.link_approved.body" to "{student_name} સાથે લિંક કરવાની તમારી વિનંતી મંજૂર થઈ ગઈ છે.",
            "notification.link_rejected.title" to "બાળ લિંક વિનંતી નકારી",
            "notification.link_rejected.body" to "{student_name} સાથે લિંક કરવાની તમારી વિનંતી નકારી દેવામાં આવી છે.",
            "notification.exam_reminder.title" to "પરીક્ષા રિમાઇન્ડર",
            "notification.exam_reminder.body" to "{student_name} ની {exam_name} {date} થી શરૂ થશે.",
            "notification.leave_approved.title" to "રજા મંજૂર",
            "notification.leave_approved.body" to "{student_name} માટે રજા વિનંતી મંજૂર થઈ ગઈ છે.",
            "notification.leave_rejected.title" to "રજા નકારી",
            "notification.leave_rejected.body" to "{student_name} માટે રજા વિનંતી નકારી દેવામાં આવી છે.",
            "notification.homework_assigned.title" to "નવું ગૃહકાર્ય",
            "notification.homework_assigned.body" to "{class_name} {section} માટે {subject} ગૃહકાર્ય આપવામાં આવ્યું છે.",
            "notification.marks_published.title" to "ગુણ પ્રકાશિત",
            "notification.marks_published.body" to "{student_name} ના {exam_name} ગુણ પ્રકાશિત થયા છે.",
            "notification.ptm_scheduled.title" to "પીટીએમ નિયોજિત",
            "notification.ptm_scheduled.body" to "માતાપિતા-શિક્ષક સંમેલન {date} ના રોજ {time} વાગ્યે નિયોજિત છે.",
            "notification.transport_delay.title" to "પરિવહન વિલંબ",
            "notification.transport_delay.body" to "બસ {bus_number} {delay_minutes} મિનિટ મોડે ચાલી રહી છે.",
        ),
        "kn" to mapOf(
            "notification.fee_reminder.title" to "ಶುಲ್ಕ ಜ್ಞಾಪನೆ",
            "notification.fee_reminder.body" to "{student_name} ಗಾಗಿ ₹{amount} ಶುಲ್ಕ {due_date} ರಂದು ಪಾವತಿಸಬೇಕು.",
            "notification.attendance_alert.title" to "ಹಾಜರಾತಿ ಎಚ್ಚರಿಕೆ",
            "notification.attendance_alert.body" to "{student_name} {date} ರಂದು ಗೈರುಹಾಜರಿದ್ದರು.",
            "notification.announcement.title" to "ಹೊಸ ಘೋಷಣೆ",
            "notification.announcement.body" to "{school_name} ಹೊಸ ಘೋಷಣೆಯನ್ನು ಪ್ರಕಟಿಸಿದೆ.",
            "notification.link_approved.title" to "ಮಕ್ಕಳ ಲಿಂಕ್ ಅನುಮೋದಿಸಲಾಗಿದೆ",
            "notification.link_approved.body" to "{student_name} ಅವರೊಂದಿಗೆ ಲಿಂಕ್ ಮಾಡಲು ನಿಮ್ಮ ವಿನಂತಿ ಅನುಮೋದಿಸಲಾಗಿದೆ.",
            "notification.link_rejected.title" to "ಮಕ್ಕಳ ಲಿಂಕ್ ವಿನಂತಿ ತಿರಸ್ಕರಿಸಲಾಗಿದೆ",
            "notification.link_rejected.body" to "{student_name} ಅವರೊಂದಿಗೆ ಲಿಂಕ್ ಮಾಡಲು ನಿಮ್ಮ ವಿನಂತಿ ತಿರಸ್ಕರಿಸಲಾಗಿದೆ.",
            "notification.exam_reminder.title" to "ಪರೀಕ್ಷೆ ಜ್ಞಾಪನೆ",
            "notification.exam_reminder.body" to "{student_name} ಅವರ {exam_name} {date} ರಿಂದ ಪ್ರಾರಂಭವಾಗುತ್ತದೆ.",
            "notification.leave_approved.title" to "ರಜೆ ಅನುಮೋದಿಸಲಾಗಿದೆ",
            "notification.leave_approved.body" to "{student_name} ಗಾಗಿ ರಜೆ ವಿನಂತಿ ಅನುಮೋದಿಸಲಾಗಿದೆ.",
            "notification.leave_rejected.title" to "ರಜೆ ತಿರಸ್ಕರಿಸಲಾಗಿದೆ",
            "notification.leave_rejected.body" to "{student_name} ಗಾಗಿ ರಜೆ ವಿನಂತಿ ತಿರಸ್ಕರಿಸಲಾಗಿದೆ.",
            "notification.homework_assigned.title" to "ಹೊಸ ಮನೆಕೆಲಸ",
            "notification.homework_assigned.body" to "{class_name} {section} ಗಾಗಿ {subject} ಮನೆಕೆಲಸ ನೀಡಲಾಗಿದೆ.",
            "notification.marks_published.title" to "ಅಂಕಗಳು ಪ್ರಕಟಿಸಲಾಗಿವೆ",
            "notification.marks_published.body" to "{student_name} ಅವರ {exam_name} ಅಂಕಗಳು ಪ್ರಕಟಿಸಲಾಗಿವೆ.",
            "notification.ptm_scheduled.title" to "ಪಿಟಿಎಂ ಶೆಡ್ಯೂಲ್ ಮಾಡಲಾಗಿದೆ",
            "notification.ptm_scheduled.body" to "ಪೋಷಕರು-ಶಿಕ್ಷಕರ ಸಭೆ {date} ರಂದು {time} ಕ್ಕೆ ಶೆಡ್ಯೂಲ್ ಮಾಡಲಾಗಿದೆ.",
            "notification.transport_delay.title" to "ಸಾರಿಗೆ ವಿಳಂಬ",
            "notification.transport_delay.body" to "ಬಸ್ {bus_number} {delay_minutes} ನಿಮಿಷ ತಡವಾಗಿ ಚಲಾಯಿಸುತ್ತಿದೆ.",
        ),
        "ml" to mapOf(
            "notification.fee_reminder.title" to "ഫീസ് ഓർമ്മപ്പെടുത്തൽ",
            "notification.fee_reminder.body" to "{student_name}-ന് ₹{amount} ഫീസ് {due_date} ന് അടയ്ക്കേണ്ടതാണ്.",
            "notification.attendance_alert.title" to "ഹാജർ മുന്നറിയിപ്പ്",
            "notification.attendance_alert.body" to "{student_name} {date} ന് സാന്നിധ്യമില്ല.",
            "notification.announcement.title" to "പുതിയ പ്രഖ്യാപനം",
            "notification.announcement.body" to "{school_name} ഒരു പുതിയ പ്രഖ്യാപനം പ്രസിദ്ധീകരിച്ചു.",
            "notification.link_approved.title" to "കുട്ടി ലിങ്ക് അംഗീകരിച്ചു",
            "notification.link_approved.body" to "{student_name}-മായി ലിങ്ക് ചെയ്യാനുള്ള നിങ്ങളുടെ അഭ്യർത്ഥന അംഗീകരിച്ചു.",
            "notification.link_rejected.title" to "കുട്ടി ലിങ്ക് അഭ്യർത്ഥന നിരസിച്ചു",
            "notification.link_rejected.body" to "{student_name}-മായി ലിങ്ക് ചെയ്യാനുള്ള നിങ്ങളുടെ അഭ്യർത്ഥന നിരസിച്ചു.",
            "notification.exam_reminder.title" to "പരീക്ഷാ ഓർമ്മപ്പെടുത്തൽ",
            "notification.exam_reminder.body" to "{student_name}-ന്റെ {exam_name} {date} ന് ആരംഭിക്കും.",
            "notification.leave_approved.title" to "അവധി അംഗീകരിച്ചു",
            "notification.leave_approved.body" to "{student_name}-ന് വേണ്ടിയുള്ള അവധി അഭ്യർത്ഥന അംഗീകരിച്ചു.",
            "notification.leave_rejected.title" to "അവധി നിരസിച്ചു",
            "notification.leave_rejected.body" to "{student_name}-ന് വേണ്ടിയുള്ള അവധി അഭ്യർത്ഥന നിരസിച്ചു.",
            "notification.homework_assigned.title" to "പുതിയ ഹോംവർക്ക്",
            "notification.homework_assigned.body" to "{class_name} {section}-ന് {subject} ഹോംവർക്ക് നൽകി.",
            "notification.marks_published.title" to "മാർക്കുകൾ പ്രസിദ്ധീകരിച്ചു",
            "notification.marks_published.body" to "{student_name}-ന്റെ {exam_name} മാർക്കുകൾ പ്രസിദ്ധീകരിച്ചു.",
            "notification.ptm_scheduled.title" to "പിടിഎം ഷെഡ്യൂൾ ചെയ്തു",
            "notification.ptm_scheduled.body" to "രക്ഷിതാവ്-അധ്യാപകൻ യോഗം {date} ന് {time} ന് ഷെഡ്യൂൾ ചെയ്തു.",
            "notification.transport_delay.title" to "ഗതാഗത കാലതാമസം",
            "notification.transport_delay.body" to "ബസ് {bus_number} {delay_minutes} മിനിറ്റ് വൈകി ഓടുന്നു.",
        ),
        "pa" to mapOf(
            "notification.fee_reminder.title" to "ਫੀਸ ਰਿਮਾਇਂਡਰ",
            "notification.fee_reminder.body" to "{student_name} ਲਈ ₹{amount} ਦੀ ਫੀਸ {due_date} ਨੂੰ ਦੇਣੀ ਹੈ.",
            "notification.attendance_alert.title" to "ਹਾਜ਼ਰੀ ਚੇਤਾਵਨੀ",
            "notification.attendance_alert.body" to "{student_name} {date} ਨੂੰ ਗੈਰਹਾਜ਼ਰ ਸੀ.",
            "notification.announcement.title" to "ਨਵੀਂ ਘੋਸ਼ਣਾ",
            "notification.announcement.body" to "{school_name} ਨੇ ਇੱਕ ਨਵੀਂ ਘੋਸ਼ਣਾ ਪ੍ਰਕਾਸ਼ਿਤ ਕੀਤੀ ਹੈ.",
            "notification.link_approved.title" to "ਬੱਚਾ ਲਿੰਕ ਮਨਜ਼ੂਰ",
            "notification.link_approved.body" to "{student_name} ਨਾਲ ਲਿੰਕ ਕਰਨ ਦੀ ਤੁਹਾਡੀ ਬੇਨਤੀ ਮਨਜ਼ੂਰ ਹੋ ਗਈ ਹੈ.",
            "notification.link_rejected.title" to "ਬੱਚਾ ਲਿੰਕ ਬੇਨਤੀ ਰੱਦ",
            "notification.link_rejected.body" to "{student_name} ਨਾਲ ਲਿੰਕ ਕਰਨ ਦੀ ਤੁਹਾਡੀ ਬੇਨਤੀ ਰੱਦ ਕਰ ਦਿੱਤੀ ਗਈ ਹੈ.",
            "notification.exam_reminder.title" to "ਪ੍ਰੀਖਿਆ ਰਿਮਾਇਂਡਰ",
            "notification.exam_reminder.body" to "{student_name} ਦੀ {exam_name} {date} ਤੋਂ ਸ਼ੁਰੂ ਹੋਵੇਗੀ.",
            "notification.leave_approved.title" to "ਛੁੱਟੀ ਮਨਜ਼ੂਰ",
            "notification.leave_approved.body" to "{student_name} ਲਈ ਛੁੱਟੀ ਬੇਨਤੀ ਮਨਜ਼ੂਰ ਹੋ ਗਈ ਹੈ.",
            "notification.leave_rejected.title" to "ਛੁੱਟੀ ਰੱਦ",
            "notification.leave_rejected.body" to "{student_name} ਲਈ ਛੁੱਟੀ ਬੇਨਤੀ ਰੱਦ ਕਰ ਦਿੱਤੀ ਗਈ ਹੈ.",
            "notification.homework_assigned.title" to "ਨਵਾਂ ਘਰੇਲੂ ਕੰਮ",
            "notification.homework_assigned.body" to "{class_name} {section} ਲਈ {subject} ਘਰੇਲੂ ਕੰਮ ਦਿੱਤਾ ਗਿਆ ਹੈ.",
            "notification.marks_published.title" to "ਅੰਕ ਪ੍ਰਕਾਸ਼ਿਤ",
            "notification.marks_published.body" to "{student_name} ਦੇ {exam_name} ਅੰਕ ਪ੍ਰਕਾਸ਼ਿਤ ਹੋ ਗਏ ਹਨ.",
            "notification.ptm_scheduled.title" to "ਪੀਟੀਐਮ ਨਿਰਧਾਰਿਤ",
            "notification.ptm_scheduled.body" to "ਮਾਪੇ-ਅਧਿਆਪਕ ਮੀਟਿੰਗ {date} ਨੂੰ {time} ਵਜੇ ਨਿਰਧਾਰਿਤ ਹੈ.",
            "notification.transport_delay.title" to "ਆਵਾਜਾਈ ਦੇਰੀ",
            "notification.transport_delay.body" to "ਬੱਸ {bus_number} {delay_minutes} ਮਿੰਟ ਦੇਰੀ ਨਾਲ ਚੱਲ ਰਹੀ ਹੈ.",
        ),
    )

    // DB-backed overrides — loaded at startup, updated at runtime (no restart needed)
    private val overrides: ConcurrentHashMap<String, ConcurrentHashMap<String, String>> = ConcurrentHashMap()

    fun setOverride(key: String, lang: String, value: String) {
        overrides.computeIfAbsent(key) { ConcurrentHashMap() }[lang] = value
    }

    fun removeOverride(key: String, lang: String) {
        overrides[key]?.remove(lang)
    }

    fun get(key: String, lang: String): String {
        return overrides[key]?.get(lang)
            ?: templates[lang]?.get(key)
            ?: templates["en"]?.get(key)
            ?: key
    }

    fun fill(key: String, lang: String, params: Map<String, String>): String {
        val template = get(key, lang)
        var result = template
        params.forEach { (k, v) ->
            result = result.replace("{$k}", v)
        }
        return result
    }

    fun hasTemplate(notificationType: String, part: String = "body"): Boolean {
        val key = "notification.${notificationType}.${part}"
        return templates["en"]?.containsKey(key) == true
    }

    fun allKeys(): Set<String> = templates["en"]?.keys ?: emptySet()

    fun compiledDefault(key: String, lang: String): String? = templates[lang]?.get(key) ?: templates["en"]?.get(key)

    fun isOverride(key: String, lang: String): Boolean = overrides[key]?.containsKey(lang) == true

    val supportedLanguages: List<String> = listOf("en", "hi", "bn", "ta", "te", "mr", "gu", "kn", "ml", "pa")
}
