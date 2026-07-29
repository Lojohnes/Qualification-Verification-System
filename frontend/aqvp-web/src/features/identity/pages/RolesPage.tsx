import { useEffect, useState } from 'react';
import { Typography, Paper } from '@mui/material';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { identityService } from '@/features/identity/services/identityService';
import type { Role } from '@/types/identity';

export function RolesPage() {
  const [roles, setRoles] = useState<Role[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    identityService
      .getRoles()
      .then((data) => setRoles(data))
      .catch(() => setRoles([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Roles
      </Typography>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={roles}
            keyExtractor={(row) => row.id}
            columns={[
              { key: 'name', header: 'Name' },
              { key: 'description', header: 'Description' },
              {
                key: 'permissions',
                header: 'Permissions',
                render: (row) => row.permissions.join(', '),
              },
            ]}
          />
        )}
      </Paper>
    </>
  );
}
