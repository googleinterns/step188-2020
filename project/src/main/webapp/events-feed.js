$(async function() {
  const allEvents = await getAllEvents();
  populateAllEvents(allEvents);
});

async function getAllEvents() {
  const allEvents = await fetch('/discovery-event-details');
  return allEvents.json();
}

function populateAllEvents(allEvents) {
  for (const event of allEvents) {
    populateEventContainer(event, 'event-container');
  }
}
