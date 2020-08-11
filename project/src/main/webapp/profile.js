$(document).ready(function() {
  updateProfile();
});

async function updateProfile() {
  const userData = await getCurrentProfileData();
  console.log(userData);
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
  const userName = userData['name'];
  const userEmail = userData['email'];
  const userInterests = userData['interests'];
  const userSkills = userData['skills'];
  $('#name').text(userName);
  $('#email').text(userEmail);
  $('#interests').text(userInterests);
  $('#skills').text(userSkills);
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
    populateEventContainer(eventVolunteering, 'events-volunteering');
  }
}

async function getUserEvents(eventType) {
  const userEvents = await fetch(
      '/user-events?' + new URLSearchParams({'event-type': eventType}));
  return userEvents.json();
}
