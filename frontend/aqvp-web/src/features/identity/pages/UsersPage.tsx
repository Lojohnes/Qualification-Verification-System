import { useEffect, useState } from 'react';
import { Typography, Paper, Box } from '@mui/material';

import { DataTable } from '@/components/ui/DataTable';
import { LoadingSpinner } from '@/components/ui/LoadingSpinner';
import { SearchBar } from '@/components/ui/SearchBar';
import { identityService } from '@/features/identity/services/identityService';
import type { UserListItem } from '@/types/identity';

export function UsersPage() {
  const [users, setUsers] = useState<UserListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    identityService
      .getUsers()
      .then((data) => setUsers(data))
      .catch(() => setUsers([]))
      .finally(() => setLoading(false));
  }, []);

  const filtered = users.filter(
    (u) =>
      u.username.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <Typography variant="h4" fontWeight={600} gutterBottom>
        Users
      </Typography>
      <Box mb={2}>
        <SearchBar value={search} onChange={setSearch} placeholder="Search users..." />
      </Box>
      <Paper elevation={2} sx={{ p: 3 }}>
        {loading ? (
          <LoadingSpinner />
        ) : (
          <DataTable
            data={filtered}
            keyExtractor={(row) => row.id}
            columns={[
              { key: 'username', header: 'Username' },
              { key: 'email', header: 'Email' },
              { key: 'firstName', header: 'First Name' },
              { key: 'lastName', header: 'Last Name' },
              {
                key: 'enabled',
                header: 'Status',
                render: (row) => (row.enabled ? 'Active' : 'Inactive'),
              },
            ]}
          />
        )}
      </Paper>
    </>
  );
}
