import { useEffect } from 'react';
import { Controller, useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  TextField,
} from '@mui/material';

import type { Qualification, QualificationRequest, QualificationType, Student } from '@/types/qualification';
import { QUALIFICATION_TYPES } from '@/types/qualification';
import type { Program } from '@/types/institution';

// Local form type: qualificationType is kept as QualificationType so the
// yup resolver infers the correct union and satisfies react-hook-form.
type QualificationFormValues = Omit<QualificationRequest, 'qualificationType'> & {
  qualificationType: QualificationType;
};

const schema: yup.ObjectSchema<QualificationFormValues> = yup.object({
  qualificationNumber: yup.string().required('Qualification number is required').max(100).defined(),
  studentId: yup.string().required('Student ID is required').defined(),
  institutionId: yup.string().required('Institution ID is required').defined(),
  programId: yup.string().optional(),
  qualificationType: yup
    .mixed<QualificationType>()
    .oneOf(
      QUALIFICATION_TYPES.map((t) => t.value),
      'Invalid qualification type'
    )
    .required('Qualification type is required'),
  qualificationName: yup.string().required('Qualification name is required').max(255).defined(),
  classification: yup.string().optional(),
  yearOfAward: yup
    .number()
    .required('Year of award is required')
    .min(1900)
    .max(new Date().getFullYear() + 1)
    .defined(),
  notes: yup.string().optional(),
});

interface QualificationFormDialogProps {
  open: boolean;
  qualification?: Qualification | null;
  institutionId: string;
  studentId?: string;
  students: Student[];
  programs: Program[];
  submitting?: boolean;
  onSubmit: (data: QualificationRequest) => void;
  onClose: () => void;
}

export function QualificationFormDialog({
  open,
  qualification,
  institutionId,
  studentId,
  students,
  programs,
  submitting,
  onSubmit,
  onClose,
}: QualificationFormDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<QualificationFormValues>({
    resolver: yupResolver(schema),
    defaultValues: {
      qualificationNumber: '',
      studentId: studentId ?? '',
      institutionId,
      programId: '',
      qualificationType: 'DEGREE',
      qualificationName: '',
      classification: '',
      yearOfAward: new Date().getFullYear(),
      notes: '',
    },
  });

  useEffect(() => {
    if (open) {
      reset({
        qualificationNumber: qualification?.qualificationNumber ?? '',
        studentId: qualification?.studentId ?? studentId ?? '',
        institutionId: qualification?.institutionId ?? institutionId,
        programId: qualification?.programId ?? '',
        qualificationType: qualification?.qualificationType ?? 'DEGREE',
        qualificationName: qualification?.qualificationName ?? '',
        classification: qualification?.classification ?? '',
        yearOfAward: qualification?.yearOfAward ?? new Date().getFullYear(),
        notes: qualification?.notes ?? '',
      });
    }
  }, [open, qualification, institutionId, studentId, reset]);

  const submitHandler: SubmitHandler<QualificationFormValues> = (data) => {
    const request: QualificationRequest = {
      ...data,
      programId: data.programId || undefined,
    };
    onSubmit(request);
  };

  const isEditingIssued =
    !!qualification && qualification.status !== 'DRAFT';

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{qualification ? 'Edit Qualification' : 'New Qualification'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <TextField
            {...register('qualificationNumber')}
            label="Qualification Number"
            fullWidth
            margin="normal"
            disabled={!!qualification}
            error={!!errors.qualificationNumber}
            helperText={errors.qualificationNumber?.message}
          />
          <FormControl fullWidth margin="normal" error={!!errors.studentId}>
            <InputLabel id="student-select-label">Student</InputLabel>
            <Controller
              name="studentId"
              control={control}
              render={({ field }) => (
                <Select
                  {...field}
                  labelId="student-select-label"
                  label="Student"
                  value={field.value || ''}
                  disabled={!!studentId || !!qualification}
                >
                  {students.map((s) => (
                    <MenuItem key={s.id} value={s.id}>
                      {`${s.studentNumber} - ${s.firstName} ${s.lastName}`}
                    </MenuItem>
                  ))}
                </Select>
              )}
            />
            {errors.studentId && <FormHelperText>{errors.studentId?.message}</FormHelperText>}
          </FormControl>

          <FormControl fullWidth margin="normal" error={!!errors.programId}>
            <InputLabel id="program-select-label">Program (optional)</InputLabel>
            <Controller
              name="programId"
              control={control}
              render={({ field }) => (
                <Select
                  {...field}
                  labelId="program-select-label"
                  label="Program (optional)"
                  value={field.value || ''}
                >
                  <MenuItem value="">
                    <em>None</em>
                  </MenuItem>
                  {programs.map((p) => (
                    <MenuItem key={p.id} value={p.id}>
                      {p.name}
                    </MenuItem>
                  ))}
                </Select>
              )}
            />
            {errors.programId && <FormHelperText>{errors.programId?.message}</FormHelperText>}
          </FormControl>
          <FormControl fullWidth margin="normal" error={!!errors.qualificationType} disabled={isEditingIssued}>
            <InputLabel id="qual-type-label">Qualification Type</InputLabel>
            <Controller
              name="qualificationType"
              control={control}
              render={({ field }) => (
                <Select
                  {...field}
                  labelId="qual-type-label"
                  label="Qualification Type"
                  value={field.value as QualificationType}
                >
                  {QUALIFICATION_TYPES.map((t) => (
                    <MenuItem key={t.value} value={t.value}>
                      {t.label}
                    </MenuItem>
                  ))}
                </Select>
              )}
            />
            {errors.qualificationType && (
              <FormHelperText>{errors.qualificationType?.message}</FormHelperText>
            )}
          </FormControl>
          <TextField
            {...register('qualificationName')}
            label="Qualification Name"
            fullWidth
            margin="normal"
            error={!!errors.qualificationName}
            helperText={errors.qualificationName?.message}
          />
          <TextField
            {...register('classification')}
            label="Classification (e.g. First Class)"
            fullWidth
            margin="normal"
            error={!!errors.classification}
            helperText={errors.classification?.message}
          />
          <TextField
            {...register('yearOfAward', { valueAsNumber: true })}
            label="Year of Award"
            type="number"
            fullWidth
            margin="normal"
            error={!!errors.yearOfAward}
            helperText={errors.yearOfAward?.message}
          />
          <TextField
            {...register('notes')}
            label="Notes"
            fullWidth
            multiline
            minRows={2}
            margin="normal"
            error={!!errors.notes}
            helperText={errors.notes?.message}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="inherit" disabled={submitting}>
            Cancel
          </Button>
          <Button type="submit" variant="contained" color="primary" disabled={submitting}>
            {submitting ? 'Saving...' : 'Save'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
  );
}
