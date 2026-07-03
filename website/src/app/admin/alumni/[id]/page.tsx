"use client";

import { use, useState } from "react";
import useSWR, { mutate } from "swr";
import Link from "next/link";
import { adminApi } from "@/lib/admin/client";
import { ApiError } from "@/lib/api";
import { Card, EmptyState, FadeIn, Badge } from "@/components/admin/Primitives";
import { AdminButton, Modal } from "@/components/admin/Toolbar";
import { IconAlumni } from "@/components/admin/icons";
import type { AlumniDto } from "@/lib/admin/types";

export default function AlumniDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const { data: alumni, error, isLoading } = useSWR<AlumniDto>(`alumni-${id}`, () => adminApi.alumniGet(id));
  const [editOpen, setEditOpen] = useState(false);
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  async function toggleFeatured() {
    setBusy(true); setErr(null);
    try {
      await adminApi.alumniToggleFeatured(id);
      await mutate(`alumni-${id}`);
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to toggle featured.");
    } finally { setBusy(false); }
  }

  async function deactivate() {
    if (!confirm("Deactivate this alumni record? They will be hidden from the directory.")) return;
    setBusy(true); setErr(null);
    try {
      await adminApi.alumniDeactivate(id);
      await mutate(`alumni-${id}`);
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to deactivate.");
    } finally { setBusy(false); }
  }

  async function verify(action: "approve" | "decline") {
    setBusy(true); setErr(null);
    try {
      await adminApi.alumniVerify(id, action);
      await mutate(`alumni-${id}`);
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to verify.");
    } finally { setBusy(false); }
  }

  return (
    <div className="space-y-5">
      <FadeIn>
        <Link href="/admin/alumni" className="text-sm font-semibold text-ink-2 hover:text-navy-deep">
          ← Back to Alumni
        </Link>
      </FadeIn>

      {isLoading && <Card><div className="p-8 text-center text-ink-2">Loading…</div></Card>}
      {error && <Card><div className="p-8 text-center text-red-500">{String(error)}</div></Card>}

      {alumni && (
        <FadeIn>
          <div className="flex items-center gap-3">
            <IconAlumni className="text-navy-deep" />
            <h1 className="text-2xl font-bold text-navy-deep">{alumni.name}</h1>
            <Badge tone={alumni.verificationStatus === "verified" || alumni.verificationStatus === "approved" ? "success" : alumni.verificationStatus === "pending" ? "warning" : "danger"}>
              {alumni.verificationStatus}
            </Badge>
            {alumni.isFeatured && <Badge tone="accent">★ Featured</Badge>}
          </div>

          {err && <p className="text-[13px] font-medium text-danger">{err}</p>}

          <div className="flex flex-wrap gap-2">
            <AdminButton variant="ghost" onClick={() => setEditOpen(true)} disabled={busy}>Edit</AdminButton>
            <AdminButton variant="ghost" onClick={toggleFeatured} disabled={busy}>
              {alumni.isFeatured ? "Unfeature" : "★ Feature"}
            </AdminButton>
            {alumni.verificationStatus === "pending" && (
              <>
                <AdminButton onClick={() => verify("approve")} disabled={busy}>Approve</AdminButton>
                <AdminButton variant="ghost" onClick={() => verify("decline")} disabled={busy}>Decline</AdminButton>
              </>
            )}
            {alumni.isActive && (
              <AdminButton variant="ghost" onClick={deactivate} disabled={busy}>Deactivate</AdminButton>
            )}
          </div>

          <div className="mt-5 grid gap-5 lg:grid-cols-2">
            {/* Profile */}
            <Card>
              <h2 className="mb-4 text-lg font-bold text-navy-deep">Profile</h2>
              <dl className="space-y-3">
                <div className="flex justify-between">
                  <dt className="text-ink-2">Graduation Year</dt>
                  <dd className="font-semibold text-navy-deep">{alumni.graduationYear}</dd>
                </div>
                {alumni.lastClass && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Last Class</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.lastClass}</dd>
                  </div>
                )}
                {alumni.currentProfession && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Profession</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.currentProfession}</dd>
                  </div>
                )}
                {alumni.company && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Company</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.company}</dd>
                  </div>
                )}
                {alumni.city && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">City</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.city}</dd>
                  </div>
                )}
                {alumni.email && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Email</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.email}</dd>
                  </div>
                )}
                {alumni.phone && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Phone</dt>
                    <dd className="font-semibold text-navy-deep">{alumni.phone}</dd>
                  </div>
                )}
                {alumni.linkedinUrl && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">LinkedIn</dt>
                    <dd><a href={alumni.linkedinUrl} target="_blank" rel="noreferrer" className="font-semibold text-blue-600 hover:underline">Profile</a></dd>
                  </div>
                )}
                {alumni.skills && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Skills</dt>
                    <dd className="text-right font-semibold text-navy-deep">{alumni.skills}</dd>
                  </div>
                )}
                {alumni.achievements && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Achievements</dt>
                    <dd className="text-right font-semibold text-navy-deep">{alumni.achievements}</dd>
                  </div>
                )}
                {alumni.isMentor && (
                  <div className="flex justify-between">
                    <dt className="text-ink-2">Mentor</dt>
                    <dd className="font-semibold text-navy-deep">Yes{alumni.mentorExpertise ? ` — ${alumni.mentorExpertise}` : ""}</dd>
                  </div>
                )}
              </dl>
              <div className="mt-4 flex gap-2">
                <Badge tone={alumni.showEmail ? "success" : "neutral"}>Email {alumni.showEmail ? "visible" : "hidden"}</Badge>
                <Badge tone={alumni.showPhone ? "success" : "neutral"}>Phone {alumni.showPhone ? "visible" : "hidden"}</Badge>
                <Badge tone={alumni.showLinkedin ? "success" : "neutral"}>LinkedIn {alumni.showLinkedin ? "visible" : "hidden"}</Badge>
              </div>
            </Card>

            {/* Career History */}
            <Card>
              <h2 className="mb-4 text-lg font-bold text-navy-deep">Career History</h2>
              {alumni.careerHistory && alumni.careerHistory.length > 0 ? (
                <div className="space-y-4">
                  {alumni.careerHistory.map((job) => (
                    <div key={job.id} className="border-l-2 border-navy/10 pl-4">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-navy-deep">{job.jobTitle}</span>
                        {job.isCurrent && <Badge tone="success">Current</Badge>}
                      </div>
                      <div className="text-sm text-ink-2">{job.company}{job.industry ? ` · ${job.industry}` : ""}</div>
                      <div className="text-xs text-ink-2">
                        {job.startDate ?? "—"} → {job.isCurrent ? "Present" : (job.endDate ?? "—")}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <EmptyState title="No career history" hint="Alumni hasn't added career details yet." />
              )}
            </Card>
          </div>

          {/* Donations for this alumni */}
          <div className="mt-5">
            <AlumniDonations alumniId={id} />
          </div>
        </FadeIn>
      )}

      {alumni && (
        <EditAlumniModal
          open={editOpen}
          onClose={() => setEditOpen(false)}
          alumni={alumni}
          onDone={() => mutate(`alumni-${id}`)}
        />
      )}
    </div>
  );
}

