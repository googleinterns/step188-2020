$(document).ready(function() {
  addOnClickListener();
  getVolunteeringOpportunityFormData();
});

/**
 * Add on click listener for add row button.
 */
function addOnClickListener() {
  $('#add-row').click(function() {
    $('#skills').append(getInputFieldForSkill());
  });
}

/**
 * Return input string for skill input field.
 * @return {string}
 */
function getInputFieldForSkill() {
  return `<input type="text" name="required-skill" placeholder="Enter a skill" \
  class="form-control" ></tr>`;
}

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

  const requiredSkillInput = document.getElementById('required-skill');
  requiredSkillInput.value = opportunityData.requiredSkills.length ?
      opportunityData.requiredSkills[0] : '';

  const opportunityForm = document.getElementById('opportunity-form');
  opportunityForm.action =
      `/volunteering-form-handler?opportunity-id=${opportunityId}`;
}
