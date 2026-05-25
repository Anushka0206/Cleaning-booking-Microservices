import { api, unwrap, getApiErrorMessage } from './client';

export { getApiErrorMessage };

/**
 * POST /api/ai/chat
 * @param {string} message
 * @param {string} [language] auto | en | hi | ar | ur
 * @param {{ role: 'user'|'assistant', content: string }[]} [history] recent turns (excludes current message)
 */
export async function sendChatMessage(message, language = 'auto', history = []) {
  const { data } = await api.post('/api/ai/chat', { message, language, history });
  return unwrap(data);
}

export async function fetchAiStatus(probe = false) {
  const { data } = await api.get('/api/ai/status', { params: { probe } });
  return unwrap(data);
}
