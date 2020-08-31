$(document).ready(function() {
  updateProfile();
});

function setDefaultTab() {
  $('#hosting-tab').addClass('active');
  $('#events-hosting').addClass('show active');
}

async function updateProfile() {
  const userData = await getCurrentProfileData();
  updateProfileEvents();
  updateProfileBasics(userData);
}

/** Get all information for current user from backend */
async function getCurrentProfileData() {
  const userData = await fetch('/profile-update');
  return userData.json();
}

/** Populate the user profile with name, email, interests, skills */
function updateProfileBasics(userData) {
  $('.name').text(userData['name']);
  $('#email').text(userData['email']);
  buildAsLabels(`#interests`, userData['interests'], 'interests');
  buildAsLabels(`#skills`, userData['skills'], 'skills');
  populateExistingImage('profile', '#profile-picture-main');
}

/** Populate the user profile with all associated events */
async function updateProfileEvents() {
  updateUserEventsParticipating();
  updateUserEventsVolunteering();
  await updateUserEventsHosting();
  setDefaultTab();
}

async function updateUserEventsHosting() {
  await updateUserEvents('hosting');
}

function updateUserEventsParticipating() {
  updateUserEvents('participating');
}

function updateUserEventsVolunteering() {
  updateUserEvents('volunteering');
}

async function updateUserEvents(participatingType) {
  const userEventsResponse = await getUserEvents(participatingType);
  let userEvents = [];
  if (participatingType === 'volunteering') {
    for (eventMap of userEventsResponse) {
      userEvents.push(eventMap['event']);
    }
  } else {
    userEvents = userEventsResponse;
  }
  const userEventsLevels = buildEventLevelsWithConstantDetail(userEvents, 3);
  populateRankedEventsWithoutFilters(userEventsLevels, participatingType);
}

async function getUserEvents(eventType) {
  const response = await fetch(
      '/user-events?' + new URLSearchParams({'event-type': eventType}));
  return response.json();
}

function buildEventLevelsWithConstantDetail(events, lod) {
  let lodArrayOfMaps = [];
  for (let i = 0; i < events.length; i++) {
    lodArrayOfMaps.push({'event': events[i], 'lod': lod});
  }
  return lodArrayOfMaps;
}

function populateRankedEventsWithoutFilters(eventLevels, eventType) {
  if (!Object.keys(eventLevels).length) {
    $(`#events-${eventType}`).html('<p id="empty">No events found.</p>');
  }
  const transposedEventLevels = transposeEventLevels(eventLevels);
  for (let i = 0; i < 4; i++) {
    for (const eventMap of transposedEventLevels[i]) {
      populateEventContainer(eventMap['event'], `events-${eventType} .row #masonry-col-${i + 1}`, eventMap['lod']);
    }
  }
}
