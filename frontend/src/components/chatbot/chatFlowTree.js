/**
 * Guided conversation tree — each path ends with an exact chat command.
 * @typedef {{ id: string, label: { en: string, hi: string }, set?: Record<string, string>, next?: string, leaf?: { message?: string, build?: (a: Record<string, string>) => string }, href?: string }} FlowOption
 * @typedef {{ id: string, question: { en: string, hi: string }, step?: { en: string, hi: string }, options: FlowOption[] }} FlowNode
 */

/** @type {Record<string, FlowNode>} */
export const CHAT_FLOW_TREE = {
  root: {
    id: 'root',
    question: { en: 'What would you like to do?', hi: 'Aap kya karna chahte ho?' },
    step: { en: 'Start', hi: 'Shuru' },
    options: [
      {
        id: 'slots',
        label: { en: 'Check available slots', hi: 'Free slots dekho' },
        next: 'slots_when',
      },
      {
        id: 'book',
        label: { en: 'Book a cleaning', hi: 'Cleaning book karo' },
        next: 'book_service',
      },
      {
        id: 'mybookings',
        label: { en: 'My bookings', hi: 'Meri bookings' },
        next: 'manage_menu',
      },
      {
        id: 'prices',
        label: { en: 'See prices', hi: 'Price batao' },
        leaf: { message: 'what are the prices?' },
      },
      {
        id: 'how',
        label: { en: 'How to book?', hi: 'Kaise book karein?' },
        leaf: { message: 'how do I book?' },
      },
      {
        id: 'who',
        label: { en: 'About assistant', hi: 'Assistant ke baare' },
        leaf: { message: 'who are you' },
      },
    ],
  },

  slots_when: {
    id: 'slots_when',
    question: { en: 'Which day should we check?', hi: 'Kis din ke slots?' },
    step: { en: 'Date', hi: 'Din' },
    options: [
      {
        id: 'tomorrow',
        label: { en: 'Tomorrow', hi: 'Kal' },
        leaf: { message: 'slots tomorrow' },
      },
      {
        id: 'today',
        label: { en: 'Today', hi: 'Aaj' },
        leaf: { message: 'slots today' },
      },
      {
        id: 'd29',
        label: { en: 'Day 29', hi: '29 tareekh' },
        leaf: { message: 'slots on 29' },
      },
      {
        id: 'd27',
        label: { en: 'Day 27', hi: '27 tareekh' },
        leaf: { message: 'slots on 27' },
      },
      {
        id: 'd26',
        label: { en: 'Day 26', hi: '26 tareekh' },
        leaf: { message: 'slots on 26' },
      },
    ],
  },

  book_service: {
    id: 'book_service',
    question: { en: 'Which service?', hi: 'Kaun si service?' },
    step: { en: 'Service', hi: 'Service' },
    options: [
      {
        id: 'standard',
        label: { en: 'Standard ($89)', hi: 'Standard ($89)' },
        set: { service: 'standard' },
        next: 'book_when',
      },
      {
        id: 'deep',
        label: { en: 'Deep clean ($129)', hi: 'Deep ($129)' },
        set: { service: 'deep' },
        next: 'book_when',
      },
      {
        id: 'basic',
        label: { en: 'Basic ($49)', hi: 'Basic ($49)' },
        set: { service: 'basic' },
        next: 'book_when',
      },
      {
        id: 'premium',
        label: { en: 'Premium ($179)', hi: 'Premium ($179)' },
        set: { service: 'premium' },
        next: 'book_when',
      },
      {
        id: 'office',
        label: { en: 'Office ($69)', hi: 'Office ($69)' },
        set: { service: 'office' },
        next: 'book_when',
      },
      {
        id: 'move',
        label: { en: 'Move in/out ($149)', hi: 'Move ($149)' },
        set: { service: 'move' },
        next: 'book_when',
      },
    ],
  },

  book_when: {
    id: 'book_when',
    question: { en: 'When do you want the visit?', hi: 'Kab aana hai?' },
    step: { en: 'Day', hi: 'Din' },
    options: [
      {
        id: 'tomorrow',
        label: { en: 'Tomorrow', hi: 'Kal' },
        set: { date: 'tomorrow' },
        next: 'book_time',
      },
      {
        id: 'today',
        label: { en: 'Today', hi: 'Aaj' },
        set: { date: 'today' },
        next: 'book_time',
      },
      {
        id: 'd29',
        label: { en: 'On day 29', hi: '29 ko' },
        set: { date: 'on 29' },
        next: 'book_time',
      },
      {
        id: 'd27',
        label: { en: 'On day 27', hi: '27 ko' },
        set: { date: 'on 27' },
        next: 'book_time',
      },
    ],
  },

  manage_menu: {
    id: 'manage_menu',
    question: { en: 'Manage your bookings', hi: 'Booking manage karo' },
    step: { en: 'Manage', hi: 'Manage' },
    options: [
      {
        id: 'list',
        label: { en: 'Show my bookings', hi: 'Bookings dikhao' },
        leaf: { message: 'my bookings' },
      },
      {
        id: 'cancel',
        label: { en: 'Cancel a booking', hi: 'Cancel karo' },
        next: 'cancel_which',
      },
      {
        id: 'reschedule',
        label: { en: 'Reschedule', hi: 'Reschedule' },
        next: 'reschedule_when',
      },
    ],
  },

  cancel_which: {
    id: 'cancel_which',
    question: { en: 'Cancel which booking?', hi: 'Kaun si cancel?' },
    step: { en: 'Cancel', hi: 'Cancel' },
    options: [
      {
        id: 'latest',
        label: { en: 'Latest booking', hi: 'Sabse latest' },
        leaf: { message: 'cancel latest' },
      },
      {
        id: 'listfirst',
        label: { en: 'Show IDs first', hi: 'Pehle list dikhao' },
        leaf: { message: 'my bookings' },
      },
    ],
  },

  reschedule_when: {
    id: 'reschedule_when',
    question: { en: 'New day for latest booking?', hi: 'Latest booking — naya din?' },
    step: { en: 'Reschedule', hi: 'Reschedule' },
    options: [
      {
        id: 'tomorrow2',
        label: { en: 'Tomorrow 2pm', hi: 'Kal 2pm' },
        leaf: { message: 'reschedule latest tomorrow 2pm' },
      },
      {
        id: 'tomorrow10',
        label: { en: 'Tomorrow 10am', hi: 'Kal 10am' },
        leaf: { message: 'reschedule latest tomorrow 10am' },
      },
      {
        id: 'd29_2',
        label: { en: 'Day 29 at 2pm', hi: '29 ko 2pm' },
        leaf: { message: 'reschedule latest on 29 2pm' },
      },
      {
        id: 'listfirst',
        label: { en: 'Show my bookings', hi: 'Bookings dikhao' },
        leaf: { message: 'my bookings' },
      },
    ],
  },

  book_time: {
    id: 'book_time',
    question: { en: 'Preferred start time?', hi: 'Kitne baje?' },
    step: { en: 'Time', hi: 'Samay' },
    options: [
      {
        id: 't9',
        label: { en: '9:00 AM', hi: '9 AM' },
        set: { time: '9am' },
        leaf: { build: buildBookMessage },
      },
      {
        id: 't10',
        label: { en: '10:00 AM', hi: '10 AM' },
        set: { time: '10am' },
        leaf: { build: buildBookMessage },
      },
      {
        id: 't2',
        label: { en: '2:00 PM', hi: '2 PM' },
        set: { time: '2pm' },
        leaf: { build: buildBookMessage },
      },
      {
        id: 't3',
        label: { en: '3:00 PM', hi: '3 PM' },
        set: { time: '3pm' },
        leaf: { build: buildBookMessage },
      },
      {
        id: 't4',
        label: { en: '4:00 PM', hi: '4 PM' },
        set: { time: '4pm' },
        leaf: { build: buildBookMessage },
      },
    ],
  },
};

