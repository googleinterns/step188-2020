$(function() {
  populateEvents();
});

async function populateEvents() {
  const rankedEvents = await getRankedEvents();
  const eventLevels = getLodsFromEvents(rankedEvents);
  populateRankedEvents(eventLevels);
}

async function getAllEvents() {
  const allEvents = await fetch('discovery-event-details');
  return allEvents.json();
}

function populateRankedEvents(eventLevels) {
  for (const eventMap of eventLevels) {
    populateEventContainer(eventMap['event'], 'event-container', eventMap['lod']);
  }
}
