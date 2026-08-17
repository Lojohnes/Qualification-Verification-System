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
  FormControlLabel,
  Switch,
  TextField,
} from '@mui/material';

import type { Institution, InstitutionRequest } from '@/types/institution';

const schema = yup.object({
  name: yup.string().required('Name is required').max(100, 'Name must not exceed 100 characters'),
  code: yup.string().required('Code is required').max(20, 'Code must not exceed 20 characters'),
  description: yup.string().max(500, 'Description must not exceed 500 characters'),
  active: yup.boolean(),
});

interface InstitutionFormDialogProps {
  open: boolean;
  institution?: Institution | null;
  submitting?: boolean;
  onSubmit: (data: InstitutionRequest) => void;
  onClose: () => void;
}

export function InstitutionFormDialog({
  open,
  institution,
  submitting,
  onSubmit,
  onClose,
}: InstitutionFormDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<InstitutionRequest>({
    resolver: yupResolver(schema),
    defaultValues: { name: '', code: '', description: '', active: true },
  });

  useEffect(() => {
    if (open) {
      reset({
        name: institution?.name ?? '',
        code: institution?.code ?? '',
        description: institution?.description ?? '',
        active: institution?.active ?? true,
      });
    }
  }, [open, institution, reset]);

  const submitHandler: SubmitHandler<InstitutionRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{institution ? 'Edit Institution' : 'New Institution'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
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
          <TextField
            {...register('description')}
            label="Description"
            fullWidth
            multiline
            minRows={2}
            margin="normal"
            error={!!errors.description}
            helperText={errors.description?.message}
          />
          <Controller
            name="active"
            control={control}
            render={({ field }) => (
              <FormControlLabel
                control={
                  <Switch
                    checked={!!field.value}
                    onChange={(e) => field.onChange(e.target.checked)}
                  />
                }
                label="Active"
              />
            )}
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
