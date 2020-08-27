$(document).ready(function() {
  updateProfile();
});

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
function updateProfileEvents() {
  updateUserEventsHosting();
  updateUserEventsParticipating();
  updateUserEventsVolunteering();
}

async function updateUserEventsHosting() {
  const userEventsHosting = await getUserEvents('hosting');
  for (const eventsKey in userEventsHosting) {
    if (userEventsHosting.hasOwnProperty(eventsKey)) {
      populateEventContainer(userEventsHosting[eventsKey], 'events-hosting', 2);
    }
  }
}

async function updateUserEventsParticipating() {
  const userEventsParticipating = await getUserEvents('participating');
  for (const eventsKey in userEventsParticipating) {
    if (userEventsParticipating.hasOwnProperty(eventsKey)) {
      populateEventContainer(userEventsParticipating[eventsKey], 'events-participating', 2);
    }
  }
}

async function updateUserEventsVolunteering() {
  const userEventsVolunteering = await getUserEvents('volunteering');
  for (const eventsKey in userEventsVolunteering) {
    if (userEventsVolunteering.hasOwnProperty(eventsKey)) {
      const eventVolunteering = userEventsVolunteering[eventsKey];
      eventVolunteering.event.opportunityName = eventVolunteering.opportunityName
      populateEventContainer(eventVolunteering.event, 'events-volunteering', 3);
    }
  }
}

async function getUserEvents(eventType) {
  const response = await fetch(
      '/user-events?' + new URLSearchParams({'event-type': eventType}));
  return response.json();
}
