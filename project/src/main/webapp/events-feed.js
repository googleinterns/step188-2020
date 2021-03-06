const filters = {}

$(function() {
  setupEvents();
});

async function setupEvents() {
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
}

/**
 * Get all search events for the keyword entered and populate
 * the events in the display.
 */
async function getAllSearchEvents() {
  const eventContainer = document.getElementById('event-container');
  if (eventContainer != null) {
    eventContainer.remove();
  }

  const searchContainer = document.getElementById('search-container');
  searchContainer.innerHTML = '';

  const keyword = document.getElementById('keyword').value;
  const response = await fetch(`search-data?keyword=${keyword}`);
  const searchEvents = await response.json();
  for (const key in searchEvents) {
    if (searchEvents.hasOwnProperty(key)) {
      populateEventContainer(searchEvents[key], 'search-container', 4);
    }
  }
}

function toggleDropdown() {
  $(".dropdown-toggle").dropdown();
}

async function getAllEvents() {
  const allEvents = await fetch('/discovery-event-details');
  return allEvents.json();
}

function populateRankedEvents(eventLevels) {
  const transposedEventLevels = transposeEventLevels(eventLevels);
  for (let i = 0; i < 4; i++) {
    for (const eventMap of transposedEventLevels[i]) {
      populateEventContainer(eventMap['event'], `masonry-col-${i + 1}`, eventMap['lod']);
      getInterestFilters(eventMap['event']);
    }
  }
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

async function populateAllEvents(allEvents) {
  const rankedEvents = await getRankedEvents(allEvents);
  const eventLevels = getLodsFromEvents(rankedEvents);
  populateRankedEvents(eventLevels);
  populateFilters(filters);
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
  persistFilterDropdown();
  $('#filterCheckBoxes').append(`<br /><br />`);
}

function persistFilterDropdown() {
  $('.dropdown-menu label').click(function(e) {
    e.stopPropagation();
  });
}

// Gives HTML for checkboxes
function getCheckboxes(key, value) {
  return `<label for=${key} class="filter-check"><input type="checkbox" id=${key} name=${key} value=${key}>
     ${key} (${value})</label>`
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

function seeAllEvents() {
  window.location.href='https://event-organizer-step-2020.uc.r.appspot.com/events-feed.html';
}