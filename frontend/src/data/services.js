// Service catalog for the UI. Bookings use durationHours + professionals → backend API.

export const services = [
  {
    id: 'basic-2h',
    name: 'Basic Home Cleaning',
    duration: '2 hours',
    durationHours: 2,
    price: 49,
    professionals: 1,
    description:
      'Standard cleaning for apartments and small homes. Dusting, vacuuming, and kitchen/bathroom wipe-down.',
    image: 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600&q=80',
    features: ['Living areas', 'Kitchen surfaces', 'Bathroom clean', 'Trash removal'],
  },
  {
    id: 'standard-2h',
    name: 'Standard Cleaning (2 Pros)',
    duration: '2 hours',
    durationHours: 2,
    price: 89,
    professionals: 2,
    description:
      'Faster clean with two professionals from the same vehicle team. Good for medium homes.',
    image: 'https://images.unsplash.com/photo-1628177142898-93e36e4e3a50?w=600&q=80',
    features: ['Two cleaners', 'Same vehicle team', 'Deep dusting', 'Floor mopping'],
  },
  {
    id: 'deep-4h',
    name: 'Deep Cleaning',
    duration: '4 hours',
    durationHours: 4,
    price: 129,
    professionals: 2,
    description:
      'Detailed deep clean including hard-to-reach areas. Best before events or seasonal refresh.',
    image: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80',
    features: ['Inside appliances', 'Baseboards', 'Windows (inside)', 'Extra bathrooms'],
  },
  {
    id: 'premium-4h',
    name: 'Premium Team Clean',
    duration: '4 hours',
    durationHours: 4,
    price: 179,
    professionals: 3,
    description:
      'Three professionals for large homes. Same vehicle assignment for coordinated service.',
    image: 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=600&q=80',
    features: ['Three cleaners', 'Large home', 'Priority scheduling', 'Supplies included'],
  },
  {
    id: 'office-2h',
    name: 'Small Office Cleaning',
    duration: '2 hours',
    durationHours: 2,
    price: 69,
    professionals: 1,
    description: 'Desk areas, meeting room, pantry, and restroom refresh for small offices.',
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=600&q=80',
    features: ['Workstations', 'Meeting room', 'Pantry', 'Restroom'],
  },
  {
    id: 'move-4h',
    name: 'Move-in / Move-out',
    duration: '4 hours',
    durationHours: 4,
    price: 149,
    professionals: 2,
    description: 'Empty home cleaning for moving day. Cabinets, closets, and full property pass.',
    image: 'https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=600&q=80',
    features: ['Empty property', 'Cabinets & closets', 'All rooms', 'Final inspection ready'],
  },
];

export function getServiceById(id) {
  return services.find((s) => s.id === id);
}
