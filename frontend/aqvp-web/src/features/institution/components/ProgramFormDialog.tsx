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
import type { Department, Faculty, Institution, Program, ProgramRequest } from '@/types/institution';

const schema = yup.object({
  institutionId: yup.string().required('Institution is required'),
  departmentId: yup.string().required('Department is required'),
  name: yup.string().required('Name is required').max(150, 'Name must not exceed 150 characters'),
  code: yup.string().required('Code is required').max(30, 'Code must not exceed 30 characters'),
  degreeLevel: yup.string().max(50, 'Degree level must not exceed 50 characters'),
  durationSemesters: yup
    .number()
    .transform((value) => (Number.isNaN(value) ? undefined : value))
    .min(1, 'Duration must be at least 1 semester')
    .nullable(),
});

interface ProgramFormDialogProps {
  open: boolean;
  program?: Program | null;
  institutions: Institution[];
  submitting?: boolean;
  onSubmit: (data: ProgramRequest) => void;
  onClose: () => void;
}

export function ProgramFormDialog({
  open,
  program,
  institutions,
  submitting,
  onSubmit,
  onClose,
}: ProgramFormDialogProps) {
  const {
    register,
    control,
    handleSubmit,
    reset,
    watch,
    setValue,
    formState: { errors },
  } = useForm<ProgramRequest>({
    resolver: yupResolver(schema) as never,
    defaultValues: {
      institutionId: '',
      departmentId: '',
      name: '',
      code: '',
      degreeLevel: '',
      durationSemesters: undefined,
    },
  });

  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [facultyId, setFacultyId] = useState('');

  const institutionId = watch('institutionId');

  useEffect(() => {
    if (open) {
      reset({
        institutionId: program?.institutionId ?? '',
        departmentId: program?.departmentId ?? '',
        name: program?.name ?? '',
        code: program?.code ?? '',
        degreeLevel: program?.degreeLevel ?? '',
        durationSemesters: program?.durationSemesters,
      });
      setFaculties([]);
      setDepartments([]);
      if (program?.departmentId) {
        institutionService
          .getDepartmentById(program.departmentId)
          .then((department) => setFacultyId(department.facultyId))
          .catch(() => setFacultyId(''));
      } else {
        setFacultyId('');
      }
    }
  }, [open, program, reset]);

  useEffect(() => {
    if (!institutionId) {
      setFaculties([]);
      return;
    }
    institutionService.getFaculties(institutionId).then(setFaculties).catch(() => setFaculties([]));
  }, [institutionId]);

  useEffect(() => {
    if (!facultyId) {
      setDepartments([]);
      return;
    }
    institutionService
      .getDepartments(facultyId)
      .then(setDepartments)
      .catch(() => setDepartments([]));
  }, [facultyId]);

  const submitHandler: SubmitHandler<ProgramRequest> = (data) => {
    onSubmit(data);
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{program ? 'Edit Program' : 'New Program'}</DialogTitle>
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
                onChange={(e) => {
                  field.onChange(e);
                  setFacultyId('');
                  setValue('departmentId', '');
                }}
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
            select
            label="Faculty"
            fullWidth
            margin="normal"
            value={facultyId}
            disabled={!institutionId}
            onChange={(e) => {
              setFacultyId(e.target.value);
              setValue('departmentId', '');
            }}
            helperText={!institutionId ? 'Select an institution first' : undefined}
          >
            {faculties.map((faculty) => (
              <MenuItem key={faculty.id} value={faculty.id}>
                {faculty.name}
              </MenuItem>
            ))}
          </TextField>
          <Controller
            name="departmentId"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                select
                label="Department"
                fullWidth
                margin="normal"
                disabled={!facultyId}
                error={!!errors.departmentId}
                helperText={
                  errors.departmentId?.message ??
                  (!facultyId ? 'Select a faculty first' : undefined)
                }
              >
                {departments.map((department) => (
                  <MenuItem key={department.id} value={department.id}>
                    {department.name}
                  </MenuItem>
                ))}
              </TextField>
            )}
          />
          <TextField
            {...register('name')}
            label="Program Name"
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
            {...register('degreeLevel')}
            label="Degree Level"
            fullWidth
            margin="normal"
            error={!!errors.degreeLevel}
            helperText={errors.degreeLevel?.message}
          />
          <TextField
            {...register('durationSemesters', { valueAsNumber: true })}
            label="Duration (Semesters)"
            type="number"
            fullWidth
            margin="normal"
            error={!!errors.durationSemesters}
            helperText={errors.durationSemesters?.message}
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
