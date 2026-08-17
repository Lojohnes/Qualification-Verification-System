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
  MenuItem,
  TextField,
} from '@mui/material';

import type { Faculty, FacultyRequest, Institution } from '@/types/institution';

const schema = yup.object({
  institutionId: yup.string().required('Institution is required'),
  name: yup.string().required('Name is required').max(100, 'Name must not exceed 100 characters'),
  code: yup.string().required('Code is required').max(20, 'Code must not exceed 20 characters'),
});

interface FacultyFormDialogProps {
  open: boolean;
  faculty?: Faculty | null;
  institutions: Institution[];
  submitting?: boolean;
  onSubmit: (data: FacultyRequest) => void;
  onClose: () => void;
}

export function FacultyFormDialog({
  open,
  faculty,
  institutions,
  submitting,
  onSubmit,
  onClose,
}: FacultyFormDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FacultyRequest>({
    resolver: yupResolver(schema),
    defaultValues: { institutionId: '', name: '', code: '' },
  });

  useEffect(() => {
    if (open) {
      reset({
        institutionId: faculty?.institutionId ?? '',
        name: faculty?.name ?? '',
        code: faculty?.code ?? '',
      });
    }
  }, [open, faculty, reset]);

  const submitHandler: SubmitHandler<FacultyRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{faculty ? 'Edit Faculty' : 'New Faculty'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <Controller
            name="institutionId"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                select
                label="Institution"
                fullWidth
                margin="normal"
                error={!!errors.institutionId}
                helperText={errors.institutionId?.message}
              >
                {institutions.map((institution) => (
                  <MenuItem key={institution.id} value={institution.id}>
                    {institution.name}
                  </MenuItem>
                ))}
              </TextField>
            )}
          />
          <TextField
            {...register('name')}
            label="Name"
            fullWidth
            margin="normal"
            error={!!errors.name}
            helperText={errors.name?.message}
          />
          <TextField
            {...register('code')}
            label="Code"
            fullWidth
            margin="normal"
            error={!!errors.code}
            helperText={errors.code?.message}
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
