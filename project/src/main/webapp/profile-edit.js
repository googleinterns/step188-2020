$(document).ready(function() {
  showCurrentUserInfo();
  setImageFormAction('profile');
  populatePrefilled('interests');
  populatePrefilled('skills');
});

/**
 * Populates inputs with current user information.
 */
async function showCurrentUserInfo() {
  const response = await fetch('/profile-update');
  const userData = await response.json();
  const userName = userData['name'];

  $('#name').val(userName);
  populateExisting('interests', userData);
  populateExisting('skills', userData);
  populateExistingImage('profile', '#profile-picture');
}

/** 
 * Populates inputs with labels that already exist
 */
function populateExisting(className, userData) {
  const existingLabels = userData[className];
  for (const label of existingLabels) {
    $(`#${className}`).tagsinput('add', label);
  }
}

function removeAllExtraInputs() {
  removeExtraInputs('#interests');
  removeExtraInputs('#skills');
}

function getInterests() {
  return getLabels('interests');
}

function getSkills() {
  return getLabels('skills');
}

function getLabels(labelType) {
  let labels = [];
  for (const label of $(`#${labelType}`).children()) {
    labels.push(label.value);
  }
  return labels;
}
