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

import type { Qualification, QualificationRequest, QualificationType } from '@/types/qualification';
import { QUALIFICATION_TYPES } from '@/types/qualification';

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
  submitting?: boolean;
  onSubmit: (data: QualificationRequest) => void;
  onClose: () => void;
}

export function QualificationFormDialog({
  open,
  qualification,
  institutionId,
  studentId,
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
    onSubmit(data as QualificationRequest);
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
          <TextField
            {...register('studentId')}
            label="Student ID"
            fullWidth
            margin="normal"
            disabled={!!studentId || !!qualification}
            error={!!errors.studentId}
            helperText={errors.studentId?.message}
          />
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
