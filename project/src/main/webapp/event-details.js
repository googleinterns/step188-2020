let eventHost = 'test@example.com'; // hard-coded event host value
let loginState = null;
let currentUser = null;

window.onload = async function onLoad() {
  getEventDetails();
  const eventHost = await getEventHost();
  const loginStatus = await getLoginStatus();
  populateVolunteeringOpportunitiesUI(eventHost, loginStatus);
  makeCreateOpportunityButtonVisible(eventHost, loginStatus);
  setSignupAction();
};


/**
 * Check the current login status to update
 * isLoggedIn and currentUser.
 */
async function getLoginStatus() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  return loginStatus;
}

/**
 * Adds the volunteering opportunities to the event card.
 */
async function populateVolunteeringOpportunitiesUI(eventHost, loginStatus) {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');

  const response = await fetch(`/event-volunteering-data?event-id=${eventId}`);
  const opportunities = await response.json();
  showVolunteeringOpportunities(opportunities, eventHost, loginStatus);
  populateOpportunitiesDropdown(opportunities);
}

async function showVolunteeringOpportunities(opportunities, eventHost, loginStatus) {
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      const volunteers =
          await getVolunteersByOpportunityId(opportunities[key].opportunityId);
      $('#volunteering-opportunities')
          .append(getListItemForOpportunity(
              opportunities[key].opportunityId,
              opportunities[key].name,
              opportunities[key].numSpotsLeft,
              opportunities[key].requiredSkills,
              volunteers, eventHost, loginStatus));
    }
  }
}

/**
 * Returns a string for a list HTML element representing an opportunity.
 * If the user is logged in and is the event host display edit
 * buttons for the volunteering opportunities.
 *
 * @param {string} opportunityId
 * @param {string} name name of opportunity
 * @param {string} numSpotsLeft number of spots left for opportunity
 * @param {string[]} requiredSkills skills for opportunity
 * @param {string} volunteers volunteers for opportunity
 * @return {string}
 */
function getListItemForOpportunity(
    opportunityId, name, numSpotsLeft, requiredSkills, volunteers, eventHost, loginStatus) {
  console.log(loginStatus.loginState);
  console.log(loginStatus.currentUser);
  console.log(eventHost);
  requiredSkillsText =
      requiredSkills.length ? requiredSkills.toString() : 'None';
  volunteersText =
      volunteers.length ? volunteers.toString() : 'None';
  let editLink = '';

  // If the user is logged in and the current user is the event host,
  // then show the edit link for the volunteering opportunity.
  if (loginStatus.loginState === 'LOGGED_IN' && !loginStatus.userEmail.localeCompare(eventHost)) {
    editLink = getLinkForOpportunity(opportunityId);
  }
  return `<li class="list-group-item">
          <p class="card-text">Volunteer Name: ${name}</p>
           <p class="card-text">Volunteer Spots Left: ${numSpotsLeft}</p>
           <p class="card-text">Required Skills: ${requiredSkillsText}</p>
           <p class="card-text">Volunteers: ${volunteersText}</p>${
  editLink}</li>`;
}

/**
 * Returns button with edit link for the opportunity with ID opportunityId.
 * @param {string} opportunityId Opportunity ID of the opportunity edit link to
 *     return.
 * @return {string}
 */
function getLinkForOpportunity(opportunityId) {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');
  return `<a href="/update-volunteering-opportunity.html?opportunity-id=${
    opportunityId}&event-id=${eventId}"
          id="logout-prompt"
          class="btn btn-outline-success my-2 my-sm-0"
          type="button"
        >Edit</a>`;
}

/**
 * Gets event details from database with eventId and fills out event page with details
 * If registering for event, register user then show event details
 */
async function getEventDetails() {
    //make sign up link go to correct
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');

  //Register for event
  if ((urlParams.get('register')) === "true") {
      registerEvent(eventId)
  }

  //View event details
  const response = await fetch('/create-event?' + new URLSearchParams({'eventId': eventId}))
  const data = await response.json();
  document.getElementById('name').innerHTML = data['name'];
  document.getElementById('description').innerHTML = data['description'];
  document.getElementById('date').innerHTML = `Date: 
  ${data['date'].month}/${data['date'].dayOfMonth}/${data['date'].year}`;
  document.getElementById('location').innerHTML =
    `Location: ${data['location']}`;
  document.getElementById('time').innerHTML = `Time: ${data['time']}`;
  document.getElementById('editLink').setAttribute('href', `/event-edit.html?eventId=${eventId}`);
}

async function getEventHost() {
    //make sign up link go to correct
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');

  //Register for event
  if ((urlParams.get('register')) === "true") {
      registerEvent(eventId)
  }

  //View event details
  const response = await fetch('/create-event?' + new URLSearchParams({'eventId': eventId}))
  const data = await response.json();
  const eventHost = data['host'].email;
  return eventHost;
}

/** Call doPost to register logged in user for event */
async function registerEvent(eventId) {
  const response = await fetch('/register-event?' + new URLSearchParams({'eventId': eventId}), {method: 'POST'} );
  document.getElementById('signup-link').setAttribute('href', '');
  document.getElementById('signup-link').innerHTML = 'You are signed up for this event.'
}

/**
 * Adds the volunteering opportunities for which the number of attendee spots
 * is greater than 0 to the dropdown selection.
 */
async function populateOpportunitiesDropdown(opportunities) {
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      if (opportunities[key].numSpotsLeft > 0) {
        $('#opportunities-options')
            .append(getOptionForOpportunity(
                opportunities[key].opportunityId,
                opportunities[key].name));
      }
    }
  }
}

/**
 * Returns option with given name as text and opportunityId as value.
 * @param {string} opportunityId value of the opportunity option to
 *     return
 * @param {string} name text for the opportunity option to return
 * @return {string}
 */
function getOptionForOpportunity(opportunityId, name) {
  return `<option value=${opportunityId}>${name}</option>`;
}

/**
 * Returns volunteer emails for the given opportunityId.
 * @param {string} opportunityId Opportunity ID for which to return
 *     volunteer emails
 * @return {string[]}
 */
async function getVolunteersByOpportunityId(opportunityId) {
  const response =
      await fetch(`/opportunity-signup-data?opportunity-id=${opportunityId}`);
  const volunteerData = await response.json();
  const volunteers = [];
  for (const key in volunteerData) {
    if (volunteerData.hasOwnProperty(key)) {
      volunteers.push(volunteerData[key].email);
    }
  }
  return volunteers;
}

function makeCreateOpportunityButtonVisible(eventHost, loginStatus) {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');

  if (loginStatus.loginState === 'LOGGED_IN' && !loginStatus.userEmail.localeCompare(eventHost)) {
    $('#add-opportunity')
          .append(`<a href=
      /create-volunteering-opportunity.html?event-id=${eventId}>Add an volunteering opportunity</a>`);
  }
}

function setSignupAction() {
  const params = (new URL(document.location)).searchParams;
  const eventId = params.get('eventId');

  const opportunitySignupForm = document.getElementById('opportunity-signup-form');
  opportunitySignupForm.action =
      `/opportunity-signup-form-handler?event-id=${eventId}`;
}