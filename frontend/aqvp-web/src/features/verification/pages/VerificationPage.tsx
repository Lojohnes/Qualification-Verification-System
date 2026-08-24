import { useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardContent,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
  Alert,
  CircularProgress,
  Divider,
} from '@mui/material';

import { verificationService } from '@/features/verification/services/verificationService';
import type { VerificationResponse } from '@/types/verification';

export function VerificationPage() {
  const [identifier, setIdentifier] = useState('');
  const [method, setMethod] = useState<'MANUAL' | 'QR_SCAN'>('MANUAL');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<VerificationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleVerify = async () => {
    setResult(null);
    setError(null);

    const trimmed = identifier.trim();
    if (!trimmed) {
      setError('Please enter a certificate number or security identifier.');
      return;
    }

    setLoading(true);
    try {
      const response = await verificationService.verify({
        qualificationNumber: method === 'MANUAL' ? trimmed : undefined,
        securityIdentifier: method === 'QR_SCAN' ? trimmed : undefined,
        method,
      });
      setResult(response);
    } catch (err) {
      setError('Verification failed. Please check the identifier and try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Verify Qualification
      </Typography>

      <Card sx={{ maxWidth: 600, mb: 3 }}>
        <CardContent>
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel id="verification-method-label">Verification Method</InputLabel>
            <Select
              labelId="verification-method-label"
              value={method}
              label="Verification Method"
              onChange={(e) => setMethod(e.target.value as 'MANUAL' | 'QR_SCAN')}
            >
              <MenuItem value="MANUAL">Certificate Number</MenuItem>
              <MenuItem value="QR_SCAN">Security Identifier / QR Code</MenuItem>
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label={method === 'MANUAL' ? 'Certificate Number' : 'Security Identifier'}
            value={identifier}
            onChange={(e) => setIdentifier(e.target.value)}
            sx={{ mb: 2 }}
          />

          <Button
            variant="contained"
            onClick={handleVerify}
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : null}
          >
            Verify
          </Button>
        </CardContent>
      </Card>

      {error && (
        <Alert severity="error" sx={{ maxWidth: 600, mb: 2 }}>
          {error}
        </Alert>
      )}

      {result && (
        <Card sx={{ maxWidth: 600 }}>
          <CardContent>
            <Alert
              severity={result.status === 'VERIFIED' ? 'success' : 'warning'}
              sx={{ mb: 2 }}
            >
              {result.status}: {result.message}
            </Alert>

            {result.qualificationName && (
              <>
                <Typography variant="h6" gutterBottom>
                  Qualification Details
                </Typography>
                <Typography variant="body1">
                  <strong>Name:</strong> {result.qualificationName}
                </Typography>
                <Typography variant="body1">
                  <strong>Number:</strong> {result.qualificationNumber}
                </Typography>
                <Typography variant="body1">
                  <strong>Classification:</strong> {result.classification}
                </Typography>
                <Typography variant="body1">
                  <strong>Year of Award:</strong> {result.yearOfAward}
                </Typography>
                {result.issuedAt && (
                  <Typography variant="body1">
                    <strong>Issued:</strong> {result.issuedAt}
                  </Typography>
                )}
                <Divider sx={{ my: 2 }} />
                <Typography variant="body2" color="text.secondary">
                  Verified at {result.verifiedAt}
                </Typography>
              </>
            )}
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
