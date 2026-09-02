import { Chip } from '@mui/material';

import { DataTable } from '@/components/ui/DataTable';
import { formatDate } from '@/utils/formatters';
import type { AuditEvent } from '@/features/audit/services/auditService';

interface RecentActivityTableProps {
  events: AuditEvent[];
}

const getStatus = (event: AuditEvent): 'success' | 'warning' | 'error' => {
  if (event.action === 'REVOKE' || event.action === 'FAILED') return 'error';
  if (event.action === 'AMEND') return 'warning';
  return 'success';
};

export function RecentActivityTable({ events }: RecentActivityTableProps) {
  return (
    <DataTable
      data={events}
      keyExtractor={(row) => row.id}
      emptyMessage="No recent activity recorded."
      columns={[
        { key: 'action', header: 'Action', render: (row) => row.eventType || row.action },
        { key: 'actor', header: 'Actor', render: (row) => row.actorName || 'System' },
        {
          key: 'status',
          header: 'Status',
          render: (row) => {
            const status = getStatus(row);
            return <Chip label={status} color={status} size="small" />;
          },
        },
        {
          key: 'timestamp',
          header: 'Timestamp',
          render: (row) => formatDate(row.occurredAt),
        },
      ]}
    />
  );
}
