import { Chip } from '@mui/material';

import { DataTable } from '@/components/ui/DataTable';
import { formatDate } from '@/utils/formatters';

interface Activity {
  id: string;
  action: string;
  actor: string;
  timestamp: string;
  status: 'success' | 'warning' | 'error';
}

const activities: Activity[] = [
  {
    id: '1',
    action: 'Qualification issued',
    actor: 'Takunda Mazambani',
    timestamp: '2026-07-29T09:23:00',
    status: 'success',
  },
  {
    id: '2',
    action: 'Verification request',
    actor: 'Wonder Mangwendeza',
    timestamp: '2026-07-29T08:45:00',
    status: 'success',
  },
  {
    id: '3',
    action: 'User role updated',
    actor: 'Tsakane Sithole',
    timestamp: '2026-07-28T16:12:00',
    status: 'warning',
  },
  {
    id: '4',
    action: 'Certificate generated',
    actor: 'Memory Chikomo',
    timestamp: '2026-07-28T14:30:00',
    status: 'success',
  },
  {
    id: '5',
    action: 'Failed login attempt',
    actor: 'Unknown',
    timestamp: '2026-07-28T11:05:00',
    status: 'error',
  },
];

export function RecentActivityTable() {
  return (
    <DataTable
      data={activities}
      keyExtractor={(row) => row.id}
      columns={[
        { key: 'action', header: 'Action' },
        { key: 'actor', header: 'Actor' },
        {
          key: 'status',
          header: 'Status',
          render: (row) => (
            <Chip
              label={row.status}
              color={
                row.status === 'success'
                  ? 'success'
                  : row.status === 'warning'
                    ? 'warning'
                    : 'error'
              }
              size="small"
            />
          ),
        },
        {
          key: 'timestamp',
          header: 'Timestamp',
          render: (row) => formatDate(row.timestamp),
        },
      ]}
    />
  );
}
