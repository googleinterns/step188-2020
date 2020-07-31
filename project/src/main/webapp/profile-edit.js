$(document).ready(function() {
  fetch("/profile-update").then(response => response.json()).then((userData) => {
    const userName = userData['name'];
    const userInterests = userData['interests'];
    const userSkills = userData['skills'];

    $('#name').val(userName);
    $('#interests').val(userInterests);
    $('#skills').val(userSkills);
  });
});
