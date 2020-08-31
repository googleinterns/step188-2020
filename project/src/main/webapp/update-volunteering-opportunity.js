$(document).ready(function() {
  getEventDetails();
  populatePrefilled('interests');
  populatePrefilled('skills');
  getVolunteeringOpportunityFormData();
});

/**
 * Get the data for the volunteering opportunity ID in the URL.
 */
async function getVolunteeringOpportunityFormData() {
  const params = (new URL(document.location)).searchParams;
  const opportunityId = params.get('opportunity-id');
  const response = await fetch(
      `/volunteering-opportunity-data?opportunity-id=${opportunityId}`);
  const opportunityData = await response.json();

  const nameInput = document.getElementById('name');
  nameInput.value = opportunityData.name;

  const numSpotsLeftInput = document.getElementById('num-spots-left');
  numSpotsLeftInput.value = opportunityData.numSpotsLeft;

  populateExistingSkills(opportunityData.requiredSkills);

  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  const eventId = urlParams.get('event-id');

  const opportunityForm = document.getElementById('opportunity-form');
  opportunityForm.action =
      `/volunteering-form-handler?opportunity-id=${opportunityId}` +
      `&event-id=${eventId}`;
}

/**
 * Populates inputs with skill labels that already exist.
 * @param {string[]} existingSkills skill labels for the opportunity.
 */
function populateExistingSkills(existingSkills) {
  $('#skills').on('itemAdded', function() {
    removeExtraInputs('#skills');
  });

  getTagsScriptWithCallback(function() {
    for (const skill of existingSkills) {
      $('#skills').tagsinput('add', skill);
    }
  });
}
