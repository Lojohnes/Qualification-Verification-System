export const formatDate = (value: string | Date | number | undefined): string => {
  if (!value) return '-';
  return new Date(value).toLocaleString();
};

export const formatStatus = (value?: string): string => {
  if (!value) return '-';
  return value
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, (char) => char.toUpperCase());
};
