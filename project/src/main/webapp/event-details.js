eventHost = 'test@example.com'; // hard-coded event host value
isLoggedIn = false;
currentUser = null;

window.onload = function onLoad() {
  checkLoginStatus();
  getVolunteeringOpportunities();
};


/**
 * Update the current login status.
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
    $('#volunteering-opportunities')
        .append(getListItemForOpportunity(
            opportunities[key].opportunityId, opportunities[key].name,
            opportunities[key].numSpotsLeft,
            opportunities[key].requiredSkills));
  }
}

/**
 * Return a string for a list HTML element representing an opportunity.
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
  const editLink = (isLoggedIn && !currentUser.localeCompare(eventHost)) ?
      getLinkForOpportunity(opportunityId) :
      '';
  return `<li class="list-group-item">
          <p class="card-text">Volunteer Name: ${name}</p>
           <p class="card-text">Volunteer Spots Left: ${numSpotsLeft}</p>
           <p class="card-text">Required Skills: ${requiredSkillsText}</p>${
      editLink}</li>`;
}

/**
 * Returns button with edit link for the opportunity with ID opportunityId.
 * @param {string} opportunityId Opportunity ID of the opportunity edit link to
 *     retrieve.
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