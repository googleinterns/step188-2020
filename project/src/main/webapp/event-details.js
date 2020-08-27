$(async function() {
  getEventDetailsWithoutOpportunities();
  const eventId = getEventId();
  const eventHost = await getEventHost();
  configureRegisterAndEditButtons(eventHost, eventId);
  setImageFormAction('event');
});

/** 
 * Adds the edit button for the host and removes the register button
 * @param {Object} eventHost email of the host of the event
 */
async function configureRegisterAndEditButtons(eventHost, eventId) {
  const loggedInUserIsHost = await getLoggedInUserIsHost(eventHost);
  populateVolunteeringOpportunitiesUI(eventHost, loggedInUserIsHost);
  if (loggedInUserIsHost) {
    const editContainer = '#edit-container .card .card-body #edit-container-2';
    addHostButtons(editContainer, eventId);
    $('#signup-link').hide();
    $('#volopp-container').hide();
  } else {
    setSignupAction();
    $('#edit-container').hide();
    $('#details')
        .append(`<a id="signup-link" href="/event-details.html?eventId=${eventId}&register=true" 
                type="button" class="btn btn-primary">Register</a>`);
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
        .append(`<a id="edit-link" href="/event-edit.html?eventId=${eventId}" 
                type="button" class="btn btn-primary" id="edit-button"> Edit event </a>`);
}

/**
 * Adds the volunteering opportunities to the event card and the dropdown
 * in the signup form.
 * @param {string} eventHost email of the host of the event
 * @param {boolean} isHost
 */
async function populateVolunteeringOpportunitiesUI(eventHost, isHost) {
  const eventId = getEventId();

  const response = await fetch(`/event-volunteering-data?event-id=${eventId}`);
  const opportunities = await response.json();
  showVolunteeringOpportunities(opportunities, eventHost, isHost);
  populateOpportunitiesDropdown(opportunities);
}

/**
 * Should the volunteering opportunities of the event card and if the
 * eventHost is the current user, add the edit link for each opportunity.
 * @param {Object} opportunities
 * @param {string} eventHost email of the host of the event
 * @param {boolean} isHost
 */
async function showVolunteeringOpportunities(
    opportunities, eventHost, isHost) {
  for (const key in opportunities) {
    if (opportunities.hasOwnProperty(key)) {
      const volunteers =
          await getVolunteersByOpportunityId(opportunities[key].opportunityId);
      $('#volunteering-opportunities .card .card-body')
          .append(getListItemForOpportunity(
              opportunities[key].opportunityId,
              opportunities[key].name,
              opportunities[key].numSpotsLeft,
              opportunities[key].requiredSkills,
              volunteers, eventHost, isHost));
       buildAsLabels(`#oppportunity-id-${opportunities[key].opportunityId} #skills`,
        opportunities[key].requiredSkills, 'skills');        
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
 * @param {boolean} isHost
 * @return {string}
 */
function getListItemForOpportunity(
    opportunityId, name, numSpotsLeft, requiredSkills,
    volunteers, eventHost, isHost) {
  const volunteersText =
      volunteers.length ? volunteers.toString().replace(/,/g, ', ') : 'None'; //remove
  let editLink = '';
  if (isHost) {
    editLink = getLinkForOpportunity(opportunityId);
  }
  return `<li class="list-group-item" id="oppportunity-id-${opportunityId}">
          <p class="card-text" id="vols-needed">
                <i class="fas fa-hand-holding-medical"></i>
                <span class="card-text">${name}</span>
          </p>
          <div class="display-inline-block" id="skills"></div><br>
           <p class="card-text">Spots Left: ${numSpotsLeft}</p>
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
    opportunityId}&event-id=${eventId}&eventId=${eventId}"
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
  if (opportunities.length) {
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
  } else {
    document.getElementById("volopp-container").remove();
    document.getElementById("volunteering-opportunities").remove();
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

  $('#opportunity-signup-form').attr('action', `/opportunity-signup-form-handler?event-id=${eventId}`);
  $('#opportunity-signup-form').show();
}
