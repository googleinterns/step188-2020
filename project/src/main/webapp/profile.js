$(document).ready(function() {
  updateProfile();
});

async function updateProfile() {
  const userData = await getCurrentProfileData();
  updateProfileBasics(userData);
  updateProfileEvents();
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
  populateExistingProfileImage();
}

/** Populate the user profile with all associated events */
function updateProfileEvents() {
  updateUserEventsHosting();
  updateUserEventsParticipating();
  updateUserEventsVolunteering();
}

async function updateUserEventsHosting() {
  const userEventsHosting = await getUserEvents('hosting');
  for (const eventHosting of userEventsHosting) {
    populateEventContainer(eventHosting, 'events-hosting');
  }
}

async function updateUserEventsParticipating() {
  const userEventsParticipating = await getUserEvents('participating');
  for (const eventParticipating of userEventsParticipating) {
    populateEventContainer(eventParticipating, 'events-participating');
  }
}

async function updateUserEventsVolunteering() {
  const userEventsVolunteering = await getUserEvents('volunteering');
  for (const eventVolunteering of userEventsVolunteering) {
    const event = eventVolunteering.event;
    await populateEventContainer(event, 'events-volunteering');
    addVolunteerRole(event.eventId, eventVolunteering.opportunityName);
  }
}

function addVolunteerRole(eventId, opportunity) {
  $(`#event-${eventId} #event-card-labels`)
      .append(`<br /><br /><div><b>Role: ${opportunity}</b></div>`);
}

async function getUserEvents(eventType) {
  const userEvents = await fetch(
      '/user-events?' + new URLSearchParams({'event-type': eventType}));
  return userEvents.json();
}
