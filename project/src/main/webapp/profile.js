$(document).ready(function() {
  fetch('/profile-update')
      .then((response) => response.json())
      .then((userData) => {
        const userName = userData['name'];
        const userEmail = userData['email'];
        const userInterests = userData['interests'];
        const userSkills = userData['skills'];
        $('#name').text(userName);
        $('#email').text(userEmail);
        $('#interests').text(userInterests);
        $('#skills').text(userSkills);
      });
});
