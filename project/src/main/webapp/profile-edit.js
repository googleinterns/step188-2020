$(document).ready(function() {
  showCurrentUserInfo();
});

/**
 * Populates inputs with current user information.
 */
async function showCurrentUserInfo() {
  const response = await fetch('/profile-update');
  const userData = await response.json();

  const userName = userData['name'];
  const userInterests = userData['interests'];
  const userSkills = userData['skills'];

  $('#name').val(userName);
  populateExisting('interests', userData);
  populateExisting('skills', userData);
}

function populateExisting(className, userData) {
  const existingLabels = userData[className];
  getTagsScriptWithCallback(function() {
    for (const label of existingLabels) {
      $(`#${className}`).append(buildTagWithoutAdder(className, label));
    }
  });
}
