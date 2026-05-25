import { Link } from 'react-router-dom';
import {
  flowBreadcrumbs,
  getFlowNode,
  resolveLeafMessage,
  t,
} from './chatFlowTree';

export default function ChatFlowGuide({
  flowState,
  language,
  isLoggedIn,
  disabled,
  onChoice,
  onSkip,
  onBack,
  onRestart,
}) {
  const node = getFlowNode(flowState.nodeId, { isLoggedIn, language });
  const crumbs = flowBreadcrumbs(flowState.stack, flowState.nodeId, language);
  const depth = flowState.stack.length + 1;

  return (
    <div className="border-t border-slate-200 bg-gradient-to-b from-slate-50 to-white px-3 py-3">
      <div className="mb-2 flex items-center justify-between gap-2">
        <div className="min-w-0 flex-1">
          <p className="text-[10px] font-semibold uppercase tracking-wide text-brand-600">
            Guided steps {depth > 0 ? `· ${depth}` : ''}
          </p>
          {crumbs.length > 0 && (
            <p className="truncate text-[11px] text-slate-500" title={crumbs.join(' → ')}>
              {crumbs.join(' → ')}
            </p>
          )}
        </div>
        <div className="flex shrink-0 gap-1">
          {flowState.stack.length > 0 && (
            <button
              type="button"
              disabled={disabled}
              onClick={onBack}
              className="rounded-md border border-slate-200 bg-white px-2 py-1 text-[11px] font-medium text-slate-600 hover:bg-slate-100 disabled:opacity-50"
            >
              Back
            </button>
          )}
          <button
            type="button"
            disabled={disabled}
            onClick={onSkip}
            className="rounded-md border border-slate-200 bg-white px-2 py-1 text-[11px] font-medium text-slate-500 hover:border-amber-200 hover:bg-amber-50 hover:text-amber-800 disabled:opacity-50"
            title="Skip questions and type freely"
          >
            Skip
          </button>
        </div>
      </div>

      <p className="mb-2 text-sm font-medium text-slate-800">{t(node.question, language)}</p>

      <div className="grid grid-cols-2 gap-1.5 sm:grid-cols-2">
        {node.options.map((opt) =>
          opt.href ? (
            <Link
              key={opt.id}
              to={opt.href}
              className="col-span-2 flex items-center justify-center rounded-xl border-2 border-brand-200 bg-brand-50 px-3 py-2.5 text-center text-xs font-semibold text-brand-800 transition hover:bg-brand-100"
            >
              {t(opt.label, language)}
            </Link>
          ) : (
            <button
              key={opt.id}
              type="button"
              disabled={disabled}
              onClick={() => onChoice(opt)}
              className="rounded-xl border border-slate-200 bg-white px-2 py-2.5 text-xs font-medium text-slate-700 shadow-sm transition hover:border-brand-400 hover:bg-brand-50 hover:text-brand-900 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {t(opt.label, language)}
            </button>
          )
        )}
      </div>

      {flowState.nodeId !== 'root' && (
        <button
          type="button"
          disabled={disabled}
          onClick={onRestart}
          className="mt-2 w-full text-center text-[11px] text-slate-400 underline-offset-2 hover:text-brand-600 hover:underline"
        >
          Start over from menu
        </button>
      )}
    </div>
  );
}

/** Preview before sending leaf message */
export function ChatFlowPreview({ message, language, disabled, onSend, onEdit }) {
  if (!message) return null;
  const hi = language === 'hi';
  return (
    <div className="border-t border-emerald-100 bg-emerald-50/80 px-3 py-2">
      <p className="mb-1 text-[10px] font-semibold uppercase text-emerald-700">
        {hi ? 'Yeh message bhejenge' : 'Ready to send'}
      </p>
      <p className="mb-2 rounded-lg border border-emerald-200 bg-white px-2 py-1.5 font-mono text-xs text-slate-800">
        {message}
      </p>
      <div className="flex gap-2">
        <button
          type="button"
          disabled={disabled}
          onClick={onSend}
          className="flex-1 rounded-lg bg-brand-600 py-2 text-xs font-semibold text-white hover:bg-brand-700 disabled:opacity-50"
        >
          {hi ? 'Bhejo' : 'Send now'}
        </button>
        <button
          type="button"
          disabled={disabled}
          onClick={onEdit}
          className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-xs font-medium text-slate-600 hover:bg-slate-50"
        >
          {hi ? 'Badlo' : 'Change'}
        </button>
      </div>
    </div>
  );
}

