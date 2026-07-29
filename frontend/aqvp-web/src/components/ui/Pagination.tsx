import { Box, TablePagination } from '@mui/material';

interface PaginationProps {
  page: number;
  size: number;
  totalElements: number;
  onPageChange: (page: number) => void;
  onSizeChange: (size: number) => void;
}

export function Pagination({
  page,
  size,
  totalElements,
  onPageChange,
  onSizeChange,
}: PaginationProps) {
  return (
    <Box display="flex" justifyContent="flex-end" mt={2}>
      <TablePagination
        component="div"
        count={totalElements}
        page={page}
        onPageChange={(_, newPage) => onPageChange(newPage)}
        rowsPerPage={size}
        onRowsPerPageChange={(e) => onSizeChange(Number(e.target.value))}
        rowsPerPageOptions={[5, 10, 20, 50]}
      />
    </Box>
  );
}
