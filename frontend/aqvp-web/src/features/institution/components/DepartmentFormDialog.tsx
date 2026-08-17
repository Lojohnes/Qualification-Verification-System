import { useEffect, useState } from 'react';
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

import { institutionService } from '@/features/institution/services/institutionService';
import type { Department, DepartmentRequest, Faculty, Institution } from '@/types/institution';

const schema = yup.object({
  facultyId: yup.string().required('Faculty is required'),
  name: yup.string().required('Name is required').max(100, 'Name must not exceed 100 characters'),
  code: yup.string().required('Code is required').max(20, 'Code must not exceed 20 characters'),
});

interface DepartmentFormDialogProps {
  open: boolean;
  department?: Department | null;
  institutions: Institution[];
  submitting?: boolean;
  onSubmit: (data: DepartmentRequest) => void;
  onClose: () => void;
}

export function DepartmentFormDialog({
  open,
  department,
  institutions,
  submitting,
  onSubmit,
  onClose,
}: DepartmentFormDialogProps) {
  const {
    control,
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<DepartmentRequest>({
    resolver: yupResolver(schema),
    defaultValues: { facultyId: '', name: '', code: '' },
  });

  const [institutionId, setInstitutionId] = useState('');
  const [faculties, setFaculties] = useState<Faculty[]>([]);

  useEffect(() => {
    if (open) {
      reset({
        facultyId: department?.facultyId ?? '',
        name: department?.name ?? '',
        code: department?.code ?? '',
      });
      setInstitutionId(department?.institutionId ?? '');
      setFaculties([]);
    }
  }, [open, department, reset]);

  useEffect(() => {
    if (!institutionId) {
      setFaculties([]);
      return;
    }
    institutionService.getFaculties(institutionId).then(setFaculties).catch(() => setFaculties([]));
  }, [institutionId]);

  const submitHandler: SubmitHandler<DepartmentRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{department ? 'Edit Department' : 'New Department'}</DialogTitle>
      <Box component="form" onSubmit={handleSubmit(submitHandler)} noValidate>
        <DialogContent>
          <TextField
            select
            label="Institution"
            fullWidth
            margin="normal"
            value={institutionId}
            onChange={(e) => {
              setInstitutionId(e.target.value);
              setValue('facultyId', '');
            }}
          >
            {institutions.map((institution) => (
              <MenuItem key={institution.id} value={institution.id}>
                {institution.name}
              </MenuItem>
            ))}
          </TextField>
          <Controller
            name="facultyId"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                select
                label="Faculty"
                fullWidth
                margin="normal"
                disabled={!institutionId}
                error={!!errors.facultyId}
                helperText={
                  errors.facultyId?.message ??
                  (!institutionId ? 'Select an institution first' : undefined)
                }
              >
                {faculties.map((faculty) => (
                  <MenuItem key={faculty.id} value={faculty.id}>
                    {faculty.name}
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
