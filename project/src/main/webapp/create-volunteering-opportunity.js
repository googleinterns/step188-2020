$(function() {
  populatePrefilled('interests');
  populatePrefilled('skills');
  setActionToCorrectRedirectURL();
});

/**
 * Set the opportunity form action to include query parameter for eventId.
 */
async function setActionToCorrectRedirectURL() {
  const params = (new URL(document.location)).searchParams;
  const eventId = params.get('event-id');

  const opportunityForm = document.getElementById('volunteering-opportunity-form');
  opportunityForm.action =
      `/volunteering-form-handler?event-id=${eventId}`;
}

