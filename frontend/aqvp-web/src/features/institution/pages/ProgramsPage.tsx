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
import { ProgramFormDialog } from '@/features/institution/components/ProgramFormDialog';
import type { Institution, Program, ProgramRequest } from '@/types/institution';

export function ProgramsPage() {
  const { showSnackbar } = useSnackbar();
  const { hasPermission } = usePermission();
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [programs, setPrograms] = useState<Program[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [institutionFilter, setInstitutionFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Program | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Program | null>(null);

  const loadPrograms = useCallback(() => {
    setLoading(true);
    institutionService
      .getPrograms(institutionFilter || undefined)
      .then(setPrograms)
      .catch(() => showSnackbar('Failed to load programs.', 'error'))
      .finally(() => setLoading(false));
  }, [institutionFilter, showSnackbar]);

  useEffect(() => {
    institutionService
      .getInstitutions()
      .then(setInstitutions)
      .catch(() => showSnackbar('Failed to load institutions.', 'error'));
  }, [showSnackbar]);

  useEffect(() => {
    loadPrograms();
  }, [loadPrograms]);

  const filtered = programs.filter(
    (program) =>
      program.name.toLowerCase().includes(search.toLowerCase()) ||
      program.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (program: Program) => {
    setEditing(program);
    setFormOpen(true);
  };

  const handleSubmit = async (data: ProgramRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await institutionService.updateProgram(editing.id, data);
        showSnackbar('Program updated successfully.', 'success');
      } else {
        await institutionService.createProgram(data);
        showSnackbar('Program created successfully.', 'success');
      }
      setFormOpen(false);
      loadPrograms();
    } catch {
      showSnackbar('Failed to save program.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await institutionService.deleteProgram(deleteTarget.id);
      showSnackbar('Program deleted successfully.', 'success');
      loadPrograms();
    } catch {
      showSnackbar('Failed to delete program.', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Programs
        </Typography>
        <PermissionGate permission="program:write">
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New Program
          </Button>
        </PermissionGate>
      </Box>
      <Box display="flex" gap={2} mb={2} flexWrap="wrap">
        <Box flexGrow={1} minWidth={240}>
          <SearchBar value={search} onChange={setSearch} placeholder="Search programs..." />
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
            emptyMessage="No programs found."
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'code', header: 'Code' },
              {
                key: 'institutionName',
                header: 'Institution',
                render: (row) => row.institutionName ?? '-',
              },
              {
                key: 'degreeLevel',
                header: 'Degree Level',
                render: (row) => row.degreeLevel ?? '-',
              },
              {
                key: 'durationSemesters',
                header: 'Duration',
                render: (row) =>
                  row.durationSemesters ? `${row.durationSemesters} semesters` : '-',
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) =>
                  hasPermission('program:write') ? (
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
      <ProgramFormDialog
        open={formOpen}
        program={editing}
        institutions={institutions}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Program"
        message={`Are you sure you want to delete "${deleteTarget?.name}"?`}
        confirmLabel="Delete"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  );
}
