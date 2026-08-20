import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  Button,
  Chip,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import PersonOffIcon from '@mui/icons-material/PersonOff';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { ConfirmDialog } from '@/components/ui/ConfirmDialog';
import { useSnackbar } from '@/hooks/useSnackbar';
import { studentService } from '@/features/qualification/services/qualificationService';
import { institutionService } from '@/features/institution/services/institutionService';
import { StudentFormDialog } from '@/features/qualification/components/StudentFormDialog';
import type { Student, StudentRequest } from '@/types/qualification';
import type { Institution } from '@/types/institution';

export function StudentsPage() {
  const { showSnackbar } = useSnackbar();
  const [students, setStudents] = useState<Student[]>([]);
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [selectedInstitutionId, setSelectedInstitutionId] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Student | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deactivateTarget, setDeactivateTarget] = useState<Student | null>(null);

  const loadStudents = useCallback(() => {
    if (!selectedInstitutionId) {
      setStudents([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    studentService
      .getStudentsByInstitution(selectedInstitutionId)
      .then(setStudents)
      .catch(() => showSnackbar('Failed to load students.', 'error'))
      .finally(() => setLoading(false));
  }, [selectedInstitutionId, showSnackbar]);

  useEffect(() => {
    institutionService
      .getInstitutions()
      .then((data) => {
        setInstitutions(data);
        if (data.length > 0) {
          setSelectedInstitutionId(data[0].id);
        }
      })
      .catch(() => showSnackbar('Failed to load institutions.', 'error'));
  }, [showSnackbar]);

  useEffect(() => {
    loadStudents();
  }, [loadStudents]);

  const filtered = students.filter(
    (s) =>
      `${s.firstName} ${s.lastName}`.toLowerCase().includes(search.toLowerCase()) ||
      s.studentNumber.toLowerCase().includes(search.toLowerCase()) ||
      (s.email ?? '').toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    if (!selectedInstitutionId) {
      showSnackbar('Select or create an institution first.', 'warning');
      return;
    }
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (student: Student) => {
    setEditing(student);
    setFormOpen(true);
  };

  const handleSubmit = async (data: StudentRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await studentService.updateStudent(editing.id, {
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          dateOfBirth: data.dateOfBirth,
          nationalId: data.nationalId,
        });
        showSnackbar('Student updated successfully.', 'success');
      } else {
        await studentService.createStudent(data);
        showSnackbar('Student created successfully.', 'success');
      }
      setFormOpen(false);
      loadStudents();
    } catch {
      showSnackbar('Failed to save student.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!deactivateTarget) return;
    try {
      await studentService.deactivateStudent(deactivateTarget.id);
      showSnackbar('Student deactivated successfully.', 'success');
      loadStudents();
    } catch {
      showSnackbar('Failed to deactivate student.', 'error');
    } finally {
      setDeactivateTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Students
        </Typography>
        <Box display="flex" alignItems="center" gap={2}>
          <FormControl fullWidth sx={{ minWidth: 280 }}>
            <InputLabel id="institution-select-label">Institution</InputLabel>
            <Select
              labelId="institution-select-label"
              value={selectedInstitutionId}
              onChange={(e) => setSelectedInstitutionId(e.target.value)}
              label="Institution"
              disabled={institutions.length === 0}
            >
              {institutions.map((inst) => (
                <MenuItem key={inst.id} value={inst.id}>
                  {inst.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New Student
          </Button>
        </Box>
      </Box>
      <Box mb={2}>
        <SearchBar value={search} onChange={setSearch} placeholder="Search by name, number or email..." />
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            emptyMessage="No students found."
            columns={[
              { key: 'studentNumber', header: 'Student #' },
              {
                key: 'name',
                header: 'Name',
                render: (row) => `${row.firstName} ${row.lastName}`,
              },
              { key: 'email', header: 'Email', render: (row) => row.email ?? '-' },
              {
                key: 'active',
                header: 'Status',
                render: (row) => (
                  <Chip
                    label={row.active ? 'Active' : 'Inactive'}
                    size="small"
                    color={row.active ? 'success' : 'default'}
                  />
                ),
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) => (
                  <>
                    <IconButton size="small" onClick={() => handleEdit(row)} aria-label="Edit">
                      <EditIcon fontSize="small" />
                    </IconButton>
                    {row.active && (
                      <IconButton
                        size="small"
                        onClick={() => setDeactivateTarget(row)}
                        aria-label="Deactivate"
                        color="error"
                      >
                        <PersonOffIcon fontSize="small" />
                      </IconButton>
                    )}
                  </>
                ),
              },
            ]}
          />
        )}
      </Paper>
      <StudentFormDialog
        open={formOpen}
        student={editing}
        institutionId={selectedInstitutionId}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deactivateTarget}
        title="Deactivate Student"
        message={`Are you sure you want to deactivate "${deactivateTarget?.firstName} ${deactivateTarget?.lastName}"?`}
        confirmLabel="Deactivate"
        onConfirm={handleDeactivate}
        onCancel={() => setDeactivateTarget(null)}
      />
    </>
  );
}