function EditAlumniModal({
  open,
  onClose,
  alumni,
  onDone,
}: {
  open: boolean;
  onClose: () => void;
  alumni: AlumniDto;
  onDone: () => Promise<void>;
}) {
  const [name, setName] = useState(alumni.name);
  const [currentProfession, setProfession] = useState(alumni.currentProfession ?? "");
  const [company, setCompany] = useState(alumni.company ?? "");
  const [city, setCity] = useState(alumni.city ?? "");
  const [email, setEmail] = useState(alumni.email ?? "");
  const [phone, setPhone] = useState(alumni.phone ?? "");
  const [linkedinUrl, setLinkedin] = useState(alumni.linkedinUrl ?? "");
  const [skills, setSkills] = useState(alumni.skills ?? "");
  const [achievements, setAchievements] = useState(alumni.achievements ?? "");
  const [isMentor, setIsMentor] = useState(alumni.isMentor);
  const [mentorExpertise, setMentorExpertise] = useState(alumni.mentorExpertise ?? "");
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  async function submit() {
    setErr(null);
    if (!name.trim()) { setErr("Name is required."); return; }
    setBusy(true);
    try {
      await adminApi.alumniUpdate(alumni.id, {
        name: name.trim(),
        currentProfession: currentProfession.trim() || null,
        company: company.trim() || null,
        city: city.trim() || null,
        email: email.trim() || null,
        phone: phone.trim() || null,
        linkedinUrl: linkedinUrl.trim() || null,
        skills: skills.trim() || null,
        achievements: achievements.trim() || null,
        isMentor,
        mentorExpertise: mentorExpertise.trim() || null,
      });
      await onDone();
      onClose();
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to save changes.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Edit alumni"
      description="Update profile information for this alumni."
      size="lg"
      footer={
        <>
          <AdminButton variant="ghost" onClick={onClose}>Cancel</AdminButton>
          <AdminButton onClick={submit} disabled={busy}>
            {busy ? "Saving…" : "Save changes"}
          </AdminButton>
        </>
      }
    >
      <div className="grid gap-3.5">
        <EditField label="Full name" value={name} onChange={setName} />
        <div className="grid grid-cols-2 gap-3.5">
          <EditField label="Profession" value={currentProfession} onChange={setProfession} />
          <EditField label="Company" value={company} onChange={setCompany} />
        </div>
        <div className="grid grid-cols-2 gap-3.5">
          <EditField label="City" value={city} onChange={setCity} />
          <EditField label="Email" value={email} onChange={setEmail} type="email" />
        </div>
        <div className="grid grid-cols-2 gap-3.5">
          <EditField label="Phone" value={phone} onChange={setPhone} />
          <EditField label="LinkedIn URL" value={linkedinUrl} onChange={setLinkedin} />
        </div>
        <EditField label="Skills" value={skills} onChange={setSkills} />
        <EditField label="Achievements" value={achievements} onChange={setAchievements} />
        <label className="flex items-center gap-2.5 cursor-pointer">
          <input
            type="checkbox"
            checked={isMentor}
            onChange={(e) => setIsMentor(e.target.checked)}
            className="h-4 w-4 rounded accent-[#6C5CE0]"
          />
          <span className="text-[13px] font-semibold text-navy-deep">Available as mentor</span>
        </label>
        {isMentor && (
          <EditField label="Mentor expertise" value={mentorExpertise} onChange={setMentorExpertise} />
        )}
        {err && <p className="text-[13px] font-medium text-danger">{err}</p>}
      </div>
    </Modal>
  );
}

function EditField({
  label,
  value,
  onChange,
  type = "text",
}: {
  label: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
}) {
  return (
    <label className="block">
      <span className="mb-1.5 block text-[11px] font-semibold uppercase tracking-wide text-ink-3">{label}</span>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-xl border border-navy/12 bg-white/80 px-3.5 py-2.5 text-[14px] text-ink outline-none transition-colors focus:border-accent focus:bg-white"
      />
    </label>
  );
}

function AlumniDonations({ alumniId }: { alumniId: string }) {
  const { data: donations, isLoading, mutate } = useSWR(`alumni-donations-${alumniId}`, () => adminApi.alumniDonations(undefined, alumniId));
  const [addOpen, setAddOpen] = useState(false);

  return (
    <Card>
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-bold text-navy-deep">Donations</h2>
        <AdminButton variant="ghost" onClick={() => setAddOpen(true)}>+ Log donation</AdminButton>
      </div>
      {isLoading && <div className="p-4 text-center text-ink-2">Loading…</div>}
      {donations && donations.length === 0 && <EmptyState title="No donations" hint="This alumni hasn't made any donations yet." />}
      {donations && donations.length > 0 && (
        <div className="space-y-3">
          {donations.map((d) => (
            <div key={d.id} className="flex items-center justify-between rounded-xl bg-navy/[0.02] px-4 py-3">
              <div>
                <div className="font-semibold text-navy-deep">₹{d.amount.toLocaleString("en-IN")}</div>
                <div className="text-sm text-ink-2">{d.donationDate}{d.campaignTitle ? ` · ${d.campaignTitle}` : ""}</div>
              </div>
              <div className="flex items-center gap-2">
                {d.is80gEligible && <Badge tone="success">80G</Badge>}
                {d.receiptNumber && <Badge tone="neutral">{d.receiptNumber}</Badge>}
              </div>
            </div>
          ))}
        </div>
      )}
      <AddDonationModal open={addOpen} onClose={() => setAddOpen(false)} alumniId={alumniId} onDone={async () => { await mutate(); }} />
    </Card>
  );
}

function AddDonationModal({
  open,
  onClose,
  alumniId,
  onDone,
}: {
  open: boolean;
  onClose: () => void;
  alumniId: string;
  onDone: () => Promise<void>;
}) {
  const [amount, setAmount] = useState("");
  const [donationDate, setDate] = useState(() => new Date().toISOString().slice(0, 10));
  const [purpose, setPurpose] = useState("");
  const [paymentMode, setPaymentMode] = useState("cash");
  const [is80gEligible, set80g] = useState(false);
  const [busy, setBusy] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  async function submit() {
    setErr(null);
    const amt = Number(amount);
    if (!amt || amt <= 0) { setErr("A valid amount is required."); return; }
    setBusy(true);
    try {
      await adminApi.alumniDonationCreate({
        alumniId,
        amount: amt,
        donationDate,
        purpose: purpose.trim() || null,
        paymentMode,
        is80gEligible,
      });
      await onDone();
      setAmount(""); setPurpose(""); set80g(false);
      onClose();
    } catch (e) {
      setErr(e instanceof ApiError ? e.message : "Failed to log donation.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Log a donation"
      description="Record a donation from this alumni."
      footer={
        <>
          <AdminButton variant="ghost" onClick={onClose}>Cancel</AdminButton>
          <AdminButton onClick={submit} disabled={busy}>
            {busy ? "Saving…" : "Log donation"}
          </AdminButton>
        </>
      }
    >
      <div className="grid gap-3.5">
        <EditField label="Amount (₹)" value={amount} onChange={setAmount} type="number" />
        <EditField label="Date" value={donationDate} onChange={setDate} type="date" />
        <EditField label="Purpose (optional)" value={purpose} onChange={setPurpose} />
        <label className="block">
          <span className="mb-1.5 block text-[11px] font-semibold uppercase tracking-wide text-ink-3">Payment mode</span>
          <select
            value={paymentMode}
            onChange={(e) => setPaymentMode(e.target.value)}
            className="w-full rounded-xl border border-navy/12 bg-white/80 px-3.5 py-2.5 text-[14px] text-ink outline-none focus:border-accent"
          >
            <option value="cash">Cash</option>
            <option value="upi">UPI</option>
            <option value="bank_transfer">Bank Transfer</option>
            <option value="cheque">Cheque</option>
            <option value="card">Card</option>
          </select>
        </label>
        <label className="flex items-center gap-2.5 cursor-pointer">
          <input type="checkbox" checked={is80gEligible} onChange={(e) => set80g(e.target.checked)} className="h-4 w-4 rounded accent-[#6C5CE0]" />
          <span className="text-[13px] font-semibold text-navy-deep">80G eligible</span>
        </label>
        {err && <p className="text-[13px] font-medium text-danger">{err}</p>}
      </div>
    </Modal>
  );
}
