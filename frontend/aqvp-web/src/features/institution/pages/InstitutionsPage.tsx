import { useCallback, useEffect, useState } from 'react';
import { Box, Button, IconButton, Paper, Typography } from '@mui/material';
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
import { InstitutionFormDialog } from '@/features/institution/components/InstitutionFormDialog';
import type { Institution, InstitutionRequest } from '@/types/institution';

export function InstitutionsPage() {
  const { showSnackbar } = useSnackbar();
  const { hasPermission } = usePermission();
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Institution | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Institution | null>(null);

  const loadInstitutions = useCallback(() => {
    setLoading(true);
    institutionService
      .getInstitutions()
      .then(setInstitutions)
      .catch(() => showSnackbar('Failed to load institutions.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  useEffect(() => {
    loadInstitutions();
  }, [loadInstitutions]);

  const filtered = institutions.filter(
    (institution) =>
      institution.name.toLowerCase().includes(search.toLowerCase()) ||
      institution.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (institution: Institution) => {
    setEditing(institution);
    setFormOpen(true);
  };

  const handleSubmit = async (data: InstitutionRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await institutionService.updateInstitution(editing.id, data);
        showSnackbar('Institution updated successfully.', 'success');
      } else {
        await institutionService.createInstitution(data);
        showSnackbar('Institution created successfully.', 'success');
      }
      setFormOpen(false);
      loadInstitutions();
    } catch {
      showSnackbar('Failed to save institution.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeactivate = async () => {
    if (!deleteTarget) return;
    try {
      await institutionService.deactivateInstitution(deleteTarget.id);
      showSnackbar('Institution deactivated successfully.', 'success');
      loadInstitutions();
    } catch {
      showSnackbar('Failed to deactivate institution.', 'error');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Institutions
        </Typography>
        <PermissionGate permission="institution:write">
          <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
            New Institution
          </Button>
        </PermissionGate>
      </Box>
      <Box mb={2}>
        <SearchBar value={search} onChange={setSearch} placeholder="Search institutions..." />
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            emptyMessage="No institutions found."
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'code', header: 'Code' },
              {
                key: 'description',
                header: 'Description',
                render: (row) => row.description ?? '-',
              },
              {
                key: 'active',
                header: 'Status',
                render: (row) => (row.active ? 'Active' : 'Inactive'),
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) =>
                  hasPermission('institution:write') ? (
                    <>
                      <IconButton size="small" onClick={() => handleEdit(row)} aria-label="Edit">
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => setDeleteTarget(row)}
                        aria-label="Deactivate"
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
      <InstitutionFormDialog
        open={formOpen}
        institution={editing}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />
      <ConfirmDialog
        open={!!deleteTarget}
        title="Deactivate Institution"
        message={`Are you sure you want to deactivate "${deleteTarget?.name}"?`}
        confirmLabel="Deactivate"
        onConfirm={handleDeactivate}
        onCancel={() => setDeleteTarget(null)}
      />
    </>
  );
}
