$(function() {
  populateEvents();
});

async function populateEvents() {
  const rankedEvents = await getRankedEvents();
  const lodMap = getLodsFromEvents(rankedEvents);
  populateRankedEvents(lodMap);
}

async function getAllEvents() {
  const allEvents = await fetch('discovery-event-details');
  return allEvents.json();
}

function populateRankedEvents(lodMap) {
  for (const eventMap of lodMap) {
    populateEventContainer(eventMap['event'], 'event-container', eventMap['lod']);
  }
}
