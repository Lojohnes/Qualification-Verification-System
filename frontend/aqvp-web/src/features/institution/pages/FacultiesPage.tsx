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
import { usePermission } from '@/hooks/usePermission';
import { PermissionGate } from '@/components/auth/PermissionGate';
import { institutionService } from '@/features/institution/services/institutionService';
import { FacultyFormDialog } from '@/features/institution/components/FacultyFormDialog';
import type { Faculty, FacultyRequest, Institution } from '@/types/institution';

export function FacultiesPage() {
  const { showSnackbar } = useSnackbar();
  const { hasPermission } = usePermission();
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [faculties, setFaculties] = useState<Faculty[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [institutionFilter, setInstitutionFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Faculty | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Faculty | null>(null);

  const loadFaculties = useCallback(() => {
    setLoading(true);
    institutionService
      .getFaculties(institutionFilter || undefined)
      .then(setFaculties)
      .catch(() => showSnackbar('Failed to load faculties.', 'error'))
      .finally(() => setLoading(false));
  }, [institutionFilter, showSnackbar]);

  useEffect(() => {
    institutionService
      .getInstitutions()
      .then(setInstitutions)
      .catch(() => showSnackbar('Failed to load institutions.', 'error'));
  }, [showSnackbar]);

  useEffect(() => {
    loadFaculties();
  }, [loadFaculties]);

  const filtered = faculties.filter(
    (faculty) =>
      faculty.name.toLowerCase().includes(search.toLowerCase()) ||
      faculty.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (faculty: Faculty) => {
    setEditing(faculty);
    setFormOpen(true);
  };

  const handleSubmit = async (data: FacultyRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await institutionService.updateFaculty(editing.id, data);
        showSnackbar('Faculty updated successfully.', 'success');
      } else {
        await institutionService.createFaculty(data);
        showSnackbar('Faculty created successfully.', 'success');
      }
      setFormOpen(false);
      loadFaculties();
    } catch {
      showSnackbar('Failed to save faculty.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await institutionService.deleteFaculty(deleteTarget.id);
      showSnackbar('Faculty deleted successfully.', 'success');
      loadFaculties();
    } catch {
      showSnackbar('Failed to delete faculty.', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Faculties
        </Typography>
        <PermissionGate permission="faculty:write">
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New Faculty
          </Button>
        </PermissionGate>
      </Box>
      <Box display="flex" gap={2} mb={2} flexWrap="wrap">
        <Box flexGrow={1} minWidth={240}>
          <SearchBar value={search} onChange={setSearch} placeholder="Search faculties..." />
        </Box>
        <TextField
          select
          size="small"
          label="Institution"
          value={institutionFilter}
          onChange={(e) => setInstitutionFilter(e.target.value)}
          sx={{ minWidth: 220 }}
        >
          <MenuItem value="">All Institutions</MenuItem>
          {institutions.map((institution) => (
            <MenuItem key={institution.id} value={institution.id}>
              {institution.name}
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
            emptyMessage="No faculties found."
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'code', header: 'Code' },
              {
                key: 'institutionName',
                header: 'Institution',
                render: (row) => row.institutionName ?? '-',
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) =>
                  hasPermission('faculty:write') ? (
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
                  ) : (
                    '-'
                  ),
              },
            ]}
          />
        )}
      </Paper>
      <FacultyFormDialog
        open={formOpen}
        faculty={editing}
        institutions={institutions}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Faculty"
        message={`Are you sure you want to delete "${deleteTarget?.name}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  );
}
