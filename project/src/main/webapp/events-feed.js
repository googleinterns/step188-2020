const filters = {}

$(async function() {
  toggleDropdown();
  // If want all events on discovery page 
  if (!(window.location.href).includes('filtered=true')) {
    const allEvents = await getAllEvents();
    populateAllEvents(allEvents); 
  } else { 
    // Get filtered events only
    const filteredEvents = await getFilteredEventsOnly(window.location.href.split("labelParams=")[1]);
    populateAllEvents(filteredEvents);
  }
});

function toggleDropdown() {
  $(".dropdown-toggle").dropdown();
}

async function getAllEvents() {
  const allEvents = await fetch('discovery-event-details');
  return allEvents.json();
}

/**
 * Gets events by specified filter
 * @constructor
 * @param {string} labelParams - label params as selected by user
 */
async function getFilteredEventsOnly(labelParams) {
  const response = await fetch('/filtered-events?' + new URLSearchParams({'labelParams': labelParams}));
  return response.json();
}

function populateAllEvents(allEvents) {
  for (const event of allEvents) {
    populateEventContainer(event, 'event-container');
    getInterestFilters(event);
  }
  populateFilters(filters)
}

// Fill filter dict with {labelName: count}
function getInterestFilters(event) {
  for (interest in event.labels) {
    if (!(event.labels[interest] in filters)) {
      filters[event.labels[interest]] = 1;
    } else {
      filters[event.labels[interest]] = filters[event.labels[interest]] + 1;
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

// Gets checked checkboxes and refreshes page
async function getFilteredEvents() {
  const checks = $('input[type="checkbox"]:checked').map(function() {
    return $(this).val();
  }).get();
  labelParams = checks.join("-");
  // refresh page while passing checked boxes as params
  window.location.href = window.location.href.split('?')[0] + '?filtered=true' + `?labelParams=${labelParams}`;
}