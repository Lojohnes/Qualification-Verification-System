import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Box,
  Button,
  Chip,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Stack,
  Tooltip,
  Typography,
} from '@mui/material';
import ArticleIcon from '@mui/icons-material/Article';
import DownloadIcon from '@mui/icons-material/Download';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import QrCodeIcon from '@mui/icons-material/QrCode';
import RefreshIcon from '@mui/icons-material/Refresh';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { qualificationService } from '@/features/qualification/services/qualificationService';
import { formatDate, formatStatus } from '@/utils/formatters';
import type { Qualification, QualificationDocument } from '@/types/qualification';

interface QualificationDocumentsDialogProps {
  open: boolean;
  qualification: Qualification | null;
  onClose: () => void;
  onError: (message: string) => void;
  onSuccess: (message: string) => void;
}

const DOCUMENT_COLORS: Record<
  QualificationDocument['documentType'],
  'default' | 'info' | 'success' | 'warning'
> = {
  CERTIFICATE: 'success',
  TRANSCRIPT: 'info',
  QR_CODE: 'warning',
};

export function QualificationDocumentsDialog({
  open,
  qualification,
  onClose,
  onError,
  onSuccess,
}: QualificationDocumentsDialogProps) {
  const [documents, setDocuments] = useState<QualificationDocument[]>([]);
  const [loading, setLoading] = useState(false);
  const [generating, setGenerating] = useState<QualificationDocument['documentType'] | null>(null);
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  const canGenerate = qualification?.status === 'ISSUED' || qualification?.status === 'AMENDED';

  const dialogTitle = useMemo(() => {
    if (!qualification) return 'Documents';
    return `Documents for ${qualification.qualificationNumber}`;
  }, [qualification]);

  const loadDocuments = useCallback(async () => {
    if (!qualification) return;
    setLoading(true);
    try {
      const data = await qualificationService.getDocuments(qualification.id);
      setDocuments(data);
    } catch {
      onError('Failed to load document metadata.');
    } finally {
      setLoading(false);
    }
  }, [qualification, onError]);

  useEffect(() => {
    if (open) {
      void loadDocuments();
    }
  }, [open, loadDocuments]);

  const generateDocument = async (type: QualificationDocument['documentType']) => {
    if (!qualification) return;
    setGenerating(type);
    try {
      if (type === 'CERTIFICATE') {
        await qualificationService.generateCertificateMetadata(qualification.id);
      } else if (type === 'TRANSCRIPT') {
        await qualificationService.generateTranscriptMetadata(qualification.id);
      } else {
        await qualificationService.generateQrCodeMetadata(qualification.id);
      }
      onSuccess(`${formatStatus(type)} generated and stored.`);
      await loadDocuments();
    } catch {
      onError(`Failed to generate ${formatStatus(type).toLowerCase()}.`);
    } finally {
      setGenerating(null);
    }
  };

  const downloadDocument = async (document: QualificationDocument) => {
    setDownloadingId(document.id);
    try {
      const blob = await qualificationService.downloadDocument(document.id);
      const url = URL.createObjectURL(blob);
      const a = window.document.createElement('a');
      a.href = url;
      a.download = document.fileName;
      window.document.body.appendChild(a);
      a.click();
      window.document.body.removeChild(a);
      setTimeout(() => URL.revokeObjectURL(url), 1000);
    } catch {
      onError('Failed to download stored document.');
    } finally {
      setDownloadingId(null);
    }
  };

  const shortValue = (value: string) => {
    if (!value) return '-';
    return value.length > 18 ? `${value.slice(0, 10)}...${value.slice(-6)}` : value;
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle>{dialogTitle}</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          <Box display="flex" gap={1} flexWrap="wrap">
            <Button
              variant="contained"
              startIcon={<PictureAsPdfIcon />}
              disabled={!canGenerate || generating !== null}
              onClick={() => generateDocument('CERTIFICATE')}
            >
              Certificate
            </Button>
            <Button
              variant="outlined"
              startIcon={<ArticleIcon />}
              disabled={!canGenerate || generating !== null}
              onClick={() => generateDocument('TRANSCRIPT')}
            >
              Transcript
            </Button>
            <Button
              variant="outlined"
              startIcon={<QrCodeIcon />}
              disabled={!canGenerate || generating !== null}
              onClick={() => generateDocument('QR_CODE')}
            >
              QR Code
            </Button>
            <Tooltip title="Refresh">
              <span>
                <IconButton
                  onClick={loadDocuments}
                  disabled={loading}
                  aria-label="Refresh documents"
                >
                  <RefreshIcon />
                </IconButton>
              </span>
            </Tooltip>
          </Box>

          {!canGenerate && (
            <Typography color="text.secondary" variant="body2">
              Issue the qualification before generating verifiable documents.
            </Typography>
          )}

          {loading ? (
            <LoadingSpinner />
          ) : (
            <DataTable
              data={documents}
              keyExtractor={(row) => row.id}
              emptyMessage="No generated documents yet."
              columns={[
                {
                  key: 'documentType',
                  header: 'Type',
                  render: (row) => (
                    <Chip
                      size="small"
                      label={formatStatus(row.documentType)}
                      color={DOCUMENT_COLORS[row.documentType] ?? 'default'}
                    />
                  ),
                },
                { key: 'fileName', header: 'File' },
                {
                  key: 'generatedAt',
                  header: 'Generated',
                  render: (row) => formatDate(row.generatedAt),
                },
                {
                  key: 'sizeBytes',
                  header: 'Size',
                  render: (row) => `${Math.max(1, Math.round(row.sizeBytes / 1024))} KB`,
                },
                {
                  key: 'sha256Hash',
                  header: 'SHA-256',
                  render: (row) => (
                    <Tooltip title={row.sha256Hash}>
                      <Typography variant="body2" component="span" fontFamily="monospace">
                        {shortValue(row.sha256Hash)}
                      </Typography>
                    </Tooltip>
                  ),
                },
                {
                  key: 'digitalSignature',
                  header: 'Signature',
                  render: (row) => (
                    <Tooltip
                      title={`${row.signatureAlgorithm} / ${row.signerKeyId}: ${row.digitalSignature}`}
                    >
                      <Typography variant="body2" component="span" fontFamily="monospace">
                        {shortValue(row.digitalSignature)}
                      </Typography>
                    </Tooltip>
                  ),
                },
                {
                  key: 'actions',
                  header: 'Actions',
                  align: 'right',
                  render: (row) => (
                    <Tooltip title="Download">
                      <span>
                        <IconButton
                          size="small"
                          onClick={() => downloadDocument(row)}
                          disabled={downloadingId === row.id}
                          aria-label="Download document"
                        >
                          <DownloadIcon fontSize="small" />
                        </IconButton>
                      </span>
                    </Tooltip>
                  ),
                },
              ]}
            />
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
}
