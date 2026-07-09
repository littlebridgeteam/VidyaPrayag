import { z } from "zod";

export const authTokenResponseSchema = z.object({
  token: z.string().min(1),
  refresh_token: z.string().min(1),
  user_id: z.string().min(1),
  name: z.string(),
  role: z.string(),
  profile_completed: z.boolean(),
  must_change_password: z.boolean().optional(),
});

export const apiEnvelopeSchema = <T extends z.ZodTypeAny>(dataSchema: T) =>
  z.object({
    success: z.boolean(),
    message: z.string().optional(),
    data: dataSchema.optional(),
    error_code: z.string().optional(),
  });

export const onboardingStatusSchema = z.object({
  school_id: z.string().nullable(),
  is_complete: z.boolean(),
  completion_percent: z.number(),
  resume_step: z.string(),
  total_step_count: z.number(),
  steps: z.array(
    z.object({
      step: z.string(),
      current_step_count: z.number(),
      is_done: z.boolean(),
    })
  ),
});

export const studentDtoSchema = z.object({
  id: z.string(),
  student_code: z.string(),
  full_name: z.string(),
  class_name: z.string().optional(),
  section: z.string().optional(),
});

export const studentListResponseSchema = z.object({
  students: z.array(studentDtoSchema),
  total: z.number(),
});

export const teacherDtoSchema = z.object({
  id: z.string(),
  full_name: z.string(),
  email: z.string().nullable().optional(),
  phone: z.string().nullable().optional(),
  subjects: z.array(z.string()).optional(),
});

export const teacherListResponseSchema = z.object({
  teachers: z.array(teacherDtoSchema),
});

export const announcementDtoSchema = z.object({
  id: z.string(),
  title: z.string(),
  body: z.string(),
  created_at: z.string(),
  audience: z.string().optional(),
});

export const announcementsListResponseSchema = z.object({
  announcements: z.array(announcementDtoSchema),
});

export const leaveRequestDtoSchema = z.object({
  id: z.string(),
  student_name: z.string(),
  type: z.string(),
  status: z.string(),
  from_date: z.string().optional(),
  to_date: z.string().optional(),
  reason: z.string().optional(),
});

export const leaveRequestListResponseSchema = z.object({
  leave_requests: z.array(leaveRequestDtoSchema),
  total: z.number().optional(),
});

export const schoolProfileDtoSchema = z.object({
  id: z.string(),
  name: z.string(),
  slug: z.string().optional(),
  board: z.string().optional(),
  city: z.string().optional(),
  state: z.string().optional(),
  contact_phone: z.string().optional(),
});

export function validateOrThrow<T>(
  data: unknown,
  schema: z.ZodSchema<T>
): T {
  const result = schema.safeParse(data);
  if (!result.success) {
    const issues = result.error.issues
      .map((i) => `${i.path.join(".")}: ${i.message}`)
      .join("; ");
    throw new Error(`API response validation failed: ${issues}`);
  }
  return result.data;
}
