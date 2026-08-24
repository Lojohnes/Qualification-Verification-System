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
  Tooltip,
  Typography,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import BlockIcon from '@mui/icons-material/Block';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import ArticleIcon from '@mui/icons-material/Article';
import QrCodeIcon from '@mui/icons-material/QrCode';
import FolderIcon from '@mui/icons-material/Folder';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { useSnackbar } from '@/hooks/useSnackbar';
import {
  qualificationService,
  studentService,
} from '@/features/qualification/services/qualificationService';
import { institutionService } from '@/features/institution/services/institutionService';
import { QualificationFormDialog } from '@/features/qualification/components/QualificationFormDialog';
import {
  IssueQualificationDialog,
  RevokeQualificationDialog,
} from '@/features/qualification/components/QualificationActionDialogs';
import { QualificationDocumentsDialog } from '@/features/qualification/components/QualificationDocumentsDialog';
import type {
  Qualification,
  QualificationRequest,
  QualificationStatus,
  Student,
} from '@/types/qualification';
import type { Institution, Program } from '@/types/institution';

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
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [students, setStudents] = useState<Student[]>([]);
  const [programs, setPrograms] = useState<Program[]>([]);
  const [selectedInstitutionId, setSelectedInstitutionId] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  // form dialog state
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Qualification | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // action dialog state
  const [issueTarget, setIssueTarget] = useState<Qualification | null>(null);
  const [revokeTarget, setRevokeTarget] = useState<Qualification | null>(null);
  const [documentsTarget, setDocumentsTarget] = useState<Qualification | null>(null);
  const [actionSubmitting, setActionSubmitting] = useState(false);

  const loadQualifications = useCallback(() => {
    if (!selectedInstitutionId) {
      setQualifications([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    qualificationService
      .getQualificationsByInstitution(selectedInstitutionId)
      .then(setQualifications)
      .catch(() => showSnackbar('Failed to load qualifications.', 'error'))
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
    if (!selectedInstitutionId) {
      setStudents([]);
      setPrograms([]);
      return;
    }
    studentService
      .getStudentsByInstitution(selectedInstitutionId)
      .then(setStudents)
      .catch(() => showSnackbar('Failed to load students.', 'error'));
    institutionService
      .getPrograms(selectedInstitutionId)
      .then(setPrograms)
      .catch(() => showSnackbar('Failed to load programs.', 'error'));
  }, [selectedInstitutionId, showSnackbar]);

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
    if (!selectedInstitutionId) {
      showSnackbar('Select or create an institution first.', 'warning');
      return;
    }
    if (students.length === 0) {
      showSnackbar('Create a student for this institution first.', 'warning');
      return;
    }
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

  const openBlobInNewTab = (blob: Blob, filename: string) => {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.target = '_blank';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  };

  const handleCertificate = async (q: Qualification) => {
    try {
      const blob = await qualificationService.generateCertificate(q.id);
      openBlobInNewTab(blob, `certificate-${q.qualificationNumber}.pdf`);
    } catch {
      showSnackbar('Failed to generate certificate.', 'error');
    }
  };

  const handleTranscript = async (q: Qualification) => {
    try {
      const blob = await qualificationService.generateTranscript(q.id);
      openBlobInNewTab(blob, `transcript-${q.qualificationNumber}.pdf`);
    } catch {
      showSnackbar('Failed to generate transcript.', 'error');
    }
  };

  const handleQrCode = async (q: Qualification) => {
    try {
      const blob = await qualificationService.generateQrCode(q.id);
      openBlobInNewTab(blob, `qr-${q.qualificationNumber}.png`);
    } catch {
      showSnackbar('Failed to generate QR code.', 'error');
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
            New Qualification
          </Button>
        </Box>
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
                    <Tooltip title="Certificate">
                      <IconButton
                        size="small"
                        onClick={() => handleCertificate(row)}
                        aria-label="Certificate"
                      >
                        <PictureAsPdfIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Transcript">
                      <IconButton
                        size="small"
                        onClick={() => handleTranscript(row)}
                        aria-label="Transcript"
                      >
                        <ArticleIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="QR Code">
                      <IconButton
                        size="small"
                        onClick={() => handleQrCode(row)}
                        aria-label="QR Code"
                      >
                        <QrCodeIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Documents">
                      <IconButton
                        size="small"
                        onClick={() => setDocumentsTarget(row)}
                        aria-label="Documents"
                      >
                        <FolderIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
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
        institutionId={selectedInstitutionId}
        students={students}
        programs={programs}
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

      <QualificationDocumentsDialog
        open={!!documentsTarget}
        qualification={documentsTarget}
        onClose={() => setDocumentsTarget(null)}
        onError={(message) => showSnackbar(message, 'error')}
        onSuccess={(message) => showSnackbar(message, 'success')}
      />
    </>
  );
}