function buildBookMessage(answers) {
  const service = answers.service || 'standard';
  const date = answers.date || 'tomorrow';
  const time = answers.time || '2pm';
  return `book ${service} ${date} ${time}`;
}

/**
 * @param {string} nodeId
 * @param {{ isLoggedIn?: boolean, language?: string }} ctx
 */
export function getFlowNode(nodeId, ctx = {}) {
  const node = CHAT_FLOW_TREE[nodeId];
  if (!node) return CHAT_FLOW_TREE.root;

  if (nodeId !== 'root') return node;

  const lang = ctx.language === 'hi' ? 'hi' : 'en';
  const options = [...node.options];
  if (!ctx.isLoggedIn) {
    options.push({
      id: 'login',
      label: { en: 'Log in first', hi: 'Pehle login' },
      href: '/login',
    });
  }
  return { ...node, options };
}

export function t(text, language) {
  if (!text) return '';
  const lang = language === 'hi' ? 'hi' : 'en';
  return text[lang] || text.en;
}

/**
 * @param {FlowOption} option
 * @param {Record<string, string>} answers
 */
export function resolveLeafMessage(option, answers) {
  if (!option.leaf) return null;
  if (option.leaf.message) return option.leaf.message;
  if (option.leaf.build) {
    const merged = { ...answers, ...option.set };
    return option.leaf.build(merged);
  }
  return null;
}

/** Breadcrumb labels from stack + current node */
export function flowBreadcrumbs(stack, currentNodeId, language) {
  const crumbs = stack
    .map((s) => {
      const n = CHAT_FLOW_TREE[s.nodeId];
      return n?.step ? t(n.step, language) : null;
    })
    .filter(Boolean);
  const current = CHAT_FLOW_TREE[currentNodeId];
  if (current?.step) crumbs.push(t(current.step, language));
  return crumbs;
}

export const INITIAL_FLOW = { nodeId: 'root', answers: {}, stack: [] };
