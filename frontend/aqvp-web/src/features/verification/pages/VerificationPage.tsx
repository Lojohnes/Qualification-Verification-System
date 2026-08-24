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

import axios from 'axios';
import { verificationService } from '@/features/verification/services/verificationService';
import type { VerificationResultResponse } from '@/types/verification';

export function VerificationPage() {
  const [qrPayload, setQrPayload] = useState('');
  const [purpose, setPurpose] = useState<'EMPLOYMENT' | 'EDUCATION' | 'IMMIGRATION' | 'OTHER'>('EMPLOYMENT');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<VerificationResultResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleVerify = async () => {
    setResult(null);
    setError(null);

    const trimmed = qrPayload.trim();
    if (!trimmed) {
      setError('Please enter a security identifier / QR payload.');
      return;
    }

    setLoading(true);
    try {
      const response = await verificationService.verifyQr({
        qrPayload: trimmed,
        purpose,
        consent: {
          consentType: 'ATTESTED_BY_VERIFIER',
          scope: 'BASIC_DETAILS',
          grantedAt: new Date().toISOString(),
          expiresAt: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
          consentReference: 'MANUAL_ATTESTATION',
        },
      });
      setResult(response);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (axios.isAxiosError(err) && err.response?.data) {
        setError(JSON.stringify(err.response.data));
      } else {
        setError('Verification failed. The record may not exist or the service is unavailable.');
      }
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
            <InputLabel id="verification-purpose-label">Verification Purpose</InputLabel>
            <Select
              labelId="verification-purpose-label"
              value={purpose}
              label="Verification Purpose"
              onChange={(e) =>
                setPurpose(e.target.value as 'EMPLOYMENT' | 'EDUCATION' | 'IMMIGRATION' | 'OTHER')
              }
            >
              <MenuItem value="EMPLOYMENT">Employment</MenuItem>
              <MenuItem value="EDUCATION">Education</MenuItem>
              <MenuItem value="IMMIGRATION">Immigration</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </Select>
          </FormControl>

          <TextField
            fullWidth
            label="QR Payload, Security Identifier, or Certificate Number"
            value={qrPayload}
            onChange={(e) => setQrPayload(e.target.value)}
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
              severity={result.outcome === 'VERIFIED' ? 'success' : 'warning'}
              sx={{ mb: 2 }}
            >
              {result.outcome} — Confidence: {result.confidence} (score: {result.matchScore})
            </Alert>

            {result.qualification && (
              <>
                <Typography variant="h6" gutterBottom>
                  Qualification Details
                </Typography>
                <Typography variant="body1">
                  <strong>Name:</strong> {result.qualification.qualificationName}
                </Typography>
                <Typography variant="body1">
                  <strong>Number:</strong> {result.qualification.qualificationNumber}
                </Typography>
                <Typography variant="body1">
                  <strong>Type:</strong> {result.qualification.qualificationType}
                </Typography>
                <Typography variant="body1">
                  <strong>Classification:</strong> {result.qualification.classification}
                </Typography>
                <Typography variant="body1">
                  <strong>Year of Award:</strong> {result.qualification.yearOfAward}
                </Typography>
                <Typography variant="body1">
                  <strong>Status:</strong> {result.qualification.status}
                </Typography>
                {result.qualification.issuedAt && (
                  <Typography variant="body1">
                    <strong>Issued:</strong> {result.qualification.issuedAt}
                  </Typography>
                )}
                <Typography variant="body1">
                  <strong>Institution:</strong> {result.qualification.institutionName}
                </Typography>
              </>
            )}

            {result.holder && (
              <>
                <Divider sx={{ my: 2 }} />
                <Typography variant="h6" gutterBottom>
                  Holder
                </Typography>
                <Typography variant="body1">
                  <strong>Name:</strong> {result.holder.firstName} {result.holder.lastName}
                </Typography>
              </>
            )}

            {result.verifiedAt && (
              <>
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
