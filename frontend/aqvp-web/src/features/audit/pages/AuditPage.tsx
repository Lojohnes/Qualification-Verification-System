import { useCallback, useEffect, useState } from 'react';
import { Box, Button, Grid, Paper, TextField, Typography } from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { useSnackbar } from '@/hooks/useSnackbar';
import { auditService, type AuditEvent, type AuditEventFilters } from '@/features/audit/services/auditService';

const initialFilters: AuditEventFilters = {
  actorName: '',
  resourceType: '',
  fromDate: '',
  toDate: '',
};

export function AuditPage() {
  const { showSnackbar } = useSnackbar();
  const [filters, setFilters] = useState(initialFilters);
  const [events, setEvents] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(true);

  const loadEvents = useCallback(async (currentFilters: AuditEventFilters) => {
    setLoading(true);
    try {
      setEvents(await auditService.searchEvents(currentFilters));
    } catch {
      showSnackbar('Failed to load audit events.', 'error');
    } finally {
      setLoading(false);
    }
  }, [showSnackbar]);

  useEffect(() => {
    void loadEvents(initialFilters);
  }, [loadEvents]);

  const handleChange = (key: keyof AuditEventFilters, value: string) => {
    setFilters((current) => ({ ...current, [key]: value }));
  };

  const handleSearch = () => {
    void loadEvents(filters);
  };

  return (
    <Box>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Audit Log
      </Typography>
      <Paper sx={{ p: 2, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={3}>
            <TextField fullWidth label="Actor" value={filters.actorName} onChange={(event) => handleChange('actorName', event.target.value)} />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <TextField fullWidth label="Resource type" value={filters.resourceType} onChange={(event) => handleChange('resourceType', event.target.value)} />
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <TextField fullWidth label="From" type="date" value={filters.fromDate} onChange={(event) => handleChange('fromDate', event.target.value)} InputLabelProps={{ shrink: true }} />
          </Grid>
          <Grid item xs={12} sm={6} md={2}>
            <TextField fullWidth label="To" type="date" value={filters.toDate} onChange={(event) => handleChange('toDate', event.target.value)} InputLabelProps={{ shrink: true }} />
          </Grid>
          <Grid item xs={12} md={2}>
            <Button fullWidth variant="contained" startIcon={<SearchIcon />} onClick={handleSearch}>
              Search
            </Button>
          </Grid>
        </Grid>
      </Paper>
      {loading ? <LoadingSpinner /> : (
        <DataTable
          data={events}
          keyExtractor={(event) => event.id}
          emptyMessage="No audit events found."
          columns={[
            { key: 'occurredAt', header: 'Occurred', render: (event) => new Date(event.occurredAt).toLocaleString() },
            { key: 'eventType', header: 'Event' },
            { key: 'action', header: 'Action' },
            { key: 'actorName', header: 'Actor', render: (event) => event.actorName || '-' },
            { key: 'resourceType', header: 'Resource', render: (event) => event.resourceName ? `${event.resourceType || '-'}: ${event.resourceName}` : event.resourceType || '-' },
            { key: 'actorRole', header: 'Role', render: (event) => event.actorRole || '-' },
          ]}
        />
      )}
    </Box>
  );
}
