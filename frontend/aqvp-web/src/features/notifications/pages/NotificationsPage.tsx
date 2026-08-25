import { useState } from 'react';
import { Box, Button, MenuItem, Paper, TextField, Typography } from '@mui/material';
import SendIcon from '@mui/icons-material/Send';

import { useSnackbar } from '@/hooks/useSnackbar';
import { notificationService, type NotificationChannel } from '@/features/notifications/services/notificationService';

export function NotificationsPage() {
  const { showSnackbar } = useSnackbar();
  const [templateCode, setTemplateCode] = useState('');
  const [channel, setChannel] = useState<NotificationChannel>('EMAIL');
  const [recipient, setRecipient] = useState('');
  const [subject, setSubject] = useState('');
  const [message, setMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    try {
      const notification = await notificationService.send({
        templateCode,
        channel,
        recipient,
        subject,
        message,
      });
      const isMock = notification.providerReference?.startsWith('mock-provider-');
      showSnackbar(
        isMock
          ? 'Notification recorded in mock mode; no email or SMS was sent.'
          : `Notification ${notification.status.toLowerCase()}.`,
        isMock ? 'warning' : notification.status === 'SENT' ? 'success' : 'error'
      );
      setTemplateCode('');
      setRecipient('');
      setSubject('');
      setMessage('');
    } catch {
      showSnackbar('Failed to send notification.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Notifications
      </Typography>
      <Paper component="form" onSubmit={handleSubmit} sx={{ p: 3, maxWidth: 720 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Dispatch an email or SMS through the Admin notification service.
        </Typography>
        <Box display="flex" flexDirection="column" gap={2}>
          <TextField required label="Template code" value={templateCode} onChange={(event) => setTemplateCode(event.target.value)} />
          <TextField select required label="Channel" value={channel} onChange={(event) => setChannel(event.target.value as NotificationChannel)}>
            <MenuItem value="EMAIL">Email</MenuItem>
            <MenuItem value="SMS">SMS</MenuItem>
          </TextField>
          <TextField required label="Recipient" value={recipient} onChange={(event) => setRecipient(event.target.value)} />
          <TextField label="Subject" value={subject} onChange={(event) => setSubject(event.target.value)} />
          <TextField required multiline minRows={5} label="Message" value={message} onChange={(event) => setMessage(event.target.value)} />
          <Button type="submit" variant="contained" startIcon={<SendIcon />} disabled={submitting}>
            {submitting ? 'Sending...' : 'Send notification'}
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
