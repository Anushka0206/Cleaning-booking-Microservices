import { useCallback, useEffect, useRef, useState } from 'react';
import { sendChatMessage, getApiErrorMessage } from '../../api/aiApi';
import { useAuth } from '../../context/AuthContext';
import { services } from '../../data/services';
import { saveBookingFromApi } from '../../utils/bookings';
import ChatMessage from './ChatMessage';
import ChatFlowGuide, { ChatFlowPreview } from './ChatFlowGuide';
import { INITIAL_FLOW, resolveLeafMessage } from './chatFlowTree';
import useVoiceAssistant from './useVoiceAssistant';

const WELCOME = {
  id: 'welcome',
  role: 'assistant',
  content:
    'Hi! Guide: book, slots, my bookings, cancel, reschedule.\nPick steps below or type freely (Skip anytime).',
};

const HISTORY_LIMIT = 8;

function buildChatHistory(messages) {
  return messages
    .filter((m) => m.role === 'user' || m.role === 'assistant')
    .slice(-HISTORY_LIMIT)
    .map((m) => ({ role: m.role, content: m.content }));
}

function persistChatBooking(booking, customerName) {
  const service =
    services.find((s) => s.id === booking.serviceId) ?? {
      id: booking.serviceId,
      name: booking.serviceName,
      price: booking.price,
      duration: `${booking.durationHours} hours`,
      durationHours: booking.durationHours,
      professionals: booking.durationHours === 4 ? 2 : 1,
    };
  saveBookingFromApi({
    customer: {
      name: customerName || 'Chat booking',
      address: '—',
      date: booking.date,
      time: booking.time,
    },
    service,
    apiBooking: {
      id: booking.id,
      startAt: booking.startAt,
      endAt: booking.endAt,
      durationHours: booking.durationHours,
      vehicleId: booking.vehicleId,
    },
  });
}

