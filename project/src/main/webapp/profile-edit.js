$(document).ready(function() {
  showCurrentUserInfo();
  setImageFormAction();
  populatePrefilled('interests');
  populatePrefilled('skills');
});

/** Set profile picture form action */
async function setImageFormAction() {
  const response = await fetch('/blob-url');
  const blobUrl = await response.text();
  $('#image-form').attr('action', blobUrl);
}

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
  populateExistingProfileImage();
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
