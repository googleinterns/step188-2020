const eventHost = 'test@example.com'; // hard-coded event host value
let loginState = null;
let currentUser = null;

window.onload = function onLoad() {
  getEventDetails();
  checkLoginStatus();
  getVolunteeringOpportunities();
  populateOpportunitiesDropdown();
};


/**
 * Check the current login status to update
 * isLoggedIn and currentUser.
 */
async function checkLoginStatus() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  loginState = loginStatus.loginState;
  currentUser = loginStatus.userEmail;
}

/**
 * Adds the volunteering opportunities to the event card.
 */
async function getVolunteeringOpportunities() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json();
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
              volunteers));
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
    opportunityId, name, numSpotsLeft, requiredSkills, volunteers) {
  requiredSkillsText =
      requiredSkills.length ? requiredSkills.toString() : 'None';
  volunteersText =
      volunteers.length ? volunteers.toString() : 'None';
  let editLink = '';

  // If the user is logged in and the current user is the event host,
  // then show the edit link for the volunteering opportunity.
  if (loginState === 'LOGGED_IN' && !currentUser.localeCompare(eventHost)) {
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
  return `<a href="/update-volunteering-opportunity.html?opportunity-id=${
    opportunityId}"
          id="logout-prompt"
          class="btn btn-outline-success my-2 my-sm-0"
          type="button"
        >Edit</a>`;
}

/**
 * Gets event details from database with eventId and fills out event page with details
 */
async function getEventDetails() {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('eventId');
  const response = await fetch('/create-event?' + new URLSearchParams({'eventId': eventId}))
  const data = await response.json();
  document.getElementById('name').innerHTML = data['name'];
  document.getElementById('description').innerHTML = data['description'];
  document.getElementById('date').innerHTML = `Date: 
  ${data['date'].month}/${data['date'].dayOfMonth}/${data['date'].year}`;
  document.getElementById('location').innerHTML =
    `Location: ${data['location']}`;
  document.getElementById('time').innerHTML = `Time: ${data['time']}`;
  const link = '/event-edit.html?eventId=' + eventId;
  document.getElementById('editLink').setAttribute('href', link);
}

/**
 * Adds the volunteering opportunities for which the number of attendee spots
 * is greater than 0 to the dropdown selection.
 */
async function populateOpportunitiesDropdown() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json();
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
