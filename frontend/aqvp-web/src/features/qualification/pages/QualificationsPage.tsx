import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  Button,
  Chip,
  IconButton,
  Paper,
  Tooltip,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import BlockIcon from '@mui/icons-material/Block';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { useSnackbar } from '@/hooks/useSnackbar';
import { qualificationService } from '@/features/qualification/services/qualificationService';
import { QualificationFormDialog } from '@/features/qualification/components/QualificationFormDialog';
import {
  IssueQualificationDialog,
  RevokeQualificationDialog,
} from '@/features/qualification/components/QualificationActionDialogs';
import type {
  Qualification,
  QualificationRequest,
  QualificationStatus,
} from '@/types/qualification';

// TODO: replace with institution picker / auth context when available
const DEMO_INSTITUTION_ID = '00000000-0000-0000-0000-000000000001';

const STATUS_COLORS: Record<
  QualificationStatus,
  'default' | 'info' | 'success' | 'warning' | 'error'
> = {
  DRAFT: 'default',
  ISSUED: 'success',
  AMENDED: 'warning',
  REVOKED: 'error',
  WITHDRAWN: 'error',
};

export function QualificationsPage() {
  const { showSnackbar } = useSnackbar();
  const [qualifications, setQualifications] = useState<Qualification[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  // form dialog state
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Qualification | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // action dialog state
  const [issueTarget, setIssueTarget] = useState<Qualification | null>(null);
  const [revokeTarget, setRevokeTarget] = useState<Qualification | null>(null);
  const [actionSubmitting, setActionSubmitting] = useState(false);

  const loadQualifications = useCallback(() => {
    setLoading(true);
    qualificationService
      .getQualificationsByInstitution(DEMO_INSTITUTION_ID)
      .then(setQualifications)
      .catch(() => showSnackbar('Failed to load qualifications.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  useEffect(() => {
    loadQualifications();
  }, [loadQualifications]);

  const filtered = qualifications.filter(
    (q) =>
      q.qualificationNumber.toLowerCase().includes(search.toLowerCase()) ||
      q.qualificationName.toLowerCase().includes(search.toLowerCase()) ||
      q.status.toLowerCase().includes(search.toLowerCase())
  );

  // ── CRUD handlers ────────────────────────────────────────────────────────
  const handleCreate = () => {
    setEditing(null);
    setFormOpen(true);
  };

  const handleEdit = (q: Qualification) => {
    setEditing(q);
    setFormOpen(true);
  };

  const handleSubmit = async (data: QualificationRequest) => {
    setSubmitting(true);
    try {
      if (editing) {
        await qualificationService.updateQualification(editing.id, data);
        showSnackbar('Qualification updated successfully.', 'success');
      } else {
        await qualificationService.createQualification(data);
        showSnackbar('Qualification created successfully.', 'success');
      }
      setFormOpen(false);
      loadQualifications();
    } catch {
      showSnackbar('Failed to save qualification.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  // ── Lifecycle action handlers ─────────────────────────────────────────────
  const handleIssue = async (notes?: string) => {
    if (!issueTarget) return;
    setActionSubmitting(true);
    try {
      await qualificationService.issueQualification(issueTarget.id, { notes });
      showSnackbar('Qualification issued successfully.', 'success');
      loadQualifications();
    } catch {
      showSnackbar('Failed to issue qualification.', 'error');
    } finally {
      setActionSubmitting(false);
      setIssueTarget(null);
    }
  };

  const handleRevoke = async (reason: string) => {
    if (!revokeTarget) return;
    setActionSubmitting(true);
    try {
      await qualificationService.revokeQualification(revokeTarget.id, { reason });
      showSnackbar('Qualification revoked successfully.', 'success');
      loadQualifications();
    } catch {
      showSnackbar('Failed to revoke qualification.', 'error');
    } finally {
      setActionSubmitting(false);
      setRevokeTarget(null);
    }
  };

  return (
    <>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4" fontWeight={600}>
          Qualifications
        </Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={handleCreate}>
          New Qualification
        </Button>
      </Box>
      <Box mb={2}>
        <SearchBar
          value={search}
          onChange={setSearch}
          placeholder="Search by number, name or status..."
        />
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            emptyMessage="No qualifications found."
            columns={[
              { key: 'qualificationNumber', header: 'Qual. #' },
              { key: 'qualificationName', header: 'Name' },
              {
                key: 'qualificationType',
                header: 'Type',
                render: (row) => row.qualificationType.replace(/_/g, ' '),
              },
              {
                key: 'yearOfAward',
                header: 'Year',
                render: (row) => String(row.yearOfAward),
              },
              {
                key: 'status',
                header: 'Status',
                render: (row) => (
                  <Chip
                    label={row.status}
                    size="small"
                    color={STATUS_COLORS[row.status] ?? 'default'}
                  />
                ),
              },
              {
                key: 'actions',
                header: 'Actions',
                align: 'right',
                render: (row) => (
                  <>
                    {row.status === 'DRAFT' && (
                      <Tooltip title="Edit">
                        <IconButton size="small" onClick={() => handleEdit(row)} aria-label="Edit">
                          <EditIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                    {row.status === 'DRAFT' && (
                      <Tooltip title="Issue">
                        <IconButton
                          size="small"
                          color="success"
                          onClick={() => setIssueTarget(row)}
                          aria-label="Issue"
                        >
                          <CheckCircleOutlineIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                    {(row.status === 'ISSUED' || row.status === 'AMENDED') && (
                      <Tooltip title="Revoke">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => setRevokeTarget(row)}
                          aria-label="Revoke"
                        >
                          <BlockIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                  </>
                ),
              },
            ]}
          />
        )}
      </Paper>

      {/* Create / Edit dialog */}
      <QualificationFormDialog
        open={formOpen}
        qualification={editing}
        institutionId={DEMO_INSTITUTION_ID}
        submitting={submitting}
        onSubmit={handleSubmit}
        onClose={() => setFormOpen(false)}
      />

      {/* Issue dialog */}
      <IssueQualificationDialog
        open={!!issueTarget}
        qualificationNumber={issueTarget?.qualificationNumber ?? ''}
        submitting={actionSubmitting}
        onConfirm={handleIssue}
        onCancel={() => setIssueTarget(null)}
      />

      {/* Revoke dialog */}
      <RevokeQualificationDialog
        open={!!revokeTarget}
        qualificationNumber={revokeTarget?.qualificationNumber ?? ''}
        submitting={actionSubmitting}
        onConfirm={handleRevoke}
        onCancel={() => setRevokeTarget(null)}
      />
    </>
  );
}
