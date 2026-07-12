import type { Metadata } from "next";
import { OnboardingGate } from "@/components/onboarding/OnboardingGate";
import { Wizard } from "@/components/onboarding/Wizard";

export const metadata: Metadata = {
  title: "Onboard your school | Enroll+",
  description:
    "Set up your school on Enroll+ in five short steps: create your admin account, add your basics, branding and academic structure, then launch.",
  robots: { index: false },
};

export default function OnboardingPage() {
  return (
    <div className="shell pb-24 pt-28 md:pt-32">
      <OnboardingGate>
        <Wizard />
      </OnboardingGate>
    </div>
  );
}
