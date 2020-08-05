const eventHost = 'test@example.com'; // hard-coded event host value
let isLoggedIn = false;
let currentUser = null;

window.onload = function onLoad() {
  checkLoginStatus();
  getVolunteeringOpportunities();
};


/**
 * Updates the current login status.
 */
async function checkLoginStatus() {
  const response = await fetch('/login-status');
  const loginStatus = await response.json();
  isLoggedIn = loginStatus.isLoggedIn;
  currentUser = loginStatus.userEmail;
  return loginStatus;
}

/**
 * Adds the volunteering opportunities to the event card.
 */
async function getVolunteeringOpportunities() {
  const response = await fetch('/event-volunteering-data');
  const opportunities = await response.json();
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      $('#volunteering-opportunities')
          .append(getListItemForOpportunity(
              opportunities[key].opportunityId, opportunities[key].name,
              opportunities[key].numSpotsLeft,
              opportunities[key].requiredSkills));
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
 * @return {string}
 */
function getListItemForOpportunity(
    opportunityId, name, numSpotsLeft, requiredSkills) {
  requiredSkillsText =
      requiredSkills.length ? requiredSkills.toString() : 'None';
  let editLink = '';

  // If the user is logged in and the current user is the event host,
  // then show the edit link for the volunteering opportunity.
  if (isLoggedIn && !currentUser.localeCompare(eventHost)) {
    editLink = getLinkForOpportunity(opportunityId);
  }
  return `<li class="list-group-item">
          <p class="card-text">Volunteer Name: ${name}</p>
           <p class="card-text">Volunteer Spots Left: ${numSpotsLeft}</p>
           <p class="card-text">Required Skills: ${requiredSkillsText}</p>${
  editLink}</li>`;
}

/**
 * Returns button with edit link for the opportunity with ID opportunityId.
 * @param {string} opportunityId Opportunity ID of the opportunity edit link to
 *     return.
 * @return {string}
 */
function getLinkForOpportunity(opportunityId) {
  return `<a href="/update-volunteering-opportunity.html?opportunityId=${
    opportunityId}"
          id="logout-prompt"
          class="btn btn-outline-success my-2 my-sm-0"
          type="button"
        >Edit</a>`;
}
