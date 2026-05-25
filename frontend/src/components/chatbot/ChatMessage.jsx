export default function ChatMessage({ role, content, typing }) {
  const isUser = role === 'user';

  return (
    <div className={`flex gap-2 ${isUser ? 'flex-row-reverse' : 'flex-row'}`}>
      <div
        className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-full text-xs ${
          isUser ? 'bg-brand-500 text-white' : 'bg-slate-100 text-slate-600'
        }`}
        aria-hidden
      >
        {isUser ? 'You' : 'JL'}
      </div>
      <div
        className={`max-w-[78%] rounded-2xl px-3.5 py-2.5 text-sm leading-relaxed shadow-sm ${
          isUser
            ? 'rounded-tr-md bg-brand-600 text-white'
            : 'rounded-tl-md border border-slate-100 bg-white text-slate-800'
        }`}
      >
        {typing ? (
          <span className="inline-flex gap-1 py-1">
            <span className="h-2 w-2 animate-bounce rounded-full bg-slate-400 [animation-delay:-0.2s]" />
            <span className="h-2 w-2 animate-bounce rounded-full bg-slate-400 [animation-delay:-0.1s]" />
            <span className="h-2 w-2 animate-bounce rounded-full bg-slate-400" />
          </span>
        ) : (
          <p className="whitespace-pre-wrap">{content}</p>
        )}
      </div>
    </div>
  );
}
