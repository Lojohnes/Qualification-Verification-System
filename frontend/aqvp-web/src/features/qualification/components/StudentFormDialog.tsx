import { useEffect } from 'react';
import { useForm, type SubmitHandler } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from '@mui/material';

import type { Student, StudentRequest } from '@/types/qualification';

const schema = yup.object({
  studentNumber: yup
    .string()
    .required('Student number is required')
    .max(100, 'Must not exceed 100 characters'),
  firstName: yup
    .string()
    .required('First name is required')
    .max(150, 'Must not exceed 150 characters'),
  lastName: yup
    .string()
    .required('Last name is required')
    .max(150, 'Must not exceed 150 characters'),
  email: yup.string().email('Invalid email').max(255),
  dateOfBirth: yup.string().optional(),
  nationalId: yup.string().max(100).optional(),
  institutionId: yup.string().required('Institution ID is required'),
});

interface StudentFormDialogProps {
  open: boolean;
  student?: Student | null;
  institutionId: string;
  submitting?: boolean;
  onSubmit: (data: StudentRequest) => void;
  onClose: () => void;
}

export function StudentFormDialog({
  open,
  student,
  institutionId,
  submitting,
  onSubmit,
  onClose,
}: StudentFormDialogProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<StudentRequest>({
    resolver: yupResolver(schema),
    defaultValues: {
      studentNumber: '',
      firstName: '',
      lastName: '',
      email: '',
      dateOfBirth: '',
      nationalId: '',
      institutionId,
    },
  });

  useEffect(() => {
    if (open) {
      reset({
        studentNumber: student?.studentNumber ?? '',
        firstName: student?.firstName ?? '',
        lastName: student?.lastName ?? '',
        email: student?.email ?? '',
        dateOfBirth: student?.dateOfBirth ?? '',
        nationalId: student?.nationalId ?? '',
        institutionId: student?.institutionId ?? institutionId,
      });
    }
  }, [open, student, institutionId, reset]);

  const submitHandler: SubmitHandler<StudentRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{student ? 'Edit Student' : 'New Student'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <TextField
            {...register('studentNumber')}
            label="Student Number"
            fullWidth
            margin="normal"
            disabled={!!student}
            error={!!errors.studentNumber}
            helperText={errors.studentNumber?.message}
          />
          <TextField
            {...register('firstName')}
            label="First Name"
            fullWidth
            margin="normal"
            error={!!errors.firstName}
            helperText={errors.firstName?.message}
          />
          <TextField
            {...register('lastName')}
            label="Last Name"
            fullWidth
            margin="normal"
            error={!!errors.lastName}
            helperText={errors.lastName?.message}
          />
          <TextField
            {...register('email')}
            label="Email"
            type="email"
            fullWidth
            margin="normal"
            error={!!errors.email}
            helperText={errors.email?.message}
          />
          <TextField
            {...register('dateOfBirth')}
            label="Date of Birth"
            type="date"
            fullWidth
            margin="normal"
            slotProps={{ inputLabel: { shrink: true } }}
            error={!!errors.dateOfBirth}
            helperText={errors.dateOfBirth?.message}
          />
          <TextField
            {...register('nationalId')}
            label="National ID"
            fullWidth
            margin="normal"
            error={!!errors.nationalId}
            helperText={errors.nationalId?.message}
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
