import { useState } from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
} from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import BlockIcon from '@mui/icons-material/Block';

// ---------------------------------------------------------------------------
// Issue Dialog
// ---------------------------------------------------------------------------
interface IssueQualificationDialogProps {
  open: boolean;
  qualificationNumber: string;
  submitting?: boolean;
  onConfirm: (notes?: string) => void;
  onCancel: () => void;
}

export function IssueQualificationDialog({
  open,
  qualificationNumber,
  submitting,
  onConfirm,
  onCancel,
}: IssueQualificationDialogProps) {
  const [notes, setNotes] = useState('');

  return (
    <Dialog open={open} onClose={onCancel} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <CheckCircleOutlineIcon color="success" />
        Issue Qualification
      </DialogTitle>
      <DialogContent>
        <Typography variant="body2" gutterBottom>
          You are about to issue qualification <strong>{qualificationNumber}</strong>. This will
          generate a security identifier and move it to <strong>ISSUED</strong> status.
        </Typography>
        <TextField
          label="Notes (optional)"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          fullWidth
          multiline
          minRows={2}
          margin="normal"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel} color="inherit" disabled={submitting}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color="success"
          disabled={submitting}
          onClick={() => onConfirm(notes || undefined)}
        >
          {submitting ? 'Issuing...' : 'Issue'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

// ---------------------------------------------------------------------------
// Revoke Dialog
// ---------------------------------------------------------------------------
interface RevokeQualificationDialogProps {
  open: boolean;
  qualificationNumber: string;
  submitting?: boolean;
  onConfirm: (reason: string) => void;
  onCancel: () => void;
}

export function RevokeQualificationDialog({
  open,
  qualificationNumber,
  submitting,
  onConfirm,
  onCancel,
}: RevokeQualificationDialogProps) {
  const [reason, setReason] = useState('');
  const canConfirm = reason.trim().length > 0;

  return (
    <Dialog open={open} onClose={onCancel} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <BlockIcon color="error" />
        Revoke Qualification
      </DialogTitle>
      <DialogContent>
        <Typography variant="body2" gutterBottom>
          You are about to <strong>revoke</strong> qualification{' '}
          <strong>{qualificationNumber}</strong>. This action is irreversible. Please provide a
          reason.
        </Typography>
        <TextField
          label="Reason *"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          fullWidth
          multiline
          minRows={2}
          margin="normal"
          required
          error={reason.length === 0}
          helperText={reason.length === 0 ? 'Reason is required' : ''}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel} color="inherit" disabled={submitting}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color="error"
          disabled={submitting || !canConfirm}
          onClick={() => onConfirm(reason)}
        >
          {submitting ? 'Revoking...' : 'Revoke'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
