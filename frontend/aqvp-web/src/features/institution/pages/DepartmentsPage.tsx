import { useCallback, useEffect, useState } from 'react';
import { Box, Button, IconButton, MenuItem, Paper, TextField, Typography } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { ConfirmDialog } from '@/components/ui/ConfirmDialog';
import { useSnackbar } from '@/hooks/useSnackbar';
import { institutionService } from '@/features/institution/services/institutionService';
import { DepartmentFormDialog } from '@/features/institution/components/DepartmentFormDialog';
import type { Department, DepartmentRequest, Faculty, Institution } from '@/types/institution';

export function DepartmentsPage() {
  const { showSnackbar } = useSnackbar();
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [facultyFilter, setFacultyFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Department | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Department | null>(null);

  const loadDepartments = useCallback(() => {
    setLoading(true);
    institutionService
      .getDepartments(facultyFilter || undefined)
      .then(setDepartments)
      .catch(() => showSnackbar('Failed to load departments.', 'error'))
      .finally(() => setLoading(false));
  }, [facultyFilter, showSnackbar]);

  useEffect(() => {
    institutionService
      .getInstitutions()
      .then(setInstitutions)
      .catch(() => showSnackbar('Failed to load institutions.', 'error'));
    institutionService
      .getFaculties()
      .then(setFaculties)
      .catch(() => showSnackbar('Failed to load faculties.', 'error'));
  }, [showSnackbar]);

  useEffect(() => {
    loadDepartments();
  }, [loadDepartments]);

  const filtered = departments.filter(
    (department) =>
      department.name.toLowerCase().includes(search.toLowerCase()) ||
      department.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (department: Department) => {
    setEditing(department);
    setFormOpen(true);
  };

  const handleSubmit = async (data: DepartmentRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await institutionService.updateDepartment(editing.id, data);
        showSnackbar('Department updated successfully.', 'success');
      } else {
        await institutionService.createDepartment(data);
        showSnackbar('Department created successfully.', 'success');
      }
      setFormOpen(false);
      loadDepartments();
    } catch {
      showSnackbar('Failed to save department.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await institutionService.deleteDepartment(deleteTarget.id);
      showSnackbar('Department deleted successfully.', 'success');
      loadDepartments();
    } catch {
      showSnackbar('Failed to delete department.', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Departments
        </Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
          New Department
        </Button>
      </Box>
      <Box display="flex" gap={2} mb={2} flexWrap="wrap">
        <Box flexGrow={1} minWidth={240}>
          <SearchBar value={search} onChange={setSearch} placeholder="Search departments..." />
        </Box>
        <TextField
          select
          size="small"
          label="Faculty"
          value={facultyFilter}
          onChange={(e) => setFacultyFilter(e.target.value)}
          sx={{ minWidth: 220 }}
        >
          <MenuItem value="">All Faculties</MenuItem>
          {faculties.map((faculty) => (
            <MenuItem key={faculty.id} value={faculty.id}>
              {faculty.name}
            </MenuItem>
          ))}
        </TextField>
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            emptyMessage="No departments found."
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'code', header: 'Code' },
              {
                key: 'facultyName',
                header: 'Faculty',
                render: (row) => row.facultyName ?? '-',
              },
              {
                key: 'institutionName',
                header: 'Institution',
                render: (row) => row.institutionName ?? '-',
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
                    <IconButton
                      size="small"
                      onClick={() => setDeleteTarget(row)}
                      aria-label="Delete"
                    >
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </>
                ),
              },
            ]}
          />
        )}
      </Paper>
      <DepartmentFormDialog
        open={formOpen}
        department={editing}
        institutions={institutions}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Department"
        message={`Are you sure you want to delete "${deleteTarget?.name}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  );
}
