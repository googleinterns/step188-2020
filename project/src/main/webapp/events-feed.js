$(async function() {
  const allEvents = await getAllEvents();
  populateAllEvents(allEvents);
});

async function getAllEvents() {
  const allEvents = await fetch('event-details');
  return allEvents.json();
}

function populateAllEvents(allEvents) {
  for (const event of allEvents) {
    populateEventContainer(event);
  }
}

/** Writes out relevant details to an event card */
function populateEventContainer(event) {
  const indexOfEventCard = 25;
  $.get('event-card.html', function(eventCardTotal) {
    const eventCardId = 'event-' + event.eventId;
    const eventCard = $(eventCardTotal).get(indexOfEventCard);
    $(eventCard).attr('id', eventCardId);
    $('#event-container').append(eventCard);
    $('#' + eventCardId + ' #event-card-title').html(event.name);
    $('#' + eventCardId + ' #event-card-description').html(event.description);
    $('#' + eventCardId + ' #event-card-date')
        .html(buildDate(event.date.year, event.date.month, event.date.dayOfMonth));
    $('#' + eventCardId + ' #event-card-time').html(event.time);
    $('#' + eventCardId + ' #event-card-location').html(event.location);
    $('#' + eventCardId + ' #event-card-volunteers')
        .html(buildVolunteers(event.opportunities));
    buildAsLabels(eventCardId, event.labels, 'interests');
    buildSkillsAsLabels(eventCardId, event.opportunities);
    addLinkToRegister(eventCardId);
    addLinkToDetails(eventCardId);
  });
}

/** Adds a hyperlink to the registration button of event card */
function addLinkToRegister(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' .btn-primary #event-register')
      .attr('href', `/event-details.html?eventId=${eventId}&register=true`);
}

/** Adds a hyperlink to the details button of event card */
function addLinkToDetails(eventCardId) {
  const eventId = eventCardId.substring(6);
  $('#' + eventCardId + ' .btn-primary #event-details')
      .attr('href', `/event-details.html?eventId=${eventId}&register=false`);
}

function buildDate(year, month, dayOfMonth) {
  return month + '/' + dayOfMonth + '/' + year;
}

/** Creates a properly-formatted volunteering opportunity string */
function buildVolunteers(opportunities) {
  let opportunityString = '';
  for (const opportunity of opportunities) {
    opportunityString += opportunity.name + ', ';
  }
  return opportunityString.slice(0, -2);
}

/** Creates a button label for each provided interest or skill */
function buildAsLabels(eventCardId, labels, className) {
  for (const label of labels) {
    const newLabelButton = document.createElement('button');
    newLabelButton.classList.add(`btn-{className}`);
    newLabelButton.classList.add('btn');
    newLabelButton.disabled = true;
    newLabelButton.innerHTML = label;
    document
        .querySelector('#' + eventCardId + ' #event-card-labels')
        .appendChild(newLabelButton);
  }
}

/** Creates the corresponding skill button labels for each volunteering opportunity */
function buildSkillsAsLabels(eventId, opportunities) {
  for (const opportunity of opportunities) {
    buildAsLabels(eventId, opportunity.requiredSkills, 'skills');
  }
}
