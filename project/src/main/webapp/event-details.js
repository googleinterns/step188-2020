$(async function() {
  getEventDetails();
  const eventId = getEventId();
  const eventHost = await getEventHost();
  configureRegisterAndEditButtons(eventHost, eventId);
  const loginStatus = await getLoginStatus();
  populateVolunteeringOpportunitiesUI(eventHost, loginStatus);
  setSignupAction();
  setImageFormAction('event');
});

/** 
 * Adds the edit button for the host and removes the register button
 * @param {Object} eventHost email of the host of the event
 */
async function configureRegisterAndEditButtons(eventHost, eventId) {
  const loggedInUserIsHost = await getLoggedInUserIsHost(eventHost);
  if (loggedInUserIsHost) {
    const editContainer = '#edit-container .card .card-body #edit-container-2';
    addHostButtons(editContainer, eventId);
    $('#signup-link').hide();
  } else {
    $('#edit-container').hide();
    $('#details')
        .append('<button id="signup-link" href="signup.html" ' + 
                'type="button" class="btn btn-primary">Register</button>');
  }
}

/**
 * Adds appropriate buttons available to hosts
 * @param {string} editContainer id of container to be appended to
 * @param {string} eventId id of relevant event
 */
function addHostButtons(editContainer, eventId) {
    $(editContainer)
        .append(`<a href="/create-volunteering-opportunity.html?eventId=${eventId}"
                  type="button" class="btn btn-primary" id="volopp-button"
                  >Add a volunteering opportunity</a>`);
    $(editContainer).append('<br /><br />');
    $(editContainer)
        .append(`<a id="edit-link" href="event-edit.html?eventId=${eventId}" 
                type="button" class="btn btn-primary" id="edit-button"> Edit event </a>`);
}

/**
 * Adds the volunteering opportunities to the event card and the dropdown
 * in the signup form.
 * @param {string} eventHost email of the host of the event
 * @param {Object} loginStatus status of
 *      current user
 */
async function populateVolunteeringOpportunitiesUI(eventHost, loginStatus) {
  const eventId = getEventId();

  const response = await fetch(`/event-volunteering-data?event-id=${eventId}`);
  const opportunities = await response.json();
  showVolunteeringOpportunities(opportunities, eventHost, loginStatus);
  populateOpportunitiesDropdown(opportunities);
}

/**
 * Should the volunteering opportunities of the event card and if the
 * eventHost is the current user, add the edit link for each opportunity.
 * @param {Object} opportunities
 * @param {string} eventHost email of the host of the event
 * @param {Object} loginStatus status of current user
 */
async function showVolunteeringOpportunities(
    opportunities, eventHost, loginStatus) {
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
 * @param {string} eventHost email of the host of the event
 * @param {Object} loginStatus status of current user
 * @return {string}
 */
function getListItemForOpportunity(
    opportunityId, name, numSpotsLeft, requiredSkills,
    volunteers, eventHost, loginStatus) {
  requiredSkillsText =
      requiredSkills.length ? requiredSkills.toString() : 'None';
  volunteersText =
      volunteers.length ? volunteers.toString() : 'None';
  let editLink = '';

  // If the user is logged in and the current user is the event host,
  // then show the edit link for the volunteering opportunity.
  if (loginStatus.loginState === 'LOGGED_IN' &&
    !loginStatus.userEmail.localeCompare(eventHost)) {
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

async function getEventHost() {
  const response = await fetch('/create-event?' + new URLSearchParams({'eventId': getEventId()}));
  const data = await response.json();
  const eventHost = data['host'].email;
  return eventHost;
}

/** Call doPost to register logged in user for event */
async function registerEvent(eventId, host) {
  const response =
      await fetch('/register-event?' + new URLSearchParams({'eventId': eventId}), {method: 'POST'} );
  const isHost = await getLoggedInUserIsHost(host);
  if (isHost) {
    $('.alert').slideDown();
    $('#signup-link').hide();
  } else {
    document.getElementById('signup-link').setAttribute('href', '');
    document.getElementById('signup-link').innerHTML = 'You are signed up for this event.';
  }
}

async function getLoggedInUserIsHost(eventHost) {
  const loggedInUserEmail = await getLoggedInUserEmail();
  return eventHost === loggedInUserEmail;
}

/**
 * Adds the volunteering opportunities for which the number of attendee spots
 * is greater than 0 to the dropdown selection.
 * @param {Object[]} opportunities to display in dropdown
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

/**
 * Set the signup action in the signup form.
 */
function setSignupAction() {
  const params = (new URL(document.location)).searchParams;
  const eventId = params.get('eventId');

  const opportunitySignupForm = document.getElementById('opportunity-signup-form');
  opportunitySignupForm.action =
      `/opportunity-signup-form-handler?event-id=${eventId}`;
}
