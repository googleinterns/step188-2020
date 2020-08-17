$(async function() {
  const allEvents = await getAllEvents();
  populateAllEvents(allEvents);
});

$(document).ready(function(){
  $(".dropdown-toggle").dropdown();
});

async function getAllEvents() {
  const allEvents = await fetch('discovery-event-details');
  return allEvents.json();
}

function populateAllEvents(allEvents) {
  for (const event of allEvents) {
    populateEventContainer(event, 'event-container');
    getInterestFilters(event);
  }
  populateFilters(filters)
}

const filters = {}

// Fill filter dict with {labelName: count}
function getInterestFilters(event) {
  for (interest in event.labels) {
    if (!(event.labels[interest] in filters)) {
      filters[event.labels[interest]] = 1;
    } else {
      filters[event.labels[interest]] = filters[interest] + 1;
    }
  }
}

// Populate filter tab with label checkboxes
function populateFilters(filters) {
    for (const key in filters) {
      $('#filterCheckBoxes')
        .append(getCheckboxes(key, filters[key]))
    }
    $('#filterCheckBoxes').append(`<br>`)
}

// Gives HTML for checkboxes
function getCheckboxes(key, value) {
    return `<input type="checkbox" id=${key} name=${key} value=${key}>
  <label for=${key}> ${key} (${value})</label><br>`
}

// Gets checked checkboxes and returns events that match that filter
async function getFilteredEvents() {
  const checks = $('input[type="checkbox"]:checked').map(function() {
    return $(this).val();
  }).get()
  labelParams = checks.join("-")

//TODO: when backend is built
// const response = await fetch('/get-filtered-events?' + new URLSearchParams({'eventId': labelParams}));
// const data = await response.json();
// call populateAllEvents(data) again if not onload
}