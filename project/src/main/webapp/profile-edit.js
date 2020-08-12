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
  const userInterests = userData['interests'];
  const userSkills = userData['skills'];

  $('#name').val(userName);
  $('#interests').val(userInterests);
  $('#skills').val(userSkills);
}
