import { useCallback, useEffect, useState } from 'react';
import {
  Box,
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
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import ArticleIcon from '@mui/icons-material/Article';
import QrCodeIcon from '@mui/icons-material/QrCode';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { useSnackbar } from '@/hooks/useSnackbar';
import { qualificationService } from '@/features/qualification/services/qualificationService';
import { documentService } from '@/features/documents/services/documentService';
import { institutionService } from '@/features/institution/services/institutionService';
import type { Qualification, QualificationStatus } from '@/types/qualification';
import type { Institution } from '@/types/institution';

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

export function DocumentsPage() {
  const { showSnackbar } = useSnackbar();
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [selectedInstitutionId, setSelectedInstitutionId] = useState<string>('');
  const [qualifications, setQualifications] = useState<Qualification[]>([]);
  const [loading, setLoading] = useState(true);

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
    loadQualifications();
  }, [loadQualifications]);

  const openBlobInNewTab = (blob: Blob) => {
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  };

  const handleDownload = async (
    type: 'certificate' | 'transcript' | 'qr',
    qualification: Qualification
  ) => {
    try {
      let blob: Blob;
      switch (type) {
        case 'certificate':
          blob = await documentService.downloadCertificate(qualification.id);
          break;
        case 'transcript':
          blob = await documentService.downloadTranscript(qualification.id);
          break;
        case 'qr':
          blob = await documentService.downloadQrCode(qualification.id);
          break;
      }
      openBlobInNewTab(blob);
    } catch {
      showSnackbar(`Failed to download ${type}.`, 'error');
    }
  };

  const canGenerateDocuments = (status: QualificationStatus) =>
    status === 'ISSUED' || status === 'AMENDED';

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Documents
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <FormControl fullWidth sx={{ mb: 2 }}>
          <InputLabel id="institution-label">Institution</InputLabel>
          <Select
            labelId="institution-label"
            value={selectedInstitutionId}
            label="Institution"
            onChange={(e) => setSelectedInstitutionId(e.target.value)}
          >
            {institutions.map((institution) => (
              <MenuItem key={institution.id} value={institution.id}>
                {institution.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Paper>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <DataTable
          data={qualifications}
          keyExtractor={(row) => row.id}
          emptyMessage="No qualifications found for the selected institution."
          columns={[
            {
              key: 'qualificationNumber',
              header: 'Certificate Number',
            },
            {
              key: 'qualificationName',
              header: 'Qualification',
            },
            {
              key: 'status',
              header: 'Status',
              render: (row) => (
                <Chip
                  label={row.status}
                  color={STATUS_COLORS[row.status]}
                  size="small"
                />
              ),
            },
            {
              key: 'securityIdentifier',
              header: 'Security Identifier',
              render: (row) => (
                <Typography variant="body2" sx={{ fontFamily: 'monospace' }}>
                  {row.securityIdentifier || '-'}
                </Typography>
              ),
            },
            {
              key: 'actions',
              header: 'Documents',
              align: 'right',
              render: (row) => (
                <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
                  <Tooltip title="Download certificate">
                    <span>
                      <IconButton
                        size="small"
                        disabled={!canGenerateDocuments(row.status)}
                        onClick={() => handleDownload('certificate', row)}
                      >
                        <PictureAsPdfIcon />
                      </IconButton>
                    </span>
                  </Tooltip>
                  <Tooltip title="Download transcript">
                    <span>
                      <IconButton
                        size="small"
                        disabled={!canGenerateDocuments(row.status)}
                        onClick={() => handleDownload('transcript', row)}
                      >
                        <ArticleIcon />
                      </IconButton>
                    </span>
                  </Tooltip>
                  <Tooltip title="Download QR code">
                    <span>
                      <IconButton
                        size="small"
                        disabled={!canGenerateDocuments(row.status)}
                        onClick={() => handleDownload('qr', row)}
                      >
                        <QrCodeIcon />
                      </IconButton>
                    </span>
                  </Tooltip>
                </Box>
              ),
            },
          ]}
        />
      )}
    </Box>
  );
}