export default function ChatbotWidget() {
  const { user } = useAuth();
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([WELCOME]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [voiceReplies, setVoiceReplies] = useState(true);
  const [language, setLanguage] = useState('auto');
  const [lastProvider, setLastProvider] = useState('');
  const [guideMode, setGuideMode] = useState(true);
  const [flowState, setFlowState] = useState(INITIAL_FLOW);
  const [pendingSend, setPendingSend] = useState(null);
  const scrollRef = useRef(null);

  const scrollToBottom = useCallback(() => {
    requestAnimationFrame(() => {
      scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' });
    });
  }, []);

  useEffect(() => {
    if (open) scrollToBottom();
  }, [messages, loading, open, pendingSend, scrollToBottom]);

  const sendMessageRef = useRef(null);

  const onFinalTranscript = useCallback((text) => {
    setInput(text);
    sendMessageRef.current?.(text);
  }, []);

  const { supported, listening, transcript, startListening, stopListening, speak, setTranscript } =
    useVoiceAssistant({ onFinalTranscript, speakReplies: voiceReplies });

  const resetFlow = useCallback(() => {
    setFlowState(INITIAL_FLOW);
    setPendingSend(null);
  }, []);

  const sendMessage = useCallback(
    async (text, { fromGuide = false } = {}) => {
      const trimmed = text?.trim();
      if (!trimmed || loading) return;

      setError('');
      setPendingSend(null);
      if (fromGuide) setGuideMode(true);

      const userMsg = { id: crypto.randomUUID(), role: 'user', content: trimmed };
      setMessages((prev) => [...prev, userMsg]);
      setInput('');
      setLoading(true);

      try {
        const history = buildChatHistory(messages);
        const data = await sendChatMessage(trimmed, language, history);
        const reply = data?.reply ?? 'No reply received.';
        if (data?.provider) setLastProvider(data.provider);
        if (data?.booking) persistChatBooking(data.booking, user?.name);
        const assistantMsg = {
          id: crypto.randomUUID(),
          role: 'assistant',
          content: reply,
        };
        setMessages((prev) => [...prev, assistantMsg]);
        resetFlow();
        if (voiceReplies) speak(reply);
      } catch (err) {
        setError(getApiErrorMessage(err));
      } finally {
        setLoading(false);
      }
    },
    [loading, voiceReplies, speak, language, messages, user?.name, resetFlow]
  );

  sendMessageRef.current = sendMessage;

  function handleFlowChoice(option) {
    if (option.href) return;

    const merged = { ...flowState.answers, ...(option.set || {}) };

    if (option.leaf) {
      const msg = resolveLeafMessage(option, merged);
      if (msg) {
        setPendingSend(msg);
        setFlowState((prev) => ({ ...prev, answers: merged }));
      }
      return;
    }

    if (option.next) {
      setFlowState((prev) => ({
        nodeId: option.next,
        answers: merged,
        stack: [...prev.stack, { nodeId: prev.nodeId, answers: prev.answers }],
      }));
      setPendingSend(null);
    }
  }

  function handleFlowBack() {
    setPendingSend(null);
    setFlowState((prev) => {
      if (prev.stack.length === 0) return INITIAL_FLOW;
      const next = [...prev.stack];
      const last = next.pop();
      return {
        nodeId: last.nodeId,
        answers: last.answers,
        stack: next,
      };
    });
  }

  function handleSkipGuide() {
    setGuideMode(false);
    setPendingSend(null);
    resetFlow();
  }

  function handleMicClick() {
    if (!supported) {
      setError('Voice not supported in this browser. Try Chrome or Edge.');
      return;
    }
    if (listening) stopListening();
    else startListening();
  }

  function handleSubmit(e) {
    e.preventDefault();
    sendMessage(input);
  }

  return (
    <>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="fixed bottom-6 right-6 z-50 flex h-14 w-14 items-center justify-center rounded-full bg-brand-600 text-2xl text-white shadow-lg ring-4 ring-brand-100 hover:bg-brand-700 focus:outline-none focus:ring-2 focus:ring-brand-400"
        aria-label={open ? 'Close assistant' : 'Open assistant'}
      >
        {open ? '✕' : '💬'}
      </button>

      {open && (
        <div
          className="fixed bottom-24 right-4 z-50 flex h-[min(560px,85vh)] w-[min(420px,calc(100vw-1.5rem))] flex-col overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-2xl"
          role="dialog"
          aria-label="AI assistant"
        >
          <header className="shrink-0 bg-gradient-to-br from-brand-600 to-brand-700 px-4 py-3 text-white">
            <div className="flex items-start justify-between gap-2">
              <div className="flex gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/20 text-sm font-bold">
                  JL
                </div>
                <div>
                  <p className="font-semibold leading-tight">JustLife Assistant</p>
                  <p className="text-xs text-brand-100">
                    {guideMode
                      ? 'Step-by-step guide'
                      : lastProvider
                        ? `via ${lastProvider}`
                        : 'Free chat'}
                  </p>
                </div>
              </div>
              <label className="flex items-center gap-1 rounded-lg bg-white/10 px-2 py-1 text-xs">
                <input
                  type="checkbox"
                  checked={voiceReplies}
                  onChange={(e) => setVoiceReplies(e.target.checked)}
                  className="rounded"
                />
                Voice
              </label>
            </div>
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value)}
              className="mt-2 w-full rounded-lg border border-white/20 bg-white/10 px-2 py-1.5 text-xs text-white backdrop-blur"
              aria-label="Reply language"
            >
              <option value="auto" className="text-slate-900">
                Language: Auto
              </option>
              <option value="en" className="text-slate-900">
                English
              </option>
              <option value="hi" className="text-slate-900">
                Hindi
              </option>
              <option value="ar" className="text-slate-900">
                Arabic
              </option>
              <option value="ur" className="text-slate-900">
                Urdu
              </option>
            </select>
          </header>

          <div ref={scrollRef} className="flex-1 space-y-3 overflow-y-auto bg-slate-50/50 p-4">
            {messages.map((m) => (
              <ChatMessage key={m.id} role={m.role} content={m.content} />
            ))}
            {loading && <ChatMessage role="assistant" typing />}
          </div>

          {(listening || transcript) && (
            <div className="shrink-0 border-t border-amber-100 bg-amber-50 px-4 py-2 text-xs text-amber-900">
              {listening ? '🎤 Listening…' : 'Heard:'} {transcript || '…'}
            </div>
          )}

          {error && (
            <p className="shrink-0 border-t border-red-100 bg-red-50 px-4 py-2 text-xs text-red-700">
              {error}
            </p>
          )}

          {guideMode ? (
            pendingSend ? (
              <ChatFlowPreview
                message={pendingSend}
                language={language}
                disabled={loading}
                onSend={() => sendMessage(pendingSend, { fromGuide: true })}
                onEdit={() => setPendingSend(null)}
              />
            ) : (
              <ChatFlowGuide
                flowState={flowState}
                language={language}
                isLoggedIn={Boolean(user)}
                disabled={loading}
                onChoice={handleFlowChoice}
                onSkip={handleSkipGuide}
                onBack={handleFlowBack}
                onRestart={resetFlow}
              />
            )
          ) : (
            <div className="shrink-0 border-t border-slate-100 bg-slate-50 px-3 py-2">
              <button
                type="button"
                disabled={loading}
                onClick={() => {
                  setGuideMode(true);
                  resetFlow();
                }}
                className="w-full rounded-lg border border-dashed border-brand-300 bg-white py-2 text-xs font-medium text-brand-700 hover:bg-brand-50"
              >
                Start step-by-step guide
              </button>
            </div>
          )}

          <form
            onSubmit={handleSubmit}
            className="flex shrink-0 gap-2 border-t border-slate-200 bg-white p-3"
          >
            <button
              type="button"
              onClick={handleMicClick}
              disabled={loading}
              className={`rounded-xl border px-3 py-2.5 text-lg ${
                listening
                  ? 'border-red-300 bg-red-50 text-red-600'
                  : 'border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
              title="Speech to text"
            >
              🎤
            </button>
            <input
              type="text"
              value={input}
              onChange={(e) => {
                setInput(e.target.value);
                setTranscript(e.target.value);
              }}
              placeholder={
                guideMode ? 'Or type here · Skip guide anytime' : 'Type your message…'
              }
              className="min-w-0 flex-1 rounded-xl border border-slate-200 bg-slate-50 px-3 py-2.5 text-sm focus:border-brand-500 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/20"
              disabled={loading}
            />
            <button
              type="submit"
              disabled={loading || !input.trim()}
              className="rounded-xl bg-brand-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-brand-700 disabled:opacity-50"
            >
              Send
            </button>
          </form>
        </div>
      )}
    </>
  );
}
