import type { Metadata } from "next";
import { LegalLayout, LegalSection } from "@/components/legal/LegalLayout";

export const metadata: Metadata = {
  title: "Delete Your Account | Enroll+",
  description:
    "Request deletion of your Enroll+ account and associated data. Learn what is deleted, what is retained, and how to submit your request.",
  alternates: { canonical: "/delete-account" },
};

export default function DeleteAccountPage() {
  return (
    <LegalLayout title="Delete Your Account" updated="18 July 2026">
      <p>
        You can request deletion of your Enroll+ account and all associated
        personal data at any time. This page explains how to submit a request,
        what data is deleted, and what may be retained for legal or operational
        reasons.
      </p>

      <LegalSection title="How to request account deletion">
        <p>
          Send an email to{" "}
          <a href="mailto:support@enrollplus.app?subject=Request%20Account%20Deletion">
            support@enrollplus.app
          </a>{" "}
          with the subject line <strong>&ldquo;Request Account Deletion&rdquo;</strong>.
          Please include the following information so we can verify your identity:
        </p>
        <ul>
          <li>Your full name</li>
          <li>The phone number or email associated with your Enroll+ account</li>
          <li>Your school name (if applicable)</li>
          <li>Your role on the platform (Parent, Teacher, or School Admin)</li>
        </ul>
        <p>
          We will process your request within <strong>30 days</strong> of
          receiving it and send a confirmation email once deletion is complete.
        </p>
      </LegalSection>

      <LegalSection title="What data is deleted">
        <p>When your account is deleted, the following data is permanently removed:</p>
        <ul>
          <li><strong>Account credentials</strong> — phone number, email, password hash, OTP records</li>
          <li><strong>Profile information</strong> — name, profile photo, role-specific profile data</li>
          <li><strong>Messages</strong> — all conversations and messages sent or received</li>
          <li><strong>Notifications</strong> — all in-app notifications associated with your account</li>
          <li><strong>Parent-child links</strong> — all linked child records and associated data</li>
          <li><strong>Push notification tokens</strong> — Firebase FCM token registrations</li>
          <li><strong>Analytics identifiers</strong> — Firebase Analytics and Crashlytics user-associated data</li>
        </ul>
      </LegalSection>

      <LegalSection title="What data may be retained">
        <p>
          Certain data may be retained for longer periods where required by law
          or legitimate institutional needs:
        </p>
        <ul>
          <li><strong>Student academic records</strong> — attendance, marks, and report cards may be retained by the school for academic compliance, even after a parent account is deleted</li>
          <li><strong>Financial records</strong> — fee payment records may be retained for tax and audit purposes</li>
          <li><strong>Anonymized analytics</strong> — aggregated, non-identifiable usage data is not linked to your account and is not affected by deletion</li>
          <li><strong>Server logs</strong> — retained for up to 90 days for security and fraud prevention</li>
        </ul>
      </LegalSection>

      <LegalSection title="What happens after deletion">
        <ul>
          <li>You will no longer be able to log in to Enroll+</li>
          <li>You will stop receiving notifications from Enroll+</li>
          <li>Your data will be removed from active systems within 30 days</li>
          <li>Backups containing your data will be purged within 90 days</li>
        </ul>
      </LegalSection>

      <LegalSection title="Questions?">
        <p>
          If you have any questions about account deletion or data retention,
          contact us at{" "}
          <a href="mailto:support@enrollplus.app">support@enrollplus.app</a>.
        </p>
      </LegalSection>
    </LegalLayout>
  );
}
