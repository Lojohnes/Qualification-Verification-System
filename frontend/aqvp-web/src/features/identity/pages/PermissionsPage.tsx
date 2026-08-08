import { useEffect, useState } from 'react';
import { Typography, Paper } from '@mui/material';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { identityService } from '@/features/identity/services/identityService';
import type { Permission } from '@/types/identity';

export function PermissionsPage() {
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    identityService
      .getPermissions()
      .then((data) => setPermissions(data))
      .catch(() => setPermissions([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Permissions
      </Typography>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={permissions}
            keyExtractor={(row) => row.id}
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'resource', header: 'Resource' },
              { key: 'action', header: 'Action' },
              { key: 'description', header: 'Description' },
            ]}
          />
        )}
      </Paper>
    </>
  );
}
