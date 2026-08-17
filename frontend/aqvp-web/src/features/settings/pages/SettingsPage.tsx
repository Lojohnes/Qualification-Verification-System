import { useEffect, useState } from 'react';
import { Box, Chip, Divider, Grid, Paper, Typography } from '@mui/material';

import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { useSnackbar } from '@/hooks/useSnackbar';
import { identityService } from '@/features/identity/services/identityService';
import { ChangePasswordForm } from '@/features/settings/components/ChangePasswordForm';
import type { ChangePasswordRequest, CurrentUserProfile } from '@/types/identity';

export function SettingsPage() {
  const { showSnackbar } = useSnackbar();
  const [profile, setProfile] = useState<CurrentUserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    identityService
      .getCurrentUser()
      .then(setProfile)
      .catch(() => showSnackbar('Failed to load profile.', 'error'))
      .finally(() => setLoading(false));
  }, [showSnackbar]);

  const handleChangePassword = async (data: ChangePasswordRequest) => {
    setSubmitting(true);
    try {
      await identityService.changePassword(data);
      showSnackbar('Password updated successfully.', 'success');
    } catch {
      showSnackbar('Failed to update password. Check your current password.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Settings
      </Typography>
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Paper elevation={2} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Profile
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Box display="flex" flexDirection="column" gap={1.5}>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Username
                  </Typography>
                  <Typography variant="body1">{profile?.username}</Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body1">{profile?.email}</Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Full Name
                  </Typography>
                  <Typography variant="body1">
                    {[profile?.firstName, profile?.lastName].filter(Boolean).join(' ') || '-'}
                  </Typography>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Roles
                  </Typography>
                  <Box display="flex" gap={0.5} flexWrap="wrap" mt={0.5}>
                    {profile?.roles.map((role) => (
                      <Chip key={role} label={role} size="small" />
                    ))}
                  </Box>
                </Box>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Status
                  </Typography>
                  <Typography variant="body1">
                    {profile?.enabled ? 'Active' : 'Inactive'}
                  </Typography>
                </Box>
              </Box>
            </Paper>
          </Grid>
          <Grid item xs={12} md={6}>
            <Paper elevation={2} sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Change Password
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <ChangePasswordForm onSubmit={handleChangePassword} submitting={submitting} />
            </Paper>
          </Grid>
        </Grid>
      )}
    </>
  );
}
