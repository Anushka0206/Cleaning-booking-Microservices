import { api, unwrap } from './client';

export async function fetchTeams() {
  const { data } = await api.post('/api/v1/vehicles', {
    pagination: { pageNumber: 1, pageSize: 50 },
  });
  const page = unwrap(data);
  return page?.content ?? [];
}
