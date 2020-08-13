$(document).ready(function() {
  showCurrentUserInfo();
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
}

/** 
 * Populates inputs with labels that already exist
 */
function populateExisting(className, userData) {
  $(className).on('itemAdded', function() {
    if ($(className).prevAll().length > 2) {
      const otherName = className === '#interests' ? '#skills' : '#interests';
      $(otherName).prev().remove();
      $(className).prev().prev().remove();
    }
  });

  const existingLabels = userData[className];
  getTagsScriptWithCallback(function() {
    for (const label of existingLabels) {
      $(`#${className}`).tagsinput('add', label);
    }
  });
}
